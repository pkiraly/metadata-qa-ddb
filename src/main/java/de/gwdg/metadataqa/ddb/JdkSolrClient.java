package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.uniqueness.SolrClient;
import de.gwdg.metadataqa.api.uniqueness.SolrConfiguration;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BaseHttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Implements the metadata-qa-api Solr contract without its legacy HTTP transport.
 *
 * <p>The API snapshot constructs an {@code Http2SolrClient} backed by Jetty 10.
 * SolrJ 10 renamed that client, so upgrading SolrJ alone would break the snapshot's
 * binary contract. This adapter keeps SolrJ 9's query and indexing behavior while
 * allowing the obsolete Jetty dependencies to leave the classpath. Less fossil fuel.
 *
 * <p>Search and TF-IDF responses remain raw UTF-8 text, as expected by the API's
 * extractors. Updates use SolrJ's JavaBin protocol over the JDK HTTP client.
 *
 * <p>Instances own their update transport. Owners should {@link #close()} them after
 * the final commit; the upstream {@link SolrClient} interface has no close hook.
 */
final class JdkSolrClient implements SolrClient, Closeable {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(JdkSolrClient.class);

  private final String baseUrl;
  private final HttpJdkSolrClient solr;

  /**
   * Uses the configured core URL unchanged for both read and update requests.
   *
   * @param configuration host, port and complete core path, for example {@code solr/ddb}
   */
  JdkSolrClient(SolrConfiguration configuration) {
    baseUrl = configuration.getUrl();
    solr = new HttpJdkSolrClient.Builder(baseUrl).build();
  }

  /**
   * Queries one field value, preserving the API's phrase and wildcard semantics.
   * Embedded quotes are escaped before UTF-8 URL encoding; {@code *} stays unquoted.
   *
   * @param field schema-controlled Solr field name, passed through as query syntax
   * @param value value to match as a quoted phrase, or {@code *} to match every value
   * @return raw response text, or {@code null} for non-200 responses and I/O failures
   */
  @Override
  public String getSolrSearchResponse(String field, String value) {
    String query = "*".equals(value) ? "*" : encode(quote(value));
    return readResponse(baseUrl + "/select/?q=" + field + ":" + query + "&rows=0");
  }

  /**
   * Requires every supplied phrase using a parenthesized {@code AND} expression.
   * Each phrase is quoted first, then the complete expression is URL encoded once.
   *
   * @param field schema-controlled Solr field name
   * @param values phrases to combine, retaining the set's iteration order
   * @return raw response text, or {@code null} for non-200 responses and I/O failures
   */
  @Override
  public String getSolrSearchResponse(String field, Set<String> values) {
    StringJoiner query = new StringJoiner(" AND ", "(", ")");
    values.forEach(value -> query.add(quote(value)));
    return readResponse(baseUrl + "/select/?q=" + field + ":" + encode(query.toString()) + "&rows=0");
  }

  /**
   * Forwards the API's already assembled TF-IDF path and parameters without rewriting.
   *
   * @param params relative handler path and query, including its leading slash
   * @param recordId retained for interface compatibility; it does not change the request
   * @return raw response text, or {@code null} for non-200 responses and I/O failures
   */
  @Override
  public String getTfIdfResponse(String params, String recordId) {
    return readResponse(baseUrl + params);
  }

  private static String quote(String value) {
    return '"' + value.replace("\"", "\\\"") + '"';
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  /**
   * Keeps the upstream read contract: concatenate UTF-8 lines, log failures and return null.
   * Each request owns and releases its reader and connection independently of the writer.
   */
  private String readResponse(String url) {
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestProperty("User-Agent", "Custom Java application");
      connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        LOGGER.error("Solr request to {} returned HTTP {}", url, connection.getResponseCode());
        return null;
      }
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
          response.append(line);
        return response.toString();
      }
    } catch (IOException e) {
      LOGGER.error("Error connecting to Solr at {}", url, e);
      return null;
    } finally {
      if (connection != null)
        connection.disconnect();
    }
  }

  /**
   * Indexes one record using the same dynamic-field mapping as the upstream client.
   * IDs are trimmed, null value lists are ignored, and fields gain {@code _ss} unless
   * they already end in {@code _ss} or {@code _sni}. Server-side document rejections
   * are logged and processing continues; checked transport failures reach the caller.
   *
   * @param id record identifier, trimmed before indexing
   * @param fields field names and their multi-valued content
   * @throws IOException if sending or reading the update fails
   * @throws SolrServerException if SolrJ cannot complete the request
   */
  @Override
  public void indexMap(String id, Map<String, List<String>> fields) throws IOException, SolrServerException {
    SolrInputDocument document = new SolrInputDocument();
    document.addField("id", id.trim());
    fields.forEach((field, values) -> {
      if (values != null) {
        String name = field.endsWith("_sni") || field.endsWith("_ss") ? field : field + "_ss";
        document.addField(name, values);
      }
    });
    try {
      solr.add(document);
    } catch (BaseHttpSolrClient.RemoteSolrException e) {
      LOGGER.warn("Solr rejected document {}", document, e);
    }
  }

  /**
   * Commits accumulated updates. Checked request failures are logged, matching the
   * upstream API's best-effort shutdown behavior rather than aborting assessment.
   */
  @Override
  public void commit() {
    try {
      solr.commit();
    } catch (SolrServerException | IOException e) {
      LOGGER.error("Could not commit Solr changes", e);
    }
  }

  /**
   * Deletes every document in the configured core and then attempts a commit.
   * The commit still runs after a checked deletion failure, preserving upstream behavior.
   */
  @Override
  public void deleteAll() {
    try {
      solr.deleteByQuery("*:*");
    } catch (SolrServerException | IOException e) {
      LOGGER.error("Could not delete Solr documents", e);
    }
    commit();
  }

  /**
   * Releases the owned SolrJ transport; this does not commit pending changes.
   * Call after the final commit, or use try-with-resources when owning the adapter directly.
   *
   * @throws IOException if transport cleanup fails
   */
  @Override
  public void close() throws IOException {
    solr.close();
  }
}

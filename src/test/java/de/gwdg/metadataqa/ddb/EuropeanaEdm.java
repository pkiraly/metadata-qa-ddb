package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.json.DataElement;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EuropeanaEdm {

  String recordAddress = "//rdf:RDF";
  XPathWrapper xPathWrapper;
  Schema schema;

  @Before
  public void setUpWith() {
    URL url = this.getClass().getResource("/europeana-edm/1.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("src/main/resources/europeana-edm-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      String xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void recordId() {
    DataElement p = schema.getPathByLabel("recordId");
    assertEquals("rdf:RDF/ore:Aggregation/@rdf:about", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("P4P5V43H5LP3PSQH6FADSLSSB46L6FNY", itemList.get(0).getValue());
  }

  @Test
  public void thumbnail() {
    DataElement p = schema.getPathByLabel("thumbnail");
    assertEquals("rdf:RDF/ore:Aggregation/edm:object/@rdf:resource", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("https://ifl.wissensbank.com/cgi-bin/starfetch.exe?oE2xeFhEKiGwNb49wg1XPM1zxNT1KJSTVbg62h3aZgW8EE.ebStLZdGJEkpChNnKMX5ZnDIDgSRgksRnBCAh KRf5PkMri23ksCLll12Ejwiu87d0etXwKhq2PmukPhQXCtMfhTeILAE/Eu277%2d0053.jpg", itemList.get(0).getValue());
  }

  @Test
  public void url() {
    DataElement p = schema.getPathByLabel("url");
    assertEquals("rdf:RDF/ore:Aggregation/edm:isShownAt/@rdf:resource", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("https://ifl.wissensbank.com/fastlink.html?search=306611000", itemList.get(0).getValue());
  }

  @Test
  public void title() {
    DataElement p = schema.getPathByLabel("dc:title");
    assertEquals("rdf:RDF/edm:ProvidedCHO/dc:title", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("Klosterkirche in Caldarusani bei Bukarest", itemList.get(0).getValue());
  }

  @Test
  public void dc_type_source() {
    DataElement p = schema.getPathByLabel("dc_type_source");
    assertEquals("rdf:RDF/edm:ProvidedCHO/dc:type/@rdf:resource", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("http://vocab.getty.edu/aat/300046300", itemList.get(0).getValue());
  }

  @Test
  public void rights() {
    DataElement p = schema.getPathByLabel("rights");
    assertEquals("rdf:RDF/edm:WebResource/edm:rights/@rdf:resource", p.getPath());

    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("http://creativecommons.org/licenses/by-nc-sa/4.0/", itemList.get(0).getValue());
  }
}

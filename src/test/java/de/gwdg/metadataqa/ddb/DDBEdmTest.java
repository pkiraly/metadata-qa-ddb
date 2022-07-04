package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.json.JsonBranch;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.OaiPmhXPath;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class DDBEdmTest {

  private Schema schema;
  private String recordAddress = "//rdf:RDF";
  private OaiPmhXPath oaiPmhXPath;

  @Before
  public void setUp() throws Exception {
    URL url = this.getClass().getResource("/ddb-edm/test.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("src/main/resources/ddb-edm-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      String xml = iterator.next();
      oaiPmhXPath = new OaiPmhXPath(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void thumbnail() {
    // JsonBranch p = schema.getPathByLabel("thumbnail");
    // assertEquals("lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource", p.getJsonPath());

    /*
    String xpath1 = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource";
    assertEquals(xpath1, p.getJsonPath());
    String xpath = p.getJsonPath();

     */
    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList("rdf:RDF/ore:Aggregation[1]/edm:isShownAt/@rdf:resource");
    assertEquals(1, itemList.size());
    assertEquals("http://nbn-resolving.org/urn:nbn:de:bvb:12-sbb00000028-8", itemList.get(0).getValue());

    Document document = oaiPmhXPath.getDocument();
    XPath xpathEngine = oaiPmhXPath.getXpathEngine();
    String xpath = "//rdf:RDF/edm:WebResource[@rdf:about = string(../ore:Aggregation[1]/edm:isShownAt/@rdf:resource)]/dcterms:rights/@rdf:resource"; //  | //rdf:RDF/ore:Aggregation[1]/edm:isShownAt/@rdf:resource

    itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("http://creativecommons.org/licenses/by-sa/4.0/", itemList.get(0).getValue());
    // assertEquals("http://nbn-resolving.org/urn:nbn:de:bvb:12-sbb00000028-8", itemList.get(1).getValue());
  }

  @Test
  public void dataProvider() {
    // JsonBranch p = schema.getPathByLabel("thumbnail");
    // assertEquals("lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource", p.getJsonPath());

    /*
    String xpath1 = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource";
    assertEquals(xpath1, p.getJsonPath());
    String xpath = p.getJsonPath();

     */
    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList("rdf:RDF/ore:Aggregation[1]/edm:dataProvider/@rdf:resource");
    assertEquals(1, itemList.size());
    assertEquals("http://d-nb.info/gnd/2022477-1", itemList.get(0).getValue());

    Document document = oaiPmhXPath.getDocument();
    XPath xpathEngine = oaiPmhXPath.getXpathEngine();
    String xpath = "//rdf:RDF/edm:Agent[@rdf:about = string(../ore:Aggregation[1]/edm:dataProvider/@rdf:resource)]/owl:sameAs/@rdf:resource"; //  | //rdf:RDF/ore:Aggregation[1]/edm:isShownAt/@rdf:resource

    itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("http://ld.zdb-services.de/data/organisations/DE-22", itemList.get(0).getValue());
  }

  @Test
  public void hasType() {
    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList("rdf:RDF/edm:ProvidedCHO[1]/edm:hasType/@rdf:resource");
    assertEquals(1, itemList.size());
    assertEquals("http://d-nb.info/gnd/4023287-6", itemList.get(0).getValue());

    Document document = oaiPmhXPath.getDocument();
    XPath xpathEngine = oaiPmhXPath.getXpathEngine();
    String xpath = "rdf:RDF/skos:Concept[@rdf:about = string(../edm:ProvidedCHO[1]/edm:hasType/@rdf:resource)]/skos:prefLabel";

    itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("Handschrift", itemList.get(0).getValue());
  }

}

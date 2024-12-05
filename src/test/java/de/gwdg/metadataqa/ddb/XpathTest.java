package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class XpathTest {
  @Test
  public void name() throws FileNotFoundException {
    Schema schema = ConfigurationReader.readSchemaYaml("schemas/marc-schema.yaml").asSchema();
    assertNotNull(schema);
    assertNotNull(schema.getNamespaces());
    assertEquals(8, schema.getNamespaces().size());

    URL url = this.getClass().getResource("/marc/marc.xml");
    File file = new File(url.getFile());

    XPathWrapper xPathWrapper = new XPathWrapper(file, schema.getNamespaces());
    // String idPath = "marc:record/marc:controllfield[@tag=\"001\"]";
    String idPath = "marc:record/marc:controlfield[@tag='001']";
    List<EdmFieldInstance> idList = xPathWrapper.extractFieldInstanceList(idPath);
    assertNotNull(idList);
    assertFalse(idList.isEmpty());
    assertEquals(1, idList.size());
    assertEquals("BDR-BV012591623-59024", idList.get(0).getValue());
  }

  // @Test
  public void lido() throws FileNotFoundException {
    String path = "//lido:lido";
    Schema schema = ConfigurationReader.readSchemaYaml("schemas/lido-schema.yaml").asSchema();
    URL url = this.getClass().getResource("/lido/20201020_sddm.xml");
    File file = new File(url.getFile());

    XPathWrapper xPathWrapper = null;
    try {
      String pathTerm = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:rightsResource/lido:rightsType/lido:term";
      String pathConceptId = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:rightsResource/lido:rightsType/lido:conceptID";
      XPathBasedIterator iterator = new XPathBasedIterator(file, path, schema.getNamespaces());
      String xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());

      List<EdmFieldInstance> itemList;
      itemList = xPathWrapper.extractFieldInstanceList(pathTerm);
      assertEquals(3, itemList.size());
      assertEquals("Freier Zugang - Rechte vorbehalten", itemList.get(0).getValue());

      itemList = xPathWrapper.extractFieldInstanceList(pathConceptId);
      assertEquals(3, itemList.size());
      assertEquals("http://www.deutsche-digitale-bibliothek.de/lizenzen/rv-fz/", itemList.get(0).getValue());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void rdf_test_dcat() throws FileNotFoundException {
    Schema schema = ConfigurationReader.readSchemaYaml("schemas/rdf-dc-schema.yaml").asSchema();
    assertNotNull(schema);
    assertNotNull(schema.getNamespaces());
    assertEquals(10, schema.getNamespaces().size());

    URL url = this.getClass().getResource("/dc/rdf-ddb-dc-sample-dcat.xml");
    File file = new File(url.getFile());

    XPathWrapper xPathWrapper = new XPathWrapper(file, schema.getNamespaces());
    String idPath = "/rdf:RDF/rdf:Description/dcterms:isReferencedBy/dcat:CatalogRecord/dc:identifier | /rdf:RDF/rdf:Description/dc:identifier/bf:Identifier/rdf:value";
    List<EdmFieldInstance> idList = xPathWrapper.extractFieldInstanceList(idPath);
    assertNotNull(idList);
    assertFalse(idList.isEmpty());
    assertEquals(1, idList.size());
    assertEquals("oai:gesis.izsoz.de:document/13294", idList.get(0).getValue());
  }

  @Test
  public void rdf_test_bf() throws FileNotFoundException {
    Schema schema = ConfigurationReader.readSchemaYaml("schemas/rdf-dc-schema.yaml").asSchema();
    assertNotNull(schema);
    assertNotNull(schema.getNamespaces());
    assertEquals(10, schema.getNamespaces().size());

    URL url = this.getClass().getResource("/dc/rdf-ddb-dc-sample-bf.xml");
    File file = new File(url.getFile());

    XPathWrapper xPathWrapper = new XPathWrapper(file, schema.getNamespaces());
    String idPath = "/rdf:RDF/rdf:Description/dcterms:isReferencedBy/dcat:CatalogRecord/dc:identifier | /rdf:RDF/rdf:Description/dc:identifier/bf:Identifier/rdf:value";
    List<EdmFieldInstance> idList = xPathWrapper.extractFieldInstanceList(idPath);
    assertNotNull(idList);
    assertFalse(idList.isEmpty());
    assertEquals(1, idList.size());
    assertEquals("umbruch:bildarchiv_6", idList.get(0).getValue());
  }

}

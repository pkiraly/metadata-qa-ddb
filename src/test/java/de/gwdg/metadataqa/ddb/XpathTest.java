package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.OaiPmhXPath;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class XpathTest {
  @Test
  public void name() throws FileNotFoundException {
    Schema schema = ConfigurationReader.readSchemaYaml("src/main/resources/marc-schema.yaml").asSchema();
    assertNotNull(schema);
    assertNotNull(schema.getNamespaces());
    assertEquals(8, schema.getNamespaces().size());

    URL url = this.getClass().getResource("/marc/marc.xml");
    File file = new File(url.getFile());

    OaiPmhXPath oaiPmhXPath = new OaiPmhXPath(file, schema.getNamespaces());
    // String idPath = "marc:record/marc:controllfield[@tag=\"001\"]";
    String idPath = "marc:record/marc:controlfield[@tag='001']";
    List<EdmFieldInstance> idList = oaiPmhXPath.extractFieldInstanceList(idPath);
    assertNotNull(idList);
    assertFalse(idList.isEmpty());
    assertEquals(1, idList.size());
    assertEquals("BDR-BV012591623-59024", idList.get(0).getValue());
  }

  @Test
  public void lido() throws FileNotFoundException {
    String path = "//lido:lido";
    Schema schema = ConfigurationReader.readSchemaYaml("src/main/resources/lido-schema.yaml").asSchema();
    URL url = this.getClass().getResource("/lido/20201020_sddm.xml");
    File file = new File(url.getFile());

    OaiPmhXPath oaiPmhXPath = null;
    try {
      String pathTerm = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:rightsResource/lido:rightsType/lido:term";
      String pathConceptId = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:rightsResource/lido:rightsType/lido:conceptID";
      XPathBasedIterator iterator = new XPathBasedIterator(file, path, schema.getNamespaces());
      String xml = iterator.next();
      oaiPmhXPath = new OaiPmhXPath(xml, schema.getNamespaces());

      List<EdmFieldInstance> itemList;
      itemList = oaiPmhXPath.extractFieldInstanceList(pathTerm);
      assertEquals(3, itemList.size());
      assertEquals("Freier Zugang - Rechte vorbehalten", itemList.get(0).getValue());

      itemList = oaiPmhXPath.extractFieldInstanceList(pathConceptId);
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
}

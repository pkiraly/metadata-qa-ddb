package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.json.JsonBranch;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.OaiPmhXPath;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class MetsMods {

  String recordAddress = "//mets:mets";
  OaiPmhXPath oaiPmhXPath;
  Schema schema;

  @Test
  public void recordId() {
    setUpWith("mets.xml");
    JsonBranch p = schema.getPathByLabel("recordId");
    assertEquals("//mods:mods/mods:recordInfo/mods:recordIdentifier", p.getJsonPath());

    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList(p.getJsonPath());
    assertEquals(2, itemList.size());
    assertEquals("981761666", itemList.get(0).getValue());
    assertEquals("http://www.zvdd.de/record/DE-B478/981761666", itemList.get(1).getValue());
  }

  @Test
  public void providerid() {
    setUpWith("mets.xml");
    JsonBranch p = schema.getPathByLabel("providerid");
    assertEquals("//mods:mods/mods:recordInfo/mods:recordContentSource/@valueURI", p.getJsonPath());

    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList(p.getJsonPath());
    assertEquals(1, itemList.size());
    assertEquals("http://ld.zdb-services.de/resource/organisations/DE-B478", itemList.get(0).getValue());
  }

  @Test
  public void thumbnail() {
    setUpWith("mets2.xml");
    JsonBranch p = schema.getPathByLabel("thumbnail");
    assertEquals("//mets:fileSec/mets:fileGrp[@USE=\"DEFAULT\"]/mets:file[@ID]/mets:FLocat/@xlink:href", p.getJsonPath());

    String xpath1 = "//mets:fileSec/mets:fileGrp[@USE=\"DEFAULT\"]/mets:file[@ID]/mets:FLocat/@xlink:href";
    assertEquals(xpath1, p.getJsonPath());
    String xpath = p.getJsonPath();

    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(3, itemList.size());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000001.jpg", itemList.get(0).getValue());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000002.jpg", itemList.get(1).getValue());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000003.jpg", itemList.get(2).getValue());
  }

  @Test
  public void url() {
    setUpWith("mets2.xml");
    JsonBranch p = schema.getPathByLabel("url");
    assertEquals("//mods:location/mods:url", p.getJsonPath());

    String xpath = p.getJsonPath();

    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(2, itemList.size());
    assertEquals("http://digital.staatsbibliothek-berlin.de/dms/werkansicht/?PPN=PPN1001301307", itemList.get(0).getValue());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000001.jpg", itemList.get(1).getValue());
  }

  @Test
  public void title() {
    setUpWith("mets2.xml");
    JsonBranch p = schema.getPathByLabel("dc:title");
    assertEquals("//mods:titleInfo/mods:title", p.getJsonPath());

    String xpath = p.getJsonPath();

    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(2, itemList.size());
    assertEquals("Jubiläum der Firma Max Schwarzlose: Hellmuth Max Schwarzlose, der Gründer der Firma und Kurt Schwarzlose, der derzeitige Seniorchef des Hauses", itemList.get(0).getValue());
    assertEquals("Porträts digital", itemList.get(1).getValue());
  }

  @Test
  public void type() {
    setUpWith("mets2.xml");
    JsonBranch p = schema.getPathByLabel("dc:type");
    assertEquals("//mods:mods/mods:genre", p.getJsonPath());

    String xpath = p.getJsonPath();

    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("monograph", itemList.get(0).getValue());
  }

  private void setUpWith(String fileName) {
    URL url = this.getClass().getResource("/metsmods/" + fileName);
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("src/main/resources/mets-mods-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      String xml = iterator.next();
      oaiPmhXPath = new OaiPmhXPath(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }

}

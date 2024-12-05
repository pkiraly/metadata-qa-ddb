package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.json.DataElement;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
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

public class MetsMods {

  String recordAddress = "//mets:mets";
  XPathWrapper xPathWrapper;
  Schema schema;

  @Test
  public void recordId() {
    setUpWith("mets.xml");
    DataElement p = schema.getPathByLabel("recordId");
    assertEquals("//mods:mods/mods:recordInfo/mods:recordIdentifier", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(2, itemList.size());
    assertEquals("981761666", itemList.get(0).getValue());
    assertEquals("http://www.zvdd.de/record/DE-B478/981761666", itemList.get(1).getValue());
  }

  @Test
  public void providerid() {
    setUpWith("mets.xml");
    DataElement p = schema.getPathByLabel("providerid");
    assertEquals("//mods:mods/mods:recordInfo/mods:recordContentSource/@valueURI", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("http://ld.zdb-services.de/resource/organisations/DE-B478", itemList.get(0).getValue());
  }

  @Test
  public void thumbnail() {
    setUpWith("mets2.xml");
    DataElement p = schema.getPathByLabel("thumbnail");
    assertEquals("//mets:fileSec/mets:fileGrp[@USE=\"DEFAULT\"]/mets:file[@ID]/mets:FLocat/@xlink:href", p.getPath());

    String xpath1 = "//mets:fileSec/mets:fileGrp[@USE=\"DEFAULT\"]/mets:file[@ID]/mets:FLocat/@xlink:href";
    assertEquals(xpath1, p.getPath());
    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(3, itemList.size());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000001.jpg", itemList.get(0).getValue());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000002.jpg", itemList.get(1).getValue());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000003.jpg", itemList.get(2).getValue());
  }

  @Test
  public void url() {
    setUpWith("mets2.xml");
    DataElement p = schema.getPathByLabel("url");
    assertEquals("//mods:location/mods:url", p.getPath());

    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(2, itemList.size());
    assertEquals("http://digital.staatsbibliothek-berlin.de/dms/werkansicht/?PPN=PPN1001301307", itemList.get(0).getValue());
    assertEquals("http://content.staatsbibliothek-berlin.de/dms/PPN1001301307/800/0/00000001.jpg", itemList.get(1).getValue());
  }

  @Test
  public void title() {
    setUpWith("mets2.xml");
    DataElement p = schema.getPathByLabel("dc:title");
    assertEquals("//mods:titleInfo/mods:title", p.getPath());

    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(2, itemList.size());
    assertEquals("Jubiläum der Firma Max Schwarzlose: Hellmuth Max Schwarzlose, der Gründer der Firma und Kurt Schwarzlose, der derzeitige Seniorchef des Hauses", itemList.get(0).getValue());
    assertEquals("Porträts digital", itemList.get(1).getValue());
  }

  @Test
  public void type() {
    setUpWith("mets2.xml");
    DataElement p = schema.getPathByLabel("dc:type");
    assertEquals("//mods:mods/mods:genre", p.getPath());

    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("monograph", itemList.get(0).getValue());
  }

  @Test
  public void dc_type_source() {
    setUpWith("mets2.xml");
    DataElement p = schema.getPathByLabel("Objekttyp_URI");
    assertEquals("//mods:mods/mods:genre/@valueURI", p.getPath());

    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("http://ddb.vocnet.org/hierarchietyp/ht021", itemList.get(0).getValue());
  }

  @Test
  public void rights() {
    setUpWith("mets2.xml");
    DataElement p = schema.getPathByLabel("rights");
    assertEquals("//mods:accessCondition[@type=\"use and reproduction\"]/@xlink:href", p.getPath());

    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(1, itemList.size());
    assertEquals("https://creativecommons.org/licenses/by-nc-sa/4.0/", itemList.get(0).getValue());
  }

  private void setUpWith(String fileName) {
    URL url = this.getClass().getResource("/metsmods/" + fileName);
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("schemas/mets-mods-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      String xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }
}

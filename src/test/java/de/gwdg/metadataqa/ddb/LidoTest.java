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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class LidoTest {

  private Schema schema;
  private String recordAddress = "//lido:lido";
  private XPathWrapper xPathWrapper;

  @Before
  public void setUp() throws Exception {
    URL url = this.getClass().getResource("/lido/20201020_sddm.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("src/main/resources/lido-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      String xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }

  }

  @Test
  public void thumbnail() {
    DataElement p = schema.getPathByLabel("thumbnail");
    assertEquals(
      "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource",
      p.getPath());

    String xpath1 = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource";
    assertEquals(xpath1, p.getPath());
    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(3, itemList.size());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_004.jpg", itemList.get(0).getValue());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_004_back.jpg", itemList.get(1).getValue());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_001.jpg", itemList.get(2).getValue());
  }

  @Test
  public void Mediendatei_Link_Resource() {
    DataElement p = schema.getPathByLabel("Mediendatei_Link_Resource");
    assertEquals("lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource", p.getPath());

    String xpath1 = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource";
    assertEquals(xpath1, p.getPath());
    String xpath = p.getPath();

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(xpath);
    assertEquals(3, itemList.size());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_004.jpg", itemList.get(0).getValue());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_004_back.jpg", itemList.get(1).getValue());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_001.jpg", itemList.get(2).getValue());
  }

  @Test
  public void objekt_im_kontext() {
    DataElement p = schema.getPathByLabel("objekt_im_kontext-record_info_link");
    assertEquals("lido:lido/lido:administrativeMetadata/lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink", p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("http://www.deutschefotothek.de/documents/obj/71818281", itemList.get(0).getValue());
  }

  @Test
  public void Q3_3() {
    String url = "https://ifl.wissensbank.com/cgi-bin/starfetch.exe?kmfOSP3jgaM.yIPUxRzI29Nvv8atZ90ZFILV0RgojGXLVSIgdaug04kgDo52UyOuCMEEIt7q1J7FkBjO.3TUCsxy3Y14X7OBqeNKXMvORo92bNHdXDKaVgTJtOpQRzKQEgmtdE4sA7w/SAm090%2d0130.jpg";
    Pattern p = Pattern.compile("[^a-zA-Z_0-9%\\?\\./:\\-]");
    Matcher m = p.matcher(url);
    assertFalse(m.find());
  }

  @Test
  public void Q2_2() {
    List<String> urls = List.of("http://d-nb.info/gnd/4242325-1", "http://ld.zdb-services.de/resource/organisations/DE-185");
    Pattern p = Pattern.compile("^(DE-\\d+|DE-MUS-\\d+|http://ld.zdb-services.de/[\\w/-]+|\\d{8}|oai\\d{13}|http://d-nb.info/gnd/[\\w-]+)$");
    for (String url : urls) {
      Matcher m = p.matcher(url);
      assertTrue(url + " should fit", m.matches());
    }
  }

  @Test
  public void Q2_2b() {
    Pattern p = Pattern.compile("^http://ld.zdb-services.de/[\\w/-]+$");
    String url = "http://ld.zdb-services.de/resource/organisations/DE-185";
    Matcher m = p.matcher(url);
    assertTrue(url + " should fit", m.matches());
  }

  @Test
  public void Objekttyp() {
    DataElement p = schema.getPathByLabel("Objekttyp");
    assertEquals(
      "lido:lido/lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType/lido:term",
      p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("Vintage Print", itemList.get(0).getValue());
  }

  @Test
  public void Objekttyp_Quellenangabe() {
    DataElement p = schema.getPathByLabel("Objekttyp_Quellenangabe");
    assertEquals(
      "lido:lido/lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType/lido:conceptID/@lido:source",
      p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(0, itemList.size());
    // assertEquals("Vintage Print", itemList.get(0).getValue());
  }

  @Test
  public void Objekttyp_URI() {
    DataElement p = schema.getPathByLabel("Objekttyp_URI");
    assertEquals(
      "lido:lido/lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType/lido:conceptID",
      p.getPath());

    List<EdmFieldInstance> itemList = xPathWrapper.extractFieldInstanceList(p.getPath());
    assertEquals(1, itemList.size());
    assertEquals("http://d-nb.info/gnd/4242325-1", itemList.get(0).getValue());
  }
}

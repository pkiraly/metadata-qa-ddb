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
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LidoTest {

  private Schema schema;
  private String recordAddress = "//lido:lido";
  private OaiPmhXPath oaiPmhXPath;

  @Before
  public void setUp() throws Exception {
    URL url = this.getClass().getResource("/lido/20201020_sddm.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("src/main/resources/lido-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      String xml = iterator.next();
      oaiPmhXPath = new OaiPmhXPath(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }

  }

  @Test
  public void thumbnail() {
    JsonBranch p = schema.getPathByLabel("thumbnail");
    assertEquals("lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource", p.getJsonPath());

    String xpath1 = "lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation/lido:linkResource";
    assertEquals(xpath1, p.getJsonPath());
    String xpath = p.getJsonPath();

    List<EdmFieldInstance> itemList = oaiPmhXPath.extractFieldInstanceList(xpath);
    assertEquals(3, itemList.size());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_004.jpg", itemList.get(0).getValue());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_004_back.jpg", itemList.get(1).getValue());
    assertEquals("http://fotothek.slub-dresden.de/fotos/sddm_hf_0003049_001.jpg", itemList.get(2).getValue());
  }

  @Test
  public void Q3_3() {
    String url = "https://ifl.wissensbank.com/cgi-bin/starfetch.exe?kmfOSP3jgaM.yIPUxRzI29Nvv8atZ90ZFILV0RgojGXLVSIgdaug04kgDo52UyOuCMEEIt7q1J7FkBjO.3TUCsxy3Y14X7OBqeNKXMvORo92bNHdXDKaVgTJtOpQRzKQEgmtdE4sA7w/SAm090%2d0130.jpg";
    Pattern p = Pattern.compile("[^a-zA-Z_0-9%\\?\\./:\\-]");
    Matcher m = p.matcher(url);
    if (m.find()) {
      System.err.println(m.group());
    }
  }
}

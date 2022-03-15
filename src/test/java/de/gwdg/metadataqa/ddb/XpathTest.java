package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.OaiPmhXPath;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

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
}

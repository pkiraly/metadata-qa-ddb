package de.gwdg.metadataqa.ddb;

public class LidoTest extends SchemaBasedTest {
  private String schemaFile = "lido-schema.yaml";
  private String recordAddress = "//lido:lido";

  public void setup(String fileName) throws Exception {
    setup("/lido/" + fileName, schemaFile, recordAddress);
  }
}

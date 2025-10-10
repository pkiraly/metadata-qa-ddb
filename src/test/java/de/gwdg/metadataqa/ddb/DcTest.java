package de.gwdg.metadataqa.ddb;

public class DcTest extends SchemaBasedTest {
  private String schemaFile = "rdf-dc-schema.yaml";
  protected String recordAddress = "//oai:record | //rdf:Description";

  public void setup(String fileName) throws Exception {
    setup("/dc/" + fileName, schemaFile, recordAddress);
  }
}

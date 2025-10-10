package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.TestCase.assertTrue;

public class SchemaBasedTest {
  protected Schema schema;
  protected XPathWrapper xPathWrapper;
  protected String xml;

  public void setup(String fileName, String schemaFile, String recordAddress) throws Exception {
    URL url = this.getClass().getResource(fileName);
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("schemas/" + schemaFile).asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }
}

package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.schema.Schema;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.TestCase.assertTrue;

public class XPathBasedIteratorTest {

  @Test
  public void test() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    URL url = this.getClass().getResource("/dc/dc-ddb-test.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());
    XPathBasedIterator iterator = new XPathBasedIterator(file, "//record");
    while (iterator.hasNext()) {
      System.err.println(iterator.next());
    }

    /*
    NodeList nodeList = iterator.call(file);
    assertEquals(5, nodeList.getLength());
    Node node = nodeList.item(0);
    System.err.println(node.getNamespaceURI());

    System.err.println(XPathBasedIterator.nodeToString(node));
    */
  }

  @Test
  public void testC() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
    String path = App.class.getClassLoader().getResource("dc-schema.yaml").getPath();
    Schema schema = ConfigurationReader.readSchemaYaml(path).asSchema();

    MeasurementConfiguration configuration = new MeasurementConfiguration()
      .disableCompletenessMeasurement()
      .disableFieldExistenceMeasurement()
      .disableFieldCardinalityMeasurement()
      .enableRuleCatalogMeasurement()
      .enableFieldExtractor();

    CalculatorFacade calculator = new CalculatorFacade(configuration)
      .setSchema(schema);
    calculator.configure();

    URL url = this.getClass().getResource("/dc/dc-ddb-test.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());
    XPathBasedIterator iterator = new XPathBasedIterator(file, "//record");
    while (iterator.hasNext()) {
      String csv = calculator.measureAsJson(iterator.next());
      System.err.println(csv);
    }
  }
}

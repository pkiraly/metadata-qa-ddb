package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.schema.Schema;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class XPathBasedIteratorTest {

  @Test
  public void test() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    URL url = this.getClass().getResource("/dc/dc-ddb-test.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());
    XPathBasedIterator iterator = new XPathBasedIterator(file, "//oai:record", Map.of("oai", "http://www.openarchives.org/OAI/2.0/"));
    assertEquals(5, iterator.getLength());
    int i = 0;
    while (iterator.hasNext()) {
      String content = iterator.next();
      assertEquals("<record ", content.substring(0, 8));
      i++;
    }
    assertEquals(5, i);

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
      .enableFieldExtractor()
      .withSolrConfiguration("localhost", "8983", "dummy");

    CalculatorFacade calculator = new CalculatorFacade(configuration)
      .setSchema(schema);
    calculator.configure();

    URL url = this.getClass().getResource("/dc/dc-ddb-test.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());
    XPathBasedIterator iterator = new XPathBasedIterator(file, "//oai:record", Map.of("oai", "http://www.openarchives.org/OAI/2.0/"));
    assertEquals(5, iterator.getLength());
    while (iterator.hasNext()) {
      String json = calculator.measureAsJson(iterator.next());
      assertTrue(json.length() > 0);
      // System.err.println(json);
    }
  }

  @Test
  public void testMets() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    URL url = this.getClass().getResource("/metsmods/mets.xml");
    File file = new File(url.getFile());
    System.err.println(file.getAbsolutePath());
    assertTrue(file.exists());
    // XPathBasedIterator iterator = new XPathBasedIterator(file, "//record");
    // XPathBasedIterator iterator = new XPathBasedIterator(file, "//metadata");
    // XPathBasedIterator iterator = new XPathBasedIterator(file, "//mets:mets"); // 0
    // XPathBasedIterator iterator = new XPathBasedIterator(file, "//mets"); // 0
    Map<String, String> manespaces = Map.of("dv", "http://dfg-viewer.de/", "mods", "http://www.loc.gov/mods/v3", "mets",
      "http://www.loc.gov/METS/", "xlink", "http://www.w3.org/1999/xlink",
      "foaf", "http://xmlns.com/foaf/0.1/");

    XPathBasedIterator iterator = new XPathBasedIterator(file, "//mets:mets", manespaces); // 0
    System.err.println(iterator.getLength());
    while (iterator.hasNext()) {
      Node node = iterator.nextNode();

      NodeList children = node.getChildNodes();
      for (int i=0; i < children.getLength(); i++) {
        Node child = children.item(i);
        if (child.getNodeType() == 1) {
          System.err.println(child);
          System.err.println("name: " + child.getNodeName());
          System.err.println("name: " + child.getAttributes().getLength());

          System.err.println("prefix: " + child.getPrefix());
          System.err.println("local name:" + child.getLocalName());
          System.err.println("namespace URI: " + child.getNamespaceURI());
          System.err.println("mase URI" + child.getBaseURI());
        }
      }
      // System.err.println(iterator.next());
    }

    /*
    NodeList nodeList = iterator.call(file);
    assertEquals(5, nodeList.getLength());
    Node node = nodeList.item(0);
    System.err.println(node.getNamespaceURI());

    System.err.println(XPathBasedIterator.nodeToString(node));
    */
  }
}

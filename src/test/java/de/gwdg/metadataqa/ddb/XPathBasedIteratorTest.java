package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.model.XmlFieldInstance;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XpathEngineFactory;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    Schema schema = ConfigurationReader.readSchemaYaml("schemas/dc-schema.yaml").asSchema();

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

  @Test
  public void testRdfDC() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
    Schema schema = ConfigurationReader.readSchemaYaml("schemas/rdf-dc-schema.yaml").asSchema();

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

    URL url = this.getClass().getResource("/dc/rdf-ddb-dc-sample-dcat.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());
    XPathBasedIterator iterator = new XPathBasedIterator(file, "//rdf:Description",
      schema.getNamespaces());
    assertEquals(1, iterator.getLength());
    while (iterator.hasNext()) {
      String content = iterator.next();
      Selector<? extends XmlFieldInstance> cache = SelectorFactory.getInstance(schema.getFormat(), content, schema.getNamespaces());
      System.err.println(content);
      List<? extends XmlFieldInstance> values = cache.get(schema.getPaths().get(0));
      System.err.println(values);

      // String json = calculator.measureAsJson(iterator.next());
      // assertTrue(json.length() > 0);
      // System.err.println(json);
    }
  }

  @Test
  public void testOr_dcat() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    String xpath = "/rdf:RDF/rdf:Description/dcterms:isReferencedBy/dcat:CatalogRecord/dc:identifier | /rdf:RDF/rdf:Description/dc:identifier/bf:Identifier/rdf:value";
    NodeList nodes = runXpath("schemas/rdf-dc-schema.yaml", "/dc/rdf-ddb-dc-sample-dcat.xml", xpath);
    assertEquals(1, nodes.getLength());
    List<String> identifiers = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); ++i) {
      identifiers.add(nodes.item(i).getTextContent());
    }
    assertEquals(1, identifiers.size());
    assertEquals("oai:gesis.izsoz.de:document/13294", identifiers.get(0));
  }

  @Test
  public void testOr_bf() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    String xpath = "/rdf:RDF/rdf:Description/dcterms:isReferencedBy/dcat:CatalogRecord/dc:identifier | /rdf:RDF/rdf:Description/dc:identifier/bf:Identifier/rdf:value";
    NodeList nodes = runXpath("schemas/rdf-dc-schema.yaml", "/dc/rdf-ddb-dc-sample-bf.xml", xpath);
    assertEquals(1, nodes.getLength());
    List<String> identifiers = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); ++i) {
      identifiers.add(nodes.item(i).getTextContent());
    }
    assertEquals(1, identifiers.size());
    assertEquals("umbruch:bildarchiv_6", identifiers.get(0));
  }

  private NodeList runXpath(String schemaFile, String xmlFile, String xpath)
      throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    Schema schema = ConfigurationReader.readSchemaYaml(schemaFile).asSchema();

    XPath xpathEngine = XpathEngineFactory.initializeEngine(schema.getNamespaces());
    URL url = this.getClass().getResource(xmlFile);
    File file = new File(url.getFile());

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(file.getPath());

    XPathExpression expr = xpathEngine.compile(xpath);
    return (NodeList)expr.evaluate(document, XPathConstants.NODESET);
  }

}

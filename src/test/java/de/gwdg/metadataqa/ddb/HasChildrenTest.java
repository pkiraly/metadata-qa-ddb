package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.schema.ApplicationScope;
import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.json.DataElement;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.rule.logical.LogicalChecker;
import de.gwdg.metadataqa.api.rule.singlefieldchecker.HasChildrenChecker;
import de.gwdg.metadataqa.api.rule.singlefieldchecker.MinLengthChecker;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class HasChildrenTest {
  private Schema schema;
  private String recordAddress = "//lido:lido";
  private XPathWrapper xPathWrapper;
  private String xml;

  @Before
  public void setUp() throws Exception {
    URL url = this.getClass().getResource("/lido/Q-9.4.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("schemas/lido-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testHasChildren_raw() {
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    String path = "lido:lido/lido:descriptiveMetadata/lido:objectRelationWrap/lido:subjectWrap/lido:subjectSet/lido:subject/lido:subjectDate/lido:date";
    List<Node> nodes = (List<Node>) cache.getFragment(path);
    List<String> children = List.of("lido:earliestDate", "lido:latestDate");
    assertEquals(2, nodes.size());
    boolean found = true;
    for (Node node : nodes) {
      for (String childPath : children) {
        List values = cache.get(childPath, childPath, node);
        if (values.isEmpty()) {
          found = false;
          break;
        }
      }
      if (!found)
        break;
    }
    assertTrue(found);
  }

  @Test
  public void testHasChildren_advanced() {
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (checker.getId().equals("Q-9.4a")) {
        HasChildrenChecker checker1 = (HasChildrenChecker) checker;
        checker1.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-9.4a").getStatus()
    );
  }

  @Test
  public void minlengthScope() throws Exception {
    URL url = this.getClass().getResource("/lido/minlength-scope.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("schemas/lido-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      xml = iterator.next();
      System.err.println(xml);
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());

      Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
      String path = "lido:lido/lido:objectDescriptionSet/lido:descriptiveNoteValue";
      DataElement dataElement = new DataElement(path);

      MinLengthChecker checker = new MinLengthChecker(dataElement, 50);
      checker.setScope(ApplicationScope.anyOf);
      checker.setId("Q-9.8");

      FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
      checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);

      System.err.println(fieldCounter.get("Q-9.8").getStatus());

    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }
}

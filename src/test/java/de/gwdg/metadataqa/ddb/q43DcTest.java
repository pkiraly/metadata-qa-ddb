package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class q43DcTest {
  private Schema schema;
  private String recordAddress = "//oai:record | //rdf:Description";
  private XPathWrapper xPathWrapper;
  private String xml;

  public void setup(String fileName) throws Exception {
    URL url = this.getClass().getResource("/dc/" + fileName);
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("schemas/rdf-dc-schema.yaml").asSchema();
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void empty() throws Exception {
    setup("Q-4.3-empty.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.pre", "Q-4.has_element1", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3c2", "Q-4.3c3", "Q-4.3cd",
      "Q-4.3d",
      "Q-4.3.is_empty", "Q-4.3.is_missing",
      "Q-4.3");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO: FAILED
      fieldCounter.get("Q-4.3").getStatus()
    );
  }

  @Test
  public void empty2() throws Exception {
    setup("Q-4.3-empty.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0.hasView");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.0.hasView").getStatus()
    );
  }

  @Test
  public void missing() throws Exception {
    setup("Q-4.3-missing.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.pre", "Q-4.pre2", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3d",
      "Q-4.3"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        // checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-4.3").getStatus()
    );
  }
}

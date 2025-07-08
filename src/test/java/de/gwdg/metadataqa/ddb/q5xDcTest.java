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

public class q5xDcTest {
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
  public void success() throws Exception {
    setup("Q-5.x.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-5.0a", "Q-5.0ax", // edm:object
      "Q-5.0b", "Q-5.0bx", // edm:isShownBy
      "Q-5.0c", "Q-5.0cx", // edm:hasView
      "Q-5.0d", "Q-5.0dx", // edm:isShownAt
      "Q-5.0e", "Q-5.0ex", // dcterms:license
      "Q-5.0f", "Q-5.0fx", // dc:rights
      "Q-5.pre", "Q-5.1"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-5.1").getStatus()
    );
  }

  @Test
  public void isNa() throws Exception {
    setup("Q-5.x.failed.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-5.0a", "Q-5.0ax", // edm:object
      "Q-5.0b", "Q-5.0bx", // edm:isShownBy
      "Q-5.0c", "Q-5.0cx", // edm:hasView
      "Q-5.0d", "Q-5.0dx", // edm:isShownAt
      "Q-5.0e", "Q-5.0ex", // dcterms:license
      "Q-5.0f", "Q-5.0fx", // dc:rights
      "Q-5.pre", "Q-5.1", "Q-5.2"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-5.1").getStatus()
    );
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-5.2").getStatus()
    );
  }
}

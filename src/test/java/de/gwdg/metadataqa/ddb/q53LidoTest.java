package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.rule.logical.AndChecker;
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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class q53LidoTest {
  private Schema schema;
  private String recordAddress = "//lido:lido";
  private XPathWrapper xPathWrapper;
  private String xml;

  private void prepare(String xmlFile) {
    URL url = this.getClass().getResource(xmlFile); // "/lido/test-Q-5.2_UND.xml");
    File file = new File(url.getFile());
    assertTrue(file.exists());

    try {
      schema = ConfigurationReader.readSchemaYaml("schemas/lido-schema.yaml").asSchema();
      // schema.getPathByLabel("rights")
      XPathBasedIterator iterator = new XPathBasedIterator(file, recordAddress, schema.getNamespaces());
      xml = iterator.next();
      xPathWrapper = new XPathWrapper(xml, schema.getNamespaces());
    } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void test_empty() {
    prepare("/lido/test-Q-5.3.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-5.1", "Q-5.3");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        if (checker instanceof AndChecker) {
          AndChecker achecker = (AndChecker) checker;
          for (RuleChecker child : achecker.getCheckers()) {
            child.setDebug();
          }
        }
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-5.3").getStatus()
    );
  }
}

package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.schema.Rule;
import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.schema.SchemaUtils;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class q94LidoTest {
  private Schema schema;
  private String recordAddress = "//lido:lido";
  private XPathWrapper xPathWrapper;
  private String xml;

  @Before
  public void setUp() throws Exception {
    URL url = this.getClass().getResource("/lido/test-Q-9.4-diplsayPlace.xml");
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
  public void test() {
    Rule rule94 = SchemaUtils.getRuleById(schema, "Q-9.4");
    assertEquals(
      List.of("Q-9.4a", "Q-9.4b", "Q-9.4c"),
      SchemaUtils.getAllDependencies(schema, rule94)
        .stream()
        .sorted()
        .collect(Collectors.toList())
    );

    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.4a", "Q-9.4b", "Q-9.4c", "Q-9.4");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    // System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-9.4").getStatus()
    );
  }
}

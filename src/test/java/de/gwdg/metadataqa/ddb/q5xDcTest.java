package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import org.junit.Test;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class q5xDcTest extends DcTest {
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

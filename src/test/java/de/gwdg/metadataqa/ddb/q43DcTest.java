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

public class q43DcTest extends DcTest {
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

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

public class q41DcTest extends DcTest {
  @Test
  public void a() throws Exception {
    setup("Q-4.1a.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.pre", "Q-4.1");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.1").getStatus()
    );
  }

  @Test
  public void b() throws Exception {
    setup("Q-4.1b.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.pre", "Q-4.1");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.1").getStatus()
    );
  }

  @Test
  public void c() throws Exception {
    setup("Q-4.1c.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.pre", "Q-4.0", "Q-4.1");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        // checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.1").getStatus()
    );
  }
}

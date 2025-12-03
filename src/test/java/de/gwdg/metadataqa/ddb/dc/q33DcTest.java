package de.gwdg.metadataqa.ddb.dc;

import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.ddb.DcTest;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class q33DcTest extends DcTest {
  @Test
  public void emptyTag() throws Exception {
    setup("Q-3.3-empty.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.wa", "Q-3.w", "Q-3.oa", "Q-3.o", "Q-3.ia", "Q-3.i", "Q-3.pre", "Q-3.0", "Q-3.3");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-3.3").getStatus()
    );
  }

  @Test
  public void missingTag() throws Exception {
    setup("Q-3.3-missing.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.wa", "Q-3.w", "Q-3.oa", "Q-3.o", "Q-3.ia", "Q-3.i", "Q-3.pre", "Q-3.0", "Q-3.3");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-3.3").getStatus()
    );
  }

  @Test
  public void smallImage() throws Exception {
    // the image size is 378 × 520 pixels
    setup("Q-3.3-small-image.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.wa", "Q-3.w", "Q-3.oa", "Q-3.o", "Q-3.ia", "Q-3.i", "Q-3.pre", "Q-3.0", "Q-3.3");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-3.3").getStatus()
    );
  }
}

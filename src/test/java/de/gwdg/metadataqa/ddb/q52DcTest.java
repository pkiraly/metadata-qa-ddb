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

public class q52DcTest extends DcTest {

  @Test
  public void dctermslicensce_leere_Lizenz_fails() throws Exception {
    setup("Q-5.x-dctermslicensce_leere_Lizenz.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-5.0a", "Q-5.0b", "Q-5.0c", "Q-5.0d", "Q-5.0e", "Q-5.0f",
      "Q-5.0ax", "Q-5.0bx", "Q-5.0cx", "Q-5.0dx", "Q-5.0ex", "Q-5.0fx",
      "Q-5.pre", "Q-5.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
        RuleCheckingOutputStatus.NA, // TODO: should be FAILED
        fieldCounter.get("Q-5.2").getStatus());
  }
}

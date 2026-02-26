package de.gwdg.metadataqa.ddb.lido;

import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.ddb.LidoTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class q52LidoTest extends LidoTest  {

  @Test
  public void test_UND() throws Exception {
    setup("Q-5.2_UND.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-5.1", "Q-5.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-5.2").getStatus()
    );
  }

  @Test
  public void test_NKC() throws Exception {
    setup("Q-5.2_NKC.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-5.1", "Q-5.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-5.2").getStatus()
    );
  }

}

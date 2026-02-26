package de.gwdg.metadataqa.ddb.lido;

import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.rule.logical.AndChecker;
import de.gwdg.metadataqa.ddb.LidoTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class q53LidoTest extends LidoTest {

  @Test
  public void test_empty() throws Exception {
    setup("Q-5.3.xml");
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

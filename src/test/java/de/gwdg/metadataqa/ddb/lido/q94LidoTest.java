package de.gwdg.metadataqa.ddb.lido;

import de.gwdg.metadataqa.api.configuration.schema.Rule;
import de.gwdg.metadataqa.api.counter.FieldCounter;
import de.gwdg.metadataqa.api.model.selector.Selector;
import de.gwdg.metadataqa.api.model.selector.SelectorFactory;
import de.gwdg.metadataqa.api.rule.RuleChecker;
import de.gwdg.metadataqa.api.rule.RuleCheckerOutput;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputStatus;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.schema.SchemaUtils;
import de.gwdg.metadataqa.ddb.LidoTest;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class q94LidoTest extends LidoTest {

  @Test
  public void displayPlace() throws Exception {
    setup("Q-9.4-displayPlace.xml");
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
      RuleCheckingOutputStatus.FAILED, // TODO: NA
      fieldCounter.get("Q-9.4").getStatus()
    );
  }

  @Test
  public void subjectActor_fehlt() throws Exception {
    setup("Q-9.4-subjectActor_fehlt.xml");
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
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    // Q-9.4a=NA, Q-9.4b=NA, Q-9.4c=NA, Q-9.4=NA
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-9.4").getStatus()
    );
  }


  @Test
  public void subjectDate_display() throws Exception {
    setup("Q-9.4-subjectDate_display.xml");
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
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    // Q-9.4a=NA, Q-9.4b=1, Q-9.4c=1, Q-9.4=0
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-9.4").getStatus()
    );
  }
}

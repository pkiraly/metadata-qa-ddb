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

public class q95DcTest extends DcTest {
  @Test
  public void empty() throws Exception {
    setup("Q-9.5.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5a",
      "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void empty2() throws Exception {
    setup("Q-9.5-dctermsTemporal_Term.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5a", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void empty3() throws Exception {
    setup("Q-9.5-dctermsAgent_nur_Term.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5a", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void empty4() throws Exception {
    setup("Q-9.5-dctermsTemporal_URI.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5a", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  /**
   * rdf:Description/dc:subject/skos:Concept/@rdf:about
   * rdf:Description/dc:subject/dcterms:Agent/@rdf:about
   * rdf:Description/dcterms:spatial/dcterms:Location/@rdf:about
   * rdf:Description/dcterms:temporal/skos:Concept/@rdf:about
   * Q-9.5a:
   *   and:
   *   - minCount: 1
   *     scope: allOf
   *   - minLength: 1
   *     scope: allOf
   *   - hasChildren: ["@rdf:about"]
   *     scope: anyOf
   * Q-9.5:
   *   and:
   *   - dependencies: [Q-9.5a]
   *   - pattern: ^http.*$
   */
  public void dctermsSpatial_simple_should_be_NA() throws Exception {
    setup("Q-9.5-dctermsSpatial_simple.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5a", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    // fieldMap={Q-9.5a=0, Q-9.5=0}
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO should be NA
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void dctermsAgent_Wikidata_URI_nur_URI_should_be_NA() throws Exception {
    setup("Q-9.5-dctermsAgent_Wikidata_URI_nur_URI.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5a", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO should be PASSED
      fieldCounter.get("Q-9.5").getStatus()
    );
  }
}

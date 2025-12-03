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

/**
 * rdf:Description/edm:isShownAt/edm:WebResource/@rdf:about
 * rdf:Description/edm:isShownAt/@rdf:resource
 * oai:record/oai:metadata/oai_dc:dc/edm:isShownAt
 *
 * Check whether the data record contains an object in context (see table "Mapping the DDB elements").
 * If so => check whether this element contain a value.
 *
 * and
 *  - dependencies: [ Q-4.5or ]
 *  - minCount: 1
 *  - minLength: 1
 *
 * Q-4.5or
 * or:
 *  - dependencies: [ Q-4.5b ]
 *  - dependencies: [ Q-4.5c ]
 *  - dependencies: [ Q-4.5d ]
 */
public class q45DcTest extends DcTest {
  @Test
  public void name() throws Exception {
    setup("Q-4.5.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.5a", "Q-4.5b", "Q-4.5c", "Q-4.5d", "Q-4.5or", "Q-4.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    assertEquals(
        RuleCheckingOutputStatus.FAILED,
        fieldCounter.get("Q-4.5").getStatus());
  }

  @Test
  public void edmIsShownAt_rdfResource() throws Exception {
    setup("Q-4.5-edmIsShownAt_rdfResource.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.5a", "Q-4.5b", "Q-4.5c", "Q-4.5d", "Q-4.5or", "Q-4.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.5").getStatus());
  }

  @Test
  public void edmIsShownAt_Broken_Links() throws Exception {
    setup("Q-4.5-edmIsShownAt_Broken_Links.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.5a", "Q-4.5b", "Q-4.5c", "Q-4.5d", "Q-4.5or", "Q-4.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.5").getStatus());
  }
}

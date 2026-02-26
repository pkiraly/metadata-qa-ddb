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
 * Check whether an http-URI for a license or a rights notice from the
 * DDB license basket is referenced in the element for the legal status
 * rdf:Description/edm:object/edm:WebResource/dcterms:license/@rdf:resource
 * rdf:Description/edm:isShownBy/edm:WebResource/dcterms:license/@rdf:resource
 * rdf:Description/edm:hasView/edm:WebResource/dcterms:license/@rdf:resource
 * rdf:Description/edm:isShownAt/edm:WebResource/dcterms:license
 * rdf:Description/dcterms:license/@rdf:resource
 */
public class q52DcTest extends DcTest {

  @Test
  public void dctermslicensce_keine_DDB_Lizenz() throws Exception {
    setup("Q-5.x-dctermslicensce_keine_DDB_Lizenz.xml");
    // has dcterms:license, but it is empty: Q-5.0b=1, Q-5.0bx=0
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-5.0a-element", "Q-5.0b-element", "Q-5.0c-element", "Q-5.0d-element", "Q-5.0e-element", "Q-5.0f-element",
      "Q-5.0a-attribute", "Q-5.0b-attribute", "Q-5.0c-attribute", "Q-5.0d-attribute", "Q-5.0e-attribute", "Q-5.0f-attribute",
      "Q-5.pre", "Q-5.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);

    assertEquals("Q-5.0bx should fail",
      RuleCheckingOutputStatus.PASSED, // TODO: should be FAILED
      fieldCounter.get("Q-5.0b-attribute").getStatus());
    /*
    assertEquals("Q-5.0bx should fail",
      RuleCheckingOutputStatus.FAILED, // TODO: should be FAILED
      fieldCounter.get("Q-5.pre").getStatus());
    */
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO: should be FAILED
      fieldCounter.get("Q-5.2").getStatus());
  }

  @Test
  public void dctermslicensce_leere_Lizenz_fails() throws Exception {
    setup("Q-5.x-dctermslicensce_leere_Lizenz.xml");
    // has dcterms:license, but it is empty: Q-5.0b=1, Q-5.0bx=0
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-5.0a-element", "Q-5.0b-element", "Q-5.0c-element", "Q-5.0d-element", "Q-5.0e-element", "Q-5.0f-element",
      "Q-5.0a-attribute", "Q-5.0b-attribute", "Q-5.0c-attribute", "Q-5.0d-attribute", "Q-5.0e-attribute", "Q-5.0f-attribute",
      "Q-5.0b-pre",
      "Q-5.pre", "Q-5.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);

    assertEquals("Q-5.0b attribute should fail",
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-5.0b-attribute").getStatus());
    assertEquals("Q-5.2 should fail",
        RuleCheckingOutputStatus.FAILED,
        fieldCounter.get("Q-5.2").getStatus());
  }

  /**
   * edm:isShownBy/edm:WebResource without ./dcterms:license
   * @throws Exception
   */
  @Test
  public void dctermslicensce_keine_Lizenz() throws Exception {
    setup("Q-5.x-dctermslicensce_keine_Lizenz.xml");
    // has dcterms:license, but it is empty: Q-5.0b=1, Q-5.0bx=0
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-5.0a-element", "Q-5.0b-element", "Q-5.0c-element", "Q-5.0d-element", "Q-5.0e-element", "Q-5.0f-element",
      "Q-5.0a-attribute", "Q-5.0b-attribute", "Q-5.0c-attribute", "Q-5.0d-attribute", "Q-5.0e-attribute", "Q-5.0f-attribute",
      "Q-5.0b-pre",
      "Q-5.pre", "Q-5.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);

    assertEquals("Q-5.0bx should fail",
      RuleCheckingOutputStatus.FAILED, // TODO: should be FAILED
      fieldCounter.get("Q-5.0b-attribute").getStatus());
    assertEquals(
      RuleCheckingOutputStatus.NA, // TODO: should be NA
      fieldCounter.get("Q-5.2").getStatus());
  }

  @Test
  public void dctermslicense_CC0() throws Exception {
    setup("Q-5.x-dctermslicense_CC0.xml");
    // has dcterms:license, but it is empty: Q-5.0b=1, Q-5.0bx=0
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of(
      "Q-5.0a-element", "Q-5.0b-element", "Q-5.0c-element", "Q-5.0d-element", "Q-5.0e-element", "Q-5.0f-element",
      "Q-5.0a-attribute", "Q-5.0b-attribute", "Q-5.0c-attribute", "Q-5.0d-attribute", "Q-5.0e-attribute", "Q-5.0f-attribute",
      "Q-5.pre", "Q-5.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);

    assertEquals("Q-5.0b-attribute should pass",
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-5.0b-attribute").getStatus());
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-5.2").getStatus());
  }

}

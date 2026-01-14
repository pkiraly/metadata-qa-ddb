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

public class q52DcTest extends DcTest {

  @Test
  /**
   * Check whether an http-URI for a license or a rights notice from the
   * DDB license basket is referenced in the element for the legal status
   * rdf:Description/edm:object/edm:WebResource/dcterms:license/@rdf:resource
   * rdf:Description/edm:isShownBy/edm:WebResource/dcterms:license/@rdf:resource
   * rdf:Description/edm:hasView/edm:WebResource/dcterms:license/@rdf:resource
   * rdf:Description/edm:isShownAt/edm:WebResource/dcterms:license
   * rdf:Description/dcterms:license/@rdf:resource
   */
  public void dctermslicensce_leere_Lizenz_fails() throws Exception {
    setup("Q-5.x-dctermslicensce_leere_Lizenz.xml");
    // has dcterms:license, but it is empty: Q-5.0b=1, Q-5.0bx=0
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

    assertEquals("Q-5.0bx should fail",
      RuleCheckingOutputStatus.FAILED, // TODO: should be FAILED
      fieldCounter.get("Q-5.0bx").getStatus());
    assertEquals(
        RuleCheckingOutputStatus.FAILED, // TODO: should be FAILED
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

    assertEquals("Q-5.0bx should fail",
      RuleCheckingOutputStatus.FAILED, // TODO: should be FAILED
      fieldCounter.get("Q-5.0bx").getStatus());
    assertEquals(
      RuleCheckingOutputStatus.NA, // TODO: should be NA
      fieldCounter.get("Q-5.2").getStatus());
  }
}

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
 * Q-3.6
 *     dcatCatalogRecord_dcCreator_Element_fehlt.xml failed. expected: 0, actual: NA
 *     edmObject - fehlt.xml failed. expected: 0, actual: NA
 *   # edmObject - leer.xml passed
 *   # edmObject.xml passed
 *   # edmObject_broken_Links.xml passed
 *   # edmObject_ohne_rdfabout.xml passed
 *
 * rdf:Description/edm:object/edm:WebResource/@rdf:about |
 * rdf:Description/edm:object/@rdf:resource |
 * oai:record/dc:identifier[@type='binary']
 */
public class q36DcTest extends DcTest {
  @Test
  public void dcatCatalogRecord_dcCreator_Element_fehlt() throws Exception {
    setup("Q-3.6-dcatCatalogRecord_dcCreator_Element_fehlt.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.wa", "Q-3.w", "Q-3.oa", "Q-3.o", "Q-3.ia", "Q-3.i", "Q-3.pre", "Q-3.0", "Q-3.6");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-3.6").getStatus()
    );
  }

  @Test
  public void edmObject_fehlt() throws Exception {
    setup("Q-3.6-edmObject-fehlt.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.wa", "Q-3.w", "Q-3.oa", "Q-3.o", "Q-3.ia", "Q-3.i", "Q-3.pre", "Q-3.0", "Q-3.6");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-3.6").getStatus()
    );
  }
}

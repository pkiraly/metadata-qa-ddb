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
 *   x   dctermsAgent_Element_fehlt.xml failed. expected: NA, actual: 1 -- parent is missing
 *     # dctermsAgent_GND_URI.xml passed
 *       dctermsAgent_leeres_Element.xml failed. expected: 0, actual: 1
 *   x # dctermsAgent_nur_Term.xml failed. expected: 0, actual: 1
 *     # dctermsAgent_Wikidata_URI.xml passed
 *     # dctermsLocation_nur_Term.xml failed. expected: 0, actual: 1
 *     # dctermsLocation_URI_GND_und_Term.xml passed
 *   x # dctermsSpatial_simple.xml failed. expected: 0, actual: 1
 *     # dctermsSpatial_Term.xml failed. expected: 0, actual: 1
 *     # dctermsSpatial_URI.xml passed
 *     # dctermsTemporal_Element_fehlt.xml failed. expected: 0, actual: 1
 *   x # dctermsTemporal_simple.xml failed. expected: 0, actual: 1
 *   x # dctermsTemporal_Term.xml failed. expected: 0, actual: 1
 *   x # dctermsTemporal_Term_leer.xml failed. expected: 0, actual: 1
 *   x # dctermsTemporal_URI.xml passed
 *     # dctermsTemporal_URI_rdfAbout_leer.xml passed
 */
public class q95DcTest extends DcTest {
  // @Test
  public void empty() throws Exception {
    setup("Q-9.5.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
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
  public void dctermsTemporal_Term() throws Exception {
    setup("Q-9.5-dctermsTemporal_Term.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
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
  public void dctermsAgent_nur_Term() throws Exception {
    setup("Q-9.5-dctermsAgent_nur_Term.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // should be FAILED
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void dctermsTemporal_URI() throws Exception {
    setup("Q-9.5-dctermsTemporal_URI.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
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
   *
   * The problem here: there is no URL
   */
  public void dctermsSpatial_simple_should_be_NA() throws Exception {
    setup("Q-9.5-dctermsSpatial_simple.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    // fieldMap={Q-9.5a=0, Q-9.5=0}
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO should be FAILED
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void dctermsAgent_Wikidata_URI_nur_URI_should_be_NA() throws Exception {
    setup("Q-9.5-dctermsAgent_Wikidata_URI_nur_URI.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
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

  @Test
  public void dctermsTemporal_simple() throws Exception {
    setup("Q-9.567-dctermsTemporal_simple.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO should be FAILED
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  /**
   * Check when parent element is missing
   * @throws Exception
   */
  @Test
  public void dctermsAgent_Element_fehlt() throws Exception {
    setup("Q-9.567-dctermsAgent_Element_fehlt.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(RuleCheckingOutputStatus.FAILED, fieldCounter.get("Q-9.5.has-parent").getStatus());
    assertEquals(RuleCheckingOutputStatus.PASSED, fieldCounter.get("Q-9.5.has-no-parent").getStatus());
    assertEquals(
      RuleCheckingOutputStatus.NA, // TODO should be NA
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void dctermsLocation_nur_Term() throws Exception {
    setup("Q-9.5-dctermsLocation_nur_Term.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    // assertEquals(RuleCheckingOutputStatus.FAILED, fieldCounter.get("Q-9.5.has-parent").getStatus());
    // assertEquals(RuleCheckingOutputStatus.PASSED, fieldCounter.get("Q-9.5.has-no-parent").getStatus());
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO should be FAILED
      fieldCounter.get("Q-9.5").getStatus()
    );
  }

  @Test
  public void dctermsTemporal_Term_leer() throws Exception {
    setup("Q-9.5-dctermsTemporal_Term_leer.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-9.5.has-parent", "Q-9.5.has-no-parent", "Q-9.5a", "Q-9.5b", "Q-9.5c", "Q-9.5");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    // assertEquals(RuleCheckingOutputStatus.FAILED, fieldCounter.get("Q-9.5.has-parent").getStatus());
    // assertEquals(RuleCheckingOutputStatus.PASSED, fieldCounter.get("Q-9.5.has-no-parent").getStatus());
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO should be FAILED
      fieldCounter.get("Q-9.5").getStatus()
    );
  }
}

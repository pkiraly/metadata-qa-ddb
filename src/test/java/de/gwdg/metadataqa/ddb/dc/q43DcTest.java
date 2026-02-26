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

public class q43DcTest extends DcTest {
  @Test
  public void empty() throws Exception {
    setup("Q-4.3-empty.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.0.isShownBy-WebResource", "Q-4.0.isShownBy",
      "Q-4.0.hasView-WebResource", "Q-4.0.hasView",
      "Q-4.0.isShownAt-WebResource", "Q-4.0.isShownAt", "Q-4.0.identifier",
      "Q-4.0.isShownBy-WebResource-about", "Q-4.0.isShownBy-resource",
      "Q-4.0.hasView-WebResource-about", "Q-4.0.hasView-resource",
      "Q-4.0.isShownAt-WebResource-about", "Q-4.0.isShownAt-resource",
      "Q-4.pre", "Q-4.has_element1", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3c2", "Q-4.3c3", "Q-4.3cd",
      "Q-4.3d",
      "Q-4.3.is_not_empty", "Q-4.3.is_missing", "Q-4.3.value_fits",
      "Q-4.3");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED, // TODO: FAILED
      fieldCounter.get("Q-4.3").getStatus()
    );
  }

  @Test
  public void empty2() throws Exception {
    setup("Q-4.3-empty.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0.hasView");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.0.hasView").getStatus()
    );
  }

  @Test
  public void missing() throws Exception {
    setup("Q-4.3-missing.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.0.isShownBy-WebResource", "Q-4.0.isShownBy",
      "Q-4.0.hasView-WebResource", "Q-4.0.hasView",
      "Q-4.0.isShownAt-WebResource", "Q-4.0.isShownAt", "Q-4.0.identifier",
      "Q-4.0.isShownBy-WebResource-about", "Q-4.0.isShownBy-resource",
      "Q-4.0.hasView-WebResource-about", "Q-4.0.hasView-resource",
      "Q-4.0.isShownAt-WebResource-about", "Q-4.0.isShownAt-resource",
      "Q-4.pre", "Q-4.pre2", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3d",
      "Q-4.3"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        // checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-4.3").getStatus()
    );
  }

  @Test
  public void edmIsShownAt_Broken_Links() throws Exception {
    setup("Q-4.3-edmIsShownAt_Broken_Links.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.pre", "Q-4.pre2", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3d",
      "Q-4.0.isShownBy-WebResource", "Q-4.0.isShownBy",
      "Q-4.0.hasView-WebResource", "Q-4.0.hasView",
      "Q-4.0.isShownAt-WebResource", "Q-4.0.isShownAt", "Q-4.0.identifier",
      "Q-4.0.isShownBy-WebResource-about", "Q-4.0.isShownBy-resource",
      "Q-4.0.hasView-WebResource-about", "Q-4.0.hasView-resource",
      "Q-4.0.isShownAt-WebResource-about", "Q-4.0.isShownAt-resource", "Q-4.0.identifier-type",
      "Q-4.has_element1", "Q-3.x", "Q-4.pre",
      "Q-4.3.is_not_empty", "Q-4.3.is_missing", "Q-4.3.value_fits",
      "Q-4.3"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        // checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.FAILED,
      fieldCounter.get("Q-4.3").getStatus()
    );
  }

  @Test
  public void edmHasView_isShownAt_isShownBy_fehlt() throws Exception {
    setup("Q-4.3-edmHasView_isShownAt_isShownBy_fehlt.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.pre", "Q-4.pre2", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3d",
      "Q-4.0.isShownBy-WebResource", "Q-4.0.isShownBy",
      "Q-4.0.hasView-WebResource", "Q-4.0.hasView",
      "Q-4.0.isShownAt-WebResource", "Q-4.0.isShownAt", "Q-4.0.identifier",
      "Q-4.0.isShownBy-WebResource-about", "Q-4.0.isShownBy-resource",
      "Q-4.0.hasView-WebResource-about", "Q-4.0.hasView-resource",
      "Q-4.0.isShownAt-WebResource-about", "Q-4.0.isShownAt-resource",
      "Q-4.0.identifier-type",
      "Q-4.has_element1", "Q-3.x", "Q-4.pre",
      "Q-4.3.is_not_empty", "Q-4.3.is_missing", "Q-4.3.value_fits",
      "Q-4.3"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        // checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-4.3").getStatus()
    );
  }

  @Test
  public void edmIsShownAt_rdfResource() throws Exception {
    setup("Q-4.3-edmIsShownAt_rdfResource.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.pre", "Q-4.pre2", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3d",
      "Q-4.0.isShownBy-WebResource", "Q-4.0.isShownBy",
      "Q-4.0.hasView-WebResource", "Q-4.0.hasView",
      "Q-4.0.isShownAt-WebResource", "Q-4.0.isShownAt", "Q-4.0.identifier",
      "Q-4.0.isShownBy-WebResource-about", "Q-4.0.isShownBy-resource",
      "Q-4.0.hasView-WebResource-about", "Q-4.0.hasView-resource",
      "Q-4.0.isShownAt-WebResource-about", "Q-4.0.isShownAt-resource", "Q-4.0.identifier-type",
      "Q-4.has_element1", "Q-3.x", "Q-4.pre",
      "Q-4.3.is_not_empty", "Q-4.3.is_missing", "Q-4.3.value_fits",
      "Q-4.3"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        // checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.3").getStatus()
    );
  }

  @Test
  public void edmIsShownAt_WebResource() throws Exception {
    setup("Q-4.3-edmIsShownAt_WebResource.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-4.0a", "Q-4.0ax",
      "Q-4.0b", "Q-4.0bx",
      "Q-4.0c", "Q-4.0cx",
      "Q-4.0d", "Q-4.0dx",
      "Q-4.0e", "Q-4.0ex",
      "Q-4.0f", "Q-4.0fx",
      "Q-4.0g", "Q-4.0gx",
      "Q-4.pre", "Q-4.pre2", "Q-3.x", "Q-4.x",
      "Q-4.3a", "Q-4.3b", "Q-4.3c", "Q-4.3d",
      "Q-4.0.isShownBy-WebResource", "Q-4.0.isShownBy",
      "Q-4.0.hasView-WebResource", "Q-4.0.hasView",
      "Q-4.0.isShownAt-WebResource", "Q-4.0.isShownAt", "Q-4.0.identifier",
      "Q-4.0.isShownBy-WebResource-about", "Q-4.0.isShownBy-resource",
      "Q-4.0.hasView-WebResource-about", "Q-4.0.hasView-resource",
      "Q-4.0.isShownAt-WebResource-about", "Q-4.0.isShownAt-resource", "Q-4.0.identifier-type",
      "Q-4.has_element1", "Q-3.x", "Q-4.pre",
      "Q-4.3.is_not_empty", "Q-4.3.is_missing", "Q-4.3.value_fits",
      "Q-4.3"
    );
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        // checker.setDebug();
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.PASSED,
      fieldCounter.get("Q-4.3").getStatus()
    );
  }
}

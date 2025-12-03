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

import static junit.framework.TestCase.assertEquals;

/**
 * lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet
 *   /lido:resourceRepresentation/lido:linkResource
 *
 * Q-3.0
 * and:
 * - minCount: 1
 * - minLength: 1
 *
 * Q-3.6
 * mandatory: true
 * and:
 * - dependencies: [ Q-3.0 ]
 * - or:
 *   - pattern: ^.*\.(jpg|jpeg|jpe|jfif|png|tiff|tif|gif|svg|svgz|pdf)$
 *   - contentType: [image/jpeg, image/png, image/tiff, image/tiff-fx, image/gif, image/svg+xml, application/pdf]
 *
 * Q-3.2
 * alwaysCheckDependencies: true
 * and:
 * - dependencies: [ Q-3.6 ]
 * - pattern: ^[a-zA-Z_0-9&%\?\./:\-]+$
 */
public class q32LidoTest extends LidoTest {
  @Test
  public void recordInfoLink_fehlt() throws Exception {
    setup("Q-3.2-recordInfoLink_fehlt.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.0", "Q-3.6", "Q-3.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-3.2").getStatus()
    );
  }

  @Test
  public void recordInfoLink_leer() throws Exception {
    setup("Q-3.2-recordInfoLink_leer.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.0", "Q-3.6", "Q-3.2");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-3.2").getStatus()
    );
  }
}

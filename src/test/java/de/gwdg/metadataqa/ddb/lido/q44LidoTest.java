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
 * lido:resourceRepresentation[@lido:type=...]/lido:linkResource
 * lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation[
 *   @lido:type='http://terminology.lido-schema.org/lido00468' or
 *   @lido:type='Provided 3D' or
 *   @lido:type='provided_3D' or
 *   @lido:type='http://terminology.lido-schema.org/lido00465' or
 *   @lido:type='Provided audio' or
 *   @lido:type='provided_audio' or
 *   @lido:type='http://terminology.lido-schema.org/lido00464' or
 *   @lido:type='provided_image' or
 *   @lido:type='Provided image' or @lido:type='image_master' or
 *   @lido:type='http://terminology.lido-schema.org/lido00482' or
 *   @lido:type='Provided text' or @lido:type='provided_text' or
 *   @lido:type='http://terminology.lido-schema.org/lido00466' or
 *   @lido:type='Provided video' or @lido:type='provided_video'
 * ]/lido:linkResource
 *
 * id: Q-4.4
 * Check whether the data record contains a media file (see table "Mapping the DDB elements").
 * If so => check whether these elements contain a value.
 *
 * dependencies: [Q-3.0, Q-4.0]
 *
 * Q-4.0
 * and:
 * - minCount: 1
 * - minLength: 1
 *
 * Q-3.0
 * lido:lido/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet
 *    /lido:resourceRepresentation/lido:linkResource
 * and:
 * - minCount: 1
 * - minLength: 1
 */
public class q44LidoTest extends LidoTest {
  @Test
  public void recordInfoLink() throws Exception {
    setup("Q-4.4-recordInfoLink.xml");
    Selector cache = SelectorFactory.getInstance(schema.getFormat(), xml);
    FieldCounter<RuleCheckerOutput> fieldCounter = new FieldCounter<>();
    List<String> ids = List.of("Q-3.0", "Q-4.0", "Q-4.4");
    for (RuleChecker checker : schema.getRuleCheckers()) {
      if (ids.contains(checker.getId())) {
        checker.update(cache, fieldCounter, RuleCheckingOutputType.STATUS);
      }
    }
    System.err.println(fieldCounter);
    assertEquals(
      RuleCheckingOutputStatus.NA,
      fieldCounter.get("Q-4.4").getStatus()
    );
  }
}

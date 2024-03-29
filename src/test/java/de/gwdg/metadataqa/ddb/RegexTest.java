package de.gwdg.metadataqa.ddb;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class RegexTest {
  @Test
  public void name() {
    String url = "http://mdz-nbn-resolving.de/urn:nbn:de:bvb:12-bsb10020668-4";
    Matcher m = Pattern.compile("^[a-zA-Z_0-9:/\\.\\-]+$").matcher(url);
    assertTrue(m.matches());
  }

}

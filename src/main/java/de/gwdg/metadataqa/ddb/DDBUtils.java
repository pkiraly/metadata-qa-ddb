package de.gwdg.metadataqa.ddb;

import java.util.regex.Pattern;

public class DDBUtils {
  private static Pattern ALPHA = Pattern.compile("[^a-züA-Z0-9_\\.() -]");

  public static String toHex(String text) {
    char ch[] = text.toCharArray();
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < ch.length; i++) {
      String character = String.valueOf(ch[i]);
      if (ALPHA.matcher(character).matches()) {
        String hexString = Integer.toHexString(ch[i]);
        if (hexString.equals("fffd") || hexString.equals("84")) {
          sb.append("ä");
        } else if (hexString.equals("94")) {
          sb.append("ö");
        } else if (hexString.equals("81")) {
          sb.append("ü");
        } else {
          sb.append("\\" + hexString);
        }
      } else
        sb.append(character);
    }
    return sb.toString();
  }
}

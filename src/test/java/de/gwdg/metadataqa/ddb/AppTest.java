package de.gwdg.metadataqa.ddb;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 * Check https://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
 */
public class AppTest {

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue( true );

        Pattern pattern = Pattern.compile("^[\\p{Alpha}\\d\\.]+$");
        assertTrue(pattern.matcher("M.ch.f.91").matches());
        assertTrue(pattern.matcher("Peter").matches());
        assertFalse(pattern.matcher("M. ch.f.91").matches());
        assertFalse(pattern.matcher("M./ch.f.91").matches());
        assertFalse(pattern.matcher("Göttingen").matches());
    }

    @Test
    public void w() {
        assertTrue( true );

        Pattern pattern = Pattern.compile("^\\w+$");
        assertTrue(pattern.matcher("Peter").matches());
        assertFalse(pattern.matcher("Göttingen").matches());
    }

    @Test
    public void Alpha() {
        assertTrue( true );

        Pattern pattern = Pattern.compile("^\\p{Alpha}+$");
        assertTrue(pattern.matcher("Peter").matches());
        assertFalse(pattern.matcher("Göttingen").matches());
    }

    @Test
    public void letters() {
        assertTrue( true );

        Pattern pattern = Pattern.compile("^\\p{L}+$");
        assertTrue(pattern.matcher("Peter").matches());
        assertTrue(pattern.matcher("Göttingen").matches());
        assertFalse(pattern.matcher("Göt tingen").matches());
    }

    @Test
    public void imagePattern() {
        assertTrue( true );

        Pattern pattern = Pattern.compile("^.*\\.(jpg|jpeg|jpe|jfif|png|tiff|tif|gif|svg|svgz|pdf)$");
        assertTrue(pattern.matcher("http://vb.uni-wuerzburg.de/ub/books/hpf540594/folio-std/DE-20__H_p_f_540-5_94__0001__0001.jpg").matches());
        // assertTrue(pattern.matcher("Göttingen").matches());
        // assertFalse(pattern.matcher("Göt tingen").matches());
    }

    @Test
    public void nonSpacePattern() {
        Pattern pattern = Pattern.compile("^[a-zA-Z_0-9:/\\.\\-]+$");
        assertTrue(pattern.matcher("http://vb.uni-wuerzburg.de/ub/permalink/itf32").matches());
    }

    @Test
    public void contentType() {
        try {
            assertEquals("text/html", getContentType("http://vb.uni-wuerzburg.de/ub/permalink/itf32"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getContentType(String url) throws IOException {
        String contentType = null;
        URL urlObj = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();

        int timeout = 1000;
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200) {
            String rawContentType = urlConnection.getHeaderField("Content-Type");
            if (rawContentType != null && StringUtils.isNotBlank(rawContentType))
                contentType = rawContentType.replaceAll("; ?charset.*$", "");
        } else {
            System.err.printf("Status code: %d.%n", responseCode);
        }
        return contentType;
    }
}

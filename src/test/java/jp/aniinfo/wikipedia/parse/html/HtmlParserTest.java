package jp.aniinfo.wikipedia.parse.html;


import jp.aniinfo.wikipedia.entity.CharacterInfo;
import jp.aniinfo.wikipedia.exception.WikiParseException;
import jp.aniinfo.wikipedia.entity.WikiInfo;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class HtmlParserTest extends TestCase {
    public void test_parse_success() {
        String[] titles = {"らき☆すた","灰羽連盟","進撃の巨人","ラブライブ!","俺の妹がこんなに可愛いわけがない","はたらく魔王さま!"};
        for (String title : titles) {
            WikiInfo info;
            try {
                info = HtmlParser.parse(title);
                assertEquals(title, info.getTitle());
                if (info.getInfobox() != null) {
                    assertTrue(info.getInfobox().entrySet().size() > 0);
                    for (Map.Entry<String, String> entry : info.getInfobox().entrySet()) {
                        assertTrue(StringUtils.isNotBlank(entry.getKey()));
                        assertTrue(StringUtils.isNotBlank(entry.getValue()));
                    }
                }

                assertTrue(StringUtils.isNotBlank(info.getDigest()));
                System.out.println("Digest:");
                System.out.println(info.getDigest());

                assertNotNull(info.getCharacters());
                if (info.getCharacters() != null) {
                    assertTrue(info.getCharacters().size() > 0);
                    for (CharacterInfo character : info.getCharacters()) {
                        assertTrue(StringUtils.isNotBlank(character.getName()));
                        assertTrue(StringUtils.isNotBlank(character.getActor()));
                        assertTrue(StringUtils.isNotBlank(character.getDescription()));
                    }
                }


                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    public void test_parse_fail() {
        HtmlParser parser = new HtmlParser();
        try {
            parser.parse("testaaatest");
            fail();
        } catch (WikiParseException e) {
            assertTrue(true);
        }
    }

    public void test_deleteInnerTable() {
        HtmlParser parser = new HtmlParser();
        String str = "<table>abc<table>defg</table>hijk</table>";
        str = parser.deleteInnerTable(str);
        assertEquals("<table>abchijk</table>", str);
    }
}

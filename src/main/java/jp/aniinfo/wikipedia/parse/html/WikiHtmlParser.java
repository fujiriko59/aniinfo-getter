package jp.aniinfo.wikipedia.parse.html;


import jp.aniinfo.wikipedia.entity.CharacterInfo;
import jp.aniinfo.wikipedia.exception.WikiParseException;
import jp.aniinfo.wikipedia.entity.WikiInfo;
import jp.aniinfo.httpclient.HttpClientFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WikiHtmlParser {
    public static int maxCharacterNum = -1;

    public static WikiInfo parse(String title) throws WikiParseException {
        WikiInfo info = new WikiInfo();

        info.setTitle(title);

        HttpClient client = HttpClientFactory.create();
        try {
            HttpGet httpGet = new HttpGet("http://ja.wikipedia.org/wiki/" + URLEncoder.encode(title, "UTF-8"));
            HttpResponse response = client.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            if (html.indexOf("class=\"infobox") < 0) {
                throw new WikiParseException(title + " does not exsists.");
            }
            info.setHtml(html);
            info.setInfobox(parseInfoBox(html));
            info.setDigest(parseDigest(html));
            String tmpTitle = title.replace("_(アニメ)", "");
            List<CharacterInfo> characters = parseCharacters(html, tmpTitle);
            if (characters != null) {
                info.setCharacters(characters);
            } else {
                info.setCharacters(parseCharactersOfPage(html, tmpTitle));
            }
        } catch (Exception e) {
            throw new WikiParseException(e);
        } finally {
            client.getConnectionManager().shutdown();
        }

        return info;
    }

    public static Map<String, String> parseInfoBox(String html) {
        Map<String, String> infobox = new HashMap<String, String>();

        if (html.indexOf("<table class=\"infobox") < 0) {
            return null;
        }

        String tmpStr = html.substring(html.indexOf("<table class=\"infobox"));
        tmpStr = deleteInnerTable(tmpStr);
        tmpStr = tmpStr.substring(0, tmpStr.indexOf("</table>"));

        while (tmpStr.indexOf("<tr") >= 0 && tmpStr.indexOf("<th") >= 0
                && tmpStr.indexOf("<td") >= 0) {
            tmpStr = tmpStr.substring(tmpStr.indexOf("<tr"));
            if (tmpStr.indexOf("<th") < tmpStr.indexOf("</tr>") && tmpStr.indexOf("<td") < tmpStr.indexOf("</tr>")) {
                tmpStr = tmpStr.substring(tmpStr.indexOf("<th"));
                tmpStr = tmpStr.substring(tmpStr.indexOf(">") + ">".length());
                String key = deleteHtmlTag(tmpStr.substring(0, tmpStr.indexOf("</th>")));
                key = deleteReference(key);

                tmpStr = tmpStr.substring(tmpStr.indexOf("<td"));
                tmpStr = tmpStr.substring(tmpStr.indexOf(">") + ">".length());
                String value = deleteHtmlTag(tmpStr.substring(0, tmpStr.indexOf("</td>")));
                value = deleteReference(value);

                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    infobox.put(key.trim(), value.trim());
                }
            }
            tmpStr = tmpStr.substring(tmpStr.indexOf("</tr>"));
        }

        return infobox;
    }

    public static String parseDigest(String html) {
        if (html.indexOf("<table class=\"infobox") < 0) {
            return null;
        }
        String tmpStr = html.substring(html.indexOf("<table class=\"infobox"));
        tmpStr = deleteInnerTable(tmpStr);
        tmpStr = tmpStr.substring(tmpStr.indexOf("</table>") + "</table>".length());
        tmpStr = tmpStr.trim();
        while (tmpStr.startsWith("<table")) {
            tmpStr = tmpStr.substring(tmpStr.indexOf("</table>") + "</table>".length());
            tmpStr = tmpStr.trim();
        }

        int digestEndsTagPos;
        if(tmpStr.indexOf("<table") < tmpStr.indexOf("<div")) {
            digestEndsTagPos = tmpStr.indexOf("<table");
        } else {
            digestEndsTagPos = tmpStr.indexOf("<div");
        }
        tmpStr = tmpStr.substring(0, digestEndsTagPos);
        tmpStr = deleteHtmlTag(tmpStr);
        tmpStr = deleteReference(tmpStr);
        return tmpStr.trim();
    }

    public static List<CharacterInfo> parseCharacters(String html, String title) {
        String tmpStr = html.substring(html.indexOf("mw-headline"));
        String characterPage = null;
        if (tmpStr.indexOf(">登場人物</span") >= 0) {
            characterPage = "登場人物";
        } else if (tmpStr.indexOf(">主要人物</span") >= 0) {
            characterPage = "主要人物";
        } else if (tmpStr.indexOf(">登場キャラクター</span") >= 0) {
            characterPage = "登場キャラクター";
        } else if (tmpStr.indexOf(">主な登場人物</span") >= 0) {
            characterPage = "主な登場人物";
        } else if (tmpStr.indexOf(">" + title + "の登場人物</span") >= 0) {
            characterPage = title + "の登場人物";
        } else if (tmpStr.indexOf(">" + title + "のキャラクター一覧</span") >= 0) {
            characterPage = title + "のキャラクター一覧";
        } else {
            return null;
        }

        tmpStr = tmpStr.substring(tmpStr.indexOf(">" + characterPage + "</span"));

        if (tmpStr.indexOf("<h2") >= 0) {
            tmpStr = tmpStr.substring(0, tmpStr.indexOf("<h2"));
        }
        if (tmpStr.indexOf("<dl") >= 0) {
            tmpStr = tmpStr.substring(tmpStr.indexOf("<dl"));
        }

        return parseCharactersSub(tmpStr);
    }

    public static List<CharacterInfo> parseCharactersOfPage(String html, String title) throws WikiParseException {
        String tmpStr = html.substring(html.indexOf("mw-headline"));
        String characterPage;
        if (tmpStr.indexOf(">" + title + "の登場人物</a") >= 0) {
            characterPage = title + "の登場人物";
        } else if (tmpStr.indexOf(">" + title + "のキャラクター一覧</a") >= 0) {
            characterPage = title + "のキャラクター一覧";
        } else if (tmpStr.indexOf(">" + title + "の登場人物一覧</a") >= 0) {
            characterPage = title + "の登場人物一覧";
        } else if(tmpStr.indexOf(">涼宮ハルヒシリーズの登場人物</a") >= 0) {
            characterPage= "涼宮ハルヒシリーズの登場人物";
        } else {
            return null;
        }

        HttpClient client = HttpClientFactory.create();
        HttpGet httpGet = new HttpGet("http://ja.wikipedia.org/wiki/" + characterPage);
        String characterHtml = null;
        try {
            HttpResponse response = client.execute(httpGet);
            characterHtml = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            throw new WikiParseException(e);
        } finally {
            client.getConnectionManager().shutdown();
        }

        if (StringUtils.isBlank(characterHtml)) {
            throw new WikiParseException("Can not get wiki html.");
        }

        tmpStr = characterHtml.substring(characterHtml.indexOf("mw-headline"));
        return parseCharactersSub(tmpStr);
    }

    private static List<CharacterInfo> parseCharactersSub(String text) {
        String tmpStr = text;
        List<CharacterInfo> list = new ArrayList<CharacterInfo>();

        while (tmpStr.indexOf("<dt") >= 0) {
            if(maxCharacterNum > -1) {
                if(list.size() >= maxCharacterNum) {
                    break;
                }
            }

            CharacterInfo info = new CharacterInfo();
            String name;
            String actor = "";
            String description = "";

            tmpStr = tmpStr.substring(tmpStr.indexOf("<dt"));
            tmpStr = tmpStr.substring(tmpStr.indexOf(">") + ">".length());
            name = tmpStr.substring(0, tmpStr.indexOf("</dt>"));
            name = deleteHtmlTag(name).replaceAll("（[^）]*）", "").trim();

            int ends = tmpStr.indexOf("<dt");
            if (ends < 0) {
                ends = tmpStr.indexOf("</dl");
            }
            while (tmpStr.indexOf("<dd") >= 0 && tmpStr.indexOf("<dd") < ends) {
                tmpStr = tmpStr.substring(tmpStr.indexOf("<dd"));
                tmpStr = tmpStr.substring(tmpStr.indexOf(">") + ">".length());

                String actorTag = null;
                if (deleteHtmlTag(tmpStr).startsWith("声 - ")) {
                    actorTag = "声 - ";
                } else if (deleteHtmlTag(tmpStr).startsWith("声：")) {
                    actorTag = "声：";
                }
                if (actorTag != null) {
                    String tmp = tmpStr.substring(0, tmpStr.indexOf("</dd>"));
                    tmp = deleteHtmlTag(tmp);
                    tmp = tmp.substring(actorTag.length());
                    actor = deleteReference(tmp.replace("/", " "));
                    actor = actor.replaceAll("（[^\\)]*）", "").trim();
                } else {
                    String tmp = tmpStr.substring(0, tmpStr.indexOf("</dd>"));
                    tmp = deleteReference(deleteHtmlTag(tmp));
                    if (StringUtils.isNotBlank(description)) {
                        description = description + " ";
                    }
                    description = description + tmp.trim();
                }
                ends = tmpStr.indexOf("<dt");
                if (ends < 0) {
                    ends = tmpStr.indexOf("</dl");
                }
            }

            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(actor)) {
                info.setName(name);
                info.setActor(actor);
                info.setDescription(description);
                list.add(info);
            }
        }

        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    protected static String deleteHtmlTag(String text) {
        String tmp = text;
        StringBuilder buf = new StringBuilder();

        while (tmp.indexOf("<") >= 0) {
            buf.append(tmp.substring(0, tmp.indexOf("<")));
            tmp = tmp.substring(tmp.indexOf(">") + ">".length());
        }
        buf.append(tmp);

        return buf.toString().replace("\n", " ");
    }

    protected static String deleteInnerTable(String text) {
        if (!text.startsWith("<table")) {
            return null;
        }

        String tmp = text;
        StringBuilder buf = new StringBuilder();

        buf.append("<table");
        tmp = tmp.substring("<table".length());

        int i = 0;
        while (true) {
            if (tmp.indexOf("<table") >= 0 && tmp.indexOf("<table") < tmp.indexOf("</table>")) {
                if (i <= 0) {
                    buf.append(tmp.substring(0, tmp.indexOf("<table")));
                }
                tmp = tmp.substring(tmp.indexOf("<table"));
                tmp = tmp.substring(tmp.indexOf(">"));
                i++;
            } else {
                if (i <= 0) {
                    buf.append(tmp);
                    break;
                } else {
                    tmp = tmp.substring(tmp.indexOf("</table>") + "</table>".length());
                    i--;
                }
            }
        }

        return buf.toString();
    }

    public static String deleteReference(String text) {
        return text.replaceAll("\\[[^\\]]*\\]", "");
    }
}

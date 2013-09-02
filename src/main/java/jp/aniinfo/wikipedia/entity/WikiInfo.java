package jp.aniinfo.wikipedia.entity;


import java.util.List;
import java.util.Map;

public class WikiInfo {
    protected String title;

    protected Map<String, String> infobox;

    protected String digest;

    protected List<CharacterInfo> characters;

    protected String html;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getInfobox() {
        return infobox;
    }

    public void setInfobox(Map<String, String> infobox) {
        this.infobox = infobox;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public List<CharacterInfo> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterInfo> characters) {
        this.characters = characters;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}

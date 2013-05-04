package jp.aniinfo.google.search.image.entity;


public class ImageInfo {
    protected String url;

    protected long width;

    protected long height;

    protected String tbUrl;

    protected long tbWidth;

    protected long tbHeight;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getTbUrl() {
        return tbUrl;
    }

    public void setTbUrl(String tbUrl) {
        this.tbUrl = tbUrl;
    }

    public long getTbWidth() {
        return tbWidth;
    }

    public void setTbWidth(long tbWidth) {
        this.tbWidth = tbWidth;
    }

    public long getTbHeight() {
        return tbHeight;
    }

    public void setTbHeight(long tbHeight) {
        this.tbHeight = tbHeight;
    }
}

package jp.aniinfo.wikipedia.exception;

public class WikiParseException extends Exception {
    public WikiParseException(Exception e) {
        super(e);
    }

    public WikiParseException(String message) {
        super(message);
    }

    public WikiParseException(String message, Exception e) {
        super(message, e);
    }
}

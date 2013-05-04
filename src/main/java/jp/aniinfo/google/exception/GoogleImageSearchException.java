package jp.aniinfo.google.exception;


public class GoogleImageSearchException extends Exception {
    public GoogleImageSearchException(Exception e) {
        super(e);
    }

    public GoogleImageSearchException(String message) {
        super(message);
    }

    public GoogleImageSearchException(String message, Exception e) {
        super(message, e);
    }
}

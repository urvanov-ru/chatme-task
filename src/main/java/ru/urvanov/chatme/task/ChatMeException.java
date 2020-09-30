package ru.urvanov.chatme.task;

public class ChatMeException extends Exception {

    private static final long serialVersionUID = 4683131521515289454L;

    public ChatMeException() {
        super();
    }

    public ChatMeException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ChatMeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatMeException(String message) {
        super(message);
    }

    public ChatMeException(Throwable cause) {
        super(cause);
    }

}

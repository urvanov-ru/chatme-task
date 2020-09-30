/**
 * 
 */
package ru.urvanov.chatme.task;

/**
 * @author fedor
 *
 */
public class PropertiesLoadException extends ChatMeException {

    private static final long serialVersionUID = -5082326646001268134L;

    public PropertiesLoadException() {
        super();
    }

    public PropertiesLoadException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PropertiesLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesLoadException(String message) {
        super(message);
    }

    public PropertiesLoadException(Throwable cause) {
        super(cause);
    }



}

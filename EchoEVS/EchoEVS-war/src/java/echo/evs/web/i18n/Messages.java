package echo.evs.web.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 * @author Dr. Volker Riediger riediger@uni-koblenz.de
 */
public class Messages {

    private static final String MESSAGE_BUNDLE = "echo.evs.web.i18n.messages";

    public static String getMessage(String key, Object... arguments) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ResourceBundle bundle = ResourceBundle.getBundle(MESSAGE_BUNDLE, fc.getViewRoot().getLocale());
        try {
            String message = bundle.getString(key);
            if (message == null) {
                return "???" + key + "???";
            }
            return MessageFormat.format(message, arguments);
        } catch (MissingResourceException e) {
            return "???" + key + "???";
        }
    }

}

package echo.evs.web;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Locale management bean
 * 
 * @author Dr. Volker Riediger riediger@uni-koblenz.de
 */
@SessionScoped
@Named
public class LocaleBean implements Serializable {

    private static final long serialVersionUID = 1390044703830900L;

    private Locale userLocale;

    /**
     * Get User locale
     * @return user locale
     */
    public Locale getUserLocale() {
        if (userLocale == null) {
            userLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        }
        if (userLocale == null) {
            userLocale = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
        }
        return userLocale;
    }

    /**
     * Set user locale 
     * @param userLocale user locale
     */
    public void setUserLocale(Locale userLocale) {
        this.userLocale = userLocale;
    }
    
    /**
     * Select english as user locale
     */
    public void selectEnglish() {
        userLocale = Locale.ENGLISH;
    }

    /**
     * Select german as user locale
     */
    public void selectGerman() {
        userLocale = Locale.GERMAN;
    }

    /**
     * Get current date and time
     * @return current date and time
     */
    public Date getCurrentDate() {
        return new Date();
    }

}

package echo.evs.web;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import echo.evs.dto.Organizer;
import echo.evs.logic.OrganizerLogic;

/**
 * Represents the session and roles actions
 * 
 * @author Team Echo
 */
@SessionScoped
@Named
@ManagedBean
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1390058792977600L;

    private static final Logger LOG = Logger.getLogger(LoginBean.class.getName());

    private Organizer currentOrganizer;

    @EJB
    private OrganizerLogic ol;

    @PostConstruct
    public void newSession() {
        getLOG().info("EVS: NEW SESSION");
    }

    /**
     * Checks if a user is logged in
     * @return true if logged in
     */
    public boolean isLoggedIn() {
        Principal p = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getUserPrincipal();
        return p != null;
    }

    /**
     * Indicates whether the current user has the Organizer role
     * 
     * @return true in case the current user is an Organizer
     */
    public boolean isOrganizerRole() {
        if (!isLoggedIn()) {
            return false;
        }
        return FacesContext.getCurrentInstance()
                .getExternalContext().isUserInRole("ORGANIZER");
    }

    /**
     * Indicates whether the current user has the Admin role
     * @return true in case the current user is an Admin
     */
    public boolean isAdminRole() {
        if (!isLoggedIn()) {
            return false;
        }

        return FacesContext.getCurrentInstance()
                .getExternalContext().isUserInRole("ADMIN");
    }
    private Principal oldPrincipal = null; // used to detect changed login

    /**
     * Obtain the current Organizer
     * 
     * @return current organizer
     */
    public Organizer getOrganizer() {

        Principal p = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getUserPrincipal();
        if (p == null) {
            setCurrentOrganizer(null);
        } else {
            if (!p.equals(oldPrincipal)) {
                getLOG().log(Level.INFO, "EVS: LOGIN user {0}", p.getName());
                setCurrentOrganizer(getOl().getCurrentUser());
                
            }
        }
        setOldPrincipal(p);
        return getCurrentOrganizer();
    }
    
    /**
     * Get the current user name if logged in
     * @return user name/mail
     */
    public String getCurrentUserName(){
        if (!isLoggedIn()) {
            return "";
        }
        
        return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
    }
    
    
    /**
     * Invalidate user current session
     */
    public void invalidateSession() {
        getLOG().log(Level.INFO, "invalidateSession()");
        Principal p = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getUserPrincipal();
        if (p != null) {
            getLOG().log(Level.INFO, "EVS: LOGOUT user {0}", p.getName());
        }

        setCurrentOrganizer(null);
        setOldPrincipal(null);
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();
    }

    /**
     * Logging out current authenticated user by invalidating the current session.
     */
    public void logout() {
        invalidateSession();
        FacesContext.getCurrentInstance()
                .responseComplete();
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return the LOG
     */
    public static Logger getLOG() {
        return LOG;
    }

    /**
     * @return the currentOrganizer
     */
    public Organizer getCurrentOrganizer() {
        return currentOrganizer;
    }

    /**
     * @param currentOrganizer the currentOrganizer to set
     */
    public void setCurrentOrganizer(Organizer currentOrganizer) {
        this.currentOrganizer = currentOrganizer;
    }

    /**
     * @return the ol
     */
    public OrganizerLogic getOl() {
        return ol;
    }

    /**
     * @param ol the ol to set
     */
    public void setOl(OrganizerLogic ol) {
        this.ol = ol;
    }

    /**
     * @return the oldPrincipal
     */
    public Principal getOldPrincipal() {
        return oldPrincipal;
    }

    /**
     * @param oldPrincipal the oldPrincipal to set
     */
    public void setOldPrincipal(Principal oldPrincipal) {
        this.oldPrincipal = oldPrincipal;
    }
}

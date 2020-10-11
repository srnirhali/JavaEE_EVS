/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.web;

import echo.evs.dto.Organizer;
import echo.evs.dto.ParticipantInList;
import echo.evs.dto.ParticipantList;
import echo.evs.logic.OrganizerLogic;
import echo.evs.web.i18n.Messages;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Represents the actions over a participants list in the list edit view
 * 
 * @author Team Echo
 */
@ViewScoped
@Named
public class ParticipantListEditBean implements Serializable {

    private static final long serialVersionUID = 1390073964450300L;

    @EJB
    private OrganizerLogic ol;

    private boolean success;
    private boolean failure;
    private String failureMessage;
    private ParticipantList participantList;
    private String uuid;

    private Set<String> emailList; // used for duplicate testing
    private String tempMail;
    private UIComponent addParticipantComponent;

    /**
     * Checks whether the participants list is valid
     * 
     * @return true when the participants list is valid
     */
    public boolean isValid() {
        return uuid != null && getParticipantList() != null;
    }

    /**
     * @return true if the list could be stored successfully
     */
    public boolean isSuccess() {
        return success && !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * @return true if the list could not be stored
     */
    public boolean isFailure() {
        return failure && !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * Get failure message shown if failure is true
     * @return failure message
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * Get uuid that is used
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set uuid that is used
     * @param uuid uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get organizer logic
     * @return the ol
     */
    public OrganizerLogic getOl() {
        return ol;
    }

    /**
     * Set organizer logic
     * @param ol the ol to set
     */
    public void setOl(OrganizerLogic ol) {
        this.ol = ol;
    }

    /**
     * Set success value
     * @param success true if successful
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Set failure value 
     * @param failure true if failed
     */
    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    /**
     * Set failure message shown when failure is true
     * @param failureMessage the failureMessage to be shown
     */
    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    /**
     * Get organizer from organizer logic
     * @return the organizer
     */
    public Organizer getOrganizer() {
        return getOl().getCurrentUser();
    }

    /**
     * Get a set of email strings used for duplicate testing
     * @return set of email strings
     */
    public Set<String> getEmailList() {
        return emailList;
    }

    /**
     * Set a set of email string used for duplicate testing
     * @param emailList set of email string
     */
    public void setEmailList(Set<String> emailList) {
        this.emailList = emailList;
    }

    /**
     * Get temporary email used for the input when adding a new participant to the list 
     * @return temporary email of participant to be added
     */
    public String getTempMail() {
        return tempMail;
    }

    /**
     * Set temporary email used for the input when adding a new participant to the list 
     * @param tempMail temporary email of participant to be added
     */
    public void setTempMail(String tempMail) {
        this.tempMail = tempMail.toLowerCase();
    }

    /**
     * Get participant list based on uuid and current organizer
     * @return participant list
     */
    public ParticipantList getParticipantList() {
        if (participantList == null) {
            if ("new".equals(uuid)) {
                participantList = new ParticipantList();
            } else {
                participantList = ol.getParticipantList(uuid);
            }
        }
        if (participantList != null) {
            emailList = getEmailsAsStringSet();
        }

        return participantList;
    }

    /**
     * Obtain the set of emails from all participants in the list. Used for duplicate testing when before adding a new email to list
     * 
     * @return the set of emails from participants in a list as a string set
     */
    private Set<String> getEmailsAsStringSet() {
        Set<String> emailsAsStrings = new HashSet<>();

        List<ParticipantInList> participants = participantList.getParticipants();
        participants.forEach((participant) -> {
            emailsAsStrings.add(participant.getEmail());
        });

        return emailsAsStrings;
    }

    /**
     * Persist participants list.
     */
    public void storeParticipantList() {

        try {
            uuid = ol.storeParticipantList(participantList);
            if (uuid != null) {
                success = true;
                failure = false;
                tempMail = "";
                // force reload since the JPA version might have changed
                participantList = null;
            }
        } catch (EJBException e) {
            success = false;
            failure = true;
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }

            failureMessage = t.getMessage();
            if (failureMessage != null && failureMessage.startsWith("Duplicate entry")) {
                failureMessage = Messages.getMessage("participantListDuplicateNameMessage", participantList.getName());
            }
        }
    }

    /**
     * Delete a Participants list
     */
    public void deleteParticipantList() {
        ol.deleteParticipantList(uuid);
    }
    
    /**
     * Get UI Component used for displaying errors when adding participants
     * @return ui component
     */
    public UIComponent getAddParticipantComponent() {
        return addParticipantComponent;
    }

    /**
     * Set UI Component used for displaying errors when adding participants
     * @param addParticipantComponent ui component
     */
    public void setAddParticipantComponent(UIComponent addParticipantComponent) {
        this.addParticipantComponent = addParticipantComponent;
    }

    /**
     * Add new participant to a list. Before adding it it checks if email is already in the list. If yes it does not add it.
     */
    public void addNewParticipant() {
        if (!tempMail.equals("")) {
            if (emailList.add(tempMail)) {
                ParticipantInList participant = new ParticipantInList();
                participant.setEmail(tempMail);
                participantList.addParticipant(participant);
                tempMail = "";
            } else {
                // Send message that this mail is a duplicate
                FacesContext.getCurrentInstance().addMessage(addParticipantComponent.getClientId(), new FacesMessage(Messages.getMessage("duplicateMailMessage", tempMail)));
                tempMail = "";
            }
        }
    }

    /**
     * Remove an specific participant from a list.
     * 
     * @param participant participant we want to remove from list.
     */
    public void removeParticipant(ParticipantInList participant) {
        participantList.removeParticipant(participant);
        emailList.remove(participant.getEmail());
    }

}

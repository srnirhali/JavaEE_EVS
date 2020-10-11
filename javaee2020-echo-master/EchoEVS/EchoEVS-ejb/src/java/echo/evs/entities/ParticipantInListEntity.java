
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Represents the participantInList entity (participant stored in participant list)
 * @author Team Echo
 */
@Entity
public class ParticipantInListEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1389956905992300L;

    private String email;

    @OneToOne
    private ParticipantListEntity participantList;

    /**
     * Empty constructor, new Entity is set to false
     */
    public ParticipantInListEntity() {
        super(false);
    }
    
    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new
     */
    public ParticipantInListEntity(boolean newEntity) {
        super(newEntity);
    }

    /**
     * Get email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    /**
     * Get Participant list this participant belongs to
     * @return the participantList
     */
    public ParticipantListEntity getParticipantList() {
        return participantList;
    }

    /**
     * Set partcipant list this participant belongs to
     * @param participantList the participantList to set
     */
    public void setParticipantList(ParticipantListEntity participantList) {
        this.participantList = participantList;
    }
}

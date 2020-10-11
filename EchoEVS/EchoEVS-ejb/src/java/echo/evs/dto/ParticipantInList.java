
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

/**
 * The Data Transfer Object representing an participant in a participant list
 * 
 * @author Team Echo
 */
public class ParticipantInList extends AbstractDTO {

    private String email;

    /**
     * Empty constructor
     */
    public ParticipantInList() {
    }

    /**
     * Constructor using all passed parameter 
     * 
     * @param uuid uuid of participant in list
     * @param jpaVersion jpaVersion
     * @param email email of participant in list
     */
    public ParticipantInList(String uuid, int jpaVersion, String email) {
        super(uuid, jpaVersion);
        this.email = email.toLowerCase();
    }

    /**
     * Gets the email of the participant in list
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the participant in list
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

}

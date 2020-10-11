/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

/**
 * The Data Transfer Object representing an participant in a poll
 * 
 * @author Team Echo
 */
public class Participant extends AbstractDTO {

    private String email;

    /**
     * Empty constructor
     */
    public Participant() {
    }

    /**
     * Constructor of participant (using all passed parameter)
     * 
     * @param uuid uuid of participant
     * @param jpaVersion jpaVersion
     * @param email email of participant
     */
    public Participant(String uuid, int jpaVersion, String email) {
        super(uuid, jpaVersion);
        this.email = email.toLowerCase();
    }

    /**
     * Gets the mail address of a participant.
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the participant
     * 
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

}

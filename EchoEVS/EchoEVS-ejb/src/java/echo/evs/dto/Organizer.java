/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Data Transfer Object representing an organizer
 * 
 * @author Team Echo
 */
@XmlRootElement
public class Organizer extends AbstractDTO {

    private String email;
    private String firstName;
    private String lastName;

    /**
     * Empty constructor of organizer
     */
    public Organizer() {

    }

    /**
     * Constructor of organizer using all passed parameters 
     * 
     * @param uuid uuid of organizer
     * @param jpaVersion jpaVersion of organizer
     * @param email email of organizer
     * @param firstName firstname of organizer
     * @param lastName  lastname of organizer
     */
    public Organizer(String uuid, int jpaVersion, String email, String firstName, String lastName) {
        super(uuid, jpaVersion);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Gets the email of the organizer
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the organizer
     * 
     * @param email the email to be set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the first name of the organizer
     * 
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of organizer
     * 
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the organizer
     * 
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the organizer
     * 
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}

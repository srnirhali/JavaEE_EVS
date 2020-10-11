/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Represents the Organizer entity
 * @author Team Echo
 */
@NamedQueries({
    @NamedQuery(
            name = "getOrganizerByEmail",
            query = "SELECT o FROM OrganizerEntity o WHERE o.email = :email"
    )
})

@Entity
public class OrganizerEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1390552591515100L;

    @Column(name = "email", unique = true)
    private String email;

    private String firstName;
    private String lastName;

    @OneToMany(
            mappedBy = "organizer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn
    private List<ParticipantListEntity> participentLists;

    @OneToMany(
            mappedBy = "organizer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn
    private List<PollEntity> polls;

    /**
     * Empty constructor, new Entity is set to false
     */
    public OrganizerEntity() {
        this(false);
    }

    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new
     */
    public OrganizerEntity(boolean newEntity) {
        super(newEntity);
        if (newEntity) {
            participentLists = new ArrayList<>();
            polls = new ArrayList<>();
        }
    }

    /**
     * Get email of organizer
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email of organizer
     * @param email the email to be set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get firstname of organizer if set
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set first name of organizer
     * @param firstName the firstName to be set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get last name of organizer
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set last name of organizer
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get participant lists owned by the organizer
     * @return the participentLists
     */
    public List<ParticipantListEntity> getParticipentLists() {
        return participentLists;
    }

    /**
     * Set participant lists owned by the organizer
     * @param participentLists the participentLists to be set
     */
    public void setParticipentLists(List<ParticipantListEntity> participentLists) {
        this.participentLists = participentLists;
    }

    /**
     * Get polls owned by organizer
     * @return the polls
     */
    public List<PollEntity> getPolls() {
        return polls;
    }

    /**
     * Set polls owned by organizer
     * @param polls the polls to set
     */
    public void setPolls(List<PollEntity> polls) {
        this.polls = polls;
    }
}

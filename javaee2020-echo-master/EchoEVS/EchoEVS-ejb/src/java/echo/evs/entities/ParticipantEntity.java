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
 * Represents the participant entity
 * @author Team Echo
 */
@Entity
public class ParticipantEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1389933482145900L;

    private String email;

    @OneToOne
    private PollEntity poll;

    /**
     * Empty constructor, new Entity is set to false
     */
    public ParticipantEntity() {
        this(false);
    }

    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new
     */
    public ParticipantEntity(boolean newEntity) {
        super(newEntity);
    }

    /**
     * Get email of participant
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email of participant
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    /**
     * Get poll this participant belongs to
     * @return poll
     */
    public PollEntity getPoll() {
        return poll;
    }

    /**
     * Set poll this participant belongs to
     * @param poll poll
     */
    public void setPoll(PollEntity poll) {
        this.poll = poll;
    }

}

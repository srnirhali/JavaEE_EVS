/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Represents the Token entity
 * @author Team Echo
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "getByToken",
            query = "SELECT c FROM TokenEntity c"
            + " WHERE c.token = :token "
    ),
    @NamedQuery(
            name = "deleteTokenByUuid",
            query = "DELETE FROM TokenEntity c WHERE c.uuid = :uuid"
    ),
    @NamedQuery(
            name = "getTokenByUuid",
            query = "SELECT c FROM TokenEntity c"
            + " WHERE c.uuid = :uuid "
    ),})
public class TokenEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1390028929379700L;

    private String token;
    @OneToOne
    private PollEntity poll;
    @OneToOne
    private ParticipantEntity participant;
    
    /**
     * Empty constructor, new Entity is set to false
     */
    public TokenEntity() {
        this(false);
    }
    
    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new
     */
    public TokenEntity(boolean newEntity) {
        super(newEntity);
    }
    

    /**
     * Get poll this token is belongig to
     * @return poll
     */
    public PollEntity getPoll() {
        return poll;
    }

    /**
     * Set poll of this token
     * @param poll poll
     */
    public void setPoll(PollEntity poll) {
        this.poll = poll;
    }

    /**
     * Get participant this token is belonging to
     * @return participant
     */
    public ParticipantEntity getParticipant() {
        return participant;
    }

    /**
     * Set participant this token is belonging to
     * @param participant participant
     */
    public void setParticipant(ParticipantEntity participant) {
        this.participant = participant;
    }

    /**
     * Get token string
     * @return token string
     */
    public String getToken() {
        return token;
    }

    /**
     * Set unique token string
     * @param token token string
     */
    public void setToken(String token) {
        this.token = token;
    }

}

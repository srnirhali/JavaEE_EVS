/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

/**
 * The Data Transfer Object representing a token
 * 
 * @author Echo
 */
public class Token extends AbstractDTO {

    private String token;
    private Participant participant;
    private Poll poll;

    /**
     * Empty constructor
     */
    public Token() {

    }

    /**
     * Constructor using all passed parameters
     * 
     * @param uuid uuid
     * @param jpaVersion jpaVersion
     * @param token token value string
     * @param participant participant associated to that token 
     * @param poll poll associated to that token
     */
    public Token(String uuid, int jpaVersion, String token, Participant participant, Poll poll) {
        super(uuid, jpaVersion);
        this.token = token;
        this.participant = participant;
        this.poll = poll;
    }
    
    /**
     * Get the unique token string
     * 
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the unique token string
     * @param token token string
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get the participant associated to the token. Could also be null if not stored
     * 
     * @return participant dto
     */
    public Participant getParticipant() {
        return participant;
    }

    /**
     * Set the participant associated to the token. Will only be permanently stored if tracking is enabled for the associated poll
     * 
     * @param participant participant dto
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * Get the poll associated to the token
     * 
     * @return poll dto
     */
    public Poll getPoll() {
        return poll;
    }

    /**
     * Set the poll associated to the token
     * 
     * @param poll poll dto
     */
    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}

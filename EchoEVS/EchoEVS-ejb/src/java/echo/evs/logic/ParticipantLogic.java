/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.logic;

import echo.evs.dto.Poll;
import echo.evs.dto.Token;
import javax.ejb.Remote;

/**
 * Defines the interfaces for the Participant actions
 * @author Team Echo
 */
@Remote
public interface ParticipantLogic {

    /**
     * Get Token DTO including references to the poll by token string
     * 
     * @param token token string
     * @return token dto
     */
    public Token getByToken(String token);

    /**
     * Submits a poll meaning it will update all vote and abstention counter and it will invalidate the token that was used so it can not be re-used
     * 
     * @param token Token DTO with reference to poll
     * @return true if successful
     */
    public boolean submitPoll(Token token);

    /**
     * Gets a poll by its result token. Only polls that are also in PUBLISHED state are considered.
     * 
     * @param resultToken resultToken
     * @return poll dto
     */
    public Poll getPollByResultToken(String resultToken);
}

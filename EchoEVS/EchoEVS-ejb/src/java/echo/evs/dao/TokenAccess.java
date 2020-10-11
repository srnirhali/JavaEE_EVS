/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dao;

import echo.evs.entities.ParticipantEntity;
import echo.evs.entities.PollEntity;
import echo.evs.entities.TokenEntity;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * Represents the actions over a token entity
 * 
 * @author Team Echo
 */
@LocalBean
@Stateless
public class TokenAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    /**
     * Creates a new Token. If tracking is enabled the reference to the participant Entity will also be stored in the token. Currently not used.
     * 
     * @param participantEntity participant this token belongs to (only stored if tracking Enabled is true)
     * @param pollEntity the poll this token belongs to
     * @param trackingEnabled If true, reference to participant is stored permanently
     * @return created toked
     */
    public TokenEntity createToken(ParticipantEntity participantEntity, PollEntity pollEntity, Boolean trackingEnabled) {
        TokenEntity te = new TokenEntity(true);
        if (trackingEnabled) {
            te.setParticipant(participantEntity);
        }
        te.setPoll(pollEntity);
        em.persist(te);
        return te;
    }

    /**
     * Creates a new Token based on parameters. If tracking is enabled the reference to the participant Entity will also be stored in the token. 
     * 
     * @param participantEntity the Participant entity this token should belong to (only stored if tracking Enabled is true) Also used to get poll reference
     * @param token string representing a unique token value
     * @param trackingEnabled If true, reference to participant is stored permanently as well
     * @return token object
     */
    public TokenEntity createToken(ParticipantEntity participantEntity, String token, Boolean trackingEnabled) {
        TokenEntity te = new TokenEntity(true);
        te.setToken(token);
        if (trackingEnabled) {
            te.setParticipant(participantEntity);
        }
        te.setPoll(participantEntity.getPoll());
        em.persist(te);
        return te;
    }

    /**
     * Obtain a token by the token string 
     * 
     * @param token token string
     * @return token entity object
     */
    public TokenEntity getByToken(String token) {
        try {
            TokenEntity te = em.createNamedQuery("getByToken", TokenEntity.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return te;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Removes an specific token
     * 
     * @param token token value string
     * @return true if removed successfully, else false
     */
    public boolean deleteToken(String token) {
        TokenEntity te = getByToken(token);
        em.remove(te);
        return true;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dao;

import echo.evs.dto.Participant;
import echo.evs.entities.ParticipantEntity;
import echo.evs.entities.PollEntity;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Represents the actions over a Participant
 * 
 * @author Team Echo
 */
@LocalBean
@Stateless
public class ParticipantAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    /**
     * Create List of Participant Entity
     * 
     * @param participants List of Participant DTOs
     * @param p poll these participants should belong to
     * @return List of Participant Entities
     */
    public List<ParticipantEntity> createParticipant(List<Participant> participants, PollEntity p) {
        return participants.stream().map(n -> createParticipant(n, p)).collect(Collectors.toList());
    }

    /**
     * Creates a participant entity for a poll
     * 
     * @param participant Participant DTOs 
     * @param p poll where the paticipant should belong to
     * @return participant entity
     */
    public ParticipantEntity createParticipant(Participant participant, PollEntity p) {
        ParticipantEntity pe = new ParticipantEntity(true);
        pe.setEmail(participant.getEmail());
        pe.setPoll(p);

        em.persist(pe);
        return pe;
    }

}

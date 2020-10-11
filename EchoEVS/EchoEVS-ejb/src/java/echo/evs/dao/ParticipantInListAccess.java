/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dao;

import echo.evs.dto.ParticipantInList;
import echo.evs.entities.ParticipantInListEntity;
import echo.evs.entities.ParticipantListEntity;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Team Echo
 */
@Stateless
@LocalBean
public class ParticipantInListAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    /**
     * Creates a participantInList entity (a participant stored in a participant list)
     * 
     * @param ple the ParticipantList-Entity (list the participant should belong to)
     * @param p ParticipantInList DTO that should be stored in list
     * @return ParticipantInList entity
     */
    public ParticipantInListEntity createParticipantInList(ParticipantListEntity ple, ParticipantInList p) {
        ParticipantInListEntity pe = new ParticipantInListEntity(true);
        pe.setEmail(p.getEmail());
        pe.setParticipantList(ple);
        em.persist(ple);
        return pe;
    }

    /**
     * Creates a list of participantInList entities (list of participant stored in a participant list)
     * 
     * @param participantListEntity the ParticipantList-Entity (list the participant should belong to)
     * @param participants List of ParticipantInList DTOs that should be stored in list
     * @return list of ParticipantInList entities
     */
    public List<ParticipantInListEntity> createParticipantInList(ParticipantListEntity participantListEntity, List<ParticipantInList> participants) {
        return participants.stream().map(p -> ParticipantInListAccess.this.createParticipantInList(participantListEntity, p)).collect(Collectors.toList());
    }
}

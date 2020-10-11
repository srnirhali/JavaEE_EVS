
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dao;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import echo.evs.entities.OrganizerEntity;
import echo.evs.entities.ParticipantListEntity;

/**
 * Represent the actions over a participant list
 * 
 * @author Team Echo
 */
@Stateless
@LocalBean
public class ParticipantListAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    /**
     * Creates a new participant list
     * 
     * @param organizer organizer of the list
     * @param Name name of the list
     * @return participant list
     */
    public ParticipantListEntity createParticipantList(OrganizerEntity organizer, String Name) {
        ParticipantListEntity listEntity = new ParticipantListEntity(true);
        listEntity.setName(Name);
        listEntity.setOrganizer(organizer);
        em.persist(listEntity);
        return listEntity;
    }

    /**
     * Obtains all participant lists from a particular organizer
     * 
     * @param organizer organizer
     * @return list of participantList entities
     */
    public List<ParticipantListEntity> getParticipantLists(OrganizerEntity organizer) {
        return em.createNamedQuery("getParticipantLists")
                .setParameter("organizer", organizer)
                .getResultList();
    }

    /**
     * Obtains a participants list by its identifier from an organizer
     * 
     * @param organizer organizer
     * @param uuid uuid
     * @return participant list object
     */
    public ParticipantListEntity getParticipantListByUuid(OrganizerEntity organizer, String uuid) {
        try {
            ParticipantListEntity ce = em.createNamedQuery("getParticipantListByUuid", ParticipantListEntity.class)
                    .setParameter("organizer", organizer)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return ce;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Removes a participants list by its identifier from an organizer
     * 
     * @param organizer organizer
     * @param uuid uuid
     * @return true if the participant list was removed successfully and false otherwise
     */
    public boolean deleteParticipantList(OrganizerEntity organizer, String uuid) {
        ParticipantListEntity ce = getParticipantListByUuid(organizer, uuid);
        if (ce == null) {
            return false;
        }
        ce.getOrganizer().getParticipentLists().remove(ce);
        ce.setOrganizer(null);
        em.remove(ce);
        return true;
    }
}

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
import echo.evs.dto.Participant;
import echo.evs.dto.Question;
import echo.evs.entities.OrganizerEntity;
import echo.evs.entities.PollEntity;
import echo.evs.enums.PollState;
import java.time.LocalDateTime;
import javax.ejb.EJB;

/**
 * Represents the actions over a poll
 * 
 * @author Team Echo
 */
@LocalBean
@Stateless
public class PollAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    @EJB
    private QuestionAccess questionAccess;

    @EJB
    private ParticipantAccess participantAccess;

    /**
     * Stores a Poll using the data received on parameters
     * 
     * @param organizer organizer
     * @param title title
     * @param startdate start date
     * @param endDate end date
     * @param description description
     * @param resultToken result token
     * @param voteCounter number of voters
     * @param trackingEnabled true if tracking is enabled
     * @param participants list of participants
     * @param questions list of questions
     * @param pollState poll state
     * @return the recently stored poll
     */
    public PollEntity createPoll(OrganizerEntity organizer, String title, LocalDateTime startdate, LocalDateTime endDate, String description, String resultToken, Integer voteCounter, Boolean trackingEnabled, List<Participant> participants, List<Question> questions, PollState pollState) {
        PollEntity p = new PollEntity(true);
        p.setTitle(title);
        p.setStartDate(startdate);
        p.setPollState(pollState);
        p.setEndDate(endDate);
        p.setDescription(description);
        p.setResultToken(resultToken);
        p.setVoteCounter(voteCounter);
        p.setTrackingEnabled(trackingEnabled);
        p.setOrganizer(organizer);
        organizer.getPolls().add(p);
        em.persist(p);

        p.setParticipants(participantAccess.createParticipant(participants, p));
        p.setQuestions(questionAccess.createQuestion(questions, p));

        return p;
    }

    /**
     * Gets a list of polls owned by a specific organizer
     * 
     * @param organizer organizer
     * @return list of poll entities
     */
    public List<PollEntity> getPollList(OrganizerEntity organizer) {
        return em.createNamedQuery("getPollList")
                .setParameter("organizer", organizer)
                .getResultList();
    }

    /**
     * Gets a list of all polls
     * 
     * @return list of all poll entities
     */
    public List<PollEntity> getPollList() {
        return em.createNamedQuery("getAllPollList")
                .getResultList();
    }

    /**
     * Gets a list of all poll title strings
     * 
     * @return list of all poll titles
     */
    public List<String> getListOfPollTitle() {
        return em.createNamedQuery("getListOfPollTitle")
                .getResultList();
    }

    /**
     * Obtains a poll by its identifier and its organizer
     *  
     * @param organizer poll organizer
     * @param uuid poll identifier
     * @return poll entity
     */
    public PollEntity getPollByUuid(OrganizerEntity organizer, String uuid) {
        try {
            PollEntity ce = em.createNamedQuery("getPollByUuid", PollEntity.class)
                    .setParameter("organizer", organizer)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return ce;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Obtains a poll by its identifier with admin rights (meaning of any organizer)
     * 
     * @param uuid poll identifier
     * @return poll entity object
     */
    public PollEntity getPollByUuid(String uuid) {
        try {
            PollEntity ce = em.createNamedQuery("getAdminPollByUuid", PollEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return ce;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Obtains a poll by the its results token
     * 
     * @param token the result token
     * @return poll object
     */
    public PollEntity getPollByResultToken(String token) {
        try {
            PollEntity ce = em.createNamedQuery("getPollByResultToken", PollEntity.class)
                    .setParameter("resulttoken", token)
                    .getSingleResult();
            return ce;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Deletes a poll
     * 
     * @param organizer organizer
     * @param uuid poll identifier
     * @return true if the poll was deleted successfully 
     */
    public boolean deletePoll(OrganizerEntity organizer, String uuid) {
        PollEntity pe = getPollByUuid(organizer, uuid);

        if (pe == null) {
            return false;
        }

        organizer.getPolls().remove(pe);
        pe.setOrganizer(null);
        pe.setParticipants(null);
        pe.setQuestions(null);

        em.remove(pe);
        return true;
    }

    /**
     * Deletes a poll with admin rights (meaning of any organizer).
     * @param uuid the uuid of the poll you want to delete
     * @return true if deleted correctly
     */
    public boolean deletePoll(String uuid) {
        PollEntity pe = getPollByUuid(uuid);

        if (pe == null) {
            return false;
        }

        pe.getOrganizer().getPolls().remove(pe);
        pe.setOrganizer(null);
        pe.setParticipants(null);
        pe.setQuestions(null);

        em.remove(pe);
        return true;
    }

    /**
     * Creates an event to schedule the end of the voting period of a poll
     * 
     * @param pe poll object
     */
    public void createEndEvent(PollEntity pe) {
        em.createNativeQuery(" Create event IF NOT EXISTS ee"
                + pe.getUuid().replace("-", "")
                + "  ON SCHEDULE AT '" + pe.getEndDate()
                + "' DO BEGIN UPDATE pollentity SET pollstate = 3 where uuid='" + pe.getUuid() + "';"
                + "delete from tokenentity where poll_id=" + pe.getId() + "; END ").executeUpdate();
    }

    /**
     * Creates an event to schedule the start of the voting period of a poll
     * 
     * @param pe poll object
     */
    public void createStartEvent(PollEntity pe) {
        em.createNativeQuery("Create event IF NOT EXISTS se"
                + pe.getUuid().replace("-", "")
                + "  ON SCHEDULE AT '" + pe.getStartDate()
                + "' DO UPDATE pollentity SET pollstate = 2 where uuid='" + pe.getUuid() + "'").executeUpdate();
    }

    /**
     * Deletes an existing start event
     * 
     * @param uuid event identifier
     */
    public void dropStartEvent(String uuid) {
        em.createNativeQuery("drop event IF EXISTS se"
                + uuid.replace("-", "")).executeUpdate();
    }

    /**
     * Deletes an existing end event
     * 
     * @param uuid event identifier
     */
    public void dropEndEvent(String uuid) {
        em.createNativeQuery("drop event IF EXISTS ee"
                + uuid.replace("-", "")).executeUpdate();
    }
}

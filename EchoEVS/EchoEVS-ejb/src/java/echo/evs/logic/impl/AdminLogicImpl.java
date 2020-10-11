/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.logic.impl;

import echo.evs.dao.PollAccess;
import echo.evs.dto.Item;
import echo.evs.dto.MultipleChoiceQuestion;
import echo.evs.dto.Organizer;
import echo.evs.dto.Participant;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.entities.ItemEntity;
import echo.evs.entities.MultipleChoiceQuestionEntity;
import echo.evs.entities.OrganizerEntity;
import echo.evs.entities.ParticipantEntity;
import echo.evs.entities.PollEntity;
import echo.evs.entities.QuestionEntity;
import echo.evs.enums.PollState;
import echo.evs.enums.QuestionType;
import echo.evs.logic.AdminLogic;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Represents the actions executed by the Administrator
 * @author Team Echo
 */
@Stateless
public class AdminLogicImpl implements AdminLogic {

    @EJB
    private PollAccess pollAccess;
    
    @EJB
    private MailService mailService;

    @Override
    @RolesAllowed(ADMIN_ROLE)
    public List<Poll> getPollList() {
        return pollAccess.getPollList().stream()
                .map(this::createDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @RolesAllowed(ADMIN_ROLE)
    public boolean deletePoll(String uuid) {
        Poll poll = createDTO(pollAccess.getPollByUuid(uuid));
        
        if (pollAccess.deletePoll(uuid)) {
            if (poll.getPollState() != PollState.PREPARED && poll.getPollState() != PollState.FINISHED) {
                Thread newThread = new Thread(() -> {
                    mailService.sendPollDeleteByAdminMail(poll);
                });
                newThread.start();
            }
            return true;
        }
        return false;
    }

    /// Create DTOs
    
    private Organizer createDTO(OrganizerEntity oe) {
        return new Organizer(oe.getUuid(), oe.getJpaVersion(), oe.getEmail(), oe.getFirstName(), oe.getLastName());
    }

    private Poll createDTO(PollEntity pe) {
        return new Poll(pe.getUuid(), pe.getJpaVersion(), pe.getTitle(), createDTO(pe.getOrganizer()), convertToDateViaSqlTimestamp(pe.getStartDate()), convertToDateViaSqlTimestamp(pe.getEndDate()), pe.getPollState(), pe.getDescription(), pe.getResultToken(), pe.getVoteCounter(), pe.getTrackingEnabled(), getParticipants(pe.getParticipants()), getQuestionList(pe.getQuestions()));
    }

    private Question createDTO(QuestionEntity qe) {
        if (qe.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            return new MultipleChoiceQuestion(qe.getUuid(), qe.getJpaVersion(), qe.getTitle(), getItemList(qe.getItems()), qe.getAbsentions(), qe.getTotalVotes(), ((MultipleChoiceQuestionEntity) qe).getMinChoices(), ((MultipleChoiceQuestionEntity) qe).getMaxChoices());
        } else {
            return new Question(qe.getUuid(), qe.getJpaVersion(), qe.getTitle(), getItemList(qe.getItems()), qe.getQuestionType(), qe.getAbsentions(), qe.getTotalVotes());
        }
    }

    private Participant createDTO(ParticipantEntity pe) {
        return new Participant(pe.getUuid(), pe.getJpaVersion(), pe.getEmail());
    }

    private Item createDTO(ItemEntity ie) {
        return new Item(ie.getUuid(), ie.getJpaVersion(), ie.getShortName(), ie.getDescription(), ie.getVotes());
    }

    //////////////////// HELPERS /////////////////////////////////////

    /**
     * Convert date to timestamp
     * 
     * @param dateToConvert
     * @return date as timestamp
     */
    private Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    /**
     * Obtain question list based on list of questionEntities
     * 
     * @param questionEntity list of question entities
     * @return list of question
     */
    public List<Question> getQuestionList(List<QuestionEntity> questionEntity) {
        return questionEntity.stream().map(this::createDTO).collect(Collectors.toList());
    }

    /**
     * Obtain participant list based on list of participant entities
     * 
     * @param participantEntity list of participant entities
     * @return list of participants
     */
    public List<Participant> getParticipants(List<ParticipantEntity> participantEntity) {
        return participantEntity.stream().map(this::createDTO).collect(Collectors.toList());
    }

    /**
     * Obtain item list based on list of item entities
     * 
     * @param itemEntity list of items entities
     * @return item list
     */
    public List<Item> getItemList(List<ItemEntity> itemEntity) {
        return itemEntity.stream().map(this::createDTO).collect(Collectors.toList());
    }
}

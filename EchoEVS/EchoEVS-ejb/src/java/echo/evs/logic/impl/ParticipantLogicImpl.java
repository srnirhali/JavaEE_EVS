/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.logic.impl;

import echo.evs.dao.ItemAccess;
import echo.evs.dao.PollAccess;
import echo.evs.dao.QuestionAccess;
import echo.evs.dao.TokenAccess;
import echo.evs.dto.Item;
import echo.evs.dto.MultipleChoiceQuestion;
import echo.evs.dto.Organizer;
import echo.evs.dto.Participant;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.dto.Token;
import echo.evs.entities.ItemEntity;
import echo.evs.entities.MultipleChoiceQuestionEntity;
import echo.evs.entities.OrganizerEntity;
import echo.evs.entities.ParticipantEntity;
import echo.evs.entities.PollEntity;
import echo.evs.entities.QuestionEntity;
import echo.evs.entities.TokenEntity;
import echo.evs.enums.PollState;
import echo.evs.enums.QuestionType;
import echo.evs.logic.ParticipantLogic;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Represents the actions executed by a Participant
 * 
 * @author Team Echo
 */
@Stateless
public class ParticipantLogicImpl implements ParticipantLogic {

    @EJB
    private TokenAccess tokenAccess;

    @EJB
    private ItemAccess itemAccess;

    @EJB
    private QuestionAccess questionAccess;

    @EJB
    private PollAccess pollAccess;

    @Override
    public Token getByToken(String token) {
        TokenEntity tokenEntity = tokenAccess.getByToken(token);
        return createDTO(tokenEntity);
    }

    @Override
    public boolean submitPoll(Token token) {
        for (Question question : token.getPoll().getQuestions()) {
            QuestionEntity qe = questionAccess.getQuestionByUuid(question.getUuid());
            int newVotes = 0;

            if (question.isAbsentVote()) {
                qe.setAbsentions(qe.getAbsentions() + 1);
                newVotes = 1;
            } else {
                for (Item item : question.getItems()) {
                    if (item.isParticipantVote()) {
                        ItemEntity ie = itemAccess.getItemByUuid(item.getUuid());
                        int vote = ie.getVotes() + 1;
                        ie.setVotes(vote);
                        newVotes += 1;
                    }
                }
            }
            qe.setTotalVotes(qe.getTotalVotes() + newVotes);
        }
        tokenAccess.deleteToken(token.getToken());
        PollEntity pe = pollAccess.getPollByUuid(token.getPoll().getUuid());
        pe.setVoteCounter(pe.getVoteCounter() + 1);
        if (pe.getTokens() == null || pe.getTokens().isEmpty()) {
            pe.setPollState(PollState.FINISHED);
            pollAccess.dropEndEvent(pe.getUuid());
        }
        return true;
    }

    @Override
    public Poll getPollByResultToken(String token) {
        PollEntity pe = pollAccess.getPollByResultToken(token);
        return pe == null ? null : createDTO(pe);
    }

    ///////////////////// CREATE DTOs //////////////////////////////////
    private Poll createDTO(PollEntity pe) {
        return new Poll(pe.getUuid(), pe.getJpaVersion(), pe.getTitle(), createDTO(pe.getOrganizer()), convertToDateViaSqlTimestamp(pe.getStartDate()), convertToDateViaSqlTimestamp(pe.getEndDate()), pe.getPollState(), pe.getDescription(), pe.getResultToken(), pe.getVoteCounter(), pe.getTrackingEnabled(), getParticipants(pe.getParticipants()), getQuestionList(pe.getQuestions()));
    }

    private Participant createDTO(ParticipantEntity pe) {
        return new Participant(pe.getUuid(), pe.getJpaVersion(), pe.getEmail());
    }

    private Token createDTO(TokenEntity token) {
        Poll poll = createDTO(token.getPoll());
        if (poll.getTrackingEnabled()) {
            Participant p = createDTO(token.getParticipant());
            return new Token(token.getUuid(), token.getJpaVersion(), token.getToken(), p, poll);
        } else {
            return new Token(token.getUuid(), token.getJpaVersion(), token.getToken(), null, poll);
        }
    }

    private Question createDTO(QuestionEntity qe) {
        if (qe.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            return new MultipleChoiceQuestion(qe.getUuid(), qe.getJpaVersion(), qe.getTitle(), getItemList(qe.getItems()), qe.getAbsentions(), qe.getTotalVotes(), ((MultipleChoiceQuestionEntity) qe).getMinChoices(), ((MultipleChoiceQuestionEntity) qe).getMaxChoices());
        } else {
            return new Question(qe.getUuid(), qe.getJpaVersion(), qe.getTitle(), getItemList(qe.getItems()), qe.getQuestionType(), qe.getAbsentions(), qe.getTotalVotes());
        }
    }

    private Item createDTO(ItemEntity ie) {
        return new Item(ie.getUuid(), ie.getJpaVersion(), ie.getShortName(), ie.getDescription(), ie.getVotes());
    }

    private Organizer createDTO(OrganizerEntity oe) {
        return new Organizer(oe.getUuid(), oe.getJpaVersion(), oe.getEmail(), oe.getFirstName(), oe.getLastName());
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
     * Obtain participant list based on list of participant entities
     * 
     * @param participantEntity list of participant entities
     * @return list of participants
     */
    private List<Participant> getParticipants(List<ParticipantEntity> participantEntity) {
        return participantEntity.stream().map(this::createDTO).collect(Collectors.toList());
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
     * Obtain item list based on list of item entities
     * 
     * @param itemEntity list of items entities
     * @return item list
     */
    public List<Item> getItemList(List<ItemEntity> itemEntity) {
        return itemEntity.stream().map(this::createDTO).collect(Collectors.toList());
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.logic;

import java.util.List;
import javax.ejb.Remote;
import echo.evs.dto.Item;
import echo.evs.dto.Organizer;
import echo.evs.dto.Participant;
import echo.evs.dto.ParticipantInList;
import echo.evs.dto.ParticipantList;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.entities.ItemEntity;
import echo.evs.entities.ParticipantEntity;
import echo.evs.entities.PollEntity;
import echo.evs.entities.QuestionEntity;
import echo.evs.enums.PollState;
import java.time.LocalDateTime;

/**
 * Defines the interfaces for the Organizer actions
 * @author Team Echo
 */
@Remote
public interface OrganizerLogic {

    public static final String ORGANIZER_ROLE = "ORGANIZER";

    /**
     * Get the current organizer that is logged in
     * 
     * @return current organzizer
     */
    public Organizer getCurrentUser();

    /**
     * Update Organizer. Not supported yet.
     * 
     * @param organizer new Organizer
     * @return uuid of organizer
     */
    public String updateOrganizer(Organizer organizer);

    // POLLS
    
    /**
     * Store the poll in the database using the poll dto values. If the poll is new it will create a poll instead. 
     * Creates/updates events depending on poll states and changes to start and end date
     * 
     * @param poll Poll to be stored
     * @return uuid of stored poll
     */
    public String storePoll(Poll poll);

    /**
     * Deletes a poll based on uuid. It will only consider polls that are owned by the organizer that calls this method.
     * If poll is in state STARTED, VOTING or PUBLISHED a notification mail will be sent to all participants.
     * 
     * @param uuid uuif of the poll to be deleted
     * @return true if successfully deleted
     */
    public boolean deletePoll(String uuid);

    /**
     * Get poll by uuid. It will only consider polls that are owned by the organizer that calls this methods.
     * 
     * @param uuid uuid of the poll
     * @return poll dto or null if no poll was found
     */
    public Poll getPoll(String uuid);

    /**
     * Creates a poll based on the passed parameters. Resulttoken and votecounter is automatically set to null/0.
     * 
     * @param title title of poll
     * @param startDate startDate of poll
     * @param endDate endDate of poll
     * @param description desciprion of poll
     * @param trackingEnabled boolean if tracking is enabled
     * @param participants list of participants
     * @param questions list of questions
     * @param pollState pollState
     * @return Poll dto
     */
    public Poll createPoll(String title, LocalDateTime startDate, LocalDateTime endDate, String description, Boolean trackingEnabled, List<Participant> participants, List<Question> questions, PollState pollState);

    /**
     * Get list of polls owned by the organizer that calls this method
     * 
     * @return list of polls
     */
    public List<Poll> getPollList();

    /**
     * Get list of all poll titles
     * 
     * @return list of poll titles
     */
    public List<String> getListOfPollTitle();

    /**
     * Get list of questions based on list of question entities
     * 
     * @param questionEntity list of question entities
     * @return list of question dtos
     */
    public List<Question> getQuestionList(List<QuestionEntity> questionEntity);

    /**
     * Get list of participants based on list of participant entities
     * 
     * @param participantEntity list of participant entities
     * @return list of participants
     */
    public List<Participant> getParticipants(List<ParticipantEntity> participantEntity);

    /**
     * Get list of items based on list of item entities
     * 
     * @param itemEntity list of item entities
     * @return list of items
     */
    public List<Item> getItemList(List<ItemEntity> itemEntity);

    /**
     * Starts a poll. Depending on the startdate it may skip the STARTED state and immediatly enters VOTING state.
     * Also generates tokens for all participants and send emails containing the token values.
     * 
     * @param poll poll to start
     * @return started poll uuid
     */
    public String startPoll(Poll poll);

    /**
     * Publish a poll. It will only publish if the poll is FINISHED and at least 3 people voted. 
     * It will generate and store a result token for a poll and then send out emails with the result page link to all participants.
     * 
     * @param uuid uuif of the poll that should be published
     * @return uuid of published poll
     */
    public String publishPoll(String uuid);

    /**
     * Send reminders emails to all participants that still have a valid token related to that poll.
     * 
     * @param uuid uuid of poll for which reminders should be sent
     * @return uuid of poll
     */
    public String sendReminders(String uuid);
    
    /**
     * Creates and stores tokens for every participant of a poll
     * 
     * @param pollEntity pollEntity for which tokens should be generated
     */
    public void createToken(PollEntity pollEntity);

    // PARTICIPANT LIST
    
    /**
     * Create and stores a participant list. 
     * 
     * @param Name name of participant list
     * @param participants list of participantInList that should be stored in list
     * @return participantList
     */
    public ParticipantList createParticipantList(String Name, List<ParticipantInList> participants);

    /**
     * Get Participant list identified by uuid. Only list are considered that are owned by the current organizer.
     * 
     * @param uuid uuid of participant list
     * @return participantlist
     */
    public ParticipantList getParticipantList(String uuid);

    /**
     * Get all participant lists owned by current organizer.
     * 
     * @return list of participantlists
     */
    public List<ParticipantList> getParticipantLists();

    /**
     * Stores a participant list based on dto values
     * 
     * @param participantList participant list dto
     * @return uuid of stored participant list
     */
    public String storeParticipantList(ParticipantList participantList);

    /**
     * Delete participant list by uuid. Only participant lists that are owned by the current organizer are considered.
     * 
     * @param uuid uuid of participant list
     * @return true if successfully deleted
     */
    public boolean deleteParticipantList(String uuid);

}

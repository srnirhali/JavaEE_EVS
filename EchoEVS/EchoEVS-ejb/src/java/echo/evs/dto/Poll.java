/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

import echo.evs.enums.PollState;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Data Transfer Object representing a poll
 * 
 * @author Team Echo
 */
@XmlRootElement
public class Poll extends AbstractDTO {

    private String title;
    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.ORDINAL)
    private PollState pollState;

    private String description;
    private String resultToken;
    private Integer voteCounter;

    private Boolean trackingEnabled;

    private List<Participant> participants;
    private Organizer organizer;
    private List<Question> questions;

    /**
     * Empty constructor
     */
    public Poll() {
        this.voteCounter = 0;
        this.trackingEnabled = false;
        this.participants = new ArrayList<>();
        this.questions = new ArrayList<>();
    }

    /**
     * The constructor of a poll using all passed parameter
     * 
     * @param uuid uuid
     * @param jpaVersion jpaVersion 
     * @param title title
     * @param organizer organizer
     * @param startDate start date
     * @param endDate end date
     * @param pollState poll state of the poll
     * @param description description
     * @param resultToken result token
     * @param voteCounter votes given to a poll
     * @param trackingEnabled boolean if tracking should be enabled
     * @param participants list of participants belonging to poll
     * @param questions list of question belonging to a poll
     */
    public Poll(String uuid, int jpaVersion, String title, Organizer organizer, Date startDate, Date endDate, PollState pollState, String description, String resultToken, Integer voteCounter, Boolean trackingEnabled, List<Participant> participants, List<Question> questions) {
        super(uuid, jpaVersion);
        this.title = title;
        this.organizer = organizer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.resultToken = resultToken;
        this.voteCounter = voteCounter;
        this.trackingEnabled = trackingEnabled;
        this.participants = participants;
        this.questions = questions;
        this.pollState = pollState;
    }

    /**
     * Gets the organizer
     * 
     * @return organizer dto
     */
    public Organizer getOrganizer() {
        return organizer;
    }

    /**
     * Sets the organizer of the poll
     * 
     * @param organizer organizer to be set
     */
    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    /**
     * Gets the title of the poll
     * 
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the poll
     * 
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the start date
     * 
     * @return start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date
     * 
     * @param startDate start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date
     * 
     * @return end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date
     * 
     * @param endDate end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the poll state 
     * 
     * @return poll state
     */
    public PollState getPollState() {
        return pollState;
    }
    
    /**
     * Sets the poll state
     * 
     * @param pollState poll state
     */
    public void setPollState(PollState pollState) {
        this.pollState = pollState;
    }

    /**
     * Gets the description of a poll
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of a poll
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the result token
     * 
     * @return result token
     */
    public String getResultToken() {
        return resultToken;
    }
    
    /**
     * Sets the result token of a poll
     * 
     * @param resultToken result token string
     */
    public void setResultToken(String resultToken) {
        this.resultToken = resultToken;
    }
    
    /**
     * Gets the vote counter of a poll
     * 
     * @return vote counter
     */
    public Integer getVoteCounter() {
        return voteCounter;
    }
    
    /**
     * Sets the vote counter of a poll
     * 
     * @param voteCounter number of voters
     */
    public void setVoteCounter(Integer voteCounter) {
        this.voteCounter = voteCounter;
    }

    /**
     * Checks wether tracking is enabled or not
     * 
     * @return true, if enabled
     */
    public Boolean getTrackingEnabled() {
        return trackingEnabled;
    }

    /**
     * Sets if tracking is enabled or not
     * 
     * @param trackingEnabled true, if tracking should be enabled
     */
    public void setTrackingEnabled(Boolean trackingEnabled) {
        this.trackingEnabled = trackingEnabled;
    }
    
    /**
     * Gets the participant of a poll
     * 
     * @return list of participants
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * Sets the participants of the poll
     * 
     * @param participants list of participants
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Gets the list of questions of the poll
     * 
     * @return list of questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Sets the list of questions of the poll
     * 
     * @param questions the list of questions to be set
     */
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    /**
     * Adds a participant to a poll
     * 
     * @param participant participant to be added
     * @return true if successfully added, else false
     */
    public boolean addParticipant(Participant participant) {
        return participants.add(participant);
    }

    /**
     * Removes a participant from the poll
     * 
     * @param participant participant to be removed
     * @return true if succuessfully removed, if not found or not removed false
     */
    public boolean removeParticipant(Participant participant) {
        if (participant.getUuid() != null) {
            return participants.remove(participant);
        } else {
            // if UUID not set we have to compare by Email-String, as otherwise wrong participants get removed
            String emailToDelete = participant.getEmail();
            for (int i = 0; i < participants.size(); i++) {
                if (participants.get(i).getEmail().equals(emailToDelete)) {
                    participants.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     * Adds a question to the poll
     * 
     * @param question question to be added
     * @return true, if successfully added, else false
     */
    public boolean addQuestion(Question question) {
        return questions.add(question);
    }

    /**
     * Removes a question from the poll
     * 
     * @param question question to be removed
     * @return true if successfully removed, else false (or if not found)
     */
    public boolean removeQuestion(Question question) {
        if (question.getUuid() != null) {
            return questions.remove(question);
        } else {
            // if UUID not set we have to compare by Title-String, as otherwise wrong questions get removed
            String questionToDelete = question.getTitle();
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).getTitle().equals(questionToDelete)) {
                    questions.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
}

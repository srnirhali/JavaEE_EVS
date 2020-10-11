/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import echo.evs.enums.PollState;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Represents the poll entity
 * 
 * @author Team Echo
 */
@NamedQueries({
    @NamedQuery(
            name = "getAllPollList",
            query = "SELECT c FROM PollEntity c"
            + " ORDER BY c.title"
    ),
    @NamedQuery(
            name = "getPollList",
            query = "SELECT c FROM PollEntity c"
            + " WHERE c.organizer = :organizer"
            + " ORDER BY c.title"
    ),
    @NamedQuery(
            name = "getPollByUuid",
            query = "SELECT c FROM PollEntity c"
            + " WHERE c.organizer = :organizer AND c.uuid = :uuid"
    ),
    @NamedQuery(
            name = "getAdminPollByUuid",
            query = "SELECT c FROM PollEntity c"
            + " WHERE c.uuid = :uuid"
    ),
    @NamedQuery(
            name = "deletePollByUuid",
            query = "DELETE FROM PollEntity c WHERE c.uuid = :uuid"
    ),
    @NamedQuery(
            name = "getPollByResultToken",
            query = "SELECT c FROM PollEntity c"
            + " WHERE c.resultToken = :resulttoken"
    ),
    @NamedQuery(name = "getListOfPollTitle",
            query = "SELECT c.title FROM PollEntity c")
})

@Entity
public class PollEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1390000632429300L;

    @Column(unique = true)
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.ORDINAL)
    private PollState pollState;

    private String description;
    private String resultToken;
    private Integer voteCounter;
    private Boolean trackingEnabled;

    @OneToMany(
            mappedBy = "poll",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ParticipantEntity> participants;

    @OneToMany(
            mappedBy = "poll",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TokenEntity> tokens;

    public List<TokenEntity> getTokens() {
        return tokens;
    }

    public void setTokens(List<TokenEntity> tokens) {
        this.tokens = tokens;
    }

    @ManyToOne
    private OrganizerEntity organizer;

    @OneToMany(
            mappedBy = "poll",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionEntity> questions;

    /**
     * Empty constructor, new Entity is set to false
     */
    public PollEntity() {
        this(false);
    }

    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new
     */
    public PollEntity(boolean newEntity) {
        super(newEntity);
    }

    /**
     * Get title of poll
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title of poll
     * @param title poll
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get start date
     * @return start date
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    /**
     * Set start date
     * @param startDate start date
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    /**
     * Get end date
     * @return end date
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Set end date
     * @param endDate end date
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Get poll state
     * @return poll state
     */
    public PollState getPollState() {
        return pollState;
    }
    
    /**
     * Set poll state
     * @param pollState poll state
     */
    public void setPollState(PollState pollState) {
        this.pollState = pollState;
    }
    
    /**
     * Get description
     * @return description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set Description
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get result token
     * @return result token
     */
    public String getResultToken() {
        return resultToken;
    }
    
    /**
     * Set result token
     * @param resultToken result token
     */
    public void setResultToken(String resultToken) {
        this.resultToken = resultToken;
    }
    
    /**
     * Get number of voters of this poll
     * @return number of voters
     */
    public Integer getVoteCounter() {
        return voteCounter;
    }
    
    /**
     * Set number of voters 
     * @param voteCounter amount of voters
     */
    public void setVoteCounter(Integer voteCounter) {
        this.voteCounter = voteCounter;
    }
    
    /**
     * Get if tracking is enabled or not
     * @return true if enabled
     */
    public Boolean getTrackingEnabled() {
        return trackingEnabled;
    }
    
    /**
     * Set if tracking is enabled
     * @param trackingEnabled true if enabled
     */
    public void setTrackingEnabled(Boolean trackingEnabled) {
        this.trackingEnabled = trackingEnabled;
    }
    
    /**
     * Get list of participants of poll
     * @return list of participants
     */
    public List<ParticipantEntity> getParticipants() {
        return participants;
    }
    
    /**
     * Set list of participants
     * @param participants list of participants
     */
    public void setParticipants(List<ParticipantEntity> participants) {
        this.participants = participants;
    }
    
    /**
     * Get organizer of poll
     * @return organizer
     */
    public OrganizerEntity getOrganizer() {
        return organizer;
    }
    
    /**
     * Set organizer of poll
     * @param organizer organizer
     */
    public void setOrganizer(OrganizerEntity organizer) {
        this.organizer = organizer;
    }

    /**
     * Get list of questions belonging to poll
     * @return list of questions
     */
    public List<QuestionEntity> getQuestions() {
        return questions;
    }

    /**
     * Set list of questions belonging to poll
     * @param questions questions to be set
     */
    public void setQuestions(List<QuestionEntity> questions) {
        this.questions = questions;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import echo.evs.enums.QuestionType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Represents the question entity
 * @author Team Echo
 */
@NamedQueries({
    @NamedQuery(
            name = "getQuestionByUuid",
            query = "SELECT q FROM QuestionEntity q"
            + " WHERE q.uuid = :uuid"
    )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class QuestionEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1390015887581900L;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private QuestionType questionType;

    private Integer totalVotes;
    private Integer absentions;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemEntity> items;

    @OneToOne
    private PollEntity poll;

    /**
     * Empty constructor, new Entity is set to false
     */
    public QuestionEntity() {
        super(false);
    }

    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new
     */
    public QuestionEntity(boolean newEntity) {
        super(newEntity);
    }

    /**
     * Get title of question
     * @return the title of the question
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title of question
     * @param title the title to be set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get question type
     * @return the questionType
     */
    public QuestionType getQuestionType() {
        return questionType;
    }

    /**
     * Set question type
     * @param questionType the questionType to be set
     */
    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    /**
     * Get total number of votes for this question. For multiple choice question this can exceed the number of participants
     * @return total number of votes
     */
    public Integer getTotalVotes() {
        return totalVotes;
    }

    /**
     * Set total number of votes
     * @param totalVotes number of total votes
     */
    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    /**
     * Get number of abstentions from this question
     * @return no. of abstention
     */
    public Integer getAbsentions() {
        return absentions;
    }

    /**
     * Set number of abstentions form this question
     * @param absentions number of abstentions
     */
    public void setAbsentions(Integer absentions) {
        this.absentions = absentions;
    }

    /**
     * Get list of items belonging to this question
     * @return the list of items
     */
    public List<ItemEntity> getItems() {
        return items;
    }

    /**
     * Set list of items belonging to this question
     * @param items the list of items to be set
     */
    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    /**
     * Get poll this question is belonging to
     * @return the poll
     */
    public PollEntity getPoll() {
        return poll;
    }

    /**
     * Set poll this question is belonging to
     * @param poll the poll to be set
     */
    public void setPoll(PollEntity poll) {
        this.poll = poll;
    }
}

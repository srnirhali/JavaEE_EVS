/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Represents the item entity
 * 
 * @author Team Echo
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "getItemByUuid",
            query = "SELECT c FROM ItemEntity c"
            + " WHERE c.uuid = :uuid"
    )
})
public class ItemEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1389853385529200L;

    private String shortName;
    private String description;
    private Integer votes;

    @OneToOne
    private QuestionEntity question;

    /**
     * Empty constructor, new Entity is set to false
     */
    public ItemEntity() {
        this(false);
    }

    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new (no uuid yet)
     */
    public ItemEntity(boolean newEntity) {
        super(newEntity);
    }

    /**
     * Get short name of item
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Set short name of item
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Get optional description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description of item
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get number of votes of item
     * @return number of votes
     */
    public Integer getVotes() {
        return votes;
    }

    /**
     * Set number of votes
     * @param votes number of votes
     */
    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    /**
     * Get Question this item belongs to
     * @return the question
     */
    public QuestionEntity getQuestion() {
        return question;
    }

    /**
     * Set item this question belongs to
     * @param question the question to be set
     */
    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

}

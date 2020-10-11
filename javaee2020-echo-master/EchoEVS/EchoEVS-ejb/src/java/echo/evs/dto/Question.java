/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

import echo.evs.enums.QuestionType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Data Transfer Object representing a question
 * 
 * @author Team Echo
 */
@XmlRootElement
public class Question extends AbstractDTO {

    private String title;
    private List<Item> items;

    @Enumerated(EnumType.ORDINAL)
    private QuestionType questionType;

    private Integer totalVotes;
    private Integer absentions;

    private boolean absentVote;

    /**
     * Empty constructor
     */
    public Question() {
        items = new ArrayList<>();
    }

    /**
     * Constructor using all passed parameters
     * 
     * @param uuid uuid
     * @param jpaVersion jpaVersion
     * @param title title
     * @param items list of items belonging to this question
     * @param questionType question type
     * @param absentions number of abstentions
     * @param totalVotes number of total votes (for multiple choice questions this might exceed the number of participants)
     */
    public Question(String uuid, int jpaVersion, String title, List<Item> items, QuestionType questionType, Integer absentions, Integer totalVotes) {
        super(uuid, jpaVersion);
        this.title = title;
        this.items = items;
        this.questionType = questionType;
        this.absentions = absentions;
        this.totalVotes = totalVotes;
    }

    /**
     * Gets the title of the question
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the question
     * 
     * @param title the title to be set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the list of items belonging to this questions
     * 
     * @return list of items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Set the list of items belonging to this question
     * 
     * @param items the list of items to be set
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * Gets the question type
     * 
     * @return the questionType
     */
    public QuestionType getQuestionType() {
        return questionType;
    }

    /**
     * Set the question type
     * 
     * @param questionType the questionType to set
     */
    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    /**
     * Gets number of abstenton of this question 
     * 
     * @return no of abstentions
     */
    public Integer getAbsentions() {
        return absentions;
    }

    /**
     * Gets number of total votes of this questions. For multiple choice questions this could exceed the number of participants
     * 
     * @return no of total votes
     */
    public Integer getTotalVotes() {
        return totalVotes;
    }

    /**
     * Add item to question
     * 
     * @param item Item to be added
     * @return true if successfully added
     */
    public boolean addItem(Item item) {
        return items.add(item);
    }

    /**
     * Removes item from question 
     * 
     * @param item remove item to be removed
     * @return true if successfully removed
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }
    
    /**
     * Checks wether 'abstention' was voted by a participant. Is not stored in the database and only used as temp value for submitting votes.
     * 
     * @return true if 'abstention' was marked
     */
    public boolean isAbsentVote() {
        return absentVote;
    }

    /**
     * Sets wether a participant absent from this question. It is not stored in the database and only used as temp value for submitting votes.
     * 
     * @param absentVote boolean wether participant absent from question
     */
    public void setAbsentVote(boolean absentVote) {
        this.absentVote = absentVote;
    }

}

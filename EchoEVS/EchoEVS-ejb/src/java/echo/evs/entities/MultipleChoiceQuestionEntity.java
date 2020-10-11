/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import echo.evs.enums.QuestionType;
import javax.persistence.Entity;

/**
 * Represents the multiple choice entity
 * @author Team Echo
 */
@Entity
public class MultipleChoiceQuestionEntity extends QuestionEntity {

    private Integer minChoices;
    private Integer maxChoices;

    /**
     * Empty constructor. Sets question type to MULTIPLE_CHOICE and not used for new entities
     */
    public MultipleChoiceQuestionEntity() {
        super(false);
        super.setQuestionType(QuestionType.MULTIPLE_CHOICE);
    }

    /**
     * Constructor that also generates uuid if entity is new. Sets question type to MULTIPLE_CHOICE
     * @param newEntity true if new
     */
    public MultipleChoiceQuestionEntity(boolean newEntity) {
        super(newEntity);
        super.setQuestionType(QuestionType.MULTIPLE_CHOICE);
    }

    /**
     * Get minimum amount of choices user should make
     * @return number of min choices
     */
    public Integer getMinChoices() {
        return minChoices;
    }

    /**
     * Set the minimum amount of choices a user should make
     * @param minChoices number of minimum amount of choices allowed
     */
    public void setMinChoices(Integer minChoices) {
        this.minChoices = minChoices;
    }

    /**
     * Get the maximum number of choices a user can make
     * @return max number of choices
     */
    public Integer getMaxChoices() {
        return maxChoices;
    }

    /**
     * Set maximum number of choices a user can make
     * @param maxChoices max number of choices
     */
    public void setMaxChoices(Integer maxChoices) {
        this.maxChoices = maxChoices;
    }

}

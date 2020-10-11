/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

import echo.evs.enums.QuestionType;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Data Transfer Object representing a multiple choice question
 * 
 * @author Team Echo
 */
@XmlRootElement
public class MultipleChoiceQuestion extends Question {

    private Integer minChoices;
    private Integer maxChoices;

    public MultipleChoiceQuestion() {
        super();
    }

    public MultipleChoiceQuestion(String uuid, int jpaVersion, String title, List<Item> items, Integer absentions, Integer totalVotes, Integer minChoices, Integer maxChoices) {
        super(uuid, jpaVersion, title, items, QuestionType.MULTIPLE_CHOICE, absentions, totalVotes);
        this.minChoices = minChoices;
        this.maxChoices = maxChoices;
    }

    public Integer getMinChoices() {
        return minChoices;
    }

    public void setMinChoices(Integer minChoices) {
        this.minChoices = minChoices;
    }

    public Integer getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(Integer maxChoices) {
        this.maxChoices = maxChoices;
    }

}

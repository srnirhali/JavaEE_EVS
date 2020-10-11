/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import echo.evs.dto.Item;
import echo.evs.dto.MultipleChoiceQuestion;
import echo.evs.dto.Participant;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.enums.PollState;
import echo.evs.enums.QuestionType;
import echo.evs.logic.OrganizerLogic;
import java.time.LocalDateTime;

/**
 * Represent the actions done in the poll overview page
 * 
 * @author Team Echo
 */
@RequestScoped
@Named
public class PollListBean implements Serializable {

    private static final long serialVersionUID = 1390147387883000L;

    @EJB
    private OrganizerLogic ol;

    private List<Poll> pollList;
    
    /**
     * Get list of polls belonging to current organizer 
     * 
     * @return list of polls
     */
    public List<Poll> getPollList() {
        if (pollList == null) {
            pollList = ol.getPollList();
        }
        return pollList;
    }

    /**
     * Deletes a poll. Can only delete owned polls.
     * 
     * @param poll poll to delete
     */
    public void deletePoll(Poll poll) {
        ol.deletePoll(poll.getUuid());
        pollList = ol.getPollList();
    }

    /**
     * Generate some test data
     */
    public void testData() {
        List<Question> questions = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setShortName("Testing Item 1");
        items.add(item);
        item = new Item();
        item.setShortName("Testing Item 2");
        items.add(item);
        Question q = new Question();
        q.setTitle("Testing Question 1");
        q.setQuestionType(QuestionType.SINGLE_CHOICE);
        q.setItems(items);
        questions.add(q);

        items = new ArrayList<>();
        item = new Item();
        item.setShortName("Testing Item 1");
        items.add(item);
        item = new Item();
        item.setShortName("Testing Item 2");
        items.add(item);
        item = new Item();
        item.setShortName("Testing Item 3");
        items.add(item);
        MultipleChoiceQuestion q1 = new MultipleChoiceQuestion();
        q1.setTitle("Testing Question 2");
        q1.setItems(items);
        q1.setMaxChoices(3);
        q1.setMinChoices(1);
        q1.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        questions.add(q1);

        List<Participant> participants = new ArrayList<>();
        Participant participant1 = new Participant();
        participant1.setEmail("srnirhali@gmail.com");
        participants.add(participant1);
        Participant participant2 = new Participant();
        participant2.setEmail("srnirhali369@gmail.com");
        participants.add(participant2);
        Participant participant3 = new Participant();
        participant3.setEmail("test3@mail.com");
        participants.add(participant3);
        String em = ol.getCurrentUser().getEmail();
        String titString = "Testing-" + ol.getCurrentUser().getEmail() + "-" + ol.getPollList().size();
        ol.createPoll(titString, LocalDateTime.now(), LocalDateTime.now(), "Description of test poll", false, participants, questions, PollState.PREPARED);
    }

}

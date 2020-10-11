/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.web;

import echo.evs.dto.Item;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.enums.PollState;
import echo.evs.logic.OrganizerLogic;
import echo.evs.logic.ParticipantLogic;
import echo.evs.web.i18n.Messages;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Team Echo
 */
@ViewScoped
@Named
public class PollResultBean implements Serializable {

    private static final long serialVersionUID = 1390272033953600L;

    @EJB
    private OrganizerLogic ol;

    @EJB
    private ParticipantLogic pl;

    private String uuid;
    private Poll poll;

    private String token;
    private Poll publishedPoll;

    private boolean success;
    private boolean tempPublished;

    /**
     * @return true if a poll with the specified uuid could be loaded and is
     * also finished or published
     */
    public boolean isValid() {
        return uuid != null && getPoll() != null && (poll.getPollState() == PollState.FINISHED | poll.getPollState() == PollState.PUBLISHED);
    }

    /**
     * Loads the poll based on the uuid. Also checks wether the poll is in state
     * finished or published
     *
     * @return loaded poll
     */
    public Poll getPoll() {
        if (poll == null) {
            poll = ol.getPoll(uuid);
            tempPublished = false;

            if (poll != null && !(poll.getPollState() == PollState.FINISHED | poll.getPollState() == PollState.PUBLISHED)) {
                poll = null;
            }
        }

        return poll;
    }

    /**
     * Get uuid that is used
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set uuid that is used
     *
     * @param uuid uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Loads a poll that is published based on result token
     * @return poll referenced by token
     */
    public Poll getPublishedPoll() {
        if (publishedPoll == null) {
            publishedPoll = pl.getPollByResultToken(token);

            if (publishedPoll != null && publishedPoll.getPollState() != PollState.PUBLISHED) {
                publishedPoll = null;
            }
        }

        return publishedPoll;
    }

    /**
     * Checks if the published poll is valid
     * @return true if publish poll is valid
     */
    public boolean isPublishValid() {
        return token != null && getPublishedPoll() != null && publishedPoll.getPollState() == PollState.PUBLISHED;
    }

    /**
     * Get token that is used
     * @return token string
     */
    public String getToken() {
        return token;
    }

    /**
     * Set token that is used
     * @param token token string
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Checks wether the poll has enough votes so the results can be shown
     * @return true if enough votes
     */
    public boolean hasEnoughVotes() {
        return poll.getVoteCounter() >= 3;
    }

    /**
     * Get the result of a question meaning no of abstention and votes of all items
     * @param question question for which you want the result
     * @return list of items including an abstention item with its number of votes
     */
    public List<Item> getResultOfQuestion(Question question) {
        Item absentItem = new Item();
        absentItem.setShortName(Messages.getMessage("questionAbsentionLabel"));
        absentItem.setVotes(question.getAbsentions());
        List<Item> resultList = new ArrayList<>();
        resultList.addAll(question.getItems());
        resultList.add(absentItem);
        return resultList;
    }

    /**
     * Calculate the percentage based on votes of an item and total votes given
     * @param votesForThisItem number of votes of an item
     * @param totalVotes number of votes of a question
     * @return string representing percentage value in decimal format
     */
    public String getPercentage(Integer votesForThisItem, Integer totalVotes) {
        float percentage = (1.0f * votesForThisItem / totalVotes) * 100;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        return df.format(percentage);
    }

    /**
     * Deletes the loaded poll
     */
    public void deletePoll() {
        ol.deletePoll(uuid);
    }

    /**
     * Publishes a poll. 
     * Checks is poll hasn't been published yet and is in state FINISHED and also has enough votes.
     */
    public void publishPoll() {
        if (hasEnoughVotes() && poll.getPollState() == PollState.FINISHED && !tempPublished) {
            tempPublished = true; // Used so button doesn't trigger multiple times
            uuid = ol.publishPoll(uuid);

            if (uuid != null) {
                success = true;
                // force reload since the JPA version might have changed
                poll = null;
            }
        } else {
            System.out.print("Not enough votes to publish Poll or already published");
        }
    }
    
    /** 
     * Checks wether success is true
     * @return true if success is set to true
     */
    public boolean isSuccess() {
        return success;
    }

}

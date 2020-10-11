/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.web;

import echo.evs.dto.Item;
import echo.evs.dto.MultipleChoiceQuestion;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.dto.Token;
import echo.evs.enums.PollState;
import echo.evs.enums.QuestionType;
import echo.evs.logic.ParticipantLogic;
import echo.evs.web.i18n.Messages;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Represents actions done when participant is voting
 * 
 * @author Team Echo
 */
@ViewScoped
@Named
public class VotingBean implements Serializable {

    private static final long serialVersionUID = 1390297419553000L;

    @EJB
    private ParticipantLogic pl;

    private String token;
    private Poll poll;
    private Token tokenPoll;

    private boolean success;
    private boolean failure;
    private String failureMessage;

    /**
     * Get token string that is used
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set token sting that is used. Loads poll based on the token
     * @param token token string
     */
    public void setToken(String token) {
        if (token != null) {
            this.token = token;
            getPollByToken();
        }
    }

    /**
     * Check if a token is set.
     * @return true if token is set
     */
    public boolean isTokenSet() {
        return token != null;
    }

    /**
     * Loads a poll based by the token that is set. 
     * Also checks if the poll is in state voting and if not sets the failure messages accordingly.
     */
    public void getPollByToken() {
        try {
            if (token != null) {
                tokenPoll = pl.getByToken(token);
                if (tokenPoll.getPoll().getPollState().equals(PollState.VOTING)) {
                    failure = false;
                    poll = tokenPoll.getPoll();
                } else {
                    token = null;
                    failure = true;
                    if (tokenPoll.getPoll().getPollState().equals(PollState.FINISHED)) {
                        failureMessage = Messages.getMessage("votingPollAlreadyEndedMessage");
                    } else {
                        failureMessage = Messages.getMessage("votingPollNotStartedMessage");
                    }
                }
            }
        } catch (Exception e) {
            failure = true;
            failureMessage = Messages.getMessage("votingTokenInvalidMessage");
            token = null;
        }

    }

    /**
     * Get poll that is loaded
     * @return poll
     */
    public Poll getPoll() {
        return poll;
    }
    
    /**
     * Set poll that is loaded
     * @param poll poll
     */
    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    /**
     * @return true if the poll could be stored successfully
     */
    public boolean isSuccess() {
        return success && !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * @return true if the poll could not be stored
     */
    public boolean isFailure() {
        return failure && !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * Get the failure message that is shown when failure is true
     * @return failure message
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * Not used anymore. Get max choices of a question
     * @param question question
     * @param item item
     */
    public void checkChoices(Question question, Item item) {
        MultipleChoiceQuestion choiceQuestion = (MultipleChoiceQuestion) question;
        choiceQuestion.getMaxChoices();
    }

    /**
     * Check if all votes are valid.
     * Checks for single and yes/no question if one item is marked or user absent from this question
     * Checks for multiple choice question if user entered the correct number of votes or absent from this question
     * 
     * @return true if valid
     */
    public boolean isVotesValid() {

        failureMessage = "";
        boolean isValid = true;

        List<Question> questions = poll.getQuestions();
        for (Question question : questions) {
            if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
                int counter = 0;
                for (Item item : question.getItems()) {
                    if (item.isParticipantVote()) {
                        counter++;
                    }
                }
                if (!(counter >= mcq.getMinChoices() && counter <= mcq.getMaxChoices()) && !question.isAbsentVote()) {
                    failure = true;
                    failureMessage = failureMessage + Messages.getMessage("votingInvalidAmountAnswersMessage", mcq.getTitle()) + " \n";
                    isValid = false;
                }
            }
            if (question.getQuestionType().equals(QuestionType.SINGLE_CHOICE) | question.getQuestionType().equals(QuestionType.YES_NO)) {
                int counter = 0;
                for (Item item : question.getItems()) {
                    if (item.isParticipantVote()) {
                        counter++;
                    }
                }
                if (counter != 1 && !question.isAbsentVote()) {
                    failure = true;
                    failureMessage = failureMessage + Messages.getMessage("votingNoVotesForQuestionMessage", question.getTitle()) + " \n";

                    isValid = false;
                }
            }

        }
        return isValid;
    }

    /**
     * Submits the vote entered by the user if votes are valid. This action will invalidate the token once votes are stored
     */
    public void submitVote() {
        if (isVotesValid()) {
            pl.submitPoll(tokenPoll);
            token = null;
            success = true;
            failure = false;
        }

    }
}

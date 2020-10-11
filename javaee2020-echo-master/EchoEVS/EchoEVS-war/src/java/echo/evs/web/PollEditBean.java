/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.web;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import echo.evs.dto.Item;
import echo.evs.dto.MultipleChoiceQuestion;
import echo.evs.dto.Participant;
import echo.evs.dto.ParticipantList;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.enums.PollState;
import echo.evs.enums.QuestionType;
import echo.evs.logic.OrganizerLogic;
import echo.evs.web.i18n.Messages;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;

/**
 * Represent the actions over a Poll when editing a poll
 * 
 * @author Team Echo
 */
@ViewScoped
@Named
public class PollEditBean implements Serializable {

    private static final long serialVersionUID = 1390674858201000L;

    @EJB
    private OrganizerLogic ol;

    private boolean success;
    private String successMessage;
    private boolean failure;
    private String failureMessage;
    private Poll poll;
    private String oldTitle;
    private List<String> pollTitles;

    private String uuid;

    private Set<String> emailList; // used for duplicate testing
    private String tempMail;
    private UIComponent addParticipantComponent;
    private List<ParticipantList> participantLists;
    private Map<ParticipantList, Boolean> participantListsCheckMap;

    private Question newQuestion;
    private boolean questionAddMode = false;
    private UIComponent storeQuestionButton;
    private Set<String> questionList; // used for duplicate testing
    private String tempItemName = "";
    private String tempItemDescription;
    private UIComponent addItemComponent;

    private UIComponent titleComponent;

    

    /**
     * @return true if a poll with the specified uuid could be loaded
     */
    public boolean isValid() {
        return uuid != null && getPoll() != null;
    }

    /**
     * @return true if the poll could be stored successfully
     */
    public boolean isSuccess() {
        return success && !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * Get the successMessage displayed when success is true
     * @return success message
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * @return true if the poll could not be stored
     */
    public boolean isFailure() {
        return failure && !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * Get the failure message displayed when failure is true
     * @return failure message
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * Get uuid that is used
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set uuid that is used
     * @param uuid uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * lazily loads the Poll with the specified UUID. Creates a new poll if uuid is new.
     * Also sets up all Sets used for duplicate testing if a poll is received (e.g email string list, question title list,..)
     * Will also load the participant lists of the organizer so that user can import these later on
     *
     * @return the Poll DTO
     */
    public Poll getPoll() {
        if (poll == null) {
            if ("new".equals(uuid)) {
                poll = new Poll();
            } else {
                poll = ol.getPoll(uuid);
            }
        }
        if (poll != null) {
            emailList = getEmailsAsStringSet();
            
            if (oldTitle == null) {
                oldTitle = poll.getTitle();
            }
            questionList = getQuestionsAsStringSet();

            participantLists = ol.getParticipantLists();
            participantListsCheckMap = new HashMap<>();

            participantLists.forEach(list -> {
                participantListsCheckMap.put(list, Boolean.FALSE);
            });
        }

        return poll;
    }

    /**
     * Stores the (edited) Poll. If it could not be stored it will set the failure message accordingly
     */
    public void storePoll() {
        try {
            if (poll.getEndDate().after(poll.getStartDate()) && poll.getEndDate().after(new Date())) {
                if (isTitleAvailable()) {
                    uuid = ol.storePoll(poll);
                    if (uuid != null) {
                        success = true;
                        successMessage = Messages.getMessage("editSuccessMessage");
                        failure = false;
                        // force reload since the JPA version might have changed
                        oldTitle = poll.getTitle();
                        poll = null;
                    }
                } else {
                    success = false;
                    failure = true;
                    failureMessage = Messages.getMessage("titleNotAvailableMessage");

                }

            } else {
                success = false;
                failure = true;
                failureMessage = Messages.getMessage("pollEditEndDateBeforeStartDateMessage");

            }
        } catch (EJBException e) {
            success = false;
            failure = true;
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            failureMessage = t.getMessage();
        }
    }

    /**
     * Checks whether the poll is in state Prepared
     * 
     * @return true if the poll is in state PREPARED and false otherwise
     */
    public boolean isPollPrepared() {
        if (poll != null) {
            if (poll.getPollState() != null) {
                return poll.getPollState().equals(PollState.PREPARED);
            }
        }
        return false;
    }

    /**
     * Checks whether the poll is in state Started
     * 
     * @return true if the poll is in state STARTED and false otherwise
     */
    public boolean isPollStarted() {
        if (poll != null) {
            if (poll.getPollState() != null) {
                return poll.getPollState().equals(PollState.STARTED);
            }
        }
        return false;
    }

    /**
     * Checks whether the poll is in state Voting
     * 
     * @return true if the poll is in state VOTING and false otherwise
     */
    public boolean isPollVoting() {
        if (poll != null) {
            if (poll.getPollState() != null) {
                return poll.getPollState().equals(PollState.VOTING);
            }
        }
        return false;
    }

    /**
     * Checks whether the poll is in state Finished
     * 
     * @return true if the poll is in state FINISHED and false otherwise
     */
    public boolean isPollFinished() {
        if (poll != null) {
            if (poll.getPollState() != null) {
                return poll.getPollState().equals(PollState.FINISHED);
            }
        }
        return false;
    }

    /**
     * Checks whether the poll is in state Published
     * 
     * @return true if the poll is in state Published and false otherwise
     */
    public boolean isPollPublished() {
        if (poll != null) {
            if (poll.getPollState() != null) {
                return poll.getPollState().equals(PollState.PUBLISHED);
            }
        }
        return false;
    }

    /**
     * Checks if the poll is valid and meets the requirements for starting and if yes starts the poll. 
     */
    public void startPoll() {
        if (isPollValid()) {
            try {
                uuid = ol.startPoll(poll);
                if (uuid != null) {
                    success = true;
                    successMessage = Messages.getMessage("startPollSuccessMessage");
                    failure = false;
                    // force reload since the JPA version might have changed
                    poll = null;
                }
            } catch (EJBException e) {
                success = false;
                failure = true;
                Throwable t = e;
                while (t.getCause() != null) {
                    t = t.getCause();
                }
                failureMessage = t.getMessage();
            }
        } else {
            success = false;
            failure = true;
        }
    }

    /**
     * Checks if the participant list is valid by verifying if it is empty and its size
     * 
     * @param list of participant
     * @return true if the participant list is valid and false otherwise
     */
    public boolean isParticpantListVaild(Set<String> list) {
        if (list != null && !list.isEmpty() && list.size() > 2) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a question is valid by verifying if it has items
     * 
     * @param items list of items 
     * @return true if the question is valid and false otherwise
     */
    public boolean isQuestionValid(List<Item> items) {
        if (items != null && !items.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a list of questions is valid by verifying if it is empty
     * 
     * @param questions
     * @return true if the question is valid and false otherwise
     */
    private boolean isQuestionListValid(List<Question> questions) {
        if (questions != null && !questions.isEmpty()) {
            // return !questions.stream().anyMatch((question) -> (!isQuestionValid(question.getItems()) ));
            return true;
        }
        return false;
    }

    /**
     * Checks if the poll is valid by checking the title, the start and end date
     * the participants list and the question list
     * 
     * @return true if the poll is valid and false otherwise
     */
    public boolean isPollValid() {
        if (!isTitleAvailable()) {
            failureMessage = Messages.getMessage("titleNotAvailableMessage");
            return false;
        }
        if (!(poll.getEndDate().after(poll.getStartDate()) && poll.getEndDate().after(new Date()))) {
            failureMessage = Messages.getMessage("pollEditEndDateBeforeStartDateMessage");
            return false;

        }
        if (isParticpantListVaild(emailList)) {
            if (isQuestionListValid(poll.getQuestions())) {
                return true;
            } else {
                failureMessage = Messages.getMessage("questionListInvalid");
                return false;
            }

        } else {
            failureMessage = Messages.getMessage("participantListInvalidMessage");
            return false;
        }
    }

    /**
     * Deletes the Poll
     */
    public void deletePoll() {
        ol.deletePoll(uuid);
    }

    private Set<String> getEmailsAsStringSet() {
        Set<String> emailsAsStrings = new HashSet<>();
        List<Participant> participants = poll.getParticipants();
        participants.forEach((participant) -> {
            emailsAsStrings.add(participant.getEmail());
        });

        return emailsAsStrings;
    }

    /**
     * Get temporary email used for the input when adding a new participant to the list
     * @return mail to be possibily added
     */
    public String getTempMail() {
        return tempMail;
    }

    /**
     * Set temporary email used for the input when adding a new participant to the list
     * @param tempMail mail to be possibily added
     */
    public void setTempMail(String tempMail) {
        this.tempMail = tempMail.toLowerCase();
    }

    /**
     * Get UI Component used for displaying errors when adding participants
     * @return add participant ui component
     */
    public UIComponent getAddParticipantComponent() {
        return addParticipantComponent;
    }

    /**
     * Set UI Component used for displaying errors when adding participants
     * @param addParticipantComponent add participant ui component
     */
    public void setAddParticipantComponent(UIComponent addParticipantComponent) {
        this.addParticipantComponent = addParticipantComponent;
    }

    /**
     * Get list of participant lists displayed in importing modal
     * @return list of participant lists
     */
    public List<ParticipantList> getParticipantLists() {
        return participantLists;
    }

    /**
     * Set list of participant lists displayed in importing modal
     * @param participantLists list of participant lists
     */
    public void setParticipantLists(List<ParticipantList> participantLists) {
        this.participantLists = participantLists;
    }

    /**
     * Get a map representing if participant list was marked for importing
     * @return map of participant list and its corresponding boolean value if marked
     */
    public Map<ParticipantList, Boolean> getParticipantListsCheckMap() {
        return participantListsCheckMap;
    }

    /**
     * Obtain the participant lists that has been selected for import
     * 
     * @return participants lists that were marked for import
     */
    public List<ParticipantList> getSelectedParticipantLists() {
        List<ParticipantList> result = new ArrayList<>();
        for (Entry<ParticipantList, Boolean> entry : participantListsCheckMap.entrySet()) {
            if (entry.getValue()) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Import the participants of the selected participant lists. Also checks that no email is entered twice.
     */
    public void importParticipantLists() {

        List<ParticipantList> selectedLists = getSelectedParticipantLists();

        selectedLists.forEach((ParticipantList list) -> {
            list.getParticipants().forEach(participantInList -> {

                if (emailList.add(participantInList.getEmail())) {
                    Participant newParticipant = new Participant();
                    newParticipant.setEmail(participantInList.getEmail());
                    poll.addParticipant(newParticipant);
                }
            });
        });
        //FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add("alert('peek-a-boo');");
    }

    /**
     * Adds a new participant to the poll. Also checks if email is already in the list and does not add it if so
     */
    public void addNewParticipant() {
        if (!tempMail.equals("")) {
            if (emailList.add(tempMail)) {
                Participant participant = new Participant();
                participant.setEmail(tempMail);

                poll.addParticipant(participant);

                tempMail = "";
            } else {
                // Send message that this mail is a duplicate
                FacesContext.getCurrentInstance().addMessage(addParticipantComponent.getClientId(), new FacesMessage(Messages.getMessage("duplicateMailMessage", tempMail)));
                tempMail = "";
            }
        }
    }

    /**
     * Removes a participant from the poll
     * 
     * @param participant participant to be removed
     */
    public void removeParticipant(Participant participant) {
        poll.removeParticipant(participant);
        emailList.remove(participant.getEmail());
    }

    /**
     * Checks wether user creates a new question and the add new question area is shown. Used for correct rendering in view
     * @return true if user wants to create a new question
     */
    public boolean isQuestionAddMode() {
        return questionAddMode;
    }

    /**
     * Set wether a user creates a new question. Used for correct rendering in view
     * @param questionAddMode true if new question panel should be shown
     */
    public void setQuestionAddMode(boolean questionAddMode) {
        this.questionAddMode = questionAddMode;
    }

    /**
     * Get the new question that is about to be added
     * @return new question that is not stored yet
     */
    public Question getNewQuestion() {
        return newQuestion;
    }

    /**
     * Set the new question that is about to be added
     * @param newQuestion new question that is not stored yet
     */
    public void setNewQuestion(Question newQuestion) {
        this.newQuestion = newQuestion;
    }

    /**
     * Get ui component used for displaying messages based on success/failure of adding new question
     * @return ui component
     */
    public UIComponent getStoreQuestionButton() {
        return storeQuestionButton;
    }

    /**
     * Set ui component used for displaying messages based on success/failure of adding new question
     * @param storeQuestionButton  ui component used for adding new questions
     */
    public void setStoreQuestionButton(UIComponent storeQuestionButton) {
        this.storeQuestionButton = storeQuestionButton;
    }

    /**
     * Get temporary item name used for the input when adding a new item to the new question
     * @return temporary item name
     */
    public String getTempItemName() {
        return tempItemName;
    }

    /**
     * Set temporary item name used for the input when adding a new item to the new question
     * @param tempItemName temp short name of item
     */
    public void setTempItemName(String tempItemName) {
        this.tempItemName = tempItemName;
    }

    /**
     * Get temporary item optional description used for the input when adding a new item to the new question
     * @return description of new item that is going to be added to new question
     */
    public String getTempItemDescription() {
        return tempItemDescription;
    }

    /**
     * Set temporary item description used for the input when adding a new item to the new question
     * @param tempItemDescription description of new item that is going to be added to new question
     */
    public void setTempItemDescription(String tempItemDescription) {
        this.tempItemDescription = tempItemDescription;
    }

    /**
     * Get ui component used for displaying messages based on success/failure of adding new items
     * @return ui component for new items
     */
    public UIComponent getAddItemComponent() {
        return addItemComponent;
    }

    /**
     * Set ui component used for displaying messages based on success/failure of adding new items
     * @param addItemComponent ui component for new items
     */
    public void setAddItemComponent(UIComponent addItemComponent) {
        this.addItemComponent = addItemComponent;
    }

    /**
     * Obtains the questions titles of the poll and returns them as a string set. Used for duplicate testing.
     * 
     * @return a set of questions titles as a string set
     */
    private Set<String> getQuestionsAsStringSet() {
        Set<String> questionsAsString = new HashSet<>();

        List<Question> questions = poll.getQuestions();
        questions.forEach((Question question) -> {
            questionsAsString.add(question.getTitle());
        });

        return questionsAsString;
    }

    /**
     * Creates a temporary new question that has a certain type. Still has to be stored when finished with editing
     * For Yes and no question it will already create the items.
     * 
     * @param questionType question type of the new question
     */
    public void addNewQuestion(QuestionType questionType) {
        if (questionType == QuestionType.MULTIPLE_CHOICE) {
            newQuestion = new MultipleChoiceQuestion();
            ((MultipleChoiceQuestion) newQuestion).setMinChoices(1);
            ((MultipleChoiceQuestion) newQuestion).setMaxChoices(2);
        } else {
            newQuestion = new Question();
        }
        newQuestion.setQuestionType(questionType);

        if (questionType == QuestionType.YES_NO) {
            Item yesItem = new Item();
            yesItem.setShortName("Yes");
            Item noItem = new Item();
            noItem.setShortName("No");

            newQuestion.addItem(yesItem);
            newQuestion.addItem(noItem);
        }

        questionAddMode = true;
    }

    /**
     * Checks if it is a valid question item, and if so, 
     * it adds it to the question that is being created
     */
    public void addNewItem() {

        if (!tempItemName.equals("")) {

            Set<String> itemListAsString = new HashSet<>();
            List<Item> items = newQuestion.getItems();
            items.forEach((item) -> {
                itemListAsString.add(item.getShortName());
            });

            if (itemListAsString.add(tempItemName)) {
                Item newItem = new Item();
                newItem.setShortName(tempItemName);

                if (!tempItemDescription.equals("") && tempItemDescription != null) {
                    newItem.setDescription(tempItemDescription);
                }

                newQuestion.addItem(newItem);
            } else {
                FacesContext.getCurrentInstance().addMessage(addItemComponent.getClientId(), new FacesMessage(Messages.getMessage("duplicateItemMessage", tempItemName)));
            }

            tempItemName = "";
            tempItemDescription = "";

        }
    }

    /**
     * Checks if it is a valid question and if so it adds it to the poll. This question will stored permanently if poll is stored.
     */
    public void storeNewQuestion() {

        if (!newQuestion.getTitle().equals("")) {
            if (questionList.add(newQuestion.getTitle())) {
                if (newQuestion.getItems().size() > 1 || newQuestion.getQuestionType() == QuestionType.YES_NO) {

                    if (newQuestion.getQuestionType() == QuestionType.MULTIPLE_CHOICE && ((MultipleChoiceQuestion) newQuestion).getMinChoices() > ((MultipleChoiceQuestion) newQuestion).getMaxChoices()) {
                        FacesContext.getCurrentInstance().addMessage(storeQuestionButton.getClientId(), new FacesMessage(FacesMessage.SEVERITY_WARN, Messages.getMessage("questionEditMinGreaterThanMaxMessage"), null));
                    } else if (newQuestion.getQuestionType() == QuestionType.MULTIPLE_CHOICE && ((MultipleChoiceQuestion) newQuestion).getMinChoices() > newQuestion.getItems().size()) {
                        FacesContext.getCurrentInstance().addMessage(storeQuestionButton.getClientId(), new FacesMessage(FacesMessage.SEVERITY_WARN, Messages.getMessage("questionEditNotEnoughItemsMessage"), null));
                    } else {
                        poll.addQuestion(newQuestion);
                        questionAddMode = false;
                    }

                } else {
                    FacesContext.getCurrentInstance().addMessage(storeQuestionButton.getClientId(), new FacesMessage(FacesMessage.SEVERITY_WARN, Messages.getMessage("questionEditNoResponeOptionsMessage"), null));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(storeQuestionButton.getClientId(), new FacesMessage(FacesMessage.SEVERITY_WARN, Messages.getMessage("questionDuplicateTitleMessage", newQuestion.getTitle()), null));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(storeQuestionButton.getClientId(), new FacesMessage(FacesMessage.SEVERITY_WARN, Messages.getMessage("questionEditNoTitleMessage"), null));
        }

    }

    /**
     * Removes a question from the poll
     * @param question question to be removed
     */
    public void removeQuestion(Question question) {
        poll.removeQuestion(question);
        questionList.remove(question.getTitle());
    }
    
    /**
     * Checks if the poll has at least 3 submitted votes.
     * @return true if at least 3 votes
     */
    public boolean hasEnoughVotes() {
        return poll.getVoteCounter() >= 3;
    }

    /**
     * Checks if the title of a poll is available 
     * 
     * @return true if the title of the polls has not been taken by a 
     * previously created poll
     */
    public boolean isTitleAvailable() {
        if (!poll.isNew()) {
            if (oldTitle.equals(poll.getTitle())) {
                return true;
            }
        }
        if (!ol.getListOfPollTitle().contains(poll.getTitle())) {
            FacesContext.getCurrentInstance().addMessage(titleComponent.getClientId(), new FacesMessage(FacesMessage.SEVERITY_INFO, Messages.getMessage("titleAvailableMessage"), null));
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(titleComponent.getClientId(), new FacesMessage(FacesMessage.SEVERITY_WARN, Messages.getMessage("titleNotAvailableMessage"), null));
            return false;
        }
    }

    /**
     * If the tracking of participants is enabled it sends reminders to participants who haven't voted yet
     */
    public void sendReminders() {
        if (poll.getTrackingEnabled()) {
            try {
                uuid = ol.sendReminders(poll.getUuid());
                if (uuid != null) {
                    success = true;
                    successMessage = Messages.getMessage("sendReminderSuccessMessage");
                    failure = false;
                    // force reload since the JPA version might have changed
                    poll = null;
                }
            } catch (EJBException e) {
                success = false;
                failure = true;
                Throwable t = e;
                while (t.getCause() != null) {
                    t = t.getCause();
                }
                failureMessage = t.getMessage();
            }
        }
    }
    
    /**
     * Get ui component used for displaying messages based on availability of the title
     * @return ui component for poll title
     */
    public UIComponent getTitleComponent() {
        return titleComponent;
    }

    /**
     * Set ui component used for displaying messages based on availability of the title
     * @param titleComponent ui component for poll title
     */
    public void setTitleComponent(UIComponent titleComponent) {
        this.titleComponent = titleComponent;
    }
}

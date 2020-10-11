/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

/**
 * The Data Transfer Object representing an item of a question
 * 
 * @author Team Echo
 */
public class Item extends AbstractDTO {

    private String shortName;
    private String description;
    private Integer votes;
    private boolean participantVote;

    /**
     * Constructor of Item
     */
    public Item() {
    }

    /**
     * Constructor of item using all passed parameter
     * 
     * @param uuid UUID of item
     * @param jpaVersion jpaVersion of item
     * @param shortName short name of item
     * @param description optional desciption of item
     * @param votes given votes of an item
     */
    public Item(String uuid, int jpaVersion, String shortName, String description, Integer votes) {
        super(uuid, jpaVersion);
        this.shortName = shortName;
        this.description = description;
        this.votes = votes;
    }

    /**
     * Get the short name of the item
     * 
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Set the short name of the item
     * 
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the description of the item
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the item
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the number of votes of an item
     * 
     * @return no of. votes
     */
    public Integer getVotes() {
        return votes;
    }

    /**
     * Sets the number of votes
     * 
     * @param votes number of votes
     */
    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    /**
     * Checks wether a participant voted for this item. Is not stored in the database and only used as temp value for submitting votes.
     * 
     * @return true if participant marked this item, else false
     */
    public boolean isParticipantVote() {
        return participantVote;
    }

    /**
     * Sets wether a participant voted for this item. It is not stored in the database and only used as temp value for submitting votes.
     * 
     * @param participantVote boolean wether participant voted for this item
     */
    public void setParticipantVote(boolean participantVote) {
        this.participantVote = participantVote;
    }

}

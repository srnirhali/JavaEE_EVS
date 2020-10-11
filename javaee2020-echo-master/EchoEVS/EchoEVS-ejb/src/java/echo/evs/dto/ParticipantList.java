
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * The Data Transfer Object representing a participant list
 * 
 * @author Team Echo
 */
public class ParticipantList extends AbstractDTO {

    private String name;

    private List<ParticipantInList> participants;

    /**
     * Constructor of participant list
     */
    public ParticipantList() {
        participants = new ArrayList<>();
    }
    
    /**
     * Constructor of particpant list using all passed parameter
     * 
     * @param uuid uuid of participant list
     * @param jpaVersion jpaVersion
     * @param name name of participant list
     * @param participants list of ParticipantInList DTOs that should belong to this participant list
     */
    public ParticipantList(String uuid, int jpaVersion, String name, List<ParticipantInList> participants) {
        super(uuid, jpaVersion);
        this.name = name;
        this.participants = participants;
    }

    /**
     * Gets the name of the participant list
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the participant list
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets participants belonging to this list
     * 
     * @return list of participantInList DTOs
     */
    public List<ParticipantInList> getParticipants() {
        return participants;
    }

    /**
     * Sets participants of this list
     * 
     * @param participants list of participantsInList dtos to be set
     */
    public void setParticipants(List<ParticipantInList> participants) {
        this.participants = participants;
    }

    /**
     * Removes a specific participant from its list
     * 
     * @param participant participantInList to be removed
     * @return true if removed, false if not (or not found)
     */
    public boolean removeParticipant(ParticipantInList participant) {
        if (participant.getUuid() != null) {
            return participants.remove(participant);
        } else {
            // if UUID not set we have to compare by Title-String, as otherwise wrong questions get removed
            String participantToDelete = participant.getEmail();
            for (int i = 0; i < participants.size(); i++) {
                if (participants.get(i).getEmail().equals(participantToDelete)) {
                    participants.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Adds a participant to the list
     * 
     * @param participant the ParticipantInList DTO to be added to the list
     */
    public void addParticipant(ParticipantInList participant) {
        participants.add(participant);
    }
}

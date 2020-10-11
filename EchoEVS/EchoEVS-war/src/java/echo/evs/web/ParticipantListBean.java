
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.web;

import echo.evs.dto.ParticipantInList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import echo.evs.dto.ParticipantList;
import echo.evs.logic.OrganizerLogic;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the actions of the participants list overview page
 * 
 * @author Team Echo
 */
@RequestScoped
@Named
public class ParticipantListBean implements Serializable {

    private static final long serialVersionUID = 1390635121463900L;

    @EJB
    private OrganizerLogic ol;

    private List<ParticipantList> participantLists;

    /**
     * Obtain all participants list belonging to current organizer
     * 
     * @return the participants lists
     */
    public List<ParticipantList> getParticipantLists() {
        if (participantLists == null) {
            participantLists = ol.getParticipantLists();
        }
        return participantLists;
    }

    /**
     * Delete an specific participant list
     * 
     * @param participantList the list of participants we want to delete
     */
    public void deleteParticipantList(ParticipantList participantList) {
        ol.deleteParticipantList(participantList.getUuid());
        participantLists = ol.getParticipantLists();
    }

    /**
     * Create test data for participant list
     */
    public void testData() {

        List<ParticipantInList> participants = new ArrayList<>();
        ParticipantInList p = new ParticipantInList();
        p.setEmail("test1@gmail.com");
        participants.add(p);
        p = new ParticipantInList();
        p.setEmail("test2@gmail.com");
        participants.add(p);

        ol.createParticipantList("New List", participants);
        participantLists = null;
    }
}

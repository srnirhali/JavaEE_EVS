/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.web;

import echo.evs.dto.Poll;
import echo.evs.logic.AdminLogic;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Represents the actions that the Admin is able to execute
 * 
 * @author Team Echo
 */
@RequestScoped
@Named
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1390608306656400L;

    @EJB
    private AdminLogic al;

    private List<Poll> pollList;

    /**
     * Obtain a list of all polls with admin rights
     * 
     * @return a list 
     */
    public List<Poll> getPollList() {
        if (pollList == null) {
            pollList = al.getPollList();
        }
        return pollList;
    }

    /**
     * Delete a particular poll with admin rights
     * 
     * @param poll  the poll we want to delete
     */
    public void deletePoll(Poll poll) {
        al.deletePoll(poll.getUuid());
        pollList = al.getPollList();
    }
}

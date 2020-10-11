/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.logic;

import echo.evs.dto.Poll;
import java.util.List;
import javax.ejb.Remote;

/**
 * Defines the interfaces for the Administrator actions
 * @author Team Echo
 */
@Remote
public interface AdminLogic {

    public static final String ADMIN_ROLE = "ADMIN";

    /**
     * Gets the list of all polls with admin rights (meaning of all organizers)
     * 
     * @return list of polls
     */
    public List<Poll> getPollList();

    /**
     * Deletes a poll identified with uuid with admin rights (meaning there is no organizer check)
     * 
     * @param uuid uuid identifier
     * @return true if deleted successfully
     */
    public boolean deletePoll(String uuid);
}

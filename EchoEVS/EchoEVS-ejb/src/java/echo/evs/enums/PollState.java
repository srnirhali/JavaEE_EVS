/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.enums;

/**
 * Enum values of the poll state
 * 
 * @author Team Echo
 */
public enum PollState {
    PREPARED, STARTED, VOTING, FINISHED, PUBLISHED;

    public String getName() {
        return this.name();
    }
}

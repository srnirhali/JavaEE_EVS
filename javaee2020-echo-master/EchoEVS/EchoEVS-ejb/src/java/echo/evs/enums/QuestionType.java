/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.enums;

/**
 * Enmus values of the question type
 * 
 * @author Team Echo
 */
public enum QuestionType {
    SINGLE_CHOICE, MULTIPLE_CHOICE, YES_NO;

    public String getName() {
        return this.name();
    }
}

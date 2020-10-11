/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.logic.impl;

import echo.evs.dto.Participant;
import echo.evs.dto.Poll;
import echo.evs.entities.ParticipantEntity;
import echo.evs.entities.PollEntity;
import echo.evs.entities.TokenEntity;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Represent the logic for email sending
 * 
 * @author Team Echo
 */
@Stateless
@LocalBean
public class MailService {

    public static final String SERVER_URL = "https://localhost:8181/EchoEVS";
    
    public String dateFormatPattern = "yyyy/MM/dd HH:mm:ss";

    public String subTokenMessage = "EchoEVS Voting started for poll {0}";

    public String subPollDeleteMessage = "EchoEVS poll {0} deleted ";

    public String subPollPublishMessage = "EchoEVS poll {0} result published";

    public String subDateChangedMessage = "EchoEVS poll {0} Date changed";

    public String subPollReminderMessage = "EchoEVS Reminder: Voting for poll {0}";

    public String tokenMessage = "<p>Hello {0},</p> <p>You have been added to following poll as participant.</p> "
            + "<p>Poll title : {1}<br/>"
            + "Start date & time : {2}<br/> "
            + "End date & time : {3} <br/>"
            + "Description : {4}</p>"
            + "<p>You can give your vote by visiting our web site and enter following token.</p>"
            + "<p>Token : {5} </p>"
            + "<p>or you can click on following link to vote.</p>"
            + "<p><a href=" + SERVER_URL + "/pages/participant/voting.xhtml?token={5}>" + SERVER_URL + "/pages/participant/voting.xhtml?token={5}</a></p>";

    public String pollDeleteMessage = "<p>Hello {0},</p> <p>The following poll has been deleted.</p>"
            + "<p>Poll title : {1} <br/>"
            + "Start date & time : {2} <br/>"
            + "End date & time : {3} <br/>"
            + "Description : {4}</p>";
    
    public String pollDeleteByAdminMessage = "<p>Hello {0},</p> <p>The following poll has been deleted by an admin.</p>"
            + "<p>Poll title : {1} <br/>"
            + "Start date & time : {2} <br/>"
            + "End date & time : {3} <br/>"
            + "Description : {4}</p>";

    public String pollPublishMessage = "<p>Hello {0},</p> <p>The following poll has been published.</p>"
            + "<p>Poll title : {1} <br/>"
            + "Start date & time : {2} <br/>"
            + "End date & time : {3} <br/>"
            + "Description : {4} </p>"
            + "<p>You can view result by clicking on following link to vote.</p>"
            + "<p><a href=" + SERVER_URL + "/pages/participant/voting_result.xhtml?token={5}>" + SERVER_URL + "/pages/participant/voting_result.xhtml?token={5}</a></p>";

    public String dateChangedMessage = "<p>Hello {0},</p> <p>The poll dates has been changed.</p>"
            + "<p>Poll title : {1} <br/>"
            + "Start date & time : {2} <br/>"
            + "End date & time : {3} <br/>"
            + "Description : {4}</p>"
            + "<p>You can give your vote by visiting our web site and enter following token.</p>"
            + "<p>token : {5} </p>"
            + "<p>or you can click on following link to vote.</p>"
            + "<p><a href=" + SERVER_URL + "/pages/participant/voting.xhtml?token={5}>" + SERVER_URL + "/pages/participant/voting.xhtml?token={5}</a></p>";

    public String reminderMessage = "<p>Hello {0},</p> <p>This is a reminder message as you have not voted for following poll yet.</p>"
            + "<p>Poll title : {1} <br/>"
            + "Start date & time : {2} <br/>"
            + "End date & time : {3} <br/>"
            + "Description : {4} </p>"
            + "<p>You can give your vote by visiting our web site and enter following token.</p>"
            + "<p>token : {5} </p>"
            + "<p>or you can click on following link to vote.</p>"
            + "<p><a href=" + SERVER_URL + "/pages/participant/voting.xhtml?token={5}>" + SERVER_URL + "/pages/participant/voting.xhtml?token={5}</a></p>";

    @Resource(lookup = "mail/echoevs")
    private Session mailSession;

    /**
     * Send email to the participants with the token to vote
     * 
     * @param pollEntity poll entity with all needed data
     */
    public void sendTokenMail(PollEntity pollEntity) {
        try {
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
            Map<ParticipantEntity, String> tokenList = mapParticipantToToken(pollEntity);

            for (ParticipantEntity participant : pollEntity.getParticipants()) {
                Message msg = new MimeMessage(mailSession);
                msg.setSubject(MessageFormat.format(subTokenMessage, pollEntity.getTitle()));
                msg.setSentDate(new Date());
                msg.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(
                                participant.getEmail(), false));

                msg.setContent(MessageFormat.format(tokenMessage,
                        participant.getEmail(),
                        pollEntity.getTitle(),
                        pollEntity.getStartDate().format(formatter),
                        pollEntity.getEndDate().format(formatter),
                        pollEntity.getDescription(),
                        tokenList.get(participant)), "text/html; charset=utf-8");
                Transport.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send email to the participants informing about the deletion of a poll
     * 
     * @param poll poll with all needed data
     */
    public void sendPollDeleteMail(Poll poll) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
            for (Participant participant : poll.getParticipants()) {
                Message msg = new MimeMessage(mailSession);
                msg.setSubject(MessageFormat.format(subPollDeleteMessage, poll.getTitle()));
                msg.setSentDate(new Date());
                msg.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(
                                participant.getEmail(), false));
                msg.setContent(MessageFormat.format(pollDeleteMessage,
                        participant.getEmail(),
                        poll.getTitle(),
                        dateFormat.format(poll.getStartDate()),
                        dateFormat.format(poll.getEndDate()),
                        poll.getDescription()), "text/html; charset=utf-8");
                Transport.send(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Send email to the participants informing about the deletion of a poll
     * 
     * @param poll poll with all needed data
     */
    public void sendPollDeleteByAdminMail(Poll poll) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
            List<Participant> receivers = poll.getParticipants();
            Participant organizer = new Participant();
            organizer.setEmail(poll.getOrganizer().getEmail());
            receivers.add(organizer);
            for (Participant participant : receivers) {
                Message msg = new MimeMessage(mailSession);
                msg.setSubject(MessageFormat.format(subPollDeleteMessage, poll.getTitle()));
                msg.setSentDate(new Date());
                msg.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(
                                participant.getEmail(), false));
                msg.setContent(MessageFormat.format(pollDeleteByAdminMessage,
                        participant.getEmail(),
                        poll.getTitle(),
                        dateFormat.format(poll.getStartDate()),
                        dateFormat.format(poll.getEndDate()),
                        poll.getDescription()), "text/html; charset=utf-8");
                Transport.send(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send reminder email to the participants who have not submit their vote
     * 
     * @param tokens list of tokens belonging to poll
     * @param poll poll the tokens belong to
     */
    public void sendReminder(List<TokenEntity> tokens, PollEntity poll) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
            for (TokenEntity token : tokens) {
                Message msg = new MimeMessage(mailSession);
                msg.setSubject(MessageFormat.format(subPollReminderMessage, poll.getTitle()));
                msg.setSentDate(new Date());
                msg.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(
                                token.getParticipant().getEmail(), false));
                msg.setContent(MessageFormat.format(reminderMessage,
                        token.getParticipant().getEmail(),
                        poll.getTitle(),
                        poll.getStartDate().format(formatter),
                        poll.getEndDate().format(formatter),
                        poll.getDescription(),
                        token.getToken()), "text/html; charset=utf-8");
                Transport.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send email to participants who voted on a poll to inform about 
     * publishing of poll results
     * @param poll poll with all needed data
     */
    public void sendResultsMail(Poll poll) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
            for (Participant participant : poll.getParticipants()) {
                Message msg = new MimeMessage(mailSession);
                msg.setSubject(MessageFormat.format(subPollPublishMessage, poll.getTitle()));
                msg.setSentDate(new Date());
                msg.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(
                                participant.getEmail(), false));
                msg.setContent(MessageFormat.format(pollPublishMessage,
                        participant.getEmail(),
                        poll.getTitle(),
                        dateFormat.format(poll.getStartDate()),
                        dateFormat.format(poll.getEndDate()),
                        poll.getDescription(),
                        poll.getResultToken()), "text/html; charset=utf-8");
                Transport.send(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Assign a token to a participant
     * @param pollEntity pollEntity for which a token participant mapping should be generated
     * @return mapping between a participant and token
     */
    public Map<ParticipantEntity, String> mapParticipantToToken(PollEntity pollEntity) {
        Map<ParticipantEntity, String> participantTokenList = new HashMap<>();

        if (pollEntity.getTrackingEnabled()) {
            List<TokenEntity> tokens = pollEntity.getTokens();
            for (TokenEntity token : tokens) {
                participantTokenList.put(token.getParticipant(), token.getToken());
            }
        } else {
            List<TokenEntity> tokens = pollEntity.getTokens();
            for (ParticipantEntity participant : pollEntity.getParticipants()) {
                Random rand = new Random();
                TokenEntity randomToken = tokens.get(rand.nextInt(tokens.size()));
                participantTokenList.put(participant, randomToken.getToken());
                tokens.remove(randomToken);
            }
        }

        return participantTokenList;
    }
}

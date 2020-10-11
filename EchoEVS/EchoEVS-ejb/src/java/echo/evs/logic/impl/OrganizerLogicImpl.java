
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.logic.impl;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import echo.evs.dao.OrganizerAccess;
import echo.evs.dao.ParticipantAccess;
import echo.evs.dao.ParticipantInListAccess;
import echo.evs.dao.ParticipantListAccess;
import echo.evs.dao.PollAccess;
import echo.evs.dao.QuestionAccess;
import echo.evs.dao.TokenAccess;
import echo.evs.dto.Item;
import echo.evs.dto.MultipleChoiceQuestion;
import echo.evs.dto.Organizer;
import echo.evs.dto.Participant;
import echo.evs.dto.ParticipantInList;
import echo.evs.dto.ParticipantList;
import echo.evs.dto.Poll;
import echo.evs.dto.Question;
import echo.evs.dto.Token;
import echo.evs.entities.ItemEntity;
import echo.evs.entities.MultipleChoiceQuestionEntity;
import echo.evs.entities.OrganizerEntity;
import echo.evs.entities.ParticipantEntity;
import echo.evs.entities.ParticipantInListEntity;
import echo.evs.entities.ParticipantListEntity;
import echo.evs.entities.PollEntity;
import echo.evs.enums.PollState;
import echo.evs.entities.QuestionEntity;
import echo.evs.entities.TokenEntity;
import echo.evs.enums.QuestionType;
import echo.evs.logic.OrganizerLogic;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Represents the actions executed by an Organizer
 * 
 * @author Team Echo
 */
@Stateless
public class OrganizerLogicImpl implements OrganizerLogic {

    @EJB
    private OrganizerAccess organizerAccess;

    @EJB
    private TokenAccess tokenAccess;

    @EJB
    private PollAccess pollAccess;

    @EJB
    private QuestionAccess questionAccess;

    @EJB
    private ParticipantAccess participantAccess;

    @EJB
    private ParticipantInListAccess participantInListAccess;

    @EJB
    private ParticipantListAccess participantListAccess;

    @EJB
    private MailService mailService;

    @Resource
    private EJBContext ejbContext;

    private static final Logger LOG = Logger.getLogger(OrganizerLogicImpl.class.getName());

    private OrganizerEntity organizer;

    @AroundInvoke
    private Object getCaller(InvocationContext ctx) throws Exception {
        Principal p = ejbContext.getCallerPrincipal();
        if (p != null) {
            organizer = organizerAccess.getUser(p.getName());
        }
        return ctx.proceed();
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public Organizer getCurrentUser() {
        return createDTO(organizer);
    }

    @Override
    public String updateOrganizer(Organizer organizer) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /////////////////////// POLLS //////////////////////////
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public Poll createPoll(String title, LocalDateTime startDate, LocalDateTime endDate, String description, Boolean trackingEnabled, List<Participant> participants, List<Question> questions, PollState pollState) {
        return createDTO(pollAccess.createPoll(organizer, title, startDate, endDate, description, null, 0, trackingEnabled, participants, questions, pollState));
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public Poll getPoll(String uuid) {
        PollEntity pe = pollAccess.getPollByUuid(organizer, uuid);

        return pe == null ? null : createDTO(pe);
    }

  
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public String storePoll(Poll poll) {
        if (poll.isNew()) {
            return pollAccess.createPoll(organizer, poll.getTitle(), convertToLocalDateTimeViaSqlTimestamp(poll.getStartDate()), convertToLocalDateTimeViaSqlTimestamp(poll.getEndDate()), poll.getDescription(), poll.getResultToken(), poll.getVoteCounter(), poll.getTrackingEnabled(), poll.getParticipants(), poll.getQuestions(), PollState.PREPARED).getUuid();
        }
        PollEntity pe = pollAccess.getPollByUuid(organizer, poll.getUuid());
        if (pe == null) {
            return null;
        }

        if (pe.getPollState().equals(PollState.PREPARED)) {
            pe.setJpaVersion(poll.getJpaVersion());
            pe.setTitle(poll.getTitle());
            pe.setDescription(poll.getDescription());
            pe.setEndDate(convertToLocalDateTimeViaSqlTimestamp(poll.getEndDate()));
            pe.setStartDate(convertToLocalDateTimeViaSqlTimestamp(poll.getStartDate()));
            pe.setTrackingEnabled(poll.getTrackingEnabled());
            pe.setParticipants(participantAccess.createParticipant(poll.getParticipants(), pe));
            pe.setQuestions(questionAccess.createQuestion(poll.getQuestions(), pe));
            organizer.getPolls().add(pe);
            pe.setOrganizer(organizer);
        } else {
            pe.setEndDate(convertToLocalDateTimeViaSqlTimestamp(poll.getEndDate()));
            pe.setStartDate(convertToLocalDateTimeViaSqlTimestamp(poll.getStartDate()));
            if (!pe.getPollState().equals(PollState.VOTING) && pe.getStartDate().isBefore(LocalDateTime.now())) {
                pollAccess.createEndEvent(pe);
                pe.setPollState(PollState.VOTING);
            }

            if (pe.getPollState().equals(PollState.STARTED)) {
                pollAccess.dropStartEvent(poll.getUuid());
                pollAccess.dropEndEvent(poll.getUuid());
                pollAccess.createStartEvent(pe);
                pollAccess.createEndEvent(pe);
            }
            if (pe.getPollState().equals(PollState.VOTING)) {
                pollAccess.dropEndEvent(poll.getUuid());
                pollAccess.createEndEvent(pe);
            }
        }

        return pe.getUuid();
    }

    
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public boolean deletePoll(String uuid) {
        Poll poll = createDTO(pollAccess.getPollByUuid(uuid));
        if (pollAccess.deletePoll(organizer, uuid)) {
            if (poll.getPollState() != PollState.PREPARED && poll.getPollState() != PollState.FINISHED) {
                Thread newThread = new Thread(() -> {
                    mailService.sendPollDeleteMail(poll);
                });
                newThread.start();
            }
            return true;
        }
        return false;
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public List<Poll> getPollList() {
        return pollAccess.getPollList(organizer)
                .stream()
                .map(this::createDTO)
                .collect(Collectors.toList());
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public List<String> getListOfPollTitle() {
        return pollAccess.getListOfPollTitle();

    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public List<Question> getQuestionList(List<QuestionEntity> questionEntity) {
        return questionEntity.stream().map(this::createDTO).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public List<Participant> getParticipants(List<ParticipantEntity> participantEntity) {
        return participantEntity.stream().map(this::createDTO).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public List<Item> getItemList(List<ItemEntity> itemEntity) {
        return itemEntity.stream().map(this::createDTO).collect(Collectors.toList());
    }

  
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public String startPoll(Poll poll) {
        PollEntity pe = pollAccess.getPollByUuid(organizer, poll.getUuid());

        if (pe == null) {
            return null;
        }

        pe.setJpaVersion(poll.getJpaVersion());
        pe.setTitle(poll.getTitle());
        pe.setStartDate(convertToLocalDateTimeViaSqlTimestamp(poll.getStartDate()));
        pe.setEndDate(convertToLocalDateTimeViaSqlTimestamp(poll.getEndDate()));
        pe.setDescription(poll.getDescription());
        pe.setTrackingEnabled(poll.getTrackingEnabled());
        pe.setParticipants(participantAccess.createParticipant(poll.getParticipants(), pe));
        pe.setQuestions(questionAccess.createQuestion(poll.getQuestions(), pe));
        organizer.getPolls().add(pe);
        pe.setOrganizer(organizer);

        if (pe.getStartDate().isBefore(LocalDateTime.now())) {
            pe.setPollState(PollState.VOTING);
            pollAccess.createEndEvent(pe);
        } else {
            pe.setPollState(PollState.STARTED);
            pollAccess.createStartEvent(pe);
            pollAccess.createEndEvent(pe);
        }

        createToken(pe);

        Thread newThread = new Thread(() -> {
            mailService.sendTokenMail(pe);
        });
        newThread.start();

        return pe.getUuid();
    }

   
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public String publishPoll(String uuid) {
        PollEntity pe = pollAccess.getPollByUuid(organizer, uuid);

        if (pe.getVoteCounter() >= 3 && pe.getPollState() == PollState.FINISHED) {
            pe.setPollState(PollState.PUBLISHED);
            String resultToken = generateToken();
            pe.setResultToken(resultToken);

            Poll poll = createDTO(pe);

            Thread newThread = new Thread(() -> {
                mailService.sendResultsMail(poll);
            });
            newThread.start();
        }

        return pe.getUuid();
    }

    
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public String sendReminders(String uuid) {
        PollEntity pe = pollAccess.getPollByUuid(organizer, uuid);

        if (pe.getTrackingEnabled()) {
            List<TokenEntity> tokens = pe.getTokens();

            Thread newThread = new Thread(() -> {
                mailService.sendReminder(tokens, pe);
            });
            newThread.start();

        }

        return pe.getUuid();
    }

   
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public void createToken(PollEntity pollEntity) {
        List<ParticipantEntity> entity = pollEntity.getParticipants();
        Boolean trackingEnabled = pollEntity.getTrackingEnabled();
        for (ParticipantEntity participantEntity : entity) {
            try {
                String token = generateToken();
                pollEntity.getTokens().add(tokenAccess.createToken(participantEntity, token, trackingEnabled));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////// PARTICIPANT LISTS ///////////////////////
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public String storeParticipantList(ParticipantList participantList) {
        if (participantList.isNew()) {
            ParticipantListEntity pe = participantListAccess.createParticipantList(organizer, participantList.getName());
            List<ParticipantInListEntity> p = participantInListAccess.createParticipantInList(pe, participantList.getParticipants());
            pe.setParticipants(p);
            return pe.getUuid();
        }
        ParticipantListEntity pe = participantListAccess.getParticipantListByUuid(organizer, participantList.getUuid());
        if (pe == null) {
            return null;
        }
        List<ParticipantInListEntity> p = participantInListAccess.createParticipantInList(pe, participantList.getParticipants());

        pe.setJpaVersion(participantList.getJpaVersion());
        pe.setName(participantList.getName());
        pe.setParticipants(p);
        return pe.getUuid();
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public ParticipantList createParticipantList(String Name, List<ParticipantInList> participants) {
        ParticipantListEntity participantListEntity = participantListAccess.createParticipantList(organizer, Name);
        List<ParticipantInListEntity> p = participantInListAccess.createParticipantInList(participantListEntity, participants);
        participantListEntity.setParticipants(p);
        return createDTO(participantListEntity);
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public ParticipantList getParticipantList(String uuid) {
        ParticipantListEntity ple = participantListAccess.getParticipantListByUuid(organizer, uuid);
        return ple == null ? null : createDTO(ple);
    }

    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public List<ParticipantList> getParticipantLists() {
        return participantListAccess.getParticipantLists(organizer)
                .stream()
                .map(this::createDTO)
                .collect(Collectors.toList());
    }

    
    @Override
    @RolesAllowed(ORGANIZER_ROLE)
    public boolean deleteParticipantList(String uuid) {
        return participantListAccess.deleteParticipantList(organizer, uuid);
    }

    ///////////////////// CREATE DTOs //////////////////////////////////
    private Organizer createDTO(OrganizerEntity oe) {
        return new Organizer(oe.getUuid(), oe.getJpaVersion(), oe.getEmail(), oe.getFirstName(), oe.getLastName());
    }

    private Poll createDTO(PollEntity pe) {
        return new Poll(pe.getUuid(), pe.getJpaVersion(), pe.getTitle(), createDTO(pe.getOrganizer()), convertToDateViaSqlTimestamp(pe.getStartDate()), convertToDateViaSqlTimestamp(pe.getEndDate()), pe.getPollState(), pe.getDescription(), pe.getResultToken(), pe.getVoteCounter(), pe.getTrackingEnabled(), getParticipants(pe.getParticipants()), getQuestionList(pe.getQuestions()));
    }

    private Question createDTO(QuestionEntity qe) {
        if (qe.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            return new MultipleChoiceQuestion(qe.getUuid(), qe.getJpaVersion(), qe.getTitle(), getItemList(qe.getItems()), qe.getAbsentions(), qe.getTotalVotes(), ((MultipleChoiceQuestionEntity) qe).getMinChoices(), ((MultipleChoiceQuestionEntity) qe).getMaxChoices());
        } else {
            return new Question(qe.getUuid(), qe.getJpaVersion(), qe.getTitle(), getItemList(qe.getItems()), qe.getQuestionType(), qe.getAbsentions(), qe.getTotalVotes());
        }
    }

    private Participant createDTO(ParticipantEntity pe) {
        return new Participant(pe.getUuid(), pe.getJpaVersion(), pe.getEmail());
    }

    private ParticipantInList createDTO(ParticipantInListEntity p) {
        return new ParticipantInList(p.getUuid(), p.getJpaVersion(), p.getEmail());
    }

    private Item createDTO(ItemEntity ie) {
        return new Item(ie.getUuid(), ie.getJpaVersion(), ie.getShortName(), ie.getDescription(), ie.getVotes());
    }

    private ParticipantList createDTO(ParticipantListEntity ple) {
        List<ParticipantInList> p = ple.getParticipants().stream().map(this::createDTO).collect(Collectors.toList());
        return new ParticipantList(ple.getUuid(), ple.getJpaVersion(), ple.getName(), p);
    }

    private Token createDTO(TokenEntity token) {
        Poll poll = createDTO(token.getPoll());
        if (poll.getTrackingEnabled()) {
            Participant p = createDTO(token.getParticipant());
            return new Token(token.getUuid(), token.getJpaVersion(), token.getToken(), p, poll);
        } else {
            return new Token(token.getUuid(), token.getJpaVersion(), token.getToken(), null, poll);
        }
    }

    //////////////////// HELPERS /////////////////////////////////////

    /**
     * Convert date to timestamp
     * 
     * @param dateToConvert
     * @return
     */
    private LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        return new java.sql.Timestamp(dateToConvert.getTime()).toLocalDateTime();
    }

    /**
     * Convert date to timestamp
     * 
     * @param dateToConvert
     * @return date timestamp
     */
    private Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    /**
     * Generate unique token string
     * 
     * @return token string
     */
    public String generateToken() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;

        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        Random random = new Random();

        String randomString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        long timestampmillis = System.currentTimeMillis();
        String baseToken = randomString + timestampmillis;
        String token = Base64.getEncoder().withoutPadding().encodeToString(baseToken.getBytes());

        return token;
    }
}

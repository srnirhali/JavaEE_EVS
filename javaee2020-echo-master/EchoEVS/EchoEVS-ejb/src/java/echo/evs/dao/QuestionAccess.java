/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dao;

import echo.evs.dto.MultipleChoiceQuestion;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import echo.evs.dto.Question;
import echo.evs.entities.MultipleChoiceQuestionEntity;
import echo.evs.entities.PollEntity;
import echo.evs.entities.QuestionEntity;
import echo.evs.enums.QuestionType;
import javax.ejb.EJB;
import javax.persistence.NoResultException;

/**
 * Represents the actions over a question entity
 * 
 * @author Team Echo
 */
@LocalBean
@Stateless
public class QuestionAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    @EJB
    private ItemAccess itemAccess;

    public List<QuestionEntity> createQuestion(List<Question> questions, PollEntity pollEntity) {
        return questions.stream().map(n -> createQuestion(n, pollEntity)).collect(Collectors.toList());
    }

    /**
     * Creates a question for a particular poll
     * 
     * @param question Question DTO
     * @param entity Poll entity to question should be added to
     * @return question entity object
     */
    public QuestionEntity createQuestion(Question question, PollEntity entity) {
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            MultipleChoiceQuestionEntity qe = new MultipleChoiceQuestionEntity(true);
            qe.setQuestionType(question.getQuestionType());
            qe.setTitle(question.getTitle());
            qe.setPoll(entity);
            qe.setMinChoices(((MultipleChoiceQuestion) question).getMinChoices());
            qe.setMaxChoices(((MultipleChoiceQuestion) question).getMaxChoices());
            qe.setTotalVotes(0);
            qe.setAbsentions(0);
            em.persist(qe);
            qe.setItems(itemAccess.createItem(question.getItems(), qe));
            return qe;
        } else {
            QuestionEntity qe = new QuestionEntity(true);
            qe.setQuestionType(question.getQuestionType());
            qe.setTitle(question.getTitle());
            qe.setPoll(entity);
            qe.setTotalVotes(0);
            qe.setAbsentions(0);
            em.persist(qe);
            qe.setItems(itemAccess.createItem(question.getItems(), qe));
            return qe;
        }
    }

    /**
     * Obtains a question by its identifier
     * 
     * @param uuid question identifier
     * @return question object
     */
    public QuestionEntity getQuestionByUuid(String uuid) {
        try {
            QuestionEntity ce = em.createNamedQuery("getQuestionByUuid", QuestionEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return ce;
        } catch (NoResultException e) {
            return null;
        }
    }

}

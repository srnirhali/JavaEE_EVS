/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dao;

import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import echo.evs.dto.Item;
import echo.evs.entities.ItemEntity;
import echo.evs.entities.QuestionEntity;
import javax.persistence.NoResultException;

/**
 * Represents the actions over a question item
 * 
 * @author Team Echo
 */
@LocalBean
@Stateless
public class ItemAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    public List<ItemEntity> createItem(List<Item> items, QuestionEntity q) {
        return items.stream().map(n -> createItem(n, q)).collect(Collectors.toList());
    }

    /**
     * Creates a new question item
     * 
     * @param item the item dto
     * @param q questionEntity the item should belongig to
     * @return item entity
     */
    public ItemEntity createItem(Item item, QuestionEntity q) {
        ItemEntity ie = new ItemEntity(true);
        ie.setShortName(item.getShortName());

        String itemDescription = item.getDescription();
        if (itemDescription != null && !itemDescription.equals("")) {
            ie.setDescription(itemDescription);
        }

        ie.setVotes(0);
        ie.setQuestion(q);
        em.persist(ie);
        return ie;
    }

    /**
     * Obtains a question item by its identifier
     * 
     * @param uuid item identifier
     * @return item entity
     */
    public ItemEntity getItemByUuid(String uuid) {
        try {
            ItemEntity ce = em.createNamedQuery("getItemByUuid", ItemEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return ce;
        } catch (NoResultException e) {
            return null;
        }
    }

}

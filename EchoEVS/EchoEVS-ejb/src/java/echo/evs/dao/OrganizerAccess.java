/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import echo.evs.entities.OrganizerEntity;

/**
 *
 * @author Team Echo
 */
@Stateless
@LocalBean
public class OrganizerAccess {

    @PersistenceContext(unitName = "Echoevs-ejbPU")
    private EntityManager em;

    /**
     * Obtains a user using the email
     * 
     * @param email String representing the email address
     * @return organizer entity
     */
    public OrganizerEntity getUser(String email) {
        OrganizerEntity oe;
        try {
            oe = em.createNamedQuery("getOrganizerByEmail", OrganizerEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            oe = new OrganizerEntity(true);
            oe.setEmail(email);
            em.persist(oe);
        }
        return oe;
    }
}

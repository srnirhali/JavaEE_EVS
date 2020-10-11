/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo.evs.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Represents the Participant List entity
 * @author Team Echo
 */
@NamedQueries({
    @NamedQuery(
            name = "getParticipantLists",
            query = "SELECT c FROM ParticipantListEntity c"
            + " WHERE c.organizer = :organizer"
    ),
    @NamedQuery(
            name = "getParticipantListByUuid",
            query = "SELECT c FROM ParticipantListEntity c"
            + " WHERE c.organizer = :organizer AND c.uuid = :uuid"
    ),
    @NamedQuery(
            name = "deleteParticipantListByUuid",
            query = "DELETE FROM ParticipantListEntity c WHERE c.uuid = :uuid"
    )
})

@Entity
@Table(uniqueConstraints
        = @UniqueConstraint(columnNames = {"organizer_id", "name"}))
public class ParticipantListEntity extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1389985992993800L;

    private String name;

    @OneToMany(
            mappedBy = "participantList",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "id")
    private List<ParticipantInListEntity> participants;

    @ManyToOne
    private OrganizerEntity organizer;

    /**
     * Empty constructor, new Entity is set to false
     */
    public ParticipantListEntity() {
        super(false);
    }

    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new
     */
    public ParticipantListEntity(boolean newEntity) {
        super(newEntity);
    }

    /**
     * Get name of participant list
     * @return the Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of participant list
     * @param name the Name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Organizer of this participant list
     * @return organizer
     */
    public OrganizerEntity getOrganizer() {
        return organizer;
    }

    /**
     * Set Organizer/Owner of this participant list
     * @param organizer organizer
     */
    public void setOrganizer(OrganizerEntity organizer) {
        this.organizer = organizer;
    }

    /**
     * Get list of participants belonging to this list
     * @return the participants
     */
    public List<ParticipantInListEntity> getParticipants() {
        return participants;
    }

    /**
     * Set list of participants belongig to this list
     * @param participants the participants to be set
     */
    public void setParticipants(List<ParticipantInListEntity> participants) {
        this.participants = participants;
    }

}

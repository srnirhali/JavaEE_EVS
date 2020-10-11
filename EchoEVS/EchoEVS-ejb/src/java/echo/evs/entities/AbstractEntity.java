package echo.evs.entities;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;

/**
 * Abstract entity class
 * 
 * @author Dr. Volker Riediger riediger@uni-koblenz.de
 */
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String uuid;

    @Version
    private int jpaVersion;

    /**
     * Empty constructor for entities that are not new (have an uuid)
     */
    public AbstractEntity() {
        this(false);
    }

    /**
     * Constructor that also generates uuid if entity is new
     * @param newEntity true if new (no uuid set)
     */
    public AbstractEntity(boolean newEntity) {
        if (newEntity) {
            uuid = UUID.randomUUID().toString();
        }
    }

    /**
     * Get id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Get Uuid
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get Jpa version
     * @return jpaVersion
     */
    public int getJpaVersion() {
        return jpaVersion;
    }

    /**
     * Set jpa version
     * @param jpaVersion jpa Version to be set
     */
    public void setJpaVersion(int jpaVersion) {
        this.jpaVersion = jpaVersion;
    }

    /**
     * Get hashCode of object   
     * @return hashCode
     */
    @Override
    public int hashCode() {
        if (uuid == null) {
            throw new IllegalStateException("UUID not set");
        }
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.uuid);
        return hash;
    }

    /**
     * Compares object with this instance
     * @param obj object that should be compared to
     * @return true if the same
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractEntity other = (AbstractEntity) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }

    /**
     * Check if uuid is set and valid
     */
    @PrePersist
    public void checkUuid() {
        if (uuid == null || uuid.length() != 36) {
            throw new IllegalStateException(getClass().getSimpleName() + ": invalid UUID (" + uuid + ")");
        }
    }

    /**
     * Outputs the classname, the id and the jpaVersion
     * @return info string
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + getId() + "/" + getJpaVersion();
    }

}

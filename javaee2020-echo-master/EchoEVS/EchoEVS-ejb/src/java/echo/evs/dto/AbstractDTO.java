package echo.evs.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract DTO that has all the parameter all other DTOs need as well
 * 
 * @author Dr. Volker Riediger riediger@uni-koblenz.de
 */
public abstract class AbstractDTO implements Serializable {

    private static final long serialVersionUID = 1389881901711700L;

    private String uuid;
    private int jpaVersion;

    /**
     * Constuctor of AbstractDTO
     */
    public AbstractDTO() {
    }

    /**
     * Constructor of AbstractDTO
     * 
     * @param uuid uuid
     * @param jpaVersion jpa version
     */
    public AbstractDTO(String uuid, int jpaVersion) {
        this.uuid = uuid;
        this.jpaVersion = jpaVersion;
    }

    /**
     * Gets the jpa version
     * 
     * @return jpaVersion
     */
    public int getJpaVersion() {
        return jpaVersion;
    }

    /**
     * Gets the unique identifier
     * 
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Checks wether this object is new (not stored in db yet, no uuid set)
     * 
     * @return true if object is new
     */
    public boolean isNew() {
        return uuid == null;
    }

    /**
     * Gets the hashCode if it has any
     * 
     * @return hashCode
     */
    @Override
    public int hashCode() {
        if (uuid == null) {
            throw new IllegalStateException("UUID not set");
        }
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.uuid);
        return hash;
    }

    /**
     * Compares this to the given object.
     * 
     * @param obj object that should be compared
     * @return true if the same one
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
        final AbstractDTO other = (AbstractDTO) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }
}

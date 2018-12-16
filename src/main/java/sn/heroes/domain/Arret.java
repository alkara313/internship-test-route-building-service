package sn.heroes.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Arret.
 */
@Entity
@Table(name = "arret")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Arret implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_a")
    private String idA;

    @Column(name = "nom_a")
    private String nomA;

    @Column(name = "lattitude")
    private String lattitude;

    @Column(name = "longitude")
    private String longitude;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdA() {
        return idA;
    }

    public Arret idA(String idA) {
        this.idA = idA;
        return this;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getNomA() {
        return nomA;
    }

    public Arret nomA(String nomA) {
        this.nomA = nomA;
        return this;
    }

    public void setNomA(String nomA) {
        this.nomA = nomA;
    }

    public String getLattitude() {
        return lattitude;
    }

    public Arret lattitude(String lattitude) {
        this.lattitude = lattitude;
        return this;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Arret longitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Arret arret = (Arret) o;
        if (arret.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), arret.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Arret{" +
            "id=" + getId() +
            ", idA='" + getIdA() + "'" +
            ", nomA='" + getNomA() + "'" +
            ", lattitude='" + getLattitude() + "'" +
            ", longitude='" + getLongitude() + "'" +
            "}";
    }
}

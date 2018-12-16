package sn.heroes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Compagnie.
 */
@Entity
@Table(name = "compagnie")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Compagnie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_c")
    private String idC;

    @Column(name = "nom_c")
    private String nomC;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Ligne ligne;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdC() {
        return idC;
    }

    public Compagnie idC(String idC) {
        this.idC = idC;
        return this;
    }

    public void setIdC(String idC) {
        this.idC = idC;
    }

    public String getNomC() {
        return nomC;
    }

    public Compagnie nomC(String nomC) {
        this.nomC = nomC;
        return this;
    }

    public void setNomC(String nomC) {
        this.nomC = nomC;
    }

    public Ligne getLigne() {
        return ligne;
    }

    public Compagnie ligne(Ligne ligne) {
        this.ligne = ligne;
        return this;
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
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
        Compagnie compagnie = (Compagnie) o;
        if (compagnie.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), compagnie.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Compagnie{" +
            "id=" + getId() +
            ", idC='" + getIdC() + "'" +
            ", nomC='" + getNomC() + "'" +
            "}";
    }
}

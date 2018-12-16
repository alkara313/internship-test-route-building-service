package sn.heroes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Ligne.
 */
@Entity
@Table(name = "ligne")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ligne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_l")
    private String idL;

    @Column(name = "num_l")
    private String numL;

    @Column(name = "nom_l")
    private String nomL;

    @Column(name = "nom_trajet")
    private String nomTrajet;

    @ManyToOne
    @JsonIgnoreProperties("lignes")
    private Heroes heroes;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "ligne_arret",
               joinColumns = @JoinColumn(name = "lignes_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "arrets_id", referencedColumnName = "id"))
    private Set<Arret> arrets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdL() {
        return idL;
    }

    public Ligne idL(String idL) {
        this.idL = idL;
        return this;
    }

    public void setIdL(String idL) {
        this.idL = idL;
    }

    public String getNumL() {
        return numL;
    }

    public Ligne numL(String numL) {
        this.numL = numL;
        return this;
    }

    public void setNumL(String numL) {
        this.numL = numL;
    }

    public String getNomL() {
        return nomL;
    }

    public Ligne nomL(String nomL) {
        this.nomL = nomL;
        return this;
    }

    public void setNomL(String nomL) {
        this.nomL = nomL;
    }

    public String getNomTrajet() {
        return nomTrajet;
    }

    public Ligne nomTrajet(String nomTrajet) {
        this.nomTrajet = nomTrajet;
        return this;
    }

    public void setNomTrajet(String nomTrajet) {
        this.nomTrajet = nomTrajet;
    }

    public Heroes getHeroes() {
        return heroes;
    }

    public Ligne heroes(Heroes heroes) {
        this.heroes = heroes;
        return this;
    }

    public void setHeroes(Heroes heroes) {
        this.heroes = heroes;
    }

    public Set<Arret> getArrets() {
        return arrets;
    }

    public Ligne arrets(Set<Arret> arrets) {
        this.arrets = arrets;
        return this;
    }

    public Ligne addArret(Arret arret) {
        this.arrets.add(arret);
        return this;
    }

    public Ligne removeArret(Arret arret) {
        this.arrets.remove(arret);
        return this;
    }

    public void setArrets(Set<Arret> arrets) {
        this.arrets = arrets;
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
        Ligne ligne = (Ligne) o;
        if (ligne.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), ligne.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Ligne{" +
            "id=" + getId() +
            ", idL='" + getIdL() + "'" +
            ", numL='" + getNumL() + "'" +
            ", nomL='" + getNomL() + "'" +
            ", nomTrajet='" + getNomTrajet() + "'" +
            "}";
    }
}

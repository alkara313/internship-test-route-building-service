package sn.heroes.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Heroes.
 */
@Entity
@Table(name = "heroes")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Heroes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_heroes")
    private String idHeroes;

    @Column(name = "nom_h")
    private String nomH;

    @Column(name = "prenom_h")
    private String prenomH;

    @Column(name = "adresse")
    private String adresse;

    @OneToOne    @JoinColumn(unique = true)
    private Compagnie comagnie;

    @OneToMany(mappedBy = "heroes")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Ligne> lignes = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdHeroes() {
        return idHeroes;
    }

    public Heroes idHeroes(String idHeroes) {
        this.idHeroes = idHeroes;
        return this;
    }

    public void setIdHeroes(String idHeroes) {
        this.idHeroes = idHeroes;
    }

    public String getNomH() {
        return nomH;
    }

    public Heroes nomH(String nomH) {
        this.nomH = nomH;
        return this;
    }

    public void setNomH(String nomH) {
        this.nomH = nomH;
    }

    public String getPrenomH() {
        return prenomH;
    }

    public Heroes prenomH(String prenomH) {
        this.prenomH = prenomH;
        return this;
    }

    public void setPrenomH(String prenomH) {
        this.prenomH = prenomH;
    }

    public String getAdresse() {
        return adresse;
    }

    public Heroes adresse(String adresse) {
        this.adresse = adresse;
        return this;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Compagnie getComagnie() {
        return comagnie;
    }

    public Heroes comagnie(Compagnie compagnie) {
        this.comagnie = compagnie;
        return this;
    }

    public void setComagnie(Compagnie compagnie) {
        this.comagnie = compagnie;
    }

    public Set<Ligne> getLignes() {
        return lignes;
    }

    public Heroes lignes(Set<Ligne> lignes) {
        this.lignes = lignes;
        return this;
    }

    public Heroes addLigne(Ligne ligne) {
        this.lignes.add(ligne);
        ligne.setHeroes(this);
        return this;
    }

    public Heroes removeLigne(Ligne ligne) {
        this.lignes.remove(ligne);
        ligne.setHeroes(null);
        return this;
    }

    public void setLignes(Set<Ligne> lignes) {
        this.lignes = lignes;
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
        Heroes heroes = (Heroes) o;
        if (heroes.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), heroes.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Heroes{" +
            "id=" + getId() +
            ", idHeroes='" + getIdHeroes() + "'" +
            ", nomH='" + getNomH() + "'" +
            ", prenomH='" + getPrenomH() + "'" +
            ", adresse='" + getAdresse() + "'" +
            "}";
    }
}

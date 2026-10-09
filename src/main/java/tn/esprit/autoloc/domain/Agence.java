package tn.esprit.autoloc.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 50)
    private String ville;

    @Column(nullable = false, length = 150)
    private String adresse;

    @Column(length = 20)
    private String telephone;

    // ===== Associations =====

    @OneToMany(mappedBy = "agence")
    private List<Employe> employes = new ArrayList<>();

    @OneToMany(mappedBy = "agence")
    private List<Vehicule> vehicules = new ArrayList<>();
}

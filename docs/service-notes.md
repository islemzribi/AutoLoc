# Atelier 4 — Couche Service : interfaces, implémentations et injections

Projet **autoloc-api** (Java 17, Spring Boot, Spring Data JPA, MySQL, Lombok).
Interfaces dans `tn.esprit.autoloc.service`, implémentations dans `tn.esprit.autoloc.service.impl`,
exception métier dans `tn.esprit.autoloc.exception`.

---

## Tableau B — Dépendances injectées par service

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Le service ne connaît que l'interface du repository : testable sans base, la dépendance `final` ne change jamais (singleton sans état) et aucune injection par attribut (`@Autowired` interdit par Sonar S6813). |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Injection typée par interface pour découpler le service de la persistance ; le constructeur rend la dépendance obligatoire, refusée par Spring si absente. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Une seule dépendance suffit au CRUD complet de la flotte ; l'injection par constructeur garantit des champs `final` et un objet service immuable. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Le référentiel d'équipements se limite à son repository ; l'inversion de contrôle permet de remplacer l'implémentation sans toucher au contrôleur. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Conformité au pattern de l'atelier : une dépendance par service, injectée par le conteneur Spring, jamais instanciée avec `new`. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Les associations `vehicule`/`client` sont persistées telles que reçues : seul le repository de la réservation est nécessaire (pas de service d'appel croisé). |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | L'agrégat `Contrat → Paiement` (cascade + orphanRemoval) est géré par JPA via l'entité : le repository du contrat suffit à la création et à la suppression en cascade des paiements. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Service en lecture seule (aucune écriture) : une dépendance unique en `final` sur `IPaiementRepository` suffit pour `findById` et `findAll`. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (Lombok `@RequiredArgsConstructor`) | Le lien vers le véhicule est recopié depuis la requête (pas de gestion des liens ici) : injection par constructeur du seul repository concerné. |

Chaque classe de service porte `@Service` + `@RequiredArgsConstructor` (et `@Transactional`,
avec `readOnly = true` sur `findById`/`findAll`). Les contrôleurs dépendent des **interfaces**
`I…Service`, jamais des `…ServiceImpl` ni des repositories.

---

## Contrat commun des 8 services CRUD

```java
<T> create(T entite);   // id non null -> IllegalArgumentException("Un nouveau ... ne doit pas avoir d'identifiant")
<T> findById(Long id);  // absent -> ResourceNotFoundException("...", id)
List<T> findAll();
<T> update(Long id, T entite); // charge l'existant, recopie les attributs modifiables, save(l'existant)
void deleteById(Long id);      // absent -> ResourceNotFoundException
```

`IPaiementService` n'expose que `findById` et `findAll` : toute écriture d'un paiement passe
par l'agrégat `Contrat` (composition `cascade = ALL, orphanRemoval = true`).

---

## Règles de gestion (adaptées aux attributs réels du modèle)

| Service | Validation `create` / `update` |
|---|---|
| Agence | `nom`, `ville`, `adresse` non vides. |
| Employe | `nom`, `prenom` non vides ; `role` non null. |
| Vehicule | `immatriculation`, `marque`, `modele` non vides ; `categorie`, `statut` non nuls ; `tarifJournalier` non nul et `>= 0` (pas de kilométrage dans l'entité). |
| Equipement | `libelle` non vide (pas d'attribut quantité dans l'entité). |
| Client | `nom`, `prenom`, `email`, `numPermis` non vides ; `dateInscription` non null. |
| Reservation | `dateDebut`, `dateFin` non nules ; `dateFin >= dateDebut` ; `statut`, `vehicule`, `client` non nuls. |
| Contrat | `dateSignature` non null ; `montantTotal` non nul et `>= 0` ; `reservation` non null. |
| Maintenance | `dateDebut` non null ; `dateFin` (si renseignée) `>= dateDebut` ; `vehicule` non null (pas d'attribut coût dans l'entité). |

Toutes les violations lèvent une `IllegalArgumentException` avec un message explicite →
`GlobalExceptionHandler` répond en **400 Bad Request**. `ResourceNotFoundException` → **404 Not Found**,
corps JSON `{message, status, timestamp}`.

---

## Points d'attention du domaine

- **`update`** recopie uniquement les attributs modifiables : jamais l'`id`, jamais les collections
  `@OneToMany` gérées par `orphanRemoval` (`Contrat.paiements`) ni les collections inverses
  (`Agence.employes`, `Vehicule.reservations`, `Client.reservations`, `Equipement.vehicules`).
  L'objet reçu n'est jamais passé tel quel à `save` : l'existant est chargé par `findById`.
- **`ContratServiceImpl.deleteById`** charge le contrat, casse le lien `Reservation.contrat`
  (`setContrat(null)`) puis `delete(contrat)` : la cascade `ALL` supprime les paiements et le
  désamorçage évite le `TransientPropertyValueException` documenté dans `docs/repository-notes.md` §3.
- Les services sont des **singletons sans état** : aucun attribut modifiable hors du repository
  injecté en `final` ; pas de `@Autowired` sur attribut, pas de `new` de repository/service.
- **`ContratServiceImpl.create`** rattache chaque paiement reçu au contrat
  (`paiement.setContrat(contrat)`) avant `save` : Jackson ne renseigne jamais la back-reference
  `mappedBy`, sans quoi l'`INSERT paiement` échouerait sur `id_contrat cannot be null`.
- **Sérialisation JSON** : `@JsonIgnore` sur les références inverses (`Agence.employes`,
  `Agence.vehicules`, `Vehicule.reservations`, `Equipement.vehicules`, `Client.reservations`,
  `Reservation.contrat`, `Paiement.contrat`) coupe les cycles bidirectionnels qui provoquaient
  une récursion infinie des réponses REST.


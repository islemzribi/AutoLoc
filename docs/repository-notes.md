# Atelier 3 — Spring Data JPA : repositories et opérations CRUD

Projet **autoloc-api** (Java 17, Spring Boot, Spring Data JPA, MySQL/MariaDB XAMPP, Lombok).
Toutes les interfaces se trouvent dans `tn.esprit.autoloc.repository` et étendent
`JpaRepository<Entité, Long>`, sans annotation `@Repository` (inutile avec Spring Data).

---

## 1. Les 9 repositories

| Interface | Étend | Justification |
|---|---|---|
| `IAgenceRepository` | `JpaRepository<Agence, Long>` | CRUD complet des agences ; `findAll()` renvoie une `List<Agence>` (utile pour lister les points de vente) ; tri et pagination hérités. |
| `IEmployeRepository` | `JpaRepository<Employe, Long>` | CRUD complet ; `findAll()` renvoie une `List` pour parcourir le personnel ; `saveAndFlush` pour forcer l'écriture lors des imports. |
| `IVehiculeRepository` | `JpaRepository<Vehicule, Long>` | CRUD complet de la flotte ; tri par immatriculation/marque et pagination pour les grands parcs ; `saveAndFlush` pour obtenir l'`id` immédiatement. |
| `IEquipementRepository` | `JpaRepository<Equipement, Long>` | CRUD complet du référentiel d'équipements (partagé par le `@ManyToMany` des véhicules). |
| `IClientRepository` | `JpaRepository<Client, Long>` | CRUD complet des clients ; `findAll()` renvoie une `List` pour l'écran clientèle ; pagination pour les annuaires volumineux. |
| `IReservationRepository` | `JpaRepository<Reservation, Long>` | CRUD complet des réservations ; tri par dates et pagination pour le planning ; `saveAndFlush` avant génération du contrat. |
| `IContratRepository` | `JpaRepository<Contrat, Long>` | CRUD complet ; l'agrégat `Contrat` pilote la composition des `Paiement` (cascade + orphanRemoval) ; tri/pagination sur `montantTotal`. |
| `IPaiementRepository` | `JpaRepository<Paiement, Long>` | CRUD complet nécessaire pour lire/compter les paiements ; **les créations et suppressions métier passent par le `Contrat`** (voir §4). |
| `IMaintenanceRepository` | `JpaRepository<Maintenance, Long>` | CRUD complet des interventions ; tri par date et pagination pour l'historique d'atelier. |

`JpaRepository` est le seul contrat qui offre à la fois : `List` en sortie, tri/pagination,
le vidage de session (`flush`) et les écritures immédiates (`saveAndFlush`).

---

## 2. Rappel des contrats Spring Data

| Contrat | Rôle |
|---|---|
| `Repository<T, ID>` | Interface marqueur racine, sans aucune méthode (indique à Spring Data de créer un proxy). |
| `CrudRepository<T, ID>` | CRUD de base. `findAll()` renvoie un **`Iterable<T>`**, pas une `List`. |
| `ListCrudRepository<T, ID>` | Même CRUD, mais `findAll()`, `findAllById()` et `saveAll()` renvoient directement des `List`. |
| `PagingAndSortingRepository<T, ID>` | Ajoute `findAll(Sort)` et `findAll(Pageable)`. **Depuis Spring Data 3.0, cette interface n'étend plus `CrudRepository`** (séparation des responsabilités). |
| `ListPagingAndSortingRepository<T, ID>` | Variante renvoyant des `List` pour `findAll(Sort)`. |
| `JpaRepository<T, ID>` | Étend `ListCrudRepository` et `ListPagingAndSortingRepository`. Ajoute `flush()`, `saveAndFlush()`, `deleteAllInBatch()`, `deleteAllByIdInBatch()`, `getReferenceById()`, etc. |

**`Iterable` vs `List` :** `findAll()` de `CrudRepository` renvoie un `Iterable<T>` : on ne peut
que l'itérer, sans `size()` ni accès par index. `ListCrudRepository`/`JpaRepository` renvoient une
`List<T>`, plus pratique côté service (`.size()`, `.get(0)`, `stream()`).

**Pourquoi `JpaRepository` pour AutoLoc ?** Il cumule le CRUD complet, les résultats en `List`,
le tri/pagination et les opérations de vidage (`flush`/`saveAndFlush`) dont les services métier
ont besoin, sans écrire une seule requête.

---

## 3. Avertissement : `deleteAllInBatch()` et `deleteAllByIdInBatch()`

Ces méthodes exécutent **un seul `DELETE` JPQL en masse**, sans charger les entités dans le
contexte de persistance. Conséquences pour AutoLoc :

- la **cascade** (`CascadeType.ALL` du `Contrat` vers les `Paiement`) n'est **pas** appliquée ;
- l'**`orphanRemoval`** non plus.

Résultat : soit des **lignes `paiement` orphelines**, soit une **violation de clé étrangère**
(`paiement.id_contrat` non satisfaite) selon l'ordre. Pour supprimer un contrat proprement,
utiliser `delete(entity)` / `deleteById(id)` (qui déclenchent cascade et orphanRemoval) et non
les variantes `*InBatch`.

> Remarque liée : supprimer le côté propriétaire (`Contrat`) d'un `@OneToOne` bidirectionnel
> `Contrat ↔ Reservation` peut lever un `TransientPropertyValueException` si l'inverse est encore
> référencé ; il faut casser le lien (`reservation.setContrat(null)`) dans la même transaction.
> Cette contrainte a été observée lors de la démonstration de l'étape 3.

---

## 4. Note sur `IPaiementRepository`

`IPaiementRepository` a été créé pour **lire** les paiements (listing, comptage, statistiques).
La relation `Contrat → Paiement` est une **composition** (`@OneToMany(cascade = ALL,
orphanRemoval = true)`). Le code métier doit donc **passer par le `Contrat`** pour :

- **créer** un paiement : `contrat.getPaiements().add(paiement)` puis `contratRepository.save(contrat)` ;
- **retirer** un paiement : `contrat.getPaiements().remove(paiement)` puis `contratRepository.save(contrat)`.

Un `paiementRepository.save(...)` / `delete(...)` direct resterait possible techniquement mais
contournerait le cycle de vie de l'agrégat `Contrat`.

---

## 5. Extraits de logs SQL (étape 3, profil `demo`)

Bannière Spring Data au démarrage :

```
RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 113 ms.
Found 9 JPA repository interfaces.
```

### a) `save()` d'une entité neuve + cascade Contrat → Paiement (INSERT)

```sql
insert into contrat (date_signature, montant_total, id_reservation, valide) values (?, ?, ?, ?)
insert into paiement (id_contrat, date_paiement, mode_paiement, montant) values (?, ?, ?, ?)
insert into paiement (id_contrat, date_paiement, mode_paiement, montant) values (?, ?, ?, ?)
-- Contrat insere : id=1 , paiements=2
```

**Interprétation :** un seul `save(contrat)` déclenche la cascade : `INSERT` du contrat puis des
deux paiements. Hibernate renvoie le contrat avec son `id` généré (IDENTITY) et la collection
`paiements` remplie.

### b) `save()` d'une entité existante (id renseigné) : SELECT puis UPDATE

```sql
select ... from contrat c1_0 join reservation r1_0 ... left join paiement p1_0 ... where c1_0.id_contrat=?
update contrat set date_signature=?, montant_total=?, id_reservation=?, valide=? where id_contrat=?
```

**Interprétation :** `save()` sur une entité détachée appelle `merge()` : Hibernate recharge
l'état courant (`SELECT`) avant de calculer et d'exécuter le `UPDATE`.

### c) `findById()` (Optional)

```sql
select ... from contrat c1_0 join reservation r1_0 ... where c1_0.id_contrat=?
-- Contrat retrouve : id=1 montant=550.00
```

**Interprétation :** `findById()` renvoie un `Optional<Contrat>` ; il est traité avec `ifPresent`
(jamais de `get()` sans vérification).

### d) `orphanRemoval` : retirer un paiement puis `save`

```sql
select ... from contrat c1_0 ... left join paiement p1_0 ...
delete from paiement where id_paiement=?
-- Paiement id=1 retire ; paiements restants=1
```

**Interprétation :** le paiement sorti de la collection `contrat.paiements` n'est plus référencé :
`orphanRemoval = true` génère automatiquement le `DELETE`.

### e) Tri et pagination (`JpaRepository`)

```sql
select ... from contrat c1_0 order by c1_0.montant_total desc
select ... from contrat c1_0 order by c1_0.montant_total desc limit ?, ?
select count(c1_0.id_contrat) from contrat c1_0
-- Page 1 : content=[2, 1] totalElements=3 totalPages=2
```

**Interprétation :** `findAll(Sort.by("montantTotal").descending())` ajoute `order by`. La
pagination (`PageRequest.of(0, 2, tri)`) ajoute `limit` + une requête `count` pour alimenter
`getContent()`, `getTotalElements()` et `getTotalPages()`.

### f) `deleteById()` : cascade vers les paiements

```sql
select ... from contrat c1_0 where c1_0.id_contrat=?
select ... from paiement p1_0 where p1_0.id_contrat=?
delete from paiement where id_paiement=?
delete from contrat where id_contrat=?
```

**Interprétation :** `deleteById()` charge le contrat, Hibernate initialise la collection des
paiements, les supprime (cascade), puis supprime le contrat.

### g) `deleteById()` sur un id inexistant

```sql
select ... from contrat c1_0 ... where c1_0.id_contrat=?
-- deleteById(999999) : aucune exception.
-- findById(999999).ifPresent(delete) : aucune exception.
```

**Interprétation :** avec Spring Data JPA (Spring Boot 4), `deleteById()` d'un id absent est un
**no-op** (aucune exception). Le pattern `findById(id).ifPresent(delete)` est également sûr.
La base de démonstration a été nettoyée en fin de runner (`contrats restants=0`, `paiements restants=0`).

---

## 6. Revue qualité (équivalent SonarQube for IDE), anomalie par anomalie

> **Ces anomalies proviennent d'une revue statique MANUELLE** (le plugin SonarQube for IDE
> n'étant pas exécutable depuis la ligne de commande). Elles doivent être **confirmées dans
> IntelliJ** avec le plugin SonarQube for IDE.

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Import à joker `import jakarta.persistence.*;` dans les **9 entités** du package `domain`. | `java:S2208` — *Wildcard imports should not be used*. Les imports à joker masquent les dépendances réelles et favorisent les collisions de noms. | Remplacés par des **imports explicites triés** (`Column`, `Entity`, `Id`, `GeneratedValue`, …). Aucun changement de schéma. |
| Imports **mal ordonnés / non groupés** et absence de ligne vide après `package` dans `Vehicule`, `Contrat`, `Client` et `Equipement` (ex. `java.util.*` éclatés de part et d'autre des imports `jakarta`/`lombok`). | Convention d'**ordre des imports** (style Sonar/Checkstyle : imports groupés et triés). | Imports réorganisés en **un groupe `java.*` trié**, une ligne vide, puis `jakarta.*`/`lombok.*` triés, avec ligne vide après `package`. |
| Fichier `application.properties` encodé en **Windows-1252** (octets `0xE0`/`0xE9`). | Fichiers sources/ressources à encodage **non UTF-8** : le filtrage Maven échoue (`MalformedInputException: Input length = 1`) et l'application ne compile pas. | Fichier **ré-encodé en UTF-8 (sans BOM)** ; le contenu (ddl-auto=update, show-sql, logs) est inchangé. |

### Points de qualité vérifiés (conformes, rien à corriger)

- Aucun `@Data` sur les entités (uniquement `@Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor`).
- Aucun import inutilisé ni dupliqué (vérifié sur les packages `domain` et `repository`).
- Aucune collection JPA non initialisée (`new ArrayList<>()` / `new HashSet<>()` partout).
- Aucune annotation `@Repository` superflue, aucune méthode ni classe inutile, aucun `System.out`.

### DDL au démarrage

Aucune **nouvelle** instruction DDL n'est introduite par ces corrections. Hibernate 7 (avec le
dialecte MySQL et une base MariaDB XAMPP rapportée comme « 5.5.5 ») émet, **avant et après**
corrections, les mêmes `alter table … modify column … enum (…)` de normalisation des colonnes
`@Enumerated(EnumType.STRING)`. `ddl-auto=update` reste en place.

# Atelier 5 - Spring MVC & SpringDoc

## Configuration

- Spring Boot 4.1.1, Java 17, SpringDoc `springdoc-openapi-starter-webmvc-ui` 3.1.1
  (version construite pour Spring Boot 4.1).
- Context path global : `server.servlet.context-path=/api`.
- Swagger UI : <http://localhost:8080/api/swagger-ui.html>
- Description OpenAPI : <http://localhost:8080/api/v3/api-docs>
- Les contrôleurs ne répètent pas `/api` dans `@RequestMapping`.

## A. Endpoints REST

Tous les identifiants sont de type `Long` (`idAgence`, `idVehicule`, `idClient`).

| Contrôleur | Verbe + route | Service appelé | Succès | Erreurs |
| --- | --- | --- | --- | --- |
| AgenceController | `GET /agences` | `IAgenceService.findAll()` | 200 | - |
| AgenceController | `GET /agences/{id}` | `IAgenceService.findById(id)` | 200 | 404 |
| AgenceController | `POST /agences` | `IAgenceService.create(agence)` | 201 | 400 |
| AgenceController | `PUT /agences/{id}` | `IAgenceService.update(id, agence)` | 200 | 400, 404 |
| AgenceController | `DELETE /agences/{id}` | `IAgenceService.deleteById(id)` | 204 | 404 |
| VehiculeController | `GET /vehicules` | `IVehiculeService.findAll()` | 200 | - |
| VehiculeController | `GET /vehicules/{id}` | `IVehiculeService.findById(id)` | 200 | 404 |
| VehiculeController | `POST /vehicules` | `IVehiculeService.create(vehicule)` | 201 | 400 |
| VehiculeController | `PUT /vehicules/{id}` | `IVehiculeService.update(id, vehicule)` | 200 | 400, 404 |
| VehiculeController | `DELETE /vehicules/{id}` | `IVehiculeService.deleteById(id)` | 204 | 404 |
| ClientController | `GET /clients` | `IClientService.findAll()` | 200 | - |
| ClientController | `GET /clients/{id}` | `IClientService.findById(id)` | 200 | 404 |
| ClientController | `POST /clients` | `IClientService.create(client)` | 201 | 400 |
| ClientController | `PUT /clients/{id}` | `IClientService.update(id, client)` | 200 | 400, 404 |
| ClientController | `DELETE /clients/{id}` | `IClientService.deleteById(id)` | 204 | 404 |

Les routes complètes incluent le context path : `http://localhost:8080/api/agences`.
Le `GlobalExceptionHandler` transforme `ResourceNotFoundException` en 404 et
`IllegalArgumentException` en 400, sans `try/catch` dans les contrôleurs.

## B. Annotations Jackson retenues

| Association | Annotation | Justification |
| --- | --- | --- |
| `Agence.vehicules` (OneToMany, côté inverse) | `@JsonIgnore` | Empêche la boucle Agence -> Vehicule -> Agence et masque la collection inverse. |
| `Agence.employes` (OneToMany, côté inverse) | `@JsonIgnore` | Même raison, association inverse non nécessaire dans le JSON. |
| `Vehicule.agence` (ManyToOne, côté propriétaire) | aucune | Le JSON peut fournir `"agence": {"idAgence": 1}` lors d'un POST/PUT. |
| `Vehicule.equipements` / `Equipement.vehicules` (ManyToMany) | `@JsonIgnore` sur `Equipement.vehicules` | Le côté inverse est masqué, le véhicule peut référencer ses équipements. |
| `Client.reservations` (OneToMany, côté inverse) | `@JsonIgnore` | Collection inverse masquée. |

Aucun `@JsonManagedReference` / `@JsonBackReference` n'a été ajouté : ce couple est
réservé aux compositions fortes (cascade `ALL`). Ici, `Vehicule.agence` doit rester
désérialisable pour rattacher un véhicule à une agence existante.

`@JsonIgnore` ignore la propriété en sérialisation **et** en désérialisation.
`@JsonBackReference` ne participe qu'à la sérialisation (le champ est omis) et reste
désérialisable ; il doit être associé à `@JsonManagedReference` sur l'autre extrémité.
Pour une liste inverse que l'on ne veut jamais exposer, `@JsonIgnore` est plus simple.

Aucune erreur `ByteBuddyInterceptor` rencontrée pendant les tests : la propriété
`spring.jackson.serialization.fail-on-empty-beans=false` n'a donc pas été ajoutée.
`spring.jpa.open-in-view` reste à sa valeur par défaut, ce qui permet de sérialiser
les associations LAZY chargées pendant la requête.

## C. Postman vs Swagger

| Outil | Apport |
| --- | --- |
| Swagger UI | Documentation vivante générée depuis le code : routes, paramètres, schémas, codes de réponse. Pratique pour découvrir et essayer une API manuellement. |
| Postman | Scénarios exécutables et reproductibles : enchaînement des requêtes, variables d'environnement, assertions automatiques (`pm.test`), exécution par collection runner ou CI. |

Exemple concret : Swagger permet de constater que `POST /api/vehicules` renvoie 201 et
d'afficher le schéma de `Vehicule`. Postman va plus loin : il crée une agence, récupère
son `idAgence`, l'injecte dans le corps du véhicule, vérifie le statut 201, sauvegarde
`vehiculeId`, puis vérifie que `GET /api/vehicules/{{vehiculeId}}` renvoie 200 et que
`PUT` renvoie bien la valeur modifiée.

## D. Anomalies SonarQube relevées

1. **S1128 - Unused imports**
   - Explication : les anciens contrôleurs retournaient des `ResponseEntity` ; après
     passage aux retours directs avec `@ResponseStatus`, les imports `ResponseEntity`
     et parfois `HttpStatus` devenaient inutiles.
   - Correction : réécriture des contrôleurs avec uniquement les imports réellement
     utilisés (`@ResponseStatus`, annotations Swagger, service, entité).

2. **S6813 - Spring field injection**
   - Explication : l'injection par attribut (`@Autowired` sur un champ) masque les
     dépendances, complique les tests et empêche l'immutabilité.
   - Correction : champ `private final` + `@RequiredArgsConstructor` (injection par
     constructeur), dépendance vers l'interface du service.

3. **S1176 - Documentation des API publiques**
   - Explication : les méthodes publiques des contrôleurs n'avaient ni Javadoc ni
     documentation d'API.
   - Correction : Javadoc de classe en français et annotations `@Operation` /
     `@ApiResponses` / `@Parameter` sur chaque méthode et chaque `@PathVariable`.

4. **Exposition directe des entités JPA (revue qualité / sécurité)**
   - Explication : les entités sont sérialisées telles quelles, ce qui couple l'API au
     modèle de persistance et peut exposer des champs sensibles ou des associations
     non voulues.
   - Correction provisoire : annotations Jackson ciblées (`@JsonIgnore` sur les côtés
     inverses). Correction définitive prévue au Chapitre 10 : DTO de requête/réponse.

5. **Préfixe `/api` dupliqué dans les contrôleurs (anomalie de routage)**
   - Explication : avec `server.servlet.context-path=/api`, un `@RequestMapping("/api/...")`
     produisait `/api/api/...`.
   - Correction : suppression du préfixe `/api` dans tous les `@RequestMapping` des
     contrôleurs existants.

## E. Réponses aux questions

1. **DispatcherServlet et HandlerMapping**
   Le `DispatcherServlet` est le front controller de Spring MVC. Il reçoit toutes les
   requêtes HTTP, interroge les `HandlerMapping` pour trouver le contrôleur et la
   méthode correspondant à l'URL, puis invoque le handler via un `HandlerAdapter`.
   Les arguments (`@PathVariable`, `@RequestBody`) sont résolus, la méthode du service
   est appelée, et le résultat est sérialisé en JSON par un `HttpMessageConverter`.

2. **Pourquoi dépendre de l'interface du service ?**
   Le contrôleur ne dépend que du contrat, pas de l'implémentation. Cela respecte
   l'inversion de dépendance, facilite les tests (mock de l'interface), permet de
   changer d'implémentation ou d'ajouter des proxys (transactions, AOP) sans modifier
   le contrôleur.

3. **Pourquoi 201, 204, 404 plutôt que 500 ?**
   201 indique qu'une ressource a été créée, 204 qu'une suppression a réussi sans corps
   de réponse, 404 que la ressource demandée n'existe pas. 500 signale une erreur interne
   du serveur, pas une erreur fonctionnelle prévisible. Des codes corrects rendent l'API
   prévisible pour les clients, les caches et les outils de monitoring.

4. **Risques de l'exposition directe des entités JPA**
   Couplage entre API et schéma de base, sérialisation de champs sensibles ou paresseux,
   boucles infinies entre associations, mass assignment (le client peut renseigner des
   champs non prévus), schéma instable lors des refactorings, et impossibilité de faire
   évoluer la base sans casser les clients. Les DTO corrigeront ces problèmes.

5. **`@JsonManagedReference` vs `@JsonIgnore`**
   `@JsonManagedReference` / `@JsonBackReference` forment un couple pour les relations
   bidirectionnelles : le côté « managed » est sérialisé, le côté « back » est omis.
   `@JsonIgnore` supprime complètement la propriété du JSON, en entrée comme en sortie.
   Pour un côté inverse qu'on ne veut jamais exposer (ex. `Agence.vehicules`),
   `@JsonIgnore` est suffisant et évite de sur-annoter ; on garde le côté propriétaire
   (`Vehicule.agence`) libre pour recevoir un identifiant.

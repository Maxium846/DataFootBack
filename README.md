Présentation : 
DataFoot est une API REST développée avec Spring Boot, permettant de récupérer et stocker des données footballistiques provenant de l'API API-Football.

Le projet Permet de gérer différentes données 
-Championnats
-Equipes
-Joueurs
-Match
-Statistique
-Classement 

Les données récupérées sont stockées dans une base POstGreSQL pour étre ensuite exploitées par le Front.

Une partie des données est aussi utilisées pour alimenter le Jeu guessThePlayer , ce jeu permet a l'utilisateur de deviner le joueur selon un niveau de difficulté qui est liés au classement direct des équipes dans les différents championnats.

Stack : 
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Spring Security / JWT
- Maven
- JUnit / Mockito


  Architecture :
  Le projet est organisé par domaine métier :
  - League
  - Team
  - Player
  - Match
  - etc

Chaque domaine contient 
- Controller
- Service
- Repository
- Dto/Mapper


Api Externe : 
- récupération des données via RestClient
- Dtoapi représentant la réponse Api
- Mapping vers les entités interne
- Sauvegarde PostgreSQL
- Exposition des Endpoints
  
Sécurité :
L'application intègre une authentification pour pouvoir jouer au jeu
- Inscription Utilisateur
- Connexion
Les information sensibles ( clé API , mdp ,username PostgreSQL) sont externalisé via des variables d'environnements

Amélioration prévus : 
- Ajouter davantage de tests
- Améliorer les performance des imports
- Ajouter les saisons pour avoir les stats des joueurs et équipes sur plusieurs saison
- Ajouter des comparatif de joueurs

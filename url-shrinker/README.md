# url-shrinker

Webapp qui a 2 buts:
  * Exposer l'API de creation/recherche de shorts url
  * Exposer une url permettant aux utilisateurs d'etre redigié vers l'url original a partir de l'url ourte


## Démarrer

### Standalone, bd embarquée
gradlew bootRun

### Démarrer une instance, bd locale

Démarrer la BD partagée (port 9090)
    
    cd ../url-storage
    gradlew bootRun

Démarrer l'application en se brancant sur la BD partagée (port 9090)
    
    cd ../url-shrinker
    gradlew bootRun --args="--spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:url-storage --spring.datasource.username=yho --spring.datasource.password=localusage"

### Démarrer plusieurs instance, bd locale

Démarrer la BD partagée (port 9090)

    cd ../url-storage
    gradlew bootRun

Démarrer l'application en se brancant sur la BD partagée, faire varier le port utilisé

    cd ../url-shrinker
    gradlew bootRun --args="--spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:url-storage --spring.datasource.username=yho --spring.datasource.password=localusage --server.port=8090"
    gradlew bootRun --args="--spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:url-storage --spring.datasource.username=yho --spring.datasource.password=localusage --server.port=8091"


Ou si on préfére lancer un process java directement

    gradlew clean build
    java -jar build\libs\url-shrinker-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:url-storage --spring.datasource.username=yho --spring.datasource.password=localusage --server.port=8090
    java -jar build\libs\url-shrinker-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:url-storage --spring.datasource.username=yho --spring.datasource.password=localusage --server.port=8091


## Customisation au runtime

### Profils Spring
Plusieurs profils permettent de changer le comportement au runtime afin de comparer les différentes approches possibles.
Ces élèments disparaitront une fois la phase de preuve de concept terminée.

#### Profils liés au stockage des urls
  * jpa : h2 , actif par défaut
  * memory : remplace h2 par une implem memoire (hashmap)
  * mongo : remplace h2 par une implem mongo -- TODO
  * redis : ajouter un cache Redis pour fournir les URLs courtes --> longues -- TODO

#### Profils liés à la stratégie de génération du code court
  * random : UUID.random, actif par défaut
  * hash : remplace la generation du code par un hashing de l'url longue
  * time : remplace la generation du code par un timeInMillis
  * seq : remplace la generation du code par l'utilisation d'une sequence -- TODO
  * pregenerate : remplace l enertion du code par le recours a des codes pre-generes -- TODO

Les profils par défaut sont donc : jpa (h2) et random.

### Autres paramétres

La configuration spring est autement modifiable au runtime via les variables d'environnement ou arguments de CLI.

Cette application dispose cependant de deux paramétres supplémentaires et surchargeables:
  * url-shrinker.retryOnNonUniqueCode : Defaut 3. ex: --url-shrinker.retryOnNonUniqueCode=10
  * url-shrinker.shortCodeLength : Defult 9. may be ineffective according to the strategy choosen. ex: --url-shrinker.shortCodeSize=20  -- TODO


### Exemples de configurations "custom"

Port différent, stockage en memoire, code basé sur un hash, 50 retry max (vs 3), code de 3 caractéres (vs 9)

    java -jar build\libs\url-shrinker-0.0.1-SNAPSHOT.jar --spring.profiles.active=memory,seq --url-shrinker.retryOnNonUniqueCode=50 --url-shrinker.shortCodeLength=3 

Port différent, stockage mongo, code basé sur une sequence

    java -jar build\libs\url-shrinker-0.0.1-SNAPSHOT.jar --server.port=9999 --spring.profiles.active=mongo,seq 

 Si on lance plusierus instances utiliser des parametres consistant en dehors du port.

## Notes du Codeur

Directement destiné aux reviewers de cet exercice.

### NDC et Todos

// NDC dans le code correspond à des commentaires destinés aux reviewer

// TODO représente les choses qui n'ont pas pu etre terminée ou qui demanderaient plus d'investigations avant de trancher

### Choses nouvelles tesées

J'ai voulu profiter de cet exercice pour tester différentes choses:
  * VsCode
  * Les record classes
  * Coder sans lombok (supposant que les record le rendent moins utile; un peu décu) ou les data class kotlin
  * gradle.kts (transparent)
  * les auditable jpa (revisite en SB3.x, pas tellement changés)
  * un systeme modulaire pour conditionner le comportement au runtime (sans passer par différentes branches de code ou des application-xxx.yml). Content du résultat mais pas utile pour tout. Dans le cadre d'un vrai POC res serieux on preferrait eviter de polluer chaque version avec des dependances non necessaires par exemple.

Eléments envisagés mais abandonnés :
  * Rest assured: abandonné faute de temps, l'idée aurait été d'ecrire un petit scenario de smoke test pour le CI en demarrant 2 instances sunr une seule BD
  * Redis et Embedded redis : abandonné car la librairie ne semble plus maintenue vois incompatible avec SB3, j'aurai aimé avoir le temps de tester un petit cache Redis mais je voulais un projet qui demarre sans aucun setup. Qui plus est le stockage pure cache (meme avec de la persistence) n'est probablement pas judicieux.

### Autres points laissés de côté délibérément

  * Sécurisation de l'API : pas tellement le sujet ici; il est évident qu'en production une API de ce genre serait sécurisée
  * Test containers : faute de temps 
  * Spring hateoas : faute de temps et pour garder le code relativement lisible
  * Swagger : même raison + pas convaincu de l'apport sur une API interne de maniere generale
  * CI/CD/deploiement sur un cloud
  * Développement réactif : ne semble pas intéressant par rapport au use case + gros potentiel de depasser le timing
  * GraphQL vs REST : idem
  * Monitoring des perfs
  * Pouvoir monitorer la fréquence des collisions
  * Cryptage des URLS stockées : une url peut contenir des informations confidentielles comme des identifiants de session ou des query params; on a contourné ici le probleme en supposant que le syteme appelant l'API ne pouvait pas fournir ce genre d'elements.

## Références

https://www.sivalabs.in/spring-boot-3-error-reporting-using-problem-details/

https://www.javaguides.net/2018/09/spring-data-jpa-auditing-with-spring-boot2-and-mysql-example.html

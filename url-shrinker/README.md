On suppose qu'il s'agit d'un besoin type youtube/instagram/whatsapp/twitter : Réduire les urls pour pouvoir les partager facilement, notamment dans les outils ou le npmbre de caracteres est limité.

La solution sera composée :
 * d'une API REST pour adminsitrer la banque d'URL:
   * Creer une nouvelle url courte
   * Récupérer les informaitons d'une url courte
   * quelques autres endpoints classiques pour faciliter les tests
 * Une url de redirection 
   * http://domain/code-court  --> http://mon-url-originale (HTTP 302)

La redirection s'effectue côté client.

Postulats:
  * Gros volume de données, sujet a augmenter avec le temps (382 milliards de photos sur google en 2030)
  * Grosse consommation du service de redirection (7 milliards de photos partagées par jour sur whatsapp, 1.3 pour instagram)


Le tiny code

Specs:
  * 9 caracteres (http://domain/xxxxxxxxx)
  * non prédictible

Le code ne pourra pas être triable (trop court pour encoder un timestamp.)
Encodé en Base 64 (par simplicité; BASE 62 serait mieux pour eviter des caracters a encoder dans l'url):
  * URL compliant
  * Bonne entropie (1,80144E+16 permutations) 

Options : 
  * Timestamp + data + encoding : simple et triable mais depasse les 9 char (meme en trichant sur l'Epoch). On ne peut le tronquer sans avoir BCP de collisions.
  * Random : simple, collisions a gérer
  * Hashing : simple, la meme URL hashée 2x donne le meme code; mais collisions a gérer, peut-être plus de collisions que random?
  * Sequence : pas de collision, la BD devient un SPOF/bottleneck pour generer le code court; Encoder en base64 la chaine de caractere generer a partir de la sequence pose deux problemes cela dit: relativement predictible et max 999999. L'idéeal serait de combiner la sequence à d'autres informaitons (ip, random, etc)
  * Pré-génération: Avoir un stock de permutations disponibles et uniques pré-générées en avance (par un daemon qui surveille que le stock disponible est toujours au dessus d'un certain seuil par exemple). Accélère la creation et simplifie la gestion des collisions. Un peu plus complexe a mettre en place et demande peut-etre un storage dédié. La methode de generation importe assez peu dans ce cas. Cet entrepôt peut lui aussi devenir un SPOF/bottelneck.
  * Combinaison de ces elements

Pour une performance optimale, on pourrait opter pounr une combinaison de:
  * BD relationnelle ou NOSQL pour stocker la data  (DynamoDB)
  * Cache pour stocker les pair code / url souvennt demandées (Memory DB ou ElasticCache for Redis)
  * Un systeme de pré-génération fournissant des codes utilisables (et garantissant que celui fournit n'a pas été utilisé par un autre)

Dans le cadre de cet exercice on choisira quelque chose de plus simple cela dit:
  * on genere une chaine de caractere "random" qu'on encode en Base64 et on verifie grace a une contrainte d'unicité qu'elle n'Est pas utilisée
  * les données liées a l'API REST seront stockés dans une base de données; on utilisera une base de données h2 démarrées dans un process séparé pour pouvoir simuler la scalabilité en demarrant plusieurs instances
  * a voir en fonction du temps: l'utilisation d'un cache sera simulé via les annotations Spring Cache
  * a voir fonciton du temps : une simulation d'un systeme de pre-generation est envisageable

Le développement aura une approche preuve de ceoncept en tête avec dans l'idée de permettre de tester différentes configurations facilement (un profil "mongo" pourrait remplacer le repository h2 par un repository branché sur un service mongo DB par exemple; on pourrait envisager la meme chose pur utiliser un cache dredis ou changer le mecanisme de generatioin du code court eglement).









Persistence :

Options :
 * SGBD traditionnel : Parfait pour les sequences et garantir l'unicité du code court, pas d'autres utilités d'avoir une BD relationelle par contre; attention au volume et aux performances; difficilement scalable passé un certain volume (cf. approche instagram dans les references)
 * NoSQL : Performances, scalabilité.
 * Cache / BD Memoire : Throughput (trés utile pour l'url de redirection); Choisir un service qui supporte la persistence. Volume par url léger; mais beaucoup d'urls a stocker: attention au coût.
 * Combinaison (SGBD ou NoSQL + Cache)




CLI:

Démarrer en local, bd embarquée
cd url-shrinker
gradlew bootRun


Démarrer la BD partagée
cd url-storage
gradlew bootRun

Démarrer une instance
cd url-shrinker
gradlew clean build

Démarrer une instance (changer le dernier parametre pour utiliser un port disponible):
java -jar build\libs\url-storage-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:url-storage --spring.datasource.username=yho --spring.datasource.password=localusage --server.port=8090


Profils:
  * default : H2 + random short code + base64
  * memory : remplace h2 par une implem memoire (hashmap)
  * mongo : remplace h2 par une implem mongo -- TODO
  * redis : ajouter un cache Redis pour fournir les URLs courtes --> longues -- TODO
  * hash : remplace la generation du code par un hashing de l'url longue
  * time : remplace la generation du code par un timeInMillis
  * seq : remplace la generation du code par l'utilisation d'une sequence -- TODO
  * pregenerate : remplace l enertion du code par le recours a des codes pre-generes -- TODO


Paramétres surchargeables:
  * url-shrinker.retryOnNonUniqueCode : Defaut 3. ex: --url-shrinker.retryOnNonUniqueCode=10
  * url-shrinker.shortCodeLength : Defult 9. may be ineffective according to the strategy choosen. ex: --url-shrinker.shortCodeSize=20  -- TODO


Port différent, stockage en memoire plutot que h2, hashing plutot que random, 50 retry plutot que 3, code sur 3 caractere plutot que 9
java -jar build\libs\url-shrinker-0.0.1-SNAPSHOT.jar --server.port=9999 --spring.profiles.active=memory,hash --url-shrinker.retryOnNonUniqueCode=50 --url-shrinker.shortCodeLength=3





Références
https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c
https://blog.codinghorror.com/url-shortening-hashes-in-practice/
https://photutorial.com/photos-statistics/
https://gosunaina.medium.com/designing-a-scalable-url-shortener-like-tiny-url-72106a7018ee
https://www.geeksforgeeks.org/how-to-design-a-tiny-url-or-url-shortener/
https://blog.codinghorror.com/url-shortening-hashes-in-practice/

https://www.sivalabs.in/spring-boot-3-error-reporting-using-problem-details/
https://www.javaguides.net/2018/09/spring-data-jpa-auditing-with-spring-boot2-and-mysql-example.html

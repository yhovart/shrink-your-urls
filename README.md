# shrink-your-urls

On suppose qu'il s'agit d'un besoin type youtube/instagram/whatsapp/twitter : Réduire les urls pour pouvoir les partager facilement, notamment dans les outils où le nombre de caracteres est limité.

## Composition du repo

    * [Le projet en lui même, tout se passe ici](./url-shrinker/README.md)
    * [Un projet vide permettant de lancer un BD h2 accessible de l'externe. Utilisation facultative](./url-storage/README.md)
    * [Les smoke tests ou plus humblement... un postman](./smoke-tests/README.md)

## Solution Globale

La solution sera composée :
 * d'une API REST pour adminsitrer la "banque" d'URL:
   * Creer une nouvelle url courte
   * Récupérer les informaitons d'une url courte
   * quelques autres endpoints classiques pour faciliter les tests
 * Une url de redirection 
   * http://domain/code-court  --> http://mon-url-originale (HTTP 302)


Postulats:
  * Gros volume de données, sujet a augmenter avec le temps (382 milliards de photos sur google en 2030)
  * Grosse consommation du service de redirection (7 milliards de photos partagées par jour sur whatsapp, 1.3 pour instagram)

## Choix techniques

### Le code court

Specs:
  * 9 caracteres (http://domain/xxxxxxxxx)
  * non prédictible

Le code ne pourra pas être triable (trop court pour encoder un timestamp.).
Une fois le code généré on l'encodera en Base 64 (par simplicité; BASE 62 serait mieux pour eviter des caracters a encoder dans l'url):
  * URL compliant
  * Bonne entropie (1,80144E+16 permutations) 

#### Options  
  * Timestamp + data + encoding : simple et triable mais depasse les 9 char (meme en trichant sur l'Epoch). On ne peut le tronquer sans avoir BCP de collisions.
  * Random : simple, collisions a gérer
  * Hashing : simple, la meme URL hashée 2x donne le meme code; mais collisions a gérer, peut-être plus de collisions que random?
  * Sequence : pas de collision, la BD devient un SPOF/bottleneck pour generer le code court; Encoder en base64 la chaine de caractere generer a partir de la sequence pose deux problemes cela dit: relativement predictible et max 999999. L'idéeal serait de combiner la sequence à d'autres informaitons (ip, random, etc)
  * Pré-génération: Avoir un stock de permutations disponibles et uniques pré-générées en avance (par un daemon qui surveille que le stock disponible est toujours au dessus d'un certain seuil par exemple). Accélère la creation et simplifie la gestion des collisions. Un peu plus complexe a mettre en place et demande peut-etre un storage dédié. La methode de generation importe assez peu dans ce cas. Cet entrepôt peut lui aussi devenir un SPOF/bottelneck.
  * Combinaison de ces elements



## La persistence

### Options :
 * SGBD traditionnel : Parfait pour les sequences et garantir l'unicité du code court, pas d'autres utilités d'avoir une BD relationelle par contre; attention au volume et aux performances; difficilement scalable passé un certain volume (cf. approche instagram dans les references)
 * NoSQL : Performances, scalabilité.
 * Cache / BD Memoire : Throughput (trés utile pour l'url de redirection); Choisir un service qui supporte la persistence. Volume par url léger; mais beaucoup d'urls a stocker: attention au coût.
 * Combinaison (SGBD ou NoSQL + Cache)

#### Choix 

Pour une performance optimale, on pourrait opter pour une combinaison de:
  * BD relationnelle ou NOSQL pour stocker la data  (DynamoDB)
  * Cache pour stocker les pair code / url souvennt demandées (Memory DB ou ElasticCache for Redis)
  * Un systeme de pré-génération fournissant des codes utilisables (et garantissant que celui fournit n'a pas été utilisé par un autre)

Si les volumes sont bien moindres que les géants du web on peut envisager quelque chose de beaucoup plus simple / pragmatique. Dans le cadre de cet exercice on choisira quelque chose de simple cela dit:
  * on genere une chaine de caractere "random" qu'on encode en Base64 et on verifie grace a une contrainte d'unicité qu'elle n'est pas utilisée
  * les données liées a l'API REST seront stockés dans une base de données; on utilisera une base de données h2 démarrées dans un process séparé pour pouvoir simuler la scalabilité en demarrant plusieurs instances

Le développement aura une approche exploratoire, preuves de concepts comparatives. On se donnera la possibilité d'explorer plusiers pistes èa partir de la meme codebase. Si le codebase ne devient pas trop chargé on pourrait envisager de l'utiliser comme base de tests comparatifs (un profil "mongo" pourrait remplacer le repository SGBD par un repository branché sur un service mongo DB par exemple; on pourrait envisager la meme chose pur utiliser un cache redis ou changer le mecanisme de generatioin du code court également).

Abandonné:
  * a voir en fonction du temps: l'utilisation d'un cache sera simulé via les annotations Spring Cache
  * a voir fonction du temps : une simulation d'un systeme de pre-generation est envisageable


## Options implémentés dans le cadre de l'exercice

Stockage en SGBR traditionnel. h2 embedded a été utilisé pour éviter les etapes d'installation. On peut lancer une BD dans un process séparées pour ne pas perdre les données a chaque redémarrage (simulation de persistance réelle).

Le code est généré aléatoirement, via un UUID (defaut), un hash + timestamp ou un simple timestamp. Il est ensuite encodé en BASE64 et tronqué à 9 char.


## Références
https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c
https://blog.codinghorror.com/url-shortening-hashes-in-practice/
https://photutorial.com/photos-statistics/
https://gosunaina.medium.com/designing-a-scalable-url-shortener-like-tiny-url-72106a7018ee
https://www.geeksforgeeks.org/how-to-design-a-tiny-url-or-url-shortener/
https://blog.codinghorror.com/url-shortening-hashes-in-practice/


# url-storage

Storage sans installation pour l'application url-shrinker.

Supporte h2 uniquement actuellement.

## Démarrer

    gradlew bootRun

La base est alors accessibles avec ces informations de connection :
  * url: jdbc:h2:tcp://localhost:9090/mem:url-storage 
  * username: yho
  * password: localusage


Il est possible de se connecter a la console h2.

http://localhost:8081/h2-console

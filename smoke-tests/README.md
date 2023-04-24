# smoke-tests

Simple collection Postman.

Valide le bon fonctionnement de l'API et de l'url de redirection.
Possibilité de changer le domain sur lequel elle s'exécute (variable "domain"); defaut http://localhost:8080
L'idée est d'intégrer cela a un pipeline de CI/CD.

## TODOs
  * newman
  * pouvoir rouler la collection en alternant les appels sur 2 instances diférentes (valider la scalabilité)
  * Tester le scenario via RestAssured
  * perfs tests (Gatling?); utile que sune une vrai infra

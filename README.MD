# Pole emploi parsing

Permet de récupérer des offres concernant plusieurs villes depuis Pôle Emploi.

## Installation

Lancer un maven install.

```bash
mvn install
```

Mettre à disposition une base de données MongoDB sur le port 27017.


## Usage

Lancer la classe OffreCollector pour récupérer le maximum d'offres pour les villes données en paramètre. (défaut : Rennes, Bordeaux, Paris).

Lancer la classe StatisticsGenerator pour créer des collections de statistiques sur différents champ via des aggrégations MongoDB. Les statistiques sont visibles dans la console, mais je conseille l'outil Compass pour une visualisation plus agréable.

## Contributing

Améliorations possibles :

- les tests, ils sont basiques et vérifient uniquement que les collections de la base de données ont été correctement remplies. Il faudrait des tests unitaires sur l'algorithmie.

- le code source :uniquement deux classes, une de collecte depuis pôle emploi et une de génération de statistiques. Il faudrait des entités, services et repository a minima.

- les requêtes ne sont pas au format Criteria

- le livrable n'a pas pu être dockerisé dans le temps imparti

- la gestion du SSL via le rest template n'est pas idéale surtout pour de la production

## License
[MIT](https://opensource.org/licenses/MIT)
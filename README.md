TP 7 : Activation du Cache de Second Niveau, Optimisation des Performances et Suppression du Problème N+1

Ce projet est un Travail Pratique (TP 7) dédié à l'optimisation des accès aux données dans un environnement JPA / Hibernate natif (API JEE standard, sans framework externe comme Spring).L'objectif principal est de comprendre et de maîtriser les techniques permettant de réduire le nombre de requêtes SQL générées par l'ORM, d'éviter le problème classique des $N+1$ requêtes et de configurer un cache de second niveau.

Objectifs d'Apprentissage:

À la fin de ce TP, vous serez capable de :Identifier et diagnostiquer le problème des $N+1$ requêtes lors de l'accès à des collections associées.Résoudre le problème $N+1$ avec JOIN FETCH dans vos requêtes JPQL.Utiliser les Entity Graphs (@NamedEntityGraph) pour définir de manière déclarative et dynamique le plan de chargement des entités.Configurer et activer le Cache de Second Niveau (L2 Cache) dans Hibernate à l'aide d'Ehcache (JCache / JSR-107).Analyser et comparer les performances à l'aide des statistiques fournies par Hibernate (hibernate.generate_statistics).

Structure du Projet:

tp7-hibernate-optimisations/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── tp/
        │           ├── entity/
        │           │   ├── Auteur.java
        │           │   └── Livre.java
        │           ├── util/
        │           │   └── JPAUtil.java
        │           └── Main.java
        └── resources/
            └── META-INF/
                └── persistence.xml


<img width="856" height="442" alt="image" src="https://github.com/user-attachments/assets/543747a4-62a4-4be5-8b0c-75924e163745" />


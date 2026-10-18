package com.example.service;

import com.example.model.Auteur;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class PerformanceTestService {

    @PersistenceContext
    public EntityManager em;

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    /**
     * Étape 7 : Mesure sans cache (démontre le problème N+1)
     */
    public void testPerformanceSansCache() {
        long start = System.currentTimeMillis();

        TypedQuery<Auteur> query = em.createQuery("SELECT a FROM Auteur a", Auteur.class);
        List<Auteur> auteurs = query.getResultList();

        // Accès aux livres pour provoquer le chargement LAZY (N requêtes supplémentaires)
        for (Auteur auteur : auteurs) {
            auteur.getLivres().size();
        }

        long end = System.currentTimeMillis();
        System.out.println("Durée sans cache (problème N+1) : " + (end - start) + " ms (" + auteurs.size() + " auteurs traités)");
    }

    /**
     * Étape 8 : Optimisation avec JOIN FETCH (résout le problème N+1 en une seule requête SQL)
     */
    public void testPerformanceAvecJoinFetch() {
        long start = System.currentTimeMillis();

        TypedQuery<Auteur> query = em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class);
        List<Auteur> auteurs = query.getResultList();

        // L'accès aux livres ne déclenche plus de requêtes supplémentaires
        for (Auteur auteur : auteurs) {
            auteur.getLivres().size();
        }

        long end = System.currentTimeMillis();
        System.out.println("Durée avec JOIN FETCH : " + (end - start) + " ms (" + auteurs.size() + " auteurs traités)");
    }

    /**
     * Étape 9 : Optimisation avec Entity Graph
     */
    public void testPerformanceAvecEntityGraph() {
        long start = System.currentTimeMillis();

        EntityGraph<?> graph = em.getEntityGraph("Auteur.livres");
        TypedQuery<Auteur> query = em.createQuery("SELECT DISTINCT a FROM Auteur a", Auteur.class);
        query.setHint("javax.persistence.fetchgraph", graph);
        List<Auteur> auteurs = query.getResultList();

        for (Auteur auteur : auteurs) {
            auteur.getLivres().size();
        }

        long end = System.currentTimeMillis();
        System.out.println("Durée avec Entity Graph : " + (end - start) + " ms (" + auteurs.size() + " auteurs traités)");
    }

    /**
     * Étape 10 : Test avec cache de second niveau et cache de requêtes
     */
    public void testPerformanceAvecCache() {
        // Première lecture (mise en cache)
        long start1 = System.currentTimeMillis();
        TypedQuery<Auteur> query1 = em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class);
        query1.setHint("org.hibernate.cacheable", true);
        List<Auteur> auteurs1 = query1.getResultList();
        for (Auteur auteur : auteurs1) {
            auteur.getLivres().size();
        }
        long end1 = System.currentTimeMillis();
        System.out.println("Durée lecture 1 (chargement DB + mise en cache) : " + (end1 - start1) + " ms");

        // Deuxième lecture (chargement direct depuis le cache)
        long start2 = System.currentTimeMillis();
        TypedQuery<Auteur> query2 = em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class);
        query2.setHint("org.hibernate.cacheable", true);
        List<Auteur> auteurs2 = query2.getResultList();
        for (Auteur auteur : auteurs2) {
            auteur.getLivres().size();
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Durée avec cache (lecture 2) : " + (end2 - start2) + " ms");
    }

    /**
     * Test complémentaire : Vérification du cache de second niveau via em.find()
     */
    public void testCacheSecondNiveauAvecFind() {
        System.out.println("--- Test de récupération individuelle via em.find (L2 Cache) ---");
        // Première lecture
        long s1 = System.currentTimeMillis();
        Auteur a1 = em.find(Auteur.class, 1L);
        if (a1 != null) {
            a1.getLivres().size();
        }
        long d1 = System.currentTimeMillis() - s1;
        System.out.println("find(Auteur 1) lecture 1 : " + d1 + " ms");

        // Vider le cache de premier niveau (session)
        em.clear();

        // Deuxième lecture : doit provenir du cache L2 sans requête SQL sur Auteur
        long s2 = System.currentTimeMillis();
        Auteur a2 = em.find(Auteur.class, 1L);
        if (a2 != null) {
            a2.getLivres().size();
        }
        long d2 = System.currentTimeMillis() - s2;
        System.out.println("find(Auteur 1) lecture 2 après em.clear() (hit L2 cache) : " + d2 + " ms");
    }
}

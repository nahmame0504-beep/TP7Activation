package com.example;

import com.example.service.DataInitService;
import com.example.service.PerformanceTestService;
import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("  TP : OPTIMISATION JPA & CACHE DE SECOND NIVEAU (EHCACHE 3)  ");
        System.out.println("==============================================================\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-cache-pu");
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true);

        EntityManager em = emf.createEntityManager();

        try {
            DataInitService dataInitService = new DataInitService();
            PerformanceTestService performanceTestService = new PerformanceTestService();

            dataInitService.em = em;
            performanceTestService.em = em;

            System.out.println(">>> 1. INITIALISATION DES DONNÉES DE TEST <<<");
            dataInitService.initData();

            // Vider le contexte de persistance (cache de 1er niveau)
            em.clear();

            System.out.println("\n>>> 2. TEST DE PERFORMANCE SANS OPTIMISATION (PROBLÈME N+1) <<<");
            performanceTestService.testPerformanceSansCache();

            em.clear();

            System.out.println("\n>>> 3. OPTIMISATION AVEC JOIN FETCH (1 SEULE REQUÊTE SQL) <<<");
            performanceTestService.testPerformanceAvecJoinFetch();

            em.clear();

            System.out.println("\n>>> 4. OPTIMISATION AVEC ENTITY GRAPH <<<");
            performanceTestService.testPerformanceAvecEntityGraph();

            em.clear();

            System.out.println("\n>>> 5. VALIDATION DU CACHE DE SECOND NIVEAU & QUERY CACHE <<<");
            performanceTestService.testPerformanceAvecCache();

            System.out.println("\n>>> 6. VALIDATION DU CACHE DE SECOND NIVEAU AVEC FIND() <<<");
            performanceTestService.testCacheSecondNiveauAvecFind();

            // Affichage des statistiques du cache Hibernate
            System.out.println("\n==============================================================");
            System.out.println("               STATISTIQUES DU CACHE HIBERNATE                ");
            System.out.println("==============================================================");
            System.out.println("Second Level Cache Hit Count   : " + stats.getSecondLevelCacheHitCount());
            System.out.println("Second Level Cache Miss Count  : " + stats.getSecondLevelCacheMissCount());
            System.out.println("Second Level Cache Put Count   : " + stats.getSecondLevelCachePutCount());
            System.out.println("Query Cache Hit Count          : " + stats.getQueryCacheHitCount());
            System.out.println("Query Cache Miss Count         : " + stats.getQueryCacheMissCount());
            System.out.println("Query Cache Put Count          : " + stats.getQueryCachePutCount());
            System.out.println("Entity Load Count              : " + stats.getEntityLoadCount());
            System.out.println("Entity Fetch Count             : " + stats.getEntityFetchCount());
            
            for (String regionName : stats.getSecondLevelCacheRegionNames()) {
                try {
                    CacheRegionStatistics regStats = stats.getCacheRegionStatistics(regionName);
                    if (regStats != null) {
                        System.out.println("Region [" + regionName + "] - Hits: " 
                                + regStats.getHitCount()
                                + ", Misses: " + regStats.getMissCount()
                                + ", Puts: " + regStats.getPutCount());
                    }
                } catch (Exception ignored) {
                }
            }
            System.out.println("==============================================================");

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
            System.out.println("\nApplication terminée avec succès.");
        }
    }
}

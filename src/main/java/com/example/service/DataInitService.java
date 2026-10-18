package com.example.service;

import com.example.model.Auteur;
import com.example.model.Livre;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class DataInitService {

    @PersistenceContext
    public EntityManager em;

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void initData() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        for (int i = 1; i <= 10; i++) {
            Auteur auteur = new Auteur();
            auteur.setNom("Auteur " + i);

            for (int j = 1; j <= 5; j++) {
                Livre livre = new Livre();
                livre.setTitre("Livre " + j + " de l'auteur " + i);
                auteur.ajouterLivre(livre);
            }

            em.persist(auteur);
        }

        tx.commit();
        System.out.println("Données initialisées : 10 auteurs créés, chacun avec 5 livres.");
    }
}

package com.example;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        Scanner scanner = new Scanner(System.in);

        //CRUD
        //CREATE
        inTransaction(entityManager -> {
            Country country = new Country();
            country.setCountryName("Sweden");
            country.setLanguageCode("sv");
            entityManager.persist(country);
            country = new Country();
            country.setCountryName("Denmark");
            country.setLanguageCode("dk");
            entityManager.persist(country);
        });
        //READ ONE
        inTransaction(entityManager -> {
            Country c = entityManager.find(Country.class, 2);
            if (c != null)
                System.out.println(c.getCountryName());
        });
        //UPDATE ONE
        inTransaction(entityManager -> {
            Country c = entityManager.find(Country.class, 1);

            if (c != null)
                c.setCountryName("Sweden");
        });
        //DELETE ONE
        inTransaction(entityManager -> {
            Country c = entityManager.find(Country.class, 4);
            if (c != null)
                entityManager.remove(c);
        });
        //READ MANY
        inTransaction(entityManager -> {
            String queryString = """
                    select c from Country c
                    """;
            var query = entityManager.createQuery(queryString, Country.class);
            List<Country> countries = query.getResultList();
            countries.forEach(System.out::println);
        });
        //READ with filter
        System.out.print("Enter country name: ");
        String name = scanner.nextLine();

        inTransaction(entityManager -> {
            String queryString = """
                    select c from Country c where c.countryName= :name
                    """;
            var query = entityManager.createQuery(queryString, Country.class);
            query.setParameter("name", name);
            List<Country> countries = query.getResultList();
            countries.forEach(System.out::println);
        });

        em.close();
    }

    static void inTransaction(Consumer<EntityManager> work) {
        try (EntityManager entityManager = JPAUtil.getEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                work.accept(entityManager);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}

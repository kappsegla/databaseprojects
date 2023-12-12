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
        String name = scanner.nextLine();

        //CRUD
        //CREATE
        inTransaction(entityManager -> {
            Country country = new Country();
            country.setCountryName("Sweden");
            country.setLanguageCode("sv");
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
                c.setCountryName("Changed");
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



//        System.out.print("Enter search term: ");
//        Scanner scanner = new Scanner(System.in);
//        String name = scanner.nextLine();
//
//        // Validate user input
//        if (name == null || name.isEmpty()) {
//            System.out.println("Invalid input.");
//            return;
//        }
//
//        TypedQuery<Country> query = em.createQuery("SELECT c FROM Country c WHERE c.countryName = :name", Country.class);
//        query.setParameter("name", name);
//        List<Country> countries = query.getResultList();
//        countries.forEach(System.out::println);

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

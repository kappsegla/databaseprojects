package com.example;

import jakarta.persistence.*;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        Scanner scanner = new Scanner(System.in);

        inTransaction(entityManager -> {
            Organization organization = entityManager.find(Organization.class,1);
            //Check for null
            Member member = new Member();
            member.setName("Martin");
            entityManager.persist(member);
            organization.addMember(member);
            organization = entityManager.find(Organization.class,2);
            organization.addMember(member);
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

import entity.Member;
import entity.Organization;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();

//        System.out.print("Enter search term: ");
//        Scanner scanner = new Scanner(System.in);
//        String name = scanner.nextLine();

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

        //https://vladmihalcea.com/entitymanager-find-getreference-jpa/


        inTransaction(entityManager -> {
            Organization org = new Organization();
            var m1 = new Member();
            entityManager.persist(m1);
            org.addMember(m1);
            var m2 = new Member();
            entityManager.persist(m2);
            org.addMember(m2);
            var m3 = new Member();
            entityManager.persist(m3);
            org.addMember(m3);
            entityManager.persist(org);
            entityManager.flush();
        });
        inTransaction(entityManager -> {
//            EntityGraph<Organization> entityGraph = entityManager.createEntityGraph(Organization.class);
//            entityGraph.addAttributeNodes("members");
//            Map<String, Object> properties = new HashMap<>();
//            properties.put("jakarta.persistence.fetchgraph", entityGraph);
//            var org = entityManager.find(Organization.class, 1, properties);
            var org = entityManager.find(Organization.class, 1);
            org.removeMember(new Member(1));
            entityManager.flush();
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
                // Log or handle the exception appropriately
                throw e;
            }
        }
    }
}

import jakarta.persistence.*;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter search term: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                logger.warning("Invalid input: search term is empty.");
                return;
            }

            try (EntityManager em = JPAUtil.getEntityManager()) {
                List<Country> countries = findCountriesByName(em, name);
                if (countries.isEmpty()) {
                    logger.info("No countries found matching: " + name);
                } else {
                    countries.forEach(System.out::println);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred", e);
        }

        //Run in transaction
        JPAUtil.getEntityManagerFactory().runInTransaction(em->{

        });
    }

    private static List<Country> findCountriesByName(EntityManager em, String name) {
        TypedQuery<Country> query = em.createQuery(
                "SELECT c FROM Country c WHERE c.countryName = :name", Country.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    public static void inTransaction(Consumer<EntityManager> work) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                work.accept(em);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}

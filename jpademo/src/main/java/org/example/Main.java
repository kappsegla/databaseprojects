package org.example;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

import java.util.List;

public class Main {
    static void main() {
        List<Class<?>> entities = getEntities("org.example");

        final PersistenceConfiguration cfg = new HibernatePersistenceConfiguration("emf")
                .jdbcUrl("jdbc:mysql://localhost:3306/jpa")
                .jdbcUsername("root")
                .jdbcPassword("root")
                .property("hibernate.hbm2ddl.auto", "update")
                .property("hibernate.show_sql", "true")
                .property("hibernate.format_sql", "true")
                .property("hibernate.highlight_sql", "true")
                .managedClasses(entities);
        //.managedClasses(Product.class, Event.class);

        try (EntityManagerFactory emf = cfg.createEntityManagerFactory()) {
            emf.runInTransaction(em -> {
                //If no Organizations in database, add some
                if (em.createQuery("select count(o) from Organization o", Long.class)
                        .getSingleResult() == 0) {
                    Organization organization1 = new Organization();
                    var member1 = new Member();
                    var member2 = new Member();
                    organization1.addMember(member1);
                    organization1.addMember(member2);
                    em.persist(organization1);
                    em.flush();
                    Organization organization2 = new Organization();
                    organization2.addMember(member1);
                    em.persist(organization2);
                }
                System.out.println("==== Using select query, N + 1 ====");
                em.createQuery("from Organization", Organization.class)
                        .getResultList().forEach(System.out::println);

                System.out.println("==== Using join fetch ====");
                //Using join fetch
                em.createQuery(
                        "select o from Organization o join fetch o.member",
                        Organization.class
                ).getResultList().forEach(System.out::println);

                System.out.println("==== Entity graph ====");
                EntityGraph<?> graph = em.getEntityGraph("Organization.member");
                em.createQuery("from Organization", Organization.class)
                        .setHint("jakarta.persistence.fetchgraph", graph)
                        .getResultList().forEach(System.out::println);
            });

//
//        emf.runInTransaction(em -> {
//            //Without cascade for persist
//                Profile profile = new Profile();
//                profile.setBio("Martin is a mediocer programmer with an imposter symptom.");
//                em.persist(profile);
//                User user = new User();
//                user.setName("marti");
//                user.setProfile(profile);
//                em.persist(user);

//                //With cascade for persist
//                Profile profile = new Profile();
//                profile.setBio("Martin is a mediocer programmer with an imposter symptom.");
//                User user = new User();
//                user.setName("marti");
//                user.setProfile(profile);
//                em.persist(user);
//        });

//            emf.runInTransaction(em ->{
//                Event event = new Event();
//                event.setInfo("An event about nothing 1");
//                em.persist(event);
//                event = new Event();
//                event.setInfo("An event about nothing 2");
//                em.persist(event);
//                event = new Event();
//                em.persist(event);
//                event.setInfo("An event about nothing 3");
//                IO.readln();
//                em.createQuery("select e from Event e")
//                        .getResultList()
//                        .forEach(System.out::println);
//                IO.readln();
//            });

            //Insert/Persist of new entity
            //Update of manged entity
//            emf.runInTransaction(em -> {
//                Product product = new Product();
//                product.setName("Pixel 10");
//                product.setPrice(BigDecimal.valueOf(12000.0));
//                em.persist(product); //<- Runs insert direct?
//                System.out.println("Product id: " + product.getId());
//                product.setName("Pixel 10 Pro"); //<- Waits with update to later. Cached
//            });

//            //Find Product and all products from database
//            emf.runInTransaction(em -> {
//                Product product = em.find(Product.class, 1);
//                System.out.println(product);
//                System.out.println("=====");
//                //JPQL
//                var query = em.createQuery("select p from Product p where p.name = 'Pixel 10'", Product.class);
//                //var query = em.createNativeQuery("select * from Product where name = 'Pixel 10'", Product.class);
//                query.getResultList().forEach(System.out::println);
//            });
//
//            emf.runInTransaction(em -> {
//                System.out.println("====Remove entity====");
//                //We don't need all the field data to remove an object.
//                //Just the reference to a managed object is needed.
//                var product = em.getReference(Product.class, 8);
//                if (product != null)
//                    em.remove(product);
//            });

        }
    }

    //Not invented here!!
    private static List<Class<?>> getEntities(String pkg) {
        List<Class<?>> entities;

        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableClassInfo()
                             .enableAnnotationInfo()
                             .acceptPackages(pkg)
                             .scan()) {

            entities = scanResult.getClassesWithAnnotation(Entity.class).loadClasses();
        }
        return entities;
    }
}

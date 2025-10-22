package org.example;

import org.hibernate.StatelessSession;

public class Main {
    public static void main(String[] args) {
        //https://thorben-janssen.com/getting-started-with-jakartae-data/
        //https://thorben-janssen.com/jakarta-data-repository/
        //https://docs.jboss.org/hibernate/orm/7.0/repositories/html_single/Hibernate_Data_Repositories.html
        StatelessSession statelessSession = HibernateUtil.getStatelessSession();

        CountryRepository repository = new CountryRepository_(statelessSession);
        repository.findAll().forEach(System.out::println);
        repository.findByName("Sweden").forEach(System.out::println);
    }
}

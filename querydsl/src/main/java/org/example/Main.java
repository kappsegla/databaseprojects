package org.example;

import jakarta.persistence.EntityManager;

public class Main {

    public static void main(String[] args) {
        try( EntityManager em = JPAUtil.getEntityManager();) {
            var queryService = new CountryQueryService(em);
            queryService.findByLanguage("sv");
        }
    }
}

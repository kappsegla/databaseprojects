package org.example;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.QCountry;

import jakarta.persistence.EntityManager;
import java.util.List;

public class CountryQueryService {

    private final JPAQueryFactory queryFactory;

    public CountryQueryService(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Country> findByLanguage(String langCode) {
        QCountry country = QCountry.country;

        return queryFactory
                .selectFrom(country)
                .where(country.languageCode.eq(langCode))
                .fetch();
    }
}

package org.example;

import org.example.jooq.tables.City;
import org.example.jooq.tables.Country;
import org.example.jooq.tables.records.CityRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.codegen.GenerationTool;
import org.jooq.impl.DSL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {
//        String xmlString = Files.readString(Path.of(Main.class.getResource("/jooq-config.xml").toURI()));
//        GenerationTool.generate(xmlString);

        String userName = "root";
        String password = "root";
        String url = "jdbc:mysql://localhost:3306/test";
        Connection conn = DriverManager.getConnection(url, userName, password);

        DSLContext context = DSL.using(conn, SQLDialect.MYSQL);

        var country = context.fetchOne(Country.COUNTRY, Country.COUNTRY.COUNTRY_NAME.eq("Sverige"));

        CityRecord city = context.newRecord(City.CITY);
        city.setCityName("Malmö");
        city.setInhabitants(357_377);
        city.setCountryId(country.getCountryId());

        city.store();

    }
}

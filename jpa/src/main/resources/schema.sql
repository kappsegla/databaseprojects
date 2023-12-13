CREATE TABLE continent (
                           continent_id INT AUTO_INCREMENT,
                           continent_name VARCHAR(255),
                           PRIMARY KEY (continent_id)
);

CREATE TABLE country (
                         country_id INT AUTO_INCREMENT,
                         country_name VARCHAR(255),
                         continent_id INT,
                         language_code VARCHAR(255),
                         currency_code VARCHAR(255),
                         national_day DATE,
                         PRIMARY KEY (country_id),
                         FOREIGN KEY (continent_id) REFERENCES continent(continent_id)
);
CREATE TABLE city (
                      city_id INT AUTO_INCREMENT,
                      city_name VARCHAR(255),
                      inhabitants INT,
                      country_id INT,
                      PRIMARY KEY (city_id),
                      FOREIGN KEY (country_id) REFERENCES country(country_id)
);

-- Lägg till kontinenter
INSERT INTO continent (continent_name) VALUES ('Europa');
INSERT INTO continent (continent_name) VALUES ('Asien');

-- Lägg till länder
INSERT INTO country (country_name, continent_id, language_code, currency_code, national_day) VALUES ('Sverige', 1, 'sv', 'SEK', '2023-06-06');
INSERT INTO country (country_name, continent_id, language_code, currency_code, national_day) VALUES ('Norge', 1, 'no', 'NOK', '2023-05-17');
INSERT INTO country (country_name, continent_id, language_code, currency_code, national_day) VALUES ('Japan', 2, 'ja', 'JPY', '2023-02-11');

-- Lägg till städer
INSERT INTO city (city_name, inhabitants, country_id) VALUES ('Stockholm', 975551, 1);
INSERT INTO city (city_name, inhabitants, country_id) VALUES ('Göteborg', 583056, 1);
INSERT INTO city (city_name, inhabitants, country_id) VALUES ('Oslo', 693491, 2);
INSERT INTO city (city_name, inhabitants, country_id) VALUES ('Bergen', 283929, 2);
INSERT INTO city (city_name, inhabitants, country_id) VALUES ('Tokyo', 9273000, 3);
INSERT INTO city (city_name, inhabitants, country_id) VALUES ('Osaka', 2691000, 3);

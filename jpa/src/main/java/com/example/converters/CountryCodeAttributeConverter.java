package com.example.converters;

import com.example.CountryCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CountryCodeAttributeConverter implements AttributeConverter<CountryCode, String> {

    @Override
    public String convertToDatabaseColumn(CountryCode countryCode) {
        return countryCode == null ? null : countryCode.code();
    }

    @Override
    public CountryCode convertToEntityAttribute(String s) {
        return s == null ? null : CountryCode.of(s);
    }
}

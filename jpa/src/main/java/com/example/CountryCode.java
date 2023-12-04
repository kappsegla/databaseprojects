package com.example;

import jakarta.validation.constraints.NotNull;

import java.util.Locale;
import java.util.Objects;

//ValueObject
public class CountryCode {
    private final String code;

    private CountryCode(@NotNull String code) {
        this.code = validate(code);
    }

    public static CountryCode of(String code){
        return new CountryCode(code);
    }

    public String code() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryCode that = (CountryCode) o;

        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    public static @NotNull String validate(@NotNull String code) {
        if (!isValid(code))
            throw new IllegalArgumentException("Invalid language code: " + code);
        return code;
    }

    public static boolean isValid(@NotNull String code) {
        return switch (code.length()) {
            case 2 -> Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).contains(code);
            case 3 -> Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA3).contains(code);
            default -> false;
        };
    }
}

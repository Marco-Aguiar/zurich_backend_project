package com.zurich.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BookStatus {
    WISHLIST,        // Ainda quer comprar ou buscar
    PLAN_TO_READ,    // Pretende começar em breve
    READING,         // Em andamento
    PAUSED,          // Parou temporariamente
    DROPPED,         // Abandonou completamente
    READ,            // Finalizou
    RECOMMENDED;     // Recomenda a outros

    @JsonCreator
    public static BookStatus fromValue(String value) {
        return BookStatus.valueOf(value.toUpperCase());
    }
}

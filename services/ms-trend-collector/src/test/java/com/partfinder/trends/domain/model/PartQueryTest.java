package com.partfinder.trends.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartQueryTest {

    @Test
    void normaliza_a_lowercase_y_trim() {
        assertEquals("filtro de aceite", new PartQuery("  Filtro De Aceite  ").value());
    }

    @Test
    void rechaza_vacio_o_solo_espacios() {
        assertThrows(IllegalArgumentException.class, () -> new PartQuery("   "));
        assertThrows(IllegalArgumentException.class, () -> new PartQuery(""));
    }

    @Test
    void rechaza_null() {
        assertThrows(NullPointerException.class, () -> new PartQuery(null));
    }

    @Test
    void igualdad_se_basa_en_valor_normalizado() {
        assertEquals(new PartQuery("ABC"), new PartQuery("abc"));
    }
}

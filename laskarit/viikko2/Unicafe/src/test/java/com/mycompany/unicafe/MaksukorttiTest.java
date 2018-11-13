package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(10);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti != null);
    }

    @Test
    public void konstruktoriAsettaaOikeanSaldon() {
        assertEquals(10, kortti.saldo());
    }

    @Test
    public void rahanLatausKasvattaaSaldoa() {
        kortti.lataaRahaa(3);
        assertEquals(13, kortti.saldo());
    }

    @Test
    public void rahanOttoVahentaaSaldoa() {
        kortti.otaRahaa(4);
        assertEquals(6, kortti.saldo());
    }

    @Test
    public void liikaaOttoEiMuutaSaldoa() {
        kortti.otaRahaa(12);
        assertEquals(10, kortti.saldo());
    }

    @Test
    public void rahanOttoPalauttaaTrueJosSaldoRiittää() {
        assertTrue(kortti.otaRahaa(1));
    }

    @Test
    public void rahanOttoPalauttaaFalseJosSaldoEiRiitä() {
        assertFalse(kortti.otaRahaa(11));
    }

    @Test
    public void toStringNayttaaTekstin() {
        assertEquals("saldo: 0.10", kortti.toString());
    }
}

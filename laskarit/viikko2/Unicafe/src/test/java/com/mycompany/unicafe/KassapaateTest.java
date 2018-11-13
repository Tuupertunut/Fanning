/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Tuupertunut
 */
public class KassapaateTest {

    Kassapaate kassa;

    @Before
    public void setUp() {
        kassa = new Kassapaate();
    }

    @Test
    public void alkusaldoaOikeaMaara() {
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void alussaEdullisiaEiMyyty() {
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void alussaMaukkaitaEiMyyty() {
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullisenMaksuKateisellaMeneeKassaan() {
        kassa.syoEdullisesti(240);
        assertEquals(100240, kassa.kassassaRahaa());
    }

    @Test
    public void maukkaanMaksuKateisellaMeneeKassaan() {
        kassa.syoMaukkaasti(400);
        assertEquals(100400, kassa.kassassaRahaa());
    }

    @Test
    public void edullisenMaksuKateisellaKasvattaaLaskuria() {
        kassa.syoEdullisesti(240);
        assertEquals(1, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukkaanMaksuKateisellaKasvattaaLaskuria() {
        kassa.syoMaukkaasti(400);
        assertEquals(1, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullisenMaksuKateisellaPalauttaaVaihtorahat() {
        assertEquals(10, kassa.syoEdullisesti(250));
    }

    @Test
    public void maukkaanMaksuKateisellaPalauttaaVaihtorahat() {
        assertEquals(20, kassa.syoMaukkaasti(420));
    }

    @Test
    public void edullisenLiianPieniMaksuKateisellaEiMeneKassaan() {
        kassa.syoEdullisesti(210);
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void maukkaanLiianPieniMaksuKateisellaEiMeneKassaan() {
        kassa.syoMaukkaasti(330);
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void edullisenLiianPieniMaksuKateisellaEiKasvataLaskuria() {
        kassa.syoEdullisesti(130);
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukkaanLiianPieniMaksuKateisellaEiKasvataLaskuria() {
        kassa.syoMaukkaasti(35);
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullisenLiianPieniMaksuKateisellaPalauttaaKaiken() {
        assertEquals(220, kassa.syoEdullisesti(220));
    }

    @Test
    public void maukkaanLiianPieniMaksuKateisellaPalauttaaKaiken() {
        assertEquals(390, kassa.syoMaukkaasti(390));
    }

    @Test
    public void edullisenMaksuKortillaVeloittaaKorttia() {
        Maksukortti kortti = new Maksukortti(250);
        kassa.syoEdullisesti(kortti);
        assertEquals(10, kortti.saldo());
    }

    @Test
    public void maukkaanMaksuKortillaVeloittaaKorttia() {
        Maksukortti kortti = new Maksukortti(450);
        kassa.syoMaukkaasti(kortti);
        assertEquals(50, kortti.saldo());
    }

    @Test
    public void edullisenMaksuKortillaKasvattaaLaskuria() {
        kassa.syoEdullisesti(new Maksukortti(240));
        assertEquals(1, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukkaanMaksuKortillaKasvattaaLaskuria() {
        kassa.syoMaukkaasti(new Maksukortti(400));
        assertEquals(1, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullisenMaksuKortillaPalauttaaTrue() {
        assertTrue(kassa.syoEdullisesti(new Maksukortti(240)));
    }

    @Test
    public void maukkaanMaksuKortillaPalauttaaTrue() {
        assertTrue(kassa.syoMaukkaasti(new Maksukortti(400)));
    }

    @Test
    public void edullisenLiianPieniMaksuKortillaEiVeloitaKorttia() {
        Maksukortti kortti = new Maksukortti(210);
        kassa.syoEdullisesti(kortti);
        assertEquals(210, kortti.saldo());
    }

    @Test
    public void maukkaanLiianPieniMaksuKortillaEiVeloitaKorttia() {
        Maksukortti kortti = new Maksukortti(330);
        kassa.syoMaukkaasti(kortti);
        assertEquals(330, kortti.saldo());
    }

    @Test
    public void edullisenLiianPieniMaksuKortillaEiKasvataLaskuria() {
        kassa.syoEdullisesti(new Maksukortti(130));
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukkaanLiianPieniMaksuKortillaEiKasvataLaskuria() {
        kassa.syoMaukkaasti(new Maksukortti(35));
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullisenLiianPieniMaksuKortillaPalauttaaFalse() {
        assertFalse(kassa.syoEdullisesti(new Maksukortti(220)));
    }

    @Test
    public void maukkaanLiianPieniMaksuKortillaPalauttaaFalse() {
        assertFalse(kassa.syoMaukkaasti(new Maksukortti(390)));
    }

    @Test
    public void edullisenMaksuKortillaEiMeneKassaan() {
        kassa.syoEdullisesti(new Maksukortti(270));
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void maukkaanMaksuKortillaEiMeneKassaan() {
        kassa.syoMaukkaasti(new Maksukortti(500));
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void rahanLatausKasvattaaKortinSaldoa() {
        Maksukortti kortti = new Maksukortti(0);
        kassa.lataaRahaaKortille(kortti, 50);
        assertEquals(50, kortti.saldo());
    }

    @Test
    public void rahanLatausLisaaRahaaKassaan() {
        kassa.lataaRahaaKortille(new Maksukortti(0), 70);
        assertEquals(100070, kassa.kassassaRahaa());
    }

    @Test
    public void negatiivinenRahanLatausEiKasvataKortinSaldoa() {
        Maksukortti kortti = new Maksukortti(0);
        kassa.lataaRahaaKortille(kortti, -50);
        assertEquals(0, kortti.saldo());
    }

    @Test
    public void negatiivinenRahanLatausEiLisaaRahaaKassaan() {
        kassa.lataaRahaaKortille(new Maksukortti(0), -70);
        assertEquals(100000, kassa.kassassaRahaa());
    }
}

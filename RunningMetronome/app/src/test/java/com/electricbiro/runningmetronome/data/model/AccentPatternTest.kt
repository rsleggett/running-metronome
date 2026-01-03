package com.electricbiro.runningmetronome.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AccentPattern enum.
 * Tests verify display names and beat values for each pattern type.
 */
class AccentPatternTest {

    @Test
    fun `AccentPattern NONE should have 0 beats`() {
        assertEquals(0, AccentPattern.NONE.beats)
    }

    @Test
    fun `AccentPattern NONE should have correct display name`() {
        assertEquals("None", AccentPattern.NONE.displayName)
    }

    @Test
    fun `AccentPattern EVERY_2ND should have 2 beats`() {
        assertEquals(2, AccentPattern.EVERY_2ND.beats)
    }

    @Test
    fun `AccentPattern EVERY_2ND should have correct display name`() {
        assertEquals("Every 2nd", AccentPattern.EVERY_2ND.displayName)
    }

    @Test
    fun `AccentPattern EVERY_3RD should have 3 beats`() {
        assertEquals(3, AccentPattern.EVERY_3RD.beats)
    }

    @Test
    fun `AccentPattern EVERY_3RD should have correct display name`() {
        assertEquals("Every 3rd", AccentPattern.EVERY_3RD.displayName)
    }

    @Test
    fun `AccentPattern EVERY_4TH should have 4 beats`() {
        assertEquals(4, AccentPattern.EVERY_4TH.beats)
    }

    @Test
    fun `AccentPattern EVERY_4TH should have correct display name`() {
        assertEquals("Every 4th", AccentPattern.EVERY_4TH.displayName)
    }

    @Test
    fun `AccentPattern should have exactly 4 entries`() {
        assertEquals(4, AccentPattern.entries.size)
    }

    @Test
    fun `AccentPattern entries should be in correct order`() {
        val entries = AccentPattern.entries

        assertEquals(AccentPattern.NONE, entries[0])
        assertEquals(AccentPattern.EVERY_2ND, entries[1])
        assertEquals(AccentPattern.EVERY_3RD, entries[2])
        assertEquals(AccentPattern.EVERY_4TH, entries[3])
    }
}

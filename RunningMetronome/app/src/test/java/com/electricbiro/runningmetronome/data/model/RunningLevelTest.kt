package com.electricbiro.runningmetronome.data.model

import org.junit.Assert.*
import org.junit.Test

class RunningLevelTest {

    // fromId

    @Test
    fun `fromId returns correct level for each valid id`() {
        assertEquals(RunningLevel.NEW, RunningLevel.fromId("new"))
        assertEquals(RunningLevel.CASUAL, RunningLevel.fromId("casual"))
        assertEquals(RunningLevel.REGULAR, RunningLevel.fromId("regular"))
        assertEquals(RunningLevel.COMPETITIVE, RunningLevel.fromId("competitive"))
    }

    @Test
    fun `fromId returns CASUAL for unknown id`() {
        assertEquals(RunningLevel.CASUAL, RunningLevel.fromId("unknown"))
    }

    @Test
    fun `fromId returns CASUAL for empty string`() {
        assertEquals(RunningLevel.CASUAL, RunningLevel.fromId(""))
    }

    // Preset counts

    @Test
    fun `each level has exactly 6 presets`() {
        for (level in RunningLevel.entries) {
            assertEquals("${level.name} should have 6 presets", 6, level.presets.size)
        }
    }

    // Preset BPM ordering

    @Test
    fun `presets within each level are in ascending BPM order`() {
        for (level in RunningLevel.entries) {
            val bpms = level.presets.map { it.bpm }
            assertEquals("${level.name} presets must be ascending", bpms.sorted(), bpms)
        }
    }

    // Preset BPM ranges

    @Test
    fun `NEW level presets are in range 130-175`() {
        val bpms = RunningLevel.NEW.presets.map { it.bpm }
        assertTrue(bpms.all { it in 130..175 })
    }

    @Test
    fun `CASUAL level presets are in range 160-190`() {
        val bpms = RunningLevel.CASUAL.presets.map { it.bpm }
        assertTrue(bpms.all { it in 160..190 })
    }

    @Test
    fun `REGULAR level presets are in range 165-195`() {
        val bpms = RunningLevel.REGULAR.presets.map { it.bpm }
        assertTrue(bpms.all { it in 165..195 })
    }

    @Test
    fun `COMPETITIVE level presets are in range 170-200`() {
        val bpms = RunningLevel.COMPETITIVE.presets.map { it.bpm }
        assertTrue(bpms.all { it in 170..200 })
    }

    // Preset IDs

    @Test
    fun `preset IDs are unique within each level`() {
        for (level in RunningLevel.entries) {
            val ids = level.presets.map { it.id }
            assertEquals("${level.name} preset IDs must be unique", ids.distinct(), ids)
        }
    }

    @Test
    fun `all levels share the same preset ID set`() {
        val expectedIds = setOf("easy", "tempo", "race", "speed", "fast", "sprint")
        for (level in RunningLevel.entries) {
            assertEquals(expectedIds, level.presets.map { it.id }.toSet())
        }
    }
}

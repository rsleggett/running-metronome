package com.electricbiro.runningmetronome.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for DrumPattern and PatternStep data classes.
 * These tests verify validation logic and data integrity.
 */
class DrumPatternTest {

    @Test
    fun `DrumPattern should have exactly 8 steps by default`() {
        val pattern = DrumPattern()

        assertEquals(8, pattern.steps.size)
    }

    @Test
    fun `DrumPattern should throw exception when created with fewer than 8 steps`() {
        val invalidSteps = List(7) { PatternStep(null) }

        val exception = assertThrows(IllegalArgumentException::class.java) {
            DrumPattern(steps = invalidSteps)
        }

        assertEquals("Pattern must have exactly 8 steps", exception.message)
    }

    @Test
    fun `DrumPattern should throw exception when created with more than 8 steps`() {
        val invalidSteps = List(9) { PatternStep(null) }

        val exception = assertThrows(IllegalArgumentException::class.java) {
            DrumPattern(steps = invalidSteps)
        }

        assertEquals("Pattern must have exactly 8 steps", exception.message)
    }

    @Test
    fun `DrumPattern should accept exactly 8 steps`() {
        val validSteps = List(8) { PatternStep(MetronomeSoundEnum.CLASSIC, 1.0f) }

        val pattern = DrumPattern(steps = validSteps)

        assertEquals(8, pattern.steps.size)
    }

    @Test
    fun `DrumPattern default sound1 should be CLASSIC`() {
        val pattern = DrumPattern()

        assertEquals(MetronomeSoundEnum.CLASSIC, pattern.sound1)
    }

    @Test
    fun `DrumPattern default sound2 should be SNARE`() {
        val pattern = DrumPattern()

        assertEquals(MetronomeSoundEnum.SNARE, pattern.sound2)
    }

    @Test
    fun `DrumPattern should allow custom sounds`() {
        val pattern = DrumPattern(
            sound1 = MetronomeSoundEnum.KNOCK,
            sound2 = MetronomeSoundEnum.DRUMTR808
        )

        assertEquals(MetronomeSoundEnum.KNOCK, pattern.sound1)
        assertEquals(MetronomeSoundEnum.DRUMTR808, pattern.sound2)
    }

    @Test
    fun `PatternStep should allow null sound for rest`() {
        val step = PatternStep(sound = null)

        assertNull(step.sound)
    }

    @Test
    fun `PatternStep should have default volume of 1_0`() {
        val step = PatternStep(sound = MetronomeSoundEnum.CLASSIC)

        assertEquals(1.0f, step.volume, 0.001f)
    }

    @Test
    fun `PatternStep should allow custom volume`() {
        val step = PatternStep(sound = MetronomeSoundEnum.CLASSIC, volume = 0.5f)

        assertEquals(0.5f, step.volume, 0.001f)
    }

    @Test
    fun `PatternStep should store all sound types`() {
        MetronomeSoundEnum.entries.forEach { sound ->
            val step = PatternStep(sound = sound)
            assertEquals(sound, step.sound)
        }
    }

    @Test
    fun `DrumPattern copy should create new instance with updated steps`() {
        val originalPattern = DrumPattern()
        val newSteps = List(8) { PatternStep(MetronomeSoundEnum.SNARE, 0.8f) }

        val updatedPattern = originalPattern.copy(steps = newSteps)

        assertNotSame(originalPattern, updatedPattern)
        assertEquals(8, updatedPattern.steps.size)
        assertEquals(MetronomeSoundEnum.SNARE, updatedPattern.steps[0].sound)
        assertEquals(0.8f, updatedPattern.steps[0].volume, 0.001f)
    }

    @Test
    fun `DrumPattern with mixed steps should maintain order`() {
        val steps = listOf(
            PatternStep(MetronomeSoundEnum.CLASSIC, 1.0f),
            PatternStep(null, 1.0f), // rest
            PatternStep(MetronomeSoundEnum.SNARE, 0.7f),
            PatternStep(null, 1.0f), // rest
            PatternStep(MetronomeSoundEnum.CLASSIC, 1.0f),
            PatternStep(null, 1.0f), // rest
            PatternStep(MetronomeSoundEnum.SNARE, 0.9f),
            PatternStep(MetronomeSoundEnum.CLASSIC, 1.0f)
        )

        val pattern = DrumPattern(steps = steps)

        assertEquals(MetronomeSoundEnum.CLASSIC, pattern.steps[0].sound)
        assertNull(pattern.steps[1].sound)
        assertEquals(MetronomeSoundEnum.SNARE, pattern.steps[2].sound)
        assertEquals(0.7f, pattern.steps[2].volume, 0.001f)
    }
}

package com.electricbiro.runningmetronome.data.model

data class Preset(
    val id: String,
    val label: String,
    val bpm: Int,
)

enum class RunningLevel(
    val id: String,
    val displayLabel: String,
    val tag: String,
    val blurb: String,
    val presets: List<Preset>,
) {
    NEW(
        id = "new",
        displayLabel = "New to running",
        tag = "Building a base",
        blurb = "Just starting out or returning after a break.",
        presets = listOf(
            Preset("easy",   "Walk",   130),
            Preset("tempo",  "Easy",   150),
            Preset("race",   "Steady", 160),
            Preset("speed",  "Push",   165),
            Preset("fast",   "Hard",   170),
            Preset("sprint", "Sprint", 175),
        ),
    ),
    CASUAL(
        id = "casual",
        displayLabel = "Casual",
        tag = "3–5 km, weekly",
        blurb = "A few runs a week. Comfortable at conversation pace.",
        presets = listOf(
            Preset("easy",   "Easy",   160),
            Preset("tempo",  "Tempo",  168),
            Preset("race",   "Race",   175),
            Preset("speed",  "Speed",  180),
            Preset("fast",   "Fast",   185),
            Preset("sprint", "Sprint", 190),
        ),
    ),
    REGULAR(
        id = "regular",
        displayLabel = "Regular runner",
        tag = "20–40 km / week",
        blurb = "Logging consistent miles. Often training for events.",
        presets = listOf(
            Preset("easy",   "Easy",   165),
            Preset("tempo",  "Tempo",  175),
            Preset("race",   "Race",   180),
            Preset("speed",  "Speed",  185),
            Preset("fast",   "Fast",   190),
            Preset("sprint", "Sprint", 195),
        ),
    ),
    COMPETITIVE(
        id = "competitive",
        displayLabel = "Competitive",
        tag = "50+ km / week",
        blurb = "Sub-elite cadence. Intervals, tempo, race-pace work.",
        presets = listOf(
            Preset("easy",   "Recovery", 170),
            Preset("tempo",  "Tempo",    180),
            Preset("race",   "Race",     185),
            Preset("speed",  "Speed",    190),
            Preset("fast",   "Fast",     195),
            Preset("sprint", "Sprint",   200),
        ),
    );

    companion object {
        fun fromId(id: String): RunningLevel = entries.find { it.id == id } ?: CASUAL
    }
}

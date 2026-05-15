package com.electricbiro.runningmetronome.ui.viewmodel

import com.electricbiro.runningmetronome.data.model.RunningLevel
import com.electricbiro.runningmetronome.data.repository.PersistedSettings
import com.electricbiro.runningmetronome.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockRepository: SettingsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeViewModel(onboardingComplete: Boolean = false): OnboardingViewModel {
        whenever(mockRepository.settings).thenReturn(
            flowOf(PersistedSettings(onboardingComplete = onboardingComplete))
        )
        return OnboardingViewModel(mockRepository)
    }

    // Initial state

    @Test
    fun `starts at WELCOME step when onboarding not complete`() {
        val vm = makeViewModel(onboardingComplete = false)
        assertEquals(OnboardingStep.WELCOME, vm.state.value.step)
        assertFalse(vm.state.value.isComplete)
    }

    @Test
    fun `starts as complete when onboarding already done`() {
        val vm = makeViewModel(onboardingComplete = true)
        assertTrue(vm.state.value.isComplete)
    }

    @Test
    fun `initial selectedLevel is null`() {
        val vm = makeViewModel()
        assertNull(vm.state.value.selectedLevel)
    }

    // Navigation: forward

    @Test
    fun `goToLevelSelect advances to LEVEL_SELECT`() {
        val vm = makeViewModel()
        vm.goToLevelSelect()
        assertEquals(OnboardingStep.LEVEL_SELECT, vm.state.value.step)
    }

    @Test
    fun `goToPermission advances to PERMISSION`() {
        val vm = makeViewModel()
        vm.goToLevelSelect()
        vm.goToPermission()
        assertEquals(OnboardingStep.PERMISSION, vm.state.value.step)
    }

    @Test
    fun `finishPermissionStep advances to APP`() {
        val vm = makeViewModel()
        vm.goToLevelSelect()
        vm.selectLevel(RunningLevel.REGULAR)
        vm.goToPermission()
        vm.finishPermissionStep()
        assertEquals(OnboardingStep.APP, vm.state.value.step)
    }

    // Navigation: back

    @Test
    fun `goBack from LEVEL_SELECT returns to WELCOME`() {
        val vm = makeViewModel()
        vm.goToLevelSelect()
        vm.goBack()
        assertEquals(OnboardingStep.WELCOME, vm.state.value.step)
    }

    @Test
    fun `goBack from PERMISSION returns to LEVEL_SELECT`() {
        val vm = makeViewModel()
        vm.goToLevelSelect()
        vm.goToPermission()
        vm.goBack()
        assertEquals(OnboardingStep.LEVEL_SELECT, vm.state.value.step)
    }

    @Test
    fun `goBack from WELCOME has no effect`() {
        val vm = makeViewModel()
        vm.goBack()
        assertEquals(OnboardingStep.WELCOME, vm.state.value.step)
    }

    @Test
    fun `goBack from APP has no effect`() {
        val vm = makeViewModel()
        vm.goToLevelSelect()
        vm.goToPermission()
        vm.finishPermissionStep()
        vm.goBack()
        assertEquals(OnboardingStep.APP, vm.state.value.step)
    }

    // Level selection

    @Test
    fun `selectLevel stores chosen level`() {
        val vm = makeViewModel()
        vm.selectLevel(RunningLevel.COMPETITIVE)
        assertEquals(RunningLevel.COMPETITIVE, vm.state.value.selectedLevel)
    }

    @Test
    fun `selectLevel can be changed before proceeding`() {
        val vm = makeViewModel()
        vm.selectLevel(RunningLevel.NEW)
        vm.selectLevel(RunningLevel.REGULAR)
        assertEquals(RunningLevel.REGULAR, vm.state.value.selectedLevel)
    }

    // Finishing onboarding

    @Test
    fun `finishPermissionStep persists selected level`() = runTest {
        val vm = makeViewModel()
        vm.selectLevel(RunningLevel.COMPETITIVE)
        vm.finishPermissionStep()
        verify(mockRepository).completeOnboarding(RunningLevel.COMPETITIVE)
    }

    @Test
    fun `finishPermissionStep defaults to CASUAL when no level selected`() = runTest {
        val vm = makeViewModel()
        vm.finishPermissionStep()
        verify(mockRepository).completeOnboarding(RunningLevel.CASUAL)
    }

    @Test
    fun `finishPermissionStep sets tourStep to 0`() {
        val vm = makeViewModel()
        vm.finishPermissionStep()
        assertEquals(0, vm.state.value.tourStep)
    }

    // Skip

    @Test
    fun `skip marks onboarding complete immediately`() {
        val vm = makeViewModel()
        vm.skip()
        assertTrue(vm.state.value.isComplete)
    }

    @Test
    fun `skip persists CASUAL level`() = runTest {
        val vm = makeViewModel()
        vm.skip()
        verify(mockRepository).completeOnboarding(RunningLevel.CASUAL)
    }

    @Test
    fun `skip sets tourStep to -1`() {
        val vm = makeViewModel()
        vm.skip()
        assertEquals(-1, vm.state.value.tourStep)
    }

    // Coachmark tour

    @Test
    fun `nextTourStep advances from 0 to 1`() {
        val vm = makeViewModel()
        vm.finishPermissionStep()
        vm.nextTourStep()
        assertEquals(1, vm.state.value.tourStep)
    }

    @Test
    fun `nextTourStep advances from 1 to 2`() {
        val vm = makeViewModel()
        vm.finishPermissionStep()
        vm.nextTourStep()
        vm.nextTourStep()
        assertEquals(2, vm.state.value.tourStep)
    }

    @Test
    fun `nextTourStep after step 2 sets tourStep to -1 and isComplete`() {
        val vm = makeViewModel()
        vm.finishPermissionStep()
        vm.nextTourStep() // 0 → 1
        vm.nextTourStep() // 1 → 2
        vm.nextTourStep() // 2 → done

        assertEquals(-1, vm.state.value.tourStep)
        assertTrue(vm.state.value.isComplete)
    }

    // Reset onboarding

    @Test
    fun `resetOnboarding sets isComplete to false`() {
        val vm = makeViewModel(onboardingComplete = true)
        vm.resetOnboarding()
        assertFalse(vm.state.value.isComplete)
    }

    @Test
    fun `resetOnboarding navigates to LEVEL_SELECT`() {
        val vm = makeViewModel(onboardingComplete = true)
        vm.resetOnboarding()
        assertEquals(OnboardingStep.LEVEL_SELECT, vm.state.value.step)
    }

    @Test
    fun `resetOnboarding clears selectedLevel`() {
        val vm = makeViewModel()
        vm.selectLevel(RunningLevel.COMPETITIVE)
        vm.resetOnboarding()
        assertNull(vm.state.value.selectedLevel)
    }

    @Test
    fun `resetOnboarding calls repository resetOnboarding`() = runTest {
        val vm = makeViewModel()
        vm.resetOnboarding()
        verify(mockRepository).resetOnboarding()
    }
}

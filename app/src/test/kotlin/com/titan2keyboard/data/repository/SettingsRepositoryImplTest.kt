package com.titan2keyboard.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.titan2keyboard.domain.model.KeyboardSettings
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Example unit test for SettingsRepository
 * Demonstrates modern testing with JUnit 5, MockK, and Coroutines Test
 */
class SettingsRepositoryImplTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: SettingsRepositoryImpl

    @BeforeEach
    fun setup() {
        dataStore = mockk(relaxed = true)
        repository = SettingsRepositoryImpl(dataStore)
    }

    @Test
    fun `settingsFlow emits default settings when preferences are empty`() = runTest {
        // Given
        every { dataStore.data } returns flowOf(emptyPreferences())

        // When
        val settings = repository.settingsFlow.first()

        // Then
        val expected = KeyboardSettings(
            autoCapitalize = true,
            keyRepeatEnabled = true,
            longPressCapitalize = false,
            doubleSpacePeriod = true,
            keyRepeatDelay = 400L,
            keyRepeatRate = 50L
        )
        assertEquals(expected, settings)
    }
}

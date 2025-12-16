package com.pedromfmachado.sword.catz

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.pedromfmachado.sword.catz.core.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CatzE2ETest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = createDispatcher()
        mockWebServer.start(8080)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun tearDown() {
        scenario.close()
        mockWebServer.shutdown()
    }

    private fun createDispatcher(): Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when {
                request.path?.startsWith("/breeds") == true -> {
                    MockResponse()
                        .setResponseCode(200)
                        .setBody(readJsonFromAssets("breeds.json"))
                }
                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private fun readJsonFromAssets(fileName: String): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun waitForBreedsList() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasText("Abyssinian")).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun breedsListDisplaysOnLaunch() {
        // Wait for data to load
        waitForBreedsList()

        // Verify the breeds list is displayed with expected items from API
        composeTestRule.onNodeWithText("Abyssinian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bengal").assertIsDisplayed()
        composeTestRule.onNodeWithText("Persian").assertIsDisplayed()
    }

    @Test
    fun favoritingBreedPersistsAcrossScreens() {
        // Wait for the list to load
        waitForBreedsList()
        composeTestRule.onNodeWithText("Abyssinian").assertIsDisplayed()

        // Tap the favorite button for Abyssinian
        composeTestRule
            .onNodeWithContentDescription("Add Abyssinian to favorites")
            .performClick()

        // Navigate to Favorites tab
        composeTestRule.onNodeWithText("Favorites").performClick()

        // Verify Abyssinian appears in favorites (from Room database)
        composeTestRule.onNodeWithText("Abyssinian").assertIsDisplayed()

        // Verify non-favorited breeds are not shown
        composeTestRule.onNodeWithText("Bengal").assertDoesNotExist()
        composeTestRule.onNodeWithText("Persian").assertDoesNotExist()
    }

    @Test
    fun detailScreenDisplaysBreedInfo() {
        // Wait for the list to load
        waitForBreedsList()
        composeTestRule.onNodeWithText("Abyssinian").assertIsDisplayed()

        // Tap on the Abyssinian breed to open detail screen
        composeTestRule.onNodeWithText("Abyssinian").performClick()

        // Verify detail screen displays breed information (from Room cache)
        composeTestRule.onNodeWithText("Abyssinian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Egypt").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active, Energetic, Independent").assertIsDisplayed()
        composeTestRule.onNodeWithText("The Abyssinian is easy to care for").assertIsDisplayed()

        // Verify section labels are displayed
        composeTestRule.onNodeWithText("Origin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temperament").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lifespan").assertIsDisplayed()

        // Verify lifespan is displayed
        composeTestRule.onNodeWithText("14 - 15 years").assertIsDisplayed()
    }
}

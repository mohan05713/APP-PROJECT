package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.ui.EcoApartmentApp
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun app_launches_with_login_screen_displayed() {
        composeTestRule.setContent {
            MyApplicationTheme {
                EcoApartmentApp()
            }
        }

        // Verify that the login screen elements are present on launch
        composeTestRule.onNodeWithTag("tab_resident_role").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_authority_role").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_submit_button").assertExists()
    }
}

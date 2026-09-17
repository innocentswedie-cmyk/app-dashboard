package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.DailyResolutionTrend
import com.example.ui.components.AgentResolutionRateLineChartCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleTrends = listOf(
      DailyResolutionTrend("Mon", "Sep 11", 32, 30, 18, 4.1f, 22, 93.8f),
      DailyResolutionTrend("Tue", "Sep 12", 38, 36, 22, 3.6f, 26, 94.7f),
      DailyResolutionTrend("Wed", "Sep 13", 48, 45, 32, 2.9f, 33, 93.8f),
      DailyResolutionTrend("Thu", "Sep 14", 41, 39, 27, 3.2f, 29, 95.1f),
      DailyResolutionTrend("Fri", "Sep 15", 35, 34, 24, 2.5f, 25, 97.1f),
      DailyResolutionTrend("Sat", "Sep 16", 22, 21, 16, 3.8f, 15, 95.5f),
      DailyResolutionTrend("Sun", "Sep 17", 22, 20, 15, 3.7f, 14, 91.0f)
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        AgentResolutionRateLineChartCard(dailyTrends = sampleTrends)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}


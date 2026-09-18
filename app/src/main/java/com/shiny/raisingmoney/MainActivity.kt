package com.shiny.raisingmoney

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shiny.raisingmoney.core.designsystem.theme.RaisingMoneyTheme
import com.shiny.raisingmoney.core.designsystem.theme.ScreenBackground
import com.shiny.raisingmoney.feature.transaction.TransactionScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // NOTE: targetSdk 35+에서는 XML의 windowLightStatusBar가 무시되므로 enableEdgeToEdge()으로 light-mode로 강제함.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
        )
        setContent {
            RaisingMoneyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ScreenBackground,
                ) {
                    TransactionScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

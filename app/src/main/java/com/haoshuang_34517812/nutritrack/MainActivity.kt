package com.haoshuang_34517812.nutritrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.haoshuang_34517812.nutritrack.theme.NutriTrackTheme
import com.haoshuang_34517812.nutritrack.navigation.NutriTrackNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutriTrackTheme{
                NutriTrackNavHost()
            }
        }
    }
}

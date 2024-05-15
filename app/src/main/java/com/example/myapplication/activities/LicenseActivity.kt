package com.example.myapplication.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.myapplication.data.FishingSession
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.screens.LicenseScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class LicenseActivity : ComponentActivity() {
    private var fishingSessions: List<FishingSession>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbContext = FishingLicenseDbContext(this)
        fishingSessions = dbContext.getFishingSessions().sortedByDescending { it.date }

        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LicenseScreen(fishingSessions)
                }
            }
        }
    }
}
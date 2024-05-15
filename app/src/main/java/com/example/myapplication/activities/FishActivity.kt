package com.example.myapplication.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.FishType
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.screens.FishScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class FishActivity : ComponentActivity() {
    private lateinit var fishTypes: List<FishType>
    private var fishingSessionGuid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbContext = FishingLicenseDbContext(this)
        fishTypes = dbContext.getAllFishTypes().sortedBy { fishType: FishType -> fishType.type}
        fishingSessionGuid = intent.getStringExtra("FISHING_SESSION_GUID")

        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                    FishScreen(fishTypes, LocalContext.current, fishingSessionGuid)
                }
            }
        }
    }
}
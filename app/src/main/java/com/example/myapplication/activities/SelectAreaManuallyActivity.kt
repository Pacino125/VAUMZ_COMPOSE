package com.example.myapplication.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.myapplication.data.Area
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.screens.SelectAreaManuallyScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

@RequiresApi(Build.VERSION_CODES.O)
class SelectAreaManuallyActivity : ComponentActivity() {
    private var allAreas: List<Area>? = null
    private var areasInUserOrganization: List<Area>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbContext = FishingLicenseDbContext(this)
        allAreas = dbContext.getAllAreas()
        //areasInUserOrganization = dbContext.getAreasInUserOrganization(currentUser?.organizationGuid ?: "")

        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SelectAreaManuallyScreen(allAreas, areasInUserOrganization)
                }
            }
        }
    }
}
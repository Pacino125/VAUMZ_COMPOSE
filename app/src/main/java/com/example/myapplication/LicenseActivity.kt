package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.FishingSession
import com.example.myapplication.data.User
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.time.format.DateTimeFormatter

class LicenseActivity : ComponentActivity() {
    private var currentUser: User? = null
    private var fishingSessions: List<FishingSession>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbContext = FishingLicenseDbContext(this)
        currentUser = dbContext.getUserByGuid(intent.getStringExtra("USER_GUID"))
        fishingSessions = dbContext.getFishingSessions().sortedByDescending { it.date }

        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LicenseScreen(fishingSessions, currentUser!!)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LicenseScreen(fishingSessions: List<FishingSession>?, currentUser: User) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.license_my_license),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        FishingSessionsSection(fishingSessions)
        ActionButtons(LocalContext.current, fishingSessions, currentUser)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FishingSessionsSection(fishingSessions: List<FishingSession>?) {
    if (fishingSessions == null) return

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.license_date),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_area_number),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_area_name),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        fishingSessions.forEach { session ->
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(if (session.isActive) Color.Gray else Color.Transparent)
            ) {
                Text(
                    text = session.date.format(DateTimeFormatter.ofPattern("dd.MM")),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = session.areaId.areaId,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = session.areaId.name,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionButtons(context: Context, fishingSessions: List<FishingSession>?, currentUser: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(48.dp)
            ) {
                Text(text = stringResource(R.string.licenses_add_fish))
            }

            if (fishingSessions != null && fishingSessions.any { it.isActive }) {
                Button(
                    onClick = {
                        val activeSession = fishingSessions.firstOrNull { it.isActive }
                        activeSession?.let {
                            FishingLicenseDbContext(context).updateFishingSessionIsActiveToFalse(it.guid)
                        }
                        (context as? Activity)?.recreate()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(48.dp)
                ) {
                    Text(text = stringResource(R.string.licenses_end_fishing))
                }
            } else {
                Button(
                    onClick = {
                        val intent = Intent(context, SelectAreaManuallyActivity::class.java)
                        intent.putExtra("USER_GUID", currentUser.guid)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(text = stringResource(R.string.licenses_start_fishing))
                }
            }
        }
    }
}
package com.example.myapplication.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.activities.FishActivity
import com.example.myapplication.activities.SelectAreaManuallyActivity
import com.example.myapplication.data.FishingSession
import com.example.myapplication.database.FishingLicenseDbContext
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LicenseScreen(fishingSessions: List<FishingSession>?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.license_my_license),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FishingSessionsSection(fishingSessions)
        }

        ActionButtons(LocalContext.current, fishingSessions)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FishingSessionsSection(fishingSessions: List<FishingSession>?) {
    if (fishingSessions == null) return
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(Modifier.verticalScroll(scrollState))
    ) {
        //Header
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.license_date),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_area_number),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_area_name),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_fish_type),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_count),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_length),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.license_weight),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
        }

        //values under header
        fishingSessions.forEach { session ->
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(if (session.isActive) Color.Gray else Color.Transparent)
            ) {
                Text(
                    text = session.date.format(DateTimeFormatter.ofPattern("dd.MM")),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = session.areaId.areaId,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = session.areaId.name,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                // If you have active fishing session, then do not display values within catch columns
                // If fishing session is not active and catch doesn't have value, display ---
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        session.catchId?.fishType?.type ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        session.catchId?.fishCount?.toString() ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        session.catchId?.length?.toString() ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        session.catchId?.weight?.toString() ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionButtons(context: Context, fishingSessions: List<FishingSession>?) {
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
            //Button is disabled, when you don't have active fishing session
            // or when you have active session in area, which is chap (catch and release)
            Button(
                onClick = {
                    val intent = Intent(context, FishActivity::class.java)
                    intent.putExtra("FISHING_SESSION_GUID", fishingSessions!!.first { it.isActive }.guid)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(48.dp),
                enabled = fishingSessions?.any { it.isActive  && it.areaId.chap == false } ?: false
            ) {
                Text(text = stringResource(R.string.license_add_fish))
            }

            //If you have active fishing session, then display button for ending it
            // Otherwise display start fishing session button
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
                    Text(text = stringResource(R.string.license_end_fishing))
                }
            } else {
                Button(
                    onClick = {
                        val intent = Intent(context, SelectAreaManuallyActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(text = stringResource(R.string.license_start_fishing))
                }
            }
        }
    }
}
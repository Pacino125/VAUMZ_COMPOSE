package com.example.myapplication.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.entities.Area
import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishType
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.events.FishingSessionEvent
import com.example.myapplication.viewModels.LicenseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LicenseScreen(viewModel: LicenseViewModel = viewModel(), navigateToSelectArea: () -> Unit, navigateToFish: () -> Unit) {
    val fishingSessions by viewModel.fishingSessions.collectAsState()
    val mapForCatches by viewModel.mapCatchesToSessions.collectAsState()
    val mapForAreas by viewModel.mapAreasToSessions.collectAsState()
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

            FishingSessionsSection(fishingSessions, viewModel, mapForCatches, mapForAreas)
        }

        ActionButtons(LocalContext.current, fishingSessions, navigateToSelectArea, navigateToFish)
    }
}

@SuppressLint("StateFlowValueCalledInComposition", "SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FishingSessionsSection(fishingSessions: List<FishingSession>?, viewModel: LicenseViewModel, mapForCatches: Map<Int, Catch>, mapForAreas: Map<Int, Area>) {

    if (fishingSessions == null) return
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(Modifier.verticalScroll(scrollState))
    ) {
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

        fishingSessions.forEach { session ->
            val formattedDate = SimpleDateFormat("dd.MM").format(session.date)

            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(if (session.isActive) Color.Gray else Color.Transparent)
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = mapForAreas[session.id]?.areaId ?: "",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = mapForAreas[session.id]?.name ?: "",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        val fishType: MutableStateFlow<FishType?> = MutableStateFlow(null)
                        val realFishType = fishType.asStateFlow().value
                        mapForCatches[session.id]?.fishTypeId?.let { viewModel.viewModelScope.launch {
                            mapForCatches[session.id]?.let { it1 ->
                                viewModel.getFishType(it1.fishTypeId).collect{ collectedFishType -> fishType.update { collectedFishType }
                                }
                            }
                        }}
                        realFishType?.type ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        mapForCatches[session.id]?.fishCount?.toString() ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        mapForCatches[session.id]?.length?.toString() ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (session.isActive) {
                        ""
                    } else {
                        mapForCatches[session.id]?.weight?.toString() ?: "---"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ActionButtons(context: Context, fishingSessions: List<FishingSession>?, navigateToSelectArea: () -> Unit, navigateToFish: () -> Unit, viewModel: LicenseViewModel = viewModel()) {
    val buttonEnabled = remember { mutableStateOf(false) }

    val activeSession = fishingSessions?.firstOrNull { it.isActive }
    if (activeSession != null) {
        val area: MutableStateFlow<Area?> = MutableStateFlow(null)
        val realArea = area.asStateFlow().value
        activeSession.areaId.let { viewModel.viewModelScope.launch {
            viewModel.getArea(activeSession.areaId).collect{
                    collectedArea -> area.update { collectedArea }
            }}}
        buttonEnabled.value = realArea?.chap == true
    }
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
                onClick = {
                    navigateToFish()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(48.dp),
                enabled = buttonEnabled.value
            ) {
                Text(text = stringResource(R.string.license_add_fish))
            }

            if (fishingSessions != null && fishingSessions.any { it.isActive }) {
                Button(
                    onClick = {
                        activeSession?.let {
                            activeSession.isActive = false
                            viewModel.onEvent(FishingSessionEvent.UpsertFishingSession(activeSession))
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
                        navigateToSelectArea()
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
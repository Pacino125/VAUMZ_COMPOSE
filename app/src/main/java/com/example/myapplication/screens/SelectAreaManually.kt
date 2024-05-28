package com.example.myapplication.screens

import android.os.Build
import androidx.compose.ui.draw.clip
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.entities.Area
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.viewModels.AreaViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectAreaManuallyScreen(viewModel: AreaViewModel = viewModel(), navigateToLicense: () -> Unit) {
    val areas by viewModel.areas.collectAsState()
    val searchText = rememberSaveable { mutableStateOf("") }
    var filteredAreas = areas
    if (searchText.value.isNotEmpty()) {
        filteredAreas = (areas.filter { it.name.contains(searchText.value, ignoreCase = true) })
    }

    val selectedAreaIndex = rememberSaveable { mutableIntStateOf(-1) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.choose_area_text),
                modifier = Modifier.padding(bottom = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            SearchBar(searchText.value, onSearchTextChanged = { searchText.value = it })
            Text(
                text = stringResource(R.string.state_areas_text),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Table(filteredAreas, selectedAreaIndex, navigateToLicense= navigateToLicense)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Table(
    areas: List<Area>?,
    selectedAreaIndex: MutableState<Int>,
    viewModel: AreaViewModel = viewModel(),
    navigateToLicense: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TableRow(
                header = true,
                areaName = stringResource(R.string.select_area_area_name),
                areaId = stringResource(R.string.select_area_area_number),
                chap = stringResource(R.string.select_area_area_chap)
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            areas?.forEachIndexed { index, area ->
                TableRow(
                    header = false,
                    areaName = area.name,
                    areaId = area.areaId,
                    chap = if (area.chap == true) stringResource(R.string.yes) else stringResource(R.string.no),
                    selected = selectedAreaIndex.value == index
                ) {
                    if (selectedAreaIndex.value != index) {
                        selectedAreaIndex.value = index
                    } else {
                        val selectedArea = areas[selectedAreaIndex.value]
                        val session = FishingSession(
                            areaId = selectedArea.id,
                            date = System.currentTimeMillis(),
                            isActive = true,
                            catchId = null
                        )

                        viewModel.viewModelScope.launch {
                            viewModel.insertNewFishingSession(session)
                        }

                        navigateToLicense()
                    }
                }
            }
        }
    }
}

@Composable
fun TableRow(
    header: Boolean,
    areaName: String,
    areaId: String,
    chap: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .run {
                if (!header) {
                    clickable { onClick() }
                } else {
                    this
                }
            }
            .background(if (selected) Color.Gray else Color.Transparent)
    ) {
        var background = Color(0xFF006400)
        if (chap == "Áno") {
            background = Color.Red
        } else if (header) {
            background = Color.Transparent
        }
        Text(
            text = areaName,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = areaId,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .weight(0.5f)
                .clip(RoundedCornerShape(8.dp))
                .background(background)
                .padding(8.dp)
        ) {
            Text(
                text = chap,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
        placeholder = { Text(stringResource(R.string.find_area), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
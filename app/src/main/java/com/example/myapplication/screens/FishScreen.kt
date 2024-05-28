package com.example.myapplication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishType
import com.example.myapplication.viewModels.CatchViewModel
import kotlinx.coroutines.launch

@Composable
fun FishScreen(viewModel: CatchViewModel = viewModel(), navigateToLicense: () -> Unit) {
    val defaultFishType = FishType(type = stringResource(R.string.select_fish), id = 0, maxCatchLength = null, minCatchLength = null)
    val fishTypes by viewModel.fishTypes.collectAsState()
    val selectedFishTypeId = rememberSaveable { mutableIntStateOf(0) }
    val selectedFishType = remember { mutableStateOf(fishTypes.find { it.id == selectedFishTypeId.intValue } ?: defaultFishType) }
    val expanded = rememberSaveable { mutableStateOf(false) }
    val countValue = rememberSaveable { mutableStateOf("") }
    val lengthValue = rememberSaveable { mutableStateOf("") }
    val weightValue = rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FishDropdown(selectedFishTypeId, selectedFishType, countValue, lengthValue, weightValue , expanded, fishTypes)
        Spacer(modifier = Modifier.height(16.dp))
        FishTextField(
            value = countValue.value,
            onValueChange = { countValue.value = it },
            label = stringResource(R.string.fish_count),
            enabled = selectedFishType.value.type in listOf(
                stringResource(R.string.fish_another_type),
                stringResource(R.string.fish_white_fish)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        FishTextField(
            value = lengthValue.value,
            onValueChange = { lengthValue.value = it },
            label = stringResource(R.string.fish_length),
            enabled = selectedFishType.value.type !in listOf(
                stringResource(R.string.fish_another_type),
                stringResource(R.string.fish_white_fish)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        FishTextField(
            value = weightValue.value,
            onValueChange = { weightValue.value = it },
            label = stringResource(R.string.fish_weight)
        )
        Spacer(modifier = Modifier.height(16.dp))
        FishButtons(viewModel, navigateToLicense, selectedFishType.value, countValue.value, lengthValue.value, weightValue.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishDropdown(
    selectedFishTypeId: MutableState<Int>,
    selectedFishType: MutableState<FishType>,
    selectedCount: MutableState<String>,
    selectedLength: MutableState<String>,
    selectedWeight: MutableState<String>,
    expanded: MutableState<Boolean>,
    fishTypes: List<FishType>
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            TextField(
                value = TextFieldValue(selectedFishType.value.type),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                fishTypes.forEach { fishType ->
                    DropdownMenuItem(
                        text = { Text(text = fishType.type) },
                        onClick = {
                            selectedFishTypeId.value = fishType.id
                            selectedFishType.value = fishType
                            selectedCount.value = ""
                            selectedLength.value = ""
                            selectedWeight.value = ""
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FishTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        singleLine = true,
        enabled = enabled
    )
}

@Composable
fun FishButtons(
    viewModel: CatchViewModel,
    navigateToLicense: () -> Unit,
    selectedFishType: FishType,
    countValue: String,
    lengthValue: String,
    weightValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FishButton(
            onClick = {
                navigateToLicense()
            },
            text = stringResource(R.string.fish_cancel),
            shouldBeEnabled = true
        )
        FishButton(
            onClick = {
                val catch = createCatch(selectedFishType, countValue, lengthValue, weightValue)
                viewModel.viewModelScope.launch {
                    viewModel.getHighestId().collect {
                        if (it == null) {
                            catch.id = 0
                        } else {
                            catch.id = it + 1
                        }
                        viewModel.insertNewCatch(catch)
                        viewModel.getActiveSession().collect { session ->
                            val copy = session.copy()
                            copy.isActive = false
                            copy.catchId = catch.id
                            viewModel.updateCurrentSession(copy)
                        }
                    }
                }

                navigateToLicense()
            },
            text = stringResource(R.string.fish_add_to_license),
            shouldBeEnabled =
            if (((selectedFishType.type == stringResource(R.string.fish_white_fish)
                        || selectedFishType.type == stringResource(R.string.fish_another_type))
                        && countValue.toIntOrNull() != null && weightValue.toDoubleOrNull() != null)) {
                true
            } else if (lengthValue.toIntOrNull() != null && weightValue.toDoubleOrNull() != null) {
                true
            } else if (selectedFishType.type == stringResource(R.string.select_fish)) {
                false
            }
            else {
                false
            }
        )
    }
}

@Composable
fun FishButton(onClick: () -> Unit, text: String, shouldBeEnabled: Boolean) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height(48.dp),
        enabled = shouldBeEnabled
    ) {
        Text(text = text)
    }
}

fun createCatch(selectedFishType: FishType, countValue: String, lengthValue: String, weightValue: String): Catch {
    return Catch(
        fishType = selectedFishType.type,
        fishCount = countValue.toIntOrNull() ?: 1,
        length = lengthValue.toIntOrNull(),
        weight = weightValue.toDoubleOrNull() ?: 0.0
    )
}
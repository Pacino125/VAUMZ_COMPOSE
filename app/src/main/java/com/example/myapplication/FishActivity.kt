package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.FishType
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.ui.theme.MyApplicationTheme

class FishActivity : ComponentActivity() {
    private lateinit var fishTypes: List<FishType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbContext = FishingLicenseDbContext(this)
        fishTypes = dbContext.getAllFishTypes().sortedBy { fishType: FishType -> fishType.type}

        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                    FishScreen(fishTypes)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishScreen(fishTypes: List<FishType>) {
    val selectedFishTypeId = rememberSaveable { mutableStateOf(fishTypes[0].guid) }
    var selectedFishType by remember { mutableStateOf(fishTypes.find { it.guid == selectedFishTypeId.value } ?: fishTypes[0]) }
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
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = !expanded.value }
            ) {
                TextField(
                    value = TextFieldValue(selectedFishType.type),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    fishTypes.forEach {
                        DropdownMenuItem(
                            text = { Text(text = it.type) },
                            onClick = {
                                selectedFishTypeId.value = it.guid
                                selectedFishType = it
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = countValue.value,
            onValueChange = { countValue.value = it },
            label = { Text(stringResource(R.string.fish_count)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            singleLine = true,
            enabled = selectedFishType.type in listOf(stringResource(R.string.fish_another_type),
                stringResource(
                    R.string.fish_white_fish
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = lengthValue.value,
            onValueChange = { lengthValue.value = it },
            label = { Text(stringResource(R.string.fish_length)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            singleLine = true,
            enabled = selectedFishType.type !in listOf(stringResource(R.string.fish_another_type),
                stringResource(
                    R.string.fish_white_fish
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = weightValue.value,
            onValueChange = { weightValue.value = it },
            label = { Text(stringResource(R.string.fish_weight)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            singleLine = true
        )
    }
}
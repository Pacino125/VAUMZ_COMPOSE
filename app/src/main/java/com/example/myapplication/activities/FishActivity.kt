package com.example.myapplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.Catch
import com.example.myapplication.data.FishType
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.UUID

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

@Composable
fun FishScreen(fishTypes: List<FishType>, context: Context, fishingSessionGuid: String?) {
    val selectedFishTypeId = rememberSaveable { mutableStateOf(fishTypes[0].guid) }
    val selectedFishType = remember { mutableStateOf(fishTypes.find { it.guid == selectedFishTypeId.value } ?: fishTypes[0]) }
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
        FishButtons(context, fishingSessionGuid, selectedFishType.value, countValue.value, lengthValue.value, weightValue.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishDropdown(
    selectedFishTypeId: MutableState<String>,
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
                        //reset all values in textfields after selecting another type of fish
                        onClick = {
                            selectedFishTypeId.value = fishType.guid
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
    context: Context,
    fishingSessionGuid: String?,
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
                val intent = Intent(context, LicenseActivity::class.java)
                context.startActivity(intent)
            },
            text = stringResource(R.string.fish_cancel),
            shouldBeEnabled = true
        )
        FishButton(
            onClick = {
                //Create new catch in tbl_catch and assign him to current fishing session.
                //End current fishing session.
                val catch = createCatch(selectedFishType, countValue, lengthValue, weightValue)
                FishingLicenseDbContext(context).addCatch(catch, fishingSessionGuid!!)
                val intent = Intent(context, LicenseActivity::class.java)
                context.startActivity(intent)
            },
            text = stringResource(R.string.fish_add_to_license),
            //If type of fish is white fish or other, then you need to have values in count and weight
            //When you have other types, you need to have values in length and weight
            shouldBeEnabled =
                if (((selectedFishType.type == stringResource(R.string.fish_white_fish)
                    || selectedFishType.type == stringResource(R.string.fish_another_type))
                    && countValue.toIntOrNull() != null && weightValue.toDoubleOrNull() != null)) {
                    true
                } else if (lengthValue.toIntOrNull() != null && weightValue.toDoubleOrNull() != null) {
                    true
                } else {
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
        UUID.randomUUID().toString(),
        selectedFishType,
        countValue.toIntOrNull() ?: 1,
        lengthValue.toIntOrNull(),
        weightValue.toDoubleOrNull() ?: 0.0
    )
}
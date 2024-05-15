package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.data.FishType
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.ui.theme.MyApplicationTheme

class FishActivity : ComponentActivity() {
    private lateinit var fishTypes: List<FishType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbContext = FishingLicenseDbContext(this)
        fishTypes = dbContext.getAllFishTypes()

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
    val selectedFishType = remember { mutableStateOf(fishTypes[0]) }
    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value}
        ) {
            TextField(
                value = selectedFishType.value.type,
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
                            selectedFishType.value = it
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}
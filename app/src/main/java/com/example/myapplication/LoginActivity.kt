package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text(stringResource(R.string.login_email)) }
            )
            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text(stringResource(R.string.login_password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                onClick = {
                    authenticate(context, emailState.value, passwordState.value)
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.login_login))
            }
        }
    }
}

fun authenticate(context: android.content.Context, email: String, password: String) {
    val dbContext = FishingLicenseDbContext(context)
    val user = dbContext.getUserByEmail(email)
    if (user != null && user.password == password) {
        val intent = Intent(context, LicenseActivity::class.java)
        intent.putExtra("USER_GUID", user.guid)
        context.startActivity(intent)

        (context as? Activity)?.finish()
    } else {
        Toast.makeText(context, "Nesprávny email alebo heslo", Toast.LENGTH_SHORT).show()
    }
}
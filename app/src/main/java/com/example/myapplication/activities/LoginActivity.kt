package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.uiComponents.ClickableText

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.login_email)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.login_password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                authenticate(context, email, password)
            }) {
                Text(stringResource(R.string.login_login))
            }
            Spacer(modifier = Modifier.height(16.dp))
            ClickableText(
                text = stringResource(R.string.login_registration),
                onClick = {
                    navigateToRegister(context)
                }
            )
        }
    }
}

fun authenticate(context: android.content.Context, email: String, password: String) {
    val dbContext = FishingLicenseDbContext(context)
    val user = dbContext.getUserByEmail(email)
    if (user != null && user.password == password) {
        val intent = Intent(context, LicenseActivity::class.java)
        context.startActivity(intent)
        (context as? Activity)?.finish()
    } else {
        Toast.makeText(context, context.getString(R.string.login_invalid_email_password), Toast.LENGTH_SHORT).show()
    }
}

fun navigateToRegister(context: android.content.Context) {
    val intent = Intent(context, RegisterActivity::class.java)
    context.startActivity(intent)
}
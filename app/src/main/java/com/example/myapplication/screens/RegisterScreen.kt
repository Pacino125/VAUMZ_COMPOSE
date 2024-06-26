package com.example.myapplication.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.activities.LoginActivity
import com.example.myapplication.data.User
import com.example.myapplication.database.FishingLicenseDbContext
import com.example.myapplication.uiComponents.ClickableText
import java.util.UUID

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var fullname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(Modifier.verticalScroll(scrollState)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.register_name)) }
        )

        TextField(
            value = fullname,
            onValueChange = { fullname = it },
            label = { Text(stringResource(R.string.register_fullname)) },
            modifier = Modifier.padding(top = 16.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.register_email)) },
            modifier = Modifier.padding(top = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        TextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text(stringResource(R.string.register_date)) },
            modifier = Modifier.padding(top = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.register_address)) },
            modifier = Modifier.padding(top = 16.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.register_password)) },
            modifier = Modifier.padding(top = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.register_confirm_password)) },
            modifier = Modifier.padding(top = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                register(
                    context,
                    name,
                    fullname,
                    email,
                    dateOfBirth,
                    address,
                    password,
                    confirmPassword
                )
            },
            enabled = name.isNotBlank()
                    && fullname.isNotBlank()
                    && email.isNotBlank()
                    && dateOfBirth.isNotBlank()
                    && address.isNotBlank()
                    && password.isNotBlank()
                    && confirmPassword.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.register_register_button))
        }

        ClickableText(
            text = stringResource(R.string.register_login),
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                navigateToLogin(context)
            }
        )
    }
}

private fun register(
    context: android.content.Context,
    name: String,
    fullname: String,
    email: String,
    dateOfBirth: String,
    address: String,
    password: String,
    confirmPassword: String
) {
    if (!email.contains("@")) {
        showError(context, context.getString(R.string.error_email_at))
        return
    }

    val dbHelper = FishingLicenseDbContext(context)
    val existingUser = dbHelper.getUserByEmail(email)
    if (existingUser != null) {
        showError(context, context.getString(R.string.error_user_exists))
        return
    }

    if (password != confirmPassword) {
        showError(context, context.getString(R.string.error_different_passwords))
        return
    }

    val user = User(
        guid = UUID.randomUUID().toString(),
        email = email,
        name = name,
        fullname = fullname,
        password = password,
        organizationGuid = null,
        child = false,
        birth = dateOfBirth,
        address = address,
        memberYear = null,
        number = null
    )

    dbHelper.insertUser(user)

    val intent = Intent(context, LoginActivity::class.java)
    context.startActivity(intent)
    (context as? Activity)?.finish()
}

private fun navigateToLogin(context: android.content.Context) {
    val intent = Intent(context, LoginActivity::class.java)
    context.startActivity(intent)
}

private fun showError(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
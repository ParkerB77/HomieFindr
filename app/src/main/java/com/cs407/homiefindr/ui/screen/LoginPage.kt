package com.cs407.homiefindr.ui.screen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs407.homiefindr.R
import com.cs407.homiefindr.data.auth.*
import com.cs407.homiefindr.data.auth.UserState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

//Create composables for ErrorText, userEmail, userPassword, and LogInSignUpButton
//Handle onclick function for LogInSignUpButton
@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (error != null)
        Text(text = error, color = Color.Red, textAlign = TextAlign.Center,modifier = modifier.fillMaxWidth())
}

@Composable
fun userEmail(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = R.string.enter_email)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = modifier.fillMaxWidth(0.8f)
    )
}

@Composable
fun userPassword(value: String,
                 onValueChange: (String) -> Unit,
                 modifier: Modifier = Modifier){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = R.string.enter_password)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier.fillMaxWidth(0.8f)
    )

}

@Composable
fun LogInSignUpButton(
    email: String,
    password: String,
    onSuccess: (UserState) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val res = LocalContext.current.resources

    Button(
        onClick = {

            when (checkEmail(email.trim())) {
                EmailResult.Empty -> {
                    onError(res.getString(R.string.email_is_empty)); return@Button
                }
                EmailResult.Invalid -> {
                    onError(res.getString(R.string.invalid_email_format)); return@Button
                }
                EmailResult.Valid -> { /* 继续校验密码 */ }
            }

            when (checkPassword(password)) {
                PasswordResult.Empty ->
                    onError(res.getString(R.string.password_is_empty))
                PasswordResult.Short ->
                    onError(res.getString(R.string.password_is_too_short))
                PasswordResult.Invalid ->
                    onError(res.getString(R.string.password_requirements))
                PasswordResult.Valid -> {

                    signIn(
                        email = email.trim(),
                        password = password,
                        onOk = {
                            val u = Firebase.auth.currentUser
                            val state = UserState(
                                id = 0,
                                name = u?.displayName.orEmpty(),   // 👈 use Firebase displayName (may be "")
                                uid = u?.uid.orEmpty()
                            )
                            onSuccess(state)
                        },
                        onErr = { msg -> onError(msg) }
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(50.dp)
    ) {
        Text(text = stringResource(id = R.string.login_or_signup))
    }
}



@Composable
fun LoginPage(

    modifier: Modifier = Modifier,
    loginButtonClick: (UserState) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val res = context.resources


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        userEmail(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(12.dp))

        userPassword(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(8.dp))

        ErrorText(error = error)

        Spacer(modifier = Modifier.height(8.dp))

        LogInSignUpButton(
            email = email,
            password = password,
            onSuccess = { userState ->
                error = null
                loginButtonClick(userState)
            },
            onError = { msg ->
                error = msg
            }
        )
    }
}
package com.haoshuang_34517812.nutritrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haoshuang_34517812.nutritrack.R
import com.haoshuang_34517812.nutritrack.theme.LightGreen
import com.haoshuang_34517812.nutritrack.theme.MediumGreen
import com.haoshuang_34517812.nutritrack.ui.components.ClinicianIdDropdown
import com.haoshuang_34517812.nutritrack.ui.components.MyTextField
import com.haoshuang_34517812.nutritrack.util.getGreetings
import com.haoshuang_34517812.nutritrack.viewmodel.FieldValidationState
import com.haoshuang_34517812.nutritrack.viewmodel.LoginState
import com.haoshuang_34517812.nutritrack.viewmodel.LoginViewModel
import java.util.Calendar
import kotlinx.coroutines.launch

/**
 * Login screen for user authentication with adaptive layout for different orientations
 *
 * @param viewModel The login view model
 * @param modifier Modifier for the screen
 * @param onLoginSuccess Callback for successful login
 * @param onGoRegister Callback to navigate to registration
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onLoginSuccess: (String) -> Unit,
    onGoRegister: () -> Unit,
) {
    // Collect state from ViewModel
    val loginState by viewModel.loginState.observeAsState(LoginState.Initial)
    val registeredIds by viewModel.registeredIds.collectAsState()
    val expanded by viewModel.isDropdownExpanded.observeAsState(false)
    val clinicianId by viewModel.clinicianId.observeAsState("")
    val errorMsg by viewModel.errorMessage.observeAsState()
    val formState by viewModel.formState.collectAsState()

    // Coroutine scope for password verification
    val coroutineScope = rememberCoroutineScope()

    // Input field state
    val passwordFieldState = remember { TextFieldState() }

    // Password visibility from StateFlow
    val passwordVisible by viewModel.passwordVisible.collectAsState()

    // Password change dialog state
    var showPasswordChangeDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var isResetPassword by remember { mutableStateOf(false) }

    // Password change fields
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var oldPasswordError by remember { mutableStateOf<String?>(null) }

    // Generate appropriate greeting based on time of day
    val greeting = remember {
        getGreetings(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
    }

    // Check current screen orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val verifyIdSelected: () -> Boolean = {
        if (clinicianId.isBlank()) {
            viewModel.setErrorWithTimeout("Please select a Clinician ID first")
            false
        } else {
            true
        }
    }

    val handleForgotPasswordClick: () -> Unit = {
        if (verifyIdSelected()) {
            dialogTitle = "Reset Password"
            isResetPassword = true
            showPasswordChangeDialog = true
        }
    }

    val handleChangePasswordClick: () -> Unit = {
        if (verifyIdSelected()) {
            dialogTitle = "Change Password"
            isResetPassword = false
            showPasswordChangeDialog = true
        }
    }

    // Update password in ViewModel when text field changes
    LaunchedEffect(passwordFieldState.text) {
        viewModel.updatePassword(passwordFieldState.text.toString())
    }

    // Handle login state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                onLoginSuccess((loginState as LoginState.Success).userId)
            }
            is LoginState.NeedsRegistration -> {
                onGoRegister()
            }
            else -> {} // Other states are handled in the UI
        }
    }

    // Render different layouts based on orientation
    if (isLandscape) {
        LandscapeLoginLayout(
            modifier = modifier,
            greeting = greeting,
            passwordFieldState = passwordFieldState,
            passwordVisible = passwordVisible,
            registeredIds = registeredIds,
            clinicianId = clinicianId,
            expanded = expanded,
            errorMsg = errorMsg,
            loginState = loginState,
            onExpandedChange = viewModel::setDropdownExpanded,
            onIdSelected = viewModel::selectClinicianId,
            onPasswordVisibilityToggle = viewModel::togglePasswordVisibility,
            onLoginClick = viewModel::login,
            onRegisterClick = {
                viewModel.navigateToRegister()
                onGoRegister()
            },
            onForgotPasswordClick = handleForgotPasswordClick,
            onChangePasswordClick = handleChangePasswordClick
        )
    } else {
        PortraitLoginLayout(
            modifier = modifier,
            greeting = greeting,
            passwordFieldState = passwordFieldState,
            passwordVisible = passwordVisible,
            registeredIds = registeredIds,
            clinicianId = clinicianId,
            expanded = expanded,
            errorMsg = errorMsg,
            loginState = loginState,
            onExpandedChange = viewModel::setDropdownExpanded,
            onIdSelected = viewModel::selectClinicianId,
            onPasswordVisibilityToggle = viewModel::togglePasswordVisibility,
            onLoginClick = viewModel::login,
            onRegisterClick = {
                viewModel.navigateToRegister()
                onGoRegister()
            },
            onForgotPasswordClick = handleForgotPasswordClick,
            onChangePasswordClick = handleChangePasswordClick
        )
    }

    // Password change dialog
    if (showPasswordChangeDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordChangeDialog = false
                oldPassword = ""
                newPassword = ""
                confirmNewPassword = ""
                passwordError = null
                oldPasswordError = null
            },
            title = {
                Text(
                    text = dialogTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins))
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Only show old password field for Change Password, not for Reset Password
                    if (!isResetPassword) {
                        // Old password field
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = {
                                oldPassword = it
                                oldPasswordError = null // Clear error when typing
                            },
                            label = { Text("Current Password") },
                            placeholder = { Text("Enter current password") },
                            singleLine = true,
                            isError = oldPasswordError != null,
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (oldPasswordError != null) {
                            Text(
                                text = oldPasswordError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // New password field
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            // Use ViewModel to update and validate the password
                            viewModel.updatePassword(it)

                            // Check password validation from ViewModel
                            passwordError = when (val validation = formState.passwordValidation) {
                                is FieldValidationState.Invalid -> validation.message
                                is FieldValidationState.Empty -> "Password cannot be empty"
                                else -> if (confirmNewPassword.isNotBlank() && it != confirmNewPassword)
                                    "Passwords do not match" else null
                            }
                        },
                        label = { Text("New Password") },
                        placeholder = { Text("Enter new password") },
                        singleLine = true,
                        isError = passwordError != null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Confirm password field
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = {
                            confirmNewPassword = it
                            // Check if passwords match
                            passwordError = if (it.isNotBlank() && it != newPassword)
                                "Passwords do not match" else null
                        },
                        label = { Text("Confirm Password") },
                        placeholder = { Text("Confirm new password") },
                        singleLine = true,
                        isError = passwordError != null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Show error if any
                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (!isResetPassword) {
                                // Verify old password first for Change Password flow
                                val passwordMatches = viewModel.verifyPassword(oldPassword)

                                if (!passwordMatches) {
                                    oldPasswordError = "Current password is incorrect"
                                    return@launch
                                }
                            }

                            // Check if passwords match for new password validation
                            if (newPassword != confirmNewPassword) {
                                passwordError = "Passwords do not match"
                                return@launch
                            }

                            // Check viewModel's password validation
                            val passwordValidation = formState.passwordValidation
                            if (passwordValidation is FieldValidationState.Invalid) {
                                passwordError = passwordValidation.message
                                return@launch
                            } else if (passwordValidation is FieldValidationState.Empty) {
                                passwordError = "Password cannot be empty"
                                return@launch
                            }

                            // All validations passed, update password
                            viewModel.updateUserInfo(newPassword)
                            showPasswordChangeDialog = false
                            oldPassword = ""
                            newPassword = ""
                            confirmNewPassword = ""
                            oldPasswordError = null
                            passwordError = null
                        }
                    },
                    enabled = (isResetPassword || oldPassword.isNotEmpty()) &&
                            newPassword.isNotEmpty() &&
                            confirmNewPassword.isNotEmpty() &&
                            passwordError == null
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPasswordChangeDialog = false
                        oldPassword = ""
                        newPassword = ""
                        confirmNewPassword = ""
                        passwordError = null
                        oldPasswordError = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Portrait layout for the login screen - vertical arrangement
 */
@Composable
private fun PortraitLoginLayout(
    modifier: Modifier,
    greeting: String,
    passwordFieldState: TextFieldState,
    passwordVisible: Boolean,
    registeredIds: List<String>,
    clinicianId: String,
    expanded: Boolean,
    errorMsg: String?,
    loginState: LoginState,
    onExpandedChange: (Boolean) -> Unit,
    onIdSelected: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    // Create scroll state for keyboard handling
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 33.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
    ) {
        // Header section with greeting
        LoginHeader(greeting)

        // Login form section
        LoginForm(
            passwordFieldState = passwordFieldState,
            passwordVisible = passwordVisible,
            registeredIds = registeredIds,
            clinicianId = clinicianId,
            expanded = expanded,
            errorMsg = errorMsg,
            loginState = loginState,
            onExpandedChange = onExpandedChange,
            onIdSelected = onIdSelected,
            onPasswordVisibilityToggle = onPasswordVisibilityToggle,
            onLoginClick = onLoginClick,
            isLandscape = false
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Registration link section
        RegistrationLink(onRegisterClick = onRegisterClick)

        Spacer(modifier = Modifier.height(16.dp))

        // Password options (moved here)
        PasswordOptionsVertical(
            onForgotPasswordClick = onForgotPasswordClick,
            onChangePasswordClick = onChangePasswordClick
        )
    }
}

/**
 * Landscape layout for the login screen - horizontal arrangement
 */
@Composable
private fun LandscapeLoginLayout(
    modifier: Modifier,
    greeting: String,
    passwordFieldState: TextFieldState,
    passwordVisible: Boolean,
    registeredIds: List<String>,
    clinicianId: String,
    expanded: Boolean,
    errorMsg: String?,
    loginState: LoginState,
    onExpandedChange: (Boolean) -> Unit,
    onIdSelected: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Greeting
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Sign in title
            Text(
                text = "Sign in",
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Greeting text
            Text(
                text = "$greeting, good to see you again",
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        // Right side: Login form
        Column(
            modifier = Modifier
                .weight(1.2f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Compact login form for landscape
            LoginForm(
                passwordFieldState = passwordFieldState,
                passwordVisible = passwordVisible,
                registeredIds = registeredIds,
                clinicianId = clinicianId,
                expanded = expanded,
                errorMsg = errorMsg,
                loginState = loginState,
                onExpandedChange = onExpandedChange,
                onIdSelected = onIdSelected,
                onPasswordVisibilityToggle = onPasswordVisibilityToggle,
                onLoginClick = onLoginClick,
                isLandscape = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Registration link
            RegistrationLink(onRegisterClick = onRegisterClick)

            Spacer(modifier = Modifier.height(8.dp))

            // Password options (moved here)
            PasswordOptionsVertical(
                onForgotPasswordClick = onForgotPasswordClick,
                onChangePasswordClick = onChangePasswordClick
            )
        }
    }
}

/**
 * Header section of the login screen with greeting
 * Only used in portrait mode
 */
@Composable
private fun LoginHeader(greeting: String) {
    Column {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Sign in",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "$greeting, good to see you again",
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Login form section with input fields and login button
 * Adapts its layout based on screen orientation
 */
@Composable
private fun LoginForm(
    passwordFieldState: TextFieldState,
    passwordVisible: Boolean,
    registeredIds: List<String>,
    clinicianId: String,
    expanded: Boolean,
    errorMsg: String?,
    loginState: LoginState,
    onExpandedChange: (Boolean) -> Unit,
    onIdSelected: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
    isLandscape: Boolean = false
) {
    // Adjust form height based on orientation
    val formHeight = if (isLandscape) 320.dp else 400.dp
    val verticalPadding = if (isLandscape) 20.dp else 24.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(formHeight),
        color = LightGreen,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = verticalPadding),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // ID Dropdown selector
            ClinicianIdDropdown(
                ids = registeredIds + listOf(stringResource(R.string.loginScreen_dropdownMenuMsg)),
                selectedId = clinicianId,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                onSelected = {
                    if (it != "Only registered IDs are shown here") {
                        onIdSelected(it)
                    }
                },
                disabledItems = setOf(stringResource(R.string.loginScreen_dropdownMenuMsg)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 10.dp))

            // Password input field
            MyTextField(
                textFieldState = passwordFieldState,
                hint = "Password",
                leadingIcon = Icons.Outlined.Lock,
                trailingIconRes = R.drawable.blind_4007613,
                onTrailingClick = onPasswordVisibilityToggle,
                isPassword = true,
                passwordVisible = passwordVisible,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 10.dp))

            // Error message container with adjusted height for landscape
            ErrorMessageBox(
                errorState = loginState,
                errorMessage = errorMsg,
                isLandscape = isLandscape
            )

            // Login button
            LoginButton(
                loginState = loginState,
                onClick = onLoginClick,
                isLandscape = isLandscape
            )
        }
    }
}

/**
 * Vertical password options with Forgot Password and Change Password links
 */
@Composable
private fun PasswordOptionsVertical(
    onForgotPasswordClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Forgot Password?",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onForgotPasswordClick)
                .padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Change Password",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onChangePasswordClick)
                .padding(vertical = 4.dp)
        )
    }
}

/**
 * Error message display box
 * Adapts height based on orientation
 */
@Composable
private fun ErrorMessageBox(
    errorState: LoginState,
    errorMessage: String?,
    isLandscape: Boolean = false
) {
    // Reduce height in landscape to save space
    val boxHeight = if (isLandscape) 28.dp else 36.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight),
        contentAlignment = Alignment.Center
    ) {
        val displayError = when {
            errorState is LoginState.Error -> errorState.message
            !errorMessage.isNullOrEmpty() -> errorMessage
            else -> null
        }

        if (displayError != null) {
            Text(
                text = displayError,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                fontSize = if (isLandscape) 13.sp else 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Login button component
 * Adapts size based on orientation
 */
@Composable
private fun LoginButton(
    loginState: LoginState,
    onClick: () -> Unit,
    isLandscape: Boolean = false
) {
    // Slightly smaller button in landscape
    val buttonHeight = if (isLandscape) 48.dp else 56.dp

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MediumGreen,
            contentColor = Color.White,
        ),
        enabled = loginState !is LoginState.Authenticating
    ) {
        if (loginState is LoginState.Authenticating) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Login",
                fontSize = if (isLandscape) 16.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.monaco)),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Registration link component
 * Consistent across both orientations
 */
@Composable
private fun RegistrationLink(
    onRegisterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Don't have an account? ",
            fontSize = 16.sp,
        )
        Text(
            text = "Register",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onRegisterClick)
        )
    }
}
package com.haoshuang_34517812.nutritrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.haoshuang_34517812.nutritrack.viewmodel.FieldValidationState
import com.haoshuang_34517812.nutritrack.viewmodel.LoginViewModel
import com.haoshuang_34517812.nutritrack.viewmodel.RegisterFormState
import com.haoshuang_34517812.nutritrack.viewmodel.RegistrationState
import kotlinx.coroutines.delay

/**
 * Registration screen for new users with adaptive layout for different orientations
 *
 * @param viewModel The login view model shared with LoginScreen
 * @param modifier Modifier for the screen
 * @param onRegisterSuccess Callback for successful registration
 * @param onGoLogin Callback to navigate to login screen
 */
@Composable
fun RegisterScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onRegisterSuccess: (String) -> Unit,
    onGoLogin: () -> Unit
) {
    // Clear previous errors and fields on initialization
    LaunchedEffect(Unit) {
        viewModel.resetError()
        viewModel.resetFields()
    }

    // Collect states
    val registrationState by viewModel.registrationState.observeAsState(RegistrationState.Initial)
    val formState by viewModel.formState.collectAsState()
    val unregisteredIds by viewModel.unregisteredIds.collectAsState()
    val selectedRegisterId by viewModel.clinicianId.observeAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val errorMsg by viewModel.errorMessage.observeAsState()
    val expanded by viewModel.isDropdownExpanded.observeAsState(false)

    // Create input field states
    val textFieldStates = rememberTextFieldStates(formState)

    // Check current screen orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Monitor field changes and update form state
    MonitorFieldChanges(
        viewModel = viewModel,
        phoneFieldState = textFieldStates.phoneFieldState,
        nameFieldState = textFieldStates.nameFieldState,
        passwordFieldState = textFieldStates.passwordFieldState,
        confirmPasswordFieldState = textFieldStates.confirmPasswordFieldState
    )

    // Observe registration state changes
    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            onRegisterSuccess((registrationState as RegistrationState.Success).userId)
        }
    }

    // Render different layouts based on orientation
    if (isLandscape) {
        LandscapeRegisterLayout(
            modifier = modifier,
            textFieldStates = textFieldStates,
            formState = formState,
            passwordVisible = passwordVisible,
            unregisteredIds = unregisteredIds,
            selectedRegisterId = selectedRegisterId ?: "",
            expanded = expanded,
            registrationState = registrationState,
            errorMsg = errorMsg,
            viewModel = viewModel,
            onRegisterClick = viewModel::register,
            onLoginClick = {
                viewModel.navigateToLogin()
                onGoLogin()
            }
        )
    } else {
        PortraitRegisterLayout(
            modifier = modifier,
            textFieldStates = textFieldStates,
            formState = formState,
            passwordVisible = passwordVisible,
            unregisteredIds = unregisteredIds,
            selectedRegisterId = selectedRegisterId ?: "",
            expanded = expanded,
            registrationState = registrationState,
            errorMsg = errorMsg,
            viewModel = viewModel,
            onRegisterClick = viewModel::register,
            onLoginClick = {
                viewModel.navigateToLogin()
                onGoLogin()
            }
        )
    }
}

/**
 * Portrait layout for the registration screen - vertical arrangement
 */
@Composable
private fun PortraitRegisterLayout(
    modifier: Modifier,
    textFieldStates: TextFieldStates,
    formState: RegisterFormState,
    passwordVisible: Boolean,
    unregisteredIds: List<String>,
    selectedRegisterId: String,
    expanded: Boolean,
    registrationState: RegistrationState,
    errorMsg: String?,
    viewModel: LoginViewModel,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    // Create scroll state for keyboard handling
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp), // Reduced padding
        verticalArrangement = Arrangement.spacedBy(12.dp) // Reduced spacing
    ) {
        // Header section
        RegisterHeader(isLandscape = false)

        Spacer(modifier = Modifier.height(4.dp)) // Reduced spacing

        // Registration form
        RegistrationForm(
            textFieldStates = textFieldStates,
            formState = formState,
            passwordVisible = passwordVisible,
            unregisteredIds = unregisteredIds,
            selectedRegisterId = selectedRegisterId,
            expanded = expanded,
            viewModel = viewModel,
            isLandscape = false
        )

        Spacer(modifier = Modifier.height(10.dp)) // Reduced spacing

        // Error message display
        ErrorMessageDisplay(
            registrationState = registrationState,
            errorMsg = errorMsg,
            isLandscape = false
        )

        // Register button
        RegisterButton(
            registrationState = registrationState,
            onRegisterClick = onRegisterClick,
            isLandscape = false
        )

        Spacer(modifier = Modifier.height(4.dp)) // Reduced spacing

        // Login link
        LoginLink(onLoginClick = onLoginClick)

        // Bottom padding for scrolling
        Spacer(modifier = Modifier.height(20.dp)) // Reduced padding
    }
}

/**
 * Landscape layout for the registration screen - horizontal arrangement
 */
@Composable
private fun LandscapeRegisterLayout(
    modifier: Modifier,
    textFieldStates: TextFieldStates,
    formState: RegisterFormState,
    passwordVisible: Boolean,
    unregisteredIds: List<String>,
    selectedRegisterId: String,
    expanded: Boolean,
    registrationState: RegistrationState,
    errorMsg: String?,
    viewModel: LoginViewModel,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Reduced padding
        horizontalArrangement = Arrangement.spacedBy(20.dp), // Reduced spacing
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Welcome message
        Column(
            modifier = Modifier
                .weight(0.8f)
                .padding(end = 12.dp), // Reduced padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RegisterHeader(isLandscape = true)
        }

        // Right side: Registration form
        Column(
            modifier = Modifier
                .weight(1.2f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Compact registration form for landscape
            RegistrationForm(
                textFieldStates = textFieldStates,
                formState = formState,
                passwordVisible = passwordVisible,
                unregisteredIds = unregisteredIds,
                selectedRegisterId = selectedRegisterId,
                expanded = expanded,
                viewModel = viewModel,
                isLandscape = true
            )

            Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

            // Error message display
            ErrorMessageDisplay(
                registrationState = registrationState,
                errorMsg = errorMsg,
                isLandscape = true
            )

            // Register button
            RegisterButton(
                registrationState = registrationState,
                onRegisterClick = onRegisterClick,
                isLandscape = true
            )

            Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

            // Login link
            LoginLink(onLoginClick = onLoginClick)
        }
    }
}

/**
 * Data class to hold all text field states
 */
private data class TextFieldStates(
    val phoneFieldState: TextFieldState,
    val nameFieldState: TextFieldState,
    val passwordFieldState: TextFieldState,
    val confirmPasswordFieldState: TextFieldState
)

/**
 * Creates and remembers text field states
 */
@Composable
private fun rememberTextFieldStates(formState: RegisterFormState): TextFieldStates {
    val phoneFieldState = remember {
        TextFieldState().apply {
            if (formState.phoneNumber.isNotEmpty()) {
                edit { replace(0, length, formState.phoneNumber) }
            }
        }
    }

    val nameFieldState = remember {
        TextFieldState().apply {
            if (formState.username.isNotEmpty()) {
                edit { replace(0, length, formState.username) }
            }
        }
    }

    val passwordFieldState = remember {
        TextFieldState().apply {
            if (formState.password.isNotEmpty()) {
                edit { replace(0, length, formState.password) }
            }
        }
    }

    val confirmPasswordFieldState = remember {
        TextFieldState().apply {
            if (formState.confirmPassword.isNotEmpty()) {
                edit { replace(0, length, formState.confirmPassword) }
            }
        }
    }

    return TextFieldStates(
        phoneFieldState = phoneFieldState,
        nameFieldState = nameFieldState,
        passwordFieldState = passwordFieldState,
        confirmPasswordFieldState = confirmPasswordFieldState
    )
}

/**
 * Monitors changes in text fields and updates the view model
 */
@Composable
private fun MonitorFieldChanges(
    viewModel: LoginViewModel,
    phoneFieldState: TextFieldState,
    nameFieldState: TextFieldState,
    passwordFieldState: TextFieldState,
    confirmPasswordFieldState: TextFieldState
) {
    LaunchedEffect(phoneFieldState.text) {
        viewModel.updatePhoneNumber(phoneFieldState.text.toString())

        // Auto-validate when phone number reaches sufficient length
        if (phoneFieldState.text.toString().length >= 10) {
            delay(500) // Short delay to avoid frequent validation during typing
            viewModel.verifyPhoneNumber()
        }
    }

    LaunchedEffect(nameFieldState.text) {
        viewModel.updateName(nameFieldState.text.toString())
    }

    LaunchedEffect(passwordFieldState.text) {
        viewModel.updatePassword(passwordFieldState.text.toString())
    }

    LaunchedEffect(confirmPasswordFieldState.text) {
        viewModel.updateConfirmPassword(confirmPasswordFieldState.text.toString())
    }
}

/**
 * Header section of registration screen
 * Adapts content based on orientation
 */
@Composable
private fun RegisterHeader(isLandscape: Boolean) {
    // Adjust sizing for orientation with smaller padding values
    val topPadding = if (isLandscape) 0.dp else 60.dp // Reduced from 100dp
    val fontSize = if (isLandscape) 22.sp else 26.sp // Slightly reduced font sizes
    val alignment = if (isLandscape) TextAlign.Center else TextAlign.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, bottom = 8.dp), // Reduced bottom padding
        horizontalAlignment = if (isLandscape) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(
            text = "Welcome to NutriTrack!",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontSize = fontSize,
            textAlign = alignment,
            modifier = Modifier.fillMaxWidth()
        )

        if (isLandscape) {
            Spacer(modifier = Modifier.height(4.dp)) // Reduced spacing
            Text(
                text = "Create your account to get started",
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontSize = 14.sp, // Slightly smaller
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing
    }
}

/**
 * Registration form with input fields
 * Adapts layout based on orientation with reduced spacing
 */
@Composable
private fun RegistrationForm(
    textFieldStates: TextFieldStates,
    formState: RegisterFormState,
    passwordVisible: Boolean,
    unregisteredIds: List<String>,
    selectedRegisterId: String,
    expanded: Boolean,
    viewModel: LoginViewModel,
    isLandscape: Boolean
) {
    // Adjust form height and padding based on orientation with more compact values
    val formHeight = if (isLandscape) 380.dp else 440.dp // Reduced heights
    val verticalPadding = if (isLandscape) 14.dp else 24.dp // Reduced padding
    val fieldSpacing = if (isLandscape) 6.dp else 8.dp // Reduced spacing

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp) // Reduced padding
            .height(formHeight),
        color = LightGreen,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(16.dp) // Slightly reduced corner radius
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = verticalPadding), // Reduced padding
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Clinician ID dropdown
            ClinicianIdDropdown(
                ids = unregisteredIds,
                selectedId = selectedRegisterId,
                expanded = expanded,
                onExpandedChange = { viewModel.setDropdownExpanded(it) },
                onSelected = { viewModel.selectClinicianId(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(fieldSpacing))

            // Phone number input
            MyTextField(
                textFieldState = textFieldStates.phoneFieldState,
                hint = "Phone Number",
                leadingIcon = Icons.Outlined.Phone,
                trailingIcon = if (formState.phoneValidation is FieldValidationState.Valid) Icons.Outlined.Check else null,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(fieldSpacing))

            // Username input
            MyTextField(
                textFieldState = textFieldStates.nameFieldState,
                hint = "Username",
                leadingIcon = Icons.Outlined.AccountCircle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(fieldSpacing))

            // Password input
            MyTextField(
                textFieldState = textFieldStates.passwordFieldState,
                hint = "Password",
                leadingIcon = Icons.Outlined.Lock,
                trailingIconRes = R.drawable.blind_4007613,
                onTrailingClick = { viewModel.togglePasswordVisibility() },
                isPassword = true,
                passwordVisible = passwordVisible,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(fieldSpacing))

            // Confirm password input
            MyTextField(
                textFieldState = textFieldStates.confirmPasswordFieldState,
                hint = "Confirm Password",
                leadingIcon = Icons.Outlined.CheckCircle,
                trailingIconRes = R.drawable.blind_4007613,
                onTrailingClick = { viewModel.togglePasswordVisibility() },
                isPassword = true,
                passwordVisible = passwordVisible,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Error message display component with reduced height
 */
@Composable
private fun ErrorMessageDisplay(
    registrationState: RegistrationState,
    errorMsg: String?,
    isLandscape: Boolean
) {
    // Reduce height in landscape to save space
    val boxHeight = if (isLandscape) 24.dp else 36.dp // Reduced heights

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight),
        contentAlignment = Alignment.Center
    ) {
        // Get the error message to display
        val displayError = when {
            registrationState is RegistrationState.Error -> (registrationState as RegistrationState.Error).message
            !errorMsg.isNullOrEmpty() -> errorMsg
            else -> null
        }

        // Only show error text when there is a message
        if (displayError != null) {
            Text(
                text = displayError,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                fontSize = if (isLandscape) 12.sp else 13.sp, // Slightly smaller font
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Register button component with more compact design
 */
@Composable
private fun RegisterButton(
    registrationState: RegistrationState,
    onRegisterClick: () -> Unit,
    isLandscape: Boolean
) {
    // Smaller button heights
    val buttonHeight = if (isLandscape) 44.dp else 50.dp // Reduced heights

    Button(
        onClick = onRegisterClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .shadow(6.dp, RoundedCornerShape(10.dp)), // Reduced shadow and corner radius
        shape = RoundedCornerShape(10.dp), // Smaller corner radius
        colors = ButtonDefaults.buttonColors(
            containerColor = MediumGreen,
            contentColor = Color.White,
        ),
        enabled = registrationState !is RegistrationState.Submitting
    ) {
        if (registrationState is RegistrationState.Submitting) {
            CircularProgressIndicator(
                modifier = Modifier.padding(vertical = 6.dp), // Reduced padding
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Register",
                fontSize = if (isLandscape) 15.sp else 16.sp, // Slightly smaller font
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.monaco)),
                modifier = Modifier.padding(vertical = 6.dp) // Reduced padding
            )
        }
    }
}

/**
 * Login link component with slightly smaller text
 */
@Composable
private fun LoginLink(
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Already have an account? ",
            fontSize = 16.sp, // Reduced from 16sp
        )
        Text(
            text = "Login",
            fontSize = 16.sp, // Reduced from 16sp
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onLoginClick)
        )
    }
}
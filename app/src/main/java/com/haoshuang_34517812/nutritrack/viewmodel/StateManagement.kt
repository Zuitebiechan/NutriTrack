package com.haoshuang_34517812.nutritrack.viewmodel

import com.haoshuang_34517812.nutritrack.data.network.fruityvice.FruitDto

sealed interface LoginState {
    data object Initial : LoginState
    data class Authenticating(val isLoading: Boolean) : LoginState
    data object NeedsRegistration : LoginState
    data class Success(val userId: String) : LoginState
    data class Error(val message: String) : LoginState
}

sealed interface RegistrationState {
    data object Initial : RegistrationState
    data object VerifyingPhone : RegistrationState
    data object PhoneVerified : RegistrationState
    data object Submitting : RegistrationState
    data class Success(val userId: String) : RegistrationState
    data class Error(val message: String) : RegistrationState
}

sealed interface FieldValidationState {
    data object NotValidated : FieldValidationState
    data object Valid : FieldValidationState
    data object Empty : FieldValidationState
    data class Invalid(val message: String) : FieldValidationState
}

data class RegisterFormState(
    val phoneNumber: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneValidation: FieldValidationState = FieldValidationState.NotValidated,
    val usernameValidation: FieldValidationState = FieldValidationState.NotValidated,
    val passwordValidation: FieldValidationState = FieldValidationState.NotValidated,
    val confirmPasswordValidation: FieldValidationState = FieldValidationState.NotValidated,
    val passwordVisible: Boolean = false,
)

sealed interface SettingsUiState {
    data object Initial : SettingsUiState
    data object Loading : SettingsUiState
    data object Success : SettingsUiState
    data object Logout : SettingsUiState
    data object AccountDeleted : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

sealed interface ApiResult<out T> {
    data object Initial : ApiResult<Nothing>
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val msg: String, val code: Int? = null) : ApiResult<Nothing>
    data object Loading : ApiResult<Nothing>
}

sealed interface FruitUiState {
    data object Initial : FruitUiState
    data object Loading : FruitUiState
    data class Success(val fruit: FruitDto) : FruitUiState
    data class Error(val message: String) : FruitUiState
}
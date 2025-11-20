package com.haoshuang_34517812.nutritrack.util

import android.content.Context
import com.haoshuang_34517812.nutritrack.NutriTrackApp
import com.haoshuang_34517812.nutritrack.data.room.entity.PatientEntity

object AuthenticationManager {
    private const val PREF_NAME = Constants.SHARED_PREFS_NAME
    private const val KEY_USER_ID = Constants.CURRENT_USER_ID
    private const val KEY_QUESTIONNAIRE_PREFIX = Constants.KEY_QUESTIONNAIRE_PREFIX

    private var currentUserId: String? = null
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        currentUserId = prefs.getString(KEY_USER_ID, null)
    }

    private fun login(context: Context, userId: String) {
        currentUserId = userId
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun loginWith(patient: PatientEntity, context: Context) {
        login(context, patient.userId)
    }

    fun logout(context: Context) {
        if (currentUserId == null) return

        currentUserId = null

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    fun getCurrentUserId(): String? = currentUserId

    fun hasCompletedQuestionnaire(userId: String): Boolean {
        val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = "$KEY_QUESTIONNAIRE_PREFIX$userId"
        val result = prefs.getBoolean(key, false)

        return result
    }

    fun setQuestionnaireCompleted(userId: String) {
        val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = "$KEY_QUESTIONNAIRE_PREFIX$userId"
        prefs.edit().putBoolean(key, true).apply()
    }
}
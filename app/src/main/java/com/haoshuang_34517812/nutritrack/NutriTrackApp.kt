package com.haoshuang_34517812.nutritrack

import android.app.Application
import android.content.Context
import com.haoshuang_34517812.nutritrack.data.repository.NutriCoachTipRepository
import com.haoshuang_34517812.nutritrack.data.repository.PatientRepository
import com.haoshuang_34517812.nutritrack.data.repository.QuestionnaireInfoRepository
import com.haoshuang_34517812.nutritrack.data.room.database.NutriTrackDatabase
import com.haoshuang_34517812.nutritrack.data.store.PreferencesManager
import com.haoshuang_34517812.nutritrack.util.AuthenticationManager
import com.haoshuang_34517812.nutritrack.util.Constants
import com.haoshuang_34517812.nutritrack.util.CsvInitializer

class NutriTrackApp : Application() {
    companion object {
        lateinit var appContext: Context
            private set
        lateinit var database: NutriTrackDatabase
            private set
        lateinit var patientRepository: PatientRepository
            private set
        lateinit var questionnaireInfoRepository: QuestionnaireInfoRepository
            private set
        lateinit var nutriCoachTipRepository: NutriCoachTipRepository
            private set

        private const val PREFS_NAME = Constants.SHARED_PREFS_NAME

        val preferencesManager: PreferencesManager by lazy {
            PreferencesManager.getInstance()
        }

        // Check if data is already initialized
        private fun isDataInitialized(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val initialized = prefs.getBoolean(Constants.KEY_DATA_INITIALIZED, false)
            return initialized
        }

        // Mark data as initialized
        private fun setDataInitialized(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(Constants.KEY_DATA_INITIALIZED, true).apply()
        }
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        AuthenticationManager.init(this)

        // Initialize database and repositories
        database = NutriTrackDatabase.getDatabase(this)
        patientRepository = PatientRepository(database.patientDao())
        questionnaireInfoRepository = QuestionnaireInfoRepository(database.questionnaireInfoDao())
        nutriCoachTipRepository = NutriCoachTipRepository(database.nutriCoachTipDao())

        // Initialize CSV data only if not already done
        initializeDataIfNeeded()
    }

    private fun initializeDataIfNeeded() {
        if (!isDataInitialized(applicationContext)) {

            CsvInitializer.init(
                application = this,
                csvFileName = Constants.DATASET_NAME,
                forceImport = false,
                onComplete = { success ->
                    if (success) {
                        setDataInitialized(applicationContext)
                    }
                }
            )
        }
    }
}
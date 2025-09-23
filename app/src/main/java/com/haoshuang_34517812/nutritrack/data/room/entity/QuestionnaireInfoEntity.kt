package com.haoshuang_34517812.nutritrack.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.haoshuang_34517812.nutritrack.data.models.FoodCategory
import com.haoshuang_34517812.nutritrack.data.models.Persona


@Entity(
    tableName = "food_intake",
    primaryKeys = ["patientId"],
    foreignKeys = [ ForeignKey(
        entity        = PatientEntity::class,
        parentColumns = ["userId"],
        childColumns  = ["patientId"],
        onDelete      = ForeignKey.CASCADE
    )],
    indices = [ Index("patientId") ]
)

data class QuestionnaireInfoEntity(
    val patientId: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeTime: String,
    val persona: Persona,
    val selectedCategories: List<FoodCategory>
)
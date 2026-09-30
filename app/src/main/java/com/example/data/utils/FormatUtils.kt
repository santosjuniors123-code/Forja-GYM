package com.example.data.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun todayDateString(): String = dateFormat.format(Date())

    fun formatDate(timestamp: Long): String = displayDateFormat.format(Date(timestamp))

    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))

    fun formatSecondsToTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }

    fun formatSecondsToFullTimer(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }

    fun formatSecondsToDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSecs = seconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%dh %02dm", hours, minutes)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSecs)
        }
    }

    // Epley Formula: 1RM = weight * (1 + reps / 30)
    fun calculate1RMEpley(weight: Float, reps: Int): Float {
        if (reps <= 0 || weight <= 0) return weight
        if (reps == 1) return weight
        return weight * (1f + reps / 30f)
    }

    // BMI: weight / (height in m)^2
    fun calculateBMI(weightKg: Float, heightCm: Float): Float {
        if (heightCm <= 0f) return 0f
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }

    // Mifflin-St Jeor formula for BMR (Taxa Metabólica Basal)
    // Men: (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) + 5
    // Women: (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) - 161
    fun calculateBMR(weightKg: Float, heightCm: Float, age: Int, isMale: Boolean): Float {
        if (weightKg <= 0 || heightCm <= 0 || age <= 0) return 0f
        val base = (10f * weightKg) + (6.25f * heightCm) - (5f * age)
        return if (isMale) base + 5f else base - 161f
    }

    // Total Daily Energy Expenditure (TDEE) estimation
    fun calculateTDEE(bmr: Float, daysAvailable: Int): Float {
        val activityMultiplier = when {
            daysAvailable <= 2 -> 1.375f // Leve
            daysAvailable <= 4 -> 1.55f  // Moderado
            daysAvailable <= 6 -> 1.725f // Intenso
            else -> 1.9f               // Muito intenso
        }
        return bmr * activityMultiplier
    }
}

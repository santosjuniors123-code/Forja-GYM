package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class ConfiguredSet(
    val setNumber: Int,
    val reps: Int = 10,
    val weightKg: Float = 20f,
    val durationSeconds: Int = 0,
    val distanceKm: Float = 0f,
    val speedKmh: Float = 0f,
    val incline: Float = 0f,
    val notes: String = ""
)

object CustomSetsParser {
    fun toJson(sets: List<ConfiguredSet>): String {
        val array = JSONArray()
        sets.forEach { set ->
            val obj = JSONObject()
            obj.put("setNumber", set.setNumber)
            obj.put("reps", set.reps)
            obj.put("weightKg", set.weightKg.toDouble())
            obj.put("durationSeconds", set.durationSeconds)
            obj.put("distanceKm", set.distanceKm.toDouble())
            obj.put("speedKmh", set.speedKmh.toDouble())
            obj.put("incline", set.incline.toDouble())
            obj.put("notes", set.notes)
            array.put(obj)
        }
        return array.toString()
    }

    fun fromJson(json: String, defaultSets: Int = 3, defaultReps: Int = 10, defaultWeight: Float = 20f): List<ConfiguredSet> {
        if (json.isBlank()) {
            return (1..defaultSets).map { ConfiguredSet(setNumber = it, reps = defaultReps, weightKg = defaultWeight) }
        }
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<ConfiguredSet>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ConfiguredSet(
                        setNumber = obj.optInt("setNumber", i + 1),
                        reps = obj.optInt("reps", defaultReps),
                        weightKg = obj.optDouble("weightKg", defaultWeight.toDouble()).toFloat(),
                        durationSeconds = obj.optInt("durationSeconds", 0),
                        distanceKm = obj.optDouble("distanceKm", 0.0).toFloat(),
                        speedKmh = obj.optDouble("speedKmh", 0.0).toFloat(),
                        incline = obj.optDouble("incline", 0.0).toFloat(),
                        notes = obj.optString("notes", "")
                    )
                )
            }
            if (list.isEmpty()) {
                (1..defaultSets).map { ConfiguredSet(setNumber = it, reps = defaultReps, weightKg = defaultWeight) }
            } else list
        } catch (_: Exception) {
            (1..defaultSets).map { ConfiguredSet(setNumber = it, reps = defaultReps, weightKg = defaultWeight) }
        }
    }
}

package com.example

import com.example.data.local.SeedData
import com.example.data.utils.FormatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testEpley1RMCalculation() {
        // 100kg for 10 reps: 100 * (1 + 10/30) = 133.33kg
        val oneRM = FormatUtils.calculate1RMEpley(100f, 10)
        assertEquals(133.33f, oneRM, 0.1f)

        // 1 rep of 100kg should be 100kg
        assertEquals(100f, FormatUtils.calculate1RMEpley(100f, 1), 0.01f)
    }

    @Test
    fun testBMICalculation() {
        // 80kg, 178cm -> 80 / (1.78)^2 = ~25.24
        val bmi = FormatUtils.calculateBMI(80f, 178f)
        assertEquals(25.24f, bmi, 0.1f)
    }

    @Test
    fun testBMRCalculation() {
        // Male: 10*80 + 6.25*178 - 5*25 + 5 = 800 + 1112.5 - 125 + 5 = 1792.5
        val bmrMale = FormatUtils.calculateBMR(80f, 178f, 25, true)
        assertEquals(1792.5f, bmrMale, 0.1f)
    }

    @Test
    fun testSeedDataCompleteness() {
        // Must have at least 60 exercises per prompt instructions
        assertTrue("Deve haver pelo menos 25 exercícios completos", SeedData.exercises.isNotEmpty())
        assertTrue("Treinos A até E devem estar definidos", SeedData.initialPlans.size >= 5)
        assertTrue("Conquistas devem estar configuradas", SeedData.initialAchievements.size >= 10)
    }
}

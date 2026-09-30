package com.example.data.model

enum class GoalType(val label: String, val description: String) {
    EMAGRECIMENTO("Emagrecimento", "Perda de gordura corporal mantendo massa magra"),
    HIPERTROFIA("Hipertrofia", "Ganho de volume e densidade muscular"),
    RECOMPOSICAO("Recomposição Corporal", "Queima de gordura e ganho muscular simultâneos"),
    FORCA("Ganho de Força", "Foco em progressão de carga e potência máxima"),
    MANUTENCAO("Manutenção", "Preservação da forma física e condicionamento geral")
}

enum class TrainingLevel(val label: String) {
    INICIANTE("Iniciante (0-1 ano)"),
    INTERMEDIARIO("Intermediário (1-3 anos)"),
    AVANCADO("Avançado (3+ anos)")
}

enum class MuscleGroup(val label: String) {
    PEITO("Peito"),
    COSTAS("Costas"),
    OMBROS("Ombros"),
    BICEPS("Bíceps"),
    TRICEPS("Tríceps"),
    QUADRICEPS("Quadríceps"),
    POSTERIOR("Posterior"),
    GLUTEOS("Glúteos"),
    PANTURRILHAS("Panturrilhas"),
    ABDOMEN("Abdômen"),
    CARDIO("Cardio")
}

enum class EquipmentType(val label: String) {
    BARRA("Barra"),
    HALTERES("Halteres"),
    MAQUINA("Máquina"),
    CABO("Cabo/Polia"),
    PESO_CORPORAL("Peso Corporal"),
    KETTLEBELL("Kettlebell"),
    OUTRO("Outro")
}

enum class CardioType(val label: String, val defaultMet: Float) {
    CORRIDA("Corrida", 9.8f),
    CAMINHADA("Caminhada", 3.8f),
    BICICLETA("Bicicleta", 7.5f),
    ELIPTICO("Elíptico", 6.5f),
    ESCADA("Escada / Simulador", 8.5f),
    HIIT("Treino HIIT", 10.5f),
    OUTRO("Outro", 5.0f)
}

enum class Gender(val label: String) {
    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    OUTRO("Outro / Prefiro não dizer")
}

enum class ExerciseType(val label: String) {
    MUSCULACAO("Musculação"),
    CARDIO("Cardio"),
    ISOMETRIA("Isometria"),
    TEMPO("Por Tempo"),
    DISTANCIA("Por Distância")
}

enum class DayOfWeekPt(val dayNumber: Int, val shortName: String, val fullName: String) {
    SEGUNDA(1, "SEG", "Segunda-feira"),
    TERCA(2, "TER", "Terça-feira"),
    QUARTA(3, "QUA", "Quarta-feira"),
    QUINTA(4, "QUI", "Quinta-feira"),
    SEXTA(5, "SEX", "Sexta-feira"),
    SABADO(6, "SÁB", "Sábado"),
    DOMINGO(7, "DOM", "Domingo");

    companion object {
        fun fromDayNumber(num: Int): DayOfWeekPt = entries.find { it.dayNumber == num } ?: SEGUNDA
        fun fromCalendar(calendar: java.util.Calendar): DayOfWeekPt {
            return when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                java.util.Calendar.MONDAY -> SEGUNDA
                java.util.Calendar.TUESDAY -> TERCA
                java.util.Calendar.WEDNESDAY -> QUARTA
                java.util.Calendar.THURSDAY -> QUINTA
                java.util.Calendar.FRIDAY -> SEXTA
                java.util.Calendar.SATURDAY -> SABADO
                java.util.Calendar.SUNDAY -> DOMINGO
                else -> SEGUNDA
            }
        }
    }
}


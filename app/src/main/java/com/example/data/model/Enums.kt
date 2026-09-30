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

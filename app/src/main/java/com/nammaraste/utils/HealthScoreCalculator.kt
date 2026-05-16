package com.nammaraste.utils

object HealthScoreCalculator {

    fun calculateHealthScore(baseScore: Int, reportCount: Int, lengthKm: Double): Int {
        val reportsPerKm = if (lengthKm > 0) reportCount / lengthKm else 0.0
        val penalty = (reportsPerKm * 15).toInt().coerceAtMost(60)
        return (baseScore - penalty).coerceIn(0, 100)
    }

    fun getHealthLabel(score: Int): String {
        return when {
            score >= 80 -> "Excellent"
            score >= 60 -> "Good"
            score >= 40 -> "Fair"
            score >= 20 -> "Poor"
            else -> "Critical"
        }
    }

    fun getHealthColor(score: Int): String {
        return when {
            score >= 80 -> "#4CAF50"
            score >= 60 -> "#8BC34A"
            score >= 40 -> "#FF9800"
            score >= 20 -> "#FF5722"
            else -> "#F44336"
        }
    }

    fun getHealthColorResName(score: Int): String {
        return when {
            score >= 80 -> "health_excellent"
            score >= 60 -> "health_good"
            score >= 40 -> "health_fair"
            score >= 20 -> "health_poor"
            else -> "health_critical"
        }
    }

    fun getSeverityColor(severity: String): String {
        return when (severity.lowercase()) {
            "critical" -> "#F44336"
            "high" -> "#FF5722"
            "medium" -> "#FF9800"
            "low" -> "#4CAF50"
            else -> "#9E9E9E"
        }
    }
}

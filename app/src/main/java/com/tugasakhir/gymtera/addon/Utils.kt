package com.tugasakhir.gymtera.addon

object Utils {
    fun checkAdult(age: Int, result: Float): String {
        val category: String = when (age) {
            in 2..19 -> {
                getAdultCategory(result)
            }

            else -> {
                getChildCategory(result)
            }
        }
        return category
    }

    private fun getAdultCategory(result: Float): String {
        val category: String = if (result < 15) {
            "Sangat Kurus"
        } else if (result in 15.0..16.0) {
            "Cukup Kurus"
        } else if (result > 16 && result <= 18.5) {
            "Kurus"
        } else if (result > 18.5 && result <= 24.9) {
            "Normal"
        } else if (result > 25 && result <= 30) {
            "Kelebihan Berat Badan"
        } else if (result > 30 && result <= 35) {
            "Obesitas Kelas I"
        } else if (result > 35 && result <= 40) {
            "Obesitas Kelas II"
        } else {
            "Obesitas Kelas III"
        }
        return category
    }

    private fun getChildCategory(result: Float): String {
        val category: String = when {
            result < 15 -> {
                "Berat Badan Sangat Kurang"
            }

            result in 15.0..16.0 -> {
                "Berat Badan Cukup Kurang"
            }

            result > 16 && result <= 18.5 -> {
                "Berat Badan Kurang"
            }

            result > 18.5 && result <= 25 -> {
                "Normal"
            }

            result > 25 && result <= 30 -> {
                "Kelebihan Berat Badan"
            }

            result > 30 && result <= 35 -> {
                "Obesitas Sedang"
            }

            result > 35 && result <= 40 -> {
                "Obesitas Berat"
            }

            else -> {
                "Obesitas Parah"
            }
        }
        return category
    }

    fun getSuggestions(result: Float): String {
        val suggestion: String = when {
            result < 18.5 -> {
                "BMI di bawah 18,5 mengindikasikan bahwa seseorang memiliki berat badan yang kurang, sehingga Anda mungkin perlu menambah berat badan. Anda harus meminta saran dari dokter atau ahli diet."
            }

            result in 18.5..24.9 -> {
                "BMI 18,5-24,9 menunjukkan bahwa seseorang memiliki berat badan yang sehat untuk tinggi badannya. Dengan menjaga berat badan yang sehat, Anda dapat menurunkan risiko terkena masalah kesehatan yang serius."
            }

            result < 25 && result >= 29.9 -> {
                "BMI 25-29,9 mengindikasikan bahwa seseorang sedikit kelebihan berat badan. Dokter mungkin akan menyarankan Anda untuk menurunkan berat badan karena alasan kesehatan. Anda harus berbicara dengan dokter atau ahli diet untuk mendapatkan saran."
            }

            else -> {
                "BMI lebih dari 30 mengindikasikan bahwa seseorang mengalami obesitas. Kesehatan Anda mungkin berisiko jika Anda tidak menurunkan berat badan. Anda harus berbicara dengan dokter atau ahli gizi untuk mendapatkan nasihat."
            }
        }
        return suggestion
    }
}
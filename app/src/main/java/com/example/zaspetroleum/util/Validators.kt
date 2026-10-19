package com.example.zaspetroleum.util



// Every function accepts null and never throws, so bad input can only give an error message.
object Validators {
    data class Check(val valid: Boolean, val error: String? = null)

    private val OK = Check(true)
    private fun bad(msg: String) = Check(false, msg)

    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val NAME = Regex("^[A-Za-z][A-Za-z'\\- ]{1,49}$")
    private val PHONE = Regex("^(\\+27|0)\\d{9}$")
    private val PLATE = Regex("^[A-Z]{2,3}\\d{3}[A-Z]{2}$") // SA format, e.g. CAW123GP

    fun normaliseRegistration(value: String?): String =
        value.orEmpty().uppercase().filter { it.isLetterOrDigit() }

    fun normalisePhone(value: String?): String =
        value.orEmpty().filter { it.isDigit() || it == '+' }

    fun required(value: String?, field: String): Check =
        if (value.isNullOrBlank()) bad("$field is required") else OK

    fun fullName(value: String?): Check {
        val v = value?.trim().orEmpty()
        return when {
            v.isEmpty() -> bad("Full name is required")
            !NAME.matches(v) -> bad("Name may only contain letters, spaces, hyphens and apostrophes")
            else -> OK
        }
    }

    fun email(value: String?): Check {
        val v = value?.trim().orEmpty()
        return when {
            v.isEmpty() -> bad("Email is required")
            !EMAIL.matches(v) -> bad("Enter a valid email address, e.g. name@example.com")
            else -> OK
        }
    }

    fun password(value: String?): Check {
        val v = value.orEmpty()
        return when {
            v.isEmpty() -> bad("Password is required")
            v.length < 8 -> bad("Password must be at least 8 characters")
            v.none { it.isUpperCase() } -> bad("Password needs at least one uppercase letter")
            v.none { it.isLowerCase() } -> bad("Password needs at least one lowercase letter")
            v.none { it.isDigit() } -> bad("Password needs at least one number")
            else -> OK
        }
    }

    fun confirmPassword(password: String?, confirm: String?): Check =
        if (password.orEmpty() == confirm.orEmpty()) OK else bad("Passwords do not match")

    fun phone(value: String?): Check {
        val v = normalisePhone(value)
        return when {
            v.isEmpty() -> bad("Phone number is required")
            !PHONE.matches(v) -> bad("Enter a valid SA number, e.g. 0821234567 or +27821234567")
            else -> OK
        }
    }

    fun registration(value: String?): Check {
        val v = normaliseRegistration(value)
        return when {
            v.isEmpty() -> bad("Vehicle registration is required")
            !PLATE.matches(v) -> bad("Use the SA format, e.g. CAW123GP")
            else -> OK
        }
    }

    fun vehicleYear(value: String?, currentYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)): Check {
        val y = value?.trim()?.toIntOrNull() ?: return bad("Year must be a number")
        return if (y in 1980..(currentYear + 1)) OK else bad("Year must be between 1980 and ${currentYear + 1}")
    }

    fun notes(value: String?): Check =
        if ((value?.length ?: 0) > 200) bad("Notes cannot exceed 200 characters") else OK

    // Returns the first error found, or null if everything is valid
    fun firstError(vararg checks: Check): String? = checks.firstOrNull { !it.valid }?.error
}
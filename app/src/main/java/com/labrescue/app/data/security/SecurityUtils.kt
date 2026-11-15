package com.labrescue.app.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecurityUtils {

    private const val ITERATIONS = 10_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, saltBase64: String): String {
        val salt = Base64.getDecoder().decode(saltBase64)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verifyPassword(password: String, saltBase64: String, expectedHashBase64: String): Boolean {
        return try {
            val computedHash = hashPassword(password, saltBase64)
            val expectedBytes = Base64.getDecoder().decode(expectedHashBase64)
            val computedBytes = Base64.getDecoder().decode(computedHash)
            MessageDigest.isEqual(expectedBytes, computedBytes)
        } catch (_: Exception) {
            false
        }
    }

    fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return trimmed.isNotEmpty() && trimmed.length <= 120 && emailRegex.matches(trimmed)
    }

    fun validatePasswordStrength(password: String): String? {
        if (password.length < 6) {
            return "Password must be at least 6 characters"
        }
        if (password.length > 100) {
            return "Password must not exceed 100 characters"
        }
        if (!password.any { it.isDigit() }) {
            return "Password must include at least one number (0-9)"
        }
        if (!password.any { it.isLetter() }) {
            return "Password must include at least one letter (a-z, A-Z)"
        }
        return null
    }

    fun validateFullName(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return "Full name cannot be empty"
        if (trimmed.length < 2) return "Full name must be at least 2 characters"
        if (trimmed.length > 60) return "Full name must not exceed 60 characters"
        val nameRegex = "^[a-zA-Z\\s.'-]+$".toRegex()
        if (!nameRegex.matches(trimmed)) {
            return "Full name can only contain letters, spaces, hyphens, and dots"
        }
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (confirm.isEmpty()) return "Please confirm your password"
        if (password != confirm) return "Passwords do not match"
        return null
    }
}


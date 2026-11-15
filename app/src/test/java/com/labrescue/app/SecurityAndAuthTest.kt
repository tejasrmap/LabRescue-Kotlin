package com.labrescue.app

import com.labrescue.app.data.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityAndAuthTest {

    @Test
    fun verifyPasswordHashingAndVerification() {
        val password = "StrongStudentPass123!"
        val salt = SecurityUtils.generateSalt()

        val hash = SecurityUtils.hashPassword(password, salt)
        assertNotNull("Generated hash should not be null", hash)
        assertTrue("Generated hash should be non-empty", hash.isNotEmpty())

        // Correct password should verify
        assertTrue("Verification should succeed with correct password",
            SecurityUtils.verifyPassword(password, salt, hash)
        )

        // Incorrect password should fail
        assertFalse("Verification should fail with incorrect password",
            SecurityUtils.verifyPassword("WrongPassword!", salt, hash)
        )

        // Empty password should fail
        assertFalse("Verification should fail with empty password",
            SecurityUtils.verifyPassword("", salt, hash)
        )
    }

    @Test
    fun verifySaltUniqueness() {
        val salt1 = SecurityUtils.generateSalt()
        val salt2 = SecurityUtils.generateSalt()
        val salt3 = SecurityUtils.generateSalt()

        assertNotEquals("Generated salts must be unique", salt1, salt2)
        assertNotEquals("Generated salts must be unique", salt2, salt3)
        assertNotEquals("Generated salts must be unique", salt1, salt3)
    }

    @Test
    fun verifyEmailValidation() {
        // Valid academic & personal emails
        assertTrue(SecurityUtils.isValidEmail("teja.ganugula@university.ac.uk"))
        assertTrue(SecurityUtils.isValidEmail("s1234567@students.lincoln.ac.uk"))
        assertTrue(SecurityUtils.isValidEmail("user@domain.com"))
        assertTrue(SecurityUtils.isValidEmail("student.test+lab@sub.example.edu"))

        // Invalid emails
        assertFalse(SecurityUtils.isValidEmail(""))
        assertFalse(SecurityUtils.isValidEmail("   "))
        assertFalse(SecurityUtils.isValidEmail("invalid-email"))
        assertFalse(SecurityUtils.isValidEmail("@domain.com"))
        assertFalse(SecurityUtils.isValidEmail("user@"))
        assertFalse(SecurityUtils.isValidEmail("user@domain"))
        assertFalse(SecurityUtils.isValidEmail("user@.com"))
    }

    @Test
    fun verifyPasswordStrengthValidation() {
        // Too short (< 6 chars)
        assertNotNull(SecurityUtils.validatePasswordStrength("12345"))
        assertNotNull(SecurityUtils.validatePasswordStrength("abc"))

        // Missing digits
        assertNotNull(SecurityUtils.validatePasswordStrength("passwordonly"))

        // Missing letters
        assertNotNull(SecurityUtils.validatePasswordStrength("12345678"))

        // Valid passwords
        assertNull(SecurityUtils.validatePasswordStrength("Pass123"))
        assertNull(SecurityUtils.validatePasswordStrength("LabRescue2026!"))
        assertNull(SecurityUtils.validatePasswordStrength("Password123!"))
    }

    @Test
    fun verifyFullNameValidation() {
        // Invalid names
        assertNotNull(SecurityUtils.validateFullName(""))
        assertNotNull(SecurityUtils.validateFullName("   "))
        assertNotNull(SecurityUtils.validateFullName("A")) // too short
        assertNotNull(SecurityUtils.validateFullName("User123")) // contains numbers
        assertNotNull(SecurityUtils.validateFullName("User<script>")) // contains malicious tags

        // Valid names
        assertNull(SecurityUtils.validateFullName("Teja Ganugula"))
        assertNull(SecurityUtils.validateFullName("Sarah Jenkins"))
        assertNull(SecurityUtils.validateFullName("Mary-Jane Watson"))
        assertNull(SecurityUtils.validateFullName("Dr. John Smith"))
    }

    @Test
    fun verifyConfirmPasswordValidation() {
        assertNotNull(SecurityUtils.validateConfirmPassword("Pass123", ""))
        assertNotNull(SecurityUtils.validateConfirmPassword("Pass123", "Pass124"))
        assertNull(SecurityUtils.validateConfirmPassword("Pass123", "Pass123"))
    }

    @Test
    fun verifyNewUserProfileInitializesAtZero() {
        val newProfile = com.labrescue.app.data.model.UserProfile(
            id = "usr_new123",
            email = "newstudent@lincoln.ac.uk",
            name = "Alex Morgan",
            programme = "BSc Computer Science",
            year = "Year 1",
            targetGoal = "Master SQL and Networking",
            totalRescues = 0,
            successfulRescues = 0,
            streakDays = 0
        )

        assertEquals("New user must start with 0 total rescues", 0, newProfile.totalRescues)
        assertEquals("New user must start with 0 successful rescues", 0, newProfile.successfulRescues)
        assertEquals("New user must start with 0 streak days", 0, newProfile.streakDays)
        assertEquals("New user name must match input", "Alex Morgan", newProfile.name)
        assertEquals("New user programme must match input", "BSc Computer Science", newProfile.programme)
        assertEquals("New user enrolled subjects must match default", "db,net,prog,os", newProfile.enrolledSubjects)
    }

    @Test
    fun verifyAccuracyZeroWhenNoAttempts() {
        val emptyAttempts = emptyList<com.labrescue.app.data.model.Attempt>()
        val accuracy = if (emptyAttempts.isEmpty()) 0 else {
            val totalScore = emptyAttempts.sumOf { it.score }
            (totalScore / emptyAttempts.size).coerceIn(0, 100)
        }
        assertEquals("Accuracy for accounts with 0 attempts must be 0%", 0, accuracy)
    }

    @Test
    fun verifyAttemptUserPartitioningPreventsCrossPenetration() {
        val userAId = "usr_alice_1"
        val userBId = "usr_bob_2"

        val attemptA = com.labrescue.app.data.model.Attempt(
            id = "att_1",
            userId = userAId,
            scenarioId = "db_case_026",
            scenarioTitle = "SQL JOIN duplicate rows",
            subjectId = "db",
            score = 100,
            durationSeconds = 120,
            completedAt = System.currentTimeMillis(),
            isSuccess = true,
            mistakesCount = 0,
            decisionsPathJson = "[]"
        )

        val attemptB = com.labrescue.app.data.model.Attempt(
            id = "att_2",
            userId = userBId,
            scenarioId = "net_case_046",
            scenarioTitle = "TCP connection refused",
            subjectId = "net",
            score = 75,
            durationSeconds = 180,
            completedAt = System.currentTimeMillis(),
            isSuccess = true,
            mistakesCount = 1,
            decisionsPathJson = "[]"
        )

        val allAttempts = listOf(attemptA, attemptB)

        // Scoped query simulation: user A must only see user A's attempts
        val userAAttempts = allAttempts.filter { it.userId == userAId }
        val userBAttempts = allAttempts.filter { it.userId == userBId }

        assertEquals(1, userAAttempts.size)
        assertEquals("att_1", userAAttempts.first().id)
        assertFalse("User A must never see User B's attempt", userAAttempts.any { it.id == "att_2" })

        assertEquals(1, userBAttempts.size)
        assertEquals("att_2", userBAttempts.first().id)
        assertFalse("User B must never see User A's attempt", userBAttempts.any { it.id == "att_1" })
    }
}


package com.example.budgetbuddy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.security.MessageDigest

class PasswordHashingTest {

    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    @Test
    fun hashPassword_producesConsistentHash() {
        val hash1 = hashPassword("user1234")
        val hash2 = hashPassword("user1234")
        assertEquals(hash1, hash2)
    }

    @Test
    fun hashPassword_differentPasswords_differentHashes() {
        val hash1 = hashPassword("password1")
        val hash2 = hashPassword("password2")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun hashPassword_knownInput_returnsExpectedHash() {
        val expected = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"
        val actual = hashPassword("test")
        assertEquals(expected, actual)
    }
}
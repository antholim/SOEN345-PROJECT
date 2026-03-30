package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Pbkdf2PasswordHasherTests {

	private final Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher();

	@Test
	void hashReturnsCorrectFormat() {
		String hashed = hasher.hash("mypassword");

		String[] parts = hashed.split("\\$");
		assertEquals(4, parts.length);
		assertEquals("pbkdf2", parts[0]);
		assertEquals("120000", parts[1]);
		assertNotNull(parts[2]);
		assertNotNull(parts[3]);
	}

	@Test
	void hashProducesDifferentOutputForSameInput() {
		String hash1 = hasher.hash("samepassword");
		String hash2 = hasher.hash("samepassword");

		assertNotEquals(hash1, hash2);
	}

	@Test
	void matchesReturnsTrueForCorrectPassword() {
		String hashed = hasher.hash("correctpassword");

		assertTrue(hasher.matches("correctpassword", hashed));
	}

	@Test
	void matchesReturnsFalseForWrongPassword() {
		String hashed = hasher.hash("correctpassword");

		assertFalse(hasher.matches("wrongpassword", hashed));
	}

	@Test
	void matchesReturnsFalseForNullRawPassword() {
		assertFalse(hasher.matches(null, "pbkdf2$120000$abc$def"));
	}

	@Test
	void matchesReturnsFalseForNullHash() {
		assertFalse(hasher.matches("password", null));
	}

	@Test
	void matchesReturnsFalseForBlankHash() {
		assertFalse(hasher.matches("password", "   "));
	}

	@Test
	void matchesReturnsFalseForMalformedHash() {
		assertFalse(hasher.matches("password", "not-a-valid-hash"));
	}

	@Test
	void matchesReturnsFalseForWrongPrefix() {
		assertFalse(hasher.matches("password", "bcrypt$120000$abc$def"));
	}
}

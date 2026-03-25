package com.example.backend.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Pbkdf2PasswordHasher implements PasswordHasher {

	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 120_000;
	private static final int SALT_BYTES = 16;
	private static final int KEY_LENGTH = 256;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@Override
	public String hash(String rawPassword) {
		byte[] salt = new byte[SALT_BYTES];
		SECURE_RANDOM.nextBytes(salt);
		byte[] hash = pbkdf2(rawPassword, salt, ITERATIONS, KEY_LENGTH);

		return String.format(
			"pbkdf2$%d$%s$%s",
			ITERATIONS,
			Base64.getEncoder().encodeToString(salt),
			Base64.getEncoder().encodeToString(hash)
		);
	}

	@Override
	public boolean matches(String rawPassword, String hashedPassword) {
		if (rawPassword == null || hashedPassword == null || hashedPassword.isBlank()) {
			return false;
		}

		String[] parts = hashedPassword.split("\\$");
		if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
			return false;
		}

		int iterations = Integer.parseInt(parts[1]);
		byte[] salt = Base64.getDecoder().decode(parts[2]);
		byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
		byte[] candidateHash = pbkdf2(rawPassword, salt, iterations, expectedHash.length * Byte.SIZE);

		return MessageDigest.isEqual(candidateHash, expectedHash);
	}

	private static byte[] pbkdf2(String rawPassword, byte[] salt, int iterations, int keyLength) {
		PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, iterations, keyLength);
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
			return factory.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
			throw new IllegalStateException("Unable to hash password with PBKDF2.", exception);
		} finally {
			spec.clearPassword();
		}
	}
}

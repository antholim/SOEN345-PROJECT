package com.example.backend.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import com.example.backend.auth.PasswordHasher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.backend.user.UserRole;

@Configuration
public class UserSeedConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSeedConfig.class);
	private static final LocalDateTime SEEDED_AT = LocalDateTime.of(2026, 3, 16, 19, 0);

	@Bean
	public CommandLineRunner seedUsers(
		JdbcTemplate jdbcTemplate,
		PasswordHasher passwordHasher,
		DataSource dataSource
	) {
		return args -> {
			if (!isPostgreSql(dataSource)) {
				LOGGER.info("Skipping demo user seeding because the datasource is not PostgreSQL.");
				return;
			}

			List.of(
			userRecord("Abraham", "Lincoln", "ab@li.com", null, "049gv0nv", UserRole.CUSTOMER),
			userRecord("Bob", "DaBuilder", "bo@da.com", null, "1o2enoe2", UserRole.CUSTOMER),
			userRecord("Charlie", "Kirk", "ch@ki.com", null, "jkwfnw21", UserRole.ADMIN),
			userRecord("Delta", "Airlines", "de@ai.com", null, "03eof342)", UserRole.ADMIN)
			).forEach(seedUser -> upsertUser(jdbcTemplate, passwordHasher, seedUser));
		};
	}

	private static SeedUserRecord userRecord(
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		String rawPassword,
		UserRole role
	) {
		return new SeedUserRecord(firstName, lastName, email, phoneNumber, rawPassword, role);
	}

	private static boolean isPostgreSql(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metadata = connection.getMetaData();
			return metadata.getDatabaseProductName()
				.toLowerCase(Locale.ROOT)
				.contains("postgresql");
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to inspect datasource metadata before seeding users.", exception);
		}
	}

	private static void upsertUser(
		JdbcTemplate jdbcTemplate,
		PasswordHasher passwordHasher,
		SeedUserRecord seedUser
	) {
		String existingHash = jdbcTemplate.query(
			"select password_hash from users where email = ?",
			rs -> rs.next() ? rs.getString("password_hash") : null,
			seedUser.email()
		);

		String passwordHash = existingHash;
		if (passwordHash == null || !passwordHasher.matches(seedUser.rawPassword(), passwordHash)) {
			passwordHash = passwordHasher.hash(seedUser.rawPassword());
		}

		jdbcTemplate.update(
			"""
				insert into users (first_name, last_name, email, phone_number, password_hash, role, created_at)
				values (?, ?, ?, ?, ?, cast(? as user_role), ?)
				on conflict (email) do update
				set first_name = excluded.first_name,
				    last_name = excluded.last_name,
				    phone_number = excluded.phone_number,
				    password_hash = excluded.password_hash,
				    role = excluded.role,
				    created_at = coalesce(users.created_at, excluded.created_at)
				""",
			seedUser.firstName(),
			seedUser.lastName(),
			seedUser.email(),
			seedUser.phoneNumber(),
			passwordHash,
			seedUser.role().name(),
			Timestamp.valueOf(SEEDED_AT)
		);
	}

	private record SeedUserRecord(
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		String rawPassword,
		UserRole role
	) {
	}
}

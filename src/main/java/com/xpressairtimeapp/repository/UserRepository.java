package com.xpressairtimeapp.repository;

import com.xpressairtimeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Checks if a user exists by email.
	 *
	 * @param email the user's email address
	 * @return true if a user with the given email exists, otherwise false
	 */
	Boolean existsByEmail(String email);

	/**
	 * Retrieves a user by email.
	 *
	 * @param email the user's email address
	 * @return an optional containing the user if found, otherwise empty
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Checks if a user exists by wallet number.
	 *
	 * @param walletNumber the user's wallet number
	 * @return true if a user with the given wallet number exists, otherwise false
	 */
	Boolean existsByWalletNumber(String walletNumber);

	/**
	 * Retrieves a user by wallet number.
	 *
	 * @param walletNumber the user's wallet number
	 * @return the user with the given wallet number
	 */
	User findByWalletNumber(String walletNumber);

}

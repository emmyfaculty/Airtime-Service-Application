package com.xpressairtimeapp.serviceimpl;

import com.xpressairtimeapp.securityconfig.JwtTokenProvider;
import com.xpressairtimeapp.dto.*;
import com.xpressairtimeapp.entity.Role;
import com.xpressairtimeapp.entity.User;
import com.xpressairtimeapp.repository.UserRepository;
import com.xpressairtimeapp.service.UserService;
import com.xpressairtimeapp.utils.WalletUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * Creates a new user account and wallet.
	 *
	 * @param userRequest the user details for creating the account
	 * @return UserResponse containing the result of the account creation
	 */
	@Override
	@Transactional
	public UserResponse createAccount(UserRequest userRequest) {
		// Check if a user with the provided email already exists
		if (userRepository.existsByEmail(userRequest.getEmail())) {
			return UserResponse.builder()
					.responseCode(WalletUtils.WALLET_EXISTS_CODE)
					.responseMessage(WalletUtils.WALLET_EXISTS_MESSAGE)
					.walletInfo(null)
					.build();
		}

		// Create a new User object from the request data
		User newUser = User.builder()
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.otherName(userRequest.getOtherName())
				.gender(userRequest.getGender())
				.address(userRequest.getAddress())
				.stateOfOrigin(userRequest.getStateOfOrigin())
				.walletNumber(WalletUtils.generateWalletNumber()) // Generate unique wallet number
				.walletBalance(BigDecimal.ZERO) // Initialize wallet balance to zero
				.email(userRequest.getEmail())
				.password(passwordEncoder.encode(userRequest.getPassword())) // Encode the password for security
				.role(Role.ROLE_USER) // Assign user role
				.phoneNumber(userRequest.getPhoneNumber())
				.build();

		// Save the new user to the database
		User savedUser = userRepository.save(newUser);

		// Build and return the response with wallet information
		return UserResponse.builder()
				.responseCode(WalletUtils.WALLET_CREATION_SUCCESS)
				.responseMessage(WalletUtils.WALLET_CREATION_MESSAGE)
				.walletInfo(WalletInfo.builder()
						.walletBalance(savedUser.getWalletBalance())
						.walletNumber(savedUser.getWalletNumber())
						.walletName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName())
						.phoneNumber(savedUser.getPhoneNumber())
						.build())
				.build();
	}

	/**
	 * Authenticates a user and generates a JWT token upon successful login.
	 *
	 * @param loginDto the login details of the user
	 * @return AirtimeResponse containing the login response with JWT token
	 */
	public AirtimeResponse login(LoginDto loginDto) {
		// Authenticate the user using the provided email and password
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
		);

		// Generate and return a response with the JWT token
		return AirtimeResponse.builder()
				.responseCode("Login Success")
				.responseMessage(jwtTokenProvider.generateToken(authentication)) // Generate JWT token
				.build();
	}

	/**
	 * Funds a user's wallet by adding a specified amount.
	 *
	 * @param fundWalletRequest the details of the funding request
	 * @return AirtimeResponse containing the result of the wallet funding
	 */
	@Override
	@Transactional
	public AirtimeResponse fundWallet(FundWalletRequest fundWalletRequest) {
		// Check if the wallet exists
		boolean isWalletExist = userRepository.existsByWalletNumber(fundWalletRequest.getWalletNumber());
		if (!isWalletExist) {
			return AirtimeResponse.builder()
					.responseCode(WalletUtils.WALLET_NOT_EXIST_CODE)
					.responseMessage(WalletUtils.WALLET_NOT_EXIST_MESSAGE)
					.walletInfo(null)
					.build();
		}

		// Find the user associated with the provided wallet number
		User userToFund = userRepository.findByWalletNumber(fundWalletRequest.getWalletNumber());
		userToFund.setWalletBalance(userToFund.getWalletBalance().add(fundWalletRequest.getAmount())); // Update wallet balance
		userRepository.save(userToFund); // Save the updated user

		// Build and return the response with updated wallet information
		return AirtimeResponse.builder()
				.responseCode(WalletUtils.WALLET_CREDITED_SUCCESS)
				.responseMessage(WalletUtils.WALLET_CREDITED_SUCCESS_MESSAGE)
				.walletInfo(WalletInfo.builder()
						.walletBalance(userToFund.getWalletBalance())
						.walletNumber(fundWalletRequest.getWalletNumber())
						.walletName(userToFund.getFirstName() + " " + userToFund.getOtherName() + " " + userToFund.getLastName())
						.phoneNumber(userToFund.getPhoneNumber())
						.build())
				.build();
	}
}

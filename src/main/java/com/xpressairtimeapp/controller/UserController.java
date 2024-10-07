package com.xpressairtimeapp.controller;

import com.xpressairtimeapp.dto.*;
import com.xpressairtimeapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs", description = "The user API")
public class UserController {

	private final UserService userService;

	/**
	 * Creates a new user account and assigns a wallet number.
	 *
	 * @param userRequest the user request containing account creation details
	 * @return AirtimeResponse containing the account creation result
	 */
	@PostMapping
	@Operation(summary = "Create a new user account", description = "This API creates a new user account and assigned wallet number to the user"
			+ "The wallet number is a 10 digit number")
	@ApiResponse(responseCode = "201",
			description = "Http status code 201 is returned when a new user account is created successfully"
			+ "The response body contains the wallet number of the user created")
	public UserResponse createAccount(@RequestBody UserRequest userRequest) {
		return userService.createAccount(userRequest);
	}

	/**
	 * Authenticates a user by validating login credentials.
	 *
	 * @param loginDto the login request details
	 * @return AirtimeResponse containing the result of the login process
	 */
	@PostMapping("/login")
	public  AirtimeResponse login(@RequestBody LoginDto loginDto) {
		return userService.login(loginDto);
	}

	/**
	 * Funds a user's wallet with the specified amount.
	 *
	 * @param request the request object containing the wallet funding details
	 * @return AirtimeResponse with the result of the wallet credit operation
	 */
	@PostMapping("/fundWallet")
	@Operation(summary = "Fund Wallet",
			description = "Given a wallet number and amount, this API credits the user wallet with the amount")
	@ApiResponse(responseCode = "201",  description = "Http status code 201 is returned and wallet credited if the wallet exist successfully"
			+ "The response body contains the wallet balance of the user")
	public AirtimeResponse creditAccount(@RequestBody FundWalletRequest request) {
		return userService.fundWallet(request);
	}

}

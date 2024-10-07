package com.xpressairtimeapp.utils;

import java.time.Year;
import java.util.Random;

public class WalletUtils {
	private static final Random random = new Random();


		private WalletUtils() {
			throw new IllegalStateException("Utility class");
		}
	public static final String WALLET_EXISTS_CODE = "001";
	public static final String WALLET_EXISTS_MESSAGE = "This user has already been created";
	public static final String WALLET_CREATION_SUCCESS = "002";
	public static final String WALLET_CREATION_MESSAGE = "Wallet has been successfully created";
	public static final String WALLET_NOT_EXIST_CODE = "003";
	public static final String WALLET_NOT_EXIST_MESSAGE = "User with the provided Wallet does not exist";
	public static final String FAILED_TRANSACTION_CODE = "004";
	public static final String FAILED_TRANSACTION_MESSAGE = "The transaction was not successful";
	public static final String WALLET_CREDITED_SUCCESS = "005";
	public static final String WALLET_CREDITED_SUCCESS_MESSAGE = "User Wallet Credited Successfully";


	public static String generateWalletNumber() {

		/**
		 * walletNumber should be current year + randomSixDigit
		 */
		Year currentYear = Year.now();
		int min = 100000;
		int max = 999999;

		int randNumber = random.nextInt(max - min + 1) + min;


		String year = String.valueOf(currentYear.getValue()); // Use getValue() to get the year as an int
		String randomNumber = String.format("%06d", randNumber); // Ensure the random number is 6 digits long

		return year + randomNumber;
	}

}

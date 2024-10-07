package com.xpressairtimeapp.service;

import com.xpressairtimeapp.dto.*;

public interface UserService {
	UserResponse createAccount(UserRequest userRequest);
	AirtimeResponse fundWallet(FundWalletRequest fundWalletRequest);
	AirtimeResponse login(LoginDto loginDto);

}

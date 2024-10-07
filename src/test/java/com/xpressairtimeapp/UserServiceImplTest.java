package com.xpressairtimeapp;

import com.xpressairtimeapp.dto.*;
import com.xpressairtimeapp.entity.User;
import com.xpressairtimeapp.repository.UserRepository;
import com.xpressairtimeapp.securityconfig.JwtTokenProvider;
import com.xpressairtimeapp.serviceimpl.UserServiceImpl;
import com.xpressairtimeapp.utils.WalletUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private FundWalletRequest fundWalletRequest;
    private LoginDto loginDto;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up mock user data
        userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setOtherName("A");
        userRequest.setGender("Male");
        userRequest.setAddress("123 Main St");
        userRequest.setStateOfOrigin("State");
        userRequest.setPhoneNumber("1234567890");

        fundWalletRequest = new FundWalletRequest();
        fundWalletRequest.setWalletNumber("WALLET-123");
        fundWalletRequest.setAmount(BigDecimal.valueOf(100.00));

        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password");

        user = User.builder()
                .email(userRequest.getEmail())
                .password("encoded_password") // This should be the encoded password
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .phoneNumber(userRequest.getPhoneNumber())
                .walletNumber("WALLET-123")
                .walletBalance(BigDecimal.ZERO)
                .build();
    }

    @Test
    public void createAccount_WhenUserExists_ShouldReturnWalletExistsResponse() {
        // Arrange
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        // Act
        UserResponse response = userService.createAccount(userRequest);

        // Assert
        assertEquals(WalletUtils.WALLET_EXISTS_CODE, response.getResponseCode());
        assertEquals(WalletUtils.WALLET_EXISTS_MESSAGE, response.getResponseMessage());
    }

    @Test
    public void createAccount_WhenUserDoesNotExist_ShouldCreateAccountSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.createAccount(userRequest);

        // Assert
        assertEquals(WalletUtils.WALLET_CREATION_SUCCESS, response.getResponseCode());
        assertEquals(WalletUtils.WALLET_CREATION_MESSAGE, response.getResponseMessage());
        assertNotNull(response.getWalletInfo());
    }

    @Test
    public void login_WhenCredentialsAreCorrect_ShouldReturnToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt.token.here");

        // Act
        AirtimeResponse response = userService.login(loginDto);

        // Assert
        assertEquals("Login Success", response.getResponseCode());
        assertEquals("jwt.token.here", response.getResponseMessage());
    }

    @Test
    public void fundWallet_WhenWalletDoesNotExist_ShouldReturnWalletNotExistResponse() {
        // Arrange
        when(userRepository.existsByWalletNumber(fundWalletRequest.getWalletNumber())).thenReturn(false);

        // Act
        AirtimeResponse response = userService.fundWallet(fundWalletRequest);

        // Assert
        assertEquals(WalletUtils.WALLET_NOT_EXIST_CODE, response.getResponseCode());
        assertEquals(WalletUtils.WALLET_NOT_EXIST_MESSAGE, response.getResponseMessage());
    }

    @Test
    public void fundWallet_WhenWalletExists_ShouldCreditWalletSuccessfully() {
        // Arrange
        when(userRepository.existsByWalletNumber(fundWalletRequest.getWalletNumber())).thenReturn(true);
        when(userRepository.findByWalletNumber(fundWalletRequest.getWalletNumber())).thenReturn(user);

        // Act
        AirtimeResponse response = userService.fundWallet(fundWalletRequest);

        // Assert
        assertEquals(WalletUtils.WALLET_CREDITED_SUCCESS, response.getResponseCode());
        assertEquals(WalletUtils.WALLET_CREDITED_SUCCESS_MESSAGE, response.getResponseMessage());
        assertNotNull(response.getWalletInfo());
        assertEquals(BigDecimal.valueOf(100.00), response.getWalletInfo().getWalletBalance());
    }
}

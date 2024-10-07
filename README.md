# Airtime Service Application

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Authentication](#authentication)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Airtime Service Application is a Spring Boot-based application that enables users to purchase airtime through an external API. This service handles the preparation of requests, communication with the external API, and the processing of responses. The application is designed to ensure reliability, maintainability, and ease of testing. It also includes user registration and login features secured by JWT tokens for authentication.

## Features

- **User Registration**: Allows new users to register by providing necessary credentials.
- **User Login**: Enables users to authenticate and receive a JWT token for subsequent requests.
- **JWT Authentication**: Secure access to the application using JSON Web Tokens.
- **Purchase Airtime**: Allows users to request airtime purchases by sending requests to an external API.
- **Error Handling**: Robust error handling that provides meaningful responses based on the API's feedback.
- **Testing**: Comprehensive unit tests implemented with JUnit and Mockito to ensure service reliability and accuracy.

## Technologies Used

- **Java**: The primary programming language used for building the service.
- **Spring Boot**: Framework for developing the service with ease of configuration and deployment.
- **Spring Security**: Provides authentication and authorization for the application.
- **JWT**: JSON Web Tokens for secure user authentication.
- **JUnit 5**: Testing framework used for unit testing.
- **Mockito**: Library used for mocking dependencies in tests.
- **Jackson**: For JSON serialization and deserialization.

## Setup and Installation

To set up the Airtime Service Application on your local machine, follow these steps:

1. **Clone the repository**:
    ```bash
    git clone https://github.com/emmyfaculty/xpress-airtime-app.git
    cd airtime-service-app
    ```

2. **Build the project**:
    Ensure you have Maven installed, then run:
    ```bash
    mvn clean install
    ```

3. **Run the application**:
    Start the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```

4. **Configure application properties**:
    Update your `application.properties` file with the necessary configuration for your external API, JWT settings, and database connection.

## Usage

After setting up the application, you can interact with the Airtime Service through its API endpoints.

### Authentication Endpoints

- **POST /api/user**
    - **Request Body**: JSON representation of user registration details (e.g., username, password, email).
    - **Response**: Returns a confirmation message upon successful registration.

- **POST /api/user/login**
    - **Request Body**: JSON representation of login credentials (e.g., username, password).
    - **Response**: Returns a JWT token for authenticated requests.
  
- **POST /api/user/fundWallet**
    - **Request Body**: JSON representation of funding wallet (e.g., walletNumber, amount).
    - **Response**: Returns a JWT token for authenticated requests.

### Airtime Purchase Endpoint

- **POST /api/airtime/purchase**
    - **Request Body**: JSON representation of `AirtimeRequestDto`
    - **Headers**: Include `Authorization: Bearer <JWT_TOKEN>`
    - **Response**: Returns an `AirtimeResponse` containing the result of the purchase request.

### Example Request for Registration

```json
{
  "firstName": "",
  "lastName": "",
  "otherName": "",
  "gender": "",
  "address": "",
  "stateOfOrigin": "",
  "email": "",
  "phoneNumber": "",
  "password": ""
}
```

### Example Request for Login

```json
{
    "username": "testuser",
    "password": "securePassword"
}
```
### Example Request for Funding wallet

```json
{
  "walletNumber": "1234567898",
  "amount": 100000
}
```

### Example Request for Airtime Purchase

```json
{
  "requestId": "",
  "uniqueCode": "",
  "details": {
    "phoneNumber": "07061645968",
    "amount": 500
  }
}
```

### Example Response

```json
{
    "responseCode": "00",
    "responseMessage": "Success",
    "airtimeApiResponse": {
        "requestId": 123,
        "referenceId": "abc",
        "data": "TestData"
    }
}
```

## Authentication

The application uses JWT for securing endpoints. Upon successful login, a JWT token is generated and returned to the user. This token should be included in the `Authorization` header for all subsequent requests to protected endpoints.

### Example of Authorization Header

```http
Authorization: Bearer <your_jwt_token>
```

## Testing

To run the unit tests for the Airtime Service, execute the following command:

```bash
mvn test
```

### Testing Overview

- **AirtimeServiceImplTest**: This class contains tests for the `AirtimeServiceImpl` class, covering:
    - Successful airtime purchase
    - Failed airtime purchase
    - Exception handling during API calls

- **AuthServiceTest**: Contains tests for user registration and login functionalities, ensuring that the authentication process works as expected.

### Example Test Case

The following is an example test case that verifies successful airtime purchases:

```java
@Test
public void testPurchaseAirtimeSuccess() throws JsonProcessingException {
    // Arrange
    AirtimeRequestDto requestDto = new AirtimeRequestDto();
    AirtimeApiResponse apiResponse = AirtimeApiResponse.builder()
            .responseCode("00")
            .responseMessage("Success")
            .requestId(Long.valueOf("123"))
            .referenceId("abc")
            .data("TestData")
            .build();

    ResponseEntity<AirtimeApiResponse> mockResponse = ResponseEntity.ok(apiResponse);
    when(restTemplate.exchange(...)).thenReturn(mockResponse);

    // Act
    AirtimeResponse response = airtimeService.purchaseAirtime(requestDto);

    // Assert
    assertNotNull(response);
    assertEquals("00", response.getResponseCode());
    assertEquals("Success", response.getResponseMessage());
}
```

## Accessing the Application:
-------------------

Api documentation available at : http://localhost:8080/swagger-ui/index.html
---

## Contributing

Contributions are welcome! If you have suggestions for improvements or bug fixes, please create a pull request or open an issue.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/MyFeature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/MyFeature`)
5. Open a pull request

[//]: # (## License)

[//]: # ()
[//]: # (This project is licensed under the MIT License - see the [LICENSE]&#40;LICENSE&#41; file for details.)
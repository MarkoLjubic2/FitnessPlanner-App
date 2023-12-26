package org.raf.sk.userservice.service;

import org.raf.sk.userservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing user-service operations.
 */
public interface UserService {

    /**
     * Retrieves a paginated list of users.
     *
     * @param pageable Pageable object containing pagination information.
     * @return Response object containing a Page of UserDto.
     */
    Response<Page<UserDto>> findAll(Pageable pageable);

    /**
     * Checks if a user with the specified ID exists.
     *
     * @param userId ID of the user to check.
     * @return Response object containing a Boolean indicating the existence of the user.
     */
    Response<Boolean> findUser(Long userId);

    /**
     * Retrieves user data for the specified user ID.
     *
     * @param userId ID of the user to retrieve data for.
     * @return Response object containing the UserDto for the specified user ID.
     */
    Response<UserDto> getUserData(Long userId);

    /**
     * Changes the total sessions for a user.
     *
     * @param jwt    JWT token for authentication.
     * @param userId ID of the user whose sessions are to be modified.
     * @param value  New value for the total sessions.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> changeTotalSessions(String jwt, Long userId, int value);

    /**
     * Bans a user based on the provided username.
     *
     * @param jwt      JWT token for authentication.
     * @param username Username of the user to be banned.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> banUser(String jwt, String username);

    /**
     * Unbans a user based on the provided username.
     *
     * @param jwt      JWT token for authentication.
     * @param username Username of the user to be unbanned.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> unbanUser(String jwt, String username);

    /**
     * Adds a new user.
     *
     * @param createUserDto Data for creating a new user.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> addUser(CreateUserDto createUserDto);

    /**
     * Adds a new manager.
     *
     * @param createManagerDto Data for creating a new manager.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> addManager(CreateManagerDto createManagerDto);

    /**
     * Updates an existing user.
     *
     * @param jwt             JWT token for authentication.
     * @param updateUserDto   Data for updating an existing user.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> updateUser(String jwt, UpdateUserDto updateUserDto);

    /**
     * Updates an existing manager.
     *
     * @param jwt               JWT token for authentication.
     * @param updateManagerDto Data for updating an existing manager.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> updateManager(String jwt, UpdateManagerDto updateManagerDto);

    /**
     * Performs user authentication and returns a token response.
     *
     * @param tokenRequestDto Data for user authentication.
     * @return Response object containing a TokenResponseDto.
     */
    Response<TokenResponseDto> login(TokenRequestDto tokenRequestDto);

    /**
     * Performs user registration.
     *
     * @param userDto Data for user registration.
     * @return Response object containing a Boolean indicating the success of the operation.
     */
    Response<Boolean> verifyUser(UserDto userDto);

}

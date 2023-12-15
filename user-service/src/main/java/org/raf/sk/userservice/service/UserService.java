package org.raf.sk.userservice.service;

import org.raf.sk.userservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Response<Page<UserDto>> findAll(Pageable pageable);

    Response<Boolean> findUser(Long userId);

    Response<UserDto> getUserData(Long userId);

    Response<Boolean> changeTotalSessions(Long userId, int value);

    Response<Boolean> banUser(String username);

    Response<Boolean> unbanUser(String username);

    Response<Boolean> addUser(CreateUserDto createUserDto);

    Response<Boolean> addManager(CreateManagerDto createManagerDto);

    Response<Boolean> updateUser(String jwt, UpdateUserDto updateUserDto);

    Response<Boolean> updateManager(String jwt, UpdateManagerDto updateManagerDto);

    Response<TokenResponseDto> login(TokenRequestDto tokenRequestDto);

}

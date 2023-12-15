package org.raf.sk.userservice.service;

import org.raf.sk.userservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    @Override
    public Response<Page<UserDto>> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Response<Boolean> findUser(Long userId) {
        return null;
    }

    @Override
    public Response<UserDto> getUserData(Long userId) {
        return null;
    }

    @Override
    public Response<Boolean> changeTotalSessions(Long userId, int value) {
        return null;
    }

    @Override
    public Response<Boolean> banUser(String username) {
        return null;
    }

    @Override
    public Response<Boolean> unbanUser(String username) {
        return null;
    }

    @Override
    public Response<Boolean> addUser(CreateUserDto createUserDto) {
        return null;
    }

    @Override
    public Response<Boolean> addManager(CreateManagerDto createManagerDto) {
        return null;
    }

    @Override
    public Response<Boolean> updateUser(String jwt, UpdateUserDto updateUserDto) {
        return null;
    }

    @Override
    public Response<Boolean> updateManager(String jwt, UpdateManagerDto updateManagerDto) {
        return null;
    }

    @Override
    public Response<TokenResponseDto> login(TokenRequestDto tokenRequestDto) {
        return null;
    }
}

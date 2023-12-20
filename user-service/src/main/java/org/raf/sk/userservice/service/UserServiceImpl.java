package org.raf.sk.userservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.userservice.dto.*;
import org.raf.sk.userservice.listener.MessageHelper;
import org.raf.sk.userservice.mapper.UserMapper;
import org.raf.sk.userservice.repository.RoleRepository;
import org.raf.sk.userservice.repository.StatusRepository;
import org.raf.sk.userservice.repository.UserRepository;
import org.raf.sk.userservice.security.tokenService.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private BCryptPasswordEncoder encoder;
    private TokenService tokenService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private RoleRepository roleRepository;
    private StatusRepository statusRepository;
    private MessageHelper messageHelper;

    @Override
    public Response<Page<UserDto>> findAll(Pageable pageable) {
        return new Response<>(200, "All users", userRepository.findAll(pageable).map(userMapper::userToUserDto));
    }

    @Override
    public Response<Boolean> findUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Response<>(200, "User found", true))
                .orElse(new Response<>(404, "User not found", false));
    }

    @Override
    public Response<UserDto> getUserData(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Response<>(200, "User found", userMapper.userToUserDto(user)))
                .orElse(new Response<>(404, "User not found", null));
    }

    @Override
    public Response<Boolean> changeTotalSessions(Long userId, int value) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setTotalSessions(user.getTotalSessions() + value);
                    userRepository.save(user);
                    return new Response<>(200, "Total sessions changed", true);
                })
                .orElse(new Response<>(404, "User not found", false));
    }

    @Override
    public Response<Boolean> banUser(String username) {
        return userRepository.findUserByUsername(username)
                .map(user -> {
                    if ("BANNED".equals(user.getUserStatus().getName())) {
                        return new Response<>(409, "User is already banned", false);
                    }
                    statusRepository.findStatusByName("BANNED").ifPresent(user::setUserStatus);
                    userRepository.save(user);
                    return new Response<>(200, "User banned", true);
                })
                .orElse(new Response<>(404, "User not found", false));
    }

    @Override
    public Response<Boolean> unbanUser(String username) {
        return userRepository.findUserByUsername(username)
                .map(user -> {
                    if ("VERIFIED".equals(user.getUserStatus().getName())) {
                        return new Response<>(409, "User is not banned", false);
                    }
                    statusRepository.findStatusByName("VERIFIED").ifPresent(user::setUserStatus);
                    userRepository.save(user);
                    return new Response<>(200, "User unbanned", true);
                })
                .orElse(new Response<>(404, "User not found", false));
    }

    // TODO: Ubaciti verifikaciju i notifikaciju
    @Override
    public Response<Boolean> addUser(CreateUserDto createUserDto) {
        return userRepository.findUserByEmail(createUserDto.getEmail())
                .map(user -> new Response<>(400, "User with this email already exists", false))
                .orElseGet(() -> userRepository.findUserByUsername(createUserDto.getUsername())
                        .map(user -> new Response<>(400, "User with this username already exists", false))
                        .orElseGet(() -> {
                            createUserDto.setPassword(encoder.encode(createUserDto.getPassword()));
                            userRepository.save(userMapper.createUserDtoToUser(createUserDto));
                            return new Response<>(200, "User created", true);
                        }));
    }

    // TODO: Ubaciti verifikaciju i notifikaciju
    @Override
    public Response<Boolean> addManager(CreateManagerDto createManagerDto) {
        return userRepository.findUserByEmail(createManagerDto.getEmail())
                .map(user -> new Response<>(400, "User with this email already exists", false))
                .orElseGet(() -> userRepository.findUserByUsername(createManagerDto.getUsername())
                        .map(user -> new Response<>(400, "User with this username already exists", false))
                        .orElseGet(() -> {
                            createManagerDto.setPassword(encoder.encode(createManagerDto.getPassword()));
                            userRepository.save(userMapper.createManagerDtoToManager(createManagerDto));
                            return new Response<>(200, "Manager created", true);
                        }));
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
        return userRepository.findUserByUsername(tokenRequestDto.getUsername())
                .map(user -> {
                    if ("BANNED".equals(user.getUserStatus().getName())) {
                        return new Response<>(403, "User is banned", new TokenResponseDto(null));
                    }
                    if (!encoder.matches(tokenRequestDto.getPassword(), user.getPassword())) {
                        return new Response<>(403, "Invalid credentials", new TokenResponseDto(null));
                    }
                    return new Response<>(200, "User logged in", new TokenResponseDto(tokenService.generate(userMapper.userToClaims(user))));
                })
                .orElse(new Response<>(404, "User not found", new TokenResponseDto(null)));
    }

}

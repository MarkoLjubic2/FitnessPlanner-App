package org.raf.sk.userservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.userservice.dto.*;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;
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
import java.util.Optional;
import java.util.regex.Pattern;

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
    public Response<Boolean> changeTotalSessions(String jwt, Long userId, int value) {
        Response<Boolean> adminAndTokenCheck = checkAdminAndTokenValidity(jwt);
        if (adminAndTokenCheck.getStatusCode() != 200) return adminAndTokenCheck;

        return userRepository.findById(userId)
                .map(user -> {
                    user.setTotalSessions(user.getTotalSessions() + value);
                    userRepository.save(user);
                    return new Response<>(200, "Total sessions changed", true);
                })
                .orElse(new Response<>(404, "User not found", false));
    }

    @Override
    public Response<Boolean> banUser(String jwt, String username) {
        Response<Boolean> adminAndTokenCheck = checkAdminAndTokenValidity(jwt);
        if (adminAndTokenCheck.getStatusCode() != 200) return adminAndTokenCheck;

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
    public Response<Boolean> unbanUser(String jwt, String username) {
        Response<Boolean> adminAndTokenCheck = checkAdminAndTokenValidity(jwt);
        if (adminAndTokenCheck.getStatusCode() != 200) return adminAndTokenCheck;

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
        if (!isEmailValid(createUserDto.getEmail()))
            return new Response<>(400, "Invalid email format", false);

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
        Response<Boolean> validationResponse = checkUserAndTokenValidity(jwt, updateUserDto);
        if (validationResponse.getStatusCode() != 200) return validationResponse;

        return userRepository.findUserByUsername(updateUserDto.getUsername())
                .map(user -> {
                    Optional.ofNullable(updateUserDto.getFirstName()).ifPresent(user::setFirstName);
                    Optional.ofNullable(updateUserDto.getLastName()).ifPresent(user::setLastName);
                    Optional.ofNullable(updateUserDto.getEmail()).ifPresent(user::setEmail);
                    userRepository.save(user);
                    return new Response<>(200, "User updated", true);
                })
                .orElse(new Response<>(404, "User not found", false));
    }

    @Override
    public Response<Boolean> updateManager(String jwt, UpdateManagerDto updateManagerDto) {
        Response<Boolean> validationResponse = checkUserAndTokenValidity(jwt, updateManagerDto);
        if (validationResponse.getStatusCode() != 200) return validationResponse;

        return userRepository.findUserByUsername(updateManagerDto.getUsername())
                .map(user -> {
                    Optional.ofNullable(updateManagerDto.getFirstName()).ifPresent(user::setFirstName);
                    Optional.ofNullable(updateManagerDto.getLastName()).ifPresent(user::setLastName);
                    Optional.ofNullable(updateManagerDto.getEmail()).ifPresent(user::setEmail);
                    Optional.ofNullable(updateManagerDto.getHall()).ifPresent(user::setHall);
                    Optional.ofNullable(updateManagerDto.getHireDate()).ifPresent(user::setHireDate);
                    userRepository.save(user);
                    return new Response<>(200, "Manager updated", true);
                })
                .orElse(new Response<>(404, "Manager not found", false));
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

    // Helper
    private Response<Boolean> checkAdminAndTokenValidity(String jwt) {
        String role = tokenService.getRole(jwt);
        if (role == null || !role.equals("ADMIN"))
            return new Response<>(401, "Unauthorized", false);
        if (!tokenService.isTokenValid(jwt))
            return new Response<>(401, "Invalid or expired token", false);

        return new Response<>(200, "Valid admin and token", true);
    }

    private Response<Boolean> checkUserAndTokenValidity(String jwt, AbstractUserDto dto) {
        String usernameFromToken = tokenService.parseToken(jwt).getSubject();
        if (usernameFromToken == null || !usernameFromToken.equals(dto.getUsername()))
            return new Response<>(401, "Unauthorized", false);
        if (!tokenService.isTokenValid(jwt))
            return new Response<>(401, "Invalid or expired token", false);
        if (dto.getEmail() != null && !isEmailValid(dto.getEmail()))
            return new Response<>(400, "Invalid email format", false);
        if (dto.getEmail() != null && userRepository.findUserByEmail(dto.getEmail()).isPresent())
            return new Response<>(400, "Email is already in use", false);

        return new Response<>(200, "User and token are valid", true);
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

}

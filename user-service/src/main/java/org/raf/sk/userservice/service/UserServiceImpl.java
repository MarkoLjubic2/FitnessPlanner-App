package org.raf.sk.userservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.userservice.client.appointment.AppointmentUserDto;
import org.raf.sk.userservice.client.notification.ActivationDto;
import org.raf.sk.userservice.client.notification.PasswordChangeDto;
import org.raf.sk.userservice.client.notification.NotificationMQ;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.dto.*;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;
import org.raf.sk.userservice.listener.MessageHelper;
import org.raf.sk.userservice.mapper.UserMapper;
import org.raf.sk.userservice.repository.StatusRepository;
import org.raf.sk.userservice.repository.UserRepository;
import org.raf.sk.userservice.security.tokenService.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.raf.sk.userservice.constants.Constants.*;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private BCryptPasswordEncoder encoder;
    private TokenService tokenService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private StatusRepository statusRepository;
    private MessageHelper messageHelper;
    private final JmsTemplate jmsTemplate;

    @Override
    public Response<Page<UserDto>> findAll(String jwt, Pageable pageable) {
        Response<Boolean> adminAndTokenCheck = checkAdminAndTokenValidity(jwt);
        if (adminAndTokenCheck.getStatusCode() != STATUS_OK)
            return new Response<>(adminAndTokenCheck.getStatusCode(), adminAndTokenCheck.getMessage(), null);

        return new Response<>(STATUS_OK, "All users", userRepository.findAll(pageable).map(userMapper::userToUserDto));
    }

    @Override
    public Response<Boolean> userExists(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Response<>(STATUS_OK, "User found", true))
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    @Override
    public Response<UserDto> getUserData(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Response<>(STATUS_OK, "User found", userMapper.userToUserDto(user)))
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", null));
    }

    @Override
    public Response<Boolean> changeTotalSessions(Long userId, int value) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setTotalSessions(user.getTotalSessions() + value);
                    userRepository.save(user);
                    return new Response<>(STATUS_OK, "Total sessions changed", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    @Override
    public Response<Boolean> banUser(String jwt, String username) {
        Response<Boolean> adminAndTokenCheck = checkAdminAndTokenValidity(jwt);
        if (adminAndTokenCheck.getStatusCode() != STATUS_OK) return adminAndTokenCheck;

        return userRepository.findUserByUsername(username)
                .map(user -> {
                    if ("BANNED".equals(user.getUserStatus().getName())) {
                        return new Response<>(STATUS_CONFLICT, "User is already banned", false);
                    }
                    statusRepository.findStatusByName("BANNED").ifPresent(user::setUserStatus);
                    userRepository.save(user);
                    return new Response<>(STATUS_OK, "User banned", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    @Override
    public Response<Boolean> unbanUser(String jwt, String username) {
        Response<Boolean> adminAndTokenCheck = checkAdminAndTokenValidity(jwt);
        if (adminAndTokenCheck.getStatusCode() != STATUS_OK) return adminAndTokenCheck;

        return userRepository.findUserByUsername(username)
                .map(user -> {
                    if ("VERIFIED".equals(user.getUserStatus().getName())) {
                        return new Response<>(STATUS_CONFLICT, "User is not banned", false);
                    }
                    statusRepository.findStatusByName("VERIFIED").ifPresent(user::setUserStatus);
                    userRepository.save(user);
                    return new Response<>(STATUS_OK, "User unbanned", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    @Override
    public Response<Boolean> addUser(CreateUserDto createUserDto) {
        if (!isEmailValid(createUserDto.getEmail()))
            return new Response<>(STATUS_BAD_REQUEST, "Invalid email format", false);

        return userRepository.findUserByEmail(createUserDto.getEmail())
                .map(user -> new Response<>(STATUS_BAD_REQUEST, "User with this email already exists", false))
                .orElseGet(() -> userRepository.findUserByUsername(createUserDto.getUsername())
                        .map(user -> new Response<>(STATUS_BAD_REQUEST, "User with this username already exists", false))
                        .orElseGet(() -> {
                            createUserDto.setPassword(encoder.encode(createUserDto.getPassword()));
                            userRepository.save(userMapper.createUserDtoToUser(createUserDto));

                            ActivationDto activationDto = new ActivationDto(createUserDto.getEmail(), createUserDto.getUsername(), tokenService.generate(userMapper.userToClaims(userMapper.createUserDtoToUser(createUserDto))));
                            NotificationMQ<ActivationDto> msg = new NotificationMQ<>("ACTIVATION", activationDto);
                            jmsTemplate.convertAndSend("send_emails", messageHelper.createTextMessage(msg));

                            return new Response<>(STATUS_OK, "User created", true);
                        }));
    }

    @Override
    public Response<Boolean> addManager(CreateManagerDto createManagerDto) {
        return userRepository.findUserByEmail(createManagerDto.getEmail())
                .map(user -> new Response<>(STATUS_BAD_REQUEST, "User with this email already exists", false))
                .orElseGet(() -> userRepository.findUserByUsername(createManagerDto.getUsername())
                        .map(user -> new Response<>(STATUS_BAD_REQUEST, "User with this username already exists", false))
                        .orElseGet(() -> {
                            createManagerDto.setPassword(encoder.encode(createManagerDto.getPassword()));
                            userRepository.save(userMapper.createManagerDtoToManager(createManagerDto));
                            return new Response<>(STATUS_OK, "Manager created", true);
                        }));
    }

    @Override
    public Response<Boolean> updateUser(String jwt, UpdateUserDto updateUserDto) {
        Response<Boolean> validationResponse = checkUserAndTokenValidity(jwt, updateUserDto);
        if (validationResponse.getStatusCode() != STATUS_OK) return validationResponse;

        return userRepository.findById(updateUserDto.getId())
                .map(user -> {
                    Optional.ofNullable(updateUserDto.getFirstName()).ifPresent(user::setFirstName);
                    Optional.ofNullable(updateUserDto.getLastName()).ifPresent(user::setLastName);
                    Optional.ofNullable(updateUserDto.getEmail()).ifPresent(user::setEmail);
                    userRepository.save(user);
                    return new Response<>(STATUS_OK, "User updated", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    @Override
    public Response<Boolean> updateManager(String jwt, UpdateManagerDto updateManagerDto) {
        Response<Boolean> validationResponse = checkUserAndTokenValidity(jwt, updateManagerDto);
        if (validationResponse.getStatusCode() != STATUS_OK) return validationResponse;

        return userRepository.findUserByUsername(updateManagerDto.getUsername())
                .map(user -> {
                    Optional.ofNullable(updateManagerDto.getFirstName()).ifPresent(user::setFirstName);
                    Optional.ofNullable(updateManagerDto.getLastName()).ifPresent(user::setLastName);
                    Optional.ofNullable(updateManagerDto.getEmail()).ifPresent(user::setEmail);
                    Optional.ofNullable(updateManagerDto.getHallId()).ifPresent(user::setHallId);
                    Optional.ofNullable(updateManagerDto.getHireDate()).ifPresent(user::setHireDate);
                    userRepository.save(user);
                    return new Response<>(STATUS_OK, "Manager updated", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "Manager not found", false));
    }

    @Override
    public Response<Boolean> changePassword(String jwt, ChangePasswordDto changePasswordDto) {
        Response<Boolean> validationResponse = checkUserAndTokenValidity(jwt, changePasswordDto);
        if (validationResponse.getStatusCode() != STATUS_OK) return validationResponse;
        return userRepository.findById(changePasswordDto.getId())
                .map(user -> {
                    if (!encoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
                        return new Response<>(STATUS_FORBIDDEN, "Invalid credentials", false);
                    }

                    user.setPassword(encoder.encode(changePasswordDto.getNewPassword()));
                    userRepository.save(user);

                    PasswordChangeDto notificationDto = new PasswordChangeDto(user.getUsername(), user.getEmail());
                    NotificationMQ<PasswordChangeDto> msg = new NotificationMQ<>("CHANGE_PASSWORD", notificationDto);
                    jmsTemplate.convertAndSend("send_emails", messageHelper.createTextMessage(msg));

                    return new Response<>(STATUS_OK, "Password changed", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    @Override
    public Response<TokenResponseDto> login(TokenRequestDto tokenRequestDto) {
        return userRepository.findUserByUsername(tokenRequestDto.getUsername())
                .map(user -> {
                    if ("BANNED".equals(user.getUserStatus().getName()) || "UNVERIFIED".equals(user.getUserStatus().getName())) {
                        return new Response<>(STATUS_FORBIDDEN, "Status forbidden", new TokenResponseDto(null));
                    }
                    if (!encoder.matches(tokenRequestDto.getPassword(), user.getPassword())) {
                        return new Response<>(STATUS_FORBIDDEN, "Invalid credentials", new TokenResponseDto(null));
                    }
                    return new Response<>(STATUS_OK, "User logged in", new TokenResponseDto(tokenService.generate(userMapper.userToClaims(user))));
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", new TokenResponseDto(null)));
    }

    @Override
    public Response<Boolean> verifyUser(String jwt) {
        String username = tokenService.getUsername(jwt);
        return userRepository.findUserByUsername(username)
                .map(user -> {
                    if ("VERIFIED".equals(user.getUserStatus().getName())) {
                        return new Response<>(STATUS_CONFLICT, "User is already verified", false);
                    }
                    statusRepository.findStatusByName("VERIFIED").ifPresent(user::setUserStatus);
                    userRepository.save(user);
                    return new Response<>(STATUS_OK, "User verified", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    @Override
    public Response<AppointmentUserDto> getAppointmentUserData(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Response<>(STATUS_OK, "User found", userMapper.userToAppointmentUserDto(user)))
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", null));
    }

    @Override
    public Response<ManagerDto> getManagerData(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Response<>(STATUS_OK, "Manager found", userMapper.userToManagerDto(user)))
                .orElse(new Response<>(STATUS_NOT_FOUND, "Manager not found", null));
    }

    // Helper

    private Response<Boolean> checkAdminAndTokenValidity(String jwt) {
        String role = tokenService.getRole(jwt);
        if (role == null || !role.equals("ADMIN"))
            return new Response<>(STATUS_UNAUTHORIZED, "Unauthorized", false);

        return new Response<>(STATUS_OK, "Valid admin and token", true);
    }

    private Response<Boolean> checkUserAndTokenValidity(String jwt, AbstractUserDto dto) {
        Long id = tokenService.getUserId(jwt);
        Long dtoId = dto.getId();
        System.out.println(id+" "+dtoId);
        if (id == null || !id.equals(dtoId))
            return new Response<>(STATUS_UNAUTHORIZED, "Unauthorized", false);
        if (!(dto instanceof ChangePasswordDto)) {
            if (dto.getEmail() != null && !isEmailValid(dto.getEmail()))
                return new Response<>(STATUS_NOT_FOUND, "Invalid email format", false);
            Optional<User> user = userRepository.findUserByEmail(dto.getEmail());
            if (dto.getEmail() != null && user.isPresent() && !user.get().getId().equals(dtoId)) {
                return new Response<>(STATUS_NOT_FOUND, "Email is already in use", false);
            }
        }
        return new Response<>(STATUS_OK, "User and token are valid", true);
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

}

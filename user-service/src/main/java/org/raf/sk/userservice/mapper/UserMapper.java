package org.raf.sk.userservice.mapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.dto.CreateManagerDto;
import org.raf.sk.userservice.dto.CreateUserDto;
import org.raf.sk.userservice.dto.UpdateUserDto;
import org.raf.sk.userservice.dto.UserDto;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;
import org.raf.sk.userservice.repository.RoleRepository;
import org.raf.sk.userservice.repository.StatusRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;

    public UserDto userToUserDto(User user) {
        return Optional.ofNullable(user)
                .map(u -> {
                    UserDto userDto = new UserDto();
                    userDto.setId(u.getId());
                    userDto.setEmail(u.getEmail());
                    userDto.setFirstName(u.getFirstName());
                    userDto.setLastName(u.getLastName());
                    userDto.setUsername(u.getUsername());
                    userDto.setDateOfBirth(u.getDateOfBirth());
                    userDto.setUserRole(u.getUserRole());
                    userDto.setUserStatus(u.getUserStatus());
                    userDto.setLicenceID(u.getLicenseID());
                    userDto.setTotalSessions(u.getTotalSessions());
                    return userDto;
                })
                .orElse(null);
    }

    public User createUserDtoToUser(CreateUserDto createUserDto) {
        return Optional.of(transferStandardDataToUser(createUserDto))
                .map(user -> {
                    roleRepository.findRoleByName("CLIENT").ifPresent(user::setUserRole);
                    statusRepository.findStatusByName("UNVERIFIED").ifPresent(user::setUserStatus);
                    user.setLicenseID(createUserDto.getLicenceID());
                    user.setTotalSessions(0);
                    return user;
                })
                .orElse(null);
    }

    public CreateUserDto userToCreateUserDto(User user) {
        return Optional.ofNullable(user)
                .map(u -> {
                    CreateUserDto createUserDto = new CreateUserDto();
                    createUserDto.setEmail(u.getEmail());
                    createUserDto.setFirstName(u.getFirstName());
                    createUserDto.setLastName(u.getLastName());
                    createUserDto.setUsername(u.getUsername());
                    createUserDto.setPassword(u.getPassword());
                    createUserDto.setLicenceID(u.getLicenseID());
                    createUserDto.setTotalSessions(u.getTotalSessions());
                    return createUserDto;
                })
                .orElse(null);
    }

    public UpdateUserDto userToUpdateUserDto(User user) {
        return Optional.ofNullable(user)
                .map(u -> {
                    UpdateUserDto updateUserDto = new UpdateUserDto();
                    updateUserDto.setEmail(u.getEmail());
                    updateUserDto.setFirstName(u.getFirstName());
                    updateUserDto.setLastName(u.getLastName());
                    updateUserDto.setUsername(u.getUsername());
                    updateUserDto.setDateOfBirth(u.getDateOfBirth());
                    return updateUserDto;
                })
                .orElse(null);
    }

    public User updateUserDtoToUser(UpdateUserDto updateUserDto) {
        return Optional.ofNullable(updateUserDto)
                .map(dto -> {
                    User user = new User();
                    user.setEmail(dto.getEmail());
                    user.setFirstName(dto.getFirstName());
                    user.setLastName(dto.getLastName());
                    user.setUsername(dto.getUsername());
                    user.setDateOfBirth(dto.getDateOfBirth());
                    return user;
                })
                .orElse(null);
    }

    public User createManagerDtoToManager(CreateManagerDto createManagerDto){
        return Optional.of(transferStandardDataToUser(createManagerDto))
                .map(manager -> {
                    roleRepository.findRoleByName("MANAGER").ifPresent(manager::setUserRole);
                    manager.setLicenseID("-1");
                    manager.setTotalSessions(-1);
                    return manager;
                })
                .orElse(null);
    }

    public CreateManagerDto managerToCreateManagerDto(User manager){
        return Optional.ofNullable(manager)
                .map(m -> {
                    CreateManagerDto createManagerDto = new CreateManagerDto();
                    createManagerDto.setEmail(m.getEmail());
                    createManagerDto.setFirstName(m.getFirstName());
                    createManagerDto.setLastName(m.getLastName());
                    createManagerDto.setUsername(m.getUsername());
                    createManagerDto.setPassword(m.getPassword());
                    return createManagerDto;
                })
                .orElse(null);
    }

    public Claims userToClaims(User user) {
        return Optional.ofNullable(user)
                .map(u -> {
                    Claims claims = Jwts.claims();
                    claims.put("id", u.getId());
                    claims.put("username", u.getUsername());
                    claims.put("role", u.getUserRole().getName());
                    claims.put("time", System.currentTimeMillis() / 1000);
                    return claims;
                })
                .orElse(null);
    }

    // Helper
    private User transferStandardDataToUser(AbstractUserDto createUserDto) {
        return Optional.ofNullable(createUserDto)
                .map(dto -> {
                    User user = new User();
                    user.setEmail(dto.getEmail());
                    user.setFirstName(dto.getFirstName());
                    user.setLastName(dto.getLastName());
                    user.setUsername(dto.getUsername());
                    user.setPassword(dto.getPassword());
                    statusRepository.findStatusByName("UNVERIFIED").ifPresent(user::setUserStatus);
                    return user;
                })
                .orElse(null);
    }

}

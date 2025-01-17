package org.raf.sk.userservice.mapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.raf.sk.userservice.client.appointment.AppointmentUserDto;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.dto.*;
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
                    roleRepository.findRoleByName("USER").ifPresent(user::setUserRole);
                    statusRepository.findStatusByName("UNVERIFIED").ifPresent(user::setUserStatus);
                    user.setLicenseID(createUserDto.getLicenceID());
                    user.setTotalSessions(0);
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
                    manager.setHallId(createManagerDto.getHallId());
                    manager.setHireDate(createManagerDto.getHireDate());
                    return manager;
                })
                .orElse(null);
    }

    public ManagerDto userToManagerDto(User user){
        return Optional.ofNullable(user)
                .map(u -> {
                    ManagerDto managerDto = new ManagerDto();
                    managerDto.setId(u.getId());
                    managerDto.setEmail(u.getEmail());
                    managerDto.setFirstName(u.getFirstName());
                    managerDto.setLastName(u.getLastName());
                    managerDto.setUsername(u.getUsername());
                    managerDto.setHallId(u.getHallId());
                    managerDto.setHireDate(u.getHireDate());
                    return managerDto;
                })
                .orElse(null);
    }

    public AppointmentUserDto userToAppointmentUserDto(User user) {
        return Optional.ofNullable(user)
                .map(u -> {
                    AppointmentUserDto appointmentUserDto = new AppointmentUserDto();
                    appointmentUserDto.setId(u.getId());
                    appointmentUserDto.setEmail(u.getEmail());
                    appointmentUserDto.setFirstName(u.getFirstName());
                    appointmentUserDto.setLastName(u.getLastName());
                    appointmentUserDto.setUsername(u.getUsername());
                    appointmentUserDto.setTotalSessions(u.getTotalSessions());
                    return appointmentUserDto;
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

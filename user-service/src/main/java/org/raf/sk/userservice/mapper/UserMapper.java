package org.raf.sk.userservice.mapper;

import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.dto.CreateManagerDto;
import org.raf.sk.userservice.dto.CreateUserDto;
import org.raf.sk.userservice.dto.UpdateUserDto;
import org.raf.sk.userservice.dto.UserDto;
import org.raf.sk.userservice.repository.RoleRepository;
import org.raf.sk.userservice.repository.StatusRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private RoleRepository roleRepository;
    private StatusRepository statusRepository;

    public UserMapper(RoleRepository roleRepository, StatusRepository statusRepository) {
        this.roleRepository = roleRepository;
        this.statusRepository = statusRepository;
    }

    public UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setDateOfBirth(user.getDateOfBirth());
        userDto.setUserRole(user.getUserRole());
        userDto.setUserStatus(user.getUserStatus());
        userDto.setLicenceID(user.getLicenceID());
        userDto.setTotalSessions(user.getTotalSessions());
        return userDto;
    }

    public User createUserDtoToUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setEmail(createUserDto.getEmail());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setUsername(createUserDto.getUsername());
        user.setPassword(createUserDto.getPassword());
        user.setUserStatus(statusRepository.findStatusByName("UNVERIFIED").get());
        user.setUserRole(roleRepository.findRoleByName("USER").get());
        user.setLicenceID(createUserDto.getLicenceID());
        user.setTotalSessions(0);
        return user;
    }

    public CreateUserDto userToCreateUserDto(User user) {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setEmail(user.getEmail());
        createUserDto.setFirstName(user.getFirstName());
        createUserDto.setLastName(user.getLastName());
        createUserDto.setUsername(user.getUsername());
        createUserDto.setPassword(user.getPassword());
        createUserDto.setLicenceID(user.getLicenceID());
        createUserDto.setTotalSessions(user.getTotalSessions());
        return createUserDto;
    }

    public UpdateUserDto userToUpdateUserDto(User user) {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setFirstName(user.getFirstName());
        updateUserDto.setLastName(user.getLastName());
        updateUserDto.setUsername(user.getUsername());
        updateUserDto.setDateOfBirth(user.getDateOfBirth());
        updateUserDto.setLicenceID(user.getLicenceID());
        updateUserDto.setTotalSessions(user.getTotalSessions());
        return updateUserDto;
    }

    public User updateUserDtoToUser(UpdateUserDto updateUserDto) {
        User user = new User();
        user.setEmail(updateUserDto.getEmail());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setUsername(updateUserDto.getUsername());
        user.setDateOfBirth(updateUserDto.getDateOfBirth());
        user.setLicenceID(updateUserDto.getLicenceID());
        user.setTotalSessions(updateUserDto.getTotalSessions());
        return user;
    }

    public User createManagerDtoToMangaer(CreateManagerDto createManagerDto){
        User manager = new User();
        manager.setEmail(createManagerDto.getEmail());
        manager.setFirstName(createManagerDto.getFirstName());
        manager.setLastName(createManagerDto.getLastName());
        manager.setUsername(createManagerDto.getUsername());
        manager.setPassword(createManagerDto.getPassword());
        //Status manager?
        manager.setUserStatus(statusRepository.findStatusByName("UNVERIFIED").get());
        manager.setUserRole(roleRepository.findRoleByName("MANAGER").get());
        manager.setLicenceID("-1");
        manager.setTotalSessions(-1);
        return manager;
    }

    public CreateManagerDto managerToCreateManagerDto(User manager){
        CreateManagerDto createManagerDto = new CreateManagerDto();
        createManagerDto.setEmail(manager.getEmail());
        createManagerDto.setFirstName(manager.getFirstName());
        createManagerDto.setLastName(manager.getLastName());
        createManagerDto.setUsername(manager.getUsername());
        createManagerDto.setPassword(manager.getPassword());
        return createManagerDto;
    }

}

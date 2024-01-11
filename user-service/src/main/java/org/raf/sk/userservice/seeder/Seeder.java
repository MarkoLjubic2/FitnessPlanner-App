package org.raf.sk.userservice.seeder;


import lombok.AllArgsConstructor;
import org.raf.sk.userservice.domain.Role;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.repository.RoleRepository;
import org.raf.sk.userservice.repository.StatusRepository;
import org.raf.sk.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Profile({"default"})
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "seed.data", havingValue = "true")
public class Seeder implements CommandLineRunner {

    private final BCryptPasswordEncoder encoder;
    private RoleRepository roleRepository;
    private StatusRepository statusRepository;
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        Role roleAdmin = new Role(1L, "ADMIN");
        Role roleManager = new Role(2L, "MANAGER");
        Role roleClient = new Role(3L, "USER");
        roleAdmin = roleRepository.save(roleAdmin);
        roleManager = roleRepository.save(roleManager);
        roleClient = roleRepository.save(roleClient);

        Status statusActive = new Status(1L, "VERIFIED");
        Status statusBanned = new Status(2L, "UNVERIFIED");
        Status statusDeleted = new Status(3L, "BANNED");
        statusActive = statusRepository.save(statusActive);
        statusRepository.save(statusBanned);
        statusRepository.save(statusDeleted);

        insertAdmins(roleAdmin, statusActive);
        insertManagers(roleManager, statusActive);
        insertClients(roleClient, statusActive);
    }

    private void insertAdmins(Role roleAdmin, Status statusActive) {
        User admin = new User("admin", encoder.encode("admin"), "admin", "admin", "admin@gmail.com", "1990-10-10");
        admin.setUserRole(roleAdmin);
        admin.setUserStatus(statusActive);
        userRepository.save(admin);
    }

    private void insertManagers(Role roleManager, Status statusActive) {
        User manager1 = new User("m1", encoder.encode("m1"), "Dragan", "Urosevic", "manager1@gmail.com", "1990-10-10");
        manager1.setHallId(1L);
        manager1.setHireDate("2022-11-10");
        manager1.setUserRole(roleManager);
        manager1.setUserStatus(statusActive);
        userRepository.save(manager1);
    }

    private void insertClients(Role roleClient, Status statusActive) {
        User client1 = new User("c1", encoder.encode("c1"), "Michael", "Johnson", "theBestEmail@gmail.com", "2001-11-03");
        client1.setLicenseID("7491412");
        client1.setTotalSessions(0);
        client1.setUserRole(roleClient);
        client1.setUserStatus(statusActive);
        userRepository.save(client1);
    }

}

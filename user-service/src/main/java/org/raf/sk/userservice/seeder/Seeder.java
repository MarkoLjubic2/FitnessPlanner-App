package org.raf.sk.userservice.seeder;


import lombok.AllArgsConstructor;
import org.raf.sk.userservice.domain.Role;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.repository.RoleRepository;
import org.raf.sk.userservice.repository.StatusRepository;
import org.raf.sk.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Date;


@Profile({"default"})
@Component
@AllArgsConstructor
public class Seeder implements CommandLineRunner {

    private RoleRepository roleRepository;
    private StatusRepository statusRepository;
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        Role roleAdmin = new Role(0L, "ADMIN");
        Role roleManager = new Role(1L, "MANAGER");
        Role roleClient = new Role(2L, "CLIENT");
        roleAdmin = roleRepository.save(roleAdmin);
        roleManager = roleRepository.save(roleManager);
        roleClient = roleRepository.save(roleClient);

        Status statusActive = new Status(0L, "VERIFIED");
        Status statusBanned = new Status(1L, "UNVERIFIED");
        Status statusDeleted = new Status(2L, "BANNED");
        statusActive = statusRepository.save(statusActive);
        statusRepository.save(statusBanned);
        statusRepository.save(statusDeleted);

        insertAdmins(roleAdmin, statusActive);
        insertManagers(roleManager, statusActive);
        insertClients(roleClient, statusActive);
    }

    private void insertAdmins(Role roleAdmin, Status statusActive) {
        User admin = new User("admin", "admin", "admin", "admin", "admin@gmail.com", Date.valueOf("1990-10-10"));
        admin.setUserRole(roleAdmin);
        admin.setUserStatus(statusActive);
        userRepository.save(admin);
    }

    private void insertManagers(Role roleManager, Status statusActive) {
        User manager1 = new User("m1", "m1", "m1", "m1", "manager1@gmail.com", Date.valueOf("1990-10-10"));
        manager1.setHall("Gym 1");
        manager1.setHireDate(Date.valueOf("2022-11-10"));
        manager1.setUserRole(roleManager);
        manager1.setUserStatus(statusActive);
        userRepository.save(manager1);
    }

    private void insertClients(Role roleClient, Status statusActive) {
        User client1 = new User("c1", "c1", "c1", "c1", "theBestEmail@gmail.com", Date.valueOf("2001-11-3"));
        client1.setLicenseID("7491412");
        client1.setTotalSessions(0);
        client1.setUserRole(roleClient);
        client1.setUserStatus(statusActive);
        userRepository.save(client1);
    }

}

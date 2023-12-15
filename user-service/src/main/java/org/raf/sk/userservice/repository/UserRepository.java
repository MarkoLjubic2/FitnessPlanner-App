package org.raf.sk.userservice.repository;

import org.raf.sk.userservice.domain.Role;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsernameAndPassword(String username, String password);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByLicenseID(String licenseID);

    @Modifying
    @Query("update User u set u.email = :email, u.username = :username, u.firstName = :firstName, u.lastName = :lastName, u.password = :password, u.dateOfBirth = :dateOfBirth, u.licenseID = :licenceID where u.id = :id")
    void updateUser(Long id, String email, String username, String firstName, String lastName, String password, Date dateOfBirth, String licenceID);

    @Modifying
    @Query("update User u set u.email = :email, u.username = :username, u.firstName = :firstName, u.lastName = :lastName, u.password = :password, u.dateOfBirth = :dateOfBirth, u.hall = :hall, u.hireDate = :hireDate where u.id = :id")
    void updateManager(Long id, String email, String username, String firstName, String lastName, String password, String phoneNumber, Date dateOfBirth, String hall, Date hireDate);

    @Modifying
    @Query("update User u set u.userStatus = :userStatus where u.id = :id")
    void updateStatus(Long id, Status userStatus);

    @Modifying
    @Query("update User u set u.userRole = :userRole where u.id = :id")
    void updateRole(Long id, Role userRole);
}

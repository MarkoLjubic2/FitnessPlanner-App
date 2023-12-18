package org.raf.sk.userservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {@Index(columnList = "username", unique = true), @Index(columnList = "email", unique = true)})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private Date dateOfBirth;
    private String firstName;
    private String lastName;
    @ManyToOne(optional = false)
    private Role userRole;
    @ManyToOne(optional = false)
    private Status userStatus;
    private String licenseID;
    private int totalSessions;
    private String hall;
    private Date hireDate;

    public User(String username, String password, String firstName, String lastName, String email, Date dateOfBirth) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

}

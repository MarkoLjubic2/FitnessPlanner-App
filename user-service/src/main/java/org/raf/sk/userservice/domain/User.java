package org.raf.sk.userservice.domain;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
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

}

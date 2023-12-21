package org.raf.sk.notificationservice.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import java.util.Date;

@Getter
@Setter
@Entity
public class Notification {

    @Id
    private Long id;
    @Email
    private String clientEmail;
    @Email
    private String managerEmail;
    @ManyToOne
    private Type type;
    private Date time;
    private String clientFirstName;
    private String clientLastName;
    private String link;
    private String hallName;
}

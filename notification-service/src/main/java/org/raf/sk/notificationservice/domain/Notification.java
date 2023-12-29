package org.raf.sk.notificationservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(indexes = {@Index(columnList = "clientEmail", unique = true)})
public class Notification {

    @Id
    private Long id;
    @Email
    private String clientEmail;
    @Email
    private String managerEmail;
    @ManyToOne(optional = false)
    private Type type;
    private Date time;
    private String clientFirstName;
    private String clientLastName;
    private String link;
    private String hallName;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

}

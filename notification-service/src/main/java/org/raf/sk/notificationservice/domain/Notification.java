package org.raf.sk.notificationservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "mail", unique = true)})
public class Notification {

    @Id
    private Long id;
    @Email
    private String mail;
    @ManyToOne(optional = false)
    private Type type;
    private String body;
    private Date time;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public Notification(String mail, Type type, String body, Date time) {
        this.mail = mail;
        this.type = type;
        this.body = body;
        this.time = time;
    }
}

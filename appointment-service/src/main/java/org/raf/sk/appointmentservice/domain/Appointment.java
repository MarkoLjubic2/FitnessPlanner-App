package org.raf.sk.appointmentservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment implements Schedulable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private Integer startTime;
    private Integer endTime;
    @ManyToOne(optional = false)
    private Training training;
    private int maxClients;
    private int currentClients;
    private boolean open;
    private String day;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public Appointment(String date, Integer startTime, Integer endTime, int maxClients, int currentClients, String day) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxClients = maxClients;
        this.currentClients = currentClients;
        this.day = day;
        this.open = true;
    }

}

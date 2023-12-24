package org.raf.sk.appointmentservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.DayOfWeek;
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
    private Date date;
    private Integer startTime;
    private Integer endTime;
    @ManyToOne(optional = false)
    private Training training;
    private int maxClients;
    private int currentClients;
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public Appointment(Date date, Integer startTime, Integer endTime, int maxClients, int currentClients, DayOfWeek dayOfWeek) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxClients = maxClients;
        this.currentClients = currentClients;
        this.dayOfWeek = dayOfWeek;
    }

}

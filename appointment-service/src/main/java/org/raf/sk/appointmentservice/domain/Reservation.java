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
public class Reservation implements Schedulable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private Integer startTime;
    private Integer endTime;
    @ManyToOne(optional = false)
    private Training training;
    private Long clientId;
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public Reservation(Date date, Integer startTime, Integer endTime, DayOfWeek day, Long clientId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = day;
        this.clientId = clientId;
    }

}

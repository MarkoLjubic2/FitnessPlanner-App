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
public class Training {

    // TODO: Da li cuvati individual kao polje ili gledati po maxParticipants?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean individual;
    private double price;
    private int maxParticipants;
    private int currentParticipants;
    @ManyToOne(optional = false)
    private Hall hall;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public Training(String name, boolean individual, double price, int maxParticipants) {
        this.name = name;
        this.individual = individual;
        this.price = price;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
    }

}

package org.raf.sk.appointmentservice.client.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUserDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private int totalSessions;

    @Override
    public String toString() {
        return "RentUserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

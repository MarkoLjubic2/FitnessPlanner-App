package org.raf.sk.userservice.client.appointment;

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

}

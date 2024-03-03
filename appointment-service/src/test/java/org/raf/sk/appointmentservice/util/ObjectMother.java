package org.raf.sk.appointmentservice.util;

import org.raf.sk.appointmentservice.client.user.AppointmentUserDto;
import org.raf.sk.appointmentservice.client.user.ManagerDto;
import org.raf.sk.appointmentservice.domain.Appointment;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.service.Response;

import java.time.LocalDate;
import java.util.List;

import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

public class ObjectMother {

    public static AppointmentUserDto createAppointmentUserDto() {
        AppointmentUserDto userDto = new AppointmentUserDto();
        userDto.setId(3L);
        userDto.setEmail("test@test.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setTotalSessions(5);

        return userDto;
    }

    public static ManagerDto createManagerDto() {
        ManagerDto managerDto = new ManagerDto();
        managerDto.setId(2L);
        managerDto.setEmail("test1@test.com");
        managerDto.setFirstName("Test1");
        managerDto.setLastName("User1");

        return managerDto;
    }

    public static AppointmentDto createAppointmentDto() {
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setId(1L);
        appointmentDto.setClientId(3L);
        appointmentDto.setTrainingId(1L);
        appointmentDto.setDate("2022-12-09");
        appointmentDto.setStartTime(12);
        appointmentDto.setEndTime(14);
        appointmentDto.setDay("Monday");
        appointmentDto.setMaxClients(1);
        appointmentDto.setCurrentClients(0);
        appointmentDto.setOpen(true);

        return appointmentDto;
    }

    public static Training createTraining() {
        Training t1 = new Training("Calisthenics", true, 1600);
        t1.setId(1L);
        t1.setHall(createHall());

        return t1;
    }

    public static Hall createHall() {
        Hall h1 = new Hall("Ahiley 1", "/", 2, 1L);
        h1.setManagerId(2L);

        return h1;
    }

    public static Appointment createClosedAppointment() {
        Appointment a1 = new Appointment((LocalDate.now().plusDays(1)).toString(), 12, 14, 1, 1, "Monday");
        a1.setOpen(false);
        a1.setTraining(createTraining());

        return a1;
    }

    public static Reservation createReservation() {
        Reservation r1 = new Reservation((LocalDate.now().plusDays(1)).toString(), 12, 14, "Monday", 3L);
        r1.setTraining(createTraining());

        return r1;
    }

    public static List<Reservation> createReservationSample() {
        Reservation r1 = new Reservation((LocalDate.now().plusDays(1)).toString(), 12, 14, "Monday", 1L);
        r1.setTraining(createTraining());
        Reservation r2 = new Reservation((LocalDate.now().plusDays(2)).toString(), 10, 11, "Tuesday", 1L);
        r2.setTraining(createTraining());
        Reservation r3 = new Reservation((LocalDate.now().plusDays(1)).toString(), 12, 13, "Wednesday", 3L);
        r3.setTraining(createTraining());
        Reservation r4 = new Reservation((LocalDate.now().plusDays(4)).toString(), 16, 18, "Monday", 1L);
        r4.setTraining(createTraining());
        Reservation r5 = new Reservation((LocalDate.now().plusDays(3)).toString(), 9, 12, "Monday", 1L);
        r5.setTraining(createTraining());

        return List.of(r1, r2, r3, r4, r5);
    }

    public static List<Appointment> createAppointmentSample() {
        Appointment a1 = new Appointment((LocalDate.now().plusDays(1)).toString(), 16, 18, 1, 0, "Monday");
        a1.setTraining(createTraining());
        Appointment a2 = new Appointment((LocalDate.now().plusDays(2)).toString(), 10, 11, 1, 0, "Tuesday");
        a2.setTraining(createTraining());
        Appointment a3 = new Appointment((LocalDate.now().plusDays(1)).toString(), 12, 13, 1, 1, "Wednesday");
        a3.setTraining(createTraining());
        Appointment a4 = new Appointment((LocalDate.now().plusDays(4)).toString(), 16, 18, 1, 0, "Monday");
        a4.setTraining(createTraining());
        Appointment a5 = new Appointment((LocalDate.now().plusDays(3)).toString(), 9, 12, 1, 0, "Monday");
        a5.setTraining(createTraining());

        return List.of(a1, a2, a3, a4, a5);
    }

    public static Response<AppointmentUserDto> createUserResponse(AppointmentUserDto userDto) {
        Response<AppointmentUserDto> response = new Response<>();
        response.setStatusCode(STATUS_OK);
        response.setMessage("Success");
        response.setData(userDto);

        return response;
    }

}

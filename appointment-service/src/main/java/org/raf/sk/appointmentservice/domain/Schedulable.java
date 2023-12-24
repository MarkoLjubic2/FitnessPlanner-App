package org.raf.sk.appointmentservice.domain;

import java.time.DayOfWeek;

public interface Schedulable {

    DayOfWeek getDayOfWeek();

    Training getTraining();

}

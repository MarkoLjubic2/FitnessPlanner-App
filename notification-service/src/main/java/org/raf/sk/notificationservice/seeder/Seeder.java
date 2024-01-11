package org.raf.sk.notificationservice.seeder;


import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.repository.TypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile({"default"})
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "seed.data", havingValue = "true")
public class Seeder implements CommandLineRunner {

    private final TypeRepository typeRepository;

    @Override
    public void run(String... args) {
        Type type1 = new Type("ACTIVATION", "Greetings %USERNAME! Please click the following link to activate your account: %LINK", "Activation");
        Type type2 = new Type("CHANGE_PASSWORD", "Greetings  %USERNAME! Your password has been changed successfully.", "Change Password");
        Type type3 = new Type("RESERVATION", "Hello %FIRST_NAME %LAST_NAME, a reservation for %HALL has been made successfully!", "Reservation");
        Type type4 = new Type("RESERVATION_CANCEL", "Hello %FIRST_NAME %LAST_NAME, a reservation for %HALL has been cancelled!", "Reservation");
        Type type5 = new Type("REMINDER", "Hello %FIRST_NAME %LAST_NAME, a reservation for %HALL is coming up!", "Reservation");

        typeRepository.save(type1);
        typeRepository.save(type2);
        typeRepository.save(type3);
        typeRepository.save(type4);
        typeRepository.save(type5);
    }

}

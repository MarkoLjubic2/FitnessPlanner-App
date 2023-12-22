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
        Type type1 = new Type("Activation Mail", "Greetings %first_name %last_name, to verify go to %link", "Activation");
        Type type2 = new Type("Change Password Mail", "Greetings %first_name %last_name, to change password go to %link", "Change Password");
        Type type3 = new Type("Reservation", "Hello %first_name %last_name, a reservation for %hall has been made successfully!", "Reservation");
        Type type4 = new Type("Reservation Cancellation", "Hello %first_name %last_name, a reservation for %hall has been cancelled!", "Reservation");
        Type type5 = new Type("Reservation Reminder", "Hello %first_name %last_name, a reservation for %hall is coming up!", "Reservation");

        typeRepository.save(type1);
        typeRepository.save(type2);
        typeRepository.save(type3);
        typeRepository.save(type4);
        typeRepository.save(type5);
    }

}

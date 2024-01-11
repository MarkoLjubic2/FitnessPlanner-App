package org.raf.sk.appointmentservice.mapper;

import lombok.NoArgsConstructor;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.dto.training.TrainingDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@NoArgsConstructor
public class TrainingMapper {

    public TrainingDto trainingToTrainingDto(Training training) {
        return Optional.ofNullable(training)
                .map(t -> {
                    TrainingDto trainingDto = new TrainingDto();
                    trainingDto.setId(t.getId());
                    trainingDto.setName(t.getName());
                    trainingDto.setIndividual(t.isIndividual());
                    trainingDto.setPrice(t.getPrice());
                    trainingDto.setHallId(t.getHall().getId());
                    return trainingDto;
                })
                .orElse(null);
    }

}

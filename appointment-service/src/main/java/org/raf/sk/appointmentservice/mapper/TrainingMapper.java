package org.raf.sk.appointmentservice.mapper;

import lombok.NoArgsConstructor;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.dto.training.CreateTrainingDto;
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
                    return trainingDto;
                })
                .orElse(null);
    }

    public Training trainingDtoToTraining(TrainingDto trainingDto) {
        return Optional.of(trainingDto)
                .map(dto -> {
                    Training training = new Training();
                    training.setId(dto.getId());
                    training.setName(dto.getName());
                    training.setIndividual(dto.isIndividual());
                    training.setPrice(dto.getPrice());
                    return training;
                })
                .orElse(null);
    }

    public CreateTrainingDto trainingToCreateTrainingDto(Training training) {
        return Optional.ofNullable(training)
                .map(t -> {
                    CreateTrainingDto createTrainingDto = new CreateTrainingDto();
                    createTrainingDto.setName(t.getName());
                    createTrainingDto.setIndividual(t.isIndividual());
                    createTrainingDto.setPrice(t.getPrice());
                    return createTrainingDto;
                })
                .orElse(null);
    }

    public Training createTrainingDtoToTraining(CreateTrainingDto createTrainingDto) {
        return Optional.of(createTrainingDto)
                .map(dto -> {
                    Training training = new Training();
                    training.setName(dto.getName());
                    training.setIndividual(dto.isIndividual());
                    training.setPrice(dto.getPrice());
                    return training;
                })
                .orElse(null);
    }

}

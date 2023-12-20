package org.raf.sk.appointmentservice.mapper;

import lombok.NoArgsConstructor;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.dto.CreateTrainingDto;
import org.raf.sk.appointmentservice.dto.TrainingDto;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class TrainingMapper {

    public TrainingDto trainingToTrainingDto(Training training) {
        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setId(training.getId());
        trainingDto.setName(training.getName());
        trainingDto.setIndividual(training.isIndividual());
        trainingDto.setPrice(training.getPrice());
        return trainingDto;
    }

    public Training trainingDtoToTraining(TrainingDto trainingDto) {
        Training training = new Training();
        training.setId(trainingDto.getId());
        training.setName(trainingDto.getName());
        training.setIndividual(trainingDto.isIndividual());
        training.setPrice(trainingDto.getPrice());
        return training;
    }

    public CreateTrainingDto trainingToCreateTrainingDto(Training training) {
        CreateTrainingDto createTrainingDto = new CreateTrainingDto();
        createTrainingDto.setName(training.getName());
        createTrainingDto.setIndividual(training.isIndividual());
        createTrainingDto.setPrice(training.getPrice());
        return createTrainingDto;
    }

    public Training createTrainingDtoToTraining(CreateTrainingDto createTrainingDto) {
        Training training = new Training();
        training.setName(createTrainingDto.getName());
        training.setIndividual(createTrainingDto.isIndividual());
        training.setPrice(createTrainingDto.getPrice());
        return training;
    }

}

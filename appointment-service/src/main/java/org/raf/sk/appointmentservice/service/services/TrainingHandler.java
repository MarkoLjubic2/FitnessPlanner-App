package org.raf.sk.appointmentservice.service.services;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.dto.training.TrainingDto;
import org.raf.sk.appointmentservice.mapper.TrainingMapper;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.raf.sk.appointmentservice.service.Response;
import org.springframework.stereotype.Component;

import static org.raf.sk.appointmentservice.constants.Constants.STATUS_NOT_FOUND;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

@Component
@AllArgsConstructor
public class TrainingHandler {

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;

    public Response<TrainingDto> findTrainingById(Long trainingId) {
        return trainingRepository.getTrainingById(trainingId)
                .map(training -> new Response<>(STATUS_OK, "Training found", trainingMapper.trainingToTrainingDto(training)))
                .orElse(new Response<>(STATUS_NOT_FOUND, "Training not found", null));
    }

}

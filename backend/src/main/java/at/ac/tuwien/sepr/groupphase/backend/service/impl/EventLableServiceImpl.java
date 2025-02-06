package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventLabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventLabelRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventLabelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventLableServiceImpl implements EventLabelService {

    private final EventLabelRepository labelRepository;
    private final EventLabelMapper labelMapper;

    public EventLableServiceImpl(EventLabelRepository labelRepository, EventLabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    @Override
    public EventLabel findByValueAndColour(String value, String color) {
        return labelRepository.findByLabelNameAndLabelColour(value, color);
    }

    @Override
    public List<EventLabel> createAll(List<EventLabelDto> newLabels) {
        List<EventLabel> labels = labelMapper.dtoListToEntityList(newLabels);
        return labelRepository.saveAll(labels);
    }
}

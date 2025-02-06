package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.repository.LabelRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    @Override
    public ItemLabel findByValueAndColour(String value, String colour) {
        LOGGER.trace("findByValueAndColour({}, {})", value, colour);
        return labelRepository.findByLabelValueAndLabelColour(value, colour);
    }

    @Override
    public List<ItemLabel> createAll(List<ItemLabelDto> newLabels) {
        LOGGER.trace("createAll({})", newLabels);
        List<ItemLabel> ingredientList = labelMapper.dtoListToEntityList(newLabels);
        return labelRepository.saveAll(ingredientList);
    }
}

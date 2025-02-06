package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AlternativeNameDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.AlternativeNameRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.AlternativeNameService;
import org.springframework.stereotype.Service;

@Service
public class AlternativeNameServiceImpl implements AlternativeNameService {

    private final AlternativeNameRepository alternativeNameRepository;

    private final ItemMapper itemMapper;

    private final AuthService authService;

    public AlternativeNameServiceImpl(AlternativeNameRepository alternativeNameRepository, ItemMapper itemMapper, AuthService authService) {
        this.alternativeNameRepository = alternativeNameRepository;
        this.itemMapper = itemMapper;
        this.authService = authService;
    }

    @Override
    public AlternativeName create(AlternativeNameDto alternativeNameDto) {
        AlternativeName alternativeName = alternativeNameRepository.save(itemMapper.alternativeNameDtoToEntity(alternativeNameDto));
        return alternativeName;
    }

    @Override
    public AlternativeName createIfNotExist(AlternativeNameDto alternativeNameDto) {
        ApplicationUser user = authService.getUserFromToken();
        AlternativeName alternativeName = null;
        AlternativeNameDto toSave = new AlternativeNameDto(null, alternativeNameDto.name(), user.getSharedFlat().getId());
        if (alternativeNameRepository.findAllByNameAndShareFlatId(toSave.name(), toSave.shareFlatId()) == null) {
            alternativeName = alternativeNameRepository.save(itemMapper.alternativeNameDtoToEntity(toSave));
        }
        return alternativeName;
    }


    public AlternativeName findById(Long id) {
        return alternativeNameRepository.findById(id.toString()).orElse(null);
    }
}

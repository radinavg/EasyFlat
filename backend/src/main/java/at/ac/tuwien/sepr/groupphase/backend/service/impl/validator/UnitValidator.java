package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class UnitValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateUnit(Unit from, Unit to, Unit persistedFrom, Unit persistedTo) throws ValidationException, ConflictException {
        LOGGER.info("validateUnit({}, {}, {}, {})", from, to, persistedFrom, persistedTo);

        if (from == null || to == null) {
            throw new ValidationException("Invalid Unit", List.of("Unit must not be null"));
        }

        List<String> errors = new ArrayList<>();

        if (persistedFrom == null || persistedTo == null) {
            errors.add("Unit does not exist");
        } else if (persistedFrom.getSubUnit() == null) {
            errors.add("Unit has no subunit");
        } else if (!persistedFrom.getSubUnit().equals(persistedTo)) {
            errors.add("Units are not compatible");
        }


        if (persistedFrom != null && persistedFrom.getSubUnit() != null && persistedFrom.getConvertFactor() == null) {
            throw new FatalException("Unit has no convert factor, there is a problem with the database");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("Conflict", errors);
        }
    }

    public void validateForCreate(UnitDto unitDto) throws ValidationException {
        LOGGER.info("validateForCreate({})", unitDto);

        List<String> errors = new ArrayList<>();

        if (unitDto.name() == null || unitDto.name().trim().isEmpty()) {
            errors.add("Unit must not be empty");
        }

        if (unitDto.subUnit() != null && !unitDto.subUnit().isEmpty() && unitDto.convertFactor() == null) {
            errors.add("Unit must have a convert factor");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Invalid Unit", errors);
        }
    }

}

package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface ItemLabelValidator {

    void validate(ItemLabelDto itemLabelDto) throws ValidationException;

}

package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;

import java.util.List;

public interface LabelService {

    /**
     * Finds if on object of type {@link ItemLabel} exists in the db.
     *
     * @param value of the label searched for
     * @param color of the label searched for
     * @return an object of type {@link ItemLabel} if label with given parameters exists in db, or null otherwise
     */
    ItemLabel findByValueAndColour(String value, String color);

    /**
     * Stores objects of type {@link ItemLabel} in the db.
     *
     * @param newLabels list of objects of type {@link ItemLabelDto} which should be stored in the db
     * @return a list of objects of type {@link ItemLabel} of labels stored in the db
     */
    List<ItemLabel> createAll(List<ItemLabelDto> newLabels);
}

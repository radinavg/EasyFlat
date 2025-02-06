package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface UnitService {

    /**
     * Find unit by id.
     *
     * @param name the id of the unit
     * @return the unit
     */
    Unit findByName(String name);

    /**
     * Find all unit entries ordered by published at date (descending).
     *
     * @return list of al unit entries
     */
    List<Unit> findAll();

    /**
     * Calculates the value of units of the target unit from the given value of the source unit.
     *
     * @param from  source unit
     * @param to    target unit
     * @param value value of source unit
     * @return the converted value
     */
    Double convertUnits(Unit from, Unit to, Double value);


    /**
     * Creates a new unit.
     *
     * @param unit the unit to create
     * @return the created unit
     * @throws ValidationException if the unit creation fails due to validation issues
     * @throws ConflictException   if there is a conflict during the creation of the unit
     */
    Unit create(UnitDto unit) throws ValidationException, ConflictException;

    /**
     * Gets the minimum subunit of a unit (used for conversion calculations).
     *
     * @param unit the unit for which to get the minimum subunit
     * @return the minimum subunit of the given unit
     */
    Unit getMinUnit(Unit unit);

    /**
     * Checks whether two units are comparable.
     *
     * @param unit1 the first unit
     * @param unit2 the second unit
     * @return true if the units are comparable, false otherwise
     */
    boolean areUnitsComparable(Unit unit1, Unit unit2);

}


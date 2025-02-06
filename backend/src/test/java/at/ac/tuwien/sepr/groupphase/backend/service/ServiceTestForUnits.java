package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
public class ServiceTestForUnits {

    @Autowired
    UnitService unitService;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
    }


    @Test
    @DisplayName("Given valid unit when create then unit is created")
    public void testCreateUnit() throws ValidationException, ConflictException {
        // Given
        UnitDto unitDto = UnitDtoBuilder.builder().name("NewUnit").convertFactor(200L).subUnit(null).build();

        // When
        Unit createdUnit = unitService.create(unitDto);

        assertThat(createdUnit).isNotNull();
        // Then
        assertAll(
            () -> assertThat(createdUnit.getName()).isEqualTo("NewUnit"),
            () -> assertThat(createdUnit.getConvertFactor()).isEqualTo(200L)
        );
    }

    @Test
    @DisplayName("Given valid unit when create then unit is created")
    public void testConvertUnitsSmallToBig() {

        // when
        Double convertedValueGtoKG = unitService.convertUnits(
            unitService.findByName("g"),
            unitService.findByName("kg"),
            500.0
        );

        Double convertedValueMLtoL = unitService.convertUnits(
            unitService.findByName("ml"),
            unitService.findByName("l"),
            500.0
        );


        // then
        assertAll(
            () -> assertThat(convertedValueGtoKG).isEqualTo(0.5),
            () -> assertThat(convertedValueMLtoL).isEqualTo(0.5)
        );
    }

    @Test
    @DisplayName("Given valid unit when create then unit is created")
    public void testConvertUnitsBigToSmall() {

        // when
        Double convertedValueKGtoG = unitService.convertUnits(
            unitService.findByName("kg"),
            unitService.findByName("g"),
            5.0
        );

        Double convertedValueLtoML = unitService.convertUnits(
            unitService.findByName("l"),
            unitService.findByName("ml"),
            5.0
        );

        Double convertedValueTSPtoML = unitService.convertUnits(
            unitService.findByName("tsp"),
            unitService.findByName("g"),
            5.0
        );
        Double convertedValueCupToG = unitService.convertUnits(
            unitService.findByName("cup"),
            unitService.findByName("g"),
            5.0
        );


        // then
        assertAll(
            () -> assertThat(convertedValueKGtoG).isEqualTo(5000),
            () -> assertThat(convertedValueLtoML).isEqualTo(5000),
            () -> assertThat(convertedValueTSPtoML).isEqualTo(25),
            () -> assertThat(convertedValueCupToG).isEqualTo(625)

        );
    }

    @Test
    @DisplayName("Given valid unit when create then unit is created")
    public void testConvertUnitsSameUnit() {
        // when
        Double convertedValue = unitService.convertUnits(
            unitService.findByName("g"),
            unitService.findByName("g"),
            500.0
        );

        // then
        assertThat(convertedValue).isEqualTo(500.0);
    }

    @Test
    @DisplayName("Given valid unit when create then unit is created")
    public void testGetMinUnit() {
        // Given


        // When
        Unit minUnitKG = unitService.getMinUnit(unitService.findByName("kg"));
        Unit minUnitTSP = unitService.getMinUnit(unitService.findByName("tsp"));
        Unit minUnitTBSP = unitService.getMinUnit(unitService.findByName("tbsp"));
        Unit minUnitTABLESPOONS = unitService.getMinUnit(unitService.findByName("tablespoons"));
        Unit minUnitTEASPOON = unitService.getMinUnit(unitService.findByName("teaspoon"));
        Unit minUnitCUPS = unitService.getMinUnit(unitService.findByName("cup"));
        Unit minUnitCUP = unitService.getMinUnit(unitService.findByName("cups"));
        Unit minUnitL = unitService.getMinUnit(unitService.findByName("l"));

        assertThat(minUnitKG).isNotNull();
        assertThat(minUnitTSP).isNotNull();
        assertThat(minUnitTBSP).isNotNull();
        assertThat(minUnitTABLESPOONS).isNotNull();
        assertThat(minUnitTEASPOON).isNotNull();
        assertThat(minUnitCUPS).isNotNull();
        assertThat(minUnitCUP).isNotNull();
        assertThat(minUnitL).isNotNull();
        assertAll(

            () -> assertThat(minUnitKG.getName()).isEqualTo("g"),
            () -> assertThat(minUnitTSP.getName()).isEqualTo("g"),
            () -> assertThat(minUnitTBSP.getName()).isEqualTo("g"),
            () -> assertThat(minUnitTABLESPOONS.getName()).isEqualTo("g"),
            () -> assertThat(minUnitTEASPOON.getName()).isEqualTo("g"),
            () -> assertThat(minUnitCUPS.getName()).isEqualTo("g"),
            () -> assertThat(minUnitCUP.getName()).isEqualTo("g"),
            () -> assertThat(minUnitL.getName()).isEqualTo("ml")
        );
    }

    @Test
    @DisplayName("Given valid unit when create then unit is created")
    public void testAreUnitsComparable() {
        // Given
        Unit gUnit = unitService.findByName("g");
        Unit kgUnit = unitService.findByName("kg");
        Unit tspUnit = unitService.findByName("tsp");
        Unit mlUnit = unitService.findByName("ml");

        // When
        boolean comparableGToKG = unitService.areUnitsComparable(gUnit, kgUnit);
        boolean comparableTSPToG = unitService.areUnitsComparable(tspUnit, gUnit);
        boolean comparableGtoML = unitService.areUnitsComparable(gUnit, mlUnit);

        // Then
        assertAll(
            () -> assertThat(comparableGToKG).isTrue(),
            () -> assertThat(comparableTSPToG).isTrue(),
            () -> assertThat(comparableGtoML).isFalse()
        );
    }


}

package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@Profile({"default", "generateData", "test", "unitTest"})
@Component("UnitDataGenerator")
public class UnitDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UnitService unitService;

    public UnitDataGenerator(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostConstruct
    public void generate() throws ValidationException, ConflictException {
        LOGGER.info("generate()");

        UnitDto g = new UnitDto("g", null, Set.of());
        UnitDto kg = new UnitDto("kg", 1000L, Set.of(g));

        UnitDto ml = new UnitDto("ml", 10L, Set.of());
        UnitDto l = new UnitDto("l", 1000L, Set.of(ml));
        UnitDto liter = new UnitDto("liter", 1000L, Set.of(ml));


        UnitDto cups = new UnitDto("cups", 125L, Set.of(g));
        UnitDto cup = new UnitDto("cup", 125L, Set.of(g));


        UnitDto pcs = new UnitDto("pcs", null, Set.of());
        UnitDto tablespoons = new UnitDto("tablespoons", 15L, Set.of(g));
        UnitDto tbsp = new UnitDto("tbsp", 15L, Set.of(g));
        UnitDto tsp = new UnitDto("tsp", 5L, Set.of(g));
        UnitDto teaspoon = new UnitDto("teaspoon", 5L, Set.of(g));
        UnitDto scoops = new UnitDto("scoops", 15L, Set.of(g));

        UnitDto ounce = new UnitDto("ounce", 30L, Set.of(g));
        UnitDto ounces = new UnitDto("ounces", 30L, Set.of(g));
        UnitDto oz = new UnitDto("oz", 30L, Set.of(g));


        UnitDto pound = new UnitDto("pound", 455L, Set.of(g));
        UnitDto gallon = new UnitDto("gallon", 3785L, Set.of(ml));



        unitService.create(g);
        unitService.create(kg);

        unitService.create(ml);
        unitService.create(l);

        unitService.create(cup);
        unitService.create(tbsp);
        unitService.create(pcs);
        unitService.create(tablespoons);
        unitService.create(pound);
        unitService.create(gallon);
        unitService.create(cups);
        unitService.create(teaspoon);
        unitService.create(tsp);
        unitService.create(liter);
        unitService.create(scoops);
        unitService.create(ounces);
        unitService.create(oz);
        unitService.create(ounce);
    }


}

package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component("CookbookDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator"})
public class CookbookDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final CookbookRepository cookbookRepository;


    public CookbookDataGenerator(CookbookRepository cookbookRepository) {
        this.cookbookRepository = cookbookRepository;
    }

    @PostConstruct
    public void generateCookbooks() {
        LOGGER.debug("generating {} Digital Storages", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            Cookbook cookbook = new Cookbook();
            cookbook.setTitle("Cookbook " + (i + 1));

            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setId((long) (i + 1));

            cookbook.setSharedFlat(sharedFlat);

            LOGGER.debug("saving cookbook {}", cookbook);
            cookbookRepository.save(cookbook);
        }
    }
}

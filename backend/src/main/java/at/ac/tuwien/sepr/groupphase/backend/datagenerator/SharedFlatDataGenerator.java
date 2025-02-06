package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test", "unitTest"})
@Component("SharedFlatDataGenerator")
@DependsOn({"CleanDatabase"})
public class SharedFlatDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final SharedFlatRepository sharedFlatRepository;
    private final PasswordEncoder passwordEncoder;

    public SharedFlatDataGenerator(SharedFlatRepository sharedFlatRepository, PasswordEncoder passwordEncoder) {
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void generateSharedFlats() {
        LOGGER.debug("generating {} Shared Flat Entities", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setName("Shared Flat " + (i + 1));
            sharedFlat.setPassword(passwordEncoder.encode("12341234"));

            LOGGER.debug("saving shared flat: {}", sharedFlat);
            sharedFlatRepository.save(sharedFlat);
        }
    }
}

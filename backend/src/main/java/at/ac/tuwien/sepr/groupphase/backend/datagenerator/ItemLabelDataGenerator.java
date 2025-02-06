package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.repository.LabelRepository;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Random;

@Profile({"generateData", "unitTest"})
@Component("ItemLabelDataGenerator")
public class ItemLabelDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 10;
    private final LabelRepository labelRepository;
    private final Faker faker = new Faker(new Random(24012024));

    public ItemLabelDataGenerator(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @PostConstruct
    public void generateItemLabels() {
        LOGGER.debug("generating {} ItemLabels", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            ItemLabel itemLabel = new ItemLabel();
            String labelValue = faker.funnyName().name();
            if (labelValue.length() > 10) {
                i--;
                continue;
            }
            itemLabel.setLabelValue(labelValue);
            itemLabel.setLabelColour(faker.color().hex());
            labelRepository.save(itemLabel);
        }
    }
}

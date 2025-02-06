package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventLabelRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Profile({"generateData", "test"})
@Component("EventDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator"})
public class EventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventsRepository eventsRepository;
    private final EventLabelRepository labelRepository;

    public EventDataGenerator(EventsRepository eventsRepository, EventLabelRepository labelRepository) {
        this.eventsRepository = eventsRepository;
        this.labelRepository = labelRepository;
    }

    @PostConstruct
    public void generateEvents() {

        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setId(1L);

        EventLabel label = new EventLabel();
        label.setLabelName("party");
        label.setLabelColour("#de1b17");
        labelRepository.save(label);
        List<EventLabel> labels = new ArrayList<>();
        labels.add(label);

        Event test1 = new Event();
        test1.setTitle("House Meeting");
        test1.setDescription("Discussing important matters regarding the shared living space.");
        test1.setSharedFlat(sharedFlat);
        test1.setStartTime(LocalTime.of(16, 0));
        test1.setEndTime(LocalTime.of(17, 0));
        test1.setDate(LocalDate.now().plusDays(7));

        Event test2 = new Event();
        test2.setTitle("Cleaning Day");
        test2.setDescription("A day dedicated to cleaning and maintaining the shared areas.");
        test2.setSharedFlat(sharedFlat);
        test2.setStartTime(LocalTime.of(0, 0));
        test2.setEndTime(LocalTime.of(23, 59));
        test2.setDate(LocalDate.now().plusDays(18));

        Event test3 = new Event();
        test3.setTitle("Movie Night");
        test3.setDescription("Gathering for a cozy movie night in the common area.");
        test3.setSharedFlat(sharedFlat);
        test3.setStartTime(LocalTime.of(16, 0));
        test3.setEndTime(LocalTime.of(17, 0));
        test3.setDate(LocalDate.now().plusDays(11));

        Event test4 = new Event();
        test4.setTitle("Game Night");
        test4.setDescription("An evening filled with games and fun in the shared space.");
        test4.setSharedFlat(sharedFlat);
        test4.setStartTime(LocalTime.of(18, 0));
        test4.setEndTime(LocalTime.of(22, 0));
        test4.setDate(LocalDate.now().plusDays(28));
        test4.setLabels(labels);

        Event test5 = new Event();
        test5.setTitle("Karaoke Night");
        test5.setDescription("Singing and dancing to favorite tunes in the common area.");
        test5.setSharedFlat(sharedFlat);
        test5.setStartTime(LocalTime.of(19, 0));
        test5.setEndTime(LocalTime.of(22, 0));
        test5.setDate(LocalDate.now().plusDays(5));
        test5.setLabels(labels);

        eventsRepository.save(test1);
        eventsRepository.save(test2);
        eventsRepository.save(test3);
        eventsRepository.save(test4);
        eventsRepository.save(test5);
    }
}

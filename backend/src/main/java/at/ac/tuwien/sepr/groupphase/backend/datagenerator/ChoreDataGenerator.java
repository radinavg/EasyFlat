package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

@Profile({"generateData", "test", "unitTest"})
@Component("ChoreDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator"})
public class ChoreDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ChoreRepository choreRepository;

    public ChoreDataGenerator(ChoreRepository choreRepository) {
        this.choreRepository = choreRepository;
    }

    @PostConstruct
    public void generateChores() {
        Chore chore = new Chore();
        chore.setName("Cleaning the Bathroom");
        chore.setDescription("You must clean the toilet too");
        chore.setPoints(6);
        chore.setEndDate(LocalDate.now());
        chore.setSharedFlat(new SharedFlat().setId(1L));
        choreRepository.save(chore);

        Chore chore1 = new Chore();
        chore1.setName("Grocery Shopping");
        chore1.setDescription("You must buy everything from Shopping List (Default)");
        chore1.setPoints(7);
        chore1.setEndDate(LocalDate.now().plusDays(2));
        chore1.setSharedFlat(new SharedFlat().setId(1L));
        choreRepository.save(chore1);

        Chore chore2 = new Chore();
        chore2.setName("Throw Trash");
        chore2.setDescription("You must throw the trash from the kitchen");
        chore2.setPoints(3);
        chore2.setEndDate(LocalDate.now().plusDays(1));
        chore2.setSharedFlat(new SharedFlat().setId(1L));
        choreRepository.save(chore2);

        Chore chore3 = new Chore();
        chore3.setName("Fix the main door");
        chore3.setDescription("Can't close easily");
        chore3.setPoints(5);
        chore3.setEndDate(LocalDate.now().plusDays(4));
        chore3.setSharedFlat(new SharedFlat().setId(1L));
        choreRepository.save(chore3);

        Chore chore4 = new Chore();
        chore4.setName("Laundry");
        chore4.setDescription("Do the laundry");
        chore4.setPoints(9);
        chore4.setEndDate(LocalDate.now().plusDays(5));
        chore4.setSharedFlat(new SharedFlat().setId(1L));
        choreRepository.save(chore4);

        Chore chore5 = new Chore();
        chore5.setName("Vacuum Cleaning");
        chore5.setDescription("Vacuum clean the entire flat");
        chore5.setPoints(8);
        chore5.setEndDate(LocalDate.now().plusDays(3));
        chore5.setSharedFlat(new SharedFlat().setId(2L));
        choreRepository.save(chore5);

        Chore chore6 = new Chore();
        chore6.setName("Mow the Lawn");
        chore6.setDescription("Mow the front and back lawn");
        chore6.setPoints(10);
        chore6.setEndDate(LocalDate.now().plusDays(6));
        chore6.setSharedFlat(new SharedFlat().setId(2L));
        choreRepository.save(chore6);

        Chore chore7 = new Chore();
        chore7.setName("Dishwashing");
        chore7.setDescription("Wash and dry all dishes");
        chore7.setPoints(4);
        chore7.setEndDate(LocalDate.now().plusDays(2));
        chore7.setSharedFlat(new SharedFlat().setId(2L));
        choreRepository.save(chore7);

        Chore chore8 = new Chore();
        chore8.setName("Watering Plants");
        chore8.setDescription("Water all the plants in the flat");
        chore8.setPoints(6);
        chore8.setEndDate(LocalDate.now().plusDays(4));
        chore8.setSharedFlat(new SharedFlat().setId(2L));
        choreRepository.save(chore8);

        Chore chore9 = new Chore();
        chore9.setName("Assemble Furniture");
        chore9.setDescription("Assemble new furniture in the living room");
        chore9.setPoints(7);
        chore9.setEndDate(LocalDate.now().plusDays(5));
        chore9.setSharedFlat(new SharedFlat().setId(2L));
        choreRepository.save(chore9);

        Chore chore10 = new Chore();
        chore10.setName("Window Cleaning");
        chore10.setDescription("Clean all windows in the flat");
        chore10.setPoints(9);
        chore10.setEndDate(LocalDate.now().plusDays(7));
        chore10.setSharedFlat(new SharedFlat().setId(3L));
        choreRepository.save(chore10);

        Chore chore11 = new Chore();
        chore11.setName("Cooking Dinner");
        chore11.setDescription("Prepare a delicious dinner for everyone");
        chore11.setPoints(8);
        chore11.setEndDate(LocalDate.now().plusDays(3));
        chore11.setSharedFlat(new SharedFlat().setId(3L));
        choreRepository.save(chore11);

        Chore chore12 = new Chore();
        chore12.setName("Walk the Dog");
        chore12.setDescription("Take the dog for a walk");
        chore12.setPoints(5);
        chore12.setEndDate(LocalDate.now().plusDays(6));
        chore12.setSharedFlat(new SharedFlat().setId(3L));
        choreRepository.save(chore12);

        Chore chore13 = new Chore();
        chore13.setName("Car Wash");
        chore13.setDescription("Wash and clean the car");
        chore13.setPoints(7);
        chore13.setEndDate(LocalDate.now().plusDays(2));
        chore13.setSharedFlat(new SharedFlat().setId(3L));
        choreRepository.save(chore13);

        Chore chore14 = new Chore();
        chore14.setName("Gardening");
        chore14.setDescription("Tend to the garden and plant new flowers");
        chore14.setPoints(6);
        chore14.setEndDate(LocalDate.now().plusDays(4));
        chore14.setSharedFlat(new SharedFlat().setId(3L));
        choreRepository.save(chore14);

        Chore chore15 = new Chore();
        chore15.setName("Study Session");
        chore15.setDescription("Study together in the living room");
        chore15.setPoints(9);
        chore15.setEndDate(LocalDate.now().plusDays(5));
        chore15.setSharedFlat(new SharedFlat().setId(3L));
        choreRepository.save(chore15);

        Chore chore16 = new Chore();
        chore16.setName("Movie Night Organizer");
        chore16.setDescription("Organize a movie night for the flat");
        chore16.setPoints(10);
        chore16.setEndDate(LocalDate.now().plusDays(7));
        chore16.setSharedFlat(new SharedFlat().setId(4L));
        choreRepository.save(chore16);

        Chore chore17 = new Chore();
        chore17.setName("Clean the refrigerator");
        chore17.setDescription("Remove old food, wipe down shelves and drawers, and deodorize.");
        chore17.setPoints(5);
        chore17.setEndDate(LocalDate.now().plusDays(3));
        chore17.setSharedFlat(new SharedFlat().setId(4L));
        choreRepository.save(chore17);

        Chore chore18 = new Chore();
        chore18.setName("Change the air filters");
        chore18.setDescription("Replace the air filters in the furnace and/or air conditioner.");
        chore18.setPoints(7);
        chore18.setEndDate(LocalDate.now().plusDays(4));
        chore18.setSharedFlat(new SharedFlat().setId(4L));
        choreRepository.save(chore18);

        Chore chore19 = new Chore();
        chore19.setName("Dust the furniture");
        chore19.setDescription("Use a feather duster or microfiber cloth to remove dust from all surfaces of furniture.");
        chore19.setPoints(4);
        chore19.setEndDate(LocalDate.now().plusDays(5));
        chore19.setSharedFlat(new SharedFlat().setId(4L));
        choreRepository.save(chore19);

        Chore chore20 = new Chore();
        chore20.setName("Vacuum the couches");
        chore20.setDescription("Use a vacuum with a brush attachment to thoroughly vacuum the cushions and crevices of couches.");
        chore20.setPoints(6);
        chore20.setEndDate(LocalDate.now().plusDays(6));
        chore20.setSharedFlat(new SharedFlat().setId(4L));
        choreRepository.save(chore20);

        Chore chore21 = new Chore();
        chore21.setName("Clean the oven");
        chore21.setDescription("Remove all racks and accessories from the oven, and clean the interior and exterior сwith oven cleaner.");
        chore21.setPoints(9);
        chore21.setEndDate(LocalDate.now().plusDays(7));
        chore21.setSharedFlat(new SharedFlat().setId(5L));
        choreRepository.save(chore21);

        Chore chore22 = new Chore();
        chore22.setName("Organize the pantry");
        chore22.setDescription("Sort and categorize food items, and discard expired or unused products.");
        chore22.setPoints(8);
        chore22.setEndDate(LocalDate.now().plusDays(8));
        chore22.setSharedFlat(new SharedFlat().setId(5L));
        choreRepository.save(chore22);

        Chore chore23 = new Chore();
        chore23.setName("Clean the windowsills");
        chore23.setDescription("Use a damp cloth to wipe down windowsills, and remove any dust or cobwebs.");
        chore23.setPoints(5);
        chore23.setEndDate(LocalDate.now().plusDays(9));
        chore23.setSharedFlat(new SharedFlat().setId(5L));
        choreRepository.save(chore23);

        Chore chore24 = new Chore();
        chore24.setName("Scrub the bathtub or shower");
        chore24.setDescription("Use a bathtub or shower cleaner and a sponge to remove soap scum and hard water stains.");
        chore24.setPoints(7);
        chore24.setEndDate(LocalDate.now().plusDays(10));
        chore24.setSharedFlat(new SharedFlat().setId(5L));
        choreRepository.save(chore24);

        Chore chore25 = new Chore();
        chore25.setName("Polish the appliances");
        chore25.setDescription("Use a polish made specifically for appliances to clean and shine the microwave, stovetop, refrigerator, and dishwasher.");
        chore25.setPoints(6);
        chore25.setEndDate(LocalDate.now().plusDays(11));
        chore25.setSharedFlat(new SharedFlat().setId(5L));
        choreRepository.save(chore25);

    }






}

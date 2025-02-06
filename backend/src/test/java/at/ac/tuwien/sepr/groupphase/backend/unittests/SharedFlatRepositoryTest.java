package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class SharedFlatRepositoryTest {

    @Autowired
    private SharedFlatRepository sharedFlatRepository;

    @Test
    @DisplayName("Positive test for saving a valid shared flat")
    public void saveValidSharedFlatThenFindItByName() {

        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setName("FlatName");
        sharedFlat.setPassword("FlatPassword");

        SharedFlat savedFlat = sharedFlatRepository.save(sharedFlat);
        SharedFlat foundFlat = sharedFlatRepository.findFirstByName("FlatName");

        assertNotNull(foundFlat);
        assertEquals(savedFlat.getId(), foundFlat.getId());
        assertEquals(savedFlat.getName(), foundFlat.getName());
        assertEquals(savedFlat.getPassword(), foundFlat.getPassword());
    }

    @Test
    @DisplayName("Positive test for deleting an existing shared flat")
    public void deleteExistingSharedFlatAndCheckIfSuccessfullyDeleted() {
        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setName("FlatName");
        sharedFlat.setPassword("FlatPassword");
        SharedFlat savedFlat = sharedFlatRepository.save(sharedFlat);

        SharedFlat foundFlat = sharedFlatRepository.findFirstByName("FlatName");
        assertNotNull(foundFlat);

        sharedFlatRepository.delete(savedFlat);
        SharedFlat deletedFlat = sharedFlatRepository.findFirstByName("FlatName");
        assertNull(deletedFlat, "Deleted flat should not be found");
    }

    @Test
    @DisplayName("Negative test for finding a non-existent shared flat")
    public void findNonExistentSharedFlatShouldReturnNull() {
        SharedFlat foundFlat = sharedFlatRepository.findFirstByName("NonExistentFlatName");

        assertNull(foundFlat, "A non-existent flat should return null");
    }

}

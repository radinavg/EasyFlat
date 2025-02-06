package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest implements TestData {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Positive test for saving a valid user")
    public void saveValidUserThenFindItByEmail() {

        ApplicationUser user = new ApplicationUser();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password");
        user.setAdmin(false);

        ApplicationUser savedUser = userRepository.save(user);
        ApplicationUser foundUser = userRepository.findUserByEmail("johndoe@example.com");

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
        assertEquals(savedUser.getFirstName(), foundUser.getFirstName());
        assertEquals(savedUser.getLastName(), foundUser.getLastName());
        assertEquals(savedUser.getPassword(), foundUser.getPassword());
        assertEquals(savedUser.getAdmin(), foundUser.getAdmin());
    }

    @Test
    @DisplayName("Negative test for saving a user with a duplicate email")
    public void saveUserWithDuplicateEmailShouldThrowException() {

        ApplicationUser user1 = new ApplicationUser();
        user1.setFirstName("Alice");
        user1.setLastName("Smith");
        user1.setEmail("alice@example.com");
        user1.setPassword("password");
        user1.setAdmin(false);
        userRepository.save(user1);

        ApplicationUser user2 = new ApplicationUser();
        user2.setFirstName("Bob");
        user2.setLastName("Johnson");
        user2.setEmail("alice@example.com");
        user2.setPassword("password");
        user2.setAdmin(false);

        assertThrows(Exception.class, () -> userRepository.save(user2));
    }

    @Test
    @DisplayName("Positive test for updating an existing user")
    public void updateExistingUserThenCompareIfDataIsUpdated() {

        ApplicationUser user = new ApplicationUser();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password");
        user.setAdmin(false);
        ApplicationUser savedUser = userRepository.save(user);
        String newEmail = "john.new@example.com";
        savedUser.setEmail(newEmail);
        ApplicationUser updatedUser = userRepository.save(savedUser);
        ApplicationUser foundUser = userRepository.findUserByEmail(newEmail);

        assertNotNull(foundUser);
        assertEquals(updatedUser.getId(), foundUser.getId());
        assertEquals("john.new@example.com", foundUser.getEmail());
    }

    @Test
    @DisplayName("Positive test for deleting an existing user")
    public void deleteExistingUserAndCheckIfSuccessfullyDeleted() {
        ApplicationUser user = new ApplicationUser();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password");
        user.setAdmin(false);
        ApplicationUser savedUser = userRepository.save(user);

        ApplicationUser foundUser = userRepository.findUserByEmail("johndoe@example.com");
        assertNotNull(foundUser);

        userRepository.delete(savedUser);
        ApplicationUser deletedUser = userRepository.findUserByEmail("johndoe@example.com");
        assertNull(deletedUser, "Deleted user should not be found");
    }
}

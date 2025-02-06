package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedFlatRepository extends JpaRepository<SharedFlat, Long> {
    /**
     * Finds the first shared flat by its name.
     *
     * @param name The name of the shared flat to find.
     * @return The SharedFlat entity.
     */
    SharedFlat findFirstByName(String name);

    /**
     * Deletes a shared flat by its name.
     *
     * @param name The name of the shared flat to delete.
     */
    void deleteByName(String name);

}

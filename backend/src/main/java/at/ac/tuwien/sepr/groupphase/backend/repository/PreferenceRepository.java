package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    @Query("SELECT c FROM preference c WHERE c.user = :user")
    Preference findByUserId(@Param("user") ApplicationUser user);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM preference p WHERE p.user = :user")
    boolean existsByUserId(@Param("user") ApplicationUser user);

    void deleteAllByUserSharedFlatIs(SharedFlat sharedFlat);

    List<Preference> findAllByUserSharedFlatIs(SharedFlat sharedFlat);
}

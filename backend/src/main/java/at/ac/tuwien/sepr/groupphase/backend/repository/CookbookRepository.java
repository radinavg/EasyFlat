package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CookbookRepository extends JpaRepository<Cookbook, Long> {

    List<Cookbook> findBySharedFlatIs(SharedFlat sharedFlat);
}

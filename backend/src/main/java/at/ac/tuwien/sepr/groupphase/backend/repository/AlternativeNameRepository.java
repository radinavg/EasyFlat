package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlternativeNameRepository extends JpaRepository<AlternativeName, String> {

    AlternativeName findAllByNameAndShareFlatId(String name, Long sharedFlatId);
}

package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLabelRepository extends JpaRepository<EventLabel, Long> {

    EventLabel findByLabelNameAndLabelColour(String labelName, String labelColour);
}

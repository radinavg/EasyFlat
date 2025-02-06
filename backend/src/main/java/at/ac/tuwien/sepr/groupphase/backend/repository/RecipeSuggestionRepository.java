package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeSuggestionRepository extends JpaRepository<RecipeSuggestion, Long> {

}

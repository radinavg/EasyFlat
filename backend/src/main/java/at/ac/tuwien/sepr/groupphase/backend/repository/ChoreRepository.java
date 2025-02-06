package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChoreRepository extends JpaRepository<Chore, Long> {
    @Query("SELECT c FROM chore c WHERE c.sharedFlat.id = :id")
    List<Chore> findAllBySharedFlatId(@Param("id") Long id);

    @Query("SELECT c FROM chore c WHERE c.name = :name")
    Chore findByName(@Param("name") String name);

    @Query("SELECT c FROM chore c WHERE c.user = :user")
    boolean existsByUserId(@Param("user") Long user
    );

    @Query("SELECT c FROM chore c WHERE c.sharedFlat.id = :id AND c.user IS NULL")
    List<Chore> findAllBySharedFlatIdWhereUserIsNull(@Param("id") Long id);

    @Query("SELECT c FROM chore c WHERE c.sharedFlat.id = :id AND c.user.id = :userId")
    List<Chore> allChoresByUserId(@Param("id") Long id, @Param("userId") Long userId);


    List<Chore> findAllByUser(ApplicationUser applicationUser);

    @Query("SELECT c FROM chore c "
        + "LEFT JOIN c.user u "
        + "WHERE c.sharedFlat.id = :id "
        + "AND (:userName IS NULL OR LOWER(CONCAT(c.user.firstName, ' ', c.user.lastName)) LIKE LOWER(CONCAT('%', :userName, '%'))) "
        + "AND (:endDate IS NULL OR c.endDate >= :endDate)")
    List<Chore> searchChores(@Param("userName") String userName, @Param("endDate") LocalDate endDate, @Param("id") Long id);

    List<Chore> searchBySharedFlatAndUserIs(SharedFlat sharedFlat, ApplicationUser o);
}

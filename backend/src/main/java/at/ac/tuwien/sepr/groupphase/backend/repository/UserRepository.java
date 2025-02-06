package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//TODO: replace this class with a correct ApplicationUser JPARepository implementation
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findUserByEmail(String email);

    @Query("SELECT u.sharedFlat.id FROM application_user u WHERE u.email = :email")
    SharedFlat findFlatIdByEmail(@Param("email") String email);

    ApplicationUser findApplicationUserBySharedFlat(SharedFlat sharedFlat);

    void deleteByEmail(String email);

    boolean existsBySharedFlat(SharedFlat flat);

    ApplicationUser findApplicationUserById(long id);

    @Query("SELECT u FROM application_user u WHERE u.sharedFlat.id = :flatId")
    List<ApplicationUser> findAllByFlatId(@Param("flatId") Long flatId);

    List<ApplicationUser> findAllBySharedFlat(SharedFlat sharedFlat);
}

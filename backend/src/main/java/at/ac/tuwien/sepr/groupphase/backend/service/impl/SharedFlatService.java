package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authorization.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.SharedFlatValidatorImpl;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class SharedFlatService implements at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatRepository sharedFlatRepository;
    private final PasswordEncoder passwordEncoder;
    private final SharedFlatMapper sharedFlatMapper;
    private final UserRepository userRepository;
    private final DigitalStorageRepository digitalStorageRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final CookbookRepository cookbookRepository;
    private final SharedFlatValidatorImpl validator;
    private final AuthService authService;
    private final ChoreRepository choreRepository;
    private final PreferenceRepository preferenceRepository;

    @Autowired
    public SharedFlatService(SharedFlatRepository sharedFlatRepository,
                             PasswordEncoder passwordEncoder,
                             SharedFlatMapper sharedFlatMapper,
                             DigitalStorageRepository digitalStorageRepository,
                             CookbookRepository cookbookRepository,
                             UserRepository userRepository,
                             ShoppingListRepository shoppingListRepository,
                             SharedFlatValidatorImpl validator, AuthService authService, ChoreRepository choreRepository, PreferenceRepository preferenceRepository) {
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
        this.sharedFlatMapper = sharedFlatMapper;
        this.userRepository = userRepository;
        this.digitalStorageRepository = digitalStorageRepository;


        this.cookbookRepository = cookbookRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.validator = validator;
        this.authService = authService;
        this.choreRepository = choreRepository;
        this.preferenceRepository = preferenceRepository;
    }


    @Transactional
    public WgDetailDto create(WgDetailDto wgDetailDto) throws ConflictException, ValidationException {
        LOGGER.trace("create({})", wgDetailDto);
        LOGGER.debug("Create a new shared flat");
        validator.validateForCreate(wgDetailDto);
        //validator.validateForCreate(sharedFlat);
        SharedFlat existingSharedFlat = sharedFlatRepository.findFirstByName(wgDetailDto.getName());
        if (existingSharedFlat != null) {
            throw new ConflictException("A flat with this name already exists");
        }

        SharedFlat newSharedFlat = new SharedFlat();
        newSharedFlat.setName(wgDetailDto.getName());
        newSharedFlat.setPassword(passwordEncoder.encode(wgDetailDto.getPassword()));
        ApplicationUser user = authService.getUserFromToken();
        sharedFlatRepository.save(newSharedFlat);
        user.setSharedFlat(newSharedFlat);
        user.setAdmin(true);
        userRepository.save(user);
        DigitalStorage digitalStorage = new DigitalStorage();
        digitalStorage.setTitle("Storage " + newSharedFlat.getName());
        digitalStorage.setSharedFlat(newSharedFlat);
        newSharedFlat.setDigitalStorage(digitalStorage);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName("Shopping List (Default)");
        shoppingList.setSharedFlat(newSharedFlat);
        shoppingListRepository.save(shoppingList);

        digitalStorageRepository.save(digitalStorage);

        Cookbook cookbook = new Cookbook();
        cookbook.setTitle("Cookbook " + newSharedFlat.getName());
        cookbook.setSharedFlat(newSharedFlat);
        newSharedFlat.setCookbook(cookbook);

        cookbookRepository.save(cookbook);

        return sharedFlatMapper.entityToWgDetailDto(newSharedFlat);
    }

    @Override
    public WgDetailDto loginWg(WgDetailDto wgDetailDto) {
        LOGGER.trace("loginWg({})", wgDetailDto);
        String name = wgDetailDto.getName();
        String rawPassword = wgDetailDto.getPassword();

        ApplicationUser user = authService.getUserFromToken();

        SharedFlat existingSharedFlat = sharedFlatRepository.findFirstByName(name);


        if (existingSharedFlat != null) {
            boolean passwordMatches = passwordEncoder.matches(rawPassword, existingSharedFlat.getPassword());

            if (passwordMatches) {
                if (user.getEmail() != null) {
                    user.setSharedFlat(existingSharedFlat);
                    user.setAdmin(false);
                    userRepository.save(user);
                }
                return sharedFlatMapper.entityToWgDetailDto(existingSharedFlat);
            } else {
                throw new IllegalStateException("Invalid credentials. Could not log in.");
            }
        } else {
            throw new IllegalStateException("Invalid credentials. Could not log in.");
        }
    }

    @Override
    @Transactional
    public WgDetailDto delete(Long id) throws AuthorizationException {
        LOGGER.trace("delete()");
        Optional<ApplicationUser> applicationUser = userRepository.findById(id);
        if (applicationUser.isPresent()) {
            ApplicationUser user = applicationUser.get();
            if (!user.getAdmin()) {
                throw new AuthorizationException("Authorization Error", List.of("User is not admin, so he can not delete the flat"));
            } else {
                SharedFlat flat = user.getSharedFlat();
                if (flat == null) {
                    throw new NotFoundException("Flat doesn't exist");
                }
                Long deletedFlatId = flat.getId();
                List<ApplicationUser> users = userRepository.findAllByFlatId(deletedFlatId);
                if (!users.isEmpty()) {
                    for (ApplicationUser us : users) {
                        if (us.getPreference() != null) {
                            preferenceRepository.delete(us.getPreference());
                        }
                        us.setPreference(null);
                        us.setSharedFlat(null);
                        us.setPoints(0);
                        us.setAdmin(false);
                        userRepository.save(us);
                    }
                }
                List<Chore> chores = choreRepository.findAllBySharedFlatId(deletedFlatId);
                if (!chores.isEmpty()) {    //are there chores
                    choreRepository.deleteAll(chores);
                }
                user.setPoints(0);
                user.setSharedFlat(null);
                user.setAdmin(null);
                userRepository.save(user);
                sharedFlatRepository.deleteById(deletedFlatId);
                return sharedFlatMapper.entityToWgDetailDto(flat);
            }
        } else {
            throw new NotFoundException("User not found");
        }
    }
}

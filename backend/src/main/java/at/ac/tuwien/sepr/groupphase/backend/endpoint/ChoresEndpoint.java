package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RepeatChoreRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/chores")
public class ChoresEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreService choreService;

    private final ChoreMapper choreMapper;

    private final UserMapper userMapper;

    public ChoresEndpoint(ChoreService choreService, ChoreMapper choreMapper, UserMapper userMapper) {
        this.choreService = choreService;
        this.choreMapper = choreMapper;
        this.userMapper = userMapper;
    }


    @PostMapping
    @Secured("ROLE_USER")
    public ChoreDto createChore(@RequestBody ChoreDto chore) throws ValidationException, ConflictException {
        LOGGER.trace("createChore({})", chore);
        return choreService.createChore(chore);
    }


    @GetMapping()
    @Secured("ROLE_USER")
    public List<ChoreDto> getChores(ChoreSearchDto searchParams) {
        LOGGER.trace("getChores({})", searchParams);
        List<Chore> lists = choreService.getChores(searchParams);
        return choreMapper.entityListToDtoList(lists);
    }

    @GetMapping("/unassigned")
    @Secured("ROLE_USER")
    public List<ChoreDto> getUnassignedChores() {
        LOGGER.trace("getUnassignedChores()");
        List<Chore> chores = choreService.getUnassignedChores();
        return choreMapper.entityListToDtoList(chores);
    }

    @PutMapping
    @Secured("ROLE_USER")
    public List<ChoreDto> assignChores() {
        LOGGER.trace("assignChores()");
        List<ChoreDto> ret = this.choreService.assignChores();
        this.choreService.deleteAllUserPreference();
        return ret;
    }

    @GetMapping("/user")
    @Secured("ROLE_USER")
    public List<ChoreDto> getChoresByUser() {
        LOGGER.trace("getChoresByUser()");
        List<Chore> chores = choreService.getChoresByUser();
        return choreMapper.entityListToDtoList(chores);
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_USER")
    public List<ChoreDto> deleteChores(@RequestParam(name = "choreIds") String choreIdsString) throws AuthorizationException {
        LOGGER.trace("deleteChores({})", choreIdsString);
        List<Long> choreIds = Arrays.stream(choreIdsString.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());

        List<Chore> deletedChores = choreService.deleteChores(choreIds);
        return choreMapper.entityListToDtoList(deletedChores);
    }

    @GetMapping("/users")
    @Secured("ROLE_USER")
    public List<UserDetailDto> getUsers() {
        LOGGER.trace("getUsers()");
        List<ApplicationUser> users = choreService.getUsers();

        return userMapper.entityListToUserDetailDtoList(users);
    }

    @PatchMapping("/{userId}")
    @Secured("ROLE_USER")
    public UserDetailDto updatePoints(@PathVariable Long userId, @RequestBody UserDetailDto userPoints) {
        LOGGER.trace("updatePoints({}, {})", userId, userPoints);

        Integer points = userPoints.getPoints();

        ApplicationUser updatedChore = choreService.updatePoints(userId, points);

        return userMapper.entityToUserDetailDto(updatedChore);
    }

    @GetMapping("/pdf")
    @Secured("ROLE_USER")
    public ResponseEntity<byte[]> generateChoreListPdf() throws IOException {
        byte[] pdfBytes = choreService.generatePdf();
        return new ResponseEntity<>(pdfBytes, HttpStatus.OK);
    }

    @PatchMapping("/repeat")
    @Secured("ROLE_USER")
    public ChoreDto repeatChore(@RequestBody RepeatChoreRequest request) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.trace("repeatChore({})", request);
        Long id = request.getId();
        Date date = request.getDate();
        return choreService.repeatChore(id, date);
    }


}

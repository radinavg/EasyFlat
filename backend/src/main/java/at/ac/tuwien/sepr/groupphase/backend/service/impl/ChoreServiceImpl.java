package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ChoreValidator;
import com.itextpdf.text.DocumentException;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ChoreServiceImpl implements ChoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreRepository choreRepository;

    private final ChoreMapper choreMapper;

    private final AuthService authService;

    private final UserRepository userRepository;

    private final PreferenceRepository preferenceRepository;

    private final ChoreValidator choreValidator;

    public ChoreServiceImpl(ChoreRepository choreRepository, ChoreMapper choreMapper, AuthService authService, UserRepository userRepository, PreferenceRepository preferenceRepository, ChoreValidator choreValidator) {
        this.choreRepository = choreRepository;
        this.choreMapper = choreMapper;
        this.authService = authService;
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.choreValidator = choreValidator;
    }

    @Transactional
    public ChoreDto createChore(ChoreDto choreDto) throws ValidationException, ConflictException {
        LOGGER.trace("createChore({})", choreDto);
        ChoreDto newChore;
        if (choreDto.description() != null) {
            newChore = choreDto.trimmed(choreDto.name().trim(), choreDto.description().trim());
        } else {
            newChore = choreDto.trimmedName(choreDto.name().trim());
        }
        this.choreValidator.validateForCreate(newChore);
        ApplicationUser applicationUser = authService.getUserFromToken();
        Chore chore = choreMapper.choreDtoToEntity(newChore);
        chore.setSharedFlat(applicationUser.getSharedFlat());
        Chore savedChore = choreRepository.save(chore);
        return choreMapper.entityToChoreDto(savedChore);
    }

    @Override
    public List<Chore> getChores(ChoreSearchDto searchParams) {
        LOGGER.trace("createChore({})", searchParams);
        ApplicationUser applicationUser = authService.getUserFromToken();
        return choreRepository.searchChores(
            (searchParams.userName() != null) ? searchParams.userName() : null,
            (searchParams.endDate() != null) ? searchParams.endDate() : null,
            applicationUser.getSharedFlat().getId());
    }

    @Override
    public List<ChoreDto> assignChores() {
        LOGGER.trace("assignChores()");
        //check the user
        ApplicationUser applicationUser = authService.getUserFromToken();
        //all chores from this flat
        List<Chore> chores = choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
        List<Chore> choresAfterAssign = choreRepository.findAllBySharedFlatIdWhereUserIsNull(applicationUser.getSharedFlat().getId());
        if (choresAfterAssign.size() == 0) {
            throw new NotFoundException("All of the chores are assigned to users");
        } else if (choresAfterAssign.size() == chores.size()) {
            return assignChoresHelp(chores);
        } else {
            return secondAssign(choresAfterAssign);
        }
    }

    private List<ChoreDto> secondAssign(List<Chore> choresAfterAssign) {
        LOGGER.trace("secondAssign({})", choresAfterAssign);
        //check the user
        ApplicationUser applicationUser = authService.getUserFromToken();
        //all chores for this flat
        List<Chore> chores = choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
        //all users from this flat
        List<ApplicationUser> users = userRepository.findAllBySharedFlat(applicationUser.getSharedFlat());
        //sort the users by points
        sortUsersByPoints(users);
        List<ApplicationUser> notAssignedUsers = new ArrayList<>();
        //5-5 or 5-6
        if (chores.size() >= users.size() && (choresAfterAssign.size() % users.size() == 0)) {
            for (int i = users.size() - choresAfterAssign.size(); i < users.size(); i++) {
                Chore toAssign = getRandomChore(choresAfterAssign);
                toAssign.setUser(users.get(i));
                choresAfterAssign.remove(toAssign);
                choreRepository.save(toAssign);
            }
            //3-5 then some users have one chore more than the others
            //5-3 then some users have no chores
        } else {
            while (!choresAfterAssign.isEmpty()) {
                int globalCount = (int) Math.floor((double) (chores.size() - choresAfterAssign.size()) / users.size());
                for (int j = users.size() - 1; j >= 0; j--) {
                    if (choreRepository.allChoresByUserId(users.get(j).getSharedFlat().getId(), users.get(j).getId()).size() <= globalCount) {
                        List<Chore> choresUser = getPreferences(users.get(j));
                        int count = 0;
                        for (Chore choreToAssign : choresUser) {
                            if (choreToAssign.getUser() == null) {
                                choreToAssign.setUser(users.get(j));
                                choresAfterAssign.remove(choreToAssign);
                                choreRepository.save(choreToAssign);
                                count++;
                                break;
                            }
                        }
                        if (count == 0) {
                            notAssignedUsers.add(users.get(j));
                        }
                    }
                }
                for (ApplicationUser user : notAssignedUsers) {
                    if (choresAfterAssign.size() == 0) {
                        break;
                    }
                    Chore toAssign = getRandomChore(choresAfterAssign);
                    toAssign.setUser(user);
                    choresAfterAssign.remove(toAssign);
                    choreRepository.save(toAssign);
                }
                if (choresAfterAssign.size() == 0) {
                    break;
                } else {
                    notAssignedUsers = new ArrayList<>();
                }
            }
        }

        return choreMapper.entityListToDtoList(choreRepository.findAllBySharedFlatId(applicationUser.getId()));
    }

    private List<ChoreDto> assignChoresHelp(List<Chore> chores) {
        LOGGER.trace("assignChoresHelp({})", chores);
        //check the user
        ApplicationUser applicationUser = authService.getUserFromToken();
        //all users from this flat
        List<ApplicationUser> users = userRepository.findAllBySharedFlat(applicationUser.getSharedFlat());
        //all chores without user from this flat are stored in chores list.

        //sort the users list by points
        this.sortUsersByPoints(users);
        //list for users, which preferences are already taken.
        List<ApplicationUser> notAssignedUsers = new ArrayList<>();

        //5users - 6 chores
        //5users - 5 chores
        if (users.size() <= chores.size()) {
            for (int i = 0; i < users.size(); i++) { // size 5
                //list of chores for one user
                List<Chore> choresUser = getPreferences(users.get(i)); //max size 4
                int count = 0;
                //loop for the chores of the user
                for (int j = 0; j < choresUser.size(); j++) {
                    if (choresUser.get(j) != null && choresUser.get(j).getUser() == null) {
                        Chore toUpdate = choresUser.get(j);
                        chores.remove(toUpdate);
                        toUpdate.setUser(users.get(i));
                        count++;
                        choreRepository.save(toUpdate);
                        break;
                    }
                }
                //if the user didn't take any chore he is added to the list of notAssignedUsers
                if (count == 0) {
                    notAssignedUsers.add(users.get(i));
                }
            }
            //assign randomly all not assigned Users
            for (ApplicationUser user : notAssignedUsers) {
                Chore toAssign = getRandomChore(chores);
                toAssign.setUser(user);
                chores.remove(toAssign);
                choreRepository.save(toAssign);
            }
            // rest of the chores that are not assigned
            //recursively add the rest of the chores
            if (!chores.isEmpty()) {
                assignChoresHelp(chores);
            }

        } else {
            //20users - 5chores
            for (int i = users.size() - chores.size(); i < users.size(); i++) {
                //list of chores for one user
                List<Chore> choresUser = getPreferences(users.get(i));
                int count = 0;
                //loop for the chores of the user
                for (int j = 0; j < choresUser.size(); j++) {
                    if (choresUser.get(j) != null && choresUser.get(j).getUser() == null) {
                        Chore toUpdate = choresUser.get(j);
                        chores.remove(toUpdate);
                        toUpdate.setUser(users.get(i));
                        count++;
                        choreRepository.save(toUpdate);
                        break;
                    }
                }
                //if the user didn't take any chore he is added to the list of notAssignedUsers
                if (count == 0) {
                    notAssignedUsers.add(users.get(i));
                }
            }
            //assign randomly all not assigned Users
            for (ApplicationUser user : notAssignedUsers) {
                Chore toAssign = getRandomChore(chores);
                toAssign.setUser(user);
                chores.remove(toAssign);
                choreRepository.save(toAssign);
            }
        }

        return choreMapper.entityListToDtoList(choreRepository.findAllBySharedFlatId(applicationUser.getId()));
    }

    @Override
    public List<Chore> getChoresByUser() {
        LOGGER.trace("getChoresByUser()");
        ApplicationUser applicationUser = authService.getUserFromToken();
        return choreRepository.findAllByUser(applicationUser);
    }

    @Override
    @Transactional
    public List<Chore> deleteChores(List<Long> choreIds) throws AuthorizationException {
        LOGGER.trace("deleteChores({})", choreIds);
        List<Chore> toDelete = choreRepository.findAllById(choreIds);
        if (toDelete.size() != choreIds.size()) {
            throw new NotFoundException("The given chores do not exist in the persistent data");
        }
        ApplicationUser user = authService.getUserFromToken();
        for (Chore chore : toDelete) {
            if (!user.getSharedFlat().equals(chore.getSharedFlat()) && !user.equals(chore.getUser())) {
                throw new AuthorizationException("Authorization error", List.of("User has no access to chore: " + chore.getName()));
            }
        }
        choreRepository.deleteAllById(choreIds);
        return toDelete;
    }

    private Chore getRandomChore(List<Chore> chores) {
        LOGGER.trace("getRandomChore({})", chores);
        if (chores == null || chores.isEmpty()) {
            throw new IllegalArgumentException("List is empty or null");
        }

        Random rand = new Random();
        int randomIndex = rand.nextInt(chores.size());
        return chores.get(randomIndex);
    }

    private List<Chore> getPreferences(ApplicationUser user) {
        LOGGER.trace("getPreferences({})", user);
        List<Chore> toReturn = new ArrayList<>();
        if (!preferenceRepository.existsByUserId(user)) {
            return toReturn;
        }
        Preference preference = preferenceRepository.findByUserId(user);
        if (preference.getFirst() != null) {
            Optional<Chore> firstChore = choreRepository.findById(preference.getFirst().getId());
            if (firstChore.isPresent()) {
                Chore choreToAdd = firstChore.get();
                toReturn.add(choreToAdd);
            }
        }
        if (preference.getSecond() != null) {
            Optional<Chore> secondChore = choreRepository.findById(preference.getSecond().getId());
            if (secondChore.isPresent()) {
                Chore choreToAdd = secondChore.get();
                toReturn.add(choreToAdd);
            }
        }
        if (preference.getThird() != null) {
            Optional<Chore> thirdChore = choreRepository.findById(preference.getThird().getId());
            if (thirdChore.isPresent()) {
                Chore choreToAdd = thirdChore.get();
                toReturn.add(choreToAdd);
            }
        }
        if (preference.getFourth() != null) {
            Optional<Chore> fourthChore = choreRepository.findById(preference.getFourth().getId());
            if (fourthChore.isPresent()) {
                Chore choreToAdd = fourthChore.get();
                toReturn.add(choreToAdd);
            }
        }
        return toReturn;
    }

    private void sortUsersByPoints(List<ApplicationUser> users) {
        LOGGER.trace("sortUsersByPoints({})", users);
        int n = users.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                ApplicationUser user1 = users.get(j);
                ApplicationUser user2 = users.get(j + 1);
                if (user1.getPoints() < user2.getPoints()) {
                    ApplicationUser temp = users.get(j);
                    users.set(j, users.get(j + 1));
                    users.set(j + 1, temp);
                }
            }
        }
    }

    @Override
    public List<ApplicationUser> getUsers() {
        LOGGER.trace("getUsers()");
        ApplicationUser existingUser = authService.getUserFromToken();
        return userRepository.findAllBySharedFlat(existingUser.getSharedFlat());
    }

    @Override
    @Transactional
    public ApplicationUser updatePoints(Long userId, Integer points) {
        LOGGER.trace("updatePoints({},{})", userId, points);
        ApplicationUser existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        existingUser.setPoints(points);
        return userRepository.save(existingUser);
    }

    public byte[] generatePdf() throws IOException {
        LOGGER.trace("generatePdf()");
        String htmlContent = this.createChoreListHtml();

        Path tempFilePath = Files.createTempFile("my-pdf", ".html");

        Files.write(tempFilePath, htmlContent.getBytes(StandardCharsets.UTF_8));

        Document document = Jsoup.parse(tempFilePath.toFile(), "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);

            renderer.setDocumentFromString(htmlContent, tempFilePath.toUri().toURL().toString());
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public ChoreDto repeatChore(Long choreId, Date newDate) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.trace("repeatChore({},{})", choreId, newDate);
        if (newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now())) {
            throw new ValidationException("The data is not valid", List.of("The deadline must be in the present or in the future"));
        }
        ApplicationUser user = authService.getUserFromToken();
        Optional<Chore> toChange = choreRepository.findById(choreId);
        if (toChange.isPresent()) {
            Chore changeChore = toChange.get();
            if (!user.getSharedFlat().equals(changeChore.getSharedFlat())) {
                throw new AuthorizationException("Authorization error", List.of("User has no access to this chore"));
            }
            changeChore.setUser(null);
            changeChore.setEndDate(newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            choreValidator.validateForUpdate(changeChore);
            user.setPoints(user.getPoints() + changeChore.getPoints());
            userRepository.save(user);
            choreRepository.save(changeChore);
            return choreMapper.entityToChoreDto(changeChore);
        } else {
            throw new NotFoundException("Chore to repeat is not found");
        }
    }

    @Override
    public List<Chore> getUnassignedChores() {
        LOGGER.trace("getUnassignedChores()");
        ApplicationUser applicationUser = authService.getUserFromToken();
        return choreRepository.searchBySharedFlatAndUserIs(applicationUser.getSharedFlat(), null);
    }

    @Override
    @Transactional
    public void deleteAllUserPreference() {
        ApplicationUser user = authService.getUserFromToken();
        List<ApplicationUser> users = userRepository.findAllBySharedFlat(user.getSharedFlat());
        for (ApplicationUser applicationUser : users) {
            applicationUser.setPreference(null);
            userRepository.save(applicationUser);
        }
        List<Preference> preferences = preferenceRepository.findAllByUserSharedFlatIs(user.getSharedFlat());
        for (Preference preference : preferences) {
            preference.setUserId(null);
            preferenceRepository.save(preference);
            preferenceRepository.deleteById(preference.getId());
        }
    }

    private String createChoreListHtml() {
        List<Chore> chores = this.getChores(new ChoreSearchDto(null, null));
        if (chores.isEmpty()) {
            throw new NotFoundException("No chores found to export!");
        }
        chores.sort(Comparator.comparing(Chore::getEndDate));

        StringBuilder htmlContent = new StringBuilder();

        htmlContent.append("<html lang=\"en\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\"></meta>");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></meta>");
        htmlContent.append("<title>Chores</title>");

        htmlContent.append("<style>");
        htmlContent.append("body { font-family: 'Arial', sans-serif; margin: 20px; }");
        htmlContent.append("h1 { text-align: center; }");
        htmlContent.append("hr { border: 1px solid #ddd; }");
        htmlContent.append(".row { display: flex; flex-wrap: wrap; justify-content: space-between; margin-bottom: 20px; }");
        htmlContent.append(".chore-card { width: calc(25% - 1em); margin: 0.5em; padding: 1em; box-sizing: border-box; border: 1px solid #ddd; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); page-break-inside: avoid;}");
        htmlContent.append(".chore-card h2 { margin-top: 0; }");
        htmlContent.append(".chore-card p { margin: 0.5em 0; }");
        htmlContent.append("</style>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");

        htmlContent.append("<h1 class=\"display-4\">Chores</h1>");
        htmlContent.append("<hr></hr>");

        for (Chore value : chores) {
            htmlContent.append("<div class=\"row\">");
            htmlContent.append("<div class=\"chore-card\">");
            htmlContent.append("<h2>").append(value.getName()).append("</h2>");
            if (value.getDescription() != null) {
                htmlContent.append("<p><strong>Description:</strong> ").append(value.getDescription()).append("</p>");
            }
            htmlContent.append("<p><strong>Deadline:</strong> ").append(formatDate(value.getEndDate(), "dd.MM.yyyy")).append("</p>");
            htmlContent.append("<p><strong>Responsible Person:</strong> ").append(value.getUser() != null ? value.getUser().getFirstName() + " " + value.getUser().getLastName() : "None").append("</p>");
            htmlContent.append("</div>");
            htmlContent.append("</div>");
        }

        htmlContent.append("</body>");
        htmlContent.append("</html>");

        return htmlContent.toString();

    }

    private static String formatDate(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
}


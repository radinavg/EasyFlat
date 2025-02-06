package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ChoreService {

    /**
     * Creates a new chore based on the provided ChoreDto.
     *
     * @param chore The ChoreDto containing chore details.
     * @return The created ChoreDto.
     * @throws ValidationException     If the provided chore data is invalid.
     * @throws ConflictException       If there is a conflict during chore creation.
     */
    ChoreDto createChore(ChoreDto chore) throws ValidationException, ConflictException;

    /**
     * Retrieves a list of chores based on the specified search parameters.
     *
     * @param searchParams The search parameters for filtering chores.
     * @return List of chores matching the search criteria.
     */
    List<Chore> getChores(ChoreSearchDto searchParams);

    /**
     * Assigns chores to users based on predefined rules.
     *
     * @return List of ChoreDto objects representing assigned chores.
     */
    List<ChoreDto> assignChores();

    /**
     * Retrieves a list of chores assigned to the authenticated user.
     *
     * @return List of chores assigned to the authenticated user.
     */
    List<Chore> getChoresByUser();

    /**
     * Deletes the specified chores.
     *
     * @param choreIds List of chore IDs to be deleted.
     * @return List of remaining chores after deletion.
     */
    List<Chore> deleteChores(List<Long> choreIds) throws AuthorizationException;

    /**
     * Retrieves a list of all users.
     *
     * @return List of ApplicationUser objects representing users.
     */
    List<ApplicationUser> getUsers();

    /**
     * Updates the points of a user identified by the provided userId.
     *
     * @param userId The ID of the user whose points will be updated.
     * @param points The new points value for the user.
     * @return The updated ApplicationUser object.
     */
    ApplicationUser updatePoints(Long userId, Integer points);

    /**
     * Generates a PDF document containing chore-related information.
     *
     * @return Byte array representing the generated PDF.
     * @throws IOException              If an I/O error occurs during PDF generation.
     */
    byte[] generatePdf() throws IOException;

    /**
     * Repeats a chore identified by choreId with a new date.
     *
     * @param choreId The ID of the chore to be repeated.
     * @param newDate The new date for the repeated chore.
     * @return The repeated ChoreDto.
     */
    ChoreDto repeatChore(Long choreId, Date newDate) throws AuthorizationException, ValidationException, ConflictException;

    /**
     * Retrieves a list of unassigned chores.
     *
     * @return List of unassigned chores.
     */
    List<Chore> getUnassignedChores();

    /**
     * Deletes all user preferences related to chores.
     */
    void deleteAllUserPreference();
}

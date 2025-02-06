package at.ac.tuwien.sepr.groupphase.backend.scheduler;


public interface ExpenseScheduler {

    /**
     * Checks in the database for repeating expenses and creates them if necessary.
     * This method run every day at 00:00
     */
    void createRepeatingExpense();

}

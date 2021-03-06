package systemtests;

import static org.junit.Assert.assertFalse;
import static seedu.giatros.commons.core.Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW;
import static seedu.giatros.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.giatros.testutil.TypicalPatients.BENSON;
import static seedu.giatros.testutil.TypicalPatients.CARL;
import static seedu.giatros.testutil.TypicalPatients.DANIEL;
import static seedu.giatros.testutil.TypicalPatients.KEYWORD_MATCHING_MEIER;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import seedu.giatros.commons.core.index.Index;
import seedu.giatros.logic.commands.DeleteCommand;
import seedu.giatros.logic.commands.FindCommand;
import seedu.giatros.logic.commands.RedoCommand;
import seedu.giatros.logic.commands.UndoCommand;
import seedu.giatros.model.Model;
import seedu.giatros.model.allergy.Allergy;

public class FindCommandSystemTest extends GiatrosBookSystemTest {

    @Test
    public void find() {
        /* Case: find multiple patients in Giatros book, command with leading spaces and trailing spaces
         * -> 2 patients found
         */
        String command = "   " + FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER + "   ";
        Model expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, BENSON, DANIEL); // first names of Benson and Daniel are "Meier"
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: repeat previous find command where patient list is displaying the patients we are finding
         * -> 2 patients found
         */
        command = FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find patient where patient list is not displaying the patient we are finding -> 1 patient found */
        command = FindCommand.COMMAND_WORD + " Carl";
        ModelHelper.setFilteredList(expectedModel, CARL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple patients in Giatros book, 2 keywords -> 2 patients found */
        command = FindCommand.COMMAND_WORD + " Benson Daniel";
        ModelHelper.setFilteredList(expectedModel, BENSON, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple patients in Giatros book, 2 keywords in reversed order -> 2 patients found */
        command = FindCommand.COMMAND_WORD + " Daniel Benson";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple patients in Giatros book, 2 keywords with 1 repeat -> 2 patients found */
        command = FindCommand.COMMAND_WORD + " Daniel Benson Daniel";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple patients in Giatros book, 2 matching keywords and 1 non-matching keyword
         * -> 2 patients found
         */
        command = FindCommand.COMMAND_WORD + " Daniel Benson NonMatchingKeyWord";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: undo previous find command -> rejected */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: redo previous find command -> rejected */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: find same patients in Giatros book after deleting 1 of them -> 1 patient found */
        executeCommand(DeleteCommand.COMMAND_WORD + " 1");
        assertFalse(getModel().getGiatrosBook().getPatientList().contains(BENSON));
        command = FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find patient in Giatros book, keyword is same as name but of different case -> 1 patient found */
        command = FindCommand.COMMAND_WORD + " MeIeR";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find patient in Giatros book, keyword is substring of name -> 0 patients found */
        command = FindCommand.COMMAND_WORD + " Mei";
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find patient in Giatros book, name is substring of keyword -> 0 patients found */
        command = FindCommand.COMMAND_WORD + " Meiers";
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find patient not in Giatros book -> 0 patients found */
        command = FindCommand.COMMAND_WORD + " Mark";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find phone number of patient in Giatros book -> 0 patients found */
        command = FindCommand.COMMAND_WORD + " " + DANIEL.getPhone().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find address of patient in Giatros book -> 0 patients found */
        command = FindCommand.COMMAND_WORD + " " + DANIEL.getAddress().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find email of patient in Giatros book -> 0 patients found */
        command = FindCommand.COMMAND_WORD + " " + DANIEL.getEmail().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find allergies of patient in Giatros book -> 0 patients found */
        List<Allergy> allergies = new ArrayList<>(DANIEL.getAllergies());
        command = FindCommand.COMMAND_WORD + " " + allergies.get(0).allergyName;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find while a patient is selected -> selected card deselected */
        showAllPatients();
        selectPatient(Index.fromOneBased(1));
        assertFalse(getPatientListPanel().getHandleToSelectedCard().getName().equals(DANIEL.getName().fullName));
        command = FindCommand.COMMAND_WORD + " Daniel";
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardDeselected();

        /* Case: find patient in empty Giatros book -> 0 patients found */
        deleteAllPatients();
        command = FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: mixed case command word -> rejected */
        command = "FiNd Meier";
        assertCommandFailure(command, MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Executes {@code command} and verifies that the command box displays an empty string, the result display
     * box displays {@code Messages#MESSAGE_PATIENTS_LISTED_OVERVIEW} with the number of people in the filtered list,
     * and the model related components equal to {@code expectedModel}.
     * These verifications are done by
     * {@code GiatrosBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the status bar remains unchanged, and the command box has the default style class, and the
     * selected card updated accordingly, depending on {@code cardStatus}.
     * @see GiatrosBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Model expectedModel) {
        String expectedResultMessage = String.format(
                MESSAGE_PATIENTS_LISTED_OVERVIEW, expectedModel.getFilteredPatientList().size());

        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchanged();
    }

    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code GiatrosBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
     * @see GiatrosBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}

package seedu.giatros.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import seedu.giatros.commons.util.InvalidationListenerManager;
import seedu.giatros.model.person.Person;
import seedu.giatros.model.person.UniquePersonList;

/**
 * Wraps all data at the Giatros-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class GiatrosBook implements ReadOnlyGiatrosBook {

    private final UniquePersonList persons;
    private final InvalidationListenerManager invalidationListenerManager = new InvalidationListenerManager();

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
    }

    public GiatrosBook() {}

    /**
     * Creates an GiatrosBook using the Persons in the {@code toBeCopied}
     */
    public GiatrosBook(ReadOnlyGiatrosBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
        indicateModified();
    }

    /**
     * Resets the existing data of this {@code GiatrosBook} with {@code newData}.
     */
    public void resetData(ReadOnlyGiatrosBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the Giatros book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the Giatros book.
     * The person must not already exist in the Giatros book.
     */
    public void addPerson(Person p) {
        persons.add(p);
        indicateModified();
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the Giatros book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the Giatros book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        indicateModified();
    }

    /**
     * Removes {@code key} from this {@code GiatrosBook}.
     * {@code key} must exist in the Giatros book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        indicateModified();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListenerManager.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListenerManager.removeListener(listener);
    }

    /**
     * Notifies listeners that the Giatros book has been modified.
     */
    protected void indicateModified() {
        invalidationListenerManager.callListeners(this);
    }

    //// util methods

    @Override
    public String toString() {
        return persons.asUnmodifiableObservableList().size() + " persons";
        // TODO: refine later
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof GiatrosBook // instanceof handles nulls
                && persons.equals(((GiatrosBook) other).persons));
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }
}

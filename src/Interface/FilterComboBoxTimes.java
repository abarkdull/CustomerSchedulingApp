package Interface;

import javafx.collections.ObservableList;

import java.time.LocalTime;

/**
 * Used for filtering combo box times using a lambda
 */
public interface FilterComboBoxTimes {

    /**
     * Empty method for use in lambda expression.
     */
    public ObservableList<LocalTime> getComboBoxTimes(LocalTime beginTime);
}

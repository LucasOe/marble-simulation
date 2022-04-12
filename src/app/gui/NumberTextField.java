package app.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class NumberTextField extends TextField {

	public interface Listener {
		void onNumberChange(double value);
	}

	private double number;
	private List<Listener> listeners = new ArrayList<>();

	public NumberTextField(double defaultValue) {
		super();
		setNumber(defaultValue);
		initHandlers();
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double value) {
		number = value;
		setText(String.valueOf(value));
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	private void notifyListeners(double value) {
		listeners.forEach(listener -> listener.onNumberChange(value));
	}

	private void initHandlers() {
		// Ensure that the new value is a number
		textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				parseInput(oldValue, newValue);
			}
		});

		// Parse number as double when focus is lost
		focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue)
					setNumber(number);
			}
		});
	}

	private void parseInput(String oldValue, String newValue) {
		try {
			// Treat empty values as zero
			number = (!newValue.isEmpty()) ? Double.parseDouble(newValue) : 0;
			notifyListeners(number);
		} catch (NumberFormatException e) {
			setText(oldValue);
		}
	}

}

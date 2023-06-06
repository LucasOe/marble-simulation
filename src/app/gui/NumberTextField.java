package app.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NumberTextField extends TextField {
	private final char[] allowedChars = new char[]{'.', '-'};
	private final List<Listener> listeners = new ArrayList<>();
	private boolean isFocused;
	private double number;
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
		DecimalFormat df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.ENGLISH));
		setText(df.format(value));
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
				if (isFocused)
					parseInput(oldValue, newValue);
			}
		});

		// Parse number as double when focus is lost
		focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				isFocused = newValue;

				if (!isFocused)
					setNumber(number);
			}
		});
	}

	private void parseInput(String oldValue, String newValue) {
		try {
			// Allow input of empty values and allowedChars
			if (newValue.isEmpty() || (newValue.length() == 1 && isAllowedChar(newValue.charAt(0))))
				return;
			number = Double.parseDouble(newValue);
			notifyListeners(number);
		} catch (NumberFormatException e) {
			setText(oldValue);
		}
	}

	// Return true if char is in allowedChars array
	private boolean isAllowedChar(char c) {
		for (char d : allowedChars) {
			if (c == d)
				return true;
		}
		return false;
	}

	public interface Listener {
		void onNumberChange(double value);
	}
}

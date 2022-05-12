package app.gui.panes;

import java.util.ArrayList;
import java.util.List;

import app.Vector;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class AccelerationPane extends VectorPane {

	public interface AccelerationPaneListener {
		void onVectorChange(Vector vector);

		void onButtonClick(String key);
	}

	String key;
	private List<AccelerationPaneListener> listeners = new ArrayList<>();

	public AccelerationPane(Vector defaultValues, String key) {
		super(defaultValues, key);
		this.key = key;
		initialze();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void addListener(AccelerationPaneListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners(String key) {
		listeners.forEach(listener -> listener.onButtonClick(key));
	}

	private void initialze() {
		Button removeButton = new Button("-");
		removeButton.setPrefSize(25, 25);
		BorderPane.setAlignment(removeButton, Pos.BOTTOM_CENTER);
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				notifyListeners(key);
			}
		});
		//setBottom(removeButton);
	}

}

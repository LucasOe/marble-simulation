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

		void onButtonClick(int index);
	}

	int index;
	private List<AccelerationPaneListener> listeners = new ArrayList<>();

	public AccelerationPane(Vector defaultValues, String name, int index) {
		super(defaultValues, name);
		this.index = index;
		initialze();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void addListener(AccelerationPaneListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners(int index) {
		listeners.forEach(listener -> listener.onButtonClick(index));
	}

	private void initialze() {
		Button removeButton = new Button("-");
		removeButton.setPrefSize(25, 25);
		BorderPane.setAlignment(removeButton, Pos.BOTTOM_CENTER);
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				notifyListeners(index);
			}
		});
		setBottom(removeButton);
	}

}

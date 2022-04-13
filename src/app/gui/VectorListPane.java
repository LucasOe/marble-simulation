package app.gui;

import app.Vector;

public class VectorListPane extends VectorPane {

	int index;

	public VectorListPane(Vector defaultValues, int index, String name) {
		super(defaultValues, name);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}

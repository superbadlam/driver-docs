package ru.driverdocs.helpers.ui;

import javafx.util.Callback;

public class Action {
	private int ID;
	private Callback<Void, Void> handler;
	private String name;
	
	public Action(int id, String name, Callback<Void, Void> handler) {
		ID = id;
		this.handler = handler;
		this.name = name;
	}

	public int getID() {
		return ID;
	}
	public Callback<Void, Void> getHandler() {
		return handler;
	}
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	} 
}

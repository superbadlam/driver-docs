package ru.driverdocs.helpers.ui;

public interface RefBookWrapper {

	Long getKeyid();

	Boolean getActuality();

	void setActuality(boolean isActual);

	String getCreateby();

	String getCreatedate();

	String getUpdateby();

	String getUpdatedate();


}
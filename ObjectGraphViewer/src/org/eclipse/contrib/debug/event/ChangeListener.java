package org.eclipse.contrib.debug.event;

public interface ChangeListener<Action extends Enum<Action>, Subject extends Object> {

	public void hasChanged(ChangeEvent<Action, Subject> e);
}

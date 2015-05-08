package org.eclipse.contrib.debug.event;

import java.util.List;

public class ChangeEvent<Action extends Enum<Action>, Subject extends Object> {
	
	private Action action;	
	private List<Subject> subjects = null;

	public ChangeEvent(Action action)
	{
		this.action = action;
	}
    
    public ChangeEvent(Action action, List<Subject> subjects)
    {
    	this(action);
    	this.subjects = subjects;
    }
    
	public Action getAction() {
		return action;
	}
	
	public List<Subject> getSubjects() {
		return subjects;
	}
	
}

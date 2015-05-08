package org.eclipse.contrib.debug.control;

import java.util.List;

import org.eclipse.contrib.debug.model.ObjectGraphViewer;
import org.eclipse.debug.core.model.IVariable;


public class Controller {
	
	static Controller controller = new Controller();

	private ObjectGraphViewer objectGraph;
	
	static public Controller get()
	{
		return controller;
	}

	public void set(ObjectGraphViewer objectGraph)
	{
		this.objectGraph = objectGraph;
	}

	@SuppressWarnings("unchecked")
	public void addGraphInput(@SuppressWarnings("rawtypes") List list) {
		objectGraph.addVariables((List<IVariable>) list);
	}
}

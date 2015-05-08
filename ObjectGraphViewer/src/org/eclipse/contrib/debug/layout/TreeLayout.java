package org.eclipse.contrib.debug.layout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class TreeLayout implements mxIGraphLayout, ChangeListener {

	private mxGraph graph;
	
	private List<mxCell> sources;
	
	public TreeLayout(mxGraph graph, mxCell source)
	{
		this.graph = graph;
		sources = new ArrayList<mxCell>(10);
		source.insert(source);
	}
	
	public mxGraph getGraph(mxCell source)
	{
		return graph;
	}
	
	public void addSource(mxCell source)
	{
		sources.add(source);
	}
	
	public void remove(mxCell source)
	{
		sources.remove(source);
	}
	
	@Override
	public void execute(Object parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveCell(Object cell, double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

}

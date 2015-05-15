package org.eclipse.contrib.debug.layout;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

public class TreeLayout {
	
	private mxGraph graph;
	private double YSPACE = 10;
	private double XSPACE = 10;
	
	private mxCell group;
	private Set<mxCell> cellsAlReadySeen;
	
	public TreeLayout(mxGraph graph)
	{
		this.graph = graph;
	}
	
	public void execute(mxCell parent, mxCell source)
	{
		LinkedList<mxCell> cells = new LinkedList<mxCell>();
		cells.add(source);
		group = parent;
		cellsAlReadySeen = new HashSet<mxCell>();
		place(cells, parent.getGeometry().getX(),  parent.getGeometry().getY());
		graph.refresh();
	}

	protected double maxWidth(LinkedList<mxCell> cells)
	{
		double maxwidth = Double.MIN_VALUE; 
		for (mxCell cell : cells)
		{
			mxGeometry geometry = cell.getGeometry();
			double width = geometry.getWidth();
			if (width > maxwidth)
			{
				maxwidth = width;
			}
		}
	    return maxwidth;
	}
	
	protected double place(LinkedList<mxCell> cells, double x0, double y0)
	{
		double y = y0;
		double maxWidth = maxWidth(cells);
		for (mxCell cell : cells)
		{
			if (!cellsAlReadySeen.contains(cell))
			{
			cellsAlReadySeen.add(cell);
			mxGeometry geometry = cell.getGeometry();
			geometry.setX(x0);
			LinkedList<mxCell> nextVertices = getNextVertices(cell);
			if (nextVertices.isEmpty())
			{
				geometry.setY(y);
				y += geometry.getHeight();
			}
			else
			{
				double ymin = y;
				y = place(nextVertices, x0 + maxWidth + XSPACE, y);
				double ymax = y;
				geometry.setY((ymin + ymax - geometry.getHeight()) / 2);
			}
			y += YSPACE;
			}
		}
		return y;
	}
	
	protected LinkedList<mxCell> getNextVertices(mxCell cell)
	{
		LinkedList<mxCell> nextVertices = new LinkedList<mxCell>();
		for (int i = 0; i < cell.getEdgeCount(); i++)
		{
			mxCell edge = (mxCell) cell.getEdgeAt(i);
			mxCell source = (mxCell) edge.getSource();
			mxCell target = (mxCell) edge.getTarget();
			{
			mxGraphModel model = (mxGraphModel) graph.getModel();
			if (source == cell && model.getNearestCommonAncestor(target, source) == group)
			{
				nextVertices.add(target);
			}
			}
		}
		for (int i = cell.getChildCount() - 1; i >= 0 ; i--)
		{
			mxCell child = (mxCell) cell.getChildAt(i);
			nextVertices.addAll(0, getNextVertices(child));
		}
		return nextVertices;
	}
}

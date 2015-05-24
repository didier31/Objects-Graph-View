package org.eclipse.contrib.debug.layout;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.eclipse.contrib.debug.model.Additional;
import org.eclipse.contrib.debug.model.IterationOnTree;
import org.eclipse.contrib.debug.model.ObjectGraphViewer;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

public class TreeLayout {
	
	private ObjectGraphViewer graph;
	private double YSPACE = 50;
	private double XSPACE = 50;
	
	private mxCell group;
	
	public TreeLayout(ObjectGraphViewer graph)
	{
		this.graph = graph;
	}
	
	public void execute(mxCell group, mxCell source)
	{	    
		mxPoint O = new mxPoint(source.getGeometry().getX(), source.getGeometry().getY());
		LinkedHashSet<mxCell> cells = new LinkedHashSet<mxCell>();
		cells.add(source);
		this.group = group;
		mxPoint M = place(cells, O);
		Object[] groupChildren = graph.getChildCells(group);
		mxRectangle childBounds = graph.getBoundingBoxFromGeometry(groupChildren);
		double dx = - childBounds.getX(),
			   dy = - childBounds.getY();
		//graph.updateGroupBounds(new Object[]{parent});
		graph.moveCells(groupChildren, dx, dy);
		graph.refresh();	
	}
	
	
	protected double maxWidth(LinkedHashSet<mxCell> cells)
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
	
	protected mxPoint place(LinkedHashSet<mxCell> cells, mxPoint O)
	{
		// M is the current end of the tree
		mxPoint M = (mxPoint) O.clone();
		double maxWidth = maxWidth(cells);
		double insertionY = M.getY();
		M.setX(M.getX() + maxWidth);
		for (mxCell cell : cells)
		{
			mxGeometry geometryOfCell = cell.getGeometry();
			geometryOfCell.setX(O.getX());
			LinkedHashSet<mxCell> nextVertices = getNextVertices(cell);
			if (nextVertices.isEmpty())
			{
				geometryOfCell.setY(M.getY());
				M.setY(M.getY() + geometryOfCell.getHeight());
				M.setY(M.getY() + YSPACE);
			}
			else
			{
				double ymin = M.getY();
				M = place(nextVertices, new mxPoint(O.getX() + maxWidth + XSPACE, O.getY()));
				double ymax = M.getY() - YSPACE;
				geometryOfCell.setY((ymin + ymax - geometryOfCell.getHeight()) / 2);
				if (insertionY > geometryOfCell.getY())
				{
					geometryOfCell.setY(insertionY);	
				}
				insertionY = geometryOfCell.getY() + geometryOfCell.getHeight() + YSPACE;
			}
		}
		return M;
	}
	
	protected LinkedHashSet<mxCell> getNextVertices(mxCell cell)
	{
		LinkedHashSet<mxCell> nextVertices = new LinkedHashSet<mxCell>();
		Additional rankOfSource = (Additional) (cell.getParent().getId().startsWith("grp") ?  cell.getValue() 
                                                                                            : cell.getParent().getValue());
		for (int i = 0; i < cell.getEdgeCount(); i++)
		{			
			mxCell edge = (mxCell) cell.getEdgeAt(i);
			mxCell source = (mxCell) edge.getSource();
			mxCell target = (mxCell) edge.getTarget();
			{
			mxGraphModel model = (mxGraphModel) graph.getModel();
			if (source == cell && model.getNearestCommonAncestor(target, source) == group)
			{
				Additional rankOfTarget = (Additional) target.getValue();
				if (rankOfTarget.getRank() == rankOfSource.getRank() + 1)
				{
				nextVertices.add(target);
				}
			}
			}
		}
		for (Object child : graph.getChildVertices(cell))
		{
			nextVertices.addAll(getNextVertices((mxCell) child));
		}
		return nextVertices;
	}
	
	protected void moveTree(mxCell root, double dx, double dy)
	{
		IterationOnTree.Action removeCell = new IterationOnTree.Action() {

		    @Override
		    public void perform(mxCell cell, ObjectGraphViewer objectGraphViewer) 
		    {
			objectGraphViewer.moveCells(new Object[]{cell}, dx, dy);
		    }		
	        };
		
	    IterationOnTree removeSubsequentCells = new IterationOnTree(graph,
		                                                            null, 
				                                                    removeCell,
				                                                    null);
	    removeSubsequentCells.perform(root);	
	}
}

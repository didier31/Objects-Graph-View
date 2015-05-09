package org.eclipse.contrib.debug.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.contrib.debug.control.TypenameModifier;
import org.eclipse.contrib.debug.ui.view.ObjectGraphViewPart;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIValue;
import org.eclipse.swt.graphics.Point;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class ObjectGraphViewer extends mxGraph implements MouseListener, KeyListener, ChangeListener {
	
	private ObjectGraphViewPart viewPart;
	private mxGraphComponent graphComponent;
	private List<ChangeListener> listOfChangeListener = new ArrayList<ChangeListener>(1); 

	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public ObjectGraphViewer(ObjectGraphViewPart objectGraphViewPart) {
		viewPart = objectGraphViewPart;
		setAutoSizeCells(true);
		setCellsMovable(true);
		setAllowLoops(true);
		setCellsEditable(false);
		setCellsDeletable(true);
		setCellsCloneable(false);
		setCellsDisconnectable(false);
		setCellsResizable(false);	
		setDropEnabled(false);
		TypenameModifier.addChangeListener(this);
	}
	
	protected void connectToExisting(mxCell newcell) 
	{	
		Object variable = newcell.getValue();
		
		if (variable instanceof ReferenceVariable)
		{
			ReferenceVariable reference = (ReferenceVariable) variable;
			IVariable var = reference.getVariable();
			try {
				IValue value = var.getValue();
				mxGraphModel model = (mxGraphModel) getModel();
				mxCell existingReferencedVariableCell = (mxCell) model.getCell(Integer.toString(value.hashCode()));
				insertEdge(getDefaultParent(), null, var.getName(), newcell, existingReferencedVariableCell, null);
			} catch (DebugException e) {
				e.printStackTrace();
			}
		}
		else if (variable instanceof ReferencedValue)
		{
			
		}
		
		
//		mxCell rootCell = ((mxCell) getDefaultParent());
//		
//		/**
//		 * Gets all existing top-level vertex
//		 */
//		mxICell rootVertex[] = new mxICell[rootCell.getChildCount()];
//		int vertexCount = 0;
//		for (int i = 0; i < rootCell.getChildCount(); i++)
//		{
//		rootVertex[vertexCount] = rootCell.getChildAt(i);
//		if (rootVertex[vertexCount].isVertex())
//		{
//			vertexCount++;
//		}
//		}
//		
//		IVariable newvar = (((ReferencedValue) newcell.getValue()).getVariable());  
//		
//		/** 
//		 * Connect existing fields to new var 
//		 */
//		for (int i = 0; i < vertexCount; i++)
//		{
//			// Get a variable at top-level
//			mxICell existingCell = rootVertex[i];
//			/**
//			 * For each attribute of this variable 
//			 */
//			for (int j = 0; j < existingCell.getChildCount(); j++)
//			{				
//				mxICell existingField = existingCell.getChildAt(j);
//				
//				if (existingField.isVertex())
//				{				
//				ReferencedValue field  = (ReferencedValue) existingField.getValue();
//				
//				IVariable attribute = field.getVariable();				
//			
//				boolean fieldIsReference;
//				try {
//					fieldIsReference = attribute.getValue().hasVariables();
//				} catch (DebugException e) {
//					fieldIsReference = false;
//					e.printStackTrace();
//				}
//				
//				if (fieldIsReference)
//				{																		
//				try {
//					if (newvar.getValue().equals(attribute.getValue()))
//					{
//						String linkName = "<NoName>";
//						try {
//							ReferencedValue var = (ReferencedValue) existingField.getValue();
//							IVariable ivar = var.getVariable();
//							linkName = ivar.getName();
//						} catch (DebugException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						insertEdge(existingCell, null, linkName, existingField, newcell, null);
//					}
//				} catch (DebugException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}		
//				}
//			}
//			}
//		}
//		
//		/**
//		 *  Connect newcell's fields to existing cells
//		 */		
//		for (int i = 0; i < newcell.getChildCount(); i++)
//		{
//			mxICell newcellField = newcell.getChildAt(i);
//			IVariable attribute = ((ReferencedValue) newcellField.getValue()).getVariable();		
//			
//			boolean fieldIsReference;
//			try {
//				fieldIsReference = attribute.getValue().hasVariables();
//			} catch (DebugException e) {
//				fieldIsReference = false;
//				e.printStackTrace();
//			}
//			
//			if (fieldIsReference)
//			{		
//			for (int j = 0; j < vertexCount; j++)
//			{
//				mxICell existingCell = rootVertex[j];				
//				
//				IVariable var = ((ReferencedValue) existingCell.getValue()).getVariable();
//				
//				try {
//					if (attribute.getValue().equals(var.getValue()))
//					{
//						String linkName = "<NoName>";
//						try {
//							linkName = var.getName();
//						} catch (DebugException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						insertEdge(existingCell, null, linkName, newcellField, existingCell, null);
//					}
//				} catch (DebugException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}				
//			}
//			}
//		}				
	}
	
	static int groupNumber = 0;
	
	public void addVariables(List<IVariable> list)
    {	
		mxCell parent = (mxCell) getDefaultParent();		
		try {
			getModel().beginUpdate();		
			
			for (IVariable var : list)
			{ 
				mxCell cell = CellBuilder.make(var, parent, this);
				connectToExisting(cell);
			}						
		}
		finally
		{
			getModel().endUpdate();
		}
						
		/* mxHierarchicalLayout layout = new mxHierarchicalLayout(this, SwingConstants.WEST);
         layout.execute(parent); */			
		
    }

	public ObjectGraphViewPart getMyViewPart() {
		return viewPart;
	}

	public void setMyViewPart(ObjectGraphViewPart viewPart) {
		this.viewPart = viewPart;
	}

	public void setGraphComponent(mxGraphComponent graphComponent) {
		// TODO Auto-generated method stub
		this.graphComponent = graphComponent;	
		
		getGraphComponent().getGraphControl().addMouseListener(ObjectGraphViewer.this);
		getGraphComponent().getGraphControl().addKeyListener(ObjectGraphViewer.this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)
		{
		mxCell cell = (mxCell) getGraphComponent().getCellAt(e.getX(), e.getY());
		if (cell.isVertex())
		{
		Variable var = (Variable) cell.getValue();	
		
	    if (var instanceof ReferenceVariable)
	    {	    
	    	try {
	    		getModel().beginUpdate();	
	    		mxCell refVarCell = CellBuilder.make(var.getVariable().getValue(), getDefaultParent(), this);
	    		CellBuilder.connectWithExisting(cell, this);
			} catch (DebugException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	finally
			{
				getModel().endUpdate();
			}
	    }
		} 
		} 
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof TypenameModifier)
		{
			refresh();
		}		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {		
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
		{
		}}
	
	public void removeCell()
	{
		
		    Object cellsToRemove[] = getSelectionCells();
		
			List<mxICell> removedCells = new LinkedList<mxICell>();
			for (Object cell : cellsToRemove)
			{
				removedCells.add((mxICell) cell);
			}
			
			// accumulator of cells to examine their childs in computation of connected cells to cells to remove 
			Set<mxICell> cellsToExplore = new TreeSet<mxICell>();
			cellsToExplore.addAll(removedCells);
			
			// Cells connected to the selections (i.e candidates to be removed too)
			Set<mxICell> connectedCells = new TreeSet<mxICell>();
			/**
			 * Makes the connected cells graph
			 */
			while (!cellsToExplore.isEmpty())
			{
				for (Iterator<mxICell> it = cellsToExplore.iterator(); it.hasNext();)
				{
					mxICell current = it.next();
					connectedCells.add(current);
					mxCell edges[] = (mxCell[]) getEdges(current, null, false, true, true, true); 
					for (mxCell edge : edges)						
					{
						mxICell t2 = (mxICell) edge.getTarget();
						if (!connectedCells.contains(t2))
						{
							cellsToExplore.add(t2);
						}
					}
				}
			}			
			
			/**
			 * Among the connected cells, 
			 * hold the ones that are target from outside.
			 */
			
			for (Iterator<mxICell> connectedCellsIt = connectedCells.iterator(); connectedCellsIt.hasNext();)
			{
				mxICell connectedCell = connectedCellsIt.next();
				mxCell incomingEdges[] = (mxCell[]) getEdges(connectedCell, null, true, false, true, true);
				for (mxCell incomingEdge : incomingEdges)
				{
					mxCell sourceCell = (mxCell) incomingEdge.getSource().getParent();
					if (!connectedCells.contains(sourceCell))
					{
						connectedCells.remove(incomingEdge.getTarget());
					}
				}
			}
			
		}		

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}


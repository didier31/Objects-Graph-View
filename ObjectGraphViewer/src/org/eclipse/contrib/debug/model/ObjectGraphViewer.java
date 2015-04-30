package org.eclipse.contrib.debug.model;

import java.util.List;

import org.eclipse.contrib.debug.ui.view.ObjectGraphViewPart;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class ObjectGraphViewer extends mxGraph implements MouseListener {
	
	private ObjectGraphViewPart viewPart;
	private mxGraphComponent graphComponent;

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
		
	}
	
	protected boolean notIn(IVariable argVar)
	{
		mxCell root = (mxCell) getDefaultParent();
		boolean notFound = true;
		IValue argValue;
		try {
			argValue = argVar.getValue();
			
		for (int i = 0; i < root.getChildCount() && notFound; i++)
		{
			mxCell vertex = (mxCell) root.getChildAt(i);
			if (vertex.isVertex())
			{
				Variable var = (Variable) vertex.getValue();
				IValue value = var.getVariable().getValue();
				notFound = !argValue.equals(value);
			}
		}
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notFound;
	}
	
	
	protected void addInput(IVariable argVar, Object parent, boolean isNamed)
	{
		if (notIn(argVar))
		{
		Variable variable = (isNamed) ? new NamedVariable(argVar) : new Variable(argVar);
		String style = mxConstants.STYLE_AUTOSIZE + ";" 
		             + mxConstants.STYLE_MOVABLE + ";" 
				     + mxConstants.STYLE_SPACING_LEFT + "=10.0";
		mxCell cell = (mxCell) createVertex(parent, String.valueOf(variable.hashCode()), variable,
				                   -1, -1, 0, 0, style, false);
		
		IValue varValue = null;;
		IVariable fields[] = null;
		try {
			varValue = argVar.getValue();
			fields = varValue.getVariables();
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
	   addCell(cell, parent);	 
	   
	   mxCell cellChild[] = new mxCell[fields.length];
	   int i = 0;
	   double width = 0;
	   
	   for (IVariable field : fields)
	   {
			cellChild[i] = (mxCell) createVertex(cell, String.valueOf(field.hashCode()), new NamedVariable(field),
	                                                 -1, -1, 0, 0, mxConstants.STYLE_AUTOSIZE, false);
			cell.insert(cellChild[i]);
			cellSizeUpdated(cellChild[i], false);
			width = Math.max(width, cellChild[i].getGeometry().getWidth());			
			i++;
	   }	   
	   
	   connectToExisting(cell);
	   
	   cellSizeUpdated(cell, false);
	   
	   width = Math.max(width, cell.getGeometry().getWidth());
	   
	   for (mxCell childCell : cellChild)
	   {
		   childCell.getGeometry().setWidth(width);
	   }
	   
	   cell.getGeometry().setWidth(width);	   	   
		
	   mxStackLayout layout = new mxStackLayout(this, false);
	   layout.execute(cell);
	   
	   setCellStyles(mxConstants.STYLE_MOVABLE,"0",cellChild);		
		}
	}
	
	protected void connectToExisting(mxCell newcell) 
	{	
		mxCell rootCell = ((mxCell) getDefaultParent());
		
		/**
		 * Gets all existing top-level vertex
		 */
		mxICell rootVertex[] = new mxICell[rootCell.getChildCount()];
		int vertexCount = 0;
		for (int i = 0; i < rootCell.getChildCount(); i++)
		{
		rootVertex[vertexCount] = rootCell.getChildAt(i);
		if (rootVertex[vertexCount].isVertex())
		{
			vertexCount++;
		}
		}
		
		IVariable newvar = (((Variable) newcell.getValue()).getVariable());  
		
		/** 
		 * Connect existing fields to new var 
		 */
		for (int i = 0; i < vertexCount; i++)
		{
			// Get a variable at top-level
			mxICell existingCell = rootVertex[i];
			/**
			 * For each attribute of this variable 
			 */
			for (int j = 0; j < existingCell.getChildCount(); j++)
			{				
				mxICell existingField = existingCell.getChildAt(j);
				
				if (existingField.isVertex())
				{				
				Variable field  = (Variable) existingField.getValue();
				
				IVariable attribute = field.getVariable();				
			
				boolean fieldIsReference;
				try {
					fieldIsReference = attribute.getValue().hasVariables();
				} catch (DebugException e) {
					fieldIsReference = false;
					e.printStackTrace();
				}
				
				if (fieldIsReference)
				{																		
				try {
					if (newvar.getValue().equals(attribute.getValue()))
					{
						String linkName = "<NoName>";
						try {
							Variable var = (Variable) existingField.getValue();
							IVariable ivar = var.getVariable();
							linkName = ivar.getName();
						} catch (DebugException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						insertEdge(existingCell, null, linkName, existingField, newcell, null);
					}
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				}
			}
			}
		}
		
		/**
		 *  Connect newcell's fields to existing cells
		 */		
		for (int i = 0; i < newcell.getChildCount(); i++)
		{
			mxICell newcellField = newcell.getChildAt(i);
			IVariable attribute = ((Variable) newcellField.getValue()).getVariable();		
			
			boolean fieldIsReference;
			try {
				fieldIsReference = attribute.getValue().hasVariables();
			} catch (DebugException e) {
				fieldIsReference = false;
				e.printStackTrace();
			}
			
			if (fieldIsReference)
			{		
			for (int j = 0; j < vertexCount; j++)
			{
				mxICell existingCell = rootVertex[j];				
				
				IVariable var = ((Variable) existingCell.getValue()).getVariable();
				
				try {
					if (attribute.getValue().equals(var.getValue()))
					{
						String linkName = "<NoName>";
						try {
							linkName = var.getName();
						} catch (DebugException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						insertEdge(existingCell, null, linkName, newcellField, existingCell, null);
					}
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			}
		}				
	}
	
	public void addInput(List<IVariable> list)
    {
		Object parent = getDefaultParent();
		try {
			getModel().beginUpdate();
			
			for (IVariable var : list)
			{ 
				addInput(var, parent, true);
			}
			
		}
		finally
		{
			getModel().endUpdate();
		}
			
			
		     mxStackLayout layout = new mxStackLayout(this, false) {
		    	 @Override
		    	 public mxRectangle getContainerSize()
		    	 {
		    		 Point size = getMyViewPart().getControl().getSize();
		    		 return new mxRectangle(0.0, 0.0, size.x, size.y);
		    	 }
		     };
			layout.execute(parent);
			repaint();
			
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
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)
		{
		mxCell cell = (mxCell) getGraphComponent().getCellAt(e.getX(), e.getY());
		Variable var = (Variable) cell.getValue();
		IVariable ivar = var.getVariable();
		boolean varIsReference = false;
		try {
			varIsReference = ivar.getValue().hasVariables();
		} catch (DebugException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    if (cell.isVertex() && cell.getParent() != getModel().getRoot() && varIsReference)
	    {	    
	    	addInput(ivar, getDefaultParent(), false);
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
}


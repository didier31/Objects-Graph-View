package org.eclipse.contrib.debug.model;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.contrib.debug.control.TypenameModifier;
import org.eclipse.contrib.debug.layout.TreeLayout;
import org.eclipse.contrib.debug.ui.view.ObjectGraphViewPart;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class ObjectGraphViewer extends mxGraph implements MouseListener, KeyListener, ChangeListener {
	
	private ObjectGraphViewPart viewPart;
	private mxGraphComponent graphComponent;
	private Hashtable<Object, mxCell> varSources = new Hashtable<Object, mxCell>(6);

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
		setCellsResizable(true);	
		setDropEnabled(false);
		setExtendParents(true);
		setExtendParentsOnAdd(true);
		setPortsEnabled(true);
		TypenameModifier.addChangeListener(this);		
	}
	
	static int groupNumber = 0;
	
	public void addVariables(List<IVariable> list)
    {			
		try {
			getModel().beginUpdate();		
			
			for (IVariable var : list)
			{ 
				mxCell group = (mxCell) insertVertex(getDefaultParent(), "grp" + Integer.toString(groupNumber), 
						                    null, -10, -10, 800, 200);
				enterGroup(group);
				mxCell cell = CellManager.make(var, null, this);
				((Additional) cell.getValue()).setRank(0);
				varSources.put(group, cell);
				groupNumber++;
				exitGroup();
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
		getGraphComponent().getGraphHandler().setRemoveCellsFromParent(false);
		
		getGraphComponent().getGraphControl().addMouseListener(ObjectGraphViewer.this);
		getGraphComponent().getGraphControl().addKeyListener(ObjectGraphViewer.this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)
		{
		mxCell cell = (mxCell) getGraphComponent().getCellAt(e.getX(), e.getY());
		if (cell.getValue() instanceof ReferenceVariable)
		{
		ReferenceVariable var = (ReferenceVariable) cell.getValue();	

		    // Init in case of the reference is the root of the graph 
    		mxCell group = (mxCell) cell.getParent();
    		mxCell sourceForLayout;   
    		// Init of group in case the reference variable is a field of a class    		
    		if (!group.getId().startsWith("grp"))
    		{
    			sourceForLayout = group;
    			group = (mxCell) group.getParent();
    		}
    		else
    		{
    			sourceForLayout = cell;
    		}
	    	try {
	    		getModel().beginUpdate();
	    		IValue ivalue = var.getVariable().getValue();
	    		mxCell referencedCell = CellManager.existingVertex(ivalue, this);
	    		if (getEdges(cell, null, false, true, false).length == 0)
	    		{	    		 
	    		if (referencedCell == null)
	    		{
	    			referencedCell = CellManager.make(ivalue, cell, group, this);
	    		}
	    		else
	    		{
	    			CellManager.connectWithExisting(cell, this);
	    		}
    		    TreeLayout layout = new TreeLayout(this);

    		    mxCell source = varSources.get(group);
    		    layout.execute(group, source);
	    		}
	    		else
	    		{
	    			// If the referenced Cell is in the same group as the source
	    			// then it can be deleted with its subsequents cells.
	    			if (referencedCell.getParent() == group)
	    			{
	    				CellManager.removeSubsequentCells(referencedCell, this);
	    			}
	    		}
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
	
	public void removeVariable()
	{
		
		    Object cellsToRemove[] = getSelectionCells();
		
			for (Object cell : cellsToRemove)
			{
				// Is the source var ?
				if (varSources.contains(cell))
				{
					Object parent = ((mxCell) cell).getParent();
					varSources.remove(parent);
					removeCells(new Object[]{parent});
				}
				// Is it the group ?
				else if (varSources.containsKey(cell))
				{
					varSources.remove(cell);
					removeCells(new Object[]{cell});
				}
			}
		}		

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}


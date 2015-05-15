package org.eclipse.contrib.debug.model;

import java.util.Hashtable;
import java.util.List;
import org.eclipse.contrib.debug.control.TypenameModifier;
import org.eclipse.contrib.debug.layout.TreeLayout;
import org.eclipse.contrib.debug.ui.view.ObjectGraphViewPart;
import org.eclipse.debug.core.DebugException;
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
		setCellsResizable(false);	
		setDropEnabled(false);
		TypenameModifier.addChangeListener(this);
	}
	
	static int groupNumber = 0;
	
	public void addVariables(List<IVariable> list)
    {			
		try {
			getModel().beginUpdate();		
			
			for (IVariable var : list)
			{ 
				Object group = insertVertex(getDefaultParent(), "grp" + Integer.toString(groupNumber), 
						                    null, -10, -10, 800, 200);
				enterGroup(group);
				mxCell cell = CellBuilder.make(var, null, this);
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
    		mxCell parent = (mxCell) cell.getParent();
    		if (!parent.getId().startsWith("grp"))
    		{
    			parent = (mxCell) parent.getParent();
    		}
	    	try {
	    		getModel().beginUpdate();	
	    		CellBuilder.make(var.getVariable().getValue(), parent, this);
	    		CellBuilder.connectWithExisting(cell, this);
	    		TreeLayout layout = new TreeLayout(this);
	    		mxCell source = varSources.get(parent);
	    		layout.execute(parent, source);	    		
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
	
	public void removeVariable()
	{
		
		    Object cellsToRemove[] = getSelectionCells();
		
			for (Object cell : cellsToRemove)
			{
				if (varSources.contains(cell))
				{
					Object parent = ((mxCell) cell).getParent();
					varSources.remove(parent);
					removeCells(new Object[]{parent});
				}
			}
		}		

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}


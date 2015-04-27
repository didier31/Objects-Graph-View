package org.eclipse.contrib.debug.model;

import java.util.List;

import org.eclipse.contrib.debug.ui.view.ObjectGraphViewPart;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.swt.graphics.Point;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class ObjectGraphViewer extends mxGraph {
	
	private ObjectGraphViewPart viewPart;

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
	
	protected void addInput(IVariable argVar, Object parent)
	{
		
		Variable variable = new Variable(argVar);
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
	   
	   for (IVariable field : fields)
	   {
			cellChild[i] = (mxCell) createVertex(cell, String.valueOf(field.hashCode()), new Variable(field),
	                                                 -1, -1, 0, 0, mxConstants.STYLE_AUTOSIZE, false);
			cell.insert(cellChild[i]);
			cellSizeUpdated(cellChild[i], false);			
			i++;
	   }
	   
	   cellSizeUpdated(cell, false);	  
		
	   mxStackLayout layout = new mxStackLayout(this, false);
	   layout.execute(cell);
	   
	   setCellStyles(mxConstants.STYLE_MOVABLE,"0",cellChild);			
	}
	
	public void addInput(List<IVariable> list)
    {
		Object parent = getDefaultParent();
		try {
			getModel().beginUpdate();
			
			for (IVariable var : list)
			{ 
				addInput(var, parent);
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
}


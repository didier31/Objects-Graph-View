package org.eclipse.contrib.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

public class CellBuilder {
	
static public mxCell make(IVariable var, Object parent, ObjectGraphViewer graph)
{
	mxCell cell = existingVertex(var, graph); 

	if (cell != null)
	{
		return cell;
	}
	
	Variable variable = Variable.makeVariable(var);
	String style = mxConstants.STYLE_AUTOSIZE + ";" 
	             + mxConstants.STYLE_MOVABLE + ";" 
			     + mxConstants.STYLE_SPACING_LEFT + "=10.0";
	
	cell = (mxCell) graph.insertVertex(parent, String.valueOf(var.hashCode()), variable,
			                                  -1, -1, 0, 0, style, false);
	
	return cell;
}

static protected mxCell existingVertex(Object o, ObjectGraphViewer graph)
{
	mxGraphModel model = (mxGraphModel) graph.getModel();
    return (mxCell) model.getCell(Integer.toString(o.hashCode()));
}

static protected void connectWithExisting(mxCell refVarCell, ObjectGraphViewer graph)
{
	mxGraphModel model = (mxGraphModel) graph.getModel();
	Object cellValue = refVarCell.getValue();
	if (cellValue instanceof ReferenceVariable)
	{
	Variable refVar = (ReferenceVariable) cellValue;
	IVariable refIvar = refVar.getVariable();
	IValue referencedValue = null;
	try {
		referencedValue = refIvar.getValue();
	} catch (DebugException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	int referencedId = referencedValue.hashCode();
	mxCell referencedCell = (mxCell) model.getCell(Integer.toString(referencedId));
	String name;
	try {
		name = refIvar.getName();
	} catch (DebugException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		name = "<No name>";
	}
	graph.insertEdge(graph.getDefaultParent(), null, name, refVarCell, referencedCell, null);
	}
}

static public mxCell make(IValue value, Object parent, ObjectGraphViewer graph)
{
	mxCell cell = existingVertex(value, graph); 

	if (cell != null)
	{
		return cell;
	}
	
	ReferencedValue record = new ReferencedValue(value);
	String style = mxConstants.STYLE_AUTOSIZE + ";" 
	             + mxConstants.STYLE_MOVABLE + ";" 
			     + mxConstants.STYLE_SPACING_LEFT + "=10.0";
	

    cell = (mxCell) graph.insertVertex(parent, String.valueOf(value.hashCode()), record,
			                                  -1, -1, 0, 0, style, false);
	IVariable fields[] = null;
	try {
		fields = value.getVariables();
	} catch (DebugException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}		
			
   graph.addCell(cell, parent);	 
   
   mxCell childCells[] = new mxCell[fields.length];
   int i = 0;
   double width = 0;
   
   for (IVariable field : fields)
   {
		childCells[i] = (mxCell) graph.createVertex(cell, String.valueOf(field.hashCode()), Variable.makeVariable(field),
                                                 -1, -1, 0, 0, mxConstants.STYLE_AUTOSIZE, false);
		cell.insert(childCells[i]);
	    /**
	     * Connect cell with existing ones.
	     */
	    connectWithExisting(childCells[i], graph);
		graph.cellSizeUpdated(childCells[i], false);
		width = Math.max(width, childCells[i].getGeometry().getWidth());			
		i++;
   }	      
   
   graph.cellSizeUpdated(cell, false);
   
   width = Math.max(width, cell.getGeometry().getWidth());
   
   for (mxCell childCell : childCells)
   {
	   childCell.getGeometry().setWidth(width);
   }
   
   cell.getGeometry().setWidth(width);	   	   
	
   mxStackLayout layout = new mxStackLayout(graph, false);
  
   layout.execute(cell);
   
   graph.setCellStyles(mxConstants.STYLE_MOVABLE,"0",childCells);	
   
   return cell;
}


}

package org.eclipse.contrib.debug.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaVariable;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;

public class CellManager {
	
static public mxCell make(IVariable var, Object parent, ObjectGraphViewer graph)
{
	mxCell cell = existingVertex(var, graph); 

	if (cell != null)
	{
		return cell;
	}
	
	Variable variable = Variable.makeVariable(var);
	String style = mxConstants.STYLE_AUTOSIZE + "=1;" 
	             + mxConstants.STYLE_MOVABLE + "=1;" 
	             + mxConstants.STYLE_RESIZABLE + "=0;"
			     + mxConstants.STYLE_SPACING_LEFT + "=10.0";
	
	cell = (mxCell) graph.insertVertex(parent, String.valueOf(var.hashCode()), variable,
			                                  -1, -1, 0, 0, style, false);
	
	graph.updateCellSize(cell, false);
	return cell;
}

static public mxCell existingVertex(Object o, ObjectGraphViewer graph)
{
	mxGraphModel model = (mxGraphModel) graph.getModel();
    return (mxCell) model.getCell(Integer.toString(o.hashCode()));
}

static public void connectWithExisting(mxCell refVarCell, ObjectGraphViewer graph)
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
	if (referencedCell != null)
	{
	String name;
	try {
		name = refIvar.getName();
	} catch (DebugException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		name = "<No name>";
	}
	final String edgeStyle = "edgeStyle=orthogonalEdgeStyle"; 
	graph.insertEdge(refVarCell, null, name, refVarCell, referencedCell, edgeStyle);
	}
	}
}

static public mxCell make(IValue value, Object parent, ObjectGraphViewer graph)
{	
	ReferencedValue record = new ReferencedValue(value);
	String style = mxConstants.STYLE_AUTOSIZE + ";" 
	             + mxConstants.STYLE_MOVABLE + ";"
	             + mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_SWIMLANE + ";"
	             + mxConstants.STYLE_RESIZABLE + "=0;"
			     + mxConstants.STYLE_SPACING_LEFT + "=10.0";
	

    mxCell cell = (mxCell) graph.insertVertex(parent, String.valueOf(value.hashCode()), record,
			                                  0, 0, 0, 0, style, false);
	IVariable fields[] = null;
	try {
		fields = value.getVariables();
	} catch (DebugException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}		
			
   graph.addCell(cell, parent);	 
   graph.updateCellSize(cell);
   
   mxCell childCells[] = new mxCell[fields.length];
   int i = 0;
   double width = 0;
   for (IVariable field : fields)
   {
	   IJavaVariable javaField = (IJavaVariable) field;
	    try {
	    	boolean isVisibleField = !javaField.isSynthetic();
	    	isVisibleField &= field.getValue().hasVariables() && !(javaField.isFinal() && javaField.isStatic());
	    	isVisibleField |= !field.getValue().hasVariables() && !javaField.isFinal();
			if (isVisibleField)
			{
			childCells[i] = (mxCell) graph.createVertex(cell, String.valueOf(field.hashCode()), Variable.makeVariable(field),
			                                         0, 0, 0, 0, mxConstants.STYLE_RESIZABLE + "=0" 
			                                        		   +";"+ mxConstants.STYLE_AUTOSIZE
			                                        		   +";" + "portConstraint=west"
			                                        		   + ";" + "portConstraint=east"
			                                                   , false);			
			cell.insert(childCells[i]);
			/**
			 * Connect cell with existing ones.
			 */
			connectWithExisting(childCells[i], graph);
			graph.updateCellSize(childCells[i], false);
			width = Math.max(width, childCells[i].getGeometry().getWidth());			
			i++;
			}
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }	      
      
   childCells = Arrays.copyOf(childCells, i);
   
   width = Math.max(width, cell.getGeometry().getWidth());
   
   for (mxCell childCell : childCells)
   {
	   childCell.getGeometry().setWidth(width);
   }
   
   cell.getGeometry().setWidth(width);	   	   
	
   mxStackLayout layout = new mxStackLayout(graph, false);
   layout.execute(cell);
   graph.updateGroupBounds(new Object[]{cell}, 0, true);
   graph.setCellStyles(mxConstants.STYLE_MOVABLE,"0",childCells);	
   return cell;
}

public static void removeSubsequentCells(Object sourceCellToRemove,
		ObjectGraphViewer objectGraphViewer) {
	    removeSubsequentCellsR(sourceCellToRemove, objectGraphViewer);
}

public static void removeSubsequentCellsR(Object sourceCellToRemove,
		ObjectGraphViewer objectGraphViewer) {

mxCell sourceCellToDelete = (mxCell) sourceCellToRemove;
LinkedList<mxCell> targetsToRemove = new LinkedList<mxCell>();
Object edges[] = objectGraphViewer.getAllEdges(new Object[]{sourceCellToRemove});
for (Object edgeObject : edges)
{
	mxCell edge = (mxCell) edgeObject;
	mxCell target = (mxCell) edge.getTerminal(false);
	if (target != sourceCellToDelete && target.getParent() == sourceCellToDelete.getParent())
	{
		targetsToRemove.add(target);
	}
}
objectGraphViewer.removeCells(new Object[]{sourceCellToDelete}, true);
for (mxICell targetToRemove : targetsToRemove)
{
	removeSubsequentCellsR(targetToRemove, objectGraphViewer);
}
}
}

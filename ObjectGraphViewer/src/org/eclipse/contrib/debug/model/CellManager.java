package org.eclipse.contrib.debug.model;

import java.util.Arrays;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaVariable;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
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

static public mxCell make(IValue value, mxCell previous, Object parent, ObjectGraphViewer graph)
{	
	ReferencedValue record = new ReferencedValue(value);
	record.setPrevious(previous);
	
	String style = mxConstants.STYLE_AUTOSIZE + ";" 
	             + mxConstants.STYLE_MOVABLE + ";"
	             + mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_SWIMLANE + ";"
	             + mxConstants.STYLE_RESIZABLE + "=0;"
	             +";" + "portConstraint=west"
			     + mxConstants.STYLE_SPACING_LEFT + "=10.0";
	

    mxCell cell = (mxCell) graph.insertVertex(parent, String.valueOf(value.hashCode()), record,
			                                  0, 0, 0, 0, style, false);
    CellManager.connectWithExisting(previous, graph);
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
	Additional rankOfReferencedValue = (Additional) referencedCell.getValue(),
	     rankOfReference = refVarCell.getParent().getId().startsWith("grp") ? (Additional) refVarCell.getValue() 
	    		                                                            : (Additional) refVarCell.getParent().getValue();
	rankOfReferencedValue.setRank(rankOfReference.getRank() + 1);
	}
	}
}

public static void inverseVisibilityOfSubsequentCells(Object sourceCell,
		                                 ObjectGraphViewer objectGraphViewer) {
	
	boolean newVisibility = !objectGraphViewer.getModel().isVisible(sourceCell);	 
	
	IterationOnTree.Action inverseVisibility = new IterationOnTree.Action() {

	    @Override
	    public void perform(mxCell cell, ObjectGraphViewer objectGraphViewer) 
	    {
		objectGraphViewer.getModel().setVisible(cell, newVisibility);
	    Object incomingEdges[] = objectGraphViewer.getIncomingEdges(sourceCell);
	    for (Object edge : incomingEdges)
	    {
	    	objectGraphViewer.getModel().setVisible(edge, newVisibility);	
	    }
	    }		
        };
	
    IterationOnTree inverseVisibilityOfCells = new IterationOnTree(objectGraphViewer,
	                                                               null, 
	                                                               inverseVisibility,
			                                                       null);
    inverseVisibilityOfCells.perform(sourceCell);
}

}

package org.eclipse.contrib.debug.model;

import com.mxgraph.model.mxCell;

public class IterationOnTree
{

	private ObjectGraphViewer objectGraphViewer;
	private Action actionBeforeSubsequents;
	private Action actionAfterSubsequents;
	private StopCondition stopCondition;
	
public IterationOnTree(ObjectGraphViewer objectGraphViewer,
		Action actionBeforeSubsequents, 
        Action actionAfterSubsequents,
        StopCondition stopCondition)
        {
	this.objectGraphViewer = objectGraphViewer;
	if (actionBeforeSubsequents != null)
	{
	this.actionBeforeSubsequents = actionBeforeSubsequents;
	}
	else
	{
		this.actionBeforeSubsequents = Action.noAction;
	}
	if (actionAfterSubsequents != null)
	{
	this.actionAfterSubsequents = actionAfterSubsequents;
	}
	else
	{
		this.actionAfterSubsequents = Action.noAction;
	}
	if (stopCondition != null)
	{
	this.stopCondition = stopCondition;
	}
	else
	{
		this.stopCondition = StopCondition.noConditionalStop;
	}
}
	
public interface Action
{
void perform(mxCell cell, ObjectGraphViewer objectGraphViewer);

final Action noAction = new Action() { 

	@Override
	public void perform(mxCell cell, ObjectGraphViewer objectGraphViewer) {
	}
};
}

public interface StopCondition
{
public boolean stopIf(mxCell cell);

final StopCondition noConditionalStop = new StopCondition() { 

	@Override
	public boolean stopIf(mxCell cell) {
		return false;
	}	
};

}	

public boolean perform(Object root) {

mxCell rootCell = (mxCell) root;
if (stopCondition.stopIf(rootCell))
	{
		return true;
	}
else
{
Additional rootRank = (Additional) rootCell.getValue();
//Action before
if (actionBeforeSubsequents != null)
{
	actionBeforeSubsequents.perform(rootCell, objectGraphViewer);
}

Object edges[] = objectGraphViewer.getAllEdges(new Object[]{root});
boolean stopConditionNOK = true;
for (int i = 0; i < edges.length && stopConditionNOK; i++)
{
	mxCell edge = (mxCell) edges[i];
	mxCell target = (mxCell) edge.getTerminal(false);
	Additional targetRank = (Additional) target.getValue();
	if (target.getParent() == rootCell.getParent()
		&&
		targetRank.getRank() > rootRank.getRank())
	{
		stopConditionNOK = perform(target);
	}
}
//Action after
if (actionAfterSubsequents != null)
{
actionAfterSubsequents.perform(rootCell, objectGraphViewer);
}
return stopConditionNOK;
}
}
}

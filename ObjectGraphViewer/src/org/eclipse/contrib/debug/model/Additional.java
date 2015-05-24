package org.eclipse.contrib.debug.model;

import com.mxgraph.model.mxCell;

public class Additional {

private int rank;

private mxCell previous = null;
	
	public mxCell getPrevious() {
	return previous;
}

public void setPrevious(mxCell previous) {
	this.previous = previous;
}

	public Additional()
	{
		rank = Integer.MAX_VALUE;
	}
	
	public void setRank(int rank)
	{
		if (rank < this.rank)
		{
			this.rank = rank;			
		}
	}
	
	public int getRank()
	{
		return rank;
	}
}

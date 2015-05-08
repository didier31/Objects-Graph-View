package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.debug.core.model.IVariable;

public class ReferenceVariable extends Variable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5945188852908478301L;

	public ReferenceVariable()
	{	
	}
	
	public ReferenceVariable(IVariable var) {
		super(var);
	}

	
	   private void writeObject(java.io.ObjectOutputStream out)
			     throws IOException
	   {
	   doWriteObject(out);
	   }
			
	 private void readObject(java.io.ObjectInputStream in)
			     throws IOException, ClassNotFoundException
	   {
	   doReadObject(in);
	   }	
}

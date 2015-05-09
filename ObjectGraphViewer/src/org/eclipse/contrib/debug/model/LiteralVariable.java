package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class LiteralVariable extends Variable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3813191475036857622L;
	private String cachedValue;

	public LiteralVariable(IVariable var) {
		super(var);
		// TODO Auto-generated constructor stub
	}

	public LiteralVariable()
	{
		super();
		cachedValue = "";
	}
	
	public String toString()
	{
		String value = null;
		try {
			value = getVariable() != null ? getVariable().getValue().getValueString() : cachedValue;
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			value = "<No Value>";
		}
		return super.toString() +  " := " + value;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException
		     {
		     doWriteObject(out);
		     try {
				out.writeObject(getVariable().getValue().getValueString());
			} catch (DebugException e) 
		     {
				out.writeObject("<No type>");
		     }
		    }
		
    private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
		     {
    	     doReadObject(in);
	         cachedValue = (String) in.readObject();
		     }	
}

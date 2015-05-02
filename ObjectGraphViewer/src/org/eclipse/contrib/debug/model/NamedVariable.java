package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class NamedVariable extends Variable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5557076139292833883L;
	private String cachedName;

	public NamedVariable(IVariable var) {
		super(var);
		// TODO Auto-generated constructor stub
	}		
	
	/**
	 * Gives cell's content when dragged.
	 */
	@Override
	public String toString()
	{
		String string = "<NoName>";
		try
		{
			string = getVariable() != null ? getVariable().getName() : cachedName;			
		    string += " : "; 
			string += super.toString();
				
			
			return string;
		}
		catch (DebugException e)
		{
			return string;
		}
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException
		     {
		     try {
				out.writeObject(getVariable().getName());
			} catch (DebugException e) {}
		    }
		
   protected void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
		     {
			 cachedName = (String) in.readObject();
		     }

}

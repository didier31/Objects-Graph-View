package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class NamedVariable extends Variable implements Serializable {

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
			string = variable != null ? variable.getName() : cachedName;			
		    string += " : "; 
			string += super.toString();
				
			
			return string;
		}
		catch (DebugException e)
		{
			return string;
		}
	}
	
	@Override
	protected void writeObject(java.io.ObjectOutputStream out)
		     throws IOException
		     {
		     try {
				out.writeObject(variable.getName());
				super.writeObject(out);
			} catch (DebugException e) {}
		    }
	
   @Override	
   protected void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
		     {
			 cachedName = (String) in.readObject();
			 readObject(in);
		     }

}

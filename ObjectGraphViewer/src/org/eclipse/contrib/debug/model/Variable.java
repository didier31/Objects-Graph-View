

package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author didier
 * 
 * a serializable Wrapper of IVariable's contrete descendants
 * 
 * This has been made necessary to avoid NotSerialisable exception thrown on
 * awt drag 'n drop of a graph cell. 
 */
public class Variable implements Serializable {

	private static final long serialVersionUID = -7665943218834959525L;
	
	protected IVariable variable;
	
	/**
	 * keep track of variable's name when a graph cell is dragged.
	 */
	protected String cachedName,
	                 cachedReferenceTypeName;

	public Variable(IVariable var)
	{
		variable = var;
	}
	
	public IVariable getVariable()
	{
		return variable;
	}
	
	
	/**
	 * Gives cell's content when dragged.
	 */
	public String toString()
	{
		String string = "<NoName>";
		try
		{
			string = variable != null ? variable.getName() : cachedName;			
		    string += " : "; 
			string += variable != null ? variable.getReferenceTypeName() : cachedReferenceTypeName;		    
			
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
				out.writeObject(variable.getName());
				out.writeObject(variable.getReferenceTypeName());
			} catch (DebugException e) {}
		    }
	
		 private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
		     {
			 cachedName = (String) in.readObject();
			 cachedReferenceTypeName = (String) in.readObject();
		     }
}

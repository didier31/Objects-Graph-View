

package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
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
	protected String cachedReferenceTypeName,
	                 cachedValue;

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
		String string = "<No Type>";
		try
		{
			string = variable != null ? variable.getReferenceTypeName() : cachedReferenceTypeName;
			
			IValue value = variable != null ? variable.getValue() : null;
			if (value != null)
			{
			if (!value.hasVariables())
			{
				string += " := " + value.getValueString();
			}
			}
			else if (cachedValue != null)
			{
				string += " := " + cachedValue;
			}
				
			
			return string;
		}
		catch (DebugException e)
		{
			return string;
		}
	}
	
	protected void writeObject(java.io.ObjectOutputStream out)
		     throws IOException
		     {
		     try {
				out.writeObject(variable.getReferenceTypeName());
				if (variable.getValue().hasVariables())
				{
					out.writeObject(null);
				}
				else
				{
				out.writeObject(variable.getValue().getValueString());
				}
			} catch (DebugException e) {}
		    }
	
		 protected void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
		     {
			 cachedReferenceTypeName = (String) in.readObject();
			 cachedValue = (String) in.readObject();
		     }
}

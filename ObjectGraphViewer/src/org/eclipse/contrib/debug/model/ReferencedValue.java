

package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.contrib.debug.control.TypenameModifier;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;

/**
 * @author didier
 * 
 * a serializable Wrapper of IVariable's contrete descendants
 * 
 * This has been made necessary to avoid NotSerialisable exception thrown on
 * awt drag 'n drop of a graph cell. 
 */
public class ReferencedValue implements Serializable {

	private static final long serialVersionUID = -7665943218834959525L;
	
	private IValue value;
	
	private String cachedReferenceTypeName;
	
	
	public ReferencedValue(IValue value)
	{
		this.value = value;
	}
	
	public IValue getValue()
	{
		return value;
	}
	
	/**
	 * Gives cell's content when dragged.
	 */
	@Override
	public String toString()
	{
		String type = null;
		
		try
		{
			type = getValue() != null ? getValue().getReferenceTypeName() : cachedReferenceTypeName;
			type = TypenameModifier.modify(type);
		}
		catch (DebugException e)
		{
			type = "<NoType>";
			e.printStackTrace();
		}
		
		return type;

	}
	

	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException
		     {
		     try {
				out.writeObject(getValue().getReferenceTypeName());
			} catch (DebugException e) {}
		    }
		
  private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
		     {
			 cachedReferenceTypeName = (String) in.readObject();
		     }
	
}

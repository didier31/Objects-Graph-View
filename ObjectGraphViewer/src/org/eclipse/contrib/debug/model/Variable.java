package org.eclipse.contrib.debug.model;

import java.io.IOException;

import org.eclipse.contrib.debug.control.TypenameModifier;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

abstract public class Variable extends Additional {

	private String cachedName;
	private String cachedReferenceTypeName;
	
	private IVariable variable;

	public Variable()
	{
		cachedName = "";
		cachedReferenceTypeName = cachedName;		
	}
	
	public Variable(IVariable var) {
		variable = var;
		setRank(Integer.MAX_VALUE);
	}
	
	static Variable makeVariable(IVariable var)
	{
		try {
			return  var.getValue().getReferenceTypeName().startsWith("char[") 
					? new StringVariable(var) : var.getValue().hasVariables() ? new ReferenceVariable(var) : new LiteralVariable(var);
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public IVariable getVariable()
	{
		return variable;
	}
	
	/**
	 * Gives cell's content even when dragged.
	 */
	@Override
	public String toString()
	{
		String name = null;
		try
		{
			name = getVariable() != null ? getVariable().getName() : cachedName;
		}
		catch (DebugException e)
		{
			e.printStackTrace();
			name = "<NoName>";
		}
		
		String type = null;
		
		try
		{
			type = getVariable() != null ? getVariable().getReferenceTypeName() : cachedReferenceTypeName;
			type = TypenameModifier.modify(type);
		}
		catch (DebugException e)
		{
			type = "<NoType>";
			e.printStackTrace();
		}
		
		return name + " : " + type;

	}
	
	protected void doWriteObject(java.io.ObjectOutputStream out)
		     throws IOException
		     {
		     try {
				out.writeObject(getVariable().getName());
				out.writeObject(getVariable().getReferenceTypeName());
			} catch (DebugException e) {}
		    }
		
   protected void doReadObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
		     {
			 cachedName = (String) in.readObject();
			 cachedReferenceTypeName = (String) in.readObject();
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

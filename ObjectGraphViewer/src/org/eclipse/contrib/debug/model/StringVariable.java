package org.eclipse.contrib.debug.model;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIFieldVariable;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;

public class StringVariable extends Variable implements Serializable {
	private String cachedValue;

	public StringVariable(IVariable var) {
		super(var);
		// TODO Auto-generated constructor stub
	}

	public StringVariable()
	{
		super();
		cachedValue = "";
	}
	
	public String toString()
	{
		String value = null;		
		try {
			value = getVariable() != null ? stringValue() : cachedValue;
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			value = "<No Value>";
		}
		return super.toString() +  " := " + value;
	}
	
	protected String stringValue() throws DebugException
	{
	IValue value = getVariable().getValue();
	String stringValue = new String();
	for (IVariable c : value.getVariables())
	{
		stringValue += c.getValue().getValueString();
	}
	return '"' + stringValue + '"';
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

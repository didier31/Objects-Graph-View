package org.eclipse.contrib.debug.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.contrib.debug.preferences.PreferenceConstants;
import org.eclipse.contrib.debug.startup.Activator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class TypenameModifier {

	private Pattern searchPattern = Pattern.compile("");
	private String replacer = new String("");
	private String lastError = new String("");
	
	private List<ChangeListener> listOfChangeListener = new ArrayList<ChangeListener>(1); 
	
	static private TypenameModifier typeModifier = new TypenameModifier();
	
	static 
	{
		Pattern pattern = testPattern(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.REPLACE));
		TypenameModifier.setPattern(pattern);
		
		TypenameModifier.setReplacer(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.REPLACER));
		
		Activator.getDefault().getPreferenceStore()
        .addPropertyChangeListener(new IPropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == PreferenceConstants.REPLACER) 
            {
            	TypenameModifier.setReplacer((String) event.getNewValue());
            }
            else if  (event.getProperty() == PreferenceConstants.REPLACE) 
            {
            	Pattern pattern = testPattern((String) event.getNewValue());
            	TypenameModifier.setPattern(pattern);
            }
          }
        });
	}
	
    static public void setPattern(Pattern pattern)
    {
    	typeModifier.searchPattern = pattern;
    	typeModifier.fireChangeEvent();
    }
    
    static public Pattern testPattern(String patternStr)
    {
    	Pattern pattern = null;
    	try {
    		pattern = Pattern.compile(patternStr);
    	}
    	catch (PatternSyntaxException e)
    	{
    		typeModifier.lastError = e.getMessage();
    	}
    	
    	return pattern;	
    }

    static public String modify(String str)
	{
    	String formatted = (typeModifier.searchPattern == null || typeModifier.searchPattern.equals("")) 
    			           ? str 
    			           : typeModifier.searchPattern.matcher(str).replaceAll(typeModifier.replacer);
    	    	
    	return formatted;
	}

	static public String lastError()
	{
		return typeModifier.lastError;
	}
	
	static public void setReplacer(String replacer)
	{
		typeModifier.replacer = replacer;
		typeModifier.fireChangeEvent();
	}
	
	static public void addChangeListener(ChangeListener listener)
	{
		typeModifier.listOfChangeListener.add(listener);
	}
	
	protected void fireChangeEvent()
	{
		ChangeEvent e = new ChangeEvent(this);
		for (Iterator<ChangeListener> it = listOfChangeListener.iterator(); it.hasNext();)
		{
			it.next().stateChanged(e);
		}
	}
}

package org.eclipse.contrib.debug.ui.view;


import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.mxgraph.swing.mxGraphComponent;
import org.eclipse.contrib.debug.model.ObjectGraphViewer;


public class ObjectGraphViewPart extends ViewPart { 
	
	public static final String ID = "org.eclipse.contrib.debug.ui.view.ObjectGraph";
	/** 
     * le graphe 
     */ 
    private SwingControl control;
    
    
    public SwingControl getControl() {
		return control;
	}

	private ObjectGraphViewer graph;	
	
    public ObjectGraphViewer getGraph()
    {
    	return graph;
    }
    
    // Caution: Called by SWT Thread
    @Override 
    public void createPartControl(final Composite parent) {
        control = new SwingControl(parent, SWT.NONE) {            
			{
                setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
                setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_RED));                
            }
            
            @Override
            protected JComponent createSwingComponent() {
            	
            	setGraph(new ObjectGraphViewer(ObjectGraphViewPart.this));       
            	
            	mxGraphComponent graphComponent = new mxGraphComponent(getGraph());
                
                graphComponent.setBorder(new EmptyBorder(0,0,0,0));  
                graphComponent.setConnectable(false);
                
                getGraph().setGraphComponent(graphComponent);
                
                return graphComponent;
            }

			public Composite getLayoutAncestor() {
                return parent;
            }
            
        };
    }

    
    protected void setGraph(ObjectGraphViewer mxGraph) {
		graph = mxGraph;
		
	}
    
        	@Override
        	public void setFocus() {
        		// TODO Auto-generated method stub
        		control.setFocus();
        	}            
} 


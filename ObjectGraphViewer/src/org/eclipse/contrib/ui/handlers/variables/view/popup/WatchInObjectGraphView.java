/**
 * 
 */
package org.eclipse.contrib.ui.handlers.variables.view.popup;

import org.eclipse.contrib.debug.control.Controller;
import org.eclipse.contrib.debug.ui.view.ObjectGraphViewPart;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author didier
 *
 */
public class WatchInObjectGraphView implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TreeSelection selection = (TreeSelection) HandlerUtil.getActiveWorkbenchWindow(event)
		                                                     .getActivePage().getSelection();	
		
		ObjectGraphViewPart objectGraphViewPart = (ObjectGraphViewPart) PlatformUI
				                                                       .getWorkbench()
				                                                       .getActiveWorkbenchWindow()
				                                                       .getActivePage()
				                                                       .findView(ObjectGraphViewPart.ID);
		if (objectGraphViewPart != null)
		{
		Controller.get().set(objectGraphViewPart.getGraph());
		Controller.get().addGraphInput(selection.toList());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isHandled()
	 */
	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}

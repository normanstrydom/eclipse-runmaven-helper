package za.co.felixsol.eclipse.runmavenhelp.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExecMavenHelperHandler extends AbstractHandler {

	private IViewPart getProjectExplorerView() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView("org.eclipse.ui.navigator.ProjectExplorer");
	}

	private void processSelection(ExecutionEvent event, ITreeSelection selection, Object selectedItem)
			throws CoreException {
		IResource pom = findPomToProcess(event, selection, selectedItem);
		if (pom != null) {
			MyLogger.logInfo("file:" + pom);
		}
	}

	private IResource findPomToProcess(ExecutionEvent event, ITreeSelection selection, Object selectedItem)
			throws CoreException {
		TreePath[] paths = selection.getPathsFor(selectedItem);
		if (paths.length > 0) {
			TreePath path = paths[paths.length - 1];
			for (int i = 0; i < path.getSegmentCount(); i++) {
				Object item = path.getSegment(path.getSegmentCount() - i - 1);
				if (item instanceof IContainer) {
					IContainer container = (IContainer) item;
					if (container.members() != null) {
						for (IResource resource : container.members()) {
							if (resource instanceof IFile) {
								IFile file = (IFile) resource;
								if ("pom.xml".equals(file.getName())) {
									return resource;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IViewPart viewPart = getProjectExplorerView();
		if (viewPart != null) {
			ISelection selection = viewPart.getSite().getSelectionProvider().getSelection();
			if ((selection != null) && (selection instanceof ITreeSelection)) {
				Iterator iterator = ((ITreeSelection) selection).iterator();
				while (iterator.hasNext()) {
					try {
						processSelection(event, (ITreeSelection) selection, iterator.next());
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			showMessage(event, "Project explorer not available.");
		}

		return null;
	}

	private void showMessage(ExecutionEvent event, String message) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "ExecMavenHelper", message);
	}

}

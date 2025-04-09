package za.co.felixsol.eclipse.runmavenhelp.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

public class ExecMavenHelperHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

//		IViewPart viewPart = PlatformUI.getWorkbench()
//			    .getActiveWorkbenchWindow()
//			    .getActivePage()
//			    .findView("org.eclipse.ui.navigator.ProjectExplorer");

		IViewPart viewPart;
		ISelection selection = null;
		try {
			viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView("org.eclipse.ui.navigator.ProjectExplorer");
//			    .findView("org.eclipse.jdt.ui.PackageExplorer");

			selection = viewPart.getSite().getSelectionProvider().getSelection();
			
			MyLogger.logInfo("viewPart.getSite().getPart()=" + viewPart.getSite().getPart());
//			viewPart.getSite().getPart().setFocus();
			
			MyLogger.logInfo("Selection - " + selection);

			boolean showTree = false;
			boolean showChildren = true;

			if (selection != null) {
				MyLogger.logInfo("Selection class - " + selection.getClass());
				ITreeSelection ts = (ITreeSelection) selection;
				
				if (showChildren) {
					Iterator iterator = ts.iterator();
					while (iterator.hasNext()) {
						Object obj = iterator.next();
						MyLogger.logInfo("Obj - " + obj + ":" + obj.getClass());
						TreePath[] paths = ts.getPathsFor(obj);
						for (int i=0; i<paths.length; i++) {
							MyLogger.logInfo("path - " + i+ ":" + paths[i] + ":" + paths[i].getClass());
							MyLogger.logInfo("segmentcount = " + paths[i].getSegmentCount());
							for (int j=0; j<paths[i].getSegmentCount(); j++) {
								MyLogger.logInfo("segment " + j + ":" + paths[i].getSegment(j));
								MyLogger.logInfo("\tclass " + j + ":" + paths[i].getSegment(j).getClass());
								
								if (paths[i].getSegment(j) instanceof IContainer) {
									MyLogger.logInfo("\t\tcontainer");
									IContainer container = (IContainer)paths[i].getSegment(j);
									if (container.members() !=null) {
										MyLogger.logInfo("\t\t\tmembers" + container.members().length);
										for (int k =0; k<container.members().length; k++) {
											MyLogger.logInfo("\t\t\t\tmember=" + container.members()[k]); 
											MyLogger.logInfo("\t\t\t\t\tis IContainer=" + (container.members()[k] instanceof IContainer)); 
										}
									}
								}
								
							}
						}
					}
				}

				if (showTree) {
					Iterator iterator = ts.iterator();
					while (iterator.hasNext()) {
						Object obj = iterator.next();
						MyLogger.logInfo("Obj - " + obj + ":" + obj.getClass());
						TreePath[] paths = ts.getPathsFor(obj);
						for (TreePath path : paths) {
							MyLogger.logInfo("Path - " + path);
							MyLogger.logInfo("Path.segCount - " + path.getSegmentCount());
							for (int i = 0; i < path.getSegmentCount(); i++) {
								MyLogger.logInfo("Seg - " + i + "=" + path.getSegment(i));
								MyLogger.logInfo("Seg class - " + i + "=" + path.getSegment(i).getClass());

								if (path.getSegment(i) instanceof IFile) {
									IFile file = (IFile) path.getSegment(i);
									MyLogger.logInfo("File name : " + file.getName());
									MyLogger.logInfo("File location : " + file.getLocation());
								}

							}
						}
					}
				}

//				TreePath[] paths = ts.getPaths();
//				if (paths != null) {
//					for (TreePath path : paths) {
//						MyLogger.logInfo("Path - " + path + ":" + path.getFirstSegment());
//						MyLogger.logInfo("ParentPath - " + path.getParentPath());
//						while (path != null) {
//							MyLogger.logInfo(" ** Path - " + path + ":" + path);
//							if ((path != null) && (path.getSegmentCount() > 0)) {
//								for (int i = 0; i < path.getSegmentCount(); i++) {
//									MyLogger.logInfo("  ** Segment("+ i + ") : " + path.getSegment(i));
//									MyLogger.logInfo("  	*** Segment("+ i + ") : " + path.getSegment(i).getClass());
//								}
//							}
//							path = path.getParentPath();
//						}
//					}
//				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

//		MyLogger.logInfo("Logggggggggtest!");
//
//		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
//		System.out.println(projects);
//		if (projects != null) {
//			System.out.println(projects.length);
//			for (IProject project : projects) {
//				System.out.println(project.getName());
//			}
//
//		}

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "CustomCommandTest",
				"Hello, Eclipse world!! - " + selection.toString());
		return null;
	}
}

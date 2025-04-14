package za.co.felixsol.eclipse.runmavenhelp.handlers;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
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

	public void launchMavenGoals(IResource project, String goals) {
		try {
			// Get the launch manager
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

			String configName = "MavenRunHelper - " + project.getProject().getName() + " - " + goals;

			ILaunchConfiguration launchConfiguration = null;

			// Get the Maven launch config type
			ILaunchConfigurationType mavenType = launchManager
					.getLaunchConfigurationType("org.eclipse.m2e.Maven2LaunchConfigurationType");

			ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations(); // mavenType.getPrototypes();
			if (configurations != null) {
				for (ILaunchConfiguration configuration : configurations) {
					if (configName.equals(configuration.getName())) {
						launchConfiguration = configuration;
						ILaunchConfigurationWorkingCopy workingCopy = launchConfiguration.getWorkingCopy();
						workingCopy.setAttribute("org.eclipse.jdt.launching.WORKING_DIRECTORY",
								project.getParent().getLocation().toOSString());
						launchConfiguration = workingCopy.doSave();
						break;
					}
				}
			}

			if (launchConfiguration == null) {
				// Create a working copy
				ILaunchConfigurationWorkingCopy workingCopy = mavenType.newInstance(null,
						launchManager.generateLaunchConfigurationName(configName));

				// Set required attributes
				workingCopy.setAttribute("org.eclipse.jdt.launching.WORKING_DIRECTORY",
						project.getParent().getLocation().toOSString());
				workingCopy.setAttribute("M2_GOALS", "clean install");
				workingCopy.setAttribute("M2_SKIP_TESTS", true);
				workingCopy.setAttribute("org.eclipse.m2e.PROFILES", "");
				workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_OUTPUT", true);
				workingCopy.setAttribute("org.eclipse.m2e.MAVEN_PROJECT_NAME", project.getName());

				// Save and launch
				launchConfiguration = workingCopy.doSave();
			}

			launchConfiguration.launch(ILaunchManager.RUN_MODE, null);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void processSelection(ExecutionEvent event, ITreeSelection selection, Object selectedItem, String goals)
			throws CoreException {
		IResource pom = findPomToProcess(event, selection, selectedItem);
		if (pom != null) {
			launchMavenGoals(pom,goals);
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
		
		String goals = "install";
		if (event.getCommand().getId().equals("za.co.felixsol.eclipse.runmavenhelp.handlers.MavenClean")) {
			goals = "clean";
		} else if (event.getCommand().getId().equals("za.co.felixsol.eclipse.runmavenhelp.handlers.MavenCleanInstall")) {
			goals = "clean install";
		}
		
		IViewPart viewPart = getProjectExplorerView();
		if (viewPart != null) {
			ISelection selection = viewPart.getSite().getSelectionProvider().getSelection();
			if ((selection != null) && (selection instanceof ITreeSelection)) {
				Iterator iterator = ((ITreeSelection) selection).iterator();
				while (iterator.hasNext()) {
					try {
						processSelection(event, (ITreeSelection) selection, iterator.next(), goals);
					} catch (CoreException e) {
						throw new ExecutionException(e.getMessage(), e);
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

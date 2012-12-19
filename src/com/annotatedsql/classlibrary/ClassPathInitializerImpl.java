package com.annotatedsql.classlibrary;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class ClassPathInitializerImpl extends ClasspathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		
		ClassPathContainerImpl container = new ClassPathContainerImpl(containerPath, containerPath.toFile());
		JavaCore.setClasspathContainer(containerPath, 
				new IJavaProject[] {project}, 
				new IClasspathContainer[] {container}, 
				null);             
	}

	@Override
    public boolean canUpdateClasspathContainer(IPath containerPath, 
            IJavaProject project) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.ClasspathContainerInitializer#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject, org.eclipse.jdt.core.IClasspathContainer)
     */
    @Override
    public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException {
        JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project },   new IClasspathContainer[] { containerSuggestion }, null);
    }
}

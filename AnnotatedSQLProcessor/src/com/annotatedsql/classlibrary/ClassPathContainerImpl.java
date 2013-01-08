package com.annotatedsql.classlibrary;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

import com.annotatedsql.Activator;

public class ClassPathContainerImpl implements IClasspathContainer {

	public final static Path ID = new Path("com.annotatedsql.LIBRARY");
	
	private IPath path;
	
	public ClassPathContainerImpl(IPath path){
		this.path = path;
	}
	
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL api = bundle.getEntry("sqlannotation-annotations.jar");
		if(api != null){
			try {
				String file = FileLocator.toFileURL(api).toString();
				if(file.startsWith("file:")){
					file = file.substring(5);
				}
				entryList.add(JavaCore.newLibraryEntry(
							new Path(file), 
							null, 
							null, 
							true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return entryList.toArray(new IClasspathEntry[entryList.size()]);
	}

	@Override
	public String getDescription() {
		return "AnnotatedSQL Library";
	}

	@Override
	public int getKind() {
		return IClasspathEntry.CPE_CONTAINER;
	}

	@Override
	public IPath getPath() {
		return path;
	}

}

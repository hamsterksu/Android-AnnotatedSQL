/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.annotatedsql.classlibrary;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.ClasspathContainerDefaultPage;

public class ClassPathContainerPage extends ClasspathContainerDefaultPage{
	
	@Override
	public void setSelection(IClasspathEntry containerEntry) {
		if(containerEntry == null){
			containerEntry = JavaCore.newContainerEntry(ClassPathContainerImpl.ID);
		}
		super.setSelection(containerEntry);
	}
	
}

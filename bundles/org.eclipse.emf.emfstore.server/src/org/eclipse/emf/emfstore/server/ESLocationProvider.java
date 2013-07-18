/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk, Maximilian Koegel - initial API and implementation
 * Edgar Mueller - API annotations
 ******************************************************************************/
package org.eclipse.emf.emfstore.server;

import org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider;

/**
 * Interface for workspace location providers. Implementing classes provide a location for the workspace - or server
 * workspace - files to store models and other files. You may subclass
 * {@link org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider} in
 * order to ease your implementation. By convention, every path should end with an folder separator char.
 * 
 * @author koegel
 * @author wesendon
 * 
 */
public interface ESLocationProvider {

	/**
	 * Get the path to the workspace directory, where the model data is stored to. This method is called only once on
	 * workspace startup to retrieve path. If you want to use profiles look at
	 * {@link DefaultServerWorkspaceLocationProvider} and it's subclasses .
	 * 
	 * @return a string representing the path
	 */
	String getWorkspaceDirectory();

}

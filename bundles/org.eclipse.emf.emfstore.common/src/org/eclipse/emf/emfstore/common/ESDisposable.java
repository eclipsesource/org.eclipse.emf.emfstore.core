/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.common;

/**
 * Disposable interface for marking classes that need to perform clean up tasks when
 * de-initialized.
 * 
 * @author emueller
 */
public interface ESDisposable {

	/**
	 * Marker method for classes that need to perform clean-up tasks.
	 */
	void dispose();
}

/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier
 ******************************************************************************/
package org.eclipse.emf.emfstore.common.extensionpoint;

/**
 * Interface for server resource set provider.
 * 
 * @author jfaltermeier
 * 
 */
public interface ESServerResourceSetProvider extends ESResourceSetProvider {

	/**
	 * Registers the dynamic models on server side.
	 */
	void registerDynamicModels();

}

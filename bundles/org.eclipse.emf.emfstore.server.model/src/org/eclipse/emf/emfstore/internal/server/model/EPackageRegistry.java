/**
 * Copyright 2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 */
package org.eclipse.emf.emfstore.internal.server.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EPackage Registry</b></em>'.
 * <!-- end-user-doc -->
 * 
 * 
 * @see org.eclipse.emf.emfstore.internal.server.model.ModelPackage#getEPackageRegistry()
 * @model
 * @generated
 */
public interface EPackageRegistry extends EObject
{
	/**
	 * Registers dynamic EPackages.
	 */
	void registerDynamicEPackages();
} // EPackageRegistry

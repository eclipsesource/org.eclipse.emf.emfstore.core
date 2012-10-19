/*******************************************************************************
 * Copyright 2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/

package org.eclipse.emf.emfstore.client.test.integration.reversibility;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Hodaie
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AttributeOperationsReversibilityTest.class, ReferenceOperationsReversibilityTest.class,
	CreateDeleteOperationsReversibilityTest.class })
public class AllReversibilityIntegrationTests {

}

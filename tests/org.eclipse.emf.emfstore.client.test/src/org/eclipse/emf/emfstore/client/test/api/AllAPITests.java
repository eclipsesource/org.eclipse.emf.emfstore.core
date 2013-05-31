/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for running all tests of canonization.
 * 
 * @author koegel
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	ServerCreationTest.class,
	ModelElementTest.class,
	ServerCommunicationTest.class,
	UnsharedLocalProjectTest.class,
	SharedProjectTest.class,
	UsersessionTest.class,
	WorkspaceTest.class
})
public class AllAPITests {

}

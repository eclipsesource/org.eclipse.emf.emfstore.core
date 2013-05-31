/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Hodaie
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.integration;

import org.eclipse.emf.emfstore.client.test.integration.forward.AllForwardIntegrationTests;
import org.eclipse.emf.emfstore.client.test.integration.reversibility.AllReversibilityIntegrationTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Hodaie
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AllForwardIntegrationTests.class, AllReversibilityIntegrationTests.class })
public class AllIntegrationTests {

}

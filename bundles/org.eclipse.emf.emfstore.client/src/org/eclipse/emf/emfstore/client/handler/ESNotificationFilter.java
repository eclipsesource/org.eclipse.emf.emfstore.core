/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * chodnick
 * koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.handler;

import org.eclipse.emf.emfstore.common.model.ESObjectContainer;
import org.eclipse.emf.emfstore.common.model.util.ESNotificationInfo;

/**
 * Interface for filtering notifications.
 * 
 * @author chodnick
 * @author koegel
 */
public interface ESNotificationFilter {

	/**
	 * Check a notification if it should be ignored.
	 * 
	 * @param notificationInfo
	 *            the {@link ESNotificationInfo} to check
	 * @param container
	 *            the collection that holds or did hold the model element the notification
	 *            is about
	 * @return true if the notification is to be ignored
	 */
	boolean check(ESNotificationInfo notificationInfo, ESObjectContainer<?> container);

}

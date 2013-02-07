/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.ui.decorators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.emfstore.client.api.ILocalProject;
import org.eclipse.emf.emfstore.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.client.model.WorkspaceProvider;
import org.eclipse.emf.emfstore.client.model.observers.CommitObserver;
import org.eclipse.emf.emfstore.client.model.observers.UpdateObserver;
import org.eclipse.emf.emfstore.server.model.api.IChangePackage;
import org.eclipse.emf.emfstore.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * The decorator that shows the version of a ProjectSpace.
 * 
 * @author Helming
 */
public class VersionDecorator extends AdapterImpl implements ILightweightLabelDecorator, UpdateObserver, CommitObserver {

	private ArrayList<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();

	/**
	 * Default constructor.
	 */
	public VersionDecorator() {
		WorkspaceProvider.getObserverBus().register(this);
	}

	/**
	 * . {@inheritDoc}
	 */
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof ProjectSpace) {
			ProjectSpace projectSpace = (ProjectSpace) element;
			StringBuilder stringBuilder = new StringBuilder();
			if (projectSpace.getBaseVersion() != null) {
				stringBuilder.append("@");
				stringBuilder.append(projectSpace.getBaseVersion().getIdentifier());
				stringBuilder.append(" [" + projectSpace.getBaseVersion().getBranch() + "]");
			} else {
				stringBuilder.append("(Not shared)");
			}
			String string = stringBuilder.toString();
			decoration.addSuffix(string);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		listeners.add(listener);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		listeners.removeAll(listeners);

	}

	/**
	 * . {@inheritDoc}
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void notifyChanged(Notification msg) {
		for (ILabelProviderListener listener : listeners) {
			listener.labelProviderChanged(new LabelProviderChangedEvent(this));
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.observers.CommitObserver#inspectChanges(org.eclipse.emf.emfstore.client.model.ProjectSpace,
	 *      org.eclipse.emf.emfstore.server.model.versioning.ChangePackage, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean inspectChanges(ProjectSpace projectSpace, ChangePackage changePackage, IProgressMonitor monitor) {
		return false;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.observers.CommitObserver#commitCompleted(org.eclipse.emf.emfstore.client.model.ProjectSpace,
	 *      org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void commitCompleted(ProjectSpace projectSpace, PrimaryVersionSpec newRevision, IProgressMonitor monitor) {
		update();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean inspectChanges(ILocalProject project, List<IChangePackage> changePackages,
		IProgressMonitor monitor) {
		return true;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.observers.UpdateObserver#updateCompleted(org.eclipse.emf.emfstore.client.api.ILocalProject,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void updateCompleted(ILocalProject project, IProgressMonitor monitor) {
		update();
	}

	private void update() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				PlatformUI.getWorkbench().getDecoratorManager()
					.update("org.eclipse.emf.emfstore.client.ui.decorators.VersionDecorator");
			}
		});
	}

}
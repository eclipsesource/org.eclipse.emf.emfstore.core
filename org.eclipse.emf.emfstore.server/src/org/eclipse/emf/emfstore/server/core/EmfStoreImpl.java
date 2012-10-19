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
package org.eclipse.emf.emfstore.server.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EnumMap;

import org.eclipse.emf.emfstore.server.EmfStore;
import org.eclipse.emf.emfstore.server.accesscontrol.AuthorizationControl;
import org.eclipse.emf.emfstore.server.core.helper.EmfStoreMethod;
import org.eclipse.emf.emfstore.server.core.helper.EmfStoreMethod.MethodId;
import org.eclipse.emf.emfstore.server.core.subinterfaces.EMFStorePropertiesSubInterfaceImpl;
import org.eclipse.emf.emfstore.server.core.subinterfaces.EPackageSubInterfaceImpl;
import org.eclipse.emf.emfstore.server.core.subinterfaces.FileTransferSubInterfaceImpl;
import org.eclipse.emf.emfstore.server.core.subinterfaces.HistorySubInterfaceImpl;
import org.eclipse.emf.emfstore.server.core.subinterfaces.ProjectPropertiesSubInterfaceImpl;
import org.eclipse.emf.emfstore.server.core.subinterfaces.ProjectSubInterfaceImpl;
import org.eclipse.emf.emfstore.server.core.subinterfaces.UserSubInterfaceImpl;
import org.eclipse.emf.emfstore.server.core.subinterfaces.VersionSubInterfaceImpl;
import org.eclipse.emf.emfstore.server.exceptions.EmfStoreException;
import org.eclipse.emf.emfstore.server.exceptions.FatalEmfStoreException;
import org.eclipse.emf.emfstore.server.model.ServerSpace;

/**
 * This is the main implementation of {@link EmfStore}.
 * 
 * @author wesendon
 * @see EmfStore
 */
public class EmfStoreImpl extends AbstractEmfstoreInterface implements InvocationHandler {

	/**
	 * Represents a method in a subinterface.
	 * 
	 * @author boehlke
	 */
	private class SubInterfaceMethod {
		private AbstractSubEmfstoreInterface iface;
		private Method method;

		public SubInterfaceMethod(AbstractSubEmfstoreInterface iface, Method m) {
			this.method = m;
			this.iface = iface;
		}

		/**
		 * @return the iface
		 */
		public AbstractSubEmfstoreInterface getIface() {
			return iface;
		}

		/**
		 * @return the method
		 */
		public Method getMethod() {
			return method;
		}
	}

	private EnumMap<MethodId, SubInterfaceMethod> subInterfaceMethods;

	/**
	 * Default constructor.
	 * 
	 * @param serverSpace
	 *            the serverspace
	 * @param authorizationControl
	 *            the accesscontrol
	 * @throws FatalEmfStoreException
	 *             in case of failure
	 */
	public EmfStoreImpl(ServerSpace serverSpace, AuthorizationControl authorizationControl)
		throws FatalEmfStoreException {
		super(serverSpace, authorizationControl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initSubInterfaces() throws FatalEmfStoreException {
		subInterfaceMethods = new EnumMap<MethodId, SubInterfaceMethod>(MethodId.class);
		addSubInterface(new HistorySubInterfaceImpl(this));
		addSubInterface(new ProjectSubInterfaceImpl(this));
		addSubInterface(new UserSubInterfaceImpl(this));
		addSubInterface(new VersionSubInterfaceImpl(this));
		addSubInterface(new FileTransferSubInterfaceImpl(this));
		addSubInterface(new ProjectPropertiesSubInterfaceImpl(this));
		addSubInterface(new EMFStorePropertiesSubInterfaceImpl(this));
		addSubInterface(new EPackageSubInterfaceImpl(this));
	}

	@Override
	protected void addSubInterface(AbstractSubEmfstoreInterface iface) {
		super.addSubInterface(iface);
		for (Method method : iface.getClass().getMethods()) {
			EmfStoreMethod implSpec = method.getAnnotation(EmfStoreMethod.class);
			if (implSpec != null) {
				subInterfaceMethods.put(implSpec.value(), new SubInterfaceMethod(iface, method));
			}
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object obj, Method method, Object[] args) throws EmfStoreException {
		MethodInvocation methodInvocation = new MethodInvocation(method.getName(), args);

		getAuthorizationControl().checkAccess(methodInvocation);
		SubInterfaceMethod subIfaceMethod = subInterfaceMethods.get(methodInvocation.getType());
		return subIfaceMethod.getIface().execute(subIfaceMethod.getMethod(), args);
	}

	/**
	 * creates a dynamic proxy backed by EmfStoreImpl.
	 * 
	 * @param serverSpace the server space
	 * @param accessControl an access control instance
	 * @return an instance of emfstore
	 * @throws IllegalArgumentException thrown by Proxy.newInstance
	 * @throws FatalEmfStoreException thrown if something fatal happens
	 */
	public static EmfStore createInterface(ServerSpace serverSpace, AuthorizationControl accessControl)
		throws IllegalArgumentException, FatalEmfStoreException {
		return (EmfStore) Proxy.newProxyInstance(EmfStoreImpl.class.getClassLoader(), new Class[] { EmfStore.class },
			new EmfStoreImpl(serverSpace, accessControl));
	}

}

/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * Change package helper class.
 *
 * @author emueller
 *
 */
public final class ChangePackageUtil {

	private ChangePackageUtil() {

	}

	/**
	 * Creates a new {@link AbstractChangePackage} depending on the client configuration behavior whether
	 * to create in-memory of file-based change packages.
	 *
	 * @param useInMemoryChangePackage
	 *            whether an in-memory change package should be created
	 *
	 * @return the created change package
	 */
	public static AbstractChangePackage createChangePackage(boolean useInMemoryChangePackage) {

		if (useInMemoryChangePackage) {
			return VersioningFactory.eINSTANCE.createChangePackage();
		}

		final FileBasedChangePackage fileBasedChangePackage = VersioningFactory.eINSTANCE
			.createFileBasedChangePackage();
		fileBasedChangePackage.initialize(FileUtil.createLocationForTemporaryChangePackage());
		return fileBasedChangePackage;
	}

	/**
	 * Given a single change package, splits it into multiple fragments.
	 *
	 * @param changePackage
	 *            the change package to be splitted
	 * @param changePackageFragmentSize
	 *            the max number of operations a single fragment may consists of
	 * @return an iterator for the created fragments
	 */
	public static Iterator<ChangePackageEnvelope> splitChangePackage(final FileBasedChangePackage changePackage,
		final int changePackageFragmentSize) {

		return new Iterator<ChangePackageEnvelope>() {

			private int fragmentIndex;
			private int count;
			private ChangePackageEnvelope envelope;
			private boolean isInitialized;

			public boolean hasNext() {

				if (!isInitialized) {
					init();
				}

				if (envelope == null) {
					envelope = VersioningFactory.eINSTANCE.createChangePackageEnvelope();
					envelope.setFragmentCount(count);
				}

				final List<String> readLines = readLines(fragmentIndex * changePackageFragmentSize, changePackage,
					changePackageFragmentSize);
				envelope.getFragment().addAll(readLines);

				envelope.setFragmentIndex(fragmentIndex);

				if (!envelope.getFragment().isEmpty() || fragmentIndex == 0) {
					return true;
				}

				return false;
			}

			private void init() {
				LineNumberReader lineNumberReader = null;
				try {
					lineNumberReader = new LineNumberReader(new FileReader(new File(changePackage.getTempFilePath())));
					lineNumberReader.skip(Long.MAX_VALUE);
					final int lines = lineNumberReader.getLineNumber() + 1;
					count = lines / changePackageFragmentSize;
					if (lines % changePackageFragmentSize != 0) {
						count += 1;
					}
				} catch (final FileNotFoundException ex) {
					throw new IllegalStateException(ex);
				} catch (final IOException ex) {
					throw new IllegalStateException(ex);
				} finally {
					IOUtils.closeQuietly(lineNumberReader);
				}
				isInitialized = true;
			}

			private List<String> readLines(int from, final FileBasedChangePackage changePackage,
				final int changePackageFragmentSize) {

				int readLines = 0;
				FileReader reader;
				final List<String> lines = new ArrayList<String>();

				try {
					reader = new FileReader(new File(changePackage.getTempFilePath()));
					final LineIterator lineIterator = new LineIterator(reader);
					int read = 0;

					while (read < from) {
						if (!lineIterator.hasNext()) {
							return lines;
						}
						lineIterator.next();
						read += 1;
					}

					while (readLines < changePackageFragmentSize && lineIterator.hasNext()) {
						final String nextLine = lineIterator.next();
						readLines += 1;
						lines.add(nextLine);
					}

				} catch (final FileNotFoundException ex) {
					throw new IllegalStateException(ex);
				}

				return lines;
			}

			public ChangePackageEnvelope next() {
				if (envelope == null) {
					final boolean hasNext = hasNext();
					if (!hasNext) {
						throw new NoSuchElementException();
					}
				}
				final ChangePackageEnvelope ret = envelope;
				envelope = null;
				fragmentIndex += 1;
				return ret;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

	}

	/**
	 * Count all leaf operations of a collection of {@link AbstractOperation}s.
	 *
	 * @param operations
	 *            a collection of operations
	 * @return the leaf operation count of all involved operations
	 */
	public static int countLeafOperations(Collection<AbstractOperation> operations) {
		int ret = 0;
		for (final AbstractOperation operation : operations) {
			if (operation instanceof CompositeOperation) {
				ret = ret + getSize((CompositeOperation) operation);
			} else {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * Count all leaf operations of a single {@link AbstractOperation}s.
	 *
	 * @param operation
	 *            a single operation
	 * @return the leaf operation count of the given operation
	 */
	public static int countLeafOperations(AbstractOperation operation) {
		return countLeafOperations(Collections.singleton(operation));
	}

	/**
	 * Count all leaf operations of all operations contained in the given list of {@link ChangePackage}s.
	 *
	 * @param changePackages
	 *            list of change packages
	 * @return the leaf operation count of all operations contained in the list of change packages
	 */
	public static int countLeafOperations(List<AbstractChangePackage> changePackages) {
		int count = 0;
		for (final AbstractChangePackage changePackage : changePackages) {
			final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
			try {
				for (final AbstractOperation operation : operations.iterable()) {
					count += countLeafOperations(operation);
				}
			} finally {
				operations.close();
			}
		}
		return count;
	}

	/**
	 * Count all root operations within the given list of {@link ChangePackage}s.
	 *
	 * @param changePackages
	 *            list of change packages
	 * @return the root operation count of all change packages
	 */
	public static int countOperations(List<AbstractChangePackage> changePackages) {
		int count = 0;
		for (final AbstractChangePackage changePackage : changePackages) {
			count += changePackage.size();
		}
		return count;
	}

	private static int getSize(CompositeOperation compositeOperation) {
		int ret = 0;
		final EList<AbstractOperation> subOperations = compositeOperation.getSubOperations();
		for (final AbstractOperation abstractOperation : subOperations) {
			if (abstractOperation instanceof CompositeOperation) {
				ret = ret + getSize((CompositeOperation) abstractOperation);
			} else {
				ret++;
			}
		}
		return ret;
	}
}

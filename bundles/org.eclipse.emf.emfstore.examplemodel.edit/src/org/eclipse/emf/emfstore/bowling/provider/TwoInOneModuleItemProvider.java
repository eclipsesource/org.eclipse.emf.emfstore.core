/**
 */
package org.eclipse.emf.emfstore.bowling.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.BowlingPackage;
import org.eclipse.emf.emfstore.bowling.TwoInOneModule;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.emfstore.bowling.TwoInOneModule} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class TwoInOneModuleItemProvider
	extends ModuleItemProvider
	implements
	IEditingDomainItemProvider,
	IStructuredItemContentProvider,
	ITreeItemContentProvider,
	IItemLabelProvider,
	IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public TwoInOneModuleItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

		}
		return itemPropertyDescriptors;
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE1);
			childrenFeatures.add(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE2);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns TwoInOneModule.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/TwoInOneModule"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((TwoInOneModule) object).getDescription();
		return label == null || label.length() == 0 ?
			getString("_UI_TwoInOneModule_type") :
			getString("_UI_TwoInOneModule_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(TwoInOneModule.class)) {
		case BowlingPackage.TWO_IN_ONE_MODULE__MODULE1:
		case BowlingPackage.TWO_IN_ONE_MODULE__MODULE2:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE1,
				BowlingFactory.eINSTANCE.createModule()));

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE1,
				BowlingFactory.eINSTANCE.createGPSModule()));

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE1,
				BowlingFactory.eINSTANCE.createElectroMagneticModule()));

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE1,
				BowlingFactory.eINSTANCE.createTwoInOneModule()));

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE2,
				BowlingFactory.eINSTANCE.createModule()));

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE2,
				BowlingFactory.eINSTANCE.createGPSModule()));

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE2,
				BowlingFactory.eINSTANCE.createElectroMagneticModule()));

		newChildDescriptors.add
			(createChildParameter
			(BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE2,
				BowlingFactory.eINSTANCE.createTwoInOneModule()));
	}

	/**
	 * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection) {
		Object childFeature = feature;
		Object childObject = child;

		boolean qualify =
			childFeature == BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE1 ||
				childFeature == BowlingPackage.Literals.TWO_IN_ONE_MODULE__MODULE2;

		if (qualify) {
			return getString("_UI_CreateChild_text2",
				new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
		}
		return super.getCreateChildText(owner, feature, child, selection);
	}

}

package com.dropbox.kaiken.processor

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers

internal fun validateFragment(descriptor: ClassDescriptor, clazz: KtClassOrObject) {
    val fqName = requireNotNull(clazz.fqName)

    check(descriptor.visibility == DescriptorVisibilities.PUBLIC) {
        "The class ${fqName.shortName()} is not public"
    }

    check(!DescriptorUtils.classCanHaveAbstractDeclaration(descriptor)) {
        "The class ${fqName.shortName()} is abstract"
    }

    check(descriptor.isFragment()) {
        "The class ${fqName.shortName()} is not an Android activity. Found: ${
        descriptor.getAllSuperClassifiers().toList().map { it.name }
        }"
    }
}

internal fun ClassifierDescriptor.isFragment(): Boolean =
    this.getAllSuperClassifiers().any {
        FqName("androidx.fragment.app.Fragment") == it.fqNameSafe
    }

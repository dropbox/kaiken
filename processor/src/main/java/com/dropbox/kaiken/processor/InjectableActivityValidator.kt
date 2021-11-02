package com.dropbox.kaiken.processor

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers

internal fun validateActivity(descriptor: ClassDescriptor?, clazz: KtClassOrObject) {
    check(descriptor != null) {
        "The ClassDescriptor for ${clazz.name} is null"
    }

    check(descriptor.visibility == DescriptorVisibilities.PUBLIC) {
        "The class ${clazz.fqName!!.shortName()} is not public"
    }

    check(!DescriptorUtils.classCanHaveAbstractDeclaration(descriptor)) {
        "The class ${clazz.fqName!!.shortName()} is abstract"
    }

    check(descriptor.isActivity()) {
        "The class ${clazz.fqName!!.shortName()} is not an Android activity. Found: ${
        descriptor.getAllSuperClassifiers().toList().map { it.name }
        }"
    }

    check(descriptor.implementsInjectorHolder()) {
        "The class ${clazz.fqName!!.shortName()} does not implement" +
            " DependencyProviderResolver"
    }
}

internal fun ClassifierDescriptor.isActivity(): Boolean =
    this.getAllSuperClassifiers().any {
        FqName("androidx.appcompat.app.AppCompatActivity") == it.fqNameSafe ||
            FqName("com.dropbox.kaiken.processor.internal.fakes.FakeActivity") == it.fqNameSafe
    }

private fun ClassifierDescriptor.implementsInjectorHolder(): Boolean =
    this.getAllSuperClassifiers().any {
        it.fqNameSafe == FqName("com.dropbox.kaiken.runtime.InjectorHolder")
    }

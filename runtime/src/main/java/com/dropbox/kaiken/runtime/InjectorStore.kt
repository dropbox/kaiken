package com.dropbox.kaiken.runtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dropbox.kaiken.Injector

internal fun <InjectorType : Injector> locateInjector(
    viewModelStoreOwner: ViewModelStoreOwner,
    injectorFactory: InjectorFactory<InjectorType>
): InjectorType {
    val injectorViewModel: InjectorViewModel<InjectorType> = locateInjectorViewModel(
        viewModelStoreOwner,
        injectorFactory
    )
    return injectorViewModel.injector
}

private fun <InjectorType : Injector> locateInjectorViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    injectorFactory: InjectorFactory<InjectorType>
): InjectorViewModel<InjectorType> {
    val viewModelProvider = ViewModelProvider(
        viewModelStoreOwner,
        InjectorViewModelFactory(injectorFactory)
    )

    @Suppress("UNCHECKED_CAST")
    return viewModelProvider.get(InjectorViewModel::class.java)
        as InjectorViewModel<InjectorType>
}

internal class InjectorViewModelFactory<InjectorType : Injector>(
    private val injectorFactory: InjectorFactory<InjectorType>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InjectorViewModel(injectorFactory.createInjector()) as T
    }
}

class InjectorViewModel<InjectorType>(
    val injector: InjectorType
) : ViewModel()

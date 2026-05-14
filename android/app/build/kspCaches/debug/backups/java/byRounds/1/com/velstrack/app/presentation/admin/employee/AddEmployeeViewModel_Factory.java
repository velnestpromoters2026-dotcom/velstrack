package com.velstrack.app.presentation.admin.employee;

import com.velstrack.app.domain.repository.AdminRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AddEmployeeViewModel_Factory implements Factory<AddEmployeeViewModel> {
  private final Provider<AdminRepository> repositoryProvider;

  public AddEmployeeViewModel_Factory(Provider<AdminRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AddEmployeeViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static AddEmployeeViewModel_Factory create(Provider<AdminRepository> repositoryProvider) {
    return new AddEmployeeViewModel_Factory(repositoryProvider);
  }

  public static AddEmployeeViewModel newInstance(AdminRepository repository) {
    return new AddEmployeeViewModel(repository);
  }
}

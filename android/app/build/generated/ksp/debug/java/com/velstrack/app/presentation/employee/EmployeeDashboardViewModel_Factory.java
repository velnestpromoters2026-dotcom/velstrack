package com.velstrack.app.presentation.employee;

import com.velstrack.app.domain.repository.EmployeeRepository;
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
public final class EmployeeDashboardViewModel_Factory implements Factory<EmployeeDashboardViewModel> {
  private final Provider<EmployeeRepository> repositoryProvider;

  public EmployeeDashboardViewModel_Factory(Provider<EmployeeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public EmployeeDashboardViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static EmployeeDashboardViewModel_Factory create(
      Provider<EmployeeRepository> repositoryProvider) {
    return new EmployeeDashboardViewModel_Factory(repositoryProvider);
  }

  public static EmployeeDashboardViewModel newInstance(EmployeeRepository repository) {
    return new EmployeeDashboardViewModel(repository);
  }
}

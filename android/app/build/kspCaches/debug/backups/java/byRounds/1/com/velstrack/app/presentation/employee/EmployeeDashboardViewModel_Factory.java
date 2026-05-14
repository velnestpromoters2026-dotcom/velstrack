package com.velstrack.app.presentation.employee;

import androidx.work.WorkManager;
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

  private final Provider<WorkManager> workManagerProvider;

  public EmployeeDashboardViewModel_Factory(Provider<EmployeeRepository> repositoryProvider,
      Provider<WorkManager> workManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public EmployeeDashboardViewModel get() {
    return newInstance(repositoryProvider.get(), workManagerProvider.get());
  }

  public static EmployeeDashboardViewModel_Factory create(
      Provider<EmployeeRepository> repositoryProvider, Provider<WorkManager> workManagerProvider) {
    return new EmployeeDashboardViewModel_Factory(repositoryProvider, workManagerProvider);
  }

  public static EmployeeDashboardViewModel newInstance(EmployeeRepository repository,
      WorkManager workManager) {
    return new EmployeeDashboardViewModel(repository, workManager);
  }
}

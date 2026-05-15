package com.velstrack.app.presentation.employee;

import android.content.Context;
import androidx.work.WorkManager;
import com.velstrack.app.core.datastore.SessionManager;
import com.velstrack.app.data.local.dao.CallDao;
import com.velstrack.app.data.remote.api.ApiService;
import com.velstrack.app.domain.repository.EmployeeRepository;
import com.velstrack.app.domain.telecom.SessionRecoveryManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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

  private final Provider<CallDao> callDaoProvider;

  private final Provider<ApiService> apiServiceProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<SessionRecoveryManager> sessionRecoveryManagerProvider;

  private final Provider<Context> contextProvider;

  public EmployeeDashboardViewModel_Factory(Provider<EmployeeRepository> repositoryProvider,
      Provider<WorkManager> workManagerProvider, Provider<CallDao> callDaoProvider,
      Provider<ApiService> apiServiceProvider, Provider<SessionManager> sessionManagerProvider,
      Provider<SessionRecoveryManager> sessionRecoveryManagerProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.workManagerProvider = workManagerProvider;
    this.callDaoProvider = callDaoProvider;
    this.apiServiceProvider = apiServiceProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.sessionRecoveryManagerProvider = sessionRecoveryManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public EmployeeDashboardViewModel get() {
    return newInstance(repositoryProvider.get(), workManagerProvider.get(), callDaoProvider.get(), apiServiceProvider.get(), sessionManagerProvider.get(), sessionRecoveryManagerProvider.get(), contextProvider.get());
  }

  public static EmployeeDashboardViewModel_Factory create(
      Provider<EmployeeRepository> repositoryProvider, Provider<WorkManager> workManagerProvider,
      Provider<CallDao> callDaoProvider, Provider<ApiService> apiServiceProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<SessionRecoveryManager> sessionRecoveryManagerProvider,
      Provider<Context> contextProvider) {
    return new EmployeeDashboardViewModel_Factory(repositoryProvider, workManagerProvider, callDaoProvider, apiServiceProvider, sessionManagerProvider, sessionRecoveryManagerProvider, contextProvider);
  }

  public static EmployeeDashboardViewModel newInstance(EmployeeRepository repository,
      WorkManager workManager, CallDao callDao, ApiService apiService,
      SessionManager sessionManager, SessionRecoveryManager sessionRecoveryManager,
      Context context) {
    return new EmployeeDashboardViewModel(repository, workManager, callDao, apiService, sessionManager, sessionRecoveryManager, context);
  }
}

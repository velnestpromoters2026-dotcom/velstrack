package com.velstrack.app.presentation.auth;

import com.velstrack.app.core.datastore.SessionManager;
import com.velstrack.app.data.remote.api.ApiService;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public AuthViewModel_Factory(Provider<ApiService> apiServiceProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(apiServiceProvider.get(), sessionManagerProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new AuthViewModel_Factory(apiServiceProvider, sessionManagerProvider);
  }

  public static AuthViewModel newInstance(ApiService apiService, SessionManager sessionManager) {
    return new AuthViewModel(apiService, sessionManager);
  }
}

package com.velstrack.app.domain.repository;

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
public final class EmployeeRepository_Factory implements Factory<EmployeeRepository> {
  private final Provider<ApiService> apiServiceProvider;

  public EmployeeRepository_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public EmployeeRepository get() {
    return newInstance(apiServiceProvider.get());
  }

  public static EmployeeRepository_Factory create(Provider<ApiService> apiServiceProvider) {
    return new EmployeeRepository_Factory(apiServiceProvider);
  }

  public static EmployeeRepository newInstance(ApiService apiService) {
    return new EmployeeRepository(apiService);
  }
}

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
public final class AdminRepository_Factory implements Factory<AdminRepository> {
  private final Provider<ApiService> apiServiceProvider;

  public AdminRepository_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public AdminRepository get() {
    return newInstance(apiServiceProvider.get());
  }

  public static AdminRepository_Factory create(Provider<ApiService> apiServiceProvider) {
    return new AdminRepository_Factory(apiServiceProvider);
  }

  public static AdminRepository newInstance(ApiService apiService) {
    return new AdminRepository(apiService);
  }
}

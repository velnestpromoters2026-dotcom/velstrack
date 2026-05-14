package com.velstrack.app.di;

import com.velstrack.app.core.datastore.SessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.Interceptor;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  private final Provider<SessionManager> sessionManagerProvider;

  public NetworkModule_ProvideAuthInterceptorFactory(
      Provider<SessionManager> sessionManagerProvider) {
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public Interceptor get() {
    return provideAuthInterceptor(sessionManagerProvider.get());
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create(
      Provider<SessionManager> sessionManagerProvider) {
    return new NetworkModule_ProvideAuthInterceptorFactory(sessionManagerProvider);
  }

  public static Interceptor provideAuthInterceptor(SessionManager sessionManager) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthInterceptor(sessionManager));
  }
}

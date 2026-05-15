package com.velstrack.app.domain.telecom;

import com.velstrack.app.data.local.dao.CallDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SessionRecoveryManager_Factory implements Factory<SessionRecoveryManager> {
  private final Provider<CallDao> callDaoProvider;

  public SessionRecoveryManager_Factory(Provider<CallDao> callDaoProvider) {
    this.callDaoProvider = callDaoProvider;
  }

  @Override
  public SessionRecoveryManager get() {
    return newInstance(callDaoProvider.get());
  }

  public static SessionRecoveryManager_Factory create(Provider<CallDao> callDaoProvider) {
    return new SessionRecoveryManager_Factory(callDaoProvider);
  }

  public static SessionRecoveryManager newInstance(CallDao callDao) {
    return new SessionRecoveryManager(callDao);
  }
}

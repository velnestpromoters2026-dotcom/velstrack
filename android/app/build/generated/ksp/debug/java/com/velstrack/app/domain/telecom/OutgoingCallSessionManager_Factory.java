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
public final class OutgoingCallSessionManager_Factory implements Factory<OutgoingCallSessionManager> {
  private final Provider<CallDao> callDaoProvider;

  public OutgoingCallSessionManager_Factory(Provider<CallDao> callDaoProvider) {
    this.callDaoProvider = callDaoProvider;
  }

  @Override
  public OutgoingCallSessionManager get() {
    return newInstance(callDaoProvider.get());
  }

  public static OutgoingCallSessionManager_Factory create(Provider<CallDao> callDaoProvider) {
    return new OutgoingCallSessionManager_Factory(callDaoProvider);
  }

  public static OutgoingCallSessionManager newInstance(CallDao callDao) {
    return new OutgoingCallSessionManager(callDao);
  }
}

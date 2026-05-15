package com.velstrack.app.domain.telecom;

import android.content.Context;
import com.velstrack.app.core.datastore.SessionManager;
import com.velstrack.app.data.local.dao.CallDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class VerifiedCallExtractor_Factory implements Factory<VerifiedCallExtractor> {
  private final Provider<CallDao> callDaoProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<Context> contextProvider;

  public VerifiedCallExtractor_Factory(Provider<CallDao> callDaoProvider,
      Provider<SessionManager> sessionManagerProvider, Provider<Context> contextProvider) {
    this.callDaoProvider = callDaoProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public VerifiedCallExtractor get() {
    return newInstance(callDaoProvider.get(), sessionManagerProvider.get(), contextProvider.get());
  }

  public static VerifiedCallExtractor_Factory create(Provider<CallDao> callDaoProvider,
      Provider<SessionManager> sessionManagerProvider, Provider<Context> contextProvider) {
    return new VerifiedCallExtractor_Factory(callDaoProvider, sessionManagerProvider, contextProvider);
  }

  public static VerifiedCallExtractor newInstance(CallDao callDao, SessionManager sessionManager,
      Context context) {
    return new VerifiedCallExtractor(callDao, sessionManager, context);
  }
}

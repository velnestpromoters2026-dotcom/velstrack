package com.velstrack.app.presentation.employee.dialer.service;

import com.velstrack.app.core.datastore.SessionManager;
import com.velstrack.app.data.local.dao.CallDao;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class VelstrackInCallService_MembersInjector implements MembersInjector<VelstrackInCallService> {
  private final Provider<CallDao> callDaoProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public VelstrackInCallService_MembersInjector(Provider<CallDao> callDaoProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.callDaoProvider = callDaoProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  public static MembersInjector<VelstrackInCallService> create(Provider<CallDao> callDaoProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new VelstrackInCallService_MembersInjector(callDaoProvider, sessionManagerProvider);
  }

  @Override
  public void injectMembers(VelstrackInCallService instance) {
    injectCallDao(instance, callDaoProvider.get());
    injectSessionManager(instance, sessionManagerProvider.get());
  }

  @InjectedFieldSignature("com.velstrack.app.presentation.employee.dialer.service.VelstrackInCallService.callDao")
  public static void injectCallDao(VelstrackInCallService instance, CallDao callDao) {
    instance.callDao = callDao;
  }

  @InjectedFieldSignature("com.velstrack.app.presentation.employee.dialer.service.VelstrackInCallService.sessionManager")
  public static void injectSessionManager(VelstrackInCallService instance,
      SessionManager sessionManager) {
    instance.sessionManager = sessionManager;
  }
}

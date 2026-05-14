package com.velstrack.app;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class VelstrackApplication_MembersInjector implements MembersInjector<VelstrackApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public VelstrackApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<VelstrackApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new VelstrackApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(VelstrackApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.velstrack.app.VelstrackApplication.workerFactory")
  public static void injectWorkerFactory(VelstrackApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}

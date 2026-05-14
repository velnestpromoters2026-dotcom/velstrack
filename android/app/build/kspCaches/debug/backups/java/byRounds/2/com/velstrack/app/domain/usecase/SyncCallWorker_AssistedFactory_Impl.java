package com.velstrack.app.domain.usecase;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SyncCallWorker_AssistedFactory_Impl implements SyncCallWorker_AssistedFactory {
  private final SyncCallWorker_Factory delegateFactory;

  SyncCallWorker_AssistedFactory_Impl(SyncCallWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public SyncCallWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<SyncCallWorker_AssistedFactory> create(
      SyncCallWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SyncCallWorker_AssistedFactory_Impl(delegateFactory));
  }
}

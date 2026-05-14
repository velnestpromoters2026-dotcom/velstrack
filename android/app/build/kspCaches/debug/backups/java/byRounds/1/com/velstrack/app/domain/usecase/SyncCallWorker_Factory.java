package com.velstrack.app.domain.usecase;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class SyncCallWorker_Factory {
  public SyncCallWorker_Factory() {
  }

  public SyncCallWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams);
  }

  public static SyncCallWorker_Factory create() {
    return new SyncCallWorker_Factory();
  }

  public static SyncCallWorker newInstance(Context appContext, WorkerParameters workerParams) {
    return new SyncCallWorker(appContext, workerParams);
  }
}

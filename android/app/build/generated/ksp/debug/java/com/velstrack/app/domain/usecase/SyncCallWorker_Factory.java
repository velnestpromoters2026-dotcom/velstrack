package com.velstrack.app.domain.usecase;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.velstrack.app.data.local.dao.CallDao;
import com.velstrack.app.data.remote.api.ApiService;
import dagger.internal.DaggerGenerated;
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
public final class SyncCallWorker_Factory {
  private final Provider<CallDao> callDaoProvider;

  private final Provider<ApiService> apiServiceProvider;

  public SyncCallWorker_Factory(Provider<CallDao> callDaoProvider,
      Provider<ApiService> apiServiceProvider) {
    this.callDaoProvider = callDaoProvider;
    this.apiServiceProvider = apiServiceProvider;
  }

  public SyncCallWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, callDaoProvider.get(), apiServiceProvider.get());
  }

  public static SyncCallWorker_Factory create(Provider<CallDao> callDaoProvider,
      Provider<ApiService> apiServiceProvider) {
    return new SyncCallWorker_Factory(callDaoProvider, apiServiceProvider);
  }

  public static SyncCallWorker newInstance(Context appContext, WorkerParameters workerParams,
      CallDao callDao, ApiService apiService) {
    return new SyncCallWorker(appContext, workerParams, callDao, apiService);
  }
}

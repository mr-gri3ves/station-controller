package com.locator.stationcontroller.db;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class StationQueryAsyncTask<Params, Result> extends AsyncTask<Params, Void, Result> {
    @NonNull
    protected StationDao stationDao;

    @Nullable
    protected QueryListener<Result> queryListener;

    public StationQueryAsyncTask(@NonNull StationDao stationDao, @Nullable QueryListener<Result> queryListener) {
        this.stationDao = stationDao;
        this.queryListener = queryListener;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (queryListener != null) {
            queryListener.onResult(result);
        }
    }
}

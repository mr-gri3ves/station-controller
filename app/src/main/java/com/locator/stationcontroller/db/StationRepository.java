package com.locator.stationcontroller.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class StationRepository {
    private static final String TAG = StationRepository.class.getSimpleName();
    private static StationRepository instance;

    @NonNull
    private final StationDao stationDao;

    private StationRepository(@NonNull StationDatabase stationDatabase) {
        this.stationDao = stationDatabase.stationDao();
    }

    public static void init(@NonNull StationDatabase stationDatabase) {
        if (instance != null) {
            Log.e(TAG, "Repository is already initialized");
            return;
        }
        synchronized (StationRepository.class) {
            instance = new StationRepository(stationDatabase);
        }
    }

    public static StationRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException(TAG + " is not initialized");
        }
        return instance;
    }

    public void getAll(@Nullable QueryListener<List<Station>> queryListener) {
        new GetAll(stationDao, queryListener).execute();
    }

    public void getAllByPhone(@NonNull String phone, @Nullable QueryListener<List<Station>> queryListener) {
        new GetAllByPhone(stationDao, queryListener).execute(phone);
    }

    public void insert(@Nullable QueryListener<List<Long>> queryListener,
                       @NonNull Station... stations) {
        new Insert(stationDao, queryListener).execute(stations);
    }

    public void update(@Nullable QueryListener<Integer> queryListener,
                       @NonNull Station... stations) {
        new Update(stationDao, queryListener).execute(stations);
    }

    public void delete(@Nullable QueryListener<Integer> queryListener,
                       @NonNull Station... stations) {
        new Delete(stationDao, queryListener).execute(stations);
    }


    private static final class GetAll extends StationQueryAsyncTask<Void, List<Station>> {

        public GetAll(@NonNull StationDao stationDao, @Nullable QueryListener<List<Station>> queryListener) {
            super(stationDao, queryListener);
        }

        @Override
        protected List<Station> doInBackground(Void... voids) {
            return stationDao.getAll();
        }
    }

    private static final class GetAllByIds extends StationQueryAsyncTask<Long, List<Station>> {

        public GetAllByIds(@NonNull StationDao stationDao, @Nullable QueryListener<List<Station>> queryListener) {
            super(stationDao, queryListener);
        }

        @Override
        protected List<Station> doInBackground(Long... longs) {
            return stationDao.getAllByIds(longs);
        }
    }

    private static final class GetAllByPhone extends StationQueryAsyncTask<String, List<Station>> {

        public GetAllByPhone(@NonNull StationDao stationDao, @Nullable QueryListener<List<Station>> queryListener) {
            super(stationDao, queryListener);
        }

        @Override
        protected List<Station> doInBackground(String... strings) {
            return stationDao.getAllByPhone(strings[0]);
        }
    }

    private static final class Insert extends StationQueryAsyncTask<Station, List<Long>> {

        public Insert(@NonNull StationDao stationDao, @Nullable QueryListener<List<Long>> queryListener) {
            super(stationDao, queryListener);
        }

        @Override
        protected List<Long> doInBackground(Station... stations) {
            return stationDao.insert(stations);
        }
    }

    private static final class Update extends StationQueryAsyncTask<Station, Integer> {

        public Update(@NonNull StationDao stationDao, @Nullable QueryListener<Integer> queryListener) {
            super(stationDao, queryListener);
        }

        @Override
        protected Integer doInBackground(Station... stations) {
            return stationDao.update(stations);
        }
    }

    private static final class Delete extends StationQueryAsyncTask<Station, Integer> {

        public Delete(@NonNull StationDao stationDao, @Nullable QueryListener<Integer> queryListener) {
            super(stationDao, queryListener);
        }

        @Override
        protected Integer doInBackground(Station... stations) {
            return stationDao.delete(stations);
        }
    }
}

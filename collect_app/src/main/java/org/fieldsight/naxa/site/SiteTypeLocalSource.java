package org.fieldsight.naxa.site;


import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

public class SiteTypeLocalSource implements BaseLocalDataSource<SiteType> {

    private static SiteTypeLocalSource INSTANCE;
    private SiteTypeDAO dao;


    private SiteTypeLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSiteTypesDAO();
    }


    public static SiteTypeLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteTypeLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<SiteType>> getAll() {
        return null;
    }

    public LiveData<List<SiteType>> getByProjectId(String projectId) {
        return dao.getByProjectId(projectId);
    }

    @Override
    public void save(SiteType... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<SiteType> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<SiteType> items) {
        dao.updateAll(items);
    }

    public void refreshCache(SiteType... siteTypes) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAll();
                dao.insert(siteTypes);
            }
        });
    }

}

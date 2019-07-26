package org.bcss.collect.naxa.site;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class SiteTypeDAO implements BaseDaoFieldSight<SiteType> {

    @Query("DELETE FROM site_types")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<SiteType> items) {
        deleteAll();
        insert(items);
    }

    @Query("SELECT * FROM site_types WHERE projectId= :projectId")
    public abstract LiveData<List<SiteType>> getByProjectId(String projectId);
}

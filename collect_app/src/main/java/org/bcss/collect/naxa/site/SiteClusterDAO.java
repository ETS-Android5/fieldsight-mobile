package org.bcss.collect.naxa.site;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.site.data.SiteRegion;

import java.util.List;

@Dao
public abstract class SiteClusterDAO implements BaseDaoFieldSight<SiteRegion> {
    @Query("SELECT * FROM site_region WHERE id =:projectId")
    public abstract LiveData<List<SiteRegion>> getByProjectId(String projectId);
}

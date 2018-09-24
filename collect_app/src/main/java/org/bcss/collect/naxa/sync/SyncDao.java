package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.onboarding.SyncableItems;

import java.util.List;
import java.util.Observable;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface SyncDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SyncableItems... items);

    @Query("SELECT * FROM sync")
    LiveData<List<SyncableItems>> getAllSyncableItems();

    @Query("SELECT checked FROM sync WHERE uid=:key")
    Flowable<Boolean> isChecked(int key);

    @Query("SELECT COUNT(*) FROM sync")
    Flowable<Integer> getItemCount();

    @Query("UPDATE sync SET progressStatus=:value WHERE uid=:key")
    void updateProgress(int key, boolean value);

    @Query("UPDATE sync SET checked=:value WHERE uid=:key")
    void updateChecked(int key, boolean value);

    @Query("UPDATE sync SET checked=:value")
    void setAllCheckedTrue(boolean value);

    @Query("UPDATE sync SET lastSyncDateTime=:value WHERE uid=:key")
    void updateDate(int key, String value);

    @Query("UPDATE sync SET downloadingStatus=:status WHERE uid=:key")
    void updateStatus(int key, int status);


    @Query("DELETE FROM sync")
    void deleteAll();

    @Query("UPDATE sync SET checked=:value WHERE uid =:syncItemType")
    void setIsDataOutOfSync(String syncItemType, boolean value);

    @Query("SELECT * from sync WHERE uid=:uid")
    Single<SyncableItems> getById(int uid);

    }

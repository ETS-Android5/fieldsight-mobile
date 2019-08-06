package org.bcss.collect.naxa.previoussubmission;

import androidx.room.Dao;
import androidx.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;

import io.reactivex.Maybe;

@Dao
public abstract class SubmissionDetailDAO implements BaseDaoFieldSight<SubmissionDetail> {
    @Query("DELETE FROM submission_detail")
    public abstract void deleteAll();

    @Query("Select * from submission_detail WHERE siteFsFormId =:fsFormId ORDER BY submissionDateTime DESC limit 1")
    public abstract Maybe<SubmissionDetail> getBySiteFsId(String fsFormId);

    @Query("Select * from submission_detail WHERE projectFsFormId =:fsFormId ORDER BY submissionDateTime DESC limit 1")
    public abstract Maybe<SubmissionDetail> getByProjectFsId(String fsFormId);

}

package org.fieldsight.naxa.v3.network;

public class SyncManager {
    /**
     * @Author: Yubaraj Poudel
     * @Since: 13/05/2019
     *
     *
     */
    String projectId;

    private SyncManager(String projectId){
        this.projectId = projectId;
    }

    public SyncManager instance(String projectId) {
        return new SyncManager(projectId);
    }

    public SyncManager fromRegions(String[] regionIds) {
        return this;
    }

    void syncSites() {

    }

    void syncExams() {

    }

    void syncMaterials() {

    }





}

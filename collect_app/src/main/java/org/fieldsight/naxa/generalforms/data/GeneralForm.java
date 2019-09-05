package org.fieldsight.naxa.generalforms.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.fieldsight.naxa.common.Constant;

import java.util.List;

@Entity(tableName = "general_forms",
        primaryKeys = {"fsFormId", "formDeployedFrom"})
public class GeneralForm {

    @NonNull
    @SerializedName("id")
    @Expose
    private String fsFormId;

    @NonNull
    private String formDeployedFrom;

    @SerializedName("em")
    @Expose
    @Ignore
    private Em em;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id_string")
    @Expose
    private String idString;

    @SerializedName("responses_count")
    @Expose
    private Integer responsesCount;

    @SerializedName("date_created")
    @Expose
    private String dateCreated;

    @SerializedName("date_modified")
    @Expose
    private String dateModified;
    @SerializedName("form_status")

    @Expose
    private Integer formStatus;
    @SerializedName("is_deployed")

    @Expose
    private Boolean isDeployed;

    @SerializedName("from_project")
    private Boolean fromProject;

    @SerializedName("default_submission_status")
    @Expose

    private Integer defaultSubmissionStatus;
    @SerializedName("xf")
    @Expose
    private Integer xf;

    @SerializedName("site")
    @Expose
    private String siteId;
    @SerializedName(value = "project")
    @Expose
    private String projectId;

    @Ignore
    @SerializedName("site_project_id")
    @Expose
    private String siteProjectId;

    @SerializedName("downloadUrl")
    private String downloadUrl;

    @SerializedName("manifestUrl")
    private String manifestUrl;


    @SerializedName("fsform")
    @Expose
    private String fsform;

    private String lastSubmissionBy;
    private String lastSubmissionDateTime;

    @Ignore
    @SerializedName("version")
    private String version;

    @Ignore
    @SerializedName("hash")
    private String hash;

    public String getSiteProjectId() {
        return siteProjectId;
    }

    //@SerializedName("latest_submission")
    @Expose
    @Ignore
    private List<FormResponse> latestSubmission = null;

    @NonNull
    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(@NonNull String fsFormId) {
        this.fsFormId = fsFormId;
    }

    @NonNull
    public String getFormDeployedFrom() {
        return formDeployedFrom;
    }

    public void setFormDeployedFrom(@NonNull String formDeployedFrom) {
        this.formDeployedFrom = formDeployedFrom;
    }

    public String getVersion() {
        return version;
    }

    public String getHash() {
        return hash;
    }

    public Em getEm() {
        return em;
    }

    public void setEm(Em em) {
        this.em = em;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public Integer getResponsesCount() {
        return responsesCount;
    }

    public void setResponsesCount(Integer responsesCount) {
        this.responsesCount = responsesCount;
    }


    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }


    public Integer getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(Integer formStatus) {
        this.formStatus = formStatus;
    }

    public Boolean getDeployed() {
        return isDeployed;
    }

    public void setIsDeployed(Boolean isDeployed) {
        this.isDeployed = isDeployed;
    }


    public Boolean getFromProject() {
        return fromProject;
    }

    public void setFromProject(Boolean fromProject) {
        this.fromProject = fromProject;
    }

    public Integer getDefaultSubmissionStatus() {
        return defaultSubmissionStatus;
    }

    public void setDefaultSubmissionStatus(Integer defaultSubmissionStatus) {
        this.defaultSubmissionStatus = defaultSubmissionStatus;
    }

    public List<FormResponse> getLatestSubmission() {
        return latestSubmission;
    }

    public void setLatestSubmission(List<FormResponse> latestSubmission) {
        this.latestSubmission = latestSubmission;
    }

    public Integer getXf() {
        return xf;
    }

    public void setXf(Integer xf) {
        this.xf = xf;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
        formDeployedFrom = projectId != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
    }

    public String getFsform() {
        return fsform;
    }

    public void setFsform(String fsform) {
        this.fsform = fsform;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getManifestUrl() {
        return manifestUrl;
    }

    public void setManifestUrl(String manifestUrl) {
        this.manifestUrl = manifestUrl;
    }

    public String getLastSubmissionBy() {
        return lastSubmissionBy;
    }

    public void setLastSubmissionBy(String lastSubmissionBy) {
        this.lastSubmissionBy = lastSubmissionBy;
    }

    public String getLastSubmissionDateTime() {
        return lastSubmissionDateTime;
    }

    public void setLastSubmissionDateTime(String lastSubmissionDateTime) {
        this.lastSubmissionDateTime = lastSubmissionDateTime;
    }
}
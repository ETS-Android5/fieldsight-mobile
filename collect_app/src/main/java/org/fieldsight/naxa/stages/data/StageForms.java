
package org.fieldsight.naxa.stages.data;

import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StageForms {

    @SerializedName("xf")
    @Expose
    private Xf xf;
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("downloadUrl")
    private String downloadUrl;

    @SerializedName("manifestUrl")
    private String manifestUrl;

    @Ignore
    @SerializedName("name")
    private String formName;

    @Ignore
    @SerializedName("hash")
    private String hash;

    @Ignore
    @SerializedName("version")
    private String version;


    public String getFormName() {
        return formName;
    }

    public String getHash() {
        return hash;
    }

    public String getVersion() {
        return version;
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

    public Xf getXf() {
        return xf;
    }

    public void setXf(Xf xf) {
        this.xf = xf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

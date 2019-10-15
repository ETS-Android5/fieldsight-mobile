
package org.fieldsight.naxa.previoussubmission.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastSubmissionResponse {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("next")
    @Expose
    private String next;
    @SerializedName("previous")
    @Expose
    private String previous;

    @SerializedName("results")
    @Expose
    private List<SubmissionDetail> submissionDetails;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<SubmissionDetail> getSubmissionDetails() {
        return submissionDetails;
    }

    public void setSubmissionDetails(List<SubmissionDetail> submissionDetails) {
        this.submissionDetails = submissionDetails;
    }

}

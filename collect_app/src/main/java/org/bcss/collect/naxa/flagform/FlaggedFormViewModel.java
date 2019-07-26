package org.bcss.collect.naxa.flagform;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.bcss.collect.naxa.data.FieldSightNotification;

public class FlaggedFormViewModel extends ViewModel {

    private MutableLiveData<FieldSightNotification> notification = new MutableLiveData<>();

    public MutableLiveData<FieldSightNotification> getNotification() {
        return notification;
    }

    public void setNotification(MutableLiveData<FieldSightNotification> notification) {
        this.notification = notification;
    }
}

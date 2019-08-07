package org.fieldsight.naxa.contact;

import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.sync.DownloadableItemLocalSource;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadUID.PROJECT_CONTACTS;

public class ContactRemoteSource implements BaseRemoteDataSource<FieldSightContactModel> {

    private static ContactRemoteSource INSTANCE;


    public static ContactRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContactRemoteSource();
        }
        return INSTANCE;
    }


    @Override
    public void getAll() {
        Disposable d = ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(PROJECT_CONTACTS);
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        DownloadableItemLocalSource.getINSTANCE().markAsRunning(PROJECT_CONTACTS);
                    }
                })
                .subscribeWith(new DisposableObserver<ArrayList<FieldSightContactModel>>() {
                    @Override
                    public void onNext(ArrayList<FieldSightContactModel> fieldSightContactModels) {
                        ContactLocalSource.getInstance().save(fieldSightContactModels);
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(PROJECT_CONTACTS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }
                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(PROJECT_CONTACTS,message);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        DisposableManager.add(d);
    }
}

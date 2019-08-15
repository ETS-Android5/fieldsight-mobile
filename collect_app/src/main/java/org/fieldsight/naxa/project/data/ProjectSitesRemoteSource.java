package org.fieldsight.naxa.project.data;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.common.GSONInstance;
import org.fieldsight.naxa.common.SharedPreferenceUtils;
import org.fieldsight.naxa.common.event.DataSyncEvent;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.fieldsight.naxa.login.model.MeResponse;
import org.fieldsight.naxa.login.model.MySites;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.site.data.SiteRegion;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.fieldsight.naxa.site.db.SiteRemoteSource;
import org.fieldsight.naxa.site.db.SiteRepository;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.sync.DownloadableItemLocalSource;
import org.fieldsight.naxa.sync.SyncRepository;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadUID.PROJECT_SITES;

public class ProjectSitesRemoteSource implements BaseRemoteDataSource<MeResponse> {
    private static ProjectSitesRemoteSource INSTANCE;
    private SiteRepository siteRepository;
    private ProjectLocalSource projectLocalSource;
    private SyncRepository syncRepository;

    public static ProjectSitesRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectSitesRemoteSource();
        }
        return INSTANCE;
    }

    public ProjectSitesRemoteSource() {
        siteRepository = SiteRepository.getInstance(SiteLocalSource.getInstance(), SiteRemoteSource.getInstance());
        projectLocalSource = ProjectLocalSource.getInstance();
        syncRepository = SyncRepository.getInstance();
    }


    private Single<List<Object>> fetchProjectSites() {
        return ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUser()
                .flatMap((Function<MeResponse, ObservableSource<List<MySites>>>) meResponse -> {
                    if (meResponse.getData() == null || !meResponse.getData().getIsSupervisor()) {
                        throw new BadUserException("You have not been assigned as a site supervisor.");
                    }
                    String user = GSONInstance.getInstance().toJson(meResponse.getData());
                    SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_KEY.USER, user);

                    return ServiceGenerator.getRxClient().create(ApiInterface.class).getAssignedSites();
                }).flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(mySites -> {
                    siteRepository.saveSitesAsVerified(mySites.getSite(), mySites.getProject());
                    return mySites.getProject();
                })
                .toList()
                .map((Function<List<Project>, Set<Project>>) projects -> {
                    ArrayList<Project> uniqueList = new ArrayList<>();
                    ArrayList<String> projectIds = new ArrayList<>();
                    for (Project project : projects) {
                        if (!projectIds.contains(project.getId())) {
                            projectIds.add(project.getId());
                            uniqueList.add(project);
                        }
                    }

                    return new HashSet<>(uniqueList);
                })
                .flattenAsObservable(new Function<Set<Project>, Iterable<Project>>() {
                    @Override
                    public Iterable<Project> apply(Set<Project> projects) throws Exception {
                        return projects;
                    }
                })
                .flatMap(new Function<Project, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Project project) throws Exception {
                        ProjectLocalSource.getInstance().save(project);
                        Observable<Project> siteRegionObservable = ServiceGenerator.getRxClient().create(ApiInterface.class)
                                .getRegionsByProjectId(project.getId())
                                .flatMap(new Function<List<SiteRegion>, ObservableSource<Project>>() {
                                    @Override
                                    public ObservableSource<Project> apply(List<SiteRegion> siteRegions) {
                                        siteRegions.add(new SiteRegion("", "Unassigned ", ""));
                                        String value = GSONInstance.getInstance().toJson(siteRegions);
                                        ProjectLocalSource.getInstance().updateSiteClusters(project.getId(), value);
                                        return Observable.just(project);
                                    }
                                });

                        return Observable.concat(siteRegionObservable, Observable.just("demo"));
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Single<List<Object>> fetchProjectAndSites() {
        return ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUser()
                .flatMap(new Function<MeResponse, ObservableSource<MySiteResponse>>() {
                    @Override
                    public ObservableSource<MySiteResponse> apply(MeResponse meResponse) {

                        if (meResponse.getData() == null || !meResponse.getData().getIsSupervisor()) {
                            throw new BadUserException("You have not been assigned as a site supervisor.");
                        }
                        String user = GSONInstance.getInstance().toJson(meResponse.getData());
                        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_KEY.USER, user);

                        return getPageAndNext(APIEndpoint.GET_MY_SITES_v2);

                    }
                })
                .concatMap(new Function<MySiteResponse, Observable<MySiteResponse>>() {
                    @Override
                    public Observable<MySiteResponse> apply(MySiteResponse mySiteResponse) {
                        return Observable.just(mySiteResponse);
                    }
                })
                .map(new Function<MySiteResponse, List<MySites>>() {
                    @Override
                    public List<MySites> apply(MySiteResponse mySiteResponse) {

                        return mySiteResponse.getResult();
                    }
                })

                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(new Function<MySites, Project>() {
                    @Override
                    public Project apply(MySites mySites) {
                        siteRepository.saveSitesAsVerified(mySites.getSite(), mySites.getProject());
                        projectLocalSource.save(mySites.getProject());

                        return mySites.getProject();
                    }
                })
                .toList()
                .map(new Function<List<Project>, Set<Integer>>() {
                    @Override
                    public Set<Integer> apply(List<Project> projects) {
                        Set<Integer> ids = new HashSet<>();
                        for (Project project : projects) {
                            if(ids.contains(Integer.parseInt(project.getId()))){
                                continue;
                            }
                            ids.add(Integer.parseInt(project.getId()));

                        }


                        return new HashSet<Integer>(ids);
                    }
                })
                .toObservable()
                .flatMapIterable(new Function<Set<Integer>, Iterable<Integer>>() {
                    @Override
                    public Iterable<Integer> apply(Set<Integer> integers) throws Exception {
                        return integers;
                    }
                })
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer;
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer id) throws Exception {
                        Observable<Integer> siteRegionObservable = ServiceGenerator.getRxClient().create(ApiInterface.class)
                                .getRegionsByProjectId(String.valueOf(id))
                                .flatMap(new Function<List<SiteRegion>, ObservableSource<Integer>>() {
                                    @Override
                                    public ObservableSource<Integer> apply(List<SiteRegion> siteRegions) {
                                        siteRegions.add(new SiteRegion("", "Unassigned ", ""));
                                        String value = GSONInstance.getInstance().toJson(siteRegions);
                                        ProjectLocalSource.getInstance().updateSiteClusters(String.valueOf(id), value);
                                        return Observable.just(id);
                                    }
                                });

                        return Observable.concat(siteRegionObservable, Observable.just("demo"));
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    private Observable<MySiteResponse> getPageAndNext(String url) {
        return ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getAssignedSites(url)
                .concatMap(new Function<MySiteResponse, ObservableSource<MySiteResponse>>() {
                    @Override
                    public ObservableSource<MySiteResponse> apply(MySiteResponse mySiteResponse) {
                        if (mySiteResponse.getNext() == null) {
                            return Observable.just(mySiteResponse);
                        }

                        return Observable.just(mySiteResponse)
                                .concatWith(getPageAndNext(mySiteResponse.getNext())
                                );
                    }
                });


    }


    @Override
    public void getAll() {
        int uid = PROJECT_SITES;

        Single<List<Object>> observable = fetchProjectAndSites();


        DisposableObserver<List<Object>> dis = observable.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                        DownloadableItemLocalSource.getINSTANCE()
                                .markAsFailed(PROJECT_SITES);
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {

                        ProjectLocalSource.getInstance().deleteAll();
                        SiteLocalSource.getInstance().deleteSyncedSitesAsync();
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_START));
                        DownloadableItemLocalSource.getINSTANCE()
                                .markAsRunning(PROJECT_SITES);
                    }
                })
                .subscribeWith(new DisposableObserver<List<Object>>() {
                    @Override
                    public void onNext(List<Object> objects) {
                        FieldSightNotificationLocalSource.getInstance().markSitesAsRead();
                        DownloadableItemLocalSource.getINSTANCE()
                                .markAsCompleted(PROJECT_SITES);
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
                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(PROJECT_SITES, message);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        DisposableManager.add(dis);

    }


}

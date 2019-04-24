package com.androiddesdecero.rxusoreal.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;

import com.androiddesdecero.rxusoreal.R;
import com.androiddesdecero.rxusoreal.adapter.ContributorAdapter;
import com.androiddesdecero.rxusoreal.api.WebService;
import com.androiddesdecero.rxusoreal.model.Contributor;
import com.androiddesdecero.rxusoreal.model.GitHubRepo;

import java.util.ArrayList;
import java.util.List;

public class RxRetrofitAnidadoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CompositeDisposable compositeDisposable;
    private List<Contributor> contributors;
    private ContributorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_retrofit_anidado);
        setUpView();
        peticionesAnidadesServidorRx();
    }

    private void setUpView() {
        compositeDisposable = new CompositeDisposable();
        contributors = new ArrayList<>();
        adapter = new ContributorAdapter(contributors);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(lim);
        recyclerView.setAdapter(adapter);
    }

    private void peticionesAnidadesServidorRx() {
        compositeDisposable.add(WebService
                .getInstance()
                .createService()
                .getReposForUserRx("JakeWharton")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                /*
                Si un observable nos devuelve como respuesta un Observable<List<Object>>
                pero nosotros en vez de como una lista queremos trabajar independiente
                con cada objeto de la lista, para ello podemos utilizar flatMapIterable
                o flatMap.
                Que nos devuelve en cada caso un objeto Observable por separado.
                 */
                .flatMap(new Function<List<GitHubRepo>, ObservableSource<GitHubRepo>>() {
                    @Override
                    public ObservableSource<GitHubRepo> apply(List<GitHubRepo> gitHubRepos) throws Exception {
                        return Observable.fromIterable(gitHubRepos);
                    }
                })
                /*
                Hacemos un flatMap en que hacemos por cada repositorio una nueva petición al
                servidor para obtener la lista de personas que han contribuido a ese repositirio.
                Es decir que si JakeWharton tiene 30 repositirios, vamos a hacer 30 llamadas al
                servidor y nos devolvera los que contribuyen a cada uno de los 30 repositiros.
                 */
                .flatMap(new Function<GitHubRepo, ObservableSource<List<Contributor>>>() {
                    @Override
                    public ObservableSource<List<Contributor>> apply(GitHubRepo gitHubRepo) throws Exception {
                        return WebService
                                .getInstance()
                                .createService()
                                .getReposContributorsRx("JakeWharton", gitHubRepo.getName())
                                .subscribeOn(Schedulers.io());
                    }
                })
                /*
                En vez de una Lista de Observables, queremos trabajar independiente con cada Observable de
                dicha lista
                 */
                .flatMap(new Function<List<Contributor>, ObservableSource<Contributor>>() {
                    @Override
                    public ObservableSource<Contributor> apply(List<Contributor> contributors) throws Exception {
                        return Observable.fromIterable(contributors);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        e -> {
                            Log.d("TAG1", e.getLogin() + " -> " + e.getContributions());
                            contributors.add(e);
                            adapter.setData(contributors);
                        },
                        error->Log.d("TAG1", error.getMessage())
                )
        );

    }

    private void peticionesAnidadesServidorRxLambda() {
        compositeDisposable.add(WebService
                .getInstance()
                .createService()
                .getReposForUserRx("JakeWharton")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .flatMapIterable(e->e)
                .flatMap(e-> WebService
                        .getInstance()
                        .createService()
                        .getReposContributorsRx("JakeWharton", e.getName())
                        .subscribeOn(Schedulers.io())
                )
                .flatMapIterable(e->e)
                .subscribe(
                        e -> Log.d("TAG1", e.getLogin() + " -> " + e.getContributions())
                )
        );

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}


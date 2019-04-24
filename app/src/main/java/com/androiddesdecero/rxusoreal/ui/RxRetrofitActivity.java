package com.androiddesdecero.rxusoreal.ui;

import android.os.Bundle;
import android.util.Log;

import com.androiddesdecero.rxusoreal.R;
import com.androiddesdecero.rxusoreal.adapter.RepositoryAdapter;
import com.androiddesdecero.rxusoreal.api.WebService;
import com.androiddesdecero.rxusoreal.model.Contributor;
import com.androiddesdecero.rxusoreal.model.GitHubRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import hu.akarnokd.rxjava2.math.MathObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RxRetrofitActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RepositoryAdapter adapter;
    private List<GitHubRepo> gitHubRepos;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_retrofit);
        setUpView();
        //sinRx();
        //conRx();
        //conRxLambda();
        //conRxOrdenarPorRepoInverso();
        //conRxContieneLenguajeProgramacion();
        //conRxContieneLenguajeProgramacionLambda();
        //conRxContieneLenguajeProgramacionLambdaTake();
        //conRxOrdenarPorEstrellas();
        //conRxAverageEstrellas();
        peticionesAnidadesServidorRx();
    }

    private void setUpView() {
        compositeDisposable = new CompositeDisposable();
        gitHubRepos = new ArrayList<>();
        adapter = new RepositoryAdapter(gitHubRepos);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(lim);
        recyclerView.setAdapter(adapter);
    }

    private void sinRx() {
        Call<List<GitHubRepo>> call = WebService
                .getInstance()
                .createService()
                .getReposForUser("JakeWharton");

        call.enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                gitHubRepos = response.body();
                for (int i = 0; i < response.body().size(); i++) {
                    GitHubRepo repo = gitHubRepos.get(i);
                    Log.d("TAG1", "Respositorio: " + i + " Nombre: " + repo.getName());
                }
                adapter.setData(gitHubRepos);
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                Log.d("TAG1", "Error: " + t.getMessage().toString());
            }
        });
    }

    /*
    Lo hacemos con RX, No hay ninguna mejora en este punto.
     */
    private void conRx() {
        compositeDisposable.add(
                WebService
                        .getInstance()
                        .createService()
                        .getReposForUserRx("JakeWharton")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Consumer<List<GitHubRepo>>() {
                                    @Override
                                    public void accept(List<GitHubRepo> gitHubRepos) throws Exception {
                                        for (int i = 0; i < gitHubRepos.size(); i++) {
                                            GitHubRepo repo = gitHubRepos.get(i);
                                            Log.d("TAG1", "Respositorio: " + i + " Nombre: " + repo.getName());
                                        }
                                        adapter.setData(gitHubRepos);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.d("TAG1", "Error: " + throwable.getMessage().toString());
                                    }
                                }
                        )
        );
    }

    private void conRxLambda() {
        compositeDisposable.add(
                WebService
                        .getInstance()
                        .createService()
                        .getReposForUserRx("JakeWharton")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                e -> {
                                    for (int i = 0; i < e.size(); i++) {
                                        GitHubRepo repo = e.get(i);
                                        Log.d("TAG1", "Respositorio: " + i + " Nombre: " + repo.getName());
                                    }
                                    adapter.setData(e);
                                },
                                error -> Log.d("TAG1", "Error: " + error.getMessage().toString())
                        )
        );
    }

    /*
    La programación Funcional Reactiva nos permite Filtar, Transformar (map)
y reducir como vimos con los operadores de RX esos Stremas de datos asyncronos y
convertirlos en otros tipos de datos que nos sirvan mejor a nuestra aplicación.
Es decir la llamada a un webservice nos puede dar uno datos, pero nosotros podemos
filtrarlos, o transformalos o reducirlos tal cual sean necesarios para nuestra app.
     */
    private void conRxOrdenarPorRepoInverso() {
        compositeDisposable.add(
                WebService
                        .getInstance()
                        .createService()
                        .getReposForUserRx("JakeWharton")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        //Map tranforma los items emitidos por un observable aplicando una funcion a cada item.
                        .map(new Function<List<GitHubRepo>, List<GitHubRepo>>() {
                            @Override
                            public List<GitHubRepo> apply(List<GitHubRepo> gitHubRepos) {
                                Collections.sort(gitHubRepos, new Comparator<GitHubRepo>() {
                                    @Override
                                    public int compare(GitHubRepo o1, GitHubRepo o2) {
                                        return o2.getName().compareTo(o1.getName());
                                    }
                                });
                                return gitHubRepos;
                            }
                        })
                        .subscribe(
                                e -> adapter.setData(e),
                                error -> Log.d("TAG1", "Error: " + error.getMessage().toString())
                        )
        );
    }

    private void conRxContieneLenguajeProgramacion() {
        gitHubRepos.clear();
        compositeDisposable.add(
                WebService
                        .getInstance()
                        .createService()
                        .getReposForUserRx("JakeWharton")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
                        .flatMap(new Function<List<GitHubRepo>, ObservableSource<GitHubRepo>>() {
                            @Override
                            public ObservableSource<GitHubRepo> apply(List<GitHubRepo> gitHubRepos) throws Exception {
                                return Observable.fromIterable(gitHubRepos);
                            }
                        })
                        .filter(new Predicate<GitHubRepo>() {
                            @Override
                            public boolean test(GitHubRepo gitHubRepo) throws Exception {
                                if (gitHubRepo.getLanguage() != null && gitHubRepo.getLanguage().equals("Java")) {
                                    return true;
                                }
                                return false;
                            }
                        })
                        .subscribe(
                                e -> {
                                    gitHubRepos.add(e);
                                    adapter.setData(gitHubRepos);
                                },
                                error -> Log.d("TAG1", "Error: " + error.getMessage().toString())
                        )
        );
    }

    private void conRxContieneLenguajeProgramacionLambda() {
        gitHubRepos.clear();
        compositeDisposable.add(
                WebService
                        .getInstance()
                        .createService()
                        .getReposForUserRx("JakeWharton")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
                        .flatMapIterable(e -> e)
                        .filter(e -> e.getLanguage() != null && e.getLanguage().equals("Kotlin"))
                        .subscribe(
                                e -> {
                                    gitHubRepos.add(e);
                                    adapter.setData(gitHubRepos);
                                },
                                error -> Log.d("TAG1", "Error: " + error.getMessage().toString())
                        )
        );
    }

    private void conRxContieneLenguajeProgramacionLambdaTake() {
        gitHubRepos.clear();
        compositeDisposable.add(
                WebService
                        .getInstance()
                        .createService()
                        .getReposForUserRx("JakeWharton")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
                        .flatMapIterable(e -> e)
                        //.take(3)
                        //.elementAt(3)
                        //.first(new GitHubRepo())
                        //.skip(3)
                        .subscribe(
                                e -> {
                                    gitHubRepos.add(e);
                                    adapter.setData(gitHubRepos);
                                },
                                error -> Log.d("TAG1", "Error: " + error.getMessage().toString())
                        )
        );
    }

    private void conRxOrdenarPorEstrellas() {
        compositeDisposable.add(
                WebService
                        .getInstance()
                        .createService()
                        .getReposForUserRx("JakeWharton")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
                        .flatMapIterable(e -> e)
                        .toSortedList((o1, o2) -> o1.getStargazers_count() - o2.getStargazers_count())
                        .subscribe(
                                e -> adapter.setData(e),
                                error -> Log.d("TAG1", "Error: " + error.getMessage().toString())
                        )
        );
    }

    private void conRxAverageEstrellas() {

        Observable<Integer> observable = WebService
                .getInstance()
                .createService()
                .getReposForUserRx("JakeWharton")
                .toObservable()
                .flatMapIterable(e -> e)
                .map(e -> e.getStargazers_count());

        MathObservable.averageDouble(observable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> Log.d("TAG1", "average:" + e),
                        error -> Log.d("TAG1", "averageError:" + error));

        MathObservable.max(observable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> Log.d("TAG1", "max:" + e),
                        error -> Log.d("TAG1", "maxError:" + error));

        MathObservable.min(observable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> Log.d("TAG1", "min:" + e),
                        error -> Log.d("TAG1", "minError:" + error));

    }

    /*
    Vamos a solictar
     */

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
                }).subscribe(
                        e -> Log.d("TAG1", e.getLogin() + " -> " + e.getContributions())
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

package com.androiddesdecero.rxusoreal.api;

import com.androiddesdecero.rxusoreal.model.Contributor;
import com.androiddesdecero.rxusoreal.model.GitHubRepo;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WebServiceApi {

    //Sin RX
    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>> getReposForUser(@Path("user") String user);

    //Con Rx
    //https://api.github.com/users/JakeWharton/repos
    @GET("/users/{user}/repos")
    Single<List<GitHubRepo>> getReposForUserRx(@Path("user") String user);

    //https://api.github.com/repos/JakeWharton/butterknife/contributors
    @GET("repos/{user}/{repo}/contributors")
    Observable<List<Contributor>> getReposContributorsRx(@Path("user") String user, @Path("repo") String repo);
}

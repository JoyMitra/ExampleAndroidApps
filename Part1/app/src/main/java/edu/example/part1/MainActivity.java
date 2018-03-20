package edu.example.part1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import edu.example.part1.constant.Constants;
import edu.example.part1.interfaces.WebService;
import edu.example.part1.pojo.Population;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "Part1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        Schedule an Observer on the io thread
        to retrieve data (json format)
        from a http server
         */
        final io.reactivex.Observable<Call<Population>> observable = io.reactivex.Observable.create(new ObservableOnSubscribe<Call<Population>>() {
            @Override
            public void subscribe(ObservableEmitter<Call<Population>> emitter) throws Exception {
                emitter.onNext(retrieveImageURls());
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        final RecyclerView recyclerView = findViewById(R.id.view);
        /*
        register a subscriber on the main thread
        to get invoked when the server sends a response.
        Log failure message if server fails to send a response
         */
        observable.subscribe(new Observer<Call<Population>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribed called");
            }

            @Override
            public void onNext(Call<Population> populationCall) {
                populationCall.enqueue(new Callback<Population>() {
                    @Override
                    public void onResponse(Call<Population> call, Response<Population> response) {
                        Population population = response.body();
                        final List<Population.WorldPopulation> worldPopulations = population.worldPopulationList;
                        ImagesAdapter imagesAdapter = new ImagesAdapter(getApplicationContext(), worldPopulations);
                        imagesAdapter.setOnItemClickListener(new ImagesAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                String url = worldPopulations.get(position).flag;
                                Intent intent = new Intent(getApplicationContext(), FullImageActivity.class);
                                intent.putExtra("url", url);
                                startActivity(intent);
                            }
                        });
                        recyclerView.setAdapter(imagesAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }

                    @Override
                    public void onFailure(Call<Population> call, Throwable t) {
                        Log.d(TAG, "failure");
                        call.cancel();
                        throw new RuntimeException(t);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "error occurred while retrieving image URLs");
                throw new RuntimeException(e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "URLs retrieved successfully");
            }
        });
    }

    /*
    Retrieves JSON data from a HTTP server using RetroFit
     */
    private Call<Population> retrieveImageURls() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebService service = retrofit.create(WebService.class);
        return service.getPops();
    }
}

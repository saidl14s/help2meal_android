package com.itcg.help2meal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by Belal on 1/23/2018.
 */

public class HomeFragment extends Fragment {

    SliderView sliderView;
    GridView gv_last;
    Vars vars = new Vars();
    ArrayList<LastRecipe> dataLastRecipes;
    String responseData;
    private static LastRecipesAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sliderView = getActivity().findViewById(R.id.new_recipes_slider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderView.setSliderAnimationDuration(2700);
        sliderView.setScrollTimeInSec(5);
        final SliderAdapter adapter = new SliderAdapter(getContext());
        adapter.setCount(4);

        sliderView.setSliderAdapter(adapter);
        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
            }
        });

        loadLastRecipes();
    }

    private void loadLastRecipes(){

        OkHttpClient httpClient = new OkHttpClient();

        gv_last = (GridView) getActivity().findViewById(R.id.gv_last_recipes);


        String urlLastRecipes = vars.URL_SERVER +"api/auth/platillos-last";
        String token_user = Hawk.get("access_token");

        RequestBody formBody = new FormBody.Builder()
                .add("user_token", token_user)
                .build();


        Request request = new Request.Builder()
                .url(urlLastRecipes)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Authorization" , "Bearer " + token_user)
                .post(formBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(vars.TAG, e.getMessage());
                // error
            }

            @Override public void onResponse(Call call, Response response) {
                Log.i(vars.TAG, response.message()+" "+response.body().toString() );
                try{
                    responseData = response.body().string();
                    if(response.isSuccessful()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    LastRecipe[] mcArray = gson.fromJson(responseData, LastRecipe[].class);
                                    List<LastRecipe> prepareListRecipes = Arrays.asList(mcArray);
                                    dataLastRecipes= new ArrayList<>();

                                    for (LastRecipe recipe : prepareListRecipes) {
                                        dataLastRecipes.add(
                                                new LastRecipe(
                                                        recipe.getId(),
                                                        recipe.getUrl_image()
                                                )
                                        );
                                    }
                                    adapter = new LastRecipesAdapter(getActivity(), dataLastRecipes);
                                    gv_last.setAdapter(adapter);
                                    //progressdialog.dismiss();
                                }catch (Exception e){
                                    Log.e(vars.TAG, e.getMessage());
                                }

                            }
                        });

                    } else {
                        //Log.e(vars.TAG, response.message()+" "+response.body().toString() );
                    }
                }catch (Exception e){

                }
            }
        });


        dataLastRecipes = new ArrayList<>();
        gv_last.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return inflater.inflate(R.layout.fragment_home, null);
    }
}

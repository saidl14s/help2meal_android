package com.itcg.help2meal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;
import com.tapadoo.alerter.Alerter;

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

public class PerfilActivity extends AppCompatActivity implements PurchasesUpdatedListener, SkuDetailsResponseListener {

    public Vars vars = new Vars();
    String dataReceived = "";
    ProgressDialog progressdialog;

    EditText et_name, et_mail, et_password,et_suscripcion;

    BillingClient mBillingClient;
    String licenceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        StatusBarUtil.setColor(this,  getColor(R.color.colorVerde),0);

        Tovuti.from(getApplicationContext()).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                // TODO: Handle the connection...
                if(isConnected){
                    progressdialog = new ProgressDialog(PerfilActivity.this);
                    progressdialog.setCancelable(false);
                    progressdialog.setTitle("Recuperando toda tú información...");
                    progressdialog.show();

                    et_name = (EditText) findViewById(R.id.et_name_user);
                    et_mail = (EditText) findViewById(R.id.et_email_user);
                    et_password = (EditText) findViewById(R.id.et_password_user);
                    et_suscripcion = (EditText) findViewById(R.id.et_suscripcion);


                    loadUserData();

                }else{
                    Alerter.create(PerfilActivity.this)
                            .setTitle("Vaya!")
                            .setText("Tenemos un problema con tu conexión a Internet.")
                            .setIcon(R.drawable.alerter_ic_notifications)
                            .setBackgroundColorRes(R.color.colorErrorMaterial)
                            .enableSwipeToDismiss()
                            .show();
                }
            }
        });


    }

    public void updateUserData(View view){
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(true); //change to false
        progressdialog.setTitle("Guardando información...");
        progressdialog.show();

        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/update";
        String token_user = Hawk.get("access_token");

        RequestBody formBody = new FormBody.Builder()
                .add("name", et_name.getText().toString())
                .add("email", et_mail.getText().toString())
                .add("password", et_password.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Authorization" , "Bearer " + token_user)
                .post(formBody)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(vars.TAG, e.getMessage());
                progressdialog.dismiss();
                // error
                Alerter.create(PerfilActivity.this)
                        .setTitle("Ocurrio un error")
                        .setText(""+e.getMessage())
                        .setIcon(R.drawable.alerter_ic_notifications)
                        .setBackgroundColorRes(R.color.colorErrorMaterial)
                        .enableSwipeToDismiss()
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) {
                progressdialog.dismiss();
                Alerter.create(PerfilActivity.this)
                        .setTitle("Perfil actualizado")
                        .setText("")
                        .setIcon(R.drawable.icon_check)
                        .setBackgroundColorRes(R.color.colorAqua)
                        .enableSwipeToDismiss()
                        .show();

            }
        });

    }

    public boolean connectPlayStore(){

        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    Log.e(vars.TAG, "onBillingSetupFinished: " );
                    getShoppingHistory();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.e(vars.TAG, "onBillingServiceDisconnected: " );
            }
        });
        return true;
    }

    private void getShoppingHistory(){
        List<String> skuList = new ArrayList<>();
        skuList.add(vars.SKU_MONTHLY);
        skuList.add(vars.SKU_YEARLY);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        mBillingClient.querySkuDetailsAsync(params.build(),this);
    }


    public void checkLicence(View view){
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(true); //change to false
        progressdialog.setTitle("Validando licencia, espere un momento por favor...");
        progressdialog.show();
        connectPlayStore();

    }

    public void loadUserData(){
        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/user";
        String token_user = Hawk.get("access_token");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Authorization" , "Bearer " + token_user)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(vars.TAG, e.getMessage());
                // error
            }

            @Override public void onResponse(Call call, Response response) {

                try {
                    dataReceived = response.body().string();
                    if(response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    Usuario userData = gson.fromJson(dataReceived, Usuario.class);
                                    progressdialog.dismiss();
                                    et_name.setText(userData.getName());
                                    et_mail.setText(userData.getEmail());
                                    //et_password.setText(userData.getPassword());

                                    Log.e(vars.TAG, dataReceived);
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
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        Log.e(vars.TAG, "" + billingResult.getDebugMessage()+" "+ billingResult.getResponseCode()+" SKUDETAILSCODE");
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK || billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED  ) {
            for (SkuDetails skuDetails : skuDetailsList) {
                String sku = skuDetails.getSku();
                String title = skuDetails.getTitle();
                if (vars.SKU_YEARLY.equals(sku) || vars.SKU_MONTHLY.equals(sku)) {
                    licenceName = title;
                    et_suscripcion.setHint(title);
                    progressdialog.hide();
                    break;
                }
            }
        }
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Log.e("Help2meal"," onPurchasesUpdated  : "+billingResult.getResponseCode()+billingResult.getDebugMessage() );

    }
}

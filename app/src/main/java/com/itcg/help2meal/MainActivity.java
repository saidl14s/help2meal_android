package com.itcg.help2meal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.paris.Paris;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.appcompat.app.AlertDialog.*;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener, SkuDetailsResponseListener, AcknowledgePurchaseResponseListener {

    BillingClient mBillingClient;

    LinearLayout lyt_ctrol_login,
            lyt_ctrol_signup;

    TextView tv_main,
            tv_title,
            tv_subtitle;

    EditText et_email_login,
            et_password_login,
            et_name_signup,
            et_email_signup,
            et_password_signup;

    ImageView iv_logo;
    ProgressDialog progressdialog;


    boolean loginFormVisible = true;

    private boolean validLicense = false;

    public Vars vars = new Vars();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setDarkMode(this);

        Tovuti.from(getApplicationContext()).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                // TODO: Handle the connection...
                if (isConnected) {
                    Hawk.init(getApplicationContext()).build();
                    progressdialog = new ProgressDialog(MainActivity.this);
                    progressdialog.setCancelable(false);
                    progressdialog.setTitle("Comprobando licencia, espera por favor ...");
                    progressdialog.show();
                    connectPlayStore();
                } else {
                    Alerter.create(MainActivity.this)
                            .setTitle("Vaya!")
                            .setText("Parece que no tienes conexión a Internet.")
                            .setBackgroundColorRes(R.color.colorErrorMaterial)
                            .show();
                }
            }
        });

        activateButtons(); // accionar botones



    }

    private void showTutorial(){

        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.btn_activate_signup), "Abrir cuenta", "Si es tú primera vez en esta aplicación haz click aquí, llena tus datos y abre una cuenta.")
                                .outerCircleColor(R.color.colorPrimary)
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.colorPrimary)   // Specify a color for the target circle
                                .titleTextSize(24)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(19)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                .textColor(R.color.white)            // Specify a color for both the title and description text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)                   // Whether to tint the target view's color
                                .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60),
                        TapTarget.forView(findViewById(R.id.btn_activate_login), "Iniciar sesión", "¿Tienes una cuenta? ingresa aquí con tu email y contraseña")
                                .outerCircleColor(R.color.colorPrimary)
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.colorPrimary)   // Specify a color for the target circle
                                .titleTextSize(24)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(19)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                .textColor(R.color.white)            // Specify a color for both the title and description text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)                   // Whether to tint the target view's color
                                .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60),
                        TapTarget.forView(findViewById(R.id.btn_login), "Ir", "Es momento de ir a la pantalla principal.")
                                .outerCircleColor(R.color.colorPrimary)
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.colorPrimary)   // Specify a color for the target circle
                                .titleTextSize(24)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(19)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                .textColor(R.color.white)            // Specify a color for both the title and description text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)                   // Whether to tint the target view's color
                                .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60)
                        )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        Hawk.put("tutorial_login", true);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });
        sequence.start();
    }

    public boolean connectPlayStore(){

        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    Log.e(vars.TAG, "onBillingSetupFinished: " );
                    //getShoppingHistory();
                    getPurchase();
                }else{
                    validLicense = false;
                    progressdialog.dismiss();
                    Alerter.create(MainActivity.this)
                            .setTitle("Vaya!")
                            .setText("Parece que tenemos un problema de comunicación con Play Store. Asegúrate de tener tú software actualizado. ")
                            .setBackgroundColorRes(R.color.colorErrorMaterial)
                            .show();
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                    Log.e(vars.TAG, "onBillingServiceDisconnected: " );

            }
        });
        return true;
    }

    public void loadSuscriptions(View view){
        /**
         * To purchase an Subscription
         */

        List<String> skuList = new ArrayList<> ();
        skuList.add(vars.SKU_MONTHLY);
        skuList.add(vars.SKU_YEARLY);
        skuList.add(vars.SKU_BIANNUAL);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SkuType.SUBS);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_item);
                            for (SkuDetails skuDetails : skuDetailsList) {
                                Log.e(vars.TAG, skuDetails.getTitle() + " "+ skuDetails.getPrice());
                                arrayAdapter.add(skuDetails.getTitle() + " "+ skuDetails.getPrice());
                            }
                            showDialogSuscriptions(arrayAdapter, skuDetailsList);
                        }
                    }
                });

    }

    public void loadSuscriptionsLogin(){

        List<String> skuList = new ArrayList<> ();
        skuList.add(vars.SKU_MONTHLY);
        skuList.add(vars.SKU_YEARLY);
        skuList.add(vars.SKU_BIANNUAL);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SkuType.SUBS);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_item);
                            for (SkuDetails skuDetails : skuDetailsList) {
                                Log.e(vars.TAG, skuDetails.getTitle() + " "+ skuDetails.getPrice());
                                arrayAdapter.add(skuDetails.getTitle() + " "+ skuDetails.getPrice());
                            }
                            showDialogSuscriptions(arrayAdapter, skuDetailsList);
                        }
                    }
                });
    }

    public void changeButtonStyles(){
        Button btn_login, btn_sigup;
        btn_login = (Button) findViewById(R.id.btn_activate_login);
        btn_sigup = (Button) findViewById(R.id.btn_activate_signup);

        if(loginFormVisible){
            Paris.style(btn_login).apply(R.style.btn_green_primary);
            Paris.style(btn_sigup).apply(R.style.btn_blue_borderer);
        }else{
            Paris.style(btn_login).apply(R.style.btn_green_borderer);
            Paris.style(btn_sigup).apply(R.style.btn_blue_primary);
        }
    }

    public void showDialogSuscriptions( ArrayAdapter<String> aa, List<SkuDetails> sd){
        final ArrayAdapter<String> arrayAdapter = aa;
        final List<SkuDetails> skuDetailsList = sd;

        //showSuscriptionsDialog(arrayAdapter, skuDetailsList);
        Builder builderSingle = new Builder(MainActivity.this);
        builderSingle.setTitle("Selecciona la suscripción");


        builderSingle.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for (SkuDetails product : skuDetailsList) {
                    if((product.getTitle() +" "+ product.getPrice()).equals(arrayAdapter.getItem(which))){
                        final BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(product)
                                .build();
                        mBillingClient.launchBillingFlow(MainActivity.this, flowParams);

                    }
                }

            }
        });
        builderSingle.show();
    }


    public void initConfiguration(){
        //connectPlayStore();
        if(Hawk.contains("access_token")){
            if(validLicense){
                String user_mail = Hawk.get("email");
                String user_password = Hawk.get("password");

                OkHttpClient httpClient = new OkHttpClient();
                String url = vars.URL_SERVER +"api/auth/login";

                RequestBody formBody = new FormBody.Builder()
                        .add("email", user_mail)
                        .add("password", user_password)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("X-Requested-With","XMLHttpRequest")
                        .post(formBody)
                        .build();


                httpClient.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        Log.e(vars.TAG, e.getMessage());
                        progressdialog.dismiss();
                        Alerter.create(MainActivity.this)
                                .setTitle("Problema con el servidor.")
                                .setText(e.getMessage())
                                .setBackgroundColorRes(R.color.colorError)
                                .show();


                    }

                    @Override public void onResponse(Call call, Response response) {

                        progressdialog.dismiss();
                        if(response.isSuccessful() && response.message() != "Unauthorized"){
                            try{
                                String i = response.body().string();

                                Gson gson = new Gson();
                                Properties properties = gson.fromJson(i,Properties.class);

                                Hawk.put("access_token", properties.getProperty("access_token"));

                                Intent intent = new Intent(MainActivity.this, HomeTabActivity.class);
                                startActivity(intent);

                                finish(); // delete this activity from stack activities


                            }catch (Exception e){
                                Alerter.create(MainActivity.this)
                                        .setTitle("Error desconocido.")
                                        .setText(e.getMessage())
                                        .setBackgroundColorRes(R.color.colorError)
                                        .show();
                            }
                        } else {
                            Alerter.create(MainActivity.this)
                                    .setTitle("Error")
                                    .setText("Ocurrio un problema con tu usuario y/o contraseña.")
                                    .setBackgroundColorRes(R.color.colorError)
                                    .show();
                        }

                    }
                });

            }else{
                progressdialog.dismiss();
                Alerter.create(MainActivity.this)
                        .setTitle("Ooops...")
                        .setText("Parece que tu membresía termino.")
                        .setBackgroundColorRes(R.color.colorWarningMaterial)
                        .show();
                loadSuscriptionsLogin();
            }
        }else{
            progressdialog.dismiss();
            if(!Hawk.contains("tutorial_login")){
                //showTutorial();
            }

        }
    }

    private void getPurchase(){
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(SkuType.SUBS);
        for (Purchase purchase : purchasesResult.getPurchasesList()) {
            Log.e(vars.TAG, purchase.getPurchaseState()+ " "+purchase.getPurchaseTime() +" "+purchase.getOriginalJson());
            acknowledgePurchase(purchase);
            validLicense = true;
            Alerter.create(MainActivity.this)
                    .setTitle("¡Genial!")
                    .setText("Tu licencia es: "+purchase.getOrderId())
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .show();
            break;
        }

        initConfiguration();

    }

    public void activateButtons(){
        lyt_ctrol_login = (LinearLayout) findViewById(R.id.lyt_ctrol_login);
        lyt_ctrol_signup = (LinearLayout) findViewById(R.id.lyt_ctrol_signup);

        // Login active, register dissabled
        lyt_ctrol_login.setVisibility(View.VISIBLE);
        lyt_ctrol_signup.setVisibility(View.GONE);

        tv_main = (TextView) findViewById(R.id.tv_main);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_subtitle = (TextView) findViewById(R.id.tv_subtitle);

        et_email_login = (EditText) findViewById(R.id.et_email_login);
        et_password_login = (EditText) findViewById(R.id.et_password_login);
        et_name_signup = (EditText) findViewById(R.id.et_name_signup);
        et_email_signup = (EditText) findViewById(R.id.et_email_signup);
        et_password_signup = (EditText) findViewById(R.id.et_password_signup);

        iv_logo = (ImageView) findViewById(R.id.iv_logo);


        final Button btn_activate_login = (Button) findViewById(R.id.btn_activate_login);
        btn_activate_login.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                lyt_ctrol_login.setVisibility(View.VISIBLE);
                lyt_ctrol_signup.setVisibility(View.GONE);
                tv_main.setText("Iniciar sesión");
                tv_title.setPadding(0,0,0,0);
                tv_title.setText("¡Bienvenido!");
                tv_subtitle.setText("Nos encanta verte de nuevo");
                tv_title.setTextColor( getResources().getColor(R.color.colorVerde));
                tv_subtitle.setTextColor( getResources().getColor(R.color.colorVerde));
                iv_logo.setVisibility(View.VISIBLE);
                loginFormVisible = true;
                changeButtonStyles();
            }
        });

        Button btn_activate_signup = (Button) findViewById(R.id.btn_activate_signup);
        btn_activate_signup.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                lyt_ctrol_login.setVisibility(View.GONE);
                lyt_ctrol_signup.setVisibility(View.VISIBLE);
                tv_main.setText("Registro");
                tv_title.setPadding(0,50,0,0);
                tv_title.setText("Empecemos...");
                tv_subtitle.setText("Cuida tu salud mientras te alimentas rico");
                tv_title.setTextColor( getResources().getColor(R.color.colorAzul));
                tv_subtitle.setTextColor( getResources().getColor(R.color.colorAzul));
                iv_logo.setVisibility(View.GONE);
                loginFormVisible = false;
                changeButtonStyles();

            }
        });

        ImageButton btn_login = (ImageButton) findViewById(R.id.btn_login);
        btn_login.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Alerter.create(MainActivity.this)
                        .setTitle("Conectando...")
                        .enableProgress(true)
                        .setBackgroundColorRes(R.color.colorVerde)
                        .show();
                // TODO Auto-generated method stub
                OkHttpClient httpClient = new OkHttpClient();
                String url = vars.URL_SERVER +"api/auth/login";

                RequestBody formBody = new FormBody.Builder()
                        .add("email", et_email_login.getText().toString())
                        .add("password", et_password_login.getText().toString())
                        .build();

                Hawk.put("email", et_email_login.getText().toString());
                Hawk.put("password", et_password_login.getText().toString());


                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("X-Requested-With","XMLHttpRequest")
                        .post(formBody)
                        .build();


                httpClient.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        Log.e(vars.TAG, e.getMessage());
                        Alerter.create(MainActivity.this)
                                .setTitle("Problema con el servidor.")
                                .setText(e.getMessage())
                                .setBackgroundColorRes(R.color.colorError)
                                .show();

                    }

                    @Override public void onResponse(Call call, Response response) {

                        if(response.isSuccessful() && response.message() != "Unauthorized"){
                            try{
                                String i = response.body().string();
                                if(validLicense){
                                    Gson gson = new Gson();
                                    Properties properties = gson.fromJson(i,Properties.class);

                                    Hawk.put("access_token", properties.getProperty("access_token"));

                                    Intent intent = new Intent(MainActivity.this, HomeTabActivity.class);
                                    startActivity(intent);

                                    finish(); // delete this activity from stack activities
                                }else{
                                    Alerter.create(MainActivity.this)
                                            .setTitle("Licencia invalida")
                                            .setText("Es necesario que reinicies tu suscripción.")
                                            .setBackgroundColorRes(R.color.colorError)
                                            .show();
                                    loadSuscriptionsLogin();
                                }


                            }catch (Exception e){
                                Log.e(vars.TAG, e.getMessage());
                            }
                        } else {
                            Alerter.create(MainActivity.this)
                                    .setTitle("Error")
                                    .setText("Ocurrio un problema con tu usuario y/o contraseña.")
                                    .setBackgroundColorRes(R.color.colorError)
                                    .show();
                        }

                    }
                });
            }
        });


        ImageButton btn_signup = (ImageButton) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(validLicense){
                    Alerter.create(MainActivity.this)
                            .setTitle("Conectando...")
                            .enableProgress(true)
                            .setBackgroundColorRes(R.color.colorVerde)
                            .show();
                    // TODO Auto-generated method stub
                    OkHttpClient httpClient = new OkHttpClient();
                    String url = vars.URL_SERVER +"api/auth/signup";

                    RequestBody formBody = new FormBody.Builder()
                            .add("name", et_name_signup.getText().toString())
                            .add("email", et_email_signup.getText().toString())
                            .add("password", et_password_signup.getText().toString())
                            .add("password_confirmation",et_password_signup.getText().toString())
                            .build();

                    Hawk.put("email", et_email_signup.getText().toString());
                    Hawk.put("password", et_password_signup.getText().toString());

                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Content-Type","application/x-www-form-urlencoded")
                            .addHeader("X-Requested-With","XMLHttpRequest")
                            .post(formBody)
                            .build();


                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            Log.e(vars.TAG, e.getMessage());
                            Alerter.create(MainActivity.this)
                                    .setTitle("Problema con el servidor.")
                                    .setText(e.getMessage())
                                    .setBackgroundColorRes(R.color.colorError)
                                    .show();

                        }

                        @Override public void onResponse(Call call, Response response) {

                            if(response.isSuccessful()){
                                try{
                                    String i = response.body().string();

                                    Gson gson = new Gson();
                                    Properties properties = gson.fromJson(i, Properties.class);

                                    Hawk.put("access_token", properties.getProperty("access_token"));

                                    Intent intent = new Intent(MainActivity.this, AlacenaActivity.class);
                                    startActivity(intent);


                                    finish(); // delete this activity from stack activities
                                }catch (Exception e){
                                    Log.e(vars.TAG, e.getMessage());
                                }
                                finish(); // delete this activity from stack activities
                            } else {
                                Alerter.create(MainActivity.this)
                                        .setTitle("Error")
                                        .setText("Intentalo de nuevo más tarde.")
                                        .setBackgroundColorRes(R.color.colorError)
                                        .show();
                                Log.e(vars.TAG, response.message()+" "+response.body().toString() );
                            }

                        }
                    });
                }else{
                    Alerter.create(MainActivity.this)
                            .setTitle("Vaya!")
                            .setText("Parece que aún no eliges una licencia.")
                            .setBackgroundColorRes(R.color.colorWarningMaterial)
                            .show();
                }

            }
        });

    }



    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Log.e("Help2meal",""+billingResult.getResponseCode()+billingResult.getDebugMessage() );
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK || billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            if(!purchases.isEmpty()){
                for(Purchase purchase: purchases) {
                    // When every a new purchase is made
                    // Here we verify our purchase
                    if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                        // Invalid purchase
                        // show error to user
                        Log.i(vars.TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
                        Alerter.create(MainActivity.this)
                                .setTitle("Oh!")
                                .setText("Parece que ocurrio un problema al procesar la compra.")
                                .setBackgroundColorRes(R.color.colorErrorMaterial)
                                .show();
                        return;
                    } else {
                        // purchase is valid
                        // Perform actions
                        validLicense = true;
                        Alerter.create(MainActivity.this)
                                .setTitle("¡Genial!")
                                .setText("Ya tienes una licencia válida.")
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .show();

                    }
                }
            }
        }


    }


    void acknowledgePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.

            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, this);
            }
        }
    }

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(vars.PUBLIC_KEY_PLAYSTORE, signedData, signature);
        } catch (IOException e) {
            Log.e(vars.TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {

        Log.e(vars.TAG, "" + billingResult.getDebugMessage()+" "+ billingResult.getResponseCode()+" SKUDETAILSCODE");

    }

    @Override
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        Log.e(vars.TAG, "onAcknowledgePurchaseResponse::: " + billingResult.getDebugMessage()+" "+ billingResult.getResponseCode()+" SKUDETAILSCODE");
    }
}

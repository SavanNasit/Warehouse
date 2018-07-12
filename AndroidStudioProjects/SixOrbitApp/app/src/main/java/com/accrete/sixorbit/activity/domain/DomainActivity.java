package com.accrete.sixorbit.activity.domain;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.login.LoginActivity;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.R.string.domain;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.version;

public class DomainActivity extends Activity implements View.OnClickListener {
    public static final String PREFS_NAME = "PingBusPrefs";
    public static final String PREFS_SEARCH_HISTORY = "SearchHistory";
    public static final String PREFS_LAST_DOMAIN = "LastDomain";
    ArrayList<String> history = new ArrayList<>();
    ProgressBar progressBar;
    private TextView textViewNext;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private AutoCompleteTextView editTextDomain;
    //  private SharedPreferences settings;
    private String lastUseDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain);
        textViewNext = (TextView) findViewById(R.id.domain_txt_next);
        progressBar = (ProgressBar) findViewById(R.id.domain_progress_bar);
        //  settings = getSharedPreferences(PREFS_NAME, 0);

        //   if (settings != null) {
        Set<String> set = AppPreferences.getSearchHistory(DomainActivity.this, AppUtils.SEARCH_HISTORY);
        //settings.getStringSet(PREFS_SEARCH_HISTORY, null);
        if (set != null) {
            history.addAll(set);
        }
        //}
        setAutoCompleteSource();
        // Set the "Enter" event on the search input
        //String domain ="http://192.168.1.13/rapidkartprocessadminv2";
        final AutoCompleteTextView editTextDomain = (AutoCompleteTextView) findViewById(R.id.domain_editText);

        /*if (settings != null) {
            editTextDomain.setText(settings.getString(PREFS_LAST_DOMAIN, lastUseDomain));
        }*/
        if (AppPreferences.getLastDomain(DomainActivity.this, AppUtils.LAST_DOMAIN) != null)
            editTextDomain.setText(AppPreferences.getLastDomain(DomainActivity.this, AppUtils.LAST_DOMAIN));
        editTextDomain.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addSearchInput(editTextDomain.getText().toString());
                    return true;
                }
                return false;
            }
        });

        textViewNext.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueTurquoiseLight));
        }

        editTextDomain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editTextDomain.setEnabled(false);
                    callgetDomain();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            editTextDomain.setEnabled(true);
                        }
                    }, 3000);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setAutoCompleteSource() {
        editTextDomain = (AutoCompleteTextView) findViewById(R.id.domain_editText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, history.toArray(new String[history.size()]));
        editTextDomain.setAdapter(adapter);
    }

    private void addSearchInput(String input) {
        if (!history.contains(input)) {
            history.add(input);
            setAutoCompleteSource();
        }
    }

    private void savePrefs() {
        // SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        //  SharedPreferences.Editor editor = settings.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(history);
        // editor.putStringSet(PREFS_SEARCH_HISTORY, set);
        // editor.putString(PREFS_LAST_DOMAIN, lastUseDomain);
        //  editor.commit();

        //Global Shared Preferences

        AppPreferences.setLastDomain(DomainActivity.this, AppUtils.LAST_DOMAIN, lastUseDomain);
        AppPreferences.setSearchHistory(DomainActivity.this, AppUtils.SEARCH_HISTORY, set);
    }

    @Override
    public void onClick(View v) {
        textViewNext.setClickable(false);
        Log.e("clickable", "false");
        callgetDomain();
        textViewNext.setClickable(true);
        Log.e("clickable", "true");
    }

    private void callgetDomain() {
        String domain = editTextDomain.getText().toString().trim();
        //String domain ="http://192.168.1.13/rapidkartprocessadminv2";
        Log.d("domain", domain);
        if (domain == null || domain.equals("")) {
            editTextDomain.setError(getString(R.string.enter_domain));
//            showError();
        } else {
            try {
                ApiClient.BASE_URL = "http://" + domain + "/";
                Log.d("domain base Url", ApiClient.BASE_URL);
                ApiClient.changeApiBaseUrl(ApiClient.BASE_URL);
                lastUseDomain = domain;
                AppPreferences.setDomain(DomainActivity.this, AppUtils.DOMAIN, ApiClient.BASE_URL = "http://" + domain + "/");
                // SharedPreferences domainsp = getSharedPreferences(PREFS_NAME, 0);
                // SharedPreferences.Editor editor = domainsp.edit();
                // editor.putString("domain", ApiClient.BASE_URL = "http://" + domain + "/");
                // editor.commit();
                //Global Shared Preferences

                savePrefs();
                if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                    getDomain();
                } else {
                    textViewNext.setEnabled(false);
                    Toast.makeText(this, R.string.not_connected_to_internet, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            textViewNext.setEnabled(true);
                        }
                    }, 3000);
                }

            } catch (IllegalArgumentException ex) {
//                CustomisedToast.error(DomainActivity.this, getString(R.string.invalid_domain), Toast.LENGTH_SHORT).show();
                showError();
                ex.printStackTrace();
            }
        }
    }

    private void showError() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_error_layout);
        dialog.setTitle("Credential error");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(getString(R.string.invalid_domain));


        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void getDomain() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        task = getString(domain);
        Call call = apiService.doGetDomain(version, key, task);
        Log.v("Request", String.valueOf(call));

        //Disable button
        textViewNext.setEnabled(false);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progressStatus);
        //Log.v("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse domain = (ApiResponse) response.body();
                if (domain != null) {
                    if (domain.getSuccess().equals(true)) {
                        Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                        ActivityOptions options =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_in_right);
                        startActivity(intentLogin, options.toBundle());
                        progressBar.setVisibility(View.GONE);
                        //Enable Button again
                        textViewNext.setEnabled(true);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        //Enable Button again
                        textViewNext.setEnabled(true);
                    }
                } else {
//                    CustomisedToast.error(DomainActivity.this, getString(R.string.invalid_domain), Toast.LENGTH_SHORT).show();
                    showError();
                    progressBar.setVisibility(View.GONE);
                    //Enable Button again
                    textViewNext.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                textViewNext.setEnabled(false);
                Toast.makeText(DomainActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        textViewNext.setEnabled(true);
                    }
                }, 3000);


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        lastUseDomain = editTextDomain.getText().toString();
        savePrefs();
    }
}

package com.accrete.sixorbit.activity.collections;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

public class SuccessfulCollectionActivity extends AppCompatActivity {
    private ConstraintLayout rootView;
    private CardView cardViewCollection;
    private CardView cardViewOptions;
    private LinearLayout anotherCollectionLayout;
    private TextView anotherCollectionTextView;
    private LinearLayout myCollectionLayout;
    private TextView myCollectionTextView;
    private LinearLayout myTransactionLayout;
    private TextView myTransactionTextView;
    private TextView amountTextView;
    private TextView dateTextView;
    private TextView txnIdTextView;
    private String txnId, strName, txnTime, txnAmount, tId;
    private TextView fromTextView;
    private TextView nameTextView;
    private LinearLayout undoCollectionLayout;
    private TextView undoCollectionTextView;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;
    private TextView downloadConfirmMessage;
    private TextView downloadTitleMessage;
    private TextView btnYes;
    private TextView btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_collection);
        findViews();
    }

    private void findViews() {
        rootView = (ConstraintLayout) findViewById(0);
        cardViewCollection = (CardView) findViewById(R.id.cardView_collection);
        cardViewOptions = (CardView) findViewById(R.id.cardView_options);
        anotherCollectionLayout = (LinearLayout) findViewById(R.id.another_collection_layout);
        anotherCollectionTextView = (TextView) findViewById(R.id.another_collection_textView);
        myCollectionLayout = (LinearLayout) findViewById(R.id.my_collection_layout);
        myCollectionTextView = (TextView) findViewById(R.id.my_collection_textView);
        myTransactionLayout = (LinearLayout) findViewById(R.id.my_transaction_layout);
        myTransactionTextView = (TextView) findViewById(R.id.my_transaction_textView);
        amountTextView = (TextView) findViewById(R.id.amount_textView);
        dateTextView = (TextView) findViewById(R.id.date_textView);
        txnIdTextView = (TextView) findViewById(R.id.txnId_textView);
        fromTextView = (TextView) findViewById(R.id.from_textView);
        nameTextView = (TextView) findViewById(R.id.name_textView);
        undoCollectionLayout = (LinearLayout) findViewById(R.id.undo_collection_layout);
        undoCollectionTextView = (TextView) findViewById(R.id.undo_collection_textView);

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        anotherCollectionTextView.setTypeface(fontAwesomeFont);
        myCollectionTextView.setTypeface(fontAwesomeFont);
        myTransactionTextView.setTypeface(fontAwesomeFont);
        undoCollectionTextView.setTypeface(fontAwesomeFont);

        //New Collection
        anotherCollectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(SuccessfulCollectionActivity.this,
                        DrawerActivity.class);
                resultIntent.putExtra("manage_collections", "create_collection");
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(resultIntent);
                finish();
            }
        });

        //My Collections
        myCollectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(SuccessfulCollectionActivity.this,
                        DrawerActivity.class);
                resultIntent.putExtra("manage_collections", "view_collections");
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(resultIntent);
                finish();
            }
        });

        //New Transactions
        myTransactionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(SuccessfulCollectionActivity.this,
                        DrawerActivity.class);
                resultIntent.putExtra("manage_collections", "view_transactions");
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(resultIntent);
                finish();
            }
        });

        if (getIntent() != null) {
            if (getIntent().hasExtra("amount")) {
                txnAmount = getIntent().getStringExtra("amount");
            }
            if (getIntent().hasExtra("txn_id")) {
                txnId = getIntent().getStringExtra("txn_id");
            }
            if (getIntent().hasExtra("t_id")) {
                tId = getIntent().getStringExtra("t_id");
            }
            if (getIntent().hasExtra("transaction_time")) {
                txnTime = getIntent().getStringExtra("transaction_time");
            }
            if (getIntent().hasExtra("name")) {
                strName = getIntent().getStringExtra("name");
                if (strName.contains("[balance")) {
                    nameTextView.setText(strName.substring(0, strName.indexOf("[balance")));
                } else if (strName.contains("[Balance")) {
                    nameTextView.setText(strName.substring(0, strName.indexOf("[Balance")));
                } else {
                    nameTextView.setText(strName);
                }
            }

            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
            amountTextView.setText(getString(R.string.Rs) + " " +
                    formatter.format(Constants.roundTwoDecimals
                            (Constants.ParseDouble(txnAmount))));
            txnIdTextView.setText("Transaction ID: " + txnId);

            if (txnTime != null && !txnTime.isEmpty()) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat outputFormat = new SimpleDateFormat("hh:mm aa, dd MMM, yyyy", Locale.US);
                    Date date = simpleDateFormat.parse(txnTime);
                    dateTextView.setText(outputFormat.format(date).toString().trim() + "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        fromTextView.setText("from");

        //Undo Collection
        undoCollectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoCollectionLayout.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        undoCollectionLayout.setEnabled(true);
                    }
                }, 3000);

                if (alertDialog == null || !alertDialog.isShowing()) {
                    undoCollectionDialog(tId);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    public void undoCollectionDialog(String txnId) {
        final View dialogView = View.inflate(SuccessfulCollectionActivity.this,
                R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SuccessfulCollectionActivity.this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        downloadTitleMessage = (TextView) dialogView.findViewById(R.id.title_textView);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        downloadTitleMessage.setText("Undo Collection");
        downloadConfirmMessage.setText("Are you sure to undo this collection?");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling API
                btnYes.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtil.getConnectivityStatusString(SuccessfulCollectionActivity.this)
                        .equals(getString(R.string.not_connected_to_internet))) {
                    undoCollection(txnId, progressBar);
                } else {
                    Toast.makeText(SuccessfulCollectionActivity.this,
                            getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnYes.setEnabled(true);
                    }
                }, 3000);
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    public void undoCollection(String tId, ProgressBar progressBar) {
        try {
            task = getString(R.string.undo_create_collection_task);
            if (AppPreferences.getIsLogin(SuccessfulCollectionActivity.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(SuccessfulCollectionActivity.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(SuccessfulCollectionActivity.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(SuccessfulCollectionActivity.this, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = apiService.undoCollection(version, key, task, userId, accessToken, tId);
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // leadList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {

                        if (apiResponse.getSuccess()) {

                            //TODO
                            finish();

                        }
                        //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(SuccessfulCollectionActivity.this, apiResponse.getMessage());
                        } else {
                            if (SuccessfulCollectionActivity.this != null) {
                                //    Toast.makeText(SuccessfulCollectionActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        hideLoader();

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (SuccessfulCollectionActivity.this != null) {
                        Toast.makeText(SuccessfulCollectionActivity.this, getString(R.string.connect_server_failed),
                                Toast.LENGTH_SHORT).show();
                    }

                    hideLoader();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    private void hideLoader() {
        if (SuccessfulCollectionActivity.this != null) {
            if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}

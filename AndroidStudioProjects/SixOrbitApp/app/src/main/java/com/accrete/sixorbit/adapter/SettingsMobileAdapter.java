package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.OtpMobileFetch;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by poonam on 21/6/17.
 */

public class SettingsMobileAdapter extends RecyclerView.Adapter<SettingsMobileAdapter.MyViewHolder> {
    private String mobileInsert;
    private boolean statusPrimaryAdd = false;
    private boolean statusPrimaryRemove = false;
    private boolean isVerified = false;
    private int positiontoUpdateMobile;
    private UpdateListener updateListener;
    private AlertDialog alertDialog;
    private int mExpandedPosition = -1;
    private Context mContext;
    private List<OtpMobileFetch> otpMobileFetches = new ArrayList<OtpMobileFetch>();
    private SettingsMobileAdapterListener listener;
    private OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
    private DatabaseHandler db;

    public SettingsMobileAdapter(Context mContext, List<OtpMobileFetch> mobiles, SettingsMobileAdapterListener listener, UpdateListener updateListener) {

        this.mContext = mContext;
        this.otpMobileFetches = mobiles;
        this.listener = listener;
        this.updateListener = updateListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_mobiles, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        db = new DatabaseHandler(mContext);
        final OtpMobileFetch mobile = otpMobileFetches.get(position);

        holder.textViewMobile.setText(mobile.getMobile());

        if (otpMobileFetches.get(position).getAumvsid() != null &&
                otpMobileFetches.get(position).getAumvsid().equalsIgnoreCase("2")) {
            //    holder.verifiedImageView.setVisibility(View.VISIBLE);
            //     holder.notVerifiedImageView.setVisibility(View.GONE);
            holder.favouriteImageView.setVisibility(View.VISIBLE);
            holder.notVerifiedImageView.setImageResource(R.drawable.icon_verified);
        } else {
            //   holder.notVerifiedImageView.setVisibility(View.VISIBLE);
            //    holder.verifiedImageView.setVisibility(View.GONE);
            holder.favouriteImageView.setVisibility(View.INVISIBLE);
            holder.notVerifiedImageView.setImageResource(R.drawable.icon_not_verified);
        }

        holder.favouriteImageView.setTag(position);
        holder.favouriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String primaryId = otpMobileFetches.get(position).getPrimaryNo();
                String aumvid = otpMobileFetches.get(position).getAumvid();
                if (!primaryId.equals("1")) {
                    // make  primary
                    openAddPrimaryDialog(aumvid, "1", position, holder.favouriteImageView);
                }
            }
        });

        if (otpMobileFetches.get(position).getPrimaryNo() != null &&
                otpMobileFetches.get(position).getPrimaryNo().equalsIgnoreCase("1")) {
            holder.favouriteImageView.setImageResource(R.drawable.icon_primary);
            holder.favouriteImageView.setVisibility(View.VISIBLE);
        } else {
            holder.favouriteImageView.setImageResource(R.drawable.icon_non_primary);
        }

        holder.notVerifiedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!otpMobileFetches.get(position).getAumvsid().equals("2")) {
                    String aumvid = otpMobileFetches.get(position).getAumvid();
                    mobileInsert = otpMobileFetches.get(position).getMobile();

                    holder.notVerifiedImageView.setEnabled(false);
                    //Call API to send sms
                    if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                        sendOTPToVerifyMobile(aumvid, position);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.notVerifiedImageView.setEnabled(true);
                        }
                    }, 3000);

                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.mobile_already_verified), Toast.LENGTH_SHORT).show();
                }
                /*if (isVerified) {
                    holder.notVerifiedImageView.setImageResource(R.drawable.icon_verified);
                } else {
                    holder.notVerifiedImageView.setImageResource(R.drawable.icon_not_verified);
                }*/
            }
        });

       /* holder.verifiedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mContext.getString(R.string.mobile_already_verified), Toast.LENGTH_SHORT).show();
            }
        });*/

        holder.deleteMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    String mobile = otpMobileFetches.get(position).getMobile();
                    /*if (otpMobileFetches.get(position).getPrimaryNo().equals("1")) {
                        Toast.makeText(mContext, "Please make another primary mobile number first then delete.", Toast.LENGTH_SHORT).show();
                    } else {
                        openDeleteMobileDialog(mobile, position);
                    }*/
                    openDeleteMobileDialog(mobile, position);
                }
            }
        });

        final boolean isExpanded = position == mExpandedPosition;
        holder.linearLayoutContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    mExpandedPosition = isExpanded ? -1 : position;
                    // collapse any currently expanded items
                    if (mExpandedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(mExpandedPosition);
                    }

                    notifyDataSetChanged();
                }
            }
        });

        holder.contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    mExpandedPosition = isExpanded ? -1 : position;
                    // collapse any currently expanded items
                    if (mExpandedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(mExpandedPosition);
                    }

                    notifyDataSetChanged();
                }
            }
        });
        holder.editMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    String aumvid = otpMobileFetches.get(position).getAumvid();
                    positiontoUpdateMobile = position;
                    openEditMobileDialog(aumvid, position);
                }
            }
        });

        if (position == 0) {
            holder.verifiedImageView.setVisibility(View.GONE);
            holder.notVerifiedImageView.setVisibility(View.GONE);
            //holder.favouriteImageView.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return otpMobileFetches.size();
    }


    private void addPrimaryStatusMobile(final String aumvid, final String statusId, final int positionToUpdate,
                                        final AlertDialog alertDialog, final ImageView favoriteImageView) {

        task = mContext.getString(R.string.primary_status_mobile);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.primaryMobile(version, key, task, userId, accessToken, aumvid, statusId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
                    otpMobileFetch.setAumvid(aumvid);
                    otpMobileFetch.setPrimaryNo(statusId);
                    db.updatePrimaryStatusMobile(otpMobileFetch);

                    //Remove other
                    otpMobileFetch.setPrimaryNo("0");
                    db.removeOthersPrimaryStatusMobile(otpMobileFetch);

                    updateListener.updateMobileRow(positionToUpdate);

                    //Update Image of favorite
                    if (statusPrimaryAdd) {
                        favoriteImageView.setImageResource(R.drawable.icon_primary);
                    } else {
                        favoriteImageView.setImageResource(R.drawable.icon_non_primary);
                    }

                    statusPrimaryAdd = true;
                    notifyDataSetChanged();
                    alertDialog.dismiss();

                    //Redirect to Main Activity

                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials((Activity) mContext, apiResponse.getMessage());
                } else {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });

    }

    private void removeStatusMobile(final String aumvid, final String statusId, final int positionToUpdate, final AlertDialog alertDialog) {

        task = mContext.getString(R.string.primary_status_mobile);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.primaryMobile(version, key, task, userId, accessToken, aumvid, statusId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, mContext.getString(R.string.changes_done), Toast.LENGTH_SHORT).show();
                    // db.updatePrimaryStatusMobile(statusId, aumvid);
                    //  db.removePrimaryStatusMobile("0", aumvid);
                    updateListener.updateMobileRow(positionToUpdate);
                    statusPrimaryRemove = true;
                    notifyDataSetChanged();
                    alertDialog.dismiss();


                    //Redirect to Main Activity

                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials((Activity) mContext, apiResponse.getMessage());
                } else if (apiResponse.getSuccess().equals("false")) {
                    statusPrimaryRemove = false;
                } else {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });


    }

    public void sendOTPToVerifyMobile(final String mobileVerifyId, final int positionToUpdate) {
        task = mContext.getString(R.string.send_mobile_otp);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.sendMobileOTPToVerifyMobile(version, key, task, accessToken, userId, "1", "2", mobileVerifyId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, mContext.getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();

                    //Redirect to Main Activity
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        openMobileDialog(mobileVerifyId, positionToUpdate);
                    }

                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials((Activity) mContext, apiResponse.getMessage());
                } else {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    public void openMobileDialog(final String mobileVerifyId, final int positionToUpdate) {
        View dialogView = View.inflate(mContext, R.layout.dialog_verify, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView;
        final EditText editTextOtp;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        editTextOtp = (EditText) dialogView.findViewById(R.id.edit_text_otp);
        editTextOtp.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        titleTextView.setText(mContext.getString(R.string.title_verify_phone));
        final Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_verify);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonCancel.setVisibility(View.GONE);
        final TextView textViewResend = (TextView) dialogView.findViewById(R.id.btn_resend);
        textViewResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewResend.setEnabled(false);
                // alertDialog.dismiss();
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    sendOTPToVerifyMobile(mobileVerifyId, positionToUpdate);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewResend.setEnabled(true);
                    }
                }, 3000);
                editTextOtp.setText("");
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAdd.setEnabled(false);
                if (editTextOtp.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, mContext.getString(R.string.title_verify_phone), Toast.LENGTH_SHORT).show();
                } else {
                    if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                        verifyMobileOTP(mobileVerifyId, editTextOtp.getText().toString().trim(), positionToUpdate, alertDialog);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonAdd.setEnabled(true);
                    }
                }, 3000);
                editTextOtp.setText("");
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    private void verifyMobileOTP(final String mobileVerifyId, String mobileOtp, final int positionToUpdate, final AlertDialog alertDialog) {
        task = mContext.getString(R.string.otp_verify_confirm);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
//        progressBar.setMax(100);
//        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.confirmVerifyPhoneOTP(version, key, task, accessToken, userId, "1", "2", mobileVerifyId, mobileOtp);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    isVerified = true;
                    otpMobileFetch.setMobile(mobileInsert);
                    otpMobileFetch.setAumvsid("2");
                    otpMobileFetch.setAumvid(mobileVerifyId);
                    db.updateVerifyStatusMobile(otpMobileFetch);
                    updateListener.updateMobileRow(positionToUpdate);
                    notifyDataSetChanged();

                    Toast.makeText(mContext, mContext.getString(R.string.otp_verified), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials((Activity) mContext, apiResponse.getMessage());
                } else {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    private void openAddPrimaryDialog(final String aumvid, final String statusPrimaryRemove, final int positionToUpdate,
                                      final ImageView imageView) {
        View dialogView = View.inflate(mContext, R.layout.dialog_primary, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView, title;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        title = (TextView) dialogView.findViewById(R.id.primary_text);
        titleTextView.setText(mContext.getString(R.string.title_primary_mobile_number));
        title.setText(mContext.getString(R.string.primary_status));
        final Button buttonOk = (Button) dialogView.findViewById(R.id.btn_ok);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOk.setEnabled(false);
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    addPrimaryStatusMobile(aumvid, statusPrimaryRemove, positionToUpdate, alertDialog, imageView);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonOk.setEnabled(true);
                    }
                }, 3000);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }


    private void openRemovePrimaryDialog(final String aumvid, final String statusPrimaryRemove, final int positiontoUpdate) {

        View dialogView = View.inflate(mContext, R.layout.dialog_primary, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView, title;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        title = (TextView) dialogView.findViewById(R.id.primary_text);
        titleTextView.setText(mContext.getString(R.string.title_non_primary_mobile_number));
        title.setText(mContext.getString(R.string.primary_status));
        Button buttonOk = (Button) dialogView.findViewById(R.id.btn_ok);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeStatusMobile(aumvid, statusPrimaryRemove, positiontoUpdate, alertDialog);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    private void openDeleteMobileDialog(final String mobile, final int positionToUpdate) {

        View dialogView = View.inflate(mContext, R.layout.dialog_delete, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView, title;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        title = (TextView) dialogView.findViewById(R.id.delete_text);
        titleTextView.setText(mContext.getString(R.string.title_delete_mobile));
        title.setText(mContext.getString(R.string.main_title_delete_mobile));
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.btn_delete);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonDelete.setEnabled(false);
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    deleteMobile(mobile, positionToUpdate, alertDialog);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonDelete.setEnabled(true);
                    }
                }, 3000);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    private void deleteMobile(final String mobile, final int positionToUpdate, final AlertDialog alertDialog) {

        task = mContext.getString(R.string.delete_mobile);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.deleteMobile(version, key, task, userId, accessToken, mobile);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, mContext.getString(R.string.mobile_deleted), Toast.LENGTH_SHORT).show();
                    db.deleteOtpMobile(mobile);
                    updateListener.updateMobileRow(positionToUpdate);
                    alertDialog.dismiss();
                    notifyDataSetChanged();
                    //Redirect to Main Activity

                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials((Activity) mContext, apiResponse.getMessage());
                } else {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    private void openEditMobileDialog(final String aumvid, final int positionToUpdate) {
        View dialogView = View.inflate(mContext, R.layout.dialog_edit_mobile, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView;
        final EditText mobileEditText;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        mobileEditText = (EditText) dialogView.findViewById(R.id.mobile);
//        titleTextView.setText(mContext.getString(R.string.title_verify_phone));
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.btn_update);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonUpdate.setEnabled(false);
                if (mobileEditText.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, "Please enter mobile number.", Toast.LENGTH_SHORT).show();
                } else if (mobileEditText.getText().toString().trim().length() != 10) {
                    Toast.makeText(mContext, "Mobile number should be of 10 digits.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                        editMobile(mobileEditText.getText().toString(), aumvid, positionToUpdate, alertDialog);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonUpdate.setEnabled(true);
                    }
                }, 3000);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    private void editMobile(final String mobile, final String aumvid, final int positionToUpdate, final AlertDialog alertDialog) {

        task = mContext.getString(R.string.edit_mobile);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.editMobile(version, key, task, userId, accessToken, mobile, aumvid);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, mContext.getString(R.string.mobile_edited), Toast.LENGTH_SHORT).show();
                    String verifyStatusId = "1";
                    db.updateEditedMobile(mobile, aumvid, verifyStatusId);
                    updateListener.updateMobileRow(positionToUpdate);
                    // notifyDataSetChanged();
                    alertDialog.dismiss();
                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials((Activity) mContext, apiResponse.getMessage());
                } else {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    public interface SettingsMobileAdapterListener {
        void onMessageRowClicked(int position);
    }

    public interface UpdateListener {
        void updateMobileRow(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMobile, editMobile, deleteMobile;
        public ImageView verifiedImageView, notVerifiedImageView, favouriteImageView;
        ProgressBar progressBar;
        private LinearLayout linearLayoutContainer, contentContainer;

        public MyViewHolder(View view) {
            super(view);
            verifiedImageView = (ImageView) view.findViewById(R.id.verified);
            notVerifiedImageView = (ImageView) view.findViewById(R.id.not_verified);
            textViewMobile = (TextView) view.findViewById(R.id.user_mobile);
            favouriteImageView = (ImageView) view.findViewById(R.id.favourite);
            editMobile = (TextView) view.findViewById(R.id.edit_phone);
            deleteMobile = (TextView) view.findViewById(R.id.delete_phone);

            progressBar = (ProgressBar) view.findViewById(R.id.settings_progress_bar);
            contentContainer = (LinearLayout) view.findViewById(R.id.message_container);
            linearLayoutContainer = (LinearLayout) view.findViewById(R.id.linear_layout_container);

        }
    }

}
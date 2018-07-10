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
import com.accrete.sixorbit.model.OtpEmailFetch;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.google.gson.GsonBuilder;

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

public class SettingsEmailAdapter extends RecyclerView.Adapter<SettingsEmailAdapter.MyViewHolder> {
    public OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
    private boolean isVerified = false;
    private boolean statusPrimaryAdd = false;
    private boolean statusPrimaryRemove = false;
    private int positiontoUpdate;
    private String emailInsert;
    private EmailValidator emailValidator;
    private Boolean flagToOpenVerifyDialogEmail;
    private AlertDialog alertDialog;
    private int mExpandedPosition = -1;
    private Context mContext;
    private List<OtpEmailFetch> otpEmailFetches;
    private SettingsEmailAdapterListener listener;
    private UpdateListener updateListener;
    private DatabaseHandler db;
    private Button buttonDelete;
    private ProgressBar progressBar;
    private int progressStatus = 0;

    public SettingsEmailAdapter(Context mContext, List<OtpEmailFetch> emails, SettingsEmailAdapterListener listener,
                                UpdateListener updateListener) {
        this.mContext = mContext;
        this.otpEmailFetches = emails;
        this.listener = listener;
        this.updateListener = updateListener;
        this.flagToOpenVerifyDialogEmail = flagToOpenVerifyDialogEmail;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_emails, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        db = new DatabaseHandler(mContext);
        final OtpEmailFetch email = otpEmailFetches.get(position);
        holder.textViewEmail.setText(email.getEmail());

        if (otpEmailFetches.get(position).getAuevsid() != null &&
                otpEmailFetches.get(position).getAuevsid().equalsIgnoreCase("2")) {
            //holder.verifiedImageView.setVisibility(View.VISIBLE);
            // holder.notVerifiedImageView.setVisibility(View.GONE);
            holder.favouriteImageView.setVisibility(View.VISIBLE);
            holder.notVerifiedImageView.setImageResource(R.drawable.icon_verified);
        } else {
            // holder.notVerifiedImageView.setVisibility(View.VISIBLE);
            // holder.verifiedImageView.setVisibility(View.GONE);
            holder.favouriteImageView.setVisibility(View.INVISIBLE);
            holder.notVerifiedImageView.setImageResource(R.drawable.icon_not_verified);
        }
//

        holder.favouriteImageView.setTag(position);
        holder.favouriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String primaryId = otpEmailFetches.get(position).getPrimaryEmail();
                String auevid = otpEmailFetches.get(position).getAuevid();

                if (!primaryId.equals("1")) {
                    //                    make non primary
                    /*openRemovePrimaryDialog(auevid, "0", position);
                    if (statusPrimaryRemove) {
                        holder.favouriteImageView.setImageResource(R.drawable.icon_non_primary);
                    } else {
                        holder.favouriteImageView.setImageResource(R.drawable.icon_primary);
                    }

                } else {*/
                    openAddPrimaryDialog(auevid, "1", position, holder.favouriteImageView);

                }
            }
        });

        if (otpEmailFetches.get(position).getPrimaryEmail() != null &&
                otpEmailFetches.get(position).getPrimaryEmail().equalsIgnoreCase("1")) {
            holder.favouriteImageView.setImageResource(R.drawable.icon_primary);
            holder.favouriteImageView.setVisibility(View.VISIBLE);
        } else {
            holder.favouriteImageView.setImageResource(R.drawable.icon_non_primary);
        }


        holder.notVerifiedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    if (!otpEmailFetches.get(position).getAuevsid().equals("2")) {
                        String auevid = otpEmailFetches.get(position).getAuevid();
                        emailInsert = otpEmailFetches.get(position).getEmail();
                        positiontoUpdate = position;

                        holder.notVerifiedImageView.setEnabled(false);
                        //Call API to send sms
                        if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                            sendOTPToVerifyEmail(auevid, position, holder.notVerifiedImageView);
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.notVerifiedImageView.setEnabled(true);
                            }
                        }, 3000);

                /*if (isVerified) {
                    holder.notVerifiedImageView.setImageResource(R.drawable.icon_verified);
                } else {
                    holder.notVerifiedImageView.setImageResource(R.drawable.icon_not_verified);
                }*/
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.email_already_verified), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        /*holder.verifiedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mContext.getString(R.string.email_already_verified), Toast.LENGTH_SHORT).show();
            }
        });*/

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
        holder.editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
//                String email = otpEmailFetches.get(position).getEmail();
                    String auevid = otpEmailFetches.get(position).getAuevid();
                    openEditEmailDialog(auevid, position, otpEmailFetches.get(position).getPrimaryEmail());
                }
            }
        });

        holder.deleteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    String email = otpEmailFetches.get(position).getEmail();
                    String auevid = otpEmailFetches.get(position).getAuevid();
                    /*if (otpEmailFetches.get(position).getPrimaryEmail().equals("1")) {
                        Toast.makeText(mContext, "Please make another primary email address first then delete.", Toast.LENGTH_SHORT).show();
                    } else {
                        openDeleteEmailDialog(email, auevid, position);
                    }*/
                    openDeleteEmailDialog(email, auevid, position);
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

        if (position == 0) {
            holder.verifiedImageView.setVisibility(View.GONE);
            holder.notVerifiedImageView.setVisibility(View.GONE);
            // holder.favouriteImageView.setVisibility(View.GONE);
        }

    }

    private void openAddPrimaryDialog(final String aumvid, final String statusPrimaryAdd, final int positiontoUpdate,
                                      final ImageView favouriteImageView) {

        View dialogView = View.inflate(mContext, R.layout.dialog_primary, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView, title;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        title = (TextView) dialogView.findViewById(R.id.primary_text);
        if (db.getAllTwoStepVerificationPrimaryEmails(userId, "1") != null &&
                db.getAllTwoStepVerificationPrimaryEmails(userId, "1").size() > 0) {
            titleTextView.setText(mContext.getString(R.string.title_change_primary_email));
        } else {
            titleTextView.setText(mContext.getString(R.string.title_make_primary_email));
        }
        title.setText("Primary Email");
        final Button buttonOk = (Button) dialogView.findViewById(R.id.btn_ok);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOk.setEnabled(false);
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    addPrimaryStatusEmail(aumvid, statusPrimaryAdd, alertDialog, positiontoUpdate, favouriteImageView);
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
        titleTextView.setText(mContext.getString(R.string.title_non_primary));
        title.setText("Primary Email");
        final Button buttonOk = (Button) dialogView.findViewById(R.id.btn_ok);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOk.setEnabled(false);
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    removeStatusEmail(aumvid, statusPrimaryRemove, positiontoUpdate, alertDialog);
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

    private void addPrimaryStatusEmail(final String auevid, final String statusId, final AlertDialog alertDialog,
                                       final int positiontoUpdate, final ImageView favouriteImageView) {

        task = mContext.getString(R.string.primary_status_email);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.primaryEmail(version, key, task, userId, accessToken, auevid, statusId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
                    otpEmailFetch.setAuevid(auevid);
                    otpEmailFetch.setPrimaryEmail(statusId);
                    db.updatePrimaryStatusEmail(otpEmailFetch);

                    //Remove other
                    otpEmailFetch.setPrimaryEmail("0");
                    db.removeOthersPrimaryStatusEmail(otpEmailFetch);

                    if (statusPrimaryAdd) {
                        favouriteImageView.setImageResource(R.drawable.icon_primary);
                    } else {
                        favouriteImageView.setImageResource(R.drawable.icon_non_primary);
                    }

                    updateListener.updateRow(positiontoUpdate);
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

    private void removeStatusEmail(final String auevid, final String statusId, final int positiontoUpdate, final AlertDialog alertDialog) {

        task = mContext.getString(R.string.primary_status_email);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.primaryEmail(version, key, task, userId, accessToken, auevid, statusId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
                    otpEmailFetch.setAuevid(auevid);
                    otpEmailFetch.setPrimaryEmail(statusId);
                    db.updatePrimaryStatusEmail(otpEmailFetch);
                    updateListener.updateRow(positiontoUpdate);
                    notifyDataSetChanged();
                    statusPrimaryRemove = true;
                    alertDialog.dismiss();
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

    private void openDeleteEmailDialog(final String email, final String auevid, final int positiontoUpdate) {

        View dialogView = View.inflate(mContext, R.layout.dialog_delete, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
//        titleTextView.setText(mContext.getString(R.string.title_verify_phone));
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_delete_progress_bar);
        buttonDelete = (Button) dialogView.findViewById(R.id.btn_delete);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    deleteEmail(email, auevid, positiontoUpdate, alertDialog);
                } else {
                    buttonDelete.setEnabled(false);
                    Toast.makeText(mContext, R.string.not_connected_to_internet, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttonDelete.setEnabled(true);
                        }
                    }, 3000);

                }
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

    private void deleteEmail(final String email, final String emailId, final int positiontoUpdate, final AlertDialog alertDialog) {
        task = mContext.getString(R.string.delete_email);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        buttonDelete.setEnabled(false);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progressStatus);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.deleteEmail(version, key, task, userId, accessToken, email);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    db.deleteOtpEmail(emailId);
                    updateListener.updateRow(positiontoUpdate);
                    notifyDataSetChanged();
                    alertDialog.dismiss();

                    progressBar.setVisibility(View.GONE);
                    //Enable Button again
                    buttonDelete.setEnabled(true);
                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    progressBar.setVisibility(View.GONE);
                    buttonDelete.setEnabled(true);
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

    private void openEditEmailDialog(final String auevid, final int positionUpdate, final String primaryStatus) {

        View dialogView = View.inflate(mContext, R.layout.dialog_edit, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView;
        final EditText emailEditText;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        emailEditText = (EditText) dialogView.findViewById(R.id.email);
//        titleTextView.setText(mContext.getString(R.string.title_verify_phone));
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.btn_update);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidator = new EmailValidator();
                buttonUpdate.setEnabled(false);
                if (!emailValidator.validateEmail(emailEditText.getText().toString())) {
                    //emailEditText.setError(mContext.getString(R.string.valid_email_error));
                    Toast.makeText(mContext, mContext.getString(R.string.valid_email_error), Toast.LENGTH_SHORT).show();
                } else if (db.checkEmailAddress(emailEditText.getText().toString().trim())) {
                    Toast.makeText(mContext, "This email address is already added.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                        editEmail(emailEditText.getText().toString().trim(), auevid, alertDialog, positionUpdate, primaryStatus);
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

    private void editEmail(final String email, final String auevid, final AlertDialog alertDialog, final int positionUpdate,
                           final String primaryStatus) {
        task = mContext.getString(R.string.edit_email);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.editEmail(version, key, task, userId, accessToken, email, auevid);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    String verifyStatusId = "1";
                    if (primaryStatus.equals("1")) {
                        OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
                        otpEmailFetch.setAuevid(auevid);
                        otpEmailFetch.setPrimaryEmail("0");
                        db.updatePrimaryStatusEmail(otpEmailFetch);
                    }
                    db.updateEditedEmail(email, auevid, verifyStatusId);
                    alertDialog.dismiss();
                    updateListener.updatedEmail(positionUpdate, auevid, email);
                    //notifyDataSetChanged();
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

    private void applyClickEvents(MyViewHolder holder, final int position, final String id) {
        holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return otpEmailFetches.size();
    }


    public void sendOTPToVerifyEmail(final String emailVerifyId, final int positiontoUpdate, final ImageView imageView) {
        task = mContext.getString(R.string.send_mobile_otp);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.sendMobileOTPToVerifyEmail(version, key, task, accessToken, userId, "1", "1", emailVerifyId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(mContext, mContext.getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
//                    alertDialog.dismiss();
                    //Redirect to Main Activity
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        openMobileDialog(emailVerifyId, positiontoUpdate, imageView);
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

    public void openMobileDialog(final String emailVerifyId, final int positiontoUpdate, final ImageView imageView) {
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
        titleTextView.setText(mContext.getString(R.string.title_verify_email));
        final Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_verify);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonCancel.setVisibility(View.GONE);
        final TextView textViewResend = (TextView) dialogView.findViewById(R.id.btn_resend);
        textViewResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewResend.setEnabled(false);
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    sendOTPToVerifyEmail(emailVerifyId, positiontoUpdate, imageView);
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
                //alertDialog.dismiss();
                if (editTextOtp.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, mContext.getString(R.string.title_verify_email), Toast.LENGTH_SHORT).show();
                } else if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    verifyEmailOTP(emailVerifyId, editTextOtp.getText().toString().trim(), positiontoUpdate, alertDialog,
                            imageView);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    private void verifyEmailOTP(final String emailVerifyId, String mobileOtp, final int positiontoUpdate,
                                final AlertDialog alertDialog, final ImageView imageView) {
        task = mContext.getString(R.string.otp_verify_confirm);
        userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
//        progressBar.setMax(100);
//        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.confirmVerifyEmailOTP(version, key, task, accessToken, userId, "1", "1", emailVerifyId, mobileOtp);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    isVerified = true;
                    otpEmailFetch.setAuevsid("2");
                    otpEmailFetch.setEmail(emailInsert);
                    otpEmailFetch.setAuevid(emailVerifyId);
                    db.updateEmailVerifyStatus(otpEmailFetch);
                    updateListener.updateRow(positiontoUpdate);
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                    notifyDataSetChanged();

                    Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);

                    imageView.setImageResource(R.drawable.icon_verified);
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


    public interface SettingsEmailAdapterListener {
        void onMessageRowClicked(int position);
    }

    public interface UpdateListener {
        void updateRow(int position);

        void updatedEmail(int position, String aueVid, String email);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEmail, editEmail, deleteEmail;
        public ImageView verifiedImageView, notVerifiedImageView, favouriteImageView;
        ProgressBar progressBar;
        private LinearLayout linearLayoutContainer, contentContainer;

        public MyViewHolder(View view) {
            super(view);
            verifiedImageView = (ImageView) view.findViewById(R.id.verified);
            notVerifiedImageView = (ImageView) view.findViewById(R.id.not_verified);
            favouriteImageView = (ImageView) view.findViewById(R.id.favourite);
            textViewEmail = (TextView) view.findViewById(R.id.user_email);
            editEmail = (TextView) view.findViewById(R.id.edit_email);
            deleteEmail = (TextView) view.findViewById(R.id.delete_email);
            contentContainer = (LinearLayout) view.findViewById(R.id.message_container);
            linearLayoutContainer = (LinearLayout) view.findViewById(R.id.linear_layout_container);
            favouriteImageView.setTag(this);
        }
    }

}
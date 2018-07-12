package com.accrete.sixorbit.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by agt on 1/9/17.
 */

public class ChatContactsService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static final String TAG = "ChatContactsService";
    static Bitmap theBitmap;
    ResultReceiver receiver;
    Bundle bundle = new Bundle();
    File mypath;
    // private SharedPreferences settings, sharedPreferences;
    //private String MyPREFERENCES = "MyPrefs";
    private DatabaseHandler db;


    /**
     * Creates an IntentService.Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    public ChatContactsService(String name) {
        super(name);
    }

    public ChatContactsService() {
        super(ChatContactsService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //   Log.d(TAG, "Api call service is  running....");

        //  Log.d(TAG, "Service Started!");
        receiver = intent.getParcelableExtra(getString(R.string.receiver));
        syncContacts();
    }

    private void syncContacts() {
        isDbSynced();
    }

    private void isDbSynced() {
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        db = new DatabaseHandler(this);
        if (db.getAllAssignee().size() != 0) {
            // dbSyncService();
        } else {
            getAssignee();
        }

    }

    //    Assignee Contacts API Call back by prakash
    public void getAssignee() {
        task = getString(R.string.chat_contacts);

        if (AppPreferences.getIsLogin(ChatContactsService.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(ChatContactsService.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(ChatContactsService.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(ChatContactsService.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String type = "";
        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();

                try {
                    for (ChatContacts contacts : apiResponse.getData().getChatContacts()) {
                        Log.d("Insert: ", "Inserting .." + contacts.getName() + " " + contacts.getMobile() + " " + contacts.getUid());
                        new ImageDownloadTask(contacts.getPhoto(), contacts).execute();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    public class ImageDownloadTask extends AsyncTask<String, String, String> {
        String image;
        ChatContacts chatContacts;

        public ImageDownloadTask(String image, ChatContacts chatContacts) {
            this.image = image;
            this.chatContacts = chatContacts;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            /*try {
                theBitmap = Glide.with(getApplicationContext()).
                        load(image).
                        asBitmap().
                        into(-1, -1).
                        get();
            } catch (ExecutionException e) {
                Log.e("TAG", "error" + e.getMessage());
            } catch (InterruptedException e) {
                Log.e("TAG", "error" + e.getMessage());
            }
            // saveToInternalStorage(theBitmap);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = Environment.getExternalStorageDirectory();

            File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File dir = new File(SDCardRoot.getAbsolutePath() + "/Android/data/" + getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mypath = new File(dir, "profile" + System.currentTimeMillis() + ".png");
            // Create imageDir
            FileOutputStream fos = null;
            try {
                if (mypath.createNewFile()) {
                    mypath.createNewFile();
                }
                fos = new FileOutputStream(mypath);
                Matrix matrix = new Matrix();
                // matrix.postRotate(270);
//                theBitmap = Bitmap.createBitmap(theBitmap, 0, 0, theBitmap.getWidth(), theBitmap.getHeight(), matrix, true);
                // Use the compress method on the BitMap object to write image to the OutputStream
                theBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("ABC", mypath.toString());*/
            return getDownloadedPath(image);
        }

        private String getDownloadedPath(String urlStr) {
            String filepath = "";
            try {
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
                File dir = new File(SDCardRoot.getAbsolutePath() + "/Android/data/" + getString(R.string.app_name));
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, "profile" + System.currentTimeMillis() + ".png");
                if (file.createNewFile()) {
                    file.createNewFile();
                }
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();
                int totalSize = urlConnection.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                }
                fileOutput.close();
                if (downloadedSize == totalSize) filepath = file.getPath();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                filepath = null;
                e.printStackTrace();
            }
            return filepath;
        }

        @Override
        protected void onPostExecute(String result) {
            // dismiss progress dialog and updateCount ui
            if (result != null) {
                chatContacts.setImagePath(result.toString());
            } else chatContacts.setImagePath("");
            if (db.checkAssigneeUId(String.valueOf(chatContacts.getUid()))) {
                ChatContacts chatContacts = new ChatContacts();
                chatContacts = db.getUserData(chatContacts.getUid());
                if (chatContacts.getImagePath() != null && (chatContacts.getImagePath().isEmpty() || chatContacts.getImagePath().equals(""))) {
                    db.updateAssigneeData(chatContacts);
                }
            } else db.addAssigneSpinner(chatContacts);
        }
    }
}

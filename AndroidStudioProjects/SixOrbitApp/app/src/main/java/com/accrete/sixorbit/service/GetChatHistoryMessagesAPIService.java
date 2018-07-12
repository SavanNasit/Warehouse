package com.accrete.sixorbit.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatMessage;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
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

public class GetChatHistoryMessagesAPIService extends IntentService {
    public static final int STATUS_RUNNING = 0;
    private static final String TAG = "GetChatHistoryMessagesAPIService";
    ResultReceiver receiver;
    private String actionChat, messageText, timeStamp = "1970-01-01";
    private DatabaseHandler db;
    private int receiverUId;

    public GetChatHistoryMessagesAPIService(String name) {
        super(name);
    }

    public GetChatHistoryMessagesAPIService() {
        super(GetChatHistoryMessagesAPIService.class.getName());
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        receiver = intent.getParcelableExtra(getString(R.string.receiver));
        receiverUId = intent.getExtras().getInt(getString(R.string.receiver_user_id), 0);
        actionChat = intent.getExtras().getString(getString(R.string.action));
        if (actionChat.equals(getString(R.string.send))) {
            messageText = intent.getExtras().getString(getString(R.string.message));
        } /*else {
            if (intent.getExtras().getString("timeStamp") != null)
                timeStamp = intent.getExtras().getString("timeStamp");
        }*/
        if (intent.getExtras().getString(getString(R.string.timestamp)) != null)
            timeStamp = intent.getExtras().getString(getString(R.string.timestamp));
        syncContacts();
    }

    private void syncContacts() {
        isDbSynced();
    }

    private void isDbSynced() {
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        db = new DatabaseHandler(this);
        if (actionChat.equals(getString(R.string.send))) {
            getSendMessageResponse();
        } else if (actionChat.contains(getString(R.string.fetch))) {
            getMessagesHistory();
        }
    }

    private void dbSyncService() {
        startService(new Intent(this, rapidKartDbSyncService.class));
    }

    //    Get Messages API
    public void getMessagesHistory() {
        try {
            task = getString(R.string.chat_messages);
            if (AppPreferences.getIsLogin(GetChatHistoryMessagesAPIService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(GetChatHistoryMessagesAPIService.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(GetChatHistoryMessagesAPIService.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(GetChatHistoryMessagesAPIService.this, AppUtils.LAST_DOMAIN);
            }
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getChatMessages(version, key, task, userId, accessToken, receiverUId + "",
                    timeStamp, "1");
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // clear the inbox
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (response.code() == 10006) {
                        Toast.makeText(getApplicationContext(), getString(R.string.contac_for_support) + "", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            for (final ChatMessage chatMessage : apiResponse.getData().getChatMessage()) {
                                new FileDownloadTask(chatMessage).execute();
                            }
                            if (!actionChat.equals(getString(R.string.fetch))) {
                                Intent intent = new Intent(getString(R.string.chat_message_broadcast_event));
                                // You can also include some extra data.
                                intent.putExtra(getString(R.string.message), getString(R.string.updated_ui_ftime));
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Send Message API
    public void getSendMessageResponse() {
        try {
            task = getString(R.string.send_message);

            if (AppPreferences.getIsLogin(GetChatHistoryMessagesAPIService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(GetChatHistoryMessagesAPIService.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(GetChatHistoryMessagesAPIService.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(GetChatHistoryMessagesAPIService.this, AppUtils.LAST_DOMAIN);
            }
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            String type = "";
            Call<ApiResponse> call = apiService.sendChatMessages(version, key, task, userId, accessToken,
                    receiverUId + "", messageText);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // clear the inbox
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        getMessagesHistory();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        /*File direct = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard" + "", getString(R.string.app_name));
            wallpaperDirectory.mkdirs();
        }*/
        /*if (isStoragePermissionGranted()) {
            File folder = new File(Environment.getExternalStorageDirectory()+ File.separator + "DebugData");

            if(!folder.exists()){
                folder.mkdir();
            }
        }*/

        File file = new File("/sdcard" + File.separator + "" + getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), imageToSave);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));

        return finalFile.toString();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private String getDownloadedPath(String urlStr, String name) {
        String filepath = "";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
            String filename = name;
            File file = new File(SDCardRoot, filename);
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
        Log.i("filepath:", " " + filepath);
        return filepath;
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        String Url, name;

        public DownloadFileAsync(String URL, String name) {
            this.Url = URL;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... aurl) {
            String filepath = "";
            try {
                URL url = new URL(Url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
                String filename = name;
                File file = new File(SDCardRoot, filename);
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
            Log.i("filepath:", " " + filepath);
            return filepath;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String str) {
            Toast.makeText(GetChatHistoryMessagesAPIService.this, str + "", Toast.LENGTH_SHORT).show();
        }
    }

    public class FileDownloadTask extends AsyncTask<String, String, String> {
        ChatMessage chatMessage;

        public FileDownloadTask(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
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
            chatMessage.setSyncId("");
            /*if (chatMessage.getFileUrl() != null && !chatMessage.getFileUrl().isEmpty() &&
                    (chatMessage.getFileType().equals("jpg") || chatMessage.getFileType().equals("png") ||
                            chatMessage.getFileType().equals("jpeg") || chatMessage.getFileType().equals("bmp"))) {
                if (getBitmapFromURL(chatMessage.getFileUrl()) != null) {
                    chatMessage.setFilePath(createDirectoryAndSaveFile(getBitmapFromURL(chatMessage.getFileUrl()), chatMessage.getFilePath()));
                }
            } else if (chatMessage.getFileUrl() != null && !chatMessage.getFileUrl().isEmpty() && chatMessage.getFileType().contains("xls")) {
                //new DownloadFileAsync(chatMessage.getFileUrl(), chatMessage.getFileName()).execute();
                chatMessage.setFilePath(getDownloadedPath(chatMessage.getFileUrl(), chatMessage.getFileName()));
            } else if (chatMessage.getFileUrl() != null && !chatMessage.getFileUrl().isEmpty() && chatMessage.getFileType().contains("pdf")) {
                //new DownloadFileAsync(chatMessage.getFileUrl(), chatMessage.getFileName()).execute();
                chatMessage.setFilePath(getDownloadedPath(chatMessage.getFileUrl(), chatMessage.getFileName()));
            }*/
            if (!db.checkChatId(chatMessage.getChatId())) {
                db.insertChatMessages(chatMessage);
            } else db.updateChatMessagesWithChatId(chatMessage);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // dismiss progress dialog and updateCount ui
            try {
                if (!actionChat.equals("fetch")) {
                    Intent intent = new Intent(getString(R.string.chat_message_broadcast_event));
                    // You can also include some extra data.
                    intent.putExtra("message", "updateUIFirstTime");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}


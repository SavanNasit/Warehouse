package com.accrete.warehouse.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.UploadDocument;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by agt on 24/1/18.
 */

public class FilesUploadAsyncTask extends AsyncTask<String, String, String> {
    private String pacId;
    private Activity activity;
    private List<UploadDocument> uploadDocumentList;
    private AlertDialog alertDialog;

    public FilesUploadAsyncTask(Activity activity, List<UploadDocument> documentList, String pacId,
                                AlertDialog alertDialog) {
        this.activity = activity;
        this.uploadDocumentList = documentList;
        this.pacId = pacId;
        this.alertDialog = alertDialog;
    }

    //android upload file to server
    public String uploadData(String pacId, String filePath) {
        String fileName = filePath;
        int serverResponseCode = 0;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = null;
        if (filePath != null)
            sourceFile = new File(filePath);

        try {
            FileInputStream fileInputStream = null;
            if (sourceFile != null)
                fileInputStream = new FileInputStream(sourceFile);

            URL url = new URL("http://" + AppPreferences.getLastDomain(activity, AppUtils.LAST_DOMAIN)
                    + "/?urlq=service" + "&version=" + Constants.version
                    + "&key=" + Constants.key
                    + "&task=" + activity.getString(R.string.manage_packages_file_upload_task)
                    + "&user_id=" + AppPreferences.getUserId(activity, AppUtils.USER_ID)
                    + "&access_token=" + AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN));

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            if (sourceFile != null)
                conn.setRequestProperty("image", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"pacid\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + pacId); // pac id
            dos.writeBytes(lineEnd);

            String[] arr = new String[uploadDocumentList.size()];
            Log.e("SIZE", arr.length + "");
            for (int i = 0; i < uploadDocumentList.size(); i++) {
                arr[i] = uploadDocumentList.get(i).getFilePath();
            }
            Log.e("ARR", Arrays.toString(arr).replace("[", "").replace("]", "")  + "");

           /* if (sourceFile != null) {
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\""
                        + Arrays.toString(arr).replace("[", "").replace("]", "") + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

            }*/
            for (int i = 0; i < uploadDocumentList.size(); i++) {

                if (sourceFile != null) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"files[]\";filename=[" + i + "]\""
                            + uploadDocumentList.get(i).getFilePath() + "\"" + lineEnd);
                }
                dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            if (fileInputStream != null) {
                bytesAvailable = fileInputStream.available();


                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }
            }
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
               }

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.e("uploadFile", "HTTP Response is : " + serverResponseMessage
                    + ": " + serverResponseCode);

            // close the streams //
            if (fileInputStream != null)
                fileInputStream.close();
            dos.flush();
            dos.close();

            int responseCode = conn.getResponseCode();
            Log.e("TAG, POST :: ", "" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.e("response:: ", "" + response
                        .toString());
                return response.toString();

            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {

            e.printStackTrace();

        }
        return "no";

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
        return uploadData(pacId, uploadDocumentList.get(0).getFilePath());
    }

    @Override
    protected void onPostExecute(String result) {
        // dismiss progress dialog and update ui
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean status = jsonObject.getBoolean("success");
            if (status) {
                Toast.makeText(activity, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

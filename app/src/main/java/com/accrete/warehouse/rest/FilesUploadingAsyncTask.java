package com.accrete.warehouse.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackageFile;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.URLConnection.guessContentTypeFromName;
import static java.util.logging.Level.INFO;

/**
 * Created by agt on 29/1/18.
 */

public class FilesUploadingAsyncTask extends AsyncTask<String, String, String> {
    private String pacId;
    private Activity activity;
    private List<PackageFile> uploadDocumentList;
    private List<PackageFile> oldDocumentList;
    private AlertDialog alertDialog;
    private ImageView imageView;
    private TextView textViewCancel;

    public FilesUploadingAsyncTask(Activity activity, List<PackageFile> documentList, String pacId,
                                   AlertDialog alertDialog, ImageView imageViewLoader, TextView btnCancel, List<PackageFile> oldDocuments) {
        this.activity = activity;
        this.uploadDocumentList = documentList;
        this.pacId = pacId;
        this.alertDialog = alertDialog;
        this.imageView = imageViewLoader;
        this.textViewCancel = btnCancel;
        this.oldDocumentList = oldDocuments;
    }

    private String mainFunction() {
        MultipartUtility utility;
        try {

            JSONArray jsonArrayOldFiles = new JSONArray();
            Log.e("packageUploadDocDetails", String.valueOf(oldDocumentList.size()));
            if (oldDocumentList != null && oldDocumentList.size() > 0) {
                for (int i = 0; i < oldDocumentList.size(); i++) {
                    JSONObject jsonObjectUploadDocDetails = new JSONObject();
                    jsonObjectUploadDocDetails.put("id", oldDocumentList.get(i).getPacfid());
                    jsonArrayOldFiles.put(jsonObjectUploadDocDetails);
                }
            }

            utility = new MultipartUtility(new URL("http://" + AppPreferences.getLastDomain(activity, AppUtils.LAST_DOMAIN)
                    + "/?urlq=service" + "&version=" + Constants.version
                    + "&key=" + Constants.key
                    + "&task=" + activity.getString(R.string.manage_packages_file_upload_task)
                    + "&user_id=" + AppPreferences.getUserId(activity, AppUtils.USER_ID)
                    + "&pacid=" + pacId
                    + "&access_token=" + AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN)));
            //    utility.addFormField("pacid", pacId);
            Log.d("old file size", String.valueOf(jsonArrayOldFiles.length()));

            JSONObject jsonObjectOldFiles = new JSONObject();
            jsonObjectOldFiles.put("old_files",jsonArrayOldFiles);
            utility.addFormField("data", jsonObjectOldFiles);

            for (int i = 0; i < uploadDocumentList.size(); i++) {
                Log.d("file size", uploadDocumentList.get(i).getFileUrl());
                utility.addFilePart("files[]", new File(uploadDocumentList.get(i).getFileUrl())) ;
            }

            byte[] response = utility.finish();
            Log.d("result1", new String(response) + " " + utility.url);

            return new String(response);

        } catch (IOException ex) {
            Toast.makeText(activity, "Upload Failed! Please try again", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
            Log.d("result2", "");
            return new String("");

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Upload Failed! Please try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.d("result2", "");
            return new String("");
        }

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
        return mainFunction();
    }

    @Override
    protected void onPostExecute(String result) {
        // dismiss progress dialog and update ui
        Log.d("result", result);

        if (uploadDocumentList.size() > 0) {
            uploadDocumentList.clear();
        } else if (oldDocumentList.size() > 0) {
            oldDocumentList.clear();
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            Log.d("result", result);
            boolean status = jsonObject.getBoolean("success");

            if (status) {

                Toast.makeText(activity, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }

            } else {

                Toast.makeText(activity, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }

            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }

            //Enable Touch Back
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            textViewCancel.setEnabled(true);

        } catch (Exception e) {

            e.printStackTrace();

            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }

            //Enable Touch Back
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            textViewCancel.setEnabled(true);

        }

    }

    public static class MultipartUtility {
        private static final Logger log = Logger.getLogger(MultipartUtility.class
                .getSimpleName());
        private static final String CRLF = "\r\n";
        private static final String CHARSET = "UTF-8";
        private static final int CONNECT_TIMEOUT = 50000;
        private static final int READ_TIMEOUT = 50000;
        private final HttpURLConnection connection;
        private final OutputStream outputStream;
        private final PrintWriter writer;
        private final String boundary;
        // for log formatting only
        private final URL url;
        private final long start;

        public MultipartUtility(final URL url) throws IOException {
            start = currentTimeMillis();
            this.url = url;

            boundary = "---------------------------" + currentTimeMillis();

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            outputStream = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET),
                    true);
        }

        public void addFormField(final String name, final JSONObject value) {
            writer.append("--").append(boundary).append(CRLF)
                    .append("Content-Disposition: form-data; name=\"").append(name)
                    .append("\"").append(CRLF)
                    .append("Content-Type: text/plain; charset=").append(CHARSET)
                    .append(CRLF).append(CRLF).append(value.toString()).append(CRLF);
        }

        public void addFilePart(final String fieldName, final File uploadFile)
                throws IOException {
            final String fileName = uploadFile.getName();
            writer.append("--").append(boundary).append(CRLF)
                    .append("Content-Disposition: form-data; name=\"")
                    .append(fieldName).append("\"; filename=\"").append(fileName)
                    .append("\"").append(CRLF).append("Content-Type: ")
                    .append(guessContentTypeFromName(fileName)).append(CRLF)
                    .append("Content-Transfer-Encoding: binary").append(CRLF)
                    .append(CRLF);

            writer.flush();
            outputStream.flush();
            try (final FileInputStream inputStream = new FileInputStream(uploadFile);) {
                final byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            writer.append(CRLF);
        }

        public void addHeaderField(String name, String value) {
            writer.append(name).append(": ").append(value).append(CRLF);
        }

        public byte[] finish() throws IOException {
            writer.append(CRLF).append("--").append(boundary).append("--")
                    .append(CRLF);
            writer.close();

            final int status = connection.getResponseCode();
            if (status != HTTP_OK) {
                throw new IOException(format("{0} failed with HTTP status: {1}",
                        url, status));
            }

            try (final InputStream is = connection.getInputStream()) {
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                final byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    bytes.write(buffer, 0, bytesRead);
                }
                log.log(INFO,
                        format("{0} took {4} ms", url,
                                (currentTimeMillis() - start)));
                return bytes.toByteArray();
            } finally {
                connection.disconnect();
            }
        }
    }
}

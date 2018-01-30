package com.accrete.warehouse.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.UploadDocument;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.Constants;

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
    private List<UploadDocument> uploadDocumentList;
    private AlertDialog alertDialog;

    public FilesUploadingAsyncTask(Activity activity, List<UploadDocument> documentList, String pacId,
                                   AlertDialog alertDialog) {
        this.activity = activity;
        this.uploadDocumentList = documentList;
        this.pacId = pacId;
        this.alertDialog = alertDialog;

    }


    private String mainFunction() {
        MultipartUtility utility;
        try {
            utility = new MultipartUtility(new URL("http://" + AppPreferences.getLastDomain(activity, AppUtils.LAST_DOMAIN)
                    + "/?urlq=service" + "&version=" + Constants.version
                    + "&key=" + Constants.key
                    + "&task=" + activity.getString(R.string.manage_packages_file_upload_task)
                    + "&user_id=" + AppPreferences.getUserId(activity, AppUtils.USER_ID)
                    + "&access_token=" + AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN)));
            utility.addFormField("pacid", pacId);

            for (int i = 0; i < uploadDocumentList.size(); i++) {
                utility.addFilePart("files[]", new File(uploadDocumentList.get(i).getFilePath()));
            }
            byte[] response = utility.finish();
            return new String(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new String("");

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

    public static class MultipartUtility {

        private static final Logger log = Logger.getLogger(MultipartUtility.class
                .getSimpleName());

        private static final String CRLF = "\r\n";
        private static final String CHARSET = "UTF-8";

        private static final int CONNECT_TIMEOUT = 15000;
        private static final int READ_TIMEOUT = 10000;

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

        public void addFormField(final String name, final String value) {
            writer.append("--").append(boundary).append(CRLF)
                    .append("Content-Disposition: form-data; name=\"").append(name)
                    .append("\"").append(CRLF)
                    .append("Content-Type: text/plain; charset=").append(CHARSET)
                    .append(CRLF).append(CRLF).append(value).append(CRLF);
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

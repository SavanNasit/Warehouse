package com.accrete.sixorbit.helper;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by poonam on 1/11/18.
 */

public class AudioRecorder {

    final String path;
    MediaRecorder recorder;

    private Context context;

    /**
     * Creates a new audio recording at the given path (relative to root of SD card).
     */
    public AudioRecorder(String path) {
        this.path = sanitizePath(path);
    }

    private String sanitizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.contains(".")) {
            path += ".mp3";
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
    }

    /**
     * Starts a new recording.
     */
    public void start() throws IOException {
        recorder = new MediaRecorder();
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card is not mounted.  It is " + state + ".");
        }

        // make sure the directory we plan to store the recording in exists
      File directory = new File(path).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Path to file could not be created.");
        }

      //  recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(path);
            recorder.prepare();
            recorder.start();
        }catch (IllegalStateException ex){
            ex.printStackTrace();
        }catch (RuntimeException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Stops a recording that has been previously started.
     */
    public void stop() throws IOException {
        if (recorder == null)
            return;
        Log.d("AD", "RecordService stopAndReleaseRecorder");
        boolean recorderStopped = false;
        boolean exception = false;

        try {
            recorder.stop();
            recorderStopped = true;
        } catch (IllegalStateException e) {
            Log.e("AD", "Failed to stop recorder.  Perhaps it wasn't started?", e);
            exception = true;
        }
        recorder.reset();
        recorder.release();
        recorder = null;
        if (exception) {
            deleteFile();
        }
        if (recorderStopped) {
            Toast.makeText(context, "End Record call",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void deleteFile() {
        Log.d("AD", "RecordService deleteFile");
      //  directory.delete();
       // directory = null;
    }

}

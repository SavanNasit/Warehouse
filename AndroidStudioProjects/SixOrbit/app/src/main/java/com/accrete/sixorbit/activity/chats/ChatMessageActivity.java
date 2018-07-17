package com.accrete.sixorbit.activity.chats;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.AppIllustration.ApplicationClass;
import com.accrete.sixorbit.adapter.ChatMessageAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.ChatMessage;
import com.accrete.sixorbit.receiver.ApiResultReceiver;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.service.ApiCallService;
import com.accrete.sixorbit.service.ChatService;
import com.accrete.sixorbit.service.GetChatHistoryMessagesAPIService;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.FilePath;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.LinearLayout.VERTICAL;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

public class ChatMessageActivity extends AppCompatActivity implements View.OnClickListener, ApiResultReceiver.Receiver,
        ChatMessageAdapter.ChatMessageListener {
    private static final int PICKFILE_RESULT_CODE = 2;
    public String userChoosenTask;
    protected ApplicationClass applicationClass;
    Handler handler = new Handler();
    String last_chat_id = "";
    Bitmap bitmap;
    String fetchFirst;
    Uri imageUri;
    private RelativeLayout linearLayout;
    private RelativeLayout topLayout;
    private ImageView statusImageView;
    private TextView nameTextView;
    private ImageView downImageView;
    private View dividerTop;
    private RecyclerView chatMessageRecyclerView;
    private View dividerBottom;
    private RelativeLayout bottomLayout;
    private EditText msgEditText;
    private ImageView sendImageView;
    private ChatMessageAdapter mAdapter;
    private DatabaseHandler databaseHandler;
    private List<ChatMessage> chatHistory, tempList;
    private int uId = 0;
    private final Runnable updateOnlineStatus = new Runnable() {
        public void run() {
            try {
                ChatContacts chatContacts = new ChatContacts();
                chatContacts = databaseHandler.getUserData(uId);
                if (chatContacts.getOnlineStatus() != null && !chatContacts.getOnlineStatus().isEmpty()) {
                    if (chatContacts.getOnlineStatus().equals(getString(R.string.online_status))) {
                        statusImageView.setImageResource(R.drawable.online_circle);
                    } else {
                        statusImageView.setImageResource(R.drawable.offline_cirlce);
                    }
                }
                handler.postDelayed(this, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private boolean backToChatList;
    private String nameUser;
    private TextView textView_empty;
    private ApiResultReceiver mReceiver;
    private String timeStamp;
    private boolean updateUIFirstTime;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if (message.equals("updateCount")) {
            } else if (message.equals("insert")) {
                if (ChatMessageActivity.this != null) {
                    setSingleNewMessage(intent.getStringExtra("chatId"));
                } else {
                }
            } else if (message.equals("updateUIFirstTime")) {
                updateUIFirstTime();
            }
        }
    };
    private ImageView attachmentsImageView;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Dialog dialog;
    private String selectedFilePath;
    private LinearLayoutManager mLayoutManager;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount, firstVisibleItem;
    private boolean loading;

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case ApiCallService.STATUS_RUNNING:
                break;
            case ApiCallService.STATUS_FINISHED:
                final ApiResponse results = resultData.getParcelable("followUp");

                break;
            case ApiCallService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backToChatList == true) {
            Intent intent = new Intent(ChatMessageActivity.this, DrawerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(getString(R.string.back_to_chat_list), backToChatList);
            startActivity(intent);
        }
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_mesaage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseHandler = new DatabaseHandler(this);

        Intent intent = getIntent();
        int notificationId = intent.getIntExtra(getString(R.string.notify_id), -1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        ChatService.messagesMap.remove(String.valueOf(notificationId));
        if (ChatService.messagesMap.size() <= 1) {
            notificationManager.cancel(1000);
        }
        ChatService.messagesMap.remove(AppPreferences.getChatNotificationNumber(this, AppUtils.PUSH_CHATS_NOTIFICATION_NUMBER));
        notificationManager.cancel(AppPreferences.getChatNotificationNumber(this, AppUtils.PUSH_CHATS_NOTIFICATION_NUMBER));

        applicationClass = (ApplicationClass) getApplication();
        applicationClass.onActivityCreated(this, savedInstanceState);

        mReceiver = new ApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        linearLayout = (RelativeLayout) findViewById(R.id.linearLayout);
        topLayout = (RelativeLayout) findViewById(R.id.top_layout);
        statusImageView = (ImageView) findViewById(R.id.status_imageView);
        nameTextView = (TextView) findViewById(R.id.name_textView);
        downImageView = (ImageView) findViewById(R.id.down_imageView);
        dividerTop = findViewById(R.id.divider_top);
        chatMessageRecyclerView = (RecyclerView) findViewById(R.id.chat_message_recycler_view);
        dividerBottom = findViewById(R.id.divider_bottom);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        attachmentsImageView = (ImageView) findViewById(R.id.attachments_imageView);
        msgEditText = (EditText) findViewById(R.id.msg_editText);
        sendImageView = (ImageView) findViewById(R.id.send_imageView);
        textView_empty = (TextView) findViewById(R.id.textView_empty);

        msgEditText.setTypeface(Typeface.SANS_SERIF);
        nameTextView.setTypeface(Typeface.SANS_SERIF);
        textView_empty.setTypeface(Typeface.SANS_SERIF);

        //Data of another user
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.fname))) {
                nameTextView.setText("@" + intent.getStringExtra(getString(R.string.fname)));
                nameUser = intent.getStringExtra(getString(R.string.fname));
            }
            if (intent.hasExtra(getString(R.string.uid))) {
                uId = intent.getIntExtra(getString(R.string.uid), 0);
            }
        }

        if (nameUser == null) {
            ChatContacts chatContacts = databaseHandler.getUserData(uId);
            nameUser = chatContacts.getName();
            backToChatList = true;
            nameTextView.setText(nameUser);
        }

        chatHistory = new ArrayList<ChatMessage>();
        mAdapter = new ChatMessageAdapter(this, chatHistory, uId, this, 0);
        mLayoutManager = new LinearLayoutManager(this, VERTICAL, true);
        mLayoutManager.setStackFromEnd(true);
        chatMessageRecyclerView.setLayoutManager(mLayoutManager);
        chatMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatMessageRecyclerView.setAdapter(mAdapter);

        //Click Listeners
        msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0 || msgEditText.getText().toString().trim().length() == 0) {
                    sendImageView.setImageResource(R.drawable.ic_send_hover);
                    sendImageView.setEnabled(false);
                } else {
                    sendImageView.setImageResource(R.drawable.ic_send);
                    sendImageView.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        attachmentsImageView.setOnClickListener(this);
        attachmentsImageView.setVisibility(View.VISIBLE);
        sendImageView.setOnClickListener(this);
        sendImageView.setEnabled(false);
        msgEditText.setHint(getString(R.string.message_hint) + "" + nameUser);
        //Load data first time
        updateUIFirstTime();

        if (chatHistory.size() > 0) {
            timeStamp = chatHistory.get(chatHistory.size() - 1).getCreatedTs();
        }
        fetchFirst = "fetch_first";
        //calling API
        if (!NetworkUtil.getConnectivityStatusString(ChatMessageActivity.this).equals(getString(R.string.not_connected_to_internet))) {
            Intent intent_service = new Intent(Intent.ACTION_SYNC, null, ChatMessageActivity.this, GetChatHistoryMessagesAPIService.class);
            intent_service.putExtra(getString(R.string.receiver_userId), uId);
            intent_service.putExtra(getString(R.string.receiver), mReceiver);
            intent_service.putExtra(getString(R.string.action), fetchFirst);
            intent_service.putExtra(getString(R.string.timestamp), timeStamp);
            startService(intent_service);
        } else {
            Toast.makeText(ChatMessageActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(getString(R.string.chat_message_broadcast_event)));

        //Handler to updateCount online status of user
        handler.post(updateOnlineStatus);

        //Scroll Listener
        chatMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                firstVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(ChatMessageActivity.this).equals("Not connected to Internet")) {
                        if (databaseHandler.getChatHistory(uId, chatHistory.size() + 1, getResources().getInteger(R.integer.chat_items_count)).size() > 0) {
                            updateUIPeriodicallyFromDb();
                        } else
                            loadMoreMessages(chatHistory.get(firstVisibleItem).getCreatedTs());
                    } else {
                        if (databaseHandler.getChatHistory(uId, chatHistory.size() + 1, getResources().getInteger(R.integer.chat_items_count)).size() > 0) {
                            updateUIPeriodicallyFromDb();
                        }
                    }
                }
            }

        });
    }

    private void loadMoreMessages(String updatedTime) {
        task = getString(R.string.chat_messages);
        if (AppPreferences.getIsLogin(ChatMessageActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(ChatMessageActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(ChatMessageActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(ChatMessageActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getChatMessages(version, key, task, userId, accessToken, uId + "",
                updatedTime, "2");
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                AsyncTask<Object, Object, Object> addUpdateMessagesAsyncTask = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        if (apiResponse != null && apiResponse.getSuccess() && apiResponse.getData().getChatMessage() != null) {
                            for (final ChatMessage chatMessage : apiResponse.getData().getChatMessage()) {
                                if (chatMessage.getChatId() != null && !chatMessage.getChatId().isEmpty()) {
                                    if (!databaseHandler.checkChatId(chatMessage.getChatId())) {
                                        databaseHandler.insertChatMessages(chatMessage);
                                        chatHistory.add(chatMessage);
                                    } else
                                        databaseHandler.updateChatMessagesWithChatId(chatMessage);
                                }
                            }
                        } else {
                            //Deleted User
                            if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(ChatMessageActivity.this, apiResponse.getMessage());
                            } else {
                                Toast.makeText(ChatMessageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        if (!apiResponse.getSuccess()) {
                            CustomisedToast.error(ChatMessageActivity.this, apiResponse.getMessage()).show();
                            loading = false;

                            //Deleted User
                            if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(ChatMessageActivity.this, apiResponse.getMessage());
                            }

                        } else {
                            loading = false;
                            mAdapter.notifyDataSetChanged();
                            //chatMessageRecyclerView.smoothScrollToPosition(chatHistory.size());
                            // updateUIPeriodicallyFromDb();
                        }
                    }
                };

                addUpdateMessagesAsyncTask.execute((Object[]) null);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ChatMessageActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateUIPeriodicallyFromDb() {
        if (databaseHandler.getChatHistory(uId, chatHistory.size() + 1, getResources().getInteger(R.integer.chat_items_count)).size() > 0) {
            if (tempList != null && tempList.size() > 0) {
                tempList.clear();
            }
            tempList = databaseHandler.getChatHistory(uId, chatHistory.size() + 1, getResources().getInteger(R.integer.chat_items_count));
            for (int i = 0; i < tempList.size(); i++) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(tempList.get(i).getMessage());
                chatMessage.setUid(tempList.get(i).getUid());
                chatMessage.setCreatedTs(tempList.get(i).getCreatedTs());
                chatMessage.setMsgType(tempList.get(i).getMsgType());
                chatHistory.add(chatMessage);
            }
        } else {
            mAdapter = new ChatMessageAdapter(this, chatHistory, uId, this, 1);
            chatMessageRecyclerView.setAdapter(mAdapter);
        }
        loading = false;
        chatMessageRecyclerView.post(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void scrollToBottom() {
        chatMessageRecyclerView.scrollToPosition(0);
    }

    public void displayMessage(ChatMessage message) {
        mAdapter.add(message);
        mAdapter.notifyDataSetChanged();
        textView_empty.setVisibility(View.GONE);
        scrollToBottom();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_imageView) {
            String messageText = msgEditText.getText().toString().trim();
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            if (TextUtils.isEmpty(messageText)) {
                return;
            }
            long time_nano = System.nanoTime();
            long micro_seconds = time_nano / 1000;

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(messageText);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            chatMessage.setMsgType("1");
            chatMessage.setCreatedTs(simpleDateFormat.format(new Date()));
            chatMessage.setSyncId(String.valueOf(micro_seconds));
            chatMessage.setChatId("0");
            chatMessage.setUid(String.valueOf(uId));
            msgEditText.setText("");
            databaseHandler.insertChatMessages(chatMessage);
            displayMessage(chatMessage);

            // Register to receive messages.
            // We are registering an observer (mMessageReceiver) to receive Intents
            // with actions named "custom-event-name".
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter(getString(R.string.chat_message_broadcast_event)));

            //Send Messages
            Intent intent_receiver = new Intent(getString(R.string.send_chat_message_broadcast_event));
            // You can also include some extra data.
            intent_receiver.putExtra(getString(R.string.message), messageText);
            intent_receiver.putExtra(getString(R.string.to), uId);
            intent_receiver.putExtra(getString(R.string.sync_id), micro_seconds);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent_receiver);
        } else if (view.getId() == R.id.attachments_imageView) {
            selectImage();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_gallery),
                getString(R.string.add_files), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_attatchments));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = false;
                /*if (checkPermissionWithRationale((Activity) getActivity(), new FollowUpsFragment(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
                    result = true;
                }*/
                result = true;
                if (items[item].equals(getString(R.string.take_photo))) {
                    userChoosenTask = getString(R.string.take_photo);
                    if (result)
                        cameraIntent();

                } else if (items[item].equals(getString(R.string.choose_from_gallery))) {
                    userChoosenTask = getString(R.string.choose_from_gallery);
                    if (result)
                        galleryIntent();

                } else if (items[item].equals(getString(R.string.add_files))) {
                    userChoosenTask = getString(R.string.add_files);
                    if (result) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, PICKFILE_RESULT_CODE);
                    }
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "on Stop");
    }

    private void galleryIntent() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICKFILE_RESULT_CODE) {

                // Get the Uri of the selected file
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);

                selectedFilePath = FilePath.getPath(this, uri);

                String displayName = null;
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                sendDocs(selectedFilePath, displayName);
            } else if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateOnlineStatus);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public void updateUIFirstTime() {
        if (databaseHandler.getChatHistory(uId, 0, 50).size() > 0) {
            //if (chatHistory == null || chatHistory.size() == 0) {
            chatHistory = databaseHandler.getChatHistory(uId, 0, 50);
            mAdapter = new ChatMessageAdapter(this, chatHistory, uId, this, 0);
            chatMessageRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            scrollToBottom();
            textView_empty.setVisibility(View.GONE);
            //} else {
            //    mAdapter.notifyDataSetChanged();
            //}
            updateUIFirstTime = true;
        } else {
            if (chatHistory == null || chatHistory.size() == 0) {
                textView_empty.setText(getString(R.string.empty_chat_message));
                textView_empty.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        /*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // bitmap = thumbnail;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        selectedFilePath = finalFile.getPath();
        dialogImagePreview();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void setSingleNewMessage(String chatId) {
        if (last_chat_id.isEmpty() || !last_chat_id.equals(chatId)) {
            last_chat_id = chatId;
            displayMessage(databaseHandler.getSingleChatmessage(chatId));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (databaseHandler.getChatHistory(uId, chatHistory.size() + 1, getResources().getInteger(R.integer.chat_items_count)).size() > 0) {
            if (tempList != null && tempList.size() > 0) {
                tempList.clear();
            }
            tempList = databaseHandler.getChatHistory(uId, chatHistory.size() + 1, getResources().getInteger(R.integer.chat_items_count));
            for (int i = 0; i < tempList.size(); i++) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(tempList.get(i).getMessage());
                chatMessage.setUid(tempList.get(i).getUid());
                chatMessage.setCreatedTs(tempList.get(i).getCreatedTs());
                chatMessage.setMsgType(tempList.get(i).getMsgType());
                chatHistory.add(chatMessage);
            }
        } else {
            mAdapter = new ChatMessageAdapter(this, chatHistory, uId, this, 1);
            chatMessageRecyclerView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmap = bm;

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), bm);
        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        selectedFilePath = finalFile.getPath();
        dialogImagePreview();
    }

    //To send Documents to the server
    private void sendDocs(final String filePath, final String fileName) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Send attachment");
        alert.setMessage("Do you really want to send " + fileName + "?");
        alert.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        long time_nano = System.nanoTime();
                        long micro_seconds = time_nano / 1000;

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setMessage("");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        chatMessage.setMsgType("1");
                        chatMessage.setCreatedTs(simpleDateFormat.format(new Date()));
                        chatMessage.setSyncId(String.valueOf(micro_seconds));
                        chatMessage.setChatId("0");
                        chatMessage.setUid(String.valueOf(uId));
                        chatMessage.setBitmap(bitmap);
                        chatMessage.setFilePath(filePath);
                        chatMessage.setFileName(fileName);
                        chatMessage.setFileType(filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()));
                        databaseHandler.insertChatMessages(chatMessage);
                        displayMessage(chatMessage);
                        dialog.dismiss();

                        if (!NetworkUtil.getConnectivityStatusString(ChatMessageActivity.this).equals("Not connected to Internet")) {
                            new uploadFileAsyncTask(filePath, "",
                                    String.valueOf(uId),
                                    String.valueOf(micro_seconds), simpleDateFormat.format(new Date())).execute();
                        }
                        //Clear edittext
                        msgEditText.setText("");

                    }
                });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedFilePath = "";
                        dialog.cancel();
                    }
                });

        alert.show();
    }

    private String createDirectoryAndSaveImageFile(Bitmap imageToSave, String fileName) {

        try {
            File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File dir = new File(SDCardRoot.getAbsolutePath() + "/Android/data/" + getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (file.createNewFile()) {
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file);
            // imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
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

    private void dialogImagePreview() {
        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        dialog.setContentView(R.layout.dialog_img_preview);
        ImageView imageViewPreview = (ImageView) dialog.findViewById(R.id.chat_message_image);
        ImageView imageViewCancel = (ImageView) dialog.findViewById(R.id.chat_image_preview_cancel);
        final EditText editText = (EditText) dialog.findViewById(R.id.chat_caption);
        TextView textViewSend = (TextView) dialog.findViewById(R.id.chat_preview_send);

        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        imageViewPreview.setImageBitmap(scaled);
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        textViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time_nano = System.nanoTime();
                long micro_seconds = time_nano / 1000;

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(editText.getText().toString().trim());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                chatMessage.setMsgType("1");
                chatMessage.setCreatedTs(simpleDateFormat.format(new Date()));
                chatMessage.setSyncId(String.valueOf(micro_seconds));
                chatMessage.setChatId("0");
                chatMessage.setUid(String.valueOf(uId));
                chatMessage.setBitmap(bitmap);
                chatMessage.setFilePath(createDirectoryAndSaveImageFile(bitmap, selectedFilePath));
                chatMessage.setFileName(selectedFilePath);
                chatMessage.setFileType(selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1, selectedFilePath.length()));
                databaseHandler.insertChatMessages(chatMessage);
                displayMessage(chatMessage);
                dialog.dismiss();

                if (!NetworkUtil.getConnectivityStatusString(ChatMessageActivity.this).equals("Not connected to Internet")) {
                    new uploadFileAsyncTask(selectedFilePath, editText.getText().toString().trim(),
                            String.valueOf(uId),
                            String.valueOf(micro_seconds), simpleDateFormat.format(new Date())).execute();
                }

                //Clear edittext
                editText.setText("");
                msgEditText.setText("");

            }
        });

        dialog.show();
    }

    //android upload file to server
    public String uploadData(String messageStr, String uId, String syncId, String filePath) {
        String fileName = filePath;
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
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

            URL url = new URL("http://" + AppPreferences.getLastDomain(ChatMessageActivity.this, AppUtils.LAST_DOMAIN)
                    + "/?urlq=service" + "&version=" + Constants.version
                    + "&key=" + Constants.key
                    + "&task=" + getString(R.string.chat_set_image)
                    + "&user_id=" + AppPreferences.getUserId(ChatMessageActivity.this, AppUtils.USER_ID)
                    + "&access_token=" + AppPreferences.getAccessToken(ChatMessageActivity.this, AppUtils.ACCESS_TOKEN));

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
            dos.writeBytes("Content-Disposition: form-data; name=\"receiver_uid\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + uId); // receiver id
            dos.writeBytes(lineEnd);


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + AppPreferences.getUserId(ChatMessageActivity.this, AppUtils.USER_ID)); // user id
            dos.writeBytes(lineEnd);


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"access_token\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + AppPreferences.getAccessToken(ChatMessageActivity.this, AppUtils.ACCESS_TOKEN)); // access token
            dos.writeBytes(lineEnd);


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"message\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + messageStr); // message
            dos.writeBytes(lineEnd);


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"sync_id\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + syncId); // sync id
            dos.writeBytes(lineEnd);


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file_type\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + fileExtension); // file type
            dos.writeBytes(lineEnd);


            if (sourceFile != null) {
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

            }
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

    public class uploadFileAsyncTask extends AsyncTask<String, String, String> {
        String filePath, message, uId, syncId, createdTs;

        public uploadFileAsyncTask(String filePath, String message, String uId, String syncId, String createdTs) {
            this.filePath = filePath;
            this.message = message;
            this.uId = uId;
            this.syncId = syncId;
            this.createdTs = createdTs;
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
            return uploadData(message, uId, syncId, filePath);
        }

        @Override
        protected void onPostExecute(String result) {
            // dismiss progress dialog and updateCount ui
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonDataObj;
                boolean status = jsonObject.getBoolean("success");
                if (status) {
                    jsonDataObj = jsonObject.getJSONObject("data");
                    if (jsonDataObj.has("sync_id")) {
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setChatId(jsonDataObj.getString("chat_id"));
                        chatMessage.setFileUrl(jsonDataObj.getString("url"));
                        chatMessage.setFileType(jsonDataObj.getString("file_type"));
                        chatMessage.setSyncId(jsonDataObj.getString("sync_id"));
                        chatMessage.setFileName(jsonDataObj.getString("file_name"));
                        chatMessage.setMessage(jsonDataObj.getString("message"));
                        chatMessage.setCreatedTs(createdTs);
                        chatMessage.setMsgType("1");

                        databaseHandler.updateChatMessages(chatMessage);
                    }
                } else {

                    //Deleted User
                    if (jsonObject.getString("result_code").equals(Constants.WRONG_CREDENTIALS) ||
                            jsonObject.getString("result_code").equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(ChatMessageActivity.this, message);
                    } else {
                        Toast.makeText(ChatMessageActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChatMessageActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
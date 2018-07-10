package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.ChatMessage;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by agt on 29/8/17.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /*private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;*/
    private static final int TYPE_ITEM = 2;
    Dialog dialog;
    Bitmap bitmap;
    Bitmap myBitmap;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private Activity mContext;
    private int uID = 0;
    private DatabaseHandler databaseHandler;
    private String loggedinUId;
    private ChatMessageListener chatMessageListener;
    private int status = 0;

    public ChatMessageAdapter(Activity mContext, List<ChatMessage> chatMessages, int uId, ChatMessageListener chatMessageListener, int status) {
        this.mContext = mContext;
        this.status = status;
        this.chatMessages = chatMessages;
        this.uID = uId;
        this.chatMessageListener = chatMessageListener;
        if (mContext != null)
            databaseHandler = new DatabaseHandler(mContext);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

       /*     Glide.with(mContext)
                    .load(src)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            myBitmap = resource;
                        }
                    });*/
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= chatMessages.size()) {
           /* if (status == 0)
                return TYPE_FOOTER;
            else return TYPE_ITEM;*/
        }
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.footerText.setText(mContext.getString(R.string.load_more));
            //apply click events
            // applyClickEvents(footerViewHolder, position);
        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (position < chatMessages.size()) {
                ChatMessage chatMessage = chatMessages.get(position);
                // Log.e("TABLE", chatMessage.getChatId() + "");
                // displaying text view data
                if (chatMessage.getMessage() != null && !chatMessage.getMessage().isEmpty()) {
                    itemViewHolder.messageTextView.setText(chatMessage.getMessage().toString().trim());
                    itemViewHolder.messageTextView.setVisibility(View.VISIBLE);
                } else {
                    itemViewHolder.messageTextView.setVisibility(View.GONE);
                }
                itemViewHolder.textViewTimePeriod.setVisibility(View.GONE);
                itemViewHolder.dividerTop.setVisibility(View.GONE);

                if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
                    loggedinUId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date current_itemDate = simpleDateFormat.parse(chatMessage.getCreatedTs());

                    DateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                    Date date = simpleDateFormat.parse(chatMessage.getCreatedTs());
                    itemViewHolder.timeTextView.setText(outputFormat.format(date).toString().trim());
                    long previousTs = 0;
                    if (position < chatMessages.size() - 1) {
                        ChatMessage pm = chatMessages.get(position + 1);
                        previousTs = simpleDateFormat.parse(pm.getCreatedTs()).getTime();
                    }
                    setTimeTextVisibility(current_itemDate.getTime(), previousTs, itemViewHolder.textViewTimePeriod,
                            itemViewHolder.dividerTop);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (chatMessage.getMsgType().equals("2")) {
                    ChatContacts chatContacts = new ChatContacts();
                    chatContacts = databaseHandler.getUserData(uID);
                    if (chatContacts.getImagePath() != null) {
                        //Displaying images into background thread
                        File file = new File(chatContacts.getImagePath());
                        Uri imageUri = Uri.fromFile(file);
                        Glide.with(mContext)
                                .load(imageUri).placeholder(R.mipmap.ic_launcher)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                                .into(itemViewHolder.profileImageView);
                    }
                    itemViewHolder.nameTextView.setText(chatContacts.getName().toString().trim());
                } else {
                    ChatContacts chatContacts = new ChatContacts();
                    chatContacts = databaseHandler.getUserData(Integer.valueOf(loggedinUId));
                    if (chatContacts.getImagePath() != null) {
                        //Displaying images into background thread
                        Glide.with(mContext)
                                .load(AppPreferences.getPhoto(mContext, AppUtils.USER_PHOTO))
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.icon_neutral_profile)
                                .into(itemViewHolder.profileImageView);

                    }
                    itemViewHolder.nameTextView.setText(chatContacts.getName().toString().trim());
                }
            }
            if (chatMessages.get(position).getFileType() != null && !chatMessages.get(position).getFileType().isEmpty()) {
                itemViewHolder.cardView.setVisibility(View.VISIBLE);
                if (chatMessages.get(position).getFileType().equals("jpg") || chatMessages.get(position).getFileType().equals("png") ||
                        chatMessages.get(position).getFileType().equals("jpeg") || chatMessages.get(position).getFileType().equals("bmp")) {
                    itemViewHolder.imageLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.fileLayout.setVisibility(View.GONE);
                    if (chatMessages.get(position).getFilePath() != null && !chatMessages.get(position).getFilePath().isEmpty()) {
                        itemViewHolder.imageView.setVisibility(View.VISIBLE);
                        File file = new File(chatMessages.get(position).getFilePath());
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inSampleSize = 4;
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        float bitmapRatio = (float) width / (float) height;
                        if (bitmapRatio > 1) {
                            width = 400;
                            height = (int) (width / bitmapRatio);
                        } else {
                            height = 400;
                            width = (int) (height * bitmapRatio);
                            itemViewHolder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, true));
                        /*bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        int height = bitmap.getHeight(), width = bitmap.getWidth();

                        if (height > 1280 && width > 960) {
                            Bitmap imgbitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                            itemViewHolder.imageView.setImageBitmap(imgbitmap);
                        } else {
                            itemViewHolder.imageView.setImageBitmap(bitmap);
                        }*/

                            // Bitmap d = new BitmapDrawable(mContext.getResources(), file.getAbsolutePath()).getBitmap();
                            // int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
                            // Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);

//                        itemViewHolder.imageView.setImageBitmap(bitmap);
                        /*if (chatMessages.get(position).getMessage() != null) {
                            itemViewHolder.messageTextView.setVisibility(View.VISIBLE);
                        } else itemViewHolder.messageTextView.setVisibility(View.GONE);*/
                        }
                    } else if (chatMessages.get(position).getFileUrl() != null && !chatMessages.get(position).getFileUrl().isEmpty()) {
                        itemViewHolder.imageView.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .load(chatMessages.get(position).getFileUrl()).placeholder(R.mipmap.ic_launcher)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .into(((ItemViewHolder) holder).imageView);
                    } else itemViewHolder.imageView.setVisibility(View.GONE);
                } else if (chatMessages.get(position).getFileType().contains("xls")) {
                    itemViewHolder.imageLayout.setVisibility(View.GONE);
                    itemViewHolder.fileLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.fileImageView.setImageResource(R.drawable.ic_description_black_24dp);
                    itemViewHolder.fileNameTextView.setText(chatMessages.get(position).getFileName());
                } else if (chatMessages.get(position).getFileType().contains("pdf")) {
                    itemViewHolder.imageLayout.setVisibility(View.GONE);
                    itemViewHolder.fileLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.fileImageView.setImageResource(R.drawable.ic_description_black_24dp);
                    itemViewHolder.fileNameTextView.setText(chatMessages.get(position).getFileName());
                } else if (chatMessages.get(position).getFileType().contains("txt")) {
                    itemViewHolder.imageLayout.setVisibility(View.GONE);
                    itemViewHolder.fileLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.fileImageView.setImageResource(R.drawable.ic_description_black_24dp);
                    itemViewHolder.fileNameTextView.setText(chatMessages.get(position).getFileName());
                } else {
                    itemViewHolder.imageLayout.setVisibility(View.GONE);
                    itemViewHolder.fileLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.fileImageView.setImageResource(R.drawable.ic_description_black_24dp);
                    itemViewHolder.fileNameTextView.setText(chatMessages.get(position).getFileName());
                }

                //Download attachments
                //  new FileDownloadTask(chatMessages.get(position)).execute();
            } else {
                itemViewHolder.imageLayout.setVisibility(View.GONE);
                itemViewHolder.cardView.setVisibility(View.GONE);
            }

            if (chatMessages.get(position).getFileType() != null && chatMessages.get(position).getFileName() != null) {
                itemViewHolder.captionTextView.setText(chatMessages.get(position).getFileName().substring(chatMessages.get(position).getFileName().lastIndexOf("/") + 1));
            }
            itemViewHolder.captionTextView.setVisibility(View.GONE);
            itemViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chatMessages.get(position).getFileType().equals("jpg") || chatMessages.get(position).getFileType().equals("png") ||
                            chatMessages.get(position).getFileType().equals("jpeg") || chatMessages.get(position).getFileType().equals("bmp")) {
                        if (chatMessages.get(position).getFilePath() != null && !chatMessages.get(position).getFilePath().isEmpty()) {
                            dialogImagePreview(chatMessages.get(position).getFilePath(),
                                    itemViewHolder.nameTextView.getText().toString(),
                                    chatMessages.get(position).getFileUrl(),
                                    chatMessages.get(position).getFileName(),
                                    chatMessages.get(position));
                        } else {
                            //Download attachments
                            if (!NetworkUtil.getConnectivityStatusString(mContext).equals("Not connected to Internet")) {
                               /* new FileDownloadTask(chatMessages.get(position),
                                        itemViewHolder.nameTextView.getText().toString()).execute();*/
                                dialogImagePreview(chatMessages.get(position).getFilePath(),
                                        itemViewHolder.nameTextView.getText().toString(),
                                        chatMessages.get(position).getFileUrl(),
                                        chatMessages.get(position).getFileName(),
                                        chatMessages.get(position));
                            } else {
                                // CustomisedToast.error(mContext, "Please check internet connection.").show();
                            }
                        }
                    }
                    //Download attachments
                    //new FileDownloadTask(chatMessages.get(position)).execute();
                }
            });
            itemViewHolder.fileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chatMessages.get(position).getFileType().contains("xls")) {
                        if (chatMessages.get(position).getFilePath() != null && !chatMessages.get(position).getFilePath().isEmpty()) {
                            openXLSFile(chatMessages.get(position).getFilePath(), chatMessages.get(position).getFileName());
                        } else {
                            //Download attachments
                            if (!NetworkUtil.getConnectivityStatusString(mContext).equals("Not connected to Internet")) {
                                new FileDownloadTask(chatMessages.get(position),
                                        itemViewHolder.nameTextView.getText().toString()).execute();
                            } else {
                                //  CustomisedToast.error(mContext, "Please check internet connection.").show();
                            }
                        }
                    } else if (chatMessages.get(position).getFileType().contains("pdf")) {
                        if (chatMessages.get(position).getFilePath() != null && !chatMessages.get(position).getFilePath().isEmpty()) {
                            openPDFFile(chatMessages.get(position).getFilePath(), chatMessages.get(position).getFileName());
                        } else {
                            //Download attachments
                            if (!NetworkUtil.getConnectivityStatusString(mContext).equals("Not connected to Internet")) {
                                new FileDownloadTask(chatMessages.get(position),
                                        itemViewHolder.nameTextView.getText().toString()).execute();
                            } else {
                                //  CustomisedToast.error(mContext, "Please check internet connection.").show();
                            }
                        }
                    } else if (chatMessages.get(position).getFileType().contains("doc")) {
                        if (chatMessages.get(position).getFilePath() != null && !chatMessages.get(position).getFilePath().isEmpty()) {
                            openDOCFile(chatMessages.get(position).getFilePath(), chatMessages.get(position).getFileName());
                        } else {
                            //Download attachments
                            if (!NetworkUtil.getConnectivityStatusString(mContext).equals("Not connected to Internet")) {
                                new FileDownloadTask(chatMessages.get(position), itemViewHolder.nameTextView.getText().toString()).execute();
                            } else {
                                //  CustomisedToast.error(mContext, "Please check internet connection.").show();
                            }
                        }
                    }
                }
            });
        }

    }

    private String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        try {
            File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File dir = new File(SDCardRoot.getAbsolutePath() + "/Android/data/" + mContext.getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (file.createNewFile()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(mContext, imageToSave);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        String selectedFilePath = finalFile.getPath();
        return selectedFilePath.toString();
    }

    private void openXLSFile(String filePath, String fileName) {
        if (filePath != null) {
            /*File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    , fileName);*/
            File file = new File(filePath);
            Uri path = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File files = new File(path.getPath());
                path = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", files);
            }
            Intent excelIntent = new Intent(Intent.ACTION_VIEW);
            excelIntent.setDataAndType(path, "application/vnd.ms-excel");
            excelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                mContext.startActivity(excelIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, mContext.getString(R.string.excel_app_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openPDFFile(String filePath, String fileName) {
        if (filePath != null) {
            /*File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    , fileName);*/
            File file = new File(filePath);
            Uri path = Uri.fromFile(file);
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    File files = new File(path.getPath());
                    path = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", files);
                }
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfIntent.setDataAndType(path, "application/pdf");
                try {
                    mContext.startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, mContext.getString(R.string.pdf_app_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openDOCFile(String filePath, String fileName) {
        if (filePath != null) {
            /*File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    , fileName);*/
            File file = new File(filePath);
            Uri path = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File files = new File(path.getPath());
                path = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", files);
            }
            Intent docIntent = new Intent(Intent.ACTION_VIEW);
            docIntent.setDataAndType(path, "application/msword");
            docIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                mContext.startActivity(docIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, mContext.getString(R.string.pdf_app_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void dialogImagePreview(String filePath, String name, String Url, final String filename,
                                    final ChatMessage chatMessage) {
        // dialog = new Dialog(mContext);
        dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar);
        //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.chat_image_dialog);
        final ImageView imageViewPreview = (ImageView) dialog.findViewById(R.id.chat_message_image);
        ImageView imageViewCancel = (ImageView) dialog.findViewById(R.id.chat_image_preview_cancel);
        TextView nameUserTextView = (TextView) dialog.findViewById(R.id.name_user_textView);
        TextView timeTextView = (TextView) dialog.findViewById(R.id.time_textView);
        nameUserTextView.setText(name);
        timeTextView.setVisibility(View.GONE);
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            //imageViewPreview.setImageBitmap(bitmap);
            Glide.with(mContext).load(bitmapToByte(bitmap))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

           /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(mContext)
                    .load(stream.toByteArray())
                    .asBitmap()
                    .into(imageViewPreview);*/
        } else {
            Glide.with(mContext)
                    .load(Url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new BitmapImageViewTarget(imageViewPreview) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            super.onResourceReady(resource, glideAnimation);
                            myBitmap = resource;
                            imageViewPreview.setImageBitmap(resource);
                            downloadImageAndStoreIntoDb(chatMessage, filename, myBitmap);
                        }

                    });
        }
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void downloadImageAndStoreIntoDb(ChatMessage chatMessage, String filename, Bitmap myBitmap) {
        //Code to be executed after desired time
        chatMessage.setSyncId("");
        chatMessage.setFilePath(createDirectoryAndSaveFile(myBitmap, filename));
        if (!databaseHandler.checkChatId(chatMessage.getChatId())) {
            databaseHandler.insertChatMessages(chatMessage);
        } else databaseHandler.updatePathWithChatId(chatMessage);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_chat_message, parent, false);
            return new ItemViewHolder(itemView);
        } /*else if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_footer_layout, parent, false);
            return new FooterViewHolder(itemView);
        }*/ else return null;

    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    public void add(ChatMessage message) {
        chatMessages.add(0, message);
    }

    private void setTimeTextVisibility(long now_tm, long msg_tm, TextView timeText, View dividerTop) {
        Date nowDate = new Date();
        nowDate.setTime(now_tm);
        Date msgDate = new Date();
        msgDate.setTime(msg_tm);
        Calendar now_calendar = Calendar.getInstance();
        now_calendar.setTimeInMillis(now_tm);
        Calendar now = Calendar.getInstance();
        Calendar msg_calendar = Calendar.getInstance();
        msg_calendar.setTimeInMillis(msg_tm);
        if (msg_tm == 0) {
            timeText.setVisibility(View.VISIBLE);
            dividerTop.setVisibility(View.VISIBLE);
            if (DateUtils.isToday(now_calendar.getTimeInMillis())) {
                timeText.setText(mContext.getString(R.string.today));
            } else if ((now.get(Calendar.DATE) - now_calendar.get(Calendar.DATE) == 1) && (now.get(Calendar.MONTH) ==
                    now_calendar.get(Calendar.MONTH)) && (now.get(Calendar.YEAR) ==
                    now_calendar.get(Calendar.YEAR))) {
                timeText.setText(mContext.getString(R.string.yesterday));
            } else
                timeText.setText("" + new SimpleDateFormat("dd MMM, yyyy").format(new Date(now_tm)));
        } else {
            if (msgDate.before(nowDate)) {

                boolean sameDay = now_calendar.get(Calendar.YEAR) == msg_calendar.get(Calendar.YEAR) &&
                        now_calendar.get(Calendar.MONTH) == msg_calendar.get(Calendar.MONTH)
                        && now_calendar.get(Calendar.DAY_OF_MONTH) == msg_calendar.get(Calendar.DAY_OF_MONTH);
                if (sameDay) {
                    timeText.setVisibility(View.GONE);
                    dividerTop.setVisibility(View.GONE);
                    timeText.setText("");
                } else {
                    timeText.setVisibility(View.VISIBLE);
                    dividerTop.setVisibility(View.VISIBLE);
                    if (DateUtils.isToday(now_calendar.getTimeInMillis())) {
                        timeText.setText(mContext.getString(R.string.today));
                    } else if ((now.get(Calendar.DATE) - now_calendar.get(Calendar.DATE) == 1) && (now.get(Calendar.MONTH) ==
                            now_calendar.get(Calendar.MONTH)) && (now.get(Calendar.YEAR) ==
                            now_calendar.get(Calendar.YEAR))) {
                        timeText.setText(mContext.getString(R.string.yesterday));
                    } else {
                        timeText.setText("" + new SimpleDateFormat("dd MMM, yyyy").format(new Date(now_tm)));
                    }
                }
            } else {
                timeText.setVisibility(View.GONE);
                dividerTop.setVisibility(View.GONE);
                timeText.setText("");
            }
        }


    }

    @Override
    public int getItemCount() {
        /*if (chatMessages.size() > (mContext.getResources().getInteger(R.integer.chat_items_count) - 1)) {
            if (status == 0)
                return this.chatMessages.size() + 1;
            else return this.chatMessages.size();
        } else*/
        return this.chatMessages.size();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
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
            File dir = new File(SDCardRoot.getAbsolutePath() + "/Android/data/" + mContext.getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
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
            urlConnection.disconnect();
            if (downloadedSize == totalSize) filepath = file.getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            filepath = null;
            e.printStackTrace();
        }
        return filepath;
    }

    public interface ChatMessageListener {
        void onMessageRowClicked(int position);
    }

    public class FileDownloadTask extends AsyncTask<String, String, String> {
        ChatMessage chatMessage;
        String name;

        public FileDownloadTask(ChatMessage chatMessage, String name) {
            this.chatMessage = chatMessage;
            this.name = name;
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
            if (chatMessage.getFileUrl() != null && !chatMessage.getFileUrl().isEmpty() &&
                    (chatMessage.getFileType().equals("jpg") || chatMessage.getFileType().equals("png") ||
                            chatMessage.getFileType().equals("jpeg") || chatMessage.getFileType().equals("bmp"))) {
                if (getBitmapFromURL(chatMessage.getFileUrl()) != null) {
                    chatMessage.setFilePath(createDirectoryAndSaveFile(getBitmapFromURL(chatMessage.getFileUrl()), chatMessage.getFileName()));
                }
            } else if (chatMessage.getFileUrl() != null && !chatMessage.getFileUrl().isEmpty() && chatMessage.getFileType().contains("xls")) {
                //new DownloadFileAsync(chatMessage.getFileUrl(), chatMessage.getFileName()).execute();
                chatMessage.setFilePath(getDownloadedPath(chatMessage.getFileUrl(), chatMessage.getFileName()));
            } else if (chatMessage.getFileUrl() != null && !chatMessage.getFileUrl().isEmpty() && chatMessage.getFileType().contains("pdf")) {
                //new DownloadFileAsync(chatMessage.getFileUrl(), chatMessage.getFileName()).execute();
                chatMessage.setFilePath(getDownloadedPath(chatMessage.getFileUrl(), chatMessage.getFileName()));
            }
            if (!databaseHandler.checkChatId(chatMessage.getChatId())) {
                databaseHandler.insertChatMessages(chatMessage);
            } else databaseHandler.updatePathWithChatId(chatMessage);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // dismiss progress dialog and update ui
            if (chatMessage.getFileType().equals("jpg") || chatMessage.getFileType().equals("png") ||
                    chatMessage.getFileType().equals("jpeg") || chatMessage.getFileType().equals("bmp")) {
                if (chatMessage.getFilePath() != null && !chatMessage.getFilePath().isEmpty()) {
                    //dialogImagePreview(chatMessage.getFilePath(), name);
                }
            } else if (chatMessage.getFileType().contains("xls")) {
                if (chatMessage.getFilePath() != null && !chatMessage.getFilePath().isEmpty()) {
                    openXLSFile(chatMessage.getFilePath(), chatMessage.getFileName());
                }
            } else if (chatMessage.getFileType().contains("pdf")) {
                if (chatMessage.getFilePath() != null && !chatMessage.getFilePath().isEmpty()) {
                    openPDFFile(chatMessage.getFilePath(), chatMessage.getFileName());
                }
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTimePeriod;
        private View dividerTop;
        private LinearLayout horizontalLayout, imageLayout, fileLayout;
        private CircleImageView profileImageView;
        private TextView nameTextView;
        private TextView timeTextView, fileNameTextView;
        private TextView messageTextView, captionTextView;
        private ImageView imageView, fileImageView;
        private CardView cardView;

        public ItemViewHolder(View view) {
            super(view);
            textViewTimePeriod = (TextView) view.findViewById(R.id.textView_timePeriod);
            dividerTop = view.findViewById(R.id.divider_top);
            horizontalLayout = (LinearLayout) view.findViewById(R.id.horizontal_layout);
            imageLayout = (LinearLayout) view.findViewById(R.id.imageLayout);
            fileLayout = (LinearLayout) view.findViewById(R.id.file_layout);
            fileNameTextView = (TextView) view.findViewById(R.id.file_name_textView);
            fileImageView = (ImageView) view.findViewById(R.id.file_imageView);
            profileImageView = (CircleImageView) view.findViewById(R.id.profile_imageView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            timeTextView = (TextView) view.findViewById(R.id.time_textView);
            messageTextView = (TextView) view.findViewById(R.id.message_textView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            captionTextView = (TextView) view.findViewById(R.id.caption_textView);
            cardView = (CardView) view.findViewById(R.id.cardView);
            textViewTimePeriod.setTypeface(Typeface.SANS_SERIF);
            nameTextView.setTypeface(Typeface.SANS_SERIF);
            timeTextView.setTypeface(Typeface.SANS_SERIF);
            messageTextView.setTypeface(Typeface.SANS_SERIF);
        }

    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;

        public FooterViewHolder(View view) {
            super(view);
            footerText = (TextView) view.findViewById(R.id.load_more_textView);
        }
    }

}
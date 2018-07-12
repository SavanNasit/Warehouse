package com.accrete.sixorbit.activity.Settings;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.cameraCapture.AlbumStorageDirFactory;
import com.accrete.sixorbit.cameraCapture.BaseAlbumDirFactory;
import com.accrete.sixorbit.cameraCapture.BitmapUtils;
import com.accrete.sixorbit.cameraCapture.FroyoAlbumDirFactory;
import com.accrete.sixorbit.fragment.Drawer.AllDatePickerFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CityName;
import com.accrete.sixorbit.model.CountryList;
import com.accrete.sixorbit.model.StateList;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by poonam on 20/10/17.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, PassDateToCounsellor {
    private static final int REQUEST_MULTIPLE_PERMISSION = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_IMAGE_GALLERY_PIC = 12;
    private final String[] PERMISSION_STORAGE =
            new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE

            };
    List<String> permissionsNeeded = new ArrayList<>();
    boolean isFirstTime = true;
    private ProgressBar progressBar;
    private String userChoosenTask, stringUsername, stringDateOfBirth, stringUserBio, stringDoorNumber,
            stringStreet, stringLocality, stringZipcode, stringCity, imageURL, dateOfBirth, userName,
            doorNumber, street, country, state, city, locality, pincode, userBio;
    private Toolbar toolbar;
    private TextInputEditText editProfileName;
    private EditText editProfileDateOfBirth;
    private EditText editProfileDescription;
    private LinearLayout addressDetails;
    private TextInputEditText editProfileDoorNumber;
    private TextInputEditText editProfileStreet;
    private AutoCompleteTextView cityTextAutoCompleteTextView;
    private AutoCompleteTextView countryAutoCompleteTextView;
    private ImageButton countryClearImageButton;
    private AutoCompleteTextView stateAutoCompleteTextView;
    private ImageButton stateClearImageButton;
    private ImageButton cityClearImageButton;
    private TextInputEditText editProfileLocality;
    private TextInputEditText editProfilePincode;
    private TextView editProfileSave;
    private ImageView editProfileUserImage;
    private ArrayList<String> listStates;
    private ArrayList<String> countriesList;
    private HashMap<String, String> hashMapcountries = new HashMap<String, String>(),
            hashMapStates = new HashMap<String, String>();
    private AllDatePickerFragment datePickerFragment;
    private int SELECT_FILE = 1;
    private Bitmap bitmap;
    private TextView editTextView;
    private Dialog dialog;
    private AlbumStorageDirFactory mAlbumStorageDirFactory;
    private String mCurrentPhotoPath;
    private String defaultAddressCountryCode, defaultAddressStateCode, strCountries, strState, status, strOfficeCity,
            strOfficeCountry, strOfficeState, addressCountryCode, addressStateCode;
    private List<CountryList> countryListArrayList = new ArrayList<>();
    private List<StateList> stateListArrayList = new ArrayList<>();
    private List<CityName> cityNameArrayList = new ArrayList<>();
    private ArrayAdapter<CityName> cityNameArrayAdapter;
    private boolean imageURLPath;
    private DatabaseHandler databaseHandler;

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Version Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        setContentView(R.layout.activity_edit_profile);
        initializeView();

    }

    private void initializeView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        editTextView = (TextView) findViewById(R.id.edit_textView);
        editProfileUserImage = (ImageView) findViewById(R.id.edit_profile_user_image);
        editProfileName = (TextInputEditText) findViewById(R.id.edit_profile_name);
        editProfileDateOfBirth = (TextInputEditText) findViewById(R.id.edit_profile_date_of_birth);
        editProfileDescription = (EditText) findViewById(R.id.edit_profile_description);
        addressDetails = (LinearLayout) findViewById(R.id.address_details);
        editProfileDoorNumber = (TextInputEditText) findViewById(R.id.edit_profile_door_number);
        editProfileStreet = (TextInputEditText) findViewById(R.id.edit_profile_street);
        countryAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.edit_profile_country);
        stateAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.edit_profile_state);
        cityTextAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.edit_profile_city);
        editProfileLocality = (TextInputEditText) findViewById(R.id.edit_profile_locality);
        editProfilePincode = (TextInputEditText) findViewById(R.id.edit_profile_pincode);
        editProfileSave = (TextView) findViewById(R.id.edit_profile_save);
        countryClearImageButton = (ImageButton) findViewById(R.id.country_clear_imageButton);
        stateClearImageButton = (ImageButton) findViewById(R.id.state_clear_imageButton);
        cityClearImageButton = (ImageButton) findViewById(R.id.city_clear_imageButton);


        //TODO Added on 22nd May
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        databaseHandler = new DatabaseHandler(this);


        toolbar.setTitle(getString(R.string.title_edit_profile));
        progressBar = (ProgressBar) findViewById(R.id.edit_profile_progress_bar);

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //   stateAdapter();
        // countryAdapter();

        editProfileDateOfBirth.setOnClickListener(this);
        editProfileSave.setOnClickListener(this);
        editProfileUserImage.setOnClickListener(this);
        editTextView.setOnClickListener(this);
        cityClearImageButton.setOnClickListener(this);
        stateClearImageButton.setOnClickListener(this);
        countryClearImageButton.setOnClickListener(this);

        countryAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryAutoCompleteTextView.showDropDown();
            }
        });


        stateAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAutoCompleteTextView.showDropDown();
            }
        });

        imageURL = AppPreferences.getPhoto(getApplicationContext(), AppUtils.USER_PHOTO);
        if (imageURL != null && !imageURL.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(imageURL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_neutral_profile)
                    .into(editProfileUserImage);

            //TODO If image is stored in given URL
            if (imageURL.contains("http") && isImage(imageURL)) {
                //Remove Profile Pic and set boolean true
                AppPreferences.setBoolean(EditProfileActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, false);
            } else
                //TODO If image has internal storage path
                if (!imageURL.contains("http") && validImagePath(imageURL)) {
                    //Remove Profile Pic and set boolean true
                    AppPreferences.setBoolean(EditProfileActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, false);
                } else {
                    //Remove Profile Pic and set boolean true
                    AppPreferences.setBoolean(EditProfileActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, true);
                }
        } else {
            //Remove Profile Pic and set boolean true
            AppPreferences.setBoolean(EditProfileActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, true);
        }
        setProfileData();

        //Underlined Edit text below profile Image
        SpannableString editString = new SpannableString("Edit");
        editString.setSpan(new UnderlineSpan(), 0, editString.length(), 0);
        editTextView.setText(editString);

    }

    private void dialogImagePreview(String fileUrl) {
        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        dialog.setContentView(R.layout.chat_image_dialog);
        final ImageView imageViewPreview = (ImageView) dialog.findViewById(R.id.chat_message_image);
        ImageView imageViewCancel = (ImageView) dialog.findViewById(R.id.chat_image_preview_cancel);
        TextView nameUserTextView = (TextView) dialog.findViewById(R.id.name_user_textView);
        TextView timeTextView = (TextView) dialog.findViewById(R.id.time_textView);
        nameUserTextView.setVisibility(View.GONE);
        timeTextView.setVisibility(View.GONE);
        if (fileUrl != null && !fileUrl.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(fileUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_neutral_profile)
                    .into(imageViewPreview);
        }
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dialogImageBitmapPreview(Bitmap bitmap) {
        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        dialog.setContentView(R.layout.chat_image_dialog);
        final ImageView imageViewPreview = (ImageView) dialog.findViewById(R.id.chat_message_image);
        ImageView imageViewCancel = (ImageView) dialog.findViewById(R.id.chat_image_preview_cancel);
        TextView nameUserTextView = (TextView) dialog.findViewById(R.id.name_user_textView);
        TextView timeTextView = (TextView) dialog.findViewById(R.id.time_textView);
        nameUserTextView.setVisibility(View.GONE);
        timeTextView.setVisibility(View.GONE);
        imageViewPreview.setImageBitmap(bitmap);
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentPhotoPath != null) {
            outState.putString("cameraImageUri", mCurrentPhotoPath);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            mCurrentPhotoPath = savedInstanceState.getString("cameraImageUri");
        }
    }

    private void setProfileData() {
        userName = AppPreferences.getUserName(getApplicationContext(), AppUtils.USER_NAME);
        userBio = AppPreferences.getUserBio(getApplicationContext(), AppUtils.USER_BIO);
        dateOfBirth = AppPreferences.getUserDateOfBirth(getApplicationContext(), AppUtils.USER_DOB);
        doorNumber = AppPreferences.getUserAddressLine1(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_ONE);
        street = AppPreferences.getUserAddressLine2(getApplicationContext(), AppUtils.USER_ADDRESS_LINE_TWO);
        country = AppPreferences.getUserCountry(getApplicationContext(), AppUtils.USER_COUNTRY);
        state = AppPreferences.getUserState(getApplicationContext(), AppUtils.USER_STATE);
        city = AppPreferences.getUserCity(getApplicationContext(), AppUtils.USER_CITY);
        locality = AppPreferences.getUserLocality(getApplicationContext(), AppUtils.USER_LOCALITY);
        pincode = AppPreferences.getUserZipcode(getApplicationContext(), AppUtils.USER_ZIP_CODE);

        if (userName != null && !userName.isEmpty() && !userName.equals("null")) {
            editProfileName.setText(userName);
        }

        if (dateOfBirth != null && !dateOfBirth.isEmpty() && !dateOfBirth.equals("null") && !dateOfBirth.equals("0000-00-00")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                editProfileDateOfBirth.setText(dateFormat.format(format.parse(dateOfBirth)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            stringDateOfBirth = dateOfBirth;
        } else {
            editProfileDateOfBirth.setText("");
        }

        if (userBio != null && !userBio.isEmpty() && !userBio.equals("null")) {
            editProfileDescription.setText(userBio);
        }

        if (doorNumber != null && !doorNumber.isEmpty() && !doorNumber.equals("null")) {
            editProfileDoorNumber.setText(doorNumber);
        }
        if (street != null && !street.isEmpty() && !street.equals("null")) {
            editProfileStreet.setText(street);
        }
        if (country != null && !country.isEmpty() && !country.equals("null")) {
            //          countryAutoCompleteTextView.setText(country);
        }
        if (state != null && !state.isEmpty() && !state.equals("null")) {
            //          stateAutoCompleteTextView.setText(state);
        }
        if (city != null && !city.isEmpty() && !city.equals("null")) {
            //    cityTextAutoCompleteTextView.setText(city);
        }

        //Set Countries
        if (AppPreferences.getUserCountryCode(this, AppUtils.USER_COUNTRY_CODE) != null ||
                !AppPreferences.getUserCountryCode(this, AppUtils.USER_COUNTRY_CODE).isEmpty()) {
            countryAutoCompleteTextView.setText(country);
            addressCountryCode = AppPreferences.getUserCountryCode(this, AppUtils.USER_COUNTRY_CODE);
            if (country.toString().trim().length() > 0) {
                countryAutoCompleteTextView.setEnabled(false);
            } else {
                countryAutoCompleteTextView.setEnabled(true);
            }

        }
        //Set States
        if (AppPreferences.getUserStateCode(this, AppUtils.USER_STATE_CODE) != null ||
                !AppPreferences.getUserStateCode(this, AppUtils.USER_STATE_CODE).isEmpty()) {
            stateAutoCompleteTextView.setText(state);
            addressStateCode = AppPreferences.getUserStateCode(this, AppUtils.USER_STATE_CODE);
            if (state.toString().trim().length() > 0) {
                stateAutoCompleteTextView.setEnabled(false);
            } else {
                stateAutoCompleteTextView.setEnabled(true);
            }
        }

        //Set City
        if (AppPreferences.getUserCity(this, AppUtils.USER_CITY) != null ||
                !AppPreferences.getUserCity(this, AppUtils.USER_CITY).isEmpty()) {
            cityTextAutoCompleteTextView.setText(city);
            if (city.toString().trim().length() > 0) {
                cityTextAutoCompleteTextView.setEnabled(false);
            } else {
                cityTextAutoCompleteTextView.setEnabled(true);
            }
        }

        //Set city code
        if (AppPreferences.getUserCityCode(this, AppUtils.USER_CITY_CODE) != null ||
                !AppPreferences.getUserCityCode(this, AppUtils.USER_CITY_CODE).isEmpty()) {
            strOfficeCity = AppPreferences.getUserCityCode(this, AppUtils.USER_CITY_CODE);
        }

        if (locality != null && !locality.isEmpty() && !locality.equals("null")) {
            editProfileLocality.setText(locality);
        }
        if (pincode != null && !pincode.isEmpty() && !pincode.equals("null")) {
            editProfilePincode.setText(pincode);
        }

        getCountriesStatesData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile_date_of_birth:
                dateOfBirth();
                break;
            case R.id.edit_profile_save:
                editProfileSave.setEnabled(false);
                sendDataToEditProfile();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editProfileSave.setEnabled(true);
                    }
                }, 3000);
                break;
            case R.id.edit_profile_user_image:
                if (!(AppPreferences.getPhoto(EditProfileActivity.this, AppUtils.USER_PHOTO)).contains("null")) {
                    if (imageURL != null && !imageURL.isEmpty()) {
                        dialogImagePreview(imageURL);
                    } else {
                        dialogImageBitmapPreview(bitmap);
                    }
                }
                break;
            case R.id.edit_textView:
                dialogSelectImage();
                break;
            case R.id.country_clear_imageButton:
                //Clearing all values of country
                countryAutoCompleteTextView.getText().clear();
                strOfficeCountry = "";
                country = "";
                countryAutoCompleteTextView.setEnabled(true);
                //Clearing All values of State if country gets removed
                stateAutoCompleteTextView.getText().clear();
                strOfficeState = "";
                state = "";
                strState = "";
                stateAutoCompleteTextView.setEnabled(true);
                //Clearing all values of city as well if state gets removed
                cityTextAutoCompleteTextView.getText().clear();
                strOfficeCity = "";
                city = "";
                stringCity = "";
                cityTextAutoCompleteTextView.setEnabled(true);
                break;
            case R.id.city_clear_imageButton:
                //Clearing all values of city
                cityTextAutoCompleteTextView.getText().clear();
                strOfficeCity = "";
                city = "";
                stringCity = "";
                cityTextAutoCompleteTextView.setEnabled(true);
                break;
            case R.id.state_clear_imageButton:
                //Clearing All values of State
                stateAutoCompleteTextView.getText().clear();
                strOfficeState = "";
                state = "";
                strState = "";
                stateAutoCompleteTextView.setEnabled(true);
                //Clearing all values of city as well if state gets removed
                cityTextAutoCompleteTextView.getText().clear();
                strOfficeCity = "";
                city = "";
                stringCity = "";
                cityTextAutoCompleteTextView.setEnabled(true);
                break;
        }

    }

    public boolean onDateSet(int year, int month, int day) {
        boolean flagValidDate;
        Calendar userAge = new GregorianCalendar(year, month, day);
        Calendar minAdultAge = new GregorianCalendar();
        minAdultAge.add(Calendar.YEAR, -18);
        if (minAdultAge.before(userAge)) {
            Toast.makeText(this, getString(R.string.date_of_birth_error), Toast.LENGTH_SHORT).show();
            flagValidDate = false;
        } else {
            flagValidDate = true;
        }
        return flagValidDate;
    }

    /* private boolean checkStateOnFocus() {
         //Clear State
         stringStateId = stateAutoCompleteTextView.getText().toString();
         for (int i = 0; i < stateListArrayList.size(); i++) {
             String temp = listStates.get(i);
             if (stringStateId.compareTo(temp) == 0) {
                 return true;
             }
         }
         stateAutoCompleteTextView.setText("");
         return false;
     }
 */
    private void sendDataToEditProfile() {

        stringUsername = editProfileName.getText().toString().trim();
        stringUserBio = editProfileDescription.getText().toString().trim();
        stringDoorNumber = editProfileDoorNumber.getText().toString().trim();
        stringCity = cityTextAutoCompleteTextView.getText().toString().trim();
        stringStreet = editProfileStreet.getText().toString().trim();
        stringLocality = editProfileLocality.getText().toString().trim();
        stringZipcode = editProfilePincode.getText().toString().trim();

        //Country
        if (addressCountryCode == null || addressCountryCode.isEmpty()) {
            strOfficeCountry = defaultAddressCountryCode;
        } else {
            strOfficeCountry = addressCountryCode;
        }

        //State
        if (addressStateCode == null || addressStateCode.isEmpty()) {
            strOfficeState = defaultAddressStateCode;
        } else {
            strOfficeState = addressStateCode;
        }

        //Pin code validations
        if (stringZipcode != null && stringZipcode.length() != 6) {
            Toast.makeText(EditProfileActivity.this, "Please enter 6 digits of pin code.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
            showLoader();
            editProfile();
        } else {
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
        /*} else {
            Toast.makeText(this, "Please select a valid state", Toast.LENGTH_SHORT).show();
            //stateAutoCompleteTextView.setText("");
        }*/
    }

    private void dateOfBirth() {
        datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_from));
    }

    private void editProfile() {
        try {
            task = getString(R.string.task_edit_profile);
            String userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            String accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getDomain(this, AppUtils.DOMAIN);
            MultipartBody.Part propertyImagePart = null;
            Call call;

            if (stringDateOfBirth == null) {
                stringDateOfBirth = "";
            }
            progressBar.setMax(100);
            progressBar.setVisibility(View.VISIBLE);
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            if (mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
                File sourceFile = new File(mCurrentPhotoPath);
                RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), sourceFile);
                propertyImagePart = MultipartBody.Part.createFormData("image", mCurrentPhotoPath, propertyImage);
                call = apiService.editProfileWithMultipart(version, key, task, userId, accessToken, stringUsername,
                        stringDateOfBirth, stringUserBio, stringDoorNumber, stringStreet, stringLocality,
                        stringZipcode, strOfficeState, strOfficeCountry, stringCity, propertyImagePart);
            } else {
                call = apiService.editProfile(version, key, task, userId, accessToken, stringUsername,
                        stringDateOfBirth, stringUserBio, stringDoorNumber, stringStreet, stringLocality,
                        stringZipcode, strOfficeState, strOfficeCountry, stringCity);
            }

            Log.v("Request", String.valueOf(call));
            Log.v("url", String.valueOf(call.request().url()));

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, retrofit2.Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
                        if (EditProfileActivity.this != null) {
                            Toast.makeText(EditProfileActivity.this, getString(R.string.profile_edit_success), Toast.LENGTH_SHORT).show();
                            setProfileDataAfterSuccess();

                            //Add Profile Pic and set boolean false
                            AppPreferences.setBoolean(EditProfileActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, false);

                            Intent intent = new Intent();
                            setResult(AppUtils.PROFILE_REQUEST_CODE, intent);
                            finish();

                            progressBar.setVisibility(View.GONE);
                        }
                    } //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        if (EditProfileActivity.this != null) {
                            progressBar.setVisibility(View.GONE);
                            //Logout
                            Constants.logoutWrongCredentials(EditProfileActivity.this, apiResponse.getMessage());
                        }
                    } else {
                        if (EditProfileActivity.this != null) {
                            Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    hideLoader();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (EditProfileActivity.this != null) {
                        hideLoader();
                        Toast.makeText(EditProfileActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                        t.printStackTrace();
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    private void deleteProfileImage() {
        task = getString(R.string.delete_profile_image);
        String userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
        String accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getDomain(this, AppUtils.DOMAIN);

        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.deleteProfileImage(version, key, task, userId, accessToken);
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    if (EditProfileActivity.this != null) {
                        Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        AppPreferences.setPhoto(EditProfileActivity.this, AppUtils.USER_PHOTO, "null");
                        editProfileUserImage.setImageResource(R.drawable.icon_neutral_profile);
                        progressBar.setVisibility(View.GONE);

                        //Remove Profile Pic and set boolean true
                        AppPreferences.setBoolean(EditProfileActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG, true);
                        mCurrentPhotoPath = null;
                        imageURL = null;
                        databaseHandler.updateAssigneeImagePath(userId);
                    }
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    if (EditProfileActivity.this != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    //Logout
                    Constants.logoutWrongCredentials(EditProfileActivity.this, apiResponse.getMessage());
                } else {
                    if (EditProfileActivity.this != null) {
                        Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (EditProfileActivity.this != null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this,
                            getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            }
        });
    }

    private void setProfileDataAfterSuccess() {

        if (stringUsername != null && !stringUsername.equals("null")) {
            AppPreferences.setUserName(EditProfileActivity.this, AppUtils.USER_NAME, stringUsername);
        }

        if (stringDateOfBirth != null && !stringDateOfBirth.equals("null")) {
            AppPreferences.setUserDateOfBirth(EditProfileActivity.this, AppUtils.USER_DOB, stringDateOfBirth);
        }

        if (stringUserBio != null && !stringUserBio.equals("null")) {
            AppPreferences.setuserBio(EditProfileActivity.this, AppUtils.USER_BIO, stringUserBio);
        } else {
            AppPreferences.setuserBio(EditProfileActivity.this, AppUtils.USER_BIO, "");
        }

        if (stringDoorNumber != null && !stringDoorNumber.equals("null")) {
            AppPreferences.setUserAddressLine1(EditProfileActivity.this, AppUtils.USER_ADDRESS_LINE_ONE, stringDoorNumber);
        }
        if (stringStreet != null && !stringStreet.equals("null")) {
            AppPreferences.setUserAddressLine2(EditProfileActivity.this, AppUtils.USER_ADDRESS_LINE_TWO, stringStreet);
        }
        if (addressCountryCode != null && !addressCountryCode.isEmpty() &&
                !addressCountryCode.equals("null")
                && countryAutoCompleteTextView.getText().toString() != null
                && !countryAutoCompleteTextView.getText().toString().isEmpty()
                && !countryAutoCompleteTextView.getText().toString().equals("null") &&
                !countryAutoCompleteTextView.isEnabled()) {
            AppPreferences.setUserCountry(EditProfileActivity.this, AppUtils.USER_COUNTRY,
                    countryAutoCompleteTextView.getText().toString());
        } else {
            AppPreferences.setUserCountry(EditProfileActivity.this, AppUtils.USER_COUNTRY,
                    "");
        }
        if (addressStateCode != null && !addressStateCode.isEmpty() &&
                !addressStateCode.equals("null") && stateAutoCompleteTextView.getText().toString() != null
                && !stateAutoCompleteTextView.getText().toString().equals("null") &&
                !stateAutoCompleteTextView.isEnabled()
                ) {
            AppPreferences.setUserState(EditProfileActivity.this, AppUtils.USER_STATE,
                    stateAutoCompleteTextView.getText().toString());
        } else {
            AppPreferences.setUserState(EditProfileActivity.this, AppUtils.USER_STATE,
                    "");
        }
        if (stringCity != null && !stringCity.equals("null")) {
            AppPreferences.setUserCity(EditProfileActivity.this, AppUtils.USER_CITY, stringCity);
        } else {
            AppPreferences.setUserCity(EditProfileActivity.this, AppUtils.USER_CITY, "");
        }
        if (stringLocality != null && !stringLocality.equals("null")) {
            AppPreferences.setUserLocality(EditProfileActivity.this, AppUtils.USER_LOCALITY, stringLocality);
        }
        if (stringZipcode != null && !stringZipcode.equals("null")) {
            AppPreferences.setUserZipCode(EditProfileActivity.this, AppUtils.USER_ZIP_CODE, stringZipcode);
        } else {
            AppPreferences.setUserZipCode(EditProfileActivity.this, AppUtils.USER_ZIP_CODE, "");
        }
        if (mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty() && !mCurrentPhotoPath.equals("null")) {
            AppPreferences.setPhoto(EditProfileActivity.this, AppUtils.USER_PHOTO, mCurrentPhotoPath);
        } else {
            AppPreferences.setPhoto(EditProfileActivity.this, AppUtils.USER_PHOTO, imageURL);
        }

        //Save State Code
        if (strOfficeCountry != null && !strOfficeCountry.isEmpty() &&
                !strOfficeCountry.equals("null")) {
            AppPreferences.setUserCountryCode(EditProfileActivity.this, AppUtils.USER_COUNTRY_CODE, addressCountryCode);
        }
        if (strOfficeState != null && !strOfficeState.isEmpty()) {
            AppPreferences.setUserStateCode(EditProfileActivity.this, AppUtils.USER_STATE_CODE, addressStateCode);
        }
        if (strOfficeCity != null && !strOfficeCity.isEmpty()) {
            AppPreferences.setUserCityCode(EditProfileActivity.this, AppUtils.USER_CITY_CODE,
                    strOfficeCity);
        }

    }

    @TargetApi(23)
    private boolean verifyPermission() {
        if (checkAllPermission()) {
            if (permissionsNeeded.size() > 0 && !isFirstTime) {
                for (String permission : permissionsNeeded) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        displayPermissionDialog("Would like to grant access to take picture from camera", PERMISSION_STORAGE, REQUEST_MULTIPLE_PERMISSION);
                        break;
                    }
                }
            } else {
                isFirstTime = false;
                requestPermissions(PERMISSION_STORAGE, REQUEST_MULTIPLE_PERMISSION);
            }
            return true;
        }
        return false;
    }

    @TargetApi(23)
    private boolean checkAllPermission() {
        boolean isPermissionRequired = false;
        for (String permission : PERMISSION_STORAGE) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
                isPermissionRequired = true;
            }
        }
        return isPermissionRequired;
    }

    @TargetApi(23)
    private void displayPermissionDialog(String msg, final String[] permission, final int resultCode) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog
                .Builder(this)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        requestPermissions(permission, resultCode);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        alertDialog.show();

    }

    private Intent dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f;
        try {
            f = mAlbumStorageDirFactory.setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(EditProfileActivity.this, this.getApplicationContext().getPackageName() +
                    ".fileprovider", f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            return takePictureIntent;
        } catch (IOException e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }

        return null;
    }

    private void dialogSelectImage() {
        final String[] items;
        if (!(AppPreferences.getPhoto(EditProfileActivity.this, AppUtils.USER_PHOTO)).contains("null") &&
                !AppPreferences.getBoolean(EditProfileActivity.this, AppUtils.USER_PROFILE_PICTURE_REMOVE_FLAG)) {
            items = new String[]{getString(R.string.take_photo), getString(R.string.choose_from_gallery)
                    , getString(R.string.delete_photo), getString(R.string.cancel)};
        } else {
            items = new String[]{getString(R.string.take_photo), getString(R.string.choose_from_gallery)
                    , getString(R.string.cancel)};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_profle_image));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Intent pictureIntent = null;
                boolean result = false;
                int resultCode = 0;
                result = true;
                if (items[item].equals(getString(R.string.take_photo))) {
                    userChoosenTask = getString(R.string.take_photo);
                    if (result) {
                        //cameraIntent();
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (verifyPermission()) return;
                        }
                        pictureIntent = dispatchTakePictureIntent();
                        resultCode = REQUEST_IMAGE_CAPTURE;
                        if (pictureIntent != null && pictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(pictureIntent, resultCode);
                        }
                    }
                } else if (items[item].equals(getString(R.string.choose_from_gallery))) {
                    userChoosenTask = getString(R.string.choose_from_gallery);
                    if (result) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (verifyGalleryPermission()) return;
                        }
                        pictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        resultCode = REQUEST_IMAGE_GALLERY_PIC;
                        if (pictureIntent != null && pictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(pictureIntent, resultCode);
                        }
                    }
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                } else if (items[item].equals(getString(R.string.delete_photo))) {
                    dialog.dismiss();
                    deletePhotoDialog();
                }
            }
        });
        builder.show();
    }

    @TargetApi(23)
    private boolean verifyGalleryPermission() {
        if (checkSelfPermission(PERMISSION_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(PERMISSION_STORAGE[0])) {
                displayPermissionDialog("Would like to grant access to " +
                                getString(R.string.app_name) + " to read your gallery",
                        new String[]{PERMISSION_STORAGE[0]}, REQUEST_PERMISSION);
            } else {
                requestPermissions(new String[]{PERMISSION_STORAGE[0]}, REQUEST_PERMISSION);
            }
            return true;
        }
        return false;
    }

    private void deletePhotoDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to remove photo?");
        builder.setNegativeButton("No, cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                        //Call Api to remove image also
                        if (!NetworkUtil.getConnectivityStatusString(EditProfileActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                            deleteProfileImage();
                        } else {
                            Toast.makeText(EditProfileActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void passDate(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            stringDateOfBirth = s;
            Date startDate = formatter.parse(stringDateOfBirth);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            //Day of the month :)
            calendar.get(Calendar.SECOND); //number of seconds
            int yy = calendar.get(Calendar.YEAR);
            ;
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            if (onDateSet(yy, mm, dd)) {
                stringDateOfBirth = targetFormat.format(startDate);
                editProfileDateOfBirth.setText(s);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void passTime(String s) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY_PIC) {
                onSelectFromGalleryResult(data);
                if (mCurrentPhotoPath != null) {
                    galleryAddPic();
                    crop(mCurrentPhotoPath);
                } else {
                    Toast.makeText(this, "Unable to select picture please retry again", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (mCurrentPhotoPath != null) {
                    galleryAddPic();
                    crop(mCurrentPhotoPath);
                    //  mCurrentPhotoPath = null;
                }
            }

        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void crop(final String photoPath) {
        getBitmap(photoPath);
    }

    private void getBitmap(final String photoPath) {
        //updateUi(true);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                return BitmapUtils.decodeSampledBitmapFromFile(photoPath, getResources().getDimensionPixelOffset(R.dimen._70sdp),
                        getResources().getDimensionPixelOffset(R.dimen._70sdp));
            }

            @Override
            protected void onPostExecute(Bitmap bitmapImg) {
                super.onPostExecute(bitmapImg);
                //updateUi(false);
                if (bitmapImg != null) {
                    editProfileUserImage.setImageBitmap(bitmapImg);
                    imageURL = null;
                    bitmap = bitmapImg;
                }
            }
        };
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
        mCurrentPhotoPath = finalFile.getPath();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_MULTIPLE_PERMISSION:
                boolean isStartActivity = true;
                int position = 0;
                for (int permission : grantResults) {
                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        //DO NOTHING
                        Log.i("TAG", "Permission is granted");
                    } else {
                        isStartActivity = false;
                        position = permission;
                    }
                }
                if (isStartActivity) {
                    Intent pictureIntent = dispatchTakePictureIntent();
                    startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    String msg = String.format(Locale.ENGLISH, "%s permission is missing ", position == 1 ? "Camera" : "Read and write");
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pictureIntent, REQUEST_IMAGE_GALLERY_PIC);
                } else {
                    String msg = String.format(Locale.ENGLISH, "%s permission is missing ", "Read");
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    public void getCountryAndStates() {
        task = getString(R.string.add_customer_dynamic_fields);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {

                        for (final CountryList countryList : apiResponse.getData().getCountryList()) {
                            if (countryList != null) {
                                countryListArrayList.add(countryList);
                            }
                        }

                        for (final StateList stateList : apiResponse.getData().getStateList()) {
                            if (stateList != null) {
                                stateListArrayList.add(stateList);
                            }
                        }

                        defaultAddressCountryCode = apiResponse.getData().getDefaultCountry();
                        defaultAddressStateCode = apiResponse.getData().getDefaultState();

                        //Spinners
                        setSpinnerAdapter();

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(EditProfileActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCountriesStatesData() {
        status = NetworkUtil.getConnectivityStatusString(EditProfileActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getCountryAndStates();
        } else {
            Toast.makeText(EditProfileActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    private void setSpinnerAdapter() {
        //Country
        countryAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryAutoCompleteTextView.showDropDown();
            }
        });

        final ArrayAdapter<CountryList> countryArrayAdapter = new ArrayAdapter<CountryList>
                (EditProfileActivity.this, R.layout.simple_spinner_item, countryListArrayList);
        countryArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        countryAutoCompleteTextView.setAdapter(countryArrayAdapter);

        //when autocomplete is clicked
        countryAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = countryArrayAdapter.getItem(position).toString();
                countryAutoCompleteTextView.setText(selectedItem);
                countryAutoCompleteTextView.setEnabled(false);
                int pos = -1;
                for (int i = 0; i < countryListArrayList.size(); i++) {
                    if (countryListArrayList.get(i).getCountryName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                addressCountryCode = countryListArrayList.get(pos).getCountryId();
            }
        });

        /*countryAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strCountries = countryAutoCompleteTextView.getText().toString();
                    for (int i = 0; i < countryListArrayList.size(); i++) {
                        String temp = countryListArrayList.get(i).getCountryName();
                        if (strCountries.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    countryAutoCompleteTextView.setText("");
                    addressCountryCode = "";
                    setDefaultCountry();
                }
            }

        });


        stateAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strState = stateAutoCompleteTextView.getText().toString();
                    for (int i = 0; i < stateListArrayList.size(); i++) {
                        String temp = stateListArrayList.get(i).getStateName();
                        if (strState.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    stateAutoCompleteTextView.setText("");
                    addressStateCode = "";
                    setDefaultState();
                }
            }

        });*/

        //State Address
        final ArrayAdapter<StateList> stateListArrayAdapter =
                new ArrayAdapter<StateList>(this, R.layout.simple_spinner_item, stateListArrayList);
        stateListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        stateAutoCompleteTextView.setAdapter(stateListArrayAdapter);

        stateAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAutoCompleteTextView.showDropDown();
            }
        });


        stateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = stateListArrayAdapter.getItem(position).toString();
                stateAutoCompleteTextView.setText(selectedItem);
                stateAutoCompleteTextView.setEnabled(false);
                int pos = -1;
                for (int i = 0; i < stateListArrayList.size(); i++) {
                    if (stateListArrayList.get(i).getStateName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                addressStateCode = stateListArrayList.get(pos).getStateId();
            }
        });


        //City
        cityTextAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityTextAutoCompleteTextView.showDropDown();
            }
        });

        //when autocomplete is clicked
        cityTextAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = cityNameArrayAdapter.getItem(position).toString();
                cityTextAutoCompleteTextView.setText(selectedItem);
                cityTextAutoCompleteTextView.setEnabled(false);
                int pos = -1;
                for (int i = 0; i < cityNameArrayList.size(); i++) {
                    if (cityNameArrayList.get(i).getName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strOfficeCity = cityNameArrayList.get(pos).getId();
            }
        });

        /*cityTextAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (int i = 0; i < cityNameArrayList.size(); i++) {
                        String temp = cityNameArrayList.get(i).getName();
                        if (cityTextAutoCompleteTextView.getText().toString().compareTo(temp) == 0) {
                            return;
                        }
                    }
                    if (cityNameArrayList != null & cityNameArrayList.size() > 0) {
                        cityTextAutoCompleteTextView.setText("");
                        strOfficeCity = "";
                    }
                }
            }

        });*/

        //Set City
        cityTextAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                status = NetworkUtil.getConnectivityStatusString(EditProfileActivity.this);
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    if (addressCountryCode != null && !addressCountryCode.isEmpty() &&
                            addressStateCode != null && !addressStateCode.isEmpty()) {
                        getAddressCity(addressCountryCode, addressStateCode, s.toString(), "office");
                    } else {
                        getAddressCity(defaultAddressCountryCode, defaultAddressStateCode, s.toString(), "office");
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Set Countries
        if (AppPreferences.getUserCountryCode(this, AppUtils.USER_COUNTRY_CODE) == null ||
                AppPreferences.getUserCountryCode(this, AppUtils.USER_COUNTRY_CODE).isEmpty()) {
            setDefaultCountry();
        }
        //Set States
        if (AppPreferences.getUserStateCode(this, AppUtils.USER_STATE_CODE) == null ||
                AppPreferences.getUserStateCode(this, AppUtils.USER_STATE_CODE).isEmpty()) {
            setDefaultState();
        }

        //Set City
        if (AppPreferences.getUserCity(this, AppUtils.USER_CITY) == null ||
                AppPreferences.getUserCity(this, AppUtils.USER_CITY).isEmpty()) {
            cityTextAutoCompleteTextView.setText(AppPreferences.getUserCity(this, AppUtils.USER_CITY));
            if (AppPreferences.getUserCity(this, AppUtils.USER_CITY).length() > 0) {
                cityTextAutoCompleteTextView.setEnabled(false);
            } else {
                cityTextAutoCompleteTextView.setEnabled(true);
            }
        }

        //Set City Code
        if (AppPreferences.getUserCityCode(this, AppUtils.USER_CITY_CODE) == null ||
                AppPreferences.getUserCityCode(this, AppUtils.USER_CITY_CODE).isEmpty()) {
            strOfficeCity = AppPreferences.getUserCityCode(this, AppUtils.USER_CITY_CODE);
        }
    }

    private void setDefaultCountry() {
        for (int i = 0; i < countryListArrayList.size(); i++) {
            if (countryListArrayList.get(i).getCountryId().equals(defaultAddressCountryCode)) {
                countryAutoCompleteTextView.setText(countryListArrayList.get(i).getCountryName());
                countryAutoCompleteTextView.setEnabled(false);
                addressCountryCode = defaultAddressCountryCode;
            }
        }
    }

    private void setDefaultState() {
        for (int i = 0; i < stateListArrayList.size(); i++) {
            if (stateListArrayList.get(i).getStateId().equals(defaultAddressStateCode)) {
                stateAutoCompleteTextView.setText(stateListArrayList.get(i).getStateName());
                stateAutoCompleteTextView.setEnabled(false);
                addressStateCode = defaultAddressStateCode;
            }
        }
    }

    public void getAddressCity(String countryId, String stateId, String searchText, final String addressType) {
        task = getString(R.string.add_customer_get_cities);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getCities(version, key, task, userId, accessToken, countryId,
                stateId, searchText);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (cityNameArrayList != null && cityNameArrayList.size() > 0) {
                            cityNameArrayList.clear();
                        }
                        for (final CityName cityName : apiResponse.getData().getCityNames()) {
                            if (cityName != null) {
                                cityNameArrayList.add(cityName);
                            }
                        }

                        cityNameArrayAdapter =
                                new ArrayAdapter<CityName>(EditProfileActivity.this,
                                        R.layout.simple_spinner_item, cityNameArrayList);
                        cityNameArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        cityTextAutoCompleteTextView.setAdapter(cityNameArrayAdapter);

                    }   //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(EditProfileActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isImage(String image_path) {
        try {                        //Your code goes here
            URLConnection connection = new URL(image_path).openConnection();
            String contentType = connection.getHeaderField("Content-Type");
            imageURLPath = contentType.startsWith("image/");
            return imageURLPath;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validImagePath(String imagePath) {
        Uri resimUri = Uri.parse(imagePath);
        File imgFile = new File(resimUri.getPath());
        if (imgFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private void hideLoader() {
        if (EditProfileActivity.this != null) {
            if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (EditProfileActivity.this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (EditProfileActivity.this != null) {
                                progressBar.setVisibility(View.VISIBLE);
                                //Disable Touch
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

}
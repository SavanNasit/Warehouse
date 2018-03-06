package com.accrete.warehouse.fragment.createpackage;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.PackageDetailsAdapter;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersExecuteFragment;
import com.accrete.warehouse.model.AlreadyCreatedPackages;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PackageDetailsList;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.SelectOrderItem;
import com.accrete.warehouse.model.UploadDocument;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AllDatePickerFragment;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.PassDateToCounsellor;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;
import static com.accrete.warehouse.utils.MSupportConstants.PICK_FILE_RESULT_CODE;

/**
 * Created by poonam on 11/28/17.
 */

public class PackageDetailsFragment extends Fragment implements PackageDetailsAdapter.PackageDetailsAdapterListener, PassDateToCounsellor, DocumentUploaderAdapter.DocAdapterListener {
    PackageDetailsList packageDetails = new PackageDetailsList();
    private RecyclerView packageDetailsRecyclerView;
    private TextView packageDetailsEmptyView, packageDetailsCreatePackage;
    private TextInputEditText packageDetailsName;
    private TextInputEditText packageDetailsMobile;
    private TextInputEditText packageDetailsEmail;
    private TextInputEditText packageDetailsBillingAddress;
    private TextInputEditText packageDetailsDeliveryAddress;
    private TextInputEditText packageDetailsTaxInvoiceSerialNo;
    private TextInputEditText packageDetailsInvoiceDate;
    private AutoCompleteTextView packageDetailsInvoiceType;
    private TextView packageDetailsUploadDoc;
    private PackageDetailsAdapter packageDetailsAdapter;
    private List<PackageDetailsList> packageDetailsList = new ArrayList<>();
    private AllDatePickerFragment datePickerFragment;
    private String stringDateOfInvoice;
    private ArrayList<String> invoiceTypeList = new ArrayList<>();
    private String strEmail, strMobile, strShippingAddress, strBillingAddress, strOrder, strType, strLocal, strInvoiceDate, strInvoiceNumber, strESugam;
    private String chkid;
    private String orderId;
    private List<PendingItems> pendingItemList = new ArrayList<>();
    private List<AlreadyCreatedPackages> packedList = new ArrayList<>();
    private AlertDialog dialogUploadDoc;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<UploadDocument> uploadDocumentList = new ArrayList<>();
    private TextView downloadConfirmMessage, titleDownloadTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private RecyclerView dialogUploadDocRecyclerView;
    private ImageView addImageView;
    private Button btnUpload;
    private ProgressBar dialogUploadProgressBar;

    public void doRefresh() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_package_details, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        packageDetailsName = (TextInputEditText) rootView.findViewById(R.id.package_details_name);
        packageDetailsMobile = (TextInputEditText) rootView.findViewById(R.id.package_details_mobile);
        packageDetailsEmail = (TextInputEditText) rootView.findViewById(R.id.package_details_email);
        packageDetailsBillingAddress = (TextInputEditText) rootView.findViewById(R.id.package_details_billing_address);
        packageDetailsDeliveryAddress = (TextInputEditText) rootView.findViewById(R.id.package_details_delivery_address);
        packageDetailsTaxInvoiceSerialNo = (TextInputEditText) rootView.findViewById(R.id.package_details_tax_invoice_serial_no);
        packageDetailsInvoiceDate = (TextInputEditText) rootView.findViewById(R.id.package_details_invoice_date);
        packageDetailsInvoiceType = (AutoCompleteTextView) rootView.findViewById(R.id.package_details_invoice_type);
        packageDetailsUploadDoc = (TextView) rootView.findViewById(R.id.package_details_upload_doc);
        packageDetailsRecyclerView = (RecyclerView) rootView.findViewById(R.id.package_details_recycler_view);
        packageDetailsEmptyView = (TextView) rootView.findViewById(R.id.package_details_empty_view);
        packageDetailsCreatePackage = (TextView) rootView.findViewById(R.id.package_details_create_package);
        packageDetailsInvoiceType.setVisibility(View.GONE);


        packageDetailsAdapter = new PackageDetailsAdapter(getActivity(), packageDetailsList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packageDetailsRecyclerView.setLayoutManager(mLayoutManager);
        packageDetailsRecyclerView.setHasFixedSize(true);
        packageDetailsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageDetailsRecyclerView.setNestedScrollingEnabled(false);
        packageDetailsRecyclerView.setAdapter(packageDetailsAdapter);

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

        packageDetailsUploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogUploadDoc(getActivity());
            }
        });

        packageDetailsInvoiceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dialog_from));
            }
        });

        invoiceTypeAdapter();
        packageDetailsInvoiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageDetailsInvoiceType.showDropDown();
            }
        });

        packageDetailsCreatePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (packageDetailsList.size() > 0) {
                    createPackage();
                } else {
                    Toast.makeText(getActivity(), R.string.error_message_create_package, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute(int position) {
        packageDetailsList.remove(position);
        packageDetailsAdapter.notifyDataSetChanged();
    }

    @Override
    public void passDate(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            stringDateOfInvoice = s;
            Date startDate = formatter.parse(stringDateOfInvoice);
            stringDateOfInvoice = targetFormat.format(startDate);
            packageDetailsInvoiceDate.setText(s);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passTime(String s) {

    }

    private void invoiceTypeAdapter() {
        invoiceTypeList.add("Retail");
        invoiceTypeList.add("Tax");
        ArrayAdapter arrayAdapterInvoiceType = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, invoiceTypeList);
        arrayAdapterInvoiceType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packageDetailsInvoiceType.setAdapter(arrayAdapterInvoiceType);
    }


    //Opening Dialog to Upload Documents
    private void openDialogUploadDoc(final Activity activity) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_upload_doc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogUploadDoc = builder.create();
        dialogUploadDoc.setCanceledOnTouchOutside(true);

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogUploadDocRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_upload_doc_recycler_view);
        addImageView = (ImageView) dialogView.findViewById(R.id.add_imageView);
        btnUpload = (Button) dialogView.findViewById(R.id.btn_upload);
        dialogUploadProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_upload_progress_bar);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        documentUploaderAdapter = new DocumentUploaderAdapter(getActivity(), uploadDocumentList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        dialogUploadDocRecyclerView.setLayoutManager(mLayoutManager);
        dialogUploadDocRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        dialogUploadDocRecyclerView.setAdapter(documentUploaderAdapter);

        if (uploadDocumentList.size() > 0) {
            uploadDocumentList.clear();
        }

        //Upload files and dismiss dialog
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    /*if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        FilesUploadingAsyncTask filesUploadingAsyncTask = new FilesUploadingAsyncTask(activity, uploadDocumentList, pacId, dialogUploadDoc);
                        filesUploadingAsyncTask.execute();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }*/
                    dialogUploadDoc.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please upload atleast one doc.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Dismiss dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    uploadDocumentList.clear();
                }
                dialogUploadDoc.dismiss();
            }
        });

        //Call Intent to select file and add into List
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });

        dialogUploadDoc.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogUploadDoc.isShowing()) {
            dialogUploadDoc.show();
        }
    }

    public void getOrderItem(List<SelectOrderItem> selectOrderItems, List<PendingItems> pendingItemsLists, String chkoid, int strQuantity) {
        if (pendingItemList.size() > 0) {
            pendingItemList.clear();
        }
       /* if(packageDetailsList!=null && packageDetailsList.size()>0){
            packageDetailsList.clear();
        }*/
        packageDetailsAdapter.notifyDataSetChanged();


        List<PackageDetailsList> pdetailList = new ArrayList<>();
        if(selectOrderItems!=null && selectOrderItems.size()>0){
        for (int i = 0; i < selectOrderItems.size(); i++) {
            packageDetails.setItem(selectOrderItems.get(i).getInventoryName());
            //packageDetails.setQuantity(selectOrderItems.get(i).getAllocatedQuantity());
            packageDetails.setQuantity(String.valueOf(strQuantity));
            packageDetails.setUnit(selectOrderItems.get(i).getUnit());
            packageDetails.setQty(selectOrderItems.get(i).getAllocatedQuantity());
            packageDetails.setBatchNumber(selectOrderItems.get(i).getInventory());
        }

        if(packageDetailsList.size()>0) {
            for (int i = 0; i < packageDetailsList.size(); i++) {
                if (packageDetailsList.get(i).getBatchNumber().equals(selectOrderItems.get(i).getInventory())) {
                    packageDetailsList.get(i).setQuantity(String.valueOf((Integer.parseInt(packageDetailsList.get(i).getQuantity())) + strQuantity));
                } else {
                    pdetailList.add(packageDetails);
                    packageDetailsList.addAll(pdetailList);
                }
            }
        }else {
            pdetailList.add(packageDetails);
            packageDetailsList.addAll(pdetailList);
        }

            packageDetailsAdapter.notifyDataSetChanged();
        }

        pendingItemList.addAll(pendingItemsLists);
        orderId = chkoid;
    }

    private void createPackage() {
        task = getString(R.string.create_package_task);
        String chkid = null;
        strEmail = packageDetailsEmail.getText().toString();
        strMobile = packageDetailsMobile.getText().toString();
        strShippingAddress = packageDetailsDeliveryAddress.getText().toString();
        strBillingAddress = packageDetailsBillingAddress.getText().toString();
        strType = "1";
        strLocal = "1";
        strOrder = orderId;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            stringDateOfInvoice = packageDetailsInvoiceDate.getText().toString();
            Date startDate = formatter.parse(stringDateOfInvoice);
            stringDateOfInvoice = targetFormat.format(startDate);
            packageDetailsInvoiceDate.setText(packageDetailsInvoiceDate.getText().toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        strInvoiceDate = stringDateOfInvoice;
        strInvoiceNumber = packageDetailsTaxInvoiceSerialNo.getText().toString();
        JSONArray jsonArrayPackageDetails = new JSONArray();

        try {
            Log.e("Pending Item List", String.valueOf(pendingItemList.size()));
            if (pendingItemList != null && pendingItemList.size() > 0) {
                for (int i = 0; i < pendingItemList.size(); i++) {
                    JSONObject jsonObjectPackageDetails = new JSONObject();
                    Log.e("name ", pendingItemList.toString());
                    jsonObjectPackageDetails.put(getString(R.string.isvid), pendingItemList.get(i).getIsvid());
                    jsonObjectPackageDetails.put(getString(R.string.isid), pendingItemList.get(i).getIsid());
                    jsonObjectPackageDetails.put(getString(R.string.iid), pendingItemList.get(i).getIid());
                    jsonObjectPackageDetails.put(getString(R.string.qty), pendingItemList.get(i).getItemQuantity());
                    jsonObjectPackageDetails.put(getString(R.string.oiid), pendingItemList.get(i).getOiid());
                    jsonObjectPackageDetails.put(getString(R.string.meaid), pendingItemList.get(i).getMeaid());
                    jsonObjectPackageDetails.put(getString(R.string.name), pendingItemList.get(i).getItemVariationName());
                    jsonObjectPackageDetails.put(getString(R.string.variation), pendingItemList.get(i).getItemVariationName());
                    jsonObjectPackageDetails.put(getString(R.string.unit), pendingItemList.get(i).getItemUnit());
                    jsonArrayPackageDetails.put(jsonObjectPackageDetails);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        UploadDocument uploadDocument = new UploadDocument();
//        File propertyImageFile = new File(uploadDocument.getFilePath());
      //  RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), propertyImageFile);
       // MultipartBody.Part propertyImagePart = MultipartBody.Part.createFormData("files", propertyImageFile.getName(), propertyImage);

        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[uploadDocumentList.size()];

        for (int index = 0; index < uploadDocumentList.size(); index++) {
            Log.d("Package Details", "requestUploadSurvey: survey image " + index + "  " + uploadDocumentList.get(index).getFilePath());
            File file = new File(uploadDocumentList.get(index).getFilePath());
            RequestBody surveyBody = RequestBody.create(MediaType.parse("*/*"), file);
            surveyImagesParts[index] = MultipartBody.Part.createFormData("files[]", file.getPath(), surveyBody);
        }

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.createPackageMultipart(version, key, task, userId, accessToken,
                strEmail, strMobile, strShippingAddress, strBillingAddress, jsonArrayPackageDetails, chkid, strOrder, strType, strLocal, strInvoiceDate
                , strInvoiceNumber, strESugam, surveyImagesParts);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        Toast.makeText(getActivity(), "Package created", Toast.LENGTH_SHORT).show();
                        RunningOrdersExecuteFragment.viewPagerExecute.setCurrentItem(2);
                       /* AlreadyCreatedPackages packages = new AlreadyCreatedPackages();
                        packages.setInvoiceDate(strInvoiceDate);
                        packages.setInvoiceNo(strInvoiceNumber);
                        packages.setCustomerName(packageDetailsName.getText().toString());
                        packages.setOrderID(strOrder);
                        packages.setPacid(apiResponse.getData().getPacid());

                        packedList.add(packages);*/

                        ((RunningOrdersExecuteFragment) getParentFragment()).sendPackageDetails();

                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("10006")) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals("20004")) {
                            packageDetailsEmptyView.setText(apiResponse.getMessage());
                            packageDetailsRecyclerView.setVisibility(View.GONE);
                            packageDetailsEmptyView.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Order Execution failed", Toast.LENGTH_LONG).show();
                Log.d("wh:createdPackage", t.getMessage());
            }
        });
    }

    //Remove file/document from list
    @Override
    public void onClickedDeleteBtn(int position) {
        if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
            uploadDocumentList.remove(position);
            documentUploaderAdapter.notifyDataSetChanged();
        }
    }

    //Add Document into List
    public void addDocument(String selectedFilePath, String fileName) {
        UploadDocument uploadDocument = new UploadDocument();
        uploadDocument.setFileName(fileName);
        uploadDocument.setFilePath(selectedFilePath);
        uploadDocument.setFileType(selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1, selectedFilePath.length()));
        uploadDocumentList.add(uploadDocument);
        documentUploaderAdapter.notifyDataSetChanged();
    }

    //Intent to select file
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        getActivity().startActivityForResult(intent, PICK_FILE_RESULT_CODE);
    }
}

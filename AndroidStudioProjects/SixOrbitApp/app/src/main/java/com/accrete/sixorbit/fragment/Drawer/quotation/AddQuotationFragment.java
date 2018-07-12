package com.accrete.sixorbit.fragment.Drawer.quotation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.AddNewAddressActivity;
import com.accrete.sixorbit.activity.barCodeScanner.ScannerActivity;
import com.accrete.sixorbit.adapter.AddressAdapter;
import com.accrete.sixorbit.adapter.QuotationChargesListAdapter;
import com.accrete.sixorbit.adapter.QuotationChargesThreeSpinnerCheckAdapter;
import com.accrete.sixorbit.adapter.QuotationChargesTwoListAdapter;
import com.accrete.sixorbit.adapter.QuotationCustomersAdapter;
import com.accrete.sixorbit.adapter.QuotationItemsAdapter;
import com.accrete.sixorbit.adapter.QuotationProductsAdapter;
import com.accrete.sixorbit.adapter.SiteAddressAdapter;
import com.accrete.sixorbit.fragment.Drawer.AllDatePickerFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.AddressList;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChargesList;
import com.accrete.sixorbit.model.ChargesList2;
import com.accrete.sixorbit.model.ChargesList3;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.ContactPerson;
import com.accrete.sixorbit.model.ContactPersonTypeData;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.CustomerList;
import com.accrete.sixorbit.model.EmailTemplateArr;
import com.accrete.sixorbit.model.ItemData;
import com.accrete.sixorbit.model.ItemList;
import com.accrete.sixorbit.model.Outlet;
import com.accrete.sixorbit.model.SearchRefferedDatum;
import com.accrete.sixorbit.model.SmsTemplateDatum;
import com.accrete.sixorbit.model.Tax;
import com.accrete.sixorbit.model.TaxList;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.accrete.sixorbit.utils.InputFilterForPercentageAndRupees;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by agt on 18/12/17.
 */

public class AddQuotationFragment extends Fragment implements View.OnClickListener,
        QuotationItemsAdapter.QuotationItemsAdapterListener, CompoundButton.OnCheckedChangeListener,
        QuotationChargesListAdapter.QuotationChargesAdapterListener,
        QuotationChargesTwoListAdapter.QuotationCharges2AdapterListener,
        QuotationChargesThreeSpinnerCheckAdapter.QuotationChargesThreeAdapterListener, PassDateToCounsellor,
        QuotationCustomersAdapter.QuotationCustomersAdapterListener,
        QuotationProductsAdapter.QuotationProductsAdapterListener, AddressAdapter.AddressItemClickListener,
        SiteAddressAdapter.SiteAddressItemClickListener {
    private static final int REQUEST_MULTIPLE_PERMISSION = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    private final String[] PERMISSION_STORAGE = new String[]{android.Manifest.permission.CAMERA};
    private List<String> permissionsNeeded = new ArrayList<>();
    private boolean isFirstTime = true;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private double roundOff;
    private TextInputLayout productSearchTextInputLayout;
    private AutoCompleteTextView productSearchAutoCompleteTextView;
    private AutoCompleteTextView contactPersonAutoCompleteTextview;
    private AlertDialog alertDialog;
    private String[] discountTypeArr = new String[]{"%", "INR"};
    private RecyclerView productsRecyclerView;
    private QuotationItemsAdapter quotationItemsAdapter;
    private List<ItemData> itemDataArrayList = new ArrayList<>();
    private ArrayList<CustomerList> customerSearchArrayList = new ArrayList<>();
    private ArrayList<ItemList> itemListArrayList = new ArrayList<>();
    private QuotationCustomersAdapter quotationCustomersAdapter;
    private QuotationProductsAdapter quotationProductsAdapter;
    private String status;
    private Dialog dialog, productsDialog, dialogSiteAddress;
    private AutoCompleteTextView customerSearchEditText;
    private LinearLayout mobileLayout;
    private TextView mobileTextView;
    private EditText mobileValueEditText;
    private LinearLayout emailLayout;
    private TextView emailTextView;
    private EditText emailValueEditText;
    private CheckBox smsCheckBox;
    private CheckBox emailCheckBox;
    private TextView saveTextView;
    private TextView showSMSPreview, showEmailPreview;
    private ImageView scanImageView;
    private LinearLayout currentAddressLayout;
    private CheckBox currentAddressCheckBox;
    private TextView currentAddressTextView;
    private LinearLayout siteAddressLayout;
    private CheckBox sameSiteAddressCheckBox;
    private CheckBox noSiteAddressCheckBox;
    private TextView siteAddressTextView;
    private ArrayList<AddressList> currentAddressListArrayList = new ArrayList<>();
    private ArrayList<AddressList> siteAddressListArrayList = new ArrayList<>();
    private ArrayList<ChargesList> preChargesListArrayList = new ArrayList<>();
    private ArrayList<ChargesList2> postChargesListArrayList = new ArrayList<>();
    private ArrayList<ChargesList3> chargesList3ArrayList = new ArrayList<>();
    private ArrayList<Outlet> outletArrayList = new ArrayList<>();
    private ArrayList<SmsTemplateDatum> smsTemplateDataArrayList = new ArrayList<>();
    private ArrayList<EmailTemplateArr> emailTemplateArrArrayList = new ArrayList<>();
    private ArrayList<TaxList> taxListArrayList = new ArrayList<>();
    private ArrayList<ChatContacts> assigneeArrayList = new ArrayList<>();
    private LinearLayout parentLayout;
    private LinearLayout totalAmountLayout;
    private TextView totalAmountTitleTextView;
    private TextView totalAmountValueTextView;
    private LinearLayout totalDiscountLayout;
    private TextView totalDiscountTitleTextView;
    private TextView totalDiscountValueTextView;
    private LinearLayout materialCostLayout;
    private TextView materialCostTitleTextView;
    private TextView materialCostValueTextView;
    private LinearLayout additionalDiscountLayout;
    private TextInputLayout additionalTextInputLayout;
    private EditText additionalEdittext;
    private Spinner additionalDiscountTypeSpinner;
    private TextView additionalDiscountValueTextView;
    private RecyclerView chargeListRecyclerView;
    private TextView taxTitleTextView;
    private RelativeLayout taxSpinnerLayout;
    private Spinner taxChargeTypeSpinner;
    private TextView taxChargeValueTextView;
    private RecyclerView chargeListTwoRecyclerView;
    private TextView chargesThreeTitleTextView;
    private RelativeLayout chargesThreeSpinnerLayout;
    private Spinner chargeTypeThreeSpinner;
    private TextView chargeThreeValueTextView;
    private QuotationChargesListAdapter quotationChargesListAdapter;
    private QuotationChargesTwoListAdapter quotationChargesTwoListAdapter;
    private LinearLayout grandTotalLayout;
    private TextView grandTotalTitleTextView;
    private TextView grandTotalValueTextView;
    private LinearLayout roundOffLayout;
    private TextView roundOffTitleTextView;
    private TextInputLayout roundOffTextInputLayout;
    private EditText roundOffEdittext;
    private TextView roundOffValueTextView;
    private LinearLayout payableAmountLayout;
    private TextView payableAmountTitleTextView;
    private TextView payableAmountValueTextView;
    private QuotationChargesThreeSpinnerCheckAdapter quotationChargesThreeSpinnerCheckAdapter;
    private Spinner outletSpinner;
    private TextInputEditText referenceTextInputEditText;
    private TextInputEditText deliveryTimeTextInputEditText;
    private TextInputEditText paymentModeTextInputEditText;
    private TextInputEditText unloadingDetailsTextInputEditText;
    private TextInputEditText remarksTextInputEditText;
    private TextInputEditText drawingNoTextInputEditText;
    private LinearLayout linearLayoutAddAddress, linearLayoutAddNewSiteAddress;
    private String stringStartDate, strUid, strBaid, strCC, strCoverage, strCuid, strTaxInclusive, strImageShow, strProductsDealerPrice,
            strCodeId, strChkId, strEtId, strQotEmId, strSmstId, strAdditionalDiscount, strReference, strDeliveryDate, strPaymentMode,
            strUnloading, strProductsRemarks, strDrawingNo, strIitId, strIsVId, strIId, strName, strMeaId, strQuantity, strPrice, strDealerPrice,
            strAmount, strMeasurements, strTaxes, strButapid, strTax, strPriceConversionRate, strConversionRate, strBoxConversionRate,
            strBoxQuantity, strUnitName, strImage, strDiscount, strDiscountType, strRemarks, strPackageQuantity, strSaid, strMail,
            strSms, strRound, newCurrentAddressSaid, addressType, newSiteAddressSaid;
    private Date startDate;
    private AllDatePickerFragment allDatePickerFragment;
    private TextView attendeeDetailsTitleTextView;
    private RelativeLayout attendeeSpinnerLayout;
    private Spinner attendeeSpinner;
    private DatabaseHandler databaseHandler;
    private CheckBox coverageCheckBoxTextView;
    private CheckBox productImageShowCheckBoxTextView;
    private CheckBox productTaxIncludeCheckBoxTextView;
    private TextView outletTitleTextView;
    private String qotemid;
    private LinearLayout addressMainLayout;
    private RecyclerView getCustomersProductsRecyclerView;
    private String strContactPerson;
    private List<ContactPerson> contactPersonListArrayList = new ArrayList<>();
    private TextInputLayout textInputContactPerson;
    private LinearLayout addContactLayout;
    private EmailValidator emailValidator;
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private TextView customerNameTitleTextView;
    private EditText customerNameValueEditText;
    private ImageButton clearCustomerInfoImageButton;
    private AddressAdapter addressAdapter;
    private SiteAddressAdapter siteAddressAdapter;
    private List<ContactPersonTypeData> contactTypeDataArrayList = new ArrayList<>();
    //Int Variable to store position of previously selected item in dialogAddItems
    private int previousQuantityPosOfProduct = 0;
    private Spinner spinnerSmsTemplate, spinnerEmailTemplate;
    private String flag;
    private LinearLayout basicInfoLayout;

    public static AddQuotationFragment newInstance(String title) {
        AddQuotationFragment f = new AddQuotationFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @Override
    public void onPreChargeDiscountTaxChange(int position, String discountValue, ChargesList chargesList, String taxValue, int positionSpinner) {

    }

    @Override
    public void onQuotationChargeTwoDiscount(int position, String discountValue, ChargesList2 chargesList2) {
        if (chargesList2.getEctid().equals("1")) {
            if (materialCostLayout.getVisibility() == View.VISIBLE) {
                chargesList2.setDiscountAmountValue(String.valueOf(
                        ((ParseDouble(chargeThreeValueTextView.getText().toString().trim()))
                                * ParseDouble(discountValue)) / 100));
                chargesList2.setDiscountValue(discountValue);
                postChargesListArrayList.set(position, chargesList2);
                if (!chargeListTwoRecyclerView.isComputingLayout()) {
                    quotationChargesTwoListAdapter.notifyDataSetChanged();
                }
            }
        } else {
            chargesList2.setDiscountAmountValue(discountValue);
            chargesList2.setDiscountValue(discountValue);
            postChargesListArrayList.set(position, chargesList2);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (!chargeListTwoRecyclerView.isComputingLayout()) {
                        quotationChargesTwoListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        setGrandTotalAndPayableAmount();
    }

    public double getDiscountedAmount() {
        double discountedAmount = 0;
        if (materialCostValueTextView.getText().toString().trim().contains(",") &&
                additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                    replace("-", "")));
        } else if (materialCostValueTextView.getText().toString().trim().contains(",")) {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", "")));
        } else if (additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim())
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                    replace("-", "")));
        } else {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim())
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", "")));
        }
        return discountedAmount;

    }

    @Override
    public void onQuotationChargeDiscountChange(int position, String discountValue, ChargesList chargesList) {
        if (chargesList.getEctid().equals("1")) {
            if (materialCostLayout.getVisibility() == View.VISIBLE) {
                if (materialCostValueTextView.getText().toString().trim().contains(",") &&
                        additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
                    chargesList.setDiscountAmountValue("" + (((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                            - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                            replace("-", ""))) *
                            (ParseDouble(discountValue.toString()))) / 100));
                } else if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                    chargesList.setDiscountAmountValue("" + (((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                            - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", ""))) *
                            (ParseDouble(discountValue.toString()))) / 100));
                } else if (additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
                    chargesList.setDiscountAmountValue("" + (((ParseDouble(materialCostValueTextView.getText().toString().trim())
                            - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                            replace("-", ""))) *
                            (ParseDouble(discountValue.toString()))) / 100));
                } else {
                    chargesList.setDiscountAmountValue("" + (((ParseDouble(materialCostValueTextView.getText().toString().trim())
                            - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", ""))) *
                            (ParseDouble(discountValue.toString()))) / 100));
                }
                chargesList.setDiscountValue(discountValue);
                preChargesListArrayList.set(position, chargesList);
                if (!chargeListRecyclerView.isComputingLayout()) {
                    quotationChargesListAdapter.notifyDataSetChanged();
                }
            }
        } else {
            chargesList.setDiscountAmountValue(discountValue);
            chargesList.setDiscountValue(discountValue);
            preChargesListArrayList.set(position, chargesList);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (!chargeListRecyclerView.isComputingLayout()) {
                        quotationChargesListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        setGrandTotalAndPayableAmount();
    }

  /*  @Override
    public void onPreChargeDiscountTaxChange(int position, String discountValue, ChargesList chargesList, TaxList taxList) {

    }*/

    public void getScanCode(String str) {
        searchedProductDetails(str, "scan");
        //  dialogAddItems(getActivity(), "scan");
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.create_quotation));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.create_quotation));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.create_quotation));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.create_quotation));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        qotemid = bundle.getString(getString(R.string.quotation_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_search_quotation_item, container, false);
        emailValidator = new EmailValidator();
        findViews(view);
        return view;
    }

    @Override
    public void removeItemAndNotify(int position) {
        if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
            itemDataArrayList.remove(position);
            quotationItemsAdapter.notifyDataSetChanged();
            updateAllMainValues();
        }
    }

    private void findViews(View view) {
        parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
        outletTitleTextView = (TextView) view.findViewById(R.id.outlet_title_textView);
        outletSpinner = (Spinner) view.findViewById(R.id.outlet_spinner);
        referenceTextInputEditText = (TextInputEditText) view.findViewById(R.id.reference_textInputEditText);
        deliveryTimeTextInputEditText = (TextInputEditText) view.findViewById(R.id.delivery_time_textInputEditText);
        paymentModeTextInputEditText = (TextInputEditText) view.findViewById(R.id.paymentMode_textInputEditText);
        unloadingDetailsTextInputEditText = (TextInputEditText) view.findViewById(R.id.unloading_details_textInputEditText);
        remarksTextInputEditText = (TextInputEditText) view.findViewById(R.id.remarks_textInputEditText);
        drawingNoTextInputEditText = (TextInputEditText) view.findViewById(R.id.drawing_no_textInputEditText);
        productSearchTextInputLayout = (TextInputLayout) view.findViewById(R.id.product_search_textInputLayout);
        productSearchAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.product_search_autoCompleteTextView);
        customerNameTitleTextView = (TextView) view.findViewById(R.id.customer_name_title_textView);
        customerNameValueEditText = (EditText) view.findViewById(R.id.customer_name_value_editText);
        clearCustomerInfoImageButton = (ImageButton) view.findViewById(R.id.clear_customerInfo_imageButton);
        productsRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        scanImageView = (ImageView) view.findViewById(R.id.scan_imageView);
        mobileLayout = (LinearLayout) view.findViewById(R.id.mobile_layout);
        mobileTextView = (TextView) view.findViewById(R.id.mobile_textView);
        mobileValueEditText = (EditText) view.findViewById(R.id.mobile_value_editText);
        emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);
        emailTextView = (TextView) view.findViewById(R.id.email_textView);
        emailValueEditText = (EditText) view.findViewById(R.id.email_value_editText);
        smsCheckBox = (CheckBox) view.findViewById(R.id.sms_checkBox);
        emailCheckBox = (CheckBox) view.findViewById(R.id.email_checkBox);
        saveTextView = (TextView) view.findViewById(R.id.save_textView);
        currentAddressLayout = (LinearLayout) view.findViewById(R.id.current_address_layout);
        // textViewCurrentAddress = (TextView) view.findViewById(R.id.textView_currentAddress);
        currentAddressCheckBox = (CheckBox) view.findViewById(R.id.currentAddress_checkBox);
        currentAddressTextView = (TextView) view.findViewById(R.id.currentAddress_textView);
        siteAddressLayout = (LinearLayout) view.findViewById(R.id.site_address_layout);
        // textViewSiteAddress = (TextView) view.findViewById(R.id.textView_siteAddress);
        sameSiteAddressCheckBox = (CheckBox) view.findViewById(R.id.sameSiteAddress_checkBox);
        noSiteAddressCheckBox = (CheckBox) view.findViewById(R.id.noSiteAddress_checkBox);
        siteAddressTextView = (TextView) view.findViewById(R.id.siteAddress_textView);
        totalAmountLayout = (LinearLayout) view.findViewById(R.id.total_amount_layout);
        totalAmountTitleTextView = (TextView) view.findViewById(R.id.total_amount_title_textView);
        totalAmountValueTextView = (TextView) view.findViewById(R.id.total_amount_value_textView);
        totalDiscountLayout = (LinearLayout) view.findViewById(R.id.total_discount_layout);
        totalDiscountTitleTextView = (TextView) view.findViewById(R.id.total_discount_title_textView);
        totalDiscountValueTextView = (TextView) view.findViewById(R.id.total_discount_value_textView);
        materialCostLayout = (LinearLayout) view.findViewById(R.id.material_cost_layout);
        materialCostTitleTextView = (TextView) view.findViewById(R.id.material_cost_title_textView);
        materialCostValueTextView = (TextView) view.findViewById(R.id.material_cost_value_textView);
        additionalDiscountLayout = (LinearLayout) view.findViewById(R.id.additional_discount_layout);
        additionalTextInputLayout = (TextInputLayout) view.findViewById(R.id.additional_textInputLayout);
        additionalEdittext = (EditText) view.findViewById(R.id.additional_edittext);
        additionalDiscountTypeSpinner = (Spinner) view.findViewById(R.id.additional_discount_type_spinner);
        additionalDiscountValueTextView = (TextView) view.findViewById(R.id.additional_discount_value_textView);
        chargeListRecyclerView = (RecyclerView) view.findViewById(R.id.chargeList_recyclerView);
        taxTitleTextView = (TextView) view.findViewById(R.id.tax_title_textView);
        taxSpinnerLayout = (RelativeLayout) view.findViewById(R.id.tax_spinner_layout);
        taxChargeTypeSpinner = (Spinner) view.findViewById(R.id.tax_charge_type_spinner);
        taxChargeValueTextView = (TextView) view.findViewById(R.id.tax_charge_value_textView);
        chargeListTwoRecyclerView = (RecyclerView) view.findViewById(R.id.chargeList_two_recyclerView);
        chargesThreeTitleTextView = (TextView) view.findViewById(R.id.charges_three_title_textView);
        chargesThreeSpinnerLayout = (RelativeLayout) view.findViewById(R.id.charges_three_spinner_layout);
        chargeTypeThreeSpinner = (Spinner) view.findViewById(R.id.charge_type_three_spinner);
        chargeThreeValueTextView = (TextView) view.findViewById(R.id.charge_three_value_textView);
        grandTotalLayout = (LinearLayout) view.findViewById(R.id.grand_total_layout);
        grandTotalTitleTextView = (TextView) view.findViewById(R.id.grand_total_title_textView);
        grandTotalValueTextView = (TextView) view.findViewById(R.id.grand_total_value_textView);
        roundOffLayout = (LinearLayout) view.findViewById(R.id.round_off_layout);
        roundOffTitleTextView = (TextView) view.findViewById(R.id.round_off_title_textView);
        roundOffTextInputLayout = (TextInputLayout) view.findViewById(R.id.round_off_textInputLayout);
        roundOffEdittext = (EditText) view.findViewById(R.id.round_off_edittext);
        roundOffValueTextView = (TextView) view.findViewById(R.id.round_off_value_textView);
        payableAmountLayout = (LinearLayout) view.findViewById(R.id.payable_amount_layout);
        payableAmountTitleTextView = (TextView) view.findViewById(R.id.payable_amount_title_textView);
        payableAmountValueTextView = (TextView) view.findViewById(R.id.payable_amount_value_textView);
        attendeeDetailsTitleTextView = (TextView) view.findViewById(R.id.attendee_details_title_textView);
        attendeeSpinnerLayout = (RelativeLayout) view.findViewById(R.id.attendee_spinner_layout);
        attendeeSpinner = (Spinner) view.findViewById(R.id.attendee_spinner);
        coverageCheckBoxTextView = (CheckBox) view.findViewById(R.id.coverage_checkBox_textView);
        productImageShowCheckBoxTextView = (CheckBox) view.findViewById(R.id.product_imageShow_checkBox_textView);
        productTaxIncludeCheckBoxTextView = (CheckBox) view.findViewById(R.id.product_taxInclude_checkBox_textView);
        coverageCheckBoxTextView.setVisibility(View.GONE);
        linearLayoutAddAddress = (LinearLayout) view.findViewById(R.id.current_address_add_address);
        linearLayoutAddNewSiteAddress = (LinearLayout) view.findViewById(R.id.site_address_add_address);
        addressMainLayout = (LinearLayout) view.findViewById(R.id.address_main_layout);
        textInputContactPerson = (TextInputLayout) view.findViewById(R.id.add_search_quotation_item_contact_person_text_input);
        contactPersonAutoCompleteTextview = (AutoCompleteTextView) view.findViewById(R.id.add_search_quotation_item_contact_person);
        addContactLayout = (LinearLayout) view.findViewById(R.id.current_address_add_contact);
        basicInfoLayout = (LinearLayout) view.findViewById(R.id.basic_info_layout);

        showSMSPreview = (TextView) view.findViewById(R.id.add_search_quotation_item_show_sms_preview);
        showSMSPreview.setPaintFlags(showSMSPreview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        showEmailPreview = (TextView) view.findViewById(R.id.add_search_quotation_item_show_email_preview);
        showEmailPreview.setPaintFlags(showEmailPreview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        spinnerSmsTemplate = (Spinner) view.findViewById(R.id.add_search_quotation_item_spinner_sms_template);
        spinnerEmailTemplate = (Spinner) view.findViewById(R.id.add_search_quotation_item_spinner_email_template);

        addContactLayout.setOnClickListener(this);
        linearLayoutAddAddress.setOnClickListener(this);
        linearLayoutAddNewSiteAddress.setOnClickListener(this);
        showSMSPreview.setOnClickListener(this);
        showEmailPreview.setOnClickListener(this);


        //Instance of DatabaseHandler class
        databaseHandler = new DatabaseHandler(getActivity());

        //Mandatory Outlet title
        String mainText = "Outlet";
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        outletTitleTextView.setText(TextUtils.concat(mainText, spannableStringBuilder));

        //Add basic attendee
        ChatContacts chatContacts = new ChatContacts();
        chatContacts.setName("Select Attendee");
        assigneeArrayList.add(chatContacts);

        //Get All Assignee
        assigneeArrayList.addAll(databaseHandler.getAllAssignee());

        //Attendee Adapter
        ArrayAdapter<ChatContacts> attendeeArrayAdapter =
                new ArrayAdapter<ChatContacts>(getActivity(), R.layout.simple_spinner_item, assigneeArrayList);
        attendeeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        attendeeSpinner.setAdapter(attendeeArrayAdapter);

        emailValueEditText.setFocusable(false);
        mobileValueEditText.setFocusable(false);
        mobileValueEditText.setClickable(false);
        emailValueEditText.setClickable(false);
        mobileLayout.setVisibility(View.GONE);
        emailLayout.setVisibility(View.GONE);
        addressMainLayout.setVisibility(View.GONE);

        //Hide textView and spinner of outlet
        outletTitleTextView.setVisibility(View.GONE);
        outletSpinner.setVisibility(View.GONE);

        //DatePicker
        allDatePickerFragment = new AllDatePickerFragment();
        allDatePickerFragment.setListener(this);
        deliveryTimeTextInputEditText.setOnClickListener(this);

        quotationItemsAdapter = new QuotationItemsAdapter(getActivity(), itemDataArrayList, this);

        //      quotationChargesListAdapter = new QuotationChargesListAdapter(getActivity(), preChargesListArrayList, this);
        quotationChargesTwoListAdapter = new QuotationChargesTwoListAdapter(getActivity(), postChargesListArrayList, this);

        //Products RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        productsRecyclerView.setLayoutManager(mLayoutManager);
        productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        productsRecyclerView.setAdapter(quotationItemsAdapter);
        productsRecyclerView.setNestedScrollingEnabled(false);

        //Change List RecyclerView
        RecyclerView.LayoutManager mChargeListLayoutManager = new LinearLayoutManager(getActivity());
        chargeListRecyclerView.setLayoutManager(mChargeListLayoutManager);
        chargeListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chargeListRecyclerView.setAdapter(quotationChargesListAdapter);
        chargeListRecyclerView.setNestedScrollingEnabled(false);

        //Charge List Two RecyclerView
        RecyclerView.LayoutManager mChargeListTwoLayoutManager = new LinearLayoutManager(getActivity());
        chargeListTwoRecyclerView.setLayoutManager(mChargeListTwoLayoutManager);
        chargeListTwoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chargeListTwoRecyclerView.setAdapter(quotationChargesTwoListAdapter);
        chargeListTwoRecyclerView.setNestedScrollingEnabled(false);

        scanImageView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        customerNameValueEditText.setOnClickListener(this);
        clearCustomerInfoImageButton.setOnClickListener(this);

        //Search Customer
        customerNameValueEditText.clearFocus();
        productSearchAutoCompleteTextView.clearFocus();
        parentLayout.requestFocus();
        emailValueEditText.requestFocus();
        customerNameValueEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (customerNameValueEditText.getText().toString().trim().length() == 0) {
                        openCustomerSearchDialog();
                    }
                }
            }
        });


        //Search Product
        productSearchAutoCompleteTextView.setThreshold(1);
        productSearchAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (productSearchAutoCompleteTextView.getText().toString().trim().length() == 0) {
                        openProductSearchDialog();
                    }
                }
            }
        });

        productSearchAutoCompleteTextView.setOnClickListener(this);
        noSiteAddressCheckBox.setOnCheckedChangeListener(this);
        sameSiteAddressCheckBox.setOnCheckedChangeListener(this);
        currentAddressCheckBox.setOnCheckedChangeListener(this);
        currentAddressTextView.setOnClickListener(this);
        siteAddressTextView.setOnClickListener(this);

        //Default Values
        additionalEdittext.setText("0");
        materialCostValueTextView.setText("0");
        totalAmountValueTextView.setText("0");
        grandTotalValueTextView.setText("0");
        payableAmountValueTextView.setText("0");
        roundOffValueTextView.setText("0");
        roundOffEdittext.setText("0");
        roundOffEdittext.setSelection(1);

        //additional discount type spinner
        ArrayAdapter<String> additionalDiscountTypeArrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, discountTypeArr);
        additionalDiscountTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        additionalDiscountTypeSpinner.setAdapter(additionalDiscountTypeArrayAdapter);

        //Additional Discount EditText
        additionalEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s != null && s.toString().length() != 0) {
                        setInputFiltersForAdditionalDiscount();
                        if (additionalDiscountTypeSpinner.getSelectedItemPosition() == 1) {
                            additionalDiscountValueTextView.setText("-" + ParseDouble(s.toString()));
                        } else {
                            if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                                additionalDiscountValueTextView.setText("-" +
                                        ((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", "")) *
                                                (ParseDouble(s.toString()))) / 100));
                            } else {
                                additionalDiscountValueTextView.setText("-" +
                                        ((ParseDouble(materialCostValueTextView.getText().toString().trim()) *
                                                (ParseDouble(s.toString()))) / 100));

                            }
                        }

                        setGrandTotalAndPayableAmount();
                    } else {
                        additionalDiscountValueTextView.setText("0");
                        setGrandTotalAndPayableAmount();
                    }
                } catch (Exception ex) {
                    additionalEdittext.setText("");
                    additionalDiscountValueTextView.setText("0");
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Additional Discount Spinner change items
        additionalDiscountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (additionalEdittext.getText().toString().trim() != null &&
                        !additionalEdittext.getText().toString().trim().isEmpty()) {
                    if (position == 0) {
                        //Set Range of typing in additional EditText
                        additionalEdittext.setFilters(new InputFilter[]{new InputFilterForPercentageAndRupees("0", "100")});

                        //Remove characters from additional editText
                        if (ParseDouble(additionalEdittext.getText().toString().trim()) > 100) {
                            additionalEdittext.setText(additionalEdittext.getText().toString().substring(0, 2));
                            additionalEdittext.setSelection(additionalEdittext.getText().toString().length());
                        }

                        if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                            additionalDiscountValueTextView.setText("-" +
                                    ((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", "")) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100));
                        } else {
                            additionalDiscountValueTextView.setText("-" +
                                    ((ParseDouble(materialCostValueTextView.getText().toString().trim()) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100));

                        }
                    } else {
                        //Set Range of typing in additional EditText for Rs
                        additionalEdittext.setFilters(new InputFilter[]
                                {new InputFilterForPercentageAndRupees("0", materialCostValueTextView.getText().toString().trim()
                                )});

                        if (additionalEdittext.getText().toString().trim().length() != 0) {
                            if (Constants.ParseDouble(additionalEdittext.getText().toString().trim()) >
                                    Constants.ParseDouble(materialCostValueTextView.getText().toString().trim())) {
                                additionalEdittext.setText("0.00");
                            } else {
                                additionalEdittext.setText(additionalEdittext.getText().toString());
                            }
                            additionalEdittext.setSelection(additionalEdittext.getText().toString().trim().length());
                        }

                        additionalDiscountValueTextView.setText("-" +
                                ParseDouble(additionalEdittext.getText().toString()));
                    }

                    setGrandTotalAndPayableAmount();
                } else {
                    if (position == 0) {
                        //Set Range of typing in additional EditText for percentage
                        additionalEdittext.setFilters(new InputFilter[]{new InputFilterForPercentageAndRupees("0", "100")});
                    } else {
                        //Set Range of typing in additional EditText for Rs
                        additionalEdittext.setFilters(new InputFilter[]
                                {new InputFilterForPercentageAndRupees("0", materialCostValueTextView.getText().toString().trim()
                                )});
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //hide layouts
        // totalAmountLayout.setVisibility(View.GONE);
        // totalDiscountLayout.setVisibility(View.GONE);
        //  materialCostLayout.setVisibility(View.GONE);
        // additionalDiscountLayout.setVisibility(View.GONE);

        //Get all charges
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getQuotationPrefilledData(getActivity());
        }

        //Tax Spinner
        taxChargeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taxChargeTypeSpinnerSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Charge List Type Three Spinner
        chargeTypeThreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (chargesList3ArrayList != null && chargesList3ArrayList.size() > 0) {
                    if (position == 0) {
                        if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                            chargeThreeValueTextView.setText("-" +
                                    ((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", "")) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100));
                        } else {
                            chargeThreeValueTextView.setText("-" +
                                    ((ParseDouble(materialCostValueTextView.getText().toString().trim()) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100));
                        }
                    } else {
                        chargeThreeValueTextView.setText("-" +
                                ParseDouble(additionalEdittext.getText().toString()));
                    }
                    setGrandTotalAndPayableAmount();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Round Off Amount
        roundOffEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.toString().length() != 0 && !s.equals("-") && !s.equals(".")) {
                    try {
                        roundOffValueTextView.setText(s);
                        payableAmountValueTextView.setText(String.valueOf(ParseDouble(grandTotalValueTextView.getText().toString().trim()) +
                                ParseDouble(s.toString())));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinnerEmailTemplate.setVisibility(View.GONE);
        showEmailPreview.setVisibility(View.GONE);
        showSMSPreview.setVisibility(View.GONE);
        spinnerSmsTemplate.setVisibility(View.GONE);
        basicInfoLayout.setVisibility(View.GONE);

        smsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinnerSmsTemplate.setVisibility(View.VISIBLE);
                    showSMSPreview.setVisibility(View.VISIBLE);
                    strSms = "1";
                } else {
                    spinnerSmsTemplate.setVisibility(View.GONE);
                    showSMSPreview.setVisibility(View.GONE);
                    strSms = "0";
                }
            }
        });


        emailCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinnerEmailTemplate.setVisibility(View.VISIBLE);
                    showEmailPreview.setVisibility(View.VISIBLE);
                    strMail = "1";
                } else {
                    spinnerEmailTemplate.setVisibility(View.GONE);
                    showEmailPreview.setVisibility(View.GONE);
                    strMail = "0";
                }
            }
        });


    }

    private void setInputFiltersForAdditionalDiscount() {
        if (additionalDiscountTypeSpinner.getSelectedItemPosition() == 0) {
            //Set Range of typing in additional EditText
            additionalEdittext.setFilters(new InputFilter[]{new InputFilterForPercentageAndRupees("0", "100")});
        } else {
            //Set Range of typing in additional EditText for Rs
            additionalEdittext.setFilters(new InputFilter[]
                    {new InputFilterForPercentageAndRupees("0", materialCostValueTextView.getText().toString().trim()
                    )});
        }
    }

    private void contactPersonData() {

        final ArrayAdapter<ContactPerson> contactPersonArrayAdapter = new ArrayAdapter<ContactPerson>(getActivity(), R.layout.simple_spinner_item, contactPersonListArrayList);
        contactPersonArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        contactPersonAutoCompleteTextview.setAdapter(contactPersonArrayAdapter);


        contactPersonAutoCompleteTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactPersonAutoCompleteTextview.showDropDown();
            }
        });

        //when autocomplete is clicked
        contactPersonAutoCompleteTextview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = contactPersonArrayAdapter.getItem(position).getName();
                contactPersonAutoCompleteTextview.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < contactPersonListArrayList.size(); i++) {
                    if (contactPersonListArrayList.get(i).getName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strContactPerson = contactPersonListArrayList.get(pos).getCodeid();
                strCodeId = contactPersonListArrayList.get(pos).getCodeid();
            }
        });

        contactPersonAutoCompleteTextview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strContactPerson = contactPersonAutoCompleteTextview.getText().toString();
                    for (int i = 0; i < contactPersonListArrayList.size(); i++) {
                        String temp = contactPersonListArrayList.get(i).getName();
                        if (strContactPerson.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    contactPersonAutoCompleteTextview.setText("");
                    strContactPerson = "";
                    strCodeId = "";
                }
            }

        });


    }

    public double sumAllCharges() {
        double sum = 0;
        for (int i = 0; i < preChargesListArrayList.size(); i++) {
            if (preChargesListArrayList.get(i).getDiscountAmountValue() != null &&
                    !preChargesListArrayList.get(i).getDiscountAmountValue().isEmpty()) {
                sum += ParseDouble(preChargesListArrayList.get(i).getDiscountAmountValue());
            }
        }
        return sum;
    }

    public void taxChargeTypeSpinnerSelection(int position) {
        double taxValue = ParseDouble(taxListArrayList.get(position).getValue()),
                taxAmount = 0, t_amount, discountedAmount = 0, finalTaxAmount = 0;

        //Calculating amount after discount
        if (materialCostValueTextView.getText().toString().trim().contains(",") &&
                additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                    replace("-", "")));
        } else if (materialCostValueTextView.getText().toString().trim().contains(",")) {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", "")));
        } else if (additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim())
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                    replace("-", "")));
        } else {
            discountedAmount = (ParseDouble(materialCostValueTextView.getText().toString().trim())
                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", "")));
        }
        discountedAmount += sumAllCharges();


        double taxType = ParseDouble(taxListArrayList.get(position).getButapatid()),
                taxCount = ParseDouble(taxListArrayList.get(position).getCount()),
                b = ParseDouble(taxListArrayList.get(position).getButaprid());
        if (taxListArrayList != null && taxListArrayList.size() > 0) {
            if (taxValue > 0) {
                if (taxCount > 0) {
                    double a = 0;
                    if (taxType == 1) {
                        t_amount = (discountedAmount * (taxValue / 2)) / 100;
                    } else {
                        t_amount = taxValue / 2;
                    }
                    switch ((int) b) {
                        case 1:
                            t_amount = (t_amount * 100) / 100;
                            break;
                        case 2:
                            t_amount = (t_amount);
                            break;
                        case 3:
                            t_amount = Math.ceil(t_amount);
                            break;
                        case 4:
                            t_amount = Math.floor(t_amount);
                            break;
                        default:
                            t_amount = (t_amount * 100) / 100;
                            break;
                    }
                    a = t_amount;
                    if (taxType == 1) {
                        t_amount = (discountedAmount * (taxValue / 2)) / 100;
                    } else {
                        t_amount = taxValue / 2;
                    }
                    switch ((int) b) {
                        case 1:
                            t_amount = (t_amount * 100) / 100;
                            break;
                        case 2:
                            t_amount = (t_amount);
                            break;
                        case 3:
                            t_amount = Math.ceil(t_amount);
                            break;
                        case 4:
                            t_amount = Math.floor(t_amount);
                            break;
                        default:
                            t_amount = (t_amount * 100) / 100;
                            break;
                    }
                    a += t_amount;
                    taxAmount = a;
                } else {
                    if (taxType == 1) {
                        taxAmount = (discountedAmount * taxValue) / 100;
                    } else {
                        taxAmount = taxValue;
                    }
                    switch ((int) b) {
                        case 1:
                            taxAmount = (taxAmount * 100) / 100;
                            break;
                        case 2:
                            taxAmount = (taxAmount);
                            break;
                        case 3:
                            taxAmount = Math.ceil(taxAmount);
                            break;
                        case 4:
                            taxAmount = Math.floor(taxAmount);
                            break;
                    }
                }
                finalTaxAmount += taxAmount;

                taxChargeValueTextView.setText(roundTwoDecimals(finalTaxAmount) + "");

                setGrandTotalAndPayableAmount();
            } else {
                if (position == 0) {
                    taxChargeValueTextView.setText("0.0");
                    setGrandTotalAndPayableAmount();
                }
            }
        }
    }

    public double sumAllChargesTwo() {
        double sum = 0;
        for (int i = 0; i < postChargesListArrayList.size(); i++) {
            if (postChargesListArrayList.get(i).getDiscountAmountValue() != null &&
                    !postChargesListArrayList.get(i).getDiscountAmountValue().isEmpty()) {
                sum += ParseDouble(postChargesListArrayList.get(i).getDiscountAmountValue());
            }
        }
        return sum;
    }

    public double sumAllChargesThree() {
        double sum = 0;
        for (int i = 1; i < chargesList3ArrayList.size(); i++) {
            if (chargesList3ArrayList.get(i).isSelected()) {
                sum += (((getDiscountedAmount() + sumAllCharges())
                        * ParseDouble(chargesList3ArrayList.get(i).getValue())) / 100);
            }
        }
        return sum;
    }

    public double sumTaxCharges() {
        double sum = 0;
        if (taxChargeValueTextView.getText().toString().trim().length() != 0) {
            sum = ParseDouble(taxChargeValueTextView.getText().toString().trim());
        }
        return sum;
    }

    private void dialogAddItems(Activity activity, final String sourceType, final ItemData itemData,
                                final int positionToEdit) {
        productsDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        productsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productsDialog.setContentView(R.layout.dialog_add_quotation_item);
        Window window = productsDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        try {
            final ArrayList<Tax> taxArrayList = new ArrayList<>();
            final TextView titleTextView = (TextView) productsDialog.findViewById(R.id.title_textView);
            LinearLayout linearLayout = (LinearLayout) productsDialog.findViewById(R.id.linearLayout);
            final EditText productNameEdittext = (EditText) productsDialog.findViewById(R.id.product_name_edittext);
            final EditText quantityEdittext = (EditText) productsDialog.findViewById(R.id.quantity_edittext);
            final Spinner quantityTypeSpinner = (Spinner) productsDialog.findViewById(R.id.quantity_type_spinner);
            final EditText priceEdittext = (EditText) productsDialog.findViewById(R.id.price_edittext);
            final EditText discountEdittext = (EditText) productsDialog.findViewById(R.id.discount_edittext);
            final EditText taxEdittext = (EditText) productsDialog.findViewById(R.id.tax_edittext);
            final Spinner discountTypeSpinner = (Spinner) productsDialog.findViewById(R.id.discount_type_spinner);
            final Spinner taxTypeSpinner = (Spinner) productsDialog.findViewById(R.id.tax_type_spinner);
            ImageView discountImageView = (ImageView) productsDialog.findViewById(R.id.discount_imageView);
            TextView textViewAdd = (TextView) productsDialog.findViewById(R.id.textView_add);
            TextView textViewBack = (TextView) productsDialog.findViewById(R.id.textView_back);
            final EditText amountEditText = (EditText) productsDialog.findViewById(R.id.amount_editText);
            TextInputLayout amountTextInputLayout = (TextInputLayout) productsDialog.findViewById(R.id.amount_textInputLayout);
            final TextInputLayout subTotalTextInputLayout = (TextInputLayout) productsDialog.findViewById(R.id.subTotal_textInputLayout);
            final EditText subtotalEditText = (EditText) productsDialog.findViewById(R.id.subtotal_editText);
            final EditText discountedAmountEdittext = (EditText) productsDialog.findViewById(R.id.discounted_amount_edittext);
            final EditText taxedAmountEdittext = (EditText) productsDialog.findViewById(R.id.taxed_amount_edittext);
            final ImageView productImageView = (ImageView) productsDialog.findViewById(R.id.product_imageView);

            if (itemData.getImage() != null && !itemData.getImage().isEmpty()) {
                productImageView.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(itemData.getImage())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(productImageView);
            } else {
                productImageView.setVisibility(View.GONE);
            }

            //Name
            productNameEdittext.setText(itemData.getName().trim());
            quantityEdittext.setText(itemData.getQuantity());
            quantityEdittext.setSelection(quantityEdittext.getText().toString().trim().length());
            priceEdittext.setText(itemData.getPrice());
            priceEdittext.setSelection(priceEdittext.getText().toString().trim().length());

            amountEditText.setEnabled(false);

            //Discount Spinner change items
            discountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Position 0 means discount in % and 1 means discount in INR
                    if (position == 0) {
                        //Set Range of typing in discount EditText
                        discountEdittext.setFilters(new InputFilter[]{new InputFilterForPercentageAndRupees("0", "100")});

                        //Remove characters from discount editText
                        if (ParseDouble(discountEdittext.getText().toString().trim()) > 100) {
                            discountEdittext.setText(discountEdittext.getText().toString().substring(0, 2));
                            discountEdittext.setSelection(discountEdittext.getText().toString().length());
                        }

                        subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                ParseDouble(quantityEdittext.getText().toString()) *
                                (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100)
                                +
                                ((((ParseDouble(priceEdittext.getText().toString()) *
                                        ParseDouble(quantityEdittext.getText().toString()) *
                                        (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100) *
                                        ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                .getValue())) / 100)));

                        discountedAmountEdittext.setText("" + roundTwoDecimals((ParseDouble(priceEdittext.getText().toString()) *
                                ParseDouble(quantityEdittext.getText().toString()) *
                                (ParseDouble(discountEdittext.getText().toString()))) / 100));

                    } else {
                        //Removing Filter
                        discountEdittext.setFilters(new InputFilter[]{});

                        subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                ParseDouble(quantityEdittext.getText().toString())) -
                                ParseDouble(discountEdittext.getText().toString()))
                                +
                                ((((ParseDouble(priceEdittext.getText().toString()) *
                                        ParseDouble(quantityEdittext.getText().toString())) -
                                        ParseDouble(discountEdittext.getText().toString()))) *
                                        ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                .getValue())) / 100));
                        if (discountEdittext.getText().toString().trim().length() != 0) {
                            if (Constants.ParseDouble(discountEdittext.getText().toString().trim()) >
                                    Constants.ParseDouble(amountEditText.getText().toString().trim())) {
                                discountEdittext.setText("0.00");
                            } else {
                                discountEdittext.setText(discountEdittext.getText().toString());
                            }
                            discountEdittext.setSelection(discountEdittext.getText().toString().trim().length());
                        }
                        discountedAmountEdittext.setText(discountEdittext.getText().toString());
                    }
                    taxedAmountEdittext.setText("" + roundTwoDecimals((((ParseDouble(priceEdittext.getText().toString()) *
                            ParseDouble(quantityEdittext.getText().toString())) -
                            ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                            (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                    .getValue()))) / 100));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //Tax Spinner Item Changes
            taxTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Tax selectedTax = (Tax) parent.getAdapter().getItem(position);
                    if (discountEdittext.getText().toString().trim() != null &&
                            discountEdittext.getText().toString().trim().length() != 0 &&
                            !discountEdittext.getText().toString().trim().equals("0")) {
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                    ParseDouble(quantityEdittext.getText().toString())) -
                                    ParseDouble(discountEdittext.getText().toString()))
                                    +
                                    ((((ParseDouble(priceEdittext.getText().toString()) *
                                            ParseDouble(quantityEdittext.getText().toString())) -
                                            ParseDouble(discountEdittext.getText().toString()))) *
                                            ParseDouble(selectedTax.getValue())) / 100));

                        } else {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                    ParseDouble(quantityEdittext.getText().toString()) *
                                    (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100)
                                    +
                                    ((((ParseDouble(priceEdittext.getText().toString()) *
                                            ParseDouble(quantityEdittext.getText().toString()) *
                                            (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100) *
                                            ParseDouble(selectedTax.getValue())) / 100)));
                        }
                    } else {
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                    ParseDouble(quantityEdittext.getText().toString())) -
                                    ParseDouble(discountEdittext.getText().toString()))
                                    +
                                    ((((ParseDouble(priceEdittext.getText().toString()) *
                                            ParseDouble(quantityEdittext.getText().toString())) -
                                            ParseDouble(discountEdittext.getText().toString()))) *
                                            ParseDouble(selectedTax.getValue())) / 100));

                        } else {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                    ParseDouble(quantityEdittext.getText().toString()) *
                                    (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100)
                                    +
                                    ((((ParseDouble(priceEdittext.getText().toString()) *
                                            ParseDouble(quantityEdittext.getText().toString()) *
                                            (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100) *
                                            ParseDouble(selectedTax.getValue())) / 100)));

                        }
                    }
                    taxedAmountEdittext.setText("" + roundTwoDecimals((((ParseDouble(priceEdittext.getText().toString()) *
                            ParseDouble(quantityEdittext.getText().toString())) -
                            ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                            (ParseDouble(selectedTax.getValue()))) / 100));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //Discount Edittext
            discountEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    try {
                        // if (s != null && s.toString().length() != 0) {
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                    ParseDouble(quantityEdittext.getText().toString())) -
                                    ParseDouble(s.toString()))
                                    +
                                    ((((ParseDouble(priceEdittext.getText().toString()) *
                                            ParseDouble(quantityEdittext.getText().toString())) -
                                            ParseDouble(s.toString()))) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100));

                            discountedAmountEdittext.setText(discountEdittext.getText().toString());
                        } else {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                    ParseDouble(quantityEdittext.getText().toString()) *
                                    (100.00 - ParseDouble(s.toString()))) / 100)
                                    +
                                    ((((ParseDouble(priceEdittext.getText().toString()) *
                                            ParseDouble(quantityEdittext.getText().toString()) *
                                            (100.00 - ParseDouble(s.toString()))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)));

                            discountedAmountEdittext.setText("" + roundTwoDecimals((ParseDouble(priceEdittext.getText().toString()) *
                                    ParseDouble(quantityEdittext.getText().toString()) *
                                    (ParseDouble(s.toString()))) / 100));
                        }
                        taxedAmountEdittext.setText("" + roundTwoDecimals((((ParseDouble(priceEdittext.getText().toString()) *
                                ParseDouble(quantityEdittext.getText().toString())) -
                                ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                                (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                        .getValue()))) / 100));
                        // }
                    } catch (NumberFormatException ex) {
                        discountEdittext.setText("");
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //Price EditText
            priceEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        //   if (s != null && s.toString().length() != 0) {

                        amountEditText.setText("" + (ParseDouble(s.toString()) *
                                ParseDouble(quantityEdittext.getText().toString())));
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(s.toString()) *
                                    ParseDouble(quantityEdittext.getText().toString())) -
                                    ParseDouble(discountEdittext.getText().toString()))
                                    +
                                    ((((ParseDouble(quantityEdittext.getText().toString()) *
                                            ParseDouble(s.toString())) -
                                            ParseDouble(discountEdittext.getText().toString()))) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100));

                            discountedAmountEdittext.setText(discountEdittext.getText().toString());
                        } else {
                            subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(quantityEdittext.getText().toString()) *
                                    ParseDouble(s.toString()) *
                                    (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100)
                                    +
                                    ((((ParseDouble(quantityEdittext.getText().toString()) *
                                            ParseDouble(s.toString()) *
                                            (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)));

                            discountedAmountEdittext.setText("" + roundTwoDecimals((ParseDouble(quantityEdittext.getText().toString()) *
                                    ParseDouble(s.toString()) *
                                    (ParseDouble(discountEdittext.getText().toString()))) / 100));

                        }
                        taxedAmountEdittext.setText("" + roundTwoDecimals((((ParseDouble(s.toString()) *
                                ParseDouble(quantityEdittext.getText().toString())) -
                                ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                                (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                        .getValue()))) / 100));
                        //}
                    } catch (NumberFormatException ex) {
                        priceEdittext.setText("");
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //Quantity EditText
            quantityEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // if (s != null && s.toString().length() != 0) {
                    amountEditText.setText("" + (ParseDouble(s.toString()) *
                            ParseDouble(priceEdittext.getText().toString())));
                    if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                        subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                ParseDouble(s.toString())) -
                                ParseDouble(discountEdittext.getText().toString()))
                                +
                                ((((ParseDouble(priceEdittext.getText().toString()) *
                                        ParseDouble(s.toString())) -
                                        ParseDouble(discountEdittext.getText().toString()))) *
                                        ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                .getValue())) / 100));

                        discountedAmountEdittext.setText(discountEdittext.getText().toString());
                    } else {
                        subtotalEditText.setText("" + roundTwoDecimals(((ParseDouble(priceEdittext.getText().toString()) *
                                ParseDouble(s.toString()) *
                                (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100)
                                +
                                ((((ParseDouble(priceEdittext.getText().toString()) *
                                        ParseDouble(s.toString()) *
                                        (100.00 - ParseDouble(discountEdittext.getText().toString()))) / 100) *
                                        ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                .getValue())) / 100)));

                        discountedAmountEdittext.setText("" + roundTwoDecimals((ParseDouble(priceEdittext.getText().toString()) *
                                ParseDouble(s.toString()) *
                                (ParseDouble(discountEdittext.getText().toString()))) / 100));
                    }
                    taxedAmountEdittext.setText("" + roundTwoDecimals((((ParseDouble(priceEdittext.getText().toString()) *
                            ParseDouble(s.toString())) -
                            ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                            (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                    .getValue()))) / 100));
                }
                //  }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            if (isNumeric(priceEdittext.getText().toString()) && isNumeric(quantityEdittext.getText().toString())) {
                amountEditText.setText("" + (ParseDouble(priceEdittext.getText().toString()) *
                        ParseDouble(quantityEdittext.getText().toString())));
            }

            ArrayAdapter<String> discountTypeArrayAdapter =
                    new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, discountTypeArr);
            discountTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            discountTypeSpinner.setAdapter(discountTypeArrayAdapter);


            //Tax
            Tax taxStatic = new Tax();
            taxStatic.setName("Select");
            taxStatic.setValue("0.0");
            taxStatic.setButapid("0");
            taxArrayList.add(taxStatic);

            taxArrayList.addAll(itemData.getTaxes());
            ArrayAdapter<Tax> taxTypeArrayAdapter =
                    new ArrayAdapter<Tax>(activity, R.layout.simple_spinner_item, taxArrayList);
            taxTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            taxTypeSpinner.setAdapter(taxTypeArrayAdapter);

            final String[] quantityTypeArr = new String[itemData.getUnitsData().size()];
            if (itemData.getUnitsData().size() > 0) {
                for (int i = 0; i < itemData.getUnitsData().size(); i++) {
                    quantityTypeArr[i] = itemData.getUnitsData().get(i).getName().toString();
                }
            }

            //Quantity type spinner
            ArrayAdapter<String> quantityTypeArrayAdapter =
                    new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, quantityTypeArr);
            quantityTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            quantityTypeSpinner.setAdapter(quantityTypeArrayAdapter);


            //Updating value of Quantity if there are multiple types of Quantities so we will match it with MeaId
            if (itemData.getMeaid() != null && !itemData.getMeaid().toString().trim().isEmpty()
                    && !itemData.getMeaid().toString().trim().equals("0")) {
                quantityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (quantityEdittext.getText().toString().trim() != null &&
                                !quantityEdittext.getText().toString().trim().isEmpty() &&
                                ParseDouble(quantityEdittext.getText().toString().trim()) > 0.0
                                && isNumeric(quantityEdittext.getText().toString().trim())) {
                            quantityEdittext.setText("" +
                                    ((ParseDouble(quantityEdittext.getText().toString().trim())) /
                                            (ParseDouble(itemData.getUnitsData().get(position).getConversionrate())))
                                            *
                                            (ParseDouble(itemData.getUnitsData().get(previousQuantityPosOfProduct).getConversionrate())));
                            previousQuantityPosOfProduct = position;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            //Add Item
            textViewAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantityEdittext.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter quantity of product first", Toast.LENGTH_SHORT).show();
                    } else if (priceEdittext.getText().length() == 0 ||
                            Constants.roundTwoDecimals(Constants.ParseDouble(priceEdittext.getText().toString().trim())) <= 0.00) {
                        Toast.makeText(getActivity(), "Please enter valid price field", Toast.LENGTH_SHORT).show();
                    } else {
                        ItemData data = new ItemData();
                        data.setName(productNameEdittext.getText().toString().trim());
                        data.setPrice(priceEdittext.getText().toString().trim());
                        data.setTax(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition()).getName().toString().trim());
                        data.setDiscount(discountEdittext.getText().toString().trim());
                        if (discountTypeSpinner.getSelectedItemPosition() == 0) {
                            data.setDiscountType("1");
                        } else {
                            data.setDiscountType("2");
                        }
                        data.setAmount(amountEditText.getText().toString().trim());
                        data.setQuantity(quantityEdittext.getText().toString().trim());
                        data.setQuantityType(quantityTypeArr[quantityTypeSpinner.getSelectedItemPosition()].toString().trim());
                        data.setSubtotalAmount(subtotalEditText.getText().toString().trim());
                        data.setBoxQty(itemData.getBoxQty());
                        data.setSelectedTaxValue(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition()).getValue().toString().trim());
                        data.setIitid(itemData.getIitid());
                        data.setIsvid(itemData.getIsvid());
                        data.setIid(itemData.getIid());
                        data.setMeaid(itemData.getMeaid());
                        data.setBoxConversionRate(itemData.getBoxConversionRate());
                        data.setRemarks("");
                        data.setPriceConversionRate(itemData.getPriceConversionRate());
                        data.setTaxes(itemData.getTaxes());
                        data.setUnitsData(itemData.getUnitsData());
                        data.setButapid(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition()).getButapid().toString().trim());
                        if (sourceType.equals("edit")) {
                            itemDataArrayList.set(positionToEdit, data);
                        } else {
                            itemDataArrayList.add(data);
                        }
                        quotationItemsAdapter.notifyDataSetChanged();
                        updateAllMainValues();
                        productsDialog.dismiss();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                }
            });

            //Back to selection
            textViewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    productsDialog.dismiss();
                    if (sourceType.equals("scan")) {
                        scanBarcode(REQUEST_IMAGE_CAPTURE);
                    }
                }
            });

            productNameEdittext.setEnabled(false);
            productNameEdittext.setFocusable(false);

            taxTypeSpinner.setSelection(0);
            //Tax Type spinner pre-selected
            if (itemData.getButapid() != null && !itemData.getButapid().isEmpty()) {
                //Auto Select value of spinner for tax
                for (int i = 0; i < taxArrayList.size(); i++) {
                    if (taxArrayList.get(i).getButapid().equals(itemData.getButapid())) {
                        taxTypeSpinner.setSelection(i);
                    }
                }
            }

            //Quantity Type spinner pre-selected
            if (itemData.getQuantityType() != null && !itemData.getQuantityType().isEmpty()) {
                //Auto Select value of spinner for Quantity
                for (int i = 0; i < itemData.getUnitsData().size(); i++) {
                    if (itemData.getQuantityType().equals(itemData.getUnitsData().get(i).getName())) {
                        quantityTypeSpinner.setSelection(i);
                    }
                }
            }

            //Check Item is going to edit or add
            if (sourceType.equals("API") || sourceType.equals("scan")) {
                discountEdittext.setText("0");
                taxEdittext.setText("0");
                titleTextView.setText("Add Product");
                if (sourceType.equals("scan")) {
                    textViewBack.setText("Back to scan");
                }
            } else {
                discountEdittext.setText(itemData.getDiscount());
                if (itemData.getDiscountType().equals("1")) {
                    discountTypeSpinner.setSelection(0);
                } else {
                    discountTypeSpinner.setSelection(1);
                }
                titleTextView.setText("Edit Product");
            }

        } catch (NumberFormatException ex) { // handle your exception
            ex.printStackTrace();
        }


        //If SourceType is edit

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        productsDialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        productsDialog.show();
    }

    //Update all main values
    public void updateAllMainValues() {
        Double totalAmount = 0.0, totalDiscount = 0.0, totalMaterialCost = 0.0;
        for (int i = 0; i < itemDataArrayList.size(); i++) {
            totalAmount += (ParseDouble(itemDataArrayList.get(i).getAmount()));
            totalMaterialCost += (ParseDouble(itemDataArrayList.get(i).getSubtotalAmount()));
            if (itemDataArrayList.get(i).getDiscountType().equals("1")) {
                totalDiscount += (((ParseDouble(itemDataArrayList.get(i).getAmount())) *
                        ParseDouble(itemDataArrayList.get(i).getDiscount())) / 100);
            } else {
                totalDiscount += (ParseDouble(itemDataArrayList.get(i).getDiscount()));
            }
        }
        totalDiscountValueTextView.setText(" " + totalDiscount + "");
        materialCostValueTextView.setText(" " + roundTwoDecimals(totalMaterialCost) + "");
        totalAmountValueTextView.setText(" " + totalAmount + "");

        if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
            totalAmountLayout.setVisibility(View.VISIBLE);
            totalDiscountLayout.setVisibility(View.VISIBLE);
            materialCostLayout.setVisibility(View.VISIBLE);
            additionalDiscountLayout.setVisibility(View.VISIBLE);
        } else {
            //  totalAmountLayout.setVisibility(View.GONE);
            //  totalDiscountLayout.setVisibility(View.GONE);
            //  materialCostLayout.setVisibility(View.GONE);
            //  additionalDiscountLayout.setVisibility(View.GONE);
        }

        //Spinner selection
        if (taxListArrayList != null && taxListArrayList.size() > 1) {
            taxChargeTypeSpinnerSelection(taxChargeTypeSpinner.getSelectedItemPosition());
        }
        setGrandTotalAndPayableAmount();
        setInputFiltersForAdditionalDiscount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_imageView:
                int resultCode = 0;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (verifyPermission()) return;
                }
                resultCode = REQUEST_IMAGE_CAPTURE;
                scanBarcode(resultCode);
                break;
            case R.id.customer_name_value_editText:
                if (customerNameValueEditText.getText().toString().trim().length() == 0) {
                    openCustomerSearchDialog();
                }
                break;
            case R.id.product_search_autoCompleteTextView:
                if (productSearchAutoCompleteTextView.getText().toString().trim().length() == 0) {
                    openProductSearchDialog();
                }
                break;
            case R.id.save_textView:
                //Disable Button
                saveTextView.setEnabled(false);

                if (getPostData()) {
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (new PreviewQuotationAsyncTask(getActivity()).getStatus() != AsyncTask.Status.RUNNING) {
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            CreateQuotationAsyncTask createQuotationAsyncTask = new CreateQuotationAsyncTask(getActivity());
                            createQuotationAsyncTask.execute();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                //Enable Button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveTextView.setEnabled(true);
                    }
                }, 3000);
                break;
            case R.id.delivery_time_textInputEditText:
                allDatePickerFragment.show(getChildFragmentManager(), getString(R.string.dailogue_from));
                break;
            case R.id.current_address_add_address:
                dialogAddNewAddress("current_address");
                break;
            case R.id.site_address_add_address:
                dialogAddNewAddress("site_address");
                break;
            case R.id.current_address_add_contact:
                dialogAddContactPerson();
                break;
            case R.id.clear_customerInfo_imageButton:
                if (customerNameValueEditText.getText().toString().trim().length() != 0) {
                    customerNameValueEditText.setText("");
                    strCuid = "";
                    strCodeId = "";
                    newCurrentAddressSaid = null;
                    newSiteAddressSaid = null;
                    mobileValueEditText.setText("");
                    emailValueEditText.setText("");
                    mobileLayout.setVisibility(View.GONE);
                    emailLayout.setVisibility(View.GONE);
                    addressMainLayout.setVisibility(View.GONE);
                    contactPersonAutoCompleteTextview.setVisibility(View.GONE);
                    textInputContactPerson.setVisibility(View.GONE);
                    contactPersonAutoCompleteTextview.setText("");
                    addContactLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.currentAddress_textView:
                if (!currentAddressTextView.getText().toString().trim().equals("No address")) {
                    openCurrentAddressesFullScreenDialog(currentAddressListArrayList);
                }
                break;
            case R.id.siteAddress_textView:
                if (!siteAddressTextView.getText().toString().trim().equals("No address")) {
                    openSiteAddressesFullScreenDialog(siteAddressListArrayList);
                }
                break;

            case R.id.add_search_quotation_item_show_sms_preview:
                flag = "SMS";
                if (new PreviewQuotationAsyncTask(getActivity()).getStatus() != AsyncTask.Status.RUNNING) {
                    getPrefillDataForPreview();
                }
                break;

            case R.id.add_search_quotation_item_show_email_preview:
                flag = "Email";
                if (new PreviewQuotationAsyncTask(getActivity()).getStatus() != AsyncTask.Status.RUNNING) {
                    getPrefillDataForPreview();
                }
                break;
        }
    }

    private void getPrefillDataForPreview() {
        //Disable Button
        saveTextView.setEnabled(false);
        showSMSPreview.setEnabled(false);
        showEmailPreview.setEnabled(false);

        if (getPostData()) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                PreviewQuotationAsyncTask previewQuotationAsyncTask = new PreviewQuotationAsyncTask(getActivity());
                previewQuotationAsyncTask.execute();
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        }

        //Enable Button
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSMSPreview.setEnabled(true);
                saveTextView.setEnabled(true);
                showEmailPreview.setEnabled(true);
            }
        }, 1000);
    }

    private void dialogShowPreview(String strMobile, String sms, String strFrom, String strTo, String strCC, String strSubject, String strBody, final String strFile, final String flags) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_preview, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog alertDialog = builder.create();

        TextView dialogPreviewPrimaryText;
        LinearLayout dialogPreviewSmsPreviewLayout;
        TextView dialogPreviewSmsMobile;
        TextView dialogPreviewSmsMessage;
        LinearLayout dialogPreviewEmailPreviewLayout;
        TextView dialogPreviewEmailFrom;
        TextView dialogPreviewEmailTo;
        TextView dialogPreviewEmailCc;
        TextView dialogPreviewEmailSubject;
        TextView dialogPreviewEmailBody;
        TextView dialogPreviewEmailAttachment;
        Button dialogPreviewBtnOk;


        dialogPreviewPrimaryText = (TextView) dialogView.findViewById(R.id.dialog_preview_primary_text);
        dialogPreviewSmsPreviewLayout = (LinearLayout) dialogView.findViewById(R.id.dialog_preview_sms_preview_layout);
        dialogPreviewSmsMobile = (TextView) dialogView.findViewById(R.id.dialog_preview_sms_mobile);
        dialogPreviewSmsMessage = (TextView) dialogView.findViewById(R.id.dialog_preview_sms_message);
        dialogPreviewEmailPreviewLayout = (LinearLayout) dialogView.findViewById(R.id.dialog_preview_email_preview_layout);
        dialogPreviewEmailFrom = (TextView) dialogView.findViewById(R.id.dialog_preview_email_from);
        dialogPreviewEmailTo = (TextView) dialogView.findViewById(R.id.dialog_preview_email_to);
        dialogPreviewEmailCc = (TextView) dialogView.findViewById(R.id.dialog_preview_email_cc);
        dialogPreviewEmailSubject = (TextView) dialogView.findViewById(R.id.dialog_preview_email_subject);
        dialogPreviewEmailBody = (TextView) dialogView.findViewById(R.id.dialog_preview_email_body);
        dialogPreviewEmailAttachment = (TextView) dialogView.findViewById(R.id.dialog_preview_email_attachment);
        dialogPreviewBtnOk = (Button) dialogView.findViewById(R.id.dialog_preview_btn_ok);
        if (flag.equals("SMS")) {
            dialogPreviewEmailPreviewLayout.setVisibility(View.GONE);
            dialogPreviewSmsPreviewLayout.setVisibility(View.VISIBLE);
            dialogPreviewSmsMobile.setText(strMobile);
            dialogPreviewSmsMessage.setText(sms);
        } else {
            dialogPreviewEmailPreviewLayout.setVisibility(View.VISIBLE);
            dialogPreviewSmsPreviewLayout.setVisibility(View.GONE);
            dialogPreviewEmailFrom.setText(strFrom);
            dialogPreviewEmailTo.setText(strTo);
            dialogPreviewEmailCc.setText(strCC);
            dialogPreviewEmailSubject.setText(strSubject);
            dialogPreviewEmailBody.setText(Html.fromHtml(strBody));
            dialogPreviewEmailAttachment.setText(strFile);
        }

        dialogPreviewBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                flag = "";
            }
        });

        dialogPreviewEmailAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Download a file and display in phone's download folder
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .mkdirs();
                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(strFile);
                DownloadManager.Request request = new DownloadManager.Request(uri)
                        .setTitle(strFile + "_quotation" + ".pdf")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                strFile + "_quotation" + ".pdf")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    //Single Contact Person
    private void dialogAddContactPerson() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_lead_add_contacts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextName, editTextDesignation, editTextEmail, editTextPhone;
        LinearLayout contactTypeLayout;
        TextView contactTypeTextView;
        final CheckBox checkBoxOwner;
        final Spinner contactTypeSpinner;

        editTextName = (EditText) dialogView.findViewById(R.id.name);
        editTextDesignation = (EditText) dialogView.findViewById(R.id.designation);
        editTextEmail = (EditText) dialogView.findViewById(R.id.email);
        editTextPhone = (EditText) dialogView.findViewById(R.id.phone_number);
        Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        contactTypeLayout = (LinearLayout) dialogView.findViewById(R.id.contactType_layout);
        contactTypeTextView = (TextView) dialogView.findViewById(R.id.contact_type_textView);
        contactTypeSpinner = (Spinner) dialogView.findViewById(R.id.contact_type_spinner);
        checkBoxOwner = (CheckBox) dialogView.findViewById(R.id.checkBox_owner);
        final TextView specifyTextView = (TextView) dialogView.findViewById(R.id.specify_textView);
        final EditText specifyEditText = (EditText) dialogView.findViewById(R.id.specify_editText);

        String simple = "Specify ";
        String colored = "*";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(simple);
        int start = spannableStringBuilder.length();
        spannableStringBuilder.append(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        specifyTextView.setText(spannableStringBuilder);

        contactTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ContactPersonTypeData selectedContact = (ContactPersonTypeData) parent.getAdapter().getItem(position);
                if (selectedContact != null && selectedContact.getExtraAttribute() != null
                        && !selectedContact.getExtraAttribute().isEmpty()) {
                    specifyEditText.setVisibility(View.VISIBLE);
                    specifyTextView.setVisibility(View.VISIBLE);
                } else {
                    specifyEditText.setVisibility(View.GONE);
                    specifyTextView.setVisibility(View.GONE);
                    specifyEditText.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Contacts contacts = new Contacts();

        //Contact Person Types
       /* ReferralAddContact referralAddContact = new ReferralAddContact();
        referralAddContact.setValue("Select");

        contactPersonTypeArrayList.add(0, referralAddContact);*/

        ArrayAdapter<ContactPersonTypeData> contactPersonTypeArrayAdapter =
                new ArrayAdapter<ContactPersonTypeData>(getActivity(), R.layout.simple_spinner_item, contactTypeDataArrayList);
        contactPersonTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        contactTypeSpinner.setAdapter(contactPersonTypeArrayAdapter);

        //Default Selected Item
        contactTypeSpinner.setSelection(0);

        //Make visible this layout here only
        if (contactTypeDataArrayList != null && contactTypeDataArrayList.size() > 1) {
            contactTypeLayout.setVisibility(View.VISIBLE);
        } else {
            contactTypeLayout.setVisibility(View.GONE);
        }
        // checkBoxOwner.setVisibility(View.VISIBLE);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().isEmpty()) {
                    editTextName.setError(getString(R.string.enter_name));
                } else if (editTextEmail.getText().toString().trim().isEmpty()) {
                    editTextEmail.setError(getString(R.string.enter_email));
                } else if (editTextEmail.getText().toString().trim().length() != 0 && !emailValidator.validateEmail(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.valid_email_error));
                } else if (editTextPhone.getText().toString().trim().isEmpty()) {
                    editTextPhone.setError(getString(R.string.enter_phone_number));
                } else if (editTextPhone.getText().toString().trim().length() != 0 &&
                        editTextPhone.getText().toString().trim().length() != 10) {
                    editTextPhone.setError("Phone number should be of 10 digits");
                } else if (specifyEditText.getVisibility() == View.VISIBLE &&
                        specifyEditText.getText().toString().trim().length() == 0) {
                    specifyEditText.setError("Please enter specify contact type.");
                } else {
                    /*if (checkBoxOwner.isChecked()) {
                        contacts.setOwner("1");
                    } else {*/
                    contacts.setOwner("0");
                    //}
                    contacts.setCodeid("");
                    contacts.setName(editTextName.getText().toString());
                    contacts.setDesignation(editTextDesignation.getText().toString());
                    contacts.setEmail(editTextEmail.getText().toString());
                    contacts.setPhoneNo(editTextPhone.getText().toString());
                    if (contactTypeSpinner.getSelectedItemPosition() != 0) {
                        contacts.setContactTypeId(contactTypeDataArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getAttributeId());
                        contacts.setContactTypeValue(contactTypeDataArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getCusavid());
                    } else {
                        contacts.setContactTypeId("");
                        contacts.setContactTypeValue("");
                    }
                    contacts.setCuId(strCuid);
                    //linearLayoutContactDetails.setVisibility(View.VISIBLE);

                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        addContactPerson(contacts);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }


                    alertDialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    //Add Single Contact Person
    public void addContactPerson(final Contacts contacts) {
        task = getString(R.string.add_customer_add_contact_person);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        try {
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();

            jsonObject.put("cd-name", contacts.getName());
            jsonObject.put("cd-email", contacts.getEmail());
            jsonObject.put("cd-phone-no", contacts.getPhoneNo());
            jsonObject.put("cd-id", contacts.getID());
            jsonObject.put("cd-designation", contacts.getDesignation());
            jsonObject.put("cd-is-owner", contacts.getOwner());
            jsonObject.put("cd-type-id", contacts.getContactTypeId());
            jsonObject.put("contact-type-value", contacts.getContactTypeValue());
            jsonArray.put(jsonObject);
        } catch (JSONException js) {
            js.printStackTrace();
        }
        Call<ApiResponse> call = apiService.addContactPerson(version, key, task, userId, accessToken, contacts.getCuId(),
                jsonObject.toString());
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        ContactPerson contactPerson = new ContactPerson();
                        contactPerson.setCodeid(apiResponse.getData().getCodeid());
                        contactPerson.setName(contacts.getName());
                        contactPersonListArrayList.add(contactPerson);
                        contactPersonAutoCompleteTextview.setText(contacts.getName());
                        contactPersonAutoCompleteTextview.setSelection(contactPerson.getName().length());
                        strCodeId = apiResponse.getData().getCodeid();
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void dialogAddNewAddress(String addressType) {
     /*   View dialogView = View.inflate(getActivity(), R.layout.dialog_add_new_address, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();*/

        Intent intentAddNewAddress = new Intent(getActivity(), AddNewAddressActivity.class);
        intentAddNewAddress.putExtra("cuid", strCuid);
        intentAddNewAddress.putExtra("addressType", addressType);
        getActivity().startActivityForResult(intentAddNewAddress, 300);
        //startActivity(intentAddNewAddress);

    }

    public void scanBarcode(int resultCode) {
        Intent intentScan = new Intent(getActivity(), ScannerActivity.class);
        getActivity().startActivityForResult(intentScan, resultCode);
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
                getActivity().requestPermissions(PERMISSION_STORAGE, REQUEST_MULTIPLE_PERMISSION);
            }
            return true;
        }
        return false;
    }

    @TargetApi(23)
    private void displayPermissionDialog(String msg, final String[] permission, final int resultCode) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog
                .Builder(getActivity())
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

    @TargetApi(23)
    private boolean checkAllPermission() {
        boolean isPermissionRequired = false;
        for (String permission : PERMISSION_STORAGE) {
            if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
                isPermissionRequired = true;
            }
        }
        return isPermissionRequired;
    }

    @Override
    public void editItemAndOpenDialog(int position) {
        dialogAddItems(getActivity(), "edit", itemDataArrayList.get(position), position);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == currentAddressCheckBox) {
            if (isChecked) {
                currentAddressTextView.setVisibility(View.GONE);
                siteAddressLayout.setVisibility(View.GONE);
                sameSiteAddressCheckBox.setVisibility(View.GONE);
                noSiteAddressCheckBox.setVisibility(View.GONE);
                siteAddressTextView.setVisibility(View.GONE);
                linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
            } else {
                currentAddressTextView.setVisibility(View.VISIBLE);
                siteAddressLayout.setVisibility(View.VISIBLE);
                sameSiteAddressCheckBox.setVisibility(View.VISIBLE);
                noSiteAddressCheckBox.setVisibility(View.VISIBLE);
                if (!sameSiteAddressCheckBox.isChecked() && !noSiteAddressCheckBox.isChecked()) {
                    siteAddressTextView.setVisibility(View.VISIBLE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.VISIBLE);
                } else {
                    siteAddressTextView.setVisibility(View.GONE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
                }
            }
        } else if (buttonView == noSiteAddressCheckBox) {
            if (isChecked) {
                siteAddressTextView.setVisibility(View.GONE);
                linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
                sameSiteAddressCheckBox.setChecked(false);
            } else {
                if (sameSiteAddressCheckBox.isChecked()) {
                    siteAddressTextView.setVisibility(View.GONE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.GONE);

                } else {
                    siteAddressTextView.setVisibility(View.VISIBLE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.VISIBLE);
                }
                if (siteAddressTextView.getText().toString().trim().length() == 0) {
                    if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
                        setSiteAddressText(0);
                    }
                }
            }
        } else if (buttonView == sameSiteAddressCheckBox) {
            if (isChecked) {
                noSiteAddressCheckBox.setChecked(false);
                siteAddressTextView.setVisibility(View.GONE);
                linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
            } else {
                if (noSiteAddressCheckBox.isChecked()) {
                    siteAddressTextView.setVisibility(View.GONE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
                } else {
                    siteAddressTextView.setVisibility(View.VISIBLE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.VISIBLE);
                    if (siteAddressTextView.getText().toString().trim().length() == 0) {
                        setSiteAddressText(0);
                    }
                }
            }
        }
    }

    public void getCustomersAddress(String id) {
        task = getString(R.string.customer_address_list);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getCustomersData(version, key, task, userId, accessToken, id);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (currentAddressListArrayList != null && currentAddressListArrayList.size() > 0) {
                        currentAddressListArrayList.clear();
                    }

                    if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
                        siteAddressListArrayList.clear();
                    }
                    if (contactTypeDataArrayList != null && contactTypeDataArrayList.size() > 0) {
                        contactTypeDataArrayList.clear();
                    }
                    if (contactPersonListArrayList != null && contactPersonListArrayList.size() > 0) {
                        contactPersonListArrayList.clear();
                    }
                    if (apiResponse.getSuccess()) {

                        //Get All Addresses
                        for (final AddressList addressList : apiResponse.getData().getAddressList()) {
                            if (addressList != null) {
                                currentAddressListArrayList.add(addressList);
                                siteAddressListArrayList.add(addressList);
                            }

                        }

                        //Get Customer Type
                        if (apiResponse.getData().getCustomerType().equals("1")) {

                            //Get All contact persons of customer
                            for (final ContactPerson contactPerson : apiResponse.getData().getContactPersonArr()) {
                                if (contactPerson != null) {
                                    contactPersonListArrayList.add(contactPerson);
                                    Log.d("contact Person", contactPerson.getName());
                                }
                            }

                            contactPersonAutoCompleteTextview.setVisibility(View.VISIBLE);
                            if (contactPersonListArrayList != null && contactPersonListArrayList.size() > 0) {
                                contactPersonAutoCompleteTextview.setText(contactPersonListArrayList.get(0).getName());
                                strCodeId = contactPersonListArrayList.get(0).getCodeid();
                            }
                            addContactLayout.setVisibility(View.VISIBLE);
                            textInputContactPerson.setVisibility(View.VISIBLE);

                        } else {
                            contactPersonAutoCompleteTextview.setVisibility(View.GONE);
                            textInputContactPerson.setVisibility(View.GONE);
                            addContactLayout.setVisibility(View.GONE);
                            contactPersonListArrayList.clear();
                        }

                        //Get Contact Person Types of customer
                        for (final ContactPersonTypeData contactPersonTypeData : apiResponse.getData().getContactPersonTypeData()) {
                            if (contactPersonTypeData != null) {
                                contactTypeDataArrayList.add(contactPersonTypeData);
                            }
                        }
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Change Text of current and site address
                    if (currentAddressListArrayList != null && currentAddressListArrayList.size() > 0) {
                        //By default we will show address of 0 pos
                        if (newCurrentAddressSaid != null && !newCurrentAddressSaid.isEmpty()) {
                            for (int i = 0; i < currentAddressListArrayList.size(); i++) {
                                if (currentAddressListArrayList.get(i).getSaid().equals(newCurrentAddressSaid)) {
                                    currentAddressListArrayList.get(i).setChecked(true);
                                    setCurrentAddressText(i);
                                }
                            }
                        } else {
                            currentAddressListArrayList.get(0).setChecked(true);
                            setCurrentAddressText(0);
                        }
                    } else {
                        currentAddressTextView.setText("No address");
                    }
                    //By default no site address will be selected
                    noSiteAddressCheckBox.setChecked(true);


                    //Set default current and site addresses after adding a new address also
                    if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
                        if (newSiteAddressSaid != null && !newSiteAddressSaid.isEmpty()) {
                            for (int i = 0; i < siteAddressListArrayList.size(); i++) {
                                if (siteAddressListArrayList.get(i).getSaid().equals(newSiteAddressSaid)) {
                                    siteAddressListArrayList.get(i).setCheckedSite(true);
                                    setSiteAddressText(i);
                                }
                            }
                        } else {
                            siteAddressListArrayList.get(0).setCheckedSite(true);
                            setSiteAddressText(0);
                        }
                    } else {
                        siteAddressTextView.setText("No address");
                    }

                    if (contactTypeDataArrayList != null && contactTypeDataArrayList.size() > 0) {
                        ContactPersonTypeData contactPersonTypeData = new ContactPersonTypeData();
                        contactPersonTypeData.setValue("Select");
                        contactTypeDataArrayList.set(0, contactPersonTypeData);
                    }


                    //Make Visible to main layout of addresses
                    addressMainLayout.setVisibility(View.VISIBLE);

                    if (contactPersonListArrayList.size() > 0) {
                        contactPersonData();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getQuotationPrefilledData(final Activity activity) {
        task = getString(R.string.quotation_tax_charges_data);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (taxListArrayList != null && taxListArrayList.size() > 0) {
                            taxListArrayList.clear();
                        }
                        if (preChargesListArrayList != null && preChargesListArrayList.size() > 0) {
                            preChargesListArrayList.clear();
                        }
                        if (postChargesListArrayList != null && postChargesListArrayList.size() > 0) {
                            postChargesListArrayList.clear();
                        }
                        if (chargesList3ArrayList != null && chargesList3ArrayList.size() > 0) {
                            chargesList3ArrayList.clear();
                        }
                        if (outletArrayList != null && outletArrayList.size() > 0) {
                            outletArrayList.clear();
                        }

                        if (smsTemplateDataArrayList != null && smsTemplateDataArrayList.size() > 0) {
                            smsTemplateDataArrayList.clear();
                        }

                        if (emailTemplateArrArrayList != null && emailTemplateArrArrayList.size() > 0) {
                            emailTemplateArrArrayList.clear();
                        }

                      /*  SmsTemplateDatum smsTemplate = new SmsTemplateDatum();
                        smsTemplate.setTitle("Select Template");
                        smsTemplateDataArrayList.add(smsTemplate);*/
                        if (smsTemplateDataArrayList.size() > 0) {
                            smsTemplateDataArrayList.clear();
                        }
                        if (emailTemplateArrArrayList.size() > 0) {
                            emailTemplateArrArrayList.clear();
                        }
                        for (final SmsTemplateDatum smsTemplateDatum : apiResponse.getData().getSmsTemplateData()) {
                            if (smsTemplateDatum != null && smsTemplateDatum.getTitle() != null) {
                                smsTemplateDataArrayList.add(smsTemplateDatum);
                            }
                        }

                     /*   EmailTemplateArr emailTemplate = new EmailTemplateArr();
                        emailTemplate.setTitle("Select Template");
                        emailTemplateArrArrayList.add(emailTemplate);*/

                        for (final EmailTemplateArr emailTemplateArr : apiResponse.getData().getEmailTemplateArr()) {
                            if (emailTemplateArr != null && emailTemplateArr.getTitle() != null) {
                                emailTemplateArrArrayList.add(emailTemplateArr);
                            }
                        }

                        ArrayAdapter<SmsTemplateDatum> smsTemplateArrayAdapter =
                                new ArrayAdapter<SmsTemplateDatum>(getActivity(), R.layout.simple_spinner_item, smsTemplateDataArrayList);
                        smsTemplateArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        spinnerSmsTemplate.setAdapter(smsTemplateArrayAdapter);
                        spinnerSmsTemplate.setSelection(0);

                        ArrayAdapter<EmailTemplateArr> emailTemplateArrayAdapter =
                                new ArrayAdapter<EmailTemplateArr>(getActivity(), R.layout.simple_spinner_item, emailTemplateArrArrayList);
                        emailTemplateArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        spinnerEmailTemplate.setAdapter(emailTemplateArrayAdapter);
                        spinnerEmailTemplate.setSelection(0);

                        //One Static
                        TaxList taxListStatic = new TaxList();
                        taxListStatic.setName("Select");
                        taxListStatic.setValue("0.0");
                        taxListStatic.setButapatid("0");
                        taxListStatic.setButapid("0");
                        taxListStatic.setButaprid("0");
                        taxListStatic.setCount("0");
                        taxListArrayList.add(taxListStatic);
                        for (final TaxList taxList : apiResponse.getData().getTaxList()) {
                            if (taxList != null) {
                                taxListArrayList.add(taxList);
                            }
                        }
                        for (final ChargesList chargesList : apiResponse.getData().getChargesList()) {
                            if (chargesList != null) {
                                chargesList.setDiscountAmountValue("0");
                                chargesList.setDiscountValue("0");
                                preChargesListArrayList.add(chargesList);
                            }
                        }
                        for (final ChargesList2 chargesList2 : apiResponse.getData().getChargesList2()) {
                            if (chargesList2 != null) {
                                chargesList2.setDiscountAmountValue("0");
                                chargesList2.setDiscountValue("0");
                                postChargesListArrayList.add(chargesList2);
                            }
                        }

                        //One Static
                        Outlet outletStatic = new Outlet();
                        outletStatic.setName("Select Outlet");
                        outletArrayList.add(outletStatic);
                        if (apiResponse.getData().getOutlet() != null) {
                            for (final Outlet outlet : apiResponse.getData().getOutlet()) {
                                if (outlet != null) {
                                    outletArrayList.add(outlet);
                                }
                            }
                        }

                        //Hide/Show Outlet
                        if (outletArrayList != null && outletArrayList.size() > 1) {
                            //Show textview and spinner of outlet
                            outletTitleTextView.setVisibility(View.VISIBLE);
                            outletSpinner.setVisibility(View.VISIBLE);
                        } else {
                            //Hide textview and spinner of outlet
                            outletTitleTextView.setVisibility(View.GONE);
                            outletSpinner.setVisibility(View.GONE);
                        }


                        //Select
                        ChargesList3 chargesList = new ChargesList3();
                        chargesList.setTitle("Select");
                        chargesList3ArrayList.add(chargesList);
                        for (final ChargesList3 chargesList3 : apiResponse.getData().getChargesList3()) {
                            if (chargesList3 != null) {
                                chargesList3ArrayList.add(chargesList3);
                            }
                        }

                        quotationChargesListAdapter.notifyDataSetChanged();
                        quotationChargesTwoListAdapter.notifyDataSetChanged();

                        //Tax Adapter
                        ArrayAdapter<TaxList> taxListArrayAdapter =
                                new ArrayAdapter<TaxList>(activity, R.layout.simple_spinner_item, taxListArrayList);
                        taxListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        taxChargeTypeSpinner.setAdapter(taxListArrayAdapter);

                        quotationChargesThreeSpinnerCheckAdapter = new QuotationChargesThreeSpinnerCheckAdapter(getActivity(), R.layout.item,
                                chargesList3ArrayList, AddQuotationFragment.this);
                        chargeTypeThreeSpinner.setAdapter(quotationChargesThreeSpinnerCheckAdapter);
                        quotationChargesThreeSpinnerCheckAdapter.notifyDataSetChanged();
                        quotationChargesThreeSpinnerCheckAdapter.setDropDownViewResource(R.layout.item);

                        //Outlet Adapter
                        ArrayAdapter<Outlet> outletArrayAdapter =
                                new ArrayAdapter<Outlet>(activity, R.layout.simple_spinner_item, outletArrayList);
                        outletArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        outletSpinner.setAdapter(outletArrayAdapter);
                        outletSpinner.setSelection(0);

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onQuotationChargeThree(final int position, final boolean checked) {
        final ChargesList3 chargesList3 = new ChargesList3();
        chargesList3.setEcid(chargesList3ArrayList.get(position).getEcid());
        chargesList3.setValue(chargesList3ArrayList.get(position).getValue());
        chargesList3.setTitle(chargesList3ArrayList.get(position).getTitle());
        chargesList3.setSelected(checked);
        chargesList3ArrayList.set(position, chargesList3);


        chargeTypeThreeSpinner.setAdapter(quotationChargesThreeSpinnerCheckAdapter);
        chargeTypeThreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ChargesList3 chargesList3 = new ChargesList3();
                chargesList3.setTitle(parent.getItemAtPosition(position).toString());
                if (checked) {
                    ((TextView) chargeTypeThreeSpinner.getSelectedView().findViewById(R.id.text)).setText(chargesList3.getTitle());
                    //chargeTypeThreeSpinner.setSelection(position);
                } else {
                    chargeTypeThreeSpinner.setSelection(0);
                    for (int i = 1; i < chargesList3ArrayList.size(); i++) {
                        if (chargesList3ArrayList.get(i).isSelected()) {
                            ((TextView) chargeTypeThreeSpinner.getSelectedView().findViewById(R.id.text)).
                                    setText(chargesList3ArrayList.get(i).getTitle());
                        }
                    }
                }
                //      ((TextView) chargeTypeThreeSpinner.getSelectedView().findViewById(R.id.text)).setText(chargesList3.getTitle());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        setGrandTotalAndPayableAmount();
    }

    public void setGrandTotalAndPayableAmount() {
        DecimalFormat format = new DecimalFormat();
        //to remove zero after decimal
        format.setDecimalSeparatorAlwaysShown(false);

        grandTotalValueTextView.setText(String.valueOf(roundTwoDecimals(getDiscountedAmount() + sumAllCharges() +
                sumAllChargesTwo() + sumAllChargesThree() + sumTaxCharges())));

        roundOff = roundTwoDecimals(Math.round(ParseDouble(grandTotalValueTextView.getText().toString().trim())) -
                ParseDouble(grandTotalValueTextView.getText().toString().trim()));

        roundOffEdittext.setText(String.valueOf(roundOff));
        roundOffValueTextView.setText(String.valueOf(roundOff));

        payableAmountValueTextView.setText(String.valueOf
                (format.format(roundOff +
                        ParseDouble(grandTotalValueTextView.getText().toString().trim()))));

        //Set Values of all dropdown/Spinner charges
        double a = 0;
        for (int i = 1; i < chargesList3ArrayList.size(); i++) {
            if (chargesList3ArrayList.get(i).isSelected()) {
                a += (((getDiscountedAmount() + sumAllCharges())
                        * ParseDouble(chargesList3ArrayList.get(i).getValue())) / 100);
            }
        }
        chargeThreeValueTextView.setText(String.valueOf(roundTwoDecimals(a)));

    }

    @Override
    public void passDate(String s) {
        stringStartDate = s;
        try {
            startDate = dateFormatter.parse(stringStartDate);
            System.out.println(startDate);
            String from = dateFormatter.format(startDate);
            deliveryTimeTextInputEditText.setText(from);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passTime(String s) {

    }

    //Get data from fields and post
    public boolean getPostData() {
        //Outlet's Spinner value
        if (outletSpinner.getVisibility() == View.VISIBLE) {
            if (outletSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please select outlet first.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                strChkId = outletArrayList.get(outletSpinner.getSelectedItemPosition()).getChkid();
            }
        }
        if (strCuid == null || strCuid.isEmpty()) {
            Toast.makeText(getActivity(), "Please select a customer first.", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Get IDs of both Addresses
        if (addressMainLayout.getVisibility() == View.VISIBLE) {
            if (currentAddressListArrayList.size() > 0) {
                if (currentAddressTextView.getVisibility() == View.VISIBLE) {
                    for (int i = 0; i < currentAddressListArrayList.size(); i++) {
                        if (currentAddressListArrayList.get(i).isChecked())
                            strSaid = currentAddressListArrayList.get(i).getSaid();
                    }

                    if (sameSiteAddressCheckBox.isChecked()) {
                        strBaid = strSaid;
                    } else if (noSiteAddressCheckBox.isChecked()) {
                        strBaid = "0";
                    } else {
                        for (int i = 0; i < siteAddressListArrayList.size(); i++) {
                            if (siteAddressListArrayList.get(i).isChecked())
                                strBaid = siteAddressListArrayList.get(i).getSaid();
                        }
                    }

                } else {
                    strSaid = "0";
                    strBaid = "0";
                }
            }
        } else {
            strSaid = "0";
            strBaid = "0";
        }

        if (referenceTextInputEditText.getText().toString().trim().length() != 0) {
            strReference = referenceTextInputEditText.getText().toString().trim();
        } else {
            strReference = " ";
        }
        if (paymentModeTextInputEditText.getText().toString().trim().length() != 0) {
            strPaymentMode = paymentModeTextInputEditText.getText().toString().trim();
        } else {
            strPaymentMode = " ";
        }
        if (unloadingDetailsTextInputEditText.getText().toString().trim().length() != 0) {
            strUnloading = unloadingDetailsTextInputEditText.getText().toString().trim();
        } else {
            strUnloading = " ";
        }
        if (remarksTextInputEditText.getText().toString().trim().length() != 0) {
            strRemarks = remarksTextInputEditText.getText().toString().trim();
        } else {
            strRemarks = " ";
        }
        if (drawingNoTextInputEditText.getText().toString().trim().length() != 0) {
            strDrawingNo = drawingNoTextInputEditText.getText().toString().trim();
        } else {
            strDrawingNo = " ";
        }

        //Delivery Date
        strDeliveryDate = "";
        /*try {
            strDeliveryDate = targetFormat.format(dateFormatter.parse(deliveryTimeTextInputEditText.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            strDeliveryDate = "";
        }*/

        if (attendeeSpinner.getSelectedItemPosition() != 0) {
            strUid = String.valueOf(assigneeArrayList.get(attendeeSpinner.getSelectedItemPosition()).getUid());
        } else {
            strUid = "0";
        }

        if (coverageCheckBoxTextView.isChecked()) {
            strCoverage = "1";
        } else {
            strCoverage = "0";
        }

        if (productTaxIncludeCheckBoxTextView.isChecked()) {
            strTaxInclusive = "1";
        } else {
            strTaxInclusive = "0";
        }

        if (productImageShowCheckBoxTextView.isChecked()) {
            strImageShow = "1";
        } else {
            strImageShow = "0";
        }

        strAdditionalDiscount = additionalDiscountValueTextView.getText().toString().trim().replace("-", "");
        if (smsCheckBox.isChecked()) {
            strSms = "1";
        } else {
            strSms = "0";
        }

        if (emailCheckBox.isChecked()) {
            strMail = "1";
        } else {
            strMail = "0";
        }

        //They all were on web, but no need of it in app
        strCC = "";
        strDealerPrice = "0";
        strEtId = "";
        strSmstId = "";
        strRemarks = "";
        strTaxes = "";

        strRound = roundOffEdittext.getText().toString().trim();

        //Checking List of Products has items or not because its mandatory
        if (itemDataArrayList == null || itemDataArrayList.size() == 0) {
            Toast.makeText(getActivity(), "Please add a product first.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void calculateAndSetData() {
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public void getFlagToRefresh(String said, String cuid, String addressType) {
        // Toast.makeText(getActivity(), said, Toast.LENGTH_SHORT).show();
        if (addressType.equals("current_address")) {
            this.newCurrentAddressSaid = said;
        } else if (addressType.equals("site_address")) {
            this.newSiteAddressSaid = said;
        }
        getCustomersAddress(cuid);
    }

    //Open Dialog to select customers
    public void openCustomerSearchDialog() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quotation_customer_search);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        customerSearchEditText = (AutoCompleteTextView) dialog.findViewById(R.id.customer_search_autoCompleteTextView);
        getCustomersProductsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        quotationCustomersAdapter = new QuotationCustomersAdapter(getActivity(),
                customerSearchArrayList, this);

        getCustomersProductsRecyclerView.setLayoutManager(mLayoutManager);
        getCustomersProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getCustomersProductsRecyclerView.setAdapter(quotationCustomersAdapter);
        getCustomersProductsRecyclerView.setNestedScrollingEnabled(false);

        customerSearchEditText.setHint("Search Customer");
        customerSearchEditText.setThreshold(1);
        customerSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (customerSearchEditText.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchCustomer(s.toString().trim());
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    //Searching customer from API after getting input from openCustomerSearchDialog
    public void searchCustomer(String str) {
        task = getString(R.string.add_quotation_search_customer_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //TODO Updated on 31st May. If we need to show balance with name of customer, we have to pass balance 1 else we need to send 0
        Call<ApiResponse> call = apiService.getSearchedCustomerReference(version, key, task, userId, accessToken, str, "0");
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (customerSearchArrayList != null && customerSearchArrayList.size() > 0) {
                            customerSearchArrayList.clear();
                        }
                        for (final CustomerList customerList : apiResponse.getData().getCustomerList()) {
                            if (customerList != null) {
                                customerSearchArrayList.add(customerList);
                            }
                        }
                        refreshCustomerRecyclerView();

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Refreshing data after getting input from openCustomerSearchDialog
    private void refreshCustomerRecyclerView() {
        quotationCustomersAdapter.notifyDataSetChanged();

        customerSearchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                SearchRefferedDatum selected = (SearchRefferedDatum) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    customerNameValueEditText.setText(selected.getName().toString().trim());
                } else if (selected.getCompanyName() != null && !selected.getCompanyName().toString().trim().isEmpty()) {
                    customerNameValueEditText.setText(selected.getCompanyName().toString().trim());
                }
                if (selected.getEmail() != null && !selected.getEmail().toString().trim().isEmpty()) {
                    emailValueEditText.setText(selected.getEmail().toString().trim());
                    emailLayout.setVisibility(View.VISIBLE);
                } else {
                    emailLayout.setVisibility(View.GONE);
                }
                if (selected.getMobile() != null && !selected.getMobile().toString().trim().isEmpty()) {
                    mobileValueEditText.setText(selected.getMobile().toString().trim());
                    mobileLayout.setVisibility(View.VISIBLE);
                } else {
                    mobileLayout.setVisibility(View.GONE);
                }

                //currentAddressLayout.setVisibility(View.VISIBLE);
                customerNameValueEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        customerNameValueEditText.setSelection(customerNameValueEditText.getText().toString().length());
                    }
                });

                //Get CUID
                strCuid = selected.getId();

                //Call API for addresses
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    getCustomersAddress(selected.getId());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }

    //Getting click from dialog after selecting customer
    @Override
    public void onCustomerClick(int position) {
        CustomerList selected = customerSearchArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            customerNameValueEditText.setText(selected.getName().toString().trim());
        } else if (selected.getCompanyName() != null && !selected.getCompanyName().toString().trim().isEmpty()) {
            customerNameValueEditText.setText(selected.getCompanyName().toString().trim());
        }
        if (selected.getEmail() != null && !selected.getEmail().toString().trim().isEmpty()) {
            emailValueEditText.setText(selected.getEmail().toString().trim());
            emailLayout.setVisibility(View.VISIBLE);
        } else {
            emailLayout.setVisibility(View.GONE);
        }
        if (selected.getMobile() != null && !selected.getMobile().toString().trim().isEmpty()) {
            mobileValueEditText.setText(selected.getMobile().toString().trim());
            mobileLayout.setVisibility(View.VISIBLE);
        } else {
            mobileLayout.setVisibility(View.GONE);
        }

        addressMainLayout.setVisibility(View.VISIBLE);
        //currentAddressLayout.setVisibility(View.VISIBLE);
        customerNameValueEditText.post(new Runnable() {
            @Override
            public void run() {
                customerNameValueEditText.setSelection(customerNameValueEditText.getText().toString().length());
            }
        });

        //Get CUID
        strCuid = selected.getId();

        //Call API for addresses
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getCustomersAddress(selected.getId());
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    //Opening Dialog to search Products
    public void openProductSearchDialog() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quotation_customer_search);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        customerSearchEditText = (AutoCompleteTextView) dialog.findViewById(R.id.customer_search_autoCompleteTextView);
        getCustomersProductsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        quotationProductsAdapter = new QuotationProductsAdapter(getActivity(),
                itemListArrayList, this);

        getCustomersProductsRecyclerView.setLayoutManager(mLayoutManager);
        getCustomersProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getCustomersProductsRecyclerView.setAdapter(quotationProductsAdapter);
        getCustomersProductsRecyclerView.setNestedScrollingEnabled(false);

        customerSearchEditText.setHint("Search Product");
        customerSearchEditText.setThreshold(1);
        customerSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (customerSearchEditText.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchProduct(s.toString().trim());
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    //Search Product After getting input from dialog
    public void searchProduct(String str) {
        task = getString(R.string.quotation_search_item);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getSearchedItem(version, key, task, userId, accessToken, str);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (itemListArrayList != null && itemListArrayList.size() > 0) {
                            itemListArrayList.clear();
                        }
                        for (final ItemList itemList : apiResponse.getData().getItemList()) {
                            if (itemList != null) {
                                itemListArrayList.add(itemList);
                            }
                        }
                        refreshProductsAutoCompleteTextView();

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Refreshing Adapter of products after getting from  API
    private void refreshProductsAutoCompleteTextView() {
        // refresh the custom Adapter
        quotationProductsAdapter.notifyDataSetChanged();
    }

    //Click even of selecting product after searching into dialog
    @Override
    public void onProductClick(int position) {
        ItemList selected = itemListArrayList.get(position);
        customerSearchEditText.setText(selected.getName().toString().trim());
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            searchedProductDetails(selected.getId(), "API");
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    //Get Details of clicked items after selecting from a dialog
    public void searchedProductDetails(String id, final String sourceType) {
        task = getString(R.string.quotation_product_details);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getSearchedProductsDetails(version, key, task, userId,
                accessToken, id, strCuid);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        final ItemData itemData = apiResponse.getData().getItemData();
                        dialogAddItems(getActivity(), sourceType, itemData, 0);
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Current Address Full screen dialog
    public void openCurrentAddressesFullScreenDialog(ArrayList<AddressList> addressListArrayList) {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_addresses);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) dialog.findViewById(R.id.toolbar);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        ImageButton backArrowImageButton = (ImageButton) dialog.findViewById(R.id.back_arrow_imageButton);

        backArrowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        addressAdapter = new AddressAdapter(getActivity(), addressListArrayList, this, "current");
        //Addresses RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(addressAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    //Site Address Full screen dialog
    public void openSiteAddressesFullScreenDialog(ArrayList<AddressList> addressListArrayList) {
        dialogSiteAddress = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialogSiteAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSiteAddress.setContentView(R.layout.dialog_addresses);
        Window window = dialogSiteAddress.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        RecyclerView recyclerView = (RecyclerView) dialogSiteAddress.findViewById(R.id.recyclerView);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) dialogSiteAddress.findViewById(R.id.toolbar);
        ImageButton backArrowImageButton = (ImageButton) dialogSiteAddress.findViewById(R.id.back_arrow_imageButton);

        backArrowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSiteAddress != null && dialogSiteAddress.isShowing()) {
                    dialogSiteAddress.dismiss();
                }
            }
        });


        siteAddressAdapter = new SiteAddressAdapter(getActivity(), addressListArrayList, this, "site");
        //Addresses RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(siteAddressAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialogSiteAddress.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogSiteAddress.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialogSiteAddress.show();
    }

    public void setCurrentAddressText(int position) {
        String addressText = "";
        final AddressList addressList = currentAddressListArrayList.get(position);
        if (addressList.getSiteName() != null && !addressList.getSiteName().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getSiteName().toString().trim() + "\n";
        }
        if (addressList.getLine1() != null && !addressList.getLine1().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine1().toString().trim() + "\n";
        }
        if (addressList.getLine2() != null && !addressList.getLine2().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine2().toString().trim() + "\n";
        }
        if (addressList.getCity() != null && !addressList.getCity().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCity().toString().trim() + "\n";
        }
        if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()
                && (addressList.getCity() == null || !addressList.getCity().toString().trim().isEmpty())) {
            addressText = addressText + addressList.getZipCode().toString().trim() + "\n";
        } else {
            if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
                addressText = addressText + " - " + addressList.getZipCode().toString().trim() + "\n";
            }
        }
        if (addressList.getState() != null && !addressList.getState().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getState().toString().trim() + "\n";
        }
        if (addressList.getCountry() != null && !addressList.getCountry().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCountry().toString().trim() + "\n";
        }

        currentAddressTextView.setText(addressText);
        for (int i = 0; i < currentAddressListArrayList.size(); i++) {
            if (currentAddressListArrayList.get(i).getSaid().equals(currentAddressListArrayList.get(position).getSaid())) {
                currentAddressListArrayList.get(i).setChecked(true);

            } else {
                currentAddressListArrayList.get(i).setChecked(false);
            }
        }
    }

    public void setSiteAddressText(int position) {
        String addressText = "";
        final AddressList addressList = siteAddressListArrayList.get(position);
        if (addressList.getSiteName() != null && !addressList.getSiteName().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getSiteName().toString().trim() + "\n";
        }
        if (addressList.getLine1() != null && !addressList.getLine1().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine1().toString().trim() + "\n";
        }
        if (addressList.getLine2() != null && !addressList.getLine2().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine2().toString().trim() + "\n";
        }
        if (addressList.getCity() != null && !addressList.getCity().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCity().toString().trim() + "\n";
        }
        if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()
                && (addressList.getCity() == null || !addressList.getCity().toString().trim().isEmpty())) {
            addressText = addressText + addressList.getZipCode().toString().trim() + "\n";
        } else {
            if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
                addressText = addressText + " - " + addressList.getZipCode().toString().trim() + "\n";
            }
        }
        if (addressList.getState() != null && !addressList.getState().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getState().toString().trim() + "\n";
        }
        if (addressList.getCountry() != null && !addressList.getCountry().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCountry().toString().trim() + "\n";
        }
        siteAddressTextView.setText(addressText);
        for (int i = 0; i < siteAddressListArrayList.size(); i++) {
            if (siteAddressListArrayList.get(i).getSaid().equals(siteAddressListArrayList.get(position).getSaid())) {
                siteAddressListArrayList.get(i).setCheckedSite(true);

            } else {
                siteAddressListArrayList.get(i).setCheckedSite(false);
            }
        }
    }

    @Override
    public void currentAddressItemClick(int position) {
        setCurrentAddressText(position);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    @Override
    public void siteAddressItemClick(int position) {
        setSiteAddressText(position);
        if (dialogSiteAddress != null && dialogSiteAddress.isShowing()) {
            dialogSiteAddress.dismiss();
        }
    }

    //To deal with empty string of amount
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    //AsyncTask to Create a new Quotation
    public class CreateQuotationAsyncTask extends AsyncTask<Void, Void, String> {
        private Activity context;

        public CreateQuotationAsyncTask(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return getString();
        }

        private String getString() {
            // TODO Auto-generated method stub

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", strUid);
                jsonObject.put("baid", strBaid);
                jsonObject.put("said", strSaid);
                jsonObject.put("coverage", strCoverage);
                jsonObject.put("cc", strCC);
                jsonObject.put("cuid", strCuid);
                jsonObject.put("tax_inclusive", strTaxInclusive);
                jsonObject.put("image_show", strImageShow);
                jsonObject.put("dealer_price", strDealerPrice);
                jsonObject.put("codeid", strCodeId);
                jsonObject.put("chkid", strChkId);
                jsonObject.put("etid", strEtId);
                jsonObject.put("qotemid", qotemid);
                jsonObject.put("smstid", strSmstId);
                jsonObject.put("discount", strAdditionalDiscount);
                jsonObject.put("mail", strMail);
                jsonObject.put("sms", strSms);
                jsonObject.put("round", strRound);
                jsonObject.put("round_field", "0");

                //TODO Basic Info
                JSONArray jsonArrayBasicInfo = new JSONArray();

                //Reference
                JSONObject basicInfoJsonObject = new JSONObject();
                basicInfoJsonObject.put("id", "1");
                basicInfoJsonObject.put("value", strReference);
                jsonArrayBasicInfo.put(basicInfoJsonObject);
                //Delivery Date
                basicInfoJsonObject = new JSONObject();
                basicInfoJsonObject.put("id", "3");
                basicInfoJsonObject.put("value", strDeliveryDate);
                jsonArrayBasicInfo.put(basicInfoJsonObject);
                //Payment Mode
                basicInfoJsonObject = new JSONObject();
                basicInfoJsonObject.put("id", "4");
                basicInfoJsonObject.put("value", strPaymentMode);
                jsonArrayBasicInfo.put(basicInfoJsonObject);
                //Unloading details
                basicInfoJsonObject = new JSONObject();
                basicInfoJsonObject.put("id", "5");
                basicInfoJsonObject.put("value", strUnloading);
                jsonArrayBasicInfo.put(basicInfoJsonObject);
                //Remarks Id
                basicInfoJsonObject = new JSONObject();
                basicInfoJsonObject.put("id", "6");
                basicInfoJsonObject.put("value", strRemarks);
                jsonArrayBasicInfo.put(basicInfoJsonObject);
                //Drawing No
                basicInfoJsonObject = new JSONObject();
                basicInfoJsonObject.put("id", "12");
                basicInfoJsonObject.put("value", strDrawingNo);
                jsonArrayBasicInfo.put(basicInfoJsonObject);

                jsonObject.put("attributes", jsonArrayBasicInfo);

                //TODO Products Items
                JSONArray productItemJsonArray = new JSONArray();
                for (int i = 0; i < itemDataArrayList.size(); i++) {
                    JSONObject productsItemJsonObject = new JSONObject();
                    productsItemJsonObject.put("iitid", itemDataArrayList.get(i).getIitid());
                    productsItemJsonObject.put("isvid", itemDataArrayList.get(i).getIsvid());
                    productsItemJsonObject.put("iid", itemDataArrayList.get(i).getIid());
                    productsItemJsonObject.put("name", itemDataArrayList.get(i).getName());
                    productsItemJsonObject.put("meaid", itemDataArrayList.get(i).getMeaid());
                    productsItemJsonObject.put("quantity", itemDataArrayList.get(i).getQuantity());
                    productsItemJsonObject.put("price", itemDataArrayList.get(i).getPrice());
                    productsItemJsonObject.put("dealer_price", strProductsDealerPrice);
                    productsItemJsonObject.put("amount", itemDataArrayList.get(i).getAmount());
                    productsItemJsonObject.put("measurements", itemDataArrayList.get(i).getMeasurement());
                    productsItemJsonObject.put("taxes", strTaxes);
                    productsItemJsonObject.put("butapid", itemDataArrayList.get(i).getButapid());
                    productsItemJsonObject.put("tax", itemDataArrayList.get(i).getSelectedTaxValue());
                    productsItemJsonObject.put("price_conversion_rate", itemDataArrayList.get(i).getPriceConversionRate());
                    productsItemJsonObject.put("conversion_rate", itemDataArrayList.get(i).getConversionRate());
                    productsItemJsonObject.put("box_conversion_rate", itemDataArrayList.get(i).getBoxConversionRate());
                    productsItemJsonObject.put("box_qty", itemDataArrayList.get(i).getBoxQty());
                    productsItemJsonObject.put("unit_name", itemDataArrayList.get(i).getUnitName());
                    productsItemJsonObject.put("image", itemDataArrayList.get(i).getImage());
                    productsItemJsonObject.put("discount", itemDataArrayList.get(i).getDiscount());
                    productsItemJsonObject.put("discount_type", itemDataArrayList.get(i).getDiscountType());
                    //     productsItemJsonObject.put("remarks", itemDataArrayList.get(i).getRemarks());
                    productsItemJsonObject.put("package_qty", itemDataArrayList.get(i).getPackageQty());
                    productsItemJsonObject.put("product_attributes", "");

                    productItemJsonArray.put(productsItemJsonObject);
                }

                JSONArray productsJsonArray = new JSONArray();
                JSONObject productsJsonObject = new JSONObject();
                productsJsonObject.put("trooid", "1");
                productsJsonObject.put("items", productItemJsonArray);

                productsJsonArray.put(productsJsonObject);

                //Array
                jsonObject.put("products", productsJsonArray);

                //TODO PRE-CHARGES
                JSONArray preChargeJSONArray = new JSONArray();
                for (int i = 0; i < preChargesListArrayList.size(); i++) {
                    JSONObject preChargeJsonObject = new JSONObject();
                    preChargeJsonObject.put("id", preChargesListArrayList.get(i).getEcid());
                    preChargeJsonObject.put("value", preChargesListArrayList.get(i).getDiscountValue());

                    preChargeJSONArray.put(preChargeJsonObject);
                }

                //Array
                jsonObject.put("pre_charge", preChargeJSONArray);

                //TODO TAX

                // Suraj told poonam to add this check

                if (taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid().equals("0")) {
                    jsonObject.put("tax", "");
                } else {
                    jsonObject.put("tax", taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid());
                }


                //TODO POST CHARGES
                JSONArray postChargeJSONArray = new JSONArray();
                for (int i = 0; i < postChargesListArrayList.size(); i++) {
                    JSONObject postChargeJsonObject = new JSONObject();
                    postChargeJsonObject.put("id", postChargesListArrayList.get(i).getEcid());
                    postChargeJsonObject.put("value", postChargesListArrayList.get(i).getDiscountValue());

                    postChargeJSONArray.put(postChargeJsonObject);
                }

                //Array
                jsonObject.put("post_charge", postChargeJSONArray);

                //TODO CHARGES
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 1; i < chargesList3ArrayList.size(); i++) {
                    if (chargesList3ArrayList.get(i).isSelected()) {
                        list.add(chargesList3ArrayList.get(i).getEcid());
                    }
                }

                //Array
                jsonObject.put("charge", new JSONArray(list));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String postParams = "data=" + jsonObject.toString();
            Log.d("POST_UPDATE", postParams);

            URL obj = null;
            HttpURLConnection con = null;
            try {
                obj = new URL(AppPreferences.getLastDomain(context, AppUtils.DOMAIN)
                        + "?urlq=service" + "&version=" + Constants.version + "&key=" + Constants.key + "&task=" + context.getString(R.string.add_quotation)
                        + "&user_id=" + AppPreferences.getUserId(context, AppUtils.USER_ID)
                        + "&access_token=" + AppPreferences.getAccessToken(context, AppUtils.ACCESS_TOKEN));
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                // For POST only - BEGIN
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                // For POST only - END
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.e("REQUEST", response.toString());
                    Log.e("RESPONSE", response.toString());
                    return response.toString();

                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (result != null) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    boolean status = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("result_code");
                    if (status) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                    //Deleted User
                    else if (code.equals(Constants.WRONG_CREDENTIALS) ||
                            code.equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), message);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    //AsyncTask to Create a new Quotation
    public class PreviewQuotationAsyncTask extends AsyncTask<Void, Void, String> {
        private Activity context;

        public PreviewQuotationAsyncTask(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return getString();
        }

        private String getString() {
            // TODO Auto-generated method stub
            try {
                if (spinnerSmsTemplate.isShown()) {
                    if (flag.equals("SMS")) {
                        strSmstId = smsTemplateDataArrayList.get(spinnerSmsTemplate.getSelectedItemPosition()).getSmstid();
                        strEtId = "";
                    }
                } else {
                    strSmstId = "";
                }
                if (spinnerEmailTemplate.isShown()) {
                    if (!flag.equals("SMS")) {
                        strEtId = emailTemplateArrArrayList.get(spinnerEmailTemplate.getSelectedItemPosition()).getEtid();
                        strSmstId = "";
                    }
                } else {
                    strEtId = "";
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", strUid);
                    jsonObject.put("baid", strBaid);
                    jsonObject.put("said", strSaid);
                    jsonObject.put("coverage", strCoverage);
                    jsonObject.put("cc", strCC);
                    jsonObject.put("cuid", strCuid);
                    jsonObject.put("tax_inclusive", strTaxInclusive);
                    jsonObject.put("image_show", strImageShow);
                    jsonObject.put("dealer_price", strDealerPrice);
                    jsonObject.put("codeid", strCodeId);
                    jsonObject.put("chkid", strChkId);
                    jsonObject.put("etid", strEtId);
                    jsonObject.put("qotemid", qotemid);
                    jsonObject.put("smstid", strSmstId);
                    jsonObject.put("discount", strAdditionalDiscount);
                    jsonObject.put("mail", strMail);
                    jsonObject.put("sms", strSms);
                    jsonObject.put("round", strRound);
                    jsonObject.put("round_field", "0");

                    //TODO Basic Info
                    JSONArray jsonArrayBasicInfo = new JSONArray();

                    //Reference
                    JSONObject basicInfoJsonObject = new JSONObject();
                    basicInfoJsonObject.put("id", "1");
                    basicInfoJsonObject.put("value", strReference);
                    jsonArrayBasicInfo.put(basicInfoJsonObject);
                    //Delivery Date
                    basicInfoJsonObject = new JSONObject();
                    basicInfoJsonObject.put("id", "3");
                    basicInfoJsonObject.put("value", strDeliveryDate);
                    jsonArrayBasicInfo.put(basicInfoJsonObject);
                    //Payment Mode
                    basicInfoJsonObject = new JSONObject();
                    basicInfoJsonObject.put("id", "4");
                    basicInfoJsonObject.put("value", strPaymentMode);
                    jsonArrayBasicInfo.put(basicInfoJsonObject);
                    //Unloading details
                    basicInfoJsonObject = new JSONObject();
                    basicInfoJsonObject.put("id", "5");
                    basicInfoJsonObject.put("value", strUnloading);
                    jsonArrayBasicInfo.put(basicInfoJsonObject);
                    //Remarks Id
                    basicInfoJsonObject = new JSONObject();
                    basicInfoJsonObject.put("id", "6");
                    basicInfoJsonObject.put("value", strRemarks);
                    jsonArrayBasicInfo.put(basicInfoJsonObject);
                    //Drawing No
                    basicInfoJsonObject = new JSONObject();
                    basicInfoJsonObject.put("id", "12");
                    basicInfoJsonObject.put("value", strDrawingNo);
                    jsonArrayBasicInfo.put(basicInfoJsonObject);

                    jsonObject.put("attributes", jsonArrayBasicInfo);

                    //TODO Products Items
                    JSONArray productItemJsonArray = new JSONArray();
                    for (int i = 0; i < itemDataArrayList.size(); i++) {
                        JSONObject productsItemJsonObject = new JSONObject();
                        productsItemJsonObject.put("iitid", itemDataArrayList.get(i).getIitid());
                        productsItemJsonObject.put("isvid", itemDataArrayList.get(i).getIsvid());
                        productsItemJsonObject.put("iid", itemDataArrayList.get(i).getIid());
                        productsItemJsonObject.put("name", itemDataArrayList.get(i).getName());
                        productsItemJsonObject.put("meaid", itemDataArrayList.get(i).getMeaid());
                        productsItemJsonObject.put("quantity", itemDataArrayList.get(i).getQuantity());
                        productsItemJsonObject.put("price", itemDataArrayList.get(i).getPrice());
                        productsItemJsonObject.put("dealer_price", strProductsDealerPrice);
                        productsItemJsonObject.put("amount", itemDataArrayList.get(i).getAmount());
                        productsItemJsonObject.put("measurements", itemDataArrayList.get(i).getMeasurement());
                        productsItemJsonObject.put("taxes", strTaxes);
                        productsItemJsonObject.put("butapid", itemDataArrayList.get(i).getButapid());
                        productsItemJsonObject.put("tax", itemDataArrayList.get(i).getSelectedTaxValue());
                        productsItemJsonObject.put("price_conversion_rate", itemDataArrayList.get(i).getPriceConversionRate());
                        productsItemJsonObject.put("conversion_rate", itemDataArrayList.get(i).getConversionRate());
                        productsItemJsonObject.put("box_conversion_rate", itemDataArrayList.get(i).getBoxConversionRate());
                        productsItemJsonObject.put("box_qty", itemDataArrayList.get(i).getBoxQty());
                        productsItemJsonObject.put("unit_name", itemDataArrayList.get(i).getUnitName());
                        productsItemJsonObject.put("image", itemDataArrayList.get(i).getImage());
                        productsItemJsonObject.put("discount", itemDataArrayList.get(i).getDiscount());
                        productsItemJsonObject.put("discount_type", itemDataArrayList.get(i).getDiscountType());
                        //     productsItemJsonObject.put("remarks", itemDataArrayList.get(i).getRemarks());
                        productsItemJsonObject.put("package_qty", itemDataArrayList.get(i).getPackageQty());
                        productsItemJsonObject.put("product_attributes", "");

                        productItemJsonArray.put(productsItemJsonObject);
                    }

                    JSONArray productsJsonArray = new JSONArray();
                    JSONObject productsJsonObject = new JSONObject();
                    productsJsonObject.put("trooid", "1");
                    productsJsonObject.put("items", productItemJsonArray);

                    productsJsonArray.put(productsJsonObject);

                    //Array
                    jsonObject.put("products", productsJsonArray);

                    //TODO PRE-CHARGES
                    JSONArray preChargeJSONArray = new JSONArray();
                    for (int i = 0; i < preChargesListArrayList.size(); i++) {
                        JSONObject preChargeJsonObject = new JSONObject();
                        preChargeJsonObject.put("id", preChargesListArrayList.get(i).getEcid());
                        preChargeJsonObject.put("value", preChargesListArrayList.get(i).getDiscountValue());
                        preChargeJSONArray.put(preChargeJsonObject);
                    }

                    //Array
                    jsonObject.put("pre_charge", preChargeJSONArray);

                    //TODO TAX

                    // Suraj told poonam to add this check

                    if (taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid().equals("0")) {
                        jsonObject.put("tax", "");
                    } else {
                        jsonObject.put("tax", taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid());
                    }


                    // For preview bit will  be 1
                    jsonObject.put("bit", "1");

                    //TODO POST CHARGES
                    JSONArray postChargeJSONArray = new JSONArray();
                    for (int i = 0; i < postChargesListArrayList.size(); i++) {
                        JSONObject postChargeJsonObject = new JSONObject();
                        postChargeJsonObject.put("id", postChargesListArrayList.get(i).getEcid());
                        postChargeJsonObject.put("value", postChargesListArrayList.get(i).getDiscountValue());

                        postChargeJSONArray.put(postChargeJsonObject);
                    }

                    //Array
                    jsonObject.put("post_charge", postChargeJSONArray);

                    //TODO CHARGES
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 1; i < chargesList3ArrayList.size(); i++) {
                        if (chargesList3ArrayList.get(i).isSelected()) {
                            list.add(chargesList3ArrayList.get(i).getEcid());
                        }
                    }

                    //Array
                    jsonObject.put("charge", new JSONArray(list));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String postParams = "data=" + jsonObject.toString();
                Log.d("POST_UPDATE", postParams);

                URL obj = null;
                HttpURLConnection con = null;
                try {
                    obj = new URL(AppPreferences.getLastDomain(context, AppUtils.DOMAIN)
                            + "?urlq=service" + "&version=1.0&key=123&task=" + context.getString(R.string.add_quotation)
                            + "&user_id=" + AppPreferences.getUserId(context, AppUtils.USER_ID)
                            + "&access_token=" + AppPreferences.getAccessToken(context, AppUtils.ACCESS_TOKEN));
                    con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    // For POST only - BEGIN
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    os.write(postParams.getBytes());
                    os.flush();
                    os.close();
                    // For POST only - END
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // success
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Log.e("REQUEST", response.toString());
                        Log.e("RESPONSE", response.toString());

                        return response.toString();

                    } else {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (result != null) {
                JSONObject jsonObject;
                try {
                    String strFrom = "";
                    String strTo = "";
                    String strSubject = "";
                    String strBody = "";
                    String strFileName = "";
                    String strMobile = "";
                    String strMessage = "";

                    jsonObject = new JSONObject(result);
                    boolean status = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("result_code");

                    // String str
                    if (status) {
                        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                        JSONObject jsonObjectDataSms = jsonObjectData.getJSONObject("sms_data");
                        JSONObject jsonObjectDataEmail = jsonObjectData.getJSONObject("email_Data");

                        if (jsonObjectDataSms.length() > 0) {
                            strMobile = jsonObjectDataSms.getString("mobile");
                            strMessage = jsonObjectDataSms.getString("message");
                        }

                        if (jsonObjectDataEmail.length() > 0) {
                            strFrom = jsonObjectDataEmail.getString("from");
                            strTo = jsonObjectDataEmail.getString("to");
                            strSubject = jsonObjectDataEmail.getString("subject");
                            strBody = jsonObjectDataEmail.getString("body");
                            strFileName = jsonObjectDataEmail.getString("filename");
                        }
                        if (flag.equals("SMS")) {
                            dialogShowPreview(strMobile, strMessage, strFrom, strTo, "", strSubject, strBody, strFileName, flag);
                        } else {
                            dialogShowPreview(strMobile, strMessage, strFrom, strTo, "", strSubject, strBody, strFileName, flag);
                        }
                    }
                    //Deleted User
                    else if (code.equals(Constants.WRONG_CREDENTIALS) ||
                            code.equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), message);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }
}

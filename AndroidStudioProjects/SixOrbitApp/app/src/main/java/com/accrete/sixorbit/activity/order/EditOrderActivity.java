package com.accrete.sixorbit.activity.order;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.barCodeScanner.ScannerActivity;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.activity.quotations.OrderQuotationsSelectCustomerActivity;
import com.accrete.sixorbit.adapter.OrderItemsAdapter;
import com.accrete.sixorbit.adapter.PaymentModesAdapter;
import com.accrete.sixorbit.adapter.QuotationChargesListAdapter;
import com.accrete.sixorbit.adapter.QuotationChargesThreeSpinnerCheckAdapter;
import com.accrete.sixorbit.adapter.QuotationChargesTwoListAdapter;
import com.accrete.sixorbit.adapter.QuotationPreChargesAdapter;
import com.accrete.sixorbit.adapter.QuotationProductsAdapter;
import com.accrete.sixorbit.adapter.QuotationThreeChargesDropDownAdapter;
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
import com.accrete.sixorbit.model.ChargesList;
import com.accrete.sixorbit.model.ChargesList2;
import com.accrete.sixorbit.model.ChargesList3;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.CustomerInfo;
import com.accrete.sixorbit.model.CustomerSalesType;
import com.accrete.sixorbit.model.EmailTemplateArr;
import com.accrete.sixorbit.model.ItemData;
import com.accrete.sixorbit.model.ItemList;
import com.accrete.sixorbit.model.LedgerSearchData;
import com.accrete.sixorbit.model.PaymentModeData;
import com.accrete.sixorbit.model.PaymentOptionAttribute;
import com.accrete.sixorbit.model.SmsTemplateDatum;
import com.accrete.sixorbit.model.Tax;
import com.accrete.sixorbit.model.TaxList;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.InputFilterForPercentageAndRupees;
import com.accrete.sixorbit.utils.ScalingUtilities;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.isNumeric;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

public class EditOrderActivity extends AppCompatActivity implements View.OnClickListener,
        QuotationProductsAdapter.QuotationProductsAdapterListener,
        OrderItemsAdapter.OrderItemsAdapterListener,
        QuotationChargesListAdapter.QuotationChargesAdapterListener,
        QuotationChargesThreeSpinnerCheckAdapter.QuotationChargesThreeAdapterListener,
        QuotationChargesTwoListAdapter.QuotationCharges2AdapterListener,
        QuotationThreeChargesDropDownAdapter.QuotationChargesThreeListener,
        QuotationPreChargesAdapter.PreChargesAdapterListener,
        RadioGroup.OnCheckedChangeListener,
        PassDateToCounsellor, PaymentModesAdapter.PaymentModesAdapterListener {
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    private static final int REQUEST_IMAGE_SCAN = 10;
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_IMAGE_GALLERY_PIC = 12;
    private final String[] PERMISSION_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Toolbar toolbar;
    private LinearLayout addItemLayout;
    private TextView addItemTextView;
    private View addProductsButton;
    private ImageView scanImageView;
    private FrameLayout container;
    private List<String> permissionsNeeded = new ArrayList<>();
    private boolean isFirstTime = true;
    private CardView cardViewInnerCustomer;
    private TextView customerNameTitleTextView;
    private TextView emailTextView;
    private TextView mobileTextView;
    private TextView changeAddCustomerTextView;
    private String orderTemId, strOrderId, strCuid, strBaid, strSaid, strChkId, strCName, strCMobile, strCEmail, status, strMail, strSms, strAssignee,
            strTaxInclusive, strRound, strAdditionalDiscount, strImageShow, strDealerPrice, strTaxes, strUid, strCoverage, strCC,
            strEtId, strSmstId, strRemarks, strProductsDealerPrice = "", strCodeId, flag = "", strExtraDiscountTitle, strExtensiveTaxFlag,
            strExtraDiscountId, strSalesLedgerName, strSalesLedgerId, strPaymentLedgerName, strPaymentLedgerId, strFlagDealerPrice = "0",
            stringStartDate, strIsVid, strProductsImageUrl, strQoId;
    private CardView cardViewInnerQuotOptions;
    private TextInputLayout selectDateTextInputLayout;
    private TextInputEditText selectDateTextInputEditText;
    private TextView customerSalesTypeTextView;
    private Spinner customerSalesTypeSpinner;
    private TextView salesLedgerTitleTextView;
    private AutoCompleteTextView salesLedgerValueAutocompleteTextView;
    private ImageButton salesLedgerClearImageButton;
    private TextView orderTypeTitleTextView;
    private RadioGroup orderTypeRadioGroup;
    private RadioButton creditRadioBtn;
    private RadioButton cashCarryRadioBtn;
    private TextInputLayout percentLimitTextInputLayout;
    private TextInputEditText percentLimitTextInputEditText;
    private TextView paymentTermsTitleTextView;
    private Spinner paymentTermsSpinner;
    private TextInputLayout daysTextInputLayout;
    private TextInputEditText daysTextInputEditText;
    private CheckBox advancePaymentCheckBoxTextView;
    private LinearLayout advancePaymentLinearLayout;
    private TextView paymentModeTitleTextView;
    private Spinner paymentModeSpinner;
    private TextView searchPaymentLedgerTitleTextView;
    private AutoCompleteTextView searchPaymentLedgerValueAutocompleteTextView;
    private ImageButton searchPaymentLedgerClearImageButton;
    private TextInputLayout advancePaymentAmountTextInputLayout;
    private TextInputEditText advancePaymentAmountTextInputEditText;
    private RecyclerView recyclerViewAdvancePayment;
    private CheckBox orderDealerPriceCheckBox;
    private CardView cardViewInnerPriceLayout;
    private LinearLayout priceLayout;
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
    private LinearLayout grandTotalLayout;
    private TextView grandTotalTitleTextView;
    private TextView grandTotalValueTextView;
    private LinearLayout roundOffLayout;
    private TextView roundOffTitleTextView;
    private EditText roundOffEdittext;
    private LinearLayout payableAmountLayout;
    private TextView payableAmountTitleTextView;
    private TextView payableAmountValueTextView;
    private LinearLayout miscellaneousLayout;
    private TextInputLayout purchaseOrderNoTextInputLayout;
    private TextInputEditText purchaseOrderNoTextInputEditText;
    private TextInputLayout purchaseOrderDateTextInputLayout;
    private TextInputEditText purchaseOrderDateTextInputEditText;
    private TextInputLayout deliveryDateTextInputLayout;
    private TextInputEditText deliveryDateTextInputEditText;
    private TextInputLayout dueDateTextInputLayout;
    private TextInputEditText dueDateTextInputEditText;
    private AutoCompleteTextView attendeeAutoCompleteTextView;
    private TextInputLayout orderRemarksTextInputLayout;
    private TextInputEditText orderRemarksTextInputEditText;
    private CheckBox smsCheckBox;
    private Spinner spinnerSmsTemplate;
    private TextView showSMSPreview;
    private CheckBox emailCheckBox;
    private Spinner spinnerEmailTemplate;
    private TextView showEmailPreview;
    private TextView saveTextView;
    private RecyclerView recyclerViewProducts, searchDialogProductsRecyclerView;
    private AutoCompleteTextView customerSearchEditText;
    private Dialog dialog, productsDialog;
    private AlertDialog preChargesDialog, errorAlertDialog;
    private ArrayList<ItemList> itemListArrayList = new ArrayList<>();
    private QuotationProductsAdapter quotationProductsAdapter;
    //Int Variable to store position of previously selected item in dialogAddItems
    private String previousQuantityPosOfProduct;
    private String[] discountTypeArr = new String[]{"%", "INR"};
    private ArrayList<ItemData> itemDataArrayList = new ArrayList<ItemData>();
    private OrderItemsAdapter orderItemsAdapter;
    private ArrayList<ChargesList> preChargesListArrayList = new ArrayList<>();
    private QuotationChargesTwoListAdapter quotationChargesTwoListAdapter;
    private QuotationChargesListAdapter quotationChargesListAdapter;
    private ArrayList<ChargesList2> postChargesListArrayList = new ArrayList<>();
    private ArrayList<ChargesList3> chargesList3ArrayList = new ArrayList<>();
    private ArrayList<TaxList> taxListArrayList = new ArrayList<>();
    private double roundOff;
    private QuotationChargesThreeSpinnerCheckAdapter quotationChargesThreeSpinnerCheckAdapter;
    private ArrayList<SmsTemplateDatum> smsTemplateDataArrayList = new ArrayList<>();
    private ArrayList<EmailTemplateArr> emailTemplateArrArrayList = new ArrayList<>();
    private ArrayList<ChatContacts> assigneeNameArrayList = new ArrayList<>();
    private List<String> assigneeNameList = new ArrayList<String>();
    private List<String> assigneeIDList = new ArrayList<String>();
    private DatabaseHandler databaseHandler;
    private ArrayAdapter arrayAdapterAssignee;
    private TextView additionalDiscountTitleTextView;
    private View viewChargesListTwo, viewChargesThree;
    private LinearLayout chargesThreeLayout;
    private RecyclerView chargeListThreeRecyclerView;
    private QuotationThreeChargesDropDownAdapter quotationThreeChargesDropDownAdapter;
    private TextView materialTaxTitleTextView;
    private LinearLayout materialTaxLayout;
    private RelativeLayout materialTaxSpinnerLayout;
    private Spinner materialTaxChargeTypeSpinner;
    private TextView materialTaxChargeValueTextView;
    private View materialTaxChargeView;
    private LinearLayout taxLayout;
    private View taxChargeView;
    private QuotationPreChargesAdapter quotationPreChargesAdapter;
    private String[] paymentTermsTypeArray = new String[]{"Select", "Advance Payment", "Payment on Delivery",
            "Days from the date of acceptance"};
    private List<CustomerSalesType> customerSalesTypeArrayList = new ArrayList<>();
    private List<PaymentModeData> paymentModeDataList = new ArrayList<>();
    private List<LedgerSearchData> ledgerSearchDataArrayList = new ArrayList<>();
    private ArrayAdapter<LedgerSearchData> ledgersArrayAdapter;
    private Date startDate;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private AllDatePickerFragment allDatePickerFragment;
    private List<PaymentOptionAttribute> paymentAttributesList = new ArrayList<>();
    private PaymentModesAdapter paymentModesAdapter;
    private CustomerInfo customerInfo;
    private AlertDialog alertDialogSMSEMail;
    private ImageView imageViewLoader;
    private String userChoosenTask;
    private AlbumStorageDirFactory mAlbumStorageDirFactory;
    private String mCurrentPhotoPath;
    private ImageView productImageView;
    private Bitmap bitmap;
    private boolean isConvertFromQuotation;
    private ImageView imageViewLoaderProductsDialog;

    @Override
    protected void onStop() {
        super.onStop();
        if (errorAlertDialog != null && errorAlertDialog.isShowing()) {
            errorAlertDialog.dismiss();
            errorAlertDialog = null;
        }
    }

    private void updatePreCharges() {
        try {
            for (int i = 0; i < preChargesListArrayList.size(); i++) {
                ChargesList chargesList = preChargesListArrayList.get(i);
                if (taxListArrayList != null && taxListArrayList.size() > 1) {
                    for (int j = 1; j < taxListArrayList.size(); j++) {
                        if (taxListArrayList.get(j).getButapid().equals(chargesList.getButapid())) {
                            chargesList.setSelectedIndex(j);
                        }
                    }
                }
                if (chargesList.getEctid() != null && chargesList.getEctid().equals("2") &&
                        chargesList.getSelectedIndex() != 0) {
                    double inputValue = Constants.ParseDouble(chargesList.getDiscountValue());
                    double taxValue = (Constants.ParseDouble(taxListArrayList.get(chargesList.
                            getSelectedIndex()).getValue()) * inputValue) / 100;
                    double totalValue = inputValue + taxValue;

                    double b = ParseDouble(taxListArrayList.get(chargesList.getSelectedIndex()).getButaprid());
                    switch ((int) b) {
                        case 1:
                            totalValue = ((totalValue * 100) / 100);
                            break;
                        case 2:
                            totalValue = (totalValue);
                            break;
                        case 3:
                            totalValue = Math.ceil(totalValue);
                            break;
                        case 4:
                            totalValue = Math.floor(totalValue);
                            break;
                        default:
                            totalValue = (totalValue * 100) / 100;
                            break;
                    }

                    chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                            ParseDouble(chargesList.getDiscountValue().toString())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                } else if (chargesList.getEctid() != null && chargesList.getEctid().equals("1")
                        && chargesList.getSelectedIndex() != 0) {
                    if (taxListArrayList.get(chargesList.getSelectedIndex()).getButapatid().equals("1")) {
                        double newValue = (ParseDouble(chargesList.getDiscountValue().toString().trim()) *
                                (ParseDouble(materialCostValueTextView.getText().toString()) -
                                        ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", "")) +
                                        ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) / 100);

                        double nextValue = ((newValue * ParseDouble(taxListArrayList.
                                get(chargesList.getSelectedIndex()).getValue())) / 100);

                        double totalValue = newValue + nextValue;

                        double b = ParseDouble(taxListArrayList.get(chargesList.getSelectedIndex()).getButaprid());
                        switch ((int) b) {
                            case 1:
                                totalValue = ((totalValue * 100) / 100);
                                break;
                            case 2:
                                totalValue = (totalValue);
                                break;
                            case 3:
                                totalValue = Math.ceil(totalValue);
                                break;
                            case 4:
                                totalValue = Math.floor(totalValue);
                                break;
                            default:
                                totalValue = (totalValue * 100) / 100;
                                break;
                        }
                        //  preChargesListArrayList.get(position).setDiscountAmountValue("" + totalValue);
                        //  chargeValueTextView.setText("" + "" + Constants.roundTwoDecimals(totalValue));
                        chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                                ParseDouble(chargesList.getDiscountValue().toString())))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                } else {
                    if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                        // chargeValueTextView.setText(chargesList.getDiscountValue().toString().trim());
                        chargesList.setDiscountValue(new BigDecimal(Constants.ParseDouble(
                                chargesList.getDiscountValue().toString()))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.ParseDouble(
                                chargesList.getDiscountValue().toString().trim()))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    } else {
                        double newValue = (((ParseDouble(materialCostValueTextView.getText().toString()) -
                                ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", ""))) +
                                ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())));

                        double nextValue = 0.0;
                        if (newValue <= 0.0) {
                            nextValue = (ParseDouble(chargesList.getDiscountValue().toString()) / 100);
                        } else {
                            nextValue = ((newValue * ParseDouble(chargesList.getDiscountValue().toString())) / 100);
                        }

                        //      double totalValue = newValue + nextValue;
                        chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                                ParseDouble(chargesList.getDiscountValue().toString())))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        //chargeValueTextView.setText("" + "" + Constants.roundTwoDecimals(nextValue));
                        chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(nextValue))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                    }
                }
                preChargesListArrayList.set(i, chargesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (quotationPreChargesAdapter != null) {
            quotationPreChargesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPreChargeDiscountTaxChange(int position, String discountValue, ChargesList chargesList,
                                             String value, int spinnerIndex) {
        //   taxPreChargeTypeSpinnerSelection(position, value, "1");
        preChargesListArrayList.get(position).setSelectedIndex(spinnerIndex);
        if (chargesList.getEctid().equals("1")) {
            if (taxListArrayList.get(spinnerIndex).getButapatid().equals("1")) {
                double newValue = (ParseDouble(discountValue) *
                        (ParseDouble(materialCostValueTextView.getText().toString()) -
                                ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", "")) +
                                ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) / 100);

                double nextValue = ((newValue * ParseDouble(taxListArrayList.get(spinnerIndex).getValue())) / 100);

                double totalValue = newValue + nextValue;

                double b = ParseDouble(taxListArrayList.get(spinnerIndex).getButaprid());
                switch ((int) b) {
                    case 1:
                        totalValue = Math.floor((totalValue * 100) / 100);
                        break;
                    case 2:
                        totalValue = (totalValue);
                        break;
                    case 3:
                        totalValue = Math.ceil(totalValue);
                        break;
                    case 4:
                        totalValue = Math.floor(totalValue);
                        break;
                    default:
                        totalValue = (totalValue * 100) / 100;
                        break;
                }
                preChargesListArrayList.get(position).setDiscountAmountValue("" + new BigDecimal(totalValue)
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            } else {

            }
        }
        taxChargeTypeSpinnerSelection(position, value, "1");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        setContentView(R.layout.layout_create_order);

        if (getIntent() != null) {
            if (getIntent().hasExtra(getString(R.string.order_id))) {
                strOrderId = getIntent().getStringExtra(getString(R.string.order_id));
            }
            if (getIntent().hasExtra("chkid")) {
                strChkId = getIntent().getStringExtra("chkid");
            }
            if (getIntent().hasExtra("cuid")) {
                strCuid = getIntent().getStringExtra("cuid");
            }
            if (getIntent().hasExtra("baid")) {
                strBaid = getIntent().getStringExtra("baid");
            }
            if (getIntent().hasExtra("said")) {
                strSaid = getIntent().getStringExtra("said");
            }
            if (getIntent().hasExtra("cName")) {
                strCName = getIntent().getStringExtra("cName");
            }
            if (getIntent().hasExtra("cEmail")) {
                strCEmail = getIntent().getStringExtra("cEmail");
            }
            if (getIntent().hasExtra("cMobile")) {
                strCMobile = getIntent().getStringExtra("cMobile");
            }
            if (getIntent().hasExtra("codeId")) {
                strCodeId = getIntent().getStringExtra("codeId");
            }
            if (getIntent().hasExtra("products")) {
                itemDataArrayList = getIntent().getParcelableArrayListExtra("products");
            }
            if (getIntent().hasExtra(getString(R.string.convert_from))) {
                isConvertFromQuotation = getIntent().getBooleanExtra(
                        getString(R.string.convert_from), false);
            }
            if (getIntent().hasExtra(getString(R.string.qo_id))) {
                strQoId = getIntent().getStringExtra(getString(R.string.qo_id));
            }
        }

        materialTaxTitleTextView = (TextView) findViewById(R.id.materialTax_title_textView);
        materialTaxLayout = (LinearLayout) findViewById(R.id.material_tax_layout);
        materialTaxSpinnerLayout = (RelativeLayout) findViewById(R.id.materialTax_spinner_layout);
        materialTaxChargeTypeSpinner = (Spinner) findViewById(R.id.materialTax_charge_type_spinner);
        materialTaxChargeValueTextView = (TextView) findViewById(R.id.materialTax_charge_value_textView);
        materialTaxChargeView = (View) findViewById(R.id.materialTax_charge_view);
        taxLayout = (LinearLayout) findViewById(R.id.tax_layout);
        taxChargeView = (View) findViewById(R.id.tax_charge_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        additionalDiscountTitleTextView = (TextView) findViewById(R.id.additional_discount_title_textView);
        chargesThreeLayout = (LinearLayout) findViewById(R.id.charges_three_layout);
        chargeListThreeRecyclerView = (RecyclerView) findViewById(R.id.chargeList_three_recyclerView);
        viewChargesListTwo = (View) findViewById(R.id.view_chargesListTwo);
        viewChargesThree = (View) findViewById(R.id.view_charges_three);
        addItemLayout = (LinearLayout) findViewById(R.id.add_item_layout);
        addItemTextView = (TextView) findViewById(R.id.addItem_textView);
        addProductsButton = (View) findViewById(R.id.addProducts_button);
        scanImageView = (ImageView) findViewById(R.id.scan_imageView);
        container = (FrameLayout) findViewById(R.id.container);
        cardViewInnerCustomer = (CardView) findViewById(R.id.card_view_inner_customer);
        customerNameTitleTextView = (TextView) findViewById(R.id.customerName_title_textView);
        emailTextView = (TextView) findViewById(R.id.email_textView);
        mobileTextView = (TextView) findViewById(R.id.mobile_textView);
        changeAddCustomerTextView = (TextView) findViewById(R.id.changeAddCustomer_textView);
        cardViewInnerQuotOptions = (CardView) findViewById(R.id.card_view_inner_quot_options);
        selectDateTextInputLayout = (TextInputLayout) findViewById(R.id.select_date_textInputLayout);
        selectDateTextInputEditText = (TextInputEditText) findViewById(R.id.select_date_textInputEditText);
        customerSalesTypeTextView = (TextView) findViewById(R.id.customer_sales_type_textView);
        customerSalesTypeSpinner = (Spinner) findViewById(R.id.customer_sales_type_spinner);
        salesLedgerTitleTextView = (TextView) findViewById(R.id.sales_ledger_title_textView);
        salesLedgerValueAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.sales_ledger_value_autocompleteTextView);
        salesLedgerClearImageButton = (ImageButton) findViewById(R.id.sales_ledger_clear_imageButton);
        orderTypeTitleTextView = (TextView) findViewById(R.id.order_type_title_textView);
        orderTypeRadioGroup = (RadioGroup) findViewById(R.id.orderType_radioGroup);
        creditRadioBtn = (RadioButton) findViewById(R.id.credit_radioBtn);
        cashCarryRadioBtn = (RadioButton) findViewById(R.id.cashCarry_radioBtn);
        percentLimitTextInputLayout = (TextInputLayout) findViewById(R.id.percentLimit_textInputLayout);
        percentLimitTextInputEditText = (TextInputEditText) findViewById(R.id.percentLimit_textInputEditText);
        paymentTermsTitleTextView = (TextView) findViewById(R.id.payment_terms_title_textView);
        paymentTermsSpinner = (Spinner) findViewById(R.id.payment_terms_spinner);
        daysTextInputLayout = (TextInputLayout) findViewById(R.id.days_textInputLayout);
        daysTextInputEditText = (TextInputEditText) findViewById(R.id.days_textInputEditText);
        advancePaymentCheckBoxTextView = (CheckBox) findViewById(R.id.advance_payment_checkBox_textView);
        advancePaymentLinearLayout = (LinearLayout) findViewById(R.id.advance_payment_linear_layout);
        paymentModeTitleTextView = (TextView) findViewById(R.id.payment_mode_title_textView);
        paymentModeSpinner = (Spinner) findViewById(R.id.payment_mode_spinner);
        searchPaymentLedgerTitleTextView = (TextView) findViewById(R.id.search_payment_ledger_title_textView);
        searchPaymentLedgerValueAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_payment_ledger_value_autocompleteTextView);
        searchPaymentLedgerClearImageButton = (ImageButton) findViewById(R.id.search_payment_ledger_clear_imageButton);
        advancePaymentAmountTextInputLayout = (TextInputLayout) findViewById(R.id.advancePayment_amount_textInputLayout);
        advancePaymentAmountTextInputEditText = (TextInputEditText) findViewById(R.id.advancePayment_amount_textInputEditText);
        recyclerViewAdvancePayment = (RecyclerView) findViewById(R.id.recyclerView_advance_payment);
        recyclerViewProducts = (RecyclerView) findViewById(R.id.recyclerView_products);
        cardViewInnerPriceLayout = (CardView) findViewById(R.id.card_view_inner_price_layout);
        priceLayout = (LinearLayout) findViewById(R.id.price_layout);
        totalAmountLayout = (LinearLayout) findViewById(R.id.total_amount_layout);
        totalAmountTitleTextView = (TextView) findViewById(R.id.total_amount_title_textView);
        totalAmountValueTextView = (TextView) findViewById(R.id.total_amount_value_textView);
        totalDiscountLayout = (LinearLayout) findViewById(R.id.total_discount_layout);
        totalDiscountTitleTextView = (TextView) findViewById(R.id.total_discount_title_textView);
        totalDiscountValueTextView = (TextView) findViewById(R.id.total_discount_value_textView);
        materialCostLayout = (LinearLayout) findViewById(R.id.material_cost_layout);
        materialCostTitleTextView = (TextView) findViewById(R.id.material_cost_title_textView);
        materialCostValueTextView = (TextView) findViewById(R.id.material_cost_value_textView);
        additionalDiscountLayout = (LinearLayout) findViewById(R.id.additional_discount_layout);
        additionalTextInputLayout = (TextInputLayout) findViewById(R.id.additional_textInputLayout);
        additionalEdittext = (EditText) findViewById(R.id.additional_edittext);
        additionalDiscountTypeSpinner = (Spinner) findViewById(R.id.additional_discount_type_spinner);
        additionalDiscountValueTextView = (TextView) findViewById(R.id.additional_discount_value_textView);
        chargeListRecyclerView = (RecyclerView) findViewById(R.id.chargeList_recyclerView);
        taxTitleTextView = (TextView) findViewById(R.id.tax_title_textView);
        taxSpinnerLayout = (RelativeLayout) findViewById(R.id.tax_spinner_layout);
        taxChargeTypeSpinner = (Spinner) findViewById(R.id.tax_charge_type_spinner);
        taxChargeValueTextView = (TextView) findViewById(R.id.tax_charge_value_textView);
        chargeListTwoRecyclerView = (RecyclerView) findViewById(R.id.chargeList_two_recyclerView);
        chargesThreeTitleTextView = (TextView) findViewById(R.id.charges_three_title_textView);
        chargesThreeSpinnerLayout = (RelativeLayout) findViewById(R.id.charges_three_spinner_layout);
        chargeTypeThreeSpinner = (Spinner) findViewById(R.id.charge_type_three_spinner);
        chargeThreeValueTextView = (TextView) findViewById(R.id.charge_three_value_textView);
        grandTotalLayout = (LinearLayout) findViewById(R.id.grand_total_layout);
        grandTotalTitleTextView = (TextView) findViewById(R.id.grand_total_title_textView);
        grandTotalValueTextView = (TextView) findViewById(R.id.grand_total_value_textView);
        roundOffLayout = (LinearLayout) findViewById(R.id.round_off_layout);
        roundOffTitleTextView = (TextView) findViewById(R.id.round_off_title_textView);
        roundOffEdittext = (EditText) findViewById(R.id.round_off_edittext);
        payableAmountLayout = (LinearLayout) findViewById(R.id.payable_amount_layout);
        payableAmountTitleTextView = (TextView) findViewById(R.id.payable_amount_title_textView);
        payableAmountValueTextView = (TextView) findViewById(R.id.payable_amount_value_textView);
        miscellaneousLayout = (LinearLayout) findViewById(R.id.miscellaneous_layout);
        purchaseOrderNoTextInputLayout = (TextInputLayout) findViewById(R.id.purchase_order_no_textInputLayout);
        purchaseOrderNoTextInputEditText = (TextInputEditText) findViewById(R.id.purchase_order_no_textInputEditText);
        purchaseOrderDateTextInputLayout = (TextInputLayout) findViewById(R.id.purchase_order_date_textInputLayout);
        purchaseOrderDateTextInputEditText = (TextInputEditText) findViewById(R.id.purchase_order_date_textInputEditText);
        deliveryDateTextInputLayout = (TextInputLayout) findViewById(R.id.delivery_date_textInputLayout);
        deliveryDateTextInputEditText = (TextInputEditText) findViewById(R.id.delivery_date_textInputEditText);
        dueDateTextInputLayout = (TextInputLayout) findViewById(R.id.due_date_textInputLayout);
        dueDateTextInputEditText = (TextInputEditText) findViewById(R.id.due_date_textInputEditText);
        attendeeAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.attendee_autoCompleteTextView);
        orderRemarksTextInputLayout = (TextInputLayout) findViewById(R.id.order_remarks_textInputLayout);
        orderRemarksTextInputEditText = (TextInputEditText) findViewById(R.id.order_remarks_textInputEditText);
        smsCheckBox = (CheckBox) findViewById(R.id.sms_checkBox);
        spinnerSmsTemplate = (Spinner) findViewById(R.id.add_search_quotation_item_spinner_sms_template);
        showSMSPreview = (TextView) findViewById(R.id.add_search_quotation_item_show_sms_preview);
        emailCheckBox = (CheckBox) findViewById(R.id.email_checkBox);
        spinnerEmailTemplate = (Spinner) findViewById(R.id.add_search_quotation_item_spinner_email_template);
        showEmailPreview = (TextView) findViewById(R.id.add_search_quotation_item_show_email_preview);
        saveTextView = (TextView) findViewById(R.id.save_textView);
        orderDealerPriceCheckBox = (CheckBox) findViewById(R.id.order_dealer_price_checkBox);
        imageViewLoader = (ImageView) findViewById(R.id.imageView_loader);

        //Hide Edit button for customer in this case
        changeAddCustomerTextView.setEnabled(false);
        changeAddCustomerTextView.setVisibility(View.GONE);

        orderItemsAdapter = new OrderItemsAdapter(EditOrderActivity.this, itemDataArrayList, this,
                strFlagDealerPrice);
        paymentModesAdapter = new PaymentModesAdapter(EditOrderActivity.this, paymentAttributesList, this);

        cardViewInnerQuotOptions.setVisibility(View.GONE);

        hidePricesCardView();

        //Payment Modes RecyclerView
        RecyclerView.LayoutManager paymentModeLayoutManager = new LinearLayoutManager(EditOrderActivity.this);
        recyclerViewAdvancePayment.setLayoutManager(paymentModeLayoutManager);
        recyclerViewAdvancePayment.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdvancePayment.setAdapter(paymentModesAdapter);
        recyclerViewAdvancePayment.setNestedScrollingEnabled(false);

        //Products RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EditOrderActivity.this);
        recyclerViewProducts.setLayoutManager(mLayoutManager);
        recyclerViewProducts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewProducts.setAdapter(orderItemsAdapter);
        recyclerViewProducts.setNestedScrollingEnabled(false);

        chargeListRecyclerView.setFocusable(false);

        //Instance of DatabaseHandler class
        databaseHandler = new DatabaseHandler(EditOrderActivity.this);

        //Add basic attendee
        /*ChatContacts chatContacts = new ChatContacts();
        chatContacts.setName("Select Attendee");
        assigneeNameArrayList.add(chatContacts);*/

        //Get All Assignee
        assigneeNameArrayList.addAll(databaseHandler.getAllAssignee());
        for (ChatContacts cn : assigneeNameArrayList) {
            assigneeNameList.add(cn.getName());
            assigneeIDList.add(String.valueOf(cn.getUid()));
        }

        //Attendee Adapter
        assigneeContactsAdapter();
        attendeeAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendeeAutoCompleteTextView.showDropDown();
            }
        });
        attendeeAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strAssignee = attendeeAutoCompleteTextView.getText().toString();
                    for (int i = 0; i < assigneeNameList.size(); i++) {
                        String temp = assigneeNameList.get(i);
                        if (strAssignee.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    attendeeAutoCompleteTextView.setText("");
                }
            }
        });

        attendeeAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = arrayAdapterAssignee.getItem(position).toString();
                attendeeAutoCompleteTextView.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < assigneeNameList.size(); i++) {
                    if (assigneeNameList.get(i).equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strAssignee = assigneeIDList.get(pos).toString();
                strUid = strAssignee;
            }
        });

        attendeeAutoCompleteTextView.setThreshold(1);

        //Set data of getIntent()
        customerNameTitleTextView.setText(strCName);
        if (strCEmail != null && !strCEmail.isEmpty()) {
            emailTextView.setText(strCEmail);
            emailTextView.setVisibility(View.VISIBLE);
        } else {
            emailTextView.setVisibility(View.GONE);
        }

        if (strCMobile != null && !strCMobile.isEmpty()) {
            mobileTextView.setText(strCMobile);
            mobileTextView.setVisibility(View.VISIBLE);
        } else {
            mobileTextView.setVisibility(View.GONE);
        }

        if (isConvertFromQuotation) {
            toolbar.setTitle("Create Order");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //On Click
        addItemTextView.setOnClickListener(this);
        scanImageView.setOnClickListener(this);
        changeAddCustomerTextView.setOnClickListener(this);
        showEmailPreview.setOnClickListener(this);
        showSMSPreview.setOnClickListener(this);
        saveTextView.setOnClickListener(this);

        quotationChargesListAdapter = new QuotationChargesListAdapter(
                EditOrderActivity.this, preChargesListArrayList,
                taxListArrayList, "0", this);
        quotationChargesTwoListAdapter = new QuotationChargesTwoListAdapter(
                EditOrderActivity.this, postChargesListArrayList, this);
        quotationThreeChargesDropDownAdapter = new QuotationThreeChargesDropDownAdapter(
                EditOrderActivity.this, chargesList3ArrayList, taxListArrayList, "0", this);

        //Charge List RecyclerView
        RecyclerView.LayoutManager mChargeListLayoutManager = new LinearLayoutManager(EditOrderActivity.this);
        chargeListRecyclerView.setLayoutManager(mChargeListLayoutManager);
        chargeListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chargeListRecyclerView.setAdapter(quotationChargesListAdapter);
        chargeListRecyclerView.setNestedScrollingEnabled(false);

        //Charge List Two RecyclerView
        RecyclerView.LayoutManager mChargeListTwoLayoutManager = new LinearLayoutManager(EditOrderActivity.this);
        chargeListTwoRecyclerView.setLayoutManager(mChargeListTwoLayoutManager);
        chargeListTwoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chargeListTwoRecyclerView.setAdapter(quotationChargesTwoListAdapter);
        chargeListTwoRecyclerView.setNestedScrollingEnabled(false);

        //Charge List Three RecyclerView
       /* RecyclerView.LayoutManager mChargeListThreeLayoutManager = new LinearLayoutManager(EditOrderActivity.this);
        chargeListThreeRecyclerView.setLayoutManager(mChargeListThreeLayoutManager);
        chargeListThreeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chargeListThreeRecyclerView.setAdapter(quotationThreeChargesDropDownAdapter);
        chargeListThreeRecyclerView.setNestedScrollingEnabled(false);*/

        //Default Values
        additionalEdittext.setText("0");
        materialCostValueTextView.setText("0");
        totalAmountValueTextView.setText("0");
        grandTotalValueTextView.setText("0");
        payableAmountValueTextView.setText("0");
        //  roundOffValueTextView.setText("0");
        roundOffEdittext.setText("0");
        roundOffEdittext.setSelection(1);

        //TODO - payment terms type spinner
        ArrayAdapter<String> paymentTermsTypeArrayAdapter =
                new ArrayAdapter<String>(EditOrderActivity.this, R.layout.simple_spinner_item, paymentTermsTypeArray);
        paymentTermsTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        paymentTermsSpinner.setAdapter(paymentTermsTypeArrayAdapter);

        //additional discount type spinner
        ArrayAdapter<String> additionalDiscountTypeArrayAdapter =
                new ArrayAdapter<String>(EditOrderActivity.this, R.layout.simple_spinner_item, discountTypeArr);
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
                            additionalDiscountValueTextView.setText("-" +
                                    new BigDecimal(roundTwoDecimals(ParseDouble(s.toString())))
                                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                                additionalDiscountValueTextView.setText("-" + new BigDecimal(
                                        roundTwoDecimals(((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", "")) *
                                                (ParseDouble(s.toString()))) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                additionalDiscountValueTextView.setText("-" + new BigDecimal(
                                        roundTwoDecimals(((ParseDouble(materialCostValueTextView.getText().toString().trim()) *
                                                (ParseDouble(s.toString()))) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());

                            }
                        }
                        updateAllMainValues();
                        setGrandTotalAndPayableAmount();
                    } else {
                        additionalDiscountValueTextView.setText("0");
                        updateAllMainValues();
                        setGrandTotalAndPayableAmount();
                    }
                    updatePreCharges();
                } catch (Exception ex) {
                    additionalEdittext.setText("0");
                    updateAllMainValues();
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
                            additionalDiscountValueTextView.setText("-" + new BigDecimal(
                                    roundTwoDecimals(((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", "")) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100)))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            additionalDiscountValueTextView.setText("-" + new BigDecimal(
                                    roundTwoDecimals(((ParseDouble(materialCostValueTextView.getText().toString().trim()) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100)))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());

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

                        additionalDiscountValueTextView.setText("-" + new BigDecimal(
                                roundTwoDecimals(ParseDouble(additionalEdittext.getText().toString())))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                    updateAllMainValues();
                    setGrandTotalAndPayableAmount();
                    updatePreCharges();
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


        //Tax Spinner
        taxChargeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taxChargeTypeSpinnerSelection(position, taxListArrayList.get(position).getValue(), "0");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Material Tax Spinner
        materialTaxChargeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setMaterialTax(position, taxListArrayList.get(position).getValue());
                updatePreCharges();
                setGrandTotalAndPayableAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        materialTaxChargeValueTextView.setText("0.0");

        //Charge List Type Three Spinner
        chargeTypeThreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (chargesList3ArrayList != null && chargesList3ArrayList.size() > 0) {
                    if (position == 0) {
                        if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                            chargeThreeValueTextView.setText("-" + new BigDecimal(
                                    ((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", "")) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            chargeThreeValueTextView.setText("-" + new BigDecimal(
                                    ((ParseDouble(materialCostValueTextView.getText().toString().trim()) *
                                            (ParseDouble(additionalEdittext.getText().toString()))) / 100))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }
                    } else {
                        chargeThreeValueTextView.setText("-" + new BigDecimal(
                                ParseDouble(additionalEdittext.getText().toString()))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
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
                        //  roundOffValueTextView.setText(s);
                        payableAmountValueTextView.setText(String.valueOf(
                                new BigDecimal(Constants.roundTwoDecimals(ParseDouble(grandTotalValueTextView.getText().toString().trim()) +
                                        ParseDouble(s.toString()))).setScale(2, RoundingMode.HALF_UP).toPlainString()));
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
        percentLimitTextInputEditText.setFilters(new InputFilter[]{new InputFilterForPercentageAndRupees("0", "100")});

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

        //Radio Group Check Listener
        orderTypeRadioGroup.setOnCheckedChangeListener(this);
        creditRadioBtn.setChecked(true);

        //TODO - Payment Terms Spinner Selection Check
        paymentTermsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position > 0 && position == 3) {
                    daysTextInputLayout.setVisibility(View.VISIBLE);
                } else {
                    daysTextInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO - Advance Payment CheckBox Condition
        advancePaymentCheckBoxTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    advancePaymentLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    advancePaymentLinearLayout.setVisibility(View.GONE);
                }
            }
        });
        advancePaymentLinearLayout.setVisibility(View.GONE);

        //TODO - Dealer Price CheckBox Change Listener
        orderDealerPriceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                        for (int i = 0; i < itemDataArrayList.size(); i++) {
                            itemDataArrayList.get(i).setStrDealerFlag("1");
                        }
                    }
                    strFlagDealerPrice = "1";
                    orderItemsAdapter.notifyDataSetChanged();

                    updateAllMainValues();
                    updateDiscount();
                    setMaterialTax(0, taxListArrayList.get(materialTaxChargeTypeSpinner.
                            getSelectedItemPosition()).getValue());
                    if (preChargesListArrayList != null && preChargesListArrayList.size() > 0) {
                        updatePreCharges();
                        updateAllMainValues();
                    }
                } else {
                    if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                        for (int i = 0; i < itemDataArrayList.size(); i++) {
                            itemDataArrayList.get(i).setStrDealerFlag("0");
                        }
                    }
                    strFlagDealerPrice = "0";
                    orderItemsAdapter.notifyDataSetChanged();

                    updateAllMainValues();
                    updateDiscount();
                    setMaterialTax(0, taxListArrayList.get(materialTaxChargeTypeSpinner.
                            getSelectedItemPosition()).getValue());
                    if (preChargesListArrayList != null && preChargesListArrayList.size() > 0) {
                        updatePreCharges();
                        updateAllMainValues();
                    }
                }
            }
        });

        //TODO - Sales Ledger Condition
        enableSalesLedgerAutoComplete();
        salesLedgerValueAutocompleteTextView.setThreshold(1);
        salesLedgerClearImageButton.setOnClickListener(this);

        //TODO - Payment Ledger Condition
        enablePaymentLedgerAutoComplete();
        searchPaymentLedgerValueAutocompleteTextView.setThreshold(1);
        searchPaymentLedgerClearImageButton.setOnClickListener(this);

        //TODO - Payment Mode Spinner Selection
        paymentModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                    paymentAttributesList.clear();
                }
                //Clear paymentledger
                enablePaymentLedgerAutoComplete();
                searchPaymentLedgerValueAutocompleteTextView.getText().clear();
                if (paymentModeDataList.get(position).getPaymentOptionAttribute() != null &&
                        paymentModeDataList.get(position).getPaymentOptionAttribute().size() > 0) {
                    paymentAttributesList.addAll(paymentModeDataList.get(position).getPaymentOptionAttribute());
                }
                paymentModesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO - TextWatcher of sales ledger
        salesLedgerValueAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (salesLedgerValueAutocompleteTextView.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(EditOrderActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        //AGID = 14 is static
                        searchSalesLedger(s.toString().trim(), "14", "sales");
                    } else {
                        Toast.makeText(EditOrderActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchPaymentLedgerValueAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchPaymentLedgerValueAutocompleteTextView.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(EditOrderActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        String agId = "";
                        if (paymentModeDataList != null && paymentModeDataList.size() > 0) {
                            agId = paymentModeDataList.get(paymentModeSpinner.getSelectedItemPosition()).getAgid();
                        }
                        searchSalesLedger(s.toString().trim(), agId, "payment");
                    } else {
                        Toast.makeText(EditOrderActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //TODO - OnItemClickListener of ledgers
        searchPaymentLedgerValueAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strPaymentLedgerId = ledgerSearchDataArrayList.get(position).getId();
                strPaymentLedgerName = ledgerSearchDataArrayList.get(position).getName();
                disablePaymentLedgerAutoComplete();
            }
        });

        salesLedgerValueAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strSalesLedgerId = ledgerSearchDataArrayList.get(position).getId();
                strSalesLedgerName = ledgerSearchDataArrayList.get(position).getName();
                disableSalesLedgerAutoComplete();
            }
        });


        //TODO - Date for cheques
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        selectDateTextInputEditText.setText(formattedDate);
        selectDateTextInputEditText.setOnClickListener(this);
        selectDateTextInputLayout.setOnClickListener(this);
        purchaseOrderDateTextInputEditText.setOnClickListener(this);
        purchaseOrderDateTextInputLayout.setOnClickListener(this);
        dueDateTextInputEditText.setOnClickListener(this);
        dueDateTextInputLayout.setOnClickListener(this);
        deliveryDateTextInputEditText.setOnClickListener(this);
        deliveryDateTextInputLayout.setOnClickListener(this);

        //TODO - Advance Payment will be hidden here forever in case of editing an order not while converting an order
        if (!isConvertFromQuotation) {
            advancePaymentCheckBoxTextView.setVisibility(View.GONE);
            advancePaymentCheckBoxTextView.setEnabled(false);
        }

        //DatePicker
        allDatePickerFragment = new AllDatePickerFragment();
        allDatePickerFragment.setListener(this);

        //Get all charges
        status = NetworkUtil.getConnectivityStatusString(EditOrderActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            showLoader();
            getOrderPrefilledData(EditOrderActivity.this, strOrderId, isConvertFromQuotation);
        }

    }

    private void updateDiscount() {
        try {
            if (additionalEdittext.getText() != null && additionalEdittext.getText().toString().length() != 0) {
                setInputFiltersForAdditionalDiscount();
                if (additionalDiscountTypeSpinner.getSelectedItemPosition() == 1) {
                    additionalDiscountValueTextView.setText("-" + new BigDecimal(
                            roundTwoDecimals(ParseDouble(additionalEdittext.getText().toString())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                } else {
                    if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                        additionalDiscountValueTextView.setText("-" + new BigDecimal(
                                roundTwoDecimals(((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", "")) *
                                        (ParseDouble(additionalEdittext.getText().toString()))) / 100)))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    } else {
                        additionalDiscountValueTextView.setText("-" + new BigDecimal(
                                roundTwoDecimals(((ParseDouble(materialCostValueTextView.getText().toString().trim()) *
                                        (ParseDouble(additionalEdittext.getText().toString()))) / 100)))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                    }
                }

                setGrandTotalAndPayableAmount();
            } else {
                additionalDiscountValueTextView.setText("0");
                setGrandTotalAndPayableAmount();
            }
        } catch (Exception ex) {
            additionalEdittext.setText("0.00");
            additionalDiscountValueTextView.setText("0");
            ex.printStackTrace();
        }
    }

    //    adapter method for Assignee Contacts Adapter
    private void assigneeContactsAdapter() {
        arrayAdapterAssignee = new ArrayAdapter(this, R.layout.simple_spinner_item, assigneeNameList);
        arrayAdapterAssignee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attendeeAutoCompleteTextView.setAdapter(arrayAdapterAssignee);
    } //Material Cost Tax

    private void setMaterialTax(int position, String value) {

        double materialCostValue, additionalTaxValue, totalValue;
        try {
            if (!materialCostValueTextView.getText().toString().trim().contains(",")) {
                materialCostValue = ParseDouble(materialCostValueTextView.getText().toString().trim());
            } else {
                materialCostValue = ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""));
            }

            additionalTaxValue = ParseDouble(additionalDiscountValueTextView.getText().toString().trim());
            //Here we are actually subtracting addition value but it shows with (-) sign so we're adding it into material cost value
            totalValue = roundTwoDecimals((ParseDouble(value) * (materialCostValue + additionalTaxValue)) / 100);
            if (totalValue < 0.00) {
                materialTaxChargeValueTextView.setText("0.00");
            } else {
                materialTaxChargeValueTextView.setText("" + new BigDecimal(totalValue)
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
            setGrandTotalAndPayableAmount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void errorDialog(Activity activity, String errorMessage) {
        final View dialogView;
        try {
            dialogView = View.inflate(activity, R.layout.dialog_edit_order_error, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView)
                    .setCancelable(false);
            errorAlertDialog = builder.create();
            errorAlertDialog.setCanceledOnTouchOutside(false);
            errorAlertDialog.setCancelable(false);

            TextView messageTextView = (TextView) dialogView.findViewById(R.id.textView_error);
            TextView okTextView = (TextView) dialogView.findViewById(R.id.ok_textView);
            messageTextView.setText(errorMessage);

            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (errorAlertDialog != null && errorAlertDialog.isShowing()) {
                        errorAlertDialog.dismiss();
                    }
                    finish();
                }
            });
            errorAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            errorAlertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_SCAN:
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
                    scanBarcode(1100);
                } else {
                    String msg = "Camera's permission isn't allowed.";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                boolean isPermission = true;
                int pos = 0;
                for (int permission : grantResults) {
                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        //DO NOTHING
                        Log.i("TAG", "Permission is granted");
                    } else {
                        isStartActivity = false;
                        pos = permission;
                    }
                }
                if (isPermission) {
                    Intent pictureIntent = dispatchTakePictureIntent();
                    startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    String msg = "Camera's permission isn't allowed";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pictureIntent, REQUEST_IMAGE_GALLERY_PIC);
                } else {
                    String msg = "Storage's permission isn't allowed";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private Intent dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f;
        try {
            f = mAlbumStorageDirFactory.setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(EditOrderActivity.this,
                    this.getApplicationContext().getPackageName() +
                            ".fileprovider", f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            return takePictureIntent;
        } catch (Exception e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }

        return null;
    }


    private void setInputFiltersForAdditionalDiscount() {
        try {
            if (additionalDiscountTypeSpinner.getSelectedItemPosition() == 0) {
                //Set Range of typing in additional EditText
                additionalEdittext.setFilters(new InputFilter[]{new InputFilterForPercentageAndRupees("0", "100")});
            } else {
                //Set Range of typing in additional EditText for Rs
                additionalEdittext.setFilters(new InputFilter[]
                        {new InputFilterForPercentageAndRupees("0", materialCostValueTextView.getText().toString().trim()
                        )});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDate(int position) {
        allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.cheque_date));
    }

    @TargetApi(23)
    private boolean verifyPermission() {
        if (checkAllPermission()) {
            if (permissionsNeeded.size() > 0 && !isFirstTime) {
                for (String permission : permissionsNeeded) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        displayPermissionDialog("Would like to grant access to take picture from camera",
                                PERMISSION_STORAGE, REQUEST_IMAGE_CAPTURE);
                        break;
                    }
                }
            } else {
                isFirstTime = false;
                requestPermissions(PERMISSION_STORAGE, REQUEST_IMAGE_CAPTURE);
            }
            return true;
        }
        return false;
    }

    @TargetApi(23)
    private boolean verifyPermissionOfCameraForScan() {
        if (checkAllPermission()) {
            if (permissionsNeeded.size() > 0 && !isFirstTime) {
                for (String permission : permissionsNeeded) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        displayPermissionDialog("Would like to grant access to take picture from camera",
                                PERMISSION_STORAGE, REQUEST_IMAGE_SCAN);
                        break;
                    }
                }
            } else {
                isFirstTime = false;
                requestPermissions(PERMISSION_STORAGE, REQUEST_IMAGE_SCAN);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.scan_imageView:
                    scanImageView.setEnabled(false);
                    //Enable again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scanImageView.setEnabled(true);
                        }
                    }, 3000);

                    int resultCode = 0;
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (verifyPermissionOfCameraForScan()) return;
                    }
                    resultCode = REQUEST_IMAGE_SCAN;
                    scanBarcode(resultCode);
                    break;
                case R.id.changeAddCustomer_textView:
                    Intent intent = new Intent(EditOrderActivity.this, OrderQuotationsSelectCustomerActivity.class);
                    if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                        intent.putParcelableArrayListExtra("products", itemDataArrayList);
                    }
                    startActivity(intent);
                    break;
                case R.id.addItem_textView:
                    addItemTextView.setEnabled(false);
                    openProductSearchDialog();
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addItemTextView.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.save_textView:
                    //Disable Button
                    saveTextView.setEnabled(false);
                    if (getPostData()) {
                        status = NetworkUtil.getConnectivityStatusString(this);
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            showLoader();
                            EditOrderAPI();
                        } else {
                            Toast.makeText(EditOrderActivity.this,
                                    getString(R.string.no_internet_try_later),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    //Enable Button
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveTextView.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.add_search_quotation_item_show_sms_preview:
                    showSMSPreview.setEnabled(false);
                    flag = "SMS";
                    getPrefillDataForPreview();
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showSMSPreview.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.add_search_quotation_item_show_email_preview:
                    showEmailPreview.setEnabled(false);
                    flag = "Email";
                    getPrefillDataForPreview();
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showEmailPreview.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.sales_ledger_clear_imageButton:
                    salesLedgerClearImageButton.setEnabled(false);
                    enableSalesLedgerAutoComplete();
                    salesLedgerValueAutocompleteTextView.getText().clear();
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            salesLedgerClearImageButton.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.search_payment_ledger_clear_imageButton:
                    searchPaymentLedgerClearImageButton.setEnabled(false);
                    enablePaymentLedgerAutoComplete();
                    searchPaymentLedgerValueAutocompleteTextView.getText().clear();
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchPaymentLedgerClearImageButton.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.purchase_order_date_textInputLayout:
                    purchaseOrderDateTextInputLayout.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.purchase_order_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            purchaseOrderDateTextInputLayout.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.purchase_order_date_textInputEditText:
                    purchaseOrderDateTextInputEditText.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.purchase_order_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            purchaseOrderDateTextInputEditText.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.delivery_date_textInputLayout:
                    deliveryDateTextInputLayout.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.delivery_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deliveryDateTextInputLayout.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.delivery_date_textInputEditText:
                    deliveryDateTextInputEditText.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.delivery_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deliveryDateTextInputEditText.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.due_date_textInputLayout:
                    dueDateTextInputLayout.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.due_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dueDateTextInputLayout.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.due_date_textInputEditText:
                    dueDateTextInputEditText.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.due_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dueDateTextInputEditText.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.select_date_textInputLayout:
                    selectDateTextInputLayout.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.select_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            selectDateTextInputLayout.setEnabled(true);
                        }
                    }, 4000);
                    break;
                case R.id.select_date_textInputEditText:
                    selectDateTextInputEditText.setEnabled(false);
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.select_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            selectDateTextInputEditText.setEnabled(true);
                        }
                    }, 4000);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPrefillDataForPreview() {
        //Disable Button
        saveTextView.setEnabled(false);
        showSMSPreview.setEnabled(false);
        showEmailPreview.setEnabled(false);

        try {
            if (getPostData()) {
                status = NetworkUtil.getConnectivityStatusString(this);
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                   /* PreviewOrderAsyncTask previewOrderAsyncTask = new PreviewOrderAsyncTask(this);
                    previewOrderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
                    previewSMSEmail();
                } else {
                    Toast.makeText(this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    //Get data from fields and post
    public boolean getPostData() {
        if (strAssignee != null && isNumeric(strAssignee)) {
            strUid = String.valueOf(strAssignee);
        } else if (attendeeAutoCompleteTextView.getText().length() == 0) {
            strUid = "0";
        } else if (strUid != null && isNumeric(strUid)) {
        } else {
            strUid = "0";
        }

        if (!creditRadioBtn.isChecked() && !cashCarryRadioBtn.isChecked()) {
            Toast.makeText(EditOrderActivity.this, "Please select Order type.", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Checking List of Products has items or not because its mandatory
        if (itemDataArrayList == null || itemDataArrayList.size() == 0) {
            Toast.makeText(this, "Please add a product first.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (deliveryDateTextInputEditText.getText().toString().length() == 0) {
            Toast.makeText(EditOrderActivity.this, "Please select delivery date.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (orderDealerPriceCheckBox.isChecked()) {
            strDealerPrice = "1";
        } else {
            strDealerPrice = "0";
        }

        strAdditionalDiscount = additionalEdittext.getText().toString().trim();
        if (smsCheckBox.isChecked()) {
            strSms = "1";
            if (smsTemplateDataArrayList != null && smsTemplateDataArrayList.size() > 0) {
                strSmstId = smsTemplateDataArrayList.get(spinnerSmsTemplate.getSelectedItemPosition()).getSmstid();
            } else {
                strSmstId = "";
            }
        } else {
            strSms = "0";
            strSmstId = "";
        }

        if (emailCheckBox.isChecked()) {
            strMail = "1";
            if (emailTemplateArrArrayList != null && emailTemplateArrArrayList.size() > 0) {
                strEtId = emailTemplateArrArrayList.get(spinnerEmailTemplate.getSelectedItemPosition()).getEtid();
            } else {
                strEtId = "";
            }
        } else {
            strMail = "0";
            strEtId = "";
        }

        //They all were on web, but no need of it in app
        strCC = "";
        strRemarks = "";
        strTaxes = "";

        strRound = roundOffEdittext.getText().toString().trim();

        return true;
    }

    public void scanBarcode(int resultCode) {
        Intent intentScan = new Intent(EditOrderActivity.this, ScannerActivity.class);
        startActivityForResult(intentScan, resultCode);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1100) {
            getScanCode(data.getStringExtra("scanResult"));
        } else if (resultCode == Activity.RESULT_OK) {
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
        try {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void crop(final String photoPath) {
        getBitmap(photoPath);
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
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
                //TODO - Post image in MultiPart Format
                status = NetworkUtil.getConnectivityStatusString(EditOrderActivity.this);
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    showLoaderInDialog(productsDialog);
                    showLoader();
                    updateItemsImage(bitmapImg, productsDialog);
                } else {
                    Toast.makeText(EditOrderActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void updateItemsImage(Bitmap bmp, Dialog dialog) {
        try {
            task = getString(R.string.update_items_image);
            String userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            String accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getDomain(this, AppUtils.DOMAIN);
            MultipartBody.Part propertyImagePart = null;
            Call call = null;
            final ApiInterface apiService = ApiClient.getClientImages().create(ApiInterface.class);
            if (mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
                File sourceFile = new File(decodeFile(mCurrentPhotoPath, 400, 400));
                RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), sourceFile);
                propertyImagePart = MultipartBody.Part.createFormData("file[]", mCurrentPhotoPath, propertyImage);
                call = apiService.updateProductsImageWithMultipart(version, key, task, userId, accessToken, strIsVid, propertyImagePart);
            }
            Log.v("Request", String.valueOf(call));
            Log.v("url", String.valueOf(call.request().url()));

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, retrofit2.Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
                        if (EditOrderActivity.this != null) {
                            Toast.makeText(EditOrderActivity.this,
                                    apiResponse.getMessage() + "", Toast.LENGTH_SHORT).show();

                            if (apiResponse.getData().getPath() != null &&
                                    !apiResponse.getData().getPath().isEmpty() &&
                                    !apiResponse.getData().getPath().equals("null")) {
                                strProductsImageUrl = apiResponse.getData().getPath();
                            }

                            if (productImageView != null && productImageView.getVisibility() == View.VISIBLE) {
                                if (bmp != null && productsDialog != null && productsDialog.isShowing()) {
                                    productImageView.setImageBitmap(bmp);
                                    //bitmap = bmp;
                                }
                            }
                        }
                    } //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        if (EditOrderActivity.this != null) {
                            //Logout
                            Constants.logoutWrongCredentials(EditOrderActivity.this,
                                    apiResponse.getMessage());
                        }
                    } else {
                        if (EditOrderActivity.this != null) {
                            Toast.makeText(EditOrderActivity.this,
                                    apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    hideLoaderInDialog(dialog);
                    hideLoader();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (EditOrderActivity.this != null) {
                        hideLoaderInDialog(dialog);
                        hideLoader();
                        Toast.makeText(EditOrderActivity.this,
                                getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().
                                create().toJson(call.request())));
                        t.printStackTrace();
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            hideLoaderInDialog(dialog);
            hideLoader();
        }
    }

    public void getScanCode(String str) {
        searchedProductDetails(str, "scan");
    }

    //Opening Dialog to search Products
    public void openProductSearchDialog() {
        try {
            dialog = new Dialog(EditOrderActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_quotation_customer_search);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            customerSearchEditText = (AutoCompleteTextView) dialog.findViewById(R.id.customer_search_autoCompleteTextView);
            searchDialogProductsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

            //Customers RecyclerView
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EditOrderActivity.this);
            quotationProductsAdapter = new QuotationProductsAdapter(EditOrderActivity.this,
                    itemListArrayList, this);

            searchDialogProductsRecyclerView.setLayoutManager(mLayoutManager);
            searchDialogProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            searchDialogProductsRecyclerView.setAdapter(quotationProductsAdapter);
            searchDialogProductsRecyclerView.setNestedScrollingEnabled(false);

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
                        status = NetworkUtil.getConnectivityStatusString(EditOrderActivity.this);
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            if (s.length() > 0) {
                                searchProduct(s.toString().trim());
                            }
                        } else {
                            Toast.makeText(EditOrderActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Click even of selecting product after searching into dialog
    @Override
    public void onProductClick(int position) {
        ItemList selected = itemListArrayList.get(position);
        //   customerSearchEditText.setText(selected.getName().toString().trim());
        status = NetworkUtil.getConnectivityStatusString(EditOrderActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            searchedProductDetails(selected.getId(), "API");
        } else {
            Toast.makeText(EditOrderActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    //Search Product After getting input from dialog
    public void searchProduct(String str) {
        try {
            task = getString(R.string.quotation_search_item);
            if (AppPreferences.getIsLogin(EditOrderActivity.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(EditOrderActivity.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(EditOrderActivity.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(EditOrderActivity.this, AppUtils.DOMAIN);
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
                            Constants.logoutWrongCredentials(EditOrderActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(EditOrderActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (EditOrderActivity.this != null) {
                        Toast.makeText(EditOrderActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Refreshing Adapter of products after getting from  API
    private void refreshProductsAutoCompleteTextView() {
        // refresh the custom Adapter
        quotationProductsAdapter.notifyDataSetChanged();
    }

    //Get Details of clicked items after selecting from a dialog
    public void searchedProductDetails(String id, final String sourceType) {
        try {
            if (sourceType != null && sourceType.equals("scan")) {
                task = getString(R.string.products_details_scan_qr_code);
            } else {
                task = getString(R.string.quotation_product_details);
            }

            if (AppPreferences.getIsLogin(EditOrderActivity.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(EditOrderActivity.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(EditOrderActivity.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(EditOrderActivity.this, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = null;
            if (sourceType != null && sourceType.equals("scan")) {
                call = apiService.getScannedProductsDetails(version, key, task, userId, accessToken, id,
                        strCuid);
            } else {
                call = apiService.getSearchedProductsDetails(version, key, task, userId, accessToken,
                        id, strCuid);
            }
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
                            if (productsDialog == null || !productsDialog.isShowing()) {
                                dialogAddItems(EditOrderActivity.this, sourceType, itemData, 0);
                            }
                        }
                        //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(EditOrderActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(EditOrderActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (EditOrderActivity.this != null) {
                        Toast.makeText(EditOrderActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String removeComma(String str) {
        if (!str.isEmpty()) {
            if (str.contains(",")) {
                str.replace(",", "");
            }
        }
        return str;
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

    private void dialogSelectImage() {
        try {
            final String[] items;
            items = new String[]{getString(R.string.take_photo), getString(R.string.choose_from_gallery)
                    , getString(R.string.cancel)};

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void dialogAddItems(Activity activity, final String sourceType, final ItemData itemData,
                                final int positionToEdit) {
        productsDialog = new Dialog(EditOrderActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
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
            productImageView = (ImageView) productsDialog.findViewById(R.id.product_imageView);
            final TextView taxTextView = (TextView) productsDialog.findViewById(R.id.tax_textView);
            final LinearLayout taxLinearLayout = (LinearLayout) productsDialog.findViewById(R.id.tax_linearLayout);
            final TextView stockValueEdittext = (TextView) productsDialog.findViewById(R.id.stock_value_edittext);
            final TextView editAddTextView = (TextView) productsDialog.findViewById(R.id.edit_add_textView);
            imageViewLoaderProductsDialog = (ImageView) productsDialog.findViewById(R.id.imageView_loader);

            //TODO Storing IsVid into a string var which will come in use for posting an image
            strIsVid = itemData.getIsvid();
            strProductsImageUrl = null;

            //Edit/Add image
            editAddTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSelectImage();
                }
            });

            if (itemData.getImage() != null && !itemData.getImage().isEmpty()) {
                productImageView.setVisibility(View.VISIBLE);
                Glide.with(EditOrderActivity.this)
                        .load(itemData.getImage())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(productImageView);
            } else {
                productImageView.setVisibility(View.GONE);
            }

            //Name
            productNameEdittext.setText(itemData.getName().trim());
            if (itemData.getStockValue() != null && !itemData.getStockValue().isEmpty()) {
                stockValueEdittext.setText("Stock: " + itemData.getStockValue().toString());
                stockValueEdittext.setVisibility(View.VISIBLE);
            } else {
                stockValueEdittext.setVisibility(View.GONE);
            }

            //TODO - Input Type for quantity
            if (itemData.getMeaid() != null &&
                    !itemData.getMeaid().isEmpty()) {
                if (itemData.getMeaid().toString().trim().equals("0")) {
                    quantityEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                    quantityEdittext.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                    quantityEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(itemData.getQuantity())))
                            .setScale(0, RoundingMode.HALF_UP).toPlainString());
                } else {
                    quantityEdittext.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    quantityEdittext.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                    quantityEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(itemData.getQuantity())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                }
            } else {
                quantityEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(itemData.getQuantity())))
                        .setScale(0, RoundingMode.HALF_UP).toPlainString());
                quantityEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                quantityEdittext.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            }
            quantityEdittext.setSelection(removeComma(quantityEdittext.getText().toString()).trim().length());

            if (orderDealerPriceCheckBox.isChecked() || (itemData.getStrDealerFlag() != null &&
                    itemData.getStrDealerFlag().equals("1"))) {
                priceEdittext.setText("" + new BigDecimal(
                        Constants.roundTwoDecimals(Constants.ParseDouble(itemData.getDealerPrice())))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            } else {
                //TODO - Updated On 10th May
                if (!sourceType.equals("edit") && itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                        !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                    priceEdittext.setText(new BigDecimal(((Constants.ParseDouble(itemData.getPrice())
                            * Constants.ParseDouble(itemData.getTaxValue())) / 100) +
                            Constants.ParseDouble(itemData.getPrice()))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                } else {
                    priceEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(itemData.getPrice())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                }
            }
            priceEdittext.setSelection(removeComma(priceEdittext.getText().toString()).trim().length());

            //TODO Commented on 4th July
            /*if (strExtensiveTaxFlag != null && strExtensiveTaxFlag.equals("1")) {
                taxLinearLayout.setVisibility(View.VISIBLE);
                taxTextView.setVisibility(View.VISIBLE);
                taxedAmountEdittext.setVisibility(View.VISIBLE);
            } else {
                taxedAmountEdittext.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);
                taxTextView.setVisibility(View.GONE);
            }*/

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
                        if (ParseDouble(removeComma(discountEdittext.getText().toString()).trim()) > 100) {
                            discountEdittext.setText("" + new BigDecimal(Constants.roundTwoDecimals(
                                    Constants.ParseDouble(removeComma(discountEdittext.getText().toString()).substring(0, 2))))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            discountEdittext.setSelection(removeComma(discountEdittext.getText().toString()).length());
                        }
                        //TODO - Updated On 10th May
                        if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                    (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                    +
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)
                                    -
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());

                        } else {

                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                    (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                    +
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }

                        discountedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                (ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                    } else {
                        //Removing Filter
                        discountEdittext.setFilters(new InputFilter[]{});

                        //TODO - Updated On 10th May
                        if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                    ParseDouble(removeComma(discountEdittext.getText().toString())))))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                    ParseDouble(removeComma(discountEdittext.getText().toString())))
                                    +
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                            ParseDouble(removeComma(discountEdittext.getText().toString())))) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)).setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }

                        if (discountEdittext.getText().toString().trim().length() != 0) {
                            if (Constants.ParseDouble(discountEdittext.getText().toString().trim()) >
                                    Constants.ParseDouble(amountEditText.getText().toString().trim())) {
                                discountEdittext.setText("0.00");
                            } else {
                                discountEdittext.setText(new BigDecimal(discountEdittext.getText().toString())
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                            discountEdittext.setSelection(discountEdittext.getText().toString().trim().length());
                        }
                        discountedAmountEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(
                                Constants.ParseDouble(removeComma(discountEdittext.getText().toString()))))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                    taxedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                            ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                            ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                            (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                    .getValue()))) / 100))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());

                    if (Constants.roundTwoDecimals(Constants.ParseDouble(discountEdittext.getText().toString().trim())) >
                            Constants.roundTwoDecimals(Constants.ParseDouble(amountEditText.getText().toString().trim()))) {
                        discountEdittext.setText("0");
                    }
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
                    if (removeComma(discountEdittext.getText().toString()).trim() != null &&
                            removeComma(discountEdittext.getText().toString()).trim().length() != 0 &&
                            !removeComma(discountEdittext.getText().toString()).trim().equals("0")) {
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(removeComma(discountEdittext.getText().toString())))))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(removeComma(discountEdittext.getText().toString())))
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                                ParseDouble(removeComma(discountEdittext.getText().toString())))) *
                                                ParseDouble(selectedTax.getValue())) / 100))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                        } else {
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(selectedTax.getValue())) / 100)
                                        -
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(selectedTax.getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(selectedTax.getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                        }
                    } else {
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(removeComma(discountEdittext.getText().toString())))))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(removeComma(discountEdittext.getText().toString())))
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                                ParseDouble(removeComma(discountEdittext.getText().toString())))) *
                                                ParseDouble(selectedTax.getValue())) / 100))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }

                        } else {
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(selectedTax.getValue())) / 100)
                                        -
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(selectedTax.getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(selectedTax.getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                        }
                    }
                    taxedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                            ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                            ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                            (ParseDouble(selectedTax.getValue()))) / 100))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
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
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(s.toString()))))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(s.toString()))
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                                ParseDouble(s.toString()))) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }

                            discountedAmountEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(
                                    Constants.ParseDouble(removeComma(discountEdittext.getText().toString()))))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        (100.00 - ParseDouble(s.toString()))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(s.toString()))) / 100) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100)
                                        -
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(s.toString()))) / 100) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        (100.00 - ParseDouble(s.toString()))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                (100.00 - ParseDouble(s.toString()))) / 100) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                            discountedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                    (ParseDouble(s.toString()))) / 100))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }
                        taxedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                                (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                        .getValue()))) / 100))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                        if (Constants.roundTwoDecimals(Constants.ParseDouble(discountEdittext.getText().toString().trim())) >
                                Constants.roundTwoDecimals(Constants.ParseDouble(amountEditText.getText().toString().trim()))) {
                            discountEdittext.setText("0");
                        }

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

                     /*   amountEditText.setText("" + new BigDecimal(roundTwoDecimals(ParseDouble(s.toString()) *
                                ParseDouble(removeComma(quantityEdittext.getText().toString()))))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());*/
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(s.toString()) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(removeComma(discountEdittext.getText().toString())))))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(s.toString()) *
                                        ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                        ParseDouble(removeComma(discountEdittext.getText().toString())))
                                        +
                                        ((((ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                ParseDouble(s.toString())) -
                                                ParseDouble(removeComma(discountEdittext.getText().toString())))) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                            discountedAmountEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(
                                    Constants.ParseDouble(removeComma(discountEdittext.getText().toString()))))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            //TODO - Updated On 10th May
                            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        ParseDouble(s.toString()) *
                                        (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                ParseDouble(s.toString()) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100)
                                        -
                                        ((((ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                ParseDouble(s.toString()) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                        ParseDouble(s.toString()) *
                                        (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                                ParseDouble(s.toString()) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                            discountedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((ParseDouble(removeComma(quantityEdittext.getText().toString())) *
                                    ParseDouble(s.toString()) *
                                    (ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());

                        }
                        taxedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((((ParseDouble(s.toString()) *
                                ParseDouble(removeComma(quantityEdittext.getText().toString()))) -
                                ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                                (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                        .getValue()))) / 100))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        if (Constants.roundTwoDecimals(Constants.ParseDouble(discountEdittext.getText().toString().trim())) >
                                Constants.roundTwoDecimals(Constants.ParseDouble(amountEditText.getText().toString().trim()))) {
                            discountEdittext.setText("0");
                        }
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
                 /*   amountEditText.setText("" + roundTwoDecimals(ParseDouble(s.toString()) *
                            ParseDouble(removeComma(priceEdittext.getText().toString()))));*/
                    if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                        //TODO - Updated On 10th May
                        if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(s.toString())) -
                                    ParseDouble(removeComma(discountEdittext.getText().toString())))))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(s.toString())) -
                                    ParseDouble(removeComma(discountEdittext.getText().toString())))
                                    +
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(s.toString())) -
                                            ParseDouble(removeComma(discountEdittext.getText().toString())))) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }

                        discountedAmountEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(
                                Constants.ParseDouble(removeComma(discountEdittext.getText().toString()))))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    } else {
                        //TODO - Updated On 10th May
                        if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(s.toString()) *
                                    (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                    +
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(s.toString()) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)
                                    -
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(s.toString()) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)))

                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            subtotalEditText.setText("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                    ParseDouble(s.toString()) *
                                    (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                    +
                                    ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                            ParseDouble(s.toString()) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }
                        discountedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                ParseDouble(s.toString()) *
                                (ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                    taxedAmountEdittext.setText("" + new BigDecimal(roundTwoDecimals((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                            ParseDouble(s.toString())) -
                            ParseDouble(discountedAmountEdittext.getText().toString().trim())) *
                            (ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                    .getValue()))) / 100))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                }
                //  }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //discounted amount text watcher
            discountedAmountEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    amountEditText.setText("" + new BigDecimal(roundTwoDecimals(ParseDouble(quantityEdittext.getText().toString()) *
                            ParseDouble(removeComma(priceEdittext.getText().toString())))
                            - ParseDouble(removeComma(s.toString())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                }
            });


            if (isNumeric(removeComma(priceEdittext.getText().toString())) && isNumeric(removeComma(quantityEdittext.getText().toString()))) {
             /*   amountEditText.setText("" + new BigDecimal(roundTwoDecimals(ParseDouble(removeComma(priceEdittext.getText().toString())) *
                        ParseDouble(removeComma(quantityEdittext.getText().toString()))))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());*/
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
                    quantityTypeArr[i] = itemData.getUnitsData().get(i).getName().toString().trim();
                }
            }

            //Quantity type spinner
            ArrayAdapter<String> quantityTypeArrayAdapter =
                    new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, quantityTypeArr);
            quantityTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            quantityTypeSpinner.setAdapter(quantityTypeArrayAdapter);

            //TODO Added on 28th May
            discountEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                            if (Constants.roundTwoDecimals(Constants.ParseDouble(discountEdittext.getText().toString().trim())) >
                                    (Constants.roundTwoDecimals(Constants.ParseDouble(priceEdittext.getText().toString().trim())) *
                                            Constants.roundTwoDecimals(Constants.ParseDouble(quantityEdittext.getText().toString().trim())))) {
                                discountEdittext.setText("0");
                            }
                        }
                    }
                }
            });


            //Updating value of Quantity if there are multiple types of Quantities so we will match it with MeaId
            if (itemData.getMeaid() != null && !itemData.getMeaid().toString().trim().isEmpty()
                    && !itemData.getMeaid().toString().trim().equals("0")) {
                quantityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (removeComma(quantityEdittext.getText().toString()).trim() != null &&
                                !removeComma(quantityEdittext.getText().toString()).trim().isEmpty() &&
                                Constants.ParseDouble(removeComma(quantityEdittext.getText().toString()).trim()) > 0.0
                                && isNumeric(removeComma(quantityEdittext.getText().toString()).trim())) {
                            if (itemData.getUnitsData().get(position).getConversionrate() != null &&
                                    !itemData.getUnitsData().get(position).getConversionrate().isEmpty()) {
                                if (previousQuantityPosOfProduct != null) {
                                    quantityEdittext.setText("" + new BigDecimal(
                                            ((ParseDouble(removeComma(quantityEdittext.getText().toString()).trim())) /
                                                    (ParseDouble(itemData.getUnitsData().get(position).getConversionrate())))
                                                    *
                                                    (ParseDouble(itemData.getUnitsData().get(
                                                            Integer.valueOf(previousQuantityPosOfProduct)).getConversionrate())))
                                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                }
                            }
                            previousQuantityPosOfProduct = String.valueOf(position);
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
                    textViewAdd.setEnabled(false);
                    if (quantityEdittext.getText().length() == 0) {
                        Toast.makeText(EditOrderActivity.this, "Please enter quantity of product first", Toast.LENGTH_SHORT).show();
                    } else if (quantityEdittext.getText().length() == 0 ||
                            Constants.roundTwoDecimals(Constants.ParseDouble(quantityEdittext.getText().toString().trim())) <= 0.00) {
                        Toast.makeText(EditOrderActivity.this, "Please enter valid quantity", Toast.LENGTH_SHORT).show();
                    } else if (priceEdittext.getText().length() == 0 ||
                            Constants.roundTwoDecimals(Constants.ParseDouble(priceEdittext.getText().toString().trim())) <= 0.00) {
                        Toast.makeText(EditOrderActivity.this, "Please enter valid price", Toast.LENGTH_SHORT).show();
                    } else if (subtotalEditText.getText().toString().contains("-") ||
                            Constants.ParseDouble(subtotalEditText.getText().toString()) <= 0) {
                        Toast.makeText(EditOrderActivity.this,
                                "Amount or Subtotal amount can't be negative or zero",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ItemData data = new ItemData();
                        data.setName(productNameEdittext.getText().toString().trim());


                        data.setTax(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition()).getName().toString().trim());
                        data.setDiscount(removeComma(discountEdittext.getText().toString()).trim());
                        if (discountTypeSpinner.getSelectedItemPosition() == 0) {
                            data.setDiscountType("1");
                        } else {
                            data.setDiscountType("2");
                        }

                        //TODO - Flag Checks
                        if (orderDealerPriceCheckBox.isChecked()) {
                            if (discountTypeSpinner.getSelectedItemPosition() == 1) {
                                data.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma
                                        (priceEdittext.getText().toString())) *
                                        ParseDouble(quantityEdittext.getText().toString())) -
                                        ParseDouble(removeComma(discountEdittext.getText().toString())))
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(quantityEdittext.getText().toString())) -
                                                ParseDouble(removeComma(discountEdittext.getText().toString())))) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            } else {
                                data.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                        ParseDouble(quantityEdittext.getText().toString()) *
                                        (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                        +
                                        ((((ParseDouble(removeComma(priceEdittext.getText().toString())) *
                                                ParseDouble(quantityEdittext.getText().toString()) *
                                                (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                                ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                        .getValue())) / 100)))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }

                            data.setStrDealerFlag("1");
                            data.setSubtotalAmount("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                    ParseDouble(quantityEdittext.getText().toString()) *
                                    (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                    +
                                    ((((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                            ParseDouble(quantityEdittext.getText().toString()) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            data.setStrDealerFlag("0");
                            data.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(itemData.getDealerPrice().toString())) *
                                    ParseDouble(quantityEdittext.getText().toString()) *
                                    (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100)
                                    +
                                    ((((ParseDouble(removeComma(itemData.getDealerPrice().toString())) *
                                            ParseDouble(quantityEdittext.getText().toString()) *
                                            (100.00 - ParseDouble(removeComma(discountEdittext.getText().toString())))) / 100) *
                                            ParseDouble(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition())
                                                    .getValue())) / 100)))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            data.setSubtotalAmount(subtotalEditText.getText().toString().trim());
                        }
                        if (Constants.ParseDouble(data.getDealerPriceSubtotal()) <= 0.0) {
                            data.setDealerPriceSubtotal("0.0");
                        }
                        if (strFlagDealerPrice != null && strFlagDealerPrice.equals("1")) {
                            data.setDealerPrice(removeComma(priceEdittext.getText().toString()).trim());
                            data.setPrice(itemData.getPrice() + "");
                        } else {
                            data.setPrice(removeComma(priceEdittext.getText().toString()).trim());
                            data.setDealerPrice(itemData.getDealerPrice() + "");
                        }

                        //TODO Updated On 8th June
                        if (strProductsImageUrl != null && !strProductsImageUrl.isEmpty()) {
                            data.setImage(strProductsImageUrl);
                        } else {
                            data.setImage(itemData.getImage());
                        }
                        data.setAmount(amountEditText.getText().toString().trim());
                        data.setQuantity(removeComma(quantityEdittext.getText().toString()).trim());
                        data.setQuantityType(quantityTypeArr[quantityTypeSpinner.getSelectedItemPosition()].toString().trim());
                        data.setBoxQty(itemData.getBoxQty());
                        data.setSelectedTaxValue(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition()).getValue().toString().trim());
                        data.setSelectedTaxName(taxArrayList.get(taxTypeSpinner.getSelectedItemPosition()).getName().toString().trim());
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
                        data.setType(itemData.getType());
                        data.setChkoiid(itemData.getChkoiid());
                        data.setPriceIncludeTaxShow(itemData.isPriceIncludeTaxShow());
                        data.setStockValue(itemData.getStockValue());
                        //data.setDealerPrice(itemData.getDealerPrice());
                        if (sourceType.equals("edit")) {
                            itemDataArrayList.set(positionToEdit, data);
                        } else {
                            itemDataArrayList.add(data);
                        }
                        orderItemsAdapter.notifyDataSetChanged();
                        showPricesCardView();
                        updateAllMainValues();

                        //additional discount updateCount
                        String valueAdditionalDiscount = additionalEdittext.getText().toString().trim();
                        additionalEdittext.setText("0");
                        additionalEdittext.setText(valueAdditionalDiscount);
                        //update material tax or tax
                        if (taxListArrayList != null && taxListArrayList.size() > 1) {
                            setMaterialTax(materialTaxChargeTypeSpinner.getSelectedItemPosition(),
                                    taxListArrayList.get(materialTaxChargeTypeSpinner.getSelectedItemPosition()).getValue());
                            taxChargeTypeSpinnerSelection(taxChargeTypeSpinner.getSelectedItemPosition(),
                                    taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getValue(), "0");
                        }
                        if (productsDialog != null && productsDialog.isShowing()) {
                            productsDialog.dismiss();
                        }
                        //productsDialog.dismiss();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (discountEdittext.hasFocus()) {
                                priceEdittext.requestFocus();
                            }

                        }

                    }, 100);


                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewAdd.setEnabled(true);
                        }
                    }, 4000);
                }
            });

            //Back to selection
            textViewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Added on 11th June
                    if (sourceType != null && sourceType.equals("edit")) {
                        if (strProductsImageUrl != null && !strProductsImageUrl.isEmpty()) {
                            itemDataArrayList.get(positionToEdit).setImage(strProductsImageUrl);
                            orderItemsAdapter.notifyDataSetChanged();
                        }
                    }
                    textViewBack.setEnabled(false);

                    if (sourceType.equals("scan")) {
                        scanBarcode(REQUEST_IMAGE_CAPTURE);
                    }

                    productsDialog.dismiss();
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewBack.setEnabled(true);
                        }
                    }, 4000);
                }
            });

            productsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (sourceType != null && sourceType.equals("edit")) {
                        if (strProductsImageUrl != null && !strProductsImageUrl.isEmpty()) {
                            itemDataArrayList.get(positionToEdit).setImage(strProductsImageUrl);
                            orderItemsAdapter.notifyDataSetChanged();
                        }
                    }

                    if (sourceType.equals("scan")) {
                        scanBarcode(REQUEST_IMAGE_CAPTURE);
                    }
                    hideLoaderInDialog(productsDialog);
                    hideLoader();
                    productsDialog.dismiss();
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

                //TODO Updated on 25th June
                if (itemData.getDiscount() != null && !itemData.getDiscount().isEmpty()) {
                    discountEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(
                            itemData.getDiscount())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                }

                if (itemData.getDiscountType() != null && !itemData.getDiscountType().isEmpty()) {
                    if (itemData.getDiscountType().equals("1")) {
                        discountTypeSpinner.setSelection(0);
                    } else {
                        discountTypeSpinner.setSelection(1);
                    }
                }
            } else {
                discountEdittext.setText(new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(
                        itemData.getDiscount())))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                if (itemData.getDiscountType().equals("1")) {
                    discountTypeSpinner.setSelection(0);
                } else {
                    discountTypeSpinner.setSelection(1);
                }
                titleTextView.setText("Edit Product");
            }

        } catch (Exception ex) { // handle your exception
            ex.printStackTrace();
        }


        //If SourceType is edit

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        productsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        productsDialog.show();
    }

    @Override
    public void removeItemAndNotify(int position) {
        try {
            if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                itemDataArrayList.remove(position);
                orderItemsAdapter.notifyDataSetChanged();
                updateAllMainValues();
            }
            if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                showPricesCardView();
            } else {
                hidePricesCardView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hidePricesCardView() {
        if (cardViewInnerPriceLayout != null && cardViewInnerPriceLayout.getVisibility() == View.VISIBLE) {
            cardViewInnerPriceLayout.setVisibility(View.GONE);
        }
    }

    private void showPricesCardView() {
        if (cardViewInnerPriceLayout != null && cardViewInnerPriceLayout.getVisibility() == View.GONE) {
            cardViewInnerPriceLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void editItemAndOpenDialog(int position) {
        if (productsDialog == null || !productsDialog.isShowing()) {
            dialogAddItems(EditOrderActivity.this, "edit", itemDataArrayList.get(position), position);
        }
    }

    @Override
    public void onQuotationChargeDiscountChange(int position, String discountValue, ChargesList chargesList) {

        try {
            if (chargesList.getEctid().equals("1")) {
                if (strExtensiveTaxFlag != null && strExtensiveTaxFlag.equals("1")) {
                    if (materialCostLayout.getVisibility() == View.VISIBLE) {
                        if (materialCostValueTextView.getText().toString().trim().contains(",") &&
                                additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                                    replace("-", "")) -
                                    -ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", ""))
                                    - ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else if (additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim())
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                                    replace("-", "")) -
                                    -ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim())
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", ""))
                                    - ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }
                        chargesList.setDiscountValue(new BigDecimal(discountValue)
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        preChargesListArrayList.set(position, chargesList);
                        if (!chargeListRecyclerView.isComputingLayout()) {
                            quotationChargesListAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    if (materialCostLayout.getVisibility() == View.VISIBLE) {
                        if (materialCostValueTextView.getText().toString().trim().contains(",") &&
                                additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                                    replace("-", ""))) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else if (materialCostValueTextView.getText().toString().trim().contains(",")) {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim().replace(",", ""))
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", ""))) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else if (additionalDiscountValueTextView.getText().toString().trim().contains(",")) {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim())
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace(",", "").
                                    replace("-", ""))) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            chargesList.setDiscountAmountValue("" + new BigDecimal(((ParseDouble(materialCostValueTextView.getText().toString().trim())
                                    - ParseDouble(additionalDiscountValueTextView.getText().toString().trim().replace("-", ""))) *
                                    (ParseDouble(discountValue.toString()))) / 100)
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }
                        chargesList.setDiscountValue(new BigDecimal(discountValue)
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        preChargesListArrayList.set(position, chargesList);
                        if (!chargeListRecyclerView.isComputingLayout()) {
                            quotationChargesListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else {
                chargesList.setDiscountAmountValue(new BigDecimal(discountValue)
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                chargesList.setDiscountValue(new BigDecimal(discountValue)
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateAllMainValues();
        setGrandTotalAndPayableAmount();
    }

    @Override
    public void onQuotationChargeTwoDiscount(int position, String discountValue, ChargesList2 chargesList2) {
        if (chargesList2.getEctid() != null && chargesList2.getEctid().equals("1")) {
            if (materialCostLayout.getVisibility() == View.VISIBLE) {
                chargesList2.setDiscountAmountValue(String.valueOf(new BigDecimal(
                        ((ParseDouble(chargeThreeValueTextView.getText().toString().trim()))
                                * ParseDouble(discountValue)) / 100)
                        .setScale(2, RoundingMode.HALF_UP).toPlainString()));
                chargesList2.setDiscountValue(new BigDecimal(discountValue)
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                postChargesListArrayList.set(position, chargesList2);
                if (!chargeListTwoRecyclerView.isComputingLayout()) {
                    quotationChargesTwoListAdapter.notifyDataSetChanged();
                }
            }
        } else {
            chargesList2.setDiscountAmountValue(new BigDecimal(discountValue)
                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
            chargesList2.setDiscountValue(new BigDecimal(discountValue)
                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
            postChargesListArrayList.set(position, chargesList2);
            runOnUiThread(new Runnable() {
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

    public void taxChargeTypeSpinnerSelection(int position, String value, String flag) {
        double taxValue, taxAmount = 0, t_amount, discountedAmount = 0, finalTaxAmount = 0;
        if (flag.equals("1")) {
            taxValue = ParseDouble(value);
        } else {
            taxValue = ParseDouble(taxListArrayList.get(position).getValue());
        }

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
                if (flag.equals("0")) {
                    taxChargeValueTextView.setText(new BigDecimal(roundTwoDecimals(finalTaxAmount))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    setGrandTotalAndPayableAmount();
                }


            } else {
                if (position == 0) {
                    if (flag.equals("0")) {
                        taxChargeValueTextView.setText("0.0");
                        setGrandTotalAndPayableAmount();
                    }
                }
            }
        }
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
        if (taxLayout.getVisibility() == View.VISIBLE && taxChargeValueTextView.getText().toString().trim().length() != 0) {
            sum = ParseDouble(taxChargeValueTextView.getText().toString().trim());
        }
        return sum;
    }

    public void setGrandTotalAndPayableAmount() {
        try {
            DecimalFormat format = null;

            format = new DecimalFormat();

            //to remove zero after decimal
            format.setDecimalSeparatorAlwaysShown(false);

            grandTotalValueTextView.setText(String.valueOf(new BigDecimal(roundTwoDecimals(getDiscountedAmount() + sumAllCharges() +
                    sumAllChargesTwo() + sumAllChargesThree() + sumTaxCharges() +
                    ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())))
                    .setScale(2, 0).toPlainString()));

            roundOff = roundTwoDecimals(Math.round(ParseDouble(grandTotalValueTextView.getText().toString().trim())) -
                    ParseDouble(grandTotalValueTextView.getText().toString().trim()));

            roundOffEdittext.setText(String.valueOf(roundOff));
            //roundOffValueTextView.setText(String.valueOf(roundOff));

            payableAmountValueTextView.setText(String.valueOf
                    (new BigDecimal(Constants.roundTwoDecimals(roundOff +
                            ParseDouble(grandTotalValueTextView.getText().toString().trim())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString()));

            //Set Values of all dropdown/Spinner charges
            double a = 0;
            for (int i = 1; i < chargesList3ArrayList.size(); i++) {
                if (chargesList3ArrayList.get(i).isSelected()) {
                    a += (((getDiscountedAmount() + sumAllCharges())
                            * ParseDouble(chargesList3ArrayList.get(i).getValue())) / 100);
                }
            }
            chargeThreeValueTextView.setText(String.valueOf(new BigDecimal(roundTwoDecimals(a))
                    .setScale(2, RoundingMode.HALF_UP).toPlainString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void getSetCustomersInfo() {
        if (customerInfo != null) {

            if (customerInfo.getName() != null && !customerInfo.getName().isEmpty()) {
                customerNameTitleTextView.setText(customerInfo.getName() + "");
            }

            if (customerInfo.getMobile() != null && !customerInfo.getMobile().isEmpty()) {
                mobileTextView.setText(customerInfo.getMobile() + "");
                mobileTextView.setVisibility(View.VISIBLE);
            }

            strCuid = customerInfo.getCuid() + "";

            if (customerInfo.getEmail() != null && !customerInfo.getEmail().isEmpty()) {
                emailTextView.setText(customerInfo.getEmail() + "");
                emailTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getOrderPrefilledData(final Activity activity, String orderId, boolean flag) {
        //TODO Updated on 12th June
        if (flag) {
            task = getString(R.string.convert_quotation_into_order_task);
        } else {
            task = getString(R.string.edit_order_info_task);
        }
        if (AppPreferences.getIsLogin(EditOrderActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(EditOrderActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(EditOrderActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(EditOrderActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = null;

        if (flag) {
            call = apiService.fetchExistingOrderDataByQoid(version, key, task, userId, accessToken, strQoId);
        } else {
            call = apiService.fetchExistingOrderData(version, key, task, userId, accessToken, orderId);
        }
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

                        if (smsTemplateDataArrayList != null && smsTemplateDataArrayList.size() > 0) {
                            smsTemplateDataArrayList.clear();
                        }

                        if (emailTemplateArrArrayList != null && emailTemplateArrArrayList.size() > 0) {
                            emailTemplateArrArrayList.clear();
                        }

                        if (smsTemplateDataArrayList.size() > 0) {
                            smsTemplateDataArrayList.clear();
                        }
                        if (emailTemplateArrArrayList.size() > 0) {
                            emailTemplateArrArrayList.clear();
                        }

                        if (customerSalesTypeArrayList != null && customerSalesTypeArrayList.size() > 0) {
                            customerSalesTypeArrayList.clear();
                        }

                        if (paymentModeDataList != null && paymentModeDataList.size() > 0) {
                            paymentModeDataList.clear();
                        }

                        if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                            paymentAttributesList.clear();
                        }

                        //TODO - Order Template Id
                        if (apiResponse.getData().getOrtemid() != null) {
                            orderTemId = apiResponse.getData().getOrtemid();
                        }

                        //TODO - Outlet Id
                        if (apiResponse.getData().getChkid() != null) {
                            strChkId = apiResponse.getData().getChkid();
                        }

                        //TODO - Shipping Address ID
                        if (apiResponse.getData().getSaid() != null) {
                            strSaid = apiResponse.getData().getSaid();
                        }

                        //TODO - Billing Address ID
                        if (apiResponse.getData().getBaid() != null) {
                            strBaid = apiResponse.getData().getBaid();
                        }

                        //TODO - Customer's Info
                        if (apiResponse.getData().getCustomerInfo() != null) {
                            customerInfo = apiResponse.getData().getCustomerInfo();
                            getSetCustomersInfo();
                        }

                        //TODO - Customer Sales Type
                        CustomerSalesType salesType = new CustomerSalesType();
                        salesType.setName("Select");
                        salesType.setCustid("0");
                        customerSalesTypeArrayList.add(salesType);
                        if (apiResponse.getData().getCustomerSalesTypes() != null &&
                                !apiResponse.getData().getCustomerSalesTypes().isEmpty() &&
                                !apiResponse.getData().getCustomerSalesTypes().equals("null")) {
                            for (CustomerSalesType type : apiResponse.getData().getCustomerSalesTypes()) {
                                customerSalesTypeArrayList.add(type);
                            }
                        }

                        ArrayAdapter<CustomerSalesType> salesTypeArrayAdapter =
                                new ArrayAdapter<CustomerSalesType>(EditOrderActivity.this,
                                        R.layout.simple_spinner_item, customerSalesTypeArrayList);
                        salesTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        customerSalesTypeSpinner.setAdapter(salesTypeArrayAdapter);
                        if (customerSalesTypeArrayList != null && customerSalesTypeArrayList.size() > 1) {
                            customerSalesTypeSpinner.setSelection(1);
                        }

                        //TODO - Payment Mode Data Type
                        PaymentModeData paymentModeData = new PaymentModeData();
                        paymentModeData.setName("Select");
                        paymentModeDataList.add(paymentModeData);
                        if (apiResponse.getData().getPaymentModeData() != null &&
                                !apiResponse.getData().getPaymentModeData().isEmpty() &&
                                !apiResponse.getData().getPaymentModeData().equals("null")) {
                            for (PaymentModeData type : apiResponse.getData().getPaymentModeData()) {
                                paymentModeDataList.add(type);
                            }
                        }

                        ArrayAdapter<PaymentModeData> modeDataArrayAdapter =
                                new ArrayAdapter<PaymentModeData>(EditOrderActivity.this,
                                        R.layout.simple_spinner_item, paymentModeDataList);
                        modeDataArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        paymentModeSpinner.setAdapter(modeDataArrayAdapter);
                        paymentModeSpinner.setSelection(0);

                        //TODO - Payment Term Day
                        if (apiResponse.getData().getPaymentTermDay() != null &&
                                !apiResponse.getData().getPaymentTermDay().isEmpty()) {
                            daysTextInputEditText.setText(apiResponse.getData().getPaymentTermDay());
                        }

                        //TODO - Sales Ledger
                        if (apiResponse.getData().getLedgerInfo() != null &&
                                apiResponse.getData().getLedgerInfo().getName() != null) {
                            salesLedgerValueAutocompleteTextView.setText(
                                    apiResponse.getData().getLedgerInfo().getName());
                            strSalesLedgerId = apiResponse.getData().getLedgerInfo().getAlid();
                            strSalesLedgerName = apiResponse.getData().getLedgerInfo().getName();
                            disableSalesLedgerAutoComplete();
                        }

                        if (apiResponse.getData().getSmsTemplateData() != null) {
                            for (final SmsTemplateDatum smsTemplateDatum : apiResponse.getData().getSmsTemplateData()) {
                                if (smsTemplateDatum != null && smsTemplateDatum.getTitle() != null) {
                                    smsTemplateDataArrayList.add(smsTemplateDatum);
                                }
                            }
                        }

                     /*   EmailTemplateArr emailTemplate = new EmailTemplateArr();
                        emailTemplate.setTitle("Select Template");
                        emailTemplateArrArrayList.add(emailTemplate);*/

                        if (apiResponse.getData().getEmailTemplateArr() != null) {
                            for (final EmailTemplateArr emailTemplateArr : apiResponse.getData().getEmailTemplateArr()) {
                                if (emailTemplateArr != null && emailTemplateArr.getTitle() != null) {
                                    emailTemplateArrArrayList.add(emailTemplateArr);
                                }
                            }
                        }

                        ArrayAdapter<SmsTemplateDatum> smsTemplateArrayAdapter =
                                new ArrayAdapter<SmsTemplateDatum>(EditOrderActivity.this, R.layout.simple_spinner_item, smsTemplateDataArrayList);
                        smsTemplateArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        spinnerSmsTemplate.setAdapter(smsTemplateArrayAdapter);
                        spinnerSmsTemplate.setSelection(0);
                        if (smsTemplateDataArrayList != null && smsTemplateDataArrayList.size() > 0) {
                            for (int i = 0; i < smsTemplateDataArrayList.size(); i++) {
                                if (smsTemplateDataArrayList.get(i).getSelected()) {
                                    spinnerSmsTemplate.setSelection(i);
                                }
                            }
                        }

                        ArrayAdapter<EmailTemplateArr> emailTemplateArrayAdapter =
                                new ArrayAdapter<EmailTemplateArr>(EditOrderActivity.this, R.layout.simple_spinner_item, emailTemplateArrArrayList);
                        emailTemplateArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        spinnerEmailTemplate.setAdapter(emailTemplateArrayAdapter);
                        spinnerEmailTemplate.setSelection(0);
                        if (emailTemplateArrArrayList != null && emailTemplateArrArrayList.size() > 0) {
                            for (int i = 0; i < emailTemplateArrArrayList.size(); i++) {
                                if (emailTemplateArrArrayList.get(i).getSelected()) {
                                    spinnerEmailTemplate.setSelection(i);
                                }
                            }
                        }

                        if (apiResponse.getData().getSmstid() != null &&
                                apiResponse.getData().getSmstid().equals("0")) {
                            smsCheckBox.setChecked(false);
                        } else {
                            smsCheckBox.setChecked(true);
                            showSMSPreview.setVisibility(View.VISIBLE);
                            spinnerSmsTemplate.setVisibility(View.VISIBLE);
                        }

                        if (apiResponse.getData().getEtid() != null &&
                                apiResponse.getData().getEtid().equals("0")) {
                            emailCheckBox.setChecked(false);
                        } else {
                            emailCheckBox.setChecked(true);
                            showEmailPreview.setVisibility(View.VISIBLE);
                            spinnerEmailTemplate.setVisibility(View.VISIBLE);
                        }


                        if (apiResponse.getData().getTaxList() != null) {
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
                        }

                        //Check null of charges List
                        if (apiResponse.getData().getChargesList() != null) {
                            for (final ChargesList chargesList : apiResponse.getData().getChargesList()) {
                                if (chargesList != null) {
                                    chargesList.setDiscountAmountValue("0");
                                    //   chargesList.setDiscountValue("0");
                                    preChargesListArrayList.add(chargesList);
                                }
                            }
                            quotationChargesListAdapter.notifyDataSetChanged();
                        }


                        //Check null of charges List 2
                        if (apiResponse.getData().getChargesList2() != null) {
                            for (final ChargesList2 chargesList2 : apiResponse.getData().getChargesList2()) {
                                if (chargesList2 != null) {
                                    chargesList2.setDiscountAmountValue("0");
                                    // chargesList2.setDiscountValue("0");
                                    postChargesListArrayList.add(chargesList2);
                                }
                            }
                            quotationChargesTwoListAdapter.notifyDataSetChanged();
                        }


                        //Select
                        ChargesList3 chargesList = new ChargesList3();
                        chargesList.setTitle("Select");
                        chargesList3ArrayList.add(chargesList);
                        //Check null of charges List 3
                        if (apiResponse.getData().getChargesList3() != null) {
                            for (final ChargesList3 chargesList3 : apiResponse.getData().getChargesList3()) {
                                if (chargesList3 != null) {
                                    chargesList3ArrayList.add(chargesList3);
                                }
                            }
                        }

                        //TODO Charges Two
                        if (postChargesListArrayList != null && postChargesListArrayList.size() > 0) {
                            quotationChargesTwoListAdapter.notifyDataSetChanged();
                            chargeListTwoRecyclerView.setVisibility(View.VISIBLE);
                            viewChargesListTwo.setVisibility(View.VISIBLE);
                        } else {
                            chargeListTwoRecyclerView.setVisibility(View.GONE);
                            viewChargesListTwo.setVisibility(View.GONE);
                        }

                        //Tax Adapter
                        ArrayAdapter<TaxList> taxListArrayAdapter =
                                new ArrayAdapter<TaxList>(activity, R.layout.simple_spinner_item, taxListArrayList);
                        taxListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        taxChargeTypeSpinner.setAdapter(taxListArrayAdapter);

                        if (taxListArrayList != null && taxListArrayList.size() > 1) {
                            for (int i = 1; i < taxListArrayList.size(); i++) {
                                if (taxListArrayList.get(i).getSelected()) {
                                    taxChargeTypeSpinner.setSelection(i);
                                }
                            }
                        }

                        //Material Cost Tax Adapter
                        ArrayAdapter<TaxList> materialCostTaxListArrayAdapter =
                                new ArrayAdapter<TaxList>(activity, R.layout.simple_spinner_item, taxListArrayList);
                        materialCostTaxListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        materialTaxChargeTypeSpinner.setAdapter(materialCostTaxListArrayAdapter);

                        if (taxListArrayList != null && taxListArrayList.size() > 1) {
                            for (int i = 1; i < taxListArrayList.size(); i++) {
                                if (taxListArrayList.get(i).getSelected()) {
                                    materialTaxChargeTypeSpinner.setSelection(i);
                                }
                            }
                        }

                        strExtensiveTaxFlag = apiResponse.getData().getExtensiveTaxRequired();
                        strFlagDealerPrice = apiResponse.getData().getDealerPriceInclude();

                        //TODO - EXTRA DISCOUNT
                        if (apiResponse.getData().getExtraDiscountData() != null &&
                                apiResponse.getData().getExtraDiscountData().size() > 0 &&
                                apiResponse.getData().getExtraDiscountData().get(0).getName() != null) {
                            strExtraDiscountTitle = apiResponse.getData()
                                    .getExtraDiscountData().get(0).getName();
                            strExtraDiscountId = apiResponse.getData()
                                    .getExtraDiscountData().get(0).getEdid();
                            if (apiResponse.getData().getExtraDiscountData().
                                    get(0).getDiscountType() != null &&
                                    apiResponse.getData().getExtraDiscountData().
                                            get(0).getDiscountType().equals("1")) {
                                additionalDiscountTypeSpinner.setSelection(0);
                            } else {
                                additionalDiscountTypeSpinner.setSelection(1);
                            }
                            if (apiResponse.getData().getExtraDiscountData().
                                    get(0).getValue() != null &&
                                    !apiResponse.getData().getExtraDiscountData().
                                            get(0).getValue().isEmpty()) {
                                additionalEdittext.setText("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                                        ParseDouble(apiResponse.getData().getExtraDiscountData().
                                                get(0).getValue()))).setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }

                        }

                        additionalDiscountTitleTextView.setText(strExtraDiscountTitle);
                        if (strExtraDiscountTitle != null && !strExtraDiscountTitle.isEmpty()) {
                            additionalDiscountLayout.setVisibility(View.VISIBLE);
                        } else {
                            additionalDiscountLayout.setVisibility(View.GONE);
                        }

                        //TODO NEW CHECK ADDED ON 28th FEB WHICH DECIDES UI
                        if (apiResponse.getData().getExtensiveTaxRequired() != null &&
                                !apiResponse.getData().getExtensiveTaxRequired().isEmpty() &&
                                apiResponse.getData().getExtensiveTaxRequired().equals("1")) {

                            quotationPreChargesAdapter = new QuotationPreChargesAdapter(activity, preChargesListArrayList,
                                    EditOrderActivity.this);
                            chargeListRecyclerView.setAdapter(quotationPreChargesAdapter);
                            quotationPreChargesAdapter.notifyDataSetChanged();

                            //Post charges
                            chargesThreeLayout.setVisibility(View.GONE);
                            chargeListThreeRecyclerView.setVisibility(View.GONE);
                            chargesThreeTitleTextView.setVisibility(View.GONE);
                            viewChargesThree.setVisibility(View.GONE);

                            //Show Material Tax & Hide Normal Tax
                            materialTaxLayout.setVisibility(View.VISIBLE);
                            materialTaxChargeView.setVisibility(View.VISIBLE);
                            materialTaxTitleTextView.setVisibility(View.VISIBLE);

                            taxTitleTextView.setVisibility(View.GONE);
                            taxChargeView.setVisibility(View.GONE);
                            taxLayout.setVisibility(View.GONE);

                        } else {
                            //Pre charges List Adapter
                            quotationChargesListAdapter.notifyDataSetChanged();

                            //Post charges
                            chargeListThreeRecyclerView.setVisibility(View.GONE);
                            chargesThreeLayout.setVisibility(View.VISIBLE);

                            quotationChargesThreeSpinnerCheckAdapter = new QuotationChargesThreeSpinnerCheckAdapter(EditOrderActivity.this, R.layout.item,
                                    chargesList3ArrayList, EditOrderActivity.this);
                            chargeTypeThreeSpinner.setAdapter(quotationChargesThreeSpinnerCheckAdapter);
                            quotationChargesThreeSpinnerCheckAdapter.notifyDataSetChanged();
                            quotationChargesThreeSpinnerCheckAdapter.setDropDownViewResource(R.layout.item);

                            //It has one static so we will match with one
                            //TODO SPINNER CHARGES Layout
                            if (chargesList3ArrayList != null && chargesList3ArrayList.size() > 1) {
                                viewChargesThree.setVisibility(View.VISIBLE);
                                chargesThreeTitleTextView.setVisibility(View.VISIBLE);
                                chargesThreeLayout.setVisibility(View.VISIBLE);
                            } else {
                                viewChargesThree.setVisibility(View.GONE);
                                chargesThreeTitleTextView.setVisibility(View.GONE);
                                chargesThreeLayout.setVisibility(View.GONE);
                            }


                            //Hide Material Tax & Show Normal Tax
                            materialTaxLayout.setVisibility(View.GONE);
                            materialTaxChargeView.setVisibility(View.GONE);
                            materialTaxTitleTextView.setVisibility(View.GONE);

                            taxTitleTextView.setVisibility(View.VISIBLE);
                            taxChargeView.setVisibility(View.VISIBLE);
                            taxLayout.setVisibility(View.VISIBLE);
                        }

                        showPricesCardView();

                        if (apiResponse.getData().getOrderProducts() != null) {
                            for (ItemData itemData : apiResponse.getData().getOrderProducts()) {
                                // if (itemData.getButapid() != null && !itemData.getButapid().isEmpty()) {
                                String selectedTax = null;
                                //Auto Selected value of spinner for tax
                                for (int i = 0; i < itemData.getTaxes().size(); i++) {
                                    if (itemData.getTaxes().get(i).getSelected()) {
                                        selectedTax = itemData.getTaxes().get(i).getValue();
                                    }
                                }
                                double price = Constants.roundTwoDecimals(Constants.ParseDouble(
                                        itemData.getPrice().toString())),
                                        dealerPrice = Constants.roundTwoDecimals(Constants.ParseDouble(
                                                itemData.getDealerPrice().toString())),
                                        quantity = Constants.roundTwoDecimals(Constants.ParseDouble(
                                                itemData.getQuantity().toString())),
                                        discount = Constants.roundTwoDecimals(Constants.ParseDouble(
                                                itemData.getDiscount().toString()));
                                //TODO - Updated On 10th May
                                if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                        !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                    itemData.setPrice(new BigDecimal(((Constants.ParseDouble(itemData.getPrice())
                                            * Constants.ParseDouble(itemData.getTax())) / 100) +
                                            Constants.ParseDouble(itemData.getPrice()))
                                            .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                    if (itemData.getDiscountType() != null &&
                                            !itemData.getDiscountType().isEmpty() &&
                                            itemData.getDiscountType().equals("1")) {
                                        itemData.setSubtotalAmount("" + new BigDecimal(roundTwoDecimals(
                                                ((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                                        ParseDouble(removeComma(itemData.getQuantity().toString())) *
                                                        (100.00 - ParseDouble(removeComma(itemData.getDiscount().toString())))) / 100)
                                                        +
                                                        ((((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                                                ParseDouble(removeComma(itemData.getQuantity().toString())) *
                                                                (100.00 - ParseDouble(removeComma(itemData.getDiscount().toString())))) / 100) *
                                                                ParseDouble(itemData.getTax())) / 100)
                                                        -
                                                        ((((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                                                ParseDouble(removeComma(itemData.getQuantity().toString())) *
                                                                (100.00 - ParseDouble(removeComma(itemData.getDiscount().toString())))) / 100) *
                                                                ParseDouble(itemData.getTax())) / 100)))
                                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                        itemData.setAmount("" + new BigDecimal(roundTwoDecimals(
                                                ((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                                        ParseDouble(removeComma(itemData.getQuantity().toString()))))))
                                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                        itemData.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals((
                                                (ParseDouble((itemData.getDealerPrice().toString())) *
                                                        ParseDouble(itemData.getQuantity().toString()) *
                                                        (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100)
                                        )).setScale(2, RoundingMode.HALF_UP).toPlainString());

                                    } else if (itemData.getDiscountType() != null &&
                                            !itemData.getDiscountType().isEmpty() &&
                                            itemData.getDiscountType().equals("2")) {
                                        itemData.setSubtotalAmount("" + new BigDecimal(roundTwoDecimals((
                                                (ParseDouble(removeComma(itemData.getPrice().toString())) *
                                                        ParseDouble(removeComma(itemData.getQuantity().toString()))) -
                                                        ParseDouble(itemData.getDiscount().toString()))))
                                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                        itemData.setAmount("" + new BigDecimal(roundTwoDecimals((
                                                (ParseDouble(removeComma(itemData.getPrice().toString())) *
                                                        ParseDouble(removeComma(itemData.getQuantity().toString()))))))
                                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                        itemData.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals((
                                                (ParseDouble((itemData.getDealerPrice().toString())) *
                                                        ParseDouble(itemData.getQuantity().toString()) *
                                                        (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100)
                                                +
                                                ((((ParseDouble((itemData.getDealerPrice().toString())) *
                                                        ParseDouble(itemData.getQuantity().toString()) *
                                                        (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100) *
                                                        ParseDouble(selectedTax)) / 100)))
                                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                    }

                                } else {
                                    //TODO - Case of Percentage
                                    if (itemData.getDiscountType() != null && itemData.getDiscountType().equals("2")) {
                                        if (selectedTax != null && !selectedTax.isEmpty()) {
                                            itemData.setSubtotalAmount("" + new BigDecimal(roundTwoDecimals(
                                                    ((price * quantity) - discount)
                                                            +
                                                            (((quantity * price) - discount) * ParseDouble(selectedTax)) / 100))
                                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                            itemData.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals((
                                                    (ParseDouble((itemData.getDealerPrice().toString())) *
                                                            ParseDouble(itemData.getQuantity().toString()) *
                                                            (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100)
                                                    +
                                                    ((((ParseDouble((itemData.getDealerPrice().toString())) *
                                                            ParseDouble(itemData.getQuantity().toString()) *
                                                            (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100) *
                                                            ParseDouble(selectedTax)) / 100)))
                                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                        } else {
                                            itemData.setSubtotalAmount("" + new BigDecimal(roundTwoDecimals(((ParseDouble(itemData.getPrice().toString()) *
                                                    ParseDouble((itemData.getQuantity().toString()))) -
                                                    ParseDouble((itemData.getDiscount().toString())))
                                            )).setScale(2, RoundingMode.HALF_UP).toPlainString());
                                            itemData.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals((
                                                    (ParseDouble((itemData.getDealerPrice().toString())) *
                                                            ParseDouble(itemData.getQuantity().toString()) *
                                                            (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100)
                                            )).setScale(2, RoundingMode.HALF_UP).toPlainString());
                                        }
                                    } else {
                                        if (selectedTax != null && !selectedTax.isEmpty()) {
                                            itemData.setSubtotalAmount("" + new BigDecimal(roundTwoDecimals((
                                                    (quantity * price * (100.00 - discount)) / 100)
                                                    +
                                                    ((((quantity * price * (100.00 - discount)) / 100) *
                                                            ParseDouble(selectedTax)) / 100)))
                                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                            itemData.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals(
                                                    ((ParseDouble((itemData.getDealerPrice().toString())) *
                                                            ParseDouble(itemData.getQuantity().toString()) *
                                                            (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100)
                                                            +
                                                            ((((ParseDouble((itemData.getDealerPrice().toString())) *
                                                                    ParseDouble(itemData.getQuantity().toString()) *
                                                                    (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100) *
                                                                    ParseDouble(selectedTax)) / 100)))
                                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                        } else {
                                            itemData.setSubtotalAmount("" + new BigDecimal(roundTwoDecimals((
                                                    (quantity * price * (100.00 - discount)) / 100)))
                                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());

                                            itemData.setDealerPriceSubtotal("" + new BigDecimal(roundTwoDecimals(
                                                    ((ParseDouble((itemData.getDealerPrice().toString())) *
                                                            ParseDouble(itemData.getQuantity().toString()) *
                                                            (100.00 - ParseDouble((itemData.getDiscount().toString())))) / 100)))
                                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                        }
                                    }
                                }
                                if (strFlagDealerPrice != null && strFlagDealerPrice.equals("1")) {
                                    itemData.setStrDealerFlag("1");
                                }


                                if (Constants.ParseDouble(itemData.getDealerPriceSubtotal()) <= 0.0) {
                                    itemData.setDealerPriceSubtotal("0.0");
                                }

                                //TODO Added on 31st May
                                if (itemData.getUnitName() != null && !itemData.getUnitName().isEmpty()) {
                                    itemData.setQuantityType(itemData.getUnitName());
                                }

                                itemDataArrayList.add(itemData);
                            }
                            orderItemsAdapter.notifyDataSetChanged();
                        }

                        if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                            orderItemsAdapter.notifyDataSetChanged();
                            updateAllMainValues();
                        }

                        //TODO - Basic Info
                        if (apiResponse.getData().getOrderType() != null) {
                            if (apiResponse.getData().getOrderType().equals("1")) {
                                creditRadioBtn.setChecked(true);
                                percentLimitTextInputEditText.setText("");
                            } else if (apiResponse.getData().getOrderType().equals("2")) {
                                cashCarryRadioBtn.setChecked(true);
                                if (apiResponse.getData().getPercentLimit() != null) {
                                    percentLimitTextInputEditText.setText(apiResponse.getData().getPercentLimit());
                                }
                            }
                        }
                        if (apiResponse.getData().getPaymentTerm() != null &&
                                !apiResponse.getData().getPaymentTerm().isEmpty()) {

                            paymentTermsSpinner.setSelection(Integer.parseInt(apiResponse.getData().getPaymentTerm()));
                        }

                        //TODO - Bottom Section
                        if (apiResponse.getData().getAssignedUserName() != null &&
                                !apiResponse.getData().getAssignedUserName().isEmpty()) {
                            attendeeAutoCompleteTextView.setText(apiResponse.getData().getAssignedUserName() + "");

                            strAssignee = apiResponse.getData().getAssignedUserName();
                            strUid = apiResponse.getData().getAssignedUserId();
                        }
                        if (apiResponse.getData().getAssignedUserId() != null &&
                                !apiResponse.getData().getAssignedUserId().isEmpty()) {
                            strUid = apiResponse.getData().getAssignedUserId();
                        }

                        if (apiResponse.getData().getCheckpointOrderRemarks() != null) {
                            orderRemarksTextInputEditText.setText(apiResponse.getData().getCheckpointOrderRemarks() + "");
                        }

                        if (apiResponse.getData().getPurchaseOrderNumber() != null) {
                            purchaseOrderNoTextInputEditText.setText(apiResponse.getData().getPurchaseOrderNumber() + "");
                        }

                        if (apiResponse.getData().getPurchaseOrderDate() != null &&
                                !apiResponse.getData().getPurchaseOrderDate().isEmpty() &&
                                !apiResponse.getData().getPurchaseOrderDate().contains("0000")) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = simpleDateFormat.parse(apiResponse.getData().getPurchaseOrderDate());
                                purchaseOrderDateTextInputEditText.setText("" + outputFormat.format(date).toString().trim());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if (apiResponse.getData().getDeliveryDate() != null &&
                                !apiResponse.getData().getDeliveryDate().isEmpty() &&
                                !apiResponse.getData().getDeliveryDate().contains("0000")) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = simpleDateFormat.parse(apiResponse.getData().getDeliveryDate());
                                deliveryDateTextInputEditText.setText("" + outputFormat.format(date).toString().trim());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if (apiResponse.getData().getDueDate() != null &&
                                !apiResponse.getData().getDueDate().isEmpty() &&
                                !apiResponse.getData().getDueDate().contains("0000")) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = simpleDateFormat.parse(apiResponse.getData().getDueDate());
                                dueDateTextInputEditText.setText("" + outputFormat.format(date).toString().trim());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        //TODO - Select Date
                        if (apiResponse.getData().getTakenDate() != null &&
                                !apiResponse.getData().getTakenDate().isEmpty() &&
                                !apiResponse.getData().getTakenDate().contains("0000")) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = simpleDateFormat.parse(apiResponse.getData().getTakenDate());
                                selectDateTextInputEditText.setText("" + outputFormat.format(date).toString().trim());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if (strExtensiveTaxFlag != null && strExtensiveTaxFlag.equals("1")) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something here
                                    updateExtensiveTaxChargesValue();
                                    quotationPreChargesAdapter.notifyDataSetChanged();
                                    updateAllMainValues();
                                }
                            }, 2500);
                        }

                        if (strFlagDealerPrice != null && strFlagDealerPrice.equals("1")) {
                            orderDealerPriceCheckBox.setChecked(true);
                        } else {
                            orderDealerPriceCheckBox.setChecked(false);
                        }


                        //TODO Updated on 20th June
                        if (postChargesListArrayList != null && postChargesListArrayList.size() > 0) {
                            for (int i = 0; i < postChargesListArrayList.size(); i++) {
                                onQuotationChargeTwoDiscount(i, postChargesListArrayList.get(i).getDiscountValue(),
                                        postChargesListArrayList.get(i));
                            }
                        }

                        cardViewInnerQuotOptions.setVisibility(View.VISIBLE);
                    }
                    //Order items are in warehouse
                    else if (apiResponse.getSuccessCode().equals("10001")) {
                        errorDialog(EditOrderActivity.this, apiResponse.getMessage());
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        if (EditOrderActivity.this != null) {
                            Constants.logoutWrongCredentials(EditOrderActivity.this, apiResponse.getMessage());
                        }
                    } else {
                        if (EditOrderActivity.this != null) {
                            Toast.makeText(EditOrderActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    //Delay values
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (apiResponse.getData().getRoundOff() != null &&
                                    !apiResponse.getData().getRoundOff().isEmpty()) {
                                roundOffEdittext.setText("" +
                                        roundTwoDecimals(Constants.ParseDouble(apiResponse.getData().getRoundOff())));
                            }
                        }
                    }, 4000);

                    hideLoader();

                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (EditOrderActivity.this != null) {
                    Log.e("Error", t.getMessage() + "");
                    Toast.makeText(EditOrderActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });

    }

    private void updateExtensiveTaxChargesValue() {
        for (int i = 0; i < preChargesListArrayList.size(); i++) {
            ChargesList chargesList = preChargesListArrayList.get(i);
            if (chargesList.getDiscountValue() != null && chargesList.getDiscountValue().length() != 0
                    && chargesList.getButapid().equals("0")) {
                if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                    preChargesListArrayList.get(i).setDiscountAmountValue("" + chargesList.getDiscountValue());
                } else {
                    double newValue = (((ParseDouble(materialCostValueTextView.getText().toString()) -
                            ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", ""))) +
                            ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())));

                    double nextValue = ((newValue * ParseDouble(chargesList.getDiscountValue())) / 100);
                    preChargesListArrayList.get(i).setDiscountAmountValue("" + new BigDecimal(nextValue)
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                }
            } else {
                if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                    double inputValue = Constants.ParseDouble(chargesList.getDiscountValue());

                    //Get position of selected Tax
                    int tax_pos = 0;
                    for (int j = 0; j < taxListArrayList.size(); j++) {
                        if (taxListArrayList.get(j).getButapid().equals(chargesList.getButapid())) {
                            tax_pos = j;
                        }
                    }

                    double taxValue = (Constants.ParseDouble(taxListArrayList.get(tax_pos)
                            .getValue()) * inputValue) / 100;
                    double totalValue = inputValue + taxValue;

                    double b = ParseDouble(taxListArrayList.get(tax_pos).getButaprid());
                    switch ((int) b) {
                        case 1:
                            totalValue = ((totalValue * 100) / 100);
                            break;
                        case 2:
                            totalValue = (totalValue);
                            break;
                        case 3:
                            totalValue = Math.ceil(totalValue);
                            break;
                        case 4:
                            totalValue = Math.floor(totalValue);
                            break;
                        default:
                            totalValue = (totalValue * 100) / 100;
                            break;
                    }

                    preChargesListArrayList.get(i).setDiscountAmountValue("" + new BigDecimal(totalValue)
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                } else {

                    //Get position of selected Tax
                    int tax_pos = 0;
                    for (int j = 0; j < taxListArrayList.size(); j++) {
                        if (taxListArrayList.get(j).getButapid().equals(chargesList.getButapid())) {
                            tax_pos = j;
                        }
                    }

                    if (taxListArrayList.get(tax_pos).getButapatid().equals("1")) {
                        double newValue = (ParseDouble(chargesList.getDiscountValue()) *
                                (ParseDouble(materialCostValueTextView.getText().toString()) -
                                        ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", "")) +
                                        ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) / 100);

                        double nextValue = ((newValue * ParseDouble(taxListArrayList.
                                get(tax_pos).getValue())) / 100);

                        double totalValue = newValue + nextValue;

                        double b = ParseDouble(taxListArrayList.get(tax_pos).getButaprid());
                        switch ((int) b) {
                            case 1:
                                totalValue = ((totalValue * 100) / 100);
                                break;
                            case 2:
                                totalValue = (totalValue);
                                break;
                            case 3:
                                totalValue = Math.ceil(totalValue);
                                break;
                            case 4:
                                totalValue = Math.floor(totalValue);
                                break;
                            default:
                                totalValue = (totalValue * 100) / 100;
                                break;
                        }

                        preChargesListArrayList.get(i).setDiscountAmountValue("" + new BigDecimal(totalValue)
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                }
            }

        }
    }

    private void enableSalesLedgerAutoComplete() {
        salesLedgerValueAutocompleteTextView.setEnabled(true);
        salesLedgerValueAutocompleteTextView.setFocusable(true);
        salesLedgerValueAutocompleteTextView.setFocusableInTouchMode(true);
        salesLedgerValueAutocompleteTextView.setClickable(true);
        salesLedgerClearImageButton.setVisibility(View.GONE);
        salesLedgerClearImageButton.setEnabled(false);
        strSalesLedgerId = "";
        strSalesLedgerName = "";
    }

    private void disableSalesLedgerAutoComplete() {
        salesLedgerValueAutocompleteTextView.setEnabled(false);
        salesLedgerValueAutocompleteTextView.setFocusable(false);
        salesLedgerValueAutocompleteTextView.setFocusableInTouchMode(false);
        salesLedgerValueAutocompleteTextView.setClickable(false);
        salesLedgerClearImageButton.setVisibility(View.VISIBLE);
        salesLedgerClearImageButton.setEnabled(true);
    }

    private void enablePaymentLedgerAutoComplete() {
        searchPaymentLedgerValueAutocompleteTextView.setEnabled(true);
        searchPaymentLedgerValueAutocompleteTextView.setFocusable(true);
        searchPaymentLedgerValueAutocompleteTextView.setFocusableInTouchMode(true);
        searchPaymentLedgerValueAutocompleteTextView.setClickable(true);
        searchPaymentLedgerClearImageButton.setVisibility(View.GONE);
        searchPaymentLedgerClearImageButton.setEnabled(false);
        strPaymentLedgerId = "";
        strPaymentLedgerName = "";
    }

    private void disablePaymentLedgerAutoComplete() {
        searchPaymentLedgerValueAutocompleteTextView.setEnabled(false);
        searchPaymentLedgerValueAutocompleteTextView.setFocusable(false);
        searchPaymentLedgerValueAutocompleteTextView.setFocusableInTouchMode(false);
        searchPaymentLedgerValueAutocompleteTextView.setClickable(false);
        searchPaymentLedgerClearImageButton.setVisibility(View.VISIBLE);
        searchPaymentLedgerClearImageButton.setEnabled(true);
    }

    @Override
    public void onQuotationChargeThree(int position, boolean checked) {
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

    //Update all main values
    public void updateAllMainValues() {
        Double totalAmount = 0.0, totalDiscount = 0.0, totalMaterialCost = 0.0;
        for (int i = 0; i < itemDataArrayList.size(); i++) {
            totalAmount += (ParseDouble(itemDataArrayList.get(i).getAmount()));

            //TODO - Dealer Price's check
            if (strFlagDealerPrice != null && strFlagDealerPrice.equals("1")) {
                totalMaterialCost += (ParseDouble(itemDataArrayList.get(i).getDealerPriceSubtotal()));
            } else {
                totalMaterialCost += (ParseDouble(itemDataArrayList.get(i).getSubtotalAmount()));
            }

            if (itemDataArrayList.get(i).getDiscountType().equals("1")) {
                totalDiscount += (((ParseDouble(itemDataArrayList.get(i).getAmount())) *
                        ParseDouble(itemDataArrayList.get(i).getDiscount())) / 100);
            } else {
                totalDiscount += (ParseDouble(itemDataArrayList.get(i).getDiscount()));
            }
        }
        totalDiscountValueTextView.setText(" " + new BigDecimal(Constants.roundTwoDecimals(totalDiscount))
                .setScale(2, RoundingMode.HALF_UP).toPlainString());
        materialCostValueTextView.setText(" " + new BigDecimal(Constants.roundTwoDecimals(totalMaterialCost))
                .setScale(2, RoundingMode.HALF_UP).toPlainString());
        totalAmountValueTextView.setText(" " + new BigDecimal(Constants.roundTwoDecimals(totalAmount))
                .setScale(2, RoundingMode.HALF_UP).toPlainString());

        if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
            materialCostLayout.setVisibility(View.VISIBLE);
            additionalDiscountLayout.setVisibility(View.VISIBLE);
            //TODO - Hide Both fields in case of Order
            totalAmountLayout.setVisibility(View.GONE);
            totalDiscountLayout.setVisibility(View.GONE);
        } else {
            //  totalAmountLayout.setVisibility(View.GONE);
            //  totalDiscountLayout.setVisibility(View.GONE);
            //  materialCostLayout.setVisibility(View.GONE);
            //  additionalDiscountLayout.setVisibility(View.GONE);
        }

        //Spinner selection
        if (taxListArrayList != null && taxListArrayList.size() > 1) {
            taxChargeTypeSpinnerSelection(taxChargeTypeSpinner.getSelectedItemPosition(),
                    taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getValue(), "0");
        }
        setGrandTotalAndPayableAmount();
        setInputFiltersForAdditionalDiscount();
    }

    private void dialogShowPreview(String strMobile, String sms, String strFrom, String strTo, String strCC, String strSubject, String strBody, final String strFile, final String flags) {
        View dialogView = View.inflate(this, R.layout.dialog_preview, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialogSMSEMail = builder.create();

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
            dialogPreviewPrimaryText.setText("SMS Preview");
        } else {
            dialogPreviewEmailPreviewLayout.setVisibility(View.VISIBLE);
            dialogPreviewSmsPreviewLayout.setVisibility(View.GONE);
            dialogPreviewEmailFrom.setText(strFrom);
            dialogPreviewEmailTo.setText(strTo);
            dialogPreviewEmailCc.setText(strCC);
            dialogPreviewEmailSubject.setText(strSubject);
            dialogPreviewEmailBody.setText(Html.fromHtml(strBody));
            dialogPreviewEmailAttachment.setText(strFile);
            dialogPreviewPrimaryText.setText("Email Preview");
        }

        dialogPreviewBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPreviewBtnOk.setEnabled(false);
                alertDialogSMSEMail.dismiss();
                flag = "";
                //Enable Again
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogPreviewBtnOk.setEnabled(true);
                    }
                }, 4000);
            }
        });

        dialogPreviewEmailAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPreviewEmailAttachment.setEnabled(false);
                try {
                    //Download a file and display in phone's download folder
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .mkdirs();
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(strFile);
                    DownloadManager.Request request = new DownloadManager.Request(uri)
                            .setTitle(strFile + "_quotation" + ".pdf")
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                    strFile + "_quotation" + ".pdf")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialogPreviewEmailAttachment.setEnabled(true);
                        }
                    }, 4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialogSMSEMail.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogSMSEMail.show();
    }

    @Override
    public void QuotationChargesThreeListener(int position, String discountValue, ChargesList3 chargesList3) {

    }

    private void pretaxChargesDialog(int position) {
        final View dialogView = View.inflate(this, R.layout.dialog_products_pretaxcharges, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        preChargesDialog = builder.create();
        preChargesDialog.setCanceledOnTouchOutside(true);

        try {

            ChargesList chargesList = preChargesListArrayList.get(position);

            final LinearLayout mainLayout = (LinearLayout) dialogView.findViewById(R.id.main_layout);
            final TextView titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
            final EditText edittext = (EditText) dialogView.findViewById(R.id.edittext);
            final RelativeLayout spinnerLayout = (RelativeLayout) dialogView.findViewById(R.id.spinner_layout);
            final Spinner chargeTypeSpinner = (Spinner) dialogView.findViewById(R.id.charge_type_spinner);
            final TextView chargeValueTextView = (TextView) dialogView.findViewById(R.id.charge_value_textView);
            final TextView saveTextView = (TextView) dialogView.findViewById(R.id.save_textView);

            //If SourceType is edit
            titleTextView.setText(chargesList.getTitle());
            if (chargesList.getDiscountAmountValue() != null && !chargesList.getDiscountAmountValue().isEmpty()) {
                chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(chargesList.getDiscountAmountValue())))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            }

            if (chargesList.getDiscountValue() != null && !chargesList.getDiscountValue().isEmpty()) {
                edittext.setText("" + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(chargesList.getDiscountValue())) + "")
                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
            }

            spinnerLayout.setVisibility(View.VISIBLE);
            ArrayAdapter<TaxList> taxListArrayAdapter =
                    new ArrayAdapter<TaxList>(EditOrderActivity.this, R.layout.simple_spinner_item, taxListArrayList);
            taxListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            chargeTypeSpinner.setAdapter(taxListArrayAdapter);

            chargeTypeSpinner.setSelection(chargesList.getSelectedIndex());
            if (taxListArrayList != null && taxListArrayList.size() > 1) {
                for (int i = 1; i < taxListArrayList.size(); i++) {
                    if (taxListArrayList.get(i).getButapid().equals(chargesList.getButapid())) {
                        chargeTypeSpinner.setSelection(i);
                    }
                }
            }

            edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (chargeTypeSpinner.getSelectedItemPosition() == 0) {
                        if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                            chargeValueTextView.setText(edittext.getText().toString().trim());

                            chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(
                                    Constants.ParseDouble(edittext.getText().toString())))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(
                                    Constants.ParseDouble(chargeValueTextView.getText().toString())))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            double newValue = (((ParseDouble(materialCostValueTextView.getText().toString()) -
                                    ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", ""))) +
                                    ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())));

                            double nextValue = ((newValue * ParseDouble(edittext.getText().toString())) / 100);

                            //double totalValue = newValue + nextValue;
                            chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(
                                    Constants.ParseDouble(edittext.getText().toString())))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(
                                    nextValue)).setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(
                                    nextValue))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }
                    } else {
                        if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                            double inputValue = Constants.ParseDouble(edittext.getText().toString().trim());
                            double taxValue = (Constants.ParseDouble(taxListArrayList.get(chargeTypeSpinner.getSelectedItemPosition())
                                    .getValue()) * inputValue) / 100;
                            double totalValue = inputValue + taxValue;

                            double b = ParseDouble(taxListArrayList.get(chargeTypeSpinner.getSelectedItemPosition()).getButaprid());
                            switch ((int) b) {
                                case 1:
                                    totalValue = ((totalValue * 100) / 100);
                                    break;
                                case 2:
                                    totalValue = (totalValue);
                                    break;
                                case 3:
                                    totalValue = Math.ceil(totalValue);
                                    break;
                                case 4:
                                    totalValue = Math.floor(totalValue);
                                    break;
                                default:
                                    totalValue = (totalValue * 100) / 100;
                                    break;
                            }

                            chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(
                                    edittext.getText().toString())))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        } else {
                            if (taxListArrayList.get(chargeTypeSpinner.getSelectedItemPosition()).getButapatid().equals("1")) {
                                double newValue = (ParseDouble(edittext.getText().toString().trim()) *
                                        (ParseDouble(materialCostValueTextView.getText().toString()) -
                                                ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", "")) +
                                                ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) / 100);

                                double nextValue = ((newValue * ParseDouble(taxListArrayList.
                                        get(chargeTypeSpinner.getSelectedItemPosition()).getValue())) / 100);

                                double totalValue = newValue + nextValue;

                                double b = ParseDouble(taxListArrayList.get(chargeTypeSpinner.getSelectedItemPosition()).getButaprid());
                                switch ((int) b) {
                                    case 1:
                                        totalValue = ((totalValue * 100) / 100);
                                        break;
                                    case 2:
                                        totalValue = (totalValue);
                                        break;
                                    case 3:
                                        totalValue = Math.ceil(totalValue);
                                        break;
                                    case 4:
                                        totalValue = Math.floor(totalValue);
                                        break;
                                    default:
                                        totalValue = (totalValue * 100) / 100;
                                        break;
                                }
                                //  preChargesListArrayList.get(position).setDiscountAmountValue("" + totalValue);
                                chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                                        ParseDouble(edittext.getText().toString())))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                        .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            }
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            chargeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (chargesList.getEctid() != null && chargesList.getEctid().equals("2") &&
                            position != 0) {
                        double inputValue = Constants.ParseDouble(edittext.getText().toString().trim());
                        double taxValue = (Constants.ParseDouble(taxListArrayList.get(position).getValue()) * inputValue) / 100;
                        double totalValue = inputValue + taxValue;

                        double b = ParseDouble(taxListArrayList.get(position).getButaprid());
                        switch ((int) b) {
                            case 1:
                                totalValue = ((totalValue * 100) / 100);
                                break;
                            case 2:
                                totalValue = (totalValue);
                                break;
                            case 3:
                                totalValue = Math.ceil(totalValue);
                                break;
                            case 4:
                                totalValue = Math.floor(totalValue);
                                break;
                            default:
                                totalValue = (totalValue * 100) / 100;
                                break;
                        }

                        chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                                ParseDouble(edittext.getText().toString())))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                    } else if (chargesList.getEctid() != null && chargesList.getEctid().equals("1")
                            && position != 0) {
                        if (taxListArrayList.get(chargeTypeSpinner.getSelectedItemPosition()).getButapatid().equals("1")) {
                            double newValue = (ParseDouble(edittext.getText().toString().trim()) *
                                    (ParseDouble(materialCostValueTextView.getText().toString()) -
                                            ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", "")) +
                                            ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())) / 100);

                            double nextValue = ((newValue * ParseDouble(taxListArrayList.
                                    get(chargeTypeSpinner.getSelectedItemPosition()).getValue())) / 100);

                            double totalValue = newValue + nextValue;

                            double b = ParseDouble(taxListArrayList.get(chargeTypeSpinner.getSelectedItemPosition()).getButaprid());
                            switch ((int) b) {
                                case 1:
                                    totalValue = ((totalValue * 100) / 100);
                                    break;
                                case 2:
                                    totalValue = (totalValue);
                                    break;
                                case 3:
                                    totalValue = Math.ceil(totalValue);
                                    break;
                                case 4:
                                    totalValue = Math.floor(totalValue);
                                    break;
                                default:
                                    totalValue = (totalValue * 100) / 100;
                                    break;
                            }
                            //  preChargesListArrayList.get(position).setDiscountAmountValue("" + totalValue);
                            chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                                    ParseDouble(edittext.getText().toString())))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(totalValue))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                        }
                    } else {
                        if (chargesList.getEctid() != null && chargesList.getEctid().equals("2")) {
                            chargeValueTextView.setText(edittext.getText().toString().trim());
                            chargesList.setDiscountValue(edittext.getText().toString());
                            chargesList.setDiscountAmountValue("" + chargeValueTextView.getText().toString().trim());
                        } else {
                            double newValue = (((ParseDouble(materialCostValueTextView.getText().toString()) -
                                    ParseDouble(additionalDiscountValueTextView.getText().toString().replace("-", ""))) +
                                    ParseDouble(materialTaxChargeValueTextView.getText().toString().trim())));

                            double nextValue = ((newValue * ParseDouble(edittext.getText().toString())) / 100);

                            //      double totalValue = newValue + nextValue;
                            chargesList.setDiscountValue("" + new BigDecimal(Constants.roundTwoDecimals(Constants.
                                    ParseDouble(edittext.getText().toString())))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargeValueTextView.setText("" + new BigDecimal(Constants.roundTwoDecimals(nextValue))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
                            chargesList.setDiscountAmountValue("" + new BigDecimal(Constants.roundTwoDecimals(nextValue))
                                    .setScale(2, RoundingMode.HALF_UP).toPlainString());

                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            saveTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveTextView.setEnabled(false);
                    chargesList.setSelectedIndex(chargeTypeSpinner.getSelectedItemPosition());
                    updatePreChargesList(chargesList, edittext.getText().toString().trim(), position);
                    if (preChargesDialog != null && preChargesDialog.isShowing()) {
                        preChargesDialog.dismiss();
                    }
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveTextView.setEnabled(true);
                        }
                    }, 4000);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        preChargesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        preChargesDialog.show();
    }

    private void updatePreChargesList(ChargesList chargesList, String discountValue, int position) {
        preChargesListArrayList.set(position, chargesList);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!chargeListRecyclerView.isComputingLayout()) {
                    quotationPreChargesAdapter.notifyDataSetChanged();
                }
            }
        });
        setGrandTotalAndPayableAmount();
    }

    @Override
    public void PreChargeDialog(int position) {
        pretaxChargesDialog(position);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.credit_radioBtn:
                if (percentLimitTextInputLayout.getVisibility() == View.VISIBLE) {
                    percentLimitTextInputLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.cashCarry_radioBtn:
                if (percentLimitTextInputLayout.getVisibility() == View.GONE) {
                    percentLimitTextInputLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void searchSalesLedger(String searchText, String agId, String type) {
        task = getString(R.string.account_ledger_search_task);
        if (AppPreferences.getBoolean(EditOrderActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(EditOrderActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(EditOrderActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(EditOrderActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = null;
        if (agId == null || agId.isEmpty()) {
            call = apiService.fetchSalesLedgerWithOutAgId(version, key, task, userId, accessToken, searchText);
        } else {
            call = apiService.fetchSalesLedgerWithAgId(version, key, task, userId, accessToken, searchText, agId);
        }
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (ledgerSearchDataArrayList != null && ledgerSearchDataArrayList.size() > 0) {
                            ledgerSearchDataArrayList.clear();
                        }
                        for (final LedgerSearchData item : apiResponse.getData().getLedgerSearchData()) {
                            if (item != null) {
                                ledgerSearchDataArrayList.add(item);
                            }
                        }

                        ledgersArrayAdapter =
                                new ArrayAdapter<LedgerSearchData>(EditOrderActivity.this,
                                        R.layout.simple_spinner_item, ledgerSearchDataArrayList);
                        ledgersArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        if (agId == null || agId.isEmpty()) {
                            searchPaymentLedgerValueAutocompleteTextView.setAdapter(ledgersArrayAdapter);
                        } else {
                            if (type.equals("payment")) {
                                searchPaymentLedgerValueAutocompleteTextView.setAdapter(ledgersArrayAdapter);
                            } else {
                                salesLedgerValueAutocompleteTextView.setAdapter(ledgersArrayAdapter);
                            }
                        }
                        ledgersArrayAdapter.notifyDataSetChanged();

                    } else if (apiResponse.getSuccessCode().equals("20004")) {

                    } else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(EditOrderActivity.this, apiResponse.getMessage());
                        } else {
                            if (searchText != null && searchText.length() > 0) {
                                Toast.makeText(EditOrderActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (EditOrderActivity.this != null) {
                    Toast.makeText(EditOrderActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void passDate(String s) {
        stringStartDate = s;
        if (allDatePickerFragment.getTag().equals(getString(R.string.cheque_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                //Date Of cheque
                if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                    paymentAttributesList.get(paymentAttributesList.size() - 1).setValue(from);
                    paymentModesAdapter.notifyDataSetChanged();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (allDatePickerFragment.getTag().equals(getString(R.string.delivery_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                deliveryDateTextInputEditText.setText(from);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (allDatePickerFragment.getTag().equals(getString(R.string.due_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                dueDateTextInputEditText.setText(from);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (allDatePickerFragment.getTag().equals(getString(R.string.purchase_order_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                purchaseOrderDateTextInputEditText.setText(from);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (allDatePickerFragment.getTag().equals(getString(R.string.select_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                selectDateTextInputEditText.setText(from);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void passTime(String s) {

    }

    @Override
    public void updateAttributes(int position, String value) {
        if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
            if (value != null) {
                paymentAttributesList.get(position).setValue(value);
            } else {
                paymentAttributesList.get(position).setValue("");
            }
        }
    }

    //Preview API call SMS/EMail
    public void previewSMSEmail() {
        try {
            if (spinnerSmsTemplate.isShown()) {
                if (flag.equals("SMS")) {
                    if (smsTemplateDataArrayList != null && smsTemplateDataArrayList.size() > 0) {
                        strSmstId = smsTemplateDataArrayList.get(spinnerSmsTemplate.getSelectedItemPosition()).getSmstid();
                    }
                    strEtId = "";
                }
            } else {
                strSmstId = "";
            }
            if (spinnerEmailTemplate.isShown()) {
                if (!flag.equals("SMS")) {
                    if (emailTemplateArrArrayList != null && emailTemplateArrArrayList.size() > 0) {
                        strEtId = emailTemplateArrArrayList.get(spinnerEmailTemplate.getSelectedItemPosition()).getEtid();
                    }
                }
            } else {
                strEtId = "";
            }
            JSONObject jsonObject = new JSONObject();

            task = getString(R.string.create_order_task);
            if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
            }

            try {
                if (isConvertFromQuotation) {
                    JSONObject jsonObjectQuotation = new JSONObject();
                    jsonObjectQuotation.put("title", "quotation");
                    jsonObjectQuotation.put("id", strQoId);

                    jsonObject.put("id", jsonObjectQuotation);
                }
                jsonObject.put("order", strOrderId);
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
                jsonObject.put("ortemid", orderTemId);
                jsonObject.put("smstid", strSmstId);
                jsonObject.put("discount", strAdditionalDiscount);
                jsonObject.put("discount_type", String.valueOf(additionalDiscountTypeSpinner.getSelectedItemPosition() + 1));
                jsonObject.put("mail", strMail);
                jsonObject.put("sms", strSms);
                jsonObject.put("attributes", "");
                jsonObject.put("round", strRound);
                jsonObject.put("round_field", "0");
                jsonObject.put("checkpoint_order_remarks", orderRemarksTextInputEditText.getText().toString());
                jsonObject.put("payment-terms", paymentTermsSpinner.getSelectedItemPosition());
                if (paymentTermsSpinner.getSelectedItemPosition() == 3) {
                    jsonObject.put("day", daysTextInputEditText.getText().toString() + "");
                } else {
                    jsonObject.put("day", "");
                }

                //Advance payment
                if (advancePaymentCheckBoxTextView.isChecked()) {
                    jsonObject.put("advance_payment", "1");
                    if (paymentModeSpinner.getSelectedItemPosition() == 0) {
                        jsonObject.put("payment_mode", "");
                    } else {
                        jsonObject.put("payment_mode", paymentModeDataList.get(paymentModeSpinner.getSelectedItemPosition())
                                .getCpoid());
                    }
                    //Payment Ledger Autocomplete
                    jsonObject.put("payment", strPaymentLedgerId);
                    //Amount's value
                    jsonObject.put("advance_amount", advancePaymentAmountTextInputEditText.getText().toString());
                } else {
                    jsonObject.put("payment_mode", "");
                    jsonObject.put("advance_payment", "0");
                    jsonObject.put("advance_amount", "");
                }
                //TODO - Payment Modes Attributes
                if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                    JSONArray paymentModesAttributesJsonArray = new JSONArray();
                    ArrayList<String> stringArray = new ArrayList<String>();

                    for (int i = 0; i < paymentAttributesList.size(); i++) {
                        JSONObject paymentModesAttributesJsonObject = new JSONObject();
                        //TODO Updated on 8th June
                        if (paymentAttributesList.get(i).getName().toLowerCase().equals("date")) {
                            //Date Format
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String chequeDate = "";
                            if (paymentAttributesList.get(i).getValue().toString().trim() != null &&
                                    !paymentAttributesList.get(i).getValue().toString().trim().isEmpty() &&
                                    !paymentAttributesList.get(i).getValue().toString().trim().contains("0000")
                                    && paymentAttributesList.get(i).getValue().toString().charAt(7) != '-') {
                                try {
                                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = simpleDateFormat.parse(paymentAttributesList.get(i).getValue().toString());
                                    paymentAttributesList.get(i).setValue(outputFormat.format(date).toString().trim());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        paymentModesAttributesJsonObject.put(paymentAttributesList.get(i).getCpoaid(),
                                paymentAttributesList.get(i).getValue());
                        //paymentModesAttributesJsonArray.put(paymentModesAttributesJsonObject);
                        stringArray.add(paymentModesAttributesJsonObject.toString());
                    }
                    jsonObject.put("payment_attrs", stringArray);
                } else {
                    jsonObject.put("payment_attrs", "");
                }

                //Customer Sales Type
                if (customerSalesTypeSpinner.getSelectedItemPosition() != 0 &&
                        customerSalesTypeArrayList != null && customerSalesTypeArrayList.size() > 0) {
                    jsonObject.put("custid", customerSalesTypeArrayList.get(customerSalesTypeSpinner.getSelectedItemPosition())
                            .getCustid());
                } else {
                    jsonObject.put("custid", "");
                }

                //Sales Ledger Id
                jsonObject.put("alid", strSalesLedgerId);
                //orderType
                if (creditRadioBtn.isChecked()) {
                    jsonObject.put("order_type", "1");
                    jsonObject.put("percent-limit", "");
                } else {
                    jsonObject.put("order_type", "2");
                    jsonObject.put("percent-limit", "" + percentLimitTextInputEditText.getText().toString());
                }

                //Purchase Order Number & date
                jsonObject.put("order_no", purchaseOrderNoTextInputEditText.getText().toString());
                jsonObject.put("date", purchaseOrderDateTextInputEditText.getText().toString());
                jsonObject.put("due_date", "" + dueDateTextInputEditText.getText().toString());
                jsonObject.put("delivery_date", "" + deliveryDateTextInputEditText.getText().toString());
                jsonObject.put("create_date", selectDateTextInputEditText.getText().toString().trim());


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
                    productsItemJsonObject.put("dealer_price", itemDataArrayList.get(i).getDealerPrice() + "");
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
                    productsItemJsonObject.put("description", "");
                    productsItemJsonObject.put("type", itemDataArrayList.get(i).getType());
                    productsItemJsonObject.put("chkoiid", itemDataArrayList.get(i).getChkoiid());
                    //TODO - Added on 19th April as static
                    productsItemJsonObject.put("roundoff", "0");
                    productsItemJsonObject.put("item_service", "0");
                    productItemJsonArray.put(productsItemJsonObject);
                }

                //Array
                jsonObject.put("cart", productItemJsonArray);

                //TODO PRE-CHARGES
                JSONArray preChargeJSONArray = new JSONArray();
                for (int i = 0; i < preChargesListArrayList.size(); i++) {
                    JSONObject preChargeJsonObject = new JSONObject();
                    preChargeJsonObject.put("id", preChargesListArrayList.get(i).getEcid());
                    preChargeJsonObject.put("value", preChargesListArrayList.get(i).getDiscountValue());
                    if (strExtensiveTaxFlag != null && strExtensiveTaxFlag.equals("1")) {
                        preChargeJsonObject.put("charges",
                                taxListArrayList.get(preChargesListArrayList.get(i).getSelectedIndex()).getButapid());
                    } else {
                        preChargeJsonObject.put("charges",
                                taxListArrayList.get(preChargesListArrayList.get(i).getSelectedIndex()).getButapid());
                    }
                    preChargeJSONArray.put(preChargeJsonObject);
                }

                //Array
                jsonObject.put("pre_charge", preChargeJSONArray);

                //TODO ADDITIONAL DISCOUNT
                JSONArray additionalDiscountJSONArray = new JSONArray();
                JSONObject additionalDiscountJsonObject = new JSONObject();
                if (strExtraDiscountId != null && !strExtraDiscountId.isEmpty()) {
                    additionalDiscountJsonObject.put("id", strExtraDiscountId);
                    additionalDiscountJsonObject.put("type", String.valueOf(additionalDiscountTypeSpinner.getSelectedItemPosition() + 1));
                    additionalDiscountJsonObject.put("discount", additionalEdittext.getText().toString().trim());
                    additionalDiscountJSONArray.put(additionalDiscountJsonObject);
                }


                //Array
                jsonObject.put("extra_discount", additionalDiscountJSONArray);

                //TODO TAX

                // Suraj told poonam to add this check
                if (materialTaxLayout.getVisibility() == View.VISIBLE) {
                    if (taxListArrayList.get(materialTaxChargeTypeSpinner.getSelectedItemPosition()).getButapid().equals("0")) {
                        jsonObject.put("material_tax", "");
                    } else {
                        jsonObject.put("material_tax", taxListArrayList.get
                                (materialTaxChargeTypeSpinner.getSelectedItemPosition()).getButapid());
                    }
                } else {
                    if (taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid().equals("0")) {
                        jsonObject.put("tax", "");
                    } else {
                        jsonObject.put("tax", taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid());
                    }
                }


                //TODO - For preview bit will  be 0
                jsonObject.put("bit", "0");

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
                jsonObject.put("charges", "");

            } catch (JSONException e) {
                e.printStackTrace();
                hideLoader();
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = apiService.sendJSONData(version, key, task, userId, accessToken, jsonObject);
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // leadList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse.getSuccess()) {
                            JSONObject jsonObject;
                            String strFrom = "";
                            String strTo = "";
                            String strSubject = "";
                            String strBody = "";
                            String strFileName = "";
                            String strMobile = "";
                            String strMessage = "";

                            jsonObject = new JSONObject(String.valueOf(new GsonBuilder().setPrettyPrinting()
                                    .create().toJson(response.body())));
                            boolean status = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");
                            String code = jsonObject.getString("result_code");

                            // String str
                            if (status) {
                                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                if (jsonObjectData.has("sms_data")) {
                                    JSONObject jsonObjectDataSms = jsonObjectData.getJSONObject("sms_data");
                                    if (jsonObjectDataSms.length() > 0) {
                                        strMobile = jsonObjectDataSms.getString("mobile");
                                        strMessage = jsonObjectDataSms.getString("message");
                                    }
                                }
                                if (jsonObjectData.has("email_Data")) {
                                    JSONObject jsonObjectDataEmail = jsonObjectData.getJSONObject("email_Data");
                                    if (jsonObjectDataEmail.length() > 0) {
                                        strFrom = jsonObjectDataEmail.getString("from");
                                        strTo = jsonObjectDataEmail.getString("to");
                                        strSubject = jsonObjectDataEmail.getString("subject");
                                        strBody = jsonObjectDataEmail.getString("body");
                                        strFileName = jsonObjectDataEmail.getString("filename");
                                    }
                                }

                                if (flag.equals("SMS")) {
                                    if (alertDialogSMSEMail == null || (alertDialogSMSEMail != null && !alertDialogSMSEMail.isShowing())) {
                                        dialogShowPreview(strMobile, strMessage, strFrom, strTo, "", strSubject, strBody, strFileName, flag);
                                    }
                                } else {
                                    if (alertDialogSMSEMail == null || (alertDialogSMSEMail != null && !alertDialogSMSEMail.isShowing())) {
                                        dialogShowPreview(strMobile, strMessage, strFrom, strTo, "", strSubject, strBody, strFileName, flag);
                                    }
                                }
                            }
                            //Deleted User
                            else if (code.equals(Constants.WRONG_CREDENTIALS) ||
                                    code.equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                if (EditOrderActivity.this != null) {
                                    Constants.logoutWrongCredentials(EditOrderActivity.this, message);
                                }
                            } else {
                                if (EditOrderActivity.this != null) {
                                    Toast.makeText(EditOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }   //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            if (EditOrderActivity.this != null) {
                                Constants.logoutWrongCredentials(EditOrderActivity.this, apiResponse.getMessage());
                            }
                        } else {
                            if (EditOrderActivity.this != null) {
                                Toast.makeText(EditOrderActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        hideLoader();

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (EditOrderActivity.this != null) {
                        Toast.makeText(EditOrderActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        hideLoader();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    //API Call to Edit a Order
    public void EditOrderAPI() {
        if (spinnerSmsTemplate.isShown()) {
            if (flag.equals("SMS")) {
                if (smsTemplateDataArrayList != null && smsTemplateDataArrayList.size() > 0) {
                    strSmstId = smsTemplateDataArrayList.get(spinnerSmsTemplate.getSelectedItemPosition()).getSmstid();
                }
                strEtId = "";
            }
        } else {
            strSmstId = "";
        }
        if (spinnerEmailTemplate.isShown()) {
            if (!flag.equals("SMS")) {
                if (emailTemplateArrArrayList != null && emailTemplateArrArrayList.size() > 0) {
                    strEtId = emailTemplateArrArrayList.get(spinnerEmailTemplate.getSelectedItemPosition()).getEtid();
                }
            }
        } else {
            strEtId = "";
        }

        task = getString(R.string.create_order_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            if (isConvertFromQuotation) {
                JSONObject jsonObjectQuotation = new JSONObject();
                jsonObjectQuotation.put("title", "quotation");
                jsonObjectQuotation.put("id", strQoId);

                jsonObject.put("id", jsonObjectQuotation);
            }
            jsonObject.put("order", strOrderId);
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
            jsonObject.put("ortemid", orderTemId);
            jsonObject.put("smstid", strSmstId);
            jsonObject.put("discount", strAdditionalDiscount);
            jsonObject.put("discount_type", String.valueOf(additionalDiscountTypeSpinner.getSelectedItemPosition() + 1));
            jsonObject.put("mail", strMail);
            jsonObject.put("sms", strSms);
            jsonObject.put("attributes", "");
            jsonObject.put("checkpoint_order_remarks", orderRemarksTextInputEditText.getText().toString());
            jsonObject.put("round", strRound);
            jsonObject.put("round_field", "0");
            jsonObject.put("payment-terms", paymentTermsSpinner.getSelectedItemPosition());
            if (paymentTermsSpinner.getSelectedItemPosition() == 3) {
                jsonObject.put("day", daysTextInputEditText.getText().toString() + "");
            } else {
                jsonObject.put("day", "");
            }

            //Advance payment
            if (advancePaymentCheckBoxTextView.isChecked()) {
                jsonObject.put("advance_payment", "1");
                if (paymentModeSpinner.getSelectedItemPosition() == 0) {
                    jsonObject.put("payment_mode", "");
                } else {
                    jsonObject.put("payment_mode", paymentModeDataList.get(paymentModeSpinner.getSelectedItemPosition())
                            .getCpoid());
                }
                //Payment Ledger Autocomplete
                jsonObject.put("payment", strPaymentLedgerId);
                //Amount's value
                jsonObject.put("advance_amount", advancePaymentAmountTextInputEditText.getText().toString());
            } else {
                jsonObject.put("payment_mode", "");
                jsonObject.put("advance_payment", "0");
                jsonObject.put("advance_amount", "");
            }
            //TODO - Payment Modes Attributes
            if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                JSONArray paymentModesAttributesJsonArray = new JSONArray();
                ArrayList<String> stringArray = new ArrayList<String>();

                for (int i = 0; i < paymentAttributesList.size(); i++) {
                    JSONObject paymentModesAttributesJsonObject = new JSONObject();

                    //TODO Updated on 8th June
                    if (paymentAttributesList.get(i).getName().toLowerCase().equals("date")) {
                        //Date Format
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String chequeDate = "";
                        if (paymentAttributesList.get(i).getValue().toString().trim() != null &&
                                !paymentAttributesList.get(i).getValue().toString().trim().isEmpty() &&
                                !paymentAttributesList.get(i).getValue().toString().trim().contains("0000")
                                && paymentAttributesList.get(i).getValue().toString().charAt(7) != '-') {
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = simpleDateFormat.parse(paymentAttributesList.get(i).getValue().toString());
                                paymentAttributesList.get(i).setValue(outputFormat.format(date).toString().trim());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    paymentModesAttributesJsonObject.put(paymentAttributesList.get(i).getCpoaid(),
                            paymentAttributesList.get(i).getValue());
                    //paymentModesAttributesJsonArray.put(paymentModesAttributesJsonObject);
                    stringArray.add(paymentModesAttributesJsonObject.toString());
                }
                jsonObject.put("payment_attrs", stringArray);
            } else {
                jsonObject.put("payment_attrs", "");
            }

            //Customer Sales Type
            if (customerSalesTypeSpinner.getSelectedItemPosition() != 0 &&
                    customerSalesTypeArrayList != null && customerSalesTypeArrayList.size() > 0) {
                jsonObject.put("custid", customerSalesTypeArrayList.get(customerSalesTypeSpinner.getSelectedItemPosition())
                        .getCustid());
            } else {
                jsonObject.put("custid", "");
            }

            //Sales Ledger Id
            jsonObject.put("alid", strSalesLedgerId);
            //orderType
            if (creditRadioBtn.isChecked()) {
                jsonObject.put("order_type", "1");
                jsonObject.put("percent-limit", "");
            } else {
                jsonObject.put("order_type", "2");
                jsonObject.put("percent-limit", "" + percentLimitTextInputEditText.getText().toString());
            }

            //Purchase Order Number & date
            jsonObject.put("order_no", purchaseOrderNoTextInputEditText.getText().toString());
            jsonObject.put("date", purchaseOrderDateTextInputEditText.getText().toString());
            jsonObject.put("due_date", "" + dueDateTextInputEditText.getText().toString());
            jsonObject.put("delivery_date", "" + deliveryDateTextInputEditText.getText().toString());
            jsonObject.put("create_date", selectDateTextInputEditText.getText().toString().trim());

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
                productsItemJsonObject.put("dealer_price", itemDataArrayList.get(i).getDealerPrice() + "");
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
                productsItemJsonObject.put("description", "");
                productsItemJsonObject.put("type", itemDataArrayList.get(i).getType());
                productsItemJsonObject.put("chkoiid", itemDataArrayList.get(i).getChkoiid());
                //TODO - Added on 19th April as static
                productsItemJsonObject.put("roundoff", "0");
                productsItemJsonObject.put("item_service", "0");
                productItemJsonArray.put(productsItemJsonObject);
            }

            //Array
            jsonObject.put("cart", productItemJsonArray);

            //TODO PRE-CHARGES
            JSONArray preChargeJSONArray = new JSONArray();
            for (int i = 0; i < preChargesListArrayList.size(); i++) {
                JSONObject preChargeJsonObject = new JSONObject();
                preChargeJsonObject.put("id", preChargesListArrayList.get(i).getEcid());
                preChargeJsonObject.put("value", preChargesListArrayList.get(i).getDiscountValue());
                if (strExtensiveTaxFlag != null && strExtensiveTaxFlag.equals("1")) {
                    preChargeJsonObject.put("charges",
                            taxListArrayList.get(preChargesListArrayList.get(i).getSelectedIndex()).getButapid());
                } else {
                    preChargeJsonObject.put("charges",
                            taxListArrayList.get(preChargesListArrayList.get(i).getSelectedIndex()).getButapid());
                }
                preChargeJSONArray.put(preChargeJsonObject);
            }

            //Array
            jsonObject.put("pre_charge", preChargeJSONArray);

            //TODO TAX

            // Suraj told poonam to add this check
            if (materialTaxLayout.getVisibility() == View.VISIBLE) {
                if (taxListArrayList.get(materialTaxChargeTypeSpinner.getSelectedItemPosition()).getButapid().equals("0")) {
                    jsonObject.put("material_tax", "");
                } else {
                    jsonObject.put("material_tax", taxListArrayList.get
                            (materialTaxChargeTypeSpinner.getSelectedItemPosition()).getButapid());
                }
            } else {
                if (taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid().equals("0")) {
                    jsonObject.put("tax", "");
                } else {
                    jsonObject.put("tax", taxListArrayList.get(taxChargeTypeSpinner.getSelectedItemPosition()).getButapid());
                }
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

            //TODO ADDITIONAL DISCOUNT
            JSONArray additionalDiscountJSONArray = new JSONArray();
            JSONObject additionalDiscountJsonObject = new JSONObject();
            if (strExtraDiscountId != null && !strExtraDiscountId.isEmpty()) {
                additionalDiscountJsonObject.put("id", strExtraDiscountId);
                additionalDiscountJsonObject.put("type", String.valueOf(additionalDiscountTypeSpinner.getSelectedItemPosition() + 1));
                additionalDiscountJsonObject.put("discount", additionalEdittext.getText().toString().trim());
                additionalDiscountJSONArray.put(additionalDiscountJsonObject);
            }


            //Array
            jsonObject.put("extra_discount", additionalDiscountJSONArray);

            //TODO - For preview bit will  be 1
            jsonObject.put("bit", "1");

            //TODO CHARGES
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 1; i < chargesList3ArrayList.size(); i++) {
                if (chargesList3ArrayList.get(i).isSelected()) {
                    list.add(chargesList3ArrayList.get(i).getEcid());
                }
            }

            //Array
            jsonObject.put("charge", new JSONArray(list));
            jsonObject.put("charges", "");

            Log.e("POST_JSON", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            hideLoader();
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.sendJSONData(version, key, task, userId, accessToken, jsonObject);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        JSONObject jsonObject;

                        jsonObject = new JSONObject(String.valueOf(new GsonBuilder().setPrettyPrinting()
                                .create().toJson(response.body())));
                        boolean status = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");
                        String code = jsonObject.getString("result_code");

                        // String str
                        if (status) {
                            if (isConvertFromQuotation) {
                                Toast.makeText(EditOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent(EditOrderActivity.this, DrawerActivity.class);
                                resultIntent.putExtra("manage_orders", "view_orders");
                                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(resultIntent);
                                finish();
                            } else {
                                Toast.makeText(EditOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        //Deleted User
                        else if (code.equals(Constants.WRONG_CREDENTIALS) ||
                                code.equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(EditOrderActivity.this, message);
                        } else {
                            Toast.makeText(EditOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }   //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        if (EditOrderActivity.this != null) {
                            Constants.logoutWrongCredentials(EditOrderActivity.this, apiResponse.getMessage());
                        }
                    } else {
                        Toast.makeText(EditOrderActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    hideLoader();

                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (EditOrderActivity.this != null) {
                    Toast.makeText(EditOrderActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });

    }

    private void hideLoader() {
        if (EditOrderActivity.this != null) {
            if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
                imageViewLoader.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (EditOrderActivity.this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (EditOrderActivity.this != null) {
                                if (imageViewLoader.getVisibility() == View.GONE) {
                                    imageViewLoader.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Ion.with(imageViewLoader)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void showLoaderInDialog(Dialog dialog) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (EditOrderActivity.this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (EditOrderActivity.this != null) {
                                if (imageViewLoaderProductsDialog.getVisibility() == View.GONE) {
                                    imageViewLoaderProductsDialog.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Ion.with(imageViewLoaderProductsDialog)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void hideLoaderInDialog(Dialog dialog) {
        if (EditOrderActivity.this != null) {
            if (imageViewLoaderProductsDialog != null && imageViewLoaderProductsDialog.getVisibility() == View.VISIBLE) {
                imageViewLoaderProductsDialog.setVisibility(View.GONE);
            }
            //Enable Touch Back
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
}
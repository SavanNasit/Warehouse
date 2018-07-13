package com.accrete.sixorbit.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.ChatMessage;
import com.accrete.sixorbit.model.Comment;
import com.accrete.sixorbit.model.Contact;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowUpFilter;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.LeadShippingAddress;
import com.accrete.sixorbit.model.Notification;
import com.accrete.sixorbit.model.Notify;
import com.accrete.sixorbit.model.OtpEmailFetch;
import com.accrete.sixorbit.model.OtpMobileFetch;
import com.accrete.sixorbit.model.Permission;
import com.accrete.sixorbit.model.PushNotifications;
import com.accrete.sixorbit.model.RecordFollowUp;
import com.accrete.sixorbit.model.SyncCheck;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 6;

    // Database Name
    private static final String DATABASE_NAME = "rapidKartFactory";

    // Follow Up's table name
    private static final String TABLE_FOLLOWUPS = "followUps";
    private static final String TABLE_RECORD_FOLLOWUPS = "recordFollowUps";
    private static final String TABLE_FILTER_FOLLOWUPS = "filterFollowUps";

    // Lead's table name
    private static final String TABLE_LEADS = "lead";
    private static final String TABLE_FOLLOWUP_CONTACTS = "followupContacts";
    private static final String TABLE_LEAD_ADDRESS = "leadAddress";

    // Contact Details table name
    private static final String TABLE_CONTACTS_DETAILS = "contactDetails";

    //  Activity feeds  table name
    private static final String TABLE_ACTIVITY_FEEDS = "activityFeeds";
    private static final String TABLE_ACTIVITY_FEEDS_COMMENT = "activityFeedsComments";

    // API sync table name
    private static final String TABLE_API_SYNC = "apiSync";

    // Assignee contacts table name
    private static final String TABLE_ASSIGNEE = "assignee";

    // Chats table name
    private static final String TABLE_CHAT_MESSAGES = "chatMessages";
    private static final String TABLE_CHAT_CONVERSATIONS = "chatUser";

    // Push notification table name
    private static final String TABLE_PUSH_NOTIFICATION = "pushNotification";

    // Enquiry table name
    private static final String TABLE_ENQUIRY = "enquiry";

    // Quotation table name
    private static final String TABLE_QUOTATION = "quotation";
    private static final String TABLE_QUOTATION_PRODUCT = "quotation_product";

    // Purchase Order table name
    private static final String TABLE_PURCHASE_ORDER = "purchaseOrder";

    // Customer table name
    private static final String TABLE_CUSTOMER = "customer";

    // Vendor table name
    private static final String TABLE_VENDOR = "vendor";

    //Activity Feeds Notifications Subscribed Modules
    private static final String TABLE_ACTIVITY_FEEDS_NOTIFICATION = "activity_feeds_notification";

    //Settings Activity OTP User emails
    private static final String TABLE_OTP_USER_EMAILS = "otp_user_emails";

    //Activity Feeds Notifications Subscribed Modules
    private static final String TABLE_OTP_USER_MOBILES = "otp_user_mobiles";

    //Pending Invoice Table name
    private static final String TABLE_PENDING_INVOICE = "pending_invoice";

    //Pending Invoice Filter Table name
    private static final String TABLE_FILTER_PENDING_INVOICE = "filter_pending_invoice";

    //User Permission Table
    private static final String TABLE_USER_PERMISSIONS = "user_permissions";

    private static final String TABLE_PREFERENCES_VALUE = "user_preferences_values";

    // Follow Ups Table Columns names
    private static final String KEY_PERSON_TYPE = "personType";
    private static final String KEY_ID = "id";
    private static final String KEY_ALERT_ON = "alert_on";
    private static final String KEY_COLOR = "color";
    private static final String KEY_CONTACT_PERSON = "contact_person";
    private static final String KEY_CUSTOMER_NAME = "customer_name";
    private static final String KEY_FOSID = "fosid";
    private static final String KEY_FOID = "foid";
    private static final String KEY_SCHEDULED_DATE = "scheduled_date";
    private static final String KEY_FOLLOW_TYPE_NAME = "follow_type_name";
    private static final String KEY_FOLLOW_TYPE = "follow_type";
    private static final String KEY_FOLLOW_TYPE_STATUS = "follow_type_status";
    private static final String KEY_FOLLOW_TYPE_STATUS_ID = "follow_type_status_id";
    private static final String KEY_TAKEN_ON = "taken_on";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_REASON = "reason";
    private static final String KEY_FEEDBACK = "feedback";
    private static final String KEY_FOLLOWUP_OUTCOME = "followup_outcome";
    private static final String KEY_COMMUNICATION_MODE = "communication_mode";
    private static final String KEY_ALERT_MODE = "alert_mode";
    private static final String KEY_ASSIGNED_USER = "assigned_user";
    private static final String KEY_UPDATED_USER = "updated_user";
    private static final String KEY_CREATED_USER = "created_user";
    private static final String KEY_CUID = "cuid";
    private static final String KEY_FOLLOWUP_SYNC_ID = "sync_id";
    private static final String KEY_FOLLOWUP_LEAD_ID = "leaid";
    private static final String KEY_CONTACT_EMAIL = "contact_person_email";
    private static final String KEY_CONTACT_MOBILE = "contact_person_mobile";
    private static final String KEY_FOLLOW_UP_CODE_ID = "codeid";
    private static final String KEY_FOLLOW_UP_COMM_ID = "commid";
    private static final String KEY_FOLLOW_UP_FOTID = "fotid";
    private static final String KEY_FOLLOW_UP_ASSIGNED_UID = "follow_up_assignee_uid";
    private static final String KEY_FOLLOW_UP_CONTACTED_PERSON = "follow_up_contacted_person";
    private static final String KEY_FOLLOW_UP_PARENT_ID = "follow_up_parent_id";
    private static final String KEY_LEAD_LOCAL_ID = "follow_up_lead_id";
    private static final String KEY_CONTACT_ID = "follow_up_contact_id";
    private static final String KEY_FOLLOW_UP_ENQUIRY_ID = "enid";
    private static final String KEY_FOLLOW_UP_QUOTATION_ID = "qoid";
    private static final String KEY_FOLLOW_UP_CUSTOMER_ORDER_ID = "chkoid";
    private static final String KEY_FOLLOW_UP_PURCHASE_ORDER_ID = "purorid";
    private static final String KEY_FOLLOW_VENDOR_ID = "venid";
    private static final String KEY_CUSTOMER_LOCAL_ID = "follow_up_customer_id";
    private static final String KEY_VENDOR_LOCAL_ID = "follow_up_vendor_id";
    private static final String KEY_FOLLOW_NUMBER = "followup_number";
    private static final String KEY_FOLLOW_FOID_REPRESENTATION = "followupId";
    private static final String KEY_FOLLOW_ENQUIRY_NUMBER = "enquiryNumber";
    private static final String KEY_FOLLOW_EOID_REPRESENTATION = "enquiryId";
    private static final String KEY_FOLLOW_QUID_REPRESENTATION = "quotationId";
    private static final String KEY_FOLLOW_QUOTATION_NUMBER = "quotationNumber";
    private static final String KEY_FOLLOW_SEQUENCE_NUMBER = "sequence_number";
    private static final String KEY_FOLLOW_ORDER_ID_REPRESENTATION = "orderIdRepresentation";
    private static final String KEY_FOLLOW_POID_REPRESENTATION = "purchaseOrderId";
    private static final String KEY_FOLLOW_PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
    private static final String KEY_FOLLOW_LEAID_REPRESENTATION = "leadIdR";
    private static final String KEY_FOLLOW_LEAD_NUMBER = "leadNumber";
    private static final String KEY_FOLLOWUP_QUOTATION_STATUS_ID = "quoSId";
    private static final String KEY_FOLLOWUP_ENQUIRY_STATUS_ID = "enSId";
    private static final String KEY_FOLLOWUP_CHKORDER_STATUS_ID = "chkOSId";
    private static final String KEY_FOLLOWUP_PURCHASE_ORDER_STATUS_ID = "purOSId";
    private static final String KEY_FOLLOWUP_LEAD_STATUS_ID = "leadSId";
    private static final String KEY_FOLLOWUP_PARENT_ID_REPRESENTATION = "follow_up_parent_id_representation";

    // Filter Follow Ups Table Columns names
    private static final String KEY_FILTER_ID = "id";
    private static final String KEY_TODAY = "today";
    private static final String KEY_YESTERDAY = "yesterday";
    private static final String KEY_TAKEN = "taken";
    private static final String KEY_PENDING = "pending";
    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";
    private static final String KEY_THIS_WEEK = "thisWeek";
    private static final String KEY_TYPE_LEAD = "lead";
    private static final String KEY_TYPE_ENQUIRY = "enquiry";
    private static final String KEY_TYPE_QUOTATION = "quotation";
    private static final String KEY_TYPE_PURCHASE_ORDER = "purchase_order";
    private static final String KEY_TYPE_SALES_ORDER = "sales_order";

    // Lead Table Columns names
    private static final String KEY_LEAD_ID = "id";
    private static final String KEY_LEAD = "leadid";
    private static final String KEY_OWNER_ID = "owner_id";
    private static final String KEY_LEATID = "leatid";
    private static final String KEY_LEAD_NAME = "name";
    private static final String KEY_LEAD_PERSON_TYPE = "lead_person_type";
    private static final String KEY_OFFICE_ADDRESS = "off_addr";
    private static final String KEY_WEBSITE = "website";
    private static final String KEY_LEAD_F_NAME = "first_name";
    private static final String KEY_LEAD_L_NAME = "last_name";
    private static final String KEY_LEAD_COMPANY_NAME = "company_name";
    private static final String KEY_LEAD_SYNC_ID = "lead_sync_id";
    private static final String KEY_GENDER_ID = "genderid";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ASSIGNED_UID = "assigned_uid";
    private static final String KEY_LEASID = "leasid";
    private static final String KEY_ENID = "enid";
    private static final String KEY_QOID = "qoid";
    private static final String KEY_CANCEL_REASON = "cancel_reason";
    private static final String KEY_SPECIAL_INSTRUCTIONS = "special_instructions";
    private static final String KEY_CREATED_UID = "cretaed_uid";
    private static final String KEY_UPDATED_UID = "updated_uid";
    private static final String KEY_CREATED_TS = "cretaed_ts";
    private static final String KEY_UPDATED_TS = "updated_ts";
    private static final String KEY_LEAD_SYNC = "sync_lead";
    private static final String KEY_LEAD_CODE_IDS = "contacts_code_ids";
    private static final String KEY_LEAD_NUMBER = "lead_number";
    private static final String KEY_LEAD_ID_REPRESENTATION = "lead_id_representation";

    //Lead Address Table Column names
    private static final String KEY_LEAD_ADDRESS_ID = "id";
    private static final String KEY_LEAD_ADDRESS_LEAID = "leaid";
    private static final String KEY_LEAD_ADDRESS_SAID = "said";
    private static final String KEY_LEAD_ADDRESS_F_NAME = "first_name";
    private static final String KEY_LEAD_ADDRESS_L_NAME = "last_name";
    private static final String KEY_LEAD_ADDRESS_LINE_ONE = "line1";
    private static final String KEY_LEAD_ADDRESS_LINE_TWO = "line2";
    private static final String KEY_LEAD_ADDRESS_COVER_ID = "coverid";
    private static final String KEY_LEAD_ADDRESS_STID = "stid";
    private static final String KEY_LEAD_ADDRESS_CTID = "ctid";
    private static final String KEY_LEAD_ADDRESS_ZIPCODE = "zipcode";
    private static final String KEY_LEAD_ADDRESS_MOBILE = "mobile";
    private static final String KEY_LEAD_ADDRESS_SATID = "satid";
    private static final String KEY_LEAD_ADDRESS_SASID = "sasid";
    private static final String KEY_LEAD_ADDRESS_CITY = "city";
    private static final String KEY_LEAD_ADDRESS_COUNTRY = "country";
    private static final String KEY_LEAD_ADDRESS_ISO_CODE = "iso_code";
    private static final String KEY_LEAD_ADDRESS_STATE = "state";
    private static final String KEY_LEAD_ADDRESS_STATE_CODE = "state_code";
    private static final String KEY_LEAD_ADDRESS_SYNC_ID = "lead_address_sync_id";
    private static final String KEY_LEAD_ADDRESS_SITE_NAME = "site_name";


    // Lead Contacts Table Columns Names
    private static final String KEY_LEAD_CONTACTS_KEY = "primary_key";
    private static final String KEY_LEAD_CONTACTS_LEAID = "leaid";
    private static final String KEY_LEAD_CONTACTS_CODE_ID = "codeid";
    private static final String KEY_LEAD_CONTACTS_NAME = "name";
    private static final String KEY_LEAD_CONTACTS_DESIGNATION = "designation";
    private static final String KEY_LEAD_CONTACTS_EMAIL = "email";
    private static final String KEY_LEAD_CONTACTS_PHONE_NUMBER = "phone_no";
    private static final String KEY_LEAD_CONTACTS_SYNC_ID = "lead_contacts_sync_id";
    private static final String KEY_LEAD_CONTACTS_CUID = "cuid";
    private static final String KEY_LEAD_CONTACTS_VENID = "venid";
    private static final String KEY_LEAD_CONTACTS_IS_OWNER = "isOwner";
    private static final String KEY_CONTACTS_QOID = "qoid";
    private static final String KEY_CONTACTS_ENID = "enid";
    private static final String KEY_CONTACTS_CUSTOMER_ORDER_ID = "customerOrderid";
    private static final String KEY_CONTACTS_PURCHASE_ORDER_ID = "purchaseOrderid";


    // API SYNC  Table Columns Names
    private static final String KEY_SYNC_ID = "id";
    private static final String KEY_SERVICE = "service";
    private static final String KEY_CALL_TIME = "callTime";

    // Assignee Spinner Contacts Table Columns Names
    private static final String KEY_ASSIGNEE_CONTACT_PRIMARY_KEY = "primary_id";
    private static final String KEY_ASSIGNEE_CONTACT_ID = "id";
    private static final String KEY_ASSIGNEE_CONTACT_NAME = "assigneeSpinnerName";
    private static final String KEY_ASSIGNE_MOBILE = "assigneSpinnerMobile";

    //Follow Up Contacts Table Columns Names
    private static final String KEY_CODE_ID = "codeid";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_PHONE_NO = "phone_no";

    //Chat Message Table Columns Names
    private static final String KEY_USER_ID = "uid";
    private static final String KEY_FIRST_NAME = "fname";
    private static final String KEY_LAST_NAME = "lname";
    private static final String KEY_USER_IMAGE = "image";
    private static final String KEY_USER_ONLINE_STATUS = "auser_online_status";
    private static final String KEY_CHAT_MESSAGE_ID = "chat_msgId";
    private static final String KEY_TEXT = "text";
    private static final String KEY_SYNC = "sync";
    private static final String KEY_READ_STATUS = "read_status";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_IMAGE_PATH = "imgPath";
    private static final String KEY_MSG_TYPE = "msgType";
    private static final String KEY_SYNC_CHAT_ID = "sync_chat_id";
    private static final String KEY_FILE_URL = "fileUrl";
    private static final String KEY_FILE_TYPE = "fileType";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_FILE_NAME = "fileName";

    //Record Up Follow Data Table Columns Names
    private static final String KEY_CHECKID = "chkoid";
    private static final String KEY_JOCAID = "jocaid";
    private static final String KEY_SCHEDULE_RADIO = "schedule_radio";
    private static final String KEY_SCHEDULE_TIME = "scheduled_time";
    private static final String KEY_ALERT_TIME = "alert_time";
    private static final String KEY_COMMUNICATED_MODE = "communicated_mode";
    private static final String KEY_OUTCOME = "outcome";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_CONTACTED_PERSON = "contacted_person";
    private static final String KEY_NEXT_FOLLOWUP = "schedule_next_followup";

    // Activity Feeds Table Columns Names
    private static final String KEY_ACTIVITY_FEEDS_PRIMARY_KEY = "af_primary_key";
    private static final String KEY_UAID = "uaid";
    private static final String KEY_UID = "uid";
    private static final String KEY_MID = "mid";
    private static final String KEY_MOTAID = "motaid";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_POST_CREATED_TS = "post_time";
    private static final String KEY_ICON = "icon";
    private static final String KEY_ICON_CODES = "icon_codes";
    private static final String KEY_COLOUR_CODE = "colour_code";
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_USER_DESIGNATION = "user_designation";
    private static final String KEY_AF_USER_IMAGE = "user_image";

    // Activity Feeds Comments Table Columns Names
    private static final String KEY_COMEMNTS_PRIMARY_KEY = "comments_primary_key";
    private static final String KEY_UACID = "uacid";
    private static final String KEY_SID = "sid";
    private static final String KEY_COMMENT_SYNC_ID = "sync_id";
    private static final String KEY_POST_UID = "post_uid";

    // Push Notification Table Columns Names
    private static final String KEY_PUSH_NOTIFICATION_ID = "id";
    private static final String KEY_PUSH_NOTIFICATION_FOID = "foid";
    private static final String KEY_PUSH_NOTIFICATION_LEAID = "leaid";
    private static final String KEY_PUSH_NOTIFICATION_MESSAGE = "message";
    private static final String KEY_PUSH_NOTIFICATION_TYPE = "type";
    private static final String KEY_PUSH_NOTIFICATION_SHOW = "show";
    private static final String KEY_PUSH_NOTIFICATION_UAID = "uaid";

    //Feeds Notifications Modules Columns Names
    private static final String KEY_FEEDS_NOTIFICATION_ID = "id";
    private static final String KEY_FEEDS_NOTIFICATION_UANID = "uanid";
    private static final String KEY_FEEDS_NOTIFICATION_UID = "uid";
    private static final String KEY_FEEDS_NOTIFICATION_MOTAID = "motaid";
    private static final String KEY_FEEDS_NOTIFICATION_MID = "mid";

    // Enquiry Table Columns Names
    private static final String KEY_ENQUIRY_ID = "id";
    private static final String KEY_ENQUIRY = "enoid";
    private static final String KEY_ENQUIRY_CUID = "cuid";

    // Quotation Table Columns Names
    private static final String KEY_QUOTATION_ID = "id";
    private static final String KEY_QUOTATION = "qoid";
    private static final String KEY_QUOTATION_CUID = "cuid";
    private static final String KEY_QUOTATION_LEADID = "leadid";
    private static final String KEY_QUOTATION_SAID = "said";
    private static final String KEY_QUOTATION_BAID = "baid";
    private static final String KEY_QUOTATION_ASSIGNED_ID = "assigned_id";
    private static final String KEY_QUOTATION_ENID = "enid";
    private static final String KEY_QUOTATION_QOSID = "qosid";
    private static final String KEY_QUOTATION_AMOUNT = "amount";
    private static final String KEY_QUOTATION_DISCOUNTED_AMOUNT = "discounted_amount";
    private static final String KEY_QUOTATION_DISCOUNT_TYPE = "discount_type";
    private static final String KEY_QUOTATION_BUTAPID = "butapid";
    private static final String KEY_QUOTATION_TAX_PERCENT = "tax_percent";
    private static final String KEY_QUOTATION_TAX_AMOUNT = "tax_amount";
    private static final String KEY_QUOTATION_TAXED_AMOUNT = "taxed_amount";
    private static final String KEY_QUOTATION_EXTRA_CHARGED_AMOUNT = "extra_charged_amount";
    private static final String KEY_QUOTATION_COMPLETE_AMOUNT = "complete_amount";
    private static final String KEY_QUOTATION_ROUND_OFF = "round_off";
    private static final String KEY_QUOTATION_PAYABLE_AMOUNT = "payable_amount";
    private static final String KEY_QUOTATION_CREATED_UID = "created_uid";
    private static final String KEY_QUOTATION_UPDATED_UID = "updated_uid";
    private static final String KEY_QUOTATION_CREATED_TS = "created_ts";
    private static final String KEY_QUOTATION_UPDATED_TS = "updated_ts";

    // Purchase Order Table Columns Names
    private static final String KEY_PURCHASE_ID = "id";
    private static final String KEY_PURCHASE = "purorid";
    private static final String KEY_PURCHASE_VENDOR_ID = "venid";

    // Customer Table Columns Names
    private static final String KEY_CUSTOMER_ID = "id";
    private static final String KEY_CUSTOMER = "cuid";
    private static final String KEY_CUSTOMER_FIRST_NAME = "fname";
    private static final String KEY_CUSTOMER_LAST_NAME = "lname";
    private static final String KEY_CUSTOMER_EMAIL = "email";
    private static final String KEY_CUSTOMER_MOBILE = "mobile";
    private static final String KEY_CUSTOMER_COMPANY_NAME = "company_name";
    private static final String KEY_CUSTOMER_WEBSITE = "website";

    // Vendor Table Columns Names
    private static final String KEY_VENDOR_ID = "id";
    private static final String KEY_VENDOR = "venid";
    private static final String KEY_VENDOR_NAME = "name";
    private static final String KEY_VENDOR_EMAIL = "vendor_email";
    private static final String KEY_VENDOR_MOBILE = "mobile";

    //User emails Table Columns Names
    private static final String KEY_EMAIL_USER_ID = "email_user_id";
    private static final String KEY_EMAIL_VERIFY_ID = "email_verify_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_EMAIL_CODE = "user_code";
    private static final String KEY_EMAIL_DATA = "user_data";
    private static final String KEY_EMAIL_VERIFY_STATUS_ID = "email_veriy_status_id";
    private static final String KEY_PRIMARY_EMAIL = "primry_email";
    private static final String KEY_EMAIL_UPDATED_TS = "email_updated_ts";
    private static final String KEY_EMAIL_CREATED_TS = "email_created_ts";


    //User mobile Table Columns Names
    private static final String KEY_MOBILE_USER_ID = "mobile_user_id";
    private static final String KEY_MOBILE_VERIFY_ID = "mobile_verify_id";
    private static final String KEY_MOBILE_VERIFY_STATUS_ID = "mobile_veriy_status_id";
    private static final String KEY_USER_MOBILE = "user_mobile";
    private static final String KEY_USER_MOBILE_IMEI = "user_mobile_imei";
    private static final String KEY_USER_MOBILE_GCM_ID = "user_mobile_gcm_id";
    private static final String KEY_USER_MOBILE_APP_VERSION = "user_mobile_app_version";
    private static final String KEY_MOBILE_CODE = "user_mobile_code";
    private static final String KEY_MOBILE_DATA = "user_mobile_data";
    private static final String KEY_PRIMARY_MOBILE = "primry_mobile";
    private static final String KEY_MOBILE_VERIFIED_TS = "mobile_verifyied_ts";
    private static final String KEY_MOBILE_CREATED_TS = "mobile_created_ts";

    //Quotation Product Table Columns Names
    private static final String KEY_QUOTATION_PRODUCT_ID = "id";
    private static final String KEY_QUOTATION_PRODUCT_QOPID = "qopid";
    private static final String KEY_QUOTATION_PRODUCT_QOID = "qoid";
    private static final String KEY_QUOTATION_PRODUCT_IID = "iid";
    private static final String KEY_QUOTATION_PRODUCT_ISVID = "isvid";
    private static final String KEY_QUOTATION_PRODUCT_VARIATION_NAME = "variation_name";
    private static final String KEY_QUOTATION_PRODUCT_QUANTITY = "quantity";
    private static final String KEY_QUOTATION_PRODUCT_PRICE = "price";
    private static final String KEY_QUOTATION_PRODUCT_SUBTOTAL = "sub_total";
    private static final String KEY_QUOTATION_PRODUCT_DISCOUNT = "discount";
    private static final String KEY_QUOTATION_PRODUCT_DISCOUNTED_AMOUNT = "discounted_amount";
    private static final String KEY_QUOTATION_PRODUCT_DISCOUNTED_TYPE = "discount_type";

    //Filter pending Invoice Column Names
    private static final String KEY_PENDING_INVOICE_FILTER_ID = "id";
    private static final String KEY_PENDING_INVOICE_FILTER_STARTDATE = "start_date";
    private static final String KEY_PENDING_INVOICE_FILTER_ENDDATE = "end_date";


    //Pending Invoice Column Names
    private static final String KEY_PENDING_INVOICE_ID = "id";
    private static final String KEY_PENDING_INVOICE_INVID = "invid";
    private static final String KEY_PENDING_INVOICE_INVOICE_NUMBER = "invoice_number";
    private static final String KEY_PENDING_INVOICE_PAYABLE_AMOUNT = "payable_amount";
    private static final String KEY_PENDING_INVOICE_PAID_AMOUNT = "paid_amount";
    private static final String KEY_PENDING_INVOICE_PAYMENT_STATUS = "payment_status";
    private static final String KEY_PENDING_INVOICE_PENDING_SINCE = "pending_since";
    private static final String KEY_PENDING_INVOICE_DATE = "date";


    //Permission Column Name
    private static final String KEY_TITLE = "title";
    private static final String KEY_PERMISSION_ID = "pid";

    private static DatabaseHandler mInstance = null;
    public Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public static DatabaseHandler getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }

        return mInstance;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Creating table for Follow Up
        String CREATE_FOLLOW_UPS_TABLE = "CREATE TABLE " + TABLE_FOLLOWUPS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_ALERT_ON + " TEXT,"
                + KEY_COLOR + " TEXT,"
                + KEY_CONTACT_PERSON + " Text,"
                + KEY_CUSTOMER_NAME + " Text,"
                + KEY_FOSID + " Text,"
                + KEY_FOID + " Text,"
                + KEY_SCHEDULED_DATE + " Text,"
                + KEY_FOLLOW_TYPE_NAME + " Text,"
                + KEY_FOLLOWUP_LEAD_ID + " Text,"
                + KEY_CUID + " Text,"
                + KEY_FOLLOW_TYPE + " Text,"
                + KEY_FOLLOW_TYPE_STATUS + " Text,"
                + KEY_FOLLOW_TYPE_STATUS_ID + " Text,"
                + KEY_TAKEN_ON + " Text,"
                + KEY_COMMENT + " Text,"
                + KEY_REASON + " Text,"
                + KEY_FEEDBACK + " Text,"
                + KEY_FOLLOWUP_OUTCOME + " Text,"
                + KEY_COMMUNICATION_MODE + " Text,"
                + KEY_ALERT_MODE + " Text,"
                + KEY_ASSIGNED_USER + " Text,"
                + KEY_CREATED_USER + " Text,"
                + KEY_UPDATED_USER + " Text,"
                + KEY_CREATED_TS + " Text,"
                + KEY_UPDATED_TS + " Text,"
                + KEY_PERSON_TYPE + " Text,"
                + KEY_FOLLOWUP_SYNC_ID + " Text,"
                + KEY_SYNC + " Text,"
                + KEY_CONTACT_MOBILE + " Text,"
                + KEY_CONTACT_EMAIL + " Text,"
                + KEY_FOLLOW_UP_CODE_ID + " Text,"
                + KEY_FOLLOW_UP_COMM_ID + " Text,"
                + KEY_FOLLOW_UP_FOTID + " Text,"
                + KEY_FOLLOW_UP_ASSIGNED_UID + " Text,"
                + KEY_FOLLOW_UP_CONTACTED_PERSON + " Text,"
                + KEY_FOLLOW_UP_PARENT_ID + " Text,"
                + KEY_LEAD_LOCAL_ID + " Text,"
                + KEY_CONTACT_ID + " Text,"
                + KEY_FOLLOW_UP_ENQUIRY_ID + " Text,"
                + KEY_FOLLOW_UP_QUOTATION_ID + " Text,"
                + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " Text,"
                + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " Text,"
                + KEY_FOLLOW_VENDOR_ID + " Text, "
                + KEY_FOLLOW_NUMBER + " Text,"
                + KEY_FOLLOW_FOID_REPRESENTATION + " Text,"
                + KEY_FOLLOW_ENQUIRY_NUMBER + " Text,"
                + KEY_FOLLOW_EOID_REPRESENTATION + " Text,"
                + KEY_FOLLOW_QUID_REPRESENTATION + " Text,"
                + KEY_FOLLOW_QUOTATION_NUMBER + " Text,"
                + KEY_FOLLOW_SEQUENCE_NUMBER + " Text,"
                + KEY_FOLLOW_ORDER_ID_REPRESENTATION + " Text,"
                + KEY_FOLLOW_POID_REPRESENTATION + " Text,"
                + KEY_FOLLOW_PURCHASE_ORDER_NUMBER + " Text,"
                + KEY_FOLLOW_LEAID_REPRESENTATION + " Text,"
                + KEY_FOLLOW_LEAD_NUMBER + " Text, "
                + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " Text,"
                + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " Text,"
                + KEY_FOLLOWUP_CHKORDER_STATUS_ID + " Text,"
                + KEY_FOLLOWUP_PURCHASE_ORDER_STATUS_ID + " Text,"
                + KEY_FOLLOWUP_LEAD_STATUS_ID + " Text,"
                + KEY_FOLLOWUP_PARENT_ID_REPRESENTATION + " Text "
                + ")";
        db.execSQL(CREATE_FOLLOW_UPS_TABLE);

        //Creating table for Filter Follow Up

        String CREATE_FILTER_FOLLOW_UPS_TABLE = "CREATE TABLE " + TABLE_FILTER_FOLLOWUPS + "("
                + KEY_FILTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_TODAY + " TEXT,"
                + KEY_YESTERDAY + " TEXT,"
                + KEY_TAKEN + " Text,"
                + KEY_PENDING + " Text,"
                + KEY_START_DATE + " Text,"
                + KEY_END_DATE + " Text,"
                + KEY_TYPE_LEAD + " Text,"
                + KEY_TYPE_ENQUIRY + " Text,"
                + KEY_TYPE_QUOTATION + " Text,"
                + KEY_TYPE_PURCHASE_ORDER + " Text,"
                + KEY_TYPE_SALES_ORDER + " Text,"
                + KEY_THIS_WEEK + " Text " + ")";
        db.execSQL(CREATE_FILTER_FOLLOW_UPS_TABLE);

        //Creating table for Record Follow Up
        String CREATE_RECORD_FOLLOWUP_TABLE = "CREATE TABLE " + TABLE_RECORD_FOLLOWUPS + "("
                + KEY_ASSIGNEE_CONTACT_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_FOID + " TEXT,"
                + KEY_FOLLOWUP_LEAD_ID + " TEXT,"
                + KEY_ENID + " TEXT," + KEY_QOID + " TEXT,"
                + KEY_CHECKID + " TEXT," + KEY_CUID + " TEXT,"
                + KEY_JOCAID + " TEXT," + KEY_SCHEDULE_RADIO + " TEXT,"
                + KEY_SCHEDULE_TIME + " TEXT," + KEY_ALERT_TIME + " TEXT,"
                + KEY_COMMUNICATED_MODE + " TEXT," + KEY_OUTCOME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT," + KEY_REASON + " TEXT,"
                + KEY_ASSIGNEE_CONTACT_NAME + " TEXT," + KEY_CONTACT_PERSON + " TEXT,"
                + KEY_ALERT_MODE + " TEXT," + KEY_COMMENT + " TEXT,"
                + KEY_CONTACTED_PERSON + " TEXT," + KEY_SYNC + " TEXT,"
                + KEY_COMMUNICATION_MODE + " TEXT,"
                + KEY_SYNC_ID + " TEXT,"
                + KEY_NEXT_FOLLOWUP + " TEXT" + ")";
        db.execSQL(CREATE_RECORD_FOLLOWUP_TABLE);

        //Creating table for  Follow Up Contacts

        String CREATE_FOLLOWUP_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FOLLOWUP_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_FOID + " Text,"
                + KEY_CODE_ID + " INTEGER  NOT NULL ,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_DESIGNATION + " Text,"
                + KEY_PHONE_NO + " Text" + ")";
        db.execSQL(CREATE_FOLLOWUP_CONTACTS_TABLE);

        //Creating table for Lead

        String CREATE_LEADS_TABLE = "CREATE TABLE " + TABLE_LEADS + "("
                + KEY_LEAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_LEAD + " TEXT,"
                + KEY_OWNER_ID + " TEXT,"
                + KEY_LEATID + " Text,"
                + KEY_LEAD_NAME + " Text,"
                + KEY_LEAD_PERSON_TYPE + " Text,"
                + KEY_OFFICE_ADDRESS + " Text,"
                + KEY_WEBSITE + " Text,"
                + KEY_LEAD_F_NAME + " Text,"
                + KEY_LEAD_L_NAME + " Text,"
                + KEY_LEAD_COMPANY_NAME + " Text,"
                + KEY_LEAD_SYNC_ID + " Text,"
                + KEY_GENDER_ID + " Text,"
                + KEY_MOBILE + " Text,"
                + KEY_EMAIL + " Text,"
                + KEY_ASSIGNED_UID + " Text,"
                + KEY_LEASID + " Text,"
                + KEY_ENID + " Text,"
                + KEY_QOID + " Text,"
                + KEY_CANCEL_REASON + " Text,"
                + KEY_SPECIAL_INSTRUCTIONS + " Text,"
                + KEY_CREATED_UID + " Text,"
                + KEY_UPDATED_UID + " Text,"
                + KEY_CREATED_TS + " Text,"
                + KEY_UPDATED_TS + " TEXT,"
                + KEY_LEAD_SYNC + " Text,"
                + KEY_LEAD_NUMBER + " Text,"
                + KEY_LEAD_ID_REPRESENTATION + " Text" + ")";
        db.execSQL(CREATE_LEADS_TABLE);

        //Creating table for contact details
        String CREATE_CONTACTS_DETAILS_TABLE = "CREATE TABLE " + TABLE_CONTACTS_DETAILS + "("
                + KEY_LEAD_CONTACTS_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_LEAD_CONTACTS_LEAID + " TEXT,"
                + KEY_LEAD_CONTACTS_CODE_ID + " TEXT,"
                + KEY_LEAD_CONTACTS_NAME + " TEXT,"
                + KEY_LEAD_CONTACTS_DESIGNATION + " Text,"
                + KEY_LEAD_CONTACTS_EMAIL + " Text,"
                + KEY_LEAD_CONTACTS_PHONE_NUMBER + " Text,"
                + KEY_LEAD_CONTACTS_SYNC_ID + " Text,"
                + KEY_LEAD_CONTACTS_CUID + " Text,"
                + KEY_LEAD_CONTACTS_VENID + " Text,"
                + KEY_LEAD_CONTACTS_IS_OWNER + " Text,"
                + KEY_CONTACTS_QOID + " Text,"
                + KEY_CONTACTS_ENID + " Text,"
                + KEY_CONTACTS_PURCHASE_ORDER_ID + " Text,"
                + KEY_CONTACTS_CUSTOMER_ORDER_ID + " Text" + ")";
        db.execSQL(CREATE_CONTACTS_DETAILS_TABLE);

        //Creating table for Lead Address
        String CREATE_LEADS_ADDRESS_TABLE = "CREATE TABLE " + TABLE_LEAD_ADDRESS + "("
                + KEY_LEAD_ADDRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_LEAD_ADDRESS_LEAID + " TEXT,"
                + KEY_LEAD_ADDRESS_SAID + " TEXT,"
                + KEY_LEAD_ADDRESS_F_NAME + " TEXT,"
                + KEY_LEAD_ADDRESS_L_NAME + " Text,"
                + KEY_LEAD_ADDRESS_LINE_ONE + " TEXT,"
                + KEY_LEAD_ADDRESS_LINE_TWO + " TEXT,"
                + KEY_LEAD_ADDRESS_COVER_ID + " Text,"
                + KEY_LEAD_ADDRESS_STID + " TEXT,"
                + KEY_LEAD_ADDRESS_CTID + " TEXT,"
                + KEY_LEAD_ADDRESS_ZIPCODE + " Text,"
                + KEY_LEAD_ADDRESS_MOBILE + " TEXT,"
                + KEY_LEAD_ADDRESS_SATID + " TEXT,"
                + KEY_LEAD_ADDRESS_SASID + " Text,"
                + KEY_LEAD_ADDRESS_CITY + " TEXT,"
                + KEY_LEAD_ADDRESS_COUNTRY + " TEXT,"
                + KEY_LEAD_ADDRESS_ISO_CODE + " Text,"
                + KEY_LEAD_ADDRESS_STATE + " TEXT,"
                + KEY_LEAD_ADDRESS_STATE_CODE + " TEXT,"
                + KEY_LEAD_ADDRESS_SYNC_ID + " TEXT,"
                + KEY_LEAD_ADDRESS_SITE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_LEADS_ADDRESS_TABLE);

        //Creating table for API Sync

        String CREATE_API_SYNC_TABLE = "CREATE TABLE " + TABLE_API_SYNC + "("
                + KEY_SYNC_ID + " INTEGER PRIMARY KEY,"
                + KEY_SERVICE + " TEXT,"
                + KEY_CALL_TIME + " Text" + ")";
        db.execSQL(CREATE_API_SYNC_TABLE);

        //Creating table for assignee spinner

        String CREATE_ASSIGNEE_SPINNER_TABLE = "CREATE TABLE " + TABLE_ASSIGNEE + "("
                + KEY_ASSIGNEE_CONTACT_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_ASSIGNEE_CONTACT_ID + " INTEGER NOT NULL,"
                + KEY_ASSIGNEE_CONTACT_NAME + " TEXT,"
                + KEY_ASSIGNE_MOBILE + " TEXT,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_USER_IMAGE + " TEXT,"
                + KEY_USER_ONLINE_STATUS + " TEXT,"
                + KEY_DESIGNATION + " TEXT" + ")";
        db.execSQL(CREATE_ASSIGNEE_SPINNER_TABLE);

        //Creating table for chats Messages.
        String CREATE_CHAT_MESSAGES_TABLE = "CREATE TABLE " + TABLE_CHAT_MESSAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_CHAT_MESSAGE_ID + " INTEGER,"
                + KEY_USER_ID + " INTEGER  NOT NULL,"
                + KEY_CREATED_AT + " TEXT,"
                + KEY_TEXT + " TEXT,"
                + KEY_MSG_TYPE + " TEXT,"
                + KEY_READ_STATUS + " TEXT,"
                + KEY_SYNC + " TEXT,"
                + KEY_SYNC_CHAT_ID + " TEXT,"
                + KEY_FILE_URL + " TEXT,"
                + KEY_FILE_TYPE + " TEXT,"
                + KEY_FILE_PATH + " TEXT,"
                + KEY_FILE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CHAT_MESSAGES_TABLE);

        //Creating table for Recent Chat List
        String CREATE_AUSER_TABLE = "CREATE TABLE " + TABLE_CHAT_CONVERSATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_USER_ID + " INTEGER ,"
                + KEY_CREATED_TS + " TEXT," + KEY_CHAT_MESSAGE_ID + " TEXT,"
                + KEY_TEXT + " TEXT" + ")";
        db.execSQL(CREATE_AUSER_TABLE);


        //Creating Table for Activity Feeds
        String CREATE_ACTIVITY_FEEDS_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_FEEDS + "("
                + KEY_ACTIVITY_FEEDS_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_UAID + " TEXT,"
                + KEY_UID + " TEXT,"
                + KEY_MID + " TEXT,"
                + KEY_MOTAID + " Text,"
                + KEY_MESSAGE + " Text,"
                + KEY_POST_CREATED_TS + " Text,"
                + KEY_ICON + " Text,"
                + KEY_ICON_CODES + " TEXT,"
                + KEY_COLOUR_CODE + " Text,"
                + KEY_USERNAME + " Text,"
                + KEY_USER_DESIGNATION + " Text,"
                + KEY_USER_IMAGE + " Text" + ")";
        db.execSQL(CREATE_ACTIVITY_FEEDS_TABLE);

        //Creating Table for comments of activity feeds
        String CREATE_COMMENTS_FEEDS_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_FEEDS_COMMENT + "("
                + KEY_COMEMNTS_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_UACID + " TEXT,"
                + KEY_UID + " TEXT,"
                + KEY_UAID + " TEXT,"
                + KEY_MESSAGE + " Text,"
                + KEY_POST_CREATED_TS + " Text,"
                + KEY_SID + " Text,"
                + KEY_COMMENT_SYNC_ID + " Text,"
                + KEY_SYNC + " Text,"
                + KEY_POST_UID + " Text" + ")";
        db.execSQL(CREATE_COMMENTS_FEEDS_TABLE);

        //Creating table for Push Notifications
        String CREATE_PUSH_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_PUSH_NOTIFICATION + "("
                + KEY_PUSH_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_PUSH_NOTIFICATION_FOID + " TEXT,"
                + KEY_PUSH_NOTIFICATION_LEAID + " TEXT,"
                + KEY_PUSH_NOTIFICATION_MESSAGE + " TEXT,"
                + KEY_PUSH_NOTIFICATION_TYPE + " TEXT,"
                + KEY_PUSH_NOTIFICATION_UAID + " TEXT,"
                + KEY_PUSH_NOTIFICATION_SHOW + " Text"
                + ")";
        db.execSQL(CREATE_PUSH_NOTIFICATION_TABLE);


        //Creating table for Enquiry
        String CREATE_ENQUIRY_TABLE = "CREATE TABLE " + TABLE_ENQUIRY + "("
                + KEY_ENQUIRY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ENQUIRY + " TEXT,"
                + KEY_ENQUIRY_CUID + " Text" + ")";
        db.execSQL(CREATE_ENQUIRY_TABLE);

        //Creating table for Quotation
        String CREATE_QUOTATION_TABLE = "CREATE TABLE " + TABLE_QUOTATION + "("
                + KEY_QUOTATION_ID + " INTEGER PRIMARY KEY,"
                + KEY_QUOTATION_CUID + " TEXT,"
                + KEY_QUOTATION_LEADID + " TEXT,"
                + KEY_QUOTATION_SAID + " TEXT,"
                + KEY_QUOTATION_BAID + " TEXT,"
                + KEY_QUOTATION_ASSIGNED_ID + " TEXT,"
                + KEY_QUOTATION_ENID + " TEXT,"
                + KEY_QUOTATION_QOSID + " TEXT,"
                + KEY_QUOTATION_AMOUNT + " TEXT,"
                + KEY_QUOTATION_DISCOUNTED_AMOUNT + " TEXT,"
                + KEY_QUOTATION_DISCOUNT_TYPE + " TEXT,"
                + KEY_QUOTATION_BUTAPID + " TEXT,"
                + KEY_QUOTATION_TAX_PERCENT + " TEXT,"
                + KEY_QUOTATION_TAX_AMOUNT + " TEXT,"
                + KEY_QUOTATION_TAXED_AMOUNT + " TEXT,"
                + KEY_QUOTATION_EXTRA_CHARGED_AMOUNT + " TEXT,"
                + KEY_QUOTATION_COMPLETE_AMOUNT + " TEXT,"
                + KEY_QUOTATION_ROUND_OFF + " TEXT,"
                + KEY_QUOTATION_PAYABLE_AMOUNT + " TEXT,"
                + KEY_QUOTATION_CREATED_UID + " TEXT,"
                + KEY_QUOTATION_UPDATED_UID + " TEXT,"
                + KEY_QUOTATION_CREATED_TS + " TEXT,"
                + KEY_QUOTATION_UPDATED_TS + " Text" + ")";
        db.execSQL(CREATE_QUOTATION_TABLE);

        //Creating table for Purchase Order
        String CREATE_PURCHASE_ORDER_TABLE = "CREATE TABLE " + TABLE_PURCHASE_ORDER + "("
                + KEY_PURCHASE_ID + " INTEGER PRIMARY KEY,"
                + KEY_PURCHASE + " TEXT,"
                + KEY_PURCHASE_VENDOR_ID + " Text" + ")";
        db.execSQL(CREATE_PURCHASE_ORDER_TABLE);

        //Creating table for Customer
        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + KEY_CUSTOMER_ID + " INTEGER PRIMARY KEY,"
                + KEY_CUSTOMER + " TEXT,"
                + KEY_CUSTOMER_FIRST_NAME + " TEXT,"
                + KEY_CUSTOMER_LAST_NAME + " TEXT,"
                + KEY_CUSTOMER_EMAIL + " TEXT,"
                + KEY_CUSTOMER_MOBILE + " TEXT,"
                + KEY_CUSTOMER_COMPANY_NAME + " TEXT,"
                + KEY_CUSTOMER_WEBSITE + " Text" + ")";
        db.execSQL(CREATE_CUSTOMER_TABLE);

        //Creating table for Vendor
        String CREATE_VENDOR_TABLE = "CREATE TABLE " + TABLE_VENDOR + "("
                + KEY_VENDOR_ID + " INTEGER PRIMARY KEY,"
                + KEY_VENDOR + " TEXT,"
                + KEY_VENDOR_NAME + " TEXT,"
                + KEY_VENDOR_EMAIL + " TEXT,"
                + KEY_VENDOR_MOBILE + " Text" + ")";
        db.execSQL(CREATE_VENDOR_TABLE);


        //Creating table for Feeds Notifications Modules
        String CREATE_FEEDS_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_FEEDS_NOTIFICATION + "("
                + KEY_FEEDS_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_FEEDS_NOTIFICATION_UANID + " TEXT,"
                + KEY_FEEDS_NOTIFICATION_UID + " TEXT,"
                + KEY_FEEDS_NOTIFICATION_MOTAID + " TEXT,"
                + KEY_FEEDS_NOTIFICATION_MID + " Text"
                + ")";
        db.execSQL(CREATE_FEEDS_NOTIFICATION_TABLE);


        //Creating table for OTP user Emails
        String CREATE_OTP_USERS_EMAILS_TABLE = "CREATE TABLE " + TABLE_OTP_USER_EMAILS + "("
                + KEY_EMAIL_USER_ID + " TEXT,"
                + KEY_EMAIL_VERIFY_ID + " TEXT,"
                + KEY_EMAIL_VERIFY_STATUS_ID + " TEXT,"
                + KEY_EMAIL_CODE + " TEXT,"
                + KEY_EMAIL_DATA + " TEXT,"
                + KEY_USER_EMAIL + " TEXT,"
                + KEY_PRIMARY_EMAIL + " TEXT,"
                + KEY_EMAIL_UPDATED_TS + " TEXT,"
                + KEY_EMAIL_CREATED_TS + " Text"
                + ")";
        db.execSQL(CREATE_OTP_USERS_EMAILS_TABLE);


        //Creating table for OTP user Mobiles
        String CREATE_OTP_USERS_MOBILE_TABLE = "CREATE TABLE " + TABLE_OTP_USER_MOBILES + "("
                + KEY_MOBILE_USER_ID + " TEXT,"
                + KEY_MOBILE_VERIFY_ID + " TEXT,"
                + KEY_MOBILE_VERIFY_STATUS_ID + " TEXT,"
                + KEY_USER_MOBILE + " TEXT,"
                + KEY_USER_MOBILE_IMEI + " TEXT,"
                + KEY_USER_MOBILE_GCM_ID + " TEXT,"
                + KEY_USER_MOBILE_APP_VERSION + " TEXT,"
                + KEY_MOBILE_CODE + " TEXT,"
                + KEY_MOBILE_DATA + " TEXT,"
                + KEY_PRIMARY_MOBILE + " TEXT,"
                + KEY_MOBILE_VERIFIED_TS + " TEXT,"
                + KEY_MOBILE_CREATED_TS + " Text"
                + ")";
        db.execSQL(CREATE_OTP_USERS_MOBILE_TABLE);

        //Creating Quotation Product Table
        String CREATE_QUOTATION_PRODUCT_TABLE = "CREATE TABLE " + TABLE_QUOTATION_PRODUCT + "("
                + KEY_QUOTATION_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_QUOTATION_PRODUCT_QOPID + " TEXT,"
                + KEY_QUOTATION_PRODUCT_QOID + " TEXT,"
                + KEY_QUOTATION_PRODUCT_IID + " TEXT,"
                + KEY_QUOTATION_PRODUCT_ISVID + " TEXT,"
                + KEY_QUOTATION_PRODUCT_VARIATION_NAME + " TEXT,"
                + KEY_QUOTATION_PRODUCT_QUANTITY + " TEXT,"
                + KEY_QUOTATION_PRODUCT_PRICE + " TEXT,"
                + KEY_QUOTATION_PRODUCT_SUBTOTAL + " TEXT,"
                + KEY_QUOTATION_PRODUCT_DISCOUNT + " TEXT,"
                + KEY_QUOTATION_PRODUCT_DISCOUNTED_AMOUNT + " TEXT,"
                + KEY_QUOTATION_PRODUCT_DISCOUNTED_TYPE + " Text"
                + ")";
        db.execSQL(CREATE_QUOTATION_PRODUCT_TABLE);


        //Creating table for Filter Pending Invoice
        String CREATE_FILTER_PENDING_INVOICE = "CREATE TABLE " + TABLE_FILTER_PENDING_INVOICE + "("
                + KEY_FILTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_PENDING_INVOICE_FILTER_STARTDATE + " TEXT,"
                + KEY_PENDING_INVOICE_FILTER_ENDDATE + " Text" + ")";
        db.execSQL(CREATE_FILTER_PENDING_INVOICE);

        //Creating table for Pending Invoice
        String CREATE_PENDING_INVOICE = "CREATE TABLE " + TABLE_PENDING_INVOICE + "("
                + KEY_PENDING_INVOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_PENDING_INVOICE_INVID + " TEXT,"
                + KEY_PENDING_INVOICE_INVOICE_NUMBER + " TEXT,"
                + KEY_PENDING_INVOICE_PAYABLE_AMOUNT + " TEXT,"
                + KEY_PENDING_INVOICE_PAID_AMOUNT + " TEXT,"
                + KEY_PENDING_INVOICE_PAYMENT_STATUS + " TEXT,"
                + KEY_PENDING_INVOICE_PENDING_SINCE + " TEXT,"
                + KEY_PENDING_INVOICE_DATE + " Text" + ")";
        db.execSQL(CREATE_PENDING_INVOICE);

        //Creating Permission table
        String CREATE_USER_PERMISSION = "CREATE TABLE " + TABLE_USER_PERMISSIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_PERMISSION_ID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_TITLE + " Text" + ")";
        db.execSQL(CREATE_USER_PERMISSION);
    }

    //Inserting Chat Messages
    public void insertChatMessages(ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHAT_MESSAGE_ID, chatMessage.getChatId());
        values.put(KEY_USER_ID, chatMessage.getUid());
        values.put(KEY_CREATED_AT, chatMessage.getCreatedTs());
        values.put(KEY_TEXT, chatMessage.getMessage());
        values.put(KEY_MSG_TYPE, chatMessage.getMsgType());
        values.put(KEY_SYNC_CHAT_ID, chatMessage.getSyncId());
        values.put(KEY_READ_STATUS, "");
        values.put(KEY_SYNC, "");
        values.put(KEY_FILE_TYPE, chatMessage.getFileType());
        values.put(KEY_FILE_URL, chatMessage.getFileUrl());
        values.put(KEY_FILE_PATH, chatMessage.getFilePath());
        values.put(KEY_FILE_NAME, chatMessage.getFileName());
        db.insert(TABLE_CHAT_MESSAGES, null, values);
        Log.d("TABLE_CHATS", "Insert" + chatMessage.getSyncId() + chatMessage.getFileUrl() + chatMessage.getFilePath());
        if (db != null && db.isOpen()) {
            //  db.close();
        }
    }

    //Get All non-sync Chat Messages
    public List<ChatMessage> getNonSyncChatMessages() {
        List<ChatMessage> chatHistory = new ArrayList<ChatMessage>();
        // Select query to get all messages
        String select = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_CHAT_MESSAGE_ID + "='" + "NULL" + "'";// + count + " ";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(select, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage();
                //chatMessage.setMe(true);
                chatMessage.setMessage(cursor.getString(4));
                chatMessage.setCreatedTs(cursor.getString(3));
                chatMessage.setUid(cursor.getString(2));
                chatMessage.setSyncId(cursor.getString(8));
                // Adding chat messages to list
                chatHistory.add(chatMessage);
            } while (cursor.moveToNext());
        }
        // return chat history list
        sqLiteDatabase.close();
        return chatHistory;
    }


    //Get All Chat Messages
    public List<ChatMessage> getChatHistory(int id, int start, int count) {
        List<ChatMessage> chatHistory = new ArrayList<ChatMessage>();
        // Select query to get all messages
        String select = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_USER_ID + "='" + id + "'"
                + " ORDER BY " + KEY_CREATED_AT + " DESC limit " + count + " OFFSET " + start;// + count + " ";
        // +  " ORDER BY " + KEY_CHAT_MESSAGE_ID + " DESC";//+ " ORDER BY " + KEY_ID + " ASC limit 10 ";//+ " LIMIT 2 ";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(select, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage();
                //chatMessage.setMe(true);
                chatMessage.setChatId(cursor.getString(1));
                chatMessage.setCreatedTs(cursor.getString(3));
                chatMessage.setMessage(cursor.getString(4));
                chatMessage.setMsgType(cursor.getString(5));
                chatMessage.setFileUrl(cursor.getString(9));
                chatMessage.setFileType(cursor.getString(10));
                chatMessage.setFilePath(cursor.getString(11));
                chatMessage.setFileName(cursor.getString(12));
                // Adding chat messages to list
                chatHistory.add(chatMessage);
            } while (cursor.moveToNext());
        }
        // return chat history list
        sqLiteDatabase.close();
        return chatHistory;
    }

    //Get All Chat Messages
    public ChatMessage getSingleChatmessage(String id) {
        List<ChatMessage> chatHistory = new ArrayList<ChatMessage>();
        // Select query to get all messages
        String select = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_CHAT_MESSAGE_ID + "='" + id + "'";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(select, null);
        ChatMessage chatMessage = null;
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                chatMessage = new ChatMessage();
                //chatMessage.setMe(true);
                chatMessage.setMessage(cursor.getString(4));
                chatMessage.setUid(cursor.getString(2));
                chatMessage.setCreatedTs(cursor.getString(3));
                chatMessage.setMsgType(cursor.getString(5));
                chatMessage.setFileUrl(cursor.getString(9));
                chatMessage.setFileType(cursor.getString(10));
                chatMessage.setFilePath(cursor.getString(11));
                chatMessage.setFileName(cursor.getString(12));
                // Adding chat messages to list
                chatHistory.add(chatMessage);
            } while (cursor.moveToNext());
        }
        // return chat history list
        sqLiteDatabase.close();
        return chatMessage;
    }


    //Get All Recent Chats
    public List<ChatMessage> getRecentChats() {
        List<ChatMessage> recentChats = new ArrayList<ChatMessage>();
        // Select query to get all messages
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_CHAT_MESSAGES + " GROUP BY " + KEY_USER_ID
                        + " ORDER BY " + KEY_CREATED_AT + " DESC "
                , new String[]{});
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(cursor.getString(4));
                chatMessage.setUid(cursor.getString(2));
                chatMessage.setCreatedTs(cursor.getString(3));
                chatMessage.setMsgType(cursor.getString(5));
                chatMessage.setFileUrl(cursor.getString(9));
                chatMessage.setFileType(cursor.getString(10));
                chatMessage.setFilePath(cursor.getString(11));
                chatMessage.setFileName(cursor.getString(12));
                recentChats.add(chatMessage);
            } while (cursor.moveToNext());
        }
        // return chat history list
        sqLiteDatabase.close();
        return recentChats;
    }

    //Check Sync Id is present or not
    public boolean checkSyncId(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_SYNC_CHAT_ID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    //Check Chat Id is present or not
    public boolean checkChatId(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_CHAT_MESSAGE_ID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
        if (cursor != null && !cursor.isClosed()) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
        }
        if (sqldb != null && sqldb.isOpen()) {
            //  sqldb.close();
        }
        return false;
    }

    //Update Chat Messages
    public void updateChatMessages(ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHAT_MESSAGE_ID, chatMessage.getChatId());
        values.put(KEY_TEXT, chatMessage.getMessage());
        values.put(KEY_MSG_TYPE, chatMessage.getMsgType());
        values.put(KEY_SYNC_CHAT_ID, chatMessage.getSyncId());
        values.put(KEY_CREATED_AT, chatMessage.getCreatedTs());
        values.put(KEY_FILE_TYPE, chatMessage.getFileType());
        values.put(KEY_FILE_URL, chatMessage.getFileUrl());
        values.put(KEY_FILE_NAME, chatMessage.getFileName());
        db.update(TABLE_CHAT_MESSAGES, values, KEY_SYNC_CHAT_ID + "=" + chatMessage.getSyncId(), null);
        Log.d("TABLE_CHAT_MESSAGES", chatMessage.getChatId() + "");
        db.close();
    }

    //Update Chat Messages with Chat Id
    public void updateChatMessagesWithChatId(ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHAT_MESSAGE_ID, chatMessage.getChatId());
        values.put(KEY_TEXT, chatMessage.getMessage());
        //  values.put(KEY_SYNC_CHAT_ID, chatMessage.getSyncId());
        values.put(KEY_MSG_TYPE, chatMessage.getMsgType());
        values.put(KEY_CREATED_AT, chatMessage.getCreatedTs());
        values.put(KEY_FILE_TYPE, chatMessage.getFileType());
        values.put(KEY_FILE_URL, chatMessage.getFileUrl());
        values.put(KEY_FILE_NAME, chatMessage.getFileName());
        db.update(TABLE_CHAT_MESSAGES, values, KEY_CHAT_MESSAGE_ID + "=" + chatMessage.getChatId(), null);

        if (db != null && db.isOpen()) {
            // db.close();
        }
    }

    //Update Path of Images/Files
    public void updatePathWithChatId(ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHAT_MESSAGE_ID, chatMessage.getChatId());
        values.put(KEY_FILE_PATH, chatMessage.getFilePath());
        values.put(KEY_CREATED_AT, chatMessage.getCreatedTs());
        values.put(KEY_FILE_TYPE, chatMessage.getFileType());
        values.put(KEY_FILE_URL, chatMessage.getFileUrl());
        db.update(TABLE_CHAT_MESSAGES, values, KEY_CHAT_MESSAGE_ID + "=" + chatMessage.getChatId(), null);

        if (db != null && db.isOpen()) {
            // db.close();
        }
    }

    //Get User Image
    public ChatContacts getUserData(int uID) {
        ChatContacts chatContacts = new ChatContacts();
        String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNEE + " WHERE " + KEY_ASSIGNEE_CONTACT_ID + "='" + uID + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            chatContacts.setImagePath(cursor.getString(4));
            chatContacts.setName(cursor.getString(2));
            chatContacts.setMobile(cursor.getString(3));
            chatContacts.setOnlineStatus(cursor.getString(6));
            chatContacts.setDesignation(cursor.getString(7));
        }
        db.close();
        return chatContacts;
    }


    public String getFollowUpUpdatedTs() {
        String updateTs = "";
        //String selectQuery = "SELECT id, MAX(Date(updated_ts)) FROM " + TABLE_FOLLOWUPS;
        String selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " ORDER BY " + KEY_UPDATED_TS + " DESC limit 1";
        //String selectQuery = "SELECT id FROM " + TABLE_FOLLOWUPS + " ORDER BY " + " Date(updated_ts) "+ " DESC limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                updateTs = cursor.getString(25);
            } while (cursor.moveToNext());
        }
        db.close();
        return updateTs;
    }

    public String getLeadUpdatedTs() {
        String updateTs = "";
        //String selectQuery = "SELECT id, MAX(Date(updated_ts)) FROM " + TABLE_FOLLOWUPS;
        String selectQuery = "SELECT * FROM " + TABLE_LEADS + " ORDER BY " + KEY_UPDATED_TS + " DESC limit 1";
        //String selectQuery = "SELECT id FROM " + TABLE_FOLLOWUPS + " ORDER BY " + " Date(updated_ts) "+ " DESC limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                updateTs = cursor.getString(23);
            } while (cursor.moveToNext());
        }
        db.close();
        return updateTs;
    }


    // Getting All followups
    public List<FollowUp> getAllfollowUps() {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();
        try {
            // Select All Query
            String selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS +
                    " ORDER BY " + KEY_ALERT_ON + " DESC ";
            // +", " + KEY_SCHEDULED_DATE + " ASC ";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    FollowUp followUp = new FollowUp();
                    followUp.setId(cursor.getInt(0));
                    followUp.setAlertOn(cursor.getString(1));
                    followUp.setColor(cursor.getInt(2));
                    followUp.setContactPerson(cursor.getString(3));
                    followUp.setName(cursor.getString(4));
                    followUp.setFosid(cursor.getString(5));
                    followUp.setFoid(cursor.getString(6));
                    followUp.setScheduledDate(cursor.getString(7));
                    followUp.setFollowupTypeName(cursor.getString(8));
                    followUp.setLeadId(cursor.getString(9));
                    followUp.setCuid(cursor.getString(10));
                    followUp.setFollowupType(cursor.getString(11));
                    followUp.setFollowupTypeStatus(cursor.getString(12));
                    followUp.setFollowupTypeStatusId(cursor.getString(13));
                    followUp.setTakenOn(cursor.getString(14));
                    followUp.setComment(cursor.getString(15));
                    followUp.setReason(cursor.getString(16));
                    followUp.setFeedback(cursor.getString(17));
                    followUp.setFollowupOutcome(cursor.getString(18));
                    followUp.setFollowupCommunicationMode(cursor.getString(19));
                    followUp.setAlertMode(cursor.getString(20));
                    followUp.setAssigned_user(cursor.getString(21));
                    followUp.setCreatedUser(cursor.getString(22));
                    followUp.setUpdatedUser(cursor.getString(23));
                    followUp.setCreatedTs(cursor.getString(24));
                    followUp.setUpdatedTs(cursor.getString(25));
                    followUp.setPerson_type(cursor.getString(26));
                    followUp.setSyncId(cursor.getString(27));
                    followUp.setSyncStatus(cursor.getString(28));
                    followUp.setContactPersonMobile(cursor.getString(29));
                    followUp.setContactPersonEmail(cursor.getString(30));
                    followUp.setCodeid(cursor.getString(31));
                    followUp.setCommid(cursor.getString(32));
                    followUp.setFotid(cursor.getString(33));
                    followUp.setAssignedUid(cursor.getString(34));
                    followUp.setContactedPerson(cursor.getString(35));
                    followUp.setParentId(cursor.getString(36));
                    followUp.setLeadLocalId(cursor.getString(37));
                    followUp.setContactsId(cursor.getString(38));
                    followUp.setEnid(cursor.getString(39));
                    followUp.setQoid(cursor.getString(40));
                    followUp.setChkoid(cursor.getString(41));
                    followUp.setPurorid(cursor.getString(42));
                    followUp.setVenid(cursor.getString(43));
                    followUp.setFollowupNumber(cursor.getString(44));
                    followUp.setFollowupId(cursor.getString(45));
                    followUp.setEnquiryNumber(cursor.getString(46));
                    followUp.setEnquiryId(cursor.getString(47));
                    followUp.setQuotationId(cursor.getString(48));
                    followUp.setQuotationNumber(cursor.getString(49));
                    followUp.setOrderSequenceNumber(cursor.getString(50));
                    followUp.setOrderId(cursor.getString(51));
                    followUp.setPurchaseOrderId(cursor.getString(52));
                    followUp.setPurchaseOrderNumber(cursor.getString(53));
                    followUp.setLeadIdR(cursor.getString(54));
                    followUp.setLeadNumber(cursor.getString(55));
                    //TODO - Added on 19th April
                    followUp.setQosid(cursor.getString(56));
                    followUp.setEnsid(cursor.getString(57));
                    followUp.setChkosid(cursor.getString(58));
                    followUp.setPurorsid(cursor.getString(59));
                    followUp.setLeasid(cursor.getString(60));
                    //TODO - Added on 10th May
                    followUp.setParentFollowupId(cursor.getString(61));
                    // Adding followup to list
                    followUpList.add(followUp);
                } while (cursor.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return followUps list
        return followUpList;
    }

    public boolean checkLeadIdInLead(String iD) {
        try {
            SQLiteDatabase sqldb = this.getWritableDatabase();
            String Query = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEAD + "=?";
            Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
            boolean exists = false;
            try {
                exists = cursor.moveToFirst();
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exists;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkSyncIdInLead(String iD) {
        try {
            SQLiteDatabase sqldb = this.getWritableDatabase();
            String Query = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEAD_SYNC_ID + "=?";
            Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
            boolean exists = false;
            try {
                if (sqldb.isOpen() && cursor != null) {
                    exists = cursor.moveToFirst();
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exists;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Getting All followups
    public List<FollowUp> getLeadfollowUps(String leadId) {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_LEAD_ID
                + "='" + leadId + "'" + " ORDER BY " + KEY_FOSID + " DESC ,"
                + KEY_SCHEDULED_DATE + " ASC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FollowUp followUp = new FollowUp();
                followUp.setAlertOn(cursor.getString(1));
                followUp.setColor(cursor.getInt(2));
                followUp.setContactPerson(cursor.getString(3));
                followUp.setName(cursor.getString(4));
                followUp.setFosid(cursor.getString(5));
                followUp.setFoid(cursor.getString(6));
                followUp.setScheduledDate(cursor.getString(7));
                followUp.setFollowupTypeName(cursor.getString(8));
                followUp.setLeadId(cursor.getString(9));
                followUp.setCuid(cursor.getString(10));
                followUp.setFollowupType(cursor.getString(11));
                followUp.setFollowupTypeStatus(cursor.getString(12));
                followUp.setFollowupTypeStatusId(cursor.getString(13));
                followUp.setTakenOn(cursor.getString(14));
                followUp.setComment(cursor.getString(15));
                followUp.setReason(cursor.getString(16));
                followUp.setFeedback(cursor.getString(17));
                followUp.setFollowupOutcome(cursor.getString(18));
                followUp.setFollowupCommunicationMode(cursor.getString(19));
                followUp.setAlertMode(cursor.getString(20));
                followUp.setAssigned_user(cursor.getString(21));
                followUp.setCreatedUser(cursor.getString(22));
                followUp.setUpdatedUser(cursor.getString(23));
                followUp.setCreatedTs(cursor.getString(24));
                followUp.setUpdatedTs(cursor.getString(25));
                followUp.setPerson_type(cursor.getString(26));
                followUp.setSyncId(cursor.getString(27));
                followUp.setSyncStatus(cursor.getString(28));
                followUp.setContactPersonMobile(cursor.getString(29));
                followUp.setContactPersonEmail(cursor.getString(30));
                followUp.setCodeid(cursor.getString(31));
                followUp.setCommid(cursor.getString(32));
                followUp.setFotid(cursor.getString(33));
                followUp.setAssignedUid(cursor.getString(34));
                followUp.setContactedPerson(cursor.getString(35));
                followUp.setParentId(cursor.getString(36));
                followUp.setLeadLocalId(cursor.getString(37));
                followUp.setContactsId(cursor.getString(38));
                followUp.setEnid(cursor.getString(39));
                followUp.setQoid(cursor.getString(40));
                followUp.setChkoid(cursor.getString(41));
                followUp.setPurorid(cursor.getString(42));
                followUp.setVenid(cursor.getString(43));
                followUp.setFollowupNumber(cursor.getString(44));
                followUp.setFollowupId(cursor.getString(45));
                followUp.setEnquiryNumber(cursor.getString(46));
                followUp.setEnquiryId(cursor.getString(47));
                followUp.setQuotationId(cursor.getString(48));
                followUp.setQuotationNumber(cursor.getString(49));
                followUp.setOrderSequenceNumber(cursor.getString(50));
                followUp.setOrderId(cursor.getString(51));
                followUp.setPurchaseOrderId(cursor.getString(52));
                followUp.setPurchaseOrderNumber(cursor.getString(53));
                followUp.setLeadIdR(cursor.getString(54));
                followUp.setLeadNumber(cursor.getString(55));
                //TODO - Added on 19th April
                followUp.setQosid(cursor.getString(56));
                followUp.setEnsid(cursor.getString(57));
                followUp.setChkosid(cursor.getString(58));
                followUp.setPurorsid(cursor.getString(59));
                followUp.setLeasid(cursor.getString(60));
                //TODO - Added on 10th May
                followUp.setParentFollowupId(cursor.getString(61));
                // Adding followup to list
                followUpList.add(followUp);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return followUpList;
    }


    //TODO - Updated On 28th march
    // Getting All followups of Quotation
    public List<FollowUp> getQuotationFollowUps(String qoId) {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOW_UP_QUOTATION_ID + "='" + qoId + "'"
                + " ORDER BY " + KEY_FOSID + " ASC ,"
                + KEY_SCHEDULED_DATE + " DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FollowUp followUp = new FollowUp();
                followUp.setAlertOn(cursor.getString(1));
                followUp.setColor(cursor.getInt(2));
                followUp.setContactPerson(cursor.getString(3));
                followUp.setName(cursor.getString(4));
                followUp.setFosid(cursor.getString(5));
                followUp.setFoid(cursor.getString(6));
                followUp.setScheduledDate(cursor.getString(7));
                followUp.setFollowupTypeName(cursor.getString(8));
                followUp.setLeadId(cursor.getString(9));
                followUp.setCuid(cursor.getString(10));
                followUp.setFollowupType(cursor.getString(11));
                followUp.setFollowupTypeStatus(cursor.getString(12));
                followUp.setFollowupTypeStatusId(cursor.getString(13));
                followUp.setTakenOn(cursor.getString(14));
                followUp.setComment(cursor.getString(15));
                followUp.setReason(cursor.getString(16));
                followUp.setFeedback(cursor.getString(17));
                followUp.setFollowupOutcome(cursor.getString(18));
                followUp.setFollowupCommunicationMode(cursor.getString(19));
                followUp.setAlertMode(cursor.getString(20));
                followUp.setAssigned_user(cursor.getString(21));
                followUp.setCreatedUser(cursor.getString(22));
                followUp.setUpdatedUser(cursor.getString(23));
                followUp.setCreatedTs(cursor.getString(24));
                followUp.setUpdatedTs(cursor.getString(25));
                followUp.setPerson_type(cursor.getString(26));
                followUp.setSyncId(cursor.getString(27));
                followUp.setSyncStatus(cursor.getString(28));
                followUp.setContactPersonMobile(cursor.getString(29));
                followUp.setContactPersonEmail(cursor.getString(30));
                followUp.setCodeid(cursor.getString(31));
                followUp.setCommid(cursor.getString(32));
                followUp.setFotid(cursor.getString(33));
                followUp.setAssignedUid(cursor.getString(34));
                followUp.setContactedPerson(cursor.getString(35));
                followUp.setParentId(cursor.getString(36));
                followUp.setLeadLocalId(cursor.getString(37));
                followUp.setContactsId(cursor.getString(38));
                followUp.setEnid(cursor.getString(39));
                followUp.setQoid(cursor.getString(40));
                followUp.setChkoid(cursor.getString(41));
                followUp.setPurorid(cursor.getString(42));
                followUp.setVenid(cursor.getString(43));
                followUp.setFollowupNumber(cursor.getString(44));
                followUp.setFollowupId(cursor.getString(45));
                followUp.setEnquiryNumber(cursor.getString(46));
                followUp.setEnquiryId(cursor.getString(47));
                followUp.setQuotationId(cursor.getString(48));
                followUp.setQuotationNumber(cursor.getString(49));
                followUp.setOrderSequenceNumber(cursor.getString(50));
                followUp.setOrderId(cursor.getString(51));
                followUp.setPurchaseOrderId(cursor.getString(52));
                followUp.setPurchaseOrderNumber(cursor.getString(53));
                followUp.setLeadIdR(cursor.getString(54));
                followUp.setLeadNumber(cursor.getString(55));
                //TODO - Added on 19th April
                followUp.setQosid(cursor.getString(56));
                followUp.setEnsid(cursor.getString(57));
                followUp.setChkosid(cursor.getString(58));
                followUp.setPurorsid(cursor.getString(59));
                followUp.setLeasid(cursor.getString(60));
                //TODO - Added on 10th May
                followUp.setParentFollowupId(cursor.getString(61));
                // Adding followup to list
                followUpList.add(followUp);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return followUpList;
    }

    //TODO - Updated On 4th April
    // Getting All followups of Order
    public List<FollowUp> getOrderFollowUps(String orderId) {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " +
                KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + "='" + orderId + "'"
                + " ORDER BY " + KEY_FOSID + " ASC ,"
                + KEY_SCHEDULED_DATE + " DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FollowUp followUp = new FollowUp();
                followUp.setAlertOn(cursor.getString(1));
                followUp.setColor(cursor.getInt(2));
                followUp.setContactPerson(cursor.getString(3));
                followUp.setName(cursor.getString(4));
                followUp.setFosid(cursor.getString(5));
                followUp.setFoid(cursor.getString(6));
                followUp.setScheduledDate(cursor.getString(7));
                followUp.setFollowupTypeName(cursor.getString(8));
                followUp.setLeadId(cursor.getString(9));
                followUp.setCuid(cursor.getString(10));
                followUp.setFollowupType(cursor.getString(11));
                followUp.setFollowupTypeStatus(cursor.getString(12));
                followUp.setFollowupTypeStatusId(cursor.getString(13));
                followUp.setTakenOn(cursor.getString(14));
                followUp.setComment(cursor.getString(15));
                followUp.setReason(cursor.getString(16));
                followUp.setFeedback(cursor.getString(17));
                followUp.setFollowupOutcome(cursor.getString(18));
                followUp.setFollowupCommunicationMode(cursor.getString(19));
                followUp.setAlertMode(cursor.getString(20));
                followUp.setAssigned_user(cursor.getString(21));
                followUp.setCreatedUser(cursor.getString(22));
                followUp.setUpdatedUser(cursor.getString(23));
                followUp.setCreatedTs(cursor.getString(24));
                followUp.setUpdatedTs(cursor.getString(25));
                followUp.setPerson_type(cursor.getString(26));
                followUp.setSyncId(cursor.getString(27));
                followUp.setSyncStatus(cursor.getString(28));
                followUp.setContactPersonMobile(cursor.getString(29));
                followUp.setContactPersonEmail(cursor.getString(30));
                followUp.setCodeid(cursor.getString(31));
                followUp.setCommid(cursor.getString(32));
                followUp.setFotid(cursor.getString(33));
                followUp.setAssignedUid(cursor.getString(34));
                followUp.setContactedPerson(cursor.getString(35));
                followUp.setParentId(cursor.getString(36));
                followUp.setLeadLocalId(cursor.getString(37));
                followUp.setContactsId(cursor.getString(38));
                followUp.setEnid(cursor.getString(39));
                followUp.setQoid(cursor.getString(40));
                followUp.setChkoid(cursor.getString(41));
                followUp.setPurorid(cursor.getString(42));
                followUp.setVenid(cursor.getString(43));
                followUp.setFollowupNumber(cursor.getString(44));
                followUp.setFollowupId(cursor.getString(45));
                followUp.setEnquiryNumber(cursor.getString(46));
                followUp.setEnquiryId(cursor.getString(47));
                followUp.setQuotationId(cursor.getString(48));
                followUp.setQuotationNumber(cursor.getString(49));
                followUp.setOrderSequenceNumber(cursor.getString(50));
                followUp.setOrderId(cursor.getString(51));
                followUp.setPurchaseOrderId(cursor.getString(52));
                followUp.setPurchaseOrderNumber(cursor.getString(53));
                followUp.setLeadIdR(cursor.getString(54));
                followUp.setLeadNumber(cursor.getString(55));
                //TODO - Added on 19th April
                followUp.setQosid(cursor.getString(56));
                followUp.setEnsid(cursor.getString(57));
                followUp.setChkosid(cursor.getString(58));
                followUp.setPurorsid(cursor.getString(59));
                followUp.setLeasid(cursor.getString(60));
                //TODO - Added on 10th May
                followUp.setParentFollowupId(cursor.getString(61));
                // Adding followup to list
                followUpList.add(followUp);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return followUpList;
    }


    //add filter

    // Updating single followUp
    public void updateFollowUp(FollowUp followUp) {
        try {
            SQLiteDatabase db = null;
            if (db == null || !db.isOpen()) {
                db = this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_ALERT_ON, followUp.getAlertOn());
                contentValues.put(KEY_COLOR, followUp.getColor());
                contentValues.put(KEY_CONTACT_PERSON, followUp.getContactPerson());
                contentValues.put(KEY_CUSTOMER_NAME, followUp.getName());
                contentValues.put(KEY_FOSID, followUp.getFosid());
                contentValues.put(KEY_FOID, followUp.getFoid());
                contentValues.put(KEY_SCHEDULED_DATE, followUp.getScheduledDate());
                contentValues.put(KEY_FOLLOW_TYPE_NAME, followUp.getType());
                contentValues.put(KEY_FOLLOWUP_LEAD_ID, followUp.getLeadId());
                contentValues.put(KEY_CUID, followUp.getCuid());
                contentValues.put(KEY_FOLLOW_TYPE, followUp.getFollowupType());
                contentValues.put(KEY_FOLLOW_TYPE_STATUS, followUp.getFollowupTypeStatus());
                contentValues.put(KEY_FOLLOW_TYPE_STATUS_ID, followUp.getFollowupTypeStatusId());
                contentValues.put(KEY_TAKEN_ON, followUp.getTakenOn());
                contentValues.put(KEY_COMMENT, followUp.getComment());
                contentValues.put(KEY_REASON, followUp.getReason());
                contentValues.put(KEY_FEEDBACK, followUp.getFeedback());
                contentValues.put(KEY_FOLLOWUP_OUTCOME, followUp.getFollowupOutcome());
                contentValues.put(KEY_COMMUNICATION_MODE, followUp.getFollowupCommunicationMode());
                contentValues.put(KEY_ALERT_MODE, followUp.getAlertMode());
                contentValues.put(KEY_ASSIGNED_USER, followUp.getAssignedUser());
                contentValues.put(KEY_CREATED_USER, followUp.getCreatedUser());
                contentValues.put(KEY_UPDATED_USER, followUp.getUpdatedUser());
                contentValues.put(KEY_CREATED_TS, followUp.getCreatedTs());
                contentValues.put(KEY_UPDATED_TS, followUp.getUpdatedTs());
                contentValues.put(KEY_PERSON_TYPE, followUp.getPerson_type());
                contentValues.put(KEY_FOLLOWUP_SYNC_ID, followUp.getSyncId());
                contentValues.put(KEY_SYNC, followUp.getSyncStatus());
                contentValues.put(KEY_CONTACT_MOBILE, followUp.getContactPersonMobile());
                contentValues.put(KEY_CONTACT_EMAIL, followUp.getContactPersonEmail());
                contentValues.put(KEY_FOLLOW_UP_CODE_ID, followUp.getCodeid());
                contentValues.put(KEY_FOLLOW_UP_COMM_ID, followUp.getCommid());
                contentValues.put(KEY_FOLLOW_UP_FOTID, followUp.getFotid());
                contentValues.put(KEY_FOLLOW_UP_ASSIGNED_UID, followUp.getAssignedUid());
                contentValues.put(KEY_FOLLOW_UP_CONTACTED_PERSON, followUp.getContactedPerson());
                contentValues.put(KEY_FOLLOW_UP_PARENT_ID, followUp.getParentId());
                contentValues.put(KEY_LEAD_LOCAL_ID, followUp.getLeadLocalId());
                contentValues.put(KEY_CONTACT_ID, followUp.getContactsId());
                contentValues.put(KEY_FOLLOW_UP_ENQUIRY_ID, followUp.getEnid());
                contentValues.put(KEY_FOLLOW_UP_QUOTATION_ID, followUp.getQoid());
                contentValues.put(KEY_FOLLOW_UP_CUSTOMER_ORDER_ID, followUp.getChkoid());
                contentValues.put(KEY_FOLLOW_UP_PURCHASE_ORDER_ID, followUp.getPurorid());
                contentValues.put(KEY_FOLLOW_VENDOR_ID, followUp.getVenid());
                contentValues.put(KEY_FOLLOW_NUMBER, followUp.getFollowupNumber());
                contentValues.put(KEY_FOLLOW_FOID_REPRESENTATION, followUp.getFollowupId());
                contentValues.put(KEY_FOLLOW_ENQUIRY_NUMBER, followUp.getEnquiryNumber());
                contentValues.put(KEY_FOLLOW_EOID_REPRESENTATION, followUp.getEnquiryId());
                contentValues.put(KEY_FOLLOW_QUID_REPRESENTATION, followUp.getQuotationId());
                contentValues.put(KEY_FOLLOW_QUOTATION_NUMBER, followUp.getQuotationNumber());
                contentValues.put(KEY_FOLLOW_SEQUENCE_NUMBER, followUp.getOrderSequenceNumber());
                contentValues.put(KEY_FOLLOW_ORDER_ID_REPRESENTATION, followUp.getOrderId());
                contentValues.put(KEY_FOLLOW_POID_REPRESENTATION, followUp.getPurchaseOrderId());
                contentValues.put(KEY_FOLLOW_PURCHASE_ORDER_NUMBER, followUp.getPurchaseOrderNumber());
                contentValues.put(KEY_FOLLOW_LEAID_REPRESENTATION, followUp.getLeadIdR());
                contentValues.put(KEY_FOLLOW_LEAD_NUMBER, followUp.getLeadNumber());
                //TODO - Added on 19th April
                contentValues.put(KEY_FOLLOWUP_QUOTATION_STATUS_ID, followUp.getQosid());
                contentValues.put(KEY_FOLLOWUP_ENQUIRY_STATUS_ID, followUp.getEnsid());
                contentValues.put(KEY_FOLLOWUP_CHKORDER_STATUS_ID, followUp.getChkosid());
                contentValues.put(KEY_FOLLOWUP_PURCHASE_ORDER_STATUS_ID, followUp.getPurorsid());
                contentValues.put(KEY_FOLLOWUP_LEAD_STATUS_ID, followUp.getLeasid());
                //TODO - Added on 10th May
                contentValues.put(KEY_FOLLOWUP_PARENT_ID_REPRESENTATION, followUp.getParentFollowupId());
                if (checkFollowUpResult(followUp.getFoid())) {
                    db.update(TABLE_FOLLOWUPS, contentValues, KEY_FOID + "=" + followUp.getFoid(), null);
                } else if (checkSyncIdFollowUp(followUp.getSyncId())) {
                    db.update(TABLE_FOLLOWUPS, contentValues, KEY_FOLLOWUP_SYNC_ID + "=" + followUp.getSyncId(), null);
                } else {
                    db.insert(TABLE_FOLLOWUPS, null, contentValues);
                }
            }
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Update name in Follow Up
    public void updateFollowUpName(String name, String leadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOMER_NAME, name);
        db.update(TABLE_FOLLOWUPS, values, KEY_FOLLOWUP_LEAD_ID + "=" + leadId, null);
    }

    // insert follow up  in table
    public void insertFollowUpData(FollowUp followUp) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ALERT_ON, followUp.getAlertOn());
            values.put(KEY_COLOR, followUp.getColor());
            values.put(KEY_CONTACT_PERSON, followUp.getContactPerson());
            values.put(KEY_CUSTOMER_NAME, followUp.getName());
            values.put(KEY_FOSID, followUp.getFosid());
            values.put(KEY_FOID, followUp.getFoid());
            values.put(KEY_SCHEDULED_DATE, followUp.getScheduledDate());
            values.put(KEY_FOLLOW_TYPE_NAME, followUp.getFollowupTypeName());
            values.put(KEY_FOLLOWUP_LEAD_ID, followUp.getLeadId());
            values.put(KEY_CUID, followUp.getCuid());
            values.put(KEY_FOLLOW_TYPE, followUp.getFollowupType());
            values.put(KEY_FOLLOW_TYPE_STATUS, followUp.getFollowupTypeStatus());
            values.put(KEY_FOLLOW_TYPE_STATUS_ID, followUp.getFollowupTypeStatusId());
            values.put(KEY_TAKEN_ON, followUp.getTakenOn());
            values.put(KEY_COMMENT, followUp.getComment());
            values.put(KEY_REASON, followUp.getReason());
            values.put(KEY_FEEDBACK, followUp.getFeedback());
            values.put(KEY_FOLLOWUP_OUTCOME, followUp.getFollowupOutcome());
            values.put(KEY_COMMUNICATION_MODE, followUp.getFollowupCommunicationMode());
            values.put(KEY_ALERT_MODE, followUp.getAlertMode());
            values.put(KEY_ASSIGNED_USER, followUp.getAssignedUser());
            values.put(KEY_CREATED_USER, followUp.getCreatedUser());
            values.put(KEY_UPDATED_USER, followUp.getUpdatedUser());
            values.put(KEY_CREATED_TS, followUp.getCreatedTs());
            values.put(KEY_UPDATED_TS, followUp.getUpdatedTs());
            values.put(KEY_PERSON_TYPE, followUp.getPerson_type());
            values.put(KEY_FOLLOWUP_SYNC_ID, followUp.getSyncId());
            values.put(KEY_SYNC, followUp.getSyncStatus());
            values.put(KEY_CONTACT_MOBILE, followUp.getContactPersonMobile());
            values.put(KEY_CONTACT_EMAIL, followUp.getContactPersonEmail());
            values.put(KEY_FOLLOW_UP_CODE_ID, followUp.getCodeid());
            values.put(KEY_FOLLOW_UP_COMM_ID, followUp.getCommid());
            values.put(KEY_FOLLOW_UP_FOTID, followUp.getFotid());
            values.put(KEY_FOLLOW_UP_ASSIGNED_UID, followUp.getAssignedUid());
            values.put(KEY_FOLLOW_UP_CONTACTED_PERSON, followUp.getContactedPerson());
            values.put(KEY_FOLLOW_UP_PARENT_ID, followUp.getParentId());
            values.put(KEY_LEAD_LOCAL_ID, followUp.getLeadLocalId());
            values.put(KEY_CONTACT_ID, followUp.getContactsId());
            values.put(KEY_FOLLOW_UP_ENQUIRY_ID, followUp.getEnid());
            values.put(KEY_FOLLOW_UP_QUOTATION_ID, followUp.getQoid());
            values.put(KEY_FOLLOW_UP_CUSTOMER_ORDER_ID, followUp.getChkoid());
            values.put(KEY_FOLLOW_UP_PURCHASE_ORDER_ID, followUp.getPurorid());
            values.put(KEY_FOLLOW_VENDOR_ID, followUp.getVenid());
            values.put(KEY_FOLLOW_NUMBER, followUp.getFollowupNumber());
            values.put(KEY_FOLLOW_FOID_REPRESENTATION, followUp.getFollowupId());
            values.put(KEY_FOLLOW_ENQUIRY_NUMBER, followUp.getEnquiryNumber());
            values.put(KEY_FOLLOW_EOID_REPRESENTATION, followUp.getEnquiryId());
            values.put(KEY_FOLLOW_QUID_REPRESENTATION, followUp.getQuotationId());
            values.put(KEY_FOLLOW_QUOTATION_NUMBER, followUp.getQuotationNumber());
            values.put(KEY_FOLLOW_SEQUENCE_NUMBER, followUp.getOrderSequenceNumber());
            values.put(KEY_FOLLOW_ORDER_ID_REPRESENTATION, followUp.getOrderId());
            values.put(KEY_FOLLOW_POID_REPRESENTATION, followUp.getPurchaseOrderId());
            values.put(KEY_FOLLOW_PURCHASE_ORDER_NUMBER, followUp.getPurchaseOrderNumber());
            values.put(KEY_FOLLOW_LEAID_REPRESENTATION, followUp.getLeadIdR());
            values.put(KEY_FOLLOW_LEAD_NUMBER, followUp.getLeadNumber());
            //TODO - Added on 19th April
            values.put(KEY_FOLLOWUP_QUOTATION_STATUS_ID, followUp.getQosid());
            values.put(KEY_FOLLOWUP_ENQUIRY_STATUS_ID, followUp.getEnsid());
            values.put(KEY_FOLLOWUP_CHKORDER_STATUS_ID, followUp.getChkosid());
            values.put(KEY_FOLLOWUP_PURCHASE_ORDER_STATUS_ID, followUp.getPurorsid());
            values.put(KEY_FOLLOWUP_LEAD_STATUS_ID, followUp.getLeasid());
            //TODO - Added on 10th May
            values.put(KEY_FOLLOWUP_PARENT_ID_REPRESENTATION, followUp.getParentFollowupId());
            db.insert(TABLE_FOLLOWUPS, null, values);
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check KEY_FOID is present or not
    public boolean checkFollowUpResult(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOID + "=?";
        boolean exists = false;
        try {
            if (iD != null && !iD.isEmpty()) {
                Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
                exists = cursor.moveToFirst();
                cursor.close();
                //   sqldb.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }


    // Checking Follow Up already exists or not
    public FollowUp getSingleFollowUpResult(String id, String type) {
        FollowUp followUp = new FollowUp();
        String countQuery;
        if (type.equals("foid"))
            countQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOID + "='" + id + "'";
        else
            countQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_SYNC_ID + "='" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    followUp.setAlertOn(cursor.getString(1));
                    followUp.setColor(cursor.getInt(2));
                    followUp.setContactPerson(cursor.getString(3));
                    followUp.setName(cursor.getString(4));
                    followUp.setFosid(cursor.getString(5));
                    followUp.setFoid(cursor.getString(6));
                    followUp.setScheduledDate(cursor.getString(7));
                    followUp.setFollowupTypeName(cursor.getString(8));
                    followUp.setLeadId(cursor.getString(9));
                    followUp.setCuid(cursor.getString(10));
                    followUp.setFollowupType(cursor.getString(11));
                    followUp.setFollowupTypeStatus(cursor.getString(12));
                    followUp.setFollowupTypeStatusId(cursor.getString(13));
                    followUp.setTakenOn(cursor.getString(14));
                    followUp.setComment(cursor.getString(15));
                    followUp.setReason(cursor.getString(16));
                    followUp.setFeedback(cursor.getString(17));
                    followUp.setFollowupOutcome(cursor.getString(18));
                    followUp.setFollowupCommunicationMode(cursor.getString(19));
                    followUp.setAlertMode(cursor.getString(20));
                    followUp.setAssigned_user(cursor.getString(21));
                    followUp.setCreatedUser(cursor.getString(22));
                    followUp.setUpdatedUser(cursor.getString(23));
                    followUp.setCreatedTs(cursor.getString(24));
                    followUp.setUpdatedTs(cursor.getString(25));
                    followUp.setPerson_type(cursor.getString(26));
                    followUp.setSyncId(cursor.getString(27));
                    followUp.setSyncStatus(cursor.getString(28));
                    followUp.setContactPersonMobile(cursor.getString(29));
                    followUp.setContactPersonEmail(cursor.getString(30));
                    followUp.setCodeid(cursor.getString(31));
                    followUp.setCommid(cursor.getString(32));
                    followUp.setFotid(cursor.getString(33));
                    followUp.setAssignedUid(cursor.getString(34));
                    followUp.setContactedPerson(cursor.getString(35));
                    followUp.setParentId(cursor.getString(36));
                    followUp.setLeadLocalId(cursor.getString(37));
                    followUp.setContactsId(cursor.getString(38));
                    followUp.setEnid(cursor.getString(39));
                    followUp.setQoid(cursor.getString(40));
                    followUp.setChkoid(cursor.getString(41));
                    followUp.setPurorid(cursor.getString(42));
                    followUp.setVenid(cursor.getString(43));
                    followUp.setFollowupNumber(cursor.getString(44));
                    followUp.setFollowupId(cursor.getString(45));
                    followUp.setEnquiryNumber(cursor.getString(46));
                    followUp.setEnquiryId(cursor.getString(47));
                    followUp.setQuotationId(cursor.getString(48));
                    followUp.setQuotationNumber(cursor.getString(49));
                    followUp.setOrderSequenceNumber(cursor.getString(50));
                    followUp.setOrderId(cursor.getString(51));
                    followUp.setPurchaseOrderId(cursor.getString(52));
                    followUp.setPurchaseOrderNumber(cursor.getString(53));
                    followUp.setLeadIdR(cursor.getString(54));
                    followUp.setLeadNumber(cursor.getString(55));
                    //TODO - Added on 19th April
                    followUp.setQosid(cursor.getString(56));
                    followUp.setEnsid(cursor.getString(57));
                    followUp.setChkosid(cursor.getString(58));
                    followUp.setPurorsid(cursor.getString(59));
                    followUp.setLeasid(cursor.getString(60));
                    //TODO - Added on 10th May
                    followUp.setParentFollowupId(cursor.getString(61));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return followUp;
    }

    // Update Follow Up status from pending to Taken into Follow Up table
    public void updateFollowUpStatus(FollowUp followUp, String type) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_FOSID, followUp.getFosid());
            values.put(KEY_COMMENT, followUp.getComment());
            values.put(KEY_FEEDBACK, followUp.getFeedback());
            values.put(KEY_FOLLOWUP_OUTCOME, followUp.getFollowupOutcome());
            values.put(KEY_CONTACT_PERSON, followUp.getContactPerson());
            values.put(KEY_REASON, followUp.getReason());
            values.put(KEY_CREATED_TS, followUp.getCreatedTs());
            values.put(KEY_UPDATED_TS, followUp.getUpdatedTs());
            values.put(KEY_CREATED_USER, followUp.getCreatedUser());
            values.put(KEY_UPDATED_USER, followUp.getUpdatedUser());
            values.put(KEY_ASSIGNED_USER, followUp.getAssignedUser());
            values.put(KEY_TAKEN_ON, followUp.getTakenOn());
            values.put(KEY_SCHEDULED_DATE, followUp.getScheduledDate());
            values.put(KEY_ALERT_ON, followUp.getAlertOn());
            values.put(KEY_FOLLOWUP_LEAD_ID, followUp.getLeadId());
            Log.e("TAGGED", followUp.getFollowupOutcome() + " " + followUp.getComment() + " " + followUp.getUpdatedUser());
            if (type.equals("foid"))
                db.update(TABLE_FOLLOWUPS, values, KEY_FOID + "=" + followUp.getFoid(), null);
            else if (type.equals("sync"))
                db.update(TABLE_FOLLOWUPS, values, KEY_FOLLOWUP_SYNC_ID + "=" + followUp.getSyncId(),
                        null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check Sync Id is present or not in Followup table
    public boolean checkSyncIdFollowUp(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_SYNC_ID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // insert follow up  in table
    public void updateFollowUpDataSyncId(FollowUp followUp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ALERT_ON, followUp.getAlertOn());
        values.put(KEY_COLOR, followUp.getColor());
        values.put(KEY_CONTACT_PERSON, followUp.getContactPerson());
        values.put(KEY_CUSTOMER_NAME, followUp.getName());
        values.put(KEY_FOSID, followUp.getFosid());
        values.put(KEY_FOID, followUp.getFoid());
        values.put(KEY_SCHEDULED_DATE, followUp.getScheduledDate());
        values.put(KEY_FOLLOW_TYPE_NAME, followUp.getFollowupTypeName());
        values.put(KEY_FOLLOWUP_LEAD_ID, followUp.getLeadId());
        values.put(KEY_CUID, followUp.getCuid());
        values.put(KEY_FOLLOW_TYPE, followUp.getFollowupType());
        values.put(KEY_FOLLOW_TYPE_STATUS, followUp.getFollowupTypeStatus());
        values.put(KEY_FOLLOW_TYPE_STATUS_ID, followUp.getFollowupTypeStatusId());
        values.put(KEY_TAKEN_ON, followUp.getTakenOn());
        values.put(KEY_COMMENT, followUp.getComment());
        values.put(KEY_REASON, followUp.getReason());
        values.put(KEY_FEEDBACK, followUp.getFeedback());
        values.put(KEY_FOLLOWUP_OUTCOME, followUp.getFollowupOutcome());
        values.put(KEY_COMMUNICATION_MODE, followUp.getFollowupCommunicationMode());
        values.put(KEY_ALERT_MODE, followUp.getAlertMode());
        values.put(KEY_ASSIGNED_USER, followUp.getAssignedUser());
        values.put(KEY_CREATED_USER, followUp.getCreatedUser());
        values.put(KEY_UPDATED_USER, followUp.getUpdatedUser());
        values.put(KEY_CREATED_TS, followUp.getCreatedTs());
        values.put(KEY_UPDATED_TS, followUp.getUpdatedTs());
        values.put(KEY_PERSON_TYPE, followUp.getPerson_type());
        values.put(KEY_FOLLOWUP_SYNC_ID, followUp.getSyncId());
        values.put(KEY_CONTACT_MOBILE, followUp.getContactPersonMobile());
        values.put(KEY_CONTACT_EMAIL, followUp.getContactPersonEmail());
        values.put(KEY_SYNC, followUp.getSyncStatus());
        values.put(KEY_LEAD_LOCAL_ID, followUp.getLeadLocalId());
        values.put(KEY_CONTACT_ID, followUp.getContactsId());
        values.put(KEY_FOLLOW_UP_ENQUIRY_ID, followUp.getEnid());
        values.put(KEY_FOLLOW_UP_QUOTATION_ID, followUp.getQoid());
        values.put(KEY_FOLLOW_UP_CUSTOMER_ORDER_ID, followUp.getChkoid());
        values.put(KEY_FOLLOW_UP_PURCHASE_ORDER_ID, followUp.getPurorid());
        values.put(KEY_FOLLOW_VENDOR_ID, followUp.getVenid());
        values.put(KEY_FOLLOW_NUMBER, followUp.getFollowupNumber());
        values.put(KEY_FOLLOW_FOID_REPRESENTATION, followUp.getFollowupId());
        values.put(KEY_FOLLOW_ENQUIRY_NUMBER, followUp.getEnquiryNumber());
        values.put(KEY_FOLLOW_EOID_REPRESENTATION, followUp.getEnquiryId());
        values.put(KEY_FOLLOW_QUID_REPRESENTATION, followUp.getQuotationId());
        values.put(KEY_FOLLOW_QUOTATION_NUMBER, followUp.getQuotationNumber());
        values.put(KEY_FOLLOW_SEQUENCE_NUMBER, followUp.getOrderSequenceNumber());
        values.put(KEY_FOLLOW_ORDER_ID_REPRESENTATION, followUp.getOrderId());
        values.put(KEY_FOLLOW_POID_REPRESENTATION, followUp.getPurchaseOrderId());
        values.put(KEY_FOLLOW_PURCHASE_ORDER_NUMBER, followUp.getPurchaseOrderNumber());
        values.put(KEY_FOLLOW_LEAID_REPRESENTATION, followUp.getLeadIdR());
        values.put(KEY_FOLLOW_LEAD_NUMBER, followUp.getLeadNumber());
        values.put(KEY_FOLLOW_UP_CODE_ID, followUp.getCodeid());
        //TODO - Added on 19th April
        values.put(KEY_FOLLOWUP_QUOTATION_STATUS_ID, followUp.getQosid());
        values.put(KEY_FOLLOWUP_ENQUIRY_STATUS_ID, followUp.getEnsid());
        values.put(KEY_FOLLOWUP_CHKORDER_STATUS_ID, followUp.getChkosid());
        values.put(KEY_FOLLOWUP_PURCHASE_ORDER_STATUS_ID, followUp.getPurorsid());
        values.put(KEY_FOLLOWUP_LEAD_STATUS_ID, followUp.getLeasid());

        Log.e("SYNC", followUp.getSyncId());
        db.update(TABLE_FOLLOWUPS, values, KEY_FOLLOWUP_SYNC_ID + "=" + followUp.getSyncId(), null);
        db.close();
    }

    public void addFollowUpFilter(FollowUpFilter followUpFilter) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODAY, followUpFilter.getToday());
        values.put(KEY_YESTERDAY, followUpFilter.getYesterday());
        values.put(KEY_TAKEN, followUpFilter.getTaken());
        values.put(KEY_PENDING, followUpFilter.getPending());
        values.put(KEY_START_DATE, followUpFilter.getStartDate());
        values.put(KEY_END_DATE, followUpFilter.getEndDate());
        values.put(KEY_TYPE_LEAD, followUpFilter.getLead());
        values.put(KEY_TYPE_ENQUIRY, followUpFilter.getEnquiry());
        values.put(KEY_TYPE_QUOTATION, followUpFilter.getQuotation());
        values.put(KEY_TYPE_PURCHASE_ORDER, followUpFilter.getPurchaseOrder());
        values.put(KEY_TYPE_SALES_ORDER, followUpFilter.getCustomerSalesOrder());
        values.put(KEY_THIS_WEEK, followUpFilter.getThisWeek());
        db.replace(TABLE_FILTER_FOLLOWUPS, null, values);
        db.close();
    }

    public void updateFollowUpFilter(FollowUpFilter followUpFilter, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODAY, followUpFilter.getToday());
        values.put(KEY_YESTERDAY, followUpFilter.getYesterday());
        values.put(KEY_TAKEN, followUpFilter.getTaken());
        values.put(KEY_PENDING, followUpFilter.getPending());
        values.put(KEY_START_DATE, followUpFilter.getStartDate());
        values.put(KEY_END_DATE, followUpFilter.getEndDate());
        values.put(KEY_TYPE_LEAD, followUpFilter.getLead());
        values.put(KEY_TYPE_ENQUIRY, followUpFilter.getEnquiry());
        values.put(KEY_TYPE_QUOTATION, followUpFilter.getQuotation());
        values.put(KEY_TYPE_PURCHASE_ORDER, followUpFilter.getPurchaseOrder());
        values.put(KEY_TYPE_SALES_ORDER, followUpFilter.getCustomerSalesOrder());
        values.put(KEY_THIS_WEEK, followUpFilter.getThisWeek());
        db.update(TABLE_FILTER_FOLLOWUPS, values, KEY_FILTER_ID + "=" + id, null);
        db.close();
    }

    //add Leads

    // Getting All followups
    public List<FollowUpFilter> getAllfollowUpfilter() {
        List<FollowUpFilter> followUpFiltersList = new ArrayList<FollowUpFilter>();
        try {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_FILTER_FOLLOWUPS;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    FollowUpFilter followUpFilters = new FollowUpFilter();
                    followUpFilters.setID(cursor.getInt(0));
                    followUpFilters.setToday(cursor.getString(1));
                    followUpFilters.setYesterday(cursor.getString(2));
                    followUpFilters.setTaken(cursor.getString(3));
                    followUpFilters.setPending(cursor.getString(4));
                    followUpFilters.setStartDate(cursor.getString(5));
                    followUpFilters.setEndDate(cursor.getString(6));
                    followUpFilters.setLead(cursor.getString(7));
                    followUpFilters.setEnquiry(cursor.getString(8));
                    followUpFilters.setQuotation(cursor.getString(9));
                    followUpFilters.setPurchaseOrder(cursor.getString(10));
                    followUpFilters.setCustomerSalesOrder(cursor.getString(11));
                    followUpFilters.setThisWeek(cursor.getString(12));
                    // Adding followup to list
                    followUpFiltersList.add(followUpFilters);
                } while (cursor.moveToNext());
            }

            // return followUps list
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return followUpFiltersList;
    }

    public List<FollowUp> getFilteredList(List<FollowUpFilter> allfollowUpfilter) {
        List<FollowUp> followUpFilteredList = new ArrayList<FollowUp>();
        String selectQuery = null;

        selectQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOSID + " IN ('" + allfollowUpfilter.get(0).getTaken() + "','" + allfollowUpfilter.get(0).getPending() + "')"
                + " AND " + KEY_SCHEDULED_DATE + " BETWEEN '" + allfollowUpfilter.get(0).getStartDate() + " 00:00:00' AND '" + allfollowUpfilter.get(0).getEndDate() + " 23:59:59'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FollowUp followUp = new FollowUp();
                followUp.setId(cursor.getInt(0));
                followUp.setAlertOn(cursor.getString(1));
                followUp.setColor(cursor.getInt(2));
                followUp.setContactPerson(cursor.getString(3));
                followUp.setName(cursor.getString(4));
                followUp.setFosid(cursor.getString(5));
                followUp.setFoid(cursor.getString(6));
                followUp.setScheduledDate(cursor.getString(7));
                followUp.setType(cursor.getString(8));
                followUp.setLeadId(cursor.getString(9));
                followUp.setPerson_type(cursor.getString(26));
                followUp.setSyncId(cursor.getString(27));
                followUp.setSyncStatus(cursor.getString(28));
                followUp.setContactPersonMobile(cursor.getString(29));
                followUp.setContactPersonEmail(cursor.getString(30));
                followUp.setParentId(cursor.getString(36));
                followUp.setLeadLocalId(cursor.getString(37));
                followUp.setContactsId(cursor.getString(38));
                //TODO - Added on 10th May
                followUp.setParentFollowupId(cursor.getString(61));
                // Adding followup to list
                followUpFilteredList.add(followUp);
            } while (cursor.moveToNext());
        }

        db.close();
        return followUpFilteredList;
    }

    public List<FollowUp> getFilteredListType(String type, String leadId, String enid, String qoid,
                                              String puroid, String salesOId, String startDate, String endDate) {
        List<FollowUp> followUpFilteredList = new ArrayList<FollowUp>();
        String selectQuery = null;

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            if (type.equals("(1,2)")) {
                selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOSID + " IN " + type + " AND ("
                        + KEY_SCHEDULED_DATE + " BETWEEN '" + startDate + " 00:00:00' AND '" + endDate + " 23:59:59') " +
                        " AND (";
            } else {
                selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOSID + " = " + type + " AND (" +
                        KEY_SCHEDULED_DATE + " BETWEEN '" + startDate + " 00:00:00' AND '" + endDate + " 23:59:59') " +
                        " AND (";
            }
        } else {
            if (type.equals("(1,2)")) {
                selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOSID + " IN " + type + " AND (";
            } else {
                selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOSID + " = " + type + " AND (";
            }
        }

        if (leadId != null && leadId.equals("1")) {
            selectQuery = selectQuery + "(" + KEY_FOLLOWUP_LEAD_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != ''" +
                    "AND " + KEY_FOLLOWUP_LEAD_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_STATUS_ID + " != '' AND " +
                    KEY_FOLLOWUP_LEAD_STATUS_ID + " NOT IN(3,4)" + ")";
        }

        if (enid != null && enid.equals("2")) {
            if (selectQuery.substring(selectQuery.length() - 5).equals("AND (")) {
                selectQuery = selectQuery + "(" + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_ENQUIRY_ID + " != ''"
                        + "AND " + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " IS NOT NULL AND " +
                        KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " != '' AND " +
                        KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " NOT IN(1,5,3))";
            } else {
                selectQuery = selectQuery + " OR " + "(" + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_ENQUIRY_ID + " != ''"
                        + "AND " + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " IS NOT NULL AND " +
                        KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " != '' AND " +
                        KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " NOT IN(1,3,5))";
            }
        }

        if (qoid != null && qoid.equals("3")) {
            if (selectQuery.substring(selectQuery.length() - 5).equals("AND (")) {
                selectQuery = selectQuery + "(" + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_QUOTATION_ID + " != ''"
                        + "AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " IS NOT NULL AND " +
                        KEY_FOLLOWUP_QUOTATION_STATUS_ID + " != '' AND " +
                        KEY_FOLLOWUP_QUOTATION_STATUS_ID + " NOT IN(1,3))";
            } else {
                selectQuery = selectQuery + " OR " + "(" + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_QUOTATION_ID + " != ''"
                        + "AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " IS NOT NULL AND " +
                        KEY_FOLLOWUP_QUOTATION_STATUS_ID + " != '' AND " +
                        KEY_FOLLOWUP_QUOTATION_STATUS_ID + " NOT IN(1,3))";
            }
        }

        if (puroid != null && puroid.equals("4")) {
            if (selectQuery.substring(selectQuery.length() - 5).equals("AND (")) {
                selectQuery = selectQuery + "(" + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " != ''" + ")";
            } else {
                selectQuery = selectQuery + " OR " + "(" + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " != ''" + ")";
            }
        }

        if (salesOId != null && salesOId.equals("5")) {
            if (selectQuery.substring(selectQuery.length() - 5).equals("AND (")) {
                selectQuery = selectQuery + "(" + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != '' AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID +
                        " IS NOT NULL AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID + " != '' AND " +
                        KEY_FOLLOWUP_CHKORDER_STATUS_ID + " NOT IN(5,6,7,10,11))";
            } else {
                selectQuery = selectQuery + " OR (" + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " +
                        KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != '' AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID +
                        " IS NOT NULL AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID + " != '' AND " +
                        KEY_FOLLOWUP_CHKORDER_STATUS_ID + " NOT IN(5,6,7,10,11))";
            }
        }

        if ((leadId == null || leadId.isEmpty()) && (enid == null || enid.isEmpty()) && (qoid == null || qoid.isEmpty())
                && (puroid == null || puroid.isEmpty()) && (salesOId == null || salesOId.isEmpty())) {
            selectQuery = selectQuery
                    + " (( " + KEY_FOLLOW_TYPE_STATUS_ID + " NOT IN(3,4) AND " + KEY_FOLLOWUP_LEAD_ID
                    + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != '' )" + " OR " +
                    "( " + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != '' )" + " OR " +
                    "( " + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_ENQUIRY_ID + " != '' )"
                    + " OR " + "( " + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " +
                    KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != '' ))" + " AND "
                    + " ((" + KEY_FOLLOWUP_LEAD_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != ''" +
                    "AND " + KEY_FOLLOWUP_LEAD_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_STATUS_ID + " != '' AND " +
                    KEY_FOLLOWUP_LEAD_STATUS_ID + " NOT IN(3,4)" + ")" +
                    " OR (" + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != ''  AND " +
                    KEY_FOLLOWUP_CHKORDER_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID + " != '' AND " +
                    KEY_FOLLOWUP_CHKORDER_STATUS_ID + " NOT IN(5,6,7,10,11) )" +
                    " OR (" + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " != '' )" +
                    " OR (" + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != '' " +
                    "AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " != '' AND " +
                    KEY_FOLLOWUP_QUOTATION_STATUS_ID + " NOT IN(1,3)" + ")" +
                    " OR (" + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_ENQUIRY_ID + " != '' " +
                    "AND " + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " != '' AND " +
                    KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " NOT IN(1,5,3)" + ")"
                    + ")";
        }

        selectQuery = selectQuery + ")";

        if (selectQuery.substring(selectQuery.length() - 6).equals("AND ()")) {
            selectQuery = selectQuery.replace(selectQuery.substring(selectQuery.length() - 6), "");
        }


        if (!type.equals("") && type != null) {
            Log.d("query", selectQuery);

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery + " ORDER BY " + KEY_FOSID + " ASC ,"
                    + KEY_SCHEDULED_DATE + " DESC ", null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    FollowUp followUp = new FollowUp();
                    followUp.setId(cursor.getInt(0));
                    followUp.setAlertOn(cursor.getString(1));
                    followUp.setColor(cursor.getInt(2));
                    followUp.setContactPerson(cursor.getString(3));
                    followUp.setName(cursor.getString(4));
                    followUp.setFosid(cursor.getString(5));
                    followUp.setFoid(cursor.getString(6));
                    followUp.setScheduledDate(cursor.getString(7));
                    followUp.setFollowupType(cursor.getString(8));
                    followUp.setLeadId(cursor.getString(9));
                    followUp.setCuid(cursor.getString(10));
                    followUp.setFollowupTypeName(cursor.getString(11));
                    followUp.setFollowupTypeStatus(cursor.getString(12));
                    followUp.setFollowupTypeStatusId(cursor.getString(13));
                    followUp.setTakenOn(cursor.getString(14));
                    followUp.setComment(cursor.getString(15));
                    followUp.setReason(cursor.getString(16));
                    followUp.setFeedback(cursor.getString(17));
                    followUp.setFollowupOutcome(cursor.getString(18));
                    followUp.setFollowupCommunicationMode(cursor.getString(19));
                    followUp.setAlertMode(cursor.getString(20));
                    followUp.setAssigned_user(cursor.getString(21));
                    followUp.setCreatedUser(cursor.getString(22));
                    followUp.setUpdatedUser(cursor.getString(23));
                    followUp.setCreatedTs(cursor.getString(24));
                    followUp.setUpdatedTs(cursor.getString(25));
                    followUp.setPerson_type(cursor.getString(26));
                    followUp.setSyncId(cursor.getString(27));
                    followUp.setSyncStatus(cursor.getString(28));
                    followUp.setContactPersonMobile(cursor.getString(29));
                    followUp.setContactPersonEmail(cursor.getString(30));
                    followUp.setCodeid(cursor.getString(31));
                    followUp.setCommid(cursor.getString(32));
                    followUp.setFotid(cursor.getString(33));
                    followUp.setAssignedUid(cursor.getString(34));
                    followUp.setContactedPerson(cursor.getString(35));
                    followUp.setParentId(cursor.getString(36));
                    followUp.setLeadLocalId(cursor.getString(37));
                    followUp.setContactsId(cursor.getString(38));
                    followUp.setEnid(cursor.getString(39));
                    followUp.setQoid(cursor.getString(40));
                    followUp.setChkoid(cursor.getString(41));
                    followUp.setPurorid(cursor.getString(42));
                    followUp.setVenid(cursor.getString(43));
                    //Added on 9th Feb
                    followUp.setFollowupNumber(cursor.getString(44));
                    followUp.setFollowupId(cursor.getString(45));
                    followUp.setEnquiryNumber(cursor.getString(46));
                    followUp.setEnquiryId(cursor.getString(47));
                    followUp.setQuotationId(cursor.getString(48));
                    followUp.setQuotationNumber(cursor.getString(49));
                    followUp.setOrderSequenceNumber(cursor.getString(50));
                    followUp.setOrderId(cursor.getString(51));
                    followUp.setPurchaseOrderId(cursor.getString(52));
                    followUp.setPurchaseOrderNumber(cursor.getString(53));
                    followUp.setLeadIdR(cursor.getString(54));
                    followUp.setLeadNumber(cursor.getString(55));
                    //TODO - Added on 19th April
                    followUp.setQosid(cursor.getString(56));
                    followUp.setEnsid(cursor.getString(57));
                    followUp.setChkosid(cursor.getString(58));
                    followUp.setPurorsid(cursor.getString(59));
                    followUp.setLeasid(cursor.getString(60));
                    //TODO - Added on 10th May
                    followUp.setParentFollowupId(cursor.getString(61));
                    // Adding followup to list
                    followUpFilteredList.add(followUp);
                } while (cursor.moveToNext());
            }

            db.close();
        } else {
            CustomisedToast.error(mContext, "Please check at least one option from taken and pending.").show();
        }
        return followUpFilteredList;
    }

    public List<FollowUp> getHomePendingFollowupsData(String type) {
        List<FollowUp> followUpFilteredList = new ArrayList<FollowUp>();
        try {
            String selectQuery = null;
            selectQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOSID + "=" + type +
                    " AND date(datetime(scheduled_date)) = date('now')" +
                    " AND ((" + KEY_FOLLOWUP_LEAD_STATUS_ID + " NOT IN(3,4) AND " + KEY_FOLLOWUP_LEAD_ID +
                    " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != '' )" +
                    " OR (" + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != '' AND " +
                    KEY_FOLLOWUP_CHKORDER_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID + " != '' AND " +
                    KEY_FOLLOWUP_CHKORDER_STATUS_ID + " NOT IN(5,6,7,10,11) )" +
                    " OR (" + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " != '' )" +
                    " OR (" + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != '' " +
                    "AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " != '' AND " +
                    KEY_FOLLOWUP_QUOTATION_STATUS_ID + " NOT IN(1,3)" + ")" +
                    " OR (" + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_ENQUIRY_ID +
                    " != '' ))" + " ORDER BY " + KEY_SCHEDULED_DATE + " ASC ";
            // " != '' AND date(datetime(scheduled_date)) = date('now')" + " ORDER BY " + KEY_SCHEDULED_DATE + " ASC ";


            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    FollowUp followUp = new FollowUp();
                    followUp.setId(cursor.getInt(0));
                    followUp.setAlertOn(cursor.getString(1));
                    followUp.setColor(cursor.getInt(2));
                    followUp.setContactPerson(cursor.getString(3));
                    followUp.setName(cursor.getString(4));
                    followUp.setFosid(cursor.getString(5));
                    followUp.setFoid(cursor.getString(6));
                    followUp.setScheduledDate(cursor.getString(7));
                    followUp.setFollowupType(cursor.getString(8));
                    followUp.setLeadId(cursor.getString(9));
                    followUp.setCuid(cursor.getString(10));
                    followUp.setFollowupTypeName(cursor.getString(11));
                    followUp.setFollowupTypeStatus(cursor.getString(12));
                    followUp.setFollowupTypeStatusId(cursor.getString(13));
                    followUp.setTakenOn(cursor.getString(14));
                    followUp.setComment(cursor.getString(15));
                    followUp.setReason(cursor.getString(16));
                    followUp.setFeedback(cursor.getString(17));
                    followUp.setFollowupOutcome(cursor.getString(18));
                    followUp.setFollowupCommunicationMode(cursor.getString(19));
                    followUp.setAlertMode(cursor.getString(20));
                    followUp.setAssigned_user(cursor.getString(21));
                    followUp.setCreatedUser(cursor.getString(22));
                    followUp.setUpdatedUser(cursor.getString(23));
                    followUp.setCreatedTs(cursor.getString(24));
                    followUp.setUpdatedTs(cursor.getString(25));
                    followUp.setPerson_type(cursor.getString(26));
                    followUp.setSyncId(cursor.getString(27));
                    followUp.setSyncStatus(cursor.getString(28));
                    followUp.setContactPersonMobile(cursor.getString(29));
                    followUp.setContactPersonEmail(cursor.getString(30));
                    followUp.setCodeid(cursor.getString(31));
                    followUp.setCommid(cursor.getString(32));
                    followUp.setFotid(cursor.getString(33));
                    followUp.setAssignedUid(cursor.getString(34));
                    followUp.setContactedPerson(cursor.getString(35));
                    followUp.setParentId(cursor.getString(36));
                    followUp.setLeadLocalId(cursor.getString(37));
                    followUp.setContactsId(cursor.getString(38));
                    followUp.setEnid(cursor.getString(39));
                    followUp.setQoid(cursor.getString(40));
                    followUp.setChkoid(cursor.getString(41));
                    followUp.setPurorid(cursor.getString(42));
                    followUp.setVenid(cursor.getString(43));
                    followUp.setFollowupNumber(cursor.getString(44));
                    followUp.setFollowupId(cursor.getString(45));
                    followUp.setEnquiryNumber(cursor.getString(46));
                    followUp.setEnquiryId(cursor.getString(47));
                    followUp.setQuotationId(cursor.getString(48));
                    followUp.setQuotationNumber(cursor.getString(49));
                    followUp.setOrderSequenceNumber(cursor.getString(50));
                    followUp.setOrderId(cursor.getString(51));
                    followUp.setPurchaseOrderId(cursor.getString(52));
                    followUp.setPurchaseOrderNumber(cursor.getString(53));
                    followUp.setLeadIdR(cursor.getString(54));
                    followUp.setLeadNumber(cursor.getString(55));
                    //TODO - Added on 19th April
                    followUp.setQosid(cursor.getString(56));
                    followUp.setEnsid(cursor.getString(57));
                    followUp.setChkosid(cursor.getString(58));
                    followUp.setPurorsid(cursor.getString(59));
                    followUp.setLeasid(cursor.getString(60));
                    //TODO - Added on 10th May
                    followUp.setParentFollowupId(cursor.getString(61));

                    // Adding followup to list
                    followUpFilteredList.add(followUp);
                } while (cursor.moveToNext());
            }

            db.close();
            return followUpFilteredList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return followUpFilteredList;
        }
    }

    //add Lead Contacts

    public int addLeads(Lead lead) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LEAD, lead.getLeaid());
        values.put(KEY_OWNER_ID, lead.getOwnerId());
        values.put(KEY_LEATID, lead.getLeatid());
        values.put(KEY_LEAD_NAME, lead.getName());
        values.put(KEY_LEAD_PERSON_TYPE, lead.getLeadPersonType());
        values.put(KEY_OFFICE_ADDRESS, lead.getOffAddr());
        values.put(KEY_WEBSITE, lead.getWebsite());
        values.put(KEY_LEAD_F_NAME, lead.getFname());
        values.put(KEY_LEAD_L_NAME, lead.getLname());
        values.put(KEY_LEAD_COMPANY_NAME, lead.getCompanyName());
        values.put(KEY_LEAD_SYNC_ID, lead.getSyncId());
        values.put(KEY_GENDER_ID, lead.getGenderid());
        values.put(KEY_MOBILE, lead.getMobile());
        values.put(KEY_EMAIL, lead.getEmail());
        values.put(KEY_ASSIGNED_UID, lead.getAssignedUid());
        values.put(KEY_LEASID, lead.getLeasid());
        values.put(KEY_ENID, String.valueOf(lead.getEnid()));
        values.put(KEY_QOID, String.valueOf(lead.getQoid()));
        values.put(KEY_CANCEL_REASON, lead.getCancelReason());
        values.put(KEY_SPECIAL_INSTRUCTIONS, lead.getSpecialInstructions());
        values.put(KEY_CREATED_UID, lead.getCreatedUid());
        values.put(KEY_UPDATED_UID, lead.getUpdatedUid());
        values.put(KEY_CREATED_TS, lead.getCreatedTs());
        values.put(KEY_UPDATED_TS, lead.getUpdatedTs());
        values.put(KEY_LEAD_SYNC, lead.getLeadSync());
        values.put(KEY_LEAD_NUMBER, lead.getLeadNumber());
        values.put(KEY_LEAD_ID_REPRESENTATION, lead.getLeadId());
        int id = (int) db.insert(TABLE_LEADS, null, values);

        Log.d("TABLE_LEADS", String.valueOf(id));
        db.close();
        return id;
    }

    //TODO Updated on 6th July 2k18
    // updateCount lead table
    public void updateLead(Lead lead, String checkSyncIdOrLeaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (checkSyncIdOrLeaId.equals("syncID")) {
                ContentValues values = new ContentValues();
                values.put(KEY_LEAD, lead.getLeaid());
                values.put(KEY_OWNER_ID, lead.getOwnerId());
                values.put(KEY_LEATID, lead.getLeatid());
                values.put(KEY_LEAD_NAME, lead.getName());
                values.put(KEY_LEAD_PERSON_TYPE, lead.getLeadPersonType());
                values.put(KEY_OFFICE_ADDRESS, lead.getOffAddr());
                values.put(KEY_WEBSITE, lead.getWebsite());
                values.put(KEY_LEAD_F_NAME, lead.getFname());
                values.put(KEY_LEAD_L_NAME, lead.getLname());
                values.put(KEY_LEAD_COMPANY_NAME, lead.getCompanyName());
                values.put(KEY_LEAD_SYNC_ID, lead.getSyncId());
                values.put(KEY_GENDER_ID, lead.getGenderid());
                values.put(KEY_MOBILE, lead.getMobile());
                values.put(KEY_EMAIL, lead.getEmail());
                values.put(KEY_ASSIGNED_UID, lead.getAssignedUid());
                values.put(KEY_LEASID, lead.getLeasid());
                values.put(KEY_ENID, lead.getEnid());
                values.put(KEY_QOID, lead.getQoid());
                values.put(KEY_CANCEL_REASON, lead.getCancelReason());
                values.put(KEY_SPECIAL_INSTRUCTIONS, lead.getSpecialInstructions());
                values.put(KEY_CREATED_UID, lead.getCreatedUid());
                values.put(KEY_UPDATED_UID, lead.getUpdatedUid());
                values.put(KEY_CREATED_TS, lead.getCreatedTs());
                values.put(KEY_UPDATED_TS, lead.getUpdatedTs());
                values.put(KEY_LEAD_SYNC, lead.getLeadSync());
                values.put(KEY_LEAD_NUMBER, lead.getLeadNumber());
                values.put(KEY_LEAD_ID_REPRESENTATION, lead.getLeadId());
                db.update(TABLE_LEADS, values, KEY_LEAD_SYNC_ID + "=" + lead.getSyncId(), null);

            } else {
                ContentValues values = new ContentValues();
                values.put(KEY_LEAD, lead.getLeaid());
                values.put(KEY_OWNER_ID, lead.getOwnerId());
                values.put(KEY_LEATID, lead.getLeatid());
                values.put(KEY_LEAD_NAME, lead.getName());
                values.put(KEY_LEAD_PERSON_TYPE, lead.getLeadPersonType());
                values.put(KEY_OFFICE_ADDRESS, lead.getOffAddr());
                values.put(KEY_WEBSITE, lead.getWebsite());
                values.put(KEY_LEAD_F_NAME, lead.getFname());
                values.put(KEY_LEAD_L_NAME, lead.getLname());
                values.put(KEY_LEAD_COMPANY_NAME, lead.getCompanyName());
                values.put(KEY_LEAD_SYNC_ID, lead.getSyncId());
                values.put(KEY_GENDER_ID, lead.getGenderid());
                values.put(KEY_MOBILE, lead.getMobile());
                values.put(KEY_EMAIL, lead.getEmail());
                values.put(KEY_ASSIGNED_UID, lead.getAssignedUid());
                values.put(KEY_LEASID, lead.getLeasid());
                values.put(KEY_ENID, lead.getEnid());
                values.put(KEY_QOID, lead.getQoid());
                values.put(KEY_CANCEL_REASON, lead.getCancelReason());
                values.put(KEY_SPECIAL_INSTRUCTIONS, lead.getSpecialInstructions());
                values.put(KEY_CREATED_UID, lead.getCreatedUid());
                values.put(KEY_UPDATED_UID, lead.getUpdatedUid());
                values.put(KEY_CREATED_TS, lead.getCreatedTs());
                values.put(KEY_UPDATED_TS, lead.getUpdatedTs());
                values.put(KEY_LEAD_SYNC, lead.getLeadSync());
                values.put(KEY_LEAD_NUMBER, lead.getLeadNumber());
                values.put(KEY_LEAD_ID_REPRESENTATION, lead.getLeadId());
                db.update(TABLE_LEADS, values, KEY_LEAD + "=" + lead.getLeaid(), null);
            }

            //db.execSQL(sql);
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Insert Follow Up Contact Data
    public int insertFollowUpContactData(Contact contact, String foId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CODE_ID, contact.getCodeid());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_DESIGNATION, contact.getDesignation());
        values.put(KEY_PHONE_NO, contact.getPhoneNo());
        values.put(KEY_FOID, foId);
        int id = (int) db.insert(TABLE_FOLLOWUP_CONTACTS, null, values);
        db.close();
        return id;
    }

    // get single api sync timeTextView

    public int addLeadContacts(Contacts contacts, String leadId, String syncId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_LEAD_CONTACTS_LEAID, leadId);
            values.put(KEY_LEAD_CONTACTS_CODE_ID, contacts.getCodeid());
            values.put(KEY_LEAD_CONTACTS_NAME, contacts.getName());
            values.put(KEY_LEAD_CONTACTS_DESIGNATION, contacts.getDesignation());
            values.put(KEY_LEAD_CONTACTS_EMAIL, contacts.getEmail());
            values.put(KEY_LEAD_CONTACTS_PHONE_NUMBER, contacts.getPhoneNo());
            values.put(KEY_LEAD_CONTACTS_SYNC_ID, syncId);
            values.put(KEY_LEAD_CONTACTS_IS_OWNER, contacts.getIsOwner());
            int id = (int) db.insert(TABLE_CONTACTS_DETAILS, null, values);

            if (db != null && db.isOpen()) {
                db.close();
            }
            return id;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    //TODO - Created on 2nd May
    //Check KEY_LEAD_CONTACTS_CODE_ID is present or not
    public boolean checkFollowUpContactResult(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        boolean exists = false;
        try {
            String Query = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " +
                    KEY_FOLLOW_UP_CODE_ID + " = " + iD;
            Cursor cursor = sqldb.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                exists = true;
            } else {
                exists = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqldb.close();
        }
        return exists;
    }

    //TODO - Created on 2nd May
    //Update Contacts in Follow Up
    public void updateFollowUpContacts(String name, String mobile, String email, String codeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_PERSON, name);
        values.put(KEY_CONTACT_MOBILE, mobile);
        values.put(KEY_CONTACT_EMAIL, email);
        db.update(TABLE_FOLLOWUPS, values, KEY_FOLLOW_UP_CODE_ID + "=" + codeId, null);
    }

    public void updateLeadContacts(Contacts contacts, String id, String flagType) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "";
        if (flagType.equals("leaid")) {

            ContentValues values = new ContentValues();
            values.put(KEY_LEAD_CONTACTS_LEAID, id);
            values.put(KEY_LEAD_CONTACTS_CODE_ID, contacts.getCodeid());
            values.put(KEY_LEAD_CONTACTS_NAME, contacts.getName());
            values.put(KEY_LEAD_CONTACTS_DESIGNATION, contacts.getDesignation());
            values.put(KEY_LEAD_CONTACTS_PHONE_NUMBER, contacts.getPhoneNo());
            values.put(KEY_LEAD_CONTACTS_EMAIL, contacts.getEmail());
            values.put(KEY_LEAD_CONTACTS_SYNC_ID, contacts.getSyncID());
            db.update(TABLE_CONTACTS_DETAILS, values, KEY_LEAD_CONTACTS_LEAID + "=" +
                    contacts.getLeaid(), null);
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_LEAD_CONTACTS_LEAID, contacts.getLeaid());
            values.put(KEY_LEAD_CONTACTS_CODE_ID, id);
            values.put(KEY_LEAD_CONTACTS_NAME, contacts.getName());
            values.put(KEY_LEAD_CONTACTS_DESIGNATION, contacts.getDesignation());
            values.put(KEY_LEAD_CONTACTS_PHONE_NUMBER, contacts.getPhoneNo());
            values.put(KEY_LEAD_CONTACTS_EMAIL, contacts.getEmail());
            values.put(KEY_LEAD_CONTACTS_SYNC_ID, contacts.getSyncID());
            db.update(TABLE_CONTACTS_DETAILS, values, KEY_LEAD_CONTACTS_CODE_ID + "=" +
                    contacts.getCodeid(), null);
        }
        db.execSQL(sql);
        db.close();
    }


    public void addLeadAddress(LeadShippingAddress leadShippingAddress, String leadId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_LEAD_ADDRESS_LEAID, leadId);
            values.put(KEY_LEAD_ADDRESS_SAID, leadShippingAddress.getSaid());
            values.put(KEY_LEAD_ADDRESS_F_NAME, leadShippingAddress.getFirstName());
            values.put(KEY_LEAD_ADDRESS_L_NAME, leadShippingAddress.getLastName());
            values.put(KEY_LEAD_ADDRESS_LINE_ONE, leadShippingAddress.getLine1());
            values.put(KEY_LEAD_ADDRESS_LINE_TWO, leadShippingAddress.getLine2());
            values.put(KEY_LEAD_ADDRESS_COVER_ID, leadShippingAddress.getCoverid());
            values.put(KEY_LEAD_ADDRESS_STID, leadShippingAddress.getStid());
            values.put(KEY_LEAD_ADDRESS_CTID, leadShippingAddress.getCtid());
            values.put(KEY_LEAD_ADDRESS_ZIPCODE, leadShippingAddress.getZipCode());
            values.put(KEY_LEAD_ADDRESS_MOBILE, leadShippingAddress.getMobile());
            values.put(KEY_LEAD_ADDRESS_SATID, leadShippingAddress.getSatid());
            values.put(KEY_LEAD_ADDRESS_SASID, leadShippingAddress.getSasid());
            values.put(KEY_LEAD_ADDRESS_CITY, leadShippingAddress.getCity());
            values.put(KEY_LEAD_ADDRESS_COUNTRY, leadShippingAddress.getCountry());
            values.put(KEY_LEAD_ADDRESS_ISO_CODE, leadShippingAddress.getIsoCode());
            values.put(KEY_LEAD_ADDRESS_STATE, leadShippingAddress.getState());
            values.put(KEY_LEAD_ADDRESS_STATE_CODE, leadShippingAddress.getStateCode());
            values.put(KEY_LEAD_ADDRESS_SYNC_ID, leadShippingAddress.getSyncID());
            values.put(KEY_LEAD_ADDRESS_SITE_NAME, leadShippingAddress.getSiteName());

            db.insert(TABLE_LEAD_ADDRESS, null, values);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Update Lead Addresses
    public int updateLeadAddress(LeadShippingAddress leadShippingAddress, String leadId,
                                 String checkSyncIdOrLeaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LEAD_ADDRESS_LEAID, leadId);
        values.put(KEY_LEAD_ADDRESS_SAID, leadShippingAddress.getSaid());
        values.put(KEY_LEAD_ADDRESS_F_NAME, leadShippingAddress.getFirstName());
        values.put(KEY_LEAD_ADDRESS_L_NAME, leadShippingAddress.getLastName());
        values.put(KEY_LEAD_ADDRESS_LINE_ONE, leadShippingAddress.getLine1());
        values.put(KEY_LEAD_ADDRESS_LINE_TWO, leadShippingAddress.getLine2());
        values.put(KEY_LEAD_ADDRESS_COVER_ID, leadShippingAddress.getCoverid());
        values.put(KEY_LEAD_ADDRESS_STID, leadShippingAddress.getStid());
        values.put(KEY_LEAD_ADDRESS_CTID, leadShippingAddress.getCtid());
        values.put(KEY_LEAD_ADDRESS_ZIPCODE, leadShippingAddress.getZipCode());
        values.put(KEY_LEAD_ADDRESS_MOBILE, leadShippingAddress.getMobile());
        values.put(KEY_LEAD_ADDRESS_SATID, leadShippingAddress.getSatid());
        values.put(KEY_LEAD_ADDRESS_SASID, leadShippingAddress.getSasid());
        values.put(KEY_LEAD_ADDRESS_CITY, leadShippingAddress.getCity());
        values.put(KEY_LEAD_ADDRESS_COUNTRY, leadShippingAddress.getCountry());
        values.put(KEY_LEAD_ADDRESS_ISO_CODE, leadShippingAddress.getIsoCode());
        values.put(KEY_LEAD_ADDRESS_STATE, leadShippingAddress.getState());
        values.put(KEY_LEAD_ADDRESS_STATE_CODE, leadShippingAddress.getStateCode());
        values.put(KEY_LEAD_ADDRESS_SYNC_ID, leadShippingAddress.getSyncID());
        values.put(KEY_LEAD_ADDRESS_SITE_NAME, leadShippingAddress.getSiteName());
        if (checkSyncIdOrLeaId.equals("syncID")) {
            db.update(TABLE_LEAD_ADDRESS, values, KEY_LEAD_ADDRESS_SYNC_ID + "=" + leadId, null);
            //selectAddressQuery = "SELECT * FROM " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_SYNC_ID + "='" + id + "'";

        } else {
            db.update(TABLE_LEAD_ADDRESS, values, KEY_LEAD_ADDRESS_LEAID + "=" + leadId, null);
            //selectAddressQuery = "SELECT * FROM " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_LEAID + "='" + id + "'";
        }

        db.close();
        return 0;
    }


    // add API Sync Time
    public void addSyncCheck(SyncCheck syncCheck) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_SERVICE, syncCheck.getService());
            values.put(KEY_CALL_TIME, syncCheck.getCallTime());

            db.insert(TABLE_API_SYNC, null, values);

            Log.v("API SYNC", values.toString());
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // updateCount API Sync timeTextView
    public void updateSyncCheck(SyncCheck syncCheck) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_SERVICE, syncCheck.getService());
            values.put(KEY_CALL_TIME, syncCheck.getCallTime());
            /*db.update(TABLE_API_SYNC, values, KEY_SERVICE + "=" +
                    syncCheck.getService(), null);*/

            String sql = "UPDATE " + TABLE_API_SYNC + " SET " +
                    KEY_SERVICE + " = '" + syncCheck.getService() + "', " +
                    KEY_CALL_TIME + " = '" + syncCheck.getCallTime() + "' " +
                    " WHERE " + KEY_SERVICE + " = '" + syncCheck.getService() + "' ";
            db.execSQL(sql);
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SyncCheck getSyncTime(String task) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_API_SYNC + " WHERE " + KEY_SERVICE + "='" + task + "'";
        SyncCheck synccheck = new SyncCheck();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    synccheck.setId(Integer.parseInt(cursor.getString(0)));
                    synccheck.setService(cursor.getString(1));
                    synccheck.setCallTime(cursor.getString(2));
                    // Adding syncCheck to list
                    Log.v("sync timeTextView", synccheck.getService() + synccheck.getCallTime());
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        // return Synccheck list
        return synccheck;
    }
    // add Assignee spinner

    // Getting All Leads
    public List<Lead> getAllLeads() {
        List<Lead> leadList = new ArrayList<Lead>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEASID + " NOT IN(4) "
                + " ORDER BY " + KEY_LEAD_ID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lead lead = new Lead();
                lead.setID(cursor.getInt(0));
                lead.setLeaid(cursor.getString(1));
                lead.setOwnerId(cursor.getString(2));
                lead.setLeatid(cursor.getString(3));
                lead.setName(cursor.getString(4));
                lead.setLeadPersonType(cursor.getString(5));
                lead.setOffAddr(cursor.getString(6));
                lead.setWebsite(cursor.getString(7));
                lead.setFname(cursor.getString(8));
                lead.setLname(cursor.getString(9));
                lead.setCompanyName(cursor.getString(10));
                lead.setSyncId(cursor.getString(11));
                lead.setGenderid(cursor.getString(12));
                lead.setMobile(cursor.getString(13));
                lead.setEmail(cursor.getString(14));
                lead.setAssignedUid(cursor.getString(15));
                lead.setLeasid(cursor.getString(16));
                lead.setEnid(cursor.getString(17));
                lead.setQoid(cursor.getString(18));
                lead.setCancelReason(cursor.getString(19));
                lead.setSpecialInstructions(cursor.getString(20));
                lead.setCreatedUid(cursor.getString(21));
                lead.setUpdatedUid(cursor.getString(22));
                lead.setCreatedTs(cursor.getString(23));
                lead.setUpdatedTs(cursor.getString(24));
                lead.setLeadSync(cursor.getString(25));
                lead.setLeadNumber(cursor.getString(26));
                lead.setLeadId(cursor.getString(27));
                leadList.add(lead);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return lead list
        return leadList;
    }

    public List<Contacts> getLeadContacts(String id, String checkSyncIdOrLeaId) {
        String selectContactsQuery;
        SQLiteDatabase db = this.getWritableDatabase();
        if (checkSyncIdOrLeaId.equals("syncID")) {
            selectContactsQuery = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " + KEY_LEAD_CONTACTS_SYNC_ID + " = " + id + "";

        } else {
            selectContactsQuery = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " + KEY_LEAD_CONTACTS_LEAID + " = " + id + "";
        }

        Cursor cursorContacts = db.rawQuery(selectContactsQuery, null);
        cursorContacts.moveToFirst();

        List<Contacts> contactsList = new ArrayList<>();

        if (cursorContacts.moveToFirst()) {
            do {
                Contacts contacts = new Contacts();
                contacts.setID(cursorContacts.getInt(0));
                contacts.setLeaid(cursorContacts.getString(1));
                contacts.setCodeid(cursorContacts.getString(2));
                contacts.setName(cursorContacts.getString(3));
                contacts.setDesignation(cursorContacts.getString(4));
                contacts.setEmail(cursorContacts.getString(5));
                contacts.setPhoneNo(cursorContacts.getString(6));
                contacts.setSyncID(cursorContacts.getString(7));
                contacts.setIsOwner(cursorContacts.getString(10));
                contactsList.add(contacts);
            }
            while (cursorContacts.moveToNext());
        }
        cursorContacts.close();
        db.close();
        // return lead list
        return contactsList;
    }


    public List<LeadShippingAddress> getLeadAddress(String id, String checkSyncIdOrLeaId) {
        String selectAddressQuery;
        SQLiteDatabase db = this.getWritableDatabase();
        if (checkSyncIdOrLeaId.equals("syncID")) {
            selectAddressQuery = "SELECT * FROM " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_SYNC_ID + " = " + id + "";

        } else {
            selectAddressQuery = "SELECT * FROM " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_LEAID + " = " + id + "";
        }
        Cursor cursorAddress = db.rawQuery(selectAddressQuery, null);
        cursorAddress.moveToFirst();

        List<LeadShippingAddress> leadShippingAddressList = new ArrayList<>();
        if (cursorAddress.moveToFirst()) {
            do {
                LeadShippingAddress leadShippingAddress = new LeadShippingAddress();
                leadShippingAddress.setId(cursorAddress.getInt(0));
                leadShippingAddress.setLeaid(cursorAddress.getString(1));
                leadShippingAddress.setSaid(cursorAddress.getString(2));
                leadShippingAddress.setFirstName(cursorAddress.getString(3));
                leadShippingAddress.setLastName(cursorAddress.getString(4));
                leadShippingAddress.setLine1(cursorAddress.getString(5));
                leadShippingAddress.setLine2(cursorAddress.getString(6));
                leadShippingAddress.setCoverid(cursorAddress.getString(7));
                leadShippingAddress.setStid(cursorAddress.getString(8));
                leadShippingAddress.setCtid(cursorAddress.getString(9));
                leadShippingAddress.setZipCode(cursorAddress.getString(10));
                leadShippingAddress.setMobile(cursorAddress.getString(11));
                leadShippingAddress.setSatid(cursorAddress.getString(12));
                leadShippingAddress.setSasid(cursorAddress.getString(13));
                leadShippingAddress.setCity(cursorAddress.getString(14));
                leadShippingAddress.setCountry(cursorAddress.getString(15));
                leadShippingAddress.setIsoCode(cursorAddress.getString(16));
                leadShippingAddress.setState(cursorAddress.getString(17));
                leadShippingAddress.setStateCode(cursorAddress.getString(18));
                leadShippingAddress.setSyncID(cursorAddress.getString(19));
                leadShippingAddress.setSiteName(cursorAddress.getString(20));

                leadShippingAddressList.add(leadShippingAddress);
            } while (cursorAddress.moveToNext());
        }

        cursorAddress.close();
        db.close();
        // return lead list
        return leadShippingAddressList;
    }


    public List<FollowUp> getLeadFollowUp(String id, String checkSyncIdOrLeaId) {
        String selectAddressQuery;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectFollowUp;
        if (checkSyncIdOrLeaId.equals("syncID")) {
            selectFollowUp = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_SYNC_ID + " = " + id + "";

        } else {
            selectFollowUp = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_LEAD_ID + " = " + id + "";
        }

        Cursor cursorFollowUp = db.rawQuery(selectFollowUp, null);
        cursorFollowUp.moveToFirst();

        List<FollowUp> leadFollowUpList = new ArrayList<>();
        if (cursorFollowUp.moveToFirst()) {
            do {

                FollowUp leadFollowUp = new FollowUp();
                leadFollowUp.setId(cursorFollowUp.getInt(0));
                leadFollowUp.setAlertOn(cursorFollowUp.getString(1));
                leadFollowUp.setColor(cursorFollowUp.getInt(2));
                leadFollowUp.setContactPerson(cursorFollowUp.getString(3));
                leadFollowUp.setName(cursorFollowUp.getString(4));
                leadFollowUp.setFosid(cursorFollowUp.getString(5));
                leadFollowUp.setFoid(cursorFollowUp.getString(6));
                leadFollowUp.setScheduledDate(cursorFollowUp.getString(7));
                leadFollowUp.setFollowupType(cursorFollowUp.getString(8));
                leadFollowUp.setLeadId(cursorFollowUp.getString(9));
                leadFollowUp.setCuid(cursorFollowUp.getString(10));
                leadFollowUp.setFollowupTypeName(cursorFollowUp.getString(11));
                leadFollowUp.setFollowupTypeStatus(cursorFollowUp.getString(12));
                leadFollowUp.setFollowupTypeStatusId(cursorFollowUp.getString(13));
                leadFollowUp.setTakenOn(cursorFollowUp.getString(14));
                leadFollowUp.setComment(cursorFollowUp.getString(15));
                leadFollowUp.setReason(cursorFollowUp.getString(16));
                leadFollowUp.setFeedback(cursorFollowUp.getString(17));
                leadFollowUp.setFollowupOutcome(cursorFollowUp.getString(18));
                leadFollowUp.setFollowupCommunicationMode(cursorFollowUp.getString(19));
                leadFollowUp.setAlertMode(cursorFollowUp.getString(20));
                leadFollowUp.setAssigned_user(cursorFollowUp.getString(21));
                leadFollowUp.setCreatedUser(cursorFollowUp.getString(22));
                leadFollowUp.setUpdatedUser(cursorFollowUp.getString(23));
                leadFollowUp.setCreatedTs(cursorFollowUp.getString(24));
                leadFollowUp.setUpdatedTs(cursorFollowUp.getString(25));
                leadFollowUp.setPerson_type(cursorFollowUp.getString(26));
                leadFollowUp.setSyncId(cursorFollowUp.getString(27));
                leadFollowUp.setSyncStatus(cursorFollowUp.getString(28));
                leadFollowUp.setContactPersonMobile(cursorFollowUp.getString(29));
                leadFollowUp.setContactPersonEmail(cursorFollowUp.getString(30));
                leadFollowUp.setCodeid(cursorFollowUp.getString(31));
                leadFollowUp.setCommid(cursorFollowUp.getString(32));
                leadFollowUp.setFotid(cursorFollowUp.getString(33));
                leadFollowUp.setAssignedUid(cursorFollowUp.getString(34));
                leadFollowUp.setContactedPerson(cursorFollowUp.getString(35));
                leadFollowUp.setParentId(cursorFollowUp.getString(36));
                leadFollowUp.setLeadLocalId(cursorFollowUp.getString(37));
                leadFollowUp.setContactsId(cursorFollowUp.getString(38));
                leadFollowUp.setEnid(cursorFollowUp.getString(39));
                leadFollowUp.setQoid(cursorFollowUp.getString(40));
                leadFollowUp.setChkoid(cursorFollowUp.getString(41));
                leadFollowUp.setPurorid(cursorFollowUp.getString(42));
                leadFollowUp.setVenid(cursorFollowUp.getString(43));
                leadFollowUp.setFollowupNumber(cursorFollowUp.getString(44));
                leadFollowUp.setFollowupId(cursorFollowUp.getString(45));
                leadFollowUp.setEnquiryNumber(cursorFollowUp.getString(46));
                leadFollowUp.setEnquiryId(cursorFollowUp.getString(47));
                leadFollowUp.setQuotationId(cursorFollowUp.getString(48));
                leadFollowUp.setQuotationNumber(cursorFollowUp.getString(49));
                leadFollowUp.setOrderSequenceNumber(cursorFollowUp.getString(50));
                leadFollowUp.setOrderId(cursorFollowUp.getString(51));
                leadFollowUp.setPurchaseOrderId(cursorFollowUp.getString(52));
                leadFollowUp.setPurchaseOrderNumber(cursorFollowUp.getString(53));
                leadFollowUp.setLeadIdR(cursorFollowUp.getString(54));
                leadFollowUp.setLeadNumber(cursorFollowUp.getString(55));
                //TODO - Added on 19th April
                leadFollowUp.setQosid(cursorFollowUp.getString(56));
                leadFollowUp.setEnsid(cursorFollowUp.getString(57));
                leadFollowUp.setChkosid(cursorFollowUp.getString(58));
                leadFollowUp.setPurorsid(cursorFollowUp.getString(59));
                leadFollowUp.setLeasid(cursorFollowUp.getString(60));
                //TODO - Added on 10th May
                leadFollowUp.setParentFollowupId(cursorFollowUp.getString(61));
                leadFollowUpList.add(leadFollowUp);
            } while (cursorFollowUp.moveToNext());
        }
        cursorFollowUp.close();
        db.close();
        // return lead list
        return leadFollowUpList;
    }

    public Lead getLead(String id, String checkSyncIdOrLeaId) {
        Lead lead = new Lead();
        try {
            String selectQuery;
            if (id != null && !id.isEmpty()) {
                if (checkSyncIdOrLeaId.equals("syncID")) {
                    selectQuery = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEAD_SYNC_ID + "='" + id + "'";

                } else {
                    selectQuery = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEAD + "='" + id + "'";
                }
            } else {
                return lead;
            }
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                lead.setID(cursor.getInt(0));
                lead.setLeaid(cursor.getString(1));
                lead.setOwnerId(cursor.getString(2));
                lead.setLeatid(cursor.getString(3));
                lead.setName(cursor.getString(4));
                lead.setLeadPersonType(cursor.getString(5));
                lead.setOffAddr(cursor.getString(6));
                lead.setWebsite(cursor.getString(7));
                lead.setFname(cursor.getString(8));
                lead.setLname(cursor.getString(9));
                lead.setCompanyName(cursor.getString(10));
                lead.setSyncId(cursor.getString(11));
                lead.setGenderid(cursor.getString(12));
                lead.setMobile(cursor.getString(13));
                lead.setEmail(cursor.getString(14));
                lead.setAssignedUid(cursor.getString(15));
                lead.setLeasid(cursor.getString(16));
                lead.setEnid(cursor.getString(17));
                lead.setQoid(cursor.getString(18));
                lead.setCancelReason(cursor.getString(19));
                lead.setSpecialInstructions(cursor.getString(20));
                lead.setCreatedUid(cursor.getString(21));
                lead.setUpdatedUid(cursor.getString(22));
                lead.setCreatedTs(cursor.getString(23));
                lead.setUpdatedTs(cursor.getString(24));
                lead.setLeadSync(cursor.getString(25));
                lead.setLeadNumber(cursor.getString(26));
                lead.setLeadId(cursor.getString(27));
            }

            String selectContactsQuery;

            if (checkSyncIdOrLeaId.equals("syncID")) {
                selectContactsQuery = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " + KEY_LEAD_CONTACTS_SYNC_ID + "='" + id + "'";

            } else {
                selectContactsQuery = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " + KEY_LEAD_CONTACTS_LEAID + "='" + id + "'";
            }

            Cursor cursorContacts = db.rawQuery(selectContactsQuery, null);
            cursorContacts.moveToFirst();

            List<Contacts> contactsList = new ArrayList<>();

            if (cursorContacts.moveToFirst()) {
                do {
                    Contacts contacts = new Contacts();
                    contacts.setID(cursorContacts.getInt(0));
                    contacts.setLeaid(cursorContacts.getString(1));
                    contacts.setCodeid(cursorContacts.getString(2));
                    contacts.setName(cursorContacts.getString(3));
                    contacts.setDesignation(cursorContacts.getString(4));
                    contacts.setEmail(cursorContacts.getString(5));
                    contacts.setPhoneNo(cursorContacts.getString(6));
                    contacts.setSyncID(cursorContacts.getString(7));
                    contacts.setIsOwner(cursorContacts.getString(10));
                    contactsList.add(contacts);
                } while (cursorContacts.moveToNext());
            }

            lead.setContacts(contactsList);
            String selectAddressQuery;

            if (checkSyncIdOrLeaId.equals("syncID")) {
                selectAddressQuery = "SELECT * FROM " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_SYNC_ID + "='" + id + "'";

            } else {
                selectAddressQuery = "SELECT * FROM " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_LEAID + "='" + id + "'";
            }
            Cursor cursorAddress = db.rawQuery(selectAddressQuery, null);
            //cursorAddress.moveToFirst();

            List<LeadShippingAddress> leadShippingAddressList = new ArrayList<>();
            if (cursorAddress.moveToFirst()) {
                do {
                    LeadShippingAddress leadShippingAddress = new LeadShippingAddress();
                    leadShippingAddress.setId(cursorAddress.getInt(0));
                    leadShippingAddress.setLeaid(cursorAddress.getString(1));
                    leadShippingAddress.setSaid(cursorAddress.getString(2));
                    leadShippingAddress.setFirstName(cursorAddress.getString(3));
                    leadShippingAddress.setLastName(cursorAddress.getString(4));
                    leadShippingAddress.setLine1(cursorAddress.getString(5));
                    leadShippingAddress.setLine2(cursorAddress.getString(6));
                    leadShippingAddress.setCoverid(cursorAddress.getString(7));
                    leadShippingAddress.setStid(cursorAddress.getString(8));
                    leadShippingAddress.setCtid(cursorAddress.getString(9));
                    leadShippingAddress.setZipCode(cursorAddress.getString(10));
                    leadShippingAddress.setMobile(cursorAddress.getString(11));
                    leadShippingAddress.setSatid(cursorAddress.getString(12));
                    leadShippingAddress.setSasid(cursorAddress.getString(13));
                    leadShippingAddress.setCity(cursorAddress.getString(14));
                    leadShippingAddress.setCountry(cursorAddress.getString(15));
                    leadShippingAddress.setIsoCode(cursorAddress.getString(16));
                    leadShippingAddress.setState(cursorAddress.getString(17));
                    leadShippingAddress.setStateCode(cursorAddress.getString(18));
                    leadShippingAddress.setSyncID(cursorAddress.getString(19));
                    leadShippingAddress.setSiteName(cursorAddress.getString(20));

                    leadShippingAddressList.add(leadShippingAddress);
                } while (cursorAddress.moveToNext());
            }

            lead.setShippingAddress(leadShippingAddressList);
            String selectFollowUp;
            if (checkSyncIdOrLeaId.equals("syncID")) {
                selectFollowUp = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_SYNC_ID + "='" + id + "'";

            } else {
                selectFollowUp = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_LEAD_ID + "='" + id + "'";
            }

            Cursor cursorFollowUp = db.rawQuery(selectFollowUp, null);
            cursorFollowUp.moveToFirst();

            List<FollowUp> leadFollowUpList = new ArrayList<>();
            if (cursorFollowUp.moveToFirst()) {
                do {
                    FollowUp leadFollowUp = new FollowUp();
                    leadFollowUp.setId(cursorFollowUp.getInt(0));
                    leadFollowUp.setAlertOn(cursorFollowUp.getString(1));
                    leadFollowUp.setColor(cursorFollowUp.getInt(2));
                    leadFollowUp.setContactPerson(cursorFollowUp.getString(3));
                    leadFollowUp.setName(cursorFollowUp.getString(4));
                    leadFollowUp.setFosid(cursorFollowUp.getString(5));
                    leadFollowUp.setFoid(cursorFollowUp.getString(6));
                    leadFollowUp.setScheduledDate(cursorFollowUp.getString(7));
                    leadFollowUp.setFollowupType(cursorFollowUp.getString(8));
                    leadFollowUp.setLeadId(cursorFollowUp.getString(9));
                    leadFollowUp.setCuid(cursorFollowUp.getString(10));
                    leadFollowUp.setFollowupTypeName(cursorFollowUp.getString(11));
                    leadFollowUp.setFollowupTypeStatus(cursorFollowUp.getString(12));
                    leadFollowUp.setFollowupTypeStatusId(cursorFollowUp.getString(13));
                    leadFollowUp.setTakenOn(cursorFollowUp.getString(14));
                    leadFollowUp.setComment(cursorFollowUp.getString(15));
                    leadFollowUp.setReason(cursorFollowUp.getString(16));
                    leadFollowUp.setFeedback(cursorFollowUp.getString(17));
                    leadFollowUp.setFollowupOutcome(cursorFollowUp.getString(18));
                    leadFollowUp.setFollowupCommunicationMode(cursorFollowUp.getString(19));
                    leadFollowUp.setAlertMode(cursorFollowUp.getString(20));
                    leadFollowUp.setAssigned_user(cursorFollowUp.getString(21));
                    leadFollowUp.setCreatedUser(cursorFollowUp.getString(22));
                    leadFollowUp.setUpdatedUser(cursorFollowUp.getString(23));
                    leadFollowUp.setCreatedTs(cursorFollowUp.getString(24));
                    leadFollowUp.setUpdatedTs(cursorFollowUp.getString(25));
                    leadFollowUp.setPerson_type(cursorFollowUp.getString(26));
                    leadFollowUp.setSyncId(cursorFollowUp.getString(27));
                    leadFollowUp.setSyncStatus(cursorFollowUp.getString(28));
                    leadFollowUp.setContactPersonMobile(cursorFollowUp.getString(29));
                    leadFollowUp.setContactPersonEmail(cursorFollowUp.getString(30));
                    leadFollowUp.setCodeid(cursorFollowUp.getString(31));
                    leadFollowUp.setCommid(cursorFollowUp.getString(32));
                    leadFollowUp.setFotid(cursorFollowUp.getString(33));
                    leadFollowUp.setAssignedUid(cursorFollowUp.getString(34));
                    leadFollowUp.setContactedPerson(cursorFollowUp.getString(35));
                    leadFollowUp.setParentId(cursorFollowUp.getString(36));
                    leadFollowUp.setLeadLocalId(cursorFollowUp.getString(37));
                    leadFollowUp.setContactsId(cursorFollowUp.getString(38));
                    leadFollowUp.setEnid(cursorFollowUp.getString(39));
                    leadFollowUp.setQoid(cursorFollowUp.getString(40));
                    leadFollowUp.setChkoid(cursorFollowUp.getString(41));
                    leadFollowUp.setPurorid(cursorFollowUp.getString(42));
                    leadFollowUp.setVenid(cursorFollowUp.getString(43));
                    leadFollowUp.setFollowupNumber(cursorFollowUp.getString(44));
                    leadFollowUp.setFollowupId(cursorFollowUp.getString(45));
                    leadFollowUp.setEnquiryNumber(cursorFollowUp.getString(46));
                    leadFollowUp.setEnquiryId(cursorFollowUp.getString(47));
                    leadFollowUp.setQuotationId(cursorFollowUp.getString(48));
                    leadFollowUp.setQuotationNumber(cursorFollowUp.getString(49));
                    leadFollowUp.setOrderSequenceNumber(cursorFollowUp.getString(50));
                    leadFollowUp.setOrderId(cursorFollowUp.getString(51));
                    leadFollowUp.setPurchaseOrderId(cursorFollowUp.getString(52));
                    leadFollowUp.setPurchaseOrderNumber(cursorFollowUp.getString(53));
                    leadFollowUp.setLeadIdR(cursorFollowUp.getString(54));
                    leadFollowUp.setLeadNumber(cursorFollowUp.getString(55));
                    //TODO - Added on 19th April
                    leadFollowUp.setQosid(cursorFollowUp.getString(56));
                    leadFollowUp.setEnsid(cursorFollowUp.getString(57));
                    leadFollowUp.setChkosid(cursorFollowUp.getString(58));
                    leadFollowUp.setPurorsid(cursorFollowUp.getString(59));
                    leadFollowUp.setLeasid(cursorFollowUp.getString(60));
                    //TODO - Added on 10th May
                    leadFollowUp.setParentFollowupId(cursorFollowUp.getString(61));
                    leadFollowUpList.add(leadFollowUp);
                } while (cursorFollowUp.moveToNext());
            }

            lead.setFollowups(leadFollowUpList);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lead;
    }

    // To get new leads
    public List<Lead> getNewLeads() {
        // Select All Query

        List<Lead> newleads = new ArrayList<Lead>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEASID + "=" + "1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lead lead = new Lead();
                lead.setID(cursor.getInt(0));
                lead.setLeaid(cursor.getString(1));
                lead.setOwnerId(cursor.getString(2));
                lead.setLeatid(cursor.getString(3));
                lead.setName(cursor.getString(4));
                lead.setLeadPersonType(cursor.getString(5));
                lead.setOffAddr(cursor.getString(6));
                lead.setWebsite(cursor.getString(7));
                lead.setFname(cursor.getString(8));
                lead.setLname(cursor.getString(9));
                lead.setCompanyName(cursor.getString(10));
                lead.setSyncId(cursor.getString(11));
                lead.setGenderid(cursor.getString(12));
                lead.setMobile(cursor.getString(13));
                lead.setEmail(cursor.getString(14));
                lead.setAssignedUid(cursor.getString(15));
                lead.setLeasid(cursor.getString(16));
                lead.setEnid(cursor.getString(17));
                lead.setQoid(cursor.getString(18));
                lead.setCancelReason(cursor.getString(19));
                lead.setSpecialInstructions(cursor.getString(20));
                lead.setCreatedUid(cursor.getString(21));
                lead.setUpdatedUid(cursor.getString(22));
                lead.setCreatedTs(cursor.getString(23));
                lead.setUpdatedTs(cursor.getString(24));
                lead.setLeadSync(cursor.getString(25));
                lead.setLeadNumber(cursor.getString(26));
                lead.setLeadId(cursor.getString(27));
                newleads.add(lead);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return lead list
        return newleads;
    }

    //Get Lead Status Id
    public String getLeadStatusId(String id) {
        String statusId = "";
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEAD + "=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        if (id != null && !id.isEmpty()) {
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    statusId = cursor.getString(16);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return statusId;
    }

    // To get new leads on basis of lead id
    public List<Lead> getFreshLeads(String id) {
        // Select All Query
        List<Lead> newleads = new ArrayList<Lead>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEAD + "=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lead lead = new Lead();
                lead.setID(cursor.getInt(0));
                lead.setLeaid(cursor.getString(1));
                lead.setOwnerId(cursor.getString(2));
                lead.setLeatid(cursor.getString(3));
                lead.setName(cursor.getString(4));
                lead.setLeadPersonType(cursor.getString(5));
                lead.setOffAddr(cursor.getString(6));
                lead.setWebsite(cursor.getString(7));
                lead.setFname(cursor.getString(8));
                lead.setLname(cursor.getString(9));
                lead.setCompanyName(cursor.getString(10));
                lead.setSyncId(cursor.getString(11));
                lead.setGenderid(cursor.getString(12));
                lead.setMobile(cursor.getString(13));
                lead.setEmail(cursor.getString(14));
                lead.setAssignedUid(cursor.getString(15));
                lead.setLeasid(cursor.getString(16));
                lead.setEnid(cursor.getString(17));
                lead.setQoid(cursor.getString(18));
                lead.setCancelReason(cursor.getString(19));
                lead.setSpecialInstructions(cursor.getString(20));
                lead.setCreatedUid(cursor.getString(21));
                lead.setUpdatedUid(cursor.getString(22));
                lead.setCreatedTs(cursor.getString(23));
                lead.setUpdatedTs(cursor.getString(24));
                lead.setLeadSync(cursor.getString(25));
                lead.setLeadNumber(cursor.getString(26));
                lead.setLeadId(cursor.getString(27));
                newleads.add(lead);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return lead list
        return newleads;
    }


    //Insert Record Follow up Data
    public int insertRecordFollowUps(String foid, String strLeaId, String strEnid, String strQoid, String strChkoid,
                                     String strCuid, String strJocaid, String selectedRadioButton, String strScheduleDate,
                                     String strAlertTime, String strCommunicatedMode, String strOutcome, String strDescription,
                                     String strReason, String strAssignee, String strContactPerson, String strAlertMode,
                                     String strComment, String strContactedPerson, String strCommunicationMode, boolean sync,
                                     String syncId, String strCheckNextFollowUp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FOID, foid);
        values.put(KEY_FOLLOWUP_LEAD_ID, strLeaId);
        values.put(KEY_ENID, strEnid);
        values.put(KEY_QOID, strQoid);
        values.put(KEY_CHECKID, strChkoid);
        values.put(KEY_CUID, strCuid);
        values.put(KEY_JOCAID, strJocaid);
        values.put(KEY_SCHEDULE_RADIO, selectedRadioButton);
        values.put(KEY_SCHEDULE_TIME, strScheduleDate);
        values.put(KEY_ALERT_TIME, strAlertTime);
        values.put(KEY_COMMUNICATED_MODE, strCommunicatedMode);
        values.put(KEY_OUTCOME, strOutcome);
        values.put(KEY_DESCRIPTION, strDescription);
        values.put(KEY_REASON, strReason);
        values.put(KEY_ASSIGNEE_CONTACT_NAME, strAssignee);
        values.put(KEY_CONTACT_PERSON, strContactPerson);
        values.put(KEY_ALERT_MODE, strAlertMode);
        values.put(KEY_COMMENT, strComment);
        values.put(KEY_CONTACTED_PERSON, strContactedPerson);
        values.put(KEY_COMMUNICATION_MODE, strCommunicationMode);
        values.put(KEY_SYNC, sync);
        values.put(KEY_SYNC_ID, syncId);
        values.put(KEY_NEXT_FOLLOWUP, strCheckNextFollowUp);
        int id = (int) db.insert(TABLE_RECORD_FOLLOWUPS, null, values);
        db.close();
        return id;
    }

    //Update Record Follow up Data
    public int updateRecordFollowUps(String foid, String strLeaId, String strEnid, String strQoid, String strChkoid,
                                     String strCuid, String strJocaid, String selectedRadioButton, String strScheduleDate,
                                     String strAlertTime, String strCommunicatedMode, String strOutcome, String strDescription,
                                     String strReason, String strAssignee, String strContactPerson, String strAlertMode,
                                     String strComment, String strContactedPerson, String strCommunicationMode, boolean sync,
                                     String syncId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FOLLOWUP_LEAD_ID, strLeaId);
        values.put(KEY_ENID, strEnid);
        values.put(KEY_QOID, strQoid);
        values.put(KEY_CHECKID, strChkoid);
        values.put(KEY_CUID, strCuid);
        values.put(KEY_JOCAID, strJocaid);
        values.put(KEY_SCHEDULE_RADIO, selectedRadioButton);
        values.put(KEY_SCHEDULE_TIME, strScheduleDate);
        values.put(KEY_ALERT_TIME, strAlertTime);
        values.put(KEY_COMMUNICATED_MODE, strCommunicatedMode);
        values.put(KEY_OUTCOME, strOutcome);
        values.put(KEY_DESCRIPTION, strDescription);
        values.put(KEY_REASON, strReason);
        values.put(KEY_ASSIGNEE_CONTACT_NAME, strAssignee);
        values.put(KEY_CONTACT_PERSON, strContactPerson);
        values.put(KEY_ALERT_MODE, strAlertMode);
        values.put(KEY_COMMENT, strComment);
        values.put(KEY_CONTACTED_PERSON, strContactedPerson);
        values.put(KEY_COMMUNICATION_MODE, strCommunicationMode);
        values.put(KEY_SYNC, sync);
        values.put(KEY_SYNC_ID, syncId);
        if (foid != null)
            db.update(TABLE_RECORD_FOLLOWUPS, values, KEY_FOID + "=" + foid, null);
        else db.update(TABLE_RECORD_FOLLOWUPS, values, KEY_SYNC_ID + "=" + syncId, null);
        db.close();
        return 0;
    }

    //Get Single Follow Up row
    public List<RecordFollowUp> getFalseRecordFollowUp(boolean status) {
        List<RecordFollowUp> recordFollowUps = new ArrayList<RecordFollowUp>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECORD_FOLLOWUPS;//+ " WHERE " + KEY_SYNC + "='" + status + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RecordFollowUp recordFollowUp = new RecordFollowUp();
                recordFollowUp.setFoId(cursor.getString(1));
                recordFollowUp.setFollowup_leadId(cursor.getString(2));
                recordFollowUp.setEnId(cursor.getString(3));
                recordFollowUp.setQoId(cursor.getString(4));
                recordFollowUp.setChkId(cursor.getString(5));
                recordFollowUp.setCuId(cursor.getString(6));
                recordFollowUp.setJocaId(cursor.getString(7));
                recordFollowUp.setSchdeuled_radio(cursor.getString(8));
                recordFollowUp.setScheduled_time(cursor.getString(9));
                recordFollowUp.setAlert_time(cursor.getString(10));
                recordFollowUp.setCommunicated_mode(cursor.getString(11));
                recordFollowUp.setOutcome(cursor.getString(12));
                recordFollowUp.setDescription(cursor.getString(13));
                recordFollowUp.setReason(cursor.getString(14));
                recordFollowUp.setAssignee_name(cursor.getString(15));
                recordFollowUp.setContact_person(cursor.getString(16));
                recordFollowUp.setAlert_mode(cursor.getString(17));
                recordFollowUp.setComment(cursor.getString(18));
                recordFollowUp.setContacted_person(cursor.getString(19));
                recordFollowUp.setSync(Integer.parseInt(cursor.getString(20)));
                recordFollowUp.setSync_id(cursor.getString(22));
                recordFollowUp.setCommunication_mode(cursor.getString(21));
                recordFollowUp.setNext_followup(cursor.getString(23));
                Log.e("TEST", cursor.getString(20) + "");
                recordFollowUps.add(recordFollowUp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return lead list
        return recordFollowUps;
    }


    // Getting All followups
    public List<FollowUp> getAllfollowUp() {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();

        //TODO - Updated Query on 28th April to show all followups of quotation also
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE "
                + " (( " + KEY_FOLLOW_TYPE_STATUS_ID + " NOT IN(3,4) AND " + KEY_FOLLOWUP_LEAD_ID
                + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != '' " +
                "AND " + KEY_FOLLOWUP_LEAD_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_STATUS_ID + " != '' AND " +
                KEY_FOLLOWUP_LEAD_STATUS_ID + " NOT IN(3,4)" + ")" + " OR " +
                "( " + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != '' )" + " OR " +
                "( " + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_ENQUIRY_ID + " != '' )"
                + " OR " + "( " + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " +
                KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != '' ))" + " AND "
                + " ((" + KEY_FOLLOWUP_LEAD_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != '' )" +
                " OR (" + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != ''  AND " +
                KEY_FOLLOWUP_CHKORDER_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID + " != '' AND " +
                KEY_FOLLOWUP_CHKORDER_STATUS_ID + " NOT IN(5,6,7,10,11) )" +
                " OR (" + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " != '' )" +
                " OR (" + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != '' " +
                "AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " != '' AND " +
                KEY_FOLLOWUP_QUOTATION_STATUS_ID + " NOT IN(1,3)" + ")" +
                " OR (" + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_ENQUIRY_ID + " != '' " +
                "AND " + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " != '' AND " +
                KEY_FOLLOWUP_ENQUIRY_STATUS_ID + " NOT IN(1,3,5)" + ")" +
                ")" + " ORDER BY " + KEY_FOSID + " ASC ,"
                + KEY_SCHEDULED_DATE + " DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorFollowUp = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursorFollowUp.moveToFirst()) {
            do {
                FollowUp leadFollowUp = new FollowUp();
                leadFollowUp.setId(cursorFollowUp.getInt(0));
                leadFollowUp.setAlertOn(cursorFollowUp.getString(1));
                leadFollowUp.setColor(cursorFollowUp.getInt(2));
                leadFollowUp.setContactPerson(cursorFollowUp.getString(3));
                leadFollowUp.setName(cursorFollowUp.getString(4));
                leadFollowUp.setFosid(cursorFollowUp.getString(5));
                leadFollowUp.setFoid(cursorFollowUp.getString(6));
                leadFollowUp.setScheduledDate(cursorFollowUp.getString(7));
                leadFollowUp.setFollowupType(cursorFollowUp.getString(8));
                leadFollowUp.setLeadId(cursorFollowUp.getString(9));
                leadFollowUp.setCuid(cursorFollowUp.getString(10));
                leadFollowUp.setFollowupTypeName(cursorFollowUp.getString(11));
                leadFollowUp.setFollowupTypeStatus(cursorFollowUp.getString(12));
                leadFollowUp.setFollowupTypeStatusId(cursorFollowUp.getString(13));
                leadFollowUp.setTakenOn(cursorFollowUp.getString(14));
                leadFollowUp.setComment(cursorFollowUp.getString(15));
                leadFollowUp.setReason(cursorFollowUp.getString(16));
                leadFollowUp.setFeedback(cursorFollowUp.getString(17));
                leadFollowUp.setFollowupOutcome(cursorFollowUp.getString(18));
                leadFollowUp.setFollowupCommunicationMode(cursorFollowUp.getString(19));
                leadFollowUp.setAlertMode(cursorFollowUp.getString(20));
                leadFollowUp.setAssigned_user(cursorFollowUp.getString(21));
                leadFollowUp.setCreatedUser(cursorFollowUp.getString(22));
                leadFollowUp.setUpdatedUser(cursorFollowUp.getString(23));
                leadFollowUp.setCreatedTs(cursorFollowUp.getString(24));
                leadFollowUp.setUpdatedTs(cursorFollowUp.getString(25));
                leadFollowUp.setPerson_type(cursorFollowUp.getString(26));
                leadFollowUp.setSyncId(cursorFollowUp.getString(27));
                leadFollowUp.setSyncStatus(cursorFollowUp.getString(28));
                leadFollowUp.setContactPersonMobile(cursorFollowUp.getString(29));
                leadFollowUp.setContactPersonEmail(cursorFollowUp.getString(30));
                leadFollowUp.setCodeid(cursorFollowUp.getString(31));
                leadFollowUp.setCommid(cursorFollowUp.getString(32));
                leadFollowUp.setFotid(cursorFollowUp.getString(33));
                leadFollowUp.setAssignedUid(cursorFollowUp.getString(34));
                leadFollowUp.setContactedPerson(cursorFollowUp.getString(35));
                leadFollowUp.setParentId(cursorFollowUp.getString(36));
                leadFollowUp.setLeadLocalId(cursorFollowUp.getString(37));
                leadFollowUp.setContactsId(cursorFollowUp.getString(38));
                leadFollowUp.setEnid(cursorFollowUp.getString(39));
                leadFollowUp.setQoid(cursorFollowUp.getString(40));
                leadFollowUp.setChkoid(cursorFollowUp.getString(41));
                leadFollowUp.setPurorid(cursorFollowUp.getString(42));
                leadFollowUp.setVenid(cursorFollowUp.getString(43));
                leadFollowUp.setFollowupNumber(cursorFollowUp.getString(44));
                leadFollowUp.setFollowupId(cursorFollowUp.getString(45));
                leadFollowUp.setEnquiryNumber(cursorFollowUp.getString(46));
                leadFollowUp.setEnquiryId(cursorFollowUp.getString(47));
                leadFollowUp.setQuotationId(cursorFollowUp.getString(48));
                leadFollowUp.setQuotationNumber(cursorFollowUp.getString(49));
                leadFollowUp.setOrderSequenceNumber(cursorFollowUp.getString(50));
                leadFollowUp.setOrderId(cursorFollowUp.getString(51));
                leadFollowUp.setPurchaseOrderId(cursorFollowUp.getString(52));
                leadFollowUp.setPurchaseOrderNumber(cursorFollowUp.getString(53));
                leadFollowUp.setLeadIdR(cursorFollowUp.getString(54));
                leadFollowUp.setLeadNumber(cursorFollowUp.getString(55));
                //TODO - Added on 19th April
                leadFollowUp.setQosid(cursorFollowUp.getString(56));
                leadFollowUp.setEnsid(cursorFollowUp.getString(57));
                leadFollowUp.setChkosid(cursorFollowUp.getString(58));
                leadFollowUp.setPurorsid(cursorFollowUp.getString(59));
                leadFollowUp.setLeasid(cursorFollowUp.getString(60));
                //TODO - Added on 10th May
                leadFollowUp.setParentFollowupId(cursorFollowUp.getString(61));
                followUpList.add(leadFollowUp);
                // Adding followup to list
            } while (cursorFollowUp.moveToNext());
        }
        db.close();
        // return followUps list
        return followUpList;
    }

    // Getting All Non Sync followups
    public List<FollowUp> getAllNonSyncfollowUp() {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS
                + " ORDER BY " + KEY_FOSID + " ASC ,"
                + KEY_SCHEDULED_DATE + " DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorFollowUp = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursorFollowUp.moveToFirst()) {
            do {
                FollowUp leadFollowUp = new FollowUp();
                leadFollowUp.setId(cursorFollowUp.getInt(0));
                leadFollowUp.setAlertOn(cursorFollowUp.getString(1));
                leadFollowUp.setColor(cursorFollowUp.getInt(2));
                leadFollowUp.setContactPerson(cursorFollowUp.getString(3));
                leadFollowUp.setName(cursorFollowUp.getString(4));
                leadFollowUp.setFosid(cursorFollowUp.getString(5));
                leadFollowUp.setFoid(cursorFollowUp.getString(6));
                leadFollowUp.setScheduledDate(cursorFollowUp.getString(7));
                leadFollowUp.setFollowupType(cursorFollowUp.getString(8));
                leadFollowUp.setLeadId(cursorFollowUp.getString(9));
                leadFollowUp.setCuid(cursorFollowUp.getString(10));
                leadFollowUp.setFollowupTypeName(cursorFollowUp.getString(11));
                leadFollowUp.setFollowupTypeStatus(cursorFollowUp.getString(12));
                leadFollowUp.setFollowupTypeStatusId(cursorFollowUp.getString(13));
                leadFollowUp.setTakenOn(cursorFollowUp.getString(14));
                leadFollowUp.setComment(cursorFollowUp.getString(15));
                leadFollowUp.setReason(cursorFollowUp.getString(16));
                leadFollowUp.setFeedback(cursorFollowUp.getString(17));
                leadFollowUp.setFollowupOutcome(cursorFollowUp.getString(18));
                leadFollowUp.setFollowupCommunicationMode(cursorFollowUp.getString(19));
                leadFollowUp.setAlertMode(cursorFollowUp.getString(20));
                leadFollowUp.setAssigned_user(cursorFollowUp.getString(21));
                leadFollowUp.setCreatedUser(cursorFollowUp.getString(22));
                leadFollowUp.setUpdatedUser(cursorFollowUp.getString(23));
                leadFollowUp.setCreatedTs(cursorFollowUp.getString(24));
                leadFollowUp.setUpdatedTs(cursorFollowUp.getString(25));
                leadFollowUp.setPerson_type(cursorFollowUp.getString(26));
                leadFollowUp.setSyncId(cursorFollowUp.getString(27));
                leadFollowUp.setSyncStatus(cursorFollowUp.getString(28));
                leadFollowUp.setContactPersonMobile(cursorFollowUp.getString(29));
                leadFollowUp.setContactPersonEmail(cursorFollowUp.getString(30));
                leadFollowUp.setCodeid(cursorFollowUp.getString(31));
                leadFollowUp.setCommid(cursorFollowUp.getString(32));
                leadFollowUp.setFotid(cursorFollowUp.getString(33));
                leadFollowUp.setAssignedUid(cursorFollowUp.getString(34));
                leadFollowUp.setContactedPerson(cursorFollowUp.getString(35));
                leadFollowUp.setParentId(cursorFollowUp.getString(36));
                leadFollowUp.setLeadLocalId(cursorFollowUp.getString(37));
                leadFollowUp.setContactsId(cursorFollowUp.getString(38));
                leadFollowUp.setEnid(cursorFollowUp.getString(39));
                leadFollowUp.setQoid(cursorFollowUp.getString(40));
                leadFollowUp.setChkoid(cursorFollowUp.getString(41));
                leadFollowUp.setPurorid(cursorFollowUp.getString(42));
                leadFollowUp.setVenid(cursorFollowUp.getString(43));
                leadFollowUp.setFollowupNumber(cursorFollowUp.getString(44));
                leadFollowUp.setFollowupId(cursorFollowUp.getString(45));
                leadFollowUp.setEnquiryNumber(cursorFollowUp.getString(46));
                leadFollowUp.setEnquiryId(cursorFollowUp.getString(47));
                leadFollowUp.setQuotationId(cursorFollowUp.getString(48));
                leadFollowUp.setQuotationNumber(cursorFollowUp.getString(49));
                leadFollowUp.setOrderSequenceNumber(cursorFollowUp.getString(50));
                leadFollowUp.setOrderId(cursorFollowUp.getString(51));
                leadFollowUp.setPurchaseOrderId(cursorFollowUp.getString(52));
                leadFollowUp.setPurchaseOrderNumber(cursorFollowUp.getString(53));
                leadFollowUp.setLeadIdR(cursorFollowUp.getString(54));
                leadFollowUp.setLeadNumber(cursorFollowUp.getString(55));
                //TODO - Added on 19th April
                leadFollowUp.setQosid(cursorFollowUp.getString(56));
                leadFollowUp.setEnsid(cursorFollowUp.getString(57));
                leadFollowUp.setChkosid(cursorFollowUp.getString(58));
                leadFollowUp.setPurorsid(cursorFollowUp.getString(59));
                leadFollowUp.setLeasid(cursorFollowUp.getString(60));
                //TODO - Added on 10th May
                leadFollowUp.setParentFollowupId(cursorFollowUp.getString(61));
                followUpList.add(leadFollowUp);
                // Adding followup to list
            } while (cursorFollowUp.moveToNext());
        }
        db.close();
        // return followUps list
        return followUpList;
    }


    public void updateLeadSync(String id, String strTrue, String syncID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_LEADS + " SET "
                + KEY_LEAD_SYNC + " = '" + strTrue + "', "
                + KEY_LEAD + " = '" + id + ", "
                + "' WHERE " + KEY_LEAD_SYNC_ID + " = " + syncID;
        db.execSQL(sql);
    }

    public void addAssigneSpinner(ChatContacts chatContacts) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ASSIGNEE_CONTACT_NAME, WordUtils.capitalize(chatContacts.getName()));
        values.put(KEY_ASSIGNE_MOBILE, chatContacts.getMobile());
        values.put(KEY_ASSIGNEE_CONTACT_ID, chatContacts.getUid());
        values.put(KEY_USER_IMAGE, chatContacts.getPhoto());
        values.put(KEY_IMAGE_PATH, chatContacts.getImagePath());
        values.put(KEY_DESIGNATION, chatContacts.getDesignation());
        db.insert(TABLE_ASSIGNEE, null, values);
        Log.v("TABLE ASSIGNE SPINNER", chatContacts.getPhoto());
        db.close();
    }

    //Check UId is present or not
    public boolean checkAssigneeUId(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_ASSIGNEE + " WHERE " + KEY_ASSIGNEE_CONTACT_ID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    //Update Assignee Data
    public void updateAssigneeData(ChatContacts chatContacts) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ASSIGNEE_CONTACT_NAME, WordUtils.capitalize(chatContacts.getName()));
        values.put(KEY_ASSIGNE_MOBILE, chatContacts.getMobile());
        values.put(KEY_USER_IMAGE, chatContacts.getPhoto());
        values.put(KEY_IMAGE_PATH, chatContacts.getImagePath());
        values.put(KEY_DESIGNATION, chatContacts.getDesignation());
        db.update(TABLE_ASSIGNEE, values, KEY_ASSIGNEE_CONTACT_ID + "=" + chatContacts.getUid(), null);
        db.close();
    }

    public void updateAssigneSpinner(ChatContacts chatContacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ONLINE_STATUS, chatContacts.getOnlineStatus());
        db.update(TABLE_ASSIGNEE, values, KEY_ASSIGNEE_CONTACT_ID + "=" + chatContacts.getUid(), null);
        Log.d("TABLE_CHAT_MESSAGES", chatContacts.getUid() + " " + chatContacts.getOnlineStatus());
        db.close();
    }

    // Getting All assignee Spinners
    public List<ChatContacts> getAllAssignee() {
        List<ChatContacts> assigneeList = new ArrayList<ChatContacts>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNEE + " ORDER BY " + KEY_ASSIGNEE_CONTACT_NAME + " COLLATE NOCASE";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatContacts chatContacts = new ChatContacts();
                chatContacts.setUid(cursor.getInt(1));
                chatContacts.setName(cursor.getString(2));
                chatContacts.setMobile(cursor.getString(3));
                chatContacts.setImagePath(cursor.getString(4));
                chatContacts.setPhoto(cursor.getString(5));
                chatContacts.setOnlineStatus(cursor.getString(6));
                chatContacts.setDesignation(cursor.getString(7));
                // Adding followup to list
                assigneeList.add(chatContacts);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return assigneeList;
    }


    // insert activity feed  in table
    public void insertActivityFeedsData(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UAID, notification.getUaid());
        values.put(KEY_UID, notification.getUid());
        values.put(KEY_MID, notification.getMid());
        values.put(KEY_MOTAID, notification.getMotaid());
        values.put(KEY_MESSAGE, notification.getMessage());
        values.put(KEY_POST_CREATED_TS, notification.getCreatedTs());
        values.put(KEY_ICON, notification.getFontawesomeicons());
        values.put(KEY_ICON_CODES, notification.getFontawesomeicons());
        values.put(KEY_COLOUR_CODE, notification.getFontbackgroundcolor());
        db.insert(TABLE_ACTIVITY_FEEDS, null, values);
        db.close();
    }

    //Check KEY_UAID is present or not
    public boolean checkNotificationUAID(String uAID) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        boolean exists = false;
        try {
            String Query = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS + " WHERE " + KEY_UAID + "=?";
            Cursor cursor = sqldb.rawQuery(Query, new String[]{uAID});
            // if (sqldb.isOpen() && cursor != null) {
            exists = cursor.moveToFirst();
            cursor.close();
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    //Check KEY_UACID is present or not (Comment Id)
    public boolean checkCommentUACID(String uACID) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS_COMMENT + " WHERE " + KEY_UACID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{uACID});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    //Check KEY_UACID is present or not (Comment Sync Id)
    public boolean checkCommentSyncID(String syncId) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS_COMMENT + " WHERE " + KEY_COMMENT_SYNC_ID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{syncId});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    // Updating activity feeds  in table
    public void updateActivityFeeds(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, notification.getUid());
        values.put(KEY_MID, notification.getMid());
        values.put(KEY_MOTAID, notification.getMotaid());
        values.put(KEY_MESSAGE, notification.getMessage());
        values.put(KEY_POST_CREATED_TS, notification.getCreatedTs());
        values.put(KEY_ICON, notification.getFontawesomeicons());
        values.put(KEY_ICON_CODES, notification.getFontawesomeicons());
        values.put(KEY_COLOUR_CODE, notification.getFontbackgroundcolor());
        if (db.isOpen())
            db.update(TABLE_ACTIVITY_FEEDS, values, KEY_UAID + "=" + notification.getUaid(), null);
        db.close();
    }

    // Fetching All Activity Feeds
    public List<Notification> getAllActivityFeeds() {
        List<Notification> notificationList = new ArrayList<Notification>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS + " ORDER BY " + KEY_UID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setUaid(String.valueOf(cursor.getInt(1)));
                notification.setUid(cursor.getString(2));
                notification.setMid(String.valueOf(cursor.getInt(3)));
                notification.setMotaid(cursor.getString(4));
                notification.setMessage(cursor.getString(5));
                notification.setCreatedTs(cursor.getString(6));
                notification.setFontawesomeicons(cursor.getString(7));
                notification.setFontbackgroundcolor(cursor.getString(9));
                // Adding feeds to list
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        db.close();
        // return feeds list
        return notificationList;
    }

    // Fetching All Activity Feeds of last week
    public List<Notification> getLastWeekActivityFeeds(String count, int start) {
        List<Notification> notificationList = new ArrayList<Notification>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS
                //+ " where " + KEY_POST_CREATED_TS
                //+ " BETWEEN '" + startdate + "' AND '" + enddate + "'"
                + " ORDER BY " + KEY_POST_CREATED_TS + " DESC limit " + count + " OFFSET " + start;
        //+ " ORDER BY " + KEY_POST_CREATED_TS + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setUaid(String.valueOf(cursor.getInt(1)));
                notification.setUid(cursor.getString(2));
                notification.setMid(String.valueOf(cursor.getInt(3)));
                notification.setMotaid(cursor.getString(4));
                notification.setMessage(cursor.getString(5));
                notification.setCreatedTs(cursor.getString(6));
                notification.setFontawesomeicons(cursor.getString(7));
                notification.setFontbackgroundcolor(cursor.getString(9));
                // Adding feeds to list
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        db.close();
        // return feeds list
        return notificationList;
    }

    // Fetching All Activity Feeds
    public List<Notification> getLastWeekActivityFeedsForAnUser(String startdate, String enddate, String uAId) {
        List<Notification> notificationList = new ArrayList<Notification>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS + " where " + KEY_UAID + "='" + uAId + "'";//+ " AND " + KEY_POST_CREATED_TS
        //   + " BETWEEN '" + startdate + "' AND '" + enddate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setUaid(String.valueOf(cursor.getInt(1)));
                notification.setUid(cursor.getString(2));
                notification.setMid(String.valueOf(cursor.getInt(3)));
                notification.setMotaid(cursor.getString(4));
                notification.setMessage(cursor.getString(5));
                notification.setCreatedTs(cursor.getString(6));
                notification.setFontawesomeicons(cursor.getString(7));
                notification.setFontbackgroundcolor(cursor.getString(9));
                // Adding feeds to list
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        db.close();
        // return feeds list
        return notificationList;
    }

    //Fetch UAID From Comments Table
    public List<String> getCommentUaid(String uid) {
        List<String> uAIDList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS_COMMENT + " where " + KEY_UID + "=" + uid
                + " OR " + KEY_POST_UID + "=" + uid
                + " GROUP BY " + KEY_UAID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String uAid = new String();
                uAid = cursor.getString(3);
                uAIDList.add(uAid);
            } while (cursor.moveToNext());
        }
        db.close();
        return uAIDList;
    }

    //Delete Activity Feeds Table
    public void deleteActivityFeedsTable() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_ACTIVITY_FEEDS);
        sqLiteDatabase.close();
    }

    // insert activity feeds comments in table
    public void insertActivityFeedsComments(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UACID, comment.getUacid());
        values.put(KEY_UAID, comment.getUaid());
        values.put(KEY_UID, comment.getUid());
        values.put(KEY_MESSAGE, comment.getComment());
        values.put(KEY_POST_CREATED_TS, comment.getCreatedTs());
        values.put(KEY_SID, comment.getSid());
        values.put(KEY_COMMENT_SYNC_ID, comment.getSync_id());
        values.put(KEY_SYNC, comment.getSync());
        values.put(KEY_POST_UID, comment.getPost_uid());
        db.insert(TABLE_ACTIVITY_FEEDS_COMMENT, null, values);
        db.close();
    }

    // updateCount activity feeds comments in table
    public void updateActivityFeedsComments(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UAID, comment.getUaid());
        values.put(KEY_UID, comment.getUid());
        values.put(KEY_SYNC, comment.getSync());
        values.put(KEY_POST_UID, comment.getPost_uid());
        db.update(TABLE_ACTIVITY_FEEDS_COMMENT, values, KEY_COMMENT_SYNC_ID + "=" + comment.getSync_id(), null);
        db.close();
    }

    // updateCount activity feeds comments completely in table
    public void updateFeedsCommentsDetail(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UACID, comment.getUacid());
        values.put(KEY_UAID, comment.getUaid());
        values.put(KEY_UID, comment.getUid());
        values.put(KEY_MESSAGE, comment.getComment());
        values.put(KEY_POST_CREATED_TS, comment.getCreatedTs());
        values.put(KEY_SID, comment.getSid());
        values.put(KEY_SYNC, comment.getSync());
        values.put(KEY_POST_UID, comment.getPost_uid());
        //values.put(KEY_COMMENT_SYNC_ID, comment.getSync_id());
        db.update(TABLE_ACTIVITY_FEEDS_COMMENT, values, KEY_COMMENT_SYNC_ID + "='" + comment.getSync_id() + "'", null);
        db.close();
    }

    //Delete Activity Feeds Comments Table
    public void deleteActivityFeedsCommentsTable() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_ACTIVITY_FEEDS_COMMENT);
        sqLiteDatabase.close();
    }

    // Fetching All Activity Feeds
    public List<Comment> getActivityFeedsComments(String id) {
        List<Comment> comments = new ArrayList<Comment>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS_COMMENT + " where " + KEY_UAID + "=" + id + "";
        //   + " ORDER BY " + KEY_UID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment();
                comment.setUacid(cursor.getString(1));
                comment.setUid(cursor.getString(2));
                //   comment.setUaid(cursor.getString(3));
                comment.setComment(cursor.getString(4));
                comment.setCreatedTs(cursor.getString(5));
                comment.setSid(cursor.getString(6));
                comment.setSync_id(cursor.getString(7));
                comment.setSync(cursor.getInt(8));
                comment.setPost_uid(cursor.getString(9));
                // Adding feeds comments to list
                comments.add(comment);
            } while (cursor.moveToNext());
        }
        db.close();
        // return comments list
        return comments;
    }

    //Get Pending Comments
    public List<Comment> getPendingComments() {
        List<Comment> comments = new ArrayList<Comment>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS_COMMENT + " where " + KEY_UACID + "=0";
        //   + " ORDER BY " + KEY_UID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment();
                //comment.setUacid(cursor.getString(1));
                comment.setUid(cursor.getString(2));
                comment.setUaid(cursor.getString(3));
                comment.setComment(cursor.getString(4));
                comment.setCreatedTs(cursor.getString(5));
                comment.setSid(cursor.getString(6));
                comment.setSync_id(cursor.getString(7));
                comment.setSync(cursor.getInt(8));
                comment.setPost_uid(cursor.getString(9));
                // Adding feeds comments to list
                comments.add(comment);
            } while (cursor.moveToNext());
        }
        db.close();
        // return comments list
        return comments;
    }

    //Inserting Push Notifications
    public void insertPushNotification(PushNotifications pushNotifications, String show) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PUSH_NOTIFICATION_FOID, pushNotifications.getFoid());
        values.put(KEY_PUSH_NOTIFICATION_MESSAGE, pushNotifications.getMessage());
        values.put(KEY_PUSH_NOTIFICATION_SHOW, show);
        values.put(KEY_PUSH_NOTIFICATION_LEAID, pushNotifications.getLeaid());
        values.put(KEY_PUSH_NOTIFICATION_UAID, pushNotifications.getUaid());
        db.insert(TABLE_PUSH_NOTIFICATION, null, values);
        db.close();
    }

    // Follow up pending count
    public int followUpsPendingCount() {
        int count = 0;
        //TODO - Changed Query on 3rd May
        String selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " where " + KEY_FOSID + " = 1 AND "
                + " (( " + KEY_FOLLOW_TYPE_STATUS_ID + " NOT IN(3,4) AND " + KEY_FOLLOWUP_LEAD_ID
                + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != '' )" + " OR " +
                "( " + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != '' )"
                + " OR " + "( " + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " +
                KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != '' ))" + " AND "
                + " ((" + KEY_FOLLOWUP_LEAD_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != '' )" +
                " OR (" + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_CUSTOMER_ORDER_ID + " != ''  AND " +
                KEY_FOLLOWUP_CHKORDER_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_CHKORDER_STATUS_ID + " != '' AND " +
                KEY_FOLLOWUP_CHKORDER_STATUS_ID + " NOT IN(5,6,7,10,11) )" +
                " OR (" + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " != '' )" +
                " OR (" + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != '' " +
                "AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + " != '' AND " +
                KEY_FOLLOWUP_QUOTATION_STATUS_ID + " NOT IN(1,3)" + ")" +
                " OR (" + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_ENQUIRY_ID +
                " != '' ))"; /*AND "
                + KEY_FOLLOWUP_LEAD_ID + " IS NOT NULL AND " + KEY_FOLLOWUP_LEAD_ID + " != ''" + " OR "
                + KEY_FOLLOW_UP_ENQUIRY_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_ENQUIRY_ID + " != ''" + " OR "
                + KEY_FOLLOW_UP_QUOTATION_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_QUOTATION_ID + " != ''" + " OR "
                + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " IS NOT NULL AND " + KEY_FOLLOW_UP_PURCHASE_ORDER_ID + " != ''";*/
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    //Lead new count
    public int leadNewCount() {
        int count = 0;
        String selectQuery = "SELECT * FROM " + TABLE_LEADS + " where " + KEY_LEASID + "=1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (db.isOpen()) {
            count = cursor.getCount();
            cursor.close();
            db.close();
        }

        return count;

    }

    //Get Followups count on the basis of lead
    public int getFollowupsCount(String leadId) {
        int count = 0;
        String selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS + " where " + KEY_FOLLOWUP_LEAD_ID + "=" + leadId +
                " AND " + KEY_FOSID + "=" + 1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (db.isOpen()) {
            count = cursor.getCount();
            cursor.close();
            db.close();
        }

        return count;

    }

    //Updating Push Notifications
    public void updatePushNotification(PushNotifications pushNotifications, String show) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(KEY_PUSH_NOTIFICATION_FOID, pushNotifications.getFoid());
        values.put(KEY_PUSH_NOTIFICATION_MESSAGE, pushNotifications.getMessage());
        values.put(KEY_PUSH_NOTIFICATION_SHOW, show);
        values.put(KEY_PUSH_NOTIFICATION_LEAID, pushNotifications.getLeaid());
        values.put(KEY_PUSH_NOTIFICATION_TYPE, pushNotifications.getType());
        db.update(TABLE_PUSH_NOTIFICATION, values, KEY_PUSH_NOTIFICATION_FOID + "=" + pushNotifications.getFoid(), null);
        db.close();
    }

    //Get Push Notifications status
    public List<PushNotifications> getPushNotificationStatus(String foid) {
        List<PushNotifications> pushNotificationsList = new ArrayList<PushNotifications>();
        String selectQuery = "SELECT * FROM " + TABLE_PUSH_NOTIFICATION + " where " + KEY_PUSH_NOTIFICATION_FOID + "=" +
                foid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PushNotifications pushNotifications = new PushNotifications();
                pushNotifications.setLeaid(cursor.getString(2));
                pushNotifications.setMessage(cursor.getString(3));
                pushNotifications.setType(cursor.getString(4));
                // Adding notification to list
                pushNotificationsList.add(pushNotifications);
            } while (cursor.moveToNext());
        }
        db.close();
        // return notifications list
        return pushNotificationsList;
    }

    //Check Fo Id is present or not
    public boolean checkFoIdInPush(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_PUSH_NOTIFICATION + " WHERE " + KEY_PUSH_NOTIFICATION_FOID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
        boolean exists = false;
        if (cursor.moveToFirst()) {
            do {
                exists = Boolean.parseBoolean(cursor.getString(6));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqldb.close();
        return exists;
    }

    // Deleting table PushNotifications
    public void deletePushNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_PUSH_NOTIFICATION);
        db.close();
    }

    // Deleting table followupsfilter
    public void deleteFollowUpFilters() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_FILTER_FOLLOWUPS);
        db.close();
    }

    // Deleting table leads
    public void deleteLead() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_LEADS);
        db.close();
    }

    public void deleteLeadContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONTACTS_DETAILS);
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public void deleteLeadAddress() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_LEAD_ADDRESS);
        db.close();
    }


    public void deleteSyncLeadContacts(String syncID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONTACTS_DETAILS + " WHERE " + KEY_LEAD_CONTACTS_SYNC_ID + " = " + syncID + "");
        db.close();
    }

    public void deleteSyncLeadAddress(String syncID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_SYNC_ID + " = " + syncID + "");
        db.close();
    }

    public void deleteSyncLeadFollowUp(String syncID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_SYNC_ID + " = " + syncID + "");
        db.close();
    }

    public void deleteLeadContacts(String leadID) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_CONTACTS_DETAILS + " WHERE " + KEY_LEAD_CONTACTS_LEAID + " = " + leadID + "");
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLeadAddress(String leadID) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_LEAD_ADDRESS + " WHERE " + KEY_LEAD_ADDRESS_LEAID + " = " + leadID + "");
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLeadFollowUp(String leadID) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            //     db.execSQL("delete from " + TABLE_FOLLOWUPS + " WHERE " + KEY_FOLLOWUP_LEAD_ID + " = " + leadID + "");
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteApiSync() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_API_SYNC);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Deleting table followups
    public void deleteFollowUp() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_FOLLOWUPS);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete Chat Messgaes
    public void deleteChatTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_CHAT_MESSAGES);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete Contacts Table
    public void deleteAssigneeContacts() {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("delete from " + TABLE_ASSIGNEE);
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (3 < DATABASE_VERSION) {
                if (!isColumnExists(TABLE_LEADS, KEY_LEAD_NUMBER)) {
                    db.execSQL("ALTER TABLE "
                            + TABLE_LEADS + " ADD COLUMN " + KEY_LEAD_NUMBER + "TEXT");
                }
                db.execSQL("ALTER TABLE "
                        + TABLE_LEADS + " ADD COLUMN " + KEY_LEAD_ID_REPRESENTATION + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_NUMBER + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_FOID_REPRESENTATION + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_ENQUIRY_NUMBER + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_EOID_REPRESENTATION + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_QUID_REPRESENTATION + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_QUOTATION_NUMBER + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_SEQUENCE_NUMBER + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_ORDER_ID_REPRESENTATION + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_POID_REPRESENTATION + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_PURCHASE_ORDER_NUMBER + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_LEAID_REPRESENTATION + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOW_LEAD_NUMBER + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_CONTACTS_DETAILS + " ADD COLUMN " + KEY_LEAD_CONTACTS_IS_OWNER + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_CONTACTS_DETAILS + " ADD COLUMN " + KEY_CONTACTS_QOID + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_CONTACTS_DETAILS + " ADD COLUMN " + KEY_CONTACTS_ENID + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_CONTACTS_DETAILS + " ADD COLUMN " + KEY_CONTACTS_PURCHASE_ORDER_ID + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_CONTACTS_DETAILS + " ADD COLUMN " + KEY_CONTACTS_CUSTOMER_ORDER_ID + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FILTER_FOLLOWUPS + " ADD COLUMN " + KEY_TYPE_SALES_ORDER + "TEXT");
                //Creating Permission table
                String CREATE_USER_PERMISSION = "CREATE TABLE if not exists " + TABLE_USER_PERMISSIONS + "("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + KEY_PERMISSION_ID + " TEXT,"
                        + KEY_NAME + " TEXT,"
                        + KEY_TITLE + " Text" + ")";
                db.execSQL(CREATE_USER_PERMISSION);
                //TODO - Added on 19th April
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOWUP_QUOTATION_STATUS_ID + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOWUP_ENQUIRY_STATUS_ID + "TEXT");
                if (!isColumnExists(TABLE_FOLLOWUPS, KEY_FOLLOWUP_CHKORDER_STATUS_ID)) {
                    db.execSQL("ALTER TABLE "
                            + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOWUP_CHKORDER_STATUS_ID + "TEXT");
                }
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOWUP_PURCHASE_ORDER_STATUS_ID + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOWUP_LEAD_STATUS_ID + "TEXT");
                db.execSQL("ALTER TABLE "
                        + TABLE_FOLLOWUPS + " ADD COLUMN " + KEY_FOLLOWUP_PARENT_ID_REPRESENTATION + "TEXT");
            }

            //TODO Added on 22nd June 2k18
            if (5 < DATABASE_VERSION) {
                if (!isColumnExists(TABLE_FILTER_FOLLOWUPS, KEY_THIS_WEEK)) {
                    db.execSQL("ALTER TABLE "
                            + TABLE_FILTER_FOLLOWUPS + " ADD COLUMN " + KEY_THIS_WEEK + "TEXT");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete Followups Contact Table
    public void deleteContact() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_FOLLOWUP_CONTACTS);
        sqLiteDatabase.close();
    }

    //Delete TABLE RECORD FOLLOWUPS
    public void deleteRecordFollowUps() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_RECORD_FOLLOWUPS);
        sqLiteDatabase.close();
    }

    //Delete Chat Conversations Table
    public void deleteChatConversationsTable() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_CHAT_CONVERSATIONS);
        sqLiteDatabase.close();
    }

    public void deleteDatabase() {
        mContext.deleteDatabase(DATABASE_NAME);
    }

    // Fetching Counts of All Unread Activity Feeds
    public int getCountsUnreadActivityFeeds(String startdate, String enddate) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select count(*) from " + TABLE_ACTIVITY_FEEDS + " where " + KEY_POST_CREATED_TS
                        + " BETWEEN '" + startdate + "' AND '" + enddate + "'" + " ORDER BY " + KEY_POST_CREATED_TS + " DESC "
                , null);
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.close();
            database.close();
            return cursor.getCount();
        } else {
            cursor.close();
            database.close();
            return 0;
        }
    }

    //Get Comments Count
    public int getCommentsCount(String UAID) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_ACTIVITY_FEEDS_COMMENT
                + " where " + KEY_UAID + "='" + UAID + "'", null);
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.close();
            database.close();
            return cursor.getCount();
        } else {
            cursor.close();
            database.close();
            return 0;
        }
    }

    public FollowUp getContactDetails(String id) {
        FollowUp leadFollowUp = new FollowUp();
        String selectQuery = "SELECT  le.mobile, le.email , co.email as email_id, co.phone_no  FROM " + TABLE_FOLLOWUPS + " fu " +
                " LEFT JOIN " + TABLE_CONTACTS_DETAILS + " co ON (fu.follow_up_contact_id = co.primary_key) LEFT JOIN " +
                TABLE_LEADS + " le ON (le.id = fu.follow_up_lead_id) WHERE le.leadid = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorFollowUp = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursorFollowUp.moveToFirst()) {
            do {
                leadFollowUp.setContactPersonMobile(cursorFollowUp.getString(cursorFollowUp.getColumnIndex("phone_no")));
                leadFollowUp.setContactPersonEmail(cursorFollowUp.getString(cursorFollowUp.getColumnIndex("email_id")));
                if (leadFollowUp.getContactPersonMobile() == null) {
                    leadFollowUp.setContactPersonMobile(cursorFollowUp.getString(cursorFollowUp.getColumnIndex("mobile")));
                }
                if (leadFollowUp.getContactPersonEmail() == null) {
                    leadFollowUp.setContactPersonEmail(cursorFollowUp.getString(cursorFollowUp.getColumnIndex("email")));
                }

               /* for (int i = 0; i < cursorFollowUp.getColumnCount(); i++) {
                    if (cursorFollowUp.getString(i) != null)
                        Log.d("join data", cursorFollowUp.getColumnName(i) + "    " + cursorFollowUp.getString(i));
                }*/
                Log.d("join data 1 ", leadFollowUp.getContactPersonEmail() + "    " + leadFollowUp.getContactPersonMobile());

            } while (cursorFollowUp.moveToNext());
        }
        db.close();
        // return followUps list
        return leadFollowUp;
    }

    public void updateLeadsReason(String status, String leadId, String syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LEASID, status);
        values.put(KEY_LEAD_SYNC, syncStatus);
        db.update(TABLE_LEADS, values, KEY_LEAD + "=" + leadId, null);
        db.close();
    }

    public void deleteCancelledLead(String leadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LEADS, KEY_LEAD + "='" + leadId + "' AND " + KEY_LEASID + "='4' AND " +
                KEY_LEAD_SYNC + "='true'", null);
    }

    public void updateLocalLeadID(String leadId, String localLeadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_LEAD_LOCAL_ID, localLeadId);
            //   values.put(KEY_CONTACT_ID,contactId);
            db.update(TABLE_FOLLOWUPS, values, KEY_FOLLOWUP_LEAD_ID + "=" + leadId, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    //Check UAId is present or not
    public boolean checkUaIdInPush(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_PUSH_NOTIFICATION + " WHERE " + KEY_PUSH_NOTIFICATION_UAID + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
        boolean exists = false;
        if (cursor.moveToFirst()) {
            do {
                exists = Boolean.parseBoolean(cursor.getString(6));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqldb.close();
        return exists;
    }

    //Delete Feeds Notify Table
    public void deleteFeedsNotify() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_ACTIVITY_FEEDS_NOTIFICATION);
        sqLiteDatabase.close();
    }

    //Delete Feeds Notify Table
    public void deleteOtpEmails() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_OTP_USER_EMAILS);
        sqLiteDatabase.close();
    }

    //Delete Feeds Notify Table
    public void deleteOtpMobiles() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_OTP_USER_MOBILES);
        sqLiteDatabase.close();
    }

    //Inserting Chat Messages
    public void insertFeedsNotifications(Notify notify) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FEEDS_NOTIFICATION_UANID, notify.getUanid());
        values.put(KEY_FEEDS_NOTIFICATION_UID, notify.getUid());
        values.put(KEY_FEEDS_NOTIFICATION_MOTAID, notify.getMotaid());
        values.put(KEY_FEEDS_NOTIFICATION_MID, notify.getMid());
        db.insert(TABLE_ACTIVITY_FEEDS_NOTIFICATION, null, values);
        db.close();
    }

    //Inserting Emails
    public void insertTwoStepVerificationEmails(OtpEmailFetch otpEmailFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL_USER_ID, otpEmailFetch.getUid());
        values.put(KEY_EMAIL_VERIFY_ID, otpEmailFetch.getAuevid());
        values.put(KEY_EMAIL_VERIFY_STATUS_ID, otpEmailFetch.getAuevsid());
        values.put(KEY_EMAIL_CODE, otpEmailFetch.getCode());
        values.put(KEY_EMAIL_DATA, otpEmailFetch.getData());
        values.put(KEY_USER_EMAIL, otpEmailFetch.getEmail());
        values.put(KEY_PRIMARY_EMAIL, otpEmailFetch.getPrimaryEmail());
        values.put(KEY_EMAIL_UPDATED_TS, otpEmailFetch.getUpdatedTs());
        values.put(KEY_EMAIL_CREATED_TS, otpEmailFetch.getCreatedTs());
        db.insert(TABLE_OTP_USER_EMAILS, null, values);
        db.close();
    }

    //Clear Table of EMails
    public void clearEmailTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OTP_USER_EMAILS, null, null);
        db.close();
    }

    //Check E-mail Address is present or not
    public boolean checkEmailAddress(String email) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_OTP_USER_EMAILS + " WHERE " + KEY_USER_EMAIL + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{email});
        boolean exists = false;
        exists = cursor.moveToFirst();
        cursor.close();
        sqldb.close();
        return exists;
    }

    //Inserting Emails
    public void insertTwoStepVerificationMobiles(OtpMobileFetch otpMobileFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MOBILE_USER_ID, otpMobileFetch.getUid());
        values.put(KEY_MOBILE_VERIFY_ID, otpMobileFetch.getAumvid());
        values.put(KEY_MOBILE_VERIFY_STATUS_ID, otpMobileFetch.getAumvsid());
        values.put(KEY_USER_MOBILE, otpMobileFetch.getMobile());
        values.put(KEY_USER_MOBILE_IMEI, otpMobileFetch.getImei());
        values.put(KEY_USER_MOBILE_GCM_ID, otpMobileFetch.getGcmId());
        values.put(KEY_USER_MOBILE_APP_VERSION, otpMobileFetch.getAppVersion());
        values.put(KEY_MOBILE_CODE, otpMobileFetch.getCode());
        values.put(KEY_MOBILE_DATA, otpMobileFetch.getData());
        values.put(KEY_PRIMARY_MOBILE, otpMobileFetch.getPrimaryNo());
        values.put(KEY_MOBILE_VERIFIED_TS, otpMobileFetch.getVerifiedTs());
        values.put(KEY_MOBILE_CREATED_TS, otpMobileFetch.getCreatedTs());
        db.insert(TABLE_OTP_USER_MOBILES, null, values);
        db.close();
    }

    //Clear Table of Mobiles
    public void clearMobilesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OTP_USER_MOBILES, null, null);
        db.close();
    }

    //Check E-mail Address is present or not
    public boolean checkMobileNumber(String mobile) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_OTP_USER_MOBILES + " WHERE " + KEY_USER_MOBILE + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{mobile});
        boolean exists = false;
        try {
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    // Updating mobile status
    public void updateVerifyStatusMobile(OtpMobileFetch otpMobileFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MOBILE_VERIFY_ID, otpMobileFetch.getAumvid());
        values.put(KEY_MOBILE_VERIFY_STATUS_ID, otpMobileFetch.getAumvsid());
        values.put(KEY_USER_MOBILE, otpMobileFetch.getMobile());
        db.update(TABLE_OTP_USER_MOBILES, values, KEY_MOBILE_VERIFY_ID + "=" + otpMobileFetch.getAumvid(), null);
        db.close();
    }
//    // Updating mobile status
//    public void updateEmail(OtpEmailFetch otpEmailFetch) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_MOBILE_VERIFY_ID, otpEmailFetch.getAuevid());
//        values.put(KEY_USER_MOBILE, otpEmailFetch.getEmail());
//        db.updateCount(TABLE_OTP_USER_MOBILES, values, KEY_MOBILE_VERIFY_ID + "=" + otpEmailFetch.getAuevid(), null);
//        db.close();
//    }

    // Updating mobile status
    public void updateEmailVerifyStatus(OtpEmailFetch otpEmailFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL_VERIFY_ID, otpEmailFetch.getAuevid());
        values.put(KEY_EMAIL_VERIFY_STATUS_ID, otpEmailFetch.getAuevsid());
        values.put(KEY_USER_EMAIL, otpEmailFetch.getEmail());
        db.update(TABLE_OTP_USER_EMAILS, values, KEY_EMAIL_VERIFY_ID + "=" + otpEmailFetch.getAuevid(), null);
        db.close();
    }

    public void updatePrimaryStatusEmail(OtpEmailFetch otpEmailFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL_VERIFY_ID, otpEmailFetch.getAuevid());
        values.put(KEY_PRIMARY_EMAIL, otpEmailFetch.getPrimaryEmail());
        db.update(TABLE_OTP_USER_EMAILS, values, KEY_EMAIL_VERIFY_ID + "=" + otpEmailFetch.getAuevid(), null);
        db.close();
    }

    public void removeOthersPrimaryStatusEmail(OtpEmailFetch otpEmailFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRIMARY_EMAIL, otpEmailFetch.getPrimaryEmail());
        db.update(TABLE_OTP_USER_EMAILS, values, KEY_EMAIL_VERIFY_ID + "!=" + otpEmailFetch.getAuevid(), null);
        db.close();
    }

    public void updatePrimaryStatusMobile(OtpMobileFetch otpMobileFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MOBILE_VERIFY_ID, otpMobileFetch.getAumvid());
        values.put(KEY_PRIMARY_MOBILE, otpMobileFetch.getPrimaryNo());
        db.update(TABLE_OTP_USER_MOBILES, values, KEY_MOBILE_VERIFY_ID + "=" + otpMobileFetch.getAumvid(), null);
        db.close();
    }

    public void removeOthersPrimaryStatusMobile(OtpMobileFetch otpMobileFetch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRIMARY_MOBILE, otpMobileFetch.getPrimaryNo());
        db.update(TABLE_OTP_USER_MOBILES, values, KEY_MOBILE_VERIFY_ID + "!=" + otpMobileFetch.getAumvid(), null);
        db.close();
    }

    public void deleteOtpEmail(String emailId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_OTP_USER_EMAILS + " WHERE " + KEY_EMAIL_VERIFY_ID + " = " + emailId + "");
        db.close();
    }

    public void deleteOtpMobile(String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_OTP_USER_MOBILES + " WHERE " + KEY_USER_MOBILE + " = '" + mobile + "'");
        db.close();
    }


    public void updateEditedMobile(String mobile, String aumvid, String verifyStatusId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MOBILE_VERIFY_ID, aumvid);
        values.put(KEY_MOBILE_VERIFY_STATUS_ID, verifyStatusId);
        values.put(KEY_USER_MOBILE, mobile);
        db.update(TABLE_OTP_USER_MOBILES, values, KEY_MOBILE_VERIFY_ID + "=" + aumvid, null);
        db.close();
    }


    public void updateEditedEmail(String email, String auevid, String verifyStatusId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL_VERIFY_ID, auevid);
        values.put(KEY_EMAIL_VERIFY_STATUS_ID, verifyStatusId);
        values.put(KEY_USER_EMAIL, email);
        db.update(TABLE_OTP_USER_EMAILS, values, KEY_EMAIL_VERIFY_ID + "=" + auevid, null);
        db.close();
    }

    // Getting All Otp Emails
    public List<OtpEmailFetch> getAllTwoStepVerificationEmails() {
        List<OtpEmailFetch> emailsList = new ArrayList<OtpEmailFetch>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OTP_USER_EMAILS + " ORDER BY " + KEY_EMAIL_USER_ID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
                otpEmailFetch.setUid(cursor.getString(0));
                otpEmailFetch.setAuevid(cursor.getString(1));
                otpEmailFetch.setAuevsid(cursor.getString(2));
                otpEmailFetch.setCode(cursor.getString(3));
                otpEmailFetch.setData(cursor.getString(4));
                otpEmailFetch.setEmail(cursor.getString(5));
                otpEmailFetch.setPrimaryEmail(cursor.getString(6));
                otpEmailFetch.setUpdatedTs(cursor.getString(7));
                otpEmailFetch.setCreatedTs(cursor.getString(8));
                // Adding followup to list
                emailsList.add(otpEmailFetch);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return emailsList;
    }


    // Getting All Otp Emails
    public List<OtpEmailFetch> getAllTwoStepVerificationPrimaryEmails(String uId, String primaryId) {
        List<OtpEmailFetch> emailsList = new ArrayList<OtpEmailFetch>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OTP_USER_EMAILS + " where " + KEY_EMAIL_USER_ID + "=" + uId + " AND "
                + KEY_PRIMARY_EMAIL + "=" + primaryId + " ORDER BY " + KEY_EMAIL_USER_ID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
                otpEmailFetch.setUid(cursor.getString(1));
                otpEmailFetch.setAuevid(cursor.getString(2));
                otpEmailFetch.setAuevsid(cursor.getString(3));
                otpEmailFetch.setCode(cursor.getString(4));
                otpEmailFetch.setEmail(cursor.getString(5));
                otpEmailFetch.setPrimaryEmail(cursor.getString(6));
                otpEmailFetch.setUpdatedTs(cursor.getString(7));
                otpEmailFetch.setCreatedTs(cursor.getString(8));
                // Adding followup to list
                emailsList.add(otpEmailFetch);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return emailsList;
    }

    // Getting All Otp Emails
    public List<OtpMobileFetch> getAllTwoStepVerificationMobiles() {
        List<OtpMobileFetch> mobilesList = new ArrayList<OtpMobileFetch>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OTP_USER_MOBILES + " ORDER BY " + KEY_MOBILE_USER_ID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
                otpMobileFetch.setUid(cursor.getString(0));
                otpMobileFetch.setAumvid(cursor.getString(1));
                otpMobileFetch.setAumvsid(cursor.getString(2));
                otpMobileFetch.setMobile(cursor.getString(3));
                otpMobileFetch.setImei(cursor.getString(4));
                otpMobileFetch.setGcmId(cursor.getString(5));
                otpMobileFetch.setAppVersion(cursor.getString(6));
                otpMobileFetch.setCode(cursor.getString(7));
                otpMobileFetch.setData(cursor.getString(8));
                otpMobileFetch.setPrimaryNo(cursor.getString(9));
                otpMobileFetch.setVerifiedTs(cursor.getString(10));
                otpMobileFetch.setCreatedTs(cursor.getString(11));
                // Adding followup to list
                mobilesList.add(otpMobileFetch);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return mobilesList;
    }


    // Getting All Otp Emails
    public List<OtpMobileFetch> getAllTwoStepVerificationPrimaryMobiles(String uId, String primaryId) {
        List<OtpMobileFetch> mobilesList = new ArrayList<OtpMobileFetch>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OTP_USER_MOBILES + " where " + KEY_MOBILE_USER_ID + "=" + uId + " AND "
                + KEY_PRIMARY_MOBILE + "=" + primaryId + " ORDER BY " + KEY_MOBILE_USER_ID + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
                otpMobileFetch.setUid(cursor.getString(0));
                otpMobileFetch.setAumvid(cursor.getString(1));
                otpMobileFetch.setAumvsid(cursor.getString(2));
                otpMobileFetch.setMobile(cursor.getString(3));
                otpMobileFetch.setImei(cursor.getString(4));
                otpMobileFetch.setGcmId(cursor.getString(5));
                otpMobileFetch.setAppVersion(cursor.getString(6));
                otpMobileFetch.setCode(cursor.getString(7));
                otpMobileFetch.setData(cursor.getString(8));
                otpMobileFetch.setPrimaryNo(cursor.getString(9));
                otpMobileFetch.setVerifiedTs(cursor.getString(10));
                otpMobileFetch.setCreatedTs(cursor.getString(11));
                // Adding followup to list
                mobilesList.add(otpMobileFetch);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return mobilesList;
    }

    // Fetching All Activity Feeds
    public List<Notification> getUnreadActivityFeeds(String startdate, String enddate) {
        List<Notification> notificationList = new ArrayList<Notification>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS + " where " + KEY_POST_CREATED_TS
                + " BETWEEN '" + startdate + "' AND '" + enddate + "'" + " ORDER BY " + KEY_POST_CREATED_TS + " DESC ";
        //   + " BETWEEN '" + startdate + "' AND '" + enddate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setUaid(String.valueOf(cursor.getInt(1)));
                notification.setUid(cursor.getString(2));
                notification.setMid(String.valueOf(cursor.getInt(3)));
                notification.setMotaid(cursor.getString(4));
                notification.setMessage(cursor.getString(5));
                notification.setCreatedTs(cursor.getString(6));
                notification.setFontawesomeicons(cursor.getString(7));
                notification.setFontbackgroundcolor(cursor.getString(9));
                // Adding feeds to list
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        db.close();
        // return feeds list
        return notificationList;
    }

    //Check MotAId is present or not
    public boolean checkMotAIdInNotify(String iD) {
        try {
            SQLiteDatabase sqldb = this.getWritableDatabase();
            String Query = "SELECT * FROM " + TABLE_ACTIVITY_FEEDS_NOTIFICATION + " WHERE " + KEY_FEEDS_NOTIFICATION_MOTAID + "=?";
            Cursor cursor = sqldb.rawQuery(Query, new String[]{iD});
            boolean exists = false;
            try {
                exists = cursor.moveToFirst();
                cursor.close();
                sqldb.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return exists;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //Insert User's Permissions Data
    public void insertUsersPermission(Permission permission) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_PERMISSION_ID, permission.getId());
            values.put(KEY_NAME, permission.getName());
            values.put(KEY_TITLE, permission.getTitle());
            db.insert(TABLE_USER_PERMISSIONS, null, values);
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Check User's Permission
    public boolean checkUsersPermission(String permission) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_USER_PERMISSIONS + " WHERE " + KEY_NAME + "=?";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{permission});
        boolean exists = false;
        try {
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return exists;
    }

    //TODO - Added on 20th April Check Contact's Present or not
    public boolean checkContactPersonExistOrNot(String syncId) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " + KEY_LEAD_CONTACTS_SYNC_ID
                + " =? ";
        Cursor cursor = sqldb.rawQuery(Query, new String[]{syncId});
        boolean exists = false;
        try {
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    // Getting All Non sync Leads
    public List<Lead> getAllNonSyncLeads(String syncValue) {
        List<Lead> leadList = new ArrayList<Lead>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LEADS + " WHERE " + KEY_LEASID + " NOT IN(4) AND " +
                KEY_LEAD_SYNC + "='" + syncValue + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lead lead = new Lead();
                lead.setID(cursor.getInt(0));
                lead.setLeaid(cursor.getString(1));
                lead.setOwnerId(cursor.getString(2));
                lead.setLeatid(cursor.getString(3));
                lead.setName(cursor.getString(4));
                lead.setLeadPersonType(cursor.getString(5));
                lead.setOffAddr(cursor.getString(6));
                lead.setWebsite(cursor.getString(7));
                lead.setFname(cursor.getString(8));
                lead.setLname(cursor.getString(9));
                lead.setCompanyName(cursor.getString(10));
                lead.setSyncId(cursor.getString(11));
                lead.setGenderid(cursor.getString(12));
                lead.setMobile(cursor.getString(13));
                lead.setEmail(cursor.getString(14));
                lead.setAssignedUid(cursor.getString(15));
                lead.setLeasid(cursor.getString(16));
                lead.setEnid(cursor.getString(17));
                lead.setQoid(cursor.getString(18));
                lead.setCancelReason(cursor.getString(19));
                lead.setSpecialInstructions(cursor.getString(20));
                lead.setCreatedUid(cursor.getString(21));
                lead.setUpdatedUid(cursor.getString(22));
                lead.setCreatedTs(cursor.getString(23));
                lead.setUpdatedTs(cursor.getString(24));
                lead.setLeadSync(cursor.getString(25));
                lead.setLeadNumber(cursor.getString(26));
                lead.setLeadId(cursor.getString(27));
                leadList.add(lead);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return lead list
        return leadList;
    }

    //TODO Added on 22nd May to delete path of loggedin User
    public void updateAssigneeImagePath(String uId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_IMAGE_PATH, "");
            db.update(TABLE_ASSIGNEE, values, KEY_ASSIGNEE_CONTACT_ID + "=" + uId, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO Added on 11th June
    public boolean isColumnExists(String table, String column) {

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    if (column.equalsIgnoreCase(name)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //TODO - Created on 15th June
    //Check KEY_LEAD_CONTACTS_CODE_ID is present or not
    public boolean checkFollowUpLeadResult(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        boolean exists = false;
        try {
            String Query = "SELECT * FROM " + TABLE_FOLLOWUPS + " WHERE " +
                    KEY_FOLLOWUP_SYNC_ID + " = " + iD;
            Cursor cursor = sqldb.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                exists = true;
            } else {
                exists = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqldb != null && sqldb.isOpen()) {
                sqldb.close();
            }
        }
        return exists;
    }

    //TODO - Created on 15th June
    public void updateFollowUpContactsCodeId(String codeId,
                                             String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_FOLLOW_UP_CODE_ID, codeId);
            db.update(TABLE_FOLLOWUPS, values, KEY_FOLLOWUP_SYNC_ID + "=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO - Added On 28th June
    // Getting All followups of Enquiry
    public List<FollowUp> getEnquiryFollowUps(String enId) {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FOLLOWUPS + " WHERE " +
                KEY_FOLLOW_UP_ENQUIRY_ID + "='" + enId + "'"
                + " ORDER BY " + KEY_FOSID + " ASC ,"
                + KEY_SCHEDULED_DATE + " DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FollowUp followUp = new FollowUp();
                followUp.setAlertOn(cursor.getString(1));
                followUp.setColor(cursor.getInt(2));
                followUp.setContactPerson(cursor.getString(3));
                followUp.setName(cursor.getString(4));
                followUp.setFosid(cursor.getString(5));
                followUp.setFoid(cursor.getString(6));
                followUp.setScheduledDate(cursor.getString(7));
                followUp.setFollowupTypeName(cursor.getString(8));
                followUp.setLeadId(cursor.getString(9));
                followUp.setCuid(cursor.getString(10));
                followUp.setFollowupType(cursor.getString(11));
                followUp.setFollowupTypeStatus(cursor.getString(12));
                followUp.setFollowupTypeStatusId(cursor.getString(13));
                followUp.setTakenOn(cursor.getString(14));
                followUp.setComment(cursor.getString(15));
                followUp.setReason(cursor.getString(16));
                followUp.setFeedback(cursor.getString(17));
                followUp.setFollowupOutcome(cursor.getString(18));
                followUp.setFollowupCommunicationMode(cursor.getString(19));
                followUp.setAlertMode(cursor.getString(20));
                followUp.setAssigned_user(cursor.getString(21));
                followUp.setCreatedUser(cursor.getString(22));
                followUp.setUpdatedUser(cursor.getString(23));
                followUp.setCreatedTs(cursor.getString(24));
                followUp.setUpdatedTs(cursor.getString(25));
                followUp.setPerson_type(cursor.getString(26));
                followUp.setSyncId(cursor.getString(27));
                followUp.setSyncStatus(cursor.getString(28));
                followUp.setContactPersonMobile(cursor.getString(29));
                followUp.setContactPersonEmail(cursor.getString(30));
                followUp.setCodeid(cursor.getString(31));
                followUp.setCommid(cursor.getString(32));
                followUp.setFotid(cursor.getString(33));
                followUp.setAssignedUid(cursor.getString(34));
                followUp.setContactedPerson(cursor.getString(35));
                followUp.setParentId(cursor.getString(36));
                followUp.setLeadLocalId(cursor.getString(37));
                followUp.setContactsId(cursor.getString(38));
                followUp.setEnid(cursor.getString(39));
                followUp.setQoid(cursor.getString(40));
                followUp.setChkoid(cursor.getString(41));
                followUp.setPurorid(cursor.getString(42));
                followUp.setVenid(cursor.getString(43));
                followUp.setFollowupNumber(cursor.getString(44));
                followUp.setFollowupId(cursor.getString(45));
                followUp.setEnquiryNumber(cursor.getString(46));
                followUp.setEnquiryId(cursor.getString(47));
                followUp.setQuotationId(cursor.getString(48));
                followUp.setQuotationNumber(cursor.getString(49));
                followUp.setOrderSequenceNumber(cursor.getString(50));
                followUp.setOrderId(cursor.getString(51));
                followUp.setPurchaseOrderId(cursor.getString(52));
                followUp.setPurchaseOrderNumber(cursor.getString(53));
                followUp.setLeadIdR(cursor.getString(54));
                followUp.setLeadNumber(cursor.getString(55));
                followUp.setQosid(cursor.getString(56));
                followUp.setEnsid(cursor.getString(57));
                followUp.setChkosid(cursor.getString(58));
                followUp.setPurorsid(cursor.getString(59));
                followUp.setLeasid(cursor.getString(60));
                followUp.setParentFollowupId(cursor.getString(61));
                // Adding followup to list
                followUpList.add(followUp);
            } while (cursor.moveToNext());
        }
        db.close();
        // return followUps list
        return followUpList;
    }

    //TODO Added on 3rd July 2k18
    //Inserting contact Persons of Customers
    public void insertCustomersContactPersons(Contacts contacts) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_LEAD_CONTACTS_CODE_ID, contacts.getCodeid());
            values.put(KEY_LEAD_CONTACTS_NAME, contacts.getName());
            values.put(KEY_LEAD_CONTACTS_CUID, contacts.getCuId());
            values.put(KEY_LEAD_CONTACTS_DESIGNATION, contacts.getDesignation());
            values.put(KEY_LEAD_CONTACTS_PHONE_NUMBER, contacts.getPhoneNo());
            values.put(KEY_LEAD_CONTACTS_EMAIL, contacts.getEmail());
            if (!checkCustomersContactPersonResult(contacts.getCodeid())) {
                db.insert(TABLE_CONTACTS_DETAILS, null, values);
            }
            //    db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO Added on 3rd July 2k18
    //Check KEY_LEAD_CONTACTS_CODE_ID is present or not
    public boolean checkCustomersContactPersonResult(String iD) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        boolean exists = false;
        try {
            String Query = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " +
                    KEY_LEAD_CONTACTS_CODE_ID + " = " + iD;
            Cursor cursor = sqldb.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                exists = true;
            } else {
                exists = false;
            }
            // cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqldb != null && sqldb.isOpen()) {
                //    sqldb.close();
            }
        }
        return exists;
    }

    //TODO - Created on 3rd July 2k18
    public void updateCustomersContactPersons(Contacts contacts) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_LEAD_CONTACTS_CODE_ID, contacts.getCodeid());
            values.put(KEY_LEAD_CONTACTS_NAME, contacts.getName());
            values.put(KEY_LEAD_CONTACTS_CUID, contacts.getCuId());
            values.put(KEY_LEAD_CONTACTS_DESIGNATION, contacts.getDesignation());
            values.put(KEY_LEAD_CONTACTS_PHONE_NUMBER, contacts.getPhoneNo());
            values.put(KEY_LEAD_CONTACTS_EMAIL, contacts.getEmail());
            db.update(TABLE_CONTACTS_DETAILS, values, KEY_LEAD_CONTACTS_CODE_ID + "=" +
                    contacts.getCodeid(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO - Created on 3rd July 2k18
    //Get All Contact Persons
    public List<Contacts> getCustomersContactPersonsList(String cuId) {
        List<Contacts> contactsList = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " +
                    KEY_LEAD_CONTACTS_CUID + " = " + cuId;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Contacts contacts = new Contacts();
                    contacts.setCodeid(cursor.getString(2));
                    contacts.setName(cursor.getString(3));
                    contacts.setDesignation(cursor.getString(4));
                    contacts.setEmail(cursor.getString(5));
                    contacts.setPhoneNo(cursor.getString(6));
                    contacts.setDesignation(cursor.getString(7));
                    contacts.setCuId(cursor.getString(8));
                    // Adding contacts to list
                    contactsList.add(contacts);
                } while (cursor.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactsList;
    }

    //TODO - Created on 3rd July 2k18
    //Get Customer Id
    public String getCustomerId(String codeId) {
        String cuId = "";
        try {
            String selectQuery = "SELECT * FROM " + TABLE_CONTACTS_DETAILS + " WHERE " +
                    KEY_LEAD_CONTACTS_CODE_ID + " = " + codeId;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    return cursor.getString(8);
                } while (cursor.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cuId;
    }

    //TODO - Created on 6th July 2k18
    //Get List of Follow ups on the basis of Contact Person's Code Id
    public List<FollowUp> getSameContactPersonsFollowUps(String iD) {
        List<FollowUp> followUpList = new ArrayList<FollowUp>();
        try {
            // Select All Query
            String selectQuery = "SELECT * FROM " + TABLE_FOLLOWUPS +
                    " WHERE " + KEY_FOLLOW_UP_CODE_ID + " = " + iD;
            // +", " + KEY_SCHEDULED_DATE + " ASC ";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    FollowUp followUp = new FollowUp();
                    followUp.setId(cursor.getInt(0));
                    followUp.setAlertOn(cursor.getString(1));
                    followUp.setColor(cursor.getInt(2));
                    followUp.setContactPerson(cursor.getString(3));
                    followUp.setName(cursor.getString(4));
                    followUp.setFosid(cursor.getString(5));
                    followUp.setFoid(cursor.getString(6));
                    followUp.setScheduledDate(cursor.getString(7));
                    followUp.setFollowupTypeName(cursor.getString(8));
                    followUp.setLeadId(cursor.getString(9));
                    followUp.setCuid(cursor.getString(10));
                    followUp.setFollowupType(cursor.getString(11));
                    followUp.setFollowupTypeStatus(cursor.getString(12));
                    followUp.setFollowupTypeStatusId(cursor.getString(13));
                    followUp.setTakenOn(cursor.getString(14));
                    followUp.setComment(cursor.getString(15));
                    followUp.setReason(cursor.getString(16));
                    followUp.setFeedback(cursor.getString(17));
                    followUp.setFollowupOutcome(cursor.getString(18));
                    followUp.setFollowupCommunicationMode(cursor.getString(19));
                    followUp.setAlertMode(cursor.getString(20));
                    followUp.setAssigned_user(cursor.getString(21));
                    followUp.setCreatedUser(cursor.getString(22));
                    followUp.setUpdatedUser(cursor.getString(23));
                    followUp.setCreatedTs(cursor.getString(24));
                    followUp.setUpdatedTs(cursor.getString(25));
                    followUp.setPerson_type(cursor.getString(26));
                    followUp.setSyncId(cursor.getString(27));
                    followUp.setSyncStatus(cursor.getString(28));
                    followUp.setContactPersonMobile(cursor.getString(29));
                    followUp.setContactPersonEmail(cursor.getString(30));
                    followUp.setCodeid(cursor.getString(31));
                    followUp.setCommid(cursor.getString(32));
                    followUp.setFotid(cursor.getString(33));
                    followUp.setAssignedUid(cursor.getString(34));
                    followUp.setContactedPerson(cursor.getString(35));
                    followUp.setParentId(cursor.getString(36));
                    followUp.setLeadLocalId(cursor.getString(37));
                    followUp.setContactsId(cursor.getString(38));
                    followUp.setEnid(cursor.getString(39));
                    followUp.setQoid(cursor.getString(40));
                    followUp.setChkoid(cursor.getString(41));
                    followUp.setPurorid(cursor.getString(42));
                    followUp.setVenid(cursor.getString(43));
                    followUp.setFollowupNumber(cursor.getString(44));
                    followUp.setFollowupId(cursor.getString(45));
                    followUp.setEnquiryNumber(cursor.getString(46));
                    followUp.setEnquiryId(cursor.getString(47));
                    followUp.setQuotationId(cursor.getString(48));
                    followUp.setQuotationNumber(cursor.getString(49));
                    followUp.setOrderSequenceNumber(cursor.getString(50));
                    followUp.setOrderId(cursor.getString(51));
                    followUp.setPurchaseOrderId(cursor.getString(52));
                    followUp.setPurchaseOrderNumber(cursor.getString(53));
                    followUp.setLeadIdR(cursor.getString(54));
                    followUp.setLeadNumber(cursor.getString(55));
                    //TODO - Added on 19th April
                    followUp.setQosid(cursor.getString(56));
                    followUp.setEnsid(cursor.getString(57));
                    followUp.setChkosid(cursor.getString(58));
                    followUp.setPurorsid(cursor.getString(59));
                    followUp.setLeasid(cursor.getString(60));
                    //TODO - Added on 10th May
                    followUp.setParentFollowupId(cursor.getString(61));
                    // Adding followup to list
                    followUpList.add(followUp);
                } while (cursor.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return followUps list
        return followUpList;
    }


    //TODO - Created on 11th July
    //Update Lead Status in Follow Up
    public void updateFollowUpsLeadStatus(String leadId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FOLLOWUP_LEAD_STATUS_ID, status);
        db.update(TABLE_FOLLOWUPS, values, KEY_FOLLOWUP_LEAD_ID + "=" + leadId, null);
    }

    //TODO - Created on 13th July 2k18
    public void deleteCustomersContactPersons(String cuId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CONTACTS_DETAILS, KEY_LEAD_CONTACTS_CUID + "=" +
                    cuId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

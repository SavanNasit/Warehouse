package com.accrete.sixorbit.fragment.Drawer.followups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.DatePickerFragment;
import com.accrete.sixorbit.fragment.Drawer.TimePickerFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.helper.WordUtils;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowupCommunicationMode;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.service.ContactPersonsAPI;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;


/**
 * Created by poonam on 10/4/17.
 */

public class RecordFollowUpFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        PassDateToCounsellor {
    TextView textViewCompany, textViewStatus, textViewSave, textViewSchedule, nextFollowupTextView;
    EditText editTextInfo, editTextComment, editTextReason, editTextScheduledTime, editTextAlertTime;
    RadioButton radioButtonScheduled, radioButtonUnscheduled;
    Spinner spinnerOutcome;
    RadioGroup radiogroup;
    LinearLayout linearLayoutAddDetails, linearLayoutFillDetails;
    CheckBox checkBoxNextFollowUp;
    String[] outcome = {"Not interested", "Call later", "Price too high", "Wrong contact", "No response"};
    //String[] communicated_mode = {"Mail", "Phone Call", "Skype Call"};
    List<ChatContacts> listContacts = new ArrayList<>();
    List<Contacts> contactArrayList = new ArrayList<>();
    List<String> listContactsName = new ArrayList<>();
    String userName;
    ProgressBar progressBar;
    ArrayAdapter arrayAdapterOutcome, arrayAdapterCommunicatedMode, arrayAdapterAssignee, adapterContactsPerson,
            adapterContactedPerson;
    String[] arrayContactPerson, arrayContactPersonId;
    List<ChatContacts> contacts2;
    SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView takenCompanyTitle;
    private TextView takenStatusTitle;
    private TextView takenScheduleTitle;
    private TextView communicatedModeTextView;
    private TextView contactPersonTextView;
    private Spinner contactPersonSpinner;
    private AutoCompleteTextView assignFollowupAutoCompleteTextView;
    private TextView communicationModeTextView;
    private Spinner communicatedModeSpinner;
    private Spinner communicationModeSpinner, contactedPersonSpinner;
    private DatabaseHandler databaseHandler;
    private String strLeaId, strQoid, strEnid, strChkoid, strCuid, strJocaid, selectedRadioButton, strSchedule, strScheduledTime, strAlertTime,
            strFromTime, strScheduleDate, strCommunicatedMode, strToTime, strOutcome, strDescription, strReason, strAssignee,
            strContactPersonId, strAlertMode, strScheduleNextFollowUp, strComment, strContactedPersonId, strCommunicationMode,
            strContactPersonName, strContactedPersonName;
    private DatePickerFragment datePickerFragment;
    private Date startDate, enddate;
    private TimePickerFragment timePickerFragment;
    private LinearLayout reasonLayout;
    private CheckBox checkboxSendMail;
    private CheckBox checkboxSendSms;
    private String leadId, foid, sync_id;
    private FollowUp followUp;
    private Lead lead;
    private List<String> assigneeNameList = new ArrayList<String>();
    private List<String> assigneeIDList = new ArrayList<String>();
    private List<FollowupCommunicationMode> communicationModeList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle.containsKey(getString(R.string.lead_id))) {
            leadId = bundle.getString(getString(R.string.lead_id));
        }
        if (bundle.containsKey(getString(R.string.foid)))
            foid = bundle.getString(getString(R.string.foid));
        else if (bundle.containsKey(getString(R.string.sync_id)))
            sync_id = bundle.getString(getString(R.string.sync_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record_follow_up, container, false);
        databaseHandler = new DatabaseHandler(getActivity());
        userName = AppPreferences.getUserName(getActivity(), AppUtils.USER_NAME);

        if (AppPreferences.getModes(getActivity(), AppUtils.FOLLOWUPS_COMMUNICATION_MODE) != null) {
            communicationModeList.addAll(AppPreferences.getModes(getActivity(), AppUtils.FOLLOWUPS_COMMUNICATION_MODE));
        }

        //Getting data from database
        if (leadId != null)
            lead = databaseHandler.getLead(leadId, getString(R.string.leaid));

        if (foid != null && !foid.isEmpty())
            followUp = databaseHandler.getSingleFollowUpResult(foid, getString(R.string.foid));
        else if (sync_id != null && !sync_id.isEmpty())
            followUp = databaseHandler.getSingleFollowUpResult(sync_id, getString(R.string.sync_id));

        contacts2 = databaseHandler.getAllAssignee();
        for (ChatContacts cn : contacts2) {
            assigneeNameList.add(cn.getName());
            assigneeIDList.add(String.valueOf(cn.getUid()));
        }
        initalizeView(rootView);
        return rootView;
    }

    private void initalizeView(final View rootView) {
        try {
            takenCompanyTitle = (TextView) rootView.findViewById(R.id.taken_company_title);
            takenStatusTitle = (TextView) rootView.findViewById(R.id.taken_status_title);
            takenScheduleTitle = (TextView) rootView.findViewById(R.id.taken_schedule_title);
            textViewCompany = (TextView) rootView.findViewById(R.id.taken_company);
            textViewStatus = (TextView) rootView.findViewById(R.id.taken_status);
            textViewSave = (TextView) rootView.findViewById(R.id.taken_save);
            textViewSchedule = (TextView) rootView.findViewById(R.id.taken_schedule);
            editTextInfo = (EditText) rootView.findViewById(R.id.taken_follow_up_info);
            editTextComment = (EditText) rootView.findViewById(R.id.taken_comment);
            editTextReason = (EditText) rootView.findViewById(R.id.taken_reason);
            editTextScheduledTime = (EditText) rootView.findViewById(R.id.taken_scheduled_time);
            editTextAlertTime = (EditText) rootView.findViewById(R.id.taken_alert_time);
            spinnerOutcome = (Spinner) rootView.findViewById(R.id.taken_outcome);
            radiogroup = (RadioGroup) rootView.findViewById(R.id.taken_radio_group);
            linearLayoutAddDetails = (LinearLayout) rootView.findViewById(R.id.taken_schedule_follow_up);
            linearLayoutFillDetails = (LinearLayout) rootView.findViewById(R.id.taken_next_follow_up_details);
            checkBoxNextFollowUp = (CheckBox) rootView.findViewById(R.id.taken_add_details);
            progressBar = (ProgressBar) rootView.findViewById(R.id.record_progress_bar);
            communicatedModeTextView = (TextView) rootView.findViewById(R.id.communicated_mode_textView);
            communicatedModeSpinner = (Spinner) rootView.findViewById(R.id.communicated_mode_spinner);
            radioButtonScheduled = (RadioButton) rootView.findViewById(R.id.taken_scheduled);
            radioButtonUnscheduled = (RadioButton) rootView.findViewById(R.id.taken_unscheduled);
            contactPersonTextView = (TextView) rootView.findViewById(R.id.contact_person_textView);
            contactPersonSpinner = (Spinner) rootView.findViewById(R.id.contact_person_spinner);
            assignFollowupAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.assign_followup_autoCompleteTextView);
            communicationModeTextView = (TextView) rootView.findViewById(R.id.communication_mode_textView);
            communicationModeSpinner = (Spinner) rootView.findViewById(R.id.communication_mode_spinner);
            reasonLayout = (LinearLayout) rootView.findViewById(R.id.reason_layout);
            checkboxSendMail = (CheckBox) rootView.findViewById(R.id.checkbox_send_mail);
            checkboxSendSms = (CheckBox) rootView.findViewById(R.id.checkbox_send_sms);
            contactedPersonSpinner = (Spinner) rootView.findViewById(R.id.contacted_person_spinner);
            nextFollowupTextView = (TextView) rootView.findViewById(R.id.next_followup_textView);

            textViewSave.setOnClickListener(this);

            communicatedModeSpinner.setOnItemSelectedListener(this);
            communicationModeSpinner.setOnItemSelectedListener(this);
            datePickerFragment = new DatePickerFragment();
            datePickerFragment.setListener(this);

            timePickerFragment = new TimePickerFragment();
            timePickerFragment.setListener(this);
            for (int i = 0; i < listContacts.size(); i++) {
                listContactsName.add(listContacts.get(i).getName());
            }

            //Set Spinners
            outcomeAdapter();
            setCommunicatedModeAdapter();

            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (checkedId == R.id.taken_scheduled) {
                        selectedRadioButton = "1";
                        if (spinnerOutcome.getSelectedItemPosition() == 0 ||
                                spinnerOutcome.getSelectedItemPosition() == 3 ||
                                spinnerOutcome.getSelectedItemPosition() == 4) {
                            //Hide Next Follow up
                            linearLayoutAddDetails.setVisibility(View.GONE);
                            linearLayoutFillDetails.setVisibility(View.GONE);

                            if (reasonLayout.getVisibility() == View.GONE) {
                                reasonLayout.setVisibility(View.VISIBLE);
                            }

                        } else {
                            //Show Next Follow up
                            linearLayoutAddDetails.setVisibility(View.VISIBLE);
                            if (reasonLayout.getVisibility() == View.VISIBLE) {
                                reasonLayout.setVisibility(View.GONE);
                            } else {
                                reasonLayout.setVisibility(View.GONE);
                            }
                            if (checkBoxNextFollowUp.isChecked()) {
                                linearLayoutFillDetails.setVisibility(View.VISIBLE);
                            } else {
                                linearLayoutFillDetails.setVisibility(View.GONE);
                            }

                        }
                    } else if (checkedId == R.id.taken_unscheduled) {
                        selectedRadioButton = "2";
                        linearLayoutAddDetails.setVisibility(View.GONE);
                        linearLayoutFillDetails.setVisibility(View.GONE);
                        nextFollowupTextView.setVisibility(View.GONE);
                        if (linearLayoutFillDetails.getVisibility() == View.VISIBLE) {
                            linearLayoutFillDetails.setVisibility(View.GONE);
                            nextFollowupTextView.setVisibility(View.GONE);
                        } else {
                            linearLayoutFillDetails.setVisibility(View.GONE);
                            nextFollowupTextView.setVisibility(View.GONE);
                        }
                    }
                }
            });

            //Check Take Schedule by default
            radioButtonScheduled.setChecked(true);

            //Assignee Follow Up
            assigneeContactsAdapter();
            assignFollowupAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    assignFollowupAutoCompleteTextView.showDropDown();
                }
            });
            assignFollowupAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strAssignee = assignFollowupAutoCompleteTextView.getText().toString();
                        for (int i = 0; i < assigneeNameList.size(); i++) {
                            String temp = assigneeNameList.get(i);
                            if (strAssignee.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        assignFollowupAutoCompleteTextView.setText("");
                    }
                }
            });

            checkBoxNextFollowUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        linearLayoutFillDetails.setVisibility(View.VISIBLE);
                  /*  Animation slide_down = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.slide_down);
                    linearLayoutFillDetails.startAnimation(slide_down);*/
                        nextFollowupTextView.setVisibility(View.VISIBLE);
                        strScheduleNextFollowUp = "1";
                    } else {
                        if (linearLayoutFillDetails.getVisibility() == View.VISIBLE) {
                            linearLayoutFillDetails.setVisibility(View.GONE);
                       /* Animation slide_up = AnimationUtils.loadAnimation(getActivity(),
                                R.anim.slide_up);
                        linearLayoutFillDetails.startAnimation(slide_up);*/
                            nextFollowupTextView.setVisibility(View.GONE);
                        } else {
                            linearLayoutFillDetails.setVisibility(View.GONE);
                            nextFollowupTextView.setVisibility(View.GONE);
                        }
                        strScheduleNextFollowUp = "2";
                    }
                }
            });

            checkBoxNextFollowUp.setChecked(false);
            linearLayoutFillDetails.setVisibility(View.GONE);
            nextFollowupTextView.setVisibility(View.GONE);
            editTextScheduledTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_from));
                }
            });

            editTextAlertTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Added in June
                    try {
                        if (editTextScheduledTime.getText().toString() == null ||
                                editTextScheduledTime.getText().toString().isEmpty()) {
                            datePickerFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.dailogue_to));
                        } else {

                            String[] strDate = editTextScheduledTime.getText().toString().split("\\s+");
                            String strYear = strDate[0].toString().substring(strDate[0].lastIndexOf("-") + 1,
                                    strDate[0].toString().length());
                            String strDayMonth = strDate[0].toString().substring(0, strDate[0].lastIndexOf("-"));
                            String strDay = strDayMonth.toString().substring(0, strDayMonth.lastIndexOf("-"));
                            String strMonth = strDayMonth.toString().substring(strDayMonth.lastIndexOf("-") + 1,
                                    strDayMonth.length());

                            /*//Calculating Time
                            String strSeconds = strDate[1].toString().substring(strDate[1].lastIndexOf(":") + 1,
                                    strDate[1].toString().length());
                            String strHourMinute = strDate[1].toString().substring(0, strDate[1].lastIndexOf(":"));
                            String strHour = strHourMinute.toString().substring(0, strHourMinute.lastIndexOf(":"));
                            String strMinute = strHourMinute.toString().substring(strHourMinute.lastIndexOf(":") + 1,
                                    strHourMinute.length());*/

                            Bundle bundle = new Bundle();

                            bundle.putString("day", strDay.toString());
                            bundle.putString("month", strMonth.toString());
                            bundle.putString("year", strYear.toString());
                            datePickerFragment.setArguments(bundle);
                            datePickerFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.dailogue_to));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_to));
                }
            });

            spinnerOutcome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (spinnerOutcome.getSelectedItemPosition() == 0 ||
                            spinnerOutcome.getSelectedItemPosition() == 3 ||
                            spinnerOutcome.getSelectedItemPosition() == 4) {
                        //Hide Next Follow up
                        linearLayoutAddDetails.setVisibility(View.GONE);
                        linearLayoutFillDetails.setVisibility(View.GONE);

                        if (reasonLayout.getVisibility() == View.GONE) {
                            reasonLayout.setVisibility(View.VISIBLE);
                       /* Animation slide_down = AnimationUtils.loadAnimation(getActivity(),
                                R.anim.slide_down);
                        reasonLayout.startAnimation(slide_down);*/
                        }
                        if (radioButtonUnscheduled.isChecked()) {
                            linearLayoutFillDetails.setVisibility(View.GONE);
                            linearLayoutAddDetails.setVisibility(View.GONE);
                        }

                    } else {
                        //Show Next Follow up
                        linearLayoutAddDetails.setVisibility(View.VISIBLE);

                        if (reasonLayout.getVisibility() == View.VISIBLE) {
                            reasonLayout.setVisibility(View.GONE);
                        /*Animation slide_up = AnimationUtils.loadAnimation(getActivity(),
                                R.anim.slide_up);
                        reasonLayout.startAnimation(slide_up);*/
                        } else {
                            reasonLayout.setVisibility(View.GONE);
                        }

                        if (checkBoxNextFollowUp.isChecked()) {
                            linearLayoutFillDetails.setVisibility(View.VISIBLE);
                        } else {
                            linearLayoutFillDetails.setVisibility(View.GONE);
                        }

                        if (radioButtonUnscheduled.isChecked()) {
                            linearLayoutFillDetails.setVisibility(View.GONE);
                            linearLayoutAddDetails.setVisibility(View.GONE);
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            //Set Data
            if (followUp.getFosid().equals("1")) {
                textViewStatus.setText(getString(R.string.followup_status_pending));
            } else if (followUp.getFosid().equals("2")) {
                textViewStatus.setText(getString(R.string.followup_status_taken));
            }
            editTextComment.setText(followUp.getComment());

            if (followUp.getFollowupCommunicationMode() != null && !followUp.getFollowupCommunicationMode().equals(null)) {
                int index = -1;
                for (int i = 0; i < communicationModeList.size(); i++) {
                    if (communicationModeList.get(i).getName().contains(followUp.getFollowupCommunicationMode())) {
                        index = i;
                        break;
                    }
                }
                communicatedModeSpinner.setSelection(index);
            }
            if (followUp.getFollowupOutcome() != null && !followUp.getFollowupOutcome().equals(null)) {
                int index = -1;
                for (int i = 0; i < outcome.length; i++) {
                    if (outcome[i].contains(followUp.getFollowupOutcome())) {
                        index = i;
                        break;
                    }
                }
                spinnerOutcome.setSelection(index);
            }

            if (followUp.getPerson_type() != null && followUp.getPerson_type().equals("2")) {
                takenCompanyTitle.setText(getString(R.string.customer));
            } else {
                takenCompanyTitle.setText(getString(R.string.company));
            }

            //TODO - Updated On 11th may
            if (lead != null && lead.getName() != null && !lead.getName().isEmpty()
                    && !lead.getName().equals("null")) {
                textViewCompany.setText(WordUtils.capitalize(lead.getName().toString().trim()) + "\n");
            } else if (followUp.getName() != null && !followUp.getName().toLowerCase().equals("null")) {
                textViewCompany.setText(WordUtils.capitalize(followUp.getName().toString().trim()) + "\n");
            } else if (followUp.getContactPerson() != null && !followUp.getContactPerson().toLowerCase().equals("null")) {
                textViewCompany.setText(WordUtils.capitalize(followUp.getContactPerson().toString().trim()) + "\n");
            }

            if (followUp.getScheduledDate() != null) {
                if (!followUp.getScheduledDate().contains("0000")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date current_itemDate = simpleDateFormat.parse(followUp.getScheduledDate());
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy hh:mm a ");
                        String str = (outputFormat.format(current_itemDate)).toString().replace("am", "AM").replace("pm", "PM");
                        textViewSchedule.setText(str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            } else textViewSchedule.setText("");
            editTextReason.setText(followUp.getReason());
            editTextComment.setText(followUp.getComment());
            assignFollowupAutoCompleteTextView.setThreshold(1);

            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                ContactPersonsAPI contactPersonsAPI = new ContactPersonsAPI(getActivity());
                if (followUp.getEnid() != null && !followUp.getEnid().isEmpty()) {
                    contactPersonsAPI.getCustomersContactPersons(getActivity(), "",
                            "", followUp.getEnid(), "", "", "", "");
                } else if (followUp.getQoid() != null && !followUp.getQoid().isEmpty()) {
                    contactPersonsAPI.getCustomersContactPersons(getActivity(),
                            followUp.getQoid(), "", "", "", "", "", "");
                } else if (followUp.getLeaid() != null && !followUp.getLeaid().isEmpty()) {
                    contactPersonsAPI.getCustomersContactPersons(getActivity(),
                            "", followUp.getLeaid(), "", "", "", "", "");
                } else if (followUp.getChkoid() != null && !followUp.getChkoid().isEmpty()) {
                    contactPersonsAPI.getCustomersContactPersons(getActivity(),
                            "", "", "", followUp.getChkoid(), "", "", "");
                } else if (followUp.getPurorid() != null && !followUp.getPurorid().isEmpty()) {
                    contactPersonsAPI.getCustomersContactPersons(getActivity(),
                            "", "", "", "", "", "", followUp.getPurorid());
                } else if (followUp.getCuid() != null && !followUp.getCuid().isEmpty()) {
                    contactPersonsAPI.getCustomersContactPersons(getActivity(),
                            "", "", "", "", followUp.getCuid(), "", "");
                }
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (lead != null) {
                        if (lead.getContacts().size() > 0) {
                            for (int i = 0; i < lead.getContacts().size(); i++) {
                                contactArrayList.add(lead.getContacts().get(i));
                            }
                        }
                    } else {
                        if (followUp.getCuid() != null && !followUp.getCuid().isEmpty()) {
                            contactArrayList.addAll(databaseHandler.getCustomersContactPersonsList(followUp.getCuid()));
                        }
                    }

                    //Contact Person Name
                    arrayContactPerson = new String[contactArrayList.size()];
                    for (int i = 0; i < contactArrayList.size(); i++) {
                        arrayContactPerson[i] = contactArrayList.get(i).getName();
                    }
                    //Contact Person Id
                    arrayContactPersonId = new String[contactArrayList.size()];
                    for (int i = 0; i < contactArrayList.size(); i++) {
                        arrayContactPersonId[i] = contactArrayList.get(i).getCodeid();
                    }

                    setContactsPerson();
                    setContactedPerson();

                    //Set Selection of Contacted Person
                    for (int i = 0; i < arrayContactPerson.length; i++) {
                        if (followUp.getContactPerson().equals(arrayContactPerson[i].toString())) {
                            contactedPersonSpinner.setSelection(i);
                        }
                    }

                    if (followUp.getCodeid() != null && followUp.getCodeid().isEmpty()) {
                        for (int i = 0; i < arrayContactPersonId.length; i++) {
                            if (followUp.getCodeid().equals(arrayContactPersonId[i].toString())) {
                                contactedPersonSpinner.setSelection(i);
                            }
                        }
                    }

                }
            }, 2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outcomeAdapter() {
        try {
            if (getActivity() != null && isAdded() && outcome != null) {
                arrayAdapterOutcome = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, outcome);
                arrayAdapterOutcome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerOutcome.setAdapter(arrayAdapterOutcome);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCommunicatedModeAdapter() {
        try {
            if (getActivity() != null && isAdded() && communicationModeList != null) {
                arrayAdapterCommunicatedMode = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, communicationModeList);
                arrayAdapterCommunicatedMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                communicatedModeSpinner.setAdapter(arrayAdapterCommunicatedMode);
                communicationModeSpinner.setAdapter(arrayAdapterCommunicatedMode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    adapter method for Assignee Contacts Adapter
    private void assigneeContactsAdapter() {
        try {
            if (getActivity() != null && isAdded() && assigneeNameList != null) {
                arrayAdapterAssignee = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, assigneeNameList);
                arrayAdapterAssignee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                assignFollowupAutoCompleteTextView.setAdapter(arrayAdapterAssignee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContactsPerson() {
        try {
            if (getActivity() != null && isAdded() && arrayContactPerson != null) {
                adapterContactsPerson = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayContactPerson);
                adapterContactsPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                contactPersonSpinner.setAdapter(adapterContactsPerson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContactedPerson() {
        try {
            if (getActivity() != null && isAdded() && arrayContactPerson!= null) {
            adapterContactedPerson = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayContactPerson);
            adapterContactedPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            contactedPersonSpinner.setAdapter(adapterContactedPerson);}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.record_follow_up));
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        long time_nano = System.nanoTime();
        long micro_seconds = time_nano / 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date scheduledDateTime = null, alertDateTime = null;
        try {
            //    valuesTobeSend();
            if (radioButtonScheduled.isChecked()) {
                if (linearLayoutAddDetails.getVisibility() == View.VISIBLE && checkBoxNextFollowUp.isChecked()) {
                    //Date Time
                    scheduledDateTime = simpleDateFormat.parse(editTextScheduledTime.getText().toString().trim());
                    alertDateTime = simpleDateFormat.parse(editTextAlertTime.getText().toString().trim());
                    valuesTobeSend();

                    if (editTextScheduledTime.getText().toString().trim().length() == 0) {
                        CustomisedToast.error(getActivity(), getString(R.string.select_schedule_time)).show();
                    } else if (editTextAlertTime.getText().toString().trim().length() == 0) {
                        CustomisedToast.error(getActivity(), getString(R.string.select_alert_time)).show();
                    } else if (scheduledDateTime != null && alertDateTime != null && scheduledDateTime.before(alertDateTime)) {
                        CustomisedToast.error(getActivity(), getString(R.string.scheduletime_more_than_alerttime)).show();
                    } else if (new Date().compareTo(scheduledDateTime) >= 0) {
                        CustomisedToast.error(getActivity(), getString(R.string.scheduletime_more_than_currenttime)).show();
                    } else if (new Date().compareTo(alertDateTime) >= 0) {
                        CustomisedToast.error(getActivity(), getString(R.string.alerttime_more_than_currenttime)).show();
                    } else {

                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            sendTakenData(micro_seconds);
                        } else {
                            databaseHandler.insertRecordFollowUps(foid, strLeaId, strEnid, strQoid,
                                    strChkoid, strCuid, strJocaid, selectedRadioButton, strScheduleDate, strAlertTime, strCommunicatedMode,
                                    strOutcome, strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment,
                                    strContactedPersonId, strCommunicationMode, false, String.valueOf(micro_seconds), strScheduleNextFollowUp);

                            //Update status of current follow up
                            FollowUp followUpOld = new FollowUp();
                            followUpOld.setFoid(foid);
                            followUpOld.setFosid("2");
                            followUpOld.setPerson_type(followUp.getPerson_type());
                            followUpOld.setTakenOn(followUp.getTakenOn());
                            followUpOld.setScheduledDate(followUp.getScheduledDate());
                            followUpOld.setAlertOn(editTextAlertTime.getText().toString().trim());
                            followUpOld.setName(followUp.getName());
                            //followUpOld.setFollowupCommunicationMode(communicationModeSpinner.getSelectedItem().toString());
                            followUpOld.setSyncId(followUp.getSyncId());
                            followUpOld.setLeadId(strLeaId);
                            followUpOld.setQoid(strQoid);
                            followUpOld.setEnid(strEnid);
                            followUpOld.setChkoid(strChkoid);
                            followUpOld.setComment(strComment);
                            followUpOld.setParentId(followUp.getParentId());
                            followUpOld.setCodeid(strContactedPersonId);
                            followUpOld.setContactPerson(strContactedPersonName);
                            followUpOld.setCreatedUser(followUp.getCreatedUser());
                            followUpOld.setUpdatedUser(userName);
                            followUpOld.setReason(editTextReason.getText().toString());
                            followUpOld.setFeedback(strDescription);
                            followUpOld.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                            followUpOld.setFollowupOutcome(strOutcome);
                            followUpOld.setCreatedTs(followUp.getCreatedTs());
                            followUpOld.setUpdatedTs(followUp.getUpdatedTs());
                            followUpOld.setFollowupCommunicationMode(communicatedModeSpinner.getSelectedItem().toString());
                            if (foid != null) {
                                databaseHandler.updateFollowUpStatus(followUpOld, getString(R.string.foid));
                            } else {
                                //followUpOld.setSyncId(followUp.getSyncId());
                                databaseHandler.updateFollowUpStatus(followUpOld, getString(R.string.sync));
                            }

                            //update lead status
                            if (strOutcome.equals("1") || strOutcome.equals("4") || strOutcome.equals("5")) {
                                if (strLeaId != null && databaseHandler.getFollowupsCount(strLeaId) == 0) {
                                    databaseHandler.updateLeadsReason("4", strLeaId, "false");
                                } else {
                                    databaseHandler.updateLeadsReason("2", strLeaId, "false");
                                }
                            } else {
                                databaseHandler.updateLeadsReason("2", strLeaId, "false");
                            }

                            //Create New Scheduled Follow Up
                            FollowUp followUpNew = new FollowUp();
                            followUpNew.setFoid("");
                            followUpNew.setFosid("1");
                            followUpNew.setScheduledDate(editTextScheduledTime.getText().toString().trim());
                            followUpNew.setAlertOn(editTextAlertTime.getText().toString().trim());
                            followUpNew.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                            followUpNew.setAlertMode(strAlertMode);
                            followUpNew.setFollowupCommunicationMode(communicationModeSpinner.getSelectedItem().toString());
                            followUpNew.setSyncId(String.valueOf(micro_seconds));
                            followUpNew.setLeadId(strLeaId);
                            followUpNew.setQoid(strQoid);
                            followUpNew.setEnid(strEnid);
                            followUpNew.setChkoid(strChkoid);
                            followUpNew.setContactPerson(strContactPersonName);
                            followUpNew.setCodeid(strContactPersonId);
                            followUpNew.setSyncStatus(String.valueOf(false));
                            databaseHandler.insertFollowUpData(followUpNew);
                            //Update List of previous fragment
                            Intent intent = new Intent(getString(R.string.refresh_followups));
                            intent.putExtra(getString(R.string.message), getString(R.string.update_list));
                            intent.putExtra(getString(R.string.foid), foid);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            //Go back to previous Fragment
                            FragmentManager fm = getFragmentManager();
                            fm.popBackStack();
                        }
                    }
                } else {
                    valuesTobeSend();

                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        sendTakenData(micro_seconds);
                    } else {
                        databaseHandler.insertRecordFollowUps(foid, strLeaId, strEnid, strQoid,
                                strChkoid, strCuid, strJocaid, selectedRadioButton, strScheduleDate, strAlertTime, strCommunicatedMode,
                                strOutcome, strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment,
                                strContactedPersonId, strCommunicationMode, false, String.valueOf(micro_seconds),
                                strScheduleNextFollowUp);

                        //Update status of current follow up
                        FollowUp followUpOld = new FollowUp();
                        followUpOld.setFoid(foid);
                        followUpOld.setFosid("2");
                        followUpOld.setPerson_type(followUp.getPerson_type());
                        followUpOld.setTakenOn(followUp.getTakenOn());
                        followUpOld.setScheduledDate(followUp.getScheduledDate());
                        followUpOld.setAlertOn(editTextAlertTime.getText().toString().trim());
                        followUpOld.setContactPerson(strContactedPersonName);
                        followUpOld.setCodeid(strContactedPersonId);
                        followUpOld.setName(followUp.getName());
                        // followUpOld.setFollowupCommunicationMode(communicationModeSpinner.getSelectedItem().toString());
                        followUpOld.setSyncId(followUp.getSyncId());
                        followUpOld.setLeadId(strLeaId);
                        followUpOld.setQoid(strQoid);
                        followUpOld.setEnid(strEnid);
                        followUpOld.setChkoid(strChkoid);
                        followUpOld.setComment(strComment);
                        followUpOld.setParentId(followUp.getParentId());
                        followUpOld.setCreatedUser(followUp.getCreatedUser());
                        followUpOld.setUpdatedUser(userName);
                        followUpOld.setReason(editTextReason.getText().toString());
                        followUpOld.setFeedback(strDescription);
                        followUpOld.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                        followUpOld.setFollowupOutcome(strOutcome);
                        followUpOld.setCreatedTs(followUp.getCreatedTs());
                        followUpOld.setUpdatedTs(followUp.getUpdatedTs());
                        followUpOld.setFollowupCommunicationMode(communicatedModeSpinner.getSelectedItem().toString());
                        if (foid != null) {
                            databaseHandler.updateFollowUpStatus(followUpOld, "foid");
                        } else {
                            //followUpOld.setSyncId(followUp.getSyncId());
                            databaseHandler.updateFollowUpStatus(followUpOld, "sync");
                        }

                        //updateCount lead status
                        if (strOutcome.equals("1") || strOutcome.equals("4") || strOutcome.equals("5")) {
                            if (strLeaId != null && databaseHandler.getFollowupsCount(strLeaId) == 0) {
                                databaseHandler.updateLeadsReason("4", strLeaId, "false");
                            } else {
                                databaseHandler.updateLeadsReason("2", strLeaId, "false");
                            }
                        } else {
                            databaseHandler.updateLeadsReason("2", strLeaId, "false");
                        }

                        //Update List of previous fragment
                        Intent intent = new Intent(getString(R.string.refresh_followups));
                        intent.putExtra(getString(R.string.message), getString(R.string.update_list));
                        intent.putExtra(getString(R.string.foid), foid);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        //Go back to previous Fragment
                        FragmentManager fm = getFragmentManager();
                        fm.popBackStack();
                    }
                }
            } else {
                valuesTobeSend();
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    sendTakenData(micro_seconds);
                } else {
                    databaseHandler.insertRecordFollowUps(foid, strLeaId, strEnid, strQoid,
                            strChkoid, strCuid, strJocaid, selectedRadioButton, currentDateFormat.format(new Date()), strAlertTime, strCommunicatedMode,
                            strOutcome, strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment,
                            strContactedPersonId, strCommunicationMode, false, String.valueOf(micro_seconds), strScheduleNextFollowUp);

                    //Added Fresh Taken FollowUp
                    FollowUp followUpNew = new FollowUp();
                    followUpNew.setFoid("");
                    followUpNew.setFosid("2");
                    followUpNew.setFollowupCommunicationMode(communicatedModeSpinner.getSelectedItem().toString());
                    followUpNew.setPerson_type(followUp.getPerson_type());
                    followUpNew.setContactPerson(strContactPersonName);
                    followUpNew.setCodeid(strContactPersonId);
                    followUpNew.setName(followUp.getName());
                    followUpNew.setSyncId(String.valueOf(micro_seconds));
                    followUpNew.setLeadId(strLeaId);
                    followUpNew.setQoid(strQoid);
                    followUpNew.setEnid(strEnid);
                    followUpNew.setChkoid(strChkoid);
                    followUpNew.setScheduledDate(currentDateFormat.format(new Date()));
                    followUpNew.setAlertOn(strAlertTime);
                    followUpNew.setAlertMode(strAlertMode);
                    followUpNew.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                    followUpNew.setSyncStatus(String.valueOf(false));
                    databaseHandler.insertFollowUpData(followUpNew);

                    //update lead status
                    if (strOutcome.equals("1") || strOutcome.equals("4") || strOutcome.equals("5")) {
                        if (strLeaId != null && databaseHandler.getFollowupsCount(strLeaId) == 0) {
                            databaseHandler.updateLeadsReason("4", strLeaId, "false");
                        } else {
                            databaseHandler.updateLeadsReason("2", strLeaId, "false");
                        }
                    } else {
                        databaseHandler.updateLeadsReason("2", strLeaId, "false");
                    }

                    //Update List of previous fragment
                    Intent intent = new Intent(getString(R.string.refresh_followups));
                    intent.putExtra(getString(R.string.message), getString(R.string.update_list));
                    intent.putExtra(getString(R.string.foid), foid);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    //Go back to previous Fragment
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStackImmediate();
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
            CustomisedToast.error(getActivity(), getString(R.string.select_date_and_time_properly)).show();
        } catch (Exception e) {
            e.printStackTrace();
            CustomisedToast.error(getActivity(), getString(R.string.select_date_and_time_properly)).show();
        }
    }

    private void sendTakenData(final long micro_seconds) {
        try {
            task = getString(R.string.record_follow_up_task);
            if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
            }

            valuesTobeSend();
            progressBar.setMax(100);
            progressBar.setVisibility(View.VISIBLE);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.recordFollowUp(version, key, task, userId, accessToken, strLeaId, strEnid, strQoid,
                    strChkoid, strCuid, strJocaid, selectedRadioButton, strScheduleDate, strAlertTime, strCommunicatedMode, strOutcome,
                    strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment, strContactedPersonId, strCommunicationMode,
                    String.valueOf(micro_seconds), foid, strScheduleNextFollowUp);
            //strFromTime,strToTime,
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
                    /*databaseHandler.updateRecordFollowUps(foid, strLeaId, strEnid, strQoid,
                            strChkoid, strCuid, strJocaid, selectedRadioButton, strScheduleDate, strAlertTime, strCommunicatedMode,
                            strOutcome, strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment,
                            strContactedPersonId, strCommunicationMode, true, String.valueOf(micro_seconds));*/
                        if (radioButtonScheduled.isChecked()) {
                            if (linearLayoutAddDetails.getVisibility() == View.VISIBLE &&
                                    checkBoxNextFollowUp.isChecked()) {
                                databaseHandler.insertRecordFollowUps(foid, strLeaId, strEnid, strQoid,
                                        strChkoid, strCuid, strJocaid, selectedRadioButton, strScheduleDate, strAlertTime, strCommunicatedMode,
                                        strOutcome, strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment,
                                        strContactedPersonId, strCommunicationMode, true, String.valueOf(micro_seconds), strScheduleNextFollowUp);


                                //Update status of current follow up
                                FollowUp followUpOld = new FollowUp();
                                followUpOld.setFoid(foid);
                                followUpOld.setFosid("2");
                                followUpOld.setPerson_type(followUp.getPerson_type());
                                followUpOld.setTakenOn(followUp.getTakenOn());
                                followUpOld.setScheduledDate(followUp.getScheduledDate());
                                followUpOld.setAlertOn(editTextAlertTime.getText().toString().trim());
                                followUpOld.setContactPerson(strContactedPersonName);
                                followUpOld.setCodeid(strContactedPersonId);
                                followUpOld.setName(followUp.getName());
                                //followUpOld.setFollowupCommunicationMode(communicationModeSpinner.getSelectedItem().toString());
                                followUpOld.setSyncId(followUp.getSyncId());
                                followUpOld.setLeadId(strLeaId);
                                followUpOld.setQoid(strQoid);
                                followUpOld.setEnid(strEnid);
                                followUpOld.setChkoid(strChkoid);
                                followUpOld.setComment(strComment);
                                followUpOld.setParentId(followUp.getParentId());
                                followUpOld.setCreatedUser(followUp.getCreatedUser());
                                followUpOld.setUpdatedUser(userName);
                                followUpOld.setReason(editTextReason.getText().toString());
                                followUpOld.setFeedback(strDescription);
                                followUpOld.setFollowupOutcome(strOutcome);
                                followUpOld.setCreatedTs(followUp.getCreatedTs());
                                followUpOld.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                                followUpOld.setUpdatedTs(followUp.getUpdatedTs());
                                followUpOld.setContactedPerson(strContactedPersonName);
                                followUpOld.setFollowupCommunicationMode(communicatedModeSpinner.getSelectedItem().toString());
                                if (foid != null) {
                                    databaseHandler.updateFollowUpStatus(followUpOld, getString(R.string.foid));
                                } else {
                                    //followUpOld.setSyncId(followUp.getSyncId());
                                    databaseHandler.updateFollowUpStatus(followUpOld, "sync");
                                }

                                //Create New Scheduled Follow Up
                                FollowUp followUpNew = new FollowUp();
                                followUpNew.setFoid("");
                                followUpNew.setFosid("1");
                                followUpNew.setPerson_type(followUp.getPerson_type());
                                followUpNew.setScheduledDate(editTextScheduledTime.getText().toString().trim());
                                followUpNew.setAlertOn(editTextAlertTime.getText().toString().trim());
                                followUpNew.setContactPerson(strContactPersonName);
                                followUpNew.setCodeid(strContactPersonId);
                                followUpNew.setName(followUp.getName());
                                followUpNew.setFollowupCommunicationMode(communicationModeSpinner.getSelectedItem().toString());
                                followUpNew.setSyncId(String.valueOf(micro_seconds));
                                followUpNew.setLeadId(strLeaId);
                                followUpNew.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                                followUpNew.setAlertMode(strAlertMode);
                                followUpNew.setCreatedTs(followUp.getCreatedTs());
                                followUpNew.setUpdatedTs(followUp.getUpdatedTs());
                                followUpNew.setCreatedUser(followUp.getCreatedUser());
                                followUpNew.setSyncStatus(String.valueOf(false));
                                databaseHandler.insertFollowUpData(followUpNew);

                                //update lead status
                                if (strOutcome.equals("1") || strOutcome.equals("4") || strOutcome.equals("5")) {
                                    if (strLeaId != null && databaseHandler.getFollowupsCount(strLeaId) == 0) {
                                        databaseHandler.updateLeadsReason("4", strLeaId, "true");
                                    } else {
                                        databaseHandler.updateLeadsReason("2", strLeaId, "true");
                                    }
                                } else {
                                    databaseHandler.updateLeadsReason("2", strLeaId, "true");
                                }

                                //Refresh Followups
                                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                                    refreshFollowUps(getActivity());
                                } else {
                                    updateUI();
                                }

                            } else {
                                databaseHandler.insertRecordFollowUps(foid, strLeaId, strEnid, strQoid,
                                        strChkoid, strCuid, strJocaid, selectedRadioButton, strScheduleDate, strAlertTime, strCommunicatedMode,
                                        strOutcome, strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment,
                                        strContactedPersonId, strCommunicationMode, true, String.valueOf(micro_seconds), strScheduleNextFollowUp);

                                //Update status of current follow up
                                FollowUp followUpOld = new FollowUp();
                                followUpOld.setFoid(foid);
                                followUpOld.setFosid("2");
                                followUpOld.setPerson_type(followUp.getPerson_type());
                                followUpOld.setTakenOn(followUp.getTakenOn());
                                followUpOld.setFeedback(strDescription);
                                followUpOld.setScheduledDate(followUp.getScheduledDate());
                                followUpOld.setAlertOn(editTextAlertTime.getText().toString().trim());
                                followUpOld.setContactPerson(strContactedPersonName);
                                followUpOld.setCodeid(strContactedPersonId);
                                followUpOld.setName(followUp.getName());
                                //followUpOld.setFollowupCommunicationMode(communicationModeSpinner.getSelectedItem().toString());
                                followUpOld.setSyncId(followUp.getSyncId());
                                followUpOld.setLeadId(strLeaId);
                                followUpOld.setQoid(strQoid);
                                followUpOld.setEnid(strEnid);
                                followUpOld.setChkoid(strChkoid);
                                followUpOld.setComment(strComment);
                                followUpOld.setParentId(followUp.getParentId());
                                followUpOld.setCreatedUser(followUp.getCreatedUser());
                                followUpOld.setUpdatedUser(userName);
                                followUpOld.setReason(editTextReason.getText().toString());
                                followUpOld.setFeedback(strDescription);
                                followUpOld.setFollowupOutcome(strOutcome);
                                followUpOld.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                                followUpOld.setCreatedTs(followUp.getCreatedTs());
                                followUpOld.setUpdatedTs(followUp.getUpdatedTs());
                                followUpOld.setContactedPerson(strContactedPersonName);
                                followUpOld.setFollowupCommunicationMode(communicatedModeSpinner.getSelectedItem().toString());
                                if (foid != null) {
                                    databaseHandler.updateFollowUpStatus(followUpOld, getString(R.string.foid));
                                } else {
                                    //followUpOld.setSyncId(String.valueOf(micro_seconds));
                                    databaseHandler.updateFollowUpStatus(followUpOld, "sync");
                                }

                                //updateCount lead status
                                if (strOutcome.equals("1") || strOutcome.equals("4") || strOutcome.equals("5")) {
                                    if (strLeaId != null && databaseHandler.getFollowupsCount(strLeaId) == 0) {
                                        databaseHandler.updateLeadsReason("4", strLeaId, "true");
                                    } else {
                                        databaseHandler.updateLeadsReason("2", strLeaId, "true");
                                    }
                                } else {
                                    databaseHandler.updateLeadsReason("2", strLeaId, "true");
                                }

                                //Refresh Followups
                                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                                    refreshFollowUps(getActivity());
                                } else {
                                    updateUI();
                                }

                            }
                        } else {
                            databaseHandler.insertRecordFollowUps(foid, strLeaId, strEnid, strQoid,
                                    strChkoid, strCuid, strJocaid, selectedRadioButton, currentDateFormat.format(new Date()), strAlertTime, strCommunicatedMode,
                                    strOutcome, strDescription, strReason, strAssignee, strContactPersonId, strAlertMode, strComment,
                                    strContactedPersonId, strCommunicationMode, true, String.valueOf(micro_seconds), strScheduleNextFollowUp);

                            //Added Fresh Taken FollowUp
                            FollowUp followUpNew = new FollowUp();
                            followUpNew.setFoid("");
                            followUpNew.setFosid("2");
                            followUpNew.setFollowupCommunicationMode(communicatedModeSpinner.getSelectedItem().toString());
                            followUpNew.setPerson_type(followUp.getPerson_type());
                            followUpNew.setContactPerson(strContactPersonName);
                            followUpNew.setCodeid(strContactPersonId);
                            followUpNew.setName(followUp.getName());
                            followUpNew.setSyncId(String.valueOf(micro_seconds));
                            followUpNew.setLeadId(strLeaId);
                            followUpNew.setQoid(strQoid);
                            followUpNew.setEnid(strEnid);
                            followUpNew.setChkoid(strChkoid);
                            followUpNew.setAssigned_user(assignFollowupAutoCompleteTextView.getText().toString());
                            followUpNew.setAlertMode(strAlertMode);
                            followUpNew.setSyncStatus(String.valueOf(false));
                            followUpNew.setScheduledDate(currentDateFormat.format(new Date()));
                            databaseHandler.insertFollowUpData(followUpNew);

                            //update lead status
                        /*if (strOutcome.equals("1") || strOutcome.equals("4") || strOutcome.equals("5")) {
                            databaseHandler.updateLeadsReason("4", strLeaId);
                        }*/
                            if (strOutcome.equals("1") || strOutcome.equals("4") || strOutcome.equals("5")) {
                                if (strLeaId != null && databaseHandler.getFollowupsCount(strLeaId) == 0) {
                                    databaseHandler.updateLeadsReason("4", strLeaId, "true");
                                } else {
                                    databaseHandler.updateLeadsReason("2", strLeaId, "true");
                                }
                            } else {
                                databaseHandler.updateLeadsReason("2", strLeaId, "true");
                            }

                            //Refresh Followups
                            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                                refreshFollowUps(getActivity());
                            } else {
                                updateUI();
                            }
                        }

                    } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        if (getActivity() != null && isAdded()) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }
                    } else {
                        if (getActivity() != null && isAdded()) {
                            Log.e("ERROR", apiResponse.getMessage());
                            CustomisedToast.error(getActivity(), apiResponse.getMessage()).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (getActivity() != null && isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        Log.d("errorInLead", t.getMessage() + "");
                        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        try {
            if (getActivity() != null && isAdded()) {
                //Update List of previous fragment
                Intent intent = new Intent(getString(R.string.refresh_followups));
                intent.putExtra(getString(R.string.message), getString(R.string.update_list));
                intent.putExtra(getString(R.string.foid), foid);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                //Go back to previous Fragment
                FragmentManager fm = getFragmentManager();
                fm.popBackStackImmediate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void valuesTobeSend() {
        if (followUp.getLeadId() != null && !followUp.getLeadId().isEmpty()) {
            strLeaId = followUp.getLeadId();
        }
        if (followUp.getQoid() != null && !followUp.getQoid().isEmpty()) {
            strQoid = followUp.getQoid();
        }
        if (followUp.getEnid() != null && !followUp.getEnid().isEmpty()) {
            strEnid = followUp.getEnid();
        }
        if (followUp.getChkoid() != null && !followUp.getChkoid().isEmpty()) {
            strChkoid = followUp.getChkoid();
        }
        /*strEnid = "";
        if (followUp.getQoid() != null && !followUp.getQoid().isEmpty()) {
            strQoid = followUp.getQoid();
        } else {
            strQoid = "";
        }
        strChkoid = "";*/
        strCuid = "";
        strJocaid = "";
        strSchedule = "";

//        strOutcome = String.valueOf(spinnerOutcome.getSelectedItemPosition() + 1);
        strCommunicatedMode = communicationModeList.get
                (communicatedModeSpinner.getSelectedItemPosition()).getCommid();
        strDescription = editTextInfo.getText().toString();
        strReason = editTextReason.getText().toString();
        if (outcome.length > 0) {
            strOutcome = String.valueOf(spinnerOutcome.getSelectedItemPosition() + 1);
        } else {
            strOutcome = "";
        }
        if (arrayContactPerson.length > 0) {
            strContactedPersonId = arrayContactPersonId[contactedPersonSpinner.getSelectedItemPosition()];
            strContactedPersonName = arrayContactPerson[contactedPersonSpinner.getSelectedItemPosition()];
        } else {
            strContactedPersonId = "";
            strContactPersonName = "";
        }
        // strContactPersonId = contactPersonSpinner.getSelectedItem().toString();
        if (arrayContactPerson.length > 0) {
            strContactPersonId = arrayContactPersonId[contactPersonSpinner.getSelectedItemPosition()];
            strContactPersonName = arrayContactPerson[contactPersonSpinner.getSelectedItemPosition()];
        } else {
            strContactPersonId = "";
            strContactPersonName = "";
        }
        strComment = editTextComment.getText().toString();
        if (radioButtonScheduled.isChecked()) {
            if (linearLayoutAddDetails.getVisibility() == View.VISIBLE &&
                    checkBoxNextFollowUp.isChecked()) {
                selectedRadioButton = "1";
                strScheduleNextFollowUp = "1";
                if (outcome.length > 0) {
                    strOutcome = String.valueOf(spinnerOutcome.getSelectedItemPosition() + 1);
                } else {
                    strOutcome = "";
                }
                //Contacted & Contact Person
                if (arrayContactPerson.length > 0) {
                    strContactedPersonId = arrayContactPersonId[contactedPersonSpinner.getSelectedItemPosition()];
                    strContactedPersonName = arrayContactPerson[contactedPersonSpinner.getSelectedItemPosition()];
                } else {
                    strContactedPersonId = "";
                    strContactPersonName = "";
                }
                if (arrayContactPerson.length > 0) {
                    strContactPersonId = arrayContactPersonId[contactPersonSpinner.getSelectedItemPosition()];
                    strContactPersonName = arrayContactPerson[contactPersonSpinner.getSelectedItemPosition()];
                } else {
                    strContactPersonId = "";
                    strContactPersonName = "";
                }

                //Assignee Id
                int pos = -1;
                for (int i = 0; i < assigneeNameList.size(); i++) {
                    if (assigneeNameList.get(i).equals(assignFollowupAutoCompleteTextView.getText().toString().trim())) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1) {
                    strAssignee = assigneeIDList.get(pos).toString();
                }

                //Communication Mode
                strCommunicationMode = communicationModeList.get(communicationModeSpinner.getSelectedItemPosition())
                        .getCommid();
                strScheduleDate = editTextScheduledTime.getText().toString();
                strAlertTime = editTextAlertTime.getText().toString();
                if (checkboxSendMail.isChecked() && !checkboxSendSms.isChecked())
                    strAlertMode = "2";
                else if (!checkboxSendMail.isChecked() && checkboxSendSms.isChecked())
                    strAlertMode = "1";
                else if (!checkboxSendMail.isChecked() && !checkboxSendSms.isChecked())
                    strAlertMode = "0";
                else strAlertMode = "[1,2]";
            } else {
                selectedRadioButton = "1";
                strContactPersonId = "";
                strContactPersonName = "";
                strAssignee = "";
                strCommunicationMode = "";
                strScheduleDate = "";
                strAlertTime = "";
                strAlertMode = "";
                strScheduleNextFollowUp = "2";
            }
        } else {
            selectedRadioButton = "2";
            strContactPersonId = "";
            strContactPersonName = "";
            strAssignee = "";
            strCommunicationMode = "";
            strScheduleDate = "";
            strAlertTime = "";
            strAlertMode = "";
            strScheduleNextFollowUp = "2";
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.taken_outcome:
                strOutcome = spinnerOutcome.getSelectedItem().toString();
                //strOutcome = outcome[position];
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void passDate(String s) {
        String stringStartDate = null, stringEndDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (datePickerFragment.getTag().equals(getString(R.string.dailogue_from))) {
            stringStartDate = s;
            try {
                startDate = formatter.parse(stringStartDate);
                System.out.println(startDate);
                stringStartDate = targetFormat.format(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            editTextScheduledTime.setText(stringStartDate);
            strScheduleDate = editTextScheduledTime.getText().toString();
            timePickerFragment.show(getFragmentManager(), getString(R.string.taken_from));
        } else {
            try {
                stringEndDate = s;
                enddate = formatter.parse(stringEndDate);
                stringEndDate = targetFormat.format(enddate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            editTextAlertTime.setText(stringEndDate);
            strAlertTime = editTextAlertTime.getText().toString();

            //TODO Added in June
            try {
                if (editTextScheduledTime.getText().toString() == null ||
                        editTextScheduledTime.getText().toString().isEmpty()) {
                    timePickerFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.taken_to));
                } else {

                    //Calculating Time
                    String[] strDate = editTextScheduledTime.getText().toString().split("\\s+");
                    String strSeconds = strDate[1].toString().substring(strDate[1].lastIndexOf(":") + 1,
                            strDate[1].toString().length());
                    String strHourMinute = strDate[1].toString().substring(0, strDate[1].lastIndexOf(":"));
                    String strHour = strHourMinute.toString().substring(0, strHourMinute.lastIndexOf(":"));
                    String strMinute = strHourMinute.toString().substring(strHourMinute.lastIndexOf(":") + 1,
                            strHourMinute.length());
                    Bundle bundle = new Bundle();
                    bundle.putString("hour", strHour.toString());
                    bundle.putString("minute", strMinute.toString());
                    timePickerFragment.setArguments(bundle);
                    timePickerFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.taken_to));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //timePickerFragment.show(getFragmentManager(), getString(R.string.taken_to));
        }
    }

    @Override
    public void passTime(String s) {
        String stringStartTime = null, stringEndTime = null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        DateFormat targetFormat = new SimpleDateFormat("HH:mm:ss");
        if (timePickerFragment.getTag().equals(getString(R.string.taken_from))) {
            try {
                Date startDate = formatter.parse(s);
                System.out.println(startDate);
                stringStartTime = targetFormat.format(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            editTextScheduledTime.setText(strScheduleDate + " " + stringStartTime);
            strFromTime = stringStartTime;
        } else {
            try {
                Date endDate = formatter.parse(s);
                System.out.println(endDate);
                stringEndTime = targetFormat.format(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            editTextAlertTime.setText(strAlertTime + " " + stringEndTime);
            strToTime = stringEndTime;
        }
    }

    //Refresh Followups
    private void refreshFollowUps(Activity activity) {
        try {
            task = getString(R.string.follow_up_fetch);
            String lastUpdated;
            if (AppPreferences.getIsLogin(activity, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(activity, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(activity, AppUtils.DOMAIN);
            }

            if (databaseHandler.getSyncTime(task).getCallTime() != null) {
                lastUpdated = databaseHandler.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            String type = "";
            Call<ApiResponse> call = apiService.getFollowUp(version, key, task, userId, accessToken, lastUpdated);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        ApiResponse apiResponse = (ApiResponse) response.body();
                        if (apiResponse.getSuccess()) {
                            for (FollowUp followUp : apiResponse.getData().getFollowups()) {

                                if (followUp.getFoid() != null && !followUp.getFoid().equals("null") && !followUp.getFoid().isEmpty()) {
                                    if (databaseHandler.checkFollowUpResult(followUp.getFoid())) {
                                        databaseHandler.updateFollowUp(followUp);
                                    } else {
                                        if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty() && databaseHandler.checkSyncIdFollowUp(followUp.getSyncId())) {
                                            databaseHandler.updateFollowUpDataSyncId(followUp);
                                        } else {
                                            if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                                    && databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                                    ) {

                                                for (int i = 0; i < databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                                    if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                            .equals(followUp.getContactedPerson())) {
                                                        followUp.setContactsId(String.valueOf(databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                                    }
                                                }
                                            }
                                            databaseHandler.insertFollowUpData(followUp);
                                            Log.d("Follow up ID 1", String.valueOf(databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getID()));
                                        }
                                    }
                                } else {
                                    if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                            && databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                            ) {

                                        for (int i = 0; i < databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                            if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                    .equals(followUp.getContactedPerson())) {
                                                followUp.setContactsId(String.valueOf(databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                            }
                                        }
                                    }
                                    databaseHandler.insertFollowUpData(followUp);
                                }


                                SyncCheck syncCheck = new SyncCheck();
                                syncCheck.setService(getString(R.string.follow_up_fetch));
                                if (!apiResponse.getData().getFollowups().equals("") && apiResponse.getData().getFollowups().size() > 0) {
                                    syncCheck.setCallTime(apiResponse.getData().getFollowups().get(apiResponse.getData().getFollowups().size() - 1).getUpdatedTs());
                                } else {
                                    syncCheck.setCallTime(databaseHandler.getFollowUpUpdatedTs());
                                    Log.d("Last updated Ts", databaseHandler.getFollowUpUpdatedTs());
                                }
                                if (databaseHandler != null) {
                                    databaseHandler.updateSyncCheck(syncCheck);
                                }
                            }
                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        } else {
                            if (getActivity() != null && isAdded()) {
                                Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (getActivity() != null && isAdded()) {
                            //Update UI
                            updateUI();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (getActivity() != null && isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
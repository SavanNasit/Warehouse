package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.Lead;

import java.util.List;

/**
 * Created by poonam on 10/7/17.
 */

public class LeadsSubContactsAdapter extends RecyclerView.Adapter<LeadsSubContactsAdapter.MyViewHolder> {
    EditSubContactsViewInterface editSubContactsViewInterface;
    private String strMode;
    private Activity activity;
    private List<Contacts> contactsList;
    private AlertDialog alertDialog;
    private Lead leadData = new Lead();
    private String checkboxSelected;

    public LeadsSubContactsAdapter(Activity activity, Lead leadData, List<Contacts> contactsList, EditSubContactsViewInterface editSubContactsViewInterface, String stringMode) {
        this.activity = activity;
        this.contactsList = contactsList;
        this.editSubContactsViewInterface = editSubContactsViewInterface;
        this.strMode = stringMode;
        this.leadData = leadData;
    }

    public final boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public LeadsSubContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_lead_sub_contacts, parent, false);

        return new LeadsSubContactsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LeadsSubContactsAdapter.MyViewHolder holder, final int position) {
        try {
            final Contacts leadContact = contactsList.get(position);
            holder.textViewName.setText(leadContact.getName());
            if (leadContact.getDesignation() != null && !leadContact.getDesignation().isEmpty()) {
                holder.textViewDesignation.setText("(" + leadContact.getDesignation() + ")");
                holder.textViewDesignation.setVisibility(View.VISIBLE);
            } else {
                holder.textViewDesignation.setText("");
                holder.textViewDesignation.setVisibility(View.GONE);
            }
            holder.textViewEmail.setText(leadContact.getEmail());
            holder.textViewPhoneNumber.setText(leadContact.getPhoneNo());

            if (contactsList.get(position).getIsOwner() != null && !contactsList.get(position).getIsOwner().isEmpty()
                    && contactsList.get(position).getIsOwner().equals("1")) {
                holder.imageViewIsOwner.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewIsOwner.setVisibility(View.INVISIBLE);
            }
       /* if (strMode.equals(activity.getString(R.string.edit_mode))) {
            holder.textViewDeleteRow.setVisibility(View.GONE);
            holder.imageViewEdit.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDeleteRow.setVisibility(View.VISIBLE);
            holder.imageViewEdit.setVisibility(View.GONE);
        }*/

            holder.textViewDeleteRow.setVisibility(View.GONE);
            holder.imageViewEdit.setVisibility(View.VISIBLE);

            holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogEditContacts(position, leadContact, holder);
                }
            });

            holder.textViewDeleteRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (contactsList.size() == 1) {
                            if (editSubContactsViewInterface != null) {
                                editSubContactsViewInterface.notifyToRemoveView(position);
                            }
                        }

                        contactsList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogEditContacts(final int position, final Contacts leadContact, final MyViewHolder holder) {
        try {
            View dialogView = View.inflate(activity, R.layout.dialog_lead_edit_contacts, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView)
                    .setCancelable(true);
            alertDialog = builder.create();
            final EditText editTextName, editTextDesignation, editTextEmail, editTextPhone;

            editTextName = (EditText) dialogView.findViewById(R.id.name);
            editTextDesignation = (EditText) dialogView.findViewById(R.id.designation);
            editTextEmail = (EditText) dialogView.findViewById(R.id.email);
            editTextPhone = (EditText) dialogView.findViewById(R.id.phone_number);
            Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
            Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
            ImageView imageViewDelete = (ImageView) dialogView.findViewById(R.id.edit_lead_delete);
            final CheckBox checkBoxOwner = (CheckBox) dialogView.findViewById(R.id.edit_contact_check_owner);

            editTextName.setText(leadContact.getName());
            editTextDesignation.setText(leadContact.getDesignation());
            editTextEmail.setText(leadContact.getEmail());
            editTextPhone.setText(leadContact.getPhoneNo());

            if (contactsList.get(position).getIsOwner() != null && !contactsList.get(position).getIsOwner().isEmpty()
                    && contactsList.get(position).getIsOwner().equals("1")) {
                checkBoxOwner.setChecked(true);
                checkboxSelected = "1";
            } else {
                checkBoxOwner.setChecked(false);
                checkboxSelected = "0";
            }

            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (editSubContactsViewInterface != null) {
                            editSubContactsViewInterface.notifyToRemoveView(position);
                        }
                        contactsList.remove(position);
                        notifyDataSetChanged();
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            checkBoxOwner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkboxSelected = "1";
                    } else {
                        checkboxSelected = "0";
                    }
                }
            });

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (editTextName.getText().toString().trim().isEmpty()) {
                            editTextName.setError(v.getContext().getString(R.string.enter_name));
                        } /*else if (editTextEmail.getText().toString().trim().isEmpty()) {
                    editTextEmail.setError(v.getContext().getString(R.string.enter_email));
                }*/ else if (editTextEmail.getText().toString().trim().length() != 0
                                && !isValidEmail(editTextEmail.getText().toString())) {
                            editTextEmail.setError(v.getContext().getString(R.string.valid_email_error));
                        } /*else if (editTextPhone.getText().toString().trim().isEmpty()) {
                    editTextPhone.setError(v.getContext().getString(R.string.enter_phone_number));
                }*/ else if (editTextPhone.getText().toString().trim().length() != 0 &&
                                editTextPhone.getText().toString().trim().length() != 10) {
                            editTextPhone.setError("Phone number should be of 10 digits");
                        } else {
                            contactsList.remove(position);
                            Contacts contacts = new Contacts();
                            contacts.setCodeid(leadContact.getCodeid());
                            contacts.setName(editTextName.getText().toString().trim());
                            contacts.setDesignation(editTextDesignation.getText().toString().trim());
                            contacts.setEmail(editTextEmail.getText().toString().trim());
                            contacts.setPhoneNo(editTextPhone.getText().toString().trim());

                            if (checkboxSelected != null && checkboxSelected.equals("1")) {
                                contacts.setIsOwner("1");
                            } else {
                                contacts.setIsOwner("0");
                            }


                            if (editSubContactsViewInterface != null) {
                                editSubContactsViewInterface.notifyToEditContact(contacts, position);
                            }
                            alertDialog.dismiss();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
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

            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public interface EditSubContactsViewInterface {
        void notifyToRemoveView(int position);

        void notifyToEditContact(Contacts contacts, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewDesignation, textViewEmail, textViewPhoneNumber, textViewDeleteRow;
        public ImageView imageViewEdit, imageViewIsOwner;
        public LinearLayout linearLayoutContainer;

        public MyViewHolder(View view) {
            super(view);
            textViewName = (TextView) view.findViewById(R.id.name);
            textViewDesignation = (TextView) view.findViewById(R.id.designation);
            textViewEmail = (TextView) view.findViewById(R.id.email);
            textViewPhoneNumber = (TextView) view.findViewById(R.id.phone_number);
            textViewDeleteRow = (TextView) view.findViewById(R.id.delete_row);
            imageViewEdit = (ImageView) view.findViewById(R.id.edit_row);
            imageViewIsOwner = (ImageView) view.findViewById(R.id.lead_sub_contacts_owner);
            linearLayoutContainer = (LinearLayout) view.findViewById(R.id.container_lead_sub_contacts);
        }
    }

}

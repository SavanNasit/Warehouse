package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.ReferralAddContact;

import java.util.List;

/**
 * Created by agt on 12/12/17.
 */

public class AddCustomerContactsAdapter extends RecyclerView.Adapter<AddCustomerContactsAdapter.MyViewHolder> {
    EditContactsViewInterface editContactsViewInterface;
    private String strMode;
    private Context mContext;
    private Activity activity;
    private List<Contacts> contactsList;
    private AlertDialog alertDialog;
    private List<ReferralAddContact> contactPersonTypeArrayList;

    public AddCustomerContactsAdapter(Activity activity, List<Contacts> contactsList,
                                      List<ReferralAddContact> contactPersonTypeArrayList,
                                      EditContactsViewInterface editContactsViewInterface, String stringMode) {
        this.activity = activity;
        this.contactsList = contactsList;
        this.contactPersonTypeArrayList = contactPersonTypeArrayList;
        this.editContactsViewInterface = editContactsViewInterface;
        this.strMode = stringMode;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_add_customer_add_contact, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Contacts contacts = contactsList.get(position);
        holder.textViewName.setText(contacts.getName());
        if (contacts.getDesignation() != null && !contacts.getDesignation().toString().trim().isEmpty()) {
            holder.textViewDesignation.setText("(" + contacts.getDesignation() + ")");
        }
        holder.textViewEmail.setText(contacts.getEmail());
        holder.textViewPhoneNumber.setText(contacts.getPhoneNo());
        if (contactPersonTypeArrayList != null && contactPersonTypeArrayList.size() > 1) {
            holder.contactTypeTextView.setText(contacts.getType());
            for (int i = 1; i < contactPersonTypeArrayList.size(); i++) {
                if (contactPersonTypeArrayList.get(i).getCusavid().equals(contacts.getContactTypeValue())) {
                    holder.contactTypeTextView.setText(contacts.getType());
                }
            }
            holder.contactTypeTextView.setVisibility(View.VISIBLE);
            if (contacts.isCheckOwner()) {
                if (contacts.getName() != null && !contacts.getName().isEmpty()) {
                    if (contacts.getName().trim().length() > 10) {
                        holder.textViewName.setText(contacts.getName().trim().substring(0, 8) + "..");
                    }
                }
                holder.textViewName.append("    [Owner]");
            }
        } else if (contacts.isCheckOwner()) {
            if (contacts.getName() != null && !contacts.getName().isEmpty()) {
                if (contacts.getName().trim().length() > 10) {
                    holder.textViewName.setText(contacts.getName().trim().substring(0, 8) + "..");
                }
            }
            holder.textViewName.append("    [Owner]");
            holder.contactTypeTextView.setVisibility(View.GONE);
        } else {
            holder.contactTypeTextView.setText("");
            holder.contactTypeTextView.setVisibility(View.GONE);
        }

        if (strMode.equals("Edit")) {
            holder.textViewDeleteRow.setVisibility(View.VISIBLE);
            holder.imageViewEdit.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDeleteRow.setVisibility(View.VISIBLE);
            holder.imageViewEdit.setVisibility(View.VISIBLE);
        }

        holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogEditContacts(position, contacts);
                if (editContactsViewInterface != null) {
                    editContactsViewInterface.notifyToEditContact(contacts, position);
                }
            }
        });

        holder.textViewDeleteRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (contactsList.size() == 1) {
                        if (editContactsViewInterface != null) {
                            editContactsViewInterface.notifyToRemoveView();
                        }
                    }
                    contactsList.remove(position);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void dialogEditContacts(final int position, Contacts leadContact) {
        View dialogView = View.inflate(activity, R.layout.dialog_lead_edit_contacts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextName, editTextDesignation, editTextEmail, editTextPhone;

        LinearLayout contactTypeLayout;
        TextView contactTypeTextView;
        Spinner contactTypeSpinner;

        editTextName = (EditText) dialogView.findViewById(R.id.name);
        editTextDesignation = (EditText) dialogView.findViewById(R.id.designation);
        editTextEmail = (EditText) dialogView.findViewById(R.id.email);
        editTextPhone = (EditText) dialogView.findViewById(R.id.phone_number);
        Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        ImageView imageViewDelete = (ImageView) dialogView.findViewById(R.id.edit_lead_delete);
        contactTypeLayout = (LinearLayout) dialogView.findViewById(R.id.contactType_layout);
        contactTypeTextView = (TextView) dialogView.findViewById(R.id.contact_type_textView);
        contactTypeSpinner = (Spinner) dialogView.findViewById(R.id.contact_type_spinner);
        contactTypeLayout.setVisibility(View.VISIBLE);

        editTextName.setText(leadContact.getName());
        editTextDesignation.setText(leadContact.getDesignation());
        editTextEmail.setText(leadContact.getEmail());
        editTextPhone.setText(leadContact.getPhoneNo());


        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (contactsList.size() == 1) {
                        if (editContactsViewInterface != null) {
                            editContactsViewInterface.notifyToRemoveView();
                        }
                    }
                    contactsList.remove(position);
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextName.getText().toString().isEmpty()) {
                    editTextName.setError(mContext.getString(R.string.enter_name));
                } else if (editTextPhone.getText().toString().isEmpty()) {
                    editTextPhone.setError(mContext.getString(R.string.enter_phone_number));
                } else {
                    contactsList.remove(position);
                    Contacts contacts = new Contacts();
                    contacts.setCodeid(contactsList.get(position).getCodeid());
                    contacts.setName(editTextName.getText().toString());
                    contacts.setDesignation(editTextDesignation.getText().toString());
                    contacts.setEmail(editTextEmail.getText().toString());
                    contacts.setPhoneNo(editTextPhone.getText().toString());
                    if (editContactsViewInterface != null) {

                        editContactsViewInterface.notifyToEditContact(contacts, position);
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

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public interface EditContactsViewInterface {
        void notifyToRemoveView();

        void notifyToEditContact(Contacts contacts, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewDesignation, textViewEmail, textViewPhoneNumber, textViewDeleteRow,
                contactTypeTextView;
        public ImageView imageViewEdit;
        private RadioGroup radioGroup;
        private RadioButton radioButton;

        public MyViewHolder(View view) {
            super(view);
            textViewName = (TextView) view.findViewById(R.id.name);
            textViewDesignation = (TextView) view.findViewById(R.id.designation);
            textViewEmail = (TextView) view.findViewById(R.id.email);
            textViewPhoneNumber = (TextView) view.findViewById(R.id.phone_number);
            textViewDeleteRow = (TextView) view.findViewById(R.id.delete_row);
            imageViewEdit = (ImageView) view.findViewById(R.id.edit_row);
            contactTypeTextView = (TextView) view.findViewById(R.id.contact_type_textView);
            radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
            radioButton = (RadioButton) view.findViewById(R.id.radioButton);
            contactTypeTextView.setVisibility(View.VISIBLE);

        }
    }

}

package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.UploadDocument;

import java.util.List;

/**
 * Created by poonam on 12/4/17.
 */

public class DocumentUploaderAdapter extends RecyclerView.Adapter<DocumentUploaderAdapter.MyViewHolder> {
    private Context context;
    private List<UploadDocument> documentList;
    private DocAdapterListener listener;

    public DocumentUploaderAdapter(Context context, List<UploadDocument> documentList, DocAdapterListener listener) {
        this.context = context;
        this.documentList = documentList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_upload_doc, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.docNameTextView.setText("" + documentList.get(position).getFileName());
        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return documentList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickedDeleteBtn(position);
            }
        });

    }

    public interface DocAdapterListener {
        void onClickedDeleteBtn(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView docNameTextView;
        private ImageView deleteImageView;

        public MyViewHolder(View view) {
            super(view);
            docNameTextView = (TextView) view.findViewById(R.id.doc_name_textView);
            deleteImageView = (ImageView) view.findViewById(R.id.delete_imageView);
        }
    }

}

package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.FlipAnimator;
import com.accrete.sixorbit.model.FollowUp;

import java.util.List;



/**
 * Created by poonam on 13/4/17.
 */

public class InDetailFollowUpAdapter extends RecyclerView.Adapter<InDetailFollowUpAdapter.MyViewHolder> {
    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    private Context mContext;
    private List<FollowUp> FollowUps;
    private InDetailFollowUpAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private int mExpandedPosition = -1;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private RecyclerView recyclerView;

    public InDetailFollowUpAdapter(Context mContext, List<FollowUp> FollowUps, InDetailFollowUpAdapterListener listener) {
        this.mContext = mContext;
        this.FollowUps = FollowUps;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_follow_up, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        FollowUp FollowUp = FollowUps.get(position);

        // displaying text view data
        holder.from.setText(FollowUp.getName());
        holder.subject.setText(FollowUp.getContactPerson());
        holder.content.setText(FollowUp.getAlertOn());


        // displaying the first letter of From in icon text

        final boolean isExpanded = position==mExpandedPosition;


        holder.subject.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);

        if (position == FollowUps.size() - 1) {
            holder.subject.setVisibility(isExpanded?View.VISIBLE:View.VISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
            //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    TransitionManager.beginDelayedTransition(recyclerView);
            //    }
                notifyDataSetChanged();
            }
        });
        try {
            holder.iconText.setText(FollowUp.getName().substring(0, 1));
        }catch (Exception e){
            e.printStackTrace();
        }
        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));


        // change the font style depending on messageTextView read status
       // applyReadStatus(holder, FollowUp);

        // handle messageTextView star
      //  applyImportant(holder, FollowUp);

        // handle icon animation
        applyIconAnimation(holder, position);

        // display profile image
        applyProfilePicture(holder);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });


    }

    private void applyProfilePicture(MyViewHolder holder) {
    /*    if (!TextUtils.isEmpty(FollowUp.getName())) {
            Glide.with(mContext).load(FollowUp.getName())
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageProfile);
            holder.imageProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        } else {
*/
            holder.imgProfile.setImageResource(R.drawable.bg_square);
            holder.iconText.setVisibility(View.VISIBLE);
       // }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(FollowUps.get(position).getFoid());
    }

    @Override
    public int getItemCount() {
        return FollowUps.size();
    }

    /*private void applyImportant(EnquiryViewHolder holder, FollowUp FollowUp) {
        if (FollowUp.isImportant()) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
        }
    }*/

   /* private void applyReadStatus(EnquiryViewHolder holder, FollowUp FollowUp) {
        if (FollowUp.isRead()) {
            holder.nameTextView.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.nameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.messageTextView));
        } else {
            holder.nameTextView.setTypeface(null, Typeface.BOLD);
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.nameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.nameTextView));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
        }
    }*/

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }


    public interface InDetailFollowUpAdapterListener {


        void onMessageRowClicked(int position);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView from, subject, content, iconText;
        public ImageView imgProfile;
        public LinearLayout contentContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;


        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            content = (TextView) view.findViewById(R.id.txt_secondary);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            contentContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);

        }

    }
}
package com.accrete.sixorbit.activity.AppIllustration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.ImageView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.chats.ChatMessageActivity;
import com.accrete.sixorbit.activity.chats.ContactsActivity;
import com.accrete.sixorbit.activity.followup.RecordFollowUpActivity;
import com.accrete.sixorbit.activity.lead.ActivityFeedsCommentActivity;
import com.accrete.sixorbit.activity.lead.LeadInfoActivity;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.service.ChatService;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import io.fabric.sdk.android.Fabric;


/**
 * Created by agt on 12/9/17.
 */

@SuppressLint("NewApi")
public class ApplicationClass extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    private static boolean isCommentsActivityVisible;
    private static Context context;

    public static boolean isActivityVisible() {
        return isCommentsActivityVisible;
    }

    public static Context context() {
        return context;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            context = getApplicationContext();
            Fabric.with(this, new Crashlytics());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                registerActivityLifecycleCallbacks(this);
            }
            runService();

       /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                    Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {
                    Glide.clear(imageView);
                }

                @Override
                public Drawable placeholder(Context ctx, String tag) {
                    //define different placeholders for different imageView targets
                    //default tags are accessible via the DrawerImageLoader.Tags
                    //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                    if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                        return DrawerUIUtils.getPlaceHolder(ctx);
                    } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                        return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(100);
                    } else if ("customUrlItem".equals(tag)) {
                        return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(100);
                    }
                    //we use the default one for
                    //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                    return super.placeholder(ctx, tag);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runService() {
        try {
            startService(new Intent(getApplicationContext(), ChatService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        try {
            Log.e("RESUMED", activity.getLocalClassName());
            if (activity instanceof ChatMessageActivity)
                isCommentsActivityVisible = true;
            else if (activity instanceof DrawerActivity) isCommentsActivityVisible = true;
            else if (activity instanceof AppIllustrationActivity) isCommentsActivityVisible = true;
            else if (activity instanceof ActivityFeedsCommentActivity)
                isCommentsActivityVisible = true;
            else if (activity instanceof ContactsActivity) isCommentsActivityVisible = true;
            else if (activity instanceof RecordFollowUpActivity) {
                isCommentsActivityVisible = true;
            } else if (activity instanceof LeadInfoActivity) {
                isCommentsActivityVisible = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        try {
            Log.e("PAUSED", activity.getLocalClassName());
            if (activity instanceof ChatMessageActivity)
                isCommentsActivityVisible = false;
            else if (activity instanceof DrawerActivity) isCommentsActivityVisible = false;
            else if (activity instanceof AppIllustrationActivity) isCommentsActivityVisible = false;
            else if (activity instanceof ActivityFeedsCommentActivity)
                isCommentsActivityVisible = false;
            else if (activity instanceof ContactsActivity) isCommentsActivityVisible = false;
            else if (activity instanceof RecordFollowUpActivity) {
                isCommentsActivityVisible = false;
            } else if (activity instanceof LeadInfoActivity) {
                isCommentsActivityVisible = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        try {
            Log.e("STOPPED", activity.getLocalClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}

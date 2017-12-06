package com.accrete.warehouse.login;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.password.PasswordActivity;
import com.accrete.warehouse.utils.EmailValidator;


/**
 * Created by poonam on 7/4/17.
 */


public class LoginActivity extends Activity implements View.OnClickListener {
    private EmailValidator emailValidator;
    TextView textViewNext;
    private EditText editTextEmail;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textViewNext = (TextView) findViewById(R.id.login_txt_next);
        editTextEmail = (EditText) findViewById(R.id.login_edittext_email);
        textViewNext.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.lightGreen));
        }
        emailValidator = new EmailValidator();
        editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    email = editTextEmail.getText().toString();
                    boolean valid = emailValidator.validateEmail(email);
                    if (valid) {
                        if (TextUtils.isEmpty(email)) {

                        } else {
                            Intent intentPassword = new Intent(LoginActivity.this, PasswordActivity.class);
                            intentPassword.putExtra(getString(R.string.email), email);
                            ActivityOptions options =
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_in_right);
                            startActivity(intentPassword, options.toBundle());
                        }
                    } else {
                        editTextEmail.setEnabled(false);
                        Toast.makeText(LoginActivity.this, getString(R.string.valid_email_error), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                editTextEmail.setEnabled(true);
                            }
                        }, 3000);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        email = editTextEmail.getText().toString();

        boolean valid = emailValidator.validateEmail(email);
        if (valid) {
            if (TextUtils.isEmpty(email)) {
                setUsernameError();
            } else {
                Intent intentPassword = new Intent(LoginActivity.this, PasswordActivity.class);
                intentPassword.putExtra(getString(R.string.email), email);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_in_right);
                startActivity(intentPassword, options.toBundle());
            }
        } else {
            textViewNext.setEnabled(false);
            Toast.makeText(LoginActivity.this, getString(R.string.valid_email_error), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    textViewNext.setEnabled(true);
                }
            }, 3000);
        }
    }


    public void setUsernameError() {
        //editTextEmail.setError(getString(R.string.username_error));
        Toast.makeText(this, getString(R.string.username_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_in_right);
        finish();
    }
}

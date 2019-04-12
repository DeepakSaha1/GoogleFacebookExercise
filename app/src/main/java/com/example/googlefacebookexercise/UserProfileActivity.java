package com.example.googlefacebookexercise;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class UserProfileActivity extends AppCompatActivity {
    private TextView mTvEmail;
    private TextView mTvName;
    private ImageView mIvProfile;
    private String mFBName;
    private String mFBEmail;
    private String mFBProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mTvName = findViewById(R.id.tv_profile_name);
        mTvEmail = findViewById(R.id.tV_profile_email);
        mIvProfile = findViewById(R.id.iv_profile_image);

        GoogleSignInAccount iAccount = getIntent().getParcelableExtra("GOOGLE_ACCOUNT");
        mFBName = getIntent().getStringExtra("NAME");
        mFBEmail = getIntent().getStringExtra("EMAIL");
        mFBProfileImage = getIntent().getStringExtra("IMAGE");

        Log.i("UserProfile", mFBName + ", " + mFBEmail + ", " + mFBProfileImage);

        if (iAccount != null) {
            mTvEmail.setText(iAccount.getEmail());
            mTvName.setText(iAccount.getDisplayName());

            Glide.with(UserProfileActivity.this)
                    .load(iAccount.getPhotoUrl())
                    .into(mIvProfile);
        } else if (mFBName != null && mFBEmail != null || mFBProfileImage != null) {
            mTvName.setText(mFBName);
            mTvEmail.setText(mFBEmail);
            Glide.with(UserProfileActivity.this)
                    .load(mFBProfileImage)
                    .into(mIvProfile);
        }

    }
}

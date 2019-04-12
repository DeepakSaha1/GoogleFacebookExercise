package com.example.googlefacebookexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mBtnGoogleSignIn;
    private LoginButton mLoginFacebook;
    private static final int RC_SIGN_IN = 0;
    private CallbackManager mCallbackManager;
    public static final String TAG = "MainActivity";

    public String mFBName;
    public String mFBEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnGoogleSignIn = findViewById(R.id.btn_sign_in_button);
        mLoginFacebook = findViewById(R.id.fb_login_button);


        // Configure sign-in to request the user's ID, mFBEmail address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions iGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, iGso);

        mBtnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        signInWithFacebook();
//        getHashkey();
    }

    // for fb update
    private void updateUI(String kFBName, String kFBEmail, String kFBProfileImage) {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        intent.putExtra("NAME", kFBName);
        intent.putExtra("EMAIL", kFBEmail);
        intent.putExtra("IMAGE", kFBProfileImage);
        startActivity(intent);
    }

    private void signInWithFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("email");
        mLoginFacebook.setReadPermissions(permissionNeeds);
        mLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest iRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Profile profile = Profile.getCurrentProfile();
                                String mFBProfileImage = profile.getProfilePictureUri(200, 200).toString();
                                Log.i(TAG, mFBProfileImage);
                                try {
                                    mFBName = object.getString("name");
                                    mFBEmail = object.getString("email");

                                    Log.i(TAG, mFBName + ", " + mFBEmail);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                updateUI(mFBName, mFBEmail, mFBProfileImage);
                            }
                        });

                Bundle iParameters = new Bundle();
                iParameters.putString("fields",
                        "name,email");
                iRequest.setParameters(iParameters);
                iRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null)
//            updateUI(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // gmail login
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> kCompletedTask) {
        try {
            GoogleSignInAccount iAccount = kCompletedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(iAccount);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);

        }
    }

    private void updateUI(GoogleSignInAccount kAccount) {
        Intent iIntent = new Intent(MainActivity.this, UserProfileActivity.class);
        iIntent.putExtra("GOOGLE_ACCOUNT", kAccount);
        Log.i("TAG", kAccount.getEmail());
        startActivity(iIntent);
    }

    /*        public void getHashkey () {
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.sig&&natures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());

                        Log.i("Base64", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("Name not found", e.getMessage(), e);

                } catch (NoSuchAlgorithmException e) {
                    Log.d("Error", e.getMessage(), e);
                }
        }*/
}

package com.tsingshare.tsingshare_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mFirstnameView;
    private EditText mLastnameView;
    private EditText mEmailView;
    private EditText mTelephoneView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mRegisterFormView;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle = this.getIntent().getExtras(); // 新Activity接收外界传入的数据
        String name = bundle.getString("name");
        int age = bundle.getInt("age");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mFirstnameView = (EditText) findViewById(R.id.firstname);
        mLastnameView = (EditText) findViewById(R.id.lastname);
        mEmailView = (EditText) findViewById(R.id.email);
        mTelephoneView = (EditText) findViewById(R.id.telephone);
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.username_sign_up_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        Button mShowLoginButton = (Button) findViewById(R.id.show_login_button);
        mShowLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "redirect to login", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "张三");
                bundle.putInt("age", 23);
                intent.putExtras(bundle);//附带上额外的数据
                startActivity(intent); ;
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstnameView.setError(null);
        mLastnameView.setError(null);
        mEmailView.setError(null);
        mTelephoneView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String firstname = mFirstnameView.getText().toString();
        String lastname = mLastnameView.getText().toString();
        String email = mEmailView.getText().toString();
        String telephone = mTelephoneView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        } else if (!isUsernameValid(firstname)) {
            mFirstnameView.setError(getString(R.string.error_invalid_username));
            focusView = mFirstnameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(lastname)) {
            mLastnameView.setError(getString(R.string.error_field_required));
            focusView = mLastnameView;
            cancel = true;
        } else if (!isUsernameValid(lastname)) {
            mLastnameView.setError(getString(R.string.error_invalid_username));
            focusView = mLastnameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(telephone)) {
            mTelephoneView.setError(getString(R.string.error_field_required));
            focusView = mTelephoneView;
            cancel = true;
        } else if (!isUsernameValid(telephone)) {
            mTelephoneView.setError(getString(R.string.error_invalid_username));
            focusView = mTelephoneView;
            cancel = true;
        }
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(firstname, lastname, email, telephone, username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFirstname;
        private final String mLastname;
        private final String mEmail;
        private final String mTelephone;
        private final String mUsername;
        private final String mPassword;

        UserRegisterTask(String firstname, String lastname, String email, String telephone, String username, String password) {
            mFirstname = firstname;
            mLastname = lastname;
            mEmail = email;
            mTelephone = telephone;
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                //Thread.sleep(2000);
                HttpClient client = new DefaultHttpClient();
                String url=getString(R.string.api_url)+"/auth/signup";
                Log.i("url", url);
                HttpPost post = new HttpPost(url);
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("firstName", mFirstname));
                pairs.add(new BasicNameValuePair("lastName", mLastname));
                pairs.add(new BasicNameValuePair("email", mEmail));
                pairs.add(new BasicNameValuePair("telephone", mTelephone));
                pairs.add(new BasicNameValuePair("username",mUsername));
                pairs.add(new BasicNameValuePair("password",mPassword));
                post.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse response = client.execute(post);

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                String line = null;
                String responseString = "";

                while ((line = rd.readLine()) != null) {

                    responseString += line;

                }
                Log.i("response", responseString);

                JSONTokener jsonParser = new JSONTokener(responseString);
                // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
                // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）
                JSONObject responseJson = (JSONObject) jsonParser.nextValue();
                // 接下来的就是JSON对象的操作了
                if(responseJson.has("_id")) {
                    String userid = responseJson.getString("_id");
                    String username = responseJson.getString("username");
                    String displayName = responseJson.getString("displayName");
                    String headimg = getString(R.string.api_url)+"/"+responseJson.getString("headimg");

                    Log.i("userid", userid);
                    Log.i("username", username);

                    // Save login status
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("userid", userid);
                    editor.putString("displayName", displayName);
                    editor.putString("username", username);
                    editor.putString("headimg", headimg);
                    editor.commit();

                    return true;
                }
                else {
                    String message = responseJson.getString("message");
                    Log.i("message", message);
                    return false;
                }

                /*
                // 使用GET方法发送请求,需要把参数加在URL后面，用?连接，参数之间用&分隔
            String url = getString(R.string.api_url)+"/auth/signin" + "?username=" + mUsername + "&password=" + mPassword;

            // 生成请求对象
            HttpGet httpGet = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();

            // 发送请求
            try
            {

                HttpResponse response = httpClient.execute(httpGet);

                // 显示响应
                showResponseResult(response);// 一个私有方法，将响应结果显示出来

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
                */
            } catch (Exception e) {
                return false;
            }

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUsername)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            // TODO: register the new account here.
            //return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(getApplicationContext(), "register success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "张三");
                bundle.putInt("age", 23);
                intent.putExtras(bundle);//附带上额外的数据
                startActivity(intent); ;
                finish();
            } else {
                Log.i("Username", mUsername);
                Log.i("Password", mPassword);
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}




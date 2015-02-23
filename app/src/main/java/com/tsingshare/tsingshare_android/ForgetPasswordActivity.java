package com.tsingshare.tsingshare_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
public class ForgetPasswordActivity extends Activity implements LoaderCallbacks<Cursor> {

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
    private UserForgetPasswordTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private View mProgressView;
    private View mForgetPasswordFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle = this.getIntent().getExtras(); // 新Activity接收外界传入的数据
        String name = bundle.getString("name");
        int age = bundle.getInt("age");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.forget_password || id == EditorInfo.IME_NULL) {
                    attemptForgetPassword();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.forget_password_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptForgetPassword();
            }
        });

        Button mShowRegisterButton = (Button) findViewById(R.id.show_register_button);
        mShowRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "redirect to register", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ForgetPasswordActivity.this, RegisterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "张三");
                bundle.putInt("age", 23);
                intent.putExtras(bundle);//附带上额外的数据
                startActivity(intent);
                ;
            }
        });

        Button mShowLoginButton = (Button) findViewById(R.id.show_login_button);
        mShowLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "redirect to login", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "张三");
                bundle.putInt("age", 23);
                intent.putExtras(bundle);//附带上额外的数据
                startActivity(intent); ;
            }
        });

        mForgetPasswordFormView = findViewById(R.id.forget_password_form);
        mProgressView = findViewById(R.id.forget_password_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptForgetPassword() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
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
            mAuthTask = new UserForgetPasswordTask(username);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return true;
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

            mForgetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mForgetPasswordFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mForgetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mForgetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
    public class UserForgetPasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;

        UserForgetPasswordTask(String username) {
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                //Thread.sleep(2000);
                HttpClient client = new DefaultHttpClient();
                String url=getString(R.string.api_url)+"/auth/forgot";
                Log.i("url", url);
                HttpPost post = new HttpPost(url);
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("username",mUsername));
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
                if(responseJson.has("_id")) { // TODO the condition maybe wrong
                    String userid = responseJson.getString("_id");
                    String username = responseJson.getString("username");

                    Log.i("userid", userid);
                    Log.i("username", username);

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
                Toast.makeText(getApplicationContext(), "restore password success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "张三");
                bundle.putInt("age", 23);
                intent.putExtras(bundle);//附带上额外的数据
                startActivity(intent); ;
                finish();
            } else {
                Log.i("Username", mUsername);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}




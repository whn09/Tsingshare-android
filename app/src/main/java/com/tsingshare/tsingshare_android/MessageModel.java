package com.tsingshare.tsingshare_android;

import android.content.Context;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whn13 on 15/2/24.
 */
public class MessageModel extends Activity {

    public MessageModel() {
    }

    public List<String> getMessageList(String userid) {
        try
        {
            // 使用GET方法发送请求,需要把参数加在URL后面，用?连接，参数之间用&分隔
            String url = "http://192.168.1.106:3000"+"/imessages" + "?userid=" + userid;
            Log.i("URL=", url);
            // 生成请求对象
            HttpGet httpGet = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();

            // 发送请求

            HttpResponse response = httpClient.execute(httpGet);

            // 显示响应
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


        }
        catch (Exception e)
        {
            Log.e("Get Error", "response error");
            e.printStackTrace();
        }
        return null;
    }
}

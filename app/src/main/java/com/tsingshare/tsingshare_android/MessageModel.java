package com.tsingshare.tsingshare_android;

import android.content.Context;
import android.app.Activity;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whn13 on 15/2/24.
 */
public class MessageModel extends Activity {

    private GetMessageCountTask mGetMessageCountTask = null;
    private GetMessageListTask mGetMessageListTask = null;
    Integer currentPage, totalPage, endPage, pageSize;
    List<String> messageList;

    public MessageModel(Context context, String userid) {
        currentPage = 1;
        totalPage = 1;
        endPage = 1;
        pageSize = 10;
        messageList = new ArrayList<String>();
        mGetMessageCountTask = new GetMessageCountTask(context, userid);
        mGetMessageCountTask.execute((Void) null);
    }

    public List<String> getMessageList(Context context, String userid) {
        if(currentPage == 0) {
            return null;
        }
        mGetMessageListTask = new GetMessageListTask(context, userid);
        mGetMessageListTask.execute((Void) null);
        return messageList;
    }

    public class GetMessageCountTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserid;
        private final Context mContext;

        GetMessageCountTask(Context context, String userid) {
            mUserid = userid;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try
            {
                // 使用GET方法发送请求,需要把参数加在URL后面，用?连接，参数之间用&分隔
                String url = mContext.getString(R.string.api_url)+"/imessages/count" + "?userid=" + mUserid;
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
                Integer totalCount = Integer.parseInt(responseString);
                if(totalCount % pageSize == 0) {
                    totalPage = totalCount / pageSize;
                }
                else {
                    totalPage = totalCount / pageSize + 1;
                }
                currentPage = totalPage;
                endPage = totalPage;
                return true;
            }
            catch (Exception e)
            {
                Log.e("Get Error", "response error");
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetMessageCountTask = null;

            if (success) {
                Toast.makeText(mContext, "count success", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Log.i("Userid", mUserid);
            }
        }

        @Override
        protected void onCancelled() {
            mGetMessageCountTask = null;
        }
    }

    public class GetMessageListTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserid;
        private final Context mContext;

        GetMessageListTask(Context context, String userid) {
            mUserid = userid;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try
            {
                // 使用GET方法发送请求,需要把参数加在URL后面，用?连接，参数之间用&分隔
                String url = mContext.getString(R.string.api_url)+"/imessages" + "?userid=" + mUserid + "&page="+currentPage+"&pagesize="+pageSize;
                currentPage--;
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
                JSONArray responseJson = (JSONArray) jsonParser.nextValue();
                // 接下来的就是JSON对象的操作了
                List<String> tmpList = new ArrayList<String>();
                for(Integer i=0;i<responseJson.length();i++) {
                    //Log.i("getJSONObject", responseJson.getJSONObject(i).getString("content"));
                    tmpList.add(responseJson.getJSONObject(i).getString("content"));
                }
                tmpList.addAll(messageList);
                messageList = tmpList;
                Log.i("messageList", messageList.toString());
                return true;
            }
            catch (Exception e)
            {
                Log.e("Get Error", "response error");
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetMessageListTask = null;

            if (success) {
                Toast.makeText(mContext, "message list success", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Log.i("Userid", mUserid);
            }
        }

        @Override
        protected void onCancelled() {
            mGetMessageListTask = null;
        }
    }
}

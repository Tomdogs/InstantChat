package com.rance.chatui.util;

import android.util.Log;

import com.rance.chatui.enity.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Tomdog on 2017/5/30.
 */

public class JsonParse {


    public String connection(){


        URL url;
        HttpURLConnection connection;
        String jsonString = null;

        try {

            //请求指令
            String path="http://c.3g.163.com/nc/article/headline/T1348647853363/0-140.html";
            url=new URL(path);

            //创建和服务端的连接
            connection =(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            if(connection.getResponseCode()==200){
                InputStream is=connection.getInputStream();       //以输入流的形式返回
                //将输入流转换成字符串
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                byte [] buffers=new byte[1024];
                int len=0;
                while((len=is.read(buffers))!=-1){
                    baos.write(buffers, 0, len);
                }
                jsonString=baos.toString();

                baos.close();
                is.close();
            }

            Log.i("java发出去的请求",jsonString);

            //异常处理
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;

    }


    public ArrayList<News> parse(String jsonData){

        ArrayList<News> newsList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(jsonData);
            //得到key
            Iterator<String> iteratorKeys = jsonObject.keys();
            while (iteratorKeys.hasNext()){
                String keys =iteratorKeys.next();
                Logger.i("得到的key:"+keys);
                JSONArray jsonArray = jsonObject.getJSONArray(keys);

                for(int i = 1;i<jsonArray.length()-1;i++){
                    /**
                     * url_3w
                     * ltitle
                     * digest
                     * imgsrc
                     * ptime
                     */
                    News news = new News();
                    JSONObject jsob = jsonArray.getJSONObject(i);
                    //System.out.println(jsob);
                    news.setLtitle(jsob.getString("title"));
                    news.setUrl3w(jsob.getString("url"));
                    news.setDigest(jsob.getString("digest"));
                    news.setImgsrc(jsob.getString("imgsrc"));
                    news.setPtime(jsob.getString("ptime"));
                    news.setSource(jsob.getString("source"));
                    newsList.add(news);
                }
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newsList;
    }
}

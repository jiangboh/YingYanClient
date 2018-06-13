package com.jiangboh.bti.yingyanclient.BaiduTrare;

import android.content.Context;

import com.baidu.trace.Trace;
import com.jiangboh.bti.yingyanclient.PublicUnit.MyFunction;
import com.jiangboh.bti.yingyanclient.PublicUnit.StaticParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 2018-6-5.
 */

public class GetWeather {
    private Trace mTrace;
    private Context context;
    private String city;

    public GetWeather(Context context,Trace mTrace)
    {
        this.context = context;
        this.mTrace = mTrace;
    }

    public void getWeatherInfo(double longitude,double latitude)
    {
        sendRequestWithHttpURLConnection(longitude, latitude);
    }

    /*public boolean getCity(double longitude,double latitude) {
        //通过经纬度获取位置
        Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        StringBuilder sb = new StringBuilder();
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            sb.append(address.getAddressLine(0)).append("\n");
            //获取天气时需要的地区
            city= address.getLocality().toString().replace("市", "");
            //当前位置
            String addressStr = sb.toString();

            MyFunction.MyPrint(d("地址信息：", "城市：" + city + "地址：" + addressStr);
            sendRequestWithHttpURLConnection();
        }

        return true;
    }*/


    private void sendRequestWithHttpURLConnection(final double longitude, final double latitude){
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(getWeatherUrl(longitude, latitude));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    showResponse(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showResponse(final String response){
        MyFunction.MyPrint(response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            String errnum = jsonObject.getString("error");
            String errMsg = jsonObject.getString("status");
            if (errnum.equals("0") && errMsg.equals("success")) {
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                JSONObject resultsInfo = resultsArray.getJSONObject(0);
                String cityName = resultsInfo.getString("currentCity");

                JSONArray weatherArray = resultsInfo.getJSONArray("weather_data");
                JSONObject weatherInfo = weatherArray.getJSONObject(0);
                String weatherCode = weatherInfo.getString("temperature");
                String weather = weatherInfo.getString("weather");
                String wind = weatherInfo.getString("wind");
                String date = weatherInfo.getString("date");
                String curr = date.substring(date.indexOf("(")+1,date.indexOf(")"));

                StaticParam.NotificationText =  cityName + ":"  + weather + "(" + weatherCode + ") " + curr;
                MyBaiduTrare.isGetWeather = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ;
    }

    // 获取接口地址
    private static String getWeatherUrl(double longitude,double latitude) {
        String url = null;
        url = "http://api.map.baidu.com/telematics/v3/weather?location=" +
                longitude + ","+ latitude +
                "&&output=json&ak=D0565f1172f038a2a3e4478310197bf8" +
                "&mcode=58:85:32:B0:63:84:9E:6F:1A:6B:B9:1E:72:43:08:EB:E5:60:7B:79;com.jiangbo.Mmscall";
        return url;
    }

}

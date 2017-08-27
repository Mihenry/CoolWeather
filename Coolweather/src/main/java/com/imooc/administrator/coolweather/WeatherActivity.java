package com.imooc.administrator.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.imooc.administrator.coolweather.gson.Forecast;
import com.imooc.administrator.coolweather.gson.Weather;
import com.imooc.administrator.coolweather.service.AutoUpdateWeatherService;
import com.imooc.administrator.coolweather.util.HttpUtil;
import com.imooc.administrator.coolweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/25.
 */

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weather_layout;
    private TextView title_city,title_update_time;
    private TextView degree_text,weather_info_text;
    private LinearLayout forecast_layout;
    /**以下三个是forecast_item.xml布局下的，的等到forecast_item.xml转换成view时，再利用view来找到这三个控件*/
    private TextView date_text,info_text,max_text,min_text;
    private TextView aqi_text,pm25_text;
    private TextView comfort_text,car_wash_text,sport_text;
    private ImageView bing_pic_img;
    private SharedPreferences prefs;
    public SwipeRefreshLayout swipe_refresh;
    private String mWeatherId;
    private Button bt_nav;
    public DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 实现背景图与状态栏的融合，改进UI的美观性!这个功能仅在Android5.0以上的系统支持
         */
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            /**
             * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN和View.SYSTEM_UI_FLAG_LAYOUT_STABLE：
             * 表示活动的布局会显示在状态栏上面
             */
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            /**将状态栏设置成透明色*/
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        init();
        /**将从服务器请求得到的天气数据存到SharedPreferences文件中*/
        prefs= PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString=prefs.getString("weather",null);
        /**若得到的数据不为空，则无需请求服务器，直接将json格式的字符串数据解析成weather对象，给控件赋值即可*/
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            mWeatherId=getIntent().getStringExtra("weatherId");
            requestWeather(mWeatherId);
            weather_layout.setVisibility(View.INVISIBLE);
        }
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        loadBackgroundPicture();
    }


    public void requestWeather(String weatherId) {
        String weatherAddress="http://guolin.tech/api/weather?cityid="
                +weatherId+"&key=3e591e7a8ae745cdacfa5ec8346190ad";
        HttpUtil.sendOkHttpRequest(weatherAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"连接失败！",Toast.LENGTH_SHORT).show();
                        /**表示刷新事件结束，并隐藏刷新进度条*/
                        swipe_refresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().toString();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&&weather.status.equals("ok")){
                            SharedPreferences.Editor editor=PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            /**
                             * 存进SharedPreferences文件的是JSON格式的字符串数据
                             */
                            editor.putString("weather",responseText);
                            editor.apply();

                            /**
                             * 以上操作，是将服务器返回的数据存到SharedPreferences文件中，现在，我们
                             * 再次读取SharedPreferences文件，并显示在界面中
                             */
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败！",Toast.LENGTH_SHORT).show();
                        }
                        /**表示刷新事件结束，并隐藏刷新进度条*/
                        swipe_refresh.setRefreshing(false);
                    }
                });
            }
        });

        /**
         * 加载背景图片
         */
        loadBackgroundPicture();

        /**
         * 点击导航按钮，显示滑动侧边栏
         */
        bt_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void init() {
        weather_layout= (ScrollView) findViewById(R.id.weather_layout);
        title_city= (TextView) findViewById(R.id.title_city);
        title_update_time= (TextView) findViewById(R.id.title_update_time);
        degree_text= (TextView) findViewById(R.id.degree_text);
        weather_info_text= (TextView) findViewById(R.id.weather_info_text);
        forecast_layout= (LinearLayout) findViewById(R.id.forecast_layout);
        aqi_text= (TextView) findViewById(R.id.aqi_text);
        pm25_text= (TextView) findViewById(R.id.pm25_text);
        comfort_text= (TextView) findViewById(R.id.comfort_text);
        car_wash_text= (TextView) findViewById(R.id.car_wash_text);
        sport_text= (TextView) findViewById(R.id.sport_text);
        bing_pic_img= (ImageView) findViewById(R.id.bing_pic_img);
        swipe_refresh= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        bt_nav= (Button) findViewById(R.id.bt_nav);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawerLayout);
    }

    /**
     *
     * 给各个控件赋值
     */
    private void showWeatherInfo(Weather weather) {
        String cityName=weather.basic.cityName;
        /** 时间的格式为2016-08-08 21:58，中间有一个空格，截断这个空格，即变成了一个字符串数组*/
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String temperature=weather.now.temperature;
        String weather_info=weather.now.more.info;

        title_city.setText(cityName);
        title_update_time.setText(updateTime);
        degree_text.setText(temperature);
        weather_info_text.setText(weather_info);

        forecast_layout.removeAllViews();
        List<Forecast> forecastList=weather.forecastList;
        for (Forecast forecast:forecastList){
            String date=forecast.date;
            String weather_info_forecast=forecast.more.info;
            String temperature_max=forecast.temperature.max;
            String temperature_min=forecast.temperature.min;

            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecast_layout,false);
            date_text= (TextView) view.findViewById(R.id.date_text);
            info_text= (TextView) view.findViewById(R.id.info_text);
            max_text= (TextView) view.findViewById(R.id.max_text);
            min_text= (TextView) view.findViewById(R.id.min_text);

            date_text.setText(date);
            info_text.setText(weather_info_forecast);
            max_text.setText(temperature_max);
            min_text.setText(temperature_min);
            forecast_layout.addView(view);
        }

        String aqi=weather.aqi.city.aqi;
        String pm25=weather.aqi.city.pm25;
        aqi_text.setText(aqi);
        pm25_text.setText(pm25);

        String comfort=weather.suggestion.comfort.info;
        String car_wash=weather.suggestion.carWash.info;
        String sport=weather.suggestion.sport.info;

        comfort_text.setText(comfort);
        car_wash_text.setText(car_wash);
        sport_text.setText(sport);

        weather_layout.setVisibility(View.VISIBLE);

        Intent intent=new Intent(this, AutoUpdateWeatherService.class);
        startService(intent);

    }

    /**
     * 访问必应服务器，赶回必应每日更新的图片的网址，然后利用Glide家在这个网址，将其设置为背景图片
     */
    private void loadBackgroundPicture() {
        /**图片路径*/
        String bingPic=prefs.getString("bing_pic",null);
        /**若已经存在图片路径*/
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bing_pic_img);
        }else {
            requestBingPic();
        }
    }

    /**
     * 访问返回必应图片的服务器接口
     */
    private void requestBingPic() {
        String requestBingPicAddress="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPicAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"连接失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String address=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",null);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(address).into(bing_pic_img);
                    }
                });
            }
        });
    }
}

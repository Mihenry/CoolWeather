package com.imooc.administrator.coolweather.service;
/**
 * 后台自动更新天气信息和背景图片
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.imooc.administrator.coolweather.gson.Weather;
import com.imooc.administrator.coolweather.util.HttpUtil;
import com.imooc.administrator.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateWeatherService extends Service {
    public AutoUpdateWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 服务启动后调用
     */
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        updateWeather();
        updateBackground();
        /**
         * 创建定时任务，实现每隔一段时间，启动该服务一次，从而实现后台更新
         */
        AlarmManager mAlarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        /**每8小时更新一次*/
        int time_interval=8*60*60*1000;
        /**
         * SystemClock.elapsedRealtime() 从系统开机至今所经历时间的毫秒数
         */
        long triggerAtMillis= SystemClock.elapsedRealtime()+time_interval;
        Intent intent1=new Intent(this,AutoUpdateWeatherService.class);
        PendingIntent pi=PendingIntent.getService(this,0,intent1,0);
        /**
         *     针对同一个PendingIntent，AlarmManager.set()函数不能设置多个alarm。调用该函数时，假如已经有
         * old alarm使用相同的PendingIntent，会先取消（cancel）old alarm，然后再设置新的alarm。
         *     取消alarm使用AlarmManager.cancel()函数，传入参数是个PendingIntent实例。该函数会将所有跟这
         * 个PendingIntent相同的Alarm全部取消，怎么判断两者是否相同，android使用的是intent.filterEquals()，
         * 具体就是判断两个PendingIntent的action、data、type、class和category是否完全相同。
         */
        mAlarmManager.cancel(pi);
        /**
         * 取消以后再设置定时器
         */
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtMillis,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 向服务器请求最新的天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        /**若已有缓存数据，将weatherId取出来，去它去请求最新的天气信息*/
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherAddress="http://guolin.tech/api/weather?cityid="
                    +weatherId+"&key=3e591e7a8ae745cdacfa5ec8346190ad";
            HttpUtil.sendOkHttpRequest(weatherAddress, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather mWeather=Utility.handleWeatherResponse(responseText);
                    if (mWeather!=null&&mWeather.status.equals("ok")){
                        /**
                         * 将最新的天气信息缓存到SharedPreferences文件中。
                         * 问题：问什么不直接显示？
                         * 原因：
                         *     因为WeatherActivity中加载天气信息，都是先去SharedPreferences文件中读取缓
                         * 存。
                         */
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateWeatherService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 向服务器请求图片路径
     * 因为加载图片时也是先访问缓存，所以我们只要将访问到的最新图片路径放到缓存中即可
     */
    private void updateBackground() {
        String requestBingPicAddress="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPicAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String pictureAddress=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateWeatherService.this).edit();
                editor.putString("bing_pic",pictureAddress);
                editor.apply();
            }
        });
    }
}

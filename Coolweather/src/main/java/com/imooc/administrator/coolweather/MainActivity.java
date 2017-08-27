package com.imooc.administrator.coolweather;
/**
 * 和风天气API key:3e591e7a8ae745cdacfa5ec8346190ad
 */

/**
 * 一、创建数据库和表
 * 1、新建省、市、县类，并继承DataSupport
 * 2、配置litepal.xml，完成映射
 * 3、在注册文件中，配置LitePalApplication
 */
/**
 * 二、创建两个通用类
 * 1、向服务器请求数据的类：HttpUtil
 * 2、处理和解析服务器返回的数据类：Utility
 */
/**
 * 三、遍历全国省市县
 *     由于这个功能在后面还会使用，因此就不写在活动里了，而是写在fragment中
 */

/**
 * 四、显示天气信息
 * （一）定义GSON实体类
 *     由于和风天气返回的JSON数据结构非常复杂，如果还使用JSONObject来解析就会很麻烦，所以这里借助GSON来对
 * 天气信息进行解析
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         *     若SharedPreferences文件中已经存在天气数据，则说明已访问过服务器，就无需让用户再次选择城市，
         * 直接跳转到WeatherActivity即可。
         */
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString!=null){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

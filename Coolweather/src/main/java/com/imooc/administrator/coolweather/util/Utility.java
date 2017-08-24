package com.imooc.administrator.coolweather.util;

import android.text.TextUtils;

import com.imooc.administrator.coolweather.db.City;
import com.imooc.administrator.coolweather.db.County;
import com.imooc.administrator.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/8/23.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
            try {
                if (!TextUtils.isEmpty(response)){
                    /**将返回的字符串转换成JSON数组*/
                    JSONArray allProvinces=new JSONArray(response);
                    for (int i=0;i<allProvinces.length();i++){
                        /**从JSON数组中根据下标取出JSON对象*/
                        JSONObject provinceObject=allProvinces.getJSONObject(i);
                        Province province=new Province();
                        /**服务器端的省级名称字段为“name”*/
                        province.setProvinceName(provinceObject.getString("name"));
                        /**服务器端的省级编码字段为“name”*/
                        province.setProvinceCode(provinceObject.getInt("id"));
                        province.save();
                    }
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        try {
            if (!TextUtils.isEmpty(response)){
                JSONArray allCities=new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
            }
            return true;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        try {
            if (!TextUtils.isEmpty(response)){
                JSONArray allCounties=new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getInt("weather_id"));
                    county.setCityId(cityId);
                }
            }
            return true;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }
}

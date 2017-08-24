package com.imooc.administrator.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 *     LitePal对表进行CRUD（增、删、改、查）操作时，必须继承DataSupport，因为LitePal中所有的CRUD操作都是
 * 由DataSupport类提供的
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;//城市编码
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

package com.imooc.administrator.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 *     LitePal对表进行CRUD（增、删、改、查）操作时，必须继承DataSupport，因为LitePal中所有的CRUD操作都是
 * 由DataSupport类提供的
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private int weatherId;//天气预报Id
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

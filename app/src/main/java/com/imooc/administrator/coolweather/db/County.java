package com.imooc.administrator.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 *     LitePal对表进行CRUD（增、删、改、查）操作时，必须继承DataSupport，因为LitePal中所有的CRUD操作都是
 * 由DataSupport类提供的
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private int countyCode;//县编码

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

    public int getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(int countyCode) {
        this.countyCode = countyCode;
    }
}

package com.imooc.administrator.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 *     LitePal对表进行CRUD（增、删、改、查）操作时，必须继承DataSupport，因为LitePal中所有的CRUD操作都是
 * 由DataSupport类提供的
 */

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceCode;//省份编码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

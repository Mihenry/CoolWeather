package com.imooc.administrator.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imooc.administrator.coolweather.db.City;
import com.imooc.administrator.coolweather.db.County;
import com.imooc.administrator.coolweather.db.Province;
import com.imooc.administrator.coolweather.util.HttpUtil;
import com.imooc.administrator.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 第一步：初始化各个控件
 * 第二步：为ListView设置适配器
 * 第三步：实现选择省、市、县，即点击ListView中的条目到下一级目录
 */

public class ChooseAreaFragment extends Fragment {
    /**
     * 第一步
     */
    private TextView title_text;
    private Button bt_back;
    private ListView listView;

    /**
     * 第二步
     */
    private ArrayAdapter<String> adapter;//主要用于显示省市县的名称，所以是String类型
    private List<String> dataList;//数据源
    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//县列表

    /**
     * 第三步
     */

    public static final int LEVEL_PROVINCE=1;
    public static final int LEVEL_CITY=2;
    public static final int LEVEL_COUNTY=1;
    private int currentLevel;//当前选中的级别
    private Province selectedProvince;//当前选中的省份
    private City selectedCity;//当前选中的市
    private County selectedCounty;//当前选中的县
    private ProgressDialog mProgressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        title_text= (TextView) view.findViewById(R.id.title_text);
        bt_back= (Button) view.findViewById(R.id.bt_back);
        listView= (ListView) view.findViewById(R.id.list_view);
        /**
         * 初始化数据源
         */
        dataList=new ArrayList<>();
        provinceList=new ArrayList<>();
        cityList=new ArrayList<>();
        countyList=new ArrayList<>();

        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * 点击listView中条目，若当前为省级，则跳转到市级目录；若点击的条目为市级，则跳转到县级目录
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        /**
         * 点击返回按钮，若当前等级为县级，则跳转到市级目录；若点击的条目为市级，则跳转到省级目录
         */
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        /**
         * 默认加载省级数据
         */
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        title_text.setText("中国");
        /**若为省级，即为最上层，不能再后退，所以，可以将后退按钮设置没有*/
        bt_back.setVisibility(View.GONE);
        /**litepal数据库查询数据方法：findAll()，找到数据库中所有的Province数据，返回一个集合*/
        provinceList=DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            /**
             * dataList一直在省、市、县之间切换，所以每次dataList更新之前都要清空一次，防止在末尾添加
             */
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            /**
             * 当数据库中有数据时，数据源赋值，通知ListView数据源改变
             */
            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_PROVINCE;
        }else {
            /**若在数据库中查询不到数据，则访问服务器，请求数据*/
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    /**
     * 查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        /**查询所选择的省下面的各个市，必须有省的id作为查询条件*/
        title_text.setText(selectedProvince.getProvinceName());
        bt_back.setVisibility(View.VISIBLE);//显示后退按钮
        cityList=DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getProvinceCode()))
                .find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_CITY;
        }else {
            /**若在数据库中查询不到数据，则访问服务器，请求数据*/
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }

    }
    /**
     * 查询全国所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        title_text.setText(selectedCity.getCityName());
        bt_back.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityId=?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }
    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        showProgress();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"加载失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().toString();
                boolean result=false;
                if (type.equals("province")){
                    /**调用Utility.handleProvinceResponse()方法，将从服务器请求的数据存储到数据库中*/
                    result=Utility.handleProvinceResponse(responseText);
                }else if (type.equals("city")){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if (type.equals("county")){
                    result=Utility.handleCountyResponse(responseText,selectedProvince.getId());
                }
                if (result){
                    /**
                     *     处理返回的请求数据，是在子线程中操作的，而我们接下来的操作设计到控件值的改变，所以，
                     * 需要回到主线程
                     */
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            if (type.equals("province")){
                                /**
                                 *     以上只是将从服务器查询到的数据保存到数据库中，如果要在ListView中显示，
                                 * 还需要再次将数据从数据库中读取出来。
                                 */
                               queryProvinces();
                            }else if (type.equals("city")){
                                queryCities();
                            }else if (type.equals("county")){
                                queryCounties();
                            }
                        }
                    });
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            Toast.makeText(getContext(),"加载失败！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示正在加载对话框
     */
    private void showProgress() {
        if (mProgressDialog==null){
            mProgressDialog=new ProgressDialog(getContext());
//            mProgressDialog=new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载。。。。");
            mProgressDialog.show();
        }
    }

    /**
     * 关闭加载对话框
     */
    private void closeProgress() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }
}

package com.aliyun.alivcsolution;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aliyun.alivcsolution.adapter.HomeViewPagerAdapter;
import com.aliyun.alivcsolution.adapter.MultilayerGridAdapter;
import com.aliyun.alivcsolution.model.ScenesModel;
import com.aliyun.svideo.base.ui.SdkVersionActivity;
import com.aliyun.svideo.common.utils.FastClickUtil;
import com.aliyun.svideo.common.utils.PermissionUtils;
import com.aliyun.svideo.snap.SnapCropSetting;
import com.aliyun.svideo.snap.SnapRecorderSetting;
import com.aliyun.vodplayerview.activity.AliyunPlayerSettingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mulberry
 */
public class MainActivity extends AppCompatActivity {

    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /**
     * 判断是编辑模块进入还是通过社区模块的编辑功能进入
     */
    private static final String INTENT_PARAM_KEY_ENTRANCE = "entrance";
    /**
     * 判断是编辑模块进入还是通过社区模块的编辑功能进入
     * svideo: 短视频
     * community: 社区
     */
    private static final String INTENT_PARAM_KEY_VALUE = "svideo";

    /**
     * 小圆点指示器
     */
    private ViewGroup points;
    /**
     * 小圆点图片集合
     */
    private ImageView[] ivPoints;
    private ViewPager viewPager;
    /**
     * 当前页数
     */
    private int currentPage;
    /**
     * 总的页数
     */
    private int totalPage;
    /**
     * 每页显示的最大数量
     */
    private int mPageSize = 6;
    /**
     * 总的数据源
     */
    private List<ScenesModel> listDatas;
    /**
     * GridView作为一个View对象添加到ViewPager集合中
     */
    private List<View> viewPagerList;
    /**
     * module数据，短视频模块, 包含视频拍摄和导入裁剪
     */
    private int[] modules = {R.string.solution_recorder, R.string.solution_crop, R.string.solution_edit, R.string.solution_player};
    private int[] homeicon = {R.mipmap.icon_home_svideo_record, R.mipmap.icon_home_svideo_crop, R.mipmap.icon_home_svideo_edit, R.mipmap.icon_home_player};

    private static final int PERMISSION_REQUEST_CODE = 1000;
    /**
     * 录制activity
     */
    private static final String ACTIVITY_NAME_RECORD = "com.aliyun.svideo.snap.SnapRecorderSetting";
    /**
     * 裁剪activity
     */
    private static final String ACTIVITY_NAME_SNAP = "com.aliyun.svideo.snap.SnapCropSetting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_main);
        boolean checkResult = PermissionUtils.checkPermissionsGroup(this, PermissionUtils.PERMISSION_CAMERA);
        if (!checkResult) {
            PermissionUtils.requestPermissions(this, PermissionUtils.PERMISSION_CAMERA, PERMISSION_REQUEST_CODE);
        }
        iniViews();

        setDatas();

        buildHomeItem();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了
                //Toast.makeText(this, "get All Permisison", Toast.LENGTH_SHORT).show();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                showPermissionDialog();
            }
        }
    }

    //系统授权设置的弹框
    AlertDialog openAppDetDialog = null;

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.app_name) + "需要访问 \"相册\"、\"摄像头\" 和 \"外部存储器\",否则会影响绝大部分功能使用, 请到 \"应用信息 -> 权限\" 中设置！");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("暂不设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create();
        }
        if (null != openAppDetDialog && !openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }
    }

    private void iniViews() {
        viewPager = (ViewPager) findViewById(R.id.home_viewPager);
        points = (ViewGroup) findViewById(R.id.points);

        ImageView ivVersion = findViewById(R.id.iv_version);
        ivVersion.setImageResource(R.mipmap.alivc_svideo_icon_version);
        ivVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FastClickUtil.isFastClickActivity(SdkVersionActivity.class.getSimpleName())) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SdkVersionActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setDatas() {
        listDatas = new ArrayList<>();
        for (int i = 0; i < modules.length; i++) {
            listDatas.add(new ScenesModel(getResources().getString(modules[i]), homeicon[i]));
        }
    }

    private void buildHomeItem() {
        LayoutInflater inflater = LayoutInflater.from(this);
        totalPage = (int) Math.ceil(listDatas.size() * 1.0 / mPageSize);
        viewPagerList = new ArrayList<>();


        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            GridView gridView = (GridView) inflater.inflate(R.layout.alivc_home_girdview, viewPager, false);
            gridView.setAdapter(new MultilayerGridAdapter(this, listDatas, i, mPageSize));
            //添加item点击监听
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Intent intent = new Intent(MainActivity.this, DemoActivity.class);
                    //startActivity(intent);
                    //
                    //int pos = position + currentPage * mPageSize;
                    //Log.i("TAG","position的值为："+position + "-->pos的值为："+pos);
                    //Toast.makeText(MainActivity.this,"你点击了 "+listDatas.get(pos).getName(),Toast.LENGTH_SHORT).show();

                    switch (position) {
                        case 0:

                            if (!FastClickUtil.isFastClickActivity(ACTIVITY_NAME_RECORD)) {
                                // 视频拍摄
                                Intent recorder = new Intent();
                                recorder.setClass(MainActivity.this, SnapRecorderSetting.class);
                                startActivity(recorder);
                            }
                            break;

                        case 1:
                            if (!FastClickUtil.isFastClickActivity(ACTIVITY_NAME_SNAP)) {
                                // 视频裁剪
                                Intent crop = new Intent();
                                crop.setClass(MainActivity.this, SnapCropSetting.class);
                                startActivity(crop);
                            }
                            break;
                        case 2:
                            if (!FastClickUtil.isFastClickActivity(ACTIVITY_NAME_SNAP)) {
                                // 视频编辑
                                Intent edit = new Intent(MainActivity.this, AlivcEditorSettingActivity.class);
                                //判断是编辑模块进入还是通过社区模块的编辑功能进入
                                //svideo: 短视频
                                //community: 社区
                                edit.putExtra(INTENT_PARAM_KEY_ENTRANCE, INTENT_PARAM_KEY_VALUE);
                                startActivity(edit);
                                break;
                            }

                        case 3:
                            boolean checkResult = com.aliyun.alivcsolution.utils.PermissionUtils.checkPermissionsGroup(MainActivity.this, permission);
                            if (!checkResult) {
                                com.aliyun.alivcsolution.utils.PermissionUtils.requestPermissions(MainActivity.this, permission, PERMISSION_REQUEST_CODE);
                            } else {
                                // 视频播放
                                Intent playerIntent = new Intent(MainActivity.this, AliyunPlayerSettingActivity.class);
                                startActivity(playerIntent);
                            }


                            break;
                        default:
                            break;
                    }
                }
            });
            //每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }

        //设置ViewPager适配器
        viewPager.setAdapter(new HomeViewPagerAdapter(viewPagerList));

        //小圆点指示器
        if (totalPage > 1) {
            ivPoints = new ImageView[totalPage];
            for (int i = 0; i < ivPoints.length; i++) {
                ImageView imageView = new ImageView(this);
                //设置图片的宽高
                imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
                if (i == 0) {
                    imageView.setBackgroundResource(R.mipmap.page_selected_indicator);
                } else {
                    imageView.setBackgroundResource(R.mipmap.page_normal_indicator);
                }
                ivPoints[i] = imageView;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = (int) getResources().getDimension(R.dimen.app_home_points_item_margin);//设置点点点view的左边距
                layoutParams.rightMargin = (int) getResources().getDimension(R.dimen.app_home_points_item_margin);
                ;//设置点点点view的右边距
                points.addView(imageView, layoutParams);
            }
            points.setVisibility(View.VISIBLE);
        } else {
            points.setVisibility(View.GONE);
        }


        //设置ViewPager滑动监听
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //改变小圆圈指示器的切换效果
                setImageBackground(position);
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < ivPoints.length; i++) {
            if (i == selectItems) {
                ivPoints[i].setBackgroundResource(R.mipmap.page_selected_indicator);
            } else {
                ivPoints[i].setBackgroundResource(R.mipmap.page_normal_indicator);
            }
        }
    }
}

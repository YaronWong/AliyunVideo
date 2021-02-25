package com.aliyun.vodplayerview.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.aliyunplayerbase.bean.AliyunVideoList;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * URL播放方式的Fragment
 */
public class AliyunPlayerTypeUrlFragment extends BaseFragment {

    private static final int REQ_CODE_PERMISSION = 0x1111;

    private EditText mUrlEditText;
    private String mUrlPath;
    private ImageView mQrCodeImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_url_player_type, container, false);
        mUrlEditText = view.findViewById(R.id.et_url);
        mQrCodeImageView = view.findViewById(R.id.iv_qrcode);
        initData();
        initListener();
        if(TextUtils.isEmpty(GlobalPlayerConfig.mUrlPath)){
            defaultPlayInfo();
        }
        return view;
    }

    private void getPlayUrlInfo(){
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayUrlInfo(new GetAuthInformation.OnGetUrlInfoListener() {
            @Override
            public void onGetUrlError(String msg) {
                if(getContext() != null){
                    ToastUtils.show(getContext(),msg);
                }
            }

            @Override
            public void onGetUrlSuccess(AliyunVideoList.VideoList dataBean) {
                if(dataBean != null){
                    List<AliyunVideoList.VideoList.VideoListItem> playInfoList = dataBean.getPlayInfoList();
                    if(playInfoList != null && playInfoList.size() > 0){
                        AliyunVideoList.VideoList.VideoListItem videoListItem = playInfoList.get(0);
                        mUrlEditText.setText(videoListItem.getPlayURL());
                        mUrlPath = videoListItem.getPlayURL();
                    }
                }
            }
        });
    }

    private void initData(){
        mUrlEditText.setText(GlobalPlayerConfig.mUrlPath);
    }

    private void initListener(){
        mQrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AliyunPlayerTypeUrlFragment.this.getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(AliyunPlayerTypeUrlFragment.this.getActivity(),
                            new String[] {Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                }
            }
        });
    }

    @Override
    public void defaultPlayInfo() {
        getPlayUrlInfo();
    }

    @Override
    public void confirmPlayInfo() {
        mUrlPath = mUrlEditText.getText().toString();
        GlobalPlayerConfig.mUrlPath = mUrlPath;
        GlobalPlayerConfig.URL_TYPE_CHECKED = true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                } else {
                    // User disagree the permission
                    ToastUtils.show(this.getActivity(), getString(R.string.alivc_player_agree_camera_permission));
                }
            }
            break;
            default:
                break;
        }
    }


}

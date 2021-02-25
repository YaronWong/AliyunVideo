package com.aliyun.vodplayerview.fragment;

import android.Manifest;
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
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;

/**
 * LiveSts播放方式的Fragment
 */
public class AliyunPlayerTypeLiveStsFragment extends BaseFragment {

    private static final int REQ_CODE_PERMISSION = 0x1112;

    /**
     * STS相关信息
     */
    private EditText  mLiveStsUrlEditText,mStsRegionEditText,mStsAccessKeyIdEditText,
            mStsSecurityTokenEditText,mStsAccessKeySecretEditText,mStsDomainEditText,mStsAppEditText,mStsStreamEditText;

    private String mUrl,mRegion,mAccessKeyId,mSecurityToken,mAccessKeySecret,mLiveStsDomain,mLiveStsApp,mLiveStream;
    /**
     * 刷新
     */
    private TextView mRefreshTextView;
    private ImageView mQrCodeImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_live_sts_player_type, container, false);
        mLiveStsUrlEditText = view.findViewById(R.id.et_live_sts_url);
        mQrCodeImageView = view.findViewById(R.id.iv_qrcode);
        mRefreshTextView = view.findViewById(R.id.tv_refresh);
        mStsRegionEditText = view.findViewById(R.id.et_live_sts_region);
        mStsDomainEditText = view.findViewById(R.id.et_live_sts_domain);
        mStsAppEditText = view.findViewById(R.id.et_live_sts_app);
        mStsStreamEditText = view.findViewById(R.id.et_live_sts_stream);
        mStsAccessKeyIdEditText = view.findViewById(R.id.et_live_sts_access_key_id);
        mStsSecurityTokenEditText = view.findViewById(R.id.et_live_sts_security_token);
        mStsAccessKeySecretEditText = view.findViewById(R.id.et_live_sts_access_key_secret);
        initData();
        initListener();
        defaultPlayInfo();

        return view;
    }

    private void getVideoPlayStsInfo(){
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayLiveStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
            @Override
            public void onGetStsError(String errorMsg) {
                ToastUtils.show(getContext(),errorMsg);
            }

            @Override
            public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                if(dataBean != null){
                    mStsRegionEditText.setText(GlobalPlayerConfig.mRegion);
                    mStsAccessKeyIdEditText.setText(dataBean.getAccessKeyId());
                    mStsSecurityTokenEditText.setText(dataBean.getSecurityToken());
                    mStsAccessKeySecretEditText.setText(dataBean.getAccessKeySecret());
                    GlobalPlayerConfig.mLiveExpiration = dataBean.getExpiration();
                }
            }
        });
    }

    private void initData(){
        mStsDomainEditText.setText(GlobalPlayerConfig.mLiveStsDomain);
        mStsAppEditText.setText(GlobalPlayerConfig.mLiveStsApp);
        mStsStreamEditText.setText(GlobalPlayerConfig.mLiveStsStream);
        mLiveStsUrlEditText.setText(GlobalPlayerConfig.mLiveStsUrl);
        mStsRegionEditText.setText(GlobalPlayerConfig.mRegion);
        mStsAccessKeyIdEditText.setText(GlobalPlayerConfig.mLiveStsAccessKeyId);
        mStsSecurityTokenEditText.setText(GlobalPlayerConfig.mLiveStsSecurityToken);
        mStsAccessKeySecretEditText.setText(GlobalPlayerConfig.mLiveStsAccessKeySecret);
    }

    private void initListener(){
        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUrl = mLiveStsUrlEditText.getText().toString();
                if(TextUtils.isEmpty(mUrl)){
                    ToastUtils.show(getContext(),R.string.alivc_refresh_vid_empty);
                    return ;
                }
                getVideoPlayStsInfo();
            }
        });

        mQrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AliyunPlayerTypeLiveStsFragment.this.getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(AliyunPlayerTypeLiveStsFragment.this.getActivity(),
                            new String[] {Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                }
            }
        });
    }


    private void getInputContent(){
        mUrl = mLiveStsUrlEditText.getText().toString();
        mRegion = mStsRegionEditText.getText().toString();
        mAccessKeyId = mStsAccessKeyIdEditText.getText().toString();
        mSecurityToken = mStsSecurityTokenEditText.getText().toString();
        mAccessKeySecret = mStsAccessKeySecretEditText.getText().toString();
        mLiveStsDomain = mStsDomainEditText.getText().toString();
        mLiveStsApp = mStsAppEditText.getText().toString();
        mLiveStream = mStsStreamEditText.getText().toString();
    }

    private void setGlobaConfig(){
        getInputContent();

        GlobalPlayerConfig.mLiveStsUrl = mUrl;
        GlobalPlayerConfig.mRegion = mRegion;
        GlobalPlayerConfig.mLiveStsAccessKeyId = mAccessKeyId;
        GlobalPlayerConfig.mLiveStsSecurityToken = mSecurityToken;
        GlobalPlayerConfig.mLiveStsAccessKeySecret = mAccessKeySecret;
        GlobalPlayerConfig.mLiveStsDomain = mLiveStsDomain;
        GlobalPlayerConfig.mLiveStsApp = mLiveStsApp;
        GlobalPlayerConfig.mLiveStsStream = mLiveStream;
    }

    @Override
    public void defaultPlayInfo() {
        getVideoPlayStsInfo();
    }

    @Override
    public void confirmPlayInfo() {
        setGlobaConfig();
        GlobalPlayerConfig.LIVE_STS_TYPE_CHECKED = true;
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

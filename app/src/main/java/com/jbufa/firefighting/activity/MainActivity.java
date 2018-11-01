
package com.jbufa.firefighting.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.RadioGroup;

import com.jbufa.firefighting.Event.PushMessage;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.fragment.MessageFragment;
import com.jbufa.firefighting.fragment.ManageFragment;
import com.jbufa.firefighting.fragment.MainPagerFragment;
import com.jbufa.firefighting.fragment.MemberFragment;
import com.jbufa.firefighting.ui.quickadapter.QuickAdapter;
import com.kongzue.dialog.v2.MessageDialog;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String CURR_INDEX = "currIndex";
    private static int currIndex = 0;

    private RadioGroup group;

    private ArrayList<String> fragmentTags;
    private FragmentManager fragmentManager;
    //    private SwipeFlingView swipeFlingView;
    private QuickAdapter<PushMessage> mAdapter;
    //    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initData(savedInstanceState);
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURR_INDEX, currIndex);
    }

    private void initData(Bundle savedInstanceState) {
        fragmentTags = new ArrayList<>(Arrays.asList("HomeFragment", "ImFragment", "InterestFragment", "MemberFragment"));
        currIndex = 0;
        if (savedInstanceState != null) {
            currIndex = savedInstanceState.getInt(CURR_INDEX);
            hideSavedFragment();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            AndPermission.with(this)
                    .runtime()
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> permissions) {
                            Log.e("ceshi", "读写权限申请成功");
                            new PgyUpdateManager.Builder()
                                    .setForced(false)                //设置是否强制更新
                                    .setUserCanRetry(true)         //失败后是否提示重新下载
                                    .setDeleteHistroyApk(true)     // 检查更新前是否删除本地历史 Apk， 默认为true
                                    .setUpdateManagerListener(new UpdateManagerListener() {
                                        @Override
                                        public void onNoUpdateAvailable() {
                                            //没有更新是回调此方法
                                            Log.e("ceshi", "已经是最新的版本了");
                                        }

                                        @Override
                                        public void onUpdateAvailable(final AppBean appBean) {
                                            //有更新回调此方法
                                            Log.e("ceshi", "需要更新"
                                                    + "新版本号是： " + appBean.getVersionCode());
                                            //调用以下方法，DownloadFileListener 才有效；
                                            //如果完全使用自己的下载方法，不需要设置DownloadFileListenerMessageDialog
                                            MessageDialog.show(MainActivity.this, "新版本", "检测到有新版本:" + appBean.getVersionName() + ",是否下载安装？", "下载", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                                                }
                                            });
                                        }

                                        @Override
                                        public void checkUpdateFailed(Exception e) {
                                            //更新检测失败回调
                                            Log.e("ceshi", "检查失败", e);
                                        }
                                    })
                                    .register();
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(@NonNull List<String> permissions) {
                            Log.e("ceshi", "申请权失败");
                        }
                    })
                    .start();

        }
    }

    private void hideSavedFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment != null) {
            fragmentManager.beginTransaction().hide(fragment).commit();
        }
    }


    private void initView() {
        group = findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.foot_bar_home:
                        currIndex = 0;
                        break;
                    case R.id.foot_bar_im:
                        currIndex = 1;
                        break;
                    case R.id.foot_bar_interest:
                        currIndex = 2;
                        break;
                    case R.id.main_footbar_user:
                        currIndex = 3;
                        break;
                    default:
                        break;
                }
                showFragment();
            }
        });
        showFragment();
    }

    private void showFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment == null) {
            fragment = instantFragment(currIndex);
        }
        for (int i = 0; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if (f != null && f.isAdded()) {
                fragmentTransaction.hide(f);
            }
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                return new MainPagerFragment();
            case 1:
                return new ManageFragment();
            case 2:
                return new MessageFragment();
            case 3:
                return new MemberFragment();
            default:
                return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        fragment.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.getString("result") != null) {
            }
        }
    }
}

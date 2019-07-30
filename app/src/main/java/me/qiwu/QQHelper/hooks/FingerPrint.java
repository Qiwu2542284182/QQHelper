package me.qiwu.QQHelper.hooks;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.ImageUtil;
import me.qiwu.QQHelper.utils.PasswordUil;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/9/4.
 */

public class FingerPrint extends ReflectHelper {
    private SettingUtils settingUtils=new SettingUtils();
    private Class<?>mQWalletPluginProxyActivity;
    private Button okButton;
    private EditText editText;
    private ArrayList<View>views=new ArrayList<>();
    public static final String ICON_FINGER_PRINT_ALIPAY_BASE64 = "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";
    public FingerPrint(ClassLoader classLoader){
        mQWalletPluginProxyActivity= XposedHelpers.findClass(QWalletPluginProxyActivity,classLoader);
    }

    public void init(){
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_STARTFINGERPRINT)){
            XposedHelpers.findAndHookMethod(mQWalletPluginProxyActivity, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    final Activity activity=(Activity)param.thisObject;
                    final String passwrod=settingUtils.getString(SettingUtils.SETTING_KEY_FINGERPSW);
                    if (passwrod.equals("")){
                        Toast.makeText(activity.getApplicationContext(),"未保存支付密码，使用密码支付",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(showFingerDialog(activity)){
                        FingerprintManagerCompat manager=FingerprintManagerCompat.from(activity);
                        manager.authenticate(null, 0, new CancellationSignal(), new FingerprintManagerCompat.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int i, CharSequence charSequence) {
                                super.onAuthenticationError(i, charSequence);
                                if (i==7){
                                    Toast.makeText(activity.getApplicationContext(),"尝试次数过多，请使用密码支付",Toast.LENGTH_SHORT).show();
                                    showNormalDialog(views);
                                }
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(activity.getApplicationContext(),"指纹错误",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationHelp(int i, CharSequence charSequence) {
                                super.onAuthenticationHelp(i, charSequence);
                            }

                            @Override
                            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult authenticationResult) {
                                super.onAuthenticationSucceeded(authenticationResult);
                                Toast.makeText(activity.getApplicationContext(),"指纹验证成功",Toast.LENGTH_SHORT).show();
                                try {
                                    editText.setText(PasswordUil.Decrypt(passwrod));
                                } catch (Exception e) {
                                    XposedBridge.log(e);
                                }
                                if (okButton!=null){
                                    okButton.performClick();
                                }
                            }
                        }, null);
                    };
                }
            });
        }
    }

    public boolean showFingerDialog(Activity activity){
        boolean isPayActivity=false;
        ViewGroup viewGroup=(ViewGroup) activity.findViewById(android.R.id.content);
        if (viewGroup!=null){
            ViewGroup rootView = (ViewGroup) viewGroup.getChildAt(0);
            if (rootView!=null&&rootView.getChildCount()==2){
                views.clear();
                getChildViews(rootView,views);
                if (isFirst(views)){
                    for (int i=0;i<views.size();i++){
                        final View v=views.get(i);
                        if (v instanceof TextView &&((TextView)v).getText().equals("输入支付密码")){
                            ((TextView)v).setText("请验证指纹");
                        }
                        if (v instanceof EditText&&v.getVisibility()==View.VISIBLE){
                            editText=(EditText)v;
                            ((View)v.getParent().getParent()).setVisibility(View.GONE);
                        }
                        if (v.getClass().getName().endsWith(".MyKeyboardWindow")){
                            isPayActivity=true;
                            ViewGroup viewGroup1=(ViewGroup) v.getParent();

                            FrameLayout frameLayout=new FrameLayout(v.getContext());
                            viewGroup1.removeViewAt(viewGroup1.getChildCount()-1);
                            v.setPadding(0,DensityUtil.dip2px(v.getContext(),68),0,0);
                            frameLayout.addView(v);
                            frameLayout.setVisibility(View.INVISIBLE);

                            FrameLayout frameLayout1=new FrameLayout(v.getContext());
                            frameLayout1.addView(frameLayout);


                            Bitmap bitmap= ImageUtil.base64ToBitmap(ICON_FINGER_PRINT_ALIPAY_BASE64);
                            ImageView imageView=new ImageView(v.getContext());
                            imageView.setImageBitmap(bitmap);
                            FrameLayout.LayoutParams layoutParams1=new FrameLayout.LayoutParams(DensityUtil.dip2px(v.getContext(),50), DensityUtil.dip2px(v.getContext(),50));
                            layoutParams1.gravity= Gravity.CENTER;

                            frameLayout1.addView(imageView,layoutParams1);
                            viewGroup1.addView(frameLayout1);

                        }
                        if (v instanceof Button &&((Button)v).getText().toString().equals("完成")){
                            okButton=(Button)v;
                        }

                    }

                }

            }
        }
        return isPayActivity;
    }

    private void showNormalDialog(ArrayList<View> arrayList){
        for (int i=0;i<arrayList.size();i++) {
            final View v = arrayList.get(i);
            if (v instanceof TextView && ((TextView) v).getText().equals("请验证指纹")) {
                ((TextView) v).setText("输入支付密码");
            }else if (v instanceof EditText){
                if (((EditText) v).getHint()!=null&&((EditText) v).getHint().toString().equals("输入短信验证码")){

                }else {
                    ((View)v.getParent().getParent()).setVisibility(View.VISIBLE);
                }

            }else if (v.getClass().getName().endsWith(".MyKeyboardWindow")){
                v.setVisibility(View.VISIBLE);
                v.setPadding(0,0,0,0);
                FrameLayout frameLayout=(FrameLayout)v.getParent().getParent();
                frameLayout.getChildAt(0).setVisibility(View.VISIBLE);
                frameLayout.getChildAt(1).setVisibility(View.GONE);
            }else if (v instanceof ImageView){
                if (v.getTag()!=null&&v.getTag().equals("指纹图标")){
                    v.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean isFirst(List<View> list){
        for (int i=0;i<list.size();i++) {
            View v = list.get(i);
            if (v instanceof FrameLayout) {
                if (v.getTag()!=null&&v.getTag().toString().contains("指纹")){
                    return false;
                }
            }
        }
        return true;
    }

    private void getChildViews(ViewGroup parent, List<View> outList) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (child == null) {
                continue;
            }
            outList.add(child);
            if (child instanceof ViewGroup) {
                getChildViews((ViewGroup) child, outList);
            } else {
            }
        }
    }
}

package com.hemaapp.wcpc_driver.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemaapp.wcpc_driver.R;

import xtom.frame.XtomObject;

/**
 * Created by wangyuxia on 2017/9/21.
 * 自定义弹框
 */
public class ButtonDialog extends XtomObject {
    private Dialog mDialog;
    private ViewGroup mContent;
    private TextView mTextView;
    private TextView leftButton;
    private TextView rightButton;
    private OnButtonListener buttonListener;

    public ButtonDialog(Context context) {
        mDialog = new Dialog(context, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.button_dialog, null);
        mContent = (ViewGroup) view.findViewById(R.id.father);
        mTextView = (TextView) view.findViewById(R.id.textview);
        leftButton = (TextView) view.findViewById(R.id.left);
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (buttonListener != null)
                    buttonListener.onLeftButtonClick(ButtonDialog.this);
            }
        });
        rightButton = (TextView) view.findViewById(R.id.right);
        rightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (buttonListener != null)
                    buttonListener.onRightButtonClick(ButtonDialog.this);
            }
        });
        mDialog.setCancelable(false);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 给弹框添加自定义View
     *
     * @param v 自定义View
     */
    public void setView(View v) {
        mContent.removeAllViews();
        mContent.addView(v);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setText(int textID) {
        mTextView.setText(textID);
    }

    public void setLeftButtonText(String text) {
        leftButton.setText(text);
    }

    public void setLeftButtonText(int textID) {
        leftButton.setText(textID);
    }

    public void setRightButtonText(String text) {
        rightButton.setText(text);
    }

    public void setRightButtonText(int textID) {
        rightButton.setText(textID);
    }

    public void setRightButtonTextColor(int color) {
        rightButton.setTextColor(color);
    }

    public void show() {
        mDialog.show();
    }

    public void cancel() {
        mDialog.cancel();
    }

    public OnButtonListener getButtonListener() {
        return buttonListener;
    }

    public void setButtonListener(OnButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }

    public interface OnButtonListener {
        public void onLeftButtonClick(ButtonDialog dialog);

        public void onRightButtonClick(ButtonDialog dialog);
    }

}


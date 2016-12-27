package com.ruby.preview.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.ruby.preview.R;

/**
 * 不确定何时结束的进度 dialog
 * Created by WangWei on 2016/1/8.
 */
public class CycleProgressDialog extends Dialog {

    TextView mMessage;

    private CycleProgressDialog(Builder builder) {
        super(builder.context, R.style.ProgressDialog);
        setContentView(R.layout.dialog_indeterminate_progress);

        mMessage = (TextView) findViewById(R.id.dialog_indeterminate_message);

        setCancelable(builder.cancelable);
        setCanceledOnTouchOutside(builder.cancelOnTouchOutSide);
        setOnCancelListener(builder.onCancelClick);
        setOnDismissListener(builder.onDismiss);

        mMessage.setText(builder.message);
    }


    /**
     * {@code IndeterminateProgressDialog} builder static inner class.
     */
    public static final class Builder {
        private boolean cancelable;
        private boolean cancelOnTouchOutSide;
        private CharSequence message;
        private OnCancelListener onCancelClick;
        private OnDismissListener onDismiss;
        private final Context context;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * Sets the {@code cancelable} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code cancelable} to set
         * @return a reference to this Builder
         */
        public Builder cancelable(boolean val) {
            cancelable = val;
            return this;
        }

        /**
         * Sets the {@code cancelOnTouchOutSide} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code cancelOnTouchOutSide} to set
         * @return a reference to this Builder
         */
        public Builder cancelOnTouchOutSide(boolean val) {
            cancelOnTouchOutSide = val;
            return this;
        }

        /**
         * Sets the {@code message} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code message} to set
         * @return a reference to this Builder
         */
        public Builder message(CharSequence val) {
            message = val;
            return this;
        }

        /**
         * Sets the {@code onCancelClick} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code onCancelClick} to set
         * @return a reference to this Builder
         */
        public Builder onCancelClick(OnCancelListener val) {
            onCancelClick = val;
            return this;
        }

        /**
         * Sets the {@code onDismiss} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code onDismiss} to set
         * @return a reference to this Builder
         */
        public Builder onDissmiss(OnDismissListener val) {
            onDismiss = val;
            return this;
        }

        /**
         * Returns a {@code IndeterminateProgressDialog} built from the parameters previously set.
         *
         * @return a {@code IndeterminateProgressDialog} built with parameters of this {@code IndeterminateProgressDialog.Builder}
         */
        public CycleProgressDialog build() {return new CycleProgressDialog(this);}

        public CycleProgressDialog show() {
            final CycleProgressDialog dialog = build();
            dialog.show();

            return dialog;
        }
    }
}

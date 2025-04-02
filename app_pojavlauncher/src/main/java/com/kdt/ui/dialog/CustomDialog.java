package com.kdt.ui.dialog;

import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kdt.ui.dialog.DraggableDialog;

import net.kdt.pojavlaunch.R;

public class CustomDialog implements DraggableDialog.DialogInitializationListener {
    private final AlertDialog dialog;
    private final String[] items;
    private final OnItemClickListener itemClickListener;
    private FrameLayout customContainer;  // Add this line

    private CustomDialog(Context context, String title, String message, String scrollmessage,
                         View customView, String confirmButtonText, String cancelButtonText,
                         OnCancelListener cancelListener, OnConfirmListener confirmListener,
                         String button1Text, String button2Text, String button3Text, String button4Text,
                         OnButtonClickListener button1Listener, OnButtonClickListener button2Listener,
                         OnButtonClickListener button3Listener, OnButtonClickListener button4Listener,
                         String[] items, OnItemClickListener itemClickListener,
                         boolean cancelable, boolean draggable) {

        this.items = items;
        this.itemClickListener = itemClickListener;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom_layout, null);

        customContainer = view.findViewById(R.id.custom_view_container); // Initialize the customContainer

        if (title != null && !title.isEmpty()) {
            // titleTextView.setText(title);
            // titleTextView.setVisibility(View.VISIBLE);
        }

        if (message != null && !message.isEmpty()) {
            // messageTextView.setText(message);
            // messageTextView.setVisibility(View.VISIBLE);
        }

        if (scrollmessage != null && !scrollmessage.isEmpty()) {
            // scrollmessageTextView.setText(scrollmessage);
            // scrollmessageTextView.setVisibility(View.VISIBLE);
            // messageScrollView.setVisibility(View.VISIBLE);
        }

        if (customView != null && customContainer != null) {
            customContainer.addView(customView);
            customContainer.setVisibility(View.VISIBLE);
        }

        if (items != null && items.length > 0) {
            // listView.setVisibility(View.VISIBLE);
            // listScrollView.setVisibility(View.VISIBLE);
        }

        if (confirmButtonText != null) {
            Button confirmButton = view.findViewById(R.id.confirm_button); // Ensure confirmButton is initialized
            confirmButton.setText(confirmButtonText);
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setOnClickListener(v -> {
                boolean shouldDismiss = true;
                if (confirmListener != null) {
                    shouldDismiss = confirmListener.onConfirm(customView);
                }
                if (shouldDismiss) dialog.dismiss();
            });
        }

        builder.setView(view);
        dialog = builder.create();

        if (draggable) dialog.setOnShowListener(dialogInterface -> DraggableDialog.initDialog(this));

        if (!cancelable) dialog.setCancelable(false);

        if (button1Listener != null) {
            // button1.setVisibility(View.VISIBLE);
            if (button1Text != null) {
                // button1.setText(button1Text);
            }
            // button1.setOnClickListener(v -> {
            //     boolean shouldDismiss = button1Listener.onClick(customView);
            //     if (shouldDismiss) dialog.dismiss();
            // });
        }

        if (button2Listener != null) {
            // button2.setVisibility(View.VISIBLE);
            if (button2Text != null) {
                // button2.setText(button2Text);
            }
            // button2.setOnClickListener(v -> {
            //     boolean shouldDismiss = button2Listener.onClick(customView);
            //     if (shouldDismiss) dialog.dismiss();
            // });
        }

        if (button3Listener != null) {
            // button3.setVisibility(View.VISIBLE);
            if (button3Text != null) {
                // button3.setText(button3Text);
            }
            // button3.setOnClickListener(v -> {
            //     boolean shouldDismiss = button3Listener.onClick(customView);
            //     if (shouldDismiss) dialog.dismiss();
            // });
        }

        if (button4Listener != null) {
            // button4.setVisibility(View.VISIBLE);
            if (button4Text != null) {
                // button4.setText(button4Text);
            }
            // button4.setOnClickListener(v -> {
            //     boolean shouldDismiss = button4Listener.onClick(customView);
            //     if (shouldDismiss) dialog.dismiss();
            // });
        }

        if (cancelListener != null) {
            // cancelButton.setVisibility(View.VISIBLE);
            if (cancelButtonText != null) {
                // cancelButton.setText(cancelButtonText);
            }
            // cancelButton.setOnClickListener(v -> {
            //     boolean shouldDismiss = cancelListener.onCancel(customView);
            //     if (shouldDismiss) dialog.dismiss();
            // });
        }

        // confirmButton.setOnClickListener(v -> {
        //     boolean shouldDismiss = true;
        //     if (confirmListener != null) shouldDismiss = confirmListener.onConfirm(customView);
        //     if (shouldDismiss) dialog.dismiss();
        // });

        if (itemClickListener != null) {
            // ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);
            // listView.setAdapter(adapter);
            // listView.setOnItemClickListener((parent, view1, position, id) -> {
            //     String item = items[position];
            //     itemClickListener.onItemClick(item, position);
            //     dialog.dismiss();
            // });
        }
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public Window onInit() {
        return dialog.getWindow();
    }

    public interface OnButtonClickListener {
        boolean onClick(View view);
    }

    public interface OnConfirmListener {
        boolean onConfirm(View view);
    }

    public interface OnCancelListener {
        boolean onCancel(View view);
    }

    public interface OnItemClickListener {
        void onItemClick(String item, @Nullable Integer index);
    }

    public static class Builder {
        private final Context context;
        private String title;
        private String message;
        private String scrollmessage;
        private View customView;
        private String button1Text;
        private String button2Text;
        private String button3Text;
        private String button4Text;
        private String confirmButtonText;
        private String cancelButtonText;
        private OnButtonClickListener button1Listener;
        private OnButtonClickListener button2Listener;
        private OnButtonClickListener button3Listener;
        private OnButtonClickListener button4Listener;
        private OnCancelListener cancelListener;
        private OnConfirmListener confirmListener;
        private String[] items;
        private OnItemClickListener itemClickListener;
        private boolean cancelable = true;
        private boolean draggable = false;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setScrollMessage(String scrollmessage) {
            this.scrollmessage = scrollmessage;
            return this;
        }

        public Builder setCustomView(View customView) {
            this.customView = customView;
            return this;
        }

        public Builder setItems(String[] items, OnItemClickListener listener) {
            this.items = items;
            this.itemClickListener = listener;
            return this;
        }

        public Builder setButton1Listener(String buttonText, OnButtonClickListener listener) {
            this.button1Text = buttonText;
            this.button1Listener = listener;
            return this;
        }

        public Builder setButton2Listener(String buttonText, OnButtonClickListener listener) {
            this.button2Text = buttonText;
            this.button2Listener = listener;
            return this;
        }

        public Builder setButton3Listener(String buttonText, OnButtonClickListener listener) {
            this.button3Text = buttonText;
            this.button3Listener = listener;
            return this;
        }

        public Builder setButton4Listener(String buttonText, OnButtonClickListener listener) {
            this.button4Text = buttonText;
            this.button4Listener = listener;
            return this;
        }

        public Builder setConfirmListener(int confirmButtonTextResId, OnConfirmListener confirmListener) {
            this.confirmButtonText = context.getString(confirmButtonTextResId);
            this.confirmListener = confirmListener;
            return this;
        }

        public Builder setCancelListener(int cancelButtonTextResId, OnCancelListener cancelListener) {
            this.cancelButtonText = context.getString(cancelButtonTextResId);
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setDraggable(boolean draggable) {
            this.draggable = draggable;
            return this;
        }

        public CustomDialog build() {
            return new CustomDialog(context, title, message, scrollmessage, customView,
                    confirmButtonText, cancelButtonText, cancelListener, confirmListener,
                    button1Text, button2Text, button3Text, button4Text,
                    button1Listener, button2Listener, button3Listener, button4Listener,
                    items, itemClickListener, cancelable, draggable);
        }
    }
}
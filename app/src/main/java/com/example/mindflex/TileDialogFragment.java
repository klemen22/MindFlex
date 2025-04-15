package com.example.mindflex;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TileDialogFragment extends DialogFragment {

    private static final String arg_title = "title";
    private static final String arg_image_resource = "image_resource";
    private static final String arg_backgroundColor = "backgroundColor";
    private static final String arg_description = "description";
    private static final String arg_activity_class = "activity_class";

    public static TileDialogFragment newInstance(String title, int image_resource, int backgroundColor, String description, Class<?> activity_class) {
        TileDialogFragment fragment = new TileDialogFragment();
        Bundle args = new Bundle();
        args.putString(arg_title, title);
        args.putInt(arg_image_resource, image_resource);
        args.putInt(arg_backgroundColor, backgroundColor);
        args.putString(arg_description, description);
        args.putString(arg_activity_class, activity_class.getName());
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_tile_dialog_fragment, container, false);

        TextView titleView = view.findViewById(R.id.dialog_title);
        ImageView imageView = view.findViewById(R.id.dialog_image);
        TextView descriptionView = view.findViewById(R.id.dialog_description);
        LinearLayout dialogLayout = view.findViewById(R.id.dialog_layout);

        Bundle args = getArguments();

        if(args != null) {
            titleView.setText(args.getString(arg_title));
            imageView.setImageResource(args.getInt(arg_image_resource));
            descriptionView.setText(args.getString(arg_description));
            String activityName = args.getString(arg_activity_class);

            // background color
            int bgColor = args.getInt(arg_backgroundColor);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(bgColor);

            float densitiy = getResources().getDisplayMetrics().density;
            float corner_radius = 14 * densitiy;

            drawable.setCornerRadius(corner_radius);
            dialogLayout.setBackground(drawable);

        }
        return view;
    }
}
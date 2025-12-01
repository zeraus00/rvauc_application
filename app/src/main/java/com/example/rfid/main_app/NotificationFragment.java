package com.example.rfid.main_app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rfid.R;
import com.example.rfid.features.notification.services.NotificationService;
import com.example.rfid.interfaces.HttpCallback;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView notifHeader;

    public NotificationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notifHeader = view.findViewById(R.id.textView32);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Fragment fragment = NotificationFragment.this;
        Activity activity = fragment.getActivity();

        if (activity == null) return;

        NotificationService.getNotifications(new HttpCallback<NotificationService.RecordResponse>() {
            @Override
            public void onSuccess(NotificationService.RecordResponse response) {
                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    View root = fragment.getView();
                    if (root == null) return;

                    LinearLayout tableContainer = root.findViewById(R.id.table_content_container);
                    if (tableContainer == null) return;

                    Context containerContext = tableContainer.getContext();

                    if (response.success) {
                        NotificationService.Record[] recordList = response.result;

                        int notifCount = recordList.length;
                        notifHeader.setText("You have " + notifCount + " Notification" + (notifCount == 1 ? "" : "s"));

                        for (NotificationService.Record record : recordList) {
                            LinearLayout linearLayout = getLinearLayout(containerContext);

                            String title = record.title;
                            String message = record.message;
                            boolean isRead = record.isRead;
                            String sentAt = record.sentAt;

                            Context context = linearLayout.getContext();
                            TextView messageView = getTextView(context, message, Color.BLACK);

                            linearLayout.addView(messageView);

                            tableContainer.addView(linearLayout);

                            View divider = new View(containerContext);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            divider.setLayoutParams(layoutParams);
                            divider.setBackgroundColor(Color.GRAY);

                            tableContainer.addView(divider);
                        }
                    }
                    else Toast.makeText(containerContext, "Fail loading data: " + response.message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                Fragment fragment = NotificationFragment.this;
                Activity activity = fragment.getActivity();
                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    Toast.makeText(fragment.getContext(), "Fail loading data: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    @NonNull
    private LinearLayout getLinearLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 8, 0, 8);

        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(16, 16, 16, 16);
        linearLayout.setBackgroundResource(R.drawable.rounded_white_bg);

//        int paddingTopDp = (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                12,
//                getResources().getDisplayMetrics()
//        );
//
//        linearLayout.setPadding(linearLayout.getPaddingLeft(), paddingTopDp, linearLayout.getPaddingRight(), linearLayout.getPaddingBottom());
        return linearLayout;
    }
    public TextView getTextView(Context context, String text, int color) {
        TextView textView = new TextView(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );

        textView.setLayoutParams(layoutParams);
        textView.setTextColor(color);
        textView.setText(text);
        textView.setTextSize(16);

        return textView;
    }
}
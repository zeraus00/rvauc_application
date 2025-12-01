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
import com.example.rfid.features.uniformcompliance.services.UniformComplianceService;
import com.example.rfid.interfaces.HttpCallback;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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
        return inflater.inflate(R.layout.fragment_status, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UniformComplianceService.viewRecords(new HttpCallback<UniformComplianceService.RecordResponse>() {
            @Override
            public void onSuccess(UniformComplianceService.RecordResponse response) {
                Fragment fragment = StatusFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    View root = fragment.getView();
                    if (root == null) return;

                    LinearLayout tableContainer = root.findViewById(R.id.table_content_container);
                    if (tableContainer == null) return;

                    if (response.success) {
                        var recordList = response.result;

                        for (UniformComplianceService.Record record : recordList) {
                            LinearLayout linearLayout = getLinearLayout(tableContainer.getContext());

                            var context = linearLayout.getContext();
                            var dateView = getTextView(context, record.date, Color.BLACK);
                            var day = record.day.substring(0, 3).toUpperCase();
                            var dayView = getTextView(context, day, Color.BLACK);
                            var timeView = getTextView(context, record.time, Color.BLACK);

                            var isCompliant = record.status.equals("compliant");
                            var statusColor = isCompliant ? Color.GREEN : Color.RED;
                            var statusView = getTextView(context, record.status, statusColor);

                            linearLayout.addView(dateView);
                            linearLayout.addView(dayView);
                            linearLayout.addView(timeView);
                            linearLayout.addView(statusView);

                            tableContainer.addView(linearLayout);

                            View divider = new View(tableContainer.getContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            divider.setLayoutParams(layoutParams);
                            divider.setBackgroundColor(Color.GRAY);

                            tableContainer.addView(divider);
                        }

                    }
                    else Toast.makeText(requireContext(), "Fail loading data: " + response.message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                Fragment fragment = StatusFragment.this;
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
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        int paddingTopDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12,
                getResources().getDisplayMetrics()
        );

        linearLayout.setPadding(linearLayout.getPaddingLeft(), paddingTopDp, linearLayout.getPaddingRight(), linearLayout.getPaddingBottom());
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

        return textView;
    }
}
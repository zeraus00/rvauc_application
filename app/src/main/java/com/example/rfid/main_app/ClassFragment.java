package com.example.rfid.main_app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;
import com.example.rfid.features.enrollments.schemas.classlist.ClassList;
import com.example.rfid.features.enrollments.schemas.classlist.ClassListElement;
import com.example.rfid.features.enrollments.schemas.scheduledclasseswithprofessor.ClassWithProfessor;
import com.example.rfid.features.enrollments.schemas.scheduledclasseswithprofessor.ClassesWithProfessor;
import com.example.rfid.features.enrollments.services.EnrollmentsService;
import com.example.rfid.interfaces.HttpCallback;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.Objects;

public class ClassFragment extends Fragment {

    LinearLayout classListContainer;

    public ClassFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        classListContainer = view.findViewById(R.id.class_list_container);

        EnrollmentsService.getClassList(new HttpCallback<EnrollmentsService.ClassListResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.ClassListResponse response) {
                Fragment fragment = ClassFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if (response.success) generateTableRows(response.result);
                    else Toast.makeText(requireContext(), "Fail loading data: " + response.message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                Fragment fragment = ClassFragment.this;
                Activity activity = fragment.getActivity();
                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    Toast.makeText(fragment.getContext(), "Fail loading data: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
        //loadSampleClasses();
        //generateTableRows();
    }

    private void showHeader(View view) {
        var activity = requireActivity();
        ConstraintLayout headerContainer = activity.findViewById(R.id.headerContainer);

        ConstraintLayout fragHeader = view.findViewById(R.id.classListFragmentHeader);
        fragHeader.removeView(fragHeader);
        var backBtn = view.findViewById(R.id.backBtn);
        var tvClassList = view.findViewById(R.id.tvClassList);
        var tvClassListDesc = view.findViewById(R.id.tvClassListDescription);

        ConstraintLayout layout = activity.findViewById(R.id.main);

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        set.connect(
                R.id.nextClass,
                ConstraintSet.TOP,
                R.id.headerContainer,
                ConstraintSet.BOTTOM
        );

        set.connect(
                R.id.nextClass,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
        );

        set.connect(
                R.id.nextClass,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
        );

        set.applyTo(layout);

        headerContainer.addView(backBtn);
        headerContainer.addView(tvClassList);
        headerContainer.addView(tvClassListDesc);
        headerContainer.addView(backBtn);
    }

    private void generateTableRows(ClassList classList) {
        classListContainer.removeAllViews();

        var context = getContext();

        for (ClassListElement e : classList.classes) {
            var cls = e.cls;
            var course = e.course;
            var offering = e.offering;
            var prof = e.professor;

            MaterialCardView cv = new MaterialCardView(context);

            cv.setId(cls.id);
            LinearLayout.LayoutParams cvParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cvParams.bottomMargin = (int) (12 * context.getResources().getDisplayMetrics().density);
            cv.setLayoutParams(cvParams);
            cv.setCardBackgroundColor(Color.parseColor("#bcc5db"));
            cv.setRadius((int) (12 * context.getResources().getDisplayMetrics().density));

            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams rlParams =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

            rl.setLayoutParams(rlParams);

            int paddingPx = (int) (16 * context.getResources().getDisplayMetrics().density);
            rl.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            rl.setBackgroundResource(R.drawable.gradient_background_2);

            ImageView ivIcon = new ImageView(context);
            ivIcon.setId(View.generateViewId());

            RelativeLayout.LayoutParams iconParams =
                    new RelativeLayout.LayoutParams(
                            (int) (20 * context.getResources().getDisplayMetrics().density),
                            (int) (20 * context.getResources().getDisplayMetrics().density)
                    );

            iconParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            iconParams.addRule(RelativeLayout.CENTER_VERTICAL);

            ivIcon.setLayoutParams(iconParams);
            ivIcon.setImageResource(R.drawable.more_than);
            ivIcon.setColorFilter(android.graphics.Color.WHITE);

            LinearLayout textSection = new LinearLayout(context);
            textSection.setId(View.generateViewId());
            textSection.setOrientation(LinearLayout.VERTICAL);

            RelativeLayout.LayoutParams textParams =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

            textParams.addRule(RelativeLayout.START_OF, ivIcon.getId());
            textParams.addRule(RelativeLayout.ALIGN_PARENT_START);

            textSection.setLayoutParams(textParams);

            textSection.setPadding(0, 0,
                    (int) (12 * context.getResources().getDisplayMetrics().density), 0);

            TextView tvClass = new TextView(context);
            var classNumber = "#" + cls.classNumber;
            var classCode = course.code;

            tvClass.setText(classNumber + " · " + classCode);
            tvClass.setTextColor(Color.parseColor("#cccccc"));
            tvClass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            TextView tvCourse = new TextView(context);

            tvCourse.setText(course.name);
            tvCourse.setTextColor(Color.WHITE);
            tvCourse.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tvCourse.setTypeface(tvCourse.getTypeface(), Typeface.BOLD);

            TextView tvScheduleInfo = new TextView(context);

            String scheduleInfo = "";

            if (offering != null) {
                scheduleInfo = offering.startTime + " - " + offering.endTime;

                String roomDetails = "N/A";

                var room = offering.room;
                if (room != null) {
                    roomDetails = room.name;
                    String building = room.building;
                    if (building != null) {
                        StringBuilder sb = new StringBuilder();

                        for (String word : building.trim().split("\\s+")) {
                            if (!word.isEmpty()) {
                                sb.append(word.charAt(0));
                            }
                        }

                        roomDetails = sb.toString() + " " + roomDetails;
                    }
                }

                scheduleInfo += " | " + roomDetails;
            }

            tvScheduleInfo.setText(scheduleInfo);
            tvScheduleInfo.setTextColor(Color.WHITE);
            tvScheduleInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

            textSection.addView(tvClass);
            textSection.addView(tvCourse);
            textSection.addView(tvScheduleInfo);

            rl.addView(textSection);
            rl.addView(ivIcon);

            cv.addView(rl);


            cv.setOnClickListener(v -> openClassAttendance(e));

            classListContainer.addView(cv);
        }
    }

    private void openClassAttendance(ClassListElement e) {
        Fragment attendanceFragment = new ClassAttendanceFragment();

        Bundle bundle = getBundle(e);

        attendanceFragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, attendanceFragment)
                .addToBackStack(null)
                .commit();
    }
    
    @NonNull
    private static Bundle getBundle(ClassListElement e) {
        var cls = e.cls;
        var course = e.course;
        var offering = e.offering;
        var prof = e.professor;

        var professor = "Prof. " + prof.surname;

        Bundle bundle = new Bundle();
        bundle.putString("professor", professor);
        bundle.putInt("classId", cls.id);
        bundle.putString("className", course.name);
        bundle.putString("classNumber", cls.classNumber);
        bundle.putString("classCode", course.code);
        bundle.putString("weekDay", offering == null ? "N/A" : offering.weekDay);
        bundle.putString("startTime", offering == null ? "N/A" : offering.startTime);
        bundle.putString("endTime", offering == null ? "N/A" : offering.endTime);
        return bundle;
    }

}

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
import com.example.rfid.features.enrollments.schemas.classruntime.ClassRuntime;
import com.example.rfid.features.enrollments.schemas.classruntime.SessionRuntime;
import com.example.rfid.features.enrollments.schemas.shared.Cls;
import com.example.rfid.features.enrollments.schemas.shared.Course;
import com.example.rfid.features.enrollments.schemas.shared.Professor;
import com.example.rfid.features.enrollments.schemas.shared.Room;
import com.example.rfid.features.enrollments.services.EnrollmentsService;
import com.example.rfid.interfaces.HttpCallback;
import com.google.android.material.card.MaterialCardView;

public class ClassFragment extends Fragment {

    LinearLayout classListContainer;
    TextView tvRuntimeStatus;
    TextView tvRuntimeClassNumberCourseCode;
    TextView tvRuntimeCourseName;
    TextView tvRuntimeProfessor;
    TextView tvRuntimeStartTime;
    TextView tvRuntimeRoom;

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

        setupViews(view);

        EnrollmentsService.getClassList(new HttpCallback<EnrollmentsService.ClassListResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.ClassListResponse response) {
                Fragment fragment = ClassFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if (response.success) generateTableRows(response.result);
                    else Toast.makeText(fragment.getContext(), "Fail loading data: " + response.message, Toast.LENGTH_SHORT).show();
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

        EnrollmentsService.getClassRuntime(new HttpCallback<EnrollmentsService.ClassRuntimeResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.ClassRuntimeResponse response) {
                Fragment fragment = ClassFragment.this;
                Activity activity = fragment.getActivity();
                if(activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if(response.success) loadRuntime(response.result);
                    else Toast.makeText(fragment.getContext(), "Fail loading data: " + response.message, Toast.LENGTH_SHORT).show();
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

    private void setupViews(View view) {
        classListContainer = view.findViewById(R.id.class_list_container);
        tvRuntimeStatus = view.findViewById(R.id.runtimeStatus);
        tvRuntimeClassNumberCourseCode = view.findViewById(R.id.runtimeClassNumberCourseCode);
        tvRuntimeCourseName = view.findViewById(R.id.runtimeCourseName);
        tvRuntimeProfessor = view.findViewById(R.id.runtimeProfessor);
        tvRuntimeStartTime = view.findViewById(R.id.runtimeStartTime);
        tvRuntimeRoom = view.findViewById(R.id.runtimeRoom);
    }

    private void loadRuntime(ClassRuntime runtime) {
        var cls = runtime.cls;
        var course = runtime.course;
        var room = runtime.offering.room;
        var professor = runtime.professor;

        tvRuntimeStatus.setText(getRuntimeDisplayStatus(runtime.session));

        tvRuntimeClassNumberCourseCode.setText(getClassNumberAndCourseCode(cls, course));
        tvRuntimeCourseName.setText(course.name);

        tvRuntimeProfessor.setText(getInstructorName(professor));

        tvRuntimeStartTime.setText(runtime.offering.startTime);
        tvRuntimeRoom.setText(getBuildingAndRoom(room));
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

            //  class number and course code
            TextView tvClass = new TextView(context);

            tvClass.setText(getClassNumberAndCourseCode(cls, course));
            tvClass.setTextColor(Color.parseColor("#cccccc"));
            tvClass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            //  course name
            TextView tvCourse = new TextView(context);

            tvCourse.setText(course.name);
            tvCourse.setTextColor(Color.WHITE);
            tvCourse.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tvCourse.setTypeface(tvCourse.getTypeface(), Typeface.BOLD);

            TextView tvScheduleInfo = new TextView(context);

            //  offering info
            String scheduleInfo = "";

            if (offering != null) {
                scheduleInfo = offering.startTime + " - " + offering.endTime;


                scheduleInfo += " | " + getBuildingAndRoom(offering.room);
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
        var enrollment = e.enrollment;


        Bundle bundle = new Bundle();
        bundle.putInt("classId", cls.id);
        bundle.putInt("enrollmentId", enrollment.id);
        bundle.putString("classNumberCourseCode", getClassNumberAndCourseCode(cls, course));
        bundle.putString("courseName", course.name);
        bundle.putString("professor", getInstructorName(e.professor));
        return bundle;
    }

    private static String getInstructorName(Professor professor) {
        return "Instructor " + professor.firstName + " " + professor.surname;
    }
    private static String getClassNumberAndCourseCode(Cls cls, Course course) {
        return  "#" + cls.classNumber + " · " + course.code;
    }
    private static String getBuildingAndRoom(Room room) {
        String roomDetails = "N/A";

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

                roomDetails = sb + " " + roomDetails;
            }
        }

        return roomDetails;
    }
    private static String getRuntimeDisplayStatus(SessionRuntime sessionRuntime) {
        var runtimeStatus = sessionRuntime.runtimeStatus;
        var finalStatus =  runtimeStatus != null ? runtimeStatus : sessionRuntime.status;

        return finalStatus.toUpperCase();
    }

}

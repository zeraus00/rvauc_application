package com.example.rfid.main_app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rfid.R;

public class AppealFragment extends Fragment {

    private Button btnCancel, btnSubmit, btnChooseMedicalFile, btnChooseParentIdFile, btnChooseExcuseLetterFile;
    private EditText descriptionInput;
    private TextView medicalFileName, parentIdFileName, excuseLetterFileName;

    private Uri medicalFileUri;
    private Uri parentIdFileUri;
    private Uri excuseLetterUri;


    private final ActivityResultLauncher<Intent> medicalFilePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> handleFilePickResult(result.getResultCode(),
                    result.getData(), "Medical", medicalFileName, uri -> medicalFileUri = uri)
    );
    private final ActivityResultLauncher<Intent> parentIdFilePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> handleFilePickResult(result.getResultCode(),
                    result.getData(), "Parent Id", parentIdFileName, uri -> parentIdFileUri = uri)
    );
    private final ActivityResultLauncher<Intent> excuseLetterFilePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> handleFilePickResult(result.getResultCode(),
                    result.getData(), "Excuse Letter", excuseLetterFileName, uri -> excuseLetterUri = uri)
    );

    private interface  UriSetter {
        void setUri(Uri uri);
    }

    private void handleFilePickResult(int resultCode,
                                      Intent data,
                                      String fileType,
                                      TextView fileNameTextView,
                                      UriSetter uriSetter) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri =data.getData();
            if (fileUri != null) {
                uriSetter.setUri(fileUri);
                String fileName = getFileName(fileUri);
                fileNameTextView.setText(fileName);

                fileNameTextView.setTextColor(getResources().getColor(R.color.blue, null));
                fileNameTextView.setOnClickListener(v -> viewFile(fileUri));
                Toast.makeText(getContext(), fileType + " File selected: " + fileName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public AppealFragment() {
    }

    public static AppealFragment newInstance(String param1, String param2) {
        AppealFragment fragment = new AppealFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appeal, container, false);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnCancel.setOnClickListener(v -> replaceFragment(new ClassAttendanceFragment()));

        btnChooseMedicalFile = view.findViewById(R.id.btnChooseMedicalFile);
        medicalFileName = view.findViewById(R.id.medicalFileName);

        btnChooseParentIdFile = view.findViewById(R.id.btnChooseParentIdFile);
        parentIdFileName = view.findViewById(R.id.parentIdFileName);

        btnChooseExcuseLetterFile = view.findViewById(R.id.btnChooseExcuseLetterFile);
        excuseLetterFileName = view.findViewById(R.id.excuseLetterFileName);

        btnSubmit.setOnClickListener(v -> {
            if (medicalFileUri == null || parentIdFileUri == null || excuseLetterUri == null) {
                StringBuilder error = new StringBuilder("Please upload the required documents: ");
                boolean missing = false;

                if (medicalFileUri == null) {
                    error.append("\n- Medical File");
                    missing = true;
                }
                if (parentIdFileUri == null) {
                    error.append("\n- Parent Id");
                    missing = true;
                }
                if (excuseLetterUri == null) {
                    error.append("\n Excuse Letter");
                    missing = true;
                }

                if (missing) {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
            submitAppeal(medicalFileUri, parentIdFileUri, excuseLetterUri);
        });

        btnChooseMedicalFile.setOnClickListener(v -> openFilePicker(medicalFilePicker));
        btnChooseParentIdFile.setOnClickListener(v -> openFilePicker(parentIdFilePicker));
        btnChooseExcuseLetterFile.setOnClickListener(v -> openFilePicker(excuseLetterFilePicker));

        return view;
    }

    // Helper method to open the file picker
    private void openFilePicker(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        launcher.launch(intent);
    }

    //Helper method to open the selected file
    private void viewFile(Uri fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, requireContext().getContentResolver().getType(fileUri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Cannot open file. Make sure you have a suitable app.", Toast.LENGTH_LONG).show();
        }
    }

    // Helper method to get the file name from its URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void submitAppeal(Uri medicalFileUri, Uri parentIdFileUri, Uri excuseLetterFileUri) {

        String medicalStatus = medicalFileUri != null ? getFileName(medicalFileUri) : "None";
        String parentIdStatus = parentIdFileUri != null ? getFileName(parentIdFileUri) : "None";
        String excuseLetterStatus = excuseLetterFileUri != null ? getFileName(excuseLetterFileUri) : "None";


        String submissionMessage = "Submitting Appeal:\n" +
                "Medical: " + medicalStatus +
                "\nParent ID: " + parentIdStatus +
                "\nExcuse Letter: " + excuseLetterStatus;

        Toast.makeText(getContext(), submissionMessage, Toast.LENGTH_LONG).show();

        // After successful submission:
        replaceFragment(new AppealSubmittedFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

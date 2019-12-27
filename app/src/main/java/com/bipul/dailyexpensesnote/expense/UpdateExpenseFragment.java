package com.bipul.dailyexpensesnote.expense;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bipul.dailyexpensesnote.database.ExpenseDatabaseHelper;
import com.bipul.dailyexpensesnote.DateValidate;
import com.bipul.dailyexpensesnote.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import io.blackbox_vision.datetimepickeredittext.view.TimePickerEditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateExpenseFragment extends Fragment {
    private Spinner typeSpinner;
    private EditText amountET;
    private DatePickerEditText dateET;
    private TimePickerEditText timeET;

    private Button updateExpenseBtn, updatedocumentBtn;
    private ImageView datePickBtn, timePickBtn, cancelUpdateBtn;
    private ImageView filesetIV;
    private LinearLayout updateCameraGalleryBtnfield, cameraBtn, galleryBtn, cencleBtn;
    private ExpenseDatabaseHelper helper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
    private String typeOfExpense;
    private Context context;


    private Bitmap bitmap = null;
    private String documentURL = "";

    private Calendar calendar;
    private int hour, minute;

    private int selectposition = 0;


    //get Bundle
    String rId = "";
    String rDate = "";
    String rTime = "";
    String rAmount = "";

    public UpdateExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_expense, container, false);
        context = container.getContext();

        Activity activity = new Activity();

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

      //  hideSoftKeyboard(activity);


        init(view);
        process(view);

        return view;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void init(View view) {
        typeSpinner = view.findViewById(R.id.updateActivityTypeSpinnerID);
        amountET = view.findViewById(R.id.updateExpenseAmountET);
        dateET = view.findViewById(R.id.updateExpenseDateET);
        timeET = view.findViewById(R.id.updateExpenseTimeET);
        updateExpenseBtn = view.findViewById(R.id.updateExpenseBtn);
        datePickBtn = view.findViewById(R.id.updateActivityDatePickerBtn);
        timePickBtn = view.findViewById(R.id.updateActiviTimePickerBtn);
        helper = new ExpenseDatabaseHelper(context);
        filesetIV = view.findViewById(R.id.updateFileIV);
        updateCameraGalleryBtnfield = view.findViewById(R.id.updateCameraGalleryBtnfield);
        cameraBtn = view.findViewById(R.id.updateCameraBtnID);
        galleryBtn = view.findViewById(R.id.updateGalleryBtnID);
        cencleBtn = view.findViewById(R.id.updateImageCencleBtnnID);
        updatedocumentBtn = view.findViewById(R.id.updateDocumentMethod);
        cancelUpdateBtn = view.findViewById(R.id.updateCancelBtn);

        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.INVISIBLE);
    }

    private void process(View view) {

        //update value..


//get value from bundle argument
        Bundle bundle = getArguments();
        if (bundle != null) {
            rId = bundle.getString("id");
            rDate = bundle.getString("date");
            rAmount = bundle.getString("amount");
            rTime = bundle.getString("time");
            typeOfExpense = bundle.getString("type");

        }


        dateET.setText(rDate);
        timeET.setText(rTime);
        amountET.setText(rAmount);


        //custom set
        //set Type into spinner
        final String[] typeExpense = {"Rent", "Food", "Utility bills", "Medicine", "Cloathing", "Transport", "Health", "Gift"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_activated_1, typeExpense);
        typeSpinner.setAdapter(arrayAdapter);


        //find position thats selected by user
        for (int i = 0; i < typeExpense.length; i++) {
            if (typeOfExpense.equals(typeExpense[i])) {
                selectposition = i;
            }
        }

        typeSpinner.setSelection(selectposition);                  //set spinner position thats request for update


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfExpense = typeExpense[position];
                //Toast.makeText(getApplicationContext(),typeOfExpense+" is selected",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //get date and time into dateET and Time ET

        datePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.custom_date_picker, null);

                Button done = view.findViewById(R.id.doneButton);
                final DatePicker datePicker = view.findViewById(R.id.datePickerDialogue);
                builder.setView(view);
                final Dialog dialog = builder.create();
                dialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        month = month + 1;
                        int year = datePicker.getYear();

                        String cDate = year + "/" + month + "/" + day;
                        Date d = null;
                        try {
                            d = dateFormat.parse(cDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String fdate = dateFormat.format(d);
                        dateET.setText(fdate);
                        dialog.dismiss();

                    }
                });

            }
        });


        //time picker listner
        final TimePickerDialog.OnTimeSetListener timePick = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                Time time = new Time(hour, minute, 0);
                calendar.setTime(time);
                String usertime = timeFormat.format(calendar.getTime());
                timeET.setText(usertime);
            }
        };


        //time set
        timePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, timePick, hour, minute, false);
                timePickerDialog.show();
            }
        });

        //call for update on database
//update data button
        updateExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uTime = timeET.getText().toString();
                String amountGet = amountET.getText().toString();
                String userDate = dateET.getText().toString();
                DateValidate dv = new DateValidate();
                dv.matcher = Pattern.compile(dv.DATE_PATTERN).matcher(userDate);           //check valid Date from ValidDate class
                if (amountGet.equals("") || userDate.equals("")) {
                    if (amountGet.equals("")) {
                        amountET.setError("please enter  amount first");
                        amountET.requestFocus();
                    } else if (userDate.equals("")) {
                        dateET.setError("please enter date first");
                        dateET.requestFocus();
                    }
                }

                //need check also valid date pattern
                else if (!dv.matcher.matches()) {
                    dateET.setError("Enter a valid Date format : yyyy/MM/dd");
                    dateET.requestFocus();
                   // Toast.makeText(context, "please select date from calender button", Toast.LENGTH_LONG).show();
                }

                //if no problem/error exist data can be put into database

                else {
                    Date d = null;
                    try {
                        d = dateFormat.parse(userDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long mdate = d.getTime();

                    int uAmount = Integer.parseInt(amountGet);
                    helper.update(rId, typeOfExpense, uAmount, mdate, uTime);
                    Toast.makeText(context, "Updated Successfully.", Toast.LENGTH_SHORT).show();

                    //back to fragment when updated expense complete
                    ExpenseFragment expenseFragment = new ExpenseFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frameLayoutID, expenseFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }


            }
        });

        //cancel update button process

        cancelUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseFragment expenseFragment = new ExpenseFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, expenseFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        //camera gallery activity


        updatedocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCameraGalleryBtnfield.setVisibility(View.VISIBLE);    //when press cencle button
            }
        });


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        cencleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCameraGalleryBtnfield.setVisibility(View.GONE);    //when press cencle button
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 0) {
                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                filesetIV.setImageBitmap(bitmap);
                changeDocumentData();                              //change database method called when update image
                updateCameraGalleryBtnfield.setVisibility(View.GONE);


            } else if (requestCode == 1) {

                Uri uri = data.getData();
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filesetIV.setImageBitmap(bitmap);
                changeDocumentData();
                updateCameraGalleryBtnfield.setVisibility(View.GONE);
                //set Image into ImageView


            }

        }
    }


    public void changeDocumentData() {
        String uDocument = null;
        if (bitmap != null) {
            uDocument = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
        }

        if (!uDocument.equals("")) {
            helper.updateDocument(rId, uDocument);          //update document data into database
        }


    }


    //encode  image to string url
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)    //example of parameter (mybitmap,Bitmap.CompressFormat.JPEG,100)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

}

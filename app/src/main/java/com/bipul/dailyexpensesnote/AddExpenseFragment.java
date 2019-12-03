package com.bipul.dailyexpensesnote;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddExpenseFragment extends Fragment {
    private Spinner typeSpinner;
    private EditText amountET, dateET, timeET;
    private Button addDocument, addExpense;

    private ImageView datePickBtn, timePickBtn, cancelAddBtn;
    private ImageView documentImage;
    private Bitmap bitmap = null;
    private String documentURL = "";
    private Context context;

    private Calendar calendar;
    private int hour, minute;

    String totalAmount;


    private LinearLayout cameraGalleryBtnField, camera, gallery, cancle;
    private String typeOfExpense;
    private DatabaseHelper helper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");

    public AddExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);
        context = container.getContext();

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        init(view);
        process(view);
        return view;

    }


    //activity of get content
    private void process(View view) {
        //set Type into spinner
        final String[] typeExpense = {"Rent", "Food", "Utility bills", "Medicine", "Cloathing", "Transport", "Health", "Gift"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_activated_1, typeExpense);
        typeSpinner.setAdapter(arrayAdapter);

        typeOfExpense = typeExpense[0];

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfExpense = typeExpense[position];
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
                String usertime = null;
                try {
                    usertime = timeFormat.format(calendar.getTime());
                } catch (Exception e) {
                    Toast.makeText(context, "Please take the time first : " + e, Toast.LENGTH_SHORT).show();
                }

                timeET.setText(usertime);

            }
        };


        //time picker
        timePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, timePick, hour, minute, false);
                timePickerDialog.updateTime(hour, minute);
                timePickerDialog.show();
            }


        });


        //add Document  section
        addDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request for permission on amera or gallery
                cameraGalleryBtnField.setVisibility(View.VISIBLE);

            }
        });


//add value into database
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getdate = dateET.getText().toString();
                DateValidate dv = new DateValidate();
                dv.matcher = Pattern.compile(dv.DATE_PATTERN).matcher(getdate);           //check valid Date from ValidDate class

                totalAmount = amountET.getText().toString();
                if (totalAmount.equals("") || dateET.getText().toString().equals("")) {
                    if (amountET.getText().toString().equals("")) {
                        amountET.setError("please enter amount");
                        amountET.requestFocus();
                    } else if (dateET.getText().toString().equals("")) {
                        dateET.setError("please enter date from date picker");
                        dateET.requestFocus();
                    }

                } else if (!dv.matcher.matches()) {
                    dateET.setError("Enter a valid Date format : yyyy/MM/dd");
                    dateET.requestFocus();
                    Toast.makeText(context, "Wrong Date format according to : yyyy/MM/dd", Toast.LENGTH_LONG).show();
                } else {
                    //add type,date,time,amount value into database
                    int uamount = Integer.valueOf(amountET.getText().toString());
                    String userDate = dateET.getText().toString();
                    Date d = null;
                    try {
                        d = dateFormat.parse(userDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long mdate = d.getTime();
                    String userTime = timeET.getText().toString();
                    if (bitmap != null) {
                        documentURL = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                    }

                    long id = helper.insertData(typeOfExpense, uamount, mdate, userTime, documentURL);

                    if (id == -1) {
                        Toast.makeText(context, "Error : Data  can not Inserted.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Expense Data : " + id + " is Inserted.", Toast.LENGTH_SHORT).show();

                       /* Bundle bundle = new Bundle();
                        bundle.putInt("a",uamount);
                         expenseFragment.setArguments(bundle);*/
                        //back to fragment when add expense complete
                        ExpenseFragment expenseFragment = new ExpenseFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.replace(R.id.frameLayoutID, expenseFragment);
                        ft.addToBackStack(null);
                        ft.commit();


                        //finish(); //need to add fragment for real time data change view

                    }


                }
            }
        });


        //impliments cancel add button
        cancelAddBtn.setOnClickListener(new View.OnClickListener() {
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


        //implement camera gallery section
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraGalleryBtnField.setVisibility(View.GONE);    //when press cencle button
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void init(View view) {
        typeSpinner = view.findViewById(R.id.addActivityTypeSpinnerID);
        amountET = view.findViewById(R.id.addActtivityexpenseAmountET);
        dateET = view.findViewById(R.id.addActivityexpenseDateET);
        timeET = view.findViewById(R.id.addActivityexpenseTimeET);
        addDocument = view.findViewById(R.id.addActivityaAddDocumentButton);
        addExpense = view.findViewById(R.id.addActivityAddExpenseBtn);
        datePickBtn = view.findViewById(R.id.addActivityDatePickerBtn);
        timePickBtn = view.findViewById(R.id.addActiviTimePickerBtn);
        documentImage = view.findViewById(R.id.fileIV);
        cameraGalleryBtnField = view.findViewById(R.id.cameraGalleryLLfield);
        camera = view.findViewById(R.id.cameraBtnID);
        gallery = view.findViewById(R.id.galleryBtnID);
        cancle = view.findViewById(R.id.cencleButtonID);
        cancelAddBtn = view.findViewById(R.id.addActivityCancelExpenseBtn);

        helper = new DatabaseHelper(context);

        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.INVISIBLE);

    }


//get Result from camera gallery image

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 0) {
                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                documentImage.setImageBitmap(bitmap);
                cameraGalleryBtnField.setVisibility(View.GONE);


            } else if (requestCode == 1) {

                Uri uri = data.getData();
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                documentImage.setImageBitmap(bitmap);
                cameraGalleryBtnField.setVisibility(View.GONE);
                //set Image into ImageView


            }

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

package com.bipul.dailyexpensesnote.income;


import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bipul.dailyexpensesnote.R;
import com.bipul.dailyexpensesnote.database.IncomeDatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeFragment extends Fragment {

    private FloatingActionButton addButton;

    private ImageView fromDatePickerBtn,toDatePickerbtn;
    private Spinner typeSpinner;
    private TextView fromTV,toTV,totalIncomeTv;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm aa");
    private String type;
    String []typeExpense;

    private Calendar calendar;
    private int year,month,fromDay,toDay,hour,minute;
    private Context context;
    private IncomeDatabaseHelper inComeHelper;
    private int totAmount=0;
    private int count=0;
    int currentPosition=0;
    private long  fdate,tdate;

    int dbAmount=0;

    private LinearLayout fromDateLL,toDateLL;


    public IncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_income, container, false);
        context=container.getContext();
        init(view);
        addData();

        try {
            process();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            pullData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void addData() {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddIncomeFragment addInComeFragment = new AddIncomeFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, addInComeFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void init(View view) {
        addButton = view.findViewById(R.id.addIncome);

        // Inflate the layout for this fragment

        typeSpinner=view.findViewById(R.id.typeSpinner);
        fromDatePickerBtn=view.findViewById(R.id.fromDatePickerBtn);
        toDatePickerbtn=view.findViewById(R.id.toDatePickerbtn);
        fromTV=view.findViewById(R.id.fromDateTV);
        toTV=view.findViewById(R.id.toDateTV);
        totalIncomeTv=view.findViewById(R.id.totalIncomeTV);
        inComeHelper=new IncomeDatabaseHelper(context);

        fromDateLL = view.findViewById(R.id.fromDataLL);
        toDateLL = view.findViewById(R.id.toDataLL);

        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("cSpinnerPosition",currentPosition);           //before destroy the activity save current state
    }

    private void process() throws ParseException {
        //set Type into spinner
        typeExpense= new String[]{"All","Salary", "Awards", "Grants", "Sale", "Rental", "Coupons", "Lottery", "Dividends","Investments"};
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_activated_1,typeExpense);
        typeSpinner.setAdapter(arrayAdapter);


        type=typeExpense[currentPosition];
        typeSpinner.setSelection(currentPosition);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;
                type=typeExpense[currentPosition];

                if(count>0){                     //set listener to pull modyifiying data when second time data set
                    try {
                        pullData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );


        //date picker part
        calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        int indexMonth=month+1;
        fromDay=1;
        toDay=calendar.get(Calendar.DAY_OF_MONTH);
        hour=calendar.get(Calendar.HOUR);
        minute=calendar.get(Calendar.MINUTE);

        //fromDate
        String nFromDate=year+"/"+indexMonth+"/"+fromDay;
        fromTV.setText(nFromDate);

        final DatePickerDialog.OnDateSetListener fromDateListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int fromDay) {
                calendar.set(year,month,fromDay);
                String userFromDate=dateFormat.format(calendar.getTime());
                //Toast.makeText(context,userFromDate+" is selected",Toast.LENGTH_LONG).show();
                fromTV.setText(userFromDate);
                if(count>0){                     //set listener to pull modyifiying data when second time data set
                    try {
                        pullData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        fromDateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set Date as begining of the month
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(),fromDateListener,year,month,fromDay);
                datePickerDialog.show();
            }
        });


        //toDate
        String nToDate=year+"/"+indexMonth+"/"+toDay;
        toTV.setText(nToDate);


        final DatePickerDialog.OnDateSetListener toDateListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int toDay) {
                calendar.set(year,month,toDay);
                String userFromDate=dateFormat.format(calendar.getTime());
                toTV.setText(userFromDate);
                if(count>0){                     //set listener to pull modyifiying data when second time data set
                    try {
                        pullData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        toDateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set Date as begining of the month
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(),toDateListener,year,month,toDay);
                datePickerDialog.show();

            }
        });

        /*
        Cursor cursor=helper.showTotalExpense(fdate,tdate,type);

        while (cursor.moveToNext()){
            int totalExpense=cursor.getInt(cursor.getColumnIndex(helper.TOT_EXPENSE));   //getTotal amount
            totalCostTv.setText("BDT "+totalExpense);
        }
*/

    }

    public  void pullData() throws ParseException {
        //getTotal expense by  type from DataBase and set Total Expense
        Date d1=dateFormat.parse(fromTV.getText().toString());
        Date d2=dateFormat.parse(toTV.getText().toString());
        fdate=d1.getTime();
        tdate=d2.getTime();

        Cursor cursor=inComeHelper.showAllData();
        while (cursor.moveToNext()){
            long dateFromDB=cursor.getLong(cursor.getColumnIndex(inComeHelper.COL_Date));
            String dbtype=cursor.getString(cursor.getColumnIndex(inComeHelper.COL_TYPE));
            count++;
            if(type.equals(typeExpense[0])&&dateFromDB>=fdate&&dateFromDB<=tdate){       //when selected All in types
                dbAmount=cursor.getInt(cursor.getColumnIndex(inComeHelper.COL_Amount));
                totAmount=totAmount+dbAmount;

            }
            else if(type.equals(dbtype)&&dateFromDB>=fdate&&dateFromDB<=tdate){          //when selected specific in types
                dbAmount=cursor.getInt(cursor.getColumnIndex(inComeHelper.COL_Amount));
                totAmount=totAmount+dbAmount;
            }

        }

        totalIncomeTv.setText(totAmount+"Tk");
      //  Toast.makeText(context, "Income "+totAmount, Toast.LENGTH_SHORT).show();
        totAmount=0;

    }
}

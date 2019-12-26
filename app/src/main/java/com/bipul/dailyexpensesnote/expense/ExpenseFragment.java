package com.bipul.dailyexpensesnote.expense;


import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bipul.dailyexpensesnote.adapter.AdapterExpense;
import com.bipul.dailyexpensesnote.database.ExpenseDatabaseHelper;
import com.bipul.dailyexpensesnote.database.IncomeDatabaseHelper;
import com.bipul.dailyexpensesnote.income.IncomeFragment;
import com.bipul.dailyexpensesnote.model.Expense;
import com.bipul.dailyexpensesnote.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

    private Spinner eTypeSpinner;
    private TextView fromDateTv, toDateTv;
    private ImageView fromDateBtn, toDateBtn;
    private TextView totalExpenseTV, totalIncomeTV, balanceTV;

    String[] typeExpense;
    private String typeOfExpense;
    private int count = 0;

    private FloatingActionButton addButton;
    private RecyclerView expenseRB;
    private List<Expense> expenseList;
    private AdapterExpense adapterExpense;
    private ExpenseDatabaseHelper helper;
    private Context context;

    int dbAmount;
    int dbAmountForExpense;
    int totalIncome = 0;
    int totalBalance = 0;
    int totalExpense = 0;
    int tExpense = 0;

    int income, balance, expense;

    private IncomeDatabaseHelper incomeDatabaseHelper;


    int tAmount = 0;


    private Calendar calendar;
    private int year, month, fromDay, toDay, hour, minute;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        context = container.getContext();

        init(view);

        scrolling();
        addData();

        try {
            initFiltardata(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        initR();
        try {
            getdata();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getTotalExpense();
        getTotalIncome();


        return view;

    }


    private void scrolling() {
        expenseRB.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addButton.getVisibility() == View.VISIBLE) {
                    addButton.hide();
                } else if (dy < 0 && addButton.getVisibility() != View.VISIBLE) {
                    addButton.show();
                }
            }
        });
    }

    private void initFiltardata(final View view) throws ParseException {
        //get data from spinner and calender

        //set Type into spinner
        typeExpense = new String[]{"All", "Food", "Bills", "Home", "Medicine", "Clothing", "Transport", "Health", "Gift", "Tex", "Baby",
                "Beauty", "Pet", "Hamburger", "Wine", "Office", "Travel", "Book", "Education", "Social", "Vegetables", "Sport", "Electronics"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_activated_1, typeExpense);
        eTypeSpinner.setAdapter(arrayAdapter);

        typeOfExpense = typeExpense[0];

        eTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfExpense = typeExpense[position];
                if (count > 0) {                     //set listener to pull modyifiying data when second time data set
                    try {
                        getdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //date picker part
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        int indexMonth = month + 1;
        fromDay = 1;
        toDay = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);


        //fromDate

        String nFromDate = year + "/" + indexMonth + "/" + fromDay;
        fromDateTv.setText(nFromDate);


        final DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int fromDay) {
                calendar.set(year, month, fromDay);
                String userFromDate = dateFormat.format(calendar.getTime());
                // Toast.makeText(context,userFromDate+" is selected",Toast.LENGTH_LONG).show();
                fromDateTv.setText(userFromDate);

                if (count > 0) {                     //set listener to pull modyifiying data when second time data set
                    try {
                        getdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set Date as begining of the month
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), fromDateListener, year, month, fromDay);
                datePickerDialog.show();
            }
        });

        //toDate
        String nToDate = year + "/" + indexMonth + "/" + toDay;
        toDateTv.setText(nToDate);


        final DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int toDay) {
                calendar.set(year, month, toDay);
                String userFromDate = dateFormat.format(calendar.getTime());
                toDateTv.setText(userFromDate);

                if (count > 0) {                     //set listener to pull modyifiying data when second time data set
                    try {
                        getdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        toDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set Date as begining of the month
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), toDateListener, year, month, toDay);
                datePickerDialog.show();

            }
        });

    }

    private void addData() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddExpenseFragment addExpenseFragment = new AddExpenseFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, addExpenseFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }


    private void getdata() throws ParseException {
        //pull data from database and add to ArrayList using cursor
        Date d1 = dateFormat.parse(fromDateTv.getText().toString());
        Date d2 = dateFormat.parse(toDateTv.getText().toString());
        long fromDate = d1.getTime();
        long toDate = d2.getTime();

        expenseList.clear();                    //clear previous data
        adapterExpense.notifyDataSetChanged();

        Cursor cursor = helper.showAllData();


        while (cursor.moveToNext()) {
            int amount = 0;
            int id = cursor.getInt(cursor.getColumnIndex(helper.COL_ID));
            String timeTo = cursor.getString(cursor.getColumnIndex(helper.COL_TIME));


            String type = cursor.getString(cursor.getColumnIndex(helper.COL_TYPE));

            long dateFromDB = cursor.getLong(cursor.getColumnIndex(helper.COL_Date));
            String dateTo = dateFormat.format(dateFromDB);


            if (dateFromDB >= fromDate && dateFromDB <= toDate) {
                amount = cursor.getInt(cursor.getColumnIndex(helper.COL_Amount));
                totalExpense = totalExpense + amount;

            }
            count++;

            if (typeOfExpense.equals(typeExpense[0]) && dateFromDB >= fromDate && dateFromDB <= toDate) {   //when selected All in types
                expenseList.add(new Expense(amount, type, dateTo, timeTo, id, null));  //add image documents next
                adapterExpense.notifyDataSetChanged();


            } else if (typeOfExpense.equals(type) && dateFromDB >= fromDate && dateFromDB <= toDate) {            //when selected specific in types
                expenseList.add(new Expense(amount, type, dateTo, timeTo, id, null));  //add image documents next
                adapterExpense.notifyDataSetChanged();
            }

        }

    }

    private void initR() {
        expenseRB.setLayoutManager(new LinearLayoutManager(context));
        expenseRB.setAdapter(adapterExpense);
    }

    private void init(View view) {
        helper = new ExpenseDatabaseHelper(context);
        addButton = view.findViewById(R.id.floatAB);
        expenseRB = view.findViewById(R.id.expenseRV);
        expenseList = new ArrayList<>();
        adapterExpense = new AdapterExpense(context, expenseList, helper);
        fromDateBtn = view.findViewById(R.id.fromDateCalenderBtn);
        toDateBtn = view.findViewById(R.id.toDateCalenderBtn);
        eTypeSpinner = view.findViewById(R.id.showActivityTypeSpinnerID);
        fromDateTv = view.findViewById(R.id.viewFromDateTV);
        toDateTv = view.findViewById(R.id.viewToDateTV);
        totalExpenseTV = view.findViewById(R.id.totalExpenseET);
        totalIncomeTV = view.findViewById(R.id.totalIncomeTV);
        balanceTV = view.findViewById(R.id.balanceTV);

        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.VISIBLE);
    }


    public void getTotalIncome() {
        incomeDatabaseHelper = new IncomeDatabaseHelper(getContext());
        Cursor cursor = incomeDatabaseHelper.getAllData();

        while (cursor.moveToNext()) {
            dbAmount = cursor.getInt(cursor.getColumnIndex(incomeDatabaseHelper.COL_Amount));
            totalIncome = dbAmount + totalIncome;
            count++;
        }

        totalBalance = getBalance(tExpense, totalIncome);
        balanceTV.setText(String.valueOf(totalBalance));
        totalBalance = 0;
        totalIncomeTV.setText(String.valueOf(totalIncome));
        totalIncome = 0;
    }

    public void getTotalExpense() {
        helper = new ExpenseDatabaseHelper(getContext());
        Cursor cursor = helper.showAllData();

        while (cursor.moveToNext()) {
            dbAmountForExpense = cursor.getInt(cursor.getColumnIndex(helper.COL_Amount));
            tExpense = dbAmountForExpense + tExpense;

            count++;
        }
        totalExpenseTV.setText(String.valueOf(totalExpense));
        totalExpense = 0;
    }

    public int getBalance(int totalExpense, int totalIncome) {
        int balance = totalIncome - totalExpense;
        return balance;
    }

}

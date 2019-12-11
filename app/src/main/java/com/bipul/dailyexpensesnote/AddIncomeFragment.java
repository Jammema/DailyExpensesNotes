package com.bipul.dailyexpensesnote;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddIncomeFragment extends Fragment {
    View view;
    FloatingActionButton addIncome;

    public AddIncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add_income, container, false);

        init();
        addData();

        return view;
    }

    private void init() {
        addIncome = view.findViewById(R.id.addIncome);
    }

    private void addData() {
        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddExpenseFragment addExpenseFragment = new AddExpenseFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, addExpenseFragment);
                ft.addToBackStack(null);
                ft.commit();
                Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

package com.pratik.expensemanager;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pratik.expensemanager.Model.Data;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    //Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating btn TextView

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private Boolean isOpen=false;

    //Animation
    private Animation fadeOpen,fadeClose;

    //Firebase..
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Dashboard Income and Expense
    private TextView totalIncomeResult;
    private TextView totoalExpenseResult;

    private TextView netBalanceResult;

    //Recycler View

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //its only practicses

        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);



        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        fab_main_btn=myview.findViewById(R.id.main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);

        //for calculation total income and expense
        totalIncomeResult =myview.findViewById(R.id.income_set_result);
        totoalExpenseResult=myview.findViewById(R.id.expense_set_result);
        netBalanceResult=myview.findViewById(R.id.totalBalance);


        //for Recycler for dashboard income expense
        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);


        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        fadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        fadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();

                if (isOpen){
                    fab_income_btn.startAnimation(fadeClose);
                    fab_expense_btn.startAnimation(fadeClose);
                    fab_income_btn.setClickable(false);
                    fab_income_btn.setClickable(false);


                    fab_income_txt.startAnimation(fadeClose);
                    fab_expense_txt.startAnimation(fadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }else{
                    fab_income_btn.startAnimation(fadeOpen);
                    fab_expense_btn.startAnimation(fadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(fadeOpen);
                    fab_expense_txt.startAnimation(fadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }

            }
        });

        //Calculate Total Income


//        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                int totalsum=0;
//                for(DataSnapshot mysnap:dataSnapshot.getChildren()){
//                    Data data =mysnap.getValue(Data.class);
//
//                    totalsum+=data.getAmount();
//
//
//                    String stResult =String.valueOf(totalsum);
//
//                    totalIncomeResult.setText("₹ "+stResult+".00");
//                }
//                final int totalbalinc=totalsum;
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        //Calculate Total Expense
//
//        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                int totalsume = 0;
//                for (DataSnapshot mysnapshot : datasnapshot.getChildren()) {
//                    Data data = mysnapshot.getValue(Data.class);
//                    totalsume += data.getAmount();
//                    String strTotalSum = String.valueOf(totalsume);
//                    totoalExpenseResult.setText("₹ "+strTotalSum+".00");
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalIncome = 0;
                for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalIncome += data.getAmount();
                    String stResult =String.valueOf(totalIncome);
                    totalIncomeResult.setText("₹ "+stResult+".00");
                }

                // Calculate total expense
                final int totalinc=totalIncome;
                mExpenseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int totalExpense = 0;
                        for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                            Data data = mysnap.getValue(Data.class);
                            totalExpense += data.getAmount();
                            String strTotalSum = String.valueOf(totalExpense);
                            totoalExpenseResult.setText("₹ "+strTotalSum+".00");
                        }

                        // Calculate net balance (Income - Expense)
                        int netBalance = totalinc - totalExpense;
                        String strTotalSum = String.valueOf(netBalance);
                        netBalanceResult.setText("₹ "+strTotalSum+".00");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler View
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManageExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManageExpense.setStackFromEnd(true);
        layoutManageExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManageExpense);



        return myview;
    }


    private void ftAnimation(){

        if (isOpen){
            fab_income_btn.startAnimation(fadeClose);
            fab_expense_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_income_btn.setClickable(false);


            fab_income_txt.startAnimation(fadeClose);
            fab_expense_txt.startAnimation(fadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }else{
            fab_income_btn.startAnimation(fadeOpen);
            fab_expense_btn.startAnimation(fadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fadeOpen);
            fab_expense_txt.startAnimation(fadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }


    }

    private void addData(){
        //Fab Button income::

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();

            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();

            }
        });
    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);

        mydialog.setView(myviewm);

        AlertDialog dialog =mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmmount = myviewm.findViewById(R.id.amount_edt);
        EditText edtType = myviewm.findViewById(R.id.type_edt);
        EditText edtNote = myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCansel=myviewm.findViewById(R.id.btnCancel);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type=edtType.getText().toString().trim();
                String ammount=edtAmmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();


                if (TextUtils.isEmpty(type)){
                    edtType.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(ammount)){
                    edtAmmount.setError("Required Field");
                    return;
                }


                int ourammontint =Integer.parseInt(ammount);
                if (TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field");
                    return;
                }

                String id=mIncomeDatabase.push() .getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ourammontint,type,note,id,mDate);

                mIncomeDatabase.child(id).setValue(data);


                Toast.makeText(getActivity(), "DATA ADDED", Toast.LENGTH_SHORT).show();

                ftAnimation();

                dialog.dismiss();

            }
        });
        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    public void expenseDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);

        mydialog.setView(myview);

        final AlertDialog dialog =mydialog.create();

        dialog.setCancelable(false);

        EditText ammount = myview.findViewById(R.id.amount_edt);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note = myview.findViewById(R.id.note_edt);


        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCansel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmammount = ammount.getText().toString().trim();
                String tmtype=type.getText().toString().trim();
                String tmnote = note.getText().toString().trim();

                if(TextUtils.isEmpty(tmammount)){
                    ammount.setError("Required Field");
                    return;
                }

                int ouramontint =Integer.parseInt(tmammount);

                if (TextUtils.isEmpty(tmtype)){
                    type.setError("Required Field");
                    return;
                }

                if (TextUtils.isEmpty(tmnote)){
                    note.setError("Required Field");
                    return;
                }


                String id=mExpenseDatabase.push() .getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ouramontint,tmtype,tmnote,id,mDate);

                mExpenseDatabase.child(id).setValue(data);


                Toast.makeText(getActivity(), "Successfully Updated", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeAmmount(model.getAmount());
                holder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };

// Assuming you have a RecyclerView named ''
        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();


        //for expense board dash

        FirebaseRecyclerOptions<Data> options1 =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseType(model.getType());
                holder.setExpenseAmmount(model.getAmount());
                holder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

// Assuming you have a RecyclerView named ''
        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();


    }

    //for income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }
        public void setIncomeType(String type){
            TextView mtype =mIncomeView.findViewById(R.id.type_income);
            mtype.setText(type);

        }

        public void setIncomeAmmount(int ammount){
            TextView mammount=mIncomeView.findViewById(R.id.ammoun_income_ds);

            String strAmmount = String.valueOf(ammount);
            mammount.setText(strAmmount);
        }

        public void setIncomeDate(String date){
            TextView mdate=mIncomeView.findViewById(R.id.date_income_ds);
            mdate.setText(date);
        }
    }


    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type){
            TextView mtype=mExpenseView.findViewById(R.id.type_expense);
            mtype.setText(type);
        }

        public void setExpenseAmmount(int ammount){
            TextView mammount = mExpenseView.findViewById(R.id.ammoun_expnense_ds);

            String strammount = String.valueOf(ammount);
            mammount.setText(strammount);
        }

        public void setExpenseDate(String date){
            TextView mdate= mExpenseView.findViewById(R.id.date_expense_ds);
            mdate.setText(date);
        }
    }
}
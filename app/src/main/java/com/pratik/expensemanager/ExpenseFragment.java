package com.pratik.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pratik.expensemanager.Model.Data;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {


    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView recyclerView;

    private TextView expenseTotalSum;

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;
    // button for update

    private Button btnUpdate;
    private Button btnDelete;

    //Data item value
    private String type;
    private String note;
    private int ammount;
    private String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview= inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid =mUser.getUid();


        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        recyclerView=myview.findViewById(R.id.recycler_id_expense);

        expenseTotalSum = myview.findViewById(R.id.expense_txt);


        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                int expensevalue=0;
                for (DataSnapshot mysnapshot:datasnapshot.getChildren()){
                    Data data=mysnapshot.getValue(Data.class);

                    expensevalue+=data.getAmount();

                    String strExpense=String.valueOf(expensevalue);

                    expenseTotalSum.setText("₹ "+strExpense+".00");




                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter =
                new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ExpenseFragment.MyViewHolder holder, int position, @NonNull Data model) {
                        holder.setType(model.getType());
                        holder.setNote(model.getNote());
                        holder.setDate(model.getDate());
                        holder.setAmmount(model.getAmount());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                post_key =getRef(position).getKey();

                                type=model.getType();
                                note=model.getNote();
                                ammount=model.getAmount();
                                UpdateDataItem();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ExpenseFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.expense_recycler_data, parent, false);
                        return new ExpenseFragment.MyViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);

        // Start listening for Firebase data
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        public void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);

        }

        public void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        public void setAmmount(int ammount){
            TextView mAmmount=mView.findViewById(R.id.ammount_txt_expense);
            String stammount=String.valueOf(ammount);
            mAmmount.setText("₹ "+stammount+".00");
        }
    }

    private void UpdateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.update_data_item,null);

        mydialog.setView(myview);

        AlertDialog dialog=mydialog.create();


        edtAmmount=myview.findViewById(R.id.amount_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        edtType=myview.findViewById(R.id.type_edt);

        //Set data to edittext
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());

        btnUpdate=myview.findViewById(R.id.btn_upd_update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String mdammount =String.valueOf(ammount);


                mdammount=edtAmmount.getText().toString().trim();

                int myAmmount =Integer.parseInt(mdammount);

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmmount,type,note,post_key,mDate);
                mExpenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
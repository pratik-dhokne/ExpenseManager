package com.pratik.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmail1;
    private EditText mPass1;
    private Button btnReg1;
    private TextView btnlog;


    //Firebase connection
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth=FirebaseAuth.getInstance();



        SigninDetails();
    }


    private void SigninDetails() {
        mEmail1=findViewById(R.id.editTextUsername1);
        mPass1=findViewById(R.id.editTextPassword1);
        btnReg1=findViewById(R.id.buttonLogin1);
        btnlog = findViewById(R.id.gotologin);

        btnReg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1 = mEmail1.getText().toString().trim();
                String passw1=mPass1.getText().toString().trim();

                if (TextUtils.isEmpty(email1)){
                    mEmail1.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(passw1)){
                    mPass1.setError("Password Required");
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email1,passw1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(getApplicationContext(),"Registration Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });




    }

}
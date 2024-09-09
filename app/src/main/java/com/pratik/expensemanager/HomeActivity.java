package com.pratik.expensemanager;

import static com.pratik.expensemanager.R.id.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    //for smoothnaviagationbar



    //fragment
    private DashBoardFragment dashBoardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    private FirebaseAuth mAuth;

    //directexit
    private long backPressTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);





        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView=findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);

        dashBoardFragment=new DashBoardFragment();
        incomeFragment=new IncomeFragment();
        expenseFragment=new ExpenseFragment();


        mAuth=FirebaseAuth.getInstance();


        bottomNavigationView=findViewById(R.id.bottomNavigationbar);
        frameLayout=findViewById(R.id.main_frame);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.dashboard) {
                    setFragment(new DashBoardFragment(),false);

                } else if (id == R.id.income) {
                    setFragment(new IncomeFragment(),false);

                } else if (id == R.id.expense) {
                    setFragment(new ExpenseFragment(),false);

                }

                return true;
            }
        });

        setFragment(new DashBoardFragment(),true);


    }

    private void setFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        if (isAppInitialized){
            fragmentTransaction.add(R.id.main_frame,fragment);
        }
        else {
            fragmentTransaction.replace(R.id.main_frame,fragment);
        }
        fragmentTransaction.commit();
    }




    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);


        if (drawerLayout.isDrawerOpen(GravityCompat.END)&&backPressTime+2000>System.currentTimeMillis()) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
        }
        backPressTime=System.currentTimeMillis();
        finishAffinity();
    }

    public void displaySelectedListener(MenuItem item){
        Fragment fragment=null;
        int id=item.getItemId();

        if (id==R.id.dashboard){
            fragment=new DashBoardFragment();
            
        } else if (id==R.id.income) {
            fragment=new IncomeFragment();
            
        } else if (id==R.id.expense) {
            fragment=new ExpenseFragment();

        } else if (id==R.id.logout){
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        if (fragment!=null){

            FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame,fragment);
            ft.commit();

        }

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item);
        return true;
    }


}
package com.example.hostelcalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignActivity extends AppCompatActivity {
    EditText fname,lname,email,password,cnfpassword;
    RadioGroup gender;
    FloatingActionButton signupbtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cnfpassword = findViewById(R.id.cnfpassword);
        signupbtn = findViewById(R.id.signUp);
        gender = findViewById(R.id.gender);
        radioButtonMale = findViewById(R.id.male);
        radioButtonFemale = findViewById(R.id.female);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButtonFemale.setError(null);
                }
        });




        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcessSignUp();
            }
        });

    }

    private void ProcessSignUp() {


        final String FirstName = fname.getText().toString().trim();
        final String LastName = lname.getText().toString().trim();
        final String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String ConfirmPassword = cnfpassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String namePattern = "^[\\p{L} .'-]+$";

        if(FirstName.length()==0){
            fname.setError("First Name is required");
            return;
        }

        if(!FirstName.matches(namePattern)){
            fname.setError("Enter Valid First Name");
            return;
        }

        if(LastName.length()==0){
            lname.setError("Last Name is required");
            return;
        }

        if(!LastName.matches(namePattern)){
            lname.setError("Enter Valid Last Name");
            return;
        }
        if(Email.length()==0){
            email.setError("Email is required");
            return;
        }
        if(!Email.matches(emailPattern)){
            email.setError("Enter Valid Email");
        }

        if(Password.length()==0){
            password.setError("Password is required");
            return;
        }
        if(Password.length()<6){
            password.setError("Password must be 6 digits ");
            return;
        }
        if(!ConfirmPassword.matches(Password)){
            cnfpassword.setError("Password not Match");
            return;
        }




        if(!radioButtonMale.isChecked() && !radioButtonFemale.isChecked()){
            radioButtonFemale.setError("Select Category");
            return;
        }
        int selectId = gender.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectId);
        final String gen = radioButton.getText().toString();


        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Mytag", "createUserWithEmail:success");
                            addDataToDatabase(Email,FirstName,LastName,gen);
                        } else {
                            Log.w("Mytag", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });


    }

    private void addDataToDatabase(String email, String firstName, String lastName, String male) {
        User user = new User(email,firstName,lastName,male);
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        databaseReference.child(userId).setValue(user);
        startActivity(new Intent(getApplicationContext(),LogInActivity.class));





    }


    public void LogIn(View view) {
        startActivity(new Intent(getApplicationContext(),HostelDetailsActivity.class));
    }
}
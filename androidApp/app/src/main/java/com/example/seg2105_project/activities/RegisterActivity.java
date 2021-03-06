package com.example.seg2105_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.seg2105_project.objects.Employee;
import com.example.seg2105_project.objects.Patient;
import com.example.seg2105_project.R;
import com.example.seg2105_project.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

import static com.example.seg2105_project.Hash_256.getSHA;
import static com.example.seg2105_project.Hash_256.toHexString;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    Switch swIsEmployee;
    EditText etName, etEmail, etPassword, etConfirmPassword;
    Button bRegister;
    User user;
    DatabaseReference reff;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    static long maxId;
    Employee e;
    Patient p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        etName = (EditText) findViewById(R.id.etName);
        swIsEmployee = (Switch) findViewById(R.id.swIsEmployee);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

        reff = FirebaseDatabase.getInstance().getReference().child("User");
        reff.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxId = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));

    }

    @Override
    public void onClick(View v) {
        String name = etName.getText().toString().trim();
        final boolean isEmployee = swIsEmployee.isChecked();
        final String email = etEmail.getText().toString().trim();
        String password1 = etPassword.getText().toString().trim();
        String password2 = etConfirmPassword.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "You must enter a name",
                    Toast.LENGTH_LONG).show();
        } else if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "You must enter an Email",
                    Toast.LENGTH_LONG).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
        } else if (password1.length() < 6 ) {
            Toast.makeText(getApplicationContext(),
                    "Password has to be at least 6 characters",
                    Toast.LENGTH_LONG).show();
        } else if (password1.equals(password2)) {

            if (isEmployee) {
                e = new Employee();
                e.setEmail(email);
                e.setName(name);
            } else {
                p = new Patient();
                p.setEmail(email);
                p.setName(name);
            }

            String password = "";
            try {
                password = toHexString(getSHA(password1));
            } catch (NoSuchAlgorithmException e) {

            }
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (isEmployee) {
                            reff = FirebaseDatabase.getInstance().getReference().child("Employee");
                            reff.child(email.replace(".", "")).setValue(e);
                        } else {
                            reff = FirebaseDatabase.getInstance().getReference().child("Patient");
                            reff.child(email.replace(".", "")).setValue(p);
                        }
                        //reff;
                        //reff.child(String.valueOf(maxId + 1)).setValue(user);
                        //FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                        Toast.makeText(getApplicationContext(),
                                "You have successfully registered as " + (isEmployee ? "Employee" : "Patient"),
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Firebase Authentication Error",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(),
                    "Two passwords don't match",
                    Toast.LENGTH_LONG).show();
        }
    }
}

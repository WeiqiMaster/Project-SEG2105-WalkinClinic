package com.example.seg2105_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.seg2105_project.dialog.DialogEmployeeAddService;
import com.example.seg2105_project.objects.Employee;
import com.example.seg2105_project.R;
import com.example.seg2105_project.objects.Service;
import com.example.seg2105_project.adapters.ServiceListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeeActivity extends AppCompatActivity implements View.OnClickListener, DialogEmployeeAddService.DialogEmployeeServiceListener {
    public final String NODE_NAME_SERVICE = "ServiceForDemo";
    Button btnEdit;
    TextView tvAddress;
    TextView tvPhoneNumber;
    TextView tvCompany;
    TextView tvLicensed;
    TextView tvDescription;

    ListView mListView;
    EditText etServiceName;
    EditText etRoleOfPerson;
    Button btnAddService;
    Button btnManageAvailability;
    ArrayList<Service> serviceList;

    ArrayList<String> serviceNamesAll;
    DatabaseReference databaseServiceList;
    DatabaseReference databaseUserList;
    DatabaseReference currentClinic;
    Employee currentEmployee;
    boolean[] checkedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        tvAddress = findViewById(R.id.tvAddress);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvCompany = findViewById(R.id.tvCompany);
        tvLicensed = findViewById(R.id.tvLicensed);
        tvDescription = findViewById(R.id.tvDescription);
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.listView);
        etServiceName = (EditText) findViewById(R.id.etServiceName);
        etRoleOfPerson = (EditText) findViewById(R.id.etRoleOfPerson);
        btnAddService = (Button) findViewById(R.id.btnAddService);
        btnAddService.setOnClickListener(this);
        btnManageAvailability = findViewById(R.id.btnManageAvailability);
        btnManageAvailability.setOnClickListener(this);

        serviceList = new ArrayList<>();

        databaseServiceList = FirebaseDatabase.getInstance().getReference().child(NODE_NAME_SERVICE);
        //databaseUserList = FirebaseDatabase.getInstance().getReference().child("User");

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = fbUser.getEmail().replace(".", "");
        currentClinic = FirebaseDatabase.getInstance().getReference().child("Employee").child(email);

        currentClinic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Object address = dataSnapshot.child("address").getValue();
//                if (address != null) {
//                    tvAddress.setText(address.toString());
//                }
//                Object company = dataSnapshot.child("company").getValue();
//                if (address != null) {
//                    tvCompany.setText(company.toString());
//                }
//                Object phoneNumber = dataSnapshot.child("phoneNumber").getValue();
//                if (address != null) {
//                    tvPhoneNumber.setText(phoneNumber.toString());
//                }
//                Object address = dataSnapshot.child("address").getValue();
//                if (address != null) {
//                    tvAddress.setText(address.toString());
//                }
                tvAddress.setText(dataSnapshot.child("address").getValue().toString());
                tvCompany.setText(dataSnapshot.child("company").getValue().toString());
                tvPhoneNumber.setText(dataSnapshot.child("phoneNumber").getValue().toString());
                tvDescription.setText(dataSnapshot.child("description").getValue().toString());
                tvLicensed.setText(Boolean.valueOf(dataSnapshot.child("licensed").getValue().toString()) ? "Yes" : "No");
                serviceList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.child("serviceList").getChildren()) {
                    serviceList.add(snapshot.getValue(Service.class));
                }
                ServiceListAdapter adapter = new ServiceListAdapter(getApplicationContext(),  R.layout.adapter_view_layout, serviceList);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        serviceNamesAll = new ArrayList<String>();
        databaseServiceList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //User user = snapshot.getValue(User.class);
                    String s = snapshot.child("name").getValue().toString()
                            + "      " + snapshot.child("roleOfPerson").getValue().toString();
                    serviceNamesAll.add(s);
                }
                checkedItems = new boolean[serviceNamesAll.size()];
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEdit:
                Intent intent = new Intent(this, ModifyEmplProfileActivity.class);
                intent.putExtra("address", tvAddress.getText().toString().trim());
                intent.putExtra("phoneNumber", tvPhoneNumber.getText().toString().trim());
                intent.putExtra("company", tvCompany.getText().toString().trim());
                intent.putExtra("licensed", tvLicensed.getText().toString().trim());
                intent.putExtra("description", tvDescription.getText().toString().trim());
                startActivityForResult(intent, 1);
                break;
            case R.id.btnAddService:
                currentClinic.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        serviceList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.child("serviceList").getChildren()) {
                            serviceList.add(snapshot.getValue(Service.class));
                            String s = snapshot.child("name").getValue().toString()
                                    + "      " + snapshot.child("roleOfPerson").getValue().toString();
                            checkedItems[serviceNamesAll.indexOf(s)] = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //checkedItems = new boolean[services.size()];
                String[] servicesArray = serviceNamesAll.toArray(new String[serviceNamesAll.size()]);

                DialogEmployeeAddService dialogUpdateServices = new DialogEmployeeAddService();
                dialogUpdateServices.passValues(servicesArray, checkedItems, serviceNamesAll, serviceList);
                dialogUpdateServices.show(getSupportFragmentManager(), "Choose Services");
                break;
            case R.id.btnManageAvailability:
                startActivity(new Intent(this, ManageAvailabilityActivity.class));
                break;
        }
    }

    @Override
    public void applyResult(boolean[] checkedItems) {
        this.checkedItems = checkedItems;
        serviceList = new ArrayList<Service>();
        for (int i = 0; i < serviceNamesAll.size(); i++) {
            if (checkedItems[i]) {
                //isAllFalse = false;
                String[] temp = serviceNamesAll.get(i).split("      ", 2);
                Service service = new Service(temp[0], temp[1]);
                serviceList.add(service);
            }
        }
        currentClinic.child("serviceList").setValue(serviceList);
        ServiceListAdapter adapter = new ServiceListAdapter(this,  R.layout.adapter_view_checked, serviceList);
        mListView.setAdapter(adapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String address = data.getStringExtra("address");
                String phoneNumber = data.getStringExtra("phoneNumber");
                String company = data.getStringExtra("company");
                tvAddress.setText(address);
                tvPhoneNumber.setText(phoneNumber);
                tvCompany.setText(company);
                String licensed = data.getExtras().getBoolean("licensed", false) ? "Yes" : "No";
                tvLicensed.setText(licensed);
                tvDescription.setText(data.getStringExtra(("description")));
            }
        }
    }
}

package pt.estoril.estorilpraia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private Button buttonLogout;
    private Button buttonSave;

    private DatabaseReference databaseReference;

    private EditText editTextName, editTextAddress, editTextAge, editTextTeam;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextTeam = (EditText) findViewById(R.id.editTextTeam);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        TextView textViewUserEmail = (TextView) findViewById(R.id.textViewMessage);
        assert textViewUserEmail != null;
        assert user != null;
        textViewUserEmail.setText("Welcome " + user.getEmail());

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        Button buttonSchedule = (Button) findViewById(R.id.buttonSchedule);
        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        assert buttonSchedule != null;
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveUserInformation() {
        String name = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String team = editTextTeam.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", name);
        editor.putString("address", address);
        editor.putString("team", team);
        editor.putString("age", age);
        editor.putString("Uid", user.getUid());
        editor.apply();

        UserInformation userInformation = new UserInformation(name, address, sharedPref.getString("email", null), team, age, false);

        databaseReference.child(sharedPref.getString("Uid", user.getUid())).setValue(userInformation);


        Toast.makeText(ProfileActivity.this, "Information saved " + name, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == buttonSave) {
            saveUserInformation();
        }
    }
}

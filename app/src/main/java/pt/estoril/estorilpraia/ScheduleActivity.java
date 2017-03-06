package pt.estoril.estorilpraia;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends AppCompatActivity {

    private ListView listViewResult;

    private DatabaseReference databaseReference;
    private CharSequence[] times = {"16:00", "16:45", "17:30", "18:15", "19:00", "19:45", "20:30", "21:15"};

    protected String dayOfWeek = "";
    private String showUserNames = "";
    protected String path;

    SharedPreferences sharedPref;
    private ArrayList<String> users = new ArrayList<>();
    private ScheduleInformation[] schedules = new ScheduleInformation[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Button buttonPickDate = (Button) findViewById(R.id.buttonPickDate);

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_listview, times);

        listViewResult = (ListView) findViewById(R.id.listViewHours);
        assert listViewResult != null;
        listViewResult.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        getDayOfWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        path = "Days/"+dayOfWeek+"/Slots/";

        fetchDayInformation();

        listViewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String str = (String) listViewResult.getItemAtPosition(position);
                path = "Days/"+dayOfWeek+"/Slots/" + str;

                hourInformationFromModel(str);

                new AlertDialog.Builder(ScheduleActivity.this)
                        .setTitle(str)
                        .setMessage(showUserNames)
                        .setPositiveButton(R.string.marcar_fisio, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                scheduleHour(str);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                showUserNames = "";
            }
        });


        assert buttonPickDate != null;
        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "Pick Date");
            }
        });
    }

    private void hourInformationFromModel(String str) {
        int i = 0;
        for( ; i < times.length; i++) {
            if(times[i] == str)
                break;
        }
        if(schedules != null)
            users = schedules[i].getUsers();
        if(users != null)
            for(String string : users)
                showUserNames += string + "\n";
    }

    private void fetchDayInformation() {
        path = "Days/"+dayOfWeek+"/Slots/";
        databaseReference = FirebaseDatabase.getInstance().getReference().child(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot hourSnapshot : dataSnapshot.getChildren()) {
                    schedules[i++] = hourSnapshot.getValue(ScheduleInformation.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            getDayOfWeek(year, month, day);
            path = "Days/"+dayOfWeek+"/Slots/";
            fetchDayInformation();
        }
    }

    /************************ Organizado ************************/
    protected void getDayOfWeek(int year, int month, int day) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(year, month, day-1);
        dayOfWeek = simpledateformat.format(date);
        switch (dayOfWeek) {
            case "Segunda-feira": dayOfWeek = "Monday"; break;
            case "Terça-feira": dayOfWeek = "Tuesday"; break;
            case "Quarta-feira": dayOfWeek = "Wednesday"; break;
            case "Quinta-feira": dayOfWeek = "Thursday"; break;
            case "Sexta-feira": dayOfWeek = "Friday"; break;
            case "Sábado" : dayOfWeek = "Monday"; break;
            case "Domingo" : dayOfWeek = "Monday"; break;
        }
    }

    private void scheduleHour(String str) {
        int i = 0;
        for( ; i < times.length; i++) {
            if(times[i] == str)
                break;
        }

        if(users == null) {
            users = new ArrayList<>();
        }
        else {
            users = schedules[i].getUsers();
        }

        long numUsers = schedules[i].getNumUsers();

        if(users.size() == 3) {
            showMessage("Can´t schedule");
            return;
        } else {
            users.add(sharedPref.getString("name", null));
            numUsers++;
            showMessage("Scheduling done");
        }

        ScheduleInformation scheduleInformation = new ScheduleInformation(numUsers == 3, numUsers, str, users);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(path);
        databaseReference.setValue(scheduleInformation);
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

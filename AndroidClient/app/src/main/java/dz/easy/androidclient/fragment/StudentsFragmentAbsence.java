package dz.easy.androidclient.fragment;

        import android.app.DatePickerDialog;
        import android.icu.util.GregorianCalendar;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.annotation.RequiresApi;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.GridLayoutManager;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
        import com.yarolegovich.lovelydialog.LovelyCustomDialog;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import butterknife.BindView;
        import butterknife.ButterKnife;
        import butterknife.internal.ListenerClass;
        import dz.easy.androidclient.Adapters.AbsenceAdapter;
        import dz.easy.androidclient.Adapters.StudentsListAbsenceAdapter;
        import dz.easy.androidclient.App.App;
        import dz.easy.androidclient.Constants.Constants;
        import dz.easy.androidclient.R;
        import dz.easy.androidclient.Util.CustomRequest;
        import dz.easy.androidclient.Util.CustomRequestArray;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class StudentsFragmentAbsence extends Fragment implements Constants, StudentsListAbsenceAdapter.AdapterInterface {

    private static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 100;
    private Spinner spinner;
    private TextView date;
    StudentsListAbsenceAdapter studentsListAdapter   = null;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    JSONObject emploiTeacher;
    private Button valider;
    private Button enable;
    private Button ajouter;
    private String finalIdSeance;

    private Boolean ajoutModif;
    private String newDate;
    LovelyCustomDialog dialog;
    View addDialog ;

    Calendar calender = Calendar.getInstance();
    int year_x, month_x, day_x;

    private static JSONObject user;
    private static JSONArray students;
    private static JSONObject module;
    public static StudentsFragmentAbsence newInstance(String user_, String student, String module_) {
        try {
            user = new JSONObject(user_);
            students = new JSONArray(student);
            module = new JSONObject(module_);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new StudentsFragmentAbsence();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_absence, container, false);

        return view;
    }

    private void showDatePicker() {

        DatePickerFragment date = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("year", year_x);
        args.putInt("month", month_x);
        args.putInt("day", day_x);

        date.setArguments(args);

        date.setCallBack(ondate);
        date.show(getActivity().getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            monthOfYear = monthOfYear + 1;
            String month = "";
            String day = "";
            String newdate = "";
            if(monthOfYear<10){
                month= "0"+monthOfYear;
            }else{
                month = String.valueOf(monthOfYear);
            }
            if(dayOfMonth<10){
                day= "0"+dayOfMonth;
            }else{
                day = String.valueOf(dayOfMonth);
            }

            newdate =  String.valueOf(year) + "-" + month + "-" +day;
            newDate = newdate;
            date.setText(newdate);
            calender.set(year,monthOfYear,dayOfMonth);
            int dayOfWeek= 0;
            dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);
            System.out.println("DAY_OF_WEEK : +======== "  + dayOfWeek );
            studentsListAdapter.changedDate(newDate);
            getSeanceByDate(dayOfWeek);
            //studentsListAdapter.notifyDataSetChanged();

        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getSeanceByTeacher();

        year_x = calender.get(Calendar.YEAR);
        month_x = calender.get(Calendar.MONTH);
        day_x = calender.get(Calendar.DAY_OF_MONTH);
        date = (TextView) view.findViewById(R.id.date);

        year_x = calender.get(Calendar.YEAR);
        month_x = calender.get(Calendar.MONTH);
        day_x = calender.get(Calendar.DAY_OF_MONTH);

        String month = "";
        String day = "";
        String newdate = "";
        if(month_x<10){
            month= "0"+month_x;
        }else{
            month = String.valueOf(month_x);
        }
        if(day_x<10){
            day= "0"+day_x;
        }else{
            day = String.valueOf(day_x);
        }

        newdate =  String.valueOf(year_x) + "-" + month + "-" +day;

        date.setText(newdate);
        newDate = date.getText().toString();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();

            }
        });
        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mRecyclerView.setHasFixedSize(true);
        //Use this now

        try {
            studentsListAdapter = new StudentsListAbsenceAdapter(students ,module.getString("_id"), (StudentsListAbsenceAdapter.AdapterInterface) StudentsFragmentAbsence.this);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        mRecyclerView.setAdapter(studentsListAdapter);
        valider = (Button) view.findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentsListAdapter.updateAbsences(getActivity());
            }
        });
        ajouter = (Button) view.findViewById(R.id.ajouter);
        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentsListAdapter.ajoutAbsences(getActivity(),finalIdSeance);
                ajouter.setVisibility(View.INVISIBLE);
                valider.setVisibility(View.VISIBLE);
            }
        });
        enable = (Button) view.findViewById(R.id.enable);
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajouter.setVisibility(View.VISIBLE);
                ajoutModif = true;
                showDatePicker();
            }
        });
        ajouter.setVisibility(View.INVISIBLE);
        valider.setVisibility(View.INVISIBLE);
        enable.setVisibility(View.VISIBLE);
        //mRecyclerView.
    }
    @Override
    public void buttonPressed(final JSONObject student,JSONArray ansences) throws JSONException {
        //Fragment fragment =   AbsenceFragment.newInstance(student,module.getString("name"),module.getString("_id"));
        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.pager , fragment).commit();
    }

    public void getSeanceByTeacher(){
        try {
            CustomRequest jsonReq = new CustomRequest(Request.Method.GET, GET_TIME_TABLE_TEACHER+ "/" + user.getString("_id"), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("EMPLOIE TEACHER : ====== " +response);
                            emploiTeacher = response;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            App.getInstance().addToRequestQueue(jsonReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSeanceByDate(final int date) {
        try{
        JSONArray day = new JSONArray();
        switch (date) {
            case 4 :
                day = emploiTeacher.getJSONArray("sunday");
                break;
            case 5 :
                day = emploiTeacher.getJSONArray("monday");
                break;
            case 6 :
                day = emploiTeacher.getJSONArray("tuesday");
                break;
            case 7 :
                day = emploiTeacher.getJSONArray("wednesday");
                break;
            case 1 :
                day = emploiTeacher.getJSONArray("thursday");
                break;
            default:
                Toast.makeText(getActivity(),"Journé non Valide",Toast.LENGTH_SHORT).show();
                break;
        }
            if(day.length()>0)
            {
                List<String> modules = new ArrayList<String>();
                final JSONArray seances = new JSONArray();
                try{
                    for (int i =0;i<day.length();i++) {
                        JSONObject seance = day.getJSONObject(i);
                        JSONObject idmodule = seance.getJSONObject("module");
                        if(idmodule.getString("_id").equals(module.getString("_id"))){
                            studentsListAdapter.enableCase(true);
                            enable.setVisibility(View.INVISIBLE);
                            String type = "";
                            try{
                                type = seance.getString("type") + " de : ";
                            }catch (Exception e){
                                e.printStackTrace();
                                type = "heur de : ";
                            }
                            String seanceSpinner = type + seance.getString("starts") + " à "+seance.getString("ends");
                            modules.add(seanceSpinner);
                            seances.put(seance);
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                if(seances.length()>1){
                    startDialog(day,seances,modules);
                }else if(seances.length()==0) {
                    Toast.makeText(getActivity(), "Pas de séance pour ce module ce jour", Toast.LENGTH_SHORT).show();
                }else{
                    getAbsneceBySeance(seances.getJSONObject(0), newDate);
                }
            }else {
                Toast.makeText(getActivity(),"Pas de séance dans ce jour",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startDialog(final JSONArray response, final JSONArray pseances, List<String> modules){
     /*   List<String> modules = new ArrayList<String>();
        final JSONArray seances = new JSONArray();
        try{
            for (int i =0;i<response.length();i++) {
                JSONObject seance = response.getJSONObject(i);
                JSONObject idmodule = seance.getJSONObject("module");
                if(idmodule.getString("_id").equals(module.getString("_id"))){
                    String type = "";
                    try{
                        type = seance.getString("type") + " de : ";
                    }catch (Exception e){
                        e.printStackTrace();
                        type = "heur de : ";
                    }
                    String seanceSpinner = type + seance.getString("starts") + " à "+seance.getString("ends");
                    modules.add(seanceSpinner);
                    seances.put(seance);
                }
            }} catch (JSONException e){
            e.printStackTrace();
        }*/
        addDialog = getActivity().getLayoutInflater().inflate(R.layout.absence_dialog, null);
        final TextView nom = (TextView) addDialog.findViewById(R.id.nomdialog);
        final Spinner etat = (Spinner) addDialog.findViewById(R.id.okdialog);
        ArrayAdapter<String> adap = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, modules);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etat.setAdapter(adap);
        nom.setText("");
        dialog = new LovelyCustomDialog(getContext(), R.style.EditTextTintTheme)
                .setTopColorRes(R.color.darkRed)
                .setView(addDialog)
                .setTitle(R.string.text_input_title)
                .setIcon(R.drawable.ic_add_alert_black_24dp)
                .setCancelable(false);
        dialog.show();
        Button ok = (Button) addDialog.findViewById(R.id.ok);
        Button annuler = (Button) addDialog.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               try{
                   String module = etat.getSelectedItem().toString();
               }catch (Exception e){
                   System.out.println("NO MODULE ");
               }
                try {
                    int position = etat.getSelectedItemPosition();
                    getAbsneceBySeance(pseances.getJSONObject(position), newDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public int getAbsneceBySeance(final JSONObject seances, final String date){
        String idSeance = "";
        try {
            idSeance = seances.getString("_id");
            finalIdSeance = seances.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomRequestArray jsonReq = new CustomRequestArray(Request.Method.GET, GET_ABSENCE_BY_SEANCE+ "/" + idSeance, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject absences = new JSONObject();
                        if(response.length()!=0){
                        try{
                            for (int i =0;i<response.length();i++) {
                                JSONObject absence = response.getJSONObject(i);
                                String serverDate = absence.getString("date").substring(0,10);
                                System.out.println("LA DATE /"+date+ " LA DATE 2 ::: /"+ serverDate);
                                if(serverDate.equals(date)){
                                    absences = absence;
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        System.out.println("ABSENCE BY With Date  : "+ absences);
                        //String idAbsence = absences.getString("_id");
                        JSONArray studentsAbs = new JSONArray();
                        try {
                             String idAbsence = absences.getString("_id");
                             studentsAbs = absences.getJSONArray("students");
                             ajouter.setVisibility(View.INVISIBLE);
                             valider.setVisibility(View.VISIBLE);
                             studentsListAdapter.changedAbsence(getActivity(),studentsAbs,idAbsence);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("STUDENTS BY With Date  : "+ absences);
                    }else{
                            ajouter.setVisibility(View.VISIBLE);
                            valider.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        App.getInstance().addToRequestQueue(jsonReq);
        return 0;

    }

}




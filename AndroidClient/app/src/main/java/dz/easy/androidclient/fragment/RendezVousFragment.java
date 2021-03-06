package dz.easy.androidclient.fragment;
import android.app.DatePickerDialog;
import android.os.Build;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dz.easy.androidclient.Activities.GroupActivity;
import dz.easy.androidclient.Activities.RendezVousActivity_;
import dz.easy.androidclient.Adapters.GroupListAdapter;
import dz.easy.androidclient.Adapters.RendezVousStatesAdapter;
import dz.easy.androidclient.App.App;
import dz.easy.androidclient.Constants.Constants;
import dz.easy.androidclient.R;
import dz.easy.androidclient.Adapters.TeachersAdapter;
import dz.easy.androidclient.Adapters.TestRecyclerViewAdapter;
import dz.easy.androidclient.Util.CustomRequest;

import dz.easy.androidclient.Services.DataReceiver;
import dz.easy.androidclient.Services.RendezVousService;
import dz.easy.androidclient.Util.CustomRequestArray;
import dz.easy.androidclient.Util.IDialog;

import static dz.easy.androidclient.App.BaseActivity.TAG;
import static dz.easy.androidclient.Services.GroupService.GET_GROUP_MODULE_TEACHER;
import static dz.easy.androidclient.Services.RendezVousService.GET_RENDEZVOUS_MANAGER;
import static dz.easy.androidclient.Services.RendezVousService.GET_RENDEZVOUS_STUDENT;
import static dz.easy.androidclient.Services.RendezVousService.GET_RENDEZVOUS_TEACHER;

public class RendezVousFragment extends Fragment implements Constants,DataReceiver.Receiver ,TestRecyclerViewAdapter.AdapterInterface{

  private static final boolean GRID_LAYOUT = false;
  private static final int ITEM_COUNT = 100;

  @BindView(R.id.recyclerView)
  RecyclerView recyclerViewRendeVous;

  LovelyCustomDialog dialog;
  View addDialog ;
  DatePickerDialog.OnDateSetListener ondate;
  Calendar calender = Calendar.getInstance();
  int year_x, month_x, day_x;
  private String newDate;

  private static JSONObject user;
  private static String rdvState;
  private IDialog dialogListner ;
  private DataReceiver mReceiver ;

  public static RendezVousFragment newInstance(JSONObject managerData,String state) {
    user = managerData;
    rdvState = state;
    return new RendezVousFragment();
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

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_recyclerview, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mReceiver = new DataReceiver(new Handler());
    mReceiver.setReceiver(this);

    ButterKnife.bind(this, view);
    if (GRID_LAYOUT) {
      recyclerViewRendeVous.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    } else {
      recyclerViewRendeVous.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    recyclerViewRendeVous.setHasFixedSize(true);

    //Use this now
    recyclerViewRendeVous.addItemDecoration(new MaterialViewPagerHeaderDecorator());

    try {
      if(user.getString("_type").equals("Teacher")){
        //Toast.makeText(getContext() , "Hi Teacher" , Toast.LENGTH_LONG).show();
        RendezVousService.getRendezVousByTeacher(getContext() , mReceiver , rdvState);
      }else if (user.getString("_type").equals("Manager")){
        //Toast.makeText(getContext() , "Hi Manager" , Toast.LENGTH_LONG).show();
        RendezVousService.getRendezvousByManager(getContext() ,mReceiver );
      }else if (user.getString("_type").equals("Student")){
        //Toast.makeText(getContext() , "Hi Student" , Toast.LENGTH_LONG).show();
        RendezVousService.getRendeVousByStudent(getContext() , mReceiver , rdvState);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }}


/*
<<<<<<< HEAD
  private void getRendeVousByStudent() {
    try {
      CustomRequestArray jsonReq = new CustomRequestArray(Request.Method.GET, GET_RDV_BY_STUDENT + "/"  + rdvState + "/" + user.getString("_id"), null,
        new Response.Listener<JSONArray>() {
          @Override
          public void onResponse(JSONArray response) {

            // JSONObject modules = response.getJSONObject("course");
            //setup materialviewpager

            if (GRID_LAYOUT) {
              recyclerViewRendeVous.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            } else {
              recyclerViewRendeVous.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
            recyclerViewRendeVous.setHasFixedSize(true);
            //Use this now
            recyclerViewRendeVous.addItemDecoration(new MaterialViewPagerHeaderDecorator());
            recyclerViewRendeVous.setAdapter(new TestRecyclerViewAdapter(response,(TestRecyclerViewAdapter.AdapterInterface) RendezVousFragment.this));

=======
>>>>>>> 55388211e7ecbd404a57417f22718441319de1fe
*/


  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    dialogListner = (RendezVousActivity_) activity ;
  }

  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {
    switch (resultCode) {
      case STATUS_RUNNING:
        dialogListner.showDialog();
        break;
      case STATUS_FINISHED:
          /* Hide progress & extract result from bundle */
        dialogListner.hideDialog();
        switch (resultData.getString("action")){
          case GET_RENDEZVOUS_MANAGER :
            String jsonStringManager = resultData.getString("result");
            JSONArray response = null;
            try {
              response = new JSONArray(jsonStringManager);
              recyclerViewRendeVous.setAdapter(new TeachersAdapter(response,(TeachersAdapter.AdapterInterface) this));

            } catch (JSONException e) {
              e.printStackTrace();
            }
            /*
<<<<<<< HEAD
            recyclerViewRendeVous.setHasFixedSize(true);

            //Use this now
            recyclerViewRendeVous.addItemDecoration(new MaterialViewPagerHeaderDecorator());
            recyclerViewRendeVous.setAdapter(new TestRecyclerViewAdapter(response,(TestRecyclerViewAdapter.AdapterInterface) RendezVousFragment.this));


          }
        }, new Response.ErrorListener() {
      *
        @Override
            public void onErrorResponse(VolleyError error) {
            */
            break ;
          case GET_RENDEZVOUS_STUDENT :
            String jsonStringStudent = resultData.getString("result");
            JSONArray responseStudent = null;
            try {
              responseStudent = new JSONArray(jsonStringStudent);
              //recyclerViewRendeVous.setAdapter(new TeachersAdapter(responseStudent));

            } catch (JSONException e) {
              e.printStackTrace();
            }
            break ;
          case GET_RENDEZVOUS_TEACHER :
            String jsonStringTeacher = resultData.getString("result");
            JSONArray responseTeacher = null;
            try {
              responseTeacher = new JSONArray(jsonStringTeacher);
              //recyclerViewRendeVous.setAdapter(new TeachersAdapter(responseTeacher));

            } catch (JSONException e) {
              e.printStackTrace();
            }
            break ;
        }

        break;
      case STATUS_ERROR:
                  /* Handle the error */
        String error = resultData.getString(Intent.EXTRA_TEXT);
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        break;
    }
  }


  private void  getRendeVousByManager(){

    CustomRequestArray jsonReq = new CustomRequestArray(Request.Method.GET, GET_RDV_BY_TEACHER, null,
      new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
          //setup materialviewpager

          if (GRID_LAYOUT) {
            recyclerViewRendeVous.setLayoutManager(new GridLayoutManager(getActivity(), 2));
          } else {
            recyclerViewRendeVous.setLayoutManager(new LinearLayoutManager(getActivity()));
          }
          recyclerViewRendeVous.setHasFixedSize(true);

          //Use this now
          recyclerViewRendeVous.addItemDecoration(new MaterialViewPagerHeaderDecorator());
          //recyclerViewRendeVous.setAdapter(new TeachersAdapter(response,(TeachersAdapter.AdapterInterface) RendezVousActivity.this));


        }
      }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {

      }
    });

    App.getInstance().addToRequestQueue(jsonReq);

  }

  @Override
  public void buttonPressed(final JSONObject rdv) {

    addDialog = getActivity().getLayoutInflater().inflate(R.layout.rdv_dialog, null);
    final TextView nom = (TextView) addDialog.findViewById(R.id.nomdialog);
    final TextView date = (TextView) addDialog.findViewById(R.id.date);
    final EditText heur = (EditText) addDialog.findViewById(R.id.heur);
    final EditText value = (EditText) addDialog.findViewById(R.id.value);

    dialog = new LovelyCustomDialog(getContext(), R.style.EditTextTintTheme)
      .setTopColorRes(R.color.darkRed)
      .setView(addDialog)
      .setTitle(R.string.text_input_title)
      .setIcon(R.drawable.ic_add_alert_black_24dp)
      .setCancelable(false);
    dialog.show();
    Button ok = (Button) addDialog.findViewById(R.id.ok);
    Button annuler = (Button) addDialog.findViewById(R.id.cancel);

    date.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showDatePicker();
      }
    });
    ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        accept(rdv,date.getText().toString(),heur.getText().toString(),value.getText().toString());
        dialog.dismiss();
      }
    });
    annuler.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        refuse(rdv,value.getText().toString());
        dialog.dismiss();
      }
    });
      ondate = new DatePickerDialog.OnDateSetListener() {
      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear,
                            int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        date.setText(year + "-" + monthOfYear+1 + "-" +dayOfMonth);
        calender.set(year,monthOfYear,dayOfMonth);
        int dayOfWeek= 0;
        dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);
        System.out.println("DAY_OF_WEEK : +======== "  + dayOfWeek );
        //studentsListAdapter.notifyDataSetChanged();

      }
    };
  }
  public void accept(JSONObject rdv, final String date, final String heur, final String remarque){

    CustomRequest jsonReq = null;
    try {
      jsonReq = new CustomRequest(Request.Method.POST, ACCEPT_RDV + "/" +rdv.getString("_id"), null,
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {

          }
        }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          //hidepDialog();
        }
      }) {
        @Override
        protected Map<String, String> getParams() {
          Map<String, String> params = new HashMap<String, String>();
          params.put("date", date);
          params.put("heur", heur);
          params.put("remarque", remarque);
          return params;
        }
      };
    } catch (JSONException e) {
      e.printStackTrace();
    }

    App.getInstance().addToRequestQueue(jsonReq);

  }

  public void refuse(JSONObject rdv,final String remarque){

    CustomRequest jsonReq = null;
    try {
      jsonReq = new CustomRequest(Request.Method.POST, REFUSE_RDV + "/" +rdv.getString("_id"), null,
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {

          }
        }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          //hidepDialog();
        }
      }) {
        @Override
        protected Map<String, String> getParams() {
          Map<String, String> params = new HashMap<String, String>();
          params.put("remarque", remarque);
          return params;
        }
      };
    } catch (JSONException e) {
      e.printStackTrace();
    }

    App.getInstance().addToRequestQueue(jsonReq);


  }
}

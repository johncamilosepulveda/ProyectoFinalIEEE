package com.example.proyectofinalieee.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.proyectofinalieee.Adapters.EventoAdapter;
import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragCalendario.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragCalendario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragCalendario extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CalendarView calendarView;
    private TextView tv_mes;
    private ListView view_eventos;

    private EventoAdapter adaptador;

    private List<EventDay> events;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragCalendario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragCalendario.
     */
    // TODO: Rename and change types and number of parameters
    public static FragCalendario newInstance(String param1, String param2) {
        FragCalendario fragment = new FragCalendario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FirebaseDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_frag_calendario, null);
        db = FirebaseDatabase.getInstance();


        view_eventos = inflate.findViewById(R.id.list_eventos);
        adaptador = new EventoAdapter(inflate.getContext());
        view_eventos.setAdapter(adaptador);
        view_eventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragEvento fragmento = new FragEvento();
                Evento evento = adaptador.getItem(i);

                Bundle bundle = new Bundle();
                bundle.putSerializable("evento", evento);
                fragmento.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedorFragments, fragmento);
                transaction.commit();
            }
        });

        calendarView = inflate.findViewById(R.id.calendarView);
        cargarCalendario();

        return inflate;
    }

    private void cargarCalendario() {
        Calendar calendar = Calendar.getInstance();
        try {
            calendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        events = new ArrayList<>();

        refrescarEventos();

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                refrescarEventos();

            }
        });


        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                refrescarEventos();
            }
        });


        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                final int year = clickedDayCalendar.get(Calendar.YEAR);
                int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
                final int dayOfMonth = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                adaptador.clear();
                Log.e("onSelectedDayChange: ", "entra al seleccionar");
                Log.e("onSelectedDayChange: ", year + "-" + month + "-" + dayOfMonth);
                db.getReference().child("Eventos")
                        .orderByChild("mMonth").equalTo(month)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                                    Evento evento = postsnapshot.getValue(Evento.class);
                                    if (evento.mYear == year && evento.mDay == dayOfMonth) {
                                        Log.e("Evento: ", evento.getNombre());
                                        adaptador.addEvent(evento);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });


    }

    public void refrescarEventos() {
        adaptador.clear();
        Calendar calendar = calendarView.getCurrentPageDate();
        calendar = calendarView.getCurrentPageDate();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        db.getReference().child("Eventos")
                .orderByChild("mMonth").equalTo(month)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        events.clear();
                        for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                            Evento evento = postsnapshot.getValue(Evento.class);
                            if (evento.mYear == year) {
                                Log.e("Evento: ", evento.getNombre());
                                Calendar evento_fecha = Calendar.getInstance();
                                evento_fecha.set(evento.getmYear(), evento.getmMonth() - 1, evento.getmDay());
                                events.add(new EventDay(evento_fecha, R.drawable.check));
                            }
                        }
                        calendarView.setEvents(events);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
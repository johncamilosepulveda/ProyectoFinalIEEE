package com.example.proyectofinalieee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Horario;

import java.util.ArrayList;

public class HorarioAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Horario> horarios;

    public HorarioAdapter(Context context) {

        this.context = context;
        horarios = new ArrayList<Horario>();
    }

    @Override
    public int getCount() {
        return horarios.size();
    }

    @Override
    public Object getItem(int i) {
        return horarios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View renglon = inflater.inflate(R.layout.renglon_horario, null);
        Horario horario = horarios.get(i);

        TextView tv_dia_semana = renglon.findViewById(R.id.tv_dia_semana);
        TextView tv_hora_inicio = renglon.findViewById(R.id.tv_hora_inicio);
        TextView tv_hora_fin = renglon.findViewById(R.id.tv_hora_fin);
        TextView tv_ubicacion = renglon.findViewById(R.id.tv_ubicacion);

        tv_dia_semana.setText(horario.getDiaSemana());

        String horaEntrada = horario.getHoraEntrada();
        String[] he = horaEntrada.split(":");
        if (Integer.parseInt(he[1]) < 10)
            he[1] = "0" + he[1];
        tv_hora_inicio.setText(he[0] + ":" + he[1]);

        String horaSalida = horario.getHoraSalida();
        String[] hs = horaSalida.split(":");
        if (Integer.parseInt(hs[1]) < 10)
            hs[1] = "0" + hs[1];
        tv_hora_fin.setText(hs[0] + ":" + hs[1]);
        tv_ubicacion.setText(horario.getLugar());

        return renglon;
    }

    public void addHorario(Horario horario) {
        horarios.add(horario);
        notifyDataSetChanged();
    }

    public ArrayList<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(ArrayList<Horario> horarios) {
        this.horarios = horarios;
        notifyDataSetChanged();
    }
}

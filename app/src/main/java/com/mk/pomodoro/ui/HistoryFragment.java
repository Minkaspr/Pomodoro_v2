package com.mk.pomodoro.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mk.pomodoro.R;
import com.mk.pomodoro.controller.HistorialApi;
import com.mk.pomodoro.controller.HistorialApiClient;
import com.mk.pomodoro.models.Historial;
import com.mk.pomodoro.ui.adapter.HistorialAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistorialAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        recyclerView = view.findViewById(R.id.rvHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        HistorialApi apiService = HistorialApiClient.getApiClient().create(HistorialApi.class);

        Call<List<Historial>> call = apiService.getHistorial();
        call.enqueue(new Callback<List<Historial>>() {
            @Override
            public void onResponse(Call<List<Historial>> call, Response<List<Historial>> response) {
                List<Historial> historiales = response.body();
                adapter = new HistorialAdapter(historiales);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Historial>> call, Throwable t) {
                // Para los errores
            }
        });

        return view;
    }
}
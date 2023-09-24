package com.mk.pomodoro.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mk.pomodoro.R;

public class CustomTimerBottomSheet extends BottomSheetDialogFragment {

    private TextInputLayout tilTiempoTrabajo, tilTiempoDescanso;
    private TextInputEditText tietTiempoTrabajo, tietTiempoDescanso;
    private Button btnCerrar, btnConfirmar;
    public static CustomTimerBottomSheet newInstance() {
        return new CustomTimerBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_custom_timer, container, false);

        tilTiempoTrabajo = view.findViewById(R.id.tilWorkingTime);
        tietTiempoTrabajo = view.findViewById(R.id.tietWorkingTime);
        tilTiempoDescanso = view.findViewById(R.id.tilBreakTime);
        tietTiempoDescanso = view.findViewById(R.id.tietBreakTime);
        btnCerrar = view.findViewById(R.id.btnClose);
        btnConfirmar = view.findViewById(R.id.btnConfirm);

        btnCerrar.setOnClickListener(v -> new Handler().postDelayed(() -> dismiss(), 250));

        tietTiempoTrabajo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarEntrada(userInput, 5, 90, tilTiempoTrabajo);
            }

            @Override
            public void afterTextChanged(Editable userInput) {}
        });

        tietTiempoTrabajo.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                tietTiempoDescanso.requestFocus();
                return true;
            }
            return false;
        });

        tietTiempoDescanso.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarEntrada(userInput, 1, 30, tilTiempoDescanso);
            }

            @Override
            public void afterTextChanged(Editable userInput) {}
        });

        tietTiempoDescanso.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tietTiempoDescanso.clearFocus();
                btnConfirmar.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        return view;
    }

    private void validarEntrada(CharSequence entradaUsuario , int minimo, int maximo, TextInputLayout til) {
        String input = entradaUsuario.toString();

        if (!input.isEmpty()) {
            int numero = Integer.parseInt(input);
            if (numero < minimo || numero > maximo) {
                til.setError("Ingresa un número entre " + minimo + " y " + maximo);
                btnConfirmar.setEnabled(false);
            } else {
                til.setError(null);
                if (tilTiempoTrabajo.getError() == null && tilTiempoDescanso.getError() == null
                        && !tietTiempoTrabajo.getText().toString().isEmpty()
                        && !tietTiempoDescanso.getText().toString().isEmpty()) {
                    btnConfirmar.setEnabled(true);
                }
            }
        } else {
            til.setError("No puede estar vacío");
            btnConfirmar.setEnabled(false);
        }
    }

}

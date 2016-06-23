package com.msk.taf.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msk.taf.R;
import com.msk.taf.calc.FormataTexto;
import com.msk.taf.db.DBTAF;

@SuppressLint("ValidFragment")
public class InfoTeste extends Fragment {

    public static final String NR_AVALIADO = "nr_avaliado";
    private static Bundle args;
    // CLASSES UTILIZADAS PARA MANIPULAR OS DADOS
    DBTAF db;
    FormataTexto formatador = new FormataTexto();
    // ELEMENTOS PARA CRIAR AS PAGINAS
    ViewPager mViewPager;
    private Cursor cursor;
    // VARIAVEIS UTILIZADAS
    private int teste;
    // ELEMENTOS QUE SERAO EXIBIDOS NAS PAGINAS
    private TextView diaTAF, generoTAF, idadeTAF, nomeTAF, taf12, taf2400,
            taf75, tafABD, tafFB, tafMedia, tafSR;
    private View rootView;

    public static InfoTeste newInstance(int nr) {

        InfoTeste info = new InfoTeste();
        args = new Bundle();
        args.putInt(NR_AVALIADO, nr);
        info.setArguments(args);
        return info;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db = new DBTAF(activity);
        db.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // COLOCA OS TESTES NA TELA
        rootView = inflater
                .inflate(R.layout.fragmento_testes, container, false);
        Inicia();

        args = getArguments();
        teste = args.getInt(NR_AVALIADO);

        ColocaValores();

        return rootView;
    }

    private void ColocaValores() {
        // COLOCA OS VALORES DO TESTE NA TELA

        //db.open();
        cursor = db.buscaTestes();

        int i = cursor.getCount();
        if ((teste >= 0) && (teste < i)) {
            if (cursor.moveToFirst()) {
                cursor.moveToPosition(teste);

                diaTAF.setText(getResources().getString(R.string.dica_data_TAF,
                        cursor.getString(17)));
                nomeTAF.setText(getResources().getString(
                        R.string.dica_avaliado, cursor.getString(1)));
                idadeTAF.setText(getResources().getString(R.string.dica_idade,
                        cursor.getString(2)));
                generoTAF.setText(getResources().getString(
                        R.string.dica_genero, cursor.getString(3)));

                Double d = Double.parseDouble(cursor.getString(5));

                taf2400.setText(getResources().getString(
                        R.string.resultado_2400,
                        formatador.formatoMinuto(cursor.getString(4)),
                        String.format("%.1f", d)));

                d = Double.parseDouble(cursor.getString(7));

                taf12.setText(getResources().getString(
                        R.string.resultado_nat12, cursor.getString(6),
                        String.format("%.1f", d)));

                d = Double.parseDouble(cursor.getString(9));

                taf75.setText(getResources().getString(
                        R.string.resultado_nat75,
                        formatador.formatoMinuto(cursor.getString(8)),
                        String.format("%.1f", d)));

                d = Double.parseDouble(cursor.getString(11));

                tafSR.setText(getResources().getString(R.string.resultado_sr,
                        formatador.formatoSegundo(cursor.getString(10)),
                        String.format("%.1f", d)));

                d = Double.parseDouble(cursor.getString(13));

                tafABD.setText(getResources().getString(R.string.resultado_abd,
                        cursor.getString(12), String.format("%.1f", d)));

                d = Double.parseDouble(cursor.getString(15));

                tafFB.setText(getResources().getString(R.string.resultado_fb,
                        cursor.getString(14), String.format("%.1f", d)));

                d = Double.parseDouble(cursor.getString(16));

                tafMedia.setText(getResources().getString(
                        R.string.resultado_taf, String.format("%.2f", d)));
            }

        }
        cursor.close();
        db.close();
    }

    private void Inicia() {
        // COLOCA CADA ITEM NA TELA
        diaTAF = (TextView) rootView.findViewById(R.id.tvDiaTAF);
        nomeTAF = (TextView) rootView.findViewById(R.id.tvNomeTAF);
        idadeTAF = (TextView) rootView.findViewById(R.id.tvIdade);
        generoTAF = (TextView) rootView.findViewById(R.id.tvSexo);
        taf2400 = (TextView) rootView.findViewById(R.id.tvNota2400);
        taf12 = (TextView) rootView.findViewById(R.id.tvNota12);
        taf75 = (TextView) rootView.findViewById(R.id.tvNota75);
        tafSR = (TextView) rootView.findViewById(R.id.tvNotaSR);
        tafABD = (TextView) rootView.findViewById(R.id.tvNotaABD);
        tafFB = (TextView) rootView.findViewById(R.id.tvNotaFB);
        tafMedia = (TextView) rootView.findViewById(R.id.tvNotaTAF);
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        db.close();
        super.onPause();
    }

    @Override
    public void onResume() {
        db.open();
        super.onResume();
    }

}

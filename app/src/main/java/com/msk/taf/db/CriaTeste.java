package com.msk.taf.db;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.taf.R;
import com.msk.taf.calc.CalculaIdade;
import com.msk.taf.calc.ConsultaTabela;
import com.msk.taf.calc.EscolheTabela;

import java.util.Calendar;
import java.util.Locale;

public class CriaTeste extends AppCompatActivity implements View.OnClickListener {

    // ELEMENTOS QUE FORNECEM DATA E DIALOGO E IMPORTAM DADOS DO XML
    final static Calendar c = Calendar.getInstance();

    // VARIAVEIS NECESSARIAS PARA CALCULAR NOTA TAF
    static String genero = "M";
    private static TextView dataNascimento;
    private static int dataDia, dataMes, dataAno, mDia, mMes, mAno;
    private SharedPreferences buscaPreferencias = null;
    private Boolean dataNascOuIdade = false;

    // CLASSES QUE REALIZAM ACOES NECESSARIAS PARA CALCULAR NOTA TAF
    private CalculaIdade idadeAvaliado = new CalculaIdade();
    private EscolheTabela tabela = new EscolheTabela();
    private ConsultaTabela tabelaTeste = new ConsultaTabela();

    // BANCO DE DADOS COM OS TESTES
    private DBTAF db = new DBTAF(this);
    private Resources r = null;

    // ELELMENTOS QUE APARECEM NA TELA
    private Button bCalculaNotas, bSalvaTeste;
    private CheckBox cbGenero;
    private LinearLayout dNascAvaliado;
    private TextView tvIdade;
    private EditText et2400, etABD, etFB, etNat12, etNat75, etSR, etIdade;
    private AppCompatEditText etNome;
    private ImageButton alteraData;
    private TextView n12, n2400, n75, nABD, nFB, nSR;
    private int[] Tab2400, TabABD, TabFB, TabNat12, TabNat75, TabSR;
    private String avaliado, resultado;
    private int distNat12, tempo2400, tempoNat75, tempoSR, qtABD, qtFB, idade,
            pronto, status;
    private double nota2400, notaABD, notaFB, notaNat12, notaNat75, notaSR,
            notaTAF;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.cria_taf);
        r = getResources();
        pronto = 0;
        db.open();
        iniciar();
        buscaPreferencias = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        dataNascOuIdade = buscaPreferencias.getBoolean("idade", true);

        if (!dataNascOuIdade) {
            // MOSTRA DATA NASCIMENTO
            dNascAvaliado.setVisibility(View.VISIBLE);
            etIdade.setVisibility(View.GONE);
            tvIdade.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 2f));
            tvIdade.setText(getResources().getString(R.string.dica_idade,
                    idade + ""));

        } else {
            // MOSTRA CAMPO IDADE
            dNascAvaliado.setVisibility(View.GONE);
            etIdade.setVisibility(View.VISIBLE);
            tvIdade.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 1f));
            tvIdade.setText(getResources().getString(R.string.dica_idade_texto));
        }

        usarActionBar();
        DataDeHoje();
        alteraData.setOnClickListener(this);
        cbGenero.setOnClickListener(this);
        bCalculaNotas.setOnClickListener(this);
        bSalvaTeste.setOnClickListener(this);
    }

    private void CalculaNotaTeste() {

        nota2400 = tabelaTeste.notaTeste(Tab2400, tempo2400);
        notaNat12 = tabelaTeste.notaTeste(TabNat12, distNat12);
        notaNat75 = tabelaTeste.notaTeste(TabNat75, tempoNat75);
        notaSR = tabelaTeste.notaTeste(TabSR, tempoSR);
        notaABD = tabelaTeste.notaTeste(TabABD, qtABD);
        notaFB = tabelaTeste.notaTeste(TabFB, qtFB);

        // Confere notas digitadas
        int i = 0;
        if (nota2400 != 0.0D)
            i = 1;
        if (notaNat12 != 0.0D)
            i++;
        if (notaNat75 != 0.0D)
            i++;
        if (notaSR != 0.0D)
            i++;
        if (notaABD != 0.0D)
            i++;
        if (notaFB != 0.0D)
            i++;
        notaTAF = (nota2400 + notaNat12 + notaNat75 + notaSR + notaABD + notaFB);

        if (i != 0)
            notaTAF = notaTAF / i;

        status = 1;
        if ((nota2400 < 3.0D) && (nota2400 != 0.0D))
            status = 0;
        if ((notaNat12 < 3.0D) && (notaNat12 != 0.0D))
            status = 0;
        if ((notaNat75 < 3.0D) && (notaNat75 != 0.0D))
            status = 0;
        if ((notaSR < 3.0D) && (notaSR != 0.0D))
            status = 0;
        if ((notaABD < 3.0D) && (notaABD != 0.0D))
            status = 0;
        if ((notaFB < 3.0D) && (notaFB != 0.0D))
            status = 0;
    }

    private void DataDeHoje() {
        mAno = c.get(Calendar.YEAR);
        mMes = c.get(Calendar.MONTH);
        mDia = c.get(Calendar.DAY_OF_MONTH);
        atualizaDataNasc();
    }

    private void EscolheTabela() {

        Tab2400 = r.getIntArray(tabela.Tab2400(genero, idade));
        TabNat12 = r.getIntArray(tabela.TabNat12(genero, idade));
        TabNat75 = r.getIntArray(tabela.TabNat75(genero, idade));
        TabSR = r.getIntArray(tabela.TabSR(genero, idade));
        TabABD = r.getIntArray(tabela.TabABD(genero, idade));
        TabFB = r.getIntArray(tabela.TabFB(genero, idade));
    }

    private void LimpaCampos() {
        etNome.setText("");
        etIdade.setText("");
        et2400.setText("");
        etNat12.setText("");
        etNat75.setText("");
        etSR.setText("");
        etABD.setText("");
        etFB.setText("");
        n2400.setText("");
        n12.setText("");
        n75.setText("");
        nSR.setText("");
        nABD.setText("");
        nFB.setText("");

        DataDeHoje();

        cbGenero.setChecked(false);
        genero = "M";
        pronto = 0;
    }

    private void MostraNotasTAF() {

        n2400.setText(String.format(Locale.US, "%.1f", nota2400));
        n12.setText(String.format(Locale.US, "%.1f", notaNat12));
        n75.setText(String.format(Locale.US, "%.1f", notaNat75));
        nSR.setText(String.format(Locale.US, "%.1f", notaSR));
        nABD.setText(String.format(Locale.US, "%.1f", notaABD));
        nFB.setText(String.format(Locale.US, "%.1f", notaFB));

        // Colorindo as notas

        if (nota2400 < 5.0D)
            n2400.setTextColor(Color.RED);
        else
            n2400.setTextColor(Color.BLUE);
        if (notaNat12 < 5.0D)
            n12.setTextColor(Color.RED);
        else
            n12.setTextColor(Color.BLUE);
        if (notaNat75 < 5.0D)
            n75.setTextColor(Color.RED);
        else
            n75.setTextColor(Color.BLUE);
        if (notaSR < 5.0D)
            nSR.setTextColor(Color.RED);
        else
            nSR.setTextColor(Color.BLUE);
        if (notaABD < 5.0D)
            nABD.setTextColor(Color.RED);
        else
            nABD.setTextColor(Color.BLUE);
        if (notaFB < 5.0D)
            nFB.setTextColor(Color.RED);
        else
            nFB.setTextColor(Color.BLUE);

    }

    private void ValoresTestes() {

        if (et2400.getText().toString().equals(""))
            tempo2400 = 0;
        else
            tempo2400 = Integer.parseInt(et2400.getText().toString());
        if (etNat12.getText().toString().equals(""))
            distNat12 = 0;
        else
            distNat12 = Integer.parseInt(etNat12.getText().toString());
        if (etNat75.getText().toString().equals(""))
            tempoNat75 = 0;
        else
            tempoNat75 = Integer.parseInt(etNat75.getText().toString());
        if (etSR.getText().toString().equals(""))
            tempoSR = 0;
        else
            tempoSR = Integer.parseInt(etSR.getText().toString());
        if (etABD.getText().toString().equals(""))
            qtABD = 0;
        else
            qtABD = Integer.parseInt(etABD.getText().toString());
        if (etFB.getText().toString().equals(""))
            qtFB = 0;
        else
            qtFB = Integer.parseInt(etFB.getText().toString());
    }

    private void iniciar() {
        dataNascimento = ((TextView) findViewById(R.id.tvdataNasc));
        n2400 = ((TextView) findViewById(R.id.tvNota2400));
        n12 = ((TextView) findViewById(R.id.tvNotaNat12));
        n75 = ((TextView) findViewById(R.id.tvNotaNat75));
        nSR = ((TextView) findViewById(R.id.tvNotaSR));
        nABD = ((TextView) findViewById(R.id.tvNotaABD));
        nFB = ((TextView) findViewById(R.id.tvNotaFB));
        alteraData = ((ImageButton) findViewById(R.id.ibAlteraData));
        bCalculaNotas = ((Button) findViewById(R.id.btCalcNotaTAF));
        bSalvaTeste = ((Button) findViewById(R.id.btSalvaTAF));
        etNome = ((AppCompatEditText) findViewById(R.id.etNomeAvaliado));
        etIdade = ((EditText) findViewById(R.id.etIdadeAvaliado));
        et2400 = ((EditText) findViewById(R.id.etTempo2400));
        etNat12 = ((EditText) findViewById(R.id.etDistNat12));
        etSR = ((EditText) findViewById(R.id.etTempoSR));
        etNat75 = ((EditText) findViewById(R.id.etTempoNat75));
        etABD = ((EditText) findViewById(R.id.etQtABD));
        etFB = ((EditText) findViewById(R.id.etQtFB));
        cbGenero = ((CheckBox) findViewById(R.id.cbGenero));
        dNascAvaliado = (LinearLayout) findViewById(R.id.lDataNascAvaliado);
        tvIdade = (TextView) findViewById(R.id.tvIdadeAvaliado);
    }

    private void atualizaDataNasc() {
        dataNascimento.setText(getResources().getString(R.string.dica_data,
                String.format(Locale.US, "%d", mDia), String.format(Locale.US, "%d", mMes + 1),
                String.format(Locale.US, "%d", mAno)));

        if (!dataNascOuIdade) {
            idade = idadeAvaliado.idade(mDia, mMes, mAno);

            if (idade >= 0) {
                tvIdade.setText(getResources().getString(R.string.dica_idade,
                        String.format(Locale.US, "%d", idade)));
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.dica_idade_errada),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onClick(View paramView) {

        switch (paramView.getId()) {

            case R.id.ibAlteraData:

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                atualizaDataNasc();
                break;
            case R.id.cbGenero:
                if (cbGenero.isChecked())
                    genero = "F";
                else
                    genero = "M";
                break;
            case R.id.btCalcNotaTAF:

                if (dataNascOuIdade) {
                    if (etIdade.getText().toString().equals("")) {
                        idade = 0;
                    } else {
                        idade = Integer.valueOf(etIdade.getText().toString());
                    }
                } else {
                    idade = idadeAvaliado.idade(mDia, mMes, mAno);
                }

                ValoresTestes();
                EscolheTabela();
                CalculaNotaTeste();
                MostraNotasTAF();

                // MENSAGEM DE APTO INAPTO

                if ((notaTAF < 6.0D) || (status != 1)) {
                    resultado = "Inapto";
                    new AlertDialog.Builder(this)
                            .setTitle("NOTA DO TAF")
                            .setMessage(
                                    "Media obtida igual a "
                                            + String.format(Locale.US, "%.2f", notaTAF))
                            .setPositiveButton("INAPTO", null).show();
                } else {
                    resultado = "Apto";
                    new AlertDialog.Builder(this)
                            .setTitle("NOTA DO TAF")
                            .setMessage(
                                    "Media obtida igual a "
                                            + String.format(Locale.US, "%.2f", notaTAF))
                            .setPositiveButton("APTO", null).show();
                }
                pronto = 1;
                break;
            case R.id.btSalvaTAF:
                if (pronto == 0) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.dica_semDados), Toast.LENGTH_LONG)
                            .show();
                } else {
                    dataAno = c.get(Calendar.YEAR);
                    dataMes = c.get(Calendar.MONTH);
                    dataDia = c.get(Calendar.DAY_OF_MONTH);
                    avaliado = etNome.getText().toString();
                    //db.open();

                    if (etNome.getText().toString().equals(""))
                        avaliado = "Sem Nome";
                    else
                        avaliado = etNome.getText().toString();

                    String nomeTAF1 = avaliado;
                    String nomeTAF2 = avaliado;

                    int a = db.contaNomeRepetido(nomeTAF1);
                    int b = 1;

                    if (a != 0) {
                        while (a != 0) {
                            nomeTAF2 = nomeTAF1 + b;
                            a = db.contaNomeRepetido(nomeTAF2);
                            b = b + 1;
                        }
                        avaliado = nomeTAF2;
                    }

                    String str3 = dataDia + "/" + dataMes + "/" + dataAno;
                    db.salvaTeste(avaliado, String.valueOf(idade), genero,
                            String.valueOf(tempo2400), String.valueOf(nota2400),
                            String.valueOf(distNat12), String.valueOf(notaNat12),
                            String.valueOf(tempoNat75), String.valueOf(notaNat75),
                            String.valueOf(tempoSR), String.valueOf(notaSR),
                            String.valueOf(qtABD), String.valueOf(notaABD),
                            String.valueOf(qtFB), String.valueOf(notaFB),
                            String.valueOf(notaTAF), str3, resultado);
                    //db.close();
                    LimpaCampos();
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.dica_salva_teste,
                                    avaliado), Toast.LENGTH_SHORT).show();
                    finish();

                }
                break;
        }
    }

    @SuppressLint("NewApi")
    private void usarActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                db.close();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            dataNascOuIdade = buscaPreferencias.getBoolean("idade", true);

            if (!dataNascOuIdade) {
                // MOSTRA DATA NASCIMENTO
                dNascAvaliado.setVisibility(View.VISIBLE);
                etIdade.setVisibility(View.GONE);
                tvIdade.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LayoutParams.WRAP_CONTENT, 2f));
                tvIdade.setText(getResources().getString(R.string.dica_idade,
                        idade + ""));

            } else {
                // MOSTRA CAMPO IDADE
                dNascAvaliado.setVisibility(View.GONE);
                etIdade.setVisibility(View.VISIBLE);
                tvIdade.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LayoutParams.WRAP_CONTENT, 1f));
                tvIdade.setText(getResources().getString(
                        R.string.dica_idade_texto));
            }
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        setResult(RESULT_OK, null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        db.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        db.open();
        super.onResume();
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new DatePickerDialog(getActivity(), this, mAno, mMes, mDia);
        }

        public void onDateSet(DatePicker view, int ano, int mes, int dia) {
            mAno = ano;
            mMes = mes;
            mDia = dia;
            dataNascimento.setText(getResources().getString(R.string.dica_data,
                    String.format(Locale.US, "%d", mDia),
                    String.format(Locale.US, "%d", mMes + 1),
                    String.format(Locale.US, "%d", mAno)));
        }
    }


}

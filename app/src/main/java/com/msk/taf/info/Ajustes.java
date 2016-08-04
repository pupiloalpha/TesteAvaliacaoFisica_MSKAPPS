package com.msk.taf.info;

import android.Manifest;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.msk.taf.R;
import com.msk.taf.db.DBTAF;
import com.msk.taf.db.ExportaParaExcel;
import com.msk.taf.db.ImportaDeExcel;

public class Ajustes extends PreferenceActivity implements
        OnPreferenceClickListener {

    private final int ESCOLHE_PASTA = 111;
    private final int ESCOLHE_XLS = 222;
    private final int ESCOLHE_BACKUP = 333;
    private Toolbar toolbar;
    private Cursor buscaTAF = null;
    private DBTAF dbTestes = new DBTAF(this);
    private ExportaParaExcel excel = new ExportaParaExcel();
    private ImportaDeExcel planilha = new ImportaDeExcel();
    private Preference backup, restaura, apagatudo, versao, exportar, importar, sobre;
    private CheckBoxPreference idade, autobkup;
    private PreferenceScreen prefs;
    private String chave, nrVersao, arquivo, pastaBackUp;
    private PackageInfo info;
    private String[][] dadosExportar;
    private String[] dadosImportar;
    private int qtTestes = 0;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferencias);

        prefs = getPreferenceScreen();

        Iniciador();
        BuscaValoresBD();

        toolbar.setTitle(getTitle());

        backup.setOnPreferenceClickListener(this);
        restaura.setOnPreferenceClickListener(this);
        apagatudo.setOnPreferenceClickListener(this);
        exportar.setOnPreferenceClickListener(this);
        importar.setOnPreferenceClickListener(this);
        sobre.setOnPreferenceClickListener(this);
        idade.setOnPreferenceClickListener(this);
        autobkup.setOnPreferenceClickListener(this);
    }

    private void BuscaValoresBD() {
        // COLOCA VALORES DE DADOS NUMA MATRIZ
        dbTestes.open();

        buscaTAF = dbTestes.buscaTestes();
        qtTestes = buscaTAF.getCount();

        dadosExportar = new String[qtTestes][19];

        for (int j = 0; j < qtTestes + 1; j++) {
            for (int i = 0; i < 19; i++) {

                if ((j >= 0) && (j < qtTestes)) {
                    if (buscaTAF.moveToFirst()) {
                        buscaTAF.moveToPosition(j);

                        dadosExportar[j][i] = buscaTAF.getString(i);
                    }
                }
            }
        }
        dbTestes.close();

    }

    private void Iniciador() {
        // COLOCA OS ELEMENTOS NA TELA
        backup = (Preference) prefs.findPreference("backup");
        restaura = (Preference) prefs.findPreference("restaura");
        apagatudo = (Preference) prefs.findPreference("apagatudo");
        versao = (Preference) prefs.findPreference("versao");
        exportar = (Preference) prefs.findPreference("excel");
        importar = (Preference) prefs.findPreference("importar");
        sobre = (Preference) prefs.findPreference("desenvolvedor");
        idade = (CheckBoxPreference) prefs.findPreference("idade");
        autobkup = (CheckBoxPreference) prefs.findPreference("autobackup");

        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        nrVersao = info.versionName;

        versao.setSummary(getResources().getString(
                R.string.pref_descricao_versao, nrVersao));


        if (idade.isChecked()) {
            idade.setSummary(R.string.pref_descricao_idade);
        } else {
            idade.setSummary(R.string.pref_descricao_data_nasc);
        }

        if (autobkup.isChecked()) {
            autobkup.setSummary(R.string.pref_descricao_auto_bkup_sim);
        } else {
            autobkup.setSummary(R.string.pref_descricao_auto_bkup_nao);
        }

        SharedPreferences sharedPref = getSharedPreferences("backup", Context.MODE_PRIVATE);
        pastaBackUp = sharedPref.getString("backup", "");

        if (!pastaBackUp.equals("")) {
            backup.setSummary(pastaBackUp);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference itemPref) {

        chave = itemPref.getKey();

        if (chave.equals("autobackup")) {

            if (autobkup.isChecked()) {
                autobkup.setSummary(R.string.pref_descricao_auto_bkup_sim);
            } else {
                autobkup.setSummary(R.string.pref_descricao_auto_bkup_nao);
            }
        }

        if (chave.equals("backup")) {
            // EXPORTA TESTES PARA EXCEL
            if (Build.VERSION.SDK_INT >= 23)
                PermissaoSD(ESCOLHE_PASTA);
            else
                abrePasta(ESCOLHE_PASTA);
        }

        if (chave.equals("restaura")) {
            // EXPORTA TESTES PARA EXCEL
            if (Build.VERSION.SDK_INT >= 23)
                PermissaoSD(ESCOLHE_BACKUP);
            else
                abrePasta(ESCOLHE_BACKUP);
        }

        if (chave.equals("apagatudo")) {
            // APAGA O BANCO DE DADOS
            dbTestes.open();
            dbTestes.deleteAll();
            Toast.makeText(getApplicationContext(),
                    getString(R.string.dica_exclusao_bd), Toast.LENGTH_SHORT)
                    .show();
            dbTestes.close();
        }

        if (chave.equals("excel")) {
            // EXPORTA TESTES PARA EXCEL

            int erro = excel.ArquivoExcel(qtTestes, dadosExportar);

            if (erro == 0) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.dica_exporta_excel),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.erro_exporta_excel),
                        Toast.LENGTH_SHORT).show();
            }
        }

        if (chave.equals("idade")) {
            if (idade.isChecked()) {
                idade.setSummary(R.string.pref_descricao_idade);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.pref_descricao_idade),
                        Toast.LENGTH_SHORT).show();
            } else {
                idade.setSummary(R.string.pref_descricao_data_nasc);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.pref_descricao_data_nasc),
                        Toast.LENGTH_SHORT).show();
            }
        }

        if (chave.equals("importar")) {
            // EXPORTA TESTES PARA EXCEL
            if (Build.VERSION.SDK_INT >= 23)
                PermissaoSD(ESCOLHE_XLS);
            else
                abrePasta(ESCOLHE_XLS);
        }

        setResult(RESULT_OK);

        return false;
    }

    private void PermissaoSD(int nr) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, nr);
            }
        } else {
            abrePasta(nr);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            abrePasta(requestCode);
        } else {
            // permission denied, boo! Disable the
            Toast.makeText(getApplicationContext(), getString(R.string.dica_semDados), Toast.LENGTH_SHORT).show();
        }
    }

    private void SalvaNomeTestes() {

        int u = dadosImportar.length;

        for (int v = 0; v < u; v++) {

            dbTestes.open();

            dbTestes.salvaAvaliadoTeste(dadosImportar[v]);

            dbTestes.close();

        }

    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.ajustes, new LinearLayout(this), false);

        toolbar = (Toolbar) contentView.findViewById(R.id.actionbar_toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FFD50000"));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        ViewGroup contentWrapper = (ViewGroup) contentView
                .findViewById(R.id.conteudo_ajustes);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    public void abrePasta(int nr) {

        if (nr == ESCOLHE_PASTA) {
            Bundle envelope = new Bundle();
            envelope.putString("tipo", "");
            Intent atividade = new Intent(this, EscolhePasta.class);
            atividade.putExtras(envelope);
            startActivityForResult(atividade, nr);
        }

        if (nr == ESCOLHE_XLS) {
            Bundle envelope = new Bundle();
            envelope.putString("tipo", ".xls");
            Intent atividade = new Intent(this, EscolhePasta.class);
            atividade.putExtras(envelope);
            startActivityForResult(atividade, nr);
        }

        if (nr == ESCOLHE_BACKUP) {
            Bundle envelope = new Bundle();
            envelope.putString("tipo", "meus_testes");
            Intent atividade = new Intent(this, EscolhePasta.class);
            atividade.putExtras(envelope);
            startActivityForResult(atividade, nr);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case ESCOLHE_PASTA:

                if (resultCode == RESULT_OK) {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        String path = (String) extras.get(EscolhePasta.CHOSEN_DIRECTORY);

                        try {

                            SharedPreferences sharedPref = getSharedPreferences("backup", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sharedPref.edit();
                            edit.putString("backup", path);
                            edit.commit();

                            // CRIA UMA COPIA DO BANCO DE DADOS NO SD
                            dbTestes.open();
                            dbTestes.copiaBD(path);
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dica_copia_bd), Toast.LENGTH_SHORT)
                                    .show();
                            dbTestes.close();
                            BackupManager android = new BackupManager(getApplicationContext());
                            android.dataChanged();

                            pastaBackUp = sharedPref.getString("backup", "");

                            if (!pastaBackUp.equals("")) {

                                backup.setSummary(pastaBackUp);
                            }

                        } catch (Exception e) {


                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.erro_copia_bd), Toast.LENGTH_SHORT)
                                    .show();
                            Log.e("Seleção de arquivos", "Deu erro!!!", e);
                        }
                    }
                }

                break;
            case ESCOLHE_XLS:
                if (resultCode == RESULT_OK) {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        arquivo = (String) extras.get(EscolhePasta.CHOSEN_DIRECTORY);

                        try {

                            // IMPORTA NOMES DO EXCEL
                            dadosImportar = planilha.ArquivoImportado(arquivo);

                            SalvaNomeTestes();

                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dica_importa_excel),
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {

                            Log.e("Seleção de arquivos", "Deu erro!!!", e);
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.erro_importa_excel),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                break;
            case ESCOLHE_BACKUP:

                if (resultCode == RESULT_OK) {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        String path = (String) extras.get(EscolhePasta.CHOSEN_DIRECTORY);

                        try {

                            // RESTAURA O BANCO DE DADOS
                            dbTestes.open();
                            dbTestes.restauraBD(path);
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dica_restaura_bd), Toast.LENGTH_SHORT)
                                    .show();
                            dbTestes.close();

                        } catch (Exception e) {


                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.erro_restaura_bd), Toast.LENGTH_SHORT)
                                    .show();
                            Log.e("Seleção de arquivos", "Deu erro!!!", e);
                        }
                    }
                }

                break;
        }
    }

}

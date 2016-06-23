package com.msk.taf;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.taf.R;
import com.msk.taf.calc.FormataTexto;
import com.msk.taf.db.DBTAF;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TesteAvaliacaoFisica extends AppCompatActivity implements
        OnItemClickListener, OnItemLongClickListener {

    // ELEMENTOS PARA MONTA A LISTA
    SimpleCursorAdapter adapter;
    DBTAF db = new DBTAF(this);
    Cursor cursor = null;
    FormataTexto formatador = new FormataTexto();
    ActionBar actionBar;
    SharedPreferences buscaPreferencias = null;

    // ITENS DA TELA
    private ListView lista;
    private TextView semtestes, nome, idade, genero, nota;
    private View viewLista;
    private ImageButton fab;
    private LayoutInflater preencheLista;
    // VARIAVEIS UTILIZADAS
    private String nome_avaliado, idade_avaliado, genero_avaliado, carta, pastaBackUp;
    private Double nota_avaliado;
    private Boolean autobkup = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_testes);

        lista = (ListView) findViewById(R.id.lvTestes);
        semtestes = (TextView) findViewById(R.id.tvSemTestes);
        fab = (ImageButton) findViewById(R.id.ibfab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.msk.taf.CRIATESTE"));
            }
        });

        PreferenceManager.getDefaultSharedPreferences(this);
        autobkup = buscaPreferencias.getBoolean("autobackup", true);
        pastaBackUp = buscaPreferencias.getString("backup", "");

        db.open();
        usarActionBar();

        ListaDeTestes();

        lista.setOnItemClickListener(this);
        lista.setOnItemLongClickListener(this);

    }

    @SuppressWarnings("deprecation")
    public void ListaDeTestes() {
        db.open();
        cursor = db.buscaTestes();
        int i = cursor.getCount();
        if (i >= 0) {
            adapter = new SimpleCursorAdapter(this, R.id.lvTestes, cursor,
                    new String[]{DBTAF.COLUNA_AVALIADO}, new int[]{
                    R.id.tvNomeAvaliado, R.id.tvIdadeAvaliado,
                    R.id.tvGeneroAvaliado, R.id.tvNotaAvaliado}) {

                public int getCount() {
                    db.open();
                    int i = db.contaTestes();
                    db.close();
                    return i;
                }

                public long getItemId(int j) {
                    return j;
                }

                public View getView(int posicao, View view, ViewGroup group) {
                    viewLista = view;
                    preencheLista = getLayoutInflater();
                    viewLista = preencheLista.inflate(R.layout.nome_avaliado,
                            null);
                    nome = ((TextView) viewLista
                            .findViewById(R.id.tvNomeAvaliado));
                    idade = ((TextView) viewLista
                            .findViewById(R.id.tvIdadeAvaliado));
                    genero = ((TextView) viewLista
                            .findViewById(R.id.tvGeneroAvaliado));
                    nota = ((TextView) viewLista
                            .findViewById(R.id.tvNotaAvaliado));

                    // db.open();
                    cursor.moveToPosition(posicao);
                    nome_avaliado = cursor.getString(1);
                    nome.setText(nome_avaliado);
                    idade_avaliado = cursor.getString(2);
                    idade.setText(getResources().getString(R.string.dica_idade,
                            idade_avaliado));
                    genero_avaliado = cursor.getString(3);
                    genero.setText(getResources().getString(
                            R.string.dica_genero, genero_avaliado));
                    nota_avaliado = Double.parseDouble(cursor.getString(16));
                    nota.setText(getResources().getString(
                            R.string.dica_nota_TAF,
                            String.format("%.2f", nota_avaliado)));

                    if (cursor.getString(18).equals("Apto"))
                        nota.setTextColor(getResources().getColor(R.color.azul_claro));
                    else
                        nota.setTextColor(getResources().getColor(R.color.vermelho));
                    // db.close();

                    return viewLista;
                }
            };
            lista.setAdapter(adapter);
            lista.setEmptyView(semtestes);
        }
        // db.close();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int posicao,
                            long arg3) {

        Bundle envelope = new Bundle();
        envelope.putInt("posicao", posicao);
        Intent atividade = new Intent("com.msk.taf.MOSTRATESTE");
        atividade.putExtras(envelope);
        startActivityForResult(atividade, 1);

    }

    @SuppressLint("NewApi")
    private void usarActionBar() {
        // Verifica a versao do Android para usar o ActionBar
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        super.onCreateOptionsMenu(paramMenu);
        getMenuInflater().inflate(R.menu.menu_inicio, paramMenu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        super.onOptionsItemSelected(paramMenuItem);
        switch (paramMenuItem.getItemId()) {
            case R.id.ajustes:
                startActivityForResult(new Intent("com.msk.taf.AJUSTES"), 0);
                break;
            case R.id.ajuda:
                startActivity(new Intent("com.msk.taf.AJUDA"));
                break;
            case R.id.sobre:
                startActivity(new Intent("com.msk.taf.SOBRE"));
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ListaDeTestes();
        }
    }

    @Override
    protected void onResume() {
        db.open();
        ListaDeTestes();
        super.onResume();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View v, int posicao,
                                   long arg3) {

        db.open();
        cursor = db.buscaTestes();
        cursor.moveToPosition(posicao);
        nome_avaliado = cursor.getString(1);

        // ENVIAR O TESTE POR MENSAGEM

        Double d = Double.parseDouble(cursor.getString(5));

        Double d2 = Double.parseDouble(cursor.getString(7));

        Double d3 = Double.parseDouble(cursor.getString(9));

        Double d4 = Double.parseDouble(cursor.getString(11));

        Double d5 = Double.parseDouble(cursor.getString(13));

        Double d6 = Double.parseDouble(cursor.getString(15));

        Double d7 = Double.parseDouble(cursor.getString(16));

        carta = getResources().getString(R.string.dica_avaliado,
                cursor.getString(1))
                + "\n"
                + getResources().getString(R.string.dica_idade,
                cursor.getString(2))
                + "\n"
                + getResources().getString(R.string.dica_genero,
                cursor.getString(3))
                + "\n"
                + getResources().getString(R.string.dica_data_TAF,
                cursor.getString(17))
                + "\n"
                + getResources().getString(R.string.dica_2400)
                + " - "
                + getResources().getString(R.string.resultado_2400,
                formatador.formatoMinuto(cursor.getString(4)),
                String.format("%.1f", d))
                + "\n"
                + getResources().getString(R.string.dica_nat12)
                + " - "
                + getResources().getString(R.string.resultado_nat12,
                cursor.getString(6), String.format("%.1f", d2))
                + "\n"
                + getResources().getString(R.string.dica_nat75)
                + " - "
                + getResources().getString(R.string.resultado_nat75,
                formatador.formatoMinuto(cursor.getString(8)),
                String.format("%.1f", d3))
                + "\n"
                + getResources().getString(R.string.dica_SR)
                + " - "
                + getResources().getString(R.string.resultado_sr,
                formatador.formatoSegundo(cursor.getString(10)),
                String.format("%.1f", d4))
                + "\n"
                + getResources().getString(R.string.dica_ABD)
                + " - "
                + getResources().getString(R.string.resultado_abd,
                cursor.getString(12), String.format("%.1f", d5))
                + "\n"
                + getResources().getString(R.string.dica_FB)
                + " - "
                + getResources().getString(R.string.resultado_fb,
                cursor.getString(14), String.format("%.1f", d6))
                + "\n"
                + getResources().getString(R.string.resultado_taf,
                String.format("%.2f", d7));

        cursor.close();
        db.close();

        Dialogo();

        return true;
    }

    private void Dialogo() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.dica_contexto));

        // set dialog message
        alertDialogBuilder.setItems(R.array.menu_contexto,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (id) {
                            case 0: // Edita teste
                                // ABRE CLASSE QUE EDITA O TESTE
                                Bundle correio = new Bundle();
                                correio.putString("avaliado", nome_avaliado);
                                Intent localIntent = new Intent(
                                        "com.msk.taf.EDITATESTE");
                                localIntent.putExtras(correio);
                                startActivityForResult(localIntent, 1);
                                break;
                            case 1:
                                // ENVIA DADOS DO TESTE
                                Intent envelope = new Intent(
                                        "android.intent.action.SEND");
                                envelope.putExtra("android.intent.extra.SUBJECT",
                                        getResources().getString(R.string.app_name));
                                envelope.putExtra("android.intent.extra.TEXT",
                                        carta);
                                envelope.setType("*/*");
                                startActivity(Intent.createChooser(
                                        envelope,
                                        getResources().getString(
                                                R.string.dica_compartilha_teste,
                                                nome_avaliado)));

                                Toast.makeText(
                                        getApplicationContext(),
                                        getResources().getString(
                                                R.string.dica_envia_teste,
                                                nome_avaliado), Toast.LENGTH_SHORT)
                                        .show();

                                break;
                            case 2: // Exclui Teste
                                db.open();
                                db.excluiTeste(nome_avaliado);
                                db.close();

                                Toast.makeText(
                                        getApplicationContext(),
                                        getResources().getString(
                                                R.string.dica_deleta_teste,
                                                nome_avaliado), Toast.LENGTH_SHORT)
                                        .show();
                                break;
                        }
                        setResult(RESULT_OK, null);
                        ListaDeTestes();
                    }

                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {

        if (autobkup == true) {
            db.open();
            db.copiaBD(pastaBackUp);
            db.close();
            BackupManager android = new BackupManager(getApplicationContext());
            android.dataChanged();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        db.close();
        super.onPause();
    }

}

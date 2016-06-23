package com.msk.taf.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.msk.taf.R;
import com.msk.taf.calc.FormataTexto;
import com.msk.taf.db.DBTAF;

@SuppressLint("ValidFragment")
public class PaginadorTestes extends AppCompatActivity implements
        OnClickListener {

    // VARIAVEIS UTILIZADAS
    private static int qtAvaliados;
    private static String nomeAvaliado;
    // CLASSES UTILIZADAS PARA MANIPULAR OS DADOS
    DBTAF db = new DBTAF(this);
    FormataTexto formatador = new FormataTexto();
    // ELEMENTOS PARA CRIAR AS PAGINAS
    Paginas paginasTeste;
    ViewPager mViewPager;
    ActionBar actionBar;
    private Cursor cursor;
    private int nrPagina;
    // ELEMENTOS QUE SERAO EXIBIDOS NAS PAGINAS
    private ImageButton envia, edita, deleta;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle correio = getIntent().getExtras();
        nrPagina = correio.getInt("posicao");

        db.open();
        cursor = db.buscaTestes();
        qtAvaliados = cursor.getCount();
        db.close();

        setContentView(R.layout.pagina_teste);

        // COLOCA O FRAGMENTO NA TELA
        paginasTeste = new Paginas(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(paginasTeste);
        mViewPager.setCurrentItem(nrPagina);
        envia = (ImageButton) findViewById(R.id.ibEnviaTeste);
        edita = (ImageButton) findViewById(R.id.ibEditaTAF);
        deleta = (ImageButton) findViewById(R.id.ibDeletaTAF);

        usarActionBar();

        envia.setOnClickListener(this);
        edita.setOnClickListener(this);
        deleta.setOnClickListener(this);

    }

    @SuppressLint("NewApi")
    private void usarActionBar() {
        // Verifica a versao do Android para usar o ActionBar
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View botoes) {

        cursor.moveToPosition(mViewPager.getCurrentItem());
        nomeAvaliado = cursor.getString(1);

        switch (botoes.getId()) {

            case R.id.ibEnviaTeste:
                // ENVIAR O TESTE POR MENSAGEM

                Double d = Double.parseDouble(cursor.getString(5));

                Double d2 = Double.parseDouble(cursor.getString(7));

                Double d3 = Double.parseDouble(cursor.getString(9));

                Double d4 = Double.parseDouble(cursor.getString(11));

                Double d5 = Double.parseDouble(cursor.getString(13));

                Double d6 = Double.parseDouble(cursor.getString(15));

                Double d7 = Double.parseDouble(cursor.getString(16));

                String carta = getResources().getString(R.string.dica_avaliado,
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

                Intent envelope = new Intent("android.intent.action.SEND");
                envelope.putExtra("android.intent.extra.SUBJECT", getResources()
                        .getString(R.string.app_name));
                envelope.putExtra("android.intent.extra.TEXT", carta);
                envelope.setType("*/*");
                startActivity(Intent.createChooser(envelope, getResources()
                        .getString(R.string.dica_compartilha_teste, nomeAvaliado)));

                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.dica_envia_teste,
                                nomeAvaliado), Toast.LENGTH_SHORT).show();

                break;
            case R.id.ibEditaTAF:
                // ABRE CLASSE QUE EDITA O TESTE
                Bundle correio = new Bundle();
                correio.putString("avaliado", nomeAvaliado);
                Intent localIntent = new Intent("com.msk.taf.EDITATESTE");
                localIntent.putExtras(correio);
                startActivityForResult(localIntent, 1);
                break;
            case R.id.ibDeletaTAF:
                // EXCLUI O TESTE DO BANCO DE DADOS
                db.open();
                db.excluiTeste(nomeAvaliado);
                qtAvaliados = db.contaTestes();
                db.close();

                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.dica_deleta_teste,
                                nomeAvaliado), Toast.LENGTH_SHORT).show();

                if (qtAvaliados > 0) {
                    paginasTeste = new Paginas(getSupportFragmentManager());
                    mViewPager.setAdapter(paginasTeste);
                    mViewPager.setCurrentItem(qtAvaliados);
                } else {
                    setResult(RESULT_OK);
                    finish();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (qtAvaliados != 0) {
                paginasTeste = new Paginas(getSupportFragmentManager());
                mViewPager.setAdapter(paginasTeste);
                mViewPager.setCurrentItem(qtAvaliados);
            } else {
                finish();
            }
        }
    }

    /**
     * CLASSE QUE GERENCIA OS FRAGMENTOS
     **/
    public static class Paginas extends FragmentStatePagerAdapter {
        public Paginas(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return InfoTeste.newInstance(i);
        }

        @Override
        public int getCount() {
            // CONSULTA A QUANTIDADE DE TESTES SALVOS
            return qtAvaliados;
        }

    }

}

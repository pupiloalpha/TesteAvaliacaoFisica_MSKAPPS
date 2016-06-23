package com.msk.taf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class DBTAF {

    // NOMES DAS COLUNAS DA TABELA DE TESTES
    public static final String COLUNA_ID_TESTES = "_id";
    // NOMES DAS COLUNAS DOS DADOS DO AVALIADO
    public static final String COLUNA_AVALIADO = "avaliado";
    public static final String COLUNA_IDADE = "idade";
    public static final String COLUNA_GENERO = "genero";
    // NOMES DAS COLUNA DAS NOTAS
    public static final String COLUNA_NOTA2400 = "nota_2400";
    public static final String COLUNA_NOTAABD = "nota_abd";
    public static final String COLUNA_NOTAFB = "nota_fb";
    public static final String COLUNA_NOTANAT12 = "nota_nat12";
    public static final String COLUNA_NOTANAT75 = "nota_nat75";
    public static final String COLUNA_NOTASR = "nota_sr";
    // NOMES DAS COLUNAS DO TESTE
    public static final String COLUNA_DATA_TAF = "data_taf";
    public static final String COLUNA_NOTA_TAF = "nota_taf";
    public static final String COLUNA_RESULTADO_TAF = "resultado_taf";
    // NOMES DAS COLUNAS COM OS VALORES DOS DADOS
    public static final String COLUNA_DNAT12 = "dist_nat12";
    public static final String COLUNA_QTABD = "qt_abd";
    public static final String COLUNA_QTFB = "qt_fb";
    public static final String COLUNA_T2400 = "tempo_2400";
    public static final String COLUNA_TNAT75 = "tempo_nat75";
    public static final String COLUNA_TSR = "tempo_sr";
    // BANCO DE DADOS
    private static final String BANCO_DE_DADOS = "meus_testes";
    private static final int VERSAO_BD = 1;
    private static final String TAG = "DBAdapter";
    // TABELAS DO BANCO DE DADOS
    private static final String TABELA_TESTES = "notas_taf";
    // METODOS QUE CRIAM AS TABELAS
    private static final String CRIA_TABELA_TESTES = "CREATE TABLE "
            + TABELA_TESTES + " ( " + COLUNA_ID_TESTES // 0
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUNA_AVALIADO // 1
            + " TEXT NOT NULL, " + COLUNA_IDADE + " TEXT NOT NULL, " // 2
            + COLUNA_GENERO + " TEXT NOT NULL, " + COLUNA_T2400 // 3 e 4
            + " TEXT NOT NULL, " + COLUNA_NOTA2400 + " TEXT NOT NULL, " // 5
            + COLUNA_DNAT12 + " TEXT NOT NULL, " + COLUNA_NOTANAT12 // 6 e 7
            + " TEXT NOT NULL, " + COLUNA_TNAT75 + " TEXT NOT NULL, " // 8
            + COLUNA_NOTANAT75 + " TEXT NOT NULL, " + COLUNA_TSR // 9 e 10
            + " TEXT NOT NULL, " + COLUNA_NOTASR + " TEXT NOT NULL, " // 11
            + COLUNA_QTABD + " TEXT NOT NULL, " + COLUNA_NOTAABD // 12 e 13
            + " TEXT NOT NULL, " + COLUNA_QTFB + " TEXT NOT NULL, " // 14
            + COLUNA_NOTAFB + " TEXT NOT NULL, " + COLUNA_NOTA_TAF // 15 e 16
            + " TEXT NOT NULL, " + COLUNA_DATA_TAF + " TEXT NOT NULL, " // 17
            + COLUNA_RESULTADO_TAF + " TEXT NOT NULL );"; // 18
    // VETOR COM AS COLUNAS DAS TABELAS
    static String[] colunas_testes = {COLUNA_ID_TESTES, COLUNA_AVALIADO,
            COLUNA_IDADE, COLUNA_GENERO, COLUNA_T2400, COLUNA_NOTA2400,
            COLUNA_DNAT12, COLUNA_NOTANAT12, COLUNA_TNAT75, COLUNA_NOTANAT75,
            COLUNA_TSR, COLUNA_NOTASR, COLUNA_QTABD, COLUNA_NOTAABD,
            COLUNA_QTFB, COLUNA_NOTAFB, COLUNA_NOTA_TAF, COLUNA_DATA_TAF,
            COLUNA_RESULTADO_TAF};
    private final Context context;
    // AUXILIARES PARA CRIAR O BANCO DE DADOS
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBTAF(Context paramContext) {
        this.context = paramContext;
        this.DBHelper = new DatabaseHelper(this.context);
    }

    // METODO QUE ALTERA INFORMACAO DO TESTE

    public boolean atualizaTeste(String avaliado, String t2400, String n2400,
                                 String dnat12, String n12, String tnat75, String n75, String tsr,
                                 String nsr, String qtadb, String nadb, String qtfb, String nfb,
                                 String nota, String data, String resultado) {
        ContentValues cv = new ContentValues();
        cv.put(COLUNA_T2400, t2400);
        cv.put(COLUNA_NOTA2400, n2400);
        cv.put(COLUNA_DNAT12, dnat12);
        cv.put(COLUNA_NOTANAT12, n12);
        cv.put(COLUNA_TNAT75, tnat75);
        cv.put(COLUNA_NOTANAT75, n75);
        cv.put(COLUNA_TSR, tsr);
        cv.put(COLUNA_NOTASR, nsr);
        cv.put(COLUNA_QTABD, qtadb);
        cv.put(COLUNA_NOTAABD, nadb);
        cv.put(COLUNA_QTFB, qtfb);
        cv.put(COLUNA_NOTAFB, nfb);
        cv.put(COLUNA_NOTA_TAF, nota);
        cv.put(COLUNA_DATA_TAF, data);
        cv.put(COLUNA_RESULTADO_TAF, resultado);
        return this.db.update(TABELA_TESTES, cv, COLUNA_AVALIADO + " = '"
                + avaliado + "' ", null) > 0;
    }

    public boolean alteraAvaliadoTeste(String avaliado, String nome_novo) {
        ContentValues cv = new ContentValues();
        cv.put(COLUNA_AVALIADO, nome_novo);
        return this.db.update(TABELA_TESTES, cv, COLUNA_AVALIADO + " = '"
                + avaliado + "' ", null) > 0;
    }

    public Cursor buscaTesteAvaliado(String nome) {
        nome = nome.replace("'", "''");
        return this.db.query(TABELA_TESTES, colunas_testes, COLUNA_AVALIADO
                + " = '" + nome + "' ", null, null, null, COLUNA_AVALIADO + " ASC");
    }

    public Cursor buscaTestes() {
        return this.db.query(TABELA_TESTES, colunas_testes, null, null, null,
                null, COLUNA_AVALIADO + " ASC");
    }

    public void close() {
        this.DBHelper.close();
        this.db.close();
    }

    public int contaTestes() {
        Cursor cursor = db.query(TABELA_TESTES, colunas_testes, null, null,
                null, null, COLUNA_AVALIADO + " ASC");
        int i = cursor.getCount();
        return i;
    }

    public int contaNomeRepetido(String nome) {
        nome = nome.replace("'", "''");
        Cursor cursor = db.query(TABELA_TESTES, colunas_testes,
                COLUNA_AVALIADO + " = '" + nome + "' ", null, null, null,
                COLUNA_AVALIADO + " ASC");
        int i = cursor.getCount();
        return i;
    }

    public void deleteAll() {
        this.db.delete(TABELA_TESTES, null, null);
    }

    public boolean excluiTeste(String nome) {
        nome = nome.replace("'", "''");
        return this.db.delete(TABELA_TESTES, COLUNA_AVALIADO + " = '" + nome
                + "' ", null) > 0;
    }

    public DBTAF open() throws SQLException {
        this.db = this.DBHelper.getWritableDatabase();
        return this;
    }

    public long salvaAvaliadoTeste(String nome) {
        ContentValues cv = new ContentValues();
        cv.put(COLUNA_AVALIADO, nome);
        cv.put(COLUNA_IDADE, "0");
        cv.put(COLUNA_GENERO, "M");
        cv.put(COLUNA_T2400, "0");
        cv.put(COLUNA_NOTA2400, "0");
        cv.put(COLUNA_DNAT12, "0");
        cv.put(COLUNA_NOTANAT12, "0");
        cv.put(COLUNA_TNAT75, "0");
        cv.put(COLUNA_NOTANAT75, "0");
        cv.put(COLUNA_TSR, "0");
        cv.put(COLUNA_NOTASR, "0");
        cv.put(COLUNA_QTABD, "0");
        cv.put(COLUNA_NOTAABD, "0");
        cv.put(COLUNA_QTFB, "0");
        cv.put(COLUNA_NOTAFB, "0");
        cv.put(COLUNA_NOTA_TAF, "0");
        cv.put(COLUNA_DATA_TAF, "0");
        cv.put(COLUNA_RESULTADO_TAF, "0");
        return this.db.insert(TABELA_TESTES, null, cv);
    }

    // METODO QUE COLOCA OS DADOS NO TESTE NO BANCO DE DADOS
    public long salvaTeste(String avaliado, String idade, String genero,
                           String t2400, String n2400, String dnat12, String n12,
                           String tnat75, String n75, String tsr, String nsr, String qtadb,
                           String nadb, String qtfb, String nfb, String nota, String data,
                           String resultado) {
        ContentValues cv = new ContentValues();
        cv.put(COLUNA_AVALIADO, avaliado);
        cv.put(COLUNA_IDADE, idade);
        cv.put(COLUNA_GENERO, genero);
        cv.put(COLUNA_T2400, t2400);
        cv.put(COLUNA_NOTA2400, n2400);
        cv.put(COLUNA_DNAT12, dnat12);
        cv.put(COLUNA_NOTANAT12, n12);
        cv.put(COLUNA_TNAT75, tnat75);
        cv.put(COLUNA_NOTANAT75, n75);
        cv.put(COLUNA_TSR, tsr);
        cv.put(COLUNA_NOTASR, nsr);
        cv.put(COLUNA_QTABD, qtadb);
        cv.put(COLUNA_NOTAABD, nadb);
        cv.put(COLUNA_QTFB, qtfb);
        cv.put(COLUNA_NOTAFB, nfb);
        cv.put(COLUNA_NOTA_TAF, nota);
        cv.put(COLUNA_DATA_TAF, data);
        cv.put(COLUNA_RESULTADO_TAF, resultado);
        return this.db.insert(TABELA_TESTES, null, cv);
    }

    // ------ CLASSE QUE CRIA O BANCO DE DADOS -----------

    @SuppressWarnings("resource")
    public void copiaBD(String pasta) {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (!pasta.equals(""))
                sd = new File(pasta);

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.msk.taf//databases//meus_testes";
                String backupDBPath = "meus_testes";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    // ----- Procedimentos para fazer BACKUP e RESTARURAR BACKUP

    // ----- METODO QUE SALVA BACK-UP NO SD CARD

    @SuppressWarnings("resource")
    public void restauraBD(String pasta) {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (!pasta.equals(""))
                sd = new File(pasta);

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.msk.taf//databases//meus_testes";
                String backupDBPath = "meus_testes";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(currentDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    // ----- METODO QUE RESTAURA BACK-UP SALVO NO SD CARD

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context paramContext) {
            super(paramContext, BANCO_DE_DADOS, null, VERSAO_BD);
        }

        public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
            paramSQLiteDatabase.execSQL(CRIA_TABELA_TESTES);
            Log.w(TAG, "DB criado com sucesso!");
        }

        public void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
                              int paramInt1, int paramInt2) {
            Log.w(TAG, "Atualizando o Banco de Dados da versao " + paramInt1
                    + " para " + paramInt2 + ", todos os dados serao perdidos!");
            paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS notas_taf");
            paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS avaliados_taf");
            onCreate(paramSQLiteDatabase);
        }
    }

}

package com.msk.taf.db;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExportaParaExcel {

    // ELEMENTOS QUE ESCREVEM O ARQUIVO EM EXCEL
    WritableWorkbook arquivoExcel;
    WritableSheet planilha;
    File cartaoSD;

    // FONTE PARA TEXTO
    WritableFont arial10font;

    // FORMATADORES DE CELULAS COM TEXTO, DATA e NUMEROS
    WritableCellFormat arial10format;

    // TEXTO PARA CELULA
    Label conteudoCelula, nomeColuna;

    private String[][] dados;
    private int qtTestes = 0;
    private int erro = 0;

    public int ArquivoExcel(int qt, String[][] valor) {

        qtTestes = qt;
        dados = new String[qt][19];
        dados = valor;

        cartaoSD = Environment.getExternalStorageDirectory();

        try {

            // -------- CRIANDO UM ARQUIVO E UMA PLANILHA EXCEL ------

            // CRIA O ARQUIVO EXCEL
            arquivoExcel = Workbook.createWorkbook(new File(cartaoSD,
                    "meus_testes.xls"));
            // CRIA UMA PLANILHA
            planilha = arquivoExcel.createSheet("Testes Salvos", 0);

            Log.i("Excel Format", "Criou arquivo excel");

            // -------- FORMATO PARA TEXTO E NUMERO NA PLANILHA ------

            FormataTextoNumero();

            // -------- ESCREVENDO NAS CELULAS DA PLANILHA ------

            EscreveNomeColunas();

            for (int j = 0; j < qtTestes + 1; j++) {
                for (int i = 0; i < 19; i++) {
                    if ((j >= 0) && (j < qtTestes)) {
                        EscreveDadosTeste(j + 1, i, dados[j][i]);
                        Log.i("Matriz Dados", "Info: " + dados[j][i] + " em A["
                                + j + "][" + i + "]");
                    }
                }
            }

            // -------- SALVANDO UM ARQUIVO E UMA PLANILHA EXCEL ------

            // ESCREVE O ARQUIVO
            arquivoExcel.write();

            // FECHA O ARQUIVO
            arquivoExcel.close();

        } catch (IOException e) {
            e.printStackTrace();
            erro = erro + 1;
        } catch (RowsExceededException e) {
            e.printStackTrace();
            erro = erro + 1;
        } catch (WriteException e) {
            e.printStackTrace();
            erro = erro + 1;
        }

        return erro;
    }

    private void FormataTextoNumero() {
        // FORMATO DO TEXTO E DO NUMERO DA CELULA

        // FORMATO DE CELULA FONTE ARIAL 10
        arial10font = new WritableFont(WritableFont.ARIAL, 10);
        arial10format = new WritableCellFormat(arial10font);

    }

    private void EscreveNomeColunas() {
        // ESCREVE O NOME DE CADA COLUNA DA PLANILHA
        try {

            // CRIA UMA CELULA COM TEXTO E FONTE DEFINIDA
            nomeColuna = new Label(0, 0, "id", arial10format);
            planilha.addCell(nomeColuna); // id
            nomeColuna = new Label(1, 0, "NOME", arial10format);
            planilha.addCell(nomeColuna); // nome
            nomeColuna = new Label(2, 0, "IDADE", arial10format);
            planilha.addCell(nomeColuna); // idade
            nomeColuna = new Label(3, 0, "GENERO", arial10format);
            planilha.addCell(nomeColuna); // genero
            nomeColuna = new Label(4, 0, "TEMPO_2400", arial10format);
            planilha.addCell(nomeColuna); // 2400
            nomeColuna = new Label(5, 0, "NOTA_2400", arial10format);
            planilha.addCell(nomeColuna);
            nomeColuna = new Label(6, 0, "DIST_NAT_12MIN", arial10format);
            planilha.addCell(nomeColuna); // nat12
            nomeColuna = new Label(7, 0, "NOTA_NAT_12MIN", arial10format);
            planilha.addCell(nomeColuna); // nat12
            nomeColuna = new Label(8, 0, "TEMPO_NAT_75M", arial10format);
            planilha.addCell(nomeColuna);
            nomeColuna = new Label(9, 0, "NOTA_NAT_75M", arial10format);
            planilha.addCell(nomeColuna); // nat75
            nomeColuna = new Label(10, 0, "TEMPO_SHUTTLE_RUN", arial10format);
            planilha.addCell(nomeColuna);
            nomeColuna = new Label(11, 0, "NOTA_SHUTTLE_RUN", arial10format);
            planilha.addCell(nomeColuna); // sr
            nomeColuna = new Label(12, 0, "QTDE_ABDOMINAL", arial10format);
            planilha.addCell(nomeColuna);
            nomeColuna = new Label(13, 0, "NOTA_ABDOMINAL", arial10format);
            planilha.addCell(nomeColuna); // abd
            nomeColuna = new Label(14, 0, "QTDE_FLEXAO_BRACO", arial10format);
            planilha.addCell(nomeColuna);
            nomeColuna = new Label(15, 0, "NOTA_FLEXAO_BRACO", arial10format);
            planilha.addCell(nomeColuna); // fb
            nomeColuna = new Label(16, 0, "NOTA_TAF", arial10format);
            planilha.addCell(nomeColuna); // notaTAF
            nomeColuna = new Label(17, 0, "DATA_TAF", arial10format);
            planilha.addCell(nomeColuna); // dataTAF
            nomeColuna = new Label(18, 0, "RESULTADO_TAF", arial10format);
            planilha.addCell(nomeColuna); // resultadoTAF
            Log.i("Excel Format", "Escreveu nome colunas");

        } catch (RowsExceededException e) {
            e.printStackTrace();
            erro = erro + 1;
        } catch (WriteException e) {
            e.printStackTrace();
            erro = erro + 1;
        }

    }

    private void EscreveDadosTeste(int nrLinha, int nrColuna, String valor) {
        // METODO QUE ESCREVE OS DADOS DO TESTE NUMA LINHA

        try {

            // -------- ESCREVENDO DADOS DO TESTE NA PLANILHA ------

            conteudoCelula = new Label(nrColuna, nrLinha, valor, arial10format);
            planilha.addCell(conteudoCelula); // id

        } catch (RowsExceededException e) {
            e.printStackTrace();
            erro = erro + 1;
        } catch (WriteException e) {
            e.printStackTrace();
            erro = erro + 1;
        }
    }

}

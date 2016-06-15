package com.msk.taf.db;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ImportaDeExcel {

    // ELEMENTOS QUE LEEM O ARQUIVO EM EXCEL
    Workbook arquivoExcel;
    Sheet planilha;
    File cartaoSD;
    Cell celula;

    private String[] dados;
    private int erro = 0;

    public String[] ArquivoImportado(String arquivo) {

        try {
            // RECEBE O NOME E O CAMINHO DO ARQUIVO EXCEL
            arquivoExcel = Workbook.getWorkbook(new File(arquivo));
            Log.i("Importa excel", "Abriu o arquivo excel");

            // PEGA A PRIMEIRA PLANILHA
            planilha = arquivoExcel.getSheet(0);
            Log.i("Importa Excel", "Buscou a planilha");

            dados = new String[planilha.getRows()];

            // LOOP PARA BUSCAR OS DADOS DAS CELULAS
            for (int i = 0; i < planilha.getRows(); i++) {
                celula = planilha.getCell(0, i);
                dados[i] = celula.getContents();
                Log.i("Importa Excel", "Buscou teste: " + dados[i]);
            }

        } catch (BiffException e) {
            e.printStackTrace();
            erro = erro + 1;
        } catch (IOException e) {
            erro = erro + 1;
            e.printStackTrace();
        }

        if (erro == 0)
            return dados;
        else
            return null;
    }


}

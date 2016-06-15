package com.msk.taf.calc;

import java.util.Calendar;

public class CalculaIdade {
    Calendar c = Calendar.getInstance();
    private int tempoCalculado;
    private int diaData, mesData, anoData, mDia, mMes, mAno, erro;

    public int idade(int dia, int mes, int ano) {

        // Data atual

        diaData = c.get(Calendar.DAY_OF_MONTH);
        mesData = c.get(Calendar.MONTH);
        anoData = c.get(Calendar.YEAR);

        // Mensagem de erro
        erro = 0;

        // Calculo de tempo decorrido
        mDia = diaData - dia;
        mMes = mesData - mes;
        mAno = anoData - ano;

        // Ajustes no tempo decorrido
        if (ano == anoData) { // Ano atual
            if (mes == mesData) { // Mes atual
                if (dia > diaData) { // Dia posterior
                    // Mensagem de erro (Data Futura)
                    erro = erro - 1;
                }
            } else if (mes < mesData) { // Mes anterior
                if (dia > diaData) { // Dia posterior
                    CorrigeDia();
                }
            } else { // Mes posterior
                // Mensagem erro (Data Futura)
                erro = erro - 1;
            }
        } else if (ano < anoData) { // Ano anterior
            if (mes == mesData) { // Mes atual
                if (dia > diaData) { // Dia posterior
                    CorrigeDia();
                }
            } else if (mes < mesData) { // Mes anterior
                if (dia > diaData) { // Dia posterior
                    CorrigeDia();
                }
            } else { // Mes posterior
                if (dia == diaData) { // Dia atual
                    CorrigeMes();
                } else if (dia < diaData) { // Dia anterior
                    CorrigeMes();
                } else { // Dia posterior
                    CorrigeMes();
                    CorrigeDia();
                }
            }
        } else { // Ano futuro
            // Mensagem de erro (Data futura)
            erro = erro - 1;
        }

        // Acrescenta dias de anos bissextos
        int bissextos = (anoData - ano) / 4;
        if (bissextos >= 1) {
            if ((int) Math.IEEEremainder(ano, 4.0D) == 0 && mes < 2)
                bissextos = bissextos + 1;
            if ((int) Math.IEEEremainder(anoData, 4.0D) == 0 && mesData > 1)
                bissextos = bissextos + 1;
            mDia = mDia + bissextos;
        }

        if (erro < 0)
            tempoCalculado = erro;
        else
            tempoCalculado = mAno;

        return tempoCalculado;
    }

    private void CorrigeMes() {

        mMes = 12 + mMes; // Corrige quantidade de meses
        mAno = mAno - 1;
        if (mAno < 0)
            mAno = 0;

    }

    private void CorrigeDia() {

        if (mesData == 1)
            mDia = 28 + mDia; // Corrige quantidade de dias
        else if (mesData == 0 || mesData == 2 || mesData == 4 || mesData == 7
                || mesData == 9 || mesData == 11)
            mDia = 31 + mDia; // Corrige quantidade de dias
        else
            mDia = 30 + mDia; // Corrige quantidade de dias
        mMes = mMes - 1;
        if (mMes < 0)
            CorrigeMes();
    }

}

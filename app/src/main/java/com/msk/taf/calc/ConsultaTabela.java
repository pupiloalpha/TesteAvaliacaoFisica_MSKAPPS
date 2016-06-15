package com.msk.taf.calc;

public class ConsultaTabela {

    public double notaTeste(int[] tabelaTAF, int valor) {

        int i = 1;
        int j = tabelaTAF.length;
        double d1 = 0.0D; // Valor inicial da nota
        double d2 = 0.0D; // Valor final da nota

        if (valor > 0) {

            if (tabelaTAF[0] > tabelaTAF[1]) { // Menor valor maior nota

                if (valor >= tabelaTAF[0]) { // Nota igual a zero
                    j = -1;
                }

                while (j > 0) {

                    if (valor >= tabelaTAF[i]) {
                        d1 = i;
                        j = -1;
                    }
                    j--;
                    i++;
                }

            } else { // Maior valor maior nota

                if (valor <= tabelaTAF[0]) { // Nota igual a zero
                    j = -1;
                }

                while (j > 0) {

                    if (valor <= tabelaTAF[i]) {
                        d1 = i;
                        j = -1;
                    }
                    j--;
                    i++;
                }
            }

            d2 = d1 / 2.0D;

            if (tabelaTAF.length <= 18) // Corrige nota para tabela com 18
                d2 = d2 + 2.0D;
        }

        return d2;
    }
}

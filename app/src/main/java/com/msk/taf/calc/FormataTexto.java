package com.msk.taf.calc;

public class FormataTexto {

    public String formatoMinuto(String valor) {

        String a = valor;

        int b = a.length();

        if (b == 1)
            a = "000" + a;
        if (b == 2)
            a = "00" + a;
        if (b == 3)
            a = "0" + a;

        String min = a.substring(0, 2);

        String seg = a.substring(2);

        String result = min + ":" + seg;

        return result;
    }

    public String formatoSegundo(String valor) {

        String a = valor;

        int b = a.length();

        if (b == 1)
            a = "000" + a;
        if (b == 2)
            a = "00" + a;
        if (b == 3)
            a = "0" + a;

        String seg = a.substring(0, 2);

        String centseg = a.substring(2);

        String result = seg + "\'" + centseg + "\"";

        return result;
    }

}

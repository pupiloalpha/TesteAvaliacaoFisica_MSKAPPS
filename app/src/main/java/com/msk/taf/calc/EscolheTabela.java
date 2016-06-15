package com.msk.taf.calc;

import com.msk.taf.R;

public class EscolheTabela {

    private int b;

    public int Tab2400(String sexo, int idade) {

        if (sexo == "M") {
            if (idade >= 50) {
                b = R.array.Tab2400_50M;
            } else if (idade > 39) {
                b = R.array.Tab2400_49M;
            } else if (idade > 29) {
                b = R.array.Tab2400_39M;
            } else if (idade > 19) {
                b = R.array.Tab2400_29M;
            } else
                b = R.array.Tab2400_19M;

        } else {
            if (idade <= 19) {
                b = R.array.Tab2400_19F;
            } else if (idade <= 29) {
                b = R.array.Tab2400_29F;
            } else if (idade <= 39) {
                b = R.array.Tab2400_39F;
            } else if (idade <= 49) {
                b = R.array.Tab2400_49F;
            } else
                b = R.array.Tab2400_50F;
        }

        return b;
    }

    public int TabABD(String sexo, int idade) {

        if (sexo == "M") {
            if (idade >= 50) {
                b = R.array.TabABD_50M;
            } else if (idade > 39) {
                b = R.array.TabABD_49M;
            } else if (idade > 29) {
                b = R.array.TabABD_39M;
            } else if (idade > 19) {
                b = R.array.TabABD_29M;
            } else
                b = R.array.TabABD_19M;

        } else {
            if (idade <= 19) {
                b = R.array.TabABD_19F;
            } else if (idade <= 29) {
                b = R.array.TabABD_29F;
            } else if (idade <= 39) {
                b = R.array.TabABD_39F;
            } else if (idade <= 49) {
                b = R.array.TabABD_49F;
            } else
                b = R.array.TabABD_50F;
        }

        return b;
    }

    public int TabFB(String sexo, int idade) {

        if (sexo == "M") {
            if (idade >= 50) {
                b = R.array.TabFB_50M;
            } else if (idade > 39) {
                b = R.array.TabFB_49M;
            } else if (idade > 29) {
                b = R.array.TabFB_39M;
            } else if (idade > 19) {
                b = R.array.TabFB_29M;
            } else
                b = R.array.TabFB_19M;

        } else {
            if (idade <= 19) {
                b = R.array.TabFB_19F;
            } else if (idade <= 29) {
                b = R.array.TabFB_29F;
            } else if (idade <= 39) {
                b = R.array.TabFB_39F;
            } else if (idade <= 49) {
                b = R.array.TabFB_49F;
            } else
                b = R.array.TabFB_50F;
        }

        return b;
    }

    public int TabNat12(String sexo, int idade) {

        if (sexo == "M") {
            if (idade >= 50) {
                b = R.array.TabNat12_50M;
            } else if (idade > 39) {
                b = R.array.TabNat12_49M;
            } else if (idade > 29) {
                b = R.array.TabNat12_39M;
            } else if (idade > 19) {
                b = R.array.TabNat12_29M;
            } else
                b = R.array.TabNat12_19M;

        } else {
            if (idade <= 19) {
                b = R.array.TabNat12_19F;
            } else if (idade <= 29) {
                b = R.array.TabNat12_29F;
            } else if (idade <= 39) {
                b = R.array.TabNat12_39F;
            } else if (idade <= 49) {
                b = R.array.TabNat12_49F;
            } else
                b = R.array.TabNat12_50F;
        }
        return b;
    }

    public int TabNat75(String sexo, int idade) {

        if (sexo == "M") {
            if (idade >= 50) {
                b = R.array.TabNat75_50M;
            } else if (idade > 39) {
                b = R.array.TabNat75_49M;
            } else if (idade > 29) {
                b = R.array.TabNat75_39M;
            } else if (idade > 19) {
                b = R.array.TabNat75_29M;
            } else
                b = R.array.TabNat75_19M;

        } else {
            if (idade <= 19) {
                b = R.array.TabNat75_19F;
            } else if (idade <= 29) {
                b = R.array.TabNat75_29F;
            } else if (idade <= 39) {
                b = R.array.TabNat75_39F;
            } else if (idade <= 49) {
                b = R.array.TabNat75_49F;
            } else
                b = R.array.TabNat75_50F;
        }

        return b;
    }

    public int TabSR(String sexo, int idade) {

        if (sexo == "M") {
            if (idade >= 50) {
                b = R.array.TabSR_50M;
            } else if (idade > 39) {
                b = R.array.TabSR_49M;
            } else if (idade > 29) {
                b = R.array.TabSR_39M;
            } else if (idade > 19) {
                b = R.array.TabSR_29M;
            } else
                b = R.array.TabSR_19M;

        } else {
            if (idade <= 19) {
                b = R.array.TabSR_19F;
            } else if (idade <= 29) {
                b = R.array.TabSR_29F;
            } else if (idade <= 39) {
                b = R.array.TabSR_39F;
            } else if (idade <= 49) {
                b = R.array.TabSR_49F;
            } else
                b = R.array.TabSR_50F;
        }

        return b;
    }

}

package com.florian.colorharmony_theory_stragety;



public class HarmonyEvent {

    public final int R;
    public final int G;
    public final int B;

    public final String HEX;

    public HarmonyEvent(int r, int g, int b, String hex){
        this.R = r;
        this.G = g;
        this.B = b;

        this.HEX = hex;
    }
}

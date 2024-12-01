package org.example.java_fx_szoftverleltar;

public class Gep {
    private int id;
    private String hely;
    private String tipus;
    private String ipcim;

    public Gep(int id, String hely, String tipus, String ipcim) {
        this.id = id;
        this.hely = hely;
        this.tipus = tipus;
        this.ipcim = ipcim;
    }

    public int getId() {
        return id;
    }

    public String getHely() {
        return hely;
    }

    public String getTipus() {
        return tipus;
    }

    public String getIpcim() {
        return ipcim;
    }
}


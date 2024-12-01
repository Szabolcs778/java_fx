
package org.example.java_fx_szoftverleltar;

public class TelepitesView {
    private int gepId;
    private String gepHely;
    private String gepTipus;
    private String gepIpcim;
    private int szoftverId;
    private String szoftverNev;
    private String szoftverKategoria;
    private String telepitesVerzio;
    private String telepitesDatum;

    public TelepitesView(int gepId, String gepHely, String gepTipus, String gepIpcim,
                         int szoftverId, String szoftverNev, String szoftverKategoria,
                         String telepitesVerzio, String telepitesDatum) {
        this.gepId = gepId;
        this.gepHely = gepHely;
        this.gepTipus = gepTipus;
        this.gepIpcim = gepIpcim;
        this.szoftverId = szoftverId;
        this.szoftverNev = szoftverNev;
        this.szoftverKategoria = szoftverKategoria;
        this.telepitesVerzio = telepitesVerzio;
        this.telepitesDatum = telepitesDatum;
    }

    // Getterek
    public int getGepId() { return gepId; }
    public String getGepHely() { return gepHely; }
    public String getGepTipus() { return gepTipus; }
    public String getGepIpcim() { return gepIpcim; }
    public int getSzoftverId() { return szoftverId; }
    public String getSzoftverNev() { return szoftverNev; }
    public String getSzoftverKategoria() { return szoftverKategoria; }
    public String getTelepitesVerzio() { return telepitesVerzio; }
    public String getTelepitesDatum() { return telepitesDatum; }
}


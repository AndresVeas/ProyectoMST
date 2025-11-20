package model;

public class Vertice {
    public String label;        // label (e.g. 'A')
    public boolean wasVisited;
    // -------------------------------------------------------------
    public Vertice (String lab)   // constructor
    {
        label = lab;
        wasVisited = false;
    }
}

package model;

public class Edge {
    private final int u;
    private final int v;
    private final int weight;

    public Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public int getU() { return u; }
    public int getV() { return v; }
    public int getWeight() { return weight; }

    // Igualdad ignorando orden (u,v) y (v,u)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge e = (Edge) o;
        return weight == e.weight &&
               ((u == e.u && v == e.v) || (u == e.v && v == e.u));
    }

    @Override
    public int hashCode() {
        int a = Math.min(u, v);
        int b = Math.max(u, v);
        int result = 31 * a + b;
        result = 31 * result + weight;
        return result;
    }
}

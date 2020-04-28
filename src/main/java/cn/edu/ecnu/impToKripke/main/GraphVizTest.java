package cn.edu.ecnu.impToKripke.main;

public class GraphVizTest {
    public static void main(String[] args) {
        GraphViz gViz = new GraphViz("C:\\Users\\JH\\Desktop\\test", "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe");
//        gViz.start_graph();
//        gViz.node("A[label = \"aa\"]");
//        gViz.node("B[label = \"bb\"]");
//        gViz.node("C[label = \"cc\"]");
//        gViz.node("D[label = \"dd\"]");
//        gViz.node("E[label = \"ee\"]");
//        gViz.addln("A->C");
//        gViz.addln("C->B");
//        gViz.addln("B->D");
//        gViz.addln("C->E");
        gViz.graph();
        try {
            gViz.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//C:\Program Files (x86)\Graphviz2.38\

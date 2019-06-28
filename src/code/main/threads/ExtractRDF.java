/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.main.threads;

import code.main.Triple;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

/**
 *
 * @author lucas
 */
public class ExtractRDF implements Runnable {

    private FileInputStream fis;
    private NxParser nx;
    private List<Triple> data = new ArrayList<>();

    public ExtractRDF(FileInputStream fis, NxParser nx, List<Triple> data) {

        this.fis = fis;
        this.nx = nx;
        this.data = data;
    }

    @Override
    public void run() {

        int i = 0;
        for (Node[] node : nx) {
            Triple rdf = new Triple();
            rdf.setSubject(node[0].toString());
            rdf.setPredicate(node[1].toString());
            rdf.setObject(node[2].toString());
            data.add(rdf);
            i++;
        }
        System.out.println("NUMERO DE TRIPLAS = " + i);

    }

}

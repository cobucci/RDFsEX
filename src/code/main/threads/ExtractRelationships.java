/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.main.threads;

import code.main.Entity;
import code.main.Triple;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class ExtractRelationships implements Runnable {

    private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private List<Triple> extractedData;
    private List<Entity> entities;
    private List<String> relationships;
    private int begin;
    private int end;

    public ExtractRelationships(List<Triple> extractedData, List<Entity> entities, List<String> relationships, int begin, int end) {

        this.extractedData = extractedData;
        this.entities = entities;
        this.relationships = relationships;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public void run() {

        for (int i = begin; i < end; i++) {

            //verificar se sao entidades de alguem
            if (verifyExistanceOfInstance(extractedData.get(i).getSubject(), entities)
                    && verifyExistanceOfInstance(extractedData.get(i).getObject(), entities)
                    && !isObjectLiteral(extractedData.get(i).getObject())
                    && !isCompositeAtribute(extractedData.get(i).getObject())
                    && !hasInvalidPredicateFromBSBM(extractedData.get(i).getPredicate())) {

                synchronized (relationships) {
                    if (!verifyExistanceOfRelationship(extractedData.get(i).getPredicate(), relationships)) {

                        relationships.add(extractedData.get(i).getPredicate());

                    }
                }

            }

        }

        try {
            writeInFileRelationships(relationships);
        } catch (IOException ex) {
            Logger.getLogger(ExtractRelationships.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // MIN E MAX RELATIONAMENTOS 5
    private void writeInFileRelationships(List<String> predicados) throws IOException {

        File f = new File("Relationships.txt");
        FileWriter fileWriter = new FileWriter(f, true);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        try (PrintWriter printWriter = new PrintWriter(buffer)) {
            for (String p : predicados) {
                printWriter.println(p);
            }
        }

        fileWriter.close();
    }

    // MIN E MAX RELATIONAMENTOS 3
    private boolean verifyExistanceOfInstance(String instance, List<Entity> entities) {

        for (Entity e : entities) {

            for (int i = 0; i < e.instances.size(); i++) {

                if (e.instances.get(i).getInstanceName().equals(instance)) {
                    return true;
                }

            }
        }
        return false;
    }

    // MIN E MAX RELATIONAMENTOS 4
    private boolean verifyExistanceOfRelationship(String rela, List<String> relationships) {

        for (String r : relationships) {

            if (r.equals(rela)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasInvalidPredicateFromBSBM(String predicate) {
        if (predicate.equals("http://purl.org/dc/elements/1.1/publisher")
                || predicate.equals("http://xmlns.com/foaf/0.1/homepage")
                || predicate.equals("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/country")
                || predicate.equals("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/offerWebpage")) {
            return true;
        }
        return false;
    }

    private boolean isCompositeAtribute(String object) {
        if (object.charAt(0) == '_' && object.charAt(1) == ':') {
            return true;
        }
        return false;
    }

    private boolean isObjectLiteral(String object) {
        if (object.startsWith("http://")) {
            return false;
        }
        return true;
    }

}

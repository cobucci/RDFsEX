/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.main.threads;

import code.main.Entity;
import code.main.Triple;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucas
 */
public class NewDataWithInstancesAndRelationships implements Runnable {

    private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private List<Triple> extractedData;
    private List<Triple> newData;
    private List<Entity> entities;
    private List<String> relationships;
    private int begin;
    private int end;

    public NewDataWithInstancesAndRelationships(List<Triple> extractedData, List<Entity> entities, List<Triple> newData, List<String> relationships, int begin, int end) {

        this.extractedData = extractedData;
        this.entities = entities;
        this.newData = newData;
        this.relationships = relationships;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public void run() {

        for (int i = begin; i < end; i++) {

            if (verifyExistanceOfInstance(extractedData.get(i).getSubject(), entities)
                    && verifyExistanceOfInstance(extractedData.get(i).getObject(), entities)
                    && verifyExistanceOfRelationship(extractedData.get(i).getPredicate(), relationships)) {

                synchronized (newData) {
                    Triple rdf = new Triple();
                    rdf.setSubject(extractedData.get(i).getSubject());
                    rdf.setPredicate(extractedData.get(i).getPredicate());
                    rdf.setObject(extractedData.get(i).getObject());
                    newData.add(rdf);
                }

            }
        }

    }

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

}

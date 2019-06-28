/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.main.threads;

import code.main.Entity;
import code.main.Instance;
import code.main.Triple;

import java.util.List;

/**
 *
 * @author lucas
 */
public class ExtractInstances implements Runnable {

    private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private List<Triple> extractedData;
    private List<Entity> entities;
    private int begin;
    private int end;
    
    public ExtractInstances(List<Triple> extractedData, List<Entity> entities, int begin, int end) {
        this.extractedData = extractedData;
        this.entities = entities;
        this.begin = begin;
        this.end = end;

    }

    @Override
    public void run() {

      
            for (int i = begin; i < end; i++) {

            if (extractedData.get(i).getPredicate().equals(RDF_TYPE)) {

                if (!isObjectLiteral(extractedData.get(i).getObject())) {

                    synchronized(entities){
                        adicionarInstancias(extractedData.get(i).getObject(), extractedData.get(i).getSubject());
                    }
                    

                }
            }

        }
        

    }


    public void adicionarInstancias(String entityName, String instanceName) {

        for (int i = 0; i < entities.size(); i++) {

            if (entities.get(i).getName().equals(entityName)) {

                for (int j = 0; j < entities.get(i).instances.size(); j++) {

                    if (entities.get(i).getInstances().get(j).getInstanceName().equals(instanceName)) {

                        return;
                    }

                }

            }

        }
        
      // synchronized (this) {
            Instance i = new Instance();
            i.setInstanceName(instanceName);
            for (Entity e : entities) {
                if (e.getName().equals(entityName)) {
                    e.instances.add(i);
                }
            }
       // }

    }

    //COMECAM AS FUNCOES DE SUPORTE
    private boolean isObjectLiteral(String object) {
        if (object.startsWith("http://")) {
            return false;
        }
        return true;
    }

}

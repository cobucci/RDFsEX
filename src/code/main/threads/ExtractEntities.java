/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.main.threads;

import code.main.Entity;
import code.main.Triple;
import java.util.List;

/**
 *
 * @author lucas
 */
public class ExtractEntities implements Runnable {

    private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    private List<Triple> extractedData;
    private List<Entity> entities;
    private int begin;
    private int end;
    
    public ExtractEntities(List<Triple> extractedData, List<Entity> entities, int begin, int end) {
        this.extractedData = extractedData;
        this.entities = entities;
        this.begin = begin;
        this.end = end;
    
    }
    
    
    @Override
    public void run() {

        extract();

    }

    private void extract() {  
        for (int i = begin; i < end; i++) {
        
            if (extractedData.get(i).getPredicate().equals(RDF_TYPE)) {

                if (!isObjectLiteral(extractedData.get(i).getObject())) {

                    if (!isObjectInstanceWithRDFTypeLine(extractedData, extractedData.get(i).getObject())) {
                        
                        synchronized(entities){
                            adicionarEntidade(extractedData.get(i).getObject());
                        }
                        

                    }

                }

            }

        }
    }
    
    

    
    public void adicionarEntidade(String nome) {

            for (int i = 0; i < entities.size(); i++) {
              
                if (entities.get(i).getName().equals(nome)) {
                    
                    return;
                }

            }
      
            Entity entity = new Entity();
            entity.setName(nome);
            entities.add(entity);

       
    }
    

    //COMECAM AS FUNCOES DE SUPORTE
    private boolean isObjectLiteral(String object) {
        if (object.startsWith("http://")) {
            return false;
        }
        return true;
    }

    private boolean isRDFTypeLine(String predicate) {
        if (predicate.equals(RDF_TYPE)) {
            return true;
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

    private boolean isObjectInstanceWithRDFTypeLine(List<Triple> data, String object) {
        for (int i = 0; i < data.size(); i++) {
            if (object.equals(data.get(i).getSubject())) {
                return true;
            }
        }
        return false;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.main.threads;

import code.main.Attribute;
import code.main.Entity;
import code.main.Triple;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cobucci
 */
public class Coherence implements Runnable{
    
    private List<Triple> extractedData;
    private List<Entity> entities;
    
    public Coherence(List<Triple> extractedData, List<Entity> entities) {
        this.extractedData = extractedData;
        this.entities = entities;
    }
    
    
    
    public void calcCoherence(){
        
        
         float coherence = 0;
         float denominadorWt = 0;
         for(Entity e : entities){
            
            denominadorWt += (float)e.instances.size() + (float)e.attributes.size();
        }
     
        for(Entity e : entities){
           
            int occurrences=0;
            float coverage=0;
          
            for(int i=0 ; i<e.attributes.size() ; i++){
                
                List<String> instances = new ArrayList<>();
                
                for(int j=0 ; j<extractedData.size() ; j++){
                    
                    if(e.attributes.get(i).getName().equals(extractedData.get(j).getPredicate())){
                        
                        //verificar se é a instancia é da entidade
                        if(verifyInstanceBelongsEntity(entities, e.getName(), extractedData.get(j).getSubject())){
                            
                            if(!verifyExistanceOfInstance(instances, extractedData.get(j).getSubject())){
                                instances.add(extractedData.get(j).getSubject());
                                occurrences++;
                                //System.out.println("entrei");
                            }
                        }
                        
                    }
                }
                      
            }
            
            if(e.instances.size() != 0 && e.attributes.size() != 0){
                coverage = (float)occurrences / ((float)e.instances.size() * (float)e.attributes.size());
                float wt = ((float)e.instances.size() + (float)e.attributes.size()) / denominadorWt;
                System.out.println("Entidade = " + e.getName() + "\nCoverage = " + coverage + "\nWT = " + wt + "\n\n");
                coherence += wt * coverage;
            }
            
   
        }
       
        System.out.println("COHERENCE OF THE DATASET = " + coherence);
       
    }
    
    
    
     private boolean verifyExistanceOfInstance(List<String> instances, String subject) {

        for(int i=0 ; i<instances.size() ; i++){
            
            if(instances.get(i).equals(subject))
               return true;
        }
        return false;
    }
     
    private boolean verifyInstanceBelongsEntity(List<Entity> entities, String entity, String instance){
        
        for(int i=0 ; i<entities.size() ; i++){
            
            if(entities.get(i).getName().equals(entity)){
                for(int j=0 ; j<entities.get(i).instances.size() ; j++){
                    
                    if(entities.get(i).getInstances().get(j).getInstanceName().equals(instance)){
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public void run() {
        calcCoherence();
    }
    
}
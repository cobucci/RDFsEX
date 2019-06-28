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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class MinMaxRelationships implements Runnable {

    private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private List<Triple> newData;
    private List<Entity> entities;
    private List<String> relationships;

    public MinMaxRelationships(List<Triple> newData, List<Entity> entities, List<String> relationships) {
        this.newData = newData;
        this.entities = entities;
        this.relationships = relationships;

    }

    @Override
    public void run() {
        try {
            minMaxRelationships();
        } catch (IOException ex) {
            Logger.getLogger(MinMaxRelationships.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void minMaxRelationships() throws IOException {

        int quantRelationships = 0;
        int cardAbaixoDe1 = 0;
        int validacao = 0;
        double mediaMin = 0, mediaMax = 0;
        for (String r : relationships) {

            for (int i = 0; i < entities.size(); i++) {

                List<Triple> smallData = new ArrayList<>();

                for (int p = 0; p < newData.size(); p++) { //triplas com sujeito = i && predicado = r

                    if (r.equals(newData.get(p).getPredicate())
                            && verifyExistanceInstance(entities, entities.get(i).getName(), newData.get(p).getSubject())) { //funcao da Instances... confiro se existe a instancia na entidade

                        Triple rdf = new Triple();
                        rdf.setSubject(newData.get(p).getSubject());
                        rdf.setPredicate(newData.get(p).getPredicate());
                        rdf.setObject(newData.get(p).getObject());
                        smallData.add(rdf);
                        /*
                            Essa parte eh pq estou calculando o min e max de cada relacionamento. Eh um min e um max de relacionamento por vez, ou seja,
                            vou pegar o relacionamento "X" e vou ver todos os relaciomentos dele, e para isso preciso salvar em algum lugar essas informacoes,
                            entao todos do smallData vao ter o relaciomento X. Fiz isso para nao ter que percorrer todo o extractData
                        
                         */
                    }
                }

                for (int j = 0; j < entities.size(); j++) {

                    List<String> instancesAlreadyCounted = new ArrayList<>();
                    int quantityOfOcorrence[] = new int[entities.get(i).getInstances().size()];
                    inicializequantityOfOccorrence(quantityOfOcorrence, entities.get(i).getInstances().size());

                    List<String> instancesAlreadyCounted2 = new ArrayList<>();
                    int quantityOfOcorrence2[] = new int[entities.get(j).getInstances().size()];
                    inicializequantityOfOccorrence(quantityOfOcorrence2, entities.get(j).getInstances().size());

                    String entityAnalyzed = entities.get(i).getName();
                    String entityCompared = entities.get(j).getName();

                    for (int w = 0; w < smallData.size(); w++) {

                        if (verifyExistanceInstance(entities, entityCompared, smallData.get(w).getObject())) {

                            validacao = 1;
                            //SUJEITO
                            int position = findPositionOfInstance(entities, smallData.get(w).getSubject(), entityAnalyzed); //MAX
                            if (position != -1) {
                                quantityOfOcorrence[position] += 1;
                            }
                            if (!verifyInstanceAlreadyCounted(instancesAlreadyCounted, smallData.get(w).getSubject())) { //MIN

                                instancesAlreadyCounted.add(smallData.get(w).getSubject());
                            }

                            //PREDICADO
                            int position2 = findPositionOfInstance(entities, smallData.get(w).getObject(), entityCompared);
                            if (position2 != -1) {
                                quantityOfOcorrence2[position2] += 1;
                            }
                            if (!verifyInstanceAlreadyCounted(instancesAlreadyCounted2, smallData.get(w).getObject())) {
                                instancesAlreadyCounted2.add(smallData.get(w).getObject());
                            }
                        }
                    }

                    if (validacao == 1) {

                        int max = maximumOccurrence(quantityOfOcorrence, entities.get(i).getInstances().size());
                        double min = (double) instancesAlreadyCounted.size() / (double) entities.get(i).getInstances().size();
                        writeInFileMinMaxRelationships(entityAnalyzed, r, entityCompared, min, max, instancesAlreadyCounted.size(), entities.get(i).getInstances().size());

                        int max2 = maximumOccurrence(quantityOfOcorrence2, entities.get(j).getInstances().size());
                        double min2 = (double) instancesAlreadyCounted2.size() / (double) entities.get(j).getInstances().size();
                        writeInFileMinMaxRelationships(entityCompared, r, entityAnalyzed, min2, max2, instancesAlreadyCounted2.size(), entities.get(j).getInstances().size());

                        quantRelationships += 2;
                        if (min < 1) {
                            cardAbaixoDe1++;
                        }
                        if (min2 < 1) {
                            cardAbaixoDe1++;
                        }

                        mediaMin += min;
                        mediaMax += (double) max;

                        validacao = 0;
                    }

                }
            }
        }
        relationships.clear();
        newData.clear();
        mediaMin = mediaMin / quantRelationships;
        mediaMax = mediaMax / quantRelationships;
        writeInFileStatisticalData(quantRelationships, cardAbaixoDe1, mediaMin, mediaMax);
    }

    private void writeInFileStatisticalData(int quantRelationships, int cardAbaixoDe1, double mediaMin, double mediaMax) throws IOException {

        float porcentagem = (100 * cardAbaixoDe1) / quantRelationships;
        File f = new File("StatisticalData.txt");
        FileWriter fileWriter = new FileWriter(f, true);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        try (PrintWriter printWriter = new PrintWriter(buffer)) {

            printWriter.printf("TOTAL DE RELACIONAMENTOS = %d\nTOTAL DE RELACIONAMENTOS COM CARDINALIDADE MINIMA MENOR QUE 1 = %d\n%.3f %% dos relacionamentos tem cardinalidade mínima inferior a 1\n", quantRelationships, cardAbaixoDe1, porcentagem);
            printWriter.printf("AVG MIN RELACIONAMENTO = %.3f\nAVG MAX RELACIONAMENTO = %.3f\n\n\n", mediaMin, mediaMax);

        }

        fileWriter.close();
    }

    // MIN E MAX RELATIONAMENTOS 2
    private List<String> extractRelationships(List<Triple> extractedData, double value) throws IOException {

        List<String> relationships = new ArrayList<>();

        for (int i = 0; i < extractedData.size() * value; i++) {

            //verificar se sao entidades de alguem
            if (verifyExistanceOfInstance(extractedData.get(i).getSubject(), entities)
                    && verifyExistanceOfInstance(extractedData.get(i).getObject(), entities)
                    && !isObjectLiteral(extractedData.get(i).getObject())
                    && !isCompositeAtribute(extractedData.get(i).getObject())
                    && !hasInvalidPredicateFromBSBM(extractedData.get(i).getPredicate())) {

                if (!verifyExistanceOfRelationship(extractedData.get(i).getPredicate(), relationships)) {

                    relationships.add(extractedData.get(i).getPredicate());
                }
            }
        }

        writeInFileRelationships(relationships);
        return relationships;
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

    // MIN E MAX RELATIONAMENTOS 6
    private void writeInFileMinMaxRelationships(String r1, String rel, String r2, double min, int max, int instancesAlreadCounted, int totalInstances) throws IOException {

        File f = new File("MinMaxRelationships.txt");
        FileWriter fileWriter = new FileWriter(f, true);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        try (PrintWriter printWriter = new PrintWriter(buffer)) {

            printWriter.printf("<%s>\n<%s>\n<%s>\n", r1, rel, r2);
            printWriter.printf("(%.3f , %d)\n\n\n", min, max);
        }

        fileWriter.close();
    }

    private int findPositionOfInstance(List<Entity> entities, String instance, String entityName) {

        for (Entity e : entities) {

            for (int i = 0; i < e.instances.size(); i++) {

                if (e.instances.get(i).getInstanceName().equals(instance)
                        && e.getName().equals(entityName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean verifyExistanceInstance(List<Entity> e, String entityName, String instanceName) {

        for (int i = 0; i < e.size(); i++) {

            if (e.get(i).getName().equals(entityName)) {

                for (int j = 0; j < e.get(i).instances.size(); j++) {

                    if (e.get(i).getInstances().get(j).getInstanceName().equals(instanceName)) {

                        return true;
                    }

                }
                return false;
            }

        }

        return false;
    }

    private void inicializequantityOfOccorrence(int[] v, int tamanho) {

        for (int i = 0; i < tamanho; i++) {

            v[i] = 0;
        }
    }

    private boolean verifyInstanceAlreadyCounted(List<String> instancesAlreadyCounted, String instance) {

        for (String iac : instancesAlreadyCounted) {

            if (iac.equals(instance)) {
                return true;
            }
        }
        return false;
    }

    private int maximumOccurrence(int[] v, int tamanho) {

        int maior = -999;
        for (int i = 0; i < tamanho; i++) {

            if (v[i] > maior) {
                maior = v[i];
            }
        }
        return maior;
    }

    private boolean isObjectLiteral(String object) {
        if (object.startsWith("http://")) {
            return false;
        }
        return true;
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

}

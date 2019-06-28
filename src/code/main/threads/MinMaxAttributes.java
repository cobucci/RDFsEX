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
public class MinMaxAttributes implements Runnable {

    private List<Triple> extractedData;
    private List<Entity> entities;

    public MinMaxAttributes(List<Triple> extractedData, List<Entity> entities) {
        this.extractedData = extractedData;
        this.entities = entities;

    }

    @Override
    public void run() {
        try {
            minMaxAttribute();
        } catch (IOException ex) {
            Logger.getLogger(MinMaxAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void minMaxAttribute() throws IOException {

        int quantAtributo = 0;
        int cardAbaixoDe1 = 0;
        double mediaMin = 0, mediaMax = 0;
        for (Entity e : entities) {

            for (int i = 0; i < e.attributes.size(); i++) {

                if (!e.attributes.isEmpty()) {

                    quantAtributo++;
                    List<String> instancesAlreadyCounted = new ArrayList<>();
                    int quantityOfOcorrence[] = new int[e.instances.size()];
                    inicializequantityOfOccorrence(quantityOfOcorrence, e.instances.size());
                    int optional = 0;

                    for (int j = 0; j < extractedData.size(); j++) {

                        if (e.attributes.get(i).getName().equals(extractedData.get(j).getPredicate())) {

                            if (verifyExistanceInstance(entities, e.getName(), extractedData.get(j).getSubject())) {

                                optional = 1;

                                int position = findPositionOfInstance(entities, extractedData.get(j).getSubject(), e.getName());  //encontrar a posicao da instancia
                                if (position != -1) {

                                    quantityOfOcorrence[position] += 1; //para encontrar o max
                                    if (!verifyInstanceAlreadyCounted(instancesAlreadyCounted, extractedData.get(j).getSubject())) {
                                        instancesAlreadyCounted.add(extractedData.get(j).getSubject());
                                    }

                                }
                            }

                        }
                    }
                    if (optional == 1) {
                        e.attributes.get(i).setOptional(false);
                    } else {
                        e.attributes.get(i).setOptional(true);
                    }

                    int max = maximumOccurrence(quantityOfOcorrence, e.instances.size());
                    double min = (double) instancesAlreadyCounted.size() / (double) e.instances.size();

                    e.attributes.get(i).setMin(min);
                    e.attributes.get(i).setMax(max);
                    mediaMin += min;
                    mediaMax += (double) max;

                    writeInFileMinMaxAttributes(e.getName(), e.attributes.get(i).getName(), instancesAlreadyCounted.size(), e.instances.size(), min, max);
                    if (min < 1) {
                        cardAbaixoDe1++;
                    }
                }

            }

        }

        mediaMin = mediaMin / quantAtributo;
        mediaMax = mediaMax / quantAtributo;
        writeInFileStatisticalData(quantAtributo, cardAbaixoDe1, mediaMin, mediaMax);

    }

    private void writeInFileStatisticalData(int quantAtributos, int cardAbaixoDe1, double mediaMin, double mediaMax) throws IOException {

        float porcentagem = (100 * cardAbaixoDe1) / quantAtributos;
        File f = new File("StatisticalData.txt");
        FileWriter fileWriter = new FileWriter(f, true);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        try (PrintWriter printWriter = new PrintWriter(buffer)) {

            printWriter.printf("TOTAL DE ATRIBUTOS = %d\nTOTAL DE ATRIBUTOS COM CARDINALIDADE MINIMA MENOR QUE 1 = %d\n%.3f %% dos atributos tem cardinalidade mínima inferior a 1\n", quantAtributos, cardAbaixoDe1, porcentagem);
            printWriter.printf("AVG MIN ATRIBUTO = %.3f\nAVG MAX ATRIBUTO = %.3f\n\n\n", mediaMin, mediaMax);

        }

        fileWriter.close();
    }

    // MIN E MAX ATRIBUTOS 2
    private void inicializequantityOfOccorrence(int[] v, int tamanho) {

        for (int i = 0; i < tamanho; i++) {

            v[i] = 0;
        }
    }

    // MIN E MAX ATRIBUTOS 3
    private boolean verifyInstanceAlreadyCounted(List<String> instancesAlreadyCounted, String instance) {

        for (String iac : instancesAlreadyCounted) {

            if (iac.equals(instance)) {
                return true;
            }
        }
        return false;
    }

    // MIN E MAX ATRIBUTOS 4
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

    // MIN E MAX ATRIBUTOS 5
    private int maximumOccurrence(int[] v, int tamanho) {

        int maior = -999;
        for (int i = 0; i < tamanho; i++) {

            if (v[i] > maior) {
                maior = v[i];
            }
        }
        return maior;
    }

    // MIN E MAX ATRIBUTOS 6
    private void writeInFileMinMaxAttributes(String entityName, String attributeName, int quantity, int totalInstances, double min, int max) throws IOException {

        File f = new File("MinMaxAttributes.txt");
        FileWriter fileWriter = new FileWriter(f, true);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        try (PrintWriter printWriter = new PrintWriter(buffer)) {

            printWriter.printf("Entidade = %s\nAtributo = %s\n", entityName, attributeName);
            printWriter.printf("(%.3f, %d)\n\n\n", min, max);
        }

        fileWriter.close();
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

}

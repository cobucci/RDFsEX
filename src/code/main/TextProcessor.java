package code.main;

import code.main.threads.Coherence;
import code.main.threads.ExtractAttributes;
import code.main.threads.ExtractEntities;
import code.main.threads.ExtractInstances;
import code.main.threads.ExtractRDF;
import code.main.threads.ExtractRelationships;
import code.main.threads.FileEntitiesAttributes;
import code.main.threads.FileEntitiesInstances;
import code.main.threads.MinMaxAttributes;
import code.main.threads.MinMaxRelationships;
import code.main.threads.NewDataWithInstancesAndRelationships;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import org.semanticweb.yars.nx.parser.NxParser;

public class TextProcessor {

    public static void main(String[] args) throws Exception {

        List<Triple> data = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();

        //lendo o dataset
        FileInputStream fis = new FileInputStream("datasets/stw.nt");
        NxParser nx = new NxParser(fis);

        //thread para transformar o dataset em triplas
        ExtractRDF extractRDF = new ExtractRDF(fis, nx, data);
        Thread threadExtractRdf = new Thread(extractRDF);
        threadExtractRdf.start();
        threadExtractRdf.join();

        //extrair entidades
        int processadores = Runtime.getRuntime().availableProcessors();
        int quantidadePorThread = data.size() / processadores;
        Thread threadsExtractEntities[] = new Thread[processadores];
        for (int i = 0; i < processadores; i++) {

            int begin = i * quantidadePorThread;
            int end;
            if (processadores - i == 1) {
                end = data.size();
            } else {
                end = (i + 1) * quantidadePorThread;
            }
            System.out.println(begin + " - " + end);

            ExtractEntities extractEntities = new ExtractEntities(data, entities, begin, end);
            threadsExtractEntities[i] = new Thread(extractEntities);
            threadsExtractEntities[i].start();
            System.out.println("Start " + i);

        }

        for (int i = 0; i < processadores; i++) {

            threadsExtractEntities[i].join();
            System.out.println("Acabou extractEntities " + i);
        }

        //extract Instances
        Thread threadsExtractInstances[] = new Thread[processadores];
        for (int i = 0; i < processadores; i++) {

            int begin = i * quantidadePorThread;
            int end;
            if (processadores - i == 1) {
                end = data.size();
            } else {
                end = (i + 1) * quantidadePorThread;
            }
            ExtractInstances extractInstances = new ExtractInstances(data, entities, begin, end);
            threadsExtractInstances[i] = new Thread(extractInstances);
            threadsExtractInstances[i].start();

        }

        for (int i = 0; i < processadores; i++) {

            threadsExtractInstances[i].join();
            System.out.println("Acabou extractInstances " + i);
        }

        //escrever entidades e instancias em um arquivo
        FileEntitiesInstances fileEntitiesInstances = new FileEntitiesInstances(entities);
        Thread threadFileEntitiesInstances = new Thread(fileEntitiesInstances);
        threadFileEntitiesInstances.start();

        //Extract Relationships
        List<String> relationships = new ArrayList<>();
        Thread threadsExtractRelationships[] = new Thread[processadores];
        for (int i = 0; i < processadores; i++) {

            int begin = i * quantidadePorThread;
            int end;
            if (processadores - i == 1) {
                end = data.size();
            } else {
                end = (i + 1) * quantidadePorThread;
            }
            ExtractRelationships extractRelationships = new ExtractRelationships(data, entities, relationships, begin, end);
            threadsExtractRelationships[i] = new Thread(extractRelationships);
            threadsExtractRelationships[i].start();
        }

        for (int i = 0; i < processadores; i++) {

            threadsExtractRelationships[i].join();
            System.out.println("Parou extractRelationships " + i);
        }

        //NEW DATA (RELATIONSHIPS)
        List<Triple> newData = new ArrayList<>();
        Thread newDataWithInstancesAndRelationships[] = new Thread[processadores];
        for (int i = 0; i < processadores; i++) {

            int begin = i * quantidadePorThread;
            int end;
            if (processadores - i == 1) {
                end = data.size();
            } else {
                end = (i + 1) * quantidadePorThread;
            }
            
            NewDataWithInstancesAndRelationships nd = new NewDataWithInstancesAndRelationships(data, entities, newData, relationships, begin, end);
            newDataWithInstancesAndRelationships[i] = new Thread(nd);
            newDataWithInstancesAndRelationships[i].start();
            System.out.println("Comecou o new Data para relacionamentos " + i);
        }

        for (int i = 0; i < processadores; i++) {

            newDataWithInstancesAndRelationships[i].join();
            System.out.println("Parou new Data " + i);
        }

        //MIN MAX RELATIONSHIPS
        MinMaxRelationships thread4 = new MinMaxRelationships(newData, entities, relationships);
        Thread t4 = new Thread(thread4);
        t4.start(); //MIN MAX RELATIONSHIP

        ExtractAttributes thread5 = new ExtractAttributes(data, entities); //atributos
        Thread t5 = new Thread(thread5);
        System.out.println("Comecou o extract attribute");
        t5.start(); //Extract atributtes
        t5.join(); //atributos
        System.out.println("Acabou Extract Attributes");

        MinMaxAttributes thread6 = new MinMaxAttributes(data, entities); //atributo
        Thread t6 = new Thread(thread6);
        System.out.println("Comecou Min Max Attributes");
        t6.start(); //MIN MAX ATRIBUTOS
        t6.join();
        System.out.println("Acabou Min Max Attribute");

        FileEntitiesAttributes thread7 = new FileEntitiesAttributes(entities); //atributo
        Thread t7 = new Thread(thread7);
        t7.start(); //ESCREVER NO ARQUIVO ENTITIES & ATRIBUTOS

        t4.join(); //MIN MAX RELATIONSHIP

        //calculo do coherence
        Coherence c = new Coherence(data, entities);
        Thread coherence = new Thread(c);
        coherence.start();
        coherence.join();

    }

}

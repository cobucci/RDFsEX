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
public class FileEntitiesInstances implements Runnable {

    private List<Entity> entities;

    public FileEntitiesInstances(List<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public void run() {

        try {
            writeInFileInstances();
        } catch (IOException ex) {
            Logger.getLogger(FileEntitiesInstances.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeInFileInstances() throws IOException {

        File f = new File("Entities&Instances.txt");
        FileWriter fileWriter = new FileWriter(f, true);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        try (PrintWriter printWriter = new PrintWriter(buffer)) {
            for (int i = 0; i < entities.size(); i++) {

                printWriter.println("ENTITY NAME : " + entities.get(i).getName() + "\nTOTAL OF INSTANCES = " + entities.get(i).getInstances().size());

                for (int j = 0; j < entities.get(i).getInstances().size(); j++) {
                    printWriter.println(entities.get(i).getInstances().get(j).getInstanceName());
                }

                printWriter.println("\n\n");
            }
        }

        fileWriter.close();
    }

}

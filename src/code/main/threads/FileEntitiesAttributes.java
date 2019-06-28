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
public class FileEntitiesAttributes implements Runnable {

    private List<Entity> entities;

    public FileEntitiesAttributes(List<Entity> entities) {

        this.entities = entities;

    }

    @Override
    public void run() {

        try {
            writeInFileEntitiesAndAttributes();
        } catch (IOException ex) {
            Logger.getLogger(FileEntitiesAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeInFileEntitiesAndAttributes() throws IOException {

        File f = new File("Entities&Attributes.txt");
        FileWriter fileWriter = new FileWriter(f, true);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        try (PrintWriter printWriter = new PrintWriter(buffer)) {

            for (Entity e : entities) {

                printWriter.println("ENTITY NAME : " + e.getName() + "\nTOTAL OF ATTRIBUTES = " + e.attributes.size());
                for (int i = 0; i < e.attributes.size(); i++) {
                    printWriter.println(e.attributes.get(i).getName() + " - Optional: " + e.attributes.get(i).isOptional() + " :: Multivalued: " + e.attributes.get(i).isMultivalued() + " :: Parent: " + e.attributes.get(i).getParent());

                }
                printWriter.println("\n\n");
            }
        }

        fileWriter.close();
    }

}

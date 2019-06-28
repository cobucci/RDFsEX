/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.main.threads;

import code.main.Attribute;
import code.main.Entity;
import code.main.Triple;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class ExtractAttributes implements Runnable {

    private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private List<Triple> extractedData;
    private List<Entity> entities;

    public ExtractAttributes(List<Triple> extractedData, List<Entity> entities) {
        this.extractedData = extractedData;
        this.entities = entities;
    }

    @Override
    public void run() {
        extract();
    }

    private void extract() {

        for (Entity e : entities) {

            for (int j = 0; j < e.getInstances().size(); j++) {

                List<Attribute> atributeList = new ArrayList<>();
                atributeList = extractAtributes(extractedData, e.getInstances().get(j).getInstanceName());
                for (int i = 0; i < atributeList.size(); i++) {
                    if (!verifyExistenceAttribute(e.getAttributes(), atributeList.get(i).getName())) {

                        e.attributes.add(atributeList.get(i));
                    }

                }

            }
        }

    }

    private List<Attribute> extractAtributes(List<Triple> extractedData, String subjectName) {
        List<Attribute> atributeList = new ArrayList<>();
        for (int i = 0; i < extractedData.size(); i++) {

            if (isObjectLiteral(extractedData.get(i).getObject())
                    && extractedData.get(i).getSubject().equals(subjectName)) {
                Attribute attribute = new Attribute();
                if (!verifyExistenceAttribute(atributeList, extractedData.get(i).getPredicate())) {
                    attribute.setName(extractedData.get(i).getPredicate());
                    attribute.setValue(extractedData.get(i).getObject());
                    atributeList.add(attribute);
                }

            } else if (isCompositeAtribute(extractedData.get(i).getObject())) {

                if (extractedData.get(i).getSubject().equals(subjectName)) {
                    System.out.println("Atributo -> " + extractedData.get(i).getObject() + " entrando pra ser composto");
                    atributeList.addAll(verifyCompositeAttributes(extractedData, i, false));
                }
            }

        }
        //System.out.println("\n");
        return atributeList;
    }

    private List<Attribute> verifyCompositeAttributes(List<Triple> extractedData, int position, boolean takeParent) {
        List<Attribute> compositeAttributes = new ArrayList<>();
        String subjectAttribute = extractedData.get(position).getObject();
        Attribute composite = new Attribute();
        composite.setName(extractedData.get(position).getPredicate());
        if (takeParent) {
            composite.setParent(extractedData.get(position).getSubject());
        }
        compositeAttributes.add(composite);
        for (int i = 0; i < extractedData.size(); i++) {
            if (extractedData.get(i).getSubject().equals(subjectAttribute)
                    && !isCompositeAtribute(extractedData.get(i).getObject())) {
                Attribute attribute = new Attribute();
                attribute.setName(extractedData.get(i).getPredicate());
                attribute.setValue(extractedData.get(i).getObject());
                attribute.setParent(subjectAttribute);
                compositeAttributes.add(attribute);
            } else if (extractedData.get(i).getSubject().equals(subjectAttribute)
                    && isCompositeAtribute(extractedData.get(i).getObject())) {
                compositeAttributes.addAll(verifyCompositeAttributes(extractedData, i, true));
            }
        }
        return compositeAttributes;
    }

    private boolean isCompositeAtribute(String object) {
        if (object.charAt(0) == '_' && object.charAt(1) == ':') {
            return true;
        }
        return false;
    }

    private boolean verifyExistenceAttribute(List<Attribute> atributeList, String attributeName) {

        for (Attribute a : atributeList) {

            if (a.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isObjectLiteral(String object) {
        if (object.startsWith("http://")) {
            return false;
        }
        return true;
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

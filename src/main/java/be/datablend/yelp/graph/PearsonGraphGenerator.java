package be.datablend.yelp.graph;

import be.datablend.yelp.model.Business;
import be.datablend.yelp.parsing.Parser;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: dsuvee (datablend.be)
 * Date: 28/11/13
 */
public class PearsonGraphGenerator {

    private final double MIN_CORRELATION = 0.8;

    Map<String,Node> nodeMap = new HashMap<String,Node>();
    Set<Relationship> relationships = new HashSet<Relationship>();

    public void generate() {
        // Create the graph
        GraphDatabaseService graph = new EmbeddedGraphDatabase("yelp-graph");

        Parser parser = new Parser("yelp_academic_dataset_business.json","yelp_academic_dataset_checkin.json");
        Set<Business> businesses1 = parser.getBusinesses();
        Set<Business> businesses2 = parser.getBusinesses();

        for (Business business1 : businesses1) {
            Transaction tx = graph.beginTx();
            for (Business business2 : businesses2) {
                if (!business1.getId().equals(business2.getId())) {
                    PearsonsCorrelation correlation = new PearsonsCorrelation();
                    double corr = correlation.correlation(getValueArray(business1), getValueArray(business2));
                    if (corr >= MIN_CORRELATION) {
                        Node business1Node = getOrCreateNode(business1, graph);
                        Node business2Node = getOrCreateNode(business2, graph);
                        Relationship relationship = business1Node.createRelationshipTo(business2Node, DynamicRelationshipType.withName("correlated"));
                        relationships.add(relationship);
                    }
                }
            }
            tx.success();
            tx.finish();
            businesses2.remove(business1);
        }

        graph.shutdown();
    }

    private Node getOrCreateNode(Business business, GraphDatabaseService graph) {
        if (!nodeMap.containsKey(business.getId())) {
            Node node = graph.createNode();
            node.setProperty("name", business.getName());
            node.setProperty("city", business.getCity());
            node.setProperty("categories", business.getCategories().toArray(new String[0]));
            nodeMap.put(business.getId(), node);
        }
        return nodeMap.get(business.getId());
    }

    private double[] getValueArray(Business business) {
        double[] valueArray = new double[business.getCheckIns().size()];
        int index = 0;
        for (Map.Entry<String,Long> entry : business.getCheckIns().entrySet()) {
            Long value = entry.getValue();
            valueArray[index++] = value.doubleValue();
        }
        return valueArray;
    }

    public static void main(String[] args) {
        new PearsonGraphGenerator().generate();
    }

}

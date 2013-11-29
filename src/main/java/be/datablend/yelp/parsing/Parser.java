package be.datablend.yelp.parsing;

import be.datablend.yelp.model.Business;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.*;

/**
 * User: dsuvee (datablend.be)
 * Date: 28/11/13
 */
public class Parser {

    private Map<String, Business.Builder> businessBuilders =  new HashMap<String, Business.Builder>();
    private final int MIN_NUMBER_OF_CHECKINS = 100;

    public Parser(String businessJsonFile, String checkInsJsonFile) {
        try {
            String businessesFile = getClass().getClassLoader().getResource(businessJsonFile).getFile();
            String checkInsFile = getClass().getClassLoader().getResource(checkInsJsonFile).getFile();
            parseBusinesses(businessesFile);
            parseCheckIns(checkInsFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Set<Business> getBusinesses() {
        Set<Business> businesses = new HashSet<Business>();
        for (Business.Builder businessBuilder : businessBuilders.values()) {
            Business business = businessBuilder.build();
            if (business.getNumberOfCheckIns() >= MIN_NUMBER_OF_CHECKINS) {
                businesses.add(business);
            }
        }
        return businesses;
    }

    // Parse the business json
    public void parseBusinesses(String businessFile) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(businessFile)));
        String line;
        while ((line = reader.readLine()) != null) {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject)parser.parse(line);
            Business.Builder businessBuilder = Business.builder();
            businessBuilder.setId((String) object.get("business_id"));
            businessBuilder.setName((String) object.get("name"));
            businessBuilder.setCity((String)object.get("city"));
            JSONArray categories = (JSONArray)object.get("categories");
            for (Object element : categories) {
                businessBuilder.addCategory((String) element);
            }
            businessBuilders.put((String) object.get("business_id"), businessBuilder);
        }
    }

    // Parse the checkin json
    public void parseCheckIns(String checkInFile) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(checkInFile)));
        String line;
        while ((line = reader.readLine()) != null) {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject)parser.parse(line);
            String businessId = (String) object.get("business_id");
            JSONObject checkIns = (JSONObject)object.get("checkin_info");
            Set<Map.Entry> entries = checkIns.entrySet();
            for (Map.Entry<String,Long> entry : entries) {
                businessBuilders.get(businessId).addCheckIn(entry.getKey(), (long)entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser("yelp_academic_dataset_business.json","yelp_academic_dataset_checkin.json");
        Set<Business> businesses = parser.getBusinesses();
        System.out.println(businesses.size());
    }

}

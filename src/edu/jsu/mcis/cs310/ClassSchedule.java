package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        Iterator<String[]> csvIterator = csv.iterator();
        if (csvIterator.hasNext()) {
            // Headers
            String[] headers = csvIterator.next();

            // Map to store Headers
            HashMap<String, Integer> headerIndexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndexMap.put(headers[i], i);
            }
           
            JsonObject scheduleTypeMap = new JsonObject();
            JsonObject subjectMap = new JsonObject();
            JsonObject courseMap = new JsonObject();
            JsonArray sectionArray = new JsonArray();
            
            while (csvIterator.hasNext()) {
                String[] rowData = csvIterator.next();

                
                String type = rowData[headerIndexMap.get(TYPE_COL_HEADER)];
                String schedule = rowData[headerIndexMap.get(SCHEDULE_COL_HEADER)];
                if (scheduleTypeMap.get(type) == null) {
                    scheduleTypeMap.put(type, schedule);
                }
                
                String subjectId = rowData[headerIndexMap.get(NUM_COL_HEADER)].replaceAll("\\d", "").replaceAll("\\s", "");
                if (subjectMap.get(subjectId) == null) {
                    String subjectHeader = rowData[headerIndexMap.get(SUBJECT_COL_HEADER)];
                    subjectMap.put(subjectId, subjectHeader);
                }
                 
                String num = rowData[headerIndexMap.get(NUM_COL_HEADER)];
                String numNoLetters = num.replaceAll("[A-Z]", "").replaceAll("\\s", "");
                if (courseMap.get(num) == null) {
                    String description = rowData[headerIndexMap.get(DESCRIPTION_COL_HEADER)];
                    int credits = Integer.parseInt(rowData[headerIndexMap.get(CREDITS_COL_HEADER)]);

                    JsonObject course = new JsonObject();
                    course.put(SUBJECTID_COL_HEADER, subjectId);
                    course.put(NUM_COL_HEADER, numNoLetters);
                    course.put(DESCRIPTION_COL_HEADER, description);
                    course.put(CREDITS_COL_HEADER, credits);
                    courseMap.put(num, course);
                }
                int crn = Integer.parseInt(rowData[headerIndexMap.get(CRN_COL_HEADER)]);
                String sectionHeader = rowData[headerIndexMap.get(SECTION_COL_HEADER)];
                String start = rowData[headerIndexMap.get(START_COL_HEADER)];
                String end = rowData[headerIndexMap.get(END_COL_HEADER)];
                String days = rowData[headerIndexMap.get(DAYS_COL_HEADER)];
                String where = rowData[headerIndexMap.get(WHERE_COL_HEADER)];
                String allInstructors = rowData[headerIndexMap.get(INSTRUCTOR_COL_HEADER)];
                
                List<String> instructors = Arrays.asList(allInstructors.split(", "));
                JsonArray instructorArray = new JsonArray();
                for (String instructor : instructors) {
                    instructorArray.add(instructor);
                }
                
               
                JsonObject sectionDetails = new JsonObject();
                sectionDetails.put(CRN_COL_HEADER, crn);
                sectionDetails.put(SECTION_COL_HEADER, sectionHeader);
                sectionDetails.put(START_COL_HEADER, start);
                sectionDetails.put(END_COL_HEADER, end);
                sectionDetails.put(DAYS_COL_HEADER, days);
                sectionDetails.put(WHERE_COL_HEADER, where);
                sectionDetails.put(INSTRUCTOR_COL_HEADER, instructorArray);
                sectionDetails.put(NUM_COL_HEADER, numNoLetters);
                sectionDetails.put(TYPE_COL_HEADER, type);
                sectionDetails.put(SUBJECTID_COL_HEADER, subjectId);

                // Add section object to section array
                sectionArray.add(sectionDetails);
            }
            
               
            JsonObject courseListMap = new JsonObject();
            courseListMap.put("scheduletype", scheduleTypeMap);
            courseListMap.put("subject", subjectMap);
            courseListMap.put("course", courseMap);
            courseListMap.put("section", sectionArray);

           
            return Jsoner.serialize(courseListMap);
        }
        
        return "{}";
        
    }
    
    public String convertJsonToCsvString(JsonObject json) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        String[] header = {
            CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER, 
            SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER, 
            END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, SCHEDULE_COL_HEADER, 
            INSTRUCTOR_COL_HEADER
        };
        csvWriter.writeNext(header);
        JsonObject scheduleTypeMap = (JsonObject)json.get("scheduletype");
        JsonObject subjectMap = (JsonObject)json.get("subject");
        JsonObject courseMap = (JsonObject)json.get("course");
        JsonArray sectionArray = (JsonArray)json.get("section");
        for (int i = 0; i < sectionArray.size(); i++) {
            JsonObject sectionDetails = sectionArray.getMap(i);
            String crn = String.valueOf(sectionDetails.get(CRN_COL_HEADER));
            String subjectId = (String)sectionDetails.get(SUBJECTID_COL_HEADER);
            String num = subjectId + " " + sectionDetails.get(NUM_COL_HEADER);
            String section = (String)sectionDetails.get(SECTION_COL_HEADER);
            String type = (String)sectionDetails.get(TYPE_COL_HEADER);
            String start = (String)sectionDetails.get(START_COL_HEADER);
            String end = (String)sectionDetails.get(END_COL_HEADER);
            String days = (String)sectionDetails.get(DAYS_COL_HEADER);
            String where = (String)sectionDetails.get(WHERE_COL_HEADER);
            
            JsonArray instructorArray = (JsonArray)sectionDetails.get(INSTRUCTOR_COL_HEADER);
            StringBuilder instructorBuilder = new StringBuilder();
            for (int j = 0; j < instructorArray.size(); j++) {
                instructorBuilder.append(instructorArray.getString(j));
                if (j < instructorArray.size() - 1) {
                    instructorBuilder.append(", ");
                }
            }
             String instructor = instructorBuilder.toString();
             String schedule = (String)scheduleTypeMap.get(type);
             
            JsonObject courseDetails = (JsonObject)courseMap.get(num);
            String description = (String)courseDetails.get(DESCRIPTION_COL_HEADER);
            String credits = String.valueOf(courseDetails.get(CREDITS_COL_HEADER));
            
            String subjectName = (String)subjectMap.get(subjectId);
            
             String[] record = {
                crn, subjectName, num, description, section, type, credits, 
                start, end, days, where, schedule, instructor
            };
            csvWriter.writeNext(record);
        }

        return writer.toString();
        
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}
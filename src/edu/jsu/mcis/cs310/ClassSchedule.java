package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
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
        JsonObject json = new JsonObject();
        
        JsonObject scheduleType = new JsonObject();
        scheduleType.put("LEC", "In-Person Instruction");
        scheduleType.put("ONL", "Online (asynchronous)");
        json.put("scheduletype", scheduleType);
        
        JsonObject subjects = new JsonObject();
        subjects.put("CS", "Computer Science");
        subjects.put("EH", "English");
        subjects.put("HY","History");
        subjects.put("MS", "Mathmatics");
        json.put("subject", subjects);
        
        JsonObject courses = new JsonObject();
        JsonArray sections = new JsonArray();
        
        String[] headers = csv.get(0);
        
        for(int i = i; i < csv.size(); i++){
            String[]row = csv.get(i);
            String crn = row [0];
            String subject = row [1];
            String num = row[2];
            String description = row[3];
            String sectionNum = row[4];
            String type = row[5];
            String credits = row[6];
            String start = row[7];
            String end = row[8];
            String days = row[9];
            String where = row[10];
            String instructor = row[11];
            
            String courseId = subject + " " + num;
            if (!courses.containsKey(courseId)){
                JsonObject course = new JsonObject();
                course.put("subjectid", subject);
                course.put("num", num);
                course.put("description", description);
                course.put("credits", credits);
                courses.put(courseId, course);
            }
            JsonObject section = new JsonObject();
            section.put("crn", crn);
            section.put("subjectid", subject);
            section.put("num", num);
            section.put("section", sectionNum);
            section.put("type", type);
            section.put("start", start);
            section.put("end", end);
            section.put("days", days);
            section.put("where", where);
            
            JsonArray instructors = new JsonArray();
            instructors.add(instructor);
            section.put("instructor", instructors);
            
            sections.add(section);
          
        }
        json.put("course", courses);
        json.put("section", sections);
        
        return json.toJson();
        
    }
    
    public String convertJsonToCsvString(JsonObject json) {
        
        return ""; // remove this!
        
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
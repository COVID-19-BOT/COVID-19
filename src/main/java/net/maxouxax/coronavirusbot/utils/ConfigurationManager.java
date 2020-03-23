package net.maxouxax.coronavirusbot.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {

    private File configFile;
    private Map<String, String> configKeys = new HashMap<>();

    public ConfigurationManager(String fileName) {
        this.configFile = new File(fileName);
    }

    public void loadData() throws IOException {
        if(!configFile.exists()){
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }

        try{
            JSONReader reader = new JSONReader(configFile);
            JSONArray array = reader.toJSONArray();

            for(int i = 0; i < array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);
                configKeys.put(object.getString("key"), object.getString("value"));
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveData(){
        JSONArray array = new JSONArray();

        for(Map.Entry<String, String> configKeys : configKeys.entrySet())
        {
            JSONObject object = new JSONObject();
            object.accumulate("key", configKeys.getKey());
            object.accumulate("value", configKeys.getValue());
            array.put(object);
        }

        try(JSONWriter writter = new JSONWriter(configFile)){

            writter.write(array);
            writter.flush();

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public boolean isSet(String key){
        return configKeys.containsKey(key);
    }

    public String getStringValue(String key){
        return configKeys.get(key);
    }

    public Long getLongValue(String key){
        return Long.valueOf(configKeys.get(key));
    }

    public void setValue(String key, String value){
        configKeys.put(key, value);
    }

}

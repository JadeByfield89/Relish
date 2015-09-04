package relish.permoveo.com.relish.model;

import java.util.Map;

/**
 * Created by byfieldj on 9/3/15.
 */
public class Setting {

    private Map<String, String> dataSet;

    public Setting(Map<String, String> data){
        this.dataSet = data;
    }

    public Map<String, String> getSettings(){

        return dataSet;
    }


}

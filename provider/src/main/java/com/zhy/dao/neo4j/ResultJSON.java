package com.zhy.dao.neo4j;

import com.alibaba.fastjson.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultJSON {
    public List<JSONObject> result ;

    public static final int NODE =1;
    public static final int LIST =2;
    public static final int INDEX =3;
    public static final int GRAPH = 4;

    private int resultype = NODE;

    public ResultJSON(){
        result = new ArrayList<>();
    }

    // 由result获取 resultjson对象
    public static ResultJSON resultTransformResultJSON(Result result, int resulttype){
        if (resulttype == ResultJSON.GRAPH){
            return graphRstTransResultJSON(result);
        }
        ResultJSON rj = new ResultJSON();
        rj.resultype = resulttype;

        while (result.hasNext()) {
            Record record = result.next();
            rj.addRecord(record);
        }
        return rj;
    }

    public static ResultJSON graphRstTransResultJSON(Result result){

        ResultJSON rj = new ResultJSON();
        Record record= result.next();
        Value nodes = record.get("nodes");
        Value rels = record.get("relationships");
        for (Value value :
                nodes.values()) {
            JSONObject jo = new JSONObject();
            jo.put("name",value.get("name").asString());
            jo.put("count",value.get("count").toString());
            rj.result.add(jo);
        }
        for (Value value :
                rels.values()) {
            JSONObject jo = new JSONObject();
            jo.put("count",value.get("count").toString());
            jo.put("type",value.get("type").asString());
            jo.put("in",value.get("in").toString());
            jo.put("out",value.get("out").toString());
            rj.result.add(jo);
        }
        rj.resultype=ResultJSON.GRAPH;
        return rj;
    }

    public static ResultJSON streamTransformResultJSON(Stream<Record> recordStream, int resultype){
        ResultJSON rj = new ResultJSON();
        rj.resultype = resultype;

        for (Record record:
                recordStream.collect(Collectors.toList())) {
            rj.addRecord(record);
        }

        return rj;
    }


    public void addRecord(Record record){
        switch (resultype){
            case NODE:
                JSONObject jo = ParseUtils.nodeRecordToJSON(record);
//                System.out.println(jo.toJSONString());
                result.add(jo);
                break;
            case LIST:
                result.add(ParseUtils.listRecordToJSON(record));
                break;
            case INDEX:
                if (ParseUtils.IndexRecordParse(record)!= null)
                    result.add(ParseUtils.IndexRecordParse(record));
        }
    }

    public ArrayList<String> getIndexes(){
        ArrayList<String> indexes = new ArrayList<>();
        for (JSONObject jo :
                result) {
            String index= (String) jo.get("label")+ "." +jo.get("property");
            System.out.println(index);
            indexes.add(index);
        }
        return indexes;
    }

    public List<JSONObject> getResult() {
        return result;
    }

    public int getResultype() {
        return resultype;
    }
    public void setResultype(int resultype) {
        this.resultype =resultype;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        // 限制返回数目
        int count = 10;
        for (JSONObject jo :
                result) {
            if (count-- == 0){
                break;
            }
            sb.append(jo.toJSONString()+"\n");
        }
        return sb.toString();
    }

}

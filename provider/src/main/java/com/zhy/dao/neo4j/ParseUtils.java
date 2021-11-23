package com.zhy.dao.neo4j;

import com.alibaba.fastjson.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.summary.SummaryCounters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ParseUtils {



    public static JSONObject nodeRecordToJSON(Record record){
//        String ans = "";

        JSONObject jo = new JSONObject();

        for (String k:record.keys() ){
            for (String recordkey:
                record.get(k).keys() ) {

                Value value = record.get(k).get(recordkey);
                jo.put(recordkey,value.asString());

//                System.out.println(recordkey + " : " + record.get(k).get(recordkey));
                //                ans += recordkey + " : " + record.get(k).get(recordkey);
            }

        }

        return jo;
//        return jo.toJSONString();
    }

    public static JSONObject listRecordToJSON(Record record){
        //        String ans = "";

        JSONObject jo = new JSONObject();

        for (String k:record.keys() ){


                Value value = record.get(k);
                if (value.getClass().getName().equals("org.neo4j.driver.internal.value.StringValue")){
                    jo.put(k,value.asString());
                }else
                    jo.put(k,value.toString());


                //                System.out.println(recordkey + " : " + record.get(k).get(recordkey));
                //                ans += recordkey + " : " + record.get(k).get(recordkey);


        }

//        System.out.println(jo.toJSONString());
        return jo;
//        return jo.toJSONString();
    }


    // 待考证 index 返回的 list 仅含一个元素
    public static JSONObject IndexRecordParse(Record record){
        JSONObject jo = null;

        List<Object> properties = record.get("properties").asList();
        List<Object> labelsOrTypes = record.get("labelsOrTypes").asList();
        if(!properties.isEmpty()){
            jo = new JSONObject();
            jo.put("property",properties.get(0).toString());
            jo.put("label",labelsOrTypes.get(0).toString());
            String type = record.get("type").asString();
            jo.put("type",type);
        }
        return jo;
    }

    public static JSONObject statusParse(SummaryCounters counters){
        JSONObject jo =new JSONObject();
        Class<?> class1 = counters.getClass();
        Method[] methods = class1.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount()>0 || method.getName().equals("hashCode")) {
                continue;
            }
            try {
//                System.out.println("执行" +method.getName() + "方法");
                Object value = method.invoke(counters);
                System.out.println(method.getName()+"="+value);
                jo.put(method.getName(),value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return jo;

    }

//    public static void executeMethodByName(String mehtod, String path) {
//        Object obj = null;
//        Method method = null;
//        String className = null;
//        try {
//            // 里面写自己的类名及路径
//            Class<?> c = Class.forName(path);
//            obj = c.newInstance();
//            className = c.getCanonicalName();
//            // 第一个参数写的是方法名,第二个\第三个\...写的是方法参数列表中参数的类型
//            method = c.getMethod(mehtod);
//            // invoke是执行该方法,并携带参数值
//        } catch (Exception e) {
//            System.out.println("反射执行出错！");
//        }
//        try {
//            System.out.println("执行" + className + "类的" + method.getName() + "方法");
//            method.invoke(obj);
//        } catch (Exception e) {
//            System.out.println("反射运行方法异常！");
//        }
//    }

}

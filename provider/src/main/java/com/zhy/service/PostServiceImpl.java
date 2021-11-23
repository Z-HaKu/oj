package com.zhy.service;



import com.zhy.dao.neo4j.ConnectUtil;
import com.zhy.dao.neo4j.ResultJSON;
import org.apache.dubbo.config.annotation.DubboService;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

@DubboService
public class PostServiceImpl implements PostService{
    @Override
    public String query(String ip, String queryStr,Integer type) {
        ResultJSON result;
        ConnectUtil con =new ConnectUtil(ip);
        Session session= con.getSession();
        Result rst = session.run(queryStr);
        result = ResultJSON.resultTransformResultJSON(rst,type);
        con.close();
        return result.toString();
    }
}

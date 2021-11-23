package com.zhy.dao.neo4j;


import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class ConnectUtil {
    private Driver driver ;

    private Session session ;

    public ConnectUtil(String ip){
        String uri ="bolt://" + ip +":7687";
        driver = GraphDatabase.driver(uri,
                AuthTokens.basic("neo4j", "123456"));
        session = driver.session();
    }

    public void close(){
        session.close();
        driver.close();
    }

    public Session getSession(){
        return session;
    }
}
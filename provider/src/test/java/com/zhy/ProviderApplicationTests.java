package com.zhy;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProviderApplicationTests {

    @Test
    void contextLoads() {
        Driver driver = GraphDatabase.driver("bolt://192.168.236.135:7687",
                AuthTokens.basic("neo4j", "123456"));
        Session session = driver.session();
        String queryStr ="match (user:UserNode) return user limit 10" ;
        Result result = session.run(queryStr);
        System.out.println(result);
        System.out.println("right");
    }



}

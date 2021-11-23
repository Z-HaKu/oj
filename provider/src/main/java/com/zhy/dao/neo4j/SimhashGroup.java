package com.zhy.dao.neo4j;

import java.io.IOException;
import java.util.Vector;

public class SimhashGroup {
    Vector<SimHash> group;

    public SimhashGroup() {
        group = new Vector<>();
    }

    public void add(SimHash element){
        group.add(element);
    }

    public SimHash reduce() throws IOException {
        SimHash res = new SimHash(64);
        for (SimHash e :
                group) {
            for (int i = 0; i < 64; i++) {
                res.v[i] += e.v[i];
            }
        }
        res.setSimHash();
        return res;
    }
}

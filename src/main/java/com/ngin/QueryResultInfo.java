package com.ngin;

import commander.Command.NEvent;

public class QueryResultInfo {
    int qid;
    int id;
    float x;
    float y;

    QueryResultInfo(NEvent c) {
        qid = c.getInts(1);
        id = c.getInts(2);
        x = c.getFloats(0);
        y = c.getFloats(1);
    }

    @Override
    public String toString() {
        return String.format("QueryResultInfo qid:%d id:%d", qid, id, x, y);
    }    
}

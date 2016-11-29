package com.anvata.gankio.entity;

import java.util.List;

/**
 * GankApi请求结果实体
 */

public class Root {
    private boolean error;

    private List<Results> results;

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean getError() {
        return this.error;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public List<Results> getResults() {
        return this.results;
    }

}
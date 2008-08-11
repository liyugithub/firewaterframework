package org.firewaterframework.mappers.jdbc;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 19, 2008
 * Time: 7:32:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueryHolder
{
    String query;
    String keyName;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}

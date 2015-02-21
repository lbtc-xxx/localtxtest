package org.nailedtothex.localtxtest;

import javax.annotation.Resource;
import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Named
public class MyItemReader extends AbstractItemReader {

    @Resource(lookup = "java:jboss/datasources/MyDS")
    DataSource ds;
    Connection cn;
    Statement st;
    ResultSet rs;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        // We set jberet.local-tx=true in job xml so we can use JDBC resources outside transaction
        cn = ds.getConnection();
        st = cn.createStatement();
        rs = st.executeQuery("select data from src order by data");
    }

    @Override
    public void close() throws Exception {
        try {
            rs.close();
        } catch (Exception e) {
            // nop
        }
        try {
            st.close();
        } catch (Exception e) {
            // nop
        }
        try {
            cn.close();
        } catch (Exception e) {
            // nop
        }
    }

    @Override
    public Object readItem() throws Exception {
        if (rs.next()) {
            return rs.getInt(1);
        }
        return null;
    }
}

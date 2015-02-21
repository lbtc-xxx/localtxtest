package org.nailedtothex.localtxtest;

import javax.annotation.Resource;
import javax.batch.api.AbstractBatchlet;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Named
public class PrepareBatchlet extends AbstractBatchlet {
    @Resource(lookup = "java:jboss/datasources/MyDS")
    DataSource ds;

    @Override
    @Transactional
    public String process() throws Exception {
        try (Connection cn = ds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate("drop table if exists src");
                st.executeUpdate("create table src (data int)");
                st.executeUpdate("drop table if exists dest");
                st.executeUpdate("create table dest (data int)");
            }

            try (PreparedStatement ps = cn.prepareStatement("insert into src (data) values (?)")) {
                for (int i = 1; i <= 100; i++) {
                    ps.setInt(1, i);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
        return null;
    }
}

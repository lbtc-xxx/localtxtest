package org.nailedtothex.localtxtest;

import javax.annotation.Resource;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
public class MyItemWriter extends AbstractItemWriter {

    private static final Logger log = Logger.getLogger(MyItemWriter.class.getName());

    @Inject
    UserTransaction ut;
    @Resource(lookup = "java:jboss/datasources/MyDS")
    DataSource ds;

    @Override
    public void writeItems(List<Object> items) throws Exception {
        // We set jberet.local-tx=true in job xml so we have to manipulate transactions with UserTransaction
        ut.begin();
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement("insert into dest (data) values (?)")) {
            for (Object o : items) {
                ps.setInt(1, (Integer) o);
                ps.addBatch();
                log.log(Level.FINE, "added to batch: {0}", o);
            }
            ps.executeBatch();
            ut.commit();
            log.info("committed");
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }
}

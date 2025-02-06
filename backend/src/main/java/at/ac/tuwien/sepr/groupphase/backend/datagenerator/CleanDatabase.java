package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Profile({"generateData", "test", "unitTest"})
@Component("CleanDatabase")
public class CleanDatabase {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final JdbcTemplate jdbcTemplate;

    public CleanDatabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * method which pulls out and clears tables. Also, it restarts every ID sequences.
     */
    @PostConstruct
    public void truncateAllTablesAndRestartIds() {
        LOGGER.info("truncateAllTablesAndRestartIds()");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE ");
        List<String> tableNames = jdbcTemplate.query("SELECT table_name FROM information_schema.tables WHERE table_schema = SCHEMA()",
            (rs, rowNum) -> rs.getString(1));
        tableNames.forEach(tableName -> jdbcTemplate.execute(String.format("TRUNCATE TABLE %s RESTART IDENTITY", tableName)));
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

}

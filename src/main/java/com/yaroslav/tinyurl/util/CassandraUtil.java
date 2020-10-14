package com.yaroslav.tinyurl.util;

import com.datastax.oss.driver.api.core.CqlSession;
import com.yaroslav.tinyurl.json.UserClicks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import javax.annotation.PostConstruct;
import java.util.List;
import com.datastax.oss.driver.api.core.type.DataTypes;
import java.util.stream.Collectors;
import com.datastax.oss.driver.api.core.cql.Row;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.dropTable;

@Component
public class CassandraUtil {
    private static final String USERS_CLICKS_TABLE = "users_clicks";
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String CLICKS_COLUMN = "clicks";

    @Autowired
    private CqlSession cassandraSession;

    public  void insertUserClicks(UserClicks user) {
        insertUserClicks(user.getId(), user.getName(), user.getClicks());
    }

    public void insertUserClicks(String userId, String userName) {
        insertUserClicks(userId, userName, 0);
    }

    public void insertUserClicks(String userId, String userName, int clicks) {
        cassandraSession.execute(
                insertInto(USERS_CLICKS_TABLE)
                        .value(ID_COLUMN, literal(userId))
                        .value(NAME_COLUMN, literal(userName))
                        .value(CLICKS_COLUMN, literal(clicks))
                        .ifNotExists()
                        .build());
    }

    public void incrementUserClicks(UserClicks user) {
        incrementUserClicks(user.getId());
    }

    public void incrementUserClicks(String userId) {
        incrementUserClicks(userId, 1);
    }

    public void incrementUserClicks(String userId, int incVal) {
        int userClicks = getUserClicks(userId).getClicks();
        cassandraSession.execute(
                update(USERS_CLICKS_TABLE)
                        .setColumn(CLICKS_COLUMN, add(
                                literal(userClicks),
                                literal(incVal)))
                        .whereColumn(ID_COLUMN).isEqualTo(literal(userId))
                        .build());
    }

    public List<UserClicks> getClicksSummary() {
        return cassandraSession.execute(
                selectFrom(USERS_CLICKS_TABLE).all()
                        .build())
                .all().stream().map(row -> new UserClicks(
                        row.getString(ID_COLUMN),
                        row.getString(NAME_COLUMN),
                        row.getInt(CLICKS_COLUMN)))
                .collect(Collectors.toList());
    }

    public UserClicks getUserClicks(String userId) {
        Row userClicksRow =  cassandraSession.execute(
                selectFrom(USERS_CLICKS_TABLE).all()
                        .whereColumn(ID_COLUMN).isEqualTo(literal(userId))
                        .build())
                .one();

        return UserClicks.parse(userClicksRow);
    }

    public void dropUsersClicksTable() {
        drop(USERS_CLICKS_TABLE);
    }

    public void drop(String tableName) {
        cassandraSession.execute(
                dropTable(tableName)
                        .ifExists()
                        .build());
    }

    @PostConstruct
    public void createUsersClicksTable() {
        cassandraSession.execute(
                SchemaBuilder.createTable(
                        "clicks", USERS_CLICKS_TABLE).ifNotExists()
                        .withPartitionKey(ID_COLUMN, DataTypes.TEXT)
                        .withColumn(NAME_COLUMN, DataTypes.TEXT)
                        .withColumn(CLICKS_COLUMN, DataTypes.INT)
                        .build());
    }
}

package ORM.Queries;
import ORM.Base.Entity;

import java.sql.SQLSyntaxErrorException;
import java.util.*;


//TODO change string varargs to generic varargs for easier use
public class SelectQuery implements Query,QueryLanguage{
    private final String operation = "SELECT";
    List<String> targets = new ArrayList<>();
    List<String> tables = new ArrayList<>();
    Map<String,String> conditions = new LinkedHashMap<>();
    String query;
    Entity entity;

    @Override
    public void addTargets(String... targets) {
        this.targets.addAll(Arrays.asList(targets));
    }

    @Override
    public void addTables(String... tables) {
        this.tables.addAll(Arrays.asList(tables));
    }

    //TODO create method to add more than one condition at once
    @Override
    public <T> void addCondition(T column, T value) {
        this.conditions.put(column.toString(),value.toString());
    }

    public void setEntity (Entity entity) { this.entity = entity; }

    //TODO multiple tables are already part of join - needs to be refactored
    @Override
    public String buildQuery() {
        try{
            StringBuilder selectQuery = new StringBuilder();
            boolean first = true;

            selectQuery.append(operation).append(space);                // "SELECT" + " "
            if (this.targets.isEmpty()) {
                selectQuery.append(all).append(space);                  // "*" + " "
            } else {
                if(this.targets.size() == 1) {
                    selectQuery.append(targets.get(0)).append(space);   // "targetColumn" + " "
                } else {
                    for (String target : targets) {
                        if(!first) {
                            selectQuery.append(comma).append(space);    // "," + " "
                        } else {
                            first = false;
                        }
                        selectQuery.append(target);                     // "--targetColumn--"
                    }
                }
            }

            selectQuery.append(space).append(from);                     // " " + "FROM" + " "

            if (this.tables.isEmpty()) {
                throw new SQLSyntaxErrorException("No tables set in Query.");
            } else {
                if(this.tables.size() == 1) {
                    selectQuery.append(space).append(tables.get(0));   // " " + "targetColumn"
                } else {
                    first = true;
                    selectQuery.append(space).append(tables.get(0));
                    for (String table : tables.subList(1,tables.size())) {
                        if(first) {
                            // join + ( + table2 + , + ... + tableN + ) + on + ( + table1 + . + columnPK1 + = + table2 + . + columnPK2 + AND + ... + )
                            first = false;
                            selectQuery.append(space).append(join).append(space);
                            selectQuery.append(brOpen);
                        } else {
                            selectQuery.append(comma).append(space);
                        }
                        selectQuery.append(table);
                    }
                    selectQuery.append(brClosed).append(space).append(on).append(space).append(brOpen);

                    first = true;
                    for(String table : tables.subList(1,tables.size())) {
                        if(first) {
                            first = false;
                        } else {
                            selectQuery.append(space).append(and).append(space);
                        }//TODO currently only works with a single PK - needs to be expanded to multiple calls for a single table in the loop.
                        selectQuery.append(table).append(dot).append(entity.getExternalFields()[0].getColumnName()).append(space).append(eq).append(space).append(tables.get(0)).append(dot).append(entity.getPrimaryFields()[0].getColumnName());
                    }
                    selectQuery.append(brClosed);
                }
            }


            if (!this.conditions.isEmpty()) {
                selectQuery.append(space).append(where).append(space);                        // "WHERE" + " "

                if(this.conditions.size() == 1) {
                    selectQuery.append(tables.get(0)).append(dot).append(conditions.keySet().stream().findFirst().get())    // "column" +
                            .append(space).append(eq).append(space);                // + " " + "=" + " "
                    selectQuery.append(conditions.values().stream().findFirst().get());   // "value"
                } else {
                    conditions.entrySet().stream().findFirst().ifPresent(e ->
                            selectQuery
                                    .append(e.getKey())                             // "column" +
                                    .append(space).append(eq).append(space)          // + " " + "=" + " "
                                    .append(e.getValue()));

                    conditions.entrySet().stream().skip(1).forEach(e -> {
                        selectQuery.append(space).append(and).append(space);        // " " + "AND" + " "
                            selectQuery.append(e.getKey())                              // "column" +
                            .append(space).append(eq).append(space);            // + " " + "=" + " "
                            selectQuery.append(e.getValue());             // "value"
                });
                }
            }
            selectQuery.append(semicolon);                                          // ";"
            this.query = selectQuery.toString();
            System.out.println(this.query);
            return this.query;
        } catch (SQLSyntaxErrorException see) {
            see.printStackTrace();
            System.out.println("test in selectQuery");
        }
        return null;
    }

    @Override
    public void updateQuery() {}

    @Override
    public String getQuery() {
        return this.query;
    }
}
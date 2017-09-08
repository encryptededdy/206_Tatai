package tatai.app.util.queries;

import tatai.app.Main;

import java.sql.ResultSet;

public class QuestionLogQuery {
    private String SQLQuery;
    public String tempOutput;

    public QuestionLogQuery(long timeBound, boolean limitSet, String questionSet) {
        if (!limitSet) {
            SQLQuery = "SELECT * FROM questions WHERE date > " + timeBound;
        } else {
            SQLQuery = "SELECT * FROM questions WHERE date > " + timeBound + " AND questionSet = '" + questionSet + "'";
        }
    }

    public void execute() {
        ResultSet results = Main.database.returnOp(SQLQuery);
        StringBuilder buildS = new StringBuilder("");
        try {
            while (results.next()) {
                buildS.append("Entry "+results.getInt("questionID")+" - Q: "+results.getString("question")+" A: "+results.getString("answer")+" Result: "+results.getBoolean("correct")+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tempOutput = buildS.toString();
    }
}

import java.util.Date;

public class SolveInfo{
    private double solveTime;
    private String scramble;
    private Date solveDate;

    public SolveInfo(double st, String s, Date d){
        solveTime = st;
        scramble = s;
        solveDate = d;
    }

    public double getSolveTime(){
        return solveTime;
    }

    public String getScramble(){
        return scramble;
    }

    public Date getSolveDate(){
        return solveDate;
    }
}


import com.kantar.sessionsjob.Session;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTests {
    @Test
    public void t00_SessionConstructor_InvalidNumberOfInputs() {
        String[] row = {"", "", "", "", ""};
        try {
            new Session(row);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("Invalid count of input params", e.getMessage());
        }
    }

    @Test
    public void t01_SessionConstructor_UnexpectedInputs() {
        String[] row = {"A", "B", "C", "D"};
        try {
            new Session(row);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void t02_SessionFields_SetEndTimeToMax() {
        String[] row = {"1234", "101", "20200101180000", "Live"};
        try {
            Session session = new Session(row);
            session.setEndTimeToMax();
            Date endTime = session.getEndTime();
            Date expectedTime;
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                expectedTime = df.parse("20200101235959");
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date");
            }
            int result = endTime.compareTo(expectedTime);
            assertEquals(0, result, "End time was not set to max!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void t03_SessionFields_CalculateSessionEndTime() {
        String[] row0 = {"1234", "101", "20200101180000", "Live"};
        String[] row1 = {"1234", "102", "20200101183000", "Live"};
        try {
            Session session1 = new Session(row0);
            Session session2 = new Session(row1);
            session1.calculateEndTime(session2);

            Date expectedTime;
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                expectedTime = df.parse("20200101182959");
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date");
            }
            int result = session1.getEndTime().compareTo(expectedTime);
            assertEquals(0, result, "Unexpected end time!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void t04_SessionFields_CalculateDuration() {
        String[] row0 = {"1234", "101", "20200101180000", "Live"};
        String[] row1 = {"1234", "102", "20200101183000", "Live"};
        try {
            Session session1 = new Session(row0);
            Session session2 = new Session(row1);
            session1.calculateEndTime(session2);
            session1.calculateDuration();

            long expectedDuration = 30 * 60;
            assertEquals(expectedDuration, session1.getDuration(), "Unexpected duration!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

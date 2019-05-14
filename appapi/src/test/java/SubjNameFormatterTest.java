import exporter.formatter.ClassSectionTextFormatter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SubjNameFormatterTest {
    @DataProvider
    public Iterator<Object[]> provide() {
        final int len = 25;
        List<Object[]> arr = new ArrayList<>();
        arr.add(new Object[] {"Securitatea Informationala", "SI", len});
        arr.add(new Object[] {"Sisteme Incorporate", "SI", len});
        arr.add(new Object[] {"Algoritmi Avansati de conducere numerica", "AACN", len});
        arr.add(new Object[] {"Tehnologii de guernare electronica", "TGE", len});
        arr.add(new Object[] {"Programarea Applicatilor Distribuite", "PAD", len});
        arr.add(new Object[] {"Programarea Applicatilor Distribuite Si Modelate Tehnicii De Construire sowtware perntry mictoelectonica", "PAD", len});

        return arr.iterator();
    }

    @Test(dataProvider = "provide")
    public void testFormatSubjectName(String subjName, String subjNameAbbr, int maxLen) throws Exception{
        System.out.println("Subject name: " + subjName + " of length: " + subjName.length());
        System.out.println(ClassSectionTextFormatter.formatTwoStringSubjectName(subjName, "c", subjNameAbbr, maxLen));
    }


}
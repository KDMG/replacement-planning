package instancegraph;

import java.util.Comparator;

public class NumericStringComparator implements Comparator<String> {

	public int compare(String string1, String string2) {
        // TODO Auto-generated method stub
        int a = Integer.parseInt(string1);
        int b = Integer.parseInt(string2);
        return Integer.compare(a, b);
    }

}

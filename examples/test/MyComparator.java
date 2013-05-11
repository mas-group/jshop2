import JSHOP2.*;
import java.util.Comparator;

public class MyComparator implements Comparator
{
  private int varIdx;

  public MyComparator(int varIdxIn)
  {
    varIdx = varIdxIn;
  }

  public int compare(Object o1, Object o2)
  {
    Term[] t1 = (Term[])o1;
    Term[] t2 = (Term[])o2;

    //-- Get the numerical values of the two terms.
    int n1 = (int)((TermNumber)t1[varIdx]).getNumber();
    int n2 = (int)((TermNumber)t2[varIdx]).getNumber();

    //-- Compare them and return the result. This particular comparison
    //-- function prefers odd numbers to even numbers.
    if ((n1 & 1) == 1)
      return -1;

    else if ((n2 & 1) == 1)
      return 1;

    return 0;
  }
}

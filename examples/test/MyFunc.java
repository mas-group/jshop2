import JSHOP2.*;

public class MyFunc implements Calculate {
  public Term call(List l)
  {
    double sum = 0;

    while (l != null)
    {
      sum += ((TermNumber)l.getHead()).getNumber();
      l = l.getRest();
    }

    return new TermNumber(sum);
  }
}

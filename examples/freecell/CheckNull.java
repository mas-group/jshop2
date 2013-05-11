import JSHOP2.*;

public class CheckNull implements Calculate {
  public Term call(List l) {
    if (l.getHead().isNil())
      return l.getRest().getHead();

    return new TermNumber(0);
  }
}

package JSHOP2;

/** This is the common interface for code calls in call terms. Each call term,
 *  when initialized, will have a pointer to an instance of a class that
 *  implements this interface.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract interface Calculate
{
  /** This abstract function implements the code call associated with the class
   *  implementing this interface.
   *
   *  @param l
   *          the arguments for this code call as a list of terms.
   *  @return
   *          the result of the code call, as a term.
  */
  public abstract Term call(List l);
}

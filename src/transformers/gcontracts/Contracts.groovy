package transformers.gcontracts

/**
 * @author andre.steingress@gmail.com
 */
@Grab('org.gcontracts:gcontracts:1.1.3')
import org.gcontracts.annotations.*

@Invariant({ maxCapacity > 1 && maxCapacity < 30 && registeredStudents.size() <= maxCapacity })
class SchoolClass {

  private def maxCapacity
  private def registeredStudents = []

  def SchoolClass(def maxCapacity)  {
    this.maxCapacity = maxCapacity
  }

  @Requires({ !isRegistered(student) })
  @Ensures({ isRegistered(student) })
  def void plus(Student student)  {
    registeredStudents << student
  }

  def isRegistered(Student student)  {
    registeredStudents.contains(student)
  }

}

class Student {

}

def schoolClass = new SchoolClass(20)

0.upto(31) {
  def student = new Student()
  schoolClass.plus student
}

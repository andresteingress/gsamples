package transformers.spock

@Grab('org.spockframework:spock-core:0.4-groovy-1.7')
import spock.lang.Specification

class SomeSpec extends Specification {
 def "spread the words"() {
   setup:
      def l = ['these', 'are', 'my', 'words']

   when:
      def r  = l*.multiply(2)

   then:
      r == ['thesethese', 'areare', 'mymy', 'wordswords']
 }
}
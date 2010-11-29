System.out.println("Hello, World!");

println 'Hello, World!'

String name = 'Andre Steingress';
def name = 'Andre Steingress'

println "Hi, my name is ${name}!"

println """ Hi, my name is ${name} and
this is a rather long
string """

assert 0.5 == 1/2

// Simple Data Types

byte b1 = 42
Byte b2 = 42

short s1 = 20000
Short s2 = 20000

int i1 = 1000000000
Integer i2 = 1000000000
def i3 = 42

long l1 = 1000000000000000000
Long l2 = 1000000000000000000
def l3 = 42L

assert 2 instanceof Integer
assert !2.equals(4)
assert 3.5 instanceof BigDecimal
assert (7/2) instanceof BigDecimal

// String to Number Conversion

def string = "300"
assert string.isNumber() 

def i4 = string.toInteger()
def i5 = string as int
def i6 = string as Integer
def i7 = new Integer(string)
def i8 = Integer.parseInt(string)
def i9 = (int) java.text.NumberFormat.instance.parse(string) 

string = "300.5" 
def n1 = java.text.NumberFormat.instance.parse(string) 

def n2 = string.toFloat()
def n3 = string.toDouble()
def n4 = string.toBigDecimal()

// Strings

// normal strings
def firstname = 'Kate'
assert firstname instanceof String

def surname = "Bush"
assert firstname * 2 == 'KateKate'
def fullname = firstname + ' ' + surname
assert fullname == 'Kate Bush'

fullname = "$firstname $surname"
assert fullname instanceof GString
assert fullname == 'Kate Bush'
assert fullname - firstname == ' Bush'
assert fullname.padLeft(10) == ' Kate Bush'


// more String operations

assert 'string'.reverse() == 'gnirts'
assert 'two words'.split().reverse().join(' ') == 'words two'

string = 'Yoda said, "can you see this?"'
revwords = string.split(' ').reverse().join(' ')
assert revwords == 'this?" see you "can said, Yoda'

revwords = string.replaceAll(/(\w*) (\w*)(\W*)/ * 3, '$2 $1$3$8 $7$6$5 $4$9')
assert revwords == 'said Yoda, "this see you can?"'

// Built-In Support for Regular Expressions

assert "Hello World!" =~ /Hello/	// Find operator
assert "Hello World!" ==~ /Hello\b.*/ // Match operator

def p = ~/Hello\b.*/	// Pattern operator
assert p.class.name == 'java.util.regex.Pattern'

def input = "Voting is open between 01 Nov 2008 and 04 Nov 2008"

def regexp = /\d\d? ... \d{4}/
def matches = input =~ regexp

println matches[0] + " - " + matches[1]

def dateFormatGroups = /(\d\d?) (...) (\d{4})/
def matchingGroups = (input =~ dateFormatGroups)

assert matchingGroups[1][1] == '04'

// Dates

import static java.util.Calendar.getInstance as now
import java.util.GregorianCalendar as D
import org.codehaus.groovy.runtime.TimeCategory

println new D(2007,11,25).time
println now().time

def date = new Date() + 1
println date

use(TimeCategory) {
println new Date() + 1.hour + 3.weeks - 2.days
}

dateStr = "1998-06-03"
date = Date.parse("yyyy-MM-dd", dateStr)
println 'Date was ' + date.format("MMM/dd/yyyy")

def holidays = boxingDay..newYearsEve

// Collective Data Types

def list = [3, new Date(), 'Jan']
assert list + list == list * 2

def map =[a:1,b:2]
assert map['a'] == 1 && map.b == 2

def letters = 'a'..'z'
def numbers = 0..<10

assert letters.size() + numbers.size() == 36

// Support for Java Features

Anonymous Classes
Annotations

import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PerformanceMonitored {
    String value()
}

@PerformanceMonitored('fast')
class Person {
   String name

}

Person.class.isAnnotationPresent(PerformanceMonitored.class)
Person.class.getAnnotation(PerformanceMonitored.class).value()

Generics

class Pair<T1, T2> {
    T1 element1
    T2 element2
}

interface Function<S,T>  {
    T op(S s)  
}

def adder = [op: { Pair<Integer, Integer> p -> p.element1 + p.element2 } ] as Function

adder.op(new Pair(element1: 1, element2: 2))

// Loops

for (int i = 0; i < n; i++) { } 
for (Type item : iterable) { }
for (item in iterable) { }

// Groovy Truth

if (1) // ...
if (object) // ...
if (collection) // ...
if (map) // ...
if (matcher) // ...

// Operator Overloading

class Person {

   def or(other)  {
      println other
   }

   @Override
   String toString()  { "i am unique" }
}

def p = new Person()
def p2 = new Person()

p | p2
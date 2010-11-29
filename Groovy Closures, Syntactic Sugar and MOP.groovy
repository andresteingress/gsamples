// Closures

def doubleNum = { num -> num * 2 }
println doubleNum(3) // => 6

def divideResult = { num, op -> op(num) / 2 }
divideResult(6, doubleNum)

// Closures and Maps

map = ['a': 1, 'b': 2]
map.each {key, value -> map[key] = value * 2}
assert map == ['a': 2, 'b': 4]

def doubler = {key, value -> map[key] = value * 2}
map.each(doubler)

assert map == ['a': 4, 'b': 8]

def doubleMethod(entry) { 
  map[entry.key] = entry.value * 2
} 

doubler = this.&doubleMethod
map.each(doubler)
assert map == ['a': 8, 'b': 16]

// Closures and Lists
assert [1, 2, 3].grep{ it < 3 } == [1, 2]
assert [1, 2, 3].any{ it % 2 == 0 }
assert [1, 2, 3].every{ it < 4 }
assert (1..9).collect{it}.join() == '123456789'
assert (1..4).collect{it * 2}.join() == '2468'

def add	= { x, y -> x + y }
def mult = { x, y -> x * y }
assert add(1, 3) == 4
assert mult(1, 3) == 3

def min = { x, y -> [x, y].min() }
def max = { x, y -> [x, y].max() }

def triple = mult.curry(3)
assert triple(2) == 6

def atLeastTen = max.curry(10)
assert atLeastTen(5) == 10
assert atLeastTen(15) == 15

// Groovy Synthactic Sugar
def someOp(x, y,z) { println x; println y; }

someOp(1,2,3)
someOp 1,2,3

someOp (1,3) {
   someOp (4,5) {
       println bla
   }
}

// Meta-Class and Groovy MOP

def obj = new Object() {
    String title
    String description
}

obj.metaClass.methods.each { println it.name }
def method = obj.metaClass.getMetaMethod('toString',null)
println method.invoke(obj,null)

def property = obj.metaClass.getMetaProperty('class')
println property.getProperty(obj)

// invokeMethod
class DynamicObject {
def invokeMethod(String name, args){
println "$name invoked!"
}
}
def obj = new DynamicObject()
obj.foo()
obj.bar()

// set/getProperty

class Expandable {
    def storage = [:]
    
    def getProperty(String name) { 
        storage[name]
    } 
    
    void setProperty(String name, value) {
        storage[name] = value
    }
}

def e = new Expandable() 
e.foo = "bar" 
println e.foo

// ExpandoMetaClass

class Foo {}

Foo.metaClass.methodMissing = {
String name, args ->
  Foo.metaClass."$name" = {
    Object[] varArgs -> name
  }
  return name
}

def foo = new Foo()
assert "hello" == foo.hello()
assert "goodbye" == foo.goodbye()

// MarkupBuilder
import groovy.xml.*
def page = new MarkupBuilder()
page.html {
    head {
        title 'Hello'
    }
    body {
        ul { 
            for (count in 1..5) {
                li "world $count"
            }
        }
    }
}

// XMLSlurper

def records = new XmlSlurper().parseText("""
<records>
    <car>Toyota</car>
    <car>Nissan</car>
</records>
""")

records.car.size()
records.car[0].text()
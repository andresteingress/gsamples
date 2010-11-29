package mini

class SessionFactory  {

  private def storage

  def SessionFactory(def storage)  {
    this.storage = storage
  }

  def createNewSession()  {
    new Session(this)
  }

  def newStorageConnection()  {
    return this.storage
  }
}

class Session {

  private def sessionFactory

  private def keyMap
  private def keyModificationListMap

  def Session(def sessionFactory)  {
    this.sessionFactory = sessionFactory
    this.keyMap = [:]
    this.keyModificationListMap = [:]
  }

  def propertyChanged(def obj)  {
    if (!keyModificationListMap[obj.class]) keyModificationListMap[obj.getClass()] = []
    keyModificationListMap[obj.class] = obj.id
  }

  def save()  {
    println "going to save all modified objects"

    keyModificationListMap.each {
      def domainClass ->
        println "Modified objects: ${domainClass}"
    }
  }

  def delete(def obj)  {
     // TODO: implement different state changing events to signal deletes, saves, updated etc.
  }

  def load(def domainClass, long identifier)  {
    if (keyMap[domainClass] && keyMap[domainClass][identifier]) return keyMap[domainClass][identifier]

    // load object from underlying storage
    def connection = sessionFactory.newStorageConnection()
    def loadedObj = connection[domainClass][identifier]
    if (!loadedObj) throw new Exception("Object with ${identifier} could not be found")

    // register object in entity map
    if (!keyMap[domainClass]) keyMap[domainClass] = [:]
    if (!keyMap[domainClass][identifier]) keyMap[domainClass][identifier] = loadedObj

    injectIdProperty(loadedObj, identifier)
    injectPropertyChangedCallback(loadedObj)

    return loadedObj
  }

  private def injectIdProperty(loadedObj, long identifier) {
    loadedObj.metaClass.getId = {-> identifier }
  }

  private def injectPropertyChangedCallback(loadedObj)  {
    loadedObj.metaClass.setProperty = { String name, Object value ->
      println "${name} invoked"

      owner.propertyChanged(loadedObj)

      def metaProperty = delegate.metaClass.getMetaProperty(name)
      if(metaProperty) metaProperty.setProperty(delegate, value)
    }
  }
}

class Person { String name }

def storage = [:]
storage[Person.class] = [:]
storage[Person.class][1l] = new Person(name: 'Max Mustermann')
storage[Person.class][2l] = new Person(name: 'Erika Mustermann')

def sessionFactory = new SessionFactory(storage)
def session = sessionFactory.createNewSession()

def person = session.load(Person, 1)

println person.id
person.name = 'Another Name'
println person.name

session.save()

def anotherPerson = session.load(Person, 1)
assert person == anotherPerson

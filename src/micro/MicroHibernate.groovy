class SessionFactory {

    private def storage

    def SessionFactory(def storage)  {
        this.storage = storage
    }

    def newStorageConnection()  {
        return storage
    }
    
    def newSession()  {
        return new Session(this)
    }
}

class Session {

    private def sessionFactory

    def Session(def sessionFactory) { this.sessionFactory = sessionFactory }

    def snapshots = [:] // a Map(Domain-Class, Map(Identifier, Properties))
    def identityMap = [:] // a Map(Domain-Class, Map(Identifier, ObjectRef))
    def modifiedPersistentObjects = [:] // a Map(Domain-Class, List(Identifier))
    
    def propertyChanged(def obj)  {
        if (!modifiedPersistentObjects[obj.getClass()]) modifiedPersistentObjects[obj.getClass()] = []
        
        modifiedPersistentObjects[obj.getClass()] << obj.id
    }
    
    def save(def obj) {
        if (modifiedPersistentObjects[obj.getClass()] && modifiedPersistentObjects[obj.getClass()][obj.id])  {
           // something changed
           
           // what changed? did a simple property change, did the contents of a relationship change? etc.
           
        } else if (!obj.metaClass.hasProperty("id"))  {
           // this is a transient object
           
           // get the id for this object
        }
    }
    
    def discard()  {
        // reverts all changes
        modifiedPersistentObjects.each {
            def domainClassType, def listOfIds -> 
                def l = listOfIds.clone()
                
                l.each {
                    def alteredObj = identityMap[domainClassType][it]
                    def oldPropertyValues = snapshots[domainClassType][it]
                    
                    oldPropertyValues.each { name, value -> if (name in ['name']) alteredObj[name] = value }
                }
        }
        
        clear()
    }
    
    def flush()  {
        if (modifiedPersistentObjects) println "flushing ..."
        
        clear()
    }
    
    def clear()  {
        snapshots.clear()
        identityMap.clear()
        modifiedPersistentObjects.clear()
    }
    
    def close()  {
        flush()
        clear()
        
        println "session is cleared."
    }
    
    def load(def domainClassType, Long identifier)  {
        
        if (identityMap[domainClassType] && identityMap[domainClassType][identifier])  {
            return identityMap[domainClassType][identifier]
        }
        
        def conn = sessionFactory.newStorageConnection()
        def loadedObj = conn[domainClassType][identifier]
        if (!loadedObj) throw new Exception("Object of type ${domainClassType} with id ${identifier} could not be found!")
        
        if (!snapshots[domainClassType]) snapshots[domainClassType] = [:]
        if (!identityMap[domainClassType]) identityMap[domainClassType] = [:]
        
        snapshots[domainClassType][identifier] = loadedObj.properties
        identityMap[domainClassType][identifier] = loadedObj
        
        loadedObj.metaClass.getId = { -> identifier } 
        loadedObj.metaClass.setProperty = { String name, Object arg ->
            def metaProperty = delegate.metaClass.getMetaProperty(name)

            if (metaProperty)  {
                owner.propertyChanged(loadedObj)
                
                metaProperty.setProperty(delegate, arg)
            }
        }
        
        return loadedObj
    }
    
    def query(def q)  {
        def ids = executeQuery(q) // returns a list of ids
        // lookup ids in cache if not found load them from storage/DB
    }
}

def storage = [:]

class Person {
    String name
}

storage[Person] = [:]
storage[Person][1 as Long] = new Person(name: 'Max Mustermann')
storage[Person][2 as Long] = new Person(name: 'Erika Mustermann')

def sessionFactory = new SessionFactory(storage)
def session = sessionFactory.newSession()

def person = session.load(Person, 1)
person.name = 'Franz Xaver Gabler'

session.discard()
session.close()

person.dump()
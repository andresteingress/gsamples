import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * This is a workshop example for ORM mapping framework basics.
 */
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

    static Log log = LogFactory.getLog(Session)

    private def sessionFactory

    def Session(def sessionFactory) { this.sessionFactory = sessionFactory }

    def snapshots = [:] // a Map(Domain-Class, Map(Identifier, Properties))
    def identityMap = [:] // a Map(Domain-Class, Map(Identifier, ObjectRef))
    def modifiedPersistentObjects = [:] // a Map(Domain-Class, List(Identifier))
    
    def propertyChanged(def obj)  {
        if (!modifiedPersistentObjects[obj.getClass()]) modifiedPersistentObjects[obj.getClass()] = []

        modifiedPersistentObjects[obj.getClass()] << obj.id

        log.info "propertyChanged of object: ${obj} with id ${obj.id}"
    }
    
    def save(def obj) {
        if (!obj.metaClass.hasProperty("id"))  {
           log.info "Going to save transient object ${obj} ..."
           // this is a transient object
           def conn = sessionFactory.newStorageConnection()
           def id = 1

           if (conn[obj.getClass()]) id = conn[obj.getClass()].keySet().max() + 1
           obj.metaClass.getId = { -> id }

           identityMap[obj.getClass()][id] = obj

           log.info "Attached ${obj} now has id ${obj.id}"
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
        if (modifiedPersistentObjects) {
          snapshots.clear()
        }
        
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
    }
    
    def load(Class<?> domainClassType, Long identifier)  {
        
        if (identityMap[domainClassType] && identityMap[domainClassType][identifier])  {
            return identityMap[domainClassType][identifier]
        }
        
        def conn = sessionFactory.newStorageConnection()
        def loadedObj = conn[domainClassType][identifier]
        if (!loadedObj) throw new Exception("Object of type ${domainClassType} with id ${identifier} could not be found!")
        
        if (!snapshots[domainClassType]) snapshots[domainClassType] = [:]
        if (!identityMap[domainClassType]) identityMap[domainClassType] = [:]


        def properties = loadedObj.getProperty("props")
        snapshots[domainClassType][identifier] = properties.inject([:], { m, property -> m[property] = loadedObj[property]; m })

        log.info "create snapshot of ${domainClassType} id ${identifier} with properties ${snapshots[domainClassType][identifier]}"

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

    String toString() { name }

    static props = ['name']
}

storage[Person] = [:]
storage[Person][1 as Long] = new Person(name: 'Max Mustermann')
storage[Person][2 as Long] = new Person(name: 'Erika Mustermann')

def sessionFactory = new SessionFactory(storage)
def session = sessionFactory.newSession()

def person = session.load(Person, 1)
person.name = 'Franz Xaver Gabler'

person = new Person(name: 'John Doe')
session.save(person)

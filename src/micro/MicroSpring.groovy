package micro

class ImageService {

  def imageLibraryServiceFacade

  def void rescale(def image, float scale)  {

  }
}

class ImageOperationsWithImageIOImpl {

  def void rescale(byte[] data, float scale)  {
     // todo: implement rescaling logic
  }
}



// second attempt: create dependencies with a simple program initialization method using Dependency Injection

def void init()  {
  def imageOperations = new ImageOperationsWithImageIOImpl()
  def imageService = new ImageService()
  imageService.imageLibraryServiceFacade = imageOperations
}

// third attempt: Dependencies are configured via external configuration files

def configFile = """
   <beans>
      <bean id='imageService' class='micro.ImageService'>
          <property name='imageLibraryServiceFacade' ref='nativeImageLibraryFacade'/>
      </bean>

      <bean id='nativeImageLibraryFacade' class='micro.ImageOperationsWithImageIOImpl'/>
   </beans>
"""

class BeanContainer {

  def beanCache = [:]

  def loadBeans(config)  {
    def beans = new XmlSlurper().parseText(config)

    beans.bean.each {
      def beanDefinition ->
        beanCache[beanDefinition.@id as String] = getClass().getClassLoader().loadClass(beanDefinition.@class.toString()).newInstance()
    }

    resolveProperties beans
  }

  def resolveProperties(beans)  {
    beans.bean.each {
      def beanDefinition ->
        beanDefinition.property.each {
          def propertyDefinition ->
            String propertyName = propertyDefinition.@name
            String ref = propertyDefinition.@ref

            beanCache[beanDefinition.@id as String][propertyName] = beanCache[ref]
        }
    }
  }

}

def container = new BeanContainer()
container.loadBeans configFile

assert container.beanCache['nativeImageLibraryFacade'] != null
assert container.beanCache['imageService'] != null
assert container.beanCache['imageService']."imageLibraryServiceFacade" != null



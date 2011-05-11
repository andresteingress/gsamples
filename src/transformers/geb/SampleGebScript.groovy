package transformers.geb

/**
 * @author andre.steingress@gmail.com
 */
@Grapes([
    @Grab("org.codehaus.geb:geb-core:latest.release"),
    @Grab("org.seleniumhq.selenium:selenium-firefox-driver:latest.release")
])
import geb.Browser
Browser.drive("http://diepresse.com") {
    assert title == "Google"

}


# Jetpack - Advanced Drop Target
(powered by iDA Mediafoundry)

Use Touch UI drop targets in a more flexible way to support array fields and composite multifields.

## Modules

The main parts of this projects are:

* core: Java bundle containing all core functionality like OSGi services, Sling Models and WCMCommand.
* ui.apps: contains the /apps part containing the html, js, css and .content.xml files.


## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle


## Testing

There are three levels of testing contained in the project:

unit test in core: this show-cases classic unit testing of the code contained in the bundle. To test, execute:

    mvn clean test

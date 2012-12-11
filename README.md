These are proof-of-concepts for JBoss AS 7 with using Gemini Blueprint.

Build with mvn clean package, deploy with mvn jboss-as:deploy.

To install Gemini Blueprint to JBoss, you'll need

- get the [aopalliance bundle](http://ebr.springsource.com/repository/app/bundle/version/detail?name=com.springsource.org.aopalliance&version=1.0.0)
- get the [commons-logging bundle](http://ebr.springsource.com/repository/app/bundle/version/detail?name=com.springsource.org.apache.commons.logging&version=1.1.1)
- get the [spring-core](http://ebr.springsource.com/repository/app/bundle/version/detail?name=org.springframework.core&version=3.1.3.RELEASE)
- get [spring-aop](http://ebr.springsource.com/repository/app/bundle/version/detail?name=org.springframework.aop&version=3.1.3.RELEASE)
- get [spring-beans](http://ebr.springsource.com/repository/app/bundle/version/detail?name=org.springframework.beans&version=3.1.3.RELEASE)
- get [spring-context](http://ebr.springsource.com/repository/app/bundle/version/detail?name=org.springframework.context&version=3.1.3.RELEASE)
- get [spring-asm](http://ebr.springsource.com/repository/app/bundle/version/detail?name=org.springframework.asm&version=3.1.3.RELEASE&searchType=bundlesBySymbolicName&searchQuery=org.springframework.asm)
- get [spring-expression](http://ebr.springsource.com/repository/app/bundle/version/detail?name=org.springframework.expression&version=3.1.3.RELEASE&searchType=bundlesBySymbolicName&searchQuery=org.springframework.expression)
- install the above
- get the actual [gemini files](http://www.eclipse.org/gemini/blueprint/download/)
- install gemini-blueprint-io
- install gemini-blueprint-core
- install gemini-blueprint-extender
 
Then you'd be good to go.

Note that Gemini Blueprint is not directly interchangeable with other Blueprint implementations right now due to it [not exporting org.osgi.service.blueprint](https://bugs.eclipse.org/bugs/show_bug.cgi?id=351755) yet.

Scope of these tests would be

<table>
    <tr>
        <th>Capability</th>
        <th>Status</th>
        <th>Notes</th>
    </tr>
    <tr>
        <td>OSGi bundle</td>
        <td>OK</td>
        <td></td>
    </tr>
    <tr>
        <td>OSGi composite bundle</td>
        <td>OK</td>
        <td></td>
    </tr>
    <tr>
        <td>war - osgibundle communication</td>
        <td>OK</td>
        <td>Using ServiceTracker, which is not optimal</td>
    </tr>
    <tr>
        <td>ejb - osgibundle communication</td>
        <td>tbd</td>
        <td></td>
    </tr>
    <tr>
        <td>wab (OSGi war)</td>
        <td>tbd</td>
        <td>Note really recommended in JBoss 7.1. JBoss does not directly support at that version, but
		forks a Jetty process for these, so they don't get the full set on features.
		Support will be in 7.2.</td>
    </tr>
    <tr>
        <td>wab - osgibundle communication</td>
        <td>tbd</td>
        <td></td>
    </tr>
    <tr>
        <td>JNDI with OSGi bundle</td>
        <td>OK</td>
        <td>JBoss 7.1 does not support, needs <a href="http://aries.apache.org/modules/jndiproject.html">Aries JNDI</a> installed</td>
    </tr>
    <tr>
        <td>JMS with OSGi bundle</td>
        <td>tbd</td>
        <td></td>
    </tr>
    <tr>
        <td>JTA with OSGi bundle</td>
        <td>tbd</td>
        <td></td>
    </tr>
    <tr>
        <td>JDBC with OSGi bundle</td>
        <td>OK</td>
        <td></td>
    </tr>
    <tr>
        <td>OSGi security</td>
        <td>-</td>
        <td>Related to bundle signing, similar to what applets have. JBoss does not implement.</td>
    </tr>
    <tr>
        <td>Petclinic app - bundle communication</td>
        <td>OK</td>
        <td>Using ServiceTracker, which is not optimal</td>
    </tr>
</table>


For JBoss 7.1.1, this WILL require Apache Aries JNDI to work.

See: http://stackoverflow.com/questions/10483740/cant-lookup-osgi-services-through-jndi

To have Apache Aries JNDI, either enable these capabilities for OSGi:

                <capability name="org.apache.aries:org.apache.aries.util:0.4"/>

                <capability name="org.apache.aries.proxy:org.apache.aries.proxy:0.4"/>

		<capability name="org.apache.aries.jndi:org.apache.aries.jndi:0.3.1"/>


To have them downloaded from the internet every time, or download from:

http://search.maven.org/remotecontent?filepath=org/apache/aries/jndi/org.apache.aries.jndi/0.3.1/org.apache.aries.jndi-0.3.1.jar
http://search.maven.org/remotecontent?filepath=org/apache/aries/proxy/org.apache.aries.proxy/0.4/org.apache.aries.proxy-0.4.jar
http://search.maven.org/remotecontent?filepath=org/apache/aries/org.apache.aries.util/0.4/org.apache.aries.util-0.4.jar

and install as bundles.

These two error messages are expected:


13:47:38,956 INFO  [org.apache.aries.jndi.startup.Activator] (MSC service thread 1-11) It was not possible to register an InitialContextFactoryBuilder with theNamingManager because another builder called org.jboss.as.naming.InitialContextFactoryBuilder was already registered. Support for calling new InitialContext() will not be enabled.: java.lang.IllegalStateException: InitialContextFactoryBuilder already set

13:47:38,968 INFO  [org.apache.aries.jndi.startup.Activator] (MSC service thread 1-11) It was not possible to register an ObjectFactoryBuilder with the NamingManager because another builder called org.jboss.as.naming.InitialContextFactoryBuilder was already registered. Looking up certain objects may not work correctly.
: java.lang.IllegalStateException: ObjectFactoryBuilder already set
        at javax.naming.spi.NamingManager.setObjectFactoryBuilder(NamingManager.java:111) [rt.jar:1.7.0_09]
        at org.apache.aries.jndi.startup.Activator.start(Activator.java:86)
        at org.apache.aries.jndi.priv.Activator.start(Activator.java:38)


Note! To enable the aries capabilities, you might need to configure a proxy.

This would be done by

set JAVA_OPTS=%JAVA_OPTS% -Dhttps.proxyHost=YOUR_PROXY_HOST -Dhttps.proxyPort=YOUR_PROXY_PORT -Dhttp.proxyHost=YOUR_PROXY_HOST -Dhttp.proxyPort=YOUR_PROXY_PORT

in standalone.bat, right before echoing bootstrap environment.

For MySQL/XA tests, see instructions for configuring at https://community.jboss.org/wiki/DataSourceConfigurationInAS7

Note that the defaults don't configure XA. See last comments on the thread (second page) for those.

Basically, what is needed is
 - install mysql
 - download mysql connector
 - install it as a module, adding dependencies to JDBC and transaction api
 - define the two data sources - note that XA has different properties than non-XA
 
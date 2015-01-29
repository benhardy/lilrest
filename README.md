<p align="right"><a href="https://travis-ci.org/benhardy/lilrest"><img src="https://travis-ci.org/benhardy/lilrest.svg?branch=master" alt="Build Status"/></a> <a href="https://codecov.io/github/benhardy/lilrest?branch=master"><img src="https://codecov.io/github/benhardy/lilrest/coverage.svg?branch=master" alt="codecov.io"></a>
lilrest
=======

A little REST server.

Ties Jetty, RESTEasy, Jackson, Skife Config and [Guice](https://github.com/google/guice) together into an extensible little server.

It sets JDK 8 as a minimum baseline.

This is extremely basic and minimal, and is best used as a starting point for extension.

Philosophy
-----------
LilREST is pretty adamant about using Guice to wire things together, in an explicit manner. This encourages you to think about separation of concerns from the start.

This is a bit different to other worthy lightweight Java web containers such as the excellent [Spark](http://sparkjava.com/), in that it's really keen on using Guice Modules. That is, it's all about dividing your project into domains of concern, packing those up into Modules, and just running it.

 This results in a little more code up front, but because there is absolutely no magic involved, you can be sure that you won't be wiped out by much in the way of unexpected side effects from LilREST itself. 

How to use
----------
In a nutshell: add the following dependency to your pom.xml (or equivalent), then extend LilREST's JaxRsServer class.

```xml
        <dependency>
            <groupId>net.aethersanctum.lilrest</groupId>
            <artifactId>lilrest-server</artifactId>
            <version>0.1.2</version>
        <dependency>
```

Simple Demo
-----------
Probably the easiest way to get started is by extending LilREST's JaxRsServer class. The only method you have to implement is mainModule, which returns a ServletModule created by you, which does all the Guice configuration that you care about. You might like to install other modules, do some bindings, or even install some servlets.

Here's an example that creates a REST endpoint that says Hello. Admittedly this is a bit more ceremony than Sinatra-inspired systems like Spark, but in the context of separating things into modules from the get-go, it ain't so bad. Bear in mind that in the JAX-RS world, you create a controller by making a resource class, which has annotated methods to return data to the client. All the munging is done for you.

```java
    public class HelloResource {
        @Path("/hello")
        @GET
        public String hello() {
            return "Hello";
        }
    }
```
Now in a real controller you'd probably have a constructor into which you @Inject dependent services or other stuff. We'll get to that. For now let's get the ball rolling with our own server class, which has a main() method to get the ball rolling. It'll also create an (anonymous, in this case) module to bind the HelloResource in the way of our choosing, so the Server class knows what it's supposed to serve.

```java
    public class HelloServer extends JaxRsServer {
        public static void main(String[] args) {
            new HelloServer().start();
        }
        
        @Override
        protected ServletModule getMainModule() {
            return new ServletModule() {
                @Override
                public void configureServlets() {
                    bind(HelloResource.class);
                }
            };
        }
        
    }
```
And boom, you're done - if you run this and browse to <a href="http://localhost:8080/hello">/hello</a> you'll get a friendly greeting. Yay! And it's all very MVC. Well, mostly Controller at this point. Let's add a Model in there. We don't really care too much about the view here.

Returning Models
----------------
What if we had some model class, say, called Person, and wanted to render that as a good old guicy blob of JSON? Piece of cake.

```java
    public class Person {
        private final int age;
        private final String name;
        // hid constructor and getters, nothing unusual there.
    }
```
We could then augment our HelloResource class with a REST endpoint that, for example returns a Person given a name.
```java
    public class HelloResource {
        // why are all these people 42 years old?
        @Path("/person/{name}")
        @GET
        public String hello(@PathParam("name") String name) {
            return new Person(42, name);
        }
        
        @Path("/hello")
        @GET
        public String hello() {
            return "Hello";
        }
    }
```
We can then fetch anyone we like. If we hit http://localhost:8080/person/arthur in our browser, we'll get back a little old blob of json, which is the serialized Person object we created, with no further work required, thanks to Jackson.
```json
{"name":"arthur",age:42}
```
Configuration
-------------
LilREST mostly uses the pretty damn excellent <a href="https://github.com/brianm/config-magic">Config Magic</a> for dealing with configuration. As you'll see by its home page, Config allows you define and annotate an interface with configuration
properties in a typesafe way. Which is so hot right now. So hot.

LilREST uses Config Magic for its own config, and your apps extending it can use the same mechanism for theirs. You don't have to, of course, but can you remember a web application that never needed to be configured? Anyone? Bueller? Nope. So here's what you do.

You can have as many config classes as you want. <b>Or none</b>. I like to do one per module, myself, but then again I'm a Separation Of Concerns evangelist. 

The config module needs to know where your configuration properties file is. You can specify its path with as the system property "useConfig". That can either refer to a file on the filesystem or, if prepended with "classpath:", on the classpath. If no config file is specified, then we try to load config properties from system properties. e.g.:
```
   -DuseConfig=configdir/app.properties              # local file
   -DuseConfig=classpath:conf/app.properties         # classpath
   -Dspam.flavor=revolting -Dspam.cans=400293        # system properties
```
Define yourself an annotated configuration interface, e.g.:
```java
public interface SpamConfig {
    @Config("spam.cans")
    @Default("0")
    int spamCans();

    @Config("spam.flavor")
    @Default("pink")
    String flavor();

    @Config("spam.when-to-toss")
    @Default("300ms")
    TimeSpan idleTimeout();
    // that's right.. config magic handles TimeSpans with fancy units
}
```
Then! Add a provider method to your module to tell the config module to bring your config to life! You can then use it in your classes in the usual way.
```java
    public class SpamModule extends AbstractModule {
        public void configure() {
            bind(SpamResource.class);
        }
        
        @Provides
        public SpamConfig serverConfiguration(ConfigFactory factory) {
              return factory.extract(SpamConfig.class);
        }
    }
    
    // let's put that SpamConfig to use in a slightly more fleshed out example
    @Singleton
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spam")
    public final class SpamResource {
        private static final Logger LOG = LoggerFactory.getLogger(SpamResource.class);
        private final SpamConfig spamConfig;

        @Inject
        SpamResource(SpamConfig config) {
            LOG.debug("Creating SpamResource");
            this.spamConfig = config;
        }

        @GET
        @Path("/flavor")
        @Nonnull
        public String flavor() {
            return "Today's spam flavor is " + spamConfig.flavor();
        }
    }
```
Other useful info.
------------------
- [Guice best practices](https://github.com/google/guice/wiki/KeepConstructorsHidden)

That's pretty much it.
----------------------
Right, off you go then. Have fun.

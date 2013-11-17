## Downloading without Maven

We understand that not everyone uses Maven or can afford a bunch of dependencies.  We've 
done all we can to reduce the number of dependencies that the core JayVee libraries use.
Below we list each of the JayVee modules and their dependencies.

### jayvee-core

The core JayVee library has only a single dependency, log4j.  We use this for (drumroll
please) logging.

* [jayvee-core.jar][core-jar-no-dependencies]
* [jayvee-core-with-dependencies.zip][core-jar-with-dependencies]

  [core-jar-no-dependencies]: downloads/jayvee-core/jayvee-core-0.1.jar
  [core-jar-with-dependencies]: downloads/jayvee-core/jayvee-core-0.1-with-dependencies.zip

---

Each module has a '-with-dependencies' link and a plain link.  The link
with dependencies will give you a zip containing all the versions of the dependencies that
we use for our own testing.  If a module has a dependency that we cannot legally 
redistribute then the with dependencies link will not contain that dependency (we will try
to call attention this fact separately).  Today, there are no such modules.
 
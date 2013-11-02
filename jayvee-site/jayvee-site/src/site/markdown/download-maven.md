## Downloading with Maven

All Maven dependencies can be downloaded from the Maven central repository and artifacts
will have coordinates of the following form (replace jayvee-core with the module of interest)

<pre><code>&lt;dependency&gt;
  &lt;groupId&gt;com.github.westonpace.jayvee&lt;/groupId&gt;
  &lt;artifactId&gt;jayvee-core&lt;/artifactId&gt;
  &lt;version&gt;0.1&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>

The JayVee modules all have the same versioning.  If you are using multiple modules it is
probably in your best interest to define the version as a Maven property.  The following
is a list of modules available.

* jayvee-core - Provides the core image classes and workflow logic
* jayvee-spring - Allows for integration with the Spring Framework


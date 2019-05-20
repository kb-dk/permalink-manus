
# METS-api and permalink

The rationale behind this package was to merge five seperate applications

* lum
* mus
* musik
* musman
* manus

such that they could use

* a single metadata framework
* a common set of applications

This goal was achieved in by Jacob Larsen &amp; Sigfrid Lundberg in
aproject called __Sjauiga__ 2006. The name of the project was an
acronym for _Sigges og Jacobs arkæologiske udgravninger i gamle
applikationer_.

To my knowledge that project has been the only one at KB that has
successfully merged older applications.

More than 2800 objects with detailed metadata are available throught this system.

## About this document

mets-api and permalink now runs using

* the new Oracle version
* they are independent of OpenCMS
* compiles and runs under JAVA 8
    
The revised permalink application still runs using Apache as proxy and will retain its current link structure. For example:

http://www.kb.dk/permalink/2006/manus/7/ara/

Which should be a proxy for a service with a URI on the form:

http://cmstest.kb.dk:8080/mets-api/permalink/gw.jsp?doc=7&amp;app=manus&amp;lang=ara

This document is a description of how to build and install mets api and permalink


## Installation

* clone from github
* configure

<pre>
 src/main/java/dk/kb/dup/metsApi/ManusDataSource.java
 </pre>
	  <p>with respect to the variables:</p>
<pre>
 private String     user     = "oracle user/schema name";
 private String     password = "very secret password for that user";
 private String     jdbcUri  = "jdbc:oracle:thin:@oracle-test-03.kb.dk:1521:TEST3";
</pre>
</li>

<li>Running API and UI on different servers

<p>If the applications are rebuilt such that permalink and mets-api are
to be running on two different hosts, then set the variable apihost</p>

<pre>
 src/main/webapp/permalink/new-menu.jsp
</pre>

<p>and</p>

<pre>
 src/main/webapp/permalink/gw.jsp
</pre>

<p>search for</p>

<pre>
 String apihost  = "localhost";
</pre>
</li>
<li>
If need be, configure the ExpiryPolicy in the cache used by the API. The default is one hour

<pre>
 config.setStoreByValue(true)
   .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR))
   .setStatisticsEnabled(true);
</pre>

<p>Other durations can be set to ZERO, ONE_MINUTE, ONE_DAY or any one defined on <a href="http://ignite.apache.org/jcache/1.0.0/javadoc/javax/cache/expiry/class-use/Duration.html">ignite web site</a></p>
</li>
	</ul>
      </li>
      <li>
	Build 

<pre>
 cd mets-api/trunk
 mvn install
</pre>

<p>This creates a file called</p>

<pre>
 target/mets-api.war
</pre>

<p>which can be installed as usual</p>
      </li>
    </ol>
  </div>

  <div>
  <h3>How to configure Apache</h3>
  <pre>
RewriteEngine on

RewriteCond %{REQUEST_URI}  .*permalink.*(css|img|js)
RewriteRule   /.*(css|img|js)/(.*)$ http://localhost:8080/mets-api/permalink/$1/$2 [P,L]

RewriteCond %{REQUEST_URI} (manus|lum|lum.proj|musman|musman.proj|mus)/.*/$
RewriteRule /.*(manus|lum|lum.proj|musman|musman.proj|mus)/([^/]*)/([^/]*)/?([^/]*)/?  http://localhost:8080/mets-api/permalink/gw.jsp?app=$1&amp;doc=$2&amp;lang=$3&amp;page=$4&amp;%{QUERY_STRING} [P,L]

  </pre>
  </div>

  <div>
  <h3>On how to close the MANUS oracle schema</h3>

  <p>Several datasets delivered through permalink and its APIs are no
  longer alive and just available as static XML documents. There are
  some 2000 files in the directories</p>

<pre>
 src/main/webapp/data/lum
 src/main/webapp/data/mus
 src/main/webapp/data/musik
 src/main/webapp/data/musman
</pre>

<p>and 750+ in</p>

<pre>
 src/main/webapp/data/manus  
</pre>

<p>The data in the former four are used directly in the running service,
whereas the manus application is still live and its data is still
delivered from an Oracle database.</p>

<p>The data directory has been maintained by the command</p>

<pre>
 scripts/get-mets-files.pl --application &lt;application abbreviation> --targetdir &lt;directory>
 where the options have the following meaning
	--application	should take one of of the applications 
			manus,mus,musman,lum as argument
	--baseuri	should take the URI of a mets-api service as an argument
			such as http://disdev-01.kb.dk:8081/mets-api/
	--targetdir	the directory where to write the files
</pre>

<p>The permalink application can be made independent of Oracle by running
the script mentioned and editing the metsPath variable in the jsp
scripts</p>

<pre>
 src/main/webapp/permalink/new-menu.jsp
 src/main/webapp/permalink/menu.jsp
 src/main/webapp/permalink/gw.jsp
</pre>

<p>Once this is done, the system is independent of Oracle, but the data
cannot be edited using a forms based system.</p>

</div>
</body>
</html>

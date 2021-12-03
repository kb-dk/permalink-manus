
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

This goal was achieved in by Jacob Larsen & Sigfrid Lundberg in
aproject called
[__Sjauiga__](https://github.com/kb-dk/data-migration-2014/tree/master/sjauiga) 2006. The
name of the project was an acronym for _Sigges og Jacobs arkæologiske
udgravninger i gamle applikationer_.

To my knowledge that project has been the only one at KB that has
successfully merged older applications.

More than 2800 objects with detailed metadata are available throught this system.

http://www5.kb.dk/permalink/2006/manus/XXX/eng/ where XXX = 1 ... 800
(give or take a few dozen in the sequence).

For instance in the Copenhagen Maimonides, i.e., manus 293, on folio 110a 
there is a portrait of Yoda in the right margin

http://www5.kb.dk/permalink/2006/manus/293/eng/110a/

There is a large table with all [manus documents in this project](src/main/webapp/data/manus/all_manus.md).

## About this document

mets-api and permalink now runs using

* the new Oracle version at KB
* they are independent of OpenCMS
* compiles and runs under JAVA 8
    
The revised permalink application still runs using Apache as proxy and will retain its current link structure. For example:

http://www.kb.dk/permalink/2006/manus/7/ara/

Which should be a proxy for a service with a URI on the form (not available on the open):

http://oraapp-test.kb.dk:8080/mets-api/permalink/gw.jsp?doc=7&app=manus&lang=ara

This document is a description of how to build and install mets api and permalink

## Content of this project

doc:
 - java-doc

src:
 - src/main/java
 - src/main/resources
 - src/main/webapp

webapp:
 - src/main/webapp/api
 - src/main/webapp/data
 - src/main/webapp/permalink
 - src/main/webapp/WEB-INF
 
scripts:
 - minor scripts (sh or perl)

## Installation

* clone from github
* configure

```
 src/main/java/dk/kb/dup/metsApi/ManusDataSource.java
```

with respect to the variables:

```
 private String     user     = "oracle user/schema name";
 private String     password = "very secret password for that user";
 private String     jdbcUri  = "jdbc:oracle:thin:@oracle-test-03.kb.dk:1521:TEST3";
```
Yes, those parameters are hardcoded in Java. Sorry.

### Running API and UI on different servers

If the applications are rebuilt such that permalink and mets-api are
to be running on two different hosts, then set the variable apihost</p>

```
 src/main/webapp/permalink/new-menu.jsp
```

and

```
 src/main/webapp/permalink/gw.jsp
```

search for

```
 String apihost  = "localhost";
```

If need be, configure the ExpiryPolicy in the cache used by the API. The default is one hour

```
 config.setStoreByValue(true)
   .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR))
   .setStatisticsEnabled(true);
```

Other durations can be set to ZERO, ONE_MINUTE, ONE_DAY or any one defined on http://ignite.apache.org/jcache/1.0.0/javadoc/javax/cache/expiry/class-use/Duration.html

Build 

```
 cd permalink-manus
 mvn install
```

This creates a file called

```
 target/mets-api.war
```

which can be installed as usual

## How to configure Apache

```
RewriteEngine on

RewriteCond %{REQUEST_URI}  .*permalink.*(css|img|js)
RewriteRule   /.*(css|img|js)/(.*)$ http://localhost:8080/mets-api/permalink/$1/$2 [P,L]

RewriteCond %{REQUEST_URI} (manus|lum|lum.proj|musman|musman.proj|mus)/.*/$
RewriteRule /.*(manus|lum|lum.proj|musman|musman.proj|mus)/([^/]*)/([^/]*)/?([^/]*)/?  http://localhost:8080/mets-api/permalink/gw.jsp?app=$1&amp;doc=$2&amp;lang=$3&amp;page=$4&amp;%{QUERY_STRING} [P,L]

```

## On how to close the MANUS oracle schema

Several datasets delivered through permalink and its APIs are no
longer alive and just available as static XML documents. There are
some 2000 files in the directories

```
 src/main/webapp/data/lum
 src/main/webapp/data/mus
 src/main/webapp/data/musik
 src/main/webapp/data/musman
```

and 750+ in

```
 src/main/webapp/data/manus  
```

The data in the former four are used directly in the running service,
whereas the manus application is still live and its data is still
delivered from an Oracle database.

The data here today December 2021 are up to date.  Last time we made a
METS dump from the Oracle database was 2018 and the data has changed
since then, so there are people working on them occasionally.

The data directory has been maintained by the command


```
 scripts/get-mets-files.pl --application &lt;application abbreviation> --targetdir &lt;directory>
 where the options have the following meaning
	--application	should take one of of the applications 
			manus,mus,musman,lum as argument
	--baseuri	should take the URI of a mets-api service as an argument
			such as http://disdev-01.kb.dk:8081/mets-api/
	--targetdir	the directory where to write the files
```

The permalink application can be made independent of Oracle by running
the script mentioned and editing the metsPath variable in the jsp
scripts

```
 src/main/webapp/permalink/new-menu.jsp
 src/main/webapp/permalink/menu.jsp
 src/main/webapp/permalink/gw.jsp
```

Once this is done, the system is independent of Oracle, but the data
can no longer be edited using a forms based system.


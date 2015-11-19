To Do:
  * integrate new Spring 2.5 security stuff (nee Acegi Security)
  * handle multi-part POSTs to temporary files
  * JythonMapper - allow web services to be scripted in Jython (or Ruby?)
  * DONE!   -   custom Spring 2.5 Firewater XML schema
  * start on intellij plug-in
  * FirewaterMVC - why not use Firewater + StringTemplate + Jython for MVC stuff?
  * PivotTreeBuilder improvements - better collections
  * remove Dom4J dependencies - one less dependency is always a good thing (use native Java XML libs)
  * ThreadPoolMapper - a mapper that manages a fixed set of sub-Mappers for memory intensive web service operations (eg. mixing down an MP3 or resizing an image)
  * ImageMapper - a mapper that can perform a number of image processing operations
  * MailMergeMapper - a mapper that merges a StringTemplate mail template and sends it out with JavaMail
  * DelegatingMapper - an abstract mapper that can be used as an extension point for building mappers that call out to other mappers as part of their logic.  Contains convenience methods for calling the Firewater RouteMapper, Jython scripting hooks, and processing XML results.
  * Example - we need a better example application - Pet Clinic?
  * DONE -     integrate the XMLVerbatim XSL transformation for all document output
  * DONE links - still need relations    - relationships in result documents are currently ad-hoc - is there a Resource abstraction that could represent link URLs in results documents?
  * master / detail support.  the difference between master(list summary) and detail(individual resource detail) style REST URLs is currently ad-hoc - should the framework have explicit support for these common patterns?
  * logos, homepage - one page landing HTML for project
  * user guide / FAQ
  * add Firewater to main Maven repo - or at least get it to deploy to googlecode
  * clean up ST reserved template variables in JDBC classes and generalize pagination in the QueryMapper
  * port to java6 (for JSR 223 - embedded language support)
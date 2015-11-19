# Firewater #

  * [To Do List](TodoList.md)

The goal of this project is to provide a framework to create highly efficient and powerful Web Service APIs.

Firewater embraces the [REST](RestPage.md) web service architecture.  This approach fully leverages existing HTTP architecture and concepts to provide operations on application Resources.

Firewater assumes that your data is stored in back-end relational databases, and returns REST Responses in a [flexible XML format](FirewaterResponsePage.md).

Firewater can be used either as a [Java API](FirewaterAPI.md), as a [stand-alone web server](FirewaterServer.md), or as a [plug-in](FirewaterServlet.md) servlet in an existing Java web application.

One of the main goals of the framework is to enable [zero code web services](ZeroCode.md).  The framework leverages [the Spring framework](http://www.springframework.org/) as a configuration engine (IoC), and allows for the creation of non-trivial relationally backed web services without writing a single line of Java code.

The framework maps the HTTP GET method to SQL queries supplied by the web service designer ( you! ).  A tenet of Firewater is that it doesn't try to generate SQL or do any fancy introspection of the database meta-data.  It leaves query design up to the designer.  What it _does_ provide is access to [StringTemplate](http://www.stringtemplate.org/) - a powerful template engine as a core feature of it's SQL processing framework.  It is easy to create flexible SQL templates to handle a variety of processing requirements, such as transforming incoming URL query arguments into keys for [sorting](QuerySorting.md), [filtering](QueryFiltering.md), [setting default values](QueryDefaultValues.md), [pagination](QueryPagination.md), and [full-text searching](QueryFullText.md).

Central to Firewater's efficiency strategy is how it processes SQL result sets.  All data for a single GET request are fetched by a single SQL _select_ statement.  At the core of Firewater's result set processing is the PivotTreeBuilder, a Java class that maps flat result sets into hierarchical XML documents.

The processing of PUT, POST, and DELETE methods similarly use the StringTemplate framework for SQL template processing.  In recognition that these Methods logically may execute many SQL Statements (adding a new User may also add a new row into the UserRole table, for example), it is possible to configure multiple SQL statements to be executed sequentially.  The framework will track new _primary keys_ generated by any statements executed and will make them available to subsequent SQL statments.

For both security and efficiency reasons, Firewater employs a flexible [validation and data binding](MapperValidation.md) framework based on Spring's bean validation and binding services.  Incoming data can be easily validated using regular expression patterns, and once validated can be transformed before being merged with the SQL template for execution.

Firewater utilizes [Acegi](http://www.acegisecurity.org/) to control web level authentication and authorization.  This powerful framework maps naturally to the REST concept of identifying underlying resources with URLs.  Specific URL patterns can be added to the security configuration and access can be controlled by using a variety of specific protocols.
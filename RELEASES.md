Ibis AdapterFramework release notes
===================================

[Tags](https://github.com/ibissource/iaf/releases)



Upcoming
--------

[JavaDocs](http://www.ibissource.org/iaf/maven/apidocs/index.html)
[Commits](https://github.com/ibissource/iaf/compare/v5.6.1...HEAD)
[![Build Status](https://travis-ci.org/ibissource/iaf.png)](https://travis-ci.org/ibissource/iaf)

- Add support for jetty-maven-plugin
- Add note "Theme Bootstrap is part of a beta version" in main page of IBIS console for theme "bootstrap"
- Put regular form fields received by rest calls in sessionKeys (next to file form fields) so they can be used in the pipeline
- Add xslt2 attribute to parameter for using XSLT 2.0 instead of only XSLT 1.0
- Avoid PipeRunException when moving a file to an already existing destination (by adding a counter suffix)
- Add possibility to log the length of messages instead of the content in the MSG log
- Add functionality to forward form fields as sessionKeys (in RestListeners)
- Add possibility to write log records (to separate log files) without the message itself (e.g. for making counts)
- Configuration warning when FxF directory doesn't exist
- Added parameter pattern 'uuid' (which can be used instead of the combination of 'hostname' and 'uid')
- Add preemptive authentication to prevent “httpstatus 407: Proxy Authentication Required" when proxy is used without an user in a http call
- Make the IBIS console function "Browse a Jdbc table" capable for MQ SQL (next to Oracle)
- Performance fix for the IBIS console function "Browse a Jdbc table" 
- Add queue info when getting queue messages (currently used in "ShowTibcoQueues" in IJA_TiBeT2)
- Add possibility to wait for a database row to be locked (instead of always skipping locked records)
- Add functionality to temporarily move and/or chomp received messages for memory purposes
- Remove error about maximum size (10 MB) exceeding messages and increase the similar warning size from 1 MB to 3 MB. For HTTP messages increase the warning size from 32 KB to 128 KB
- Add possibility to fix the namespace of TIBCO response messages (instead of just copying the namespace)
- Enable parameters in xpathExpression of XmlSwitch
- Fix bug in namespace awareness (which was introduced in November 2013)
- Performance fix for the IBIS console function "Browse a Jdbc table" (get column information directly instead of by selecting first record)
- Write loaded configuration to file when IBIS is started so it's possible to query on it (e.g. via Splunk)
- Fix bug "(SQLException) FOR UPDATE clause allowed only for DECLARE CURSOR" for non Oracle dbms which was introduced with lockWait attribute
- Fix strange bug in DirectoryListener (which occurred in Ibis4Scan)
- Bugfix database actions not being part of transaction when using BTM
- Add possibility for a RestListener to stream received documents from and into a database table
- Better m2e configuration (no need to overwrite/change org.eclipse.wst.common.component anymore)
- Add class to browse and remove queue messages with input and output a xml message (very useful for test purposes)
- Move Test Tool 1 from IbisTestToolWEB to Maven module Ibis AdapterFramework Larva
- Show http body in exception thrown by http sender in case status code indicates an error
- Make multipart work for http sender in case only inputMessageParam is used (without extra parameters)
- Bugfix RestListenerServlet that didn't read http body anymore for POST method
- Add support for paramsInUrl, inputMessageParam and multipart to Larva HttpSender
- Add custom pipe for interrupting processing when timeout is exceeded
- Add facility to show the age of the current first message in the queue when pendingMsgCount>0 and receiverCount=0 (currently used in "ShowTibcoQueues" in IJA_TiBeT2)
- Add facility to check EsbJmsListeners on lost connections
- Next to the methode type GET and POST, also the method types PUT and DELETE are now possible in HttpSender
- With the base64 attribute in HttpSender it is possible to receive and pass on non-string results
- Add completeFileHeader attribute in ZipWriterPipe
- Bugfix NPE when changing log level in console and nonstandard log4j configuration is used
- Use default log4j config in example webapp
- Use AppConstants in Larva Test Tool when testtool.properties not found
- Configure example webapp to use Larva Test Tool
- Add Test.properties to AppConstants for local properties which should not be added to the version control system
- Bugfix ForEachChildElementPipe with blockSize which was skipping remaining items after last block
- Remove datasource.deleteTable function from Larva
- Remove autoignore function from Larva
- Replace testtool.properties with AppConstants
- Remove unused and broken Larva jsp's
- Add scenariosroot.default property
- Add support to Larva for Maven based Eclipse projects
- Sort available scenarios root directories before unavailable ones
- Adjust the filling of the ESB Common Message Header in the SOAP Header
- Add copyAEProperties attribute in EsbJmsListener
- Add charset parameter to MailSender
- Make MailSender parameters messageType and messageBase64 thread-safe
- Add queueRegex attribute to GetTibcoQueues
- Add defaultValueMethods to Parameter
- Don't use ConversationId from previous sender response
- Add GetPrincipalPipe and IsUserInRolePipe to stub4testtool.xsl
- Add MessageStoreSender and MessageStoreListener
- Add size of message to response in GetTibcoQueues (and chomp very large message)
- Add writeSuffix parameter to FileHandler (next to writeSuffix attribute)
- Add file type bin for mime type application/octet-stream in FileViewerServlet
- Make it possible to generate a WSDL based on WsdlXmlValidator
- Add Last Message to Show ConfigurationStatus
- Bugfix monitoring events for input/output-Validators/Wrappers being ignored



5.6.1
---

[Commits](https://github.com/ibissource/iaf/compare/v5.6...v5.6.1)
[![Build Status](https://travis-ci.org/ibissource/iaf.png?branch=v5.6.1)](https://travis-ci.org/ibissource/iaf)

- Fixed bug in EsbSoapWrapper where Result element was inserted instead of Status element 



5.6
---

[Commits](https://github.com/ibissource/iaf/compare/v5.5...v5.6)
[![Build Status](https://travis-ci.org/ibissource/iaf.png?branch=v5.6)](https://travis-ci.org/ibissource/iaf)

- Move missing errorStorage warning from MessageKeeper (at adapter level) and logfile to ConfigurationWarnings (top of console main page).
- Replace (broken) enforceMQCompliancy on JmsSender with MQSender.
- Remove FXF 1 and 2 support.
- Fix Ibis name and DTAP stage in Bootstrap theme.
- Add theme switch button.
- Add stream support to FilePipe and FileSender.
- Add permission rules to FileViewerServlet.
- Added the possibility for enabling LDAP authentication and authorization without a deployment descriptor
- Added functionality for unit testing (TestTool)
- Added some MS SQL support
- Extended functionality for MoveFilePipe and CleanupOldFilesPipe, and introduced UploadFilePipe
- Introduction of SimpleJdbcListener; activate pipeline based on a select count query
- Added possibility to process zipped xml files with a BOM (Byte Order Mark)
- Added locker functionality to pipeline element (it was already available for scheduler element)

### Non backwards compatible changes

- Attribute enforceMQCompliancy on JmsSender has been removed, use nl.nn.adapterframework.extensions.ibm.MQSender instead of nl.nn.adapterframework.jms.JmsSender when setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ) is needed.
- Support for FXF 1 and 2 as been dropped.



5.5
---

[Commits](https://github.com/ibissource/iaf/compare/v5.4...v5.5)
[![Build Status](https://travis-ci.org/ibissource/iaf.png?branch=v5.5)](https://travis-ci.org/ibissource/iaf)

- Also when not transacted don't retrow exception caught in JMS listener (caused connection to be closed and caused possible other threads on the same listener to experience "javax.jms.IllegalStateException: Consumer closed").
- Tweaked error logging and configuration warnings about transactional processing.
    - Show requirement for errorStorage on FF EsbJmsListener as configuration warning instead of log warning on every failed message.
    - Removed logging error "not transacted, ... will not be retried" and warning "has no errorSender or errorStorage, message ... will be lost" (when a listener should run under transaction control (ITransactionRequirements) a configuration warning is already shown).
    - Removed logging error "message ... had error in processing; current retry-count: 0" (on error in pipeline an appropriate action (e.g. logging) should already been done).
    - Don't throw RCV_MESSAGE_TO_ERRORSTORE_EVENT and don't log "moves ... to errorSender/errorStorage" when no errorSender or errorStorage present.
    - Removed some unused code and comments like ibis42compatibility.
    - Renamed var retry to manualRetry for better code readability.
- Prevent java.io.NotSerializableException when the application server wants to persist sessions.
- Prevent problems with control characters in Test Tool GUI (replace them with inverted question mark + "#" + number of character + ";").
- Alpha version of new design Ibis console.
- Better support for Active Directory and other LdapSender improvements.
    - Make "filter" on LDAP error/warning messages work for AD too.
    - Added unicodePwd encoding.
    - Added changeUnicodePwd operation.
    - Added challenge operation to LdapSender (LdapChallengePipe has been deprecated).
    - Made it possible to specify principal and credentials as parameters.
    - Set errorSessionKey to errorReason by default.
    - Cleaned debug logging code and exclude password from being logged.
- Fixed a lot of javadoc warnings.
- Introduction of XmlFileElementIteratorPipe; streamed processing of (very large) xml files
- Better integration of Maven and Eclipse (using Kepler SR2).
- added "Transaction Service" to console function "Show Security Items", and added configuration warning "receiver/pipeline transaction timeout exceeds system transaction timeout"



5.4
---

[Commits](https://github.com/ibissource/iaf/compare/v5_3...v5_4)
[![Build Status](https://travis-ci.org/ibissource/iaf.png?branch=v5.4)](https://travis-ci.org/ibissource/iaf)

- First steps towards running IBISes on TIBCO AMX (as WebApp in Composite)
- added "Used SapSystems" to console function "Show Security Items"
- prevent OutOfMemoryError in console function "Adapter Logging" caused by a lot of files in a directory
- added facility to hide properties in console functions "Show configuration" and "Show Environment variables"
- Fixed problems with XSD's using special imports.
- Removed unused code which generates a NPE on JBoss Web/7.0.13.Final.
- Replace non valid xml characters when formatting error message.
- Made it possible to add http headers when using a HttpSender.
- Fixed exception in file viewer when context root of IAF instance is /.
- Bugfix addRootNamespace.
- Made it possible to override the address location in the generated WSDL.
- Possibility to dynamically load adapters.



5.3
---

[Commits](https://github.com/ibissource/iaf/compare/v5_2...v5_3)

- Better DB2 support.
- Some steps towards making a release with Maven.
- First steps towards dynamic adapters.
- Specific java class, which returns Tibco queue information in a xml, is extended with more information.
- On the main page of the IBIS console ("Show configurationStatus") for each RestListener a clickable icon is added (this replaces separate bookmarks).
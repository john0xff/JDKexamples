//package jdbc.mysql;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.lang.ref.PhantomReference;
//import java.lang.ref.ReferenceQueue;
//import java.lang.reflect.Proxy;
//import java.net.URLDecoder;
//import java.sql.Driver;
//import java.sql.DriverPropertyInfo;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//import java.util.Properties;
//import java.util.Set;
//import java.util.StringTokenizer;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class NonRegisteringDriver
//  implements Driver
//{
//  private static final String ALLOWED_QUOTES = "\"'";
//  private static final String REPLICATION_URL_PREFIX = "jdbc:mysql:replication://";
//  private static final String URL_PREFIX = "jdbc:mysql://";
//  private static final String MXJ_URL_PREFIX = "jdbc:mysql:mxj://";
//  public static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance://";
//  protected static final ConcurrentHashMap<ConnectionPhantomReference, ConnectionPhantomReference> connectionPhantomRefs = new ConcurrentHashMap();
//
//  protected static final ReferenceQueue<ConnectionImpl> refQueue = new ReferenceQueue();
//
//  public static final String OS = getOSName();
//  public static final String PLATFORM = getPlatform();
//  public static final String LICENSE = "GPL";
//  public static final String RUNTIME_VENDOR = System.getProperty("java.vendor");
//  public static final String RUNTIME_VERSION = System.getProperty("java.version");
//  public static final String VERSION = "5.1.30";
//  public static final String NAME = "MySQL Connector Java";
//  public static final String DBNAME_PROPERTY_KEY = "DBNAME";
//  public static final boolean DEBUG = false;
//  public static final int HOST_NAME_INDEX = 0;
//  public static final String HOST_PROPERTY_KEY = "HOST";
//  public static final String NUM_HOSTS_PROPERTY_KEY = "NUM_HOSTS";
//  public static final String PASSWORD_PROPERTY_KEY = "password";
//  public static final int PORT_NUMBER_INDEX = 1;
//  public static final String PORT_PROPERTY_KEY = "PORT";
//  public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
//  public static final boolean TRACE = false;
//  public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
//  public static final String USER_PROPERTY_KEY = "user";
//  public static final String PROTOCOL_PROPERTY_KEY = "PROTOCOL";
//  public static final String PATH_PROPERTY_KEY = "PATH";
//
//  public static String getOSName()
//  {
//    return System.getProperty("os.name");
//  }
//
//  public static String getPlatform()
//  {
//    return System.getProperty("os.arch");
//  }
//
//  static int getMajorVersionInternal()
//  {
//    return safeIntParse("5");
//  }
//
//  static int getMinorVersionInternal()
//  {
//    return safeIntParse("1");
//  }
//
//  protected static String[] parseHostPortPair(String hostPortPair)
//    throws SQLException
//  {
//    String[] splitValues = new String[2];
//
//    if (StringUtils.startsWithIgnoreCaseAndWs(hostPortPair, "address")) {
//      splitValues[0] = hostPortPair.trim();
//      splitValues[1] = null;
//
//      return splitValues;
//    }
//
//    int portIndex = hostPortPair.indexOf(":");
//
//    String hostname = null;
//
//    if (portIndex != -1) {
//      if (portIndex + 1 < hostPortPair.length()) {
//        String portAsString = hostPortPair.substring(portIndex + 1);
//        hostname = hostPortPair.substring(0, portIndex);
//
//        splitValues[0] = hostname;
//
//        splitValues[1] = portAsString;
//      } else {
//        throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.37"), "01S00", null);
//      }
//    }
//    else
//    {
//      splitValues[0] = hostPortPair;
//      splitValues[1] = null;
//    }
//
//    return splitValues;
//  }
//
//  private static int safeIntParse(String intAsString) {
//    try {
//      return Integer.parseInt(intAsString); } catch (NumberFormatException nfe) {
//    }
//    return 0;
//  }
//
//  public NonRegisteringDriver()
//    throws SQLException
//  {
//  }
//
//  public boolean acceptsURL(String url)
//    throws SQLException
//  {
//    return parseURL(url, null) != null;
//  }
//
//  public java.sql.Connection connect(String url, Properties info)
//    throws SQLException
//  {
//    if (url != null) {
//      if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://"))
//        return connectLoadBalanced(url, info);
//      if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://"))
//      {
//        return connectReplicationConnection(url, info);
//      }
//    }
//
//    Properties props = null;
//
//    if ((props = parseURL(url, info)) == null) {
//      return null;
//    }
//
//    if (!"1".equals(props.getProperty("NUM_HOSTS"))) {
//      return connectFailover(url, info);
//    }
//    try
//    {
//      return ConnectionImpl.getInstance(host(props), port(props), props, database(props), url);
//    }
//    catch (SQLException sqlEx)
//    {
//      throw sqlEx;
//    } catch (Exception ex) {
//      SQLException sqlEx = SQLError.createSQLException(Messages.getString("NonRegisteringDriver.17") + ex.toString() + Messages.getString("NonRegisteringDriver.18"), "08001", null);
//
//      sqlEx.initCause(ex);
//
//      throw sqlEx;
//    }
//  }
//
//  protected static void trackConnection(Connection newConn)
//  {
//    ConnectionPhantomReference phantomRef = new ConnectionPhantomReference((ConnectionImpl)newConn, refQueue);
//    connectionPhantomRefs.put(phantomRef, phantomRef);
//  }
//
//  private java.sql.Connection connectLoadBalanced(String url, Properties info) throws SQLException
//  {
//    Properties parsedProps = parseURL(url, info);
//
//    if (parsedProps == null) {
//      return null;
//    }
//
//    parsedProps.remove("roundRobinLoadBalance");
//
//    int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
//
//    List hostList = new ArrayList();
//
//    for (int i = 0; i < numHosts; i++) {
//      int index = i + 1;
//
//      hostList.add(parsedProps.getProperty(new StringBuilder().append("HOST.").append(index).toString()) + ":" + parsedProps.getProperty(new StringBuilder().append("PORT.").append(index).toString()));
//    }
//
//    LoadBalancingConnectionProxy proxyBal = new LoadBalancingConnectionProxy(hostList, parsedProps);
//
//    return (java.sql.Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { LoadBalancedConnection.class }, proxyBal);
//  }
//
//  private java.sql.Connection connectFailover(String url, Properties info)
//    throws SQLException
//  {
//    Properties parsedProps = parseURL(url, info);
//
//    if (parsedProps == null) {
//      return null;
//    }
//
//    parsedProps.remove("roundRobinLoadBalance");
//    parsedProps.setProperty("autoReconnect", "false");
//
//    int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
//
//    List hostList = new ArrayList();
//
//    for (int i = 0; i < numHosts; i++) {
//      int index = i + 1;
//
//      hostList.add(parsedProps.getProperty(new StringBuilder().append("HOST.").append(index).toString()) + ":" + parsedProps.getProperty(new StringBuilder().append("PORT.").append(index).toString()));
//    }
//
//    FailoverConnectionProxy connProxy = new FailoverConnectionProxy(hostList, parsedProps);
//
//    return (java.sql.Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, connProxy);
//  }
//
//  protected java.sql.Connection connectReplicationConnection(String url, Properties info)
//    throws SQLException
//  {
//    Properties parsedProps = parseURL(url, info);
//
//    if (parsedProps == null) {
//      return null;
//    }
//
//    Properties masterProps = (Properties)parsedProps.clone();
//    Properties slavesProps = (Properties)parsedProps.clone();
//
//    slavesProps.setProperty("com.mysql.jdbc.ReplicationConnection.isSlave", "true");
//
//    int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
//
//    if (numHosts < 2) {
//      throw SQLError.createSQLException("Must specify at least one slave host to connect to for master/slave replication load-balancing functionality", "01S00", null);
//    }
//
//    List slaveHostList = new ArrayList();
//    List masterHostList = new ArrayList();
//
//    String firstHost = masterProps.getProperty("HOST.1") + ":" + masterProps.getProperty("PORT.1");
//
//    boolean usesExplicitServerType = isHostPropertiesList(firstHost);
//
//    for (int i = 0; i < numHosts; i++) {
//      int index = i + 1;
//
//      masterProps.remove("HOST." + index);
//      masterProps.remove("PORT." + index);
//      slavesProps.remove("HOST." + index);
//      slavesProps.remove("PORT." + index);
//
//      String host = parsedProps.getProperty("HOST." + index);
//      String port = parsedProps.getProperty("PORT." + index);
//      if (usesExplicitServerType) {
//        if (isHostMaster(host))
//          masterHostList.add(host);
//        else {
//          slaveHostList.add(host);
//        }
//      }
//      else if (i == 0)
//        masterHostList.add(host + ":" + port);
//      else {
//        slaveHostList.add(host + ":" + port);
//      }
//
//    }
//
//    slavesProps.remove("NUM_HOSTS");
//    masterProps.remove("NUM_HOSTS");
//    masterProps.remove("HOST");
//    masterProps.remove("PORT");
//    slavesProps.remove("HOST");
//    slavesProps.remove("PORT");
//
//    return new ReplicationConnection(masterProps, slavesProps, masterHostList, slaveHostList);
//  }
//
//  private boolean isHostMaster(String host)
//  {
//    if (isHostPropertiesList(host)) {
//      Properties hostSpecificProps = expandHostKeyValues(host);
//      if ((hostSpecificProps.containsKey("type")) && ("master".equalsIgnoreCase(hostSpecificProps.get("type").toString())))
//      {
//        return true;
//      }
//    }
//    return false;
//  }
//
//  public String database(Properties props)
//  {
//    return props.getProperty("DBNAME");
//  }
//
//  public int getMajorVersion()
//  {
//    return getMajorVersionInternal();
//  }
//
//  public int getMinorVersion()
//  {
//    return getMinorVersionInternal();
//  }
//
//  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
//    throws SQLException
//  {
//    if (info == null) {
//      info = new Properties();
//    }
//
//    if ((url != null) && (url.startsWith("jdbc:mysql://"))) {
//      info = parseURL(url, info);
//    }
//
//    DriverPropertyInfo hostProp = new DriverPropertyInfo("HOST", info.getProperty("HOST"));
//
//    hostProp.required = true;
//    hostProp.description = Messages.getString("NonRegisteringDriver.3");
//
//    DriverPropertyInfo portProp = new DriverPropertyInfo("PORT", info.getProperty("PORT", "3306"));
//
//    portProp.required = false;
//    portProp.description = Messages.getString("NonRegisteringDriver.7");
//
//    DriverPropertyInfo dbProp = new DriverPropertyInfo("DBNAME", info.getProperty("DBNAME"));
//
//    dbProp.required = false;
//    dbProp.description = "Database name";
//
//    DriverPropertyInfo userProp = new DriverPropertyInfo("user", info.getProperty("user"));
//
//    userProp.required = true;
//    userProp.description = Messages.getString("NonRegisteringDriver.13");
//
//    DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", info.getProperty("password"));
//
//    passwordProp.required = true;
//    passwordProp.description = Messages.getString("NonRegisteringDriver.16");
//
//    DriverPropertyInfo[] dpi = ConnectionPropertiesImpl.exposeAsDriverPropertyInfo(info, 5);
//
//    dpi[0] = hostProp;
//    dpi[1] = portProp;
//    dpi[2] = dbProp;
//    dpi[3] = userProp;
//    dpi[4] = passwordProp;
//
//    return dpi;
//  }
//
//  public String host(Properties props)
//  {
//    return props.getProperty("HOST", "localhost");
//  }
//
//  public boolean jdbcCompliant()
//  {
//    return false;
//  }
//
//  public Properties parseURL(String url, Properties defaults) throws SQLException
//  {
//    Properties urlProps = defaults != null ? new Properties(defaults) : new Properties();
//
//    if (url == null) {
//      return null;
//    }
//
//    if ((!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://")))
//    {
//      return null;
//    }
//
//    int beginningOfSlashes = url.indexOf("//");
//
//    if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://"))
//    {
//      urlProps.setProperty("socketFactory", "com.mysql.management.driverlaunched.ServerLauncherSocketFactory");
//    }
//
//    int index = url.indexOf("?");
//
//    if (index != -1) {
//      String paramString = url.substring(index + 1, url.length());
//      url = url.substring(0, index);
//
//      StringTokenizer queryParams = new StringTokenizer(paramString, "&");
//
//      while (queryParams.hasMoreTokens()) {
//        String parameterValuePair = queryParams.nextToken();
//
//        int indexOfEquals = StringUtils.indexOfIgnoreCase(0, parameterValuePair, "=");
//
//        String parameter = null;
//        String value = null;
//
//        if (indexOfEquals != -1) {
//          parameter = parameterValuePair.substring(0, indexOfEquals);
//
//          if (indexOfEquals + 1 < parameterValuePair.length()) {
//            value = parameterValuePair.substring(indexOfEquals + 1);
//          }
//        }
//
//        if ((value != null) && (value.length() > 0) && (parameter != null) && (parameter.length() > 0)) {
//          try
//          {
//            urlProps.put(parameter, URLDecoder.decode(value, "UTF-8"));
//          }
//          catch (UnsupportedEncodingException badEncoding)
//          {
//            urlProps.put(parameter, URLDecoder.decode(value));
//          }
//          catch (NoSuchMethodError nsme) {
//            urlProps.put(parameter, URLDecoder.decode(value));
//          }
//        }
//      }
//    }
//
//    url = url.substring(beginningOfSlashes + 2);
//
//    String hostStuff = null;
//
//    int slashIndex = StringUtils.indexOfIgnoreCaseRespectMarker(0, url, "/", "\"'", "\"'", true);
//
//    if (slashIndex != -1) {
//      hostStuff = url.substring(0, slashIndex);
//
//      if (slashIndex + 1 < url.length())
//        urlProps.put("DBNAME", url.substring(slashIndex + 1, url.length()));
//    }
//    else
//    {
//      hostStuff = url;
//    }
//
//    int numHosts = 0;
//
//    if ((hostStuff != null) && (hostStuff.trim().length() > 0)) {
//      List hosts = StringUtils.split(hostStuff, ",", "\"'", "\"'", false);
//
//      for (String hostAndPort : hosts) {
//        numHosts++;
//
//        String[] hostPortPair = parseHostPortPair(hostAndPort);
//
//        if ((hostPortPair[0] != null) && (hostPortPair[0].trim().length() > 0))
//          urlProps.setProperty("HOST." + numHosts, hostPortPair[0]);
//        else {
//          urlProps.setProperty("HOST." + numHosts, "localhost");
//        }
//
//        if (hostPortPair[1] != null)
//          urlProps.setProperty("PORT." + numHosts, hostPortPair[1]);
//        else
//          urlProps.setProperty("PORT." + numHosts, "3306");
//      }
//    }
//    else {
//      numHosts = 1;
//      urlProps.setProperty("HOST.1", "localhost");
//      urlProps.setProperty("PORT.1", "3306");
//    }
//
//    urlProps.setProperty("NUM_HOSTS", String.valueOf(numHosts));
//    urlProps.setProperty("HOST", urlProps.getProperty("HOST.1"));
//    urlProps.setProperty("PORT", urlProps.getProperty("PORT.1"));
//
//    String propertiesTransformClassName = urlProps.getProperty("propertiesTransform");
//
//    if (propertiesTransformClassName != null) {
//      try {
//        ConnectionPropertiesTransform propTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
//
//        urlProps = propTransformer.transformProperties(urlProps);
//      } catch (InstantiationException e) {
//        throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
//      }
//      catch (IllegalAccessException e)
//      {
//        throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
//      }
//      catch (ClassNotFoundException e)
//      {
//        throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
//      }
//
//    }
//
//    if ((Util.isColdFusion()) && (urlProps.getProperty("autoConfigureForColdFusion", "true").equalsIgnoreCase("true")))
//    {
//      String configs = urlProps.getProperty("useConfigs");
//
//      StringBuffer newConfigs = new StringBuffer();
//
//      if (configs != null) {
//        newConfigs.append(configs);
//        newConfigs.append(",");
//      }
//
//      newConfigs.append("coldFusion");
//
//      urlProps.setProperty("useConfigs", newConfigs.toString());
//    }
//
//    String configNames = null;
//
//    if (defaults != null) {
//      configNames = defaults.getProperty("useConfigs");
//    }
//
//    if (configNames == null) {
//      configNames = urlProps.getProperty("useConfigs");
//    }
//
//    if (configNames != null) {
//      List splitNames = StringUtils.split(configNames, ",", true);
//
//      Properties configProps = new Properties();
//
//      Iterator namesIter = splitNames.iterator();
//
//      while (namesIter.hasNext()) {
//        String configName = (String)namesIter.next();
//        try
//        {
//          InputStream configAsStream = getClass().getResourceAsStream("configs/" + configName + ".properties");
//
//          if (configAsStream == null) {
//            throw SQLError.createSQLException("Can't find configuration template named '" + configName + "'", "01S00", null);
//          }
//
//          configProps.load(configAsStream);
//        } catch (IOException ioEx) {
//          SQLException sqlEx = SQLError.createSQLException("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx, "01S00", null);
//
//          sqlEx.initCause(ioEx);
//
//          throw sqlEx;
//        }
//      }
//
//      Iterator propsIter = urlProps.keySet().iterator();
//
//      while (propsIter.hasNext()) {
//        String key = propsIter.next().toString();
//        String property = urlProps.getProperty(key);
//        configProps.setProperty(key, property);
//      }
//
//      urlProps = configProps;
//    }
//
//    if (defaults != null) {
//      Iterator propsIter = defaults.keySet().iterator();
//
//      while (propsIter.hasNext()) {
//        String key = propsIter.next().toString();
//        if (!key.equals("NUM_HOSTS")) {
//          String property = defaults.getProperty(key);
//          urlProps.setProperty(key, property);
//        }
//      }
//    }
//
//    return urlProps;
//  }
//
//  public int port(Properties props)
//  {
//    return Integer.parseInt(props.getProperty("PORT", "3306"));
//  }
//
//  public String property(String name, Properties props)
//  {
//    return props.getProperty(name);
//  }
//
//  public static Properties expandHostKeyValues(String host)
//  {
//    Properties hostProps = new Properties();
//
//    if (isHostPropertiesList(host)) {
//      host = host.substring("address=".length() + 1);
//      List hostPropsList = StringUtils.split(host, ")", "'\"", "'\"", true);
//
//      for (String propDef : hostPropsList) {
//        if (propDef.startsWith("(")) {
//          propDef = propDef.substring(1);
//        }
//
//        List kvp = StringUtils.split(propDef, "=", "'\"", "'\"", true);
//
//        String key = (String)kvp.get(0);
//        String value = kvp.size() > 1 ? (String)kvp.get(1) : null;
//
//        if ((value != null) && (((value.startsWith("\"")) && (value.endsWith("\""))) || ((value.startsWith("'")) && (value.endsWith("'"))))) {
//          value = value.substring(1, value.length() - 1);
//        }
//
//        if (value != null) {
//          if (("HOST".equalsIgnoreCase(key)) || ("DBNAME".equalsIgnoreCase(key)) || ("PORT".equalsIgnoreCase(key)) || ("PROTOCOL".equalsIgnoreCase(key)) || ("PATH".equalsIgnoreCase(key)))
//          {
//            key = key.toUpperCase(Locale.ENGLISH);
//          } else if (("user".equalsIgnoreCase(key)) || ("password".equalsIgnoreCase(key)))
//          {
//            key = key.toLowerCase(Locale.ENGLISH);
//          }
//
//          hostProps.setProperty(key, value);
//        }
//      }
//    }
//
//    return hostProps;
//  }
//
//  public static boolean isHostPropertiesList(String host) {
//    return (host != null) && (StringUtils.startsWithIgnoreCase(host, "address="));
//  }
//
//  static
//  {
//    AbandonedConnectionCleanupThread referenceThread = new AbandonedConnectionCleanupThread();
//    referenceThread.setDaemon(true);
//    referenceThread.start();
//  }
//
//  static class ConnectionPhantomReference extends PhantomReference<ConnectionImpl>
//  {
//    private NetworkResources io;
//
//    ConnectionPhantomReference(ConnectionImpl connectionImpl, ReferenceQueue<ConnectionImpl> q)
//    {
//      super(q);
//      try
//      {
//        this.io = connectionImpl.getIO().getNetworkResources();
//      }
//      catch (SQLException e) {
//      }
//    }
//
//    void cleanup() {
//      if (this.io != null)
//        try {
//          this.io.forceClose();
//        } finally {
//          this.io = null;
//        }
//    }
//  }
//}
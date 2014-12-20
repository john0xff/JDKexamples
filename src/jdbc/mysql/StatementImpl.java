//package jdbc.mysql;
//
//import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
//import com.mysql.jdbc.exceptions.MySQLTimeoutException;
//import com.mysql.jdbc.log.LogUtils;
//import com.mysql.jdbc.profiler.ProfilerEvent;
//import com.mysql.jdbc.profiler.ProfilerEventHandler;
//import java.io.InputStream;
//import java.math.BigInteger;
//import java.sql.BatchUpdateException;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.SQLWarning;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Enumeration;
//import java.util.GregorianCalendar;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class StatementImpl
//  implements Statement
//{
//  protected static final String PING_MARKER = "/* ping */";
//  protected Object cancelTimeoutMutex = new Object();
//
//  static int statementCounter = 1;
//  public static final byte USES_VARIABLES_FALSE = 0;
//  public static final byte USES_VARIABLES_TRUE = 1;
//  public static final byte USES_VARIABLES_UNKNOWN = -1;
//  protected boolean wasCancelled = false;
//  protected boolean wasCancelledByTimeout = false;
//  protected List<Object> batchedArgs;
//  protected SingleByteCharsetConverter charConverter = null;
//
//  protected String charEncoding = null;
//
//  protected volatile MySQLConnection connection = null;
//
//  protected long connectionId = 0L;
//
//  protected String currentCatalog = null;
//
//  protected boolean doEscapeProcessing = true;
//
//  protected ProfilerEventHandler eventSink = null;
//
//  private int fetchSize = 0;
//
//  protected boolean isClosed = false;
//
//  protected long lastInsertId = -1L;
//
//  protected int maxFieldSize = MysqlIO.getMaxBuf();
//
//  protected int maxRows = -1;
//
//  protected Set<ResultSetInternalMethods> openResults = new HashSet();
//
//  protected boolean pedantic = false;
//  protected String pointOfOrigin;
//  protected boolean profileSQL = false;
//
//  protected ResultSetInternalMethods results = null;
//
//  protected ResultSetInternalMethods generatedKeysResults = null;
//
//  protected int resultSetConcurrency = 0;
//
//  protected int resultSetType = 0;
//  protected int statementId;
//  protected int timeoutInMillis = 0;
//
//  protected long updateCount = -1L;
//
//  protected boolean useUsageAdvisor = false;
//
//  protected SQLWarning warningChain = null;
//
//  protected boolean clearWarningsCalled = false;
//
//  protected boolean holdResultsOpenOverClose = false;
//
//  protected ArrayList<ResultSetRow> batchedGeneratedKeys = null;
//
//  protected boolean retrieveGeneratedKeys = false;
//
//  protected boolean continueBatchOnError = false;
//
//  protected PingTarget pingTarget = null;
//  protected boolean useLegacyDatetimeCode;
//  private ExceptionInterceptor exceptionInterceptor;
//  protected boolean lastQueryIsOnDupKeyUpdate = false;
//
//  protected final AtomicBoolean statementExecuting = new AtomicBoolean(false);
//
//  private boolean isImplicitlyClosingResults = false;
//
//  private int originalResultSetType = 0;
//  private int originalFetchSize = 0;
//
//  private boolean isPoolable = true;
//  private InputStream localInfileInputStream;
//  protected final boolean version5013OrNewer;
//  private boolean closeOnCompletion = false;
//
//  public StatementImpl(MySQLConnection c, String catalog)
//    throws SQLException
//  {
//    if ((c == null) || (c.isClosed())) {
//      throw SQLError.createSQLException(Messages.getString("Statement.0"), "08003", null);
//    }
//
//    this.connection = c;
//    this.connectionId = this.connection.getId();
//    this.exceptionInterceptor = this.connection.getExceptionInterceptor();
//
//    this.currentCatalog = catalog;
//    this.pedantic = this.connection.getPedantic();
//    this.continueBatchOnError = this.connection.getContinueBatchOnError();
//    this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
//
//    if (!this.connection.getDontTrackOpenResources()) {
//      this.connection.registerStatement(this);
//    }
//
//    if (this.connection != null) {
//      this.maxFieldSize = this.connection.getMaxAllowedPacket();
//
//      int defaultFetchSize = this.connection.getDefaultFetchSize();
//
//      if (defaultFetchSize != 0) {
//        setFetchSize(defaultFetchSize);
//      }
//
//      if (this.connection.getUseUnicode()) {
//        this.charEncoding = this.connection.getEncoding();
//
//        this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
//      }
//
//      boolean profiling = (this.connection.getProfileSql()) || (this.connection.getUseUsageAdvisor()) || (this.connection.getLogSlowQueries());
//
//      if ((this.connection.getAutoGenerateTestcaseScript()) || (profiling)) {
//        this.statementId = (statementCounter++);
//      }
//
//      if (profiling) {
//        this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
//        this.profileSQL = this.connection.getProfileSql();
//        this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
//        this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
//      }
//
//      int maxRowsConn = this.connection.getMaxRows();
//
//      if (maxRowsConn != -1) {
//        setMaxRows(maxRowsConn);
//      }
//
//      this.holdResultsOpenOverClose = this.connection.getHoldResultsOpenOverStatementClose();
//    }
//
//    this.version5013OrNewer = this.connection.versionMeetsMinimum(5, 0, 13);
//  }
//
//  public void addBatch(String sql)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.batchedArgs == null) {
//        this.batchedArgs = new ArrayList();
//      }
//
//      if (sql != null)
//        this.batchedArgs.add(sql);
//    }
//  }
//
//  public List<Object> getBatchedArgs()
//  {
//    return this.batchedArgs == null ? null : Collections.unmodifiableList(this.batchedArgs);
//  }
//
//  public void cancel()
//    throws SQLException
//  {
//    if (!this.statementExecuting.get()) {
//      return;
//    }
//
//    if ((!this.isClosed) && (this.connection != null) && (this.connection.versionMeetsMinimum(5, 0, 0)))
//    {
//      Connection cancelConn = null;
//      java.sql.Statement cancelStmt = null;
//      try
//      {
//        cancelConn = this.connection.duplicate();
//        cancelStmt = cancelConn.createStatement();
//        cancelStmt.execute("KILL QUERY " + this.connection.getIO().getThreadId());
//
//        this.wasCancelled = true;
//      } finally {
//        if (cancelStmt != null) {
//          cancelStmt.close();
//        }
//
//        if (cancelConn != null)
//          cancelConn.close();
//      }
//    }
//  }
//
//  protected MySQLConnection checkClosed()
//    throws SQLException
//  {
//    MySQLConnection c = this.connection;
//
//    if (c == null) {
//      throw SQLError.createSQLException(Messages.getString("Statement.49"), "08003", getExceptionInterceptor());
//    }
//
//    return c;
//  }
//
//  protected void checkForDml(String sql, char firstStatementChar)
//    throws SQLException
//  {
//    if ((firstStatementChar == 'I') || (firstStatementChar == 'U') || (firstStatementChar == 'D') || (firstStatementChar == 'A') || (firstStatementChar == 'C') || (firstStatementChar == 'T') || (firstStatementChar == 'R'))
//    {
//      String noCommentSql = StringUtils.stripComments(sql, "'\"", "'\"", true, false, true, true);
//
//      if ((StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DROP")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "CREATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "ALTER")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "TRUNCATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "RENAME")))
//      {
//        throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", getExceptionInterceptor());
//      }
//    }
//  }
//
//  protected void checkNullOrEmptyQuery(String sql)
//    throws SQLException
//  {
//    if (sql == null) {
//      throw SQLError.createSQLException(Messages.getString("Statement.59"), "S1009", getExceptionInterceptor());
//    }
//
//    if (sql.length() == 0)
//      throw SQLError.createSQLException(Messages.getString("Statement.61"), "S1009", getExceptionInterceptor());
//  }
//
//  public void clearBatch()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.batchedArgs != null)
//        this.batchedArgs.clear();
//    }
//  }
//
//  public void clearWarnings()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      this.clearWarningsCalled = true;
//      this.warningChain = null;
//    }
//  }
//
//  public void close()
//    throws SQLException
//  {
//    realClose(true, true);
//  }
//
//  protected void closeAllOpenResults()
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = this.connection;
//
//    if (locallyScopedConn == null) return;
//
//    synchronized (locallyScopedConn.getConnectionMutex()) {
//      if (this.openResults != null) {
//        for (ResultSetInternalMethods element : this.openResults) {
//          try {
//            element.realClose(false);
//          } catch (SQLException sqlEx) {
//            AssertionFailedException.shouldNotHappen(sqlEx);
//          }
//        }
//
//        this.openResults.clear();
//      }
//    }
//  }
//
//  protected void implicitlyCloseAllOpenResults()
//    throws SQLException
//  {
//    this.isImplicitlyClosingResults = true;
//    try {
//      if ((!this.connection.getHoldResultsOpenOverStatementClose()) && (!this.connection.getDontTrackOpenResources()) && (!this.holdResultsOpenOverClose))
//      {
//        if (this.results != null) {
//          this.results.realClose(false);
//        }
//        if (this.generatedKeysResults != null) {
//          this.generatedKeysResults.realClose(false);
//        }
//        closeAllOpenResults();
//      }
//    } finally {
//      this.isImplicitlyClosingResults = false;
//    }
//  }
//
//  public void removeOpenResultSet(ResultSetInternalMethods rs) {
//    try {
//      synchronized (checkClosed().getConnectionMutex()) {
//        if (this.openResults != null) {
//          this.openResults.remove(rs);
//        }
//
//        boolean hasMoreResults = rs.getNextResultSet() != null;
//
//        if ((this.results == rs) && (!hasMoreResults)) {
//          this.results = null;
//        }
//        if (this.generatedKeysResults == rs) {
//          this.generatedKeysResults = null;
//        }
//
//        if ((!this.isImplicitlyClosingResults) && (!hasMoreResults))
//          checkAndPerformCloseOnCompletionAction();
//      }
//    }
//    catch (SQLException e)
//    {
//    }
//  }
//
//  public int getOpenResultSetCount() {
//    try {
//      synchronized (checkClosed().getConnectionMutex()) {
//        if (this.openResults != null) {
//          return this.openResults.size();
//        }
//
//        return 0;
//      }
//    }
//    catch (SQLException e) {
//    }
//    return 0;
//  }
//
//  private void checkAndPerformCloseOnCompletionAction()
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        if ((isCloseOnCompletion()) && (!this.connection.getDontTrackOpenResources()) && (getOpenResultSetCount() == 0) && ((this.results == null) || (!this.results.reallyResult()) || (this.results.isClosed())) && ((this.generatedKeysResults == null) || (!this.generatedKeysResults.reallyResult()) || (this.generatedKeysResults.isClosed())))
//        {
//          realClose(false, false);
//        }
//      }
//    }
//    catch (SQLException e)
//    {
//    }
//  }
//
//  private ResultSetInternalMethods createResultSetUsingServerFetch(String sql)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      java.sql.PreparedStatement pStmt = this.connection.prepareStatement(sql, this.resultSetType, this.resultSetConcurrency);
//
//      pStmt.setFetchSize(this.fetchSize);
//
//      if (this.maxRows > -1) {
//        pStmt.setMaxRows(this.maxRows);
//      }
//
//      statementBegins();
//
//      pStmt.execute();
//
//      ResultSetInternalMethods rs = ((StatementImpl)pStmt).getResultSetInternal();
//
//      rs.setStatementUsedForFetchingRows((PreparedStatement)pStmt);
//
//      this.results = rs;
//
//      return rs;
//    }
//  }
//
//  protected boolean createStreamingResultSet()
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        return (this.resultSetType == 1003) && (this.resultSetConcurrency == 1007) && (this.fetchSize == -2147483648);
//      }
//    }
//    catch (SQLException e)
//    {
//    }
//    return false;
//  }
//
//  public void enableStreamingResults()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      this.originalResultSetType = this.resultSetType;
//      this.originalFetchSize = this.fetchSize;
//
//      setFetchSize(-2147483648);
//      setResultSetType(1003);
//    }
//  }
//
//  public void disableStreamingResults() throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if ((this.fetchSize == -2147483648) && (this.resultSetType == 1003))
//      {
//        setFetchSize(this.originalFetchSize);
//        setResultSetType(this.originalResultSetType);
//      }
//    }
//  }
//
//  public boolean execute(String sql)
//    throws SQLException
//  {
//    return execute(sql, false);
//  }
//
//  private boolean execute(String sql, boolean returnGeneratedKeys) throws SQLException {
//    MySQLConnection locallyScopedConn = checkClosed();
//    char firstNonWsChar;
//    boolean isSelect;
//    boolean doStreaming;
//    synchronized (locallyScopedConn.getConnectionMutex()) {
//      this.retrieveGeneratedKeys = returnGeneratedKeys;
//      this.lastQueryIsOnDupKeyUpdate = false;
//      if (returnGeneratedKeys) {
//        this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyInString(sql);
//      }
//      resetCancelledState();
//
//      checkNullOrEmptyQuery(sql);
//
//      checkClosed();
//
//      firstNonWsChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
//
//      isSelect = true;
//
//      if (firstNonWsChar != 'S') {
//        isSelect = false;
//
//        if (locallyScopedConn.isReadOnly()) {
//          throw SQLError.createSQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009", getExceptionInterceptor());
//        }
//
//      }
//
//      doStreaming = createStreamingResultSet();
//    }
//
//    try
//    {
//      if ((doStreaming) && (locallyScopedConn.getNetTimeoutForStreamingResults() > 0))
//      {
//        executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + locallyScopedConn.getNetTimeoutForStreamingResults());
//      }
//      Object escapedSqlResult;
//      if (this.doEscapeProcessing) {
//        escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), locallyScopedConn);
//
//        if ((escapedSqlResult instanceof String))
//          sql = (String)escapedSqlResult;
//        else {
//          sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
//        }
//      }
//
//      implicitlyCloseAllOpenResults();
//
//      if ((sql.charAt(0) == '/') && 
//        (sql.startsWith("/* ping */"))) {
//        doPingInstead();
//
//        escapedSqlResult = 1; jsr 491; return escapedSqlResult;
//      }
//
//      CachedResultSetMetaData cachedMetaData = null;
//
//      ResultSetInternalMethods rs = null;
//
//      this.batchedGeneratedKeys = null;
//
//      if (useServerFetch()) {
//        rs = createResultSetUsingServerFetch(sql);
//      } else {
//        timeoutTask = null;
//
//        String oldCatalog = null;
//        try
//        {
//          if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
//          {
//            timeoutTask = new CancelTask(this);
//            locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
//          }
//
//          if (!locallyScopedConn.getCatalog().equals(this.currentCatalog))
//          {
//            oldCatalog = locallyScopedConn.getCatalog();
//            locallyScopedConn.setCatalog(this.currentCatalog);
//          }
//
//          Field[] cachedFields = null;
//
//          if (locallyScopedConn.getCacheResultSetMetadata()) {
//            cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
//
//            if (cachedMetaData != null) {
//              cachedFields = cachedMetaData.fields;
//            }
//
//          }
//
//          locallyScopedConn.setSessionMaxRows(isSelect ? this.maxRows : -1);
//
//          statementBegins();
//
//          rs = locallyScopedConn.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
//
//          if (timeoutTask != null) {
//            if (timeoutTask.caughtWhileCancelling != null) {
//              throw timeoutTask.caughtWhileCancelling;
//            }
//
//            timeoutTask.cancel();
//            timeoutTask = null;
//          }
//
//          synchronized (this.cancelTimeoutMutex) {
//            if (this.wasCancelled) {
//              SQLException cause = null;
//
//              if (this.wasCancelledByTimeout)
//                cause = new MySQLTimeoutException();
//              else {
//                cause = new MySQLStatementCancelledException();
//              }
//
//              resetCancelledState();
//
//              throw cause;
//            }
//          }
//        } finally {
//          if (timeoutTask != null) {
//            timeoutTask.cancel();
//            locallyScopedConn.getCancelTimer().purge();
//          }
//
//          if (oldCatalog != null) {
//            locallyScopedConn.setCatalog(oldCatalog);
//          }
//        }
//      }
//
//      if (rs != null) {
//        this.lastInsertId = rs.getUpdateID();
//
//        this.results = rs;
//
//        rs.setFirstCharOfQuery(firstNonWsChar);
//
//        if (rs.reallyResult()) {
//          if (cachedMetaData != null) {
//            locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
//          }
//          else if (this.connection.getCacheResultSetMetadata()) {
//            locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
//          }
//
//        }
//
//      }
//
//      CancelTask timeoutTask = (rs != null) && (rs.reallyResult()) ? 1 : 0; jsr 17; return timeoutTask;
//    } finally {
//      jsr 6; } localObject5 = returnAddress; this.statementExecuting.set(false); ret;
//
//    localObject6 = finally;
//
//    throw localObject6;
//  }
//
//  protected void statementBegins() {
//    this.clearWarningsCalled = false;
//    this.statementExecuting.set(true);
//  }
//
//  protected void resetCancelledState() throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.cancelTimeoutMutex == null) {
//        return;
//      }
//
//      synchronized (this.cancelTimeoutMutex) {
//        this.wasCancelled = false;
//        this.wasCancelledByTimeout = false;
//      }
//    }
//  }
//
//  public boolean execute(String sql, int returnGeneratedKeys)
//    throws SQLException
//  {
//    if (returnGeneratedKeys == 1) {
//      checkClosed();
//
//      MySQLConnection locallyScopedConn = this.connection;
//
//      synchronized (locallyScopedConn.getConnectionMutex())
//      {
//        boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
//
//        locallyScopedConn.setReadInfoMsgEnabled(true);
//        try
//        {
//          boolean bool1 = execute(sql, true); jsr 17; return bool1;
//        } finally {
//          jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
//      }
//
//    }
//
//    return execute(sql);
//  }
//
//  public boolean execute(String sql, int[] generatedKeyIndices)
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = checkClosed();
//
//    synchronized (locallyScopedConn.getConnectionMutex()) {
//      if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0))
//      {
//        this.retrieveGeneratedKeys = true;
//
//        boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
//
//        locallyScopedConn.setReadInfoMsgEnabled(true);
//        try
//        {
//          boolean bool1 = execute(sql, true); jsr 17; return bool1;
//        } finally {
//          jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
//      }
//
//      return execute(sql);
//    }
//  }
//
//  public boolean execute(String sql, String[] generatedKeyNames)
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = checkClosed();
//
//    synchronized (locallyScopedConn.getConnectionMutex()) {
//      if ((generatedKeyNames != null) && (generatedKeyNames.length > 0))
//      {
//        this.retrieveGeneratedKeys = true;
//
//        boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
//
//        locallyScopedConn.setReadInfoMsgEnabled(true);
//        try
//        {
//          boolean bool1 = execute(sql, true); jsr 17; return bool1;
//        } finally {
//          jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
//      }
//
//      return execute(sql);
//    }
//  }
//
//  public int[] executeBatch()
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = checkClosed();
//
//    synchronized (locallyScopedConn.getConnectionMutex()) {
//      if (locallyScopedConn.isReadOnly()) {
//        throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009", getExceptionInterceptor());
//      }
//
//      implicitlyCloseAllOpenResults();
//
//      if ((this.batchedArgs == null) || (this.batchedArgs.size() == 0)) {
//        return new int[0];
//      }
//
//      int individualStatementTimeout = this.timeoutInMillis;
//      this.timeoutInMillis = 0;
//
//      CancelTask timeoutTask = null;
//      try
//      {
//        resetCancelledState();
//
//        statementBegins();
//        try
//        {
//          this.retrieveGeneratedKeys = true;
//
//          int[] updateCounts = null;
//
//          if (this.batchedArgs != null) {
//            nbrCommands = this.batchedArgs.size();
//
//            this.batchedGeneratedKeys = new ArrayList(this.batchedArgs.size());
//
//            boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
//
//            if ((locallyScopedConn.versionMeetsMinimum(4, 1, 1)) && ((multiQueriesEnabled) || ((locallyScopedConn.getRewriteBatchedStatements()) && (nbrCommands > 4))))
//            {
//              int[] arrayOfInt1 = executeBatchUsingMultiQueries(multiQueriesEnabled, nbrCommands, individualStatementTimeout); return arrayOfInt1;
//            }
//
//            if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
//            {
//              timeoutTask = new CancelTask(this);
//              locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
//            }
//
//            updateCounts = new int[nbrCommands];
//
//            for (int i = 0; i < nbrCommands; i++) {
//              updateCounts[i] = -3;
//            }
//
//            SQLException sqlEx = null;
//
//            int commandIndex = 0;
//
//            for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
//              try {
//                String sql = (String)this.batchedArgs.get(commandIndex);
//                updateCounts[commandIndex] = executeUpdate(sql, true, true);
//
//                getBatchedGeneratedKeys(containsOnDuplicateKeyInString(sql) ? 1 : 0);
//              } catch (SQLException ex) {
//                updateCounts[commandIndex] = -3;
//
//                if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
//                {
//                  sqlEx = ex;
//                } else {
//                  int[] newUpdateCounts = new int[commandIndex];
//
//                  if (hasDeadlockOrTimeoutRolledBackTx(ex)) {
//                    for (int i = 0; i < newUpdateCounts.length; i++)
//                      newUpdateCounts[i] = -3;
//                  }
//                  else {
//                    System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
//                  }
//
//                  throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
//                }
//
//              }
//
//            }
//
//            if (sqlEx != null) {
//              throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
//            }
//
//          }
//
//          if (timeoutTask != null) {
//            if (timeoutTask.caughtWhileCancelling != null) {
//              throw timeoutTask.caughtWhileCancelling;
//            }
//
//            timeoutTask.cancel();
//
//            locallyScopedConn.getCancelTimer().purge();
//            timeoutTask = null;
//          }
//
//          int nbrCommands = updateCounts != null ? updateCounts : new int[0]; return nbrCommands;
//        } finally {
//          this.statementExecuting.set(false);
//        }
//      }
//      finally {
//        jsr 6; } localObject4 = returnAddress; if (timeoutTask != null) {
//        timeoutTask.cancel();
//
//        locallyScopedConn.getCancelTimer().purge();
//      }
//
//      resetCancelledState();
//
//      this.timeoutInMillis = individualStatementTimeout;
//
//      clearBatch(); ret;
//    }
//  }
//
//  protected final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex)
//  {
//    int vendorCode = ex.getErrorCode();
//
//    switch (vendorCode) {
//    case 1206:
//    case 1213:
//      return true;
//    case 1205:
//      return !this.version5013OrNewer;
//    }
//    return false;
//  }
//
//  private int[] executeBatchUsingMultiQueries(boolean multiQueriesEnabled, int nbrCommands, int individualStatementTimeout)
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = checkClosed();
//
//    synchronized (locallyScopedConn.getConnectionMutex()) {
//      if (!multiQueriesEnabled) {
//        locallyScopedConn.getIO().enableMultiQueries();
//      }
//
//      java.sql.Statement batchStmt = null;
//
//      CancelTask timeoutTask = null;
//      try
//      {
//        int[] updateCounts = new int[nbrCommands];
//
//        for (int i = 0; i < nbrCommands; i++) {
//          updateCounts[i] = -3;
//        }
//
//        int commandIndex = 0;
//
//        StringBuffer queryBuf = new StringBuffer();
//
//        batchStmt = locallyScopedConn.createStatement();
//
//        if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
//        {
//          timeoutTask = new CancelTask((StatementImpl)batchStmt);
//          locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
//        }
//
//        int counter = 0;
//
//        int numberOfBytesPerChar = 1;
//
//        String connectionEncoding = locallyScopedConn.getEncoding();
//
//        if (StringUtils.startsWithIgnoreCase(connectionEncoding, "utf"))
//          numberOfBytesPerChar = 3;
//        else if (CharsetMapping.isMultibyteCharset(connectionEncoding)) {
//          numberOfBytesPerChar = 2;
//        }
//
//        int escapeAdjust = 1;
//
//        batchStmt.setEscapeProcessing(this.doEscapeProcessing);
//
//        if (this.doEscapeProcessing)
//        {
//          escapeAdjust = 2;
//        }
//
//        SQLException sqlEx = null;
//
//        int argumentSetsInBatchSoFar = 0;
//
//        for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
//          String nextQuery = (String)this.batchedArgs.get(commandIndex);
//
//          if (((queryBuf.length() + nextQuery.length()) * numberOfBytesPerChar + 1 + 4) * escapeAdjust + 32 > this.connection.getMaxAllowedPacket())
//          {
//            try
//            {
//              batchStmt.execute(queryBuf.toString(), 1);
//            } catch (SQLException ex) {
//              sqlEx = handleExceptionForBatch(commandIndex, argumentSetsInBatchSoFar, updateCounts, ex);
//            }
//
//            counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
//
//            queryBuf = new StringBuffer();
//            argumentSetsInBatchSoFar = 0;
//          }
//
//          queryBuf.append(nextQuery);
//          queryBuf.append(";");
//          argumentSetsInBatchSoFar++;
//        }
//
//        if (queryBuf.length() > 0) {
//          try {
//            batchStmt.execute(queryBuf.toString(), 1);
//          } catch (SQLException ex) {
//            sqlEx = handleExceptionForBatch(commandIndex - 1, argumentSetsInBatchSoFar, updateCounts, ex);
//          }
//
//          counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
//        }
//
//        if (timeoutTask != null) {
//          if (timeoutTask.caughtWhileCancelling != null) {
//            throw timeoutTask.caughtWhileCancelling;
//          }
//
//          timeoutTask.cancel();
//
//          locallyScopedConn.getCancelTimer().purge();
//
//          timeoutTask = null;
//        }
//
//        if (sqlEx != null) {
//          throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
//        }
//
//        ex = updateCounts != null ? updateCounts : new int[0]; jsr 17; return ex;
//      } finally {
//        jsr 6; } localObject2 = returnAddress; if (timeoutTask != null) {
//        timeoutTask.cancel();
//
//        locallyScopedConn.getCancelTimer().purge();
//      }
//
//      resetCancelledState();
//      try
//      {
//        if (batchStmt != null)
//          batchStmt.close();
//      }
//      finally {
//        if (!multiQueriesEnabled)
//          locallyScopedConn.getIO().disableMultiQueries();  }  } ret;
//
//    localObject5 = finally;
//
//    throw localObject5;
//  }
//
//  protected int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, int[] updateCounts)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      updateCounts[(updateCountCounter++)] = batchedStatement.getUpdateCount();
//
//      boolean doGenKeys = this.batchedGeneratedKeys != null;
//
//      byte[][] row = (byte[][])null;
//
//      if (doGenKeys) {
//        long generatedKey = batchedStatement.getLastInsertID();
//
//        row = new byte[1][];
//        row[0] = StringUtils.getBytes(Long.toString(generatedKey));
//        this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
//      }
//
//      while ((batchedStatement.getMoreResults()) || (batchedStatement.getUpdateCount() != -1)) {
//        updateCounts[(updateCountCounter++)] = batchedStatement.getUpdateCount();
//
//        if (doGenKeys) {
//          long generatedKey = batchedStatement.getLastInsertID();
//
//          row = new byte[1][];
//          row[0] = StringUtils.getBytes(Long.toString(generatedKey));
//          this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
//        }
//      }
//
//      return updateCountCounter;
//    }
//  }
//
//  protected SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, int[] updateCounts, SQLException ex)
//    throws BatchUpdateException
//  {
//    for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; j--)
//      updateCounts[j] = -3;
//    SQLException sqlEx;
//    if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
//    {
//      sqlEx = ex;
//    } else {
//      int[] newUpdateCounts = new int[endOfBatchIndex];
//      System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex);
//
//      BatchUpdateException batchException = new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
//
//      batchException.initCause(ex);
//      throw batchException;
//    }
//    SQLException sqlEx;
//    return sqlEx;
//  }
//
//  public ResultSet executeQuery(String sql)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      MySQLConnection locallyScopedConn = this.connection;
//
//      this.retrieveGeneratedKeys = false;
//
//      resetCancelledState();
//
//      checkNullOrEmptyQuery(sql);
//
//      boolean doStreaming = createStreamingResultSet();
//
//      if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
//      {
//        executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
//      }
//
//      if (this.doEscapeProcessing) {
//        Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), this.connection);
//
//        if ((escapedSqlResult instanceof String))
//          sql = (String)escapedSqlResult;
//        else {
//          sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
//        }
//      }
//
//      char firstStatementChar = StringUtils.firstNonWsCharUc(sql, findStartOfStatement(sql));
//
//      if ((sql.charAt(0) == '/') && 
//        (sql.startsWith("/* ping */"))) {
//        doPingInstead();
//
//        return this.results;
//      }
//
//      checkForDml(sql, firstStatementChar);
//
//      implicitlyCloseAllOpenResults();
//
//      CachedResultSetMetaData cachedMetaData = null;
//
//      if (useServerFetch()) {
//        this.results = createResultSetUsingServerFetch(sql);
//
//        return this.results;
//      }
//
//      CancelTask timeoutTask = null;
//
//      String oldCatalog = null;
//      try
//      {
//        if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
//        {
//          timeoutTask = new CancelTask(this);
//          locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
//        }
//
//        if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
//          oldCatalog = locallyScopedConn.getCatalog();
//          locallyScopedConn.setCatalog(this.currentCatalog);
//        }
//
//        Field[] cachedFields = null;
//
//        if (locallyScopedConn.getCacheResultSetMetadata()) {
//          cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
//
//          if (cachedMetaData != null) {
//            cachedFields = cachedMetaData.fields;
//          }
//        }
//
//        locallyScopedConn.setSessionMaxRows(this.maxRows);
//
//        statementBegins();
//
//        this.results = locallyScopedConn.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
//
//        if (timeoutTask != null) {
//          if (timeoutTask.caughtWhileCancelling != null) {
//            throw timeoutTask.caughtWhileCancelling;
//          }
//
//          timeoutTask.cancel();
//
//          locallyScopedConn.getCancelTimer().purge();
//
//          timeoutTask = null;
//        }
//
//        synchronized (this.cancelTimeoutMutex) {
//          if (this.wasCancelled) {
//            SQLException cause = null;
//
//            if (this.wasCancelledByTimeout)
//              cause = new MySQLTimeoutException();
//            else {
//              cause = new MySQLStatementCancelledException();
//            }
//
//            resetCancelledState();
//
//            throw cause;
//          }
//        }
//      } finally {
//        this.statementExecuting.set(false);
//
//        if (timeoutTask != null) {
//          timeoutTask.cancel();
//
//          locallyScopedConn.getCancelTimer().purge();
//        }
//
//        if (oldCatalog != null) {
//          locallyScopedConn.setCatalog(oldCatalog);
//        }
//      }
//
//      this.lastInsertId = this.results.getUpdateID();
//
//      if (cachedMetaData != null) {
//        locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
//      }
//      else if (this.connection.getCacheResultSetMetadata()) {
//        locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
//      }
//
//      return this.results;
//    }
//  }
//
//  protected void doPingInstead() throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.pingTarget != null)
//        this.pingTarget.doPing();
//      else {
//        this.connection.ping();
//      }
//
//      ResultSetInternalMethods fakeSelectOneResultSet = generatePingResultSet();
//      this.results = fakeSelectOneResultSet;
//    }
//  }
//
//  protected ResultSetInternalMethods generatePingResultSet() throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      Field[] fields = { new Field(null, "1", -5, 1) };
//      ArrayList rows = new ArrayList();
//      byte[] colVal = { 49 };
//
//      rows.add(new ByteArrayRow(new byte[][] { colVal }, getExceptionInterceptor()));
//
//      return (ResultSetInternalMethods)DatabaseMetaData.buildResultSet(fields, rows, this.connection);
//    }
//  }
//
//  protected void executeSimpleNonQuery(MySQLConnection c, String nonQuery)
//    throws SQLException
//  {
//    c.execSQL(this, nonQuery, -1, null, 1003, 1007, false, this.currentCatalog, null, false).close();
//  }
//
//  public int executeUpdate(String sql)
//    throws SQLException
//  {
//    return executeUpdate(sql, false, false);
//  }
//
//  protected int executeUpdate(String sql, boolean isBatch, boolean returnGeneratedKeys)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      MySQLConnection locallyScopedConn = this.connection;
//
//      char firstStatementChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
//
//      ResultSetInternalMethods rs = null;
//
//      this.retrieveGeneratedKeys = returnGeneratedKeys;
//
//      resetCancelledState();
//
//      checkNullOrEmptyQuery(sql);
//
//      if (this.doEscapeProcessing) {
//        Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn(), this.connection);
//
//        if ((escapedSqlResult instanceof String))
//          sql = (String)escapedSqlResult;
//        else {
//          sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
//        }
//      }
//
//      if (locallyScopedConn.isReadOnly(false)) {
//        throw SQLError.createSQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009", getExceptionInterceptor());
//      }
//
//      if (StringUtils.startsWithIgnoreCaseAndWs(sql, "select")) {
//        throw SQLError.createSQLException(Messages.getString("Statement.46"), "01S03", getExceptionInterceptor());
//      }
//
//      implicitlyCloseAllOpenResults();
//
//      CancelTask timeoutTask = null;
//
//      String oldCatalog = null;
//      try
//      {
//        if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
//        {
//          timeoutTask = new CancelTask(this);
//          locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
//        }
//
//        if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
//          oldCatalog = locallyScopedConn.getCatalog();
//          locallyScopedConn.setCatalog(this.currentCatalog);
//        }
//
//        locallyScopedConn.setSessionMaxRows(-1);
//
//        statementBegins();
//
//        rs = locallyScopedConn.execSQL(this, sql, -1, null, 1003, 1007, false, this.currentCatalog, null, isBatch);
//
//        if (timeoutTask != null) {
//          if (timeoutTask.caughtWhileCancelling != null) {
//            throw timeoutTask.caughtWhileCancelling;
//          }
//
//          timeoutTask.cancel();
//
//          locallyScopedConn.getCancelTimer().purge();
//
//          timeoutTask = null;
//        }
//
//        synchronized (this.cancelTimeoutMutex) {
//          if (this.wasCancelled) {
//            SQLException cause = null;
//
//            if (this.wasCancelledByTimeout)
//              cause = new MySQLTimeoutException();
//            else {
//              cause = new MySQLStatementCancelledException();
//            }
//
//            resetCancelledState();
//
//            throw cause;
//          }
//        }
//      } finally {
//        if (timeoutTask != null) {
//          timeoutTask.cancel();
//
//          locallyScopedConn.getCancelTimer().purge();
//        }
//
//        if (oldCatalog != null) {
//          locallyScopedConn.setCatalog(oldCatalog);
//        }
//
//        if (!isBatch) {
//          this.statementExecuting.set(false);
//        }
//      }
//
//      this.results = rs;
//
//      rs.setFirstCharOfQuery(firstStatementChar);
//
//      this.updateCount = rs.getUpdateCount();
//
//      int truncatedUpdateCount = 0;
//
//      if (this.updateCount > 2147483647L)
//        truncatedUpdateCount = 2147483647;
//      else {
//        truncatedUpdateCount = (int)this.updateCount;
//      }
//
//      this.lastInsertId = rs.getUpdateID();
//
//      return truncatedUpdateCount;
//    }
//  }
//
//  public int executeUpdate(String sql, int returnGeneratedKeys)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (returnGeneratedKeys == 1) {
//        MySQLConnection locallyScopedConn = this.connection;
//
//        boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
//
//        locallyScopedConn.setReadInfoMsgEnabled(true);
//        try
//        {
//          int i = executeUpdate(sql, false, true); jsr 16; return i;
//        } finally {
//          jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
//      }
//
//      return executeUpdate(sql);
//    }
//  }
//
//  public int executeUpdate(String sql, int[] generatedKeyIndices)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0)) {
//        checkClosed();
//
//        MySQLConnection locallyScopedConn = this.connection;
//
//        boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
//
//        locallyScopedConn.setReadInfoMsgEnabled(true);
//        try
//        {
//          int i = executeUpdate(sql, false, true); jsr 16; return i;
//        } finally {
//          jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
//      }
//
//      return executeUpdate(sql);
//    }
//  }
//
//  public int executeUpdate(String sql, String[] generatedKeyNames)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if ((generatedKeyNames != null) && (generatedKeyNames.length > 0)) {
//        MySQLConnection locallyScopedConn = this.connection;
//
//        boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
//
//        locallyScopedConn.setReadInfoMsgEnabled(true);
//        try
//        {
//          int i = executeUpdate(sql, false, true); jsr 16; return i;
//        } finally {
//          jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
//      }
//
//      return executeUpdate(sql);
//    }
//  }
//
//  protected Calendar getCalendarInstanceForSessionOrNew()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.connection != null) {
//        return this.connection.getCalendarInstanceForSessionOrNew();
//      }
//
//      return new GregorianCalendar();
//    }
//  }
//
//  public java.sql.Connection getConnection()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.connection;
//    }
//  }
//
//  public int getFetchDirection()
//    throws SQLException
//  {
//    return 1000;
//  }
//
//  public int getFetchSize()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.fetchSize;
//    }
//  }
//
//  public ResultSet getGeneratedKeys()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (!this.retrieveGeneratedKeys) {
//        throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), "S1009", getExceptionInterceptor());
//      }
//
//      if (this.batchedGeneratedKeys == null) {
//        if (this.lastQueryIsOnDupKeyUpdate) {
//          return this.generatedKeysResults = getGeneratedKeysInternal(1);
//        }
//        return this.generatedKeysResults = getGeneratedKeysInternal();
//      }
//
//      Field[] fields = new Field[1];
//      fields[0] = new Field("", "GENERATED_KEY", -5, 17);
//      fields[0].setConnection(this.connection);
//
//      this.generatedKeysResults = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(this.batchedGeneratedKeys), this.connection, this, false);
//
//      return this.generatedKeysResults;
//    }
//  }
//
//  protected ResultSetInternalMethods getGeneratedKeysInternal()
//    throws SQLException
//  {
//    int numKeys = getUpdateCount();
//    return getGeneratedKeysInternal(numKeys);
//  }
//
//  protected ResultSetInternalMethods getGeneratedKeysInternal(int numKeys) throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      Field[] fields = new Field[1];
//      fields[0] = new Field("", "GENERATED_KEY", -5, 17);
//      fields[0].setConnection(this.connection);
//      fields[0].setUseOldNameMetadata(true);
//
//      ArrayList rowSet = new ArrayList();
//
//      long beginAt = getLastInsertID();
//
//      if (beginAt < 0L) {
//        fields[0].setUnsigned();
//      }
//
//      if (this.results != null) {
//        String serverInfo = this.results.getServerInfo();
//
//        if ((numKeys > 0) && (this.results.getFirstCharOfQuery() == 'R') && (serverInfo != null) && (serverInfo.length() > 0))
//        {
//          numKeys = getRecordCountFromInfo(serverInfo);
//        }
//
//        if ((beginAt != 0L) && (numKeys > 0)) {
//          for (int i = 0; i < numKeys; i++) {
//            byte[][] row = new byte[1][];
//            if (beginAt > 0L) {
//              row[0] = StringUtils.getBytes(Long.toString(beginAt));
//            } else {
//              byte[] asBytes = new byte[8];
//              asBytes[7] = ((byte)(int)(beginAt & 0xFF));
//              asBytes[6] = ((byte)(int)(beginAt >>> 8));
//              asBytes[5] = ((byte)(int)(beginAt >>> 16));
//              asBytes[4] = ((byte)(int)(beginAt >>> 24));
//              asBytes[3] = ((byte)(int)(beginAt >>> 32));
//              asBytes[2] = ((byte)(int)(beginAt >>> 40));
//              asBytes[1] = ((byte)(int)(beginAt >>> 48));
//              asBytes[0] = ((byte)(int)(beginAt >>> 56));
//
//              BigInteger val = new BigInteger(1, asBytes);
//
//              row[0] = val.toString().getBytes();
//            }
//            rowSet.add(new ByteArrayRow(row, getExceptionInterceptor()));
//            beginAt += this.connection.getAutoIncrementIncrement();
//          }
//        }
//      }
//
//      ResultSetImpl gkRs = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(rowSet), this.connection, this, false);
//
//      return gkRs;
//    }
//  }
//
//  protected int getId()
//  {
//    return this.statementId;
//  }
//
//  public long getLastInsertID()
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        return this.lastInsertId;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public long getLongUpdateCount()
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        if (this.results == null) {
//          return -1L;
//        }
//
//        if (this.results.reallyResult()) {
//          return -1L;
//        }
//
//        return this.updateCount;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public int getMaxFieldSize()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.maxFieldSize;
//    }
//  }
//
//  public int getMaxRows()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.maxRows <= 0) {
//        return 0;
//      }
//
//      return this.maxRows;
//    }
//  }
//
//  public boolean getMoreResults()
//    throws SQLException
//  {
//    return getMoreResults(1);
//  }
//
//  public boolean getMoreResults(int current)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.results == null) {
//        return false;
//      }
//
//      boolean streamingMode = createStreamingResultSet();
//
//      while ((streamingMode) && 
//        (this.results.reallyResult()) && 
//        (this.results.next()));
//      ResultSetInternalMethods nextResultSet = this.results.getNextResultSet();
//
//      switch (current)
//      {
//      case 1:
//        if (this.results != null) {
//          if ((!streamingMode) && (!this.connection.getDontTrackOpenResources())) {
//            this.results.realClose(false);
//          }
//
//          this.results.clearNextResult(); } break;
//      case 3:
//        if (this.results != null) {
//          if ((!streamingMode) && (!this.connection.getDontTrackOpenResources())) {
//            this.results.realClose(false);
//          }
//
//          this.results.clearNextResult();
//        }
//
//        closeAllOpenResults();
//
//        break;
//      case 2:
//        if (!this.connection.getDontTrackOpenResources()) {
//          this.openResults.add(this.results);
//        }
//
//        this.results.clearNextResult();
//
//        break;
//      default:
//        throw SQLError.createSQLException(Messages.getString("Statement.19"), "S1009", getExceptionInterceptor());
//      }
//
//      this.results = nextResultSet;
//
//      if (this.results == null) {
//        this.updateCount = -1L;
//        this.lastInsertId = -1L;
//      } else if (this.results.reallyResult()) {
//        this.updateCount = -1L;
//        this.lastInsertId = -1L;
//      } else {
//        this.updateCount = this.results.getUpdateCount();
//        this.lastInsertId = this.results.getUpdateID();
//      }
//
//      boolean moreResults = (this.results != null) && (this.results.reallyResult());
//      if (!moreResults)
//        checkAndPerformCloseOnCompletionAction();
//      return moreResults;
//    }
//  }
//
//  public int getQueryTimeout()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.timeoutInMillis / 1000;
//    }
//  }
//
//  private int getRecordCountFromInfo(String serverInfo)
//  {
//    StringBuffer recordsBuf = new StringBuffer();
//    int recordsCount = 0;
//    int duplicatesCount = 0;
//
//    char c = '\000';
//
//    int length = serverInfo.length();
//    for (int i = 0; 
//      i < length; i++) {
//      c = serverInfo.charAt(i);
//
//      if (Character.isDigit(c))
//      {
//        break;
//      }
//    }
//    recordsBuf.append(c);
//    i++;
//
//    for (; i < length; i++) {
//      c = serverInfo.charAt(i);
//
//      if (!Character.isDigit(c))
//      {
//        break;
//      }
//      recordsBuf.append(c);
//    }
//
//    recordsCount = Integer.parseInt(recordsBuf.toString());
//
//    StringBuffer duplicatesBuf = new StringBuffer();
//
//    for (; i < length; i++) {
//      c = serverInfo.charAt(i);
//
//      if (Character.isDigit(c))
//      {
//        break;
//      }
//    }
//    duplicatesBuf.append(c);
//    i++;
//
//    for (; i < length; i++) {
//      c = serverInfo.charAt(i);
//
//      if (!Character.isDigit(c))
//      {
//        break;
//      }
//      duplicatesBuf.append(c);
//    }
//
//    duplicatesCount = Integer.parseInt(duplicatesBuf.toString());
//
//    return recordsCount - duplicatesCount;
//  }
//
//  public ResultSet getResultSet()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return (this.results != null) && (this.results.reallyResult()) ? this.results : null;
//    }
//  }
//
//  public int getResultSetConcurrency()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.resultSetConcurrency;
//    }
//  }
//
//  public int getResultSetHoldability()
//    throws SQLException
//  {
//    return 1;
//  }
//
//  protected ResultSetInternalMethods getResultSetInternal() {
//    try {
//      synchronized (checkClosed().getConnectionMutex()) {
//        return this.results;
//      }
//    } catch (SQLException e) {  }
//
//    return this.results;
//  }
//
//  public int getResultSetType()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.resultSetType;
//    }
//  }
//
//  public int getUpdateCount()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.results == null) {
//        return -1;
//      }
//
//      if (this.results.reallyResult()) {
//        return -1;
//      }
//
//      int truncatedUpdateCount = 0;
//
//      if (this.results.getUpdateCount() > 2147483647L)
//        truncatedUpdateCount = 2147483647;
//      else {
//        truncatedUpdateCount = (int)this.results.getUpdateCount();
//      }
//
//      return truncatedUpdateCount;
//    }
//  }
//
//  public SQLWarning getWarnings()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      if (this.clearWarningsCalled) {
//        return null;
//      }
//
//      if (this.connection.versionMeetsMinimum(4, 1, 0)) {
//        SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings(this.connection);
//
//        if (this.warningChain != null)
//          this.warningChain.setNextWarning(pendingWarningsFromServer);
//        else {
//          this.warningChain = pendingWarningsFromServer;
//        }
//
//        return this.warningChain;
//      }
//
//      return this.warningChain;
//    }
//  }
//
//  protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = this.connection;
//
//    if (locallyScopedConn == null) return;
//
//    synchronized (locallyScopedConn.getConnectionMutex())
//    {
//      if (this.isClosed) return;
//
//      if ((this.useUsageAdvisor) && 
//        (!calledExplicitly)) {
//        String message = Messages.getString("Statement.63") + Messages.getString("Statement.64");
//
//        this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
//      }
//
//      if (closeOpenResults) {
//        closeOpenResults = (!this.holdResultsOpenOverClose) && (!this.connection.getDontTrackOpenResources());
//      }
//
//      if (closeOpenResults) {
//        if (this.results != null) {
//          try
//          {
//            this.results.close();
//          }
//          catch (Exception ex)
//          {
//          }
//        }
//        if (this.generatedKeysResults != null) {
//          try
//          {
//            this.generatedKeysResults.close();
//          }
//          catch (Exception ex)
//          {
//          }
//        }
//        closeAllOpenResults();
//      }
//
//      if ((this.connection != null) && 
//        (!this.connection.getDontTrackOpenResources())) {
//        this.connection.unregisterStatement(this);
//      }
//
//      this.isClosed = true;
//
//      this.results = null;
//      this.generatedKeysResults = null;
//      this.connection = null;
//      this.warningChain = null;
//      this.openResults = null;
//      this.batchedGeneratedKeys = null;
//      this.localInfileInputStream = null;
//      this.pingTarget = null;
//    }
//  }
//
//  public void setCursorName(String name)
//    throws SQLException
//  {
//  }
//
//  public void setEscapeProcessing(boolean enable)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      this.doEscapeProcessing = enable;
//    }
//  }
//
//  public void setFetchDirection(int direction)
//    throws SQLException
//  {
//    switch (direction) {
//    case 1000:
//    case 1001:
//    case 1002:
//      break;
//    default:
//      throw SQLError.createSQLException(Messages.getString("Statement.5"), "S1009", getExceptionInterceptor());
//    }
//  }
//
//  public void setFetchSize(int rows)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (((rows < 0) && (rows != -2147483648)) || ((this.maxRows > 0) && (rows > getMaxRows())))
//      {
//        throw SQLError.createSQLException(Messages.getString("Statement.7"), "S1009", getExceptionInterceptor());
//      }
//
//      this.fetchSize = rows;
//    }
//  }
//
//  public void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose) {
//    try {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.holdResultsOpenOverClose = holdResultsOpenOverClose;
//      }
//    }
//    catch (SQLException e)
//    {
//    }
//  }
//
//  public void setMaxFieldSize(int max)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (max < 0) {
//        throw SQLError.createSQLException(Messages.getString("Statement.11"), "S1009", getExceptionInterceptor());
//      }
//
//      int maxBuf = this.connection != null ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
//
//      if (max > maxBuf) {
//        throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[] { Long.valueOf(maxBuf) }), "S1009", getExceptionInterceptor());
//      }
//
//      this.maxFieldSize = max;
//    }
//  }
//
//  public void setMaxRows(int max)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if ((max > 50000000) || (max < 0)) {
//        throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009", getExceptionInterceptor());
//      }
//
//      if (max == 0) {
//        max = -1;
//      }
//
//      this.maxRows = max;
//    }
//  }
//
//  public void setQueryTimeout(int seconds)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (seconds < 0) {
//        throw SQLError.createSQLException(Messages.getString("Statement.21"), "S1009", getExceptionInterceptor());
//      }
//
//      this.timeoutInMillis = (seconds * 1000);
//    }
//  }
//
//  void setResultSetConcurrency(int concurrencyFlag)
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.resultSetConcurrency = concurrencyFlag;
//      }
//    }
//    catch (SQLException e)
//    {
//    }
//  }
//
//  void setResultSetType(int typeFlag)
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.resultSetType = typeFlag;
//      }
//    }
//    catch (SQLException e)
//    {
//    }
//  }
//
//  protected void getBatchedGeneratedKeys(java.sql.Statement batchedStatement) throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.retrieveGeneratedKeys) {
//        ResultSet rs = null;
//        try
//        {
//          rs = batchedStatement.getGeneratedKeys();
//
//          while (rs.next())
//            this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
//        }
//        finally
//        {
//          if (rs != null)
//            rs.close();
//        }
//      }
//    }
//  }
//
//  protected void getBatchedGeneratedKeys(int maxKeys) throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.retrieveGeneratedKeys) {
//        ResultSet rs = null;
//        try
//        {
//          if (maxKeys == 0)
//            rs = getGeneratedKeysInternal();
//          else {
//            rs = getGeneratedKeysInternal(maxKeys);
//          }
//          while (rs.next())
//            this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
//        }
//        finally
//        {
//          this.isImplicitlyClosingResults = true;
//        }
//
//        ret;
//      }
//    }
//  }
//
//  private boolean useServerFetch()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return (this.connection.isCursorFetchEnabled()) && (this.fetchSize > 0) && (this.resultSetConcurrency == 1007) && (this.resultSetType == 1003);
//    }
//  }
//
//  public boolean isClosed()
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = this.connection;
//    if (locallyScopedConn == null) {
//      return true;
//    }
//    synchronized (locallyScopedConn.getConnectionMutex()) {
//      return this.isClosed;
//    }
//  }
//
//  public boolean isPoolable()
//    throws SQLException
//  {
//    return this.isPoolable;
//  }
//
//  public void setPoolable(boolean poolable) throws SQLException {
//    this.isPoolable = poolable;
//  }
//
//  public boolean isWrapperFor(Class<?> iface)
//    throws SQLException
//  {
//    checkClosed();
//
//    return iface.isInstance(this);
//  }
//
//  public Object unwrap(Class<?> iface)
//    throws SQLException
//  {
//    try
//    {
//      return Util.cast(iface, this); } catch (ClassCastException cce) {
//    }
//    throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
//  }
//
//  protected int findStartOfStatement(String sql)
//  {
//    int statementStartPos = 0;
//
//    if (StringUtils.startsWithIgnoreCaseAndWs(sql, "/*")) {
//      statementStartPos = sql.indexOf("*/");
//
//      if (statementStartPos == -1)
//        statementStartPos = 0;
//      else
//        statementStartPos += 2;
//    }
//    else if ((StringUtils.startsWithIgnoreCaseAndWs(sql, "--")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "#")))
//    {
//      statementStartPos = sql.indexOf('\n');
//
//      if (statementStartPos == -1) {
//        statementStartPos = sql.indexOf('\r');
//
//        if (statementStartPos == -1) {
//          statementStartPos = 0;
//        }
//      }
//    }
//
//    return statementStartPos;
//  }
//
//  public InputStream getLocalInfileInputStream()
//  {
//    return this.localInfileInputStream;
//  }
//
//  public void setLocalInfileInputStream(InputStream stream) {
//    this.localInfileInputStream = stream;
//  }
//
//  public void setPingTarget(PingTarget pingTarget) {
//    this.pingTarget = pingTarget;
//  }
//
//  public ExceptionInterceptor getExceptionInterceptor() {
//    return this.exceptionInterceptor;
//  }
//
//  protected boolean containsOnDuplicateKeyInString(String sql) {
//    return getOnDuplicateKeyLocation(sql) != -1;
//  }
//
//  protected int getOnDuplicateKeyLocation(String sql) {
//    return StringUtils.indexOfIgnoreCaseRespectMarker(0, sql, "ON DUPLICATE KEY UPDATE ", "\"'`", "\"'`", !this.connection.isNoBackslashEscapesSet());
//  }
//
//  public void closeOnCompletion()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      this.closeOnCompletion = true;
//    }
//  }
//
//  public boolean isCloseOnCompletion() throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.closeOnCompletion;
//    }
//  }
//
//  class CancelTask extends TimerTask
//  {
//    long connectionId = 0L;
//    String origHost = "";
//    SQLException caughtWhileCancelling = null;
//    StatementImpl toCancel;
//    Properties origConnProps = null;
//    String origConnURL = "";
//
//    CancelTask(StatementImpl cancellee) throws SQLException {
//      this.connectionId = cancellee.connectionId;
//      this.origHost = StatementImpl.this.connection.getHost();
//      this.toCancel = cancellee;
//      this.origConnProps = new Properties();
//
//      Properties props = StatementImpl.this.connection.getProperties();
//
//      Enumeration keys = props.propertyNames();
//
//      while (keys.hasMoreElements()) {
//        String key = keys.nextElement().toString();
//        this.origConnProps.setProperty(key, props.getProperty(key));
//      }
//
//      this.origConnURL = StatementImpl.this.connection.getURL();
//    }
//
//    public void run()
//    {
//      Thread cancelThread = new Thread()
//      {
//        public void run()
//        {
//          Connection cancelConn = null;
//          java.sql.Statement cancelStmt = null;
//          try
//          {
//            if (StatementImpl.this.connection.getQueryTimeoutKillsConnection()) {
//              StatementImpl.CancelTask.this.toCancel.wasCancelled = true;
//              StatementImpl.CancelTask.this.toCancel.wasCancelledByTimeout = true;
//              StatementImpl.this.connection.realClose(false, false, true, new MySQLStatementCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
//            }
//            else {
//              synchronized (StatementImpl.this.cancelTimeoutMutex) {
//                if (StatementImpl.CancelTask.this.origConnURL.equals(StatementImpl.this.connection.getURL()))
//                {
//                  cancelConn = StatementImpl.this.connection.duplicate();
//                  cancelStmt = cancelConn.createStatement();
//                  cancelStmt.execute("KILL QUERY " + StatementImpl.CancelTask.this.connectionId);
//                } else {
//                  try {
//                    cancelConn = (Connection)DriverManager.getConnection(StatementImpl.CancelTask.this.origConnURL, StatementImpl.CancelTask.this.origConnProps);
//                    cancelStmt = cancelConn.createStatement();
//                    cancelStmt.execute("KILL QUERY " + StatementImpl.CancelTask.this.connectionId);
//                  }
//                  catch (NullPointerException npe) {
//                  }
//                }
//                StatementImpl.CancelTask.this.toCancel.wasCancelled = true;
//                StatementImpl.CancelTask.this.toCancel.wasCancelledByTimeout = true;
//              }
//            }
//          } catch (SQLException sqlEx) {
//            StatementImpl.CancelTask.this.caughtWhileCancelling = sqlEx;
//          }
//          catch (NullPointerException npe)
//          {
//          }
//          finally
//          {
//            if (cancelStmt != null) {
//              try {
//                cancelStmt.close();
//              } catch (SQLException sqlEx) {
//                throw new RuntimeException(sqlEx.toString());
//              }
//            }
//
//            if (cancelConn != null) {
//              try {
//                cancelConn.close();
//              } catch (SQLException sqlEx) {
//                throw new RuntimeException(sqlEx.toString());
//              }
//            }
//
//            StatementImpl.CancelTask.this.toCancel = null;
//            StatementImpl.CancelTask.this.origConnProps = null;
//            StatementImpl.CancelTask.this.origConnURL = null;
//          }
//        }
//      };
//      cancelThread.start();
//    }
//  }
//}
//package jdbc.mysql;
//
//import com.mysql.jdbc.log.LogUtils;
//import com.mysql.jdbc.profiler.ProfilerEvent;
//import com.mysql.jdbc.profiler.ProfilerEventHandler;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.Reader;
//import java.io.StringReader;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Constructor;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.sql.Array;
//import java.sql.Date;
//import java.sql.Ref;
//import java.sql.SQLException;
//import java.sql.SQLWarning;
//import java.sql.Statement;
//import java.sql.Time;
//import java.sql.Timestamp;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//import java.util.StringTokenizer;
//import java.util.TimeZone;
//import java.util.TreeMap;
//
//public class ResultSetImpl
//  implements ResultSetInternalMethods
//{
//  private static final Constructor<?> JDBC_4_RS_4_ARG_CTOR;
//  private static final Constructor<?> JDBC_4_RS_6_ARG_CTOR;
//  private static final Constructor<?> JDBC_4_UPD_RS_6_ARG_CTOR;
//  protected static final double MIN_DIFF_PREC;
//  protected static final double MAX_DIFF_PREC;
//  static int resultCounter;
//  protected String catalog = null;
//
//  protected Map<String, Integer> columnLabelToIndex = null;
//
//  protected Map<String, Integer> columnToIndexCache = null;
//
//  protected boolean[] columnUsed = null;
//  protected volatile MySQLConnection connection;
//  protected long connectionId = 0L;
//
//  protected int currentRow = -1;
//  TimeZone defaultTimeZone;
//  protected boolean doingUpdates = false;
//
//  protected ProfilerEventHandler eventSink = null;
//
//  Calendar fastDateCal = null;
//
//  protected int fetchDirection = 1000;
//
//  protected int fetchSize = 0;
//  protected Field[] fields;
//  protected char firstCharOfQuery;
//  protected Map<String, Integer> fullColumnNameToIndex = null;
//
//  protected Map<String, Integer> columnNameToIndex = null;
//
//  protected boolean hasBuiltIndexMapping = false;
//
//  protected boolean isBinaryEncoded = false;
//
//  protected boolean isClosed = false;
//
//  protected ResultSetInternalMethods nextResultSet = null;
//
//  protected boolean onInsertRow = false;
//  protected StatementImpl owningStatement;
//  protected String pointOfOrigin;
//  protected boolean profileSql = false;
//
//  protected boolean reallyResult = false;
//  protected int resultId;
//  protected int resultSetConcurrency = 0;
//
//  protected int resultSetType = 0;
//  protected RowData rowData;
//  protected String serverInfo = null;
//  PreparedStatement statementUsedForFetchingRows;
//  protected ResultSetRow thisRow = null;
//  protected long updateCount;
//  protected long updateId = -1L;
//
//  private boolean useStrictFloatingPoint = false;
//
//  protected boolean useUsageAdvisor = false;
//
//  protected SQLWarning warningChain = null;
//
//  protected boolean wasNullFlag = false;
//  protected Statement wrapperStatement;
//  protected boolean retainOwningStatement;
//  protected Calendar gmtCalendar = null;
//
//  protected boolean useFastDateParsing = false;
//
//  private boolean padCharsWithSpace = false;
//  private boolean jdbcCompliantTruncationForReads;
//  private boolean useFastIntParsing = true;
//  private boolean useColumnNamesInFindColumn;
//  private ExceptionInterceptor exceptionInterceptor;
//  static final char[] EMPTY_SPACE;
//  private boolean onValidRow = false;
//  private String invalidRowReason = null;
//  protected boolean useLegacyDatetimeCode;
//  private TimeZone serverTimeZoneTz;
//
//  protected static BigInteger convertLongToUlong(long longVal)
//  {
//    byte[] asBytes = new byte[8];
//    asBytes[7] = ((byte)(int)(longVal & 0xFF));
//    asBytes[6] = ((byte)(int)(longVal >>> 8));
//    asBytes[5] = ((byte)(int)(longVal >>> 16));
//    asBytes[4] = ((byte)(int)(longVal >>> 24));
//    asBytes[3] = ((byte)(int)(longVal >>> 32));
//    asBytes[2] = ((byte)(int)(longVal >>> 40));
//    asBytes[1] = ((byte)(int)(longVal >>> 48));
//    asBytes[0] = ((byte)(int)(longVal >>> 56));
//
//    return new BigInteger(1, asBytes);
//  }
//
//  protected static ResultSetImpl getInstance(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt)
//    throws SQLException
//  {
//    if (!Util.isJdbc4()) {
//      return new ResultSetImpl(updateCount, updateID, conn, creatorStmt);
//    }
//
//    return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_4_ARG_CTOR, new Object[] { Long.valueOf(updateCount), Long.valueOf(updateID), conn, creatorStmt }, conn.getExceptionInterceptor());
//  }
//
//  protected static ResultSetImpl getInstance(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt, boolean isUpdatable)
//    throws SQLException
//  {
//    if (!Util.isJdbc4()) {
//      if (!isUpdatable) {
//        return new ResultSetImpl(catalog, fields, tuples, conn, creatorStmt);
//      }
//
//      return new UpdatableResultSet(catalog, fields, tuples, conn, creatorStmt);
//    }
//
//    if (!isUpdatable) {
//      return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
//    }
//
//    return (ResultSetImpl)Util.handleNewInstance(JDBC_4_UPD_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
//  }
//
//  public ResultSetImpl(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt)
//  {
//    this.updateCount = updateCount;
//    this.updateId = updateID;
//    this.reallyResult = false;
//    this.fields = new Field[0];
//
//    this.connection = conn;
//    this.owningStatement = creatorStmt;
//
//    this.retainOwningStatement = false;
//
//    if (this.connection != null) {
//      this.exceptionInterceptor = this.connection.getExceptionInterceptor();
//
//      this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
//
//      this.connectionId = this.connection.getId();
//      this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
//      this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
//
//      this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
//    }
//  }
//
//  public ResultSetImpl(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt)
//    throws SQLException
//  {
//    this.connection = conn;
//
//    this.retainOwningStatement = false;
//
//    if (this.connection != null) {
//      this.exceptionInterceptor = this.connection.getExceptionInterceptor();
//      this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint();
//
//      setDefaultTimeZone(this.connection.getDefaultTimeZone());
//      this.connectionId = this.connection.getId();
//      this.useFastDateParsing = this.connection.getUseFastDateParsing();
//      this.profileSql = this.connection.getProfileSql();
//      this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
//
//      this.jdbcCompliantTruncationForReads = this.connection.getJdbcCompliantTruncationForReads();
//      this.useFastIntParsing = this.connection.getUseFastIntParsing();
//      this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
//      this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
//    }
//
//    this.owningStatement = creatorStmt;
//
//    this.catalog = catalog;
//
//    this.fields = fields;
//    this.rowData = tuples;
//    this.updateCount = this.rowData.size();
//
//    this.reallyResult = true;
//
//    if (this.rowData.size() > 0) {
//      if ((this.updateCount == 1L) && 
//        (this.thisRow == null)) {
//        this.rowData.close();
//        this.updateCount = -1L;
//      }
//    }
//    else {
//      this.thisRow = null;
//    }
//
//    this.rowData.setOwner(this);
//
//    if (this.fields != null) {
//      initializeWithMetadata();
//    }
//    this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
//
//    this.useColumnNamesInFindColumn = this.connection.getUseColumnNamesInFindColumn();
//
//    setRowPositionValidity();
//  }
//
//  public void initializeWithMetadata() throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      this.rowData.setMetadata(this.fields);
//
//      this.columnToIndexCache = new HashMap();
//
//      if ((this.profileSql) || (this.connection.getUseUsageAdvisor())) {
//        this.columnUsed = new boolean[this.fields.length];
//        this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
//        this.resultId = (resultCounter++);
//        this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
//        this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
//      }
//
//      if (this.connection.getGatherPerformanceMetrics()) {
//        this.connection.incrementNumberOfResultSetsCreated();
//
//        Set tableNamesSet = new HashSet();
//
//        for (int i = 0; i < this.fields.length; i++) {
//          Field f = this.fields[i];
//
//          String tableName = f.getOriginalTableName();
//
//          if (tableName == null) {
//            tableName = f.getTableName();
//          }
//
//          if (tableName != null) {
//            if (this.connection.lowerCaseTableNames()) {
//              tableName = tableName.toLowerCase();
//            }
//
//            tableNamesSet.add(tableName);
//          }
//        }
//
//        this.connection.reportNumberOfTablesAccessed(tableNamesSet.size());
//      }
//    }
//  }
//
//  private synchronized void createCalendarIfNeeded() {
//    if (this.fastDateCal == null) {
//      this.fastDateCal = new GregorianCalendar(Locale.US);
//      this.fastDateCal.setTimeZone(getDefaultTimeZone());
//    }
//  }
//
//  public boolean absolute(int row)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      boolean b;
//      boolean b;
//      if (this.rowData.size() == 0) {
//        b = false;
//      } else {
//        if (this.onInsertRow) {
//          this.onInsertRow = false;
//        }
//
//        if (this.doingUpdates) {
//          this.doingUpdates = false;
//        }
//
//        if (this.thisRow != null)
//          this.thisRow.closeOpenStreams();
//        boolean b;
//        if (row == 0) {
//          beforeFirst();
//          b = false;
//        }
//        else
//        {
//          boolean b;
//          if (row == 1) {
//            b = first();
//          }
//          else
//          {
//            boolean b;
//            if (row == -1) {
//              b = last();
//            }
//            else
//            {
//              boolean b;
//              if (row > this.rowData.size()) {
//                afterLast();
//                b = false;
//              }
//              else
//              {
//                boolean b;
//                if (row < 0)
//                {
//                  int newRowPosition = this.rowData.size() + row + 1;
//                  boolean b;
//                  if (newRowPosition <= 0) {
//                    beforeFirst();
//                    b = false;
//                  } else {
//                    b = absolute(newRowPosition);
//                  }
//                } else {
//                  row--;
//                  this.rowData.setCurrentRow(row);
//                  this.thisRow = this.rowData.getAt(row);
//                  b = true;
//                }
//              }
//            }
//          }
//        }
//      }
//      setRowPositionValidity();
//
//      return b;
//    }
//  }
//
//  public void afterLast()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      if (this.onInsertRow) {
//        this.onInsertRow = false;
//      }
//
//      if (this.doingUpdates) {
//        this.doingUpdates = false;
//      }
//
//      if (this.thisRow != null) {
//        this.thisRow.closeOpenStreams();
//      }
//
//      if (this.rowData.size() != 0) {
//        this.rowData.afterLast();
//        this.thisRow = null;
//      }
//
//      setRowPositionValidity();
//    }
//  }
//
//  public void beforeFirst()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      if (this.onInsertRow) {
//        this.onInsertRow = false;
//      }
//
//      if (this.doingUpdates) {
//        this.doingUpdates = false;
//      }
//
//      if (this.rowData.size() == 0) {
//        return;
//      }
//
//      if (this.thisRow != null) {
//        this.thisRow.closeOpenStreams();
//      }
//
//      this.rowData.beforeFirst();
//      this.thisRow = null;
//
//      setRowPositionValidity();
//    }
//  }
//
//  public void buildIndexMapping()
//    throws SQLException
//  {
//    int numFields = this.fields.length;
//    this.columnLabelToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
//    this.fullColumnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
//    this.columnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
//
//    for (int i = numFields - 1; i >= 0; i--) {
//      Integer index = Integer.valueOf(i);
//      String columnName = this.fields[i].getOriginalName();
//      String columnLabel = this.fields[i].getName();
//      String fullColumnName = this.fields[i].getFullName();
//
//      if (columnLabel != null) {
//        this.columnLabelToIndex.put(columnLabel, index);
//      }
//
//      if (fullColumnName != null) {
//        this.fullColumnNameToIndex.put(fullColumnName, index);
//      }
//
//      if (columnName != null) {
//        this.columnNameToIndex.put(columnName, index);
//      }
//
//    }
//
//    this.hasBuiltIndexMapping = true;
//  }
//
//  public void cancelRowUpdates()
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  protected final MySQLConnection checkClosed()
//    throws SQLException
//  {
//    MySQLConnection c = this.connection;
//
//    if (c == null) {
//      throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", getExceptionInterceptor());
//    }
//
//    return c;
//  }
//
//  protected final void checkColumnBounds(int columnIndex)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (columnIndex < 1) {
//        throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
//      }
//
//      if (columnIndex > this.fields.length) {
//        throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
//      }
//
//      if ((this.profileSql) || (this.useUsageAdvisor))
//        this.columnUsed[(columnIndex - 1)] = true;
//    }
//  }
//
//  protected void checkRowPos()
//    throws SQLException
//  {
//    checkClosed();
//
//    if (!this.onValidRow)
//      throw SQLError.createSQLException(this.invalidRowReason, "S1000", getExceptionInterceptor());
//  }
//
//  private void setRowPositionValidity()
//    throws SQLException
//  {
//    if ((!this.rowData.isDynamic()) && (this.rowData.size() == 0)) {
//      this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
//
//      this.onValidRow = false;
//    } else if (this.rowData.isBeforeFirst()) {
//      this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
//
//      this.onValidRow = false;
//    } else if (this.rowData.isAfterLast()) {
//      this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
//
//      this.onValidRow = false;
//    } else {
//      this.onValidRow = true;
//      this.invalidRowReason = null;
//    }
//  }
//
//  public synchronized void clearNextResult()
//  {
//    this.nextResultSet = null;
//  }
//
//  public void clearWarnings()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      this.warningChain = null;
//    }
//  }
//
//  public void close()
//    throws SQLException
//  {
//    realClose(true);
//  }
//
//  private int convertToZeroWithEmptyCheck()
//    throws SQLException
//  {
//    if (this.connection.getEmptyStringsConvertToZero()) {
//      return 0;
//    }
//
//    throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
//  }
//
//  private String convertToZeroLiteralStringWithEmptyCheck()
//    throws SQLException
//  {
//    if (this.connection.getEmptyStringsConvertToZero()) {
//      return "0";
//    }
//
//    throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
//  }
//
//  public ResultSetInternalMethods copy()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      ResultSetInternalMethods rs = getInstance(this.catalog, this.fields, this.rowData, this.connection, this.owningStatement, false);
//
//      return rs;
//    }
//  }
//
//  public void redefineFieldsForDBMD(Field[] f) {
//    this.fields = f;
//
//    for (int i = 0; i < this.fields.length; i++) {
//      this.fields[i].setUseOldNameMetadata(true);
//      this.fields[i].setConnection(this.connection);
//    }
//  }
//
//  public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException
//  {
//    cachedMetaData.fields = this.fields;
//    cachedMetaData.columnNameToIndex = this.columnLabelToIndex;
//    cachedMetaData.fullColumnNameToIndex = this.fullColumnNameToIndex;
//    cachedMetaData.metadata = getMetaData();
//  }
//
//  public void initializeFromCachedMetaData(CachedResultSetMetaData cachedMetaData) {
//    this.fields = cachedMetaData.fields;
//    this.columnLabelToIndex = cachedMetaData.columnNameToIndex;
//    this.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
//    this.hasBuiltIndexMapping = true;
//  }
//
//  public void deleteRow()
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  private String extractStringFromNativeColumn(int columnIndex, int mysqlType)
//    throws SQLException
//  {
//    int columnIndexMinusOne = columnIndex - 1;
//
//    this.wasNullFlag = false;
//
//    if (this.thisRow.isNull(columnIndexMinusOne)) {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    String encoding = this.fields[columnIndexMinusOne].getCharacterSet();
//
//    return this.thisRow.getString(columnIndex - 1, encoding, this.connection);
//  }
//
//  protected Date fastDateCreate(Calendar cal, int year, int month, int day) throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.useLegacyDatetimeCode) {
//        return TimeUtil.fastDateCreate(year, month, day, cal);
//      }
//
//      if (cal == null) {
//        createCalendarIfNeeded();
//        cal = this.fastDateCal;
//      }
//
//      boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
//
//      return TimeUtil.fastDateCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : cal, cal, year, month, day);
//    }
//  }
//
//  protected Time fastTimeCreate(Calendar cal, int hour, int minute, int second)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (!this.useLegacyDatetimeCode) {
//        return TimeUtil.fastTimeCreate(hour, minute, second, cal, getExceptionInterceptor());
//      }
//
//      if (cal == null) {
//        createCalendarIfNeeded();
//        cal = this.fastDateCal;
//      }
//
//      return TimeUtil.fastTimeCreate(cal, hour, minute, second, getExceptionInterceptor());
//    }
//  }
//
//  protected Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (!this.useLegacyDatetimeCode) {
//        return TimeUtil.fastTimestampCreate(cal.getTimeZone(), year, month, day, hour, minute, seconds, secondsPart);
//      }
//
//      if (cal == null) {
//        createCalendarIfNeeded();
//        cal = this.fastDateCal;
//      }
//
//      boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
//
//      return TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : null, cal, year, month, day, hour, minute, seconds, secondsPart);
//    }
//  }
//
//  public int findColumn(String columnName)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      if (!this.hasBuiltIndexMapping) {
//        buildIndexMapping();
//      }
//
//      Integer index = (Integer)this.columnToIndexCache.get(columnName);
//
//      if (index != null) {
//        return index.intValue() + 1;
//      }
//
//      index = (Integer)this.columnLabelToIndex.get(columnName);
//
//      if ((index == null) && (this.useColumnNamesInFindColumn)) {
//        index = (Integer)this.columnNameToIndex.get(columnName);
//      }
//
//      if (index == null) {
//        index = (Integer)this.fullColumnNameToIndex.get(columnName);
//      }
//
//      if (index != null) {
//        this.columnToIndexCache.put(columnName, index);
//
//        return index.intValue() + 1;
//      }
//
//      for (int i = 0; i < this.fields.length; i++) {
//        if (this.fields[i].getName().equalsIgnoreCase(columnName))
//          return i + 1;
//        if (this.fields[i].getFullName().equalsIgnoreCase(columnName))
//        {
//          return i + 1;
//        }
//      }
//
//      throw SQLError.createSQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022", getExceptionInterceptor());
//    }
//  }
//
//  public boolean first()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      boolean b = true;
//
//      if (this.rowData.isEmpty()) {
//        b = false;
//      }
//      else {
//        if (this.onInsertRow) {
//          this.onInsertRow = false;
//        }
//
//        if (this.doingUpdates) {
//          this.doingUpdates = false;
//        }
//
//        this.rowData.beforeFirst();
//        this.thisRow = this.rowData.next();
//      }
//
//      setRowPositionValidity();
//
//      return b;
//    }
//  }
//
//  public Array getArray(int i)
//    throws SQLException
//  {
//    checkColumnBounds(i);
//
//    throw SQLError.notImplemented();
//  }
//
//  public Array getArray(String colName)
//    throws SQLException
//  {
//    return getArray(findColumn(colName));
//  }
//
//  public InputStream getAsciiStream(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//
//    if (!this.isBinaryEncoded) {
//      return getBinaryStream(columnIndex);
//    }
//
//    return getNativeBinaryStream(columnIndex);
//  }
//
//  public InputStream getAsciiStream(String columnName)
//    throws SQLException
//  {
//    return getAsciiStream(findColumn(columnName));
//  }
//
//  public BigDecimal getBigDecimal(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      String stringVal = getString(columnIndex);
//
//      if (stringVal != null) {
//        if (stringVal.length() == 0)
//        {
//          BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
//
//          return val;
//        }
//        try
//        {
//          return new BigDecimal(stringVal);
//        }
//        catch (NumberFormatException ex)
//        {
//          throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//        }
//
//      }
//
//      return null;
//    }
//
//    return getNativeBigDecimal(columnIndex);
//  }
//
//  /** @deprecated */
//  public BigDecimal getBigDecimal(int columnIndex, int scale)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      String stringVal = getString(columnIndex);
//
//      if (stringVal != null) {
//        if (stringVal.length() == 0) {
//          BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
//          try
//          {
//            return val.setScale(scale);
//          } catch (ArithmeticException ex) {
//            try {
//              return val.setScale(scale, 4);
//            }
//            catch (ArithmeticException arEx) {
//              throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//            }
//
//          }
//
//        }
//
//        try
//        {
//          val = new BigDecimal(stringVal);
//        }
//        catch (NumberFormatException ex)
//        {
//          BigDecimal val;
//          if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
//            long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
//
//            val = new BigDecimal(valueAsLong);
//          } else {
//            throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
//          }
//
//        }
//
//        try
//        {
//          return val.setScale(scale);
//        }
//        catch (ArithmeticException ex)
//        {
//          try
//          {
//            BigDecimal val;
//            return val.setScale(scale, 4);
//          } catch (ArithmeticException arithEx) {
//            throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
//          }
//
//        }
//
//      }
//
//      return null;
//    }
//
//    return getNativeBigDecimal(columnIndex, scale);
//  }
//
//  public BigDecimal getBigDecimal(String columnName)
//    throws SQLException
//  {
//    return getBigDecimal(findColumn(columnName));
//  }
//
//  /** @deprecated */
//  public BigDecimal getBigDecimal(String columnName, int scale)
//    throws SQLException
//  {
//    return getBigDecimal(findColumn(columnName), scale);
//  }
//
//  private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale)
//    throws SQLException
//  {
//    if (stringVal != null) {
//      if (stringVal.length() == 0) {
//        BigDecimal bdVal = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
//        try
//        {
//          return bdVal.setScale(scale);
//        } catch (ArithmeticException ex) {
//          try {
//            return bdVal.setScale(scale, 4);
//          } catch (ArithmeticException arEx) {
//            throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
//          }
//
//        }
//
//      }
//
//      try
//      {
//        return new BigDecimal(stringVal).setScale(scale);
//      } catch (ArithmeticException ex) {
//        try {
//          return new BigDecimal(stringVal).setScale(scale, 4);
//        }
//        catch (ArithmeticException arEx) {
//          throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
//        }
//
//      }
//      catch (NumberFormatException ex)
//      {
//        if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
//          long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
//          try
//          {
//            return new BigDecimal(valueAsLong).setScale(scale);
//          } catch (ArithmeticException arEx1) {
//            try {
//              return new BigDecimal(valueAsLong).setScale(scale, 4);
//            }
//            catch (ArithmeticException arEx2) {
//              throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
//            }
//
//          }
//
//        }
//
//        if ((this.fields[(columnIndex - 1)].getMysqlType() == 1) && (this.connection.getTinyInt1isBit()) && (this.fields[(columnIndex - 1)].getLength() == 1L))
//        {
//          return new BigDecimal(stringVal.equalsIgnoreCase("true") ? 1 : 0).setScale(scale);
//        }
//
//        throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
//      }
//
//    }
//
//    return null;
//  }
//
//  public InputStream getBinaryStream(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//
//    if (!this.isBinaryEncoded) {
//      checkColumnBounds(columnIndex);
//
//      int columnIndexMinusOne = columnIndex - 1;
//
//      if (this.thisRow.isNull(columnIndexMinusOne)) {
//        this.wasNullFlag = true;
//
//        return null;
//      }
//
//      this.wasNullFlag = false;
//
//      return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
//    }
//
//    return getNativeBinaryStream(columnIndex);
//  }
//
//  public InputStream getBinaryStream(String columnName)
//    throws SQLException
//  {
//    return getBinaryStream(findColumn(columnName));
//  }
//
//  public java.sql.Blob getBlob(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      checkRowPos();
//
//      checkColumnBounds(columnIndex);
//
//      int columnIndexMinusOne = columnIndex - 1;
//
//      if (this.thisRow.isNull(columnIndexMinusOne))
//        this.wasNullFlag = true;
//      else {
//        this.wasNullFlag = false;
//      }
//
//      if (this.wasNullFlag) {
//        return null;
//      }
//
//      if (!this.connection.getEmulateLocators()) {
//        return new Blob(this.thisRow.getColumnValue(columnIndexMinusOne), getExceptionInterceptor());
//      }
//
//      return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
//    }
//
//    return getNativeBlob(columnIndex);
//  }
//
//  public java.sql.Blob getBlob(String colName)
//    throws SQLException
//  {
//    return getBlob(findColumn(colName));
//  }
//
//  public boolean getBoolean(int columnIndex)
//    throws SQLException
//  {
//    checkColumnBounds(columnIndex);
//
//    int columnIndexMinusOne = columnIndex - 1;
//
//    Field field = this.fields[columnIndexMinusOne];
//
//    if (field.getMysqlType() == 16) {
//      return byteArrayToBoolean(columnIndexMinusOne);
//    }
//
//    this.wasNullFlag = false;
//
//    int sqlType = field.getSQLType();
//    long boolVal;
//    switch (sqlType) {
//    case 16:
//      if (field.getMysqlType() == -1) {
//        String stringVal = getString(columnIndex);
//
//        return getBooleanFromString(stringVal);
//      }
//
//      boolVal = getLong(columnIndex, false);
//
//      return (boolVal == -1L) || (boolVal > 0L);
//    case -7:
//    case -6:
//    case -5:
//    case 2:
//    case 3:
//    case 4:
//    case 5:
//    case 6:
//    case 7:
//    case 8:
//      boolVal = getLong(columnIndex, false);
//
//      return (boolVal == -1L) || (boolVal > 0L);
//    case -4:
//    case -3:
//    case -2:
//    case -1:
//    case 0:
//    case 1:
//    case 9:
//    case 10:
//    case 11:
//    case 12:
//    case 13:
//    case 14:
//    case 15: } if (this.connection.getPedantic())
//    {
//      switch (sqlType) {
//      case -4:
//      case -3:
//      case -2:
//      case 70:
//      case 91:
//      case 92:
//      case 93:
//      case 2000:
//      case 2002:
//      case 2003:
//      case 2004:
//      case 2005:
//      case 2006:
//        throw SQLError.createSQLException("Required type conversion not allowed", "22018", getExceptionInterceptor());
//      }
//
//    }
//
//    if ((sqlType == -2) || (sqlType == -3) || (sqlType == -4) || (sqlType == 2004))
//    {
//      return byteArrayToBoolean(columnIndexMinusOne);
//    }
//
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getBoolean()", columnIndex, this.thisRow.getColumnValue(columnIndexMinusOne), this.fields[columnIndex], new int[] { 16, 5, 1, 2, 3, 8, 4 });
//    }
//
//    String stringVal = getString(columnIndex);
//
//    return getBooleanFromString(stringVal);
//  }
//
//  private boolean byteArrayToBoolean(int columnIndexMinusOne) throws SQLException
//  {
//    Object value = this.thisRow.getColumnValue(columnIndexMinusOne);
//
//    if (value == null) {
//      this.wasNullFlag = true;
//
//      return false;
//    }
//
//    this.wasNullFlag = false;
//
//    if (((byte[])value).length == 0) {
//      return false;
//    }
//
//    byte boolVal = ((byte[])(byte[])value)[0];
//
//    if (boolVal == 49)
//      return true;
//    if (boolVal == 48) {
//      return false;
//    }
//
//    return (boolVal == -1) || (boolVal > 0);
//  }
//
//  public boolean getBoolean(String columnName)
//    throws SQLException
//  {
//    return getBoolean(findColumn(columnName));
//  }
//
//  private final boolean getBooleanFromString(String stringVal) throws SQLException
//  {
//    if ((stringVal != null) && (stringVal.length() > 0)) {
//      int c = Character.toLowerCase(stringVal.charAt(0));
//
//      return (c == 116) || (c == 121) || (c == 49) || (stringVal.equals("-1"));
//    }
//
//    return false;
//  }
//
//  public byte getByte(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      String stringVal = getString(columnIndex);
//
//      if ((this.wasNullFlag) || (stringVal == null)) {
//        return 0;
//      }
//
//      return getByteFromString(stringVal, columnIndex);
//    }
//
//    return getNativeByte(columnIndex);
//  }
//
//  public byte getByte(String columnName)
//    throws SQLException
//  {
//    return getByte(findColumn(columnName));
//  }
//
//  private final byte getByteFromString(String stringVal, int columnIndex)
//    throws SQLException
//  {
//    if ((stringVal != null) && (stringVal.length() == 0)) {
//      return (byte)convertToZeroWithEmptyCheck();
//    }
//
//    if (stringVal == null) {
//      return 0;
//    }
//
//    stringVal = stringVal.trim();
//    try
//    {
//      int decimalIndex = stringVal.indexOf(".");
//
//      if (decimalIndex != -1) {
//        double valueAsDouble = Double.parseDouble(stringVal);
//
//        if ((this.jdbcCompliantTruncationForReads) && (
//          (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
//        {
//          throwRangeException(stringVal, columnIndex, -6);
//        }
//
//        return (byte)(int)valueAsDouble;
//      }
//
//      long valueAsLong = Long.parseLong(stringVal);
//
//      if ((this.jdbcCompliantTruncationForReads) && (
//        (valueAsLong < -128L) || (valueAsLong > 127L)))
//      {
//        throwRangeException(String.valueOf(valueAsLong), columnIndex, -6);
//      }
//
//      return (byte)(int)valueAsLong; } catch (NumberFormatException NFE) {
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Value____173") + stringVal + Messages.getString("ResultSet.___is_out_of_range_[-127,127]_174"), "S1009", getExceptionInterceptor());
//  }
//
//  public byte[] getBytes(int columnIndex)
//    throws SQLException
//  {
//    return getBytes(columnIndex, false);
//  }
//
//  protected byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      checkRowPos();
//
//      checkColumnBounds(columnIndex);
//
//      int columnIndexMinusOne = columnIndex - 1;
//
//      if (this.thisRow.isNull(columnIndexMinusOne))
//        this.wasNullFlag = true;
//      else {
//        this.wasNullFlag = false;
//      }
//
//      if (this.wasNullFlag) {
//        return null;
//      }
//
//      return this.thisRow.getColumnValue(columnIndexMinusOne);
//    }
//
//    return getNativeBytes(columnIndex, noConversion);
//  }
//
//  public byte[] getBytes(String columnName)
//    throws SQLException
//  {
//    return getBytes(findColumn(columnName));
//  }
//
//  private final byte[] getBytesFromString(String stringVal) throws SQLException
//  {
//    if (stringVal != null) {
//      return StringUtils.getBytes(stringVal, this.connection.getEncoding(), this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection, getExceptionInterceptor());
//    }
//
//    return null;
//  }
//
//  public int getBytesSize() throws SQLException {
//    RowData localRowData = this.rowData;
//
//    checkClosed();
//
//    if ((localRowData instanceof RowDataStatic)) {
//      int bytesSize = 0;
//
//      int numRows = localRowData.size();
//
//      for (int i = 0; i < numRows; i++) {
//        bytesSize += localRowData.getAt(i).getBytesSize();
//      }
//
//      return bytesSize;
//    }
//
//    return -1;
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
//  public Reader getCharacterStream(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      checkColumnBounds(columnIndex);
//
//      int columnIndexMinusOne = columnIndex - 1;
//
//      if (this.thisRow.isNull(columnIndexMinusOne)) {
//        this.wasNullFlag = true;
//
//        return null;
//      }
//
//      this.wasNullFlag = false;
//
//      return this.thisRow.getReader(columnIndexMinusOne);
//    }
//
//    return getNativeCharacterStream(columnIndex);
//  }
//
//  public Reader getCharacterStream(String columnName)
//    throws SQLException
//  {
//    return getCharacterStream(findColumn(columnName));
//  }
//
//  private final Reader getCharacterStreamFromString(String stringVal) throws SQLException
//  {
//    if (stringVal != null) {
//      return new StringReader(stringVal);
//    }
//
//    return null;
//  }
//
//  public java.sql.Clob getClob(int i)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      String asString = getStringForClob(i);
//
//      if (asString == null) {
//        return null;
//      }
//
//      return new Clob(asString, getExceptionInterceptor());
//    }
//
//    return getNativeClob(i);
//  }
//
//  public java.sql.Clob getClob(String colName)
//    throws SQLException
//  {
//    return getClob(findColumn(colName));
//  }
//
//  private final java.sql.Clob getClobFromString(String stringVal) throws SQLException {
//    return new Clob(stringVal, getExceptionInterceptor());
//  }
//
//  public int getConcurrency()
//    throws SQLException
//  {
//    return 1007;
//  }
//
//  public String getCursorName()
//    throws SQLException
//  {
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00", getExceptionInterceptor());
//  }
//
//  public Date getDate(int columnIndex)
//    throws SQLException
//  {
//    return getDate(columnIndex, null);
//  }
//
//  public Date getDate(int columnIndex, Calendar cal)
//    throws SQLException
//  {
//    if (this.isBinaryEncoded) {
//      return getNativeDate(columnIndex, cal);
//    }
//
//    if (!this.useFastDateParsing) {
//      String stringVal = getStringInternal(columnIndex, false);
//
//      if (stringVal == null) {
//        return null;
//      }
//
//      return getDateFromString(stringVal, columnIndex, cal);
//    }
//
//    checkColumnBounds(columnIndex);
//
//    int columnIndexMinusOne = columnIndex - 1;
//    Date tmpDate = this.thisRow.getDateFast(columnIndexMinusOne, this.connection, this, cal);
//    if ((this.thisRow.isNull(columnIndexMinusOne)) || (tmpDate == null))
//    {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    return tmpDate;
//  }
//
//  public Date getDate(String columnName)
//    throws SQLException
//  {
//    return getDate(findColumn(columnName));
//  }
//
//  public Date getDate(String columnName, Calendar cal)
//    throws SQLException
//  {
//    return getDate(findColumn(columnName), cal);
//  }
//
//  private final Date getDateFromString(String stringVal, int columnIndex, Calendar targetCalendar) throws SQLException
//  {
//    int year = 0;
//    int month = 0;
//    int day = 0;
//    try
//    {
//      this.wasNullFlag = false;
//
//      if (stringVal == null) {
//        this.wasNullFlag = true;
//
//        return null;
//      }
//
//      stringVal = stringVal.trim();
//
//      if ((stringVal.equals("0")) || (stringVal.equals("0000-00-00")) || (stringVal.equals("0000-00-00 00:00:00")) || (stringVal.equals("00000000000000")) || (stringVal.equals("0")))
//      {
//        if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
//        {
//          this.wasNullFlag = true;
//
//          return null;
//        }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
//        {
//          throw SQLError.createSQLException("Value '" + stringVal + "' can not be represented as java.sql.Date", "S1009", getExceptionInterceptor());
//        }
//
//        return fastDateCreate(targetCalendar, 1, 1, 1);
//      }
//      if (this.fields[(columnIndex - 1)].getMysqlType() == 7)
//      {
//        switch (stringVal.length()) {
//        case 19:
//        case 21:
//          year = Integer.parseInt(stringVal.substring(0, 4));
//          month = Integer.parseInt(stringVal.substring(5, 7));
//          day = Integer.parseInt(stringVal.substring(8, 10));
//
//          return fastDateCreate(targetCalendar, year, month, day);
//        case 8:
//        case 14:
//          year = Integer.parseInt(stringVal.substring(0, 4));
//          month = Integer.parseInt(stringVal.substring(4, 6));
//          day = Integer.parseInt(stringVal.substring(6, 8));
//
//          return fastDateCreate(targetCalendar, year, month, day);
//        case 6:
//        case 10:
//        case 12:
//          year = Integer.parseInt(stringVal.substring(0, 2));
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          month = Integer.parseInt(stringVal.substring(2, 4));
//          day = Integer.parseInt(stringVal.substring(4, 6));
//
//          return fastDateCreate(targetCalendar, year + 1900, month, day);
//        case 4:
//          year = Integer.parseInt(stringVal.substring(0, 4));
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          month = Integer.parseInt(stringVal.substring(2, 4));
//
//          return fastDateCreate(targetCalendar, year + 1900, month, 1);
//        case 2:
//          year = Integer.parseInt(stringVal.substring(0, 2));
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          return fastDateCreate(targetCalendar, year + 1900, 1, 1);
//        case 3:
//        case 5:
//        case 7:
//        case 9:
//        case 11:
//        case 13:
//        case 15:
//        case 16:
//        case 17:
//        case 18:
//        case 20: } throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//      }
//
//      if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
//      {
//        if ((stringVal.length() == 2) || (stringVal.length() == 1)) {
//          year = Integer.parseInt(stringVal);
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          year += 1900;
//        } else {
//          year = Integer.parseInt(stringVal.substring(0, 4));
//        }
//
//        return fastDateCreate(targetCalendar, year, 1, 1);
//      }if (this.fields[(columnIndex - 1)].getMysqlType() == 11) {
//        return fastDateCreate(targetCalendar, 1970, 1, 1);
//      }
//      if (stringVal.length() < 10) {
//        if (stringVal.length() == 8) {
//          return fastDateCreate(targetCalendar, 1970, 1, 1);
//        }
//
//        throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//      }
//
//      if (stringVal.length() != 18) {
//        year = Integer.parseInt(stringVal.substring(0, 4));
//        month = Integer.parseInt(stringVal.substring(5, 7));
//        day = Integer.parseInt(stringVal.substring(8, 10));
//      }
//      else {
//        StringTokenizer st = new StringTokenizer(stringVal, "- ");
//
//        year = Integer.parseInt(st.nextToken());
//        month = Integer.parseInt(st.nextToken());
//        day = Integer.parseInt(st.nextToken());
//      }
//
//      return fastDateCreate(targetCalendar, year, month, day);
//    } catch (SQLException sqlEx) {
//      throw sqlEx;
//    } catch (Exception e) {
//      SQLException sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//
//      sqlEx.initCause(e);
//
//      throw sqlEx;
//    }
//  }
//
//  private TimeZone getDefaultTimeZone() {
//    if ((!this.useLegacyDatetimeCode) && (this.connection != null)) {
//      return this.serverTimeZoneTz;
//    }
//
//    return this.connection.getDefaultTimeZone();
//  }
//
//  public double getDouble(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      return getDoubleInternal(columnIndex);
//    }
//
//    return getNativeDouble(columnIndex);
//  }
//
//  public double getDouble(String columnName)
//    throws SQLException
//  {
//    return getDouble(findColumn(columnName));
//  }
//
//  private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException
//  {
//    return getDoubleInternal(stringVal, columnIndex);
//  }
//
//  protected double getDoubleInternal(int colIndex)
//    throws SQLException
//  {
//    return getDoubleInternal(getString(colIndex), colIndex); } 
//  protected double getDoubleInternal(String stringVal, int colIndex) throws SQLException { // Byte code:
//    //   0: aload_1
//    //   1: ifnonnull +5 -> 6
//    //   4: dconst_0
//    //   5: dreturn
//    //   6: aload_1
//    //   7: invokevirtual 199	java/lang/String:length	()I
//    //   10: ifne +9 -> 19
//    //   13: aload_0
//    //   14: invokespecial 247	com/mysql/jdbc/ResultSetImpl:convertToZeroWithEmptyCheck	()I
//    //   17: i2d
//    //   18: dreturn
//    //   19: aload_1
//    //   20: invokestatic 251	java/lang/Double:parseDouble	(Ljava/lang/String;)D
//    //   23: dstore_3
//    //   24: aload_0
//    //   25: getfield 46	com/mysql/jdbc/ResultSetImpl:useStrictFloatingPoint	Z
//    //   28: ifeq +120 -> 148
//    //   31: dload_3
//    //   32: ldc2_w 316
//    //   35: dcmpl
//    //   36: ifne +10 -> 46
//    //   39: ldc2_w 318
//    //   42: dstore_3
//    //   43: goto +105 -> 148
//    //   46: dload_3
//    //   47: ldc2_w 320
//    //   50: dcmpl
//    //   51: ifne +10 -> 61
//    //   54: ldc2_w 322
//    //   57: dstore_3
//    //   58: goto +90 -> 148
//    //   61: dload_3
//    //   62: ldc2_w 324
//    //   65: dcmpl
//    //   66: ifne +10 -> 76
//    //   69: ldc2_w 326
//    //   72: dstore_3
//    //   73: goto +75 -> 148
//    //   76: dload_3
//    //   77: ldc2_w 328
//    //   80: dcmpl
//    //   81: ifne +10 -> 91
//    //   84: ldc2_w 330
//    //   87: dstore_3
//    //   88: goto +60 -> 148
//    //   91: dload_3
//    //   92: ldc2_w 332
//    //   95: dcmpl
//    //   96: ifne +10 -> 106
//    //   99: ldc2_w 330
//    //   102: dstore_3
//    //   103: goto +45 -> 148
//    //   106: dload_3
//    //   107: ldc2_w 334
//    //   110: dcmpl
//    //   111: ifne +10 -> 121
//    //   114: ldc2_w 336
//    //   117: dstore_3
//    //   118: goto +30 -> 148
//    //   121: dload_3
//    //   122: ldc2_w 338
//    //   125: dcmpl
//    //   126: ifne +10 -> 136
//    //   129: ldc2_w 340
//    //   132: dstore_3
//    //   133: goto +15 -> 148
//    //   136: dload_3
//    //   137: ldc2_w 342
//    //   140: dcmpl
//    //   141: ifne +7 -> 148
//    //   144: ldc2_w 336
//    //   147: dstore_3
//    //   148: dload_3
//    //   149: dreturn
//    //   150: astore_3
//    //   151: aload_0
//    //   152: getfield 58	com/mysql/jdbc/ResultSetImpl:fields	[Lcom/mysql/jdbc/Field;
//    //   155: iload_2
//    //   156: iconst_1
//    //   157: isub
//    //   158: aaload
//    //   159: invokevirtual 209	com/mysql/jdbc/Field:getMysqlType	()I
//    //   162: bipush 16
//    //   164: if_icmpne +14 -> 178
//    //   167: aload_0
//    //   168: iload_2
//    //   169: invokespecial 210	com/mysql/jdbc/ResultSetImpl:getNumericRepresentationOfSQLBitType	(I)J
//    //   172: lstore 4
//    //   174: lload 4
//    //   176: l2d
//    //   177: dreturn
//    //   178: ldc_w 344
//    //   181: iconst_2
//    //   182: anewarray 9	java/lang/Object
//    //   185: dup
//    //   186: iconst_0
//    //   187: aload_1
//    //   188: aastore
//    //   189: dup
//    //   190: iconst_1
//    //   191: iload_2
//    //   192: invokestatic 128	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
//    //   195: aastore
//    //   196: invokestatic 141	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
//    //   199: ldc 142
//    //   201: aload_0
//    //   202: invokevirtual 138	com/mysql/jdbc/ResultSetImpl:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
//    //   205: invokestatic 139	com/mysql/jdbc/SQLError:createSQLException	(Ljava/lang/String;Ljava/lang/String;Lcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
//    //   208: athrow
//    //
//    // Exception table:
//    //   from	to	target	type
//    //   0	5	150	java/lang/NumberFormatException
//    //   6	18	150	java/lang/NumberFormatException
//    //   19	149	150	java/lang/NumberFormatException } 
//  public int getFetchDirection() throws SQLException { synchronized (checkClosed().getConnectionMutex()) {
//      return this.fetchDirection;
//    }
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
//  public char getFirstCharOfQuery()
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        return this.firstCharOfQuery;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public float getFloat(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      String val = null;
//
//      val = getString(columnIndex);
//
//      return getFloatFromString(val, columnIndex);
//    }
//
//    return getNativeFloat(columnIndex);
//  }
//
//  public float getFloat(String columnName)
//    throws SQLException
//  {
//    return getFloat(findColumn(columnName));
//  }
//
//  private final float getFloatFromString(String val, int columnIndex) throws SQLException
//  {
//    try {
//      if (val != null) {
//        if (val.length() == 0) {
//          return convertToZeroWithEmptyCheck();
//        }
//
//        float f = Float.parseFloat(val);
//
//        if ((this.jdbcCompliantTruncationForReads) && (
//          (f == 1.4E-45F) || (f == 3.4028235E+38F))) {
//          double valAsDouble = Double.parseDouble(val);
//
//          if ((valAsDouble < 1.401298464324817E-045D - MIN_DIFF_PREC) || (valAsDouble > 3.402823466385289E+038D - MAX_DIFF_PREC))
//          {
//            throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
//          }
//
//        }
//
//        return f;
//      }
//
//      return 0.0F;
//    } catch (NumberFormatException nfe) {
//      try {
//        Double valueAsDouble = new Double(val);
//        float valueAsFloat = valueAsDouble.floatValue();
//
//        if (this.jdbcCompliantTruncationForReads)
//        {
//          if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == (1.0F / -1.0F))) || (valueAsFloat == (1.0F / 1.0F)))
//          {
//            throwRangeException(valueAsDouble.toString(), columnIndex, 6);
//          }
//
//        }
//
//        return valueAsFloat;
//      }
//      catch (NumberFormatException newNfe) {
//      }
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString("ResultSet.___in_column__201") + columnIndex, "S1009", getExceptionInterceptor());
//  }
//
//  public int getInt(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//
//    if (!this.isBinaryEncoded) {
//      int columnIndexMinusOne = columnIndex - 1;
//      if (this.useFastIntParsing) {
//        checkColumnBounds(columnIndex);
//
//        if (this.thisRow.isNull(columnIndexMinusOne))
//          this.wasNullFlag = true;
//        else {
//          this.wasNullFlag = false;
//        }
//
//        if (this.wasNullFlag) {
//          return 0;
//        }
//
//        if (this.thisRow.length(columnIndexMinusOne) == 0L) {
//          return convertToZeroWithEmptyCheck();
//        }
//
//        boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
//
//        if (!needsFullParse) {
//          try {
//            return getIntWithOverflowCheck(columnIndexMinusOne);
//          }
//          catch (NumberFormatException nfe) {
//            try {
//              return parseIntAsDouble(columnIndex, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
//            }
//            catch (NumberFormatException newNfe)
//            {
//              if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
//                long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
//
//                if ((this.connection.getJdbcCompliantTruncationForReads()) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
//                {
//                  throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
//                }
//
//                return (int)valueAsLong;
//              }
//
//              throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009", getExceptionInterceptor());
//            }
//
//          }
//
//        }
//
//      }
//
//      String val = null;
//      try
//      {
//        val = getString(columnIndex);
//
//        if (val != null) {
//          if (val.length() == 0) {
//            return convertToZeroWithEmptyCheck();
//          }
//
//          if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
//          {
//            int intVal = Integer.parseInt(val);
//
//            checkForIntegerTruncation(columnIndexMinusOne, null, intVal);
//
//            return intVal;
//          }
//
//          int intVal = parseIntAsDouble(columnIndex, val);
//
//          checkForIntegerTruncation(columnIndex, null, intVal);
//
//          return intVal;
//        }
//
//        return 0;
//      } catch (NumberFormatException nfe) {
//        try {
//          return parseIntAsDouble(columnIndex, val);
//        }
//        catch (NumberFormatException newNfe)
//        {
//          if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
//            long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
//
//            if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
//            {
//              throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
//            }
//
//            return (int)valueAsLong;
//          }
//
//          throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + val + "'", "S1009", getExceptionInterceptor());
//        }
//
//      }
//
//    }
//
//    return getNativeInt(columnIndex);
//  }
//
//  public int getInt(String columnName)
//    throws SQLException
//  {
//    return getInt(findColumn(columnName));
//  }
//
//  private final int getIntFromString(String val, int columnIndex) throws SQLException
//  {
//    try {
//      if (val != null)
//      {
//        if (val.length() == 0) {
//          return convertToZeroWithEmptyCheck();
//        }
//
//        if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
//        {
//          val = val.trim();
//
//          int valueAsInt = Integer.parseInt(val);
//
//          if ((this.jdbcCompliantTruncationForReads) && (
//            (valueAsInt == -2147483648) || (valueAsInt == 2147483647)))
//          {
//            long valueAsLong = Long.parseLong(val);
//
//            if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
//            {
//              throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
//            }
//
//          }
//
//          return valueAsInt;
//        }
//
//        double valueAsDouble = Double.parseDouble(val);
//
//        if ((this.jdbcCompliantTruncationForReads) && (
//          (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
//        {
//          throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
//        }
//
//        return (int)valueAsDouble;
//      }
//
//      return 0;
//    } catch (NumberFormatException nfe) {
//      try {
//        double valueAsDouble = Double.parseDouble(val);
//
//        if ((this.jdbcCompliantTruncationForReads) && (
//          (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
//        {
//          throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
//        }
//
//        return (int)valueAsDouble;
//      }
//      catch (NumberFormatException newNfe) {
//      }
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString("ResultSet.___in_column__207") + columnIndex, "S1009", getExceptionInterceptor());
//  }
//
//  public long getLong(int columnIndex)
//    throws SQLException
//  {
//    return getLong(columnIndex, true);
//  }
//
//  private long getLong(int columnIndex, boolean overflowCheck) throws SQLException {
//    if (!this.isBinaryEncoded) {
//      checkRowPos();
//
//      int columnIndexMinusOne = columnIndex - 1;
//
//      if (this.useFastIntParsing)
//      {
//        checkColumnBounds(columnIndex);
//
//        if (this.thisRow.isNull(columnIndexMinusOne))
//          this.wasNullFlag = true;
//        else {
//          this.wasNullFlag = false;
//        }
//
//        if (this.wasNullFlag) {
//          return 0L;
//        }
//
//        if (this.thisRow.length(columnIndexMinusOne) == 0L) {
//          return convertToZeroWithEmptyCheck();
//        }
//
//        boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
//
//        if (!needsFullParse) {
//          try {
//            return getLongWithOverflowCheck(columnIndexMinusOne, overflowCheck);
//          }
//          catch (NumberFormatException nfe) {
//            try {
//              return parseLongAsDouble(columnIndexMinusOne, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
//            }
//            catch (NumberFormatException newNfe)
//            {
//              if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
//                return getNumericRepresentationOfSQLBitType(columnIndex);
//              }
//
//              throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009", getExceptionInterceptor());
//            }
//
//          }
//
//        }
//
//      }
//
//      String val = null;
//      try
//      {
//        val = getString(columnIndex);
//
//        if (val != null) {
//          if (val.length() == 0) {
//            return convertToZeroWithEmptyCheck();
//          }
//
//          if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
//            return parseLongWithOverflowCheck(columnIndexMinusOne, null, val, overflowCheck);
//          }
//
//          return parseLongAsDouble(columnIndexMinusOne, val);
//        }
//
//        return 0L;
//      } catch (NumberFormatException nfe) {
//        try {
//          return parseLongAsDouble(columnIndexMinusOne, val);
//        }
//        catch (NumberFormatException newNfe)
//        {
//          throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + val + "'", "S1009", getExceptionInterceptor());
//        }
//
//      }
//
//    }
//
//    return getNativeLong(columnIndex, overflowCheck, true);
//  }
//
//  public long getLong(String columnName)
//    throws SQLException
//  {
//    return getLong(findColumn(columnName));
//  }
//
//  private final long getLongFromString(String val, int columnIndexZeroBased) throws SQLException
//  {
//    try {
//      if (val != null)
//      {
//        if (val.length() == 0) {
//          return convertToZeroWithEmptyCheck();
//        }
//
//        if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
//          return parseLongWithOverflowCheck(columnIndexZeroBased, null, val, true);
//        }
//
//        return parseLongAsDouble(columnIndexZeroBased, val);
//      }
//
//      return 0L;
//    }
//    catch (NumberFormatException nfe) {
//      try {
//        return parseLongAsDouble(columnIndexZeroBased, val);
//      }
//      catch (NumberFormatException newNfe) {
//      }
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString("ResultSet.___in_column__212") + (columnIndexZeroBased + 1), "S1009", getExceptionInterceptor());
//  }
//
//  public java.sql.ResultSetMetaData getMetaData()
//    throws SQLException
//  {
//    checkClosed();
//
//    return new ResultSetMetaData(this.fields, this.connection.getUseOldAliasMetadataBehavior(), this.connection.getYearIsDateType(), getExceptionInterceptor());
//  }
//
//  protected Array getNativeArray(int i)
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  protected InputStream getNativeAsciiStream(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//
//    return getNativeBinaryStream(columnIndex);
//  }
//
//  protected BigDecimal getNativeBigDecimal(int columnIndex)
//    throws SQLException
//  {
//    checkColumnBounds(columnIndex);
//
//    int scale = this.fields[(columnIndex - 1)].getDecimals();
//
//    return getNativeBigDecimal(columnIndex, scale);
//  }
//
//  protected BigDecimal getNativeBigDecimal(int columnIndex, int scale)
//    throws SQLException
//  {
//    checkColumnBounds(columnIndex);
//
//    String stringVal = null;
//
//    Field f = this.fields[(columnIndex - 1)];
//
//    Object value = this.thisRow.getColumnValue(columnIndex - 1);
//
//    if (value == null) {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    switch (f.getSQLType()) {
//    case 2:
//    case 3:
//      stringVal = StringUtils.toAsciiString((byte[])value);
//
//      break;
//    default:
//      stringVal = getNativeString(columnIndex);
//    }
//
//    return getBigDecimalFromString(stringVal, columnIndex, scale);
//  }
//
//  protected InputStream getNativeBinaryStream(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//
//    int columnIndexMinusOne = columnIndex - 1;
//
//    if (this.thisRow.isNull(columnIndexMinusOne)) {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    switch (this.fields[columnIndexMinusOne].getSQLType()) {
//    case -7:
//    case -4:
//    case -3:
//    case -2:
//    case 2004:
//      return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
//    }
//
//    byte[] b = getNativeBytes(columnIndex, false);
//
//    if (b != null) {
//      return new ByteArrayInputStream(b);
//    }
//
//    return null;
//  }
//
//  protected java.sql.Blob getNativeBlob(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//
//    checkColumnBounds(columnIndex);
//
//    Object value = this.thisRow.getColumnValue(columnIndex - 1);
//
//    if (value == null)
//      this.wasNullFlag = true;
//    else {
//      this.wasNullFlag = false;
//    }
//
//    if (this.wasNullFlag) {
//      return null;
//    }
//
//    int mysqlType = this.fields[(columnIndex - 1)].getMysqlType();
//
//    byte[] dataAsBytes = null;
//
//    switch (mysqlType) {
//    case 249:
//    case 250:
//    case 251:
//    case 252:
//      dataAsBytes = (byte[])value;
//      break;
//    default:
//      dataAsBytes = getNativeBytes(columnIndex, false);
//    }
//
//    if (!this.connection.getEmulateLocators()) {
//      return new Blob(dataAsBytes, getExceptionInterceptor());
//    }
//
//    return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
//  }
//
//  public static boolean arraysEqual(byte[] left, byte[] right) {
//    if (left == null) {
//      return right == null;
//    }
//    if (right == null) {
//      return false;
//    }
//    if (left.length != right.length) {
//      return false;
//    }
//    for (int i = 0; i < left.length; i++) {
//      if (left[i] != right[i]) {
//        return false;
//      }
//    }
//    return true;
//  }
//
//  protected byte getNativeByte(int columnIndex)
//    throws SQLException
//  {
//    return getNativeByte(columnIndex, true);
//  }
//
//  protected byte getNativeByte(int columnIndex, boolean overflowCheck) throws SQLException {
//    checkRowPos();
//
//    checkColumnBounds(columnIndex);
//
//    Object value = this.thisRow.getColumnValue(columnIndex - 1);
//
//    if (value == null) {
//      this.wasNullFlag = true;
//
//      return 0;
//    }
//
//    this.wasNullFlag = false;
//
//    columnIndex--;
//
//    Field field = this.fields[columnIndex];
//    long valueAsLong;
//    short valueAsShort;
//    switch (field.getMysqlType()) {
//    case 16:
//      valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -128L) || (valueAsLong > 127L)))
//      {
//        throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
//      }
//
//      return (byte)(int)valueAsLong;
//    case 1:
//      byte valueAsByte = ((byte[])(byte[])value)[0];
//
//      if (!field.isUnsigned()) {
//        return valueAsByte;
//      }
//
//      valueAsShort = valueAsByte >= 0 ? (short)valueAsByte : (short)(valueAsByte + 256);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && 
//        (valueAsShort > 127)) {
//        throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
//      }
//
//      return (byte)valueAsShort;
//    case 2:
//    case 13:
//      valueAsShort = getNativeShort(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsShort < -128) || (valueAsShort > 127)))
//      {
//        throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
//      }
//
//      return (byte)valueAsShort;
//    case 3:
//    case 9:
//      int valueAsInt = getNativeInt(columnIndex + 1, false);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsInt < -128) || (valueAsInt > 127))) {
//        throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, -6);
//      }
//
//      return (byte)valueAsInt;
//    case 4:
//      float valueAsFloat = getNativeFloat(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsFloat < -128.0F) || (valueAsFloat > 127.0F)))
//      {
//        throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, -6);
//      }
//
//      return (byte)(int)valueAsFloat;
//    case 5:
//      double valueAsDouble = getNativeDouble(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
//      {
//        throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -6);
//      }
//
//      return (byte)(int)valueAsDouble;
//    case 8:
//      valueAsLong = getNativeLong(columnIndex + 1, false, true);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsLong < -128L) || (valueAsLong > 127L)))
//      {
//        throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
//      }
//
//      return (byte)(int)valueAsLong;
//    case 6:
//    case 7:
//    case 10:
//    case 11:
//    case 12:
//    case 14:
//    case 15: } if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getByte()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
//    }
//
//    return getByteFromString(getNativeString(columnIndex + 1), columnIndex + 1);
//  }
//
//  protected byte[] getNativeBytes(int columnIndex, boolean noConversion)
//    throws SQLException
//  {
//    checkRowPos();
//
//    checkColumnBounds(columnIndex);
//
//    Object value = this.thisRow.getColumnValue(columnIndex - 1);
//
//    if (value == null)
//      this.wasNullFlag = true;
//    else {
//      this.wasNullFlag = false;
//    }
//
//    if (this.wasNullFlag) {
//      return null;
//    }
//
//    Field field = this.fields[(columnIndex - 1)];
//
//    int mysqlType = field.getMysqlType();
//
//    if (noConversion) {
//      mysqlType = 252;
//    }
//
//    switch (mysqlType) {
//    case 16:
//    case 249:
//    case 250:
//    case 251:
//    case 252:
//      return (byte[])value;
//    case 15:
//    case 253:
//    case 254:
//      if ((value instanceof byte[])) {
//        return (byte[])value;
//      }
//      break;
//    }
//    int sqlType = field.getSQLType();
//
//    if ((sqlType == -3) || (sqlType == -2)) {
//      return (byte[])value;
//    }
//
//    return getBytesFromString(getNativeString(columnIndex));
//  }
//
//  protected Reader getNativeCharacterStream(int columnIndex)
//    throws SQLException
//  {
//    int columnIndexMinusOne = columnIndex - 1;
//
//    switch (this.fields[columnIndexMinusOne].getSQLType()) {
//    case -1:
//    case 1:
//    case 12:
//    case 2005:
//      if (this.thisRow.isNull(columnIndexMinusOne)) {
//        this.wasNullFlag = true;
//
//        return null;
//      }
//
//      this.wasNullFlag = false;
//
//      return this.thisRow.getReader(columnIndexMinusOne);
//    }
//
//    String asString = getStringForClob(columnIndex);
//
//    if (asString == null) {
//      return null;
//    }
//
//    return getCharacterStreamFromString(asString);
//  }
//
//  protected java.sql.Clob getNativeClob(int columnIndex)
//    throws SQLException
//  {
//    String stringVal = getStringForClob(columnIndex);
//
//    if (stringVal == null) {
//      return null;
//    }
//
//    return getClobFromString(stringVal);
//  }
//
//  private String getNativeConvertToString(int columnIndex, Field field)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      int sqlType = field.getSQLType();
//      int mysqlType = field.getMysqlType();
//      int intVal;
//      long longVal;
//      switch (sqlType) {
//      case -7:
//        return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
//      case 16:
//        boolean booleanVal = getBoolean(columnIndex);
//
//        if (this.wasNullFlag) {
//          return null;
//        }
//
//        return String.valueOf(booleanVal);
//      case -6:
//        byte tinyintVal = getNativeByte(columnIndex, false);
//
//        if (this.wasNullFlag) {
//          return null;
//        }
//
//        if ((!field.isUnsigned()) || (tinyintVal >= 0)) {
//          return String.valueOf(tinyintVal);
//        }
//
//        short unsignedTinyVal = (short)(tinyintVal & 0xFF);
//
//        return String.valueOf(unsignedTinyVal);
//      case 5:
//        intVal = getNativeInt(columnIndex, false);
//
//        if (this.wasNullFlag) {
//          return null;
//        }
//
//        if ((!field.isUnsigned()) || (intVal >= 0)) {
//          return String.valueOf(intVal);
//        }
//
//        intVal &= 65535;
//
//        return String.valueOf(intVal);
//      case 4:
//        intVal = getNativeInt(columnIndex, false);
//
//        if (this.wasNullFlag) {
//          return null;
//        }
//
//        if ((!field.isUnsigned()) || (intVal >= 0) || (field.getMysqlType() == 9))
//        {
//          return String.valueOf(intVal);
//        }
//
//        longVal = intVal & 0xFFFFFFFF;
//
//        return String.valueOf(longVal);
//      case -5:
//        if (!field.isUnsigned()) {
//          longVal = getNativeLong(columnIndex, false, true);
//
//          if (this.wasNullFlag) {
//            return null;
//          }
//
//          return String.valueOf(longVal);
//        }
//
//        long longVal = getNativeLong(columnIndex, false, false);
//
//        if (this.wasNullFlag) {
//          return null;
//        }
//
//        return String.valueOf(convertLongToUlong(longVal));
//      case 7:
//        float floatVal = getNativeFloat(columnIndex);
//
//        if (this.wasNullFlag) {
//          return null;
//        }
//
//        return String.valueOf(floatVal);
//      case 6:
//      case 8:
//        double doubleVal = getNativeDouble(columnIndex);
//
//        if (this.wasNullFlag) {
//          return null;
//        }
//
//        return String.valueOf(doubleVal);
//      case 2:
//      case 3:
//        String stringVal = StringUtils.toAsciiString(this.thisRow.getColumnValue(columnIndex - 1));
//
//        if (stringVal != null) {
//          this.wasNullFlag = false;
//
//          if (stringVal.length() == 0) {
//            BigDecimal val = new BigDecimal(0);
//
//            return val.toString();
//          }
//          BigDecimal val;
//          try {
//            val = new BigDecimal(stringVal);
//          } catch (NumberFormatException ex) {
//            throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//          }
//
//          return val.toString();
//        }
//
//        this.wasNullFlag = true;
//
//        return null;
//      case -1:
//      case 1:
//      case 12:
//        return extractStringFromNativeColumn(columnIndex, mysqlType);
//      case -4:
//      case -3:
//      case -2:
//        if (!field.isBlob())
//          return extractStringFromNativeColumn(columnIndex, mysqlType);
//        if (!field.isBinary()) {
//          return extractStringFromNativeColumn(columnIndex, mysqlType);
//        }
//        byte[] data = getBytes(columnIndex);
//        Object obj = data;
//
//        if ((data != null) && (data.length >= 2)) {
//          if ((data[0] == -84) && (data[1] == -19)) {
//            try
//            {
//              ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
//
//              ObjectInputStream objIn = new ObjectInputStream(bytesIn);
//
//              obj = objIn.readObject();
//              objIn.close();
//              bytesIn.close();
//            } catch (ClassNotFoundException cnfe) {
//              throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
//            }
//            catch (IOException ex)
//            {
//              obj = data;
//            }
//          }
//
//          return obj.toString();
//        }
//
//        return extractStringFromNativeColumn(columnIndex, mysqlType);
//      case 91:
//        if (mysqlType == 13) {
//          short shortVal = getNativeShort(columnIndex);
//
//          if (!this.connection.getYearIsDateType())
//          {
//            if (this.wasNullFlag) {
//              return null;
//            }
//
//            return String.valueOf(shortVal);
//          }
//
//          if (field.getLength() == 2L)
//          {
//            if (shortVal <= 69) {
//              shortVal = (short)(shortVal + 100);
//            }
//
//            shortVal = (short)(shortVal + 1900);
//          }
//
//          return fastDateCreate(null, shortVal, 1, 1).toString();
//        }
//
//        if (this.connection.getNoDatetimeStringSync()) {
//          byte[] asBytes = getNativeBytes(columnIndex, true);
//
//          if (asBytes == null) {
//            return null;
//          }
//
//          if (asBytes.length == 0)
//          {
//            return "0000-00-00";
//          }
//
//          int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
//
//          int month = asBytes[2];
//          int day = asBytes[3];
//
//          if ((year == 0) && (month == 0) && (day == 0)) {
//            return "0000-00-00";
//          }
//        }
//
//        Date dt = getNativeDate(columnIndex);
//
//        if (dt == null) {
//          return null;
//        }
//
//        return String.valueOf(dt);
//      case 92:
//        Time tm = getNativeTime(columnIndex, null, this.defaultTimeZone, false);
//
//        if (tm == null) {
//          return null;
//        }
//
//        return String.valueOf(tm);
//      case 93:
//        if (this.connection.getNoDatetimeStringSync()) {
//          byte[] asBytes = getNativeBytes(columnIndex, true);
//
//          if (asBytes == null) {
//            return null;
//          }
//
//          if (asBytes.length == 0)
//          {
//            return "0000-00-00 00:00:00";
//          }
//
//          int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
//
//          int month = asBytes[2];
//          int day = asBytes[3];
//
//          if ((year == 0) && (month == 0) && (day == 0)) {
//            return "0000-00-00 00:00:00";
//          }
//        }
//
//        Timestamp tstamp = getNativeTimestamp(columnIndex, null, this.defaultTimeZone, false);
//
//        if (tstamp == null) {
//          return null;
//        }
//
//        String result = String.valueOf(tstamp);
//
//        if (!this.connection.getNoDatetimeStringSync()) {
//          return result;
//        }
//
//        if (result.endsWith(".0")) {
//          return result.substring(0, result.length() - 2);
//        }
//        break;
//      }
//      return extractStringFromNativeColumn(columnIndex, mysqlType);
//    }
//  }
//
//  protected Date getNativeDate(int columnIndex)
//    throws SQLException
//  {
//    return getNativeDate(columnIndex, null);
//  }
//
//  protected Date getNativeDate(int columnIndex, Calendar cal)
//    throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    int columnIndexMinusOne = columnIndex - 1;
//
//    int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
//
//    Date dateToReturn = null;
//
//    if (mysqlType == 10)
//    {
//      dateToReturn = this.thisRow.getNativeDate(columnIndexMinusOne, this.connection, this, cal);
//    }
//    else {
//      TimeZone tz = cal != null ? cal.getTimeZone() : getDefaultTimeZone();
//
//      boolean rollForward = (tz != null) && (!tz.equals(getDefaultTimeZone()));
//
//      dateToReturn = (Date)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 91, mysqlType, tz, rollForward, this.connection, this);
//    }
//
//    if (dateToReturn == null)
//    {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    return dateToReturn;
//  }
//
//  Date getNativeDateViaParseConversion(int columnIndex) throws SQLException {
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getDate()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 10 });
//    }
//
//    String stringVal = getNativeString(columnIndex);
//
//    return getDateFromString(stringVal, columnIndex, null);
//  }
//
//  protected double getNativeDouble(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    columnIndex--;
//
//    if (this.thisRow.isNull(columnIndex)) {
//      this.wasNullFlag = true;
//
//      return 0.0D;
//    }
//
//    this.wasNullFlag = false;
//
//    Field f = this.fields[columnIndex];
//
//    switch (f.getMysqlType()) {
//    case 5:
//      return this.thisRow.getNativeDouble(columnIndex);
//    case 1:
//      if (!f.isUnsigned()) {
//        return getNativeByte(columnIndex + 1);
//      }
//
//      return getNativeShort(columnIndex + 1);
//    case 2:
//    case 13:
//      if (!f.isUnsigned()) {
//        return getNativeShort(columnIndex + 1);
//      }
//
//      return getNativeInt(columnIndex + 1);
//    case 3:
//    case 9:
//      if (!f.isUnsigned()) {
//        return getNativeInt(columnIndex + 1);
//      }
//
//      return getNativeLong(columnIndex + 1);
//    case 8:
//      long valueAsLong = getNativeLong(columnIndex + 1);
//
//      if (!f.isUnsigned()) {
//        return valueAsLong;
//      }
//
//      BigInteger asBigInt = convertLongToUlong(valueAsLong);
//
//      return asBigInt.doubleValue();
//    case 4:
//      return getNativeFloat(columnIndex + 1);
//    case 16:
//      return getNumericRepresentationOfSQLBitType(columnIndex + 1);
//    case 6:
//    case 7:
//    case 10:
//    case 11:
//    case 12:
//    case 14:
//    case 15: } String stringVal = getNativeString(columnIndex + 1);
//
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getDouble()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
//    }
//
//    return getDoubleFromString(stringVal, columnIndex + 1);
//  }
//
//  protected float getNativeFloat(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    columnIndex--;
//
//    if (this.thisRow.isNull(columnIndex)) {
//      this.wasNullFlag = true;
//
//      return 0.0F;
//    }
//
//    this.wasNullFlag = false;
//
//    Field f = this.fields[columnIndex];
//    long valueAsLong;
//    switch (f.getMysqlType()) {
//    case 16:
//      valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
//
//      return (float)valueAsLong;
//    case 5:
//      Double valueAsDouble = new Double(getNativeDouble(columnIndex + 1));
//
//      float valueAsFloat = valueAsDouble.floatValue();
//
//      if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == (1.0F / -1.0F))) || (valueAsFloat == (1.0F / 1.0F)))
//      {
//        throwRangeException(valueAsDouble.toString(), columnIndex + 1, 6);
//      }
//
//      return (float)getNativeDouble(columnIndex + 1);
//    case 1:
//      if (!f.isUnsigned()) {
//        return getNativeByte(columnIndex + 1);
//      }
//
//      return getNativeShort(columnIndex + 1);
//    case 2:
//    case 13:
//      if (!f.isUnsigned()) {
//        return getNativeShort(columnIndex + 1);
//      }
//
//      return getNativeInt(columnIndex + 1);
//    case 3:
//    case 9:
//      if (!f.isUnsigned()) {
//        return getNativeInt(columnIndex + 1);
//      }
//
//      return (float)getNativeLong(columnIndex + 1);
//    case 8:
//      valueAsLong = getNativeLong(columnIndex + 1);
//
//      if (!f.isUnsigned()) {
//        return (float)valueAsLong;
//      }
//
//      BigInteger asBigInt = convertLongToUlong(valueAsLong);
//
//      return asBigInt.floatValue();
//    case 4:
//      return this.thisRow.getNativeFloat(columnIndex);
//    case 6:
//    case 7:
//    case 10:
//    case 11:
//    case 12:
//    case 14:
//    case 15: } String stringVal = getNativeString(columnIndex + 1);
//
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getFloat()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
//    }
//
//    return getFloatFromString(stringVal, columnIndex + 1);
//  }
//
//  protected int getNativeInt(int columnIndex)
//    throws SQLException
//  {
//    return getNativeInt(columnIndex, true);
//  }
//
//  protected int getNativeInt(int columnIndex, boolean overflowCheck) throws SQLException {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    columnIndex--;
//
//    if (this.thisRow.isNull(columnIndex)) {
//      this.wasNullFlag = true;
//
//      return 0;
//    }
//
//    this.wasNullFlag = false;
//
//    Field f = this.fields[columnIndex];
//    long valueAsLong;
//    double valueAsDouble;
//    switch (f.getMysqlType()) {
//    case 16:
//      valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
//      {
//        throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
//      }
//
//      return (short)(int)valueAsLong;
//    case 1:
//      byte tinyintVal = getNativeByte(columnIndex + 1, false);
//
//      if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
//        return tinyintVal;
//      }
//
//      return tinyintVal + 256;
//    case 2:
//    case 13:
//      short asShort = getNativeShort(columnIndex + 1, false);
//
//      if ((!f.isUnsigned()) || (asShort >= 0)) {
//        return asShort;
//      }
//
//      return asShort + 65536;
//    case 3:
//    case 9:
//      int valueAsInt = this.thisRow.getNativeInt(columnIndex);
//
//      if (!f.isUnsigned()) {
//        return valueAsInt;
//      }
//
//      valueAsLong = valueAsInt >= 0 ? valueAsInt : valueAsInt + 4294967296L;
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 2147483647L))
//      {
//        throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
//      }
//
//      return (int)valueAsLong;
//    case 8:
//      valueAsLong = getNativeLong(columnIndex + 1, false, true);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
//      {
//        throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
//      }
//
//      return (int)valueAsLong;
//    case 5:
//      valueAsDouble = getNativeDouble(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
//      {
//        throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
//      }
//
//      return (int)valueAsDouble;
//    case 4:
//      valueAsDouble = getNativeFloat(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
//      {
//        throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
//      }
//
//      return (int)valueAsDouble;
//    case 6:
//    case 7:
//    case 10:
//    case 11:
//    case 12:
//    case 14:
//    case 15: } String stringVal = getNativeString(columnIndex + 1);
//
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getInt()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
//    }
//
//    return getIntFromString(stringVal, columnIndex + 1);
//  }
//
//  protected long getNativeLong(int columnIndex)
//    throws SQLException
//  {
//    return getNativeLong(columnIndex, true, true);
//  }
//
//  protected long getNativeLong(int columnIndex, boolean overflowCheck, boolean expandUnsignedLong) throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    columnIndex--;
//
//    if (this.thisRow.isNull(columnIndex)) {
//      this.wasNullFlag = true;
//
//      return 0L;
//    }
//
//    this.wasNullFlag = false;
//
//    Field f = this.fields[columnIndex];
//    double valueAsDouble;
//    switch (f.getMysqlType()) {
//    case 16:
//      return getNumericRepresentationOfSQLBitType(columnIndex + 1);
//    case 1:
//      if (!f.isUnsigned()) {
//        return getNativeByte(columnIndex + 1);
//      }
//
//      return getNativeInt(columnIndex + 1);
//    case 2:
//      if (!f.isUnsigned()) {
//        return getNativeShort(columnIndex + 1);
//      }
//
//      return getNativeInt(columnIndex + 1, false);
//    case 13:
//      return getNativeShort(columnIndex + 1);
//    case 3:
//    case 9:
//      int asInt = getNativeInt(columnIndex + 1, false);
//
//      if ((!f.isUnsigned()) || (asInt >= 0)) {
//        return asInt;
//      }
//
//      return asInt + 4294967296L;
//    case 8:
//      long valueAsLong = this.thisRow.getNativeLong(columnIndex);
//
//      if ((!f.isUnsigned()) || (!expandUnsignedLong)) {
//        return valueAsLong;
//      }
//
//      BigInteger asBigInt = convertLongToUlong(valueAsLong);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(9223372036854775807L))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(-9223372036854775808L))) < 0)))
//      {
//        throwRangeException(asBigInt.toString(), columnIndex + 1, -5);
//      }
//
//      return getLongFromString(asBigInt.toString(), columnIndex);
//    case 5:
//      valueAsDouble = getNativeDouble(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
//      {
//        throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
//      }
//
//      return ()valueAsDouble;
//    case 4:
//      valueAsDouble = getNativeFloat(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
//      {
//        throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
//      }
//
//      return ()valueAsDouble;
//    case 6:
//    case 7:
//    case 10:
//    case 11:
//    case 12:
//    case 14:
//    case 15: } String stringVal = getNativeString(columnIndex + 1);
//
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getLong()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
//    }
//
//    return getLongFromString(stringVal, columnIndex + 1);
//  }
//
//  protected Ref getNativeRef(int i)
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  protected short getNativeShort(int columnIndex)
//    throws SQLException
//  {
//    return getNativeShort(columnIndex, true);
//  }
//
//  protected short getNativeShort(int columnIndex, boolean overflowCheck) throws SQLException {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    columnIndex--;
//
//    if (this.thisRow.isNull(columnIndex)) {
//      this.wasNullFlag = true;
//
//      return 0;
//    }
//
//    this.wasNullFlag = false;
//
//    Field f = this.fields[columnIndex];
//    int valueAsInt;
//    long valueAsLong;
//    switch (f.getMysqlType())
//    {
//    case 1:
//      byte tinyintVal = getNativeByte(columnIndex + 1, false);
//
//      if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
//        return (short)tinyintVal;
//      }
//
//      return (short)(tinyintVal + 256);
//    case 2:
//    case 13:
//      short asShort = this.thisRow.getNativeShort(columnIndex);
//
//      if (!f.isUnsigned()) {
//        return asShort;
//      }
//
//      valueAsInt = asShort & 0xFFFF;
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767))
//      {
//        throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
//      }
//
//      return (short)valueAsInt;
//    case 3:
//    case 9:
//      if (!f.isUnsigned()) {
//        valueAsInt = getNativeInt(columnIndex + 1, false);
//
//        if (((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767)) || (valueAsInt < -32768))
//        {
//          throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
//        }
//
//        return (short)valueAsInt;
//      }
//
//      valueAsLong = getNativeLong(columnIndex + 1, false, true);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 32767L))
//      {
//        throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
//      }
//
//      return (short)(int)valueAsLong;
//    case 8:
//      valueAsLong = getNativeLong(columnIndex + 1, false, false);
//
//      if (!f.isUnsigned()) {
//        if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//          (valueAsLong < -32768L) || (valueAsLong > 32767L)))
//        {
//          throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
//        }
//
//        return (short)(int)valueAsLong;
//      }
//
//      BigInteger asBigInt = convertLongToUlong(valueAsLong);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(32767))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(-32768))) < 0)))
//      {
//        throwRangeException(asBigInt.toString(), columnIndex + 1, 5);
//      }
//
//      return (short)getIntFromString(asBigInt.toString(), columnIndex + 1);
//    case 5:
//      double valueAsDouble = getNativeDouble(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D)))
//      {
//        throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 5);
//      }
//
//      return (short)(int)valueAsDouble;
//    case 4:
//      float valueAsFloat = getNativeFloat(columnIndex + 1);
//
//      if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
//        (valueAsFloat < -32768.0F) || (valueAsFloat > 32767.0F)))
//      {
//        throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, 5);
//      }
//
//      return (short)(int)valueAsFloat;
//    case 6:
//    case 7:
//    case 10:
//    case 11:
//    case 12: } String stringVal = getNativeString(columnIndex + 1);
//
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getShort()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
//    }
//
//    return getShortFromString(stringVal, columnIndex + 1);
//  }
//
//  protected String getNativeString(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    if (this.fields == null) {
//      throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_133"), "S1002", getExceptionInterceptor());
//    }
//
//    if (this.thisRow.isNull(columnIndex - 1)) {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    String stringVal = null;
//
//    Field field = this.fields[(columnIndex - 1)];
//
//    stringVal = getNativeConvertToString(columnIndex, field);
//    int mysqlType = field.getMysqlType();
//
//    if ((mysqlType != 7) && (mysqlType != 10) && (field.isZeroFill()) && (stringVal != null))
//    {
//      int origLength = stringVal.length();
//
//      StringBuffer zeroFillBuf = new StringBuffer(origLength);
//
//      long numZeros = field.getLength() - origLength;
//
//      for (long i = 0L; i < numZeros; i += 1L) {
//        zeroFillBuf.append('0');
//      }
//
//      zeroFillBuf.append(stringVal);
//
//      stringVal = zeroFillBuf.toString();
//    }
//
//    return stringVal;
//  }
//
//  private Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
//    throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    int columnIndexMinusOne = columnIndex - 1;
//
//    int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
//
//    Time timeVal = null;
//
//    if (mysqlType == 11) {
//      timeVal = this.thisRow.getNativeTime(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
//    }
//    else
//    {
//      timeVal = (Time)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 92, mysqlType, tz, rollForward, this.connection, this);
//    }
//
//    if (timeVal == null)
//    {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    return timeVal;
//  }
//
//  Time getNativeTimeViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException
//  {
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getTime()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 11 });
//    }
//
//    String strTime = getNativeString(columnIndex);
//
//    return getTimeFromString(strTime, targetCalendar, columnIndex, tz, rollForward);
//  }
//
//  private Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
//    throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    int columnIndexMinusOne = columnIndex - 1;
//
//    Timestamp tsVal = null;
//
//    int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
//
//    switch (mysqlType) {
//    case 7:
//    case 12:
//      tsVal = this.thisRow.getNativeTimestamp(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
//
//      break;
//    default:
//      tsVal = (Timestamp)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 93, mysqlType, tz, rollForward, this.connection, this);
//    }
//
//    if (tsVal == null)
//    {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    return tsVal;
//  }
//
//  Timestamp getNativeTimestampViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException
//  {
//    if (this.useUsageAdvisor) {
//      issueConversionViaParsingWarning("getTimestamp()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 7, 12 });
//    }
//
//    String strTimestamp = getNativeString(columnIndex);
//
//    return getTimestampFromString(columnIndex, targetCalendar, strTimestamp, tz, rollForward);
//  }
//
//  protected InputStream getNativeUnicodeStream(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//
//    return getBinaryStream(columnIndex);
//  }
//
//  protected URL getNativeURL(int colIndex)
//    throws SQLException
//  {
//    String val = getString(colIndex);
//
//    if (val == null) {
//      return null;
//    }
//    try
//    {
//      return new URL(val); } catch (MalformedURLException mfe) {
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____141") + val + "'", "S1009", getExceptionInterceptor());
//  }
//
//  public synchronized ResultSetInternalMethods getNextResultSet()
//  {
//    return this.nextResultSet;
//  }
//
//  public Object getObject(int columnIndex)
//    throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    int columnIndexMinusOne = columnIndex - 1;
//
//    if (this.thisRow.isNull(columnIndexMinusOne)) {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    Field field = this.fields[columnIndexMinusOne];
//    String stringVal;
//    switch (field.getSQLType()) {
//    case -7:
//    case 16:
//      if ((field.getMysqlType() == 16) && (!field.isSingleBit()))
//      {
//        return getBytes(columnIndex);
//      }
//
//      return Boolean.valueOf(getBoolean(columnIndex));
//    case -6:
//      if (!field.isUnsigned()) {
//        return Integer.valueOf(getByte(columnIndex));
//      }
//
//      return Integer.valueOf(getInt(columnIndex));
//    case 5:
//      return Integer.valueOf(getInt(columnIndex));
//    case 4:
//      if ((!field.isUnsigned()) || (field.getMysqlType() == 9))
//      {
//        return Integer.valueOf(getInt(columnIndex));
//      }
//
//      return Long.valueOf(getLong(columnIndex));
//    case -5:
//      if (!field.isUnsigned()) {
//        return Long.valueOf(getLong(columnIndex));
//      }
//
//      stringVal = getString(columnIndex);
//
//      if (stringVal == null) {
//        return null;
//      }
//      try
//      {
//        return new BigInteger(stringVal);
//      } catch (NumberFormatException nfe) {
//        throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
//      }
//
//    case 2:
//    case 3:
//      stringVal = getString(columnIndex);
//
//      if (stringVal != null) {
//        if (stringVal.length() == 0) {
//          BigDecimal val = new BigDecimal(0);
//
//          return val;
//        }
//        BigDecimal val;
//        try {
//          val = new BigDecimal(stringVal);
//        } catch (NumberFormatException ex) {
//          throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//        }
//
//        return val;
//      }
//
//      return null;
//    case 7:
//      return new Float(getFloat(columnIndex));
//    case 6:
//    case 8:
//      return new Double(getDouble(columnIndex));
//    case 1:
//    case 12:
//      if (!field.isOpaqueBinary()) {
//        return getString(columnIndex);
//      }
//
//      return getBytes(columnIndex);
//    case -1:
//      if (!field.isOpaqueBinary()) {
//        return getStringForClob(columnIndex);
//      }
//
//      return getBytes(columnIndex);
//    case -4:
//    case -3:
//    case -2:
//      if (field.getMysqlType() == 255)
//        return getBytes(columnIndex);
//      if ((field.isBinary()) || (field.isBlob())) {
//        byte[] data = getBytes(columnIndex);
//
//        if (this.connection.getAutoDeserialize()) {
//          Object obj = data;
//
//          if ((data != null) && (data.length >= 2)) {
//            if ((data[0] == -84) && (data[1] == -19))
//              try
//              {
//                ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
//
//                ObjectInputStream objIn = new ObjectInputStream(bytesIn);
//
//                obj = objIn.readObject();
//                objIn.close();
//                bytesIn.close();
//              } catch (ClassNotFoundException cnfe) {
//                throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
//              }
//              catch (IOException ex)
//              {
//                obj = data;
//              }
//            else {
//              return getString(columnIndex);
//            }
//          }
//
//          return obj;
//        }
//
//        return data;
//      }
//
//      return getBytes(columnIndex);
//    case 91:
//      if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
//      {
//        return Short.valueOf(getShort(columnIndex));
//      }
//
//      return getDate(columnIndex);
//    case 92:
//      return getTime(columnIndex);
//    case 93:
//      return getTimestamp(columnIndex);
//    }
//
//    return getString(columnIndex);
//  }
//
//  public <T> T getObject(int columnIndex, Class<T> type)
//    throws SQLException
//  {
//    if (type == null) {
//      throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor());
//    }
//
//    if (type.equals(String.class))
//      return getString(columnIndex);
//    if (type.equals(BigDecimal.class))
//      return getBigDecimal(columnIndex);
//    if ((type.equals(Boolean.class)) || (type.equals(Boolean.TYPE)))
//      return Boolean.valueOf(getBoolean(columnIndex));
//    if ((type.equals(Integer.class)) || (type.equals(Integer.TYPE)))
//      return Integer.valueOf(getInt(columnIndex));
//    if ((type.equals(Long.class)) || (type.equals(Long.TYPE)))
//      return Long.valueOf(getLong(columnIndex));
//    if ((type.equals(Float.class)) || (type.equals(Float.TYPE)))
//      return Float.valueOf(getFloat(columnIndex));
//    if ((type.equals(Double.class)) || (type.equals(Double.TYPE)))
//      return Double.valueOf(getDouble(columnIndex));
//    if (type.equals([B.class))
//      return getBytes(columnIndex);
//    if (type.equals(Date.class))
//      return getDate(columnIndex);
//    if (type.equals(Time.class))
//      return getTime(columnIndex);
//    if (type.equals(Timestamp.class))
//      return getTimestamp(columnIndex);
//    if (type.equals(Clob.class))
//      return getClob(columnIndex);
//    if (type.equals(Blob.class))
//      return getBlob(columnIndex);
//    if (type.equals(Array.class))
//      return getArray(columnIndex);
//    if (type.equals(Ref.class))
//      return getRef(columnIndex);
//    if (type.equals(URL.class)) {
//      return getURL(columnIndex);
//    }
//
//    if (this.connection.getAutoDeserialize()) {
//      try {
//        return getObject(columnIndex);
//      } catch (ClassCastException cce) {
//        SQLException sqlEx = SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", getExceptionInterceptor());
//
//        sqlEx.initCause(cce);
//
//        throw sqlEx;
//      }
//    }
//
//    throw SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", getExceptionInterceptor());
//  }
//
//  public <T> T getObject(String columnLabel, Class<T> type)
//    throws SQLException
//  {
//    return getObject(findColumn(columnLabel), type);
//  }
//
//  public Object getObject(int i, Map<String, Class<?>> map)
//    throws SQLException
//  {
//    return getObject(i);
//  }
//
//  public Object getObject(String columnName)
//    throws SQLException
//  {
//    return getObject(findColumn(columnName));
//  }
//
//  public Object getObject(String colName, Map<String, Class<?>> map)
//    throws SQLException
//  {
//    return getObject(findColumn(colName), map);
//  }
//
//  public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException
//  {
//    checkRowPos();
//    checkColumnBounds(columnIndex);
//
//    Object value = this.thisRow.getColumnValue(columnIndex - 1);
//
//    if (value == null) {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    Field field = this.fields[(columnIndex - 1)];
//
//    switch (desiredSqlType)
//    {
//    case -7:
//    case 16:
//      return Boolean.valueOf(getBoolean(columnIndex));
//    case -6:
//      return Integer.valueOf(getInt(columnIndex));
//    case 5:
//      return Integer.valueOf(getInt(columnIndex));
//    case 4:
//      if ((!field.isUnsigned()) || (field.getMysqlType() == 9))
//      {
//        return Integer.valueOf(getInt(columnIndex));
//      }
//
//      return Long.valueOf(getLong(columnIndex));
//    case -5:
//      if (field.isUnsigned()) {
//        return getBigDecimal(columnIndex);
//      }
//
//      return Long.valueOf(getLong(columnIndex));
//    case 2:
//    case 3:
//      String stringVal = getString(columnIndex);
//
//      if (stringVal != null) {
//        if (stringVal.length() == 0) {
//          BigDecimal val = new BigDecimal(0);
//
//          return val;
//        }
//        BigDecimal val;
//        try {
//          val = new BigDecimal(stringVal);
//        } catch (NumberFormatException ex) {
//          throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
//        }
//
//        return val;
//      }
//
//      return null;
//    case 7:
//      return new Float(getFloat(columnIndex));
//    case 6:
//      if (!this.connection.getRunningCTS13()) {
//        return new Double(getFloat(columnIndex));
//      }
//      return new Float(getFloat(columnIndex));
//    case 8:
//      return new Double(getDouble(columnIndex));
//    case 1:
//    case 12:
//      return getString(columnIndex);
//    case -1:
//      return getStringForClob(columnIndex);
//    case -4:
//    case -3:
//    case -2:
//      return getBytes(columnIndex);
//    case 91:
//      if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
//      {
//        return Short.valueOf(getShort(columnIndex));
//      }
//
//      return getDate(columnIndex);
//    case 92:
//      return getTime(columnIndex);
//    case 93:
//      return getTimestamp(columnIndex);
//    }
//
//    return getString(columnIndex);
//  }
//
//  public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType)
//    throws SQLException
//  {
//    return getObjectStoredProc(i, desiredSqlType);
//  }
//
//  public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException
//  {
//    return getObjectStoredProc(findColumn(columnName), desiredSqlType);
//  }
//
//  public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException
//  {
//    return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
//  }
//
//  public Ref getRef(int i)
//    throws SQLException
//  {
//    checkColumnBounds(i);
//    throw SQLError.notImplemented();
//  }
//
//  public Ref getRef(String colName)
//    throws SQLException
//  {
//    return getRef(findColumn(colName));
//  }
//
//  public int getRow()
//    throws SQLException
//  {
//    checkClosed();
//
//    int currentRowNumber = this.rowData.getCurrentRowNumber();
//    int row = 0;
//
//    if (!this.rowData.isDynamic()) {
//      if ((currentRowNumber < 0) || (this.rowData.isAfterLast()) || (this.rowData.isEmpty()))
//      {
//        row = 0;
//      }
//      else row = currentRowNumber + 1;
//    }
//    else
//    {
//      row = currentRowNumber + 1;
//    }
//
//    return row;
//  }
//
//  public String getServerInfo()
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        return this.serverInfo;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  private long getNumericRepresentationOfSQLBitType(int columnIndex) throws SQLException
//  {
//    Object value = this.thisRow.getColumnValue(columnIndex - 1);
//
//    if ((this.fields[(columnIndex - 1)].isSingleBit()) || (((byte[])value).length == 1))
//    {
//      return ((byte[])(byte[])value)[0];
//    }
//
//    byte[] asBytes = (byte[])value;
//
//    int shift = 0;
//
//    long[] steps = new long[asBytes.length];
//
//    for (int i = asBytes.length - 1; i >= 0; i--) {
//      steps[i] = ((asBytes[i] & 0xFF) << shift);
//      shift += 8;
//    }
//
//    long valueAsLong = 0L;
//
//    for (int i = 0; i < asBytes.length; i++) {
//      valueAsLong |= steps[i];
//    }
//
//    return valueAsLong;
//  }
//
//  public short getShort(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      checkRowPos();
//
//      if (this.useFastIntParsing)
//      {
//        checkColumnBounds(columnIndex);
//
//        Object value = this.thisRow.getColumnValue(columnIndex - 1);
//
//        if (value == null)
//          this.wasNullFlag = true;
//        else {
//          this.wasNullFlag = false;
//        }
//
//        if (this.wasNullFlag) {
//          return 0;
//        }
//
//        byte[] shortAsBytes = (byte[])value;
//
//        if (shortAsBytes.length == 0) {
//          return (short)convertToZeroWithEmptyCheck();
//        }
//
//        boolean needsFullParse = false;
//
//        for (int i = 0; i < shortAsBytes.length; i++) {
//          if (((char)shortAsBytes[i] == 'e') || ((char)shortAsBytes[i] == 'E'))
//          {
//            needsFullParse = true;
//
//            break;
//          }
//        }
//
//        if (!needsFullParse) {
//          try {
//            return parseShortWithOverflowCheck(columnIndex, shortAsBytes, null);
//          }
//          catch (NumberFormatException nfe)
//          {
//            try {
//              return parseShortAsDouble(columnIndex, StringUtils.toString(shortAsBytes));
//            }
//            catch (NumberFormatException newNfe)
//            {
//              if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
//                long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
//
//                if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L)))
//                {
//                  throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
//                }
//
//                return (short)(int)valueAsLong;
//              }
//
//              throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + StringUtils.toString(shortAsBytes) + "'", "S1009", getExceptionInterceptor());
//            }
//
//          }
//
//        }
//
//      }
//
//      String val = null;
//      try
//      {
//        val = getString(columnIndex);
//
//        if (val != null)
//        {
//          if (val.length() == 0) {
//            return (short)convertToZeroWithEmptyCheck();
//          }
//
//          if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
//          {
//            return parseShortWithOverflowCheck(columnIndex, null, val);
//          }
//
//          return parseShortAsDouble(columnIndex, val);
//        }
//
//        return 0;
//      } catch (NumberFormatException nfe) {
//        try {
//          return parseShortAsDouble(columnIndex, val);
//        }
//        catch (NumberFormatException newNfe)
//        {
//          if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
//            long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
//
//            if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L)))
//            {
//              throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
//            }
//
//            return (short)(int)valueAsLong;
//          }
//
//          throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + val + "'", "S1009", getExceptionInterceptor());
//        }
//
//      }
//
//    }
//
//    return getNativeShort(columnIndex);
//  }
//
//  public short getShort(String columnName)
//    throws SQLException
//  {
//    return getShort(findColumn(columnName));
//  }
//
//  private final short getShortFromString(String val, int columnIndex) throws SQLException
//  {
//    try {
//      if (val != null)
//      {
//        if (val.length() == 0) {
//          return (short)convertToZeroWithEmptyCheck();
//        }
//
//        if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
//        {
//          return parseShortWithOverflowCheck(columnIndex, null, val);
//        }
//
//        return parseShortAsDouble(columnIndex, val);
//      }
//
//      return 0;
//    } catch (NumberFormatException nfe) {
//      try {
//        return parseShortAsDouble(columnIndex, val);
//      }
//      catch (NumberFormatException newNfe) {
//      }
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString("ResultSet.___in_column__218") + columnIndex, "S1009", getExceptionInterceptor());
//  }
//
//  public Statement getStatement()
//    throws SQLException
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        if (this.wrapperStatement != null) {
//          return this.wrapperStatement;
//        }
//
//        return this.owningStatement;
//      }
//    }
//    catch (SQLException sqlEx) {
//      if (!this.retainOwningStatement) {
//        throw SQLError.createSQLException("Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", "S1000", getExceptionInterceptor());
//      }
//
//      if (this.wrapperStatement != null) {
//        return this.wrapperStatement;
//      }
//    }
//    return this.owningStatement;
//  }
//
//  public String getString(int columnIndex)
//    throws SQLException
//  {
//    String stringVal = getStringInternal(columnIndex, true);
//
//    if ((this.padCharsWithSpace) && (stringVal != null)) {
//      Field f = this.fields[(columnIndex - 1)];
//
//      if (f.getMysqlType() == 254) {
//        int fieldLength = (int)f.getLength() / f.getMaxBytesPerCharacter();
//
//        int currentLength = stringVal.length();
//
//        if (currentLength < fieldLength) {
//          StringBuffer paddedBuf = new StringBuffer(fieldLength);
//          paddedBuf.append(stringVal);
//
//          int difference = fieldLength - currentLength;
//
//          paddedBuf.append(EMPTY_SPACE, 0, difference);
//
//          stringVal = paddedBuf.toString();
//        }
//      }
//    }
//
//    return stringVal;
//  }
//
//  public String getString(String columnName)
//    throws SQLException
//  {
//    return getString(findColumn(columnName));
//  }
//
//  private String getStringForClob(int columnIndex) throws SQLException {
//    String asString = null;
//
//    String forcedEncoding = this.connection.getClobCharacterEncoding();
//
//    if (forcedEncoding == null) {
//      if (!this.isBinaryEncoded)
//        asString = getString(columnIndex);
//      else
//        asString = getNativeString(columnIndex);
//    }
//    else {
//      try {
//        byte[] asBytes = null;
//
//        if (!this.isBinaryEncoded)
//          asBytes = getBytes(columnIndex);
//        else {
//          asBytes = getNativeBytes(columnIndex, true);
//        }
//
//        if (asBytes != null)
//          asString = StringUtils.toString(asBytes, forcedEncoding);
//      }
//      catch (UnsupportedEncodingException uee) {
//        throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
//      }
//
//    }
//
//    return asString;
//  }
//
//  protected String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      checkRowPos();
//      checkColumnBounds(columnIndex);
//
//      if (this.fields == null) {
//        throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_99"), "S1002", getExceptionInterceptor());
//      }
//
//      int internalColumnIndex = columnIndex - 1;
//
//      if (this.thisRow.isNull(internalColumnIndex)) {
//        this.wasNullFlag = true;
//
//        return null;
//      }
//
//      this.wasNullFlag = false;
//
//      Field metadata = this.fields[internalColumnIndex];
//
//      String stringVal = null;
//
//      if (metadata.getMysqlType() == 16) {
//        if (metadata.isSingleBit()) {
//          byte[] value = this.thisRow.getColumnValue(internalColumnIndex);
//
//          if (value.length == 0) {
//            return String.valueOf(convertToZeroWithEmptyCheck());
//          }
//
//          return String.valueOf(value[0]);
//        }
//
//        return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
//      }
//
//      String encoding = metadata.getCharacterSet();
//
//      stringVal = this.thisRow.getString(internalColumnIndex, encoding, this.connection);
//
//      if (metadata.getMysqlType() == 13) {
//        if (!this.connection.getYearIsDateType()) {
//          return stringVal;
//        }
//
//        Date dt = getDateFromString(stringVal, columnIndex, null);
//
//        if (dt == null) {
//          this.wasNullFlag = true;
//
//          return null;
//        }
//
//        this.wasNullFlag = false;
//
//        return dt.toString();
//      }
//
//      if ((checkDateTypes) && (!this.connection.getNoDatetimeStringSync())) {
//        switch (metadata.getSQLType()) {
//        case 92:
//          Time tm = getTimeFromString(stringVal, null, columnIndex, getDefaultTimeZone(), false);
//
//          if (tm == null) {
//            this.wasNullFlag = true;
//
//            return null;
//          }
//
//          this.wasNullFlag = false;
//
//          return tm.toString();
//        case 91:
//          Date dt = getDateFromString(stringVal, columnIndex, null);
//
//          if (dt == null) {
//            this.wasNullFlag = true;
//
//            return null;
//          }
//
//          this.wasNullFlag = false;
//
//          return dt.toString();
//        case 93:
//          Timestamp ts = getTimestampFromString(columnIndex, null, stringVal, getDefaultTimeZone(), false);
//
//          if (ts == null) {
//            this.wasNullFlag = true;
//
//            return null;
//          }
//
//          this.wasNullFlag = false;
//
//          return ts.toString();
//        }
//
//      }
//
//      return stringVal;
//    }
//
//    return getNativeString(columnIndex);
//  }
//
//  public Time getTime(int columnIndex)
//    throws SQLException
//  {
//    return getTimeInternal(columnIndex, null, getDefaultTimeZone(), false);
//  }
//
//  public Time getTime(int columnIndex, Calendar cal)
//    throws SQLException
//  {
//    return getTimeInternal(columnIndex, cal, cal.getTimeZone(), true);
//  }
//
//  public Time getTime(String columnName)
//    throws SQLException
//  {
//    return getTime(findColumn(columnName));
//  }
//
//  public Time getTime(String columnName, Calendar cal)
//    throws SQLException
//  {
//    return getTime(findColumn(columnName), cal);
//  }
//
//  private Time getTimeFromString(String timeAsString, Calendar targetCalendar, int columnIndex, TimeZone tz, boolean rollForward)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      int hr = 0;
//      int min = 0;
//      int sec = 0;
//      try
//      {
//        if (timeAsString == null) {
//          this.wasNullFlag = true;
//
//          return null;
//        }
//
//        timeAsString = timeAsString.trim();
//
//        if ((timeAsString.equals("0")) || (timeAsString.equals("0000-00-00")) || (timeAsString.equals("0000-00-00 00:00:00")) || (timeAsString.equals("00000000000000")))
//        {
//          if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
//          {
//            this.wasNullFlag = true;
//
//            return null;
//          }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
//          {
//            throw SQLError.createSQLException("Value '" + timeAsString + "' can not be represented as java.sql.Time", "S1009", getExceptionInterceptor());
//          }
//
//          return fastTimeCreate(targetCalendar, 0, 0, 0);
//        }
//
//        this.wasNullFlag = false;
//
//        Field timeColField = this.fields[(columnIndex - 1)];
//
//        if (timeColField.getMysqlType() == 7)
//        {
//          int length = timeAsString.length();
//
//          switch (length)
//          {
//          case 19:
//            hr = Integer.parseInt(timeAsString.substring(length - 8, length - 6));
//
//            min = Integer.parseInt(timeAsString.substring(length - 5, length - 3));
//
//            sec = Integer.parseInt(timeAsString.substring(length - 2, length));
//
//            break;
//          case 12:
//          case 14:
//            hr = Integer.parseInt(timeAsString.substring(length - 6, length - 4));
//
//            min = Integer.parseInt(timeAsString.substring(length - 4, length - 2));
//
//            sec = Integer.parseInt(timeAsString.substring(length - 2, length));
//
//            break;
//          case 10:
//            hr = Integer.parseInt(timeAsString.substring(6, 8));
//            min = Integer.parseInt(timeAsString.substring(8, 10));
//            sec = 0;
//
//            break;
//          case 11:
//          case 13:
//          case 15:
//          case 16:
//          case 17:
//          case 18:
//          default:
//            throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009", getExceptionInterceptor());
//          }
//
//          SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
//
//          if (this.warningChain == null)
//            this.warningChain = precisionLost;
//          else
//            this.warningChain.setNextWarning(precisionLost);
//        }
//        else if (timeColField.getMysqlType() == 12) {
//          hr = Integer.parseInt(timeAsString.substring(11, 13));
//          min = Integer.parseInt(timeAsString.substring(14, 16));
//          sec = Integer.parseInt(timeAsString.substring(17, 19));
//
//          SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
//
//          if (this.warningChain == null)
//            this.warningChain = precisionLost;
//          else
//            this.warningChain.setNextWarning(precisionLost);
//        } else {
//          if (timeColField.getMysqlType() == 10) {
//            return fastTimeCreate(targetCalendar, 0, 0, 0);
//          }
//
//          if ((timeAsString.length() != 5) && (timeAsString.length() != 8))
//          {
//            throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + timeAsString + Messages.getString("ResultSet.___in_column__268") + columnIndex, "S1009", getExceptionInterceptor());
//          }
//
//          hr = Integer.parseInt(timeAsString.substring(0, 2));
//          min = Integer.parseInt(timeAsString.substring(3, 5));
//          sec = timeAsString.length() == 5 ? 0 : Integer.parseInt(timeAsString.substring(6));
//        }
//
//        Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
//
//        synchronized (sessionCalendar)
//        {
//        }
//
//        throw localObject1;
//      } catch (RuntimeException ex) {
//        SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", getExceptionInterceptor());
//
//        sqlEx.initCause(ex);
//
//        throw sqlEx;
//      }
//    }
//  }
//
//  private Time getTimeInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
//    throws SQLException
//  {
//    checkRowPos();
//
//    if (this.isBinaryEncoded) {
//      return getNativeTime(columnIndex, targetCalendar, tz, rollForward);
//    }
//
//    if (!this.useFastDateParsing) {
//      String timeAsString = getStringInternal(columnIndex, false);
//
//      return getTimeFromString(timeAsString, targetCalendar, columnIndex, tz, rollForward);
//    }
//
//    checkColumnBounds(columnIndex);
//
//    int columnIndexMinusOne = columnIndex - 1;
//
//    if (this.thisRow.isNull(columnIndexMinusOne)) {
//      this.wasNullFlag = true;
//
//      return null;
//    }
//
//    this.wasNullFlag = false;
//
//    return this.thisRow.getTimeFast(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
//  }
//
//  public Timestamp getTimestamp(int columnIndex)
//    throws SQLException
//  {
//    return getTimestampInternal(columnIndex, null, getDefaultTimeZone(), false);
//  }
//
//  public Timestamp getTimestamp(int columnIndex, Calendar cal)
//    throws SQLException
//  {
//    return getTimestampInternal(columnIndex, cal, cal.getTimeZone(), true);
//  }
//
//  public Timestamp getTimestamp(String columnName)
//    throws SQLException
//  {
//    return getTimestamp(findColumn(columnName));
//  }
//
//  public Timestamp getTimestamp(String columnName, Calendar cal)
//    throws SQLException
//  {
//    return getTimestamp(findColumn(columnName), cal);
//  }
//
//  private Timestamp getTimestampFromString(int columnIndex, Calendar targetCalendar, String timestampValue, TimeZone tz, boolean rollForward)
//    throws SQLException
//  {
//    try
//    {
//      this.wasNullFlag = false;
//
//      if (timestampValue == null) {
//        this.wasNullFlag = true;
//
//        return null;
//      }
//
//      timestampValue = timestampValue.trim();
//
//      int length = timestampValue.length();
//
//      Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
//
//      synchronized (sessionCalendar) {
//        if ((length > 0) && (timestampValue.charAt(0) == '0') && ((timestampValue.equals("0000-00-00")) || (timestampValue.equals("0000-00-00 00:00:00")) || (timestampValue.equals("00000000000000")) || (timestampValue.equals("0"))))
//        {
//          if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
//          {
//            this.wasNullFlag = true;
//
//            return null;
//          }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
//          {
//            throw SQLError.createSQLException("Value '" + timestampValue + "' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
//          }
//
//          return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
//        }
//        if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
//        {
//          if (!this.useLegacyDatetimeCode) {
//            return TimeUtil.fastTimestampCreate(tz, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0);
//          }
//
//          return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
//        }
//
//        if (timestampValue.endsWith(".")) {
//          timestampValue = timestampValue.substring(0, timestampValue.length() - 1);
//        }
//
//        int year = 0;
//        int month = 0;
//        int day = 0;
//        int hour = 0;
//        int minutes = 0;
//        int seconds = 0;
//        int nanos = 0;
//
//        switch (length) {
//        case 19:
//        case 20:
//        case 21:
//        case 22:
//        case 23:
//        case 24:
//        case 25:
//        case 26:
//          year = Integer.parseInt(timestampValue.substring(0, 4));
//          month = Integer.parseInt(timestampValue.substring(5, 7));
//
//          day = Integer.parseInt(timestampValue.substring(8, 10));
//          hour = Integer.parseInt(timestampValue.substring(11, 13));
//
//          minutes = Integer.parseInt(timestampValue.substring(14, 16));
//
//          seconds = Integer.parseInt(timestampValue.substring(17, 19));
//
//          nanos = 0;
//
//          if (length > 19) {
//            int decimalIndex = timestampValue.lastIndexOf('.');
//
//            if (decimalIndex != -1) {
//              if (decimalIndex + 2 <= length) {
//                nanos = Integer.parseInt(timestampValue.substring(decimalIndex + 1));
//
//                int numDigits = length - (decimalIndex + 1);
//
//                if (numDigits < 9) {
//                  int factor = (int)Math.pow(10.0D, 9 - numDigits);
//                  nanos *= factor;
//                }
//              } else {
//                throw new IllegalArgumentException();
//              }
//
//            }
//
//          }
//
//          break;
//        case 14:
//          year = Integer.parseInt(timestampValue.substring(0, 4));
//          month = Integer.parseInt(timestampValue.substring(4, 6));
//
//          day = Integer.parseInt(timestampValue.substring(6, 8));
//          hour = Integer.parseInt(timestampValue.substring(8, 10));
//
//          minutes = Integer.parseInt(timestampValue.substring(10, 12));
//
//          seconds = Integer.parseInt(timestampValue.substring(12, 14));
//
//          break;
//        case 12:
//          year = Integer.parseInt(timestampValue.substring(0, 2));
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          year += 1900;
//
//          month = Integer.parseInt(timestampValue.substring(2, 4));
//
//          day = Integer.parseInt(timestampValue.substring(4, 6));
//          hour = Integer.parseInt(timestampValue.substring(6, 8));
//          minutes = Integer.parseInt(timestampValue.substring(8, 10));
//
//          seconds = Integer.parseInt(timestampValue.substring(10, 12));
//
//          break;
//        case 10:
//          if ((this.fields[(columnIndex - 1)].getMysqlType() == 10) || (timestampValue.indexOf("-") != -1))
//          {
//            year = Integer.parseInt(timestampValue.substring(0, 4));
//            month = Integer.parseInt(timestampValue.substring(5, 7));
//
//            day = Integer.parseInt(timestampValue.substring(8, 10));
//            hour = 0;
//            minutes = 0;
//          } else {
//            year = Integer.parseInt(timestampValue.substring(0, 2));
//
//            if (year <= 69) {
//              year += 100;
//            }
//
//            month = Integer.parseInt(timestampValue.substring(2, 4));
//
//            day = Integer.parseInt(timestampValue.substring(4, 6));
//            hour = Integer.parseInt(timestampValue.substring(6, 8));
//            minutes = Integer.parseInt(timestampValue.substring(8, 10));
//
//            year += 1900;
//          }
//
//          break;
//        case 8:
//          if (timestampValue.indexOf(":") != -1) {
//            hour = Integer.parseInt(timestampValue.substring(0, 2));
//
//            minutes = Integer.parseInt(timestampValue.substring(3, 5));
//
//            seconds = Integer.parseInt(timestampValue.substring(6, 8));
//
//            year = 1970;
//            month = 1;
//            day = 1;
//          }
//          else
//          {
//            year = Integer.parseInt(timestampValue.substring(0, 4));
//            month = Integer.parseInt(timestampValue.substring(4, 6));
//
//            day = Integer.parseInt(timestampValue.substring(6, 8));
//
//            year -= 1900;
//            month--;
//          }
//          break;
//        case 6:
//          year = Integer.parseInt(timestampValue.substring(0, 2));
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          year += 1900;
//
//          month = Integer.parseInt(timestampValue.substring(2, 4));
//
//          day = Integer.parseInt(timestampValue.substring(4, 6));
//
//          break;
//        case 4:
//          year = Integer.parseInt(timestampValue.substring(0, 2));
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          year += 1900;
//
//          month = Integer.parseInt(timestampValue.substring(2, 4));
//
//          day = 1;
//
//          break;
//        case 2:
//          year = Integer.parseInt(timestampValue.substring(0, 2));
//
//          if (year <= 69) {
//            year += 100;
//          }
//
//          year += 1900;
//          month = 1;
//          day = 1;
//
//          break;
//        case 3:
//        case 5:
//        case 7:
//        case 9:
//        case 11:
//        case 13:
//        case 15:
//        case 16:
//        case 17:
//        case 18:
//        default:
//          throw new SQLException("Bad format for Timestamp '" + timestampValue + "' in column " + columnIndex + ".", "S1009");
//        }
//
//        if (!this.useLegacyDatetimeCode) {
//          return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
//        }
//
//        return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
//      }
//
//    }
//    catch (RuntimeException e)
//    {
//      SQLException sqlEx = SQLError.createSQLException("Cannot convert value '" + timestampValue + "' from column " + columnIndex + " to TIMESTAMP.", "S1009", getExceptionInterceptor());
//
//      sqlEx.initCause(e);
//
//      throw sqlEx;
//    }
//  }
//
//  private Timestamp getTimestampInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
//    throws SQLException
//  {
//    if (this.isBinaryEncoded) {
//      return getNativeTimestamp(columnIndex, targetCalendar, tz, rollForward);
//    }
//
//    Timestamp tsVal = null;
//
//    if (!this.useFastDateParsing) {
//      String timestampValue = getStringInternal(columnIndex, false);
//
//      tsVal = getTimestampFromString(columnIndex, targetCalendar, timestampValue, tz, rollForward);
//    }
//    else
//    {
//      checkClosed();
//      checkRowPos();
//      checkColumnBounds(columnIndex);
//
//      tsVal = this.thisRow.getTimestampFast(columnIndex - 1, targetCalendar, tz, rollForward, this.connection, this);
//    }
//
//    if (tsVal == null)
//      this.wasNullFlag = true;
//    else {
//      this.wasNullFlag = false;
//    }
//
//    return tsVal;
//  }
//
//  public int getType()
//    throws SQLException
//  {
//    return this.resultSetType;
//  }
//
//  /** @deprecated */
//  public InputStream getUnicodeStream(int columnIndex)
//    throws SQLException
//  {
//    if (!this.isBinaryEncoded) {
//      checkRowPos();
//
//      return getBinaryStream(columnIndex);
//    }
//
//    return getNativeBinaryStream(columnIndex);
//  }
//
//  /** @deprecated */
//  public InputStream getUnicodeStream(String columnName)
//    throws SQLException
//  {
//    return getUnicodeStream(findColumn(columnName));
//  }
//
//  public long getUpdateCount() {
//    return this.updateCount;
//  }
//
//  public long getUpdateID() {
//    return this.updateId;
//  }
//
//  public URL getURL(int colIndex)
//    throws SQLException
//  {
//    String val = getString(colIndex);
//
//    if (val == null) {
//      return null;
//    }
//    try
//    {
//      return new URL(val); } catch (MalformedURLException mfe) {
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009", getExceptionInterceptor());
//  }
//
//  public URL getURL(String colName)
//    throws SQLException
//  {
//    String val = getString(colName);
//
//    if (val == null) {
//      return null;
//    }
//    try
//    {
//      return new URL(val); } catch (MalformedURLException mfe) {
//    }
//    throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009", getExceptionInterceptor());
//  }
//
//  public SQLWarning getWarnings()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.warningChain;
//    }
//  }
//
//  public void insertRow()
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public boolean isAfterLast()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      boolean b = this.rowData.isAfterLast();
//
//      return b;
//    }
//  }
//
//  public boolean isBeforeFirst()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.rowData.isBeforeFirst();
//    }
//  }
//
//  public boolean isFirst()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.rowData.isFirst();
//    }
//  }
//
//  public boolean isLast()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      return this.rowData.isLast();
//    }
//  }
//
//  private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      StringBuffer originalQueryBuf = new StringBuffer();
//
//      if ((this.owningStatement != null) && ((this.owningStatement instanceof PreparedStatement)))
//      {
//        originalQueryBuf.append(Messages.getString("ResultSet.CostlyConversionCreatedFromQuery"));
//        originalQueryBuf.append(((PreparedStatement)this.owningStatement).originalSql);
//
//        originalQueryBuf.append("\n\n");
//      } else {
//        originalQueryBuf.append(".");
//      }
//
//      StringBuffer convertibleTypesBuf = new StringBuffer();
//
//      for (int i = 0; i < typesWithNoParseConversion.length; i++) {
//        convertibleTypesBuf.append(MysqlDefs.typeToName(typesWithNoParseConversion[i]));
//        convertibleTypesBuf.append("\n");
//      }
//
//      String message = Messages.getString("ResultSet.CostlyConversion", new Object[] { methodName, Integer.valueOf(columnIndex + 1), fieldInfo.getOriginalName(), fieldInfo.getOriginalTableName(), originalQueryBuf.toString(), value != null ? value.getClass().getName() : ResultSetMetaData.getClassNameForJavaType(fieldInfo.getSQLType(), fieldInfo.isUnsigned(), fieldInfo.getMysqlType(), (fieldInfo.isBinary()) || (fieldInfo.isBlob()) ? 1 : false, fieldInfo.isOpaqueBinary(), this.connection.getYearIsDateType()), MysqlDefs.typeToName(fieldInfo.getMysqlType()), convertibleTypesBuf.toString() });
//
//      this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
//    }
//  }
//
//  public boolean last()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      boolean b = true;
//
//      if (this.rowData.size() == 0) {
//        b = false;
//      }
//      else {
//        if (this.onInsertRow) {
//          this.onInsertRow = false;
//        }
//
//        if (this.doingUpdates) {
//          this.doingUpdates = false;
//        }
//
//        if (this.thisRow != null) {
//          this.thisRow.closeOpenStreams();
//        }
//
//        this.rowData.beforeLast();
//        this.thisRow = this.rowData.next();
//      }
//
//      setRowPositionValidity();
//
//      return b;
//    }
//  }
//
//  public void moveToCurrentRow()
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void moveToInsertRow()
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public boolean next()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      if (this.onInsertRow) {
//        this.onInsertRow = false;
//      }
//
//      if (this.doingUpdates) {
//        this.doingUpdates = false;
//      }
//
//      if (!reallyResult()) {
//        throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor());
//      }
//
//      if (this.thisRow != null)
//        this.thisRow.closeOpenStreams();
//      boolean b;
//      boolean b;
//      if (this.rowData.size() == 0) {
//        b = false;
//      } else {
//        this.thisRow = this.rowData.next();
//        boolean b;
//        if (this.thisRow == null) {
//          b = false;
//        } else {
//          clearWarnings();
//
//          b = true;
//        }
//
//      }
//
//      setRowPositionValidity();
//
//      return b;
//    }
//  }
//
//  private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException
//  {
//    if (val == null) {
//      return 0;
//    }
//
//    double valueAsDouble = Double.parseDouble(val);
//
//    if ((this.jdbcCompliantTruncationForReads) && (
//      (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
//    {
//      throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
//    }
//
//    return (int)valueAsDouble;
//  }
//
//  private int getIntWithOverflowCheck(int columnIndex) throws SQLException {
//    int intValue = this.thisRow.getInt(columnIndex);
//
//    checkForIntegerTruncation(columnIndex, null, intValue);
//
//    return intValue;
//  }
//
//  private void checkForIntegerTruncation(int columnIndex, byte[] valueAsBytes, int intValue)
//    throws SQLException
//  {
//    if ((this.jdbcCompliantTruncationForReads) && (
//      (intValue == -2147483648) || (intValue == 2147483647))) {
//      String valueAsString = null;
//
//      if (valueAsBytes == null) {
//        valueAsString = this.thisRow.getString(columnIndex, this.fields[columnIndex].getCharacterSet(), this.connection);
//      }
//
//      long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
//
//      if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
//      {
//        throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex + 1, 4);
//      }
//    }
//  }
//
//  private long parseLongAsDouble(int columnIndexZeroBased, String val)
//    throws NumberFormatException, SQLException
//  {
//    if (val == null) {
//      return 0L;
//    }
//
//    double valueAsDouble = Double.parseDouble(val);
//
//    if ((this.jdbcCompliantTruncationForReads) && (
//      (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
//    {
//      throwRangeException(val, columnIndexZeroBased + 1, -5);
//    }
//
//    return ()valueAsDouble;
//  }
//
//  private long getLongWithOverflowCheck(int columnIndexZeroBased, boolean doOverflowCheck) throws SQLException {
//    long longValue = this.thisRow.getLong(columnIndexZeroBased);
//
//    if (doOverflowCheck) {
//      checkForLongTruncation(columnIndexZeroBased, null, longValue);
//    }
//
//    return longValue;
//  }
//
//  private long parseLongWithOverflowCheck(int columnIndexZeroBased, byte[] valueAsBytes, String valueAsString, boolean doCheck)
//    throws NumberFormatException, SQLException
//  {
//    long longValue = 0L;
//
//    if ((valueAsBytes == null) && (valueAsString == null)) {
//      return 0L;
//    }
//
//    if (valueAsBytes != null) {
//      longValue = StringUtils.getLong(valueAsBytes);
//    }
//    else
//    {
//      valueAsString = valueAsString.trim();
//
//      longValue = Long.parseLong(valueAsString);
//    }
//
//    if ((doCheck) && (this.jdbcCompliantTruncationForReads)) {
//      checkForLongTruncation(columnIndexZeroBased, valueAsBytes, longValue);
//    }
//
//    return longValue;
//  }
//
//  private void checkForLongTruncation(int columnIndexZeroBased, byte[] valueAsBytes, long longValue) throws SQLException {
//    if ((longValue == -9223372036854775808L) || (longValue == 9223372036854775807L))
//    {
//      String valueAsString = null;
//
//      if (valueAsBytes == null) {
//        valueAsString = this.thisRow.getString(columnIndexZeroBased, this.fields[columnIndexZeroBased].getCharacterSet(), this.connection);
//      }
//
//      double valueAsDouble = Double.parseDouble(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
//
//      if ((valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D))
//      {
//        throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndexZeroBased + 1, -5);
//      }
//    }
//  }
//
//  private short parseShortAsDouble(int columnIndex, String val)
//    throws NumberFormatException, SQLException
//  {
//    if (val == null) {
//      return 0;
//    }
//
//    double valueAsDouble = Double.parseDouble(val);
//
//    if ((this.jdbcCompliantTruncationForReads) && (
//      (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D)))
//    {
//      throwRangeException(String.valueOf(valueAsDouble), columnIndex, 5);
//    }
//
//    return (short)(int)valueAsDouble;
//  }
//
//  private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString)
//    throws NumberFormatException, SQLException
//  {
//    short shortValue = 0;
//
//    if ((valueAsBytes == null) && (valueAsString == null)) {
//      return 0;
//    }
//
//    if (valueAsBytes != null) {
//      shortValue = StringUtils.getShort(valueAsBytes);
//    }
//    else
//    {
//      valueAsString = valueAsString.trim();
//
//      shortValue = Short.parseShort(valueAsString);
//    }
//
//    if ((this.jdbcCompliantTruncationForReads) && (
//      (shortValue == -32768) || (shortValue == 32767))) {
//      long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
//
//      if ((valueAsLong < -32768L) || (valueAsLong > 32767L))
//      {
//        throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex, 5);
//      }
//
//    }
//
//    return shortValue;
//  }
//
//  public boolean prev()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      int rowIndex = this.rowData.getCurrentRowNumber();
//
//      if (this.thisRow != null) {
//        this.thisRow.closeOpenStreams();
//      }
//
//      boolean b = true;
//
//      if (rowIndex - 1 >= 0) {
//        rowIndex--;
//        this.rowData.setCurrentRow(rowIndex);
//        this.thisRow = this.rowData.getAt(rowIndex);
//
//        b = true;
//      } else if (rowIndex - 1 == -1) {
//        rowIndex--;
//        this.rowData.setCurrentRow(rowIndex);
//        this.thisRow = null;
//
//        b = false;
//      } else {
//        b = false;
//      }
//
//      setRowPositionValidity();
//
//      return b;
//    }
//  }
//
//  public boolean previous()
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (this.onInsertRow) {
//        this.onInsertRow = false;
//      }
//
//      if (this.doingUpdates) {
//        this.doingUpdates = false;
//      }
//
//      return prev();
//    }
//  }
//
//  public void realClose(boolean calledExplicitly)
//    throws SQLException
//  {
//    MySQLConnection locallyScopedConn = this.connection;
//
//    if (locallyScopedConn == null) return;
//
//    synchronized (locallyScopedConn.getConnectionMutex())
//    {
//      if (this.isClosed) return;
//      try
//      {
//        if (this.useUsageAdvisor)
//        {
//          if (!calledExplicitly) {
//            this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver")));
//          }
//
//          if ((this.rowData instanceof RowDataStatic))
//          {
//            if (this.rowData.size() > this.connection.getResultSetSizeThreshold())
//            {
//              this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Too_Large_Result_Set", new Object[] { Integer.valueOf(this.rowData.size()), Integer.valueOf(this.connection.getResultSetSizeThreshold()) })));
//            }
//
//            if ((!isLast()) && (!isAfterLast()) && (this.rowData.size() != 0))
//            {
//              this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[] { Integer.valueOf(getRow()), Integer.valueOf(this.rowData.size()) })));
//            }
//
//          }
//
//          if ((this.columnUsed.length > 0) && (!this.rowData.wasEmpty())) {
//            StringBuffer buf = new StringBuffer(Messages.getString("ResultSet.The_following_columns_were_never_referenced"));
//
//            boolean issueWarn = false;
//
//            for (int i = 0; i < this.columnUsed.length; i++) {
//              if (this.columnUsed[i] == 0) {
//                if (!issueWarn)
//                  issueWarn = true;
//                else {
//                  buf.append(", ");
//                }
//
//                buf.append(this.fields[i].getFullName());
//              }
//            }
//
//            if (issueWarn) {
//              this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), 0, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, buf.toString()));
//            }
//
//          }
//
//        }
//
//      }
//      finally
//      {
//        if ((this.owningStatement != null) && (calledExplicitly)) {
//          this.owningStatement.removeOpenResultSet(this);
//        }
//
//        SQLException exceptionDuringClose = null;
//
//        if (this.rowData != null) {
//          try {
//            this.rowData.close();
//          } catch (SQLException sqlEx) {
//            exceptionDuringClose = sqlEx;
//          }
//        }
//
//        if (this.statementUsedForFetchingRows != null) {
//          try {
//            this.statementUsedForFetchingRows.realClose(true, false);
//          } catch (SQLException sqlEx) {
//            if (exceptionDuringClose != null)
//              exceptionDuringClose.setNextException(sqlEx);
//            else {
//              exceptionDuringClose = sqlEx;
//            }
//          }
//        }
//
//        this.rowData = null;
//        this.defaultTimeZone = null;
//        this.fields = null;
//        this.columnLabelToIndex = null;
//        this.fullColumnNameToIndex = null;
//        this.columnToIndexCache = null;
//        this.eventSink = null;
//        this.warningChain = null;
//
//        if (!this.retainOwningStatement) {
//          this.owningStatement = null;
//        }
//
//        this.catalog = null;
//        this.serverInfo = null;
//        this.thisRow = null;
//        this.fastDateCal = null;
//        this.connection = null;
//
//        this.isClosed = true;
//
//        if (exceptionDuringClose != null)
//          throw exceptionDuringClose;
//      }
//    }
//  }
//
//  public boolean isClosed()
//    throws SQLException
//  {
//    return this.isClosed;
//  }
//
//  public boolean reallyResult() {
//    if (this.rowData != null) {
//      return true;
//    }
//
//    return this.reallyResult;
//  }
//
//  public void refreshRow()
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public boolean relative(int rows)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex())
//    {
//      if (this.rowData.size() == 0) {
//        setRowPositionValidity();
//
//        return false;
//      }
//
//      if (this.thisRow != null) {
//        this.thisRow.closeOpenStreams();
//      }
//
//      this.rowData.moveRowRelative(rows);
//      this.thisRow = this.rowData.getAt(this.rowData.getCurrentRowNumber());
//
//      setRowPositionValidity();
//
//      return (!this.rowData.isAfterLast()) && (!this.rowData.isBeforeFirst());
//    }
//  }
//
//  public boolean rowDeleted()
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  public boolean rowInserted()
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  public boolean rowUpdated()
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  protected void setBinaryEncoded()
//  {
//    this.isBinaryEncoded = true;
//  }
//
//  private void setDefaultTimeZone(TimeZone defaultTimeZone) throws SQLException {
//    synchronized (checkClosed().getConnectionMutex()) {
//      this.defaultTimeZone = defaultTimeZone;
//    }
//  }
//
//  public void setFetchDirection(int direction)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if ((direction != 1000) && (direction != 1001) && (direction != 1002))
//      {
//        throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009", getExceptionInterceptor());
//      }
//
//      this.fetchDirection = direction;
//    }
//  }
//
//  public void setFetchSize(int rows)
//    throws SQLException
//  {
//    synchronized (checkClosed().getConnectionMutex()) {
//      if (rows < 0) {
//        throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009", getExceptionInterceptor());
//      }
//
//      this.fetchSize = rows;
//    }
//  }
//
//  public void setFirstCharOfQuery(char c)
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.firstCharOfQuery = c;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  protected synchronized void setNextResultSet(ResultSetInternalMethods nextResultSet)
//  {
//    this.nextResultSet = nextResultSet;
//  }
//
//  public void setOwningStatement(StatementImpl owningStatement) {
//    try {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.owningStatement = owningStatement;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  protected synchronized void setResultSetConcurrency(int concurrencyFlag)
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.resultSetConcurrency = concurrencyFlag;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  protected synchronized void setResultSetType(int typeFlag)
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.resultSetType = typeFlag;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  protected synchronized void setServerInfo(String info)
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.serverInfo = info;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public synchronized void setStatementUsedForFetchingRows(PreparedStatement stmt) {
//    try {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.statementUsedForFetchingRows = stmt;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public synchronized void setWrapperStatement(Statement wrapperStatement)
//  {
//    try
//    {
//      synchronized (checkClosed().getConnectionMutex()) {
//        this.wrapperStatement = wrapperStatement;
//      }
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException
//  {
//    String datatype = null;
//
//    switch (jdbcType) {
//    case -6:
//      datatype = "TINYINT";
//      break;
//    case 5:
//      datatype = "SMALLINT";
//      break;
//    case 4:
//      datatype = "INTEGER";
//      break;
//    case -5:
//      datatype = "BIGINT";
//      break;
//    case 7:
//      datatype = "REAL";
//      break;
//    case 6:
//      datatype = "FLOAT";
//      break;
//    case 8:
//      datatype = "DOUBLE";
//      break;
//    case 3:
//      datatype = "DECIMAL";
//      break;
//    case -4:
//    case -3:
//    case -2:
//    case -1:
//    case 0:
//    case 1:
//    case 2:
//    default:
//      datatype = " (JDBC type '" + jdbcType + "')";
//    }
//
//    throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003", getExceptionInterceptor());
//  }
//
//  public String toString()
//  {
//    if (this.reallyResult) {
//      return super.toString();
//    }
//
//    return "Result set representing update count of " + this.updateCount;
//  }
//
//  public void updateArray(int arg0, Array arg1)
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  public void updateArray(String arg0, Array arg1)
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  public void updateAsciiStream(int columnIndex, InputStream x, int length)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateAsciiStream(String columnName, InputStream x, int length)
//    throws SQLException
//  {
//    updateAsciiStream(findColumn(columnName), x, length);
//  }
//
//  public void updateBigDecimal(int columnIndex, BigDecimal x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateBigDecimal(String columnName, BigDecimal x)
//    throws SQLException
//  {
//    updateBigDecimal(findColumn(columnName), x);
//  }
//
//  public void updateBinaryStream(int columnIndex, InputStream x, int length)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateBinaryStream(String columnName, InputStream x, int length)
//    throws SQLException
//  {
//    updateBinaryStream(findColumn(columnName), x, length);
//  }
//
//  public void updateBlob(int arg0, java.sql.Blob arg1)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateBlob(String arg0, java.sql.Blob arg1)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateBoolean(int columnIndex, boolean x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateBoolean(String columnName, boolean x)
//    throws SQLException
//  {
//    updateBoolean(findColumn(columnName), x);
//  }
//
//  public void updateByte(int columnIndex, byte x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateByte(String columnName, byte x)
//    throws SQLException
//  {
//    updateByte(findColumn(columnName), x);
//  }
//
//  public void updateBytes(int columnIndex, byte[] x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateBytes(String columnName, byte[] x)
//    throws SQLException
//  {
//    updateBytes(findColumn(columnName), x);
//  }
//
//  public void updateCharacterStream(int columnIndex, Reader x, int length)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateCharacterStream(String columnName, Reader reader, int length)
//    throws SQLException
//  {
//    updateCharacterStream(findColumn(columnName), reader, length);
//  }
//
//  public void updateClob(int arg0, java.sql.Clob arg1)
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  public void updateClob(String columnName, java.sql.Clob clob)
//    throws SQLException
//  {
//    updateClob(findColumn(columnName), clob);
//  }
//
//  public void updateDate(int columnIndex, Date x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateDate(String columnName, Date x)
//    throws SQLException
//  {
//    updateDate(findColumn(columnName), x);
//  }
//
//  public void updateDouble(int columnIndex, double x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateDouble(String columnName, double x)
//    throws SQLException
//  {
//    updateDouble(findColumn(columnName), x);
//  }
//
//  public void updateFloat(int columnIndex, float x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateFloat(String columnName, float x)
//    throws SQLException
//  {
//    updateFloat(findColumn(columnName), x);
//  }
//
//  public void updateInt(int columnIndex, int x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateInt(String columnName, int x)
//    throws SQLException
//  {
//    updateInt(findColumn(columnName), x);
//  }
//
//  public void updateLong(int columnIndex, long x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateLong(String columnName, long x)
//    throws SQLException
//  {
//    updateLong(findColumn(columnName), x);
//  }
//
//  public void updateNull(int columnIndex)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateNull(String columnName)
//    throws SQLException
//  {
//    updateNull(findColumn(columnName));
//  }
//
//  public void updateObject(int columnIndex, Object x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateObject(int columnIndex, Object x, int scale)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateObject(String columnName, Object x)
//    throws SQLException
//  {
//    updateObject(findColumn(columnName), x);
//  }
//
//  public void updateObject(String columnName, Object x, int scale)
//    throws SQLException
//  {
//    updateObject(findColumn(columnName), x);
//  }
//
//  public void updateRef(int arg0, Ref arg1)
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  public void updateRef(String arg0, Ref arg1)
//    throws SQLException
//  {
//    throw SQLError.notImplemented();
//  }
//
//  public void updateRow()
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateShort(int columnIndex, short x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateShort(String columnName, short x)
//    throws SQLException
//  {
//    updateShort(findColumn(columnName), x);
//  }
//
//  public void updateString(int columnIndex, String x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateString(String columnName, String x)
//    throws SQLException
//  {
//    updateString(findColumn(columnName), x);
//  }
//
//  public void updateTime(int columnIndex, Time x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateTime(String columnName, Time x)
//    throws SQLException
//  {
//    updateTime(findColumn(columnName), x);
//  }
//
//  public void updateTimestamp(int columnIndex, Timestamp x)
//    throws SQLException
//  {
//    throw new NotUpdatable();
//  }
//
//  public void updateTimestamp(String columnName, Timestamp x)
//    throws SQLException
//  {
//    updateTimestamp(findColumn(columnName), x);
//  }
//
//  public boolean wasNull()
//    throws SQLException
//  {
//    return this.wasNullFlag;
//  }
//
//  protected Calendar getGmtCalendar()
//  {
//    if (this.gmtCalendar == null) {
//      this.gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//    }
//
//    return this.gmtCalendar;
//  }
//
//  protected ExceptionInterceptor getExceptionInterceptor() {
//    return this.exceptionInterceptor;
//  }
//
//  static
//  {
//    if (Util.isJdbc4()) {
//      try {
//        JDBC_4_RS_4_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { Long.TYPE, Long.TYPE, MySQLConnection.class, StatementImpl.class });
//
//        JDBC_4_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { String.class, [Lcom.mysql.jdbc.Field.class, RowData.class, MySQLConnection.class, StatementImpl.class });
//
//        JDBC_4_UPD_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4UpdatableResultSet").getConstructor(new Class[] { String.class, [Lcom.mysql.jdbc.Field.class, RowData.class, MySQLConnection.class, StatementImpl.class });
//      }
//      catch (SecurityException e)
//      {
//        throw new RuntimeException(e);
//      } catch (NoSuchMethodException e) {
//        throw new RuntimeException(e);
//      } catch (ClassNotFoundException e) {
//        throw new RuntimeException(e);
//      }
//    } else {
//      JDBC_4_RS_4_ARG_CTOR = null;
//      JDBC_4_RS_6_ARG_CTOR = null;
//      JDBC_4_UPD_RS_6_ARG_CTOR = null;
//    }
//
//    MIN_DIFF_PREC = Float.parseFloat(Float.toString(1.4E-45F)) - Double.parseDouble(Float.toString(1.4E-45F));
//
//    MAX_DIFF_PREC = Float.parseFloat(Float.toString(3.4028235E+38F)) - Double.parseDouble(Float.toString(3.4028235E+38F));
//
//    resultCounter = 1;
//
//    EMPTY_SPACE = new char['ÿ'];
//
//    for (int i = 0; i < EMPTY_SPACE.length; i++)
//      EMPTY_SPACE[i] = ' ';
//  }
//}
/*
 * ServerDb.java
 *
 */
package jhelp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * This class presents server directly working with database.
 * The complete connection string should take the form of:<br>
 * <code><pre>
 *     jdbc:subprotocol://servername:port/datasource:user=username:password=password
 * </pre></code>
 * Sample for using MS Access data source:<br>
 * <code><pre>
 *  private static final String accessDBURLPrefix
 *      = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
 *  private static final String accessDBURLSuffix
 *      = ";DriverID=22;READONLY=false}";
 *  // Initialize the JdbcOdbc Bridge Driver
 *  try {
 *         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
 *      } catch(ClassNotFoundException e) {
 *         System.err.println("JdbcOdbc Bridge Driver not found!");
 *      }
 *
 *  // Example: method for connection to a Access Database
 *  public Connection getAccessDBConnection(String filename)
 *                           throws SQLException {
 *       String databaseURL = accessDBURLPrefix + filename + accessDBURLSuffix;
 *       return DriverManager.getConnection(databaseURL, "", "");
 *   }
 *</pre></code>
 *  @author <strong >Y.D.Zakovryashin, 2009</strong>
 */
public class ServerDb implements JHelp, AutoCloseable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private static String url;
    public static final String DEFAULT_URL = "jdbc:derby://localhost:1527/dbName";
    private Connection connection;
    private Item item, d;
    private Item [] def;
    private Statement s;
    private PreparedStatement p;
    private String str_def, str_term;
   

    /**
     * Creates a new instance of <code>ServerDb</code> with default parameters.
     * Default parameters are:<br>
     * <ol>
     * <li><code>ServerDb</code> host is &laquo;localhost&raquo;;</li>
     * <li>{@link java.net.ServerSocket} is opened on
     * {@link jhelp.JHelp#DEFAULT_DATABASE_PORT};</li>
     * </ol>
     */
    public ServerDb() {
        this(DEFAULT_DATABASE_PORT);
        System.out.println("SERVERDb: default constructor");
    }
//
//    /**
//     * Constructor creates new instance of <code>ServerDb</code>. 
//     * @param port defines port for {@link java.net.ServerSocket} object.
//     */
    public ServerDb(int port) {
        System.out.println("SERVERDb: constructor");
    }

    /**
     * Constructor creates new instance of <code>ServerDb</code>. 
     * @param url
     * @param args array of {@link java.lang.String} type contains connection
     * parameters.
     */
    public ServerDb(String url) {
        if (url == null || url.trim().isEmpty()){
        this.url = DEFAULT_URL;
        return;
        }
        this.url = url.trim();
    }

    /**
     * Start method for <code>ServerDb</code> application.
     * @param args array of {@link java.lang.String} type contains connection
     * parameters.
     */
    public static void main(String[] args) {
        try (ServerDb db = new ServerDb(url)){
        if (db.checkDriver()){
            db.run();
            db.connect();
        }
    }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
    }}
    
    boolean checkDriver() throws SQLException{
        Driver d = DriverManager.getDriver(url);
        System.out.println("Driver for " + url + " is "
                + d.getClass().getCanonicalName());
        return true;
    }
    
    private void run(){
        System.out.println("SERVERDb: run");
        try{
            Connection c = DriverManager.getConnection(url, "java", "java");
            s = c.createStatement(
            ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_UPDATABLE);
            connection = c;
            }catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    void findData (ResultSet rs, String str) throws SQLException{
        String qry = "SELECT tbldefinitions.definition FROM tbldefinitions INNER JOIN tblterms "
            + "ON(tblterms.id = tbldefinitions.term_id)" +
                    " WHERE tblterms.term = \'"+ str.trim() +"\'";
       rs = s.executeQuery(qry);
       while (rs.next()){
       str_def = rs.getString(1);
       }
        printResultSet(s.executeQuery(qry));
        rs.close();
        s.close();
    }
    
           
    void addData (PreparedStatement p,  String term_str, String def_str) throws SQLException{
        String qry ="SELECT MAX (tblterms.id) FROM tblterms";
        try (ResultSet rs = s.executeQuery(qry)) {
            printResultSet(s.executeQuery(qry));
            while (rs.next()){
                int id = rs.getInt(1);
                id++;
                p = connection.prepareStatement(
                        "INSERT INTO tblterms (id, term) values(?, ?)");
                p.setInt(1, id);
                p.setString(2, term_str);
                p.addBatch();
                
                p.executeUpdate();
                
                p = connection.prepareStatement(
                        "INSERT INTO tbldefinitions (id, definition,  term_id) values(?, ?, ?)");
                p.setInt(1, id);
                p.setString(2, def_str);
                p.setInt(3, id);
                p.addBatch();
                p.executeUpdate();
            }
        }
        s.close();    
    }
    
    void editData (PreparedStatement p,  String term_str, String def_str) throws SQLException{
        String qry ="SELECT tblterms.id FROM tblterms "
                + "WHERE tblterms.term = \'"+ term_str.trim() +"\'";
        try (ResultSet rs = s.executeQuery(qry)) {
            printResultSet(s.executeQuery(qry));
            while (rs.next()){
                int id = rs.getInt(1);   
                
                p = connection.prepareStatement(
                        "UPDATE tbldefinitions SET tbldefinitions.definition = \'" +
                                def_str.trim() + "\' WHERE term_id = " + id);
                p.executeUpdate();
                
            }
        }
        s.close();
    }
    
    void deleteData (PreparedStatement p,  String str) throws SQLException{
        String qry ="SELECT tblterms.id FROM tblterms "
                + "WHERE tblterms.term = \'"+ str.trim() +"\'";
        try (ResultSet rs = s.executeQuery(qry)) {
            printResultSet(s.executeQuery(qry));
            while (rs.next()){
                int id = rs.getInt(1);   
                
                p = connection.prepareStatement(
                        "DELETE FROM tbldefinitions WHERE term_id = " + id);
                p.addBatch();
                
                p = connection.prepareStatement(
                        "DELETE FROM tblterms WHERE id = " + id);
                p.addBatch();
                p.executeUpdate();
                
            }
        }
        s.close();
    }
    
    void nextData (PreparedStatement p,  String str) throws SQLException{
        String qry ="SELECT tblterms.id FROM tblterms "
                + "WHERE tblterms.term = \'"+ str.trim() +"\'";
        ResultSet rs = s.executeQuery(qry);
        int id = rs.getInt(1);
            while (rs.next()){
            p = connection.prepareStatement("SELECT MAX (tblterms.id) FROM tblterms");
            int max_id = rs.getInt(1);  
                if (max_id != id){
                p = connection.prepareStatement(
                        "SELECT tblterms.term, tbldefinitions.definition "
                                + "FROM tbldefinitions "
                                + "INNER JOIN tblterms ON(tblterms.id = tbldefinitions.term_id)" +
                    " WHERE tblterms.id = "+ ++id);
                p.executeQuery();
                str_term = rs.getString(1); 
                str_def = rs.getString(2); 
                }
                else{
                p = connection.prepareStatement(
                        "SELECT tblterms.term, tbldefinitions.definition "
                                + "FROM tbldefinitions "
                                + "INNER JOIN tblterms ON(tblterms.id = tbldefinitions.term_id)" +
                    " WHERE tblterms.id = 1");
                p.executeQuery();
                str_term = rs.getString(1); 
                str_def = rs.getString(2);
                }
            }
        
        rs.close();
        s.close();
    }
    
    void previousData (PreparedStatement p,  String str) throws SQLException{
        String qry ="SELECT tblterms.id FROM tblterms "
                + "WHERE tblterms.term = \'"+ str.trim() +"\'";
        ResultSet rs = s.executeQuery(qry);
        int id = rs.getInt(1);
            while (rs.next()){
                if (id != 1){
                p = connection.prepareStatement(
                        "SELECT tblterms.term, tbldefinitions.definition "
                                + "FROM tbldefinitions "
                                + "INNER JOIN tblterms ON(tblterms.id = tbldefinitions.term_id)" +
                    " WHERE tblterms.id = "+ --id);
                p.executeQuery();
                str_term = rs.getString(1); 
                str_def = rs.getString(2); 
                }
                else{
                    p = connection.prepareStatement("SELECT MAX (tblterms.id) FROM tblterms");
                    int max_id = rs.getInt(1);
                    p = connection.prepareStatement(
                        "SELECT tblterms.term, tbldefinitions.definition "
                                + "FROM tbldefinitions "
                                + "INNER JOIN tblterms ON(tblterms.id = tbldefinitions.term_id)" +
                    " WHERE tblterms.id = " + max_id);
                    p.executeQuery();
                    str_term = rs.getString(1); 
                    str_def = rs.getString(2);
                }
            }
        
        rs.close();
        s.close();
    }
    

    public void printResultSet (ResultSet rs) throws SQLException{
        try {
            if (rs == null || rs.isClosed()) {
            System.out.println("ResultSet is NULL or closed");
            return;
        }
        
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        int counter = 0;
        for (int i = 0; i < cols; i++){
            System.out.print(md.getColumnLabel(i+1) + "\t");
        }
            System.out.println("\n--------------------------------");
            StringBuilder sb = new StringBuilder();
            while(rs.next()){
                for (int i = 1; i <= cols; ++i){
                switch(md.getColumnType(i)){
                    case Types.INTEGER:
                        sb.append(rs.getInt(i)).append('\t');
                        break;
                    case Types.VARCHAR:
                        sb.append(rs.getString(i)).append('\t');
                        break;
                    default:
                        sb.append("Unknown type").append('\t');
                }
                
                }
                sb.append('\n');
                
            }
            System.out.println(sb.toString().trim());
        } catch (SQLException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    
    
      
    /**
     * Method defines job cycle for client request processing.
     */

    /**
     *
     * @return error code. The method returns {@link JHelp#OK} if streams are
     * opened successfully, otherwise the method returns {@link JHelp#ERROR}.
     */
    public int connect() {
        System.out.println("SERVERDb: connect");
        
        try (Socket clientSocket = new Socket("localhost",DEFAULT_DATABASE_PORT)) {
                
                input = new ObjectInputStream(clientSocket.getInputStream());
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                Object obj = input.readObject();//считываем объект

               if (obj instanceof Data) {
                    Data data = (Data)obj;
                    data = new Data(data.getOperation(), data.getKey(), data.getValues());
                    System.out.println(data.getOperation());
                    switch (data.getOperation()){
                        case 1://find
                            findData(null, data.getKey().getItem());
                            item = new Item (-1, data.getKey().getItem(), -1);
                            d = new Item (-1, str_def, -1);
                            def = new Item [1];
                            def[0] = d;
                            data = new Data(1, item, def);
                            output.writeObject(data);
                            break;
                        case 2://add
                            System.out.println(data.getKey().getItem() + data.getValue(0).getItem());
                            addData(null, data.getKey().getItem(), data.getValue(0).getItem());
                            data = new Data(2, null, null);
                            output.writeObject(data);
                            break;
                        case 3://edit
                            editData(null, data.getKey().getItem(), data.getValue(0).getItem());
                            data = new Data(3, null, null);
                            output.writeObject(data);
                            break;
                        case 4://delete
                            deleteData(null, data.getKey().getItem());
                            data = new Data(4, null, null);
                            output.writeObject(data);
                            break;
                        case 5://next
                            nextData(null, data.getKey().getItem());
                            item = new Item (-1, str_term, -1);
                            d = new Item (-1, str_def, -1);
                            def = new Item [1];
                            def[0] = d;
                            data = new Data(5, item, def);
                            output.writeObject(data);
                            break;
                        case 6://previous
                            previousData(null, data.getKey().getItem());
                            item = new Item (-1, str_term, -1);
                            d = new Item (-1, str_def, -1);
                            def = new Item [1];
                            def[0] = d;
                            data = new Data(6, item, def);
                            output.writeObject(data);
                            break;    
                        default:
                            output.writeObject(ERROR);
                        }
                                       
                    }
                

                    System.out.println("SERVERDb: finished.");
                }catch (IOException | ClassNotFoundException | SQLException e) {//
            System.err.println("Error: " + e.getMessage());
                }
        return JHelp.ERROR;
        
    }

    /**
     * Method sets connection to database and create {@link java.net.ServerSocket}
     * object for waiting of client's connection requests.
     * @return error code. Method returns {@link jhelp.JHelp#READY} in success
     * case. Otherwise method return {@link jhelp.JHelp#ERROR} or error code.
     */
    public int connect(String[] args) {
        System.out.println("SERVERDb: connect");
        return JHelp.READY;
    }

    /**
     * Method returns result of client request to a database.
     * @param data object of {@link jhelp.Data} type with request to database.
     * @return object of {@link jhelp.Data} type with results of request to a
     * database.
     * @see Data
     * @since 1.0
     */
    public Data getData(Data data) {
        System.out.println("SERVERDb: getData");
        return null;
    }

    /**
     * Method disconnects <code>ServerDb</code> object from a database and closes
     * {@link java.net.ServerSocket} object.
     * @return disconnect result. Method returns {@link #DISCONNECT} value, if
     * the process ends successfully. Othewise the method returns error code,
     * for example {@link #ERROR}.
     * @see jhelp.JHelp#DISCONNECT
     * @since 1.0
     */
    public int disconnect() {
        System.out.println("SERVERDb: disconnect");
        return JHelp.DISCONNECT;
    }

    @Override
    public void close()throws Exception{
        if(connection.isValid(5)){
            System.out.println("Error: connection is valid.");
            connection.close();
        }
        else {
            System.out.println("End.");
        }
    }
}

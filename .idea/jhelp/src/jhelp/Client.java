/*
 * Client.java
 *
 */
package jhelp;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Client class provides users's interface of the application.
 * @author <strong >Y.D.Zakovryashin, 2009</strong>
 * @version 1.0
 */
public class Client extends JFrame implements JHelp {
    private static final String DEFAULT_TEXT = "Input term...";
    public String HOST;
    public String PORT;
    public String VERSION;
    private JTabbedPane clientPane;
    private JLabel term_label;
    private JLabel def_label;
    private JLabel host_label;
    private JLabel port_label;
    private JLabel ver_label;
    private JLabel help_label;
    private JTextField term_text;
    private JTextField def_text;
    private JTextField host_text;
    private JTextField port_text;
    private JTextField ver_text;
    private JButton find;
    private JButton add;
    private JButton edit;
    private JButton delete;
    private JButton next;
    private JButton previous;
    private JButton exit;
    private JButton save;
    private JButton cancel;
    private JButton choose;
    private JMenuItem mChoose;
    private JMenuItem mFind;
    private JMenuItem mAdd;
    private JMenuItem mEdit;
    private JMenuItem mDelete;
    private JMenuItem mNext;
    private JMenuItem mPrevious;
    private JMenuItem mExit;
    private JMenuItem mSave;
    private JMenuItem mCancel;
    private JMenuItem mHelp;
    private ClientListener cl;
    private Socket clientSocket;
    public int indOfOper;
   
    /**
     * Static constant for serialization
     */
    public static final long serialVersionUID = 1234;
    /**
     * Programm properties
     */
    private Properties prop;
    /**
     * Private Data object presents informational data.
     */
    private Data data;
    private Item item,def;
    
    public Client(){//--
        setTitle("JHelp, v. 1.0");
        setSize (640, 480);
        setLocation(180, 120);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        init();
        setVisible(true);
    }
    /**
     * Constructor with parameters.
     * @param args Array of {@link String} objects. Each item of this array can
     * define any client's property.
     */
    public Client(String[] args) {
        System.out.println("Client: constructor");
    }

    /**
     * Method for application start
     * @param args agrgument of command string
     */
    static public void main(String[] args) {
        Client client = new Client();
    }

    private void init(){
        
        cl = new ClientListener(this);
        clientPane = new JTabbedPane();
        initFirstPane ();
        initSecondPane ();
        initThirdPane ();
        initMenu();
        changeState(false);
        getContentPane().add(clientPane);
    }
    
    private void initFirstPane(){
        JPanel p = new JPanel();
        p.setLayout(null);
        p.add(term_label = new JLabel("Term:"));
        term_label.setBounds(6, 10, 60, 20);
        p.add(term_text = new JTextField(DEFAULT_TEXT));
        term_text.setBounds(50, 10, 450, 20);
        term_text.addKeyListener(cl);
        term_text.addFocusListener(new FocusAdapter(){//при получении фокуса на поле оно обнуляется
        @Override
        public void focusGained(FocusEvent e){
            if (term_text.getText().equals(DEFAULT_TEXT)){
                term_text.setText(" ");
            }
            if (term_text.getText().trim().isEmpty()){
                changeState(false);
            }
            }
        });
        term_text.addMouseListener(new MouseAdapter() {
                
                    @Override
                    public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    if (!term_text.getText().trim().isEmpty() && 
                            !term_text.getText().equals(DEFAULT_TEXT)){
                        changeState(true);
                    }
                    }
        }); 
        
        p.add(def_label = new JLabel("Definitions:"));
        def_label.setBounds(6, 38, 100, 20);
        p.add(def_text = new JTextField(null));
        def_text.setBounds(6, 70, 494, 330);
        
        p.add(find = new JButton("Find"));
        find.setBounds(517, 10, 85, 20);
        find.addActionListener(cl);
        
        p.add(add = new JButton("Add"));
        add.setBounds(517, 70, 85, 20);
        add.addActionListener(cl);
        
        p.add(edit = new JButton("Edit"));
        edit.setBounds(517, 100, 85, 20);
        edit.addActionListener(cl);
        
        p.add(delete = new JButton("Delete"));
        delete.setBounds(517, 130, 85, 20);
        delete.addActionListener(cl);
        
        p.add(next = new JButton("Next"));
        next.setBounds(517, 190, 85, 20);
        next.addActionListener(cl);
        
        p.add(previous = new JButton("Previous"));
        previous.setBounds(517, 220, 85, 20);
        previous.addActionListener(cl);
        
        p.add(exit = new JButton("Exit"));
        exit.setBounds(517, 379, 85, 20);
        exit.addActionListener(cl);
        clientPane.addTab("Main", p);
    }
    
    private void initSecondPane(){
        JPanel p = new JPanel();
        p.setLayout(null);
        p.add(host_label = new JLabel("host:"));
        host_label.setBounds(6, 10, 60, 20);
        p.add(host_text = new JTextField());
        host_text.setBounds(60, 10, 200, 20);
        host_text.addKeyListener(cl);
        p.add(port_label = new JLabel("port:"));
        port_label.setBounds(6, 40, 60, 20);
        p.add(port_text = new JTextField());
        port_text.setBounds(60, 40, 200, 20);
        port_text.addKeyListener(cl);
        p.add(ver_label = new JLabel("version:"));
        ver_label.setBounds(6, 70, 60, 20);
        p.add(ver_text = new JTextField());
        ver_text.setBounds(60, 70, 200, 20);
        ver_text.addKeyListener(cl);
        p.add(choose = new JButton("Choose config..."));
        choose.setBounds(497, 10, 130, 20);
        choose.setActionCommand("choose");
        choose.addActionListener(cl);
        p.add(save = new JButton("Save"));
        save.setBounds(497, 40, 130, 20);
        save.addActionListener(cl);
        p.add(cancel = new JButton("Cancel"));
        cancel.setBounds(497, 70, 130, 20);
        cancel.addActionListener(cl);
        p.add(exit = new JButton("Exit"));
        exit.setBounds(517, 379, 85, 20);
        exit.addActionListener(cl);
        clientPane.addTab("Settings", p);
    }
    
    private void initThirdPane(){
        JPanel p = new JPanel();
        p.setLayout(null);
        p.add(help_label = new JLabel("About programm: JHelp, v1.0."));
        help_label.setBounds(6, 10, 300, 50);
        p.add(exit = new JButton("Exit"));
        exit.setBounds(517, 379, 85, 20);
        exit.addActionListener(cl);
        clientPane.addTab("Help", p);
    }
    
    private void initMenu() {
        mFind = new JMenuItem("Find              Alt-f");
        mFind.setActionCommand("Find");
        mFind.addActionListener(cl);
        mFind.setMnemonic('f');
        
        mAdd = new JMenuItem("Add");
        mAdd.addActionListener(cl);
        mAdd.setMnemonic('a');
        
        mEdit = new JMenuItem("Edit");
        mEdit.addActionListener(cl);
        mEdit.setMnemonic('e');
        
        mDelete = new JMenuItem("Delete");
        mDelete.addActionListener(cl);
        mDelete.setMnemonic('d');
        
        mNext = new JMenuItem("Next");
        mNext.addActionListener(cl);
        mNext.setMnemonic('n');
        
        mPrevious = new JMenuItem("Previous");
        mPrevious.addActionListener(cl);
        mPrevious.setMnemonic('p');
        
        mChoose = new JMenuItem("Choose config...");
        mChoose.setActionCommand("choose");
        mChoose.addActionListener(cl);
        mChoose.setMnemonic('z');
        
        
        mSave = new JMenuItem("Save");
        mSave.addActionListener(cl);
        mSave.setMnemonic('s');
        
        mCancel = new JMenuItem("Cancel");
        mCancel.addActionListener(cl);
        mCancel.setMnemonic('c');
        
        mExit = new JMenuItem("Exit");
        mExit.addActionListener(cl);
        mExit.setMnemonic('x');

        mHelp = new JMenuItem("About");
        mHelp.setActionCommand("Help");
        mHelp.addActionListener(cl);
        mHelp.setMnemonic('h');

        ///
        JMenu file = new JMenu("File");
        file.add(mChoose);
        file.addSeparator();
        file.add(mExit);
        
        JMenu edit = new JMenu("Edit");
        edit.add(mFind);
        edit.add(mNext);
        edit.add(mPrevious);
        edit.add(mAdd);
        edit.add(mEdit);
        edit.add(mDelete);
        
        
        JMenu tools = new JMenu("Settings");
        tools.add(mSave);
        tools.add(mCancel);
        
        JMenu help = new JMenu("Help");
        help.add(mHelp);

        ///
        JMenuBar mb = new JMenuBar();

        mb.add(file);
        mb.add(edit);
        mb.add(tools);
        mb.add(help);

        ///
        setJMenuBar(mb);

    }
   
    /**
     * Запуск формы
     */
    public void run() {
        System.out.println("Client: run");
        init();   
    }
    
    /**
     * Method set connection to default server with default parameters
     * @return error code
     * Соединение с сервером и передача данных типа Data(считываем значения полей ввода 
     * Term и Definitions). 
     * При получении ответа от сервера для выборок Find, Next, Previous
     * вносим значения выборок в поля ввода Term и Definitions.
     * При получении ответа от сервера для выборок Add, Edit, Delete
     * выдаем сообщение об удачном внесении изменений или выдаем сообщение об ошибке.
     * 
     */

    public int connect() {
        System.out.println("Client: connect");
        try (Socket clientSocket = new Socket("localhost",DEFAULT_SERVER_PORT)) {
                ObjectOutputStream output = new ObjectOutputStream(
                            clientSocket.getOutputStream());
                
                ObjectInputStream input = new ObjectInputStream(
                            clientSocket.getInputStream());
                item = new Item (-1, term_text.getText(), -1);
                def = new Item(-1, def_text.getText(), -1);
                Item[] d = new Item [1];
                d[0] = def;
                data = new Data(indOfOper, item, d);//1-number of operation, 2 - term; 3 - def
                output.writeObject(data);
                Object obj = input.readObject();
                    if (obj instanceof Data) {
                        data = (Data) obj;
                        term_text.setText(data.getKey().getItem());
                        def_text.setText(data.getValue(0).getItem());
                        int nOp = data.getOperation();
                        switch (nOp){
                            case 2:
                                JOptionPane.showMessageDialog(null, "Data added to the database");
                                term_text.setText("");
                                def_text.setText("");
                                break;
                            case 3:
                                JOptionPane.showMessageDialog(null, "Data has been changed");
                                term_text.setText("");
                                def_text.setText("");
                                break;
                            case 4:
                                JOptionPane.showMessageDialog(null, "Data has been deleted");
                                term_text.setText("");
                                def_text.setText("");
                                break;  
                                
                        }
                        
                    }else{
                        System.out.println("Error: invalid type of object.");
                        JOptionPane.showMessageDialog(null, "Error: invalid type of object.");
                    }
                    System.out.println("Client finished.");
                }catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            }
        return JHelp.ERROR;
    }
    
    /**
     * Method set connection to server with parameters defines by argument 
     * <code>args</code>
     * @return error code
     */
    @Override
    public int connect(String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    void changeState (boolean b){
        def_text.setEnabled(b);
        find.setEnabled(b);
        mFind.setEnabled(b);
        add.setEnabled(b);
        mAdd.setEnabled(b);
        edit.setEnabled(b);
        mEdit.setEnabled(b);
        delete.setEnabled(b);
        mDelete.setEnabled(b);
        next.setEnabled (b);
        mNext.setEnabled(b);
        previous.setEnabled(b);
        mPrevious.setEnabled(b);
    }
    
    /**
     * Method gets data from data source
     * @param data initial object (template)
     * @return new object
     */
    @Override
    public Data getData(Data data) {
        System.out.println("Client: getData");
        
        return null;
    }

    /**
     * Метод setConfig позволяет выбрать файл с конфигурацией и считать из него
     * в соответствующие поля формы данные: хост, порт и версию.
     */     
   void setConfig () throws IOException{
        JFileChooser c = new JFileChooser("./");
        FileNameExtensionFilter ff = new FileNameExtensionFilter("Config files", "cfg");
        c.setFileFilter(ff);
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        c.setMultiSelectionEnabled(false);
        int r = c.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION){
            prop = new Properties();
            prop.load(new FileInputStream(c.getSelectedFile()));
            HOST = prop.getProperty("host");
            PORT = prop.getProperty("port");
            VERSION = prop.getProperty("version");
            
            host_text.setText(HOST);
            port_text.setText(PORT);
            ver_text.setText(VERSION);
        } 
   }
   
   /**
     * Метод saveConfig сохраняет измененную конфигурацию в файл change_config.cfg
     * с уведомлением пользователя об этом.
     */
   void saveConfig () throws FileNotFoundException, IOException{
        prop = new Properties();
        prop.put("host", host_text.getText());
        prop.put("port", port_text.getText());
        prop.put("version", ver_text.getText());
        File f = new File("change_config.cfg");
        if (f.exists()){
            prop.store(new FileOutputStream(f), null);
            } else {
            f.createNewFile();
            prop.store(new FileOutputStream(f), null);
        }
	JOptionPane.showMessageDialog(null, "All changes are saved \nin file "
                + "change_config.cfg");    
    }
   
    /**
     * Метод cancelConfig возвращает дефолтную конфигурацию из файла, выбранного 
     * изначально, с уведомлением пользователя об этом.
     */
    void cancelConfig(){
        host_text.setText(HOST);
        port_text.setText(PORT);
        ver_text.setText(VERSION);
        JOptionPane.showMessageDialog(null, "All changes are canceled");
    }

   /**
     * Method disconnects client and server
     * @return error code
     */
    public int disconnect() {
        System.out.println("Client: disconnect");
        return JHelp.ERROR;
    }    
    
}

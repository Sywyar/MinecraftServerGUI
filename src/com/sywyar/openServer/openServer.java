package com.sywyar.openserver;

import com.sywyar.openserver.build.BuildToolsThread;
import com.sywyar.openserver.build.BuiltMain;
import com.sywyar.openserver.build.JTextFieldHintListener;
import com.sywyar.openserver.jdialogs.TipsJDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class OpenServer extends JFrame implements Runnable {
    public static int windowWidth=800;
    public static int windowHigh=600;
    public static boolean build = false;
    private static JTextField jt1=new JTextField("如有代理地址，请在此输入");
    private static final JLabel jLabel=new JLabel("");
    public static String version;
    public static String javaPATH = "";
    public static boolean error =false;
    public static boolean stop = false;
    public static boolean expire = false;
    public static boolean isClick = false;
    private final Thread thread = new Thread(this);
    public static InputStream iconInput = OpenServer.class.getResourceAsStream("/com/sywyar/openserver/img/minecraft.png");
    public static Image image;
    public static ArrayList<Process> processArrayList = new ArrayList<>();

    static {
        try {
            image = ImageIO.read(Objects.requireNonNull(iconInput));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OpenServer() {
        this.setIconImage(image);
        this.setTitle("Minecraft开服器");
        this.setSize(windowWidth,windowHigh);
        this.setLocationRelativeTo(null);//窗口居中
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//设置窗口关闭键
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopProcess();
                System.exit(0);
            }
        });
        this.setResizable(false);//窗口大小不可变
        this.setLayout(null);
    }

    public static void main(String[] args) throws IOException {
        OpenServer openServer = new OpenServer();
        File file = new File(System.getProperty("user.dir")+"//BuildTools//BuildTools.jar");
        if (!file.exists()){
            download(new URL("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"),file.getName());
        }
        ArrayList<String> serverList = getServerList();
        String[] versions = serverList.toArray(new String[0]);
        JComboBox<String> list = new JComboBox<>(versions);
        JPanel jpp1 = new JPanel();
        jpp1.add(list);
        JButton button = new JButton("确定");
        jpp1.setBounds(0,0,125,60);
        button.setBounds(135,0,80,60);
        openServer.add(BorderLayout.WEST,jpp1);
        openServer.add(button);
        openServer.repaint();
        openServer.setVisible(true);
        jt1.setBounds(220,0,750-220,60);
        jt1.addFocusListener(new JTextFieldHintListener(jt1,"如果您想使用环境以外的java，请在此输入(例:F:\\JDK1.8\\bin\\java.exe)"));
        openServer.add(jt1);

        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openServer.remove(button);
                openServer.remove(jt1);
                if (!jt1.getText().contains("如果您想使用环境以外的java，请在此输入(例:F:\\JDK1.8\\bin\\java.exe)") && !jt1.getText().isEmpty()){
                    javaPATH =jt1.getText();
                }
                String version = (String) list.getSelectedItem();
                jt1 =new JTextField();
                jt1.addFocusListener(new JTextFieldHintListener(jt1,"输入命令以执行"));
                jt1.setBounds(220,0,750-220,60);
                jLabel.setHorizontalAlignment(SwingConstants.LEFT);
                jLabel.setVerticalAlignment(SwingConstants.TOP);
                JScrollPane jScrollPane = new JScrollPane(jLabel);
                jScrollPane.setBounds(10,80,750,550-70);
                jScrollPane.getVerticalScrollBar().addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        isClick=true;
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isClick=false;
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
                jScrollPane.getVerticalScrollBar().addAdjustmentListener(e1 -> {
                    if (!isClick){
                        e1.getAdjustable().setValue(e1.getAdjustable().getMaximum());
                    }
                });
                openServer.add(jt1);
                openServer.add(jScrollPane);
                openServer.repaint();
                OpenServer.version =version;
                File spigot = new File(System.getProperty("user.dir")+"//BuildTools//"+version+"//spigot-"+version+".jar");
                if (!spigot.exists()){
                    BuildToolsThread buildToolsThread = new BuildToolsThread(version,jLabel,"java -jar BuildTools.jar -rev "+version,javaPATH);
                    buildToolsThread.start();
                }else {
                    build=true;
                }
                JButton button1 = new JButton("执行命令");
                button1.setBounds(125,0,90,60);
                openServer.add(button1);
                BuiltMain builtMain = new BuiltMain(jLabel,jt1,version,button1,openServer,javaPATH);
                builtMain.start();
                openServer.thread.start();
                File file = new File(System.getProperty("user.dir")+"//BuildTools//tips.tips");
                try {
                    String text = readFile(file);
                    if (!file.exists() || !text.contains("true")){
                        new TipsJDialog();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static ArrayList<String> getServerList() {
        ArrayList<String> serverList = new ArrayList<>();
        try {
            URL url = new URL("https://hub.spigotmc.org/versions/");
            try {
                InputStream is = url.openStream();
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String data = br.readLine();
                while (data!=null) {
                    if (data.contains(">") && data.contains("</a>")){
                        String name = result(data,">","</a>");
                        int num = name.length()-name.replaceAll("\\.","").length();
                        if (num>=2){
                            if (name.contains(".json")){
                                serverList.add(result("start"+name,"start",".json"));
                            }
                        }
                    }
                    data = br.readLine();
                }
                br.close();
                isr.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverList;
    }

    public static boolean download(URL downloadURL,String fileName){
        try {
            URLConnection conn = downloadURL.openConnection();
            InputStream inStream = conn.getInputStream();
            File file = new File(System.getProperty("user.dir")+"//BuildTools//"+fileName);
            if (!file.exists()){
                mkdirs(file);
            }
            FileOutputStream fs = new FileOutputStream(System.getProperty("user.dir")+"//BuildTools//"+fileName);
            byte[] buffer = new byte[1204];
            int bytesum = 0,byteread;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void mkdirs(File file) {
        try {
            if (file.isFile()){
                if (!file.createNewFile()){
                    mkdirs(file.getParentFile());
                }
            }else {
                if (!file.mkdirs()){
                    mkdirs(file.getParentFile());
                }
            }
        } catch (IOException e) {
            mkdirs(file.getParentFile());
        }
    }

    public static String result(String str,String start,String end){
        int strStartIndex = str.indexOf(start);
        int strEndIndex = str.indexOf(end);
        return str.substring(strStartIndex, strEndIndex).substring(start.length());
    }

    @Override
    public void run() {
        while (true){
            if (error||stop){
                if (expire){
                    File spigot = new File(System.getProperty("user.dir")+"//BuildTools//"+version+"//spigot-"+version+".jar");
                    spigot.delete();
                }
                stopProcess();
                System.exit(0);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String readFile(File file) throws IOException {
        if (file.exists()){
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                sb.append(temp).append("\n");
            }
            bufferedReader.close();
            return sb.toString();
        }else {
            return "";
        }
    }

    public static void stopProcess(){
        for (Process process:processArrayList){
            if (process.isAlive()){
                process.destroyForcibly();
            }
        }
    }
}
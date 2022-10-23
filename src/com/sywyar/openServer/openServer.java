package com.sywyar.openServer;

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

import static com.sywyar.openServer.openServer.image;

public class openServer extends JFrame implements Runnable {
    public static int windowWidth=800;
    public static int windowHigh=600;
    public static boolean build = false;
    private static JTextField jt1=new JTextField("如有代理地址，请在此输入");
    private static JLabel jLabel=new JLabel("");
    public static String version;
    public static String cmdProxy = "";
    public static boolean error =false;
    public static boolean stop = false;
    public static boolean expire = false;
    public static boolean isClick = false;
    private final Thread thread = new Thread(this);
    public static InputStream iconInput = openServer.class.getResourceAsStream("/com/sywyar/openServer/img/minecraft.png");
    public static Image image;
    public static ArrayList<Process> processArrayList = new ArrayList<>();

    static {
        try {
            image = ImageIO.read(Objects.requireNonNull(iconInput));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public openServer() {
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
        openServer openServer = new openServer();
        File file = new File(System.getProperty("user.dir")+"//BuildTools//BuildTools.jar");
        if (!file.exists()){
            download(new URL("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"),file.getName());
        }
        ArrayList<String> serverList = getServerList();
        String[] versions = serverList.toArray(new String[0]);
        JComboBox list = new JComboBox(versions);
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
        jt1.addFocusListener(new JTextFieldHintListener(jt1,"如有代理地址，请在此输入(只会在构建时使用)"));
        openServer.add(jt1);

        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openServer.remove(button);
                openServer.remove(jt1);
                if (!jt1.getText().contains("如有代理地址，请在此输入(只会在构建时使用)") && !jt1.getText().equals("")){
                    cmdProxy="set http_proxy="+jt1.getText()+" & set https_proxy="+jt1.getText()+" & ";
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
                com.sywyar.openServer.openServer.version =version;
                File spigot = new File(System.getProperty("user.dir")+"//BuildTools//"+version+"//spigot-"+version+".jar");
                if (!spigot.exists()){
                    buildToolsThread buildToolsThread = new buildToolsThread(version,jt1,cmdProxy+"java -jar BuildTools.jar -rev "+version,jLabel);
                    buildToolsThread.start();
                }else {
                    build=true;
                }
                JButton button1 = new JButton("执行命令");
                button1.setBounds(125,0,90,60);
                openServer.add(button1);
                built built = new built(jLabel,jt1,version,button1,openServer);
                built.start();
                openServer.thread.start();
                File file = new File(System.getProperty("user.dir")+"//BuildTools//tips.tips");
                try {
                    String text = readFile(file);
                    if (!file.exists() || !text.contains("true")){
                        new tips();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static ArrayList<String> getServerList() {
        ArrayList<String> serverList = new ArrayList();
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
            if (file.getName().contains(".")){
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
            process.destroyForcibly();
        }
    }
}

class tips extends JDialog {
    public tips() throws IOException {
        setIconImage(image);
        setTitle("Tips");
        setSize(800,400);
        setLocationRelativeTo(null);//窗口居中
        setResizable(false);//窗口大小不可变
        setLayout(null);
        JCheckBox jCheckBox = new JCheckBox("不再提示");
        JButton jButton = new JButton("确定");
        JLabel jLabel = new JLabel("<html><body>Tips<br>" +
                "1.一般来说，当您点击右上角的叉叉时，本软件会顺带强制结束掉构建和服务器进程<br>" +
                "2.构建过程非常慢，你可以在等待构建完成的时间内做其他的事情，甚至是睡一觉？<br>" +
                "3.第一次构建时(无论版本)会进行下载，如果太慢，您可以使用一些魔法来加速(cmd)<br>" +
                "4.无一例外的是，如果检测到了相对应版本的服务器核心，将不会进行重新构建<br>" +
                "5.当服务器核心不是最新的时候，需要重新构建，这时启动服务器核心会让您等待大几秒，大部分情况本软件能够捕捉到，只需在弹窗确定即可<br>" +
                "6.在某些没有捕捉到服务器核心过期的情况下，可以自行删除服务器核心(在本软件目录下的BuildTools中相对于的版本号文件夹下的spigot-版本号.jar文件就是您的核心)<br>" +
                "7.在删除对应版本的服务器核心后重新启动本软件并选择相应版本就可以重新构建<br>" +
                "8.感谢您使用本软件，遇到问题发送邮件(Sywyari@163.com)即可" +
                "</body></html>");
        Font font = new Font("微软雅黑", Font.BOLD, 13);
        jLabel.setFont(font);
        jLabel.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel.setVerticalAlignment(SwingConstants.TOP);
        jButton.addActionListener(e -> {
            if (jCheckBox.isSelected()){
                File file = new File(System.getProperty("user.dir")+"//BuildTools//tips.tips");
                openServer.mkdirs(file);
                built.Writer("true",file.getPath());
            }
            setVisible(false);
        });
        jLabel.setBounds(0,0,800,300);
        jButton.setBounds(700,320,100,50);
        jCheckBox.setBounds(0,320,700,50);
        add(jLabel);
        add(jCheckBox);
        add(jButton);
        setVisible(true);
    }
}
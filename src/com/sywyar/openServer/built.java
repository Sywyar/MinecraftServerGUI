package com.sywyar.openServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.Charset;

import static com.sywyar.openServer.built.version;
import static com.sywyar.openServer.openServer.image;
import static com.sywyar.openServer.openServer.processArrayList;

public class built extends Thread{
    private JLabel jLabel;
    private JTextField jTextField;
    private JButton jButton;
    private openServer openServerJFrame;
    public static String version;
    public static String eula = "";

    public built(JLabel jLabel,JTextField jTextField,String version,JButton jButton,openServer openServer){
        this.jLabel=jLabel;
        this.jTextField=jTextField;
        built.version =version;
        this.jButton = jButton;
        this.openServerJFrame=openServer;
    }

    public static void Writer(String str, String path){
        try {
            FileWriter fileWriter = new FileWriter(path);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write(str);
            fileWriter.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        StringBuilder dian = new StringBuilder(".");
        int s=0,m=0,h=0;
        boolean isError = false;
        while (true){
            try {
                if (!openServer.build){
                    String time = ((h==0)?"":h+"h ")+((m==0)?"":m+"m ")+s+"s";
                    jTextField.setText("["+time+"]"+"正在等待构建完成"+dian);
                    if (dian.length()>=10){
                        dian = new StringBuilder(".");
                    }
                    dian.append(".");
                    s++;
                    if (s==60){
                        m++;
                        s=0;
                    }
                    if (m==60){
                        h++;
                        m=0;
                    }
                }else {
                    File spigot = new File(System.getProperty("user.dir")+"//BuildTools//"+version+"//spigot-"+version+".jar");
                    if (spigot.exists()){
                        File dir = new File(System.getProperty("user.dir")+"//BuildTools//"+version);
                        Process ps = Runtime.getRuntime().exec("java -jar spigot-"+version+".jar nogui", null, dir);
                        processArrayList.add(ps);
                        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream(), Charset.forName("GBK")));
                        jButton.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                OutputStream outputStream = ps.getOutputStream();
                                PrintWriter printWriter = new PrintWriter(outputStream);
                                printWriter.write(jTextField.getText());
                                printWriter.println();
                                printWriter.flush();
                                jTextField.setText("");
                            }
                        });
                        String line;
                        StringBuilder all= new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            if (line.contains("this build is outdated")){
                                new expire();
                            }
                            all.append("<br>").append(line);
                            jLabel.setText("<html><body>"+all+"</body></html>");
                        }
                        processArrayList.remove(ps);
                        File file = new File(dir.getPath()+"\\eula.txt");
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        StringBuilder sb = new StringBuilder();
                        String temp = "";
                        while ((temp = bufferedReader.readLine()) != null) {
                            sb.append(temp).append("\n");
                        }
                        bufferedReader.close();
                        String text = sb.toString();
                        if (text.contains("eula=false")){
                            new eula();
                            while (true){
                                Thread.sleep(1000);
                                jLabel.setText("<html><body>正在等待协议同意"+dian+"</body></html>");
                                if (dian.length()>=10){
                                    dian = new StringBuilder(".");
                                }
                                dian.append(".");
                                if (!eula.equals("")){
                                    boolean eula = Boolean.parseBoolean(built.eula);
                                    if (eula){
                                        openServerJFrame.remove(jButton);
                                        jButton = new JButton("执行命令");
                                        jButton.setBounds(125,0,90,60);
                                        openServerJFrame.add(jButton);
                                        text=text.replace("eula=false","eula=true");
                                        Writer(text,file.getPath());
                                    }else {
                                        openServer.stop=true;
                                        System.exit(0);
                                    }
                                    break;
                                }
                            }
                        }else {
                            break;
                        }
                        br.close();
                        ps.waitFor();
                    }else {
                        new error();
                        isError=true;
                        break;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!isError){
            openServer.stop=true;
        }
    }
}

class eula extends JDialog{
    public eula() throws IOException {
        this.setIconImage(image);
        this.setTitle("需要同意来自mojang的eula.txt");
        setSize(800,400);
        this.setLocationRelativeTo(null);//窗口居中
        this.setResizable(false);//窗口大小不可变
        this.setLayout(null);
        JLabel label1 = new JLabel("开启服务器需要同意来自mojang的eula.txt.您可以打开协议查看详细");
        label1.setBounds(0,0,800,100);
        Button yes = new Button("我同意");
        Button no = new Button("我拒绝");
        Button openTxt = new Button("打开协议");
        yes.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                built.eula="true";
                setVisible(false);
            }
        });
        no.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                built.eula="false";
                setVisible(false);
            }
        });
        openTxt.addActionListener(e -> {
            try {
                new ProcessBuilder("cmd", "/c","start https://aka.ms/MinecraftEULA"/*+System.getProperty("user.dir")+"\\BuildTools\\"+built.version+"\\eula.txt"*/).inheritIO().start().waitFor();
            } catch (InterruptedException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        yes.setBounds(0,100,266,150);
        no.setBounds(266,100,266,150);
        openTxt.setBounds(266*2,100,266,150);
        add(yes);
        add(no);
        add(openTxt);
        add(label1);
        this.setLayout(null);
        setVisible(true);
    }
}

class error extends JDialog{
    public error() throws IOException {
        this.setIconImage(image);
        this.setTitle("构建失败服务器核心失败");
        setSize(800,400);
        this.setLocationRelativeTo(null);//窗口居中
        this.setResizable(false);//窗口大小不可变
        this.setLayout(null);
        JLabel label1 = new JLabel("构建失败,将打开构建日志让您查看详细信息,如果看不懂,请发送给你的朋友来帮助你!");
        label1.setBounds(0,0,800,100);
        JButton button = new JButton("打开日志");
        button.setBounds(0,100,800,150);
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openServer.error=true;
                    new ProcessBuilder("cmd", "/c","start "+System.getProperty("user.dir")+"\\BuildTools\\BuildTools.log.txt").inheritIO().start().waitFor();
                    setVisible(false);
                } catch (InterruptedException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        add(label1);
        add(button);
        this.setLayout(null);
        setVisible(true);
    }
}

class expire extends JDialog{
    public expire() {
        setIconImage(image);
        setTitle("过期的服务器核心");
        setSize(400,400);
        setLocationRelativeTo(null);//窗口居中
        setResizable(false);//窗口大小不可变
        setLayout(null);
        JLabel jLabel = new JLabel("<html><body>检测到"+version+"核心过期，是否在下次启动是重新构建?(注:只有在此软件自行关闭的情况才生效。不用担心，在服务器关闭时，本软件自动关闭)");
        JButton jButton =new JButton("在下次启动重新构建");
        JButton jButton1 = new JButton("不重新构建");
        jButton.addActionListener(e -> {openServer.expire=true;setVisible(false);});
        jButton1.addActionListener(e -> setVisible(false));
        Font font = new Font("微软雅黑", Font.BOLD, 20);
        jLabel.setFont(font);
        jLabel.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel.setVerticalAlignment(SwingConstants.TOP);
        jLabel.setBounds(0,0,400,200);
        jButton.setBounds(200,200,200,200);
        jButton1.setBounds(0,200,200,200);
        add(jLabel);
        add(jButton);
        add(jButton1);
        setVisible(true);
    }
}
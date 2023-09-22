package com.sywyar.openserver.build;

import com.sywyar.openserver.OpenServer;
import com.sywyar.openserver.jdialogs.ErrorJDialog;
import com.sywyar.openserver.jdialogs.EulaJDialog;
import com.sywyar.openserver.jdialogs.ExpireJDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.Charset;

import static com.sywyar.openserver.OpenServer.processArrayList;

public class BuiltMain extends Thread{
    private final JLabel jLabel;
    private final JTextField jTextField;
    private JButton jButton;
    private final OpenServer openServerJFrame;
    private final String javaPATH;
    public static String version;
    public static String eula = "";

    public BuiltMain(JLabel jLabel, JTextField jTextField, String version, JButton jButton, OpenServer openServer, String javaPATH){
        this.jLabel=jLabel;
        this.jTextField=jTextField;
        BuiltMain.version =version;
        this.jButton = jButton;
        this.openServerJFrame=openServer;
        this.javaPATH=javaPATH;
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
                if (!OpenServer.build){
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
                    jTextField.setText("输入命令以执行");
                    File spigot = new File(System.getProperty("user.dir")+"//BuildTools//"+version+"//spigot-"+version+".jar");
                    if (spigot.exists()){
                        File dir = new File(System.getProperty("user.dir")+"//BuildTools//"+version);
                        String cmd = "java -jar spigot-"+version+".jar nogui";
                        if (!javaPATH.isEmpty()){
                            cmd = cmd.replace("java",javaPATH);
                        }
                        Process ps = Runtime.getRuntime().exec(cmd, null, dir);
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
                        long start = System.currentTimeMillis();
                        boolean expired = true;
                        while ((line = br.readLine()) != null) {
                            if (expired && (System.currentTimeMillis()-start)>=1000*15){
                                new ExpireJDialog();
                                expired=false;
                            }
                            all.append("<br>").append(line);
                            jLabel.setText("<html><body>"+all+"</body></html>");
                        }
                        processArrayList.remove(ps);
                        File file = new File(dir.getPath()+"\\eula.txt");
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        StringBuilder sb = new StringBuilder();
                        String temp;
                        while ((temp = bufferedReader.readLine()) != null) {
                            sb.append(temp).append("\n");
                        }
                        bufferedReader.close();
                        String text = sb.toString();
                        if (text.contains("eula=false")){
                            new EulaJDialog();
                            while (true){
                                Thread.sleep(1000);
                                jLabel.setText("<html><body>正在等待协议同意"+dian+"</body></html>");
                                if (dian.length()>=10){
                                    dian = new StringBuilder(".");
                                }
                                dian.append(".");
                                if (!eula.isEmpty()){
                                    boolean eula = Boolean.parseBoolean(BuiltMain.eula);
                                    if (eula){
                                        openServerJFrame.remove(jButton);
                                        jButton = new JButton("执行命令");
                                        jButton.setBounds(125,0,90,60);
                                        openServerJFrame.add(jButton);
                                        text=text.replace("eula=false","eula=true");
                                        Writer(text,file.getPath());
                                    }else {
                                        OpenServer.stop=true;
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
                        new ErrorJDialog();
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
            OpenServer.stop=true;
        }
    }
}
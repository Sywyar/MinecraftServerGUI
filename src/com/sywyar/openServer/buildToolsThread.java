package com.sywyar.openServer;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static com.sywyar.openServer.openServer.processArrayList;

public class buildToolsThread extends Thread{
    private String version;
    private String cmd;
    private JTextField jTextArea;
    private JLabel jLabel;

    public buildToolsThread(String version,JTextField jTextArea,String cmd,JLabel jLabel){
        this.version=version;
        this.jTextArea = jTextArea;
        this.cmd = cmd;
        this.jLabel=jLabel;
    }

    @Override
    public void run() {
        try {
            File dir = new File(System.getProperty("user.dir")+"//BuildTools");
            Process ps = Runtime.getRuntime().exec(cmd, null, dir);
            processArrayList.add(ps);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream(), Charset.forName("GBK")));
            String line;
            StringBuilder all= new StringBuilder();
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                all.append("第").append(lineNum).append("行:").append(line).append("<br>");
                jLabel.setText("<html><body>"+all+"</body></html>");
                lineNum++;
            }
            File file = new File(dir.getPath()+"//"+version);
            if (!file.exists()){
                file.mkdirs();
            }
            processArrayList.remove(ps);
            new ProcessBuilder("cmd", "/c", "cd BuildTools & move "+"spigot-"+version+".jar"+" "+version).inheritIO().start().waitFor();
            openServer.build=true;
            br.close();
            ps.waitFor();
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

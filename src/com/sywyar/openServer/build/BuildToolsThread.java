package com.sywyar.openserver.build;

import com.sywyar.openserver.OpenServer;
import com.sywyar.openserver.jdialogs.ErrorJDialog;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static com.sywyar.openserver.OpenServer.processArrayList;

public class BuildToolsThread extends Thread{
    private final String version;
    private String cmd;
    private final JLabel jLabel;
    private final String javaPATH;

    public BuildToolsThread(String version, JLabel jLabel, String cmd, String javaPATH){
        this.version=version;
        this.cmd = cmd;
        this.jLabel=jLabel;
        this.javaPATH=javaPATH;
    }

    @Override
    public void run() {
        try {
            File dir = new File(System.getProperty("user.dir")+"//BuildTools");
            if (!javaPATH.isEmpty()){
                cmd = cmd.replace("java",javaPATH);
            }
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
            if (ps.waitFor()!=0){
                new ErrorJDialog();
            }else{
                File file = new File(dir.getPath()+"//"+version);
                if (!file.exists()){
                    file.mkdirs();
                }
                processArrayList.remove(ps);
                new ProcessBuilder("cmd", "/c", "cd BuildTools & move "+"spigot-"+version+".jar"+" "+version).inheritIO().start().waitFor();
                OpenServer.build=true;
                br.close();
                ps.waitFor();
            }
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

package com.sywyar.openserver.jdialogs;

import com.sywyar.openserver.build.BuiltMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static com.sywyar.openserver.OpenServer.image;

public class EulaJDialog extends JDialog{
    public EulaJDialog() {
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
                BuiltMain.eula="true";
                setVisible(false);
            }
        });
        no.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BuiltMain.eula="false";
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

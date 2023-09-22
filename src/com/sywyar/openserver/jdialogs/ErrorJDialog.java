package com.sywyar.openserver.jdialogs;

import com.sywyar.openserver.OpenServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static com.sywyar.openserver.OpenServer.image;

public class ErrorJDialog extends JDialog {
    public ErrorJDialog() throws IOException {
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
                    OpenServer.error=true;
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

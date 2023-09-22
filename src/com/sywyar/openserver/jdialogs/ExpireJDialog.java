package com.sywyar.openserver.jdialogs;

import com.sywyar.openserver.OpenServer;

import javax.swing.*;
import java.awt.*;

import static com.sywyar.openserver.build.BuiltMain.version;
import static com.sywyar.openserver.OpenServer.image;

public class ExpireJDialog extends JDialog {
    public ExpireJDialog() {
        setIconImage(image);
        setTitle("过期的服务器核心");
        setSize(400,400);
        setLocationRelativeTo(null);//窗口居中
        setResizable(false);//窗口大小不可变
        setLayout(null);
        JLabel jLabel = new JLabel("<html><body>检测到"+version+"核心过期，是否在下次启动是重新构建?(注:只有在此软件自行关闭的情况才生效。不用担心，在服务器关闭时，本软件自动关闭)");
        JButton jButton =new JButton("在下次启动重新构建");
        JButton jButton1 = new JButton("不重新构建");
        jButton.addActionListener(e -> {
            OpenServer.expire=true;setVisible(false);});
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
